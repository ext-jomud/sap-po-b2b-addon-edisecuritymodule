package com.sap.aii.b2b.edifact.sec.api;

public class SignatureServiceCharacter {
   private String composite;
   private String serviceCharQual;
   private String serviceChar;

   public SignatureServiceCharacter() {
   }

   private SignatureServiceCharacter(String composite) {
      this.composite = composite;
   }

   public static SignatureServiceCharacter parse(String composite, String escapedCompositeSeparator) {
      SignatureServiceCharacter serviceChar = new SignatureServiceCharacter(composite);
      String[] parts = composite.split(escapedCompositeSeparator);

      for(int i = 0; i < parts.length; ++i) {
         switch (i) {
            case 0:
               serviceChar.serviceCharQual = parts[0].trim();
               break;
            case 1:
               serviceChar.serviceChar = parts[1].trim();
         }
      }

      return serviceChar;
   }

   public String getComposite() {
      return this.composite;
   }

   public String getServiceCharQual() {
      return this.serviceCharQual;
   }

   public String getServiceChar() {
      return this.serviceChar;
   }

   public void setServiceCharQual(String serviceCharQual) {
      this.serviceCharQual = serviceCharQual;
   }

   public void setServiceChar(String serviceChar) {
      this.serviceChar = serviceChar;
   }

   public String generate(String compositeSeparator) throws CompositeGenException {
      if (this.serviceChar != null && !"".equals(this.serviceChar) && this.serviceCharQual != null && !"".equals(this.serviceCharQual)) {
         this.composite = this.serviceCharQual + compositeSeparator + this.serviceChar;
         return this.composite;
      } else {
         throw new CompositeGenException("Mandatory Fields Missing");
      }
   }
}
