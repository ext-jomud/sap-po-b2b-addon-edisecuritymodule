package com.sap.aii.edi.sec.module;

import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.aii.b2b.edifact.sec.api.CompositeGenException;
import com.sap.aii.b2b.edifact.sec.api.ObjectTypeIdentification;
import com.sap.aii.b2b.edifact.sec.api.SegmentGenException;
import com.sap.aii.b2b.edifact.sec.api.UNOSegment;
import com.sap.aii.b2b.edifact.sec.api.USRSegment;
import com.sap.aii.b2b.edifact.sec.api.ValidationResult;
import com.sap.aii.b2b.sec.api.CMSException;
import com.sap.aii.b2b.sec.api.CertificateApi;
import com.sap.aii.b2b.sec.api.KeystoreUtilsException;
import com.sap.aii.b2b.sec.api.SigningUtilsIAIK;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.PublicAPIAccessFactory;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.exception.MessagingException;
import com.sap.tc.logging.Location;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.ResourceException;
import javax.sql.DataSource;

public class AutackSecurityUtil {
   EDIMessageUtil msgUtil;
   private transient Location _location = Location.getLocation(AutackSecurityUtil.class);
   private AuditAccess auditAccess = null;
   public static final String DEFAULT_ENCODING = "ISO-8859-1";
   static final String UTF_8 = "UTF-8";
   @Resource(
      name = "B2B/CONVERTER/EDIFACT",
      type = DataSource.class
   )
   private DataSource dataSource;
   private boolean autackRequired = false;
   private boolean isMsgVerified = true;
   private PropertyHandler propHandler;
   private CMSException cmsEx;
   private Exception ex;
   private String unhRefNo;
   private String filterFromEnvelop;
   private String encodingFromEnvelop;
   private static final List<String> securitySegmentList = Arrays.asList("USA", "USB", "USC", "USD", "USE", "USF", "UNO", "UNP", "USH", "USL", "USR", "UST", "USU", "USX", "USY");
   private static final List<String> headerSegmentList = Arrays.asList("UNA", "UNB", "UNG", "UNH", "UNS", "UNT", "UNE", "UNZ");
   private static final String DISTINCT_SEGMENT_QUERY = "SELECT DISTINCT SEGMENT FROM B2B_EDI_DEF_SEG WHERE MESSAGEVERSION = ?";

   public AutackSecurityUtil(PropertyHandler propHandler) {
      String SIGNATURE = "AutackSecurityUtil()";
      this.msgUtil = new EDIMessageUtil();
      this.propHandler = propHandler;

      try {
         this.auditAccess = PublicAPIAccessFactory.getPublicAPIAccess().getAuditAccess();
      } catch (MessagingException e) {
         this._location.errorT("AutackSecurityUtil()", "Error while getting AuditAccess " + e);
      }

   }

   private void initDataSource() {
      if (this.dataSource == null) {
         try {
            this.dataSource = (DataSource)(new InitialContext()).lookup("jdbc/SAP" + System.getProperty("SAPSYSTEMNAME") + "DB");
         } catch (NamingException e1) {
            throw new RuntimeException(e1);
         }
      }

   }

   public List<String> inboundMessage(String ediMessage, MessageKey messageKey, String msgEncoding) throws ModuleException, KeystoreUtilsException, ResourceException, UnsupportedEncodingException {
      String SIGNATURE = "inboundMessage(String ediMessage, MessageKey messageKey)";
      this._location.entering("inboundMessage(String ediMessage, MessageKey messageKey)");
      List<String> autackMessage = new ArrayList();
      List<String> messages = new ArrayList();
      AutackHelper autackHelper = new AutackMsgGenerator(messageKey, this.propHandler);
      String ediMessageWithoutSecuritySegments = this.removeSecurityHeadersFromSignedMsg(ediMessage, messageKey, autackHelper);
      this._location.debugT("EDI Message after removing security headers :\n" + ediMessage);
      String signatureParam = this.propHandler.getProperty("verifyMsgSignature", "TRUE");
      if (signatureParam.equalsIgnoreCase("TRUE") || this.isAutackRequired()) {
         if (this.isAutackRequired() && this.auditAccess != null) {
            this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.SUCCESS, "AUTACK  is requested. Therefore the message signature will be verified. ");
         }

         try {
            this.verifySignature(messageKey, msgEncoding);
         } catch (CMSException e) {
            this.cmsEx = e;
         }
      }

      if (!this.msgUtil.getMessageType().equals(this.msgUtil.getAUTACK()) && this.isAutackRequired()) {
         messages = this.generateAUTACKMessage(ediMessage, messageKey, autackHelper, msgEncoding);
         this._location.debugT("Generated AUTACK Message :\n" + autackMessage);
      }

