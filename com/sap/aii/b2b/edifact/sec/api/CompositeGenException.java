package com.sap.aii.b2b.edifact.sec.api;

public class CompositeGenException extends Exception {
   private static final long serialVersionUID = 1L;

   public CompositeGenException(String message, Throwable cause) {
      super(message, cause);
   }

   public CompositeGenException(String message) {
      super(message);
   }

   public CompositeGenException(Throwable cause) {
      super(cause);
   }
}
