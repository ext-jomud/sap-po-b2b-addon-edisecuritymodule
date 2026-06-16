package com.sap.aii.b2b.edifact.sec.api;

public class InterchangePartner {
   private String composite;
   private String partnerID;
   private String partnerQual;
   private String partnerInternalID;
   private String partnerInternalSubID;

   public InterchangePartner() {
   }

   private InterchangePartner(String composite) {
      this.composite = composite;
   }

   public static InterchangePartner parse(String composite, String escapedCompositeSeparator) {
      InterchangePartner partner = new InterchangePartner(composite);
      String[] parts = composite.split(escapedCompositeSeparator);

      for(int i = 0; i < parts.length; ++i) {
         switch (i) {
            case 0:
               partner.partnerID = parts[0].trim();
               break;
            case 1:
               partner.partnerQual = parts[1].trim();
               break;
            case 2:
               partner.partnerInternalID = parts[2].trim();
               break;
            case 3:
               partner.partnerInternalSubID = parts[3].trim();
         }
      }

      return partner;
   }

   public String getPartnerID() {
      return this.partnerID;
   }

   public String getPartnerQual() {
      return this.partnerQual;
   }

   public String getPartnerInternalID() {
      return this.partnerInternalID;
   }

   public String getPartnerInternalSubID() {
      return this.partnerInternalSubID;
   }

   public String getComposite() {
      return this.composite;
   }

   public void setPartnerID(String partnerID) {
      this.partnerID = partnerID;
   }

   public void setPartnerQual(String partnerQual) {
      this.partnerQual = partnerQual;
   }

   public void setPartnerInternalID(String partnerInternalID) {
      this.partnerInternalID = partnerInternalID;
   }

   public void setPartnerInternalSubID(String partnerInternalSubID) {
      this.partnerInternalSubID = partnerInternalSubID;
   }

   public String generate(String compositeSeparator) throws CompositeGenException {
      if (this.partnerID != null && !"".equals(this.partnerID)) {
         this.composite = "";
         if (this.partnerInternalSubID != null && !"".equals(this.partnerInternalSubID)) {
            this.composite = compositeSeparator + this.partnerInternalSubID;
         }

         if (this.partnerInternalID != null && !"".equals(this.partnerInternalID)) {
            this.composite = compositeSeparator + this.partnerInternalID + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.partnerQual != null && !"".equals(this.partnerQual)) {
            this.composite = compositeSeparator + this.partnerQual + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         this.composite = this.partnerID + this.composite;
         return this.composite;
      } else {
         throw new CompositeGenException("Mandatory field Missing.");
      }
   }
}
