package com.sap.aii.af.ackstatus.api.types;

import com.sap.aii.af.ackstatus.api.AckStatusException;
import java.io.Serializable;

public enum AckPresenceType implements Serializable {
   REQUESTED(1, "Requested"),
   NOT_SOLICITED(10, "Not Solicited"),
   SOLICITED(20, "Solicited"),
   GENERATED(30, "Generated"),
   SENT(40, "Sent"),
   NOT_REQUESTED(50, "Not Requested"),
   RECIPT_PENDING(60, "Pending"),
   RECEIVED(70, "Received"),
   VALIDATED(80, "Validated");

   private final int ackPresenceType;
   private final String description;

   private AckPresenceType(int ackPresenceType, String description) {
      this.ackPresenceType = ackPresenceType;
      this.description = description;
   }

   private AckPresenceType(int ackPresenceType) {
      this(ackPresenceType, "");
   }

   public short ackPresenceType() {
      return (short)this.ackPresenceType;
   }

   public String description() {
      return this.description;
   }

   public static AckPresenceType getAckPresenceType(short ackPresenceType) throws AckStatusException {
      if (ackPresenceType < 10) {
         mapToNewValues(ackPresenceType);
      }

      for(AckPresenceType ackPresenceType2 : values()) {
         if (ackPresenceType2.ackPresenceType == ackPresenceType) {
            return ackPresenceType2;
         }
      }

      throw new AckStatusException("ACK_STATUS_ACK_PRESENCE_TYPE_NOT_FOUND", "Given AckPresenceType is NOT found");
   }

   private static short mapToNewValues(short ackPresenceType) {
      short retVal = -1;
      switch (ackPresenceType) {
         case 0:
            return 10;
         case 1:
            return 20;
         case 2:
            return 30;
         case 3:
            return 40;
         case 4:
            return 60;
         case 5:
            return 70;
         default:
            return retVal;
      }
   }
}
