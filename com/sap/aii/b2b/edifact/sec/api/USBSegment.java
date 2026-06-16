package com.sap.aii.b2b.edifact.sec.api;

public class USBSegment {
   private SecurityTimestamp timeStamp;
   private String segment;
   private String responseType;
   private InterchangePartner senderInterchange;
   private InterchangePartner receiverInterchange;

   public USBSegment() {
   }

   private USBSegment(String segment) {
      this.segment = segment;
   }

   public static USBSegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator) {
      USBSegment usbSeg = new USBSegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);
      String escapedCompositeSeparator = "\\" + compositeSeparator;
      int deLength = dataElements.length;

      for(int i = 1; i < deLength; ++i) {
         switch (i) {
            case 1:
               usbSeg.responseType = dataElements[i].trim();
               break;
            case 2:
               usbSeg.timeStamp = SecurityTimestamp.parse(dataElements[i], escapedCompositeSeparator);
               break;
            case 3:
               usbSeg.senderInterchange = InterchangePartner.parse(dataElements[i], escapedCompositeSeparator);
               break;
            case 4:
               usbSeg.receiverInterchange = InterchangePartner.parse(dataElements[i], escapedCompositeSeparator);
         }
      }

      return usbSeg;
   }

   public String getSegment() {
      return this.segment;
   }

   public String getResponseType() {
      return this.responseType;
   }

   public InterchangePartner getSenderInterchange() {
      return this.senderInterchange;
   }

   public InterchangePartner getReceiverInterchange() {
      return this.receiverInterchange;
   }

   public SecurityTimestamp getTimeStamp() {
      return this.timeStamp;
   }

   public void setTimeStamp(SecurityTimestamp timeStamp) {
      this.timeStamp = timeStamp;
   }

   public void setResponseType(String responseType) {
      this.responseType = responseType;
   }

   public void setSenderInterchange(InterchangePartner senderInterchange) {
      this.senderInterchange = senderInterchange;
   }

   public void setReceiverInterchange(InterchangePartner receiverInterchange) {
      this.receiverInterchange = receiverInterchange;
   }

   public String generate(char dataElementSeparator, char compositeSeparator) throws SegmentGenException, CompositeGenException {
      if (this.responseType != null && !"".equals(this.responseType) && this.senderInterchange != null && this.receiverInterchange != null) {
         this.segment = "USB" + dataElementSeparator + this.responseType + dataElementSeparator;
         if (this.timeStamp != null) {
            this.segment = this.segment + this.timeStamp.generate(compositeSeparator + "");
         }

         this.segment = this.segment + dataElementSeparator + this.senderInterchange.generate(compositeSeparator + "") + dataElementSeparator + this.receiverInterchange.generate(compositeSeparator + "");
         return this.segment;
      } else {
         throw new SegmentGenException("Mandatory data elements missing");
      }
   }
}
