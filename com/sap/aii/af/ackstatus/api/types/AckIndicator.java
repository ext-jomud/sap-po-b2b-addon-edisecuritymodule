package com.sap.aii.af.ackstatus.api.types;

import com.sap.aii.af.ackstatus.api.AckStatusException;
import java.io.Serializable;

public enum AckIndicator implements Serializable {
   GREEN(0, "Success"),
   YELLOW(1, "Pending"),
   RED(2, "Error");

   private final int ackIndicator;
   private final String description;

   private AckIndicator(int ackIndicator, String description) {
      this.ackIndicator = ackIndicator;
      this.description = description;
   }

   private AckIndicator(int ackIndicator) {
      this(ackIndicator, "");
   }

   public short ackIndicator() {
      return (short)this.ackIndicator;
   }

   public String description() {
      return this.description;
   }

   public static AckIndicator getAckIndicator(short ackIndicator) throws AckStatusException {
      for(AckIndicator ackIndicator2 : values()) {
         if (ackIndicator2.ackIndicator == ackIndicator) {
            return ackIndicator2;
         }
      }

      throw new AckStatusException("ACK_STATUS_ACK_INDICATOR_NOT_FOUND", "Given AckIndicator is NOT found");
   }
}