      messages.add(ediMessageWithoutSecuritySegments);
      Collections.reverse(messages);
      this._location.exiting("inboundMessage(String ediMessage, MessageKey messageKey)");
      return messages;
   }

   private String removeSecurityHeadersFromSignedMsg(String ediMessage, MessageKey messageKey, AutackHelper autackHelper) throws ModuleException {
      String SIGNATURE = "removeSecurityHeadersFromSignedMsg(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)";
      this._location.entering("removeSecurityHeadersFromSignedMsg(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)");
      String modifiedMsg = "";
      String genAutack = this.propHandler.getProperty("generateAutAck", "NO");

      try {
         this.readUNASegment(ediMessage);
         List<String> segmentList = this.getEdifactSplittedSegments(ediMessage);
         this.readUNBSegment();

         for(String segment : segmentList) {
            segment = segment.trim();
            String segStart = segment.substring(0, 3);
            if (segStart.startsWith(this.msgUtil.getUNH())) {
               String[] messageVersionParts = segment.split("\\" + this.msgUtil.getDATA_ELEMENT_SEPARATOR())[2].split("\\" + this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR());
               this.msgUtil.setMessageVersion(messageVersionParts[2]);
               this.msgUtil.setMessageRelease(messageVersionParts[1]);
               this.msgUtil.setMessageType(messageVersionParts[0]);
               if (messageVersionParts.length > 4) {
                  this.msgUtil.setMessageSubVersion(messageVersionParts[4].split("\\" + this.msgUtil.getSEGMENT_TERMINATOR())[0]);
               }

               this._location.debugT("removeSecurityHeadersFromSignedMsg(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)", "Message Type : " + this.msgUtil.getMessageType());
               this._location.debugT("removeSecurityHeadersFromSignedMsg(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)", "Message Version : " + this.msgUtil.getMessageVersion());
               this.initializeSegmentList();
            }

            if (securitySegmentList.contains(segStart)) {
               if (!segStart.startsWith(this.msgUtil.getUSH())) {
                  if (this.msgUtil.getMessageType().equals(this.msgUtil.getAUTACK()) && segStart.startsWith(this.msgUtil.getUSX())) {
                     String[] messageReferences = segment.split("\\" + this.msgUtil.getDATA_ELEMENT_SEPARATOR());
                     if (messageReferences.length > 2) {
                        String interchangeControlReference = messageReferences[1];
                        this.msgUtil.setInterchangeControlNumberForAUTACK(interchangeControlReference);
                     }
                  }
               } else {
                  this.parseUSHSegment(segment, messageKey);
                  if (!genAutack.equalsIgnoreCase("Envelop")) {
                     if (!genAutack.equalsIgnoreCase("NO") && !genAutack.equalsIgnoreCase("Not Required")) {
                        if (genAutack.equalsIgnoreCase("YES") || genAutack.equalsIgnoreCase("Required")) {
                           this.autackRequired = true;
                        }
                     } else {
                        this.autackRequired = false;
                     }
                  }

                  if (!this.msgUtil.getMessageType().equals(this.msgUtil.getAUTACK())) {
                     if (this.isAutackRequired()) {
                        (new AckStatusUtil()).createAckStatusRequested(messageKey.getMessageId(), this.getCorrelationId(false), AckStatusUtil.ACK_TYPE_AUTACK);
                     } else {
                        (new AckStatusUtil()).createAckStatusNotRequested(messageKey.getMessageId(), this.getCorrelationId(false), AckStatusUtil.ACK_TYPE_AUTACK);
                     }
                  }
               }

               if (this.auditAccess != null) {
                  this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.SUCCESS, " Removing security segment from the payload --> " + segment);
               }
            }

            if (!securitySegmentList.contains(segStart) && (this.msgUtil.getSegInVersionList().contains(segStart) || headerSegmentList.contains(segStart))) {
               modifiedMsg = modifiedMsg + segment;
            }
         }

         if (this.msgUtil.getMessageType().equals(this.msgUtil.getAUTACK())) {
            (new AckStatusUtil()).createAckStatusAckReceived(messageKey.getMessageId(), this.getCorrelationId(true), AckStatusUtil.ACK_TYPE_AUTACK);
         }
      } catch (ResourceException e) {
         String err = "Error while setting the XML Payload ";
         this._location.catching(err, e);
         this._location.errorT("removeSecurityHeadersFromSignedMsg(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)", err + e);
      }

      this._location.exiting("removeSecurityHeadersFromSignedMsg(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)");
      return modifiedMsg;
   }

   private void parseUSHSegment(String segment, MessageKey messageKey) {
      if (segment.split("\\" + this.msgUtil.getDATA_ELEMENT_SEPARATOR()).length > 4) {
         this.autackRequired = segment.split("\\" + this.msgUtil.getDATA_ELEMENT_SEPARATOR())[4].equalsIgnoreCase("2");
         if (segment.split("\\" + this.msgUtil.getDATA_ELEMENT_SEPARATOR()).length > 5) {
            this.filterFromEnvelop = segment.split("\\" + this.msgUtil.getDATA_ELEMENT_SEPARATOR())[5];
            if (segment.split("\\" + this.msgUtil.getDATA_ELEMENT_SEPARATOR()).length > 6) {
               this.encodingFromEnvelop = segment.split("\\" + this.msgUtil.getDATA_ELEMENT_SEPARATOR())[6];
            } else {
               this.encodingFromEnvelop = "UTF-8";
               if (this.auditAccess != null) {
                  this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.WARNING, " USH Segment has not got all the conditional fields. Assuming Encoding as UTF-8. " + segment);
               }
            }
         } else {
            this.filterFromEnvelop = "EDC";
            if (this.auditAccess != null) {
               this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.WARNING, " USH Segment has not got all the conditional fields. Assuming Filtering algorithm as EDC. " + segment);
            }
         }
      } else {
         this.autackRequired = false;
         if (this.auditAccess != null) {
            this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.WARNING, " USH Segment has not got all the conditional fields. Assuming AUTACK generation not required. " + segment);
         }
      }

   }

   private void verifySignature(MessageKey messageKey, String msgEncoding) throws KeystoreUtilsException, ResourceException, ModuleException, UnsupportedEncodingException, CMSException {
      String SIGNATURE = "verifySignature(MessageKey messageKey)";
      this._location.entering("verifySignature(MessageKey messageKey)");
      String encType = this.generateEncodingString(this.encodingFromEnvelop);
      String filterType = this.generateFilterTypeString(this.filterFromEnvelop);
      String binEncoding = this.propHandler.getProperty("messageBinDataEncoding", this.resolveBinEncoding(msgEncoding));
      CMSException ex = null;

      for(int i = 0; i < this.msgUtil.getVerifyMsgList().size(); ++i) {
         String msgToVerify = (String)this.msgUtil.getVerifyMsgList().get(i);
         String signature = (String)this.msgUtil.getSignatureMsgList().get(i);
         String inboundCertificate = "";
         CertificateApi certApi = null;
         PublicKey pubKey = null;
         String certFromKeystore = this.propHandler.getProperty("usePartnerCertFromNWA", "false");
         if (certFromKeystore.equalsIgnoreCase("false")) {
            inboundCertificate = this.msgUtil.getInboundCertificate();
            certApi = new CertificateApi(inboundCertificate.getBytes(binEncoding), filterType);
            pubKey = certApi.getPublicKey();
         } else {
            String keyStore = this.propHandler.getProperty("partnerCertificateView", (String)null);
            String alias = this.propHandler.getProperty("partnerCertificateAlias", (String)null);
            certApi = new CertificateApi(keyStore, alias);
            pubKey = certApi.getPublicKey();
         }

         boolean verify = false;

         try {
            verify = SigningUtilsIAIK.verify(msgToVerify.getBytes(encType), signature.getBytes(binEncoding), (RSAPublicKey)pubKey, filterType);
         } catch (CMSException e) {
            ex = e;
         }

         if (this.msgUtil.getMessageType().equals(this.msgUtil.getAUTACK()) && verify) {
            (new AckStatusUtil()).createAckStatusAcceptedReceived(messageKey.getMessageId(), this.getCorrelationId(true), AckStatusUtil.ACK_TYPE_AUTACK);
         } else if (this.msgUtil.getMessageType().equals(this.msgUtil.getAUTACK()) && !verify) {
            (new AckStatusUtil()).createAckStatusRejectedReceived(messageKey.getMessageId(), this.getCorrelationId(true), AckStatusUtil.ACK_TYPE_AUTACK);
         }

         if (!verify && !this.isAutackRequired()) {
            throw new ModuleException("\nSignature verification failed for incoming message with \nMessage type: " + this.msgUtil.getMessageType() + "\nMessage Version: " + this.msgUtil.getMessageVersion() + "\nSender Identification: " + this.msgUtil.getInterchangeSenderIdentification() + "\nReceiver Identification: " + this.msgUtil.getInterchangeRecipientIdentification() + "\nInterchange Control Number: " + this.msgUtil.getInterchangeControlNumber());
         }

         this.msgUtil.getVerifiactionResultList().add(verify);
         if (!verify) {
            this.isMsgVerified = false;
         }
      }

      if (ex != null) {
         throw ex;
      } else {
         this._location.exiting("verifySignature(MessageKey messageKey)");
      }
   }

   private String generateFilterTypeString(String filter) {
      if (filter.equalsIgnoreCase("5")) {
         return "EDA";
      } else if (filter.equalsIgnoreCase("6")) {
         return "EDC";
      } else {
         return filter.equalsIgnoreCase("7") ? "BASE64" : "EDC";
      }
   }

   private String generateEncodingString(String enc) {
      if (enc.equalsIgnoreCase("7")) {
         return "UTF-8";
      } else {
         return enc.equalsIgnoreCase("2") ? "ASCII" : "EDC";
      }
   }

   private List<String> generateAUTACKMessage(String ediMessage, MessageKey messageKey, AutackHelper autackHelper, String msgEncoding) throws ModuleException, ResourceException {
      String SIGNATURE = "generateAUTACKMessage(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)";
      this._location.entering("generateAUTACKMessage(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)");
      String finalMsg = "";
      boolean noException = false;
      List<String> autackMsgList = new ArrayList();

      try {
         this.readUNASegment(ediMessage);
         this.getSubMessageForAutack(ediMessage);
         this.readUNBSegment();
         String keyStore = this.propHandler.getProperty("ownCertificateView", (String)null);
         String alias = this.propHandler.getProperty("ownCertificateAlias", (String)null);
         String encType = this.propHandler.getProperty("encodingType", "UTF-8");
         String binEncoding = this.propHandler.getProperty("messageBinDataEncoding", this.resolveBinEncoding(msgEncoding));
         String filterType = this.propHandler.getProperty("filterAlgorithm", "EDC");
         String keyStorePK = this.propHandler.getProperty("ownPrivateKeyView", (String)null);
         String aliasPK = this.propHandler.getProperty("ownPrivateKeyAlias", (String)null);
         if (!filterType.equals("EDC") && !filterType.equals("EDA") && !filterType.equals("BASE64") && !filterType.equals("HEX")) {
            this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.ERROR, "Filter function " + filterType + " is invalid");
            throw new ModuleException("Filter function " + filterType + " is invalid");
         }

         this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.SUCCESS, "Generating security segments. Filter function " + filterType + " will be applied");
         CertificateApi certApi = new CertificateApi(keyStore, alias);
         String certRef = certApi.getHexSerialNumber();
         String issuerPlainText = certApi.getIssuerDN();
         byte[] issuer = certApi.getIssuerDNEncoded(filterType, encType);
         String issuerString = new String(issuer, binEncoding);
         String unbSegment = this.prepareUNBSegment();

         for(int index = 0; index < this.msgUtil.getSubMsgList().size(); ++index) {
            finalMsg = "";
            String unhSegment = this.prepareUNHSegment();
            boolean isMsgVerified = false;
            if (this.msgUtil.getVerifiactionResultList() != null && this.msgUtil.getVerifiactionResultList().size() > index) {
               isMsgVerified = (Boolean)this.msgUtil.getVerifiactionResultList().get(index);
            }

            String securityHeaderMsg = "";
            String securityTrailerMsg = "";
            String ush = autackHelper.generateUSHSegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getREPEAT_CHARACTER(), this.msgUtil.getInterchangeSenderIdentification(), this.msgUtil.getInterchangeRecipientIdentification());
            securityHeaderMsg = securityHeaderMsg + ush + this.msgUtil.getSEGMENT_TERMINATOR();
            String usa = autackHelper.generateOuterUSASegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getREPEAT_CHARACTER());
            securityHeaderMsg = securityHeaderMsg + usa + this.msgUtil.getSEGMENT_TERMINATOR();
            String usc = autackHelper.generateUSCSegment(issuerString, this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getREPEAT_CHARACTER(), certRef);
            securityHeaderMsg = securityHeaderMsg + usc + this.msgUtil.getSEGMENT_TERMINATOR();
            usa = autackHelper.generateUSASegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getREPEAT_CHARACTER(), binEncoding, certApi, filterType);
            securityHeaderMsg = securityHeaderMsg + usa + this.msgUtil.getSEGMENT_TERMINATOR();
            String usb = autackHelper.generateUSBSegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getREPEAT_CHARACTER(), this.msgUtil.getInterchangeSenderIdentification(), this.msgUtil.getInterchangeRecipientIdentification());
            securityHeaderMsg = securityHeaderMsg + usb + this.msgUtil.getSEGMENT_TERMINATOR();
            String usx = autackHelper.generateUSXSegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getInterchangeControlNumber());
            securityHeaderMsg = securityHeaderMsg + usx + this.msgUtil.getSEGMENT_TERMINATOR();
            String usy = autackHelper.generateUSYSegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), isMsgVerified);
            securityHeaderMsg = securityHeaderMsg + usy + this.msgUtil.getSEGMENT_TERMINATOR();
            if (isMsgVerified) {
               (new AckStatusUtil()).createAckStatusAcceptedGeneratered(messageKey.getMessageId(), this.getCorrelationId(true), AckStatusUtil.ACK_TYPE_AUTACK);
            } else {
               (new AckStatusUtil()).createAckStatusRejectedGeneratered(messageKey.getMessageId(), this.getCorrelationId(true), AckStatusUtil.ACK_TYPE_AUTACK);
            }

            byte[] sign = SigningUtilsIAIK.sign(securityHeaderMsg.getBytes(encType), keyStorePK, aliasPK, filterType);
            String signedResult = new String(sign, binEncoding);
            String ust = autackHelper.generateUSTSegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR());
            securityTrailerMsg = securityTrailerMsg + ust + this.msgUtil.getSEGMENT_TERMINATOR();
            String usr = autackHelper.generateUSRSegment(signedResult, this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getREPEAT_CHARACTER());
            securityTrailerMsg = securityTrailerMsg + usr + this.msgUtil.getSEGMENT_TERMINATOR();
            String untSegment = this.prepareUNT(10);
            finalMsg = finalMsg + unbSegment + this.msgUtil.getSEGMENT_TERMINATOR() + unhSegment + securityHeaderMsg + securityTrailerMsg + untSegment;
            String unoSegment = "";
            String unpSegment = "";
            String objData = "";
            if (!"NO".equalsIgnoreCase(this.propHandler.getProperty("includeOwnCertificate", "YES"))) {
               this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.SUCCESS, " Certificate has been requested to be sent along with the message. Generating UNO and UNP segments");
               byte[] objBytes = certApi.getPKCS7EncodedCertificateBytes(filterType);
               objData = new String(objBytes, binEncoding);
               String dataLen = "" + objData.length();
               unoSegment = autackHelper.generateUNOSegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getREPEAT_CHARACTER(), certRef, dataLen, filterType) + this.msgUtil.getSEGMENT_TERMINATOR();
               unpSegment = autackHelper.generateUNPSegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), dataLen) + this.msgUtil.getSEGMENT_TERMINATOR();
            }

            finalMsg = this.msgUtil.getUnaSegment() + this.msgUtil.getUngSegment() + finalMsg + unoSegment + objData + unpSegment + this.msgUtil.getUnzSegment();
            autackMsgList.add(finalMsg);
            autackHelper.generateRandomNumber();
         }

         noException = true;
      } catch (ResourceException e) {
         this._location.errorT("generateAUTACKMessage(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)", "Error while generating security segments " + e);
         this.ex = e;
      } catch (SegmentGenException e) {
         this._location.errorT("generateAUTACKMessage(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)", "Error while generating security segments " + e);
         this.ex = e;
      } catch (CompositeGenException e) {
         this._location.errorT("generateAUTACKMessage(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)", "Error while generating security segments " + e);
         this.ex = e;
      } catch (KeystoreUtilsException e) {
         this._location.errorT("generateAUTACKMessage(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)", e.getMessage());
         this.ex = e;
      } catch (CMSException e) {
         this._location.errorT("generateAUTACKMessage(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)", e.getMessage());
         this.ex = e;
      } catch (UnsupportedEncodingException e) {
         this._location.errorT("generateAUTACKMessage(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)", e.getMessage());
         this.ex = e;
      } finally {
         if (!noException) {
            (new AckStatusUtil()).createAckStatusRejectedGeneratered(messageKey.getMessageId(), this.getCorrelationId(true), AckStatusUtil.ACK_TYPE_AUTACK);
            if (this.auditAccess != null) {
               this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.ERROR, " The AUTACK message was not generated because of the below exception: " + this.ex);
            }
         }

      }

      this._location.exiting("generateAUTACKMessage(String ediMessage, MessageKey messageKey, AutackHelper autackHelper)");
      return autackMsgList;
   }

   public String outboundMessage(String ediMessage, MessageKey messageKey, String msgEncoding) throws ModuleException {
      String SIGNATURE = "outboundMessage(String ediMessage, MessageKey messageKey)";
      this._location.entering("outboundMessage(String ediMessage, MessageKey messageKey)");
      String finalMsg = "";

      try {
         this.readUNASegment(ediMessage);
         this.getSubMessageForAutack(ediMessage);
         this.readUNBSegment();
         AutackHelper autackHelper = new SignedEDIMsgGenerator(messageKey, this.getCorrelationId(false), this.propHandler);
         String keyStore = this.propHandler.getProperty("ownCertificateView", (String)null);
         String alias = this.propHandler.getProperty("ownCertificateAlias", (String)null);
         String encType = this.propHandler.getProperty("encodingType", "UTF-8");
         String binEncoding = this.propHandler.getProperty("messageBinDataEncoding", this.resolveBinEncoding(msgEncoding));
         String filterType = this.propHandler.getProperty("filterAlgorithm", "EDC");
         if (!filterType.equals("EDC") && !filterType.equals("EDA") && !filterType.equals("BASE64") && !filterType.equals("HEX")) {
            this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.ERROR, "Filter function " + filterType + " is invalid");
            throw new ModuleException("Filter function " + filterType + " is invalid");
         }

         this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.SUCCESS, "Generating security segments. Filter function " + filterType + " will be applied");
         CertificateApi certApi = new CertificateApi(keyStore, alias);
         String certRef = certApi.getHexSerialNumber();
         String issuerPlainText = certApi.getIssuerDN();
         byte[] issuer = certApi.getIssuerDNEncoded(filterType, encType);
         String issuerString = new String(issuer, binEncoding);

         for(int index = 0; index < this.msgUtil.getSubMsgList().size(); ++index) {
            String subMsg = (String)this.msgUtil.getSubMsgList().get(index);
            String unhSegment = (String)this.msgUtil.getUnhSegmentList().get(index);
            String untSegment = (String)this.msgUtil.getUntSegmentList().get(index);
            String securityHeaderMsg = "";
            String securityTrailerMsg = "";
            String ush = autackHelper.generateUSHSegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getREPEAT_CHARACTER(), this.msgUtil.getInterchangeSenderIdentification(), this.msgUtil.getInterchangeRecipientIdentification());
            securityHeaderMsg = securityHeaderMsg + ush + this.msgUtil.getSEGMENT_TERMINATOR();
            String usa = autackHelper.generateOuterUSASegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getREPEAT_CHARACTER());
            securityHeaderMsg = securityHeaderMsg + usa + this.msgUtil.getSEGMENT_TERMINATOR();
            String usc = autackHelper.generateUSCSegment(issuerString, this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getREPEAT_CHARACTER(), certRef);
            securityHeaderMsg = securityHeaderMsg + usc + this.msgUtil.getSEGMENT_TERMINATOR();
            usa = autackHelper.generateUSASegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getREPEAT_CHARACTER(), binEncoding, certApi, filterType);
            securityHeaderMsg = securityHeaderMsg + usa + this.msgUtil.getSEGMENT_TERMINATOR();
            subMsg = securityHeaderMsg + subMsg;
            String keyStorePK = this.propHandler.getProperty("ownPrivateKeyView", (String)null);
            String aliasPK = this.propHandler.getProperty("ownPrivateKeyAlias", (String)null);
            byte[] sign = SigningUtilsIAIK.sign(subMsg.getBytes(encType), keyStorePK, aliasPK, filterType);
            String signedResult = new String(sign, binEncoding);
            String ust = autackHelper.generateUSTSegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR());
            securityTrailerMsg = securityTrailerMsg + ust + this.msgUtil.getSEGMENT_TERMINATOR();
            String usr = autackHelper.generateUSRSegment(signedResult, this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getREPEAT_CHARACTER());
            securityTrailerMsg = securityTrailerMsg + usr + this.msgUtil.getSEGMENT_TERMINATOR();
            finalMsg = finalMsg + unhSegment + subMsg + securityTrailerMsg + untSegment;
            autackHelper.generateRandomNumber();
         }

         String unoSegment = "";
         String unpSegment = "";
         String objData = "";
         if (!"NO".equalsIgnoreCase(this.propHandler.getProperty("includeOwnCertificate", "YES"))) {
            this.auditAccess.addAuditLogEntry(messageKey, AuditLogStatus.SUCCESS, " Certificate has been requested to be sent along with the message. Generating UNO and UNP segments");
            byte[] objBytes = certApi.getPKCS7EncodedCertificateBytes(filterType);
            objData = new String(objBytes, binEncoding);
            String dataLen = "" + objData.length();
            unoSegment = autackHelper.generateUNOSegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getREPEAT_CHARACTER(), certRef, dataLen, filterType) + this.msgUtil.getSEGMENT_TERMINATOR();
            unpSegment = autackHelper.generateUNPSegment(this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), dataLen) + this.msgUtil.getSEGMENT_TERMINATOR();
         }

         finalMsg = this.msgUtil.getUnaSegment() + this.msgUtil.getUnbSegment() + this.msgUtil.getUngSegment() + finalMsg + unoSegment + objData + unpSegment + this.msgUtil.getUnzSegment();
      } catch (ResourceException e) {
         String err = "Error while generating security segments ";
         this._location.catching(err, e);
         this._location.errorT("outboundMessage(String ediMessage, MessageKey messageKey)", err + e);
      } catch (SegmentGenException e) {
         String err = "Error while generating security segments ";
         this._location.catching(err, e);
         this._location.errorT("outboundMessage(String ediMessage, MessageKey messageKey)", err + e);
      } catch (CompositeGenException e) {
         String err = "Error while generating security composites ";
         this._location.catching(err, e);
         this._location.errorT("outboundMessage(String ediMessage, MessageKey messageKey)", err + e);
      } catch (KeystoreUtilsException e) {
         String err = "Error while accessing keystore ";
         this._location.catching(err, e);
         this._location.errorT("outboundMessage(String ediMessage, MessageKey messageKey)", err + e);
         this._location.errorT("outboundMessage(String ediMessage, MessageKey messageKey)", e.getMessage());
      } catch (CMSException e) {
         String err = "Error while reading the certificate ";
         this._location.catching(err, e);
         this._location.errorT("outboundMessage(String ediMessage, MessageKey messageKey)", err + e);
      } catch (UnsupportedEncodingException e) {
         String err = "Error while reading from the encoding ";
         this._location.catching(err, e);
         this._location.errorT("outboundMessage(String ediMessage, MessageKey messageKey)", err + e);
      }

      this._location.exiting("outboundMessage(String ediMessage, MessageKey messageKey)");
      return finalMsg;
   }

   private String getCorrelationId(boolean inbound) {
      String SIGNATURE = "getCorrelationId(boolean inbound";
      this._location.entering("getCorrelationId(boolean inbound");
      String correlationId = "";
      if (inbound) {
         if (this.msgUtil.getMessageType().equals(this.msgUtil.getAUTACK())) {
            correlationId = this.msgUtil.getInterchangeControlNumberForAUTACK().trim() + "_" + this.msgUtil.getInterchangeRecipientIdentification().trim() + "_" + this.msgUtil.getInterchangeSenderIdentification().trim();
         } else {
            correlationId = this.msgUtil.getInterchangeControlNumber().trim() + "_" + this.msgUtil.getInterchangeRecipientIdentification().trim() + "_" + this.msgUtil.getInterchangeSenderIdentification().trim();
         }
      } else {
         correlationId = this.msgUtil.getInterchangeControlNumber().trim() + "_" + this.msgUtil.getInterchangeSenderIdentification().trim() + "_" + this.msgUtil.getInterchangeRecipientIdentification().trim();
      }

      this._location.debugT("getCorrelationId(boolean inbound", correlationId);
      this._location.exiting("getCorrelationId(boolean inbound");
      return correlationId;
   }

   private void initializeSegmentList() {
      this.initDataSource();
      Connection conn = null;
      PreparedStatement ps = null;
      ResultSet rs = null;

      try {
         conn = this.dataSource.getConnection();
         ps = conn.prepareStatement("SELECT DISTINCT SEGMENT FROM B2B_EDI_DEF_SEG WHERE MESSAGEVERSION = ?");
         ps.setString(1, this.msgUtil.getMessageVersion());
         rs = ps.executeQuery();

         while(rs.next()) {
            this.msgUtil.getSegInVersionList().add(rs.getString(1));
         }
      } catch (SQLException e1) {
         throw new UnsupportedOperationException("Cannot find segments in table B2B_EDI_DEF_SEG for version " + this.msgUtil.getMessageVersion() + ". Error " + e1.getMessage(), e1);
      } finally {
         try {
            if (rs != null) {
               rs.close();
            }
         } catch (Exception e) {
            this._location.catching(e);
            this._location.errorT(e.getMessage());
         }

         try {
            if (ps != null) {
               ps.close();
            }
         } catch (Exception ex) {
            this._location.catching(ex);
            this._location.errorT(ex.getMessage());
         }

         try {
            if (conn != null) {
               conn.close();
            }
         } catch (Exception e) {
            this._location.catching(e);
            this._location.errorT(e.getMessage());
         }

      }

   }

   private void getSubMessageForAutack(String message) throws ResourceException, ModuleException {
      String SIGNATURE = "getSubMessageForAutack(String message)";
      this._location.entering("getSubMessageForAutack(String message)");
      String subMsg = "";
      String scope = this.propHandler.getProperty("securityScope", "2");
      if (scope.equalsIgnoreCase("UNB")) {
         scope = "1";
      } else if (scope.equalsIgnoreCase("UNH")) {
         scope = "2";
      }

      int indexOf = -1;
      int begin_index = 0;
      boolean firstTime = true;

      while(message != null && (indexOf = message.indexOf(this.msgUtil.getSEGMENT_TERMINATOR(), begin_index)) != -1) {
         if (indexOf == 0) {
            throw new ResourceException("The message segment structure can not be parsed. No segment terminator \"" + this.msgUtil.getSEGMENT_TERMINATOR() + "\" was found in the message part \"" + message + "\"");
         }

         int escapeIndex = indexOf - 1;
         if (indexOf > message.indexOf(this.msgUtil.getRELEASE_CHARACTER(), begin_index)) {
            while(message.indexOf(this.msgUtil.getRELEASE_CHARACTER(), begin_index) <= escapeIndex && message.charAt(escapeIndex) == this.msgUtil.getRELEASE_CHARACTER()) {
               --escapeIndex;
            }
         }

         if (!this.isDifferenceEven(escapeIndex, indexOf)) {
            String segment = message.substring(0, indexOf + 1);
            if (segment.startsWith(this.msgUtil.getUNA())) {
               this.msgUtil.setUnaSegment(segment);
            } else if (segment.startsWith(this.msgUtil.getUNB())) {
               this.msgUtil.setUnbSegment(segment);
            } else if (segment.startsWith(this.msgUtil.getUNG())) {
               this.msgUtil.setUngSegment(segment);
            } else if (segment.startsWith(this.msgUtil.getUNZ())) {
               this.msgUtil.setUnzSegment(segment);
               if (scope.equals("1")) {
                  this.msgUtil.getSubMsgList().add(subMsg);
                  subMsg = "";
               }
            } else if (segment.startsWith(this.msgUtil.getUNH())) {
               this.msgUtil.getUnhSegmentList().add(segment);
               if (!firstTime && scope.equals("1")) {
                  subMsg = subMsg + segment;
               }

               firstTime = false;
            } else if (segment.startsWith(this.msgUtil.getUNT())) {
               this.msgUtil.getUntSegmentList().add(segment);
               if (scope.equals("1")) {
                  subMsg = subMsg + segment;
               }

               if (scope.equals("2")) {
                  this.msgUtil.getSubMsgList().add(subMsg);
                  subMsg = "";
               }
            } else {
               subMsg = subMsg + segment;
            }

            if (message != null) {
               int indexTo = message.length() > indexOf ? indexOf + 1 : indexOf;
               message = message.substring(indexTo).trim();
            }

            begin_index = 0;
         } else {
            begin_index = indexOf + 1;
         }
      }

      this._location.debugT("getSubMessageForAutack(String message)", "UNA, UNB, UNG, UNZ segments for AUTACK initialized");
      this._location.exiting("getSubMessageForAutack(String message)");
   }

   private List<String> getEdifactSplittedSegments(String message) throws ResourceException {
      String SIGNATURE = "getEdifactSplittedSegments(String message)";
      this._location.entering("getEdifactSplittedSegments(String message)");
      List<String> segmentList = new ArrayList();
      boolean msgRequiredToVerify = false;
      int indexOf = -1;
      int begin_index = 0;
      String verifyMsg = "";

      while(message != null && (indexOf = message.indexOf(this.msgUtil.getSEGMENT_TERMINATOR(), begin_index)) != -1) {
         if (indexOf == 0) {
            throw new ResourceException("The message segment structure can not be parsed. No segment terminator \"" + this.msgUtil.getSEGMENT_TERMINATOR() + "\" was found in the message part \"" + message + "\"");
         }

         int escapeIndex = indexOf - 1;
         if (indexOf > message.indexOf(this.msgUtil.getRELEASE_CHARACTER(), begin_index)) {
            while(message.indexOf(this.msgUtil.getRELEASE_CHARACTER(), begin_index) <= escapeIndex && message.charAt(escapeIndex) == this.msgUtil.getRELEASE_CHARACTER()) {
               --escapeIndex;
            }
         }

         if (!this.isDifferenceEven(escapeIndex, indexOf)) {
            String segment = message.substring(0, indexOf + 1);
            segmentList.add(segment);
            if (segment.startsWith(this.msgUtil.getUNB())) {
               this.msgUtil.setUnbSegment(segment);
            }

            if (segment.startsWith(this.msgUtil.getUSH())) {
               msgRequiredToVerify = true;
               verifyMsg = "";
            } else if (segment.startsWith(this.msgUtil.getUST())) {
               if (!verifyMsg.equals("")) {
                  this.msgUtil.getVerifyMsgList().add(verifyMsg);
               }

               msgRequiredToVerify = false;
            }

            if (msgRequiredToVerify) {
               verifyMsg = verifyMsg + segment;
            }

            if (segment.startsWith(this.msgUtil.getUSR())) {
               USRSegment usr = USRSegment.parse(segment, this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getSEGMENT_TERMINATOR());
               String signMsg = ((ValidationResult)usr.getResults().get(0)).getValidationValue();
               this.msgUtil.getSignatureMsgList().add(signMsg);
            }

            if (message != null) {
               int indexTo = message.length() > indexOf ? indexOf + 1 : indexOf;
               message = message.substring(indexTo).trim();
            }

            if (segment.startsWith(this.msgUtil.getUNO())) {
               UNOSegment uno = UNOSegment.parse(segment, this.msgUtil.getDATA_ELEMENT_SEPARATOR(), this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR(), this.msgUtil.getSEGMENT_TERMINATOR());
               String objectLength = ((ObjectTypeIdentification)uno.getObjectTypeIDs().get(0)).getObjectTypeQual();
               int dataSize = Integer.parseInt(objectLength);
               this.msgUtil.setInboundCertificate(message.substring(0, dataSize));
               message = message.substring(dataSize);
            }

            begin_index = 0;
         } else {
            begin_index = indexOf + 1;
         }
      }

      this._location.exiting("getEdifactSplittedSegments(String message)");
      return segmentList;
   }

   private boolean isDifferenceEven(int lowerValue, int higherValue) {
      if (lowerValue >= 0 && higherValue >= 0 && lowerValue <= higherValue) {
         return (higherValue - lowerValue) % 2 == 0;
      } else {
         throw new IllegalArgumentException("Could not calculate diffence between lowerValue: " + lowerValue + " and higherValue: " + higherValue);
      }
   }

   private void readUNASegment(String ediMessage) throws ResourceException {
      String SIGNATURE = "readUNASegment(String ediMessage)";
      this._location.entering("readUNASegment(String ediMessage)");
      if (ediMessage.length() < 3) {
         throw new ResourceException("No Edifact UNA-Segment identifier found");
      } else {
         String startSegment = ediMessage.substring(0, 20);
         if (startSegment.startsWith(this.msgUtil.getUNA())) {
            this.msgUtil.setCOMPOSITE_ELEMENT_SEPARATOR(startSegment.charAt(3));
            this.msgUtil.setDATA_ELEMENT_SEPARATOR(startSegment.charAt(4));
            this.msgUtil.setRELEASE_CHARACTER(startSegment.charAt(6));
            char sep = startSegment.charAt(7);
            if (sep != ' ') {
               this.msgUtil.setREPEAT_CHARACTER(sep);
            }

            this.msgUtil.setSEGMENT_TERMINATOR(startSegment.charAt(8));
            this._location.debugT("readUNASegment(String ediMessage)", "UNA segment found");
         } else {
            this._location.debugT("readUNASegment(String ediMessage)", "UNA segment not found. Assuming default parameters");
         }

         this._location.exiting("readUNASegment(String ediMessage)");
      }
   }

   private void readUNBSegment() {
      String SIGNATURE = "readUNBSegment()";
      this._location.entering("readUNBSegment()");
      String[] dataElements = this.msgUtil.getUnbSegment().split("\\" + this.msgUtil.getDATA_ELEMENT_SEPARATOR());
      if (dataElements.length < 2) {
         this.msgUtil.setSyntaxID("");
      } else {
         this.msgUtil.setSyntaxID(dataElements[1]);
         if (dataElements.length < 3) {
            this.msgUtil.setInterchangeSenderIdentification("");
            this.msgUtil.setInterchangeRecipientIdentification("");
         } else {
            String[] compositeElements = dataElements[2].split("\\" + this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR());
            if (compositeElements.length < 2) {
               this.msgUtil.setInterchangeSenderIdentification(dataElements[2]);
               this._location.debugT("readUNBSegment()", "Interchange Sender contains only Interchange Sender Identification {0} and not the code qualifier.", new Object[]{dataElements[2]});
            } else {
               this.msgUtil.setInterchangeSenderIdentification(compositeElements[0] + ":" + compositeElements[1]);
               this._location.debugT("readUNBSegment()", "Interchange Sender contains Interchange Sender Identification {0} and Identification code qualifier {1}.", new Object[]{compositeElements[0], compositeElements[1]});
            }

            if (dataElements.length < 4) {
               this.msgUtil.setInterchangeRecipientIdentification("");
            } else {
               compositeElements = dataElements[3].split("\\" + this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR());
               if (compositeElements.length < 2) {
                  this.msgUtil.setInterchangeRecipientIdentification(dataElements[3]);
                  this._location.debugT("readUNBSegment()", "Interchange Recipient contains only Interchange Recipient Identification {0} and not the code qualifier.", new Object[]{dataElements[3]});
               } else {
                  this.msgUtil.setInterchangeRecipientIdentification(compositeElements[0] + ":" + compositeElements[1]);
                  this._location.debugT("readUNBSegment()", "Interchange Recipient contains Interchange Recipient Identification {0} and Identification code qualifier {1}.", new Object[]{compositeElements[0], compositeElements[1]});
               }

               if (dataElements.length > 5) {
                  String interchangeControlNumberDataElement = dataElements[5].split("\\" + this.msgUtil.getSEGMENT_TERMINATOR())[0];
                  if (dataElements.length == 6) {
                     if (interchangeControlNumberDataElement.length() >= 1) {
                        this.msgUtil.setInterchangeControlNumber(interchangeControlNumberDataElement);
                     }
                  } else {
                     this.msgUtil.setInterchangeControlNumber(interchangeControlNumberDataElement);
                  }
               }
            }
         }
      }

   }

   private String prepareUNHSegment() throws ModuleException {
      String msg = this.msgUtil.getUNH() + this.msgUtil.getDATA_ELEMENT_SEPARATOR();
      String autAckVersion = this.propHandler.getProperty("autackVersion", "1:1");
      if (autAckVersion.equalsIgnoreCase("DIR")) {
         autAckVersion = this.msgUtil.getMessageRelease() + this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR() + this.msgUtil.getMessageVersion();
      }

      this.unhRefNo = this.generateRandom(12);
      msg = msg + this.unhRefNo + this.msgUtil.getDATA_ELEMENT_SEPARATOR();
      msg = msg + this.msgUtil.getAUTACK() + this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR() + autAckVersion + ":UN";
      if (!this.msgUtil.getMessageSubVersion().equals("")) {
         msg = msg + this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR() + this.msgUtil.getMessageSubVersion();
      }

      msg = msg + this.msgUtil.getSEGMENT_TERMINATOR();
      return msg;
   }

   private String generateRandom(int length) {
      Random random = new Random();
      char[] digits = new char[length];
      digits[0] = (char)(random.nextInt(9) + 49);

      for(int i = 1; i < length; ++i) {
         digits[i] = (char)(random.nextInt(10) + 48);
      }

      return new String(digits);
   }

   private String prepareUNT(int count) {
      String msg = this.msgUtil.getUNT() + this.msgUtil.getDATA_ELEMENT_SEPARATOR();
      msg = msg + String.valueOf(count) + this.msgUtil.getDATA_ELEMENT_SEPARATOR();
      msg = msg + this.unhRefNo + this.msgUtil.getSEGMENT_TERMINATOR();
      return msg;
   }

   private String prepareUNBSegment() {
      String msg = this.msgUtil.getUNB() + this.msgUtil.getDATA_ELEMENT_SEPARATOR();
      msg = msg + this.msgUtil.getSyntaxID() + this.msgUtil.getDATA_ELEMENT_SEPARATOR();
      msg = msg + this.msgUtil.getInterchangeRecipientIdentification() + this.msgUtil.getDATA_ELEMENT_SEPARATOR();
      msg = msg + this.msgUtil.getInterchangeSenderIdentification() + this.msgUtil.getDATA_ELEMENT_SEPARATOR();
      Date now = new Date();
      SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
      SimpleDateFormat timeformat = new SimpleDateFormat("HHmmss");
      String date = dateformat.format(now);
      String time = timeformat.format(now);
      msg = msg + date + this.msgUtil.getCOMPOSITE_ELEMENT_SEPARATOR() + time + this.msgUtil.getDATA_ELEMENT_SEPARATOR();
      msg = msg + this.msgUtil.getInterchangeControlNumber();
      return msg;
   }

   public boolean isAutackRequired() {
      return this.autackRequired;
   }

   public boolean isMsgVerified() {
      return this.isMsgVerified;
   }

   public EDIMessageUtil getMsgUtil() {
      return this.msgUtil;
   }

   public CMSException getCmsEx() {
      return this.cmsEx;
   }

   private String resolveBinEncoding(String msgEncoding) {
      String result = "ISO-8859-1";
      if (msgEncoding.toLowerCase().contains("iso-8859")) {
         result = msgEncoding;
      } else if (msgEncoding.toLowerCase().contains("windows")) {
         result = msgEncoding;
      } else if (msgEncoding.toLowerCase().contains("utf")) {
         result = "ISO-8859-1";
      }

      return result;
   }
}
