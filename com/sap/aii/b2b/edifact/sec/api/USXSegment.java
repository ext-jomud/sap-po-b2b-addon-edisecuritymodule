package com.sap.aii.b2b.edifact.sec.api;

public class USXSegment {
   private String segment;
   private String interchangeControlRef;
   private InterchangePartner interchangeSender;
   private InterchangePartner interchangeReceiver;
   private String groupRefNumber;
   private ApplicationPartner appSender;
   private ApplicationPartner appReceiver;
   private String msgRefNumber;
   private MessageIdentifier msgIdentifier;
   private String pkgRefNumber;
   private SecurityTimestamp securityTime;

   public USXSegment() {
   }

   private USXSegment(String segment) {
      this.segment = segment;
   }

   public static USXSegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator) {
      USXSegment usx = new USXSegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);
      String escapedCompositeSeparator = "\\" + compositeSeparator;

      for(int i = 1; i < dataElements.length; ++i) {
         switch (i) {
            case 1:
               usx.interchangeControlRef = dataElements[0].trim();
               break;
            case 2:
               usx.interchangeSender = InterchangePartner.parse(dataElements[1], escapedCompositeSeparator);
               break;
            case 3:
               usx.interchangeReceiver = InterchangePartner.parse(dataElements[2], escapedCompositeSeparator);
               break;
            case 4:
               usx.groupRefNumber = dataElements[3].trim();
               break;
            case 5:
               usx.appSender = ApplicationPartner.parse(dataElements[4], escapedCompositeSeparator);
               break;
            case 6:
               usx.appReceiver = ApplicationPartner.parse(dataElements[5], escapedCompositeSeparator);
               break;
            case 7:
               usx.msgRefNumber = dataElements[6].trim();
               break;
            case 8:
               usx.msgIdentifier = MessageIdentifier.parse(dataElements[7], escapedCompositeSeparator);
               break;
            case 9:
               usx.pkgRefNumber = dataElements[8].trim();
               break;
            case 10:
               usx.securityTime = SecurityTimestamp.parse(dataElements[9], escapedCompositeSeparator);
         }
      }

      return usx;
   }

   public String getInterchangeControlRef() {
      return this.interchangeControlRef;
   }

   public InterchangePartner getInterchangeSender() {
      return this.interchangeSender;
   }

   public InterchangePartner getInterchangeReceiver() {
      return this.interchangeReceiver;
   }

   public String getGroupRefNumber() {
      return this.groupRefNumber;
   }

   public ApplicationPartner getAppSender() {
      return this.appSender;
   }

   public ApplicationPartner getAppReceiver() {
      return this.appReceiver;
   }

   public String getMsgRefNumber() {
      return this.msgRefNumber;
   }

   public MessageIdentifier getMsgIdentifier() {
      return this.msgIdentifier;
   }

   public String getPkgRefNumber() {
      return this.pkgRefNumber;
   }

   public SecurityTimestamp getSecurityTime() {
      return this.securityTime;
   }

   public String getSegment() {
      return this.segment;
   }

   public void setInterchangeControlRef(String interchangeControlRef) {
      this.interchangeControlRef = interchangeControlRef;
   }

   public void setInterchangeSender(InterchangePartner interchangeSender) {
      this.interchangeSender = interchangeSender;
   }

   public void setInterchangeReceiver(InterchangePartner interchangeReceiver) {
      this.interchangeReceiver = interchangeReceiver;
   }

   public void setGroupRefNumber(String groupRefNumber) {
      this.groupRefNumber = groupRefNumber;
   }

   public void setAppSender(ApplicationPartner appSender) {
      this.appSender = appSender;
   }

   public void setAppReceiver(ApplicationPartner appReceiver) {
      this.appReceiver = appReceiver;
   }

   public void setMsgRefNumber(String msgRefNumber) {
      this.msgRefNumber = msgRefNumber;
   }

   public void setMsgIdentifier(MessageIdentifier msgIdentifier) {
      this.msgIdentifier = msgIdentifier;
   }

   public void setPkgRefNumber(String pkgRefNumber) {
      this.pkgRefNumber = pkgRefNumber;
   }

   public void setSecurityTime(SecurityTimestamp securityTime) {
      this.securityTime = securityTime;
   }

   public String generate(char dataElementSeparator, char compositeSeparator) throws SegmentGenException, CompositeGenException {
      if (this.interchangeControlRef != null && !"".equals(this.interchangeControlRef)) {
         this.segment = "";
         if (this.securityTime != null) {
            this.segment = dataElementSeparator + this.securityTime.generate(compositeSeparator + "");
         }

         if (this.pkgRefNumber != null && !"".equals(this.pkgRefNumber)) {
            this.segment = dataElementSeparator + this.pkgRefNumber + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.msgIdentifier != null) {
            this.segment = dataElementSeparator + this.msgIdentifier.generate(compositeSeparator + "") + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.msgRefNumber != null && !"".equals(this.msgRefNumber)) {
            this.segment = dataElementSeparator + this.msgRefNumber + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.appReceiver != null) {
            this.segment = dataElementSeparator + this.appReceiver.generate(compositeSeparator + "") + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.appSender != null) {
            this.segment = dataElementSeparator + this.appSender.generate(compositeSeparator + "") + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.groupRefNumber != null && !"".equals(this.groupRefNumber)) {
            this.segment = dataElementSeparator + this.groupRefNumber + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.interchangeReceiver != null) {
            this.segment = dataElementSeparator + this.interchangeReceiver.generate(compositeSeparator + "") + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.interchangeSender != null) {
            this.segment = dataElementSeparator + this.interchangeSender.generate(compositeSeparator + "") + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.interchangeControlRef != null && !"".equals(this.interchangeControlRef)) {
            this.segment = dataElementSeparator + this.interchangeControlRef + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         this.segment = "USX" + this.segment;
         return this.segment;
      } else {
         throw new SegmentGenException("Mandatory data elements missing");
      }
   }
}
