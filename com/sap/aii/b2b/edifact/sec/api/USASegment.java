package com.sap.aii.b2b.edifact.sec.api;

import java.util.ArrayList;
import java.util.List;

public class USASegment {
   private SecurityAlgorithm securityAlgo;
   private List<SecurityAlgorithmParam> algoParameters = new ArrayList();
   private String segment;

   public USASegment() {
   }

   private USASegment(String segment) {
      this.segment = segment;
   }

   public static USASegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator) {
      USASegment usaSegment = new USASegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);
      String escapedCompositeSeparator = "\\" + compositeSeparator;
      int deLength = dataElements.length;

      for(int i = 1; i < deLength; ++i) {
         if (i == 1) {
            usaSegment.securityAlgo = SecurityAlgorithm.parse(dataElements[i].trim(), escapedCompositeSeparator);
         } else {
            usaSegment.algoParameters.add(SecurityAlgorithmParam.parse(dataElements[i].trim(), escapedCompositeSeparator));
         }
      }

      return usaSegment;
   }

   public static USASegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator, char repetitionSeperator) {
      USASegment usaSegment = new USASegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);
      String escapedCompositeSeparator = "\\" + compositeSeparator;
      int deLength = dataElements.length;

      for(int i = 1; i < deLength; ++i) {
         switch (i) {
            case 1:
               usaSegment.securityAlgo = SecurityAlgorithm.parse(dataElements[i].trim(), escapedCompositeSeparator);
               break;
            case 2:
               String[] parameterComposites = dataElements[i].split("\\" + repetitionSeperator);

               for(String parameterComp : parameterComposites) {
                  usaSegment.algoParameters.add(SecurityAlgorithmParam.parse(parameterComp, compositeSeparator + ""));
               }
         }
      }

      return usaSegment;
   }

   public SecurityAlgorithm getSecurityAlgo() {
      return this.securityAlgo;
   }

   public List<SecurityAlgorithmParam> getAlgoParameters() {
      return this.algoParameters;
   }

   public String getSegment() {
      return this.segment;
   }

   public void setSecurityAlgo(SecurityAlgorithm securityAlgo) {
      this.securityAlgo = securityAlgo;
   }

   public void setAlgoParameters(List<SecurityAlgorithmParam> algoParameters) {
      this.algoParameters = algoParameters;
   }

   public String generate(char dataElementSeparator, char compositeSeparator) throws SegmentGenException, CompositeGenException {
      if (this.securityAlgo == null) {
         throw new SegmentGenException("Mandatory data element missing");
      } else {
         this.segment = "USA" + dataElementSeparator + this.securityAlgo.generate(compositeSeparator + "");
         if (this.algoParameters != null) {
            for(int i = 0; i < this.algoParameters.size(); ++i) {
               this.segment = this.segment + dataElementSeparator + ((SecurityAlgorithmParam)this.algoParameters.get(i)).generate(compositeSeparator + "");
            }
         }

         return this.segment;
      }
   }

   public String generate(char dataElementSeparator, char compositeSeparator, char repetitionChar) throws SegmentGenException, CompositeGenException {
      if (this.securityAlgo == null) {
         throw new SegmentGenException("Mandatory data element missing");
      } else {
         this.segment = "USA" + dataElementSeparator + this.securityAlgo.generate(compositeSeparator + "");
         if (this.algoParameters != null && this.algoParameters.size() > 0) {
            this.segment = this.segment + dataElementSeparator + ((SecurityAlgorithmParam)this.algoParameters.get(0)).generate(compositeSeparator + "");

            for(int i = 1; i < this.algoParameters.size(); ++i) {
               this.segment = this.segment + repetitionChar + ((SecurityAlgorithmParam)this.algoParameters.get(i)).generate(compositeSeparator + "");
            }
         }

         return this.segment;
      }
   }
}
