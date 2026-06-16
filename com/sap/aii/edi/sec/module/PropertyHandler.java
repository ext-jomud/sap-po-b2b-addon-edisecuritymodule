package com.sap.aii.edi.sec.module;

import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.MessagePropertyKey;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.tc.logging.Location;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PropertyHandler implements Serializable {
   private static final long serialVersionUID = 1L;
   private Map<String, String> dynamicHeaders = null;
   private boolean tpmEnabled = false;
   private static final String TPM_NAMESPACE = "http://sap.com/xi/b2b/tpm";
   private ModuleContext context = null;
   private transient Location _location = Location.getLocation(PropertyHandler.class);
   private AuditAccess audit = null;
   private MessageKey msgKey = null;
   private String moduleName = null;
   private String direction;

   public PropertyHandler(ModuleContext context, Message message, AuditAccess audit, String moduleName) throws ModuleException {
      String SIGNATURE = "PropertyHandler(ModuleContext context, Message message, AuditAccess audit, String moduleName)";
      this._location.entering("PropertyHandler(ModuleContext context, Message message, AuditAccess audit, String moduleName)");
      this.dynamicHeaders = new HashMap();
      this.context = context;
      this.tpmEnabled = Boolean.valueOf(context.getContextData("tpm.enable"));
      this.audit = audit;
      this.msgKey = message.getMessageKey();
      this.moduleName = moduleName;
      if (this.tpmEnabled) {
         for(MessagePropertyKey key : message.getMessagePropertyKeys()) {
            if ("http://sap.com/xi/b2b/tpm".equals(key.getPropertyNamespace())) {
               String value = message.getMessageProperty(key);
               this.dynamicHeaders.put(key.getPropertyName(), value);
            }
         }
      }

      this._location.exiting("PropertyHandler(ModuleContext context, Message message, AuditAccess audit, String moduleName)");
   }

   public boolean isTpmEnabled() {
      return this.tpmEnabled;
   }

   public String getProperty(String propertyName, String defaultValue) throws ModuleException {
      String SIGNATURE = "getProperty(String propertyName)";
      this._location.entering("getProperty(String propertyName)", new Object[]{propertyName, "Default value: " + defaultValue});
      String value = null;
      if (this.tpmEnabled) {
         this._location.debugT("getProperty(String propertyName)", "TPM is enabled.");
         if (propertyName == null || propertyName.length() == 0) {
            return null;
         }

         value = (String)this.dynamicHeaders.get(this.direction + "." + propertyName);
         if (value != null) {
            this.audit.addAuditLogEntry(this.msgKey, AuditLogStatus.SUCCESS, this.moduleName + ": Property: " + propertyName + " value is set as: " + value + ". Read from TPM.");
         }
      }

      if (value == null || value.trim().length() == 0) {
         this._location.debugT("getProperty(String propertyName)", "Value will be extracted from the context.");
         value = this.context.getContextData(propertyName);
         if (value != null) {
            this.audit.addAuditLogEntry(this.msgKey, AuditLogStatus.SUCCESS, this.moduleName + ": Property: " + propertyName + " value is set as: " + value + ". Read from Channel.");
         } else {
            if (defaultValue == null) {
               throw new IllegalArgumentException("The module-parameter " + propertyName + " has to be set");
            }

            value = defaultValue;
         }
      }

      this._location.exiting("getProperty(String propertyName)", new Object[]{value});
      return value;
   }

   public void setDirection(String direction) {
      if (direction.equalsIgnoreCase("Inbound")) {
         this.direction = "in";
      } else if (direction.equalsIgnoreCase("Outbound")) {
         this.direction = "out";
      } else {
         this.direction = direction;
      }

   }
}
