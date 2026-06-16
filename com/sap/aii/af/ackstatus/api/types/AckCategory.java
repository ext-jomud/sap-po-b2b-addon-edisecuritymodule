package com.sap.aii.af.ackstatus.api.types;

import com.sap.aii.af.ackstatus.api.AckStatusException;
import java.io.Serializable;

public enum AckCategory implements Serializable {
   TECHNICAL(0, "Technical Acknowledgement"),
   FUNCTIONAL(1, "Functional Acknowledgement");

   private final int ackCategory;
   private final String description;

   private AckCategory(int ackIndicator, String description) {
      this.ackCategory = ackIndicator;
      this.description = description;
   }

   private AckCategory(int ackIndicator) {
      this(ackIndicator, "");
   }

   public short ackCategory() {
      return (short)this.ackCategory;
   }

   public String description() {
      return this.description;
   }

   public static AckCategory getAckCategory(short ackCategory) throws AckStatusException {
      for(AckCategory ackCategory2 : values()) {
         if (ackCategory2.ackCategory == ackCategory) {
            return ackCategory2;
         }
      }

      throw new AckStatusException("ACK_STATUS_ACK_CATEGORY_NOT_FOUND", "Given Ack Category is NOT found");
   }
}
