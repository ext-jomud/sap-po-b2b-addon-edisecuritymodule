package com.sap.aii.b2b.edifact.sec.api;

public class MessageIdentifier {
   private String composite;
   private String msgType;
   private String msgVersion;
   private String msgRelNumber;
   private String controllingAgency;
   private String associationCode;
   private String dirVersionNumber;
   private String msgTypeSubFuncID;

   public MessageIdentifier() {
   }

   private MessageIdentifier(String composite) {
      this.composite = composite;
   }

   public static MessageIdentifier parse(String composite, String escapedCompositeSeparator) {
      MessageIdentifier msgID = new MessageIdentifier(composite);
      String[] parts = composite.split(escapedCompositeSeparator);

      for(int i = 0; i < parts.length; ++i) {
         switch (i) {
            case 0:
               msgID.msgType = parts[0].trim();
               break;
            case 1:
               msgID.msgVersion = parts[1].trim();
               break;
            case 2:
               msgID.msgRelNumber = parts[2].trim();
               break;
            case 3:
               msgID.controllingAgency = parts[3].trim();
               break;
            case 4:
               msgID.associationCode = parts[4].trim();
               break;
            case 5:
               msgID.dirVersionNumber = parts[5].trim();
               break;
            case 6:
               msgID.msgTypeSubFuncID = parts[6].trim();
         }
      }

      return msgID;
   }

   public String getMsgType() {
      return this.msgType;
   }

   public String getMsgVersion() {
      return this.msgVersion;
   }

   public String getMsgRelNumber() {
      return this.msgRelNumber;
   }

   public String getControllingAgency() {
      return this.controllingAgency;
   }

   public String getAssociationCode() {
      return this.associationCode;
   }

   public String getDirVersionNumber() {
      return this.dirVersionNumber;
   }

   public String getMsgTypeSubFuncID() {
      return this.msgTypeSubFuncID;
   }

   public String getComposite() {
      return this.composite;
   }

   public void setMsgType(String msgType) {
      this.msgType = msgType;
   }

   public void setMsgVersion(String msgVersion) {
      this.msgVersion = msgVersion;
   }

   public void setMsgRelNumber(String msgRelNumber) {
      this.msgRelNumber = msgRelNumber;
   }

   public void setControllingAgency(String controllingAgency) {
      this.controllingAgency = controllingAgency;
   }

   public void setAssociationCode(String associationCode) {
      this.associationCode = associationCode;
   }

   public void setDirVersionNumber(String dirVersionNumber) {
      this.dirVersionNumber = dirVersionNumber;
   }

   public void setMsgTypeSubFuncID(String msgTypeSubFuncID) {
      this.msgTypeSubFuncID = msgTypeSubFuncID;
   }

   public String generate(String compositeSeparator) throws CompositeGenException {
      if (this.msgType != null && !"".equals(this.msgType) && this.msgVersion != null && !"".equals(this.msgVersion) && this.msgRelNumber != null && !"".equals(this.msgRelNumber) && this.controllingAgency != null && !"".equals(this.controllingAgency)) {
         this.composite = "";
         if (this.msgTypeSubFuncID != null && !"".equals(this.msgTypeSubFuncID)) {
            this.composite = compositeSeparator + this.msgTypeSubFuncID;
         }

         if (this.dirVersionNumber != null && !"".equals(this.dirVersionNumber)) {
            this.composite = compositeSeparator + this.dirVersionNumber + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         if (this.associationCode != null && !"".equals(this.associationCode)) {
            this.composite = compositeSeparator + this.associationCode + this.composite;
         } else if (!"".equals(this.composite)) {
            this.composite = compositeSeparator + this.composite;
         }

         this.composite = this.msgType + compositeSeparator + this.msgVersion + compositeSeparator + this.msgRelNumber + compositeSeparator + this.controllingAgency + this.composite;
         return this.composite;
      } else {
         throw new CompositeGenException("Mandatory Fields Missing.");
      }
   }
}
