package com.sap.aii.edi.sec.module;

import com.sap.aii.af.lib.mp.module.Module;
import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.aii.b2b.sec.api.KeystoreUtilsException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.MessagePropertyKey;
import com.sap.engine.interfaces.messaging.api.Payload;
import com.sap.engine.interfaces.messaging.api.PublicAPIAccessFactory;
import com.sap.engine.interfaces.messaging.api.XMLPayload;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.exception.InvalidParamException;
import com.sap.engine.interfaces.messaging.api.exception.MessagingException;
import com.sap.engine.interfaces.messaging.api.exception.PayloadFormatException;
import com.sap.tc.logging.Location;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.util.List;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.resource.ResourceException;

public class EdiSecurityModule implements SessionBean, Module {
   private static final long serialVersionUID = 1L;
   private static final String MODULE_NAME = "EdiSecurityModule";
   private static final String _AUTACK = "AUTACK";
   private transient Location _location = Location.getLocation(EdiSecurityModule.class);
   private AuditAccess auditAccess = null;
   private PropertyHandler propHandler;

   public EdiSecurityModule() {
      String SIGNATURE = "EdiSecurityModule()";

      try {
         this.auditAccess = PublicAPIAccessFactory.getPublicAPIAccess().getAuditAccess();
      } catch (MessagingException e) {
         this._location.errorT("EdiSecurityModule()", "Error while getting AuditAccess " + e);
      }

   }

   public void ejbActivate() throws EJBException, RemoteException {
   }

   public void ejbPassivate() throws EJBException, RemoteException {
   }

   public void ejbRemove() throws EJBException, RemoteException {
   }

   public void setSessionContext(SessionContext arg0) throws EJBException, RemoteException {
   }

   public ModuleData process(ModuleContext ctx, ModuleData md) throws ModuleException {
      String SIGNATURE = "process(ModuleContext mc, ModuleData md)";
      this._location.entering("process(ModuleContext mc, ModuleData md)");

      XMLPayload document;
      Message message;
      MessageKey messageKey;
      try {
         Object principalData = md.getPrincipalData();
         message = (Message)principalData;
         messageKey = message.getMessageKey();
         document = message.getDocument();
         if (this.auditAccess != null) {
            this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.SUCCESS, "EdiSecurityModule is being executed");
         }

         this.propHandler = new PropertyHandler(ctx, message, this.auditAccess, "EdiSecurityModule");
      } catch (Exception e) {
         String err = "Cannot get Message InputStream: ";
         this._location.catching(err, e);
         this._location.errorT("process(ModuleContext mc, ModuleData md)", err + e);
         throw new ModuleException(err + e.getMessage(), e);
      } catch (OutOfMemoryError e) {
         String err = "Not Enough Memory ";
         this._location.catching(err, e);
         this._location.errorT("process(ModuleContext mc, ModuleData md)", err + e);
         throw new ModuleException(err + e.getMessage(), e);
      }

      byte[] content = message.getDocument().getContent();
      String msgEncoding = this.propHandler.getProperty("messageEncoding", "ISO-8859-1");

