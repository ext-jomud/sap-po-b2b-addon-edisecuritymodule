package com.sap.aii.b2b.sec.api;

public class KeystoreUtilsException extends Exception {
   private static final long serialVersionUID = 1L;

   public KeystoreUtilsException(String message, Throwable cause) {
      super(message, cause);
   }

   public KeystoreUtilsException(String message) {
      super(message);
   }

   public KeystoreUtilsException(Throwable cause) {
      super(cause);
   }
}
