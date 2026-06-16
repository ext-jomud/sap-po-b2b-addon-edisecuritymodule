package com.sap.aii.b2b.edifact.sec.api;

public class SecurityID {
   private String composite;
   private String secPartyQual;
   private String keyName;
   private String secPartyID;
   private String secPartyCLQual;
   private String secPartyCLAgency;
   private String secPartyName1;
   private String secPartyName2;
   private String secPartyName3;

   public SecurityID() {
   }

   private SecurityID(String composite) {
      this.composite = composite;
   }

   public static SecurityID parse(String composite, String escapedCompositeSeparator) {
      SecurityID secID = new SecurityID(composite);
      String[] parts = composite.split(escapedCompositeSeparator);

      for(int i = 0; i < parts.length; ++i) {
         switch (i) {
            case 0:
               secID.secPartyQual = parts[0].trim();
               break;
            case 1:
               secID.keyName = parts[1].trim();
               break;
            case 2:
               secID.secPartyID = parts[2].trim();
               break;
            case 3:
               secID.secPartyCLQual = parts[3].trim();
               break;
            case 4:
               secID.secPartyCLAgency = parts[4].trim();
               break;
            case 5:
               secID.secPartyName1 = parts[5].trim();
               break;
            case 6:
               secID.secPartyName2 = parts[6].trim();
               break;
            case 7:
               secID.secPartyName3 = parts[7].trim();
         }
      }

      return secID;
   }

   public String getComposite() {
      return this.composite;
   }

   public String getSecPartyQual() {
      return this.secPartyQual;
   }

   public String getKeyName() {
      return this.keyName;
   }

   public String getSecPartyID() {
      return this.secPartyID;
   }

   public String getSecPartyCLQual() {
      return this.secPartyCLQual;
   }

   public String getSecPartyCLAgency() {
      return this.secPartyCLAgency;
   }

   public String getSecPartyName1() {
      return this.secPartyName1;
   }

   public String getSecPartyName2() {
      return this.secPartyName2;
   }

   public String getSecPartyName3() {
      return this.secPartyName3;
   }

   public void setSecPartyQual(String secPartyQual) {
      this.secPartyQual = secPartyQual;
   }

   public void setKeyName(String keyName) {
      this.keyName = keyName;
   }

   public void setSecPartyID(String secPartyID) {
      this.secPartyID = secPartyID;
   }

   public void setSecPartyCLQual(String secPartyCLQual) {
      this.secPartyCLQual = secPartyCLQual;
   }

   public void setSecPartyCLAgency(String secPartyCLAgency) {
      this.secPartyCLAgency = secPartyCLAgency;
   }

   public void setSecPartyName1(String secPartyName1) {
      this.secPartyName1 = secPartyName1;
   }

   public void setSecPartyName2(String secPartyName2) {
      this.secPartyName2 = secPartyName2;
   }

   public void setSecPartyName3(String secPartyName3) {
      this.secPartyName3 = secPartyName3;
   }

   public String generate(String compositeSeparator) throws CompositeGenException {
      if (this.secPartyQual != null && !"".equals(this.secPartyQual)) {
         this.composite = "";
         if (this.secPartyName3 != null && !"".equals(this.secPartyName3)) {
            this.composite = compositeSeparator + this.secPartyName3;
         }

         if (this.secPartyName2 != null && !"".equals(this.secPartyName2)) {
            this.composite = compositeSeparator + this.secPartyName2 + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.secPartyName1 != null && !"".equals(this.secPartyName1)) {
            this.composite = compositeSeparator + this.secPartyName1 + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.secPartyCLAgency != null && !"".equals(this.secPartyCLAgency)) {
            this.composite = compositeSeparator + this.secPartyCLAgency + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.secPartyCLQual != null && !"".equals(this.secPartyCLQual)) {
            this.composite = compositeSeparator + this.secPartyCLQual + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.secPartyID != null && !"".equals(this.secPartyID)) {
            this.composite = compositeSeparator + this.secPartyID + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.keyName != null && !"".equals(this.keyName)) {
            this.composite = compositeSeparator + this.keyName + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         this.composite = this.secPartyQual + this.composite;
         return this.composite;
      } else {
         throw new CompositeGenException("Mandtory field missing");
      }
   }
}
