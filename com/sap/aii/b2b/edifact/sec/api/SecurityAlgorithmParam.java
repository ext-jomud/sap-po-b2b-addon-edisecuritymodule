package com.sap.aii.b2b.edifact.sec.api;

public class SecurityAlgorithmParam {
   private String composite;
   private String algorithmQual;
   private String algorithmValue;

   public SecurityAlgorithmParam() {
   }

   private SecurityAlgorithmParam(String composite) {
      this.composite = composite;
   }

   public static SecurityAlgorithmParam parse(String composite, String escapedCompositeSeparator) {
      SecurityAlgorithmParam algoComp = new SecurityAlgorithmParam(composite);
      String[] dataElements = composite.split(escapedCompositeSeparator);
      int deLength = dataElements.length;

      for(int i = 0; i < deLength; ++i) {
         switch (deLength) {
            case 1:
               algoComp.algorithmQual = dataElements[0].trim();
               break;
            case 2:
               algoComp.algorithmValue = dataElements[1].trim();
         }
      }

      return algoComp;
   }

   public String getComposite() {
      return this.composite;
   }

   public String getAlgorithmQual() {
      return this.algorithmQual;
   }

   public String getAlgorithmValue() {
      return this.algorithmValue;
   }

   public void setAlgorithmQual(String algorithmQual) {
      this.algorithmQual = algorithmQual;
   }

   public void setAlgorithmValue(String algorithmValue) {
      this.algorithmValue = algorithmValue;
   }

   public String generate(String compositeSepartor) throws CompositeGenException {
      if (this.algorithmQual != null && !"".equals(this.algorithmQual) && this.algorithmValue != null && !"".equals(this.algorithmValue)) {
         this.composite = this.algorithmQual + compositeSepartor + this.algorithmValue;
         return this.composite;
      } else {
         throw new CompositeGenException("Mandaotry fields missing.");
      }
   }
}
