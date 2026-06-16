package com.sap.aii.b2b.edifact.sec.api;

import java.util.ArrayList;
import java.util.List;

public class UNOSegment {
   private String segment;
   private static final int REFERENCE_ID_MAXCOUNT = 99;
   private static final int OBJECT_TYPE_MAXCOUNT = 99;
   private String pkgRefNum;
   private List<ReferenceIdentification> referenceIDs = new ArrayList();
   private List<ObjectTypeIdentification> objectTypeIDs = new ArrayList();
   private ObjectStatus objectStatus;
   private DialogueReference dialogueReference;
   private TransferStatus transferStatus;
   private InitiationTimestamp initiationTimestamp;
   private String testIndicator;

   public UNOSegment() {
   }

   private UNOSegment(String segment) {
      this.segment = segment;
   }

   public static UNOSegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator) {
      UNOSegment uno = new UNOSegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);
      String escapedCompositeSeperator = "\\" + compositeSeparator;
      int refIDsCount = 0;
      int objTypeCount = 0;

      for(int i = 1; i < dataElements.length; ++i) {
         int index = i - refIDsCount - objTypeCount;
         switch (index) {
            case 1:
               uno.pkgRefNum = dataElements[i].trim();
               break;
            case 2:
               uno.referenceIDs.add(ReferenceIdentification.parse(dataElements[i], escapedCompositeSeperator));
               if (refIDsCount < 98 && i < dataElements.length - 1 && dataElements[i].split(escapedCompositeSeperator).length == dataElements[i + 1].split(escapedCompositeSeperator).length) {
                  ++refIDsCount;
               }
               break;
            case 3:
               uno.objectTypeIDs.add(ObjectTypeIdentification.parse(dataElements[i], escapedCompositeSeperator));
               if (objTypeCount < 98 && i < dataElements.length - 1 && dataElements[i].split(escapedCompositeSeperator).length == dataElements[i + 1].split(escapedCompositeSeperator).length) {
                  ++objTypeCount;
               }
               break;
            case 4:
               uno.objectStatus = ObjectStatus.parse(dataElements[i], escapedCompositeSeperator);
               break;
            case 5:
               uno.dialogueReference = DialogueReference.parse(dataElements[i], escapedCompositeSeperator);
               break;
            case 6:
               uno.transferStatus = TransferStatus.parse(dataElements[i], escapedCompositeSeperator);
               break;
            case 7:
               uno.initiationTimestamp = InitiationTimestamp.parse(dataElements[i], escapedCompositeSeperator);
               break;
            case 8:
               uno.testIndicator = dataElements[i].trim();
         }
      }

      return uno;
   }

   public static UNOSegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator, char repetitionSeperator) {
      UNOSegment uno = new UNOSegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);
      String escapedCompositeSeperator = "\\" + compositeSeparator;
      int refIDsCount = 0;
      int objTypeCount = 0;

      for(int i = 1; i < dataElements.length; ++i) {
         int index = i - refIDsCount - objTypeCount;
         switch (index) {
            case 1:
               uno.pkgRefNum = dataElements[i].trim();
               break;
            case 2:
               String[] refIDsComposites = dataElements[i].split("\\" + repetitionSeperator);

               for(String refIDsComp : refIDsComposites) {
                  uno.referenceIDs.add(ReferenceIdentification.parse(refIDsComp, compositeSeparator + ""));
               }
               break;
            case 3:
               String[] objTypeIDsComposites = dataElements[i].split("\\" + repetitionSeperator);

               for(String objTypeIDsComp : objTypeIDsComposites) {
                  uno.objectTypeIDs.add(ObjectTypeIdentification.parse(objTypeIDsComp, compositeSeparator + ""));
               }
               break;
            case 4:
               uno.objectStatus = ObjectStatus.parse(dataElements[i], escapedCompositeSeperator);
               break;
            case 5:
               uno.dialogueReference = DialogueReference.parse(dataElements[i], escapedCompositeSeperator);
               break;
            case 6:
               uno.transferStatus = TransferStatus.parse(dataElements[i], escapedCompositeSeperator);
               break;
            case 7:
               uno.initiationTimestamp = InitiationTimestamp.parse(dataElements[i], escapedCompositeSeperator);
               break;
            case 8:
               uno.testIndicator = dataElements[i].trim();
         }
      }

      return uno;
   }

   public String getSegment() {
      return this.segment;
   }

   public String getPkgRefNum() {
      return this.pkgRefNum;
   }

   public void setPkgRefNum(String pkgRefNum) {
      this.pkgRefNum = pkgRefNum;
   }

   public List<ReferenceIdentification> getReferenceIDs() {
      return this.referenceIDs;
   }

   public void setReferenceIDs(List<ReferenceIdentification> referenceIDs) {
      this.referenceIDs = referenceIDs;
   }

   public List<ObjectTypeIdentification> getObjectTypeIDs() {
      return this.objectTypeIDs;
   }

   public void setObjectTypeIDs(List<ObjectTypeIdentification> objectTypeIDs) {
      this.objectTypeIDs = objectTypeIDs;
   }

   public ObjectStatus getObjectStatus() {
      return this.objectStatus;
   }

   public void setObjectStatus(ObjectStatus objectStatus) {
      this.objectStatus = objectStatus;
   }

   public DialogueReference getDialogueReference() {
      return this.dialogueReference;
   }

   public void setDialogueReference(DialogueReference dialogueReference) {
      this.dialogueReference = dialogueReference;
   }

   public TransferStatus getTransferStatus() {
      return this.transferStatus;
   }

   public void setTransferStatus(TransferStatus transferStatus) {
      this.transferStatus = transferStatus;
   }

   public InitiationTimestamp getInitiationTimestamp() {
      return this.initiationTimestamp;
   }

   public void setInitiationTimestamp(InitiationTimestamp initiationTimestamp) {
      this.initiationTimestamp = initiationTimestamp;
   }

   public String getTestIndicator() {
      return this.testIndicator;
   }

   public void setTestIndicator(String testIndicator) {
      this.testIndicator = testIndicator;
   }

   public String generate(char dataElementSeparator, char compositeSeparator) throws SegmentGenException, CompositeGenException {
      if (this.pkgRefNum != null && !"".equals(this.pkgRefNum) && this.referenceIDs != null && this.objectTypeIDs != null && this.objectStatus != null) {
         this.segment = "";
         if (this.testIndicator != null && !"".equals(this.testIndicator)) {
            this.segment = dataElementSeparator + this.testIndicator;
         }

         if (this.initiationTimestamp != null) {
            this.segment = dataElementSeparator + this.initiationTimestamp.generate(compositeSeparator + "") + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.transferStatus != null) {
            this.segment = dataElementSeparator + this.transferStatus.generate(compositeSeparator + "") + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.dialogueReference != null) {
            this.segment = dataElementSeparator + this.dialogueReference.generate(compositeSeparator + "") + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         this.segment = dataElementSeparator + this.objectStatus.generate(compositeSeparator + "") + this.segment;

         for(int i = this.objectTypeIDs.size() - 1; i >= 0; --i) {
            this.segment = dataElementSeparator + ((ObjectTypeIdentification)this.objectTypeIDs.get(i)).generate(compositeSeparator + "") + this.segment;
         }

         for(int i = this.referenceIDs.size() - 1; i >= 0; --i) {
            this.segment = dataElementSeparator + ((ReferenceIdentification)this.referenceIDs.get(i)).generate(compositeSeparator + "") + this.segment;
         }

         this.segment = "UNO" + dataElementSeparator + this.pkgRefNum + this.segment;
         return this.segment;
      } else {
         throw new SegmentGenException("Mandatory data elements missing");
      }
   }

   public String generate(char dataElementSeparator, char compositeSeparator, char repetitionChar) throws SegmentGenException, CompositeGenException {
      if (this.pkgRefNum != null && !"".equals(this.pkgRefNum) && this.referenceIDs != null && this.objectTypeIDs != null && this.objectStatus != null) {
         this.segment = "";
         if (this.testIndicator != null && !"".equals(this.testIndicator)) {
            this.segment = dataElementSeparator + this.testIndicator;
         }

         if (this.initiationTimestamp != null) {
            this.segment = dataElementSeparator + this.initiationTimestamp.generate(compositeSeparator + "") + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.transferStatus != null) {
            this.segment = dataElementSeparator + this.transferStatus.generate(compositeSeparator + "") + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.dialogueReference != null) {
            this.segment = dataElementSeparator + this.dialogueReference.generate(compositeSeparator + "") + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         this.segment = dataElementSeparator + this.objectStatus.generate(compositeSeparator + "") + this.segment;
         this.segment = ((ObjectTypeIdentification)this.objectTypeIDs.get(this.objectTypeIDs.size() - 1)).generate(compositeSeparator + "") + this.segment;

         for(int i = this.objectTypeIDs.size() - 2; i >= 0; --i) {
            this.segment = ((ObjectTypeIdentification)this.objectTypeIDs.get(i)).generate(compositeSeparator + "") + repetitionChar + this.segment;
         }

         this.segment = dataElementSeparator + this.segment;
         this.segment = ((ReferenceIdentification)this.referenceIDs.get(this.referenceIDs.size() - 1)).generate(compositeSeparator + "") + this.segment;

         for(int i = this.referenceIDs.size() - 2; i >= 0; --i) {
            this.segment = ((ReferenceIdentification)this.referenceIDs.get(i)).generate(compositeSeparator + "") + repetitionChar + this.segment;
         }

         this.segment = dataElementSeparator + this.segment;
         this.segment = "UNO" + dataElementSeparator + this.pkgRefNum + this.segment;
         return this.segment;
      } else {
         throw new SegmentGenException("Mandatory data elements missing");
      }
   }
}
