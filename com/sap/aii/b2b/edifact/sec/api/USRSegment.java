package com.sap.aii.b2b.edifact.sec.api;

import java.util.ArrayList;
import java.util.List;

public class USRSegment {
   private String segment;
   private List<ValidationResult> results = new ArrayList();

   public USRSegment() {
   }

   private USRSegment(String segment) {
      this.segment = segment;
   }

   public static USRSegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator) {
      USRSegment usr = new USRSegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String signStr = segment.substring(5);
      usr.results.add(ValidationResult.parse(signStr, "\\" + compositeSeparator));
      return usr;
   }

   public static USRSegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator, char repetitionSeparator) {
      USRSegment usr = new USRSegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);
      if (dataElements.length > 1) {
         String[] resultComposites = dataElements[1].split("\\" + repetitionSeparator);

         for(String resultComp : resultComposites) {
            usr.results.add(ValidationResult.parse(resultComp, compositeSeparator + ""));
         }
      }

      return usr;
   }

   public String getSegment() {
      return this.segment;
   }

   public List<ValidationResult> getResults() {
      return this.results;
   }

   public void setResults(List<ValidationResult> results) {
      this.results = results;
   }

   public String generate(char dataElementSeparator, char compositeSeparator) throws SegmentGenException, CompositeGenException {
      if (this.results == null) {
         throw new SegmentGenException("Mandatory data element missing");
      } else {
         this.segment = "USR";

         for(int i = 0; i < this.results.size(); ++i) {
            this.segment = this.segment + dataElementSeparator + ((ValidationResult)this.results.get(i)).generate(compositeSeparator + "");
         }

         return this.segment;
      }
   }

   public String generate(char dataElementSeparator, char compositeSeparator, char seperationChar) throws SegmentGenException, CompositeGenException {
      if (this.results == null) {
         throw new SegmentGenException("Mandatory data element missing");
      } else {
         this.segment = "USR";
         if (this.results.size() > 0) {
            this.segment = this.segment + dataElementSeparator + ((ValidationResult)this.results.get(0)).generate(compositeSeparator + "");
         }

         for(int i = 1; i < this.results.size(); ++i) {
            this.segment = this.segment + seperationChar + ((ValidationResult)this.results.get(i)).generate(compositeSeparator + "");
         }

         return this.segment;
      }
   }
}