      try {
         msgEncoding = Charset.forName(msgEncoding).toString();
      } catch (IllegalArgumentException var19) {
         this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.WARNING, "Property 'messageEncoding' has invalid value. Processing will continue using default encoding ISO-8859-1");
         msgEncoding = "ISO-8859-1";
      }

      String ediMessage = new String(content, Charset.forName(msgEncoding));
      List<String> messages = null;
      String direction = this.propHandler.getProperty("secureMsgDirection", (String)null);
      this.propHandler.setDirection(direction);
      AutackSecurityUtil autSecUtil = new AutackSecurityUtil(this.propHandler);

      try {
         if (direction != null && "OUTBOUND".equalsIgnoreCase(direction)) {
            ediMessage = autSecUtil.outboundMessage(ediMessage, messageKey, msgEncoding);
         } else {
            messages = autSecUtil.inboundMessage(ediMessage, messageKey, msgEncoding);
            if (messages != null && messages.size() != 0) {
               ediMessage = (String)messages.get(0);
            }
         }
      } catch (KeystoreUtilsException e) {
         String err = "Error while accessing the Keystore ";
         this._location.catching(err, e);
         this._location.errorT("process(ModuleContext mc, ModuleData md)", err + e);
         this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.ERROR, err + " : " + e.getLocalizedMessage());
      } catch (ResourceException e) {
         String err = "Could not initialize the resource ";
         this._location.catching(err, e);
         this._location.errorT("process(ModuleContext mc, ModuleData md)", err + e);
         throw new ModuleException("Resource Exception: " + e.getMessage(), e);
      } catch (UnsupportedEncodingException e) {
         String err = "Encoding type is unsupported ";
         this._location.catching(err, e);
         this._location.errorT("process(ModuleContext mc, ModuleData md)", err + e);
         throw new ModuleException("Resource Exception: " + e.getMessage(), e);
      }

      if (autSecUtil.getCmsEx() != null) {
         String err = "Could not verify the signature: ";
         this._location.catching(err, autSecUtil.getCmsEx());
         this._location.errorT("process(ModuleContext mc, ModuleData md)", err + autSecUtil.getCmsEx());
         this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.ERROR, err + " : " + autSecUtil.getCmsEx().getLocalizedMessage());
      }

      String signatureParam = this.propHandler.getProperty("verifyMsgSignature", "TRUE");
      XMLPayload payload = message.createXMLPayload();

      try {
         payload.setContentType(document.getContentType());
         payload.setDescription(document.getDescription());
         payload.setName(document.getName());
         payload.setContent(ediMessage.getBytes(Charset.forName(msgEncoding)));
         message.setDocument(payload);
         if (autSecUtil.isAutackRequired() && !autSecUtil.getMsgUtil().getMessageType().equalsIgnoreCase("AUTACK")) {
            MessagePropertyKey msgKey = null;
            msgKey = new MessagePropertyKey("AUTACK_REQUESTED", AckStatusUtil.ADAPTER_NAMESPACE);
            if (msgKey != null) {
               message.setMessageProperty(msgKey, "YES");
            }

            if (messages != null) {
               for(int i = 1; i < messages.size(); ++i) {
                  Payload attachment = message.createPayload();
                  attachment.setName("AUTACK");
                  attachment.setContent(((String)messages.get(i)).getBytes(Charset.forName(msgEncoding)));
                  message.addAttachment(attachment);
               }
            }

            if (!autSecUtil.isMsgVerified()) {
               msgKey = new MessagePropertyKey("SIGNATURE_VERIFY", AckStatusUtil.ADAPTER_NAMESPACE);
               if (msgKey != null) {
                  message.setMessageProperty(msgKey, "FAIL");
               }
            } else {
               msgKey = new MessagePropertyKey("SIGNATURE_VERIFY", AckStatusUtil.ADAPTER_NAMESPACE);
               if (msgKey != null) {
                  message.setMessageProperty(msgKey, "PASS");
               }
            }

            if (signatureParam.equalsIgnoreCase("FALSE")) {
               message.setMessageProperty(msgKey, "PASS");
            }
         } else {
            MessagePropertyKey msgKey = null;
            msgKey = new MessagePropertyKey("AUTACK_REQUESTED", AckStatusUtil.ADAPTER_NAMESPACE);
            if (msgKey != null) {
               message.setMessageProperty(msgKey, "NO");
            }
         }

         md.setPrincipalData(message);
      } catch (InvalidParamException e) {
         String err = "Error while setting the XML Payload ";
         this._location.catching(err, e);
         this._location.errorT("process(ModuleContext mc, ModuleData md)", err + e);
      } catch (PayloadFormatException e) {
         String err = "Error in formation of Payload format ";
         this._location.catching(err, e);
         this._location.errorT("process(ModuleContext mc, ModuleData md)", err + e);
      }

      this._location.exiting("process(ModuleContext mc, ModuleData md)");
      return md;
   }
}
