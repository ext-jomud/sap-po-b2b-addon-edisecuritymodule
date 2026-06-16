package com.sap.aii.b2b.edifact.sec.api;

public class SecurityAlgorithm {
   private String composite;
   private String algorithm;
   private String cryptMode;
   private String operationId;
   private String algoCodeId;
   private String algoCodeCoded;
   private String paddingMech;
   private String paddingMechCoded;

   public SecurityAlgorithm() {
   }

   public SecurityAlgorithm(String composite) {
      this.composite = composite;
   }

   public static SecurityAlgorithm parse(String composite, String escapedCompositeSeparator) {
      SecurityAlgorithm algoComp = new SecurityAlgorithm(composite);
      String[] dataElements = composite.split(escapedCompositeSeparator);
      int deLength = dataElements.length;

      for(int i = 0; i < deLength; ++i) {
         switch (i) {
            case 0:
               algoComp.algorithm = dataElements[0].trim();
               break;
            case 1:
               algoComp.cryptMode = dataElements[1].trim();
               break;
            case 2:
               algoComp.operationId = dataElements[2].trim();
               break;
            case 3:
               algoComp.algoCodeId = dataElements[3].trim();
               break;
            case 4:
               algoComp.algoCodeCoded = dataElements[4].trim();
               break;
            case 5:
               algoComp.paddingMech = dataElements[5].trim();
               break;
            case 6:
               algoComp.paddingMechCoded = dataElements[6].trim();
         }
      }

      return algoComp;
   }

   public String getComposite() {
      return this.composite;
   }

   public String getAlgoCodeCoded() {
      return this.algoCodeCoded;
   }

   public String getAlgorithm() {
      return this.algorithm;
   }

   public String getCryptMode() {
      return this.cryptMode;
   }

   public String getOperationId() {
      return this.operationId;
   }

   public String getAlgoCodeId() {
      return this.algoCodeId;
   }

   public String getPaddingMech() {
      return this.paddingMech;
   }

   public String getPaddingMechCoded() {
      return this.paddingMechCoded;
   }

   public void setAlgoCodeCoded(String algoCoded) {
      this.algoCodeCoded = algoCoded;
   }

   public void setAlgorithm(String algorithm) {
      this.algorithm = algorithm;
   }

   public void setCryptMode(String cryptMode) {
      this.cryptMode = cryptMode;
   }

   public void setOperationId(String operationId) {
      this.operationId = operationId;
   }

   public void setAlgoCodeId(String algoCodeId) {
      this.algoCodeId = algoCodeId;
   }

   public void setPaddingMech(String paddingMech) {
      this.paddingMech = paddingMech;
   }

   public void setPaddingMechCoded(String paddingMechCoded) {
      this.paddingMechCoded = paddingMechCoded;
   }

   public String generate(String compositeSeparator) throws CompositeGenException {
      if (this.algorithm != null && !"".equals(this.algorithm)) {
         this.composite = "";
         if (this.paddingMechCoded != null && !"".equals(this.paddingMechCoded)) {
            this.composite = compositeSeparator + this.paddingMechCoded;
         }

         if (this.paddingMech != null && !"".equals(this.paddingMech)) {
            this.composite = compositeSeparator + this.paddingMech + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.algoCodeCoded != null && !"".equals(this.algoCodeCoded)) {
            this.composite = compositeSeparator + this.algoCodeCoded + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.algoCodeId != null && !"".equals(this.algoCodeId)) {
            this.composite = compositeSeparator + this.algoCodeId + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.operationId != null && !"".equals(this.operationId)) {
            this.composite = compositeSeparator + this.operationId + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.cryptMode != null && !"".equals(this.cryptMode)) {
            this.composite = compositeSeparator + this.cryptMode + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         this.composite = this.algorithm + this.composite;
         return this.composite;
      } else {
         throw new CompositeGenException("Mandtory field missing");
      }
   }
}
