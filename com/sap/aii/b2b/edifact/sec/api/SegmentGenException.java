package com.sap.aii.b2b.edifact.sec.api;

public class SegmentGenException extends Exception {
   private static final long serialVersionUID = 1L;

   public SegmentGenException(String message, Throwable cause) {
      super(message, cause);
   }

   public SegmentGenException(String message) {
      super(message);
   }

   public SegmentGenException(Throwable cause) {
      super(cause);
   }
}
