package com.sap.aii.b2b.edifact.sec.api;

public class UNPSegment {
   private String pkgLength;
   private String pkgRefNum;
   private String segment;

   public UNPSegment() {
   }

   private UNPSegment(String segment) {
      this.segment = segment;
   }

   public static UNPSegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator) {
      UNPSegment unpSeg = new UNPSegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);
      int deLength = dataElements.length;

      for(int i = 1; i < deLength; ++i) {
         switch (i) {
            case 1:
               unpSeg.pkgLength = dataElements[i].trim();
               break;
            case 2:
               unpSeg.pkgRefNum = dataElements[i].trim();
         }
      }

      return unpSeg;
   }

   public String getSegment() {
      return this.segment;
   }

   public String getPkgLength() {
      return this.pkgLength;
   }

   public String getPkgRefNum() {
      return this.pkgRefNum;
   }

   public void setPkgLength(String pkgLength) {
      this.pkgLength = pkgLength;
   }

   public void setPkgRefNum(String pkgRefNum) {
      this.pkgRefNum = pkgRefNum;
   }

   public String generate(char dataElementSeparator, char compositeSeparator) throws SegmentGenException, CompositeGenException {
      if (this.pkgLength != null && !"".equals(this.pkgLength) && this.pkgRefNum != null && !"".equals(this.pkgRefNum)) {
         this.segment = "UNP" + dataElementSeparator + this.pkgLength + dataElementSeparator + this.pkgRefNum;
         return this.segment;
      } else {
         throw new SegmentGenException("Mandatory data elements missing");
      }
   }
}
