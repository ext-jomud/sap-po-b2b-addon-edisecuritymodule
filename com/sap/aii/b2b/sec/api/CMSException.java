package com.sap.aii.b2b.sec.api;

public class CMSException extends Exception {
   private static final long serialVersionUID = 1L;

   public CMSException(String message, Throwable cause) {
      super(message, cause);
   }

   public CMSException(String message) {
      super(message);
   }

   public CMSException(Throwable cause) {
      super(cause);
   }
}
