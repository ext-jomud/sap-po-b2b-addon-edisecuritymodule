package com.sap.aii.af.ackstatus.api;

import java.io.Serializable;

public class AckStatusException extends Exception implements Serializable {
   private static final String VERSION_ID = "$Id: //tc/xpi.b2b.toolkit/PIBtwoB2_02_REL/src/SCs/sap.com/PIB2BTOOLKIT/DCs/sap.com/edi/security/module/_comp/ejbModule/com/sap/aii/af/ackstatus/api/AckStatusException.java#1 $";
   private static final long serialVersionUID = -4930795060909043465L;
   public static final String ACK_STATUS_GENERAL_EXCEPTION = "ACK_STATUS_GENERAL_EXCEPTION";
   public static final String ACK_STATUS_ACCESS_EXCEPTION = "ACK_STATUS_ACCESS_EXCEPTION";
   public static final String ACK_STATUS_ACK_TYPE_NOT_FOUND = "ACK_STATUS_ACK_TYPE_NOT_FOUND";
   public static final String ACK_STATUS_ACK_CATEGORY_NOT_FOUND = "ACK_STATUS_ACK_CATEGORY_NOT_FOUND";
   public static final String ACK_STATUS_ACK_PRESENCE_TYPE_NOT_FOUND = "ACK_STATUS_ACK_PRESENCE_TYPE_NOT_FOUND";
   public static final String ACK_STATUS_ACK_INDICATOR_NOT_FOUND = "ACK_STATUS_ACK_INDICATOR_NOT_FOUND";
   public static final String ACK_STATUS_STATUS_CODE_NOT_FOUND = "ACK_STATUS_STATUS_CODE_NOT_FOUND";
   public static final String ACK_STATUS_NAMESPACE_NOT_REGISTERED = "ACK_STATUS_NAMESPACE_NOT_REGISTERED";
   public static final String ACK_STATUS_HANDLER_SERVICE_NOT_AVAILABLE = "ACK_STATUS_HANDLER_SERVICE_NOT_AVAILABLE";
   private String errorCode = null;

   public String getErrorCode() {
      return this.errorCode;
   }

   public void setErrorCode(String errorCode) {
      this.errorCode = errorCode;
   }

   public AckStatusException(String message) {
      super(message);
   }

   public AckStatusException(Throwable cause) {
      super(cause);
   }

   public AckStatusException(String message, Throwable cause) {
      super(message, cause);
   }

   public AckStatusException(String errorCode, String message, Throwable cause) {
      super(message, cause);
      this.errorCode = errorCode;
   }

   public AckStatusException(String errorCode, String message) {
      super(message);
      this.errorCode = errorCode;
   }
}
