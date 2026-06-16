package com.sap.aii.b2b.edifact.sec.api;

public class InitiationTimestamp {
   private String composite;
   private String eventDate;
   private String eventTime;
   private String timeOffset;

   public InitiationTimestamp() {
   }

   private InitiationTimestamp(String composite) {
      this.composite = composite;
   }

   public static InitiationTimestamp parse(String composite, String escapedCompositeSeparator) {
      InitiationTimestamp timestamp = new InitiationTimestamp(composite);
      String[] parts = composite.split(escapedCompositeSeparator);

      for(int i = 0; i < parts.length; ++i) {
         switch (i) {
            case 0:
               timestamp.eventDate = parts[0].trim();
               break;
            case 1:
               timestamp.eventTime = parts[1].trim();
               break;
            case 2:
               timestamp.timeOffset = parts[2].trim();
         }
      }

      return timestamp;
   }

   public String getComposite() {
      return this.composite;
   }

   public String getEventDate() {
      return this.eventDate;
   }

   public void setEventDate(String eventDate) {
      this.eventDate = eventDate;
   }

   public String getEventTime() {
      return this.eventTime;
   }

   public void setEventTime(String eventTime) {
      this.eventTime = eventTime;
   }

   public String getTimeOffset() {
      return this.timeOffset;
   }

   public void setTimeOffset(String timeOffset) {
      this.timeOffset = timeOffset;
   }

   public String generate(String compositeSeparator) throws CompositeGenException {
      this.composite = "";
      if (this.timeOffset != null && !"".equals(this.timeOffset)) {
         this.composite = compositeSeparator + this.timeOffset;
      }

      if (this.eventTime != null && !"".equals(this.eventTime)) {
         this.composite = compositeSeparator + this.eventTime + this.composite;
      } else if (!"".equals(this.composite)) {
         this.composite = compositeSeparator + this.composite;
      }

      if (this.eventDate != null && !"".equals(this.eventDate)) {
         this.composite = this.eventDate + this.composite;
      } else if (!"".equals(this.composite)) {
         this.composite = compositeSeparator + this.composite;
      }

      return this.composite;
   }
}
