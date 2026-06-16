package com.sap.aii.b2b.edifact.sec.api;

public class USTSegment {
   private String segment;
   private String secRefNum;
   private String totalSegments;

   public USTSegment() {
   }

   private USTSegment(String segment) {
      this.segment = segment;
   }

   public static USTSegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator) {
      USTSegment ustSeg = new USTSegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);
      int deLength = dataElements.length;

      for(int i = 1; i < deLength; ++i) {
         switch (i) {
            case 1:
               ustSeg.secRefNum = dataElements[i].trim();
               break;
            case 2:
               ustSeg.totalSegments = dataElements[i].trim();
         }
      }

      return ustSeg;
   }

   public String getSegment() {
      return this.segment;
   }

   public String getSecRefNum() {
      return this.secRefNum;
   }

   public String getTotalSegments() {
      return this.totalSegments;
   }

   public void setSecRefNum(String secRefNum) {
      this.secRefNum = secRefNum;
   }

   public void setTotalSegments(String totalSegments) {
      this.totalSegments = totalSegments;
   }

   public String generate(char dataElementSeparator, char compositeSeparator) throws SegmentGenException, CompositeGenException {
      if (this.secRefNum != null && !"".equals(this.secRefNum) && this.totalSegments != null && !"".equals(this.totalSegments)) {
         this.segment = "UST" + dataElementSeparator + this.secRefNum + dataElementSeparator + this.totalSegments;
         return this.segment;
      } else {
         throw new SegmentGenException("Mandatory data elements missing");
      }
   }
}
