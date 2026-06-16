package com.sap.aii.b2b.edifact.sec.api;

public class ObjectTypeIdentification {
   private String composite;
   private String objTypeQual;
   private String objTypeAttrID;
   private String objTypeAttr;
   private String controllingAgency;

   public ObjectTypeIdentification() {
   }

   private ObjectTypeIdentification(String composite) {
      this.composite = composite;
   }

   public static ObjectTypeIdentification parse(String composite, String escapedCompositeSeparator) {
      ObjectTypeIdentification objTypeID = new ObjectTypeIdentification(composite);
      String[] parts = composite.split(escapedCompositeSeparator);

      for(int i = 0; i < parts.length; ++i) {
         switch (i) {
            case 0:
               objTypeID.objTypeQual = parts[0].trim();
               break;
            case 1:
               objTypeID.objTypeAttrID = parts[1].trim();
               break;
            case 2:
               objTypeID.objTypeAttr = parts[2].trim();
               break;
            case 3:
               objTypeID.controllingAgency = parts[3].trim();
         }
      }

      return objTypeID;
   }

   public String getComposite() {
      return this.composite;
   }

   public String getObjectTypeQual() {
      return this.objTypeQual;
   }

   public void setObjectTypeQual(String objTypeQual) {
      this.objTypeQual = objTypeQual;
   }

   public String getObjecTypeAttrID() {
      return this.objTypeAttrID;
   }

   public void setObjectTypeAttrID(String objTypeAttrID) {
      this.objTypeAttrID = objTypeAttrID;
   }

   public String getObjectTypeAttribute() {
      return this.objTypeAttr;
   }

   public void setObjectTypeAttribute(String objTypeAttr) {
      this.objTypeAttr = objTypeAttr;
   }

   public String getControllingAgency() {
      return this.controllingAgency;
   }

   public void setControllingAgency(String controllingAgency) {
      this.controllingAgency = controllingAgency;
   }

   public String generate(String compositeSeparator) throws CompositeGenException {
      if (this.objTypeQual != null && !"".equals(this.objTypeQual)) {
         this.composite = "";
         if (this.controllingAgency != null && !"".equals(this.controllingAgency)) {
            this.composite = compositeSeparator + this.controllingAgency;
         }

         if (this.objTypeAttr != null && !"".equals(this.objTypeAttr)) {
            this.composite = compositeSeparator + this.objTypeAttr + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.objTypeAttrID != null && !"".equals(this.objTypeAttrID)) {
            this.composite = compositeSeparator + this.objTypeAttrID + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         this.composite = this.objTypeQual + this.composite;
         return this.composite;
      } else {
         throw new CompositeGenException("Mandatory field Missing.");
      }
   }
}
