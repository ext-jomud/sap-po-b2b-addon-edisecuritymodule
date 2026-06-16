package com.sap.aii.b2b.edifact.sec.api;

public class ApplicationPartner {
   private String composite;
   private String applicationID;
   private String applicationQual;

   public ApplicationPartner() {
   }

   private ApplicationPartner(String composite) {
      this.composite = composite;
   }

   public static ApplicationPartner parse(String composite, String escapedCompositeSeparator) {
      ApplicationPartner partner = new ApplicationPartner(composite);
      String[] parts = composite.split(escapedCompositeSeparator);

      for(int i = 0; i < parts.length; ++i) {
         switch (i) {
            case 0:
               partner.applicationID = parts[0].trim();
               break;
            case 1:
               partner.applicationQual = parts[1].trim();
         }
      }

      return partner;
   }

   public String getApplicationID() {
      return this.applicationID;
   }

   public String getApplicationQual() {
      return this.applicationQual;
   }

   public String getComposite() {
      return this.composite;
   }

   public void setApplicationID(String applicationID) {
      this.applicationID = applicationID;
   }

   public void setApplicationQual(String applicationQual) {
      this.applicationQual = applicationQual;
   }

   public String generate(String compositeSeparator) throws CompositeGenException {
      if (this.applicationID != null && !"".equals(this.applicationID)) {
         this.composite = this.applicationID;
         if (this.applicationQual != null || !"".equals(this.applicationQual)) {
            this.composite = this.composite + compositeSeparator + this.applicationQual;
         }

         return this.composite;
      } else {
         throw new CompositeGenException("Mandatory field Missing.");
      }
   }
}
