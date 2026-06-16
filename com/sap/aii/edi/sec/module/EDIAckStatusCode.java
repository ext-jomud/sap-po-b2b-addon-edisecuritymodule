package com.sap.aii.edi.sec.module;

public enum EDIAckStatusCode {
   PARTIALLY_ACCEPTED_RECV("PARTIALLY_ACCEPTED_RECV", "Received and validated - Partially accepted"),
   PARTIALLY_ACCEPTED_SENT("PARTIALLY_ACCEPTED_SENT", "Sent - Partially accepted"),
   PARTIALLY_ACCEPTED_NOT_SENT("PARTIALLY_ACCEPTED_NOT_SENT", "Not Sent - Partially accepted"),
   PARTIALLY_ACCEPTED_GENERATED("PARTIALLY_ACCEPTED_GENERATED", "Generated - Partially accepted"),
   REJECTED_RECV("REJECTED_RECV", "Received and validated - Rejected"),
   REJECTED_SENT("REJECTED_SENT", "Sent - Rejected"),
   REJECTED_NOT_SENT("REJECTED_NOT_SENT", "Not Sent - Rejected"),
   REJECTED_GENERATED("REJECTED_GENERATED", "Generated - Rejected"),
   ACCEPTED_RECV("ACCEPTED_RECV", "Received and validated - Accepted or acknowledged"),
   ACCEPTED_SENT("ACCEPTED_SENT", "Sent - Accepted or acknowledged"),
   ACCEPTED_NOT_SENT("ACCEPTED_NOT_SENT", "Not Sent - Accepted or acknowledged"),
   ACCEPTED_GENERATED("ACCEPTED_GENERATED", "Generated - Accepted or acknowledged"),
   ACK_SOLICITED("ACK_SOLICITED", "Acknowledgement solicited from partner"),
   ACK_NOT_SOLICITED("ACK_NOT_SOLICITED", "Acknowledgement not solicited from partner"),
   ACK_NOT_REQUESTED("ACK_NOT_REQUESTED", "Acknowledgement not requested by partner"),
   ACK_REQUESTED("ACK_REQUESTED", "Acknowledgement requested by partner"),
   ACK_RECEIVED("ACK_RECEIVED", "Acknowledgement received from partner");

   private String code;
   private String reasonText;

   private EDIAckStatusCode(String code, String reasonText) {
      this.code = code;
      this.reasonText = reasonText;
   }

   public String getCode() {
      return this.code;
   }

   public String getReasonText() {
      return this.reasonText;
   }

   public void setReasonText(String reasonText) {
      this.reasonText = reasonText;
   }

   public static EDIAckStatusCode getAckStatusCode(boolean isNegative, boolean isPartial, boolean isInboundAck) {
      if (isInboundAck) {
         return isPartial ? PARTIALLY_ACCEPTED_RECV : (isNegative ? REJECTED_RECV : ACCEPTED_RECV);
      } else {
         return isPartial ? PARTIALLY_ACCEPTED_SENT : (isNegative ? REJECTED_SENT : ACCEPTED_SENT);
      }
   }

   public static EDIAckStatusCode getAckStatusCode(boolean isNegative, boolean isPartial, boolean isInboundAck, boolean isGenerated) {
      if (isGenerated) {
         return isPartial ? PARTIALLY_ACCEPTED_RECV : (isNegative ? REJECTED_RECV : ACCEPTED_RECV);
      } else {
         return getAckStatusCode(isNegative, isPartial, isInboundAck);
      }
   }
}
