package com.sap.aii.b2b.edifact.sec.api;

public class ReferenceIdentification {
   private String composite;
   private String referenceQual;
   private String referenceID;

   public ReferenceIdentification() {
   }

   private ReferenceIdentification(String composite) {
      this.composite = composite;
   }

   public static ReferenceIdentification parse(String composite, String escapedCompositeSeparator) {
      ReferenceIdentification refID = new ReferenceIdentification(composite);
      String[] parts = composite.split(escapedCompositeSeparator);

      for(int i = 0; i < parts.length; ++i) {
         switch (i) {
            case 0:
               refID.referenceQual = parts[0].trim();
               break;
            case 1:
               refID.referenceID = parts[1].trim();
         }
      }

      return refID;
   }

   public String getComposite() {
      return this.composite;
   }

   public String getReferenceQual() {
      return this.referenceQual;
   }

   public void setReferenceQual(String referenceQual) {
      this.referenceQual = referenceQual;
   }

   public String getReferenceID() {
      return this.referenceID;
   }

   public void setReferenceID(String referenceID) {
      this.referenceID = referenceID;
   }

   public String generate(String compositeSeparator) throws CompositeGenException {
      if (this.referenceQual != null && !"".equals(this.referenceQual) && this.referenceID != null && !"".equals(this.referenceID)) {
         this.composite = this.referenceQual + compositeSeparator + this.referenceID;
         return this.composite;
      } else {
         throw new CompositeGenException("Mandatory fields Missing.");
      }
   }
}
