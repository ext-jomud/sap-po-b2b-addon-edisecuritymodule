package com.sap.aii.b2b.edifact.sec.api;

public class ObjectStatus {
   private String composite;
   private String objectLength;
   private String segmentCount;
   private String transferSeq;
   private String indicator;

   public ObjectStatus() {
   }

   private ObjectStatus(String composite) {
      this.composite = composite;
   }

   public static ObjectStatus parse(String composite, String escapedCompositeSeparator) {
      ObjectStatus objStatus = new ObjectStatus(composite);
      String[] parts = composite.split(escapedCompositeSeparator);

      for(int i = 0; i < parts.length; ++i) {
         switch (i) {
            case 0:
               objStatus.objectLength = parts[0].trim();
               break;
            case 1:
               objStatus.segmentCount = parts[1].trim();
               break;
            case 2:
               objStatus.transferSeq = parts[2].trim();
               break;
            case 3:
               objStatus.indicator = parts[3].trim();
         }
      }

      return objStatus;
   }

   public String getComposite() {
      return this.composite;
   }

   public String getObjectLength() {
      return this.objectLength;
   }

   public void setObjectLength(String objectLength) {
      this.objectLength = objectLength;
   }

   public String getSegmentCount() {
      return this.segmentCount;
   }

   public void setSegmentCount(String segmentCount) {
      this.segmentCount = segmentCount;
   }

   public String getTransferSequence() {
      return this.transferSeq;
   }

   public void setTransferSequence(String transferSeq) {
      this.transferSeq = transferSeq;
   }

   public String getIndicator() {
      return this.indicator;
   }

   public void setIndicator(String indicator) {
      this.indicator = indicator;
   }

   public String generate(String compositeSeparator) throws CompositeGenException {
      if (this.objectLength != null && !"".equals(this.objectLength)) {
         this.composite = "";
         if (this.indicator != null && !"".equals(this.indicator)) {
            this.composite = compositeSeparator + this.indicator;
         }

         if (this.transferSeq != null && !"".equals(this.transferSeq)) {
            this.composite = compositeSeparator + this.transferSeq + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.segmentCount != null && !"".equals(this.segmentCount)) {
            this.composite = compositeSeparator + this.segmentCount + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         this.composite = this.objectLength + this.composite;
         return this.composite;
      } else {
         throw new CompositeGenException("Mandatory field Missing.");
      }
   }
}
