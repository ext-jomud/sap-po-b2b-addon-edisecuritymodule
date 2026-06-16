package com.sap.aii.b2b.edifact.sec.api;

public class DialogueReference {
   private String composite;
   private String initiatorControlRef;
   private String initiatorRefID;
   private String controllingAgency;
   private String responderControlRef;

   public DialogueReference() {
   }

   private DialogueReference(String composite) {
      this.composite = composite;
   }

   public static DialogueReference parse(String composite, String escapedCompositeSeparator) {
      DialogueReference dialRef = new DialogueReference(composite);
      String[] parts = composite.split(escapedCompositeSeparator);

      for(int i = 0; i < parts.length; ++i) {
         switch (i) {
            case 0:
               dialRef.initiatorControlRef = parts[0].trim();
               break;
            case 1:
               dialRef.initiatorRefID = parts[1].trim();
               break;
            case 2:
               dialRef.controllingAgency = parts[2].trim();
               break;
            case 3:
               dialRef.responderControlRef = parts[3].trim();
         }
      }

      return dialRef;
   }

   public String getComposite() {
      return this.composite;
   }

   public String getInitiatorControlReference() {
      return this.initiatorControlRef;
   }

   public void setInitiatorControlReference(String initiatorControlRef) {
      this.initiatorControlRef = initiatorControlRef;
   }

   public String getInitiatorRefID() {
      return this.initiatorRefID;
   }

   public void setInitiatorReferenceID(String initiatorRefID) {
      this.initiatorRefID = initiatorRefID;
   }

   public String getControllingAgency() {
      return this.controllingAgency;
   }

   public void setControllingAgency(String controllingAgency) {
      this.controllingAgency = controllingAgency;
   }

   public String getResponderControlReference() {
      return this.responderControlRef;
   }

   public void setResponderControlReference(String responderControlRef) {
      this.responderControlRef = responderControlRef;
   }

   public String generate(String compositeSeparator) throws CompositeGenException {
      if (this.initiatorControlRef != null && !"".equals(this.initiatorControlRef)) {
         this.composite = "";
         if (this.responderControlRef != null && !"".equals(this.responderControlRef)) {
            this.composite = compositeSeparator + this.responderControlRef;
         }

         if (this.controllingAgency != null && !"".equals(this.controllingAgency)) {
            this.composite = compositeSeparator + this.controllingAgency + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.initiatorRefID != null && !"".equals(this.initiatorRefID)) {
            this.composite = compositeSeparator + this.initiatorRefID + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         this.composite = this.initiatorControlRef + this.composite;
         return this.composite;
      } else {
         throw new CompositeGenException("Mandatory field Missing.");
      }
   }
}
