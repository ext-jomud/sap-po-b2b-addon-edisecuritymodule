package com.sap.aii.b2b.edifact.sec.api;

public class TransferStatus {
   private String composite;
   private String senderSeqNum;
   private String transferPosition;
   private String duplicateIndicator;

   public TransferStatus() {
   }

   private TransferStatus(String composite) {
      this.composite = composite;
   }

   public static TransferStatus parse(String composite, String escapedCompositeSeparator) {
      TransferStatus transferStatus = new TransferStatus(composite);
      String[] parts = composite.split(escapedCompositeSeparator);

      for(int i = 0; i < parts.length; ++i) {
         switch (i) {
            case 0:
               transferStatus.senderSeqNum = parts[0].trim();
               break;
            case 1:
               transferStatus.transferPosition = parts[1].trim();
               break;
            case 2:
               transferStatus.duplicateIndicator = parts[2].trim();
         }
      }

      return transferStatus;
   }

   public String getComposite() {
      return this.composite;
   }

   public String getSenderSequenceNumber() {
      return this.senderSeqNum;
   }

   public void setSenderSequenceNumber(String senderSeqNum) {
      this.senderSeqNum = senderSeqNum;
   }

   public String getTransferPosition() {
      return this.transferPosition;
   }

   public void setTransferPosition(String transferPosition) {
      this.transferPosition = transferPosition;
   }

   public String getDuplicateIndicator() {
      return this.duplicateIndicator;
   }

   public void setDuplicateIndicator(String duplicateIndicator) {
      this.duplicateIndicator = duplicateIndicator;
   }

   public String generate(String compositeSeparator) throws CompositeGenException {
      this.composite = "";
      if (this.duplicateIndicator != null && !"".equals(this.duplicateIndicator)) {
         this.composite = compositeSeparator + this.duplicateIndicator;
      }

      if (this.transferPosition != null && !"".equals(this.transferPosition)) {
         this.composite = compositeSeparator + this.transferPosition + this.composite;
      } else if (!"".equals(this.composite)) {
         this.composite = compositeSeparator + this.composite;
      }

      if (this.senderSeqNum != null && !"".equals(this.senderSeqNum)) {
         this.composite = this.senderSeqNum + this.composite;
      } else if (!"".equals(this.composite)) {
         this.composite = compositeSeparator + this.composite;
      }

      return this.composite;
   }
}
