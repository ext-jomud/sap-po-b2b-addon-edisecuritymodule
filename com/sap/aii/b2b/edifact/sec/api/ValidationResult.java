package com.sap.aii.b2b.edifact.sec.api;

public class ValidationResult {
   private String composite;
   private String validationQual;
   private String validationValue;

   public ValidationResult() {
   }

   private ValidationResult(String composite) {
      this.composite = composite;
   }

   public static ValidationResult parse(String composite, String escapedCompositeSeparator) {
      ValidationResult result = new ValidationResult(composite);
      String[] parts = composite.split(escapedCompositeSeparator);

      for(int i = 0; i < parts.length; ++i) {
         switch (i) {
            case 0:
               result.validationQual = parts[0].trim();
               break;
            case 1:
               result.validationValue = parts[1].trim();
         }
      }

      return result;
   }

   public String getComposite() {
      return this.composite;
   }

   public String getValidationQual() {
      return this.validationQual;
   }

   public String getValidationValue() {
      return this.validationValue;
   }

   public void setValidationQual(String validationQual) {
      this.validationQual = validationQual;
   }

   public void setValidationValue(String validationValue) {
      this.validationValue = validationValue;
   }

   public String generate(String compositeSeparator) throws CompositeGenException {
      if (this.validationQual != null && !"".equals(this.validationQual)) {
         this.composite = this.validationQual;
         if (this.validationValue != null && !"".equals(this.validationValue)) {
            this.composite = this.composite + compositeSeparator + this.validationValue;
         }

         return this.composite;
      } else {
         throw new CompositeGenException("Mandatory field missing.");
      }
   }
}
