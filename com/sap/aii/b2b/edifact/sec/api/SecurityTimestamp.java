package com.sap.aii.b2b.edifact.sec.api;

public class SecurityTimestamp {
   private String composite;
   private String qualifier;
   private String date;
   private String time;
   private String timeOffset;

   public SecurityTimestamp() {
   }

   private SecurityTimestamp(String composite) {
      this.composite = composite;
   }

   public static SecurityTimestamp parse(String composite, String escapedCompositeSeparator) {
      SecurityTimestamp timestamp = new SecurityTimestamp(composite);
      String[] parts = composite.split(escapedCompositeSeparator);

      for(int i = 0; i < parts.length; ++i) {
         switch (i) {
            case 0:
               timestamp.qualifier = parts[0].trim();
               break;
            case 1:
               timestamp.date = parts[1].trim();
               break;
            case 2:
               timestamp.time = parts[2].trim();
               break;
            case 3:
               timestamp.timeOffset = parts[3].trim();
         }
      }

      return timestamp;
   }

   public String getQualifier() {
      return this.qualifier;
   }

   public String getDate() {
      return this.date;
   }

   public String getTime() {
      return this.time;
   }

   public String getTimeOffset() {
      return this.timeOffset;
   }

   public String getComposite() {
      return this.composite;
   }

   public void setQualifier(String qualifier) {
      this.qualifier = qualifier;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public void setTime(String time) {
      this.time = time;
   }

   public void setTimeOffset(String timeOffset) {
      this.timeOffset = timeOffset;
   }

   public String generate(String compositeSeparator) throws CompositeGenException {
      if (this.qualifier != null && !"".equals(this.qualifier)) {
         this.composite = "";
         if (this.timeOffset != null && !"".equals(this.timeOffset)) {
            this.composite = compositeSeparator + this.timeOffset;
         }

         if (this.time != null && !"".equals(this.time)) {
            this.composite = compositeSeparator + this.time + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.time != null && !"".equals(this.date)) {
            this.composite = compositeSeparator + this.date + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         this.composite = this.qualifier + this.composite;
         return this.composite;
      } else {
         throw new CompositeGenException("Mandatory field Missing");
      }
   }
}
