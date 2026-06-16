package com.sap.aii.b2b.edifact.sec.api;

import java.util.ArrayList;
import java.util.List;

public class USYSegment {
   private String segment;
   private static int VALIDATION_RESULTS_COUNT = 2;
   private String secRefNumber;
   private List<ValidationResult> results = new ArrayList();
   private String secErrorCode;

   public USYSegment() {
   }

   private USYSegment(String segment) {
      this.segment = segment;
   }

   public static USYSegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator) {
      USYSegment usy = new USYSegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);
      int valResultsCount = 0;
      String escapedCompositeSeparator = "\\" + compositeSeparator;

      for(int i = 1; i < dataElements.length; ++i) {
         int compositeIndex = i - valResultsCount;
         switch (compositeIndex) {
            case 1:
               usy.secRefNumber = dataElements[i].trim();
               break;
            case 2:
               usy.results.add(ValidationResult.parse(dataElements[i], escapedCompositeSeparator));
               if (valResultsCount < VALIDATION_RESULTS_COUNT - 1 && i < dataElements.length - 1 && dataElements[i].split(escapedCompositeSeparator).length == dataElements[i + 1].split(escapedCompositeSeparator).length) {
                  ++valResultsCount;
               }
               break;
            case 3:
               usy.secErrorCode = dataElements[i].trim();
         }
      }

      return usy;
   }

   public static USYSegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator, char repetitionSeparator) {
      USYSegment usy = new USYSegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);

      for(int i = 1; i < dataElements.length; ++i) {
         switch (i) {
            case 1:
               usy.secRefNumber = dataElements[0].trim();
               break;
            case 2:
               String[] resultComposites = dataElements[1].split("\\" + repetitionSeparator);

               for(String resultComp : resultComposites) {
                  usy.results.add(ValidationResult.parse(resultComp, compositeSeparator + ""));
               }
               break;
            case 3:
               usy.secErrorCode = dataElements[2].trim();
         }
      }

      return usy;
   }

   public String getSegment() {
      return this.segment;
   }

   public String getSecRefNumber() {
      return this.secRefNumber;
   }

   public List<ValidationResult> getResults() {
      return this.results;
   }

   public String getSecErrorCode() {
      return this.secErrorCode;
   }

   public void setSecRefNumber(String secRefNumber) {
      this.secRefNumber = secRefNumber;
   }

   public void setResults(List<ValidationResult> results) {
      this.results = results;
   }

   public void setSecErrorCode(String secErrorCode) {
      this.secErrorCode = secErrorCode;
   }

   public String generate(char dataElementSeparator, char compositeSeparator) throws SegmentGenException, CompositeGenException {
      if (this.secRefNumber != null && !"".equals(this.secRefNumber)) {
         this.segment = "";
         if (this.secErrorCode != null && !"".equals(this.secErrorCode)) {
            this.segment = dataElementSeparator + this.secErrorCode;
         }

         if (this.results != null && this.results.size() > 0) {
            for(int i = this.results.size() - 1; i >= 0; --i) {
               this.segment = dataElementSeparator + ((ValidationResult)this.results.get(i)).generate(compositeSeparator + "") + this.segment;
            }
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         this.segment = "USY" + dataElementSeparator + this.secRefNumber + this.segment;
         return this.segment;
      } else {
         throw new SegmentGenException("Mandatory data element missing");
      }
   }

   public String generate(char dataElementSeparator, char compositeSeparator, char repetitionChar) throws SegmentGenException, CompositeGenException {
      if (this.secRefNumber != null && !"".equals(this.secRefNumber)) {
         this.segment = "";
         if (this.secErrorCode != null && !"".equals(this.secErrorCode)) {
            this.segment = dataElementSeparator + this.secErrorCode;
         }

         if (this.results != null && this.results.size() > 0) {
            this.segment = ((ValidationResult)this.results.get(this.results.size() - 1)).generate(compositeSeparator + "") + this.segment;

            for(int i = this.results.size() - 2; i >= 0; --i) {
               this.segment = ((ValidationResult)this.results.get(i)).generate(compositeSeparator + "") + repetitionChar + this.segment;
            }

            this.segment = dataElementSeparator + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         this.segment = "USY" + dataElementSeparator + this.secRefNumber + this.segment;
         return this.segment;
      } else {
         throw new SegmentGenException("Mandatory data element missing");
      }
   }
}
