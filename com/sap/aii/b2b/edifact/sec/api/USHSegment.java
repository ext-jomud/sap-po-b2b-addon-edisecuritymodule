package com.sap.aii.b2b.edifact.sec.api;

import java.util.ArrayList;
import java.util.List;

public class USHSegment {
   private String securityService;
   private String segment;
   private String secRefNum;
   private String securityScope;
   private String responseType;
   private String filterFunction;
   private String charSet;
   private String secProvider;
   private List<SecurityID> secIDDetails = new ArrayList();
   private String secSeqNo;
   private SecurityTimestamp timeStamp;
   private static final int MAX_SECID_DETAILS_COUNT = 2;

   public USHSegment() {
   }

   private USHSegment(String segment) {
      this.segment = segment;
   }

   public static USHSegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator) {
      USHSegment ushSeg = new USHSegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);
      String escapedCompositeSeparator = "\\" + compositeSeparator;
      int deLength = dataElements.length;
      int secIDCompositeCount = 0;

      for(int i = 1; i < deLength; ++i) {
         int compositeIndex = i - secIDCompositeCount;
         switch (compositeIndex) {
            case 1:
               ushSeg.securityService = dataElements[i].trim();
               break;
            case 2:
               ushSeg.secRefNum = dataElements[i].trim();
               break;
            case 3:
               ushSeg.securityScope = dataElements[i].trim();
               break;
            case 4:
               ushSeg.responseType = dataElements[i].trim();
               break;
            case 5:
               ushSeg.filterFunction = dataElements[i].trim();
               break;
            case 6:
               ushSeg.charSet = dataElements[i].trim();
               break;
            case 7:
               ushSeg.secProvider = dataElements[i].trim();
               break;
            case 8:
               ushSeg.secIDDetails.add(SecurityID.parse(dataElements[i], escapedCompositeSeparator));
               if (secIDCompositeCount < 1 && i < dataElements.length - 1 && dataElements[i].split(escapedCompositeSeparator).length == dataElements[i + 1].split(escapedCompositeSeparator).length) {
                  ++secIDCompositeCount;
               }
               break;
            case 9:
               ushSeg.secSeqNo = dataElements[i].trim();
               break;
            case 10:
               ushSeg.timeStamp = SecurityTimestamp.parse(dataElements[i], escapedCompositeSeparator);
         }
      }

      return ushSeg;
   }

   public static USHSegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator, char repetitionSeparator) {
      USHSegment ushSeg = new USHSegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);
      String escapedCompositeSeparator = "\\" + compositeSeparator;
      int deLength = dataElements.length;

      for(int i = 1; i < deLength; ++i) {
         switch (i) {
            case 1:
               ushSeg.securityService = dataElements[i].trim();
               break;
            case 2:
               ushSeg.secRefNum = dataElements[i].trim();
               break;
            case 3:
               ushSeg.securityScope = dataElements[i].trim();
               break;
            case 4:
               ushSeg.responseType = dataElements[i].trim();
               break;
            case 5:
               ushSeg.filterFunction = dataElements[i].trim();
               break;
            case 6:
               ushSeg.charSet = dataElements[i].trim();
               break;
            case 7:
               ushSeg.secProvider = dataElements[i].trim();
               break;
            case 8:
               String[] resultComposites = dataElements[1].split("\\" + repetitionSeparator);

               for(String resultComp : resultComposites) {
                  ushSeg.secIDDetails.add(SecurityID.parse(resultComp, compositeSeparator + ""));
               }
               break;
            case 9:
               ushSeg.secSeqNo = dataElements[i].trim();
               break;
            case 10:
               ushSeg.timeStamp = SecurityTimestamp.parse(dataElements[i], escapedCompositeSeparator);
         }
      }

      return ushSeg;
   }

   public String getSecurityService() {
      return this.securityService;
   }

   public String getSegment() {
      return this.segment;
   }

   public String getSecurityReferenceNum() {
      return this.secRefNum;
   }

   public String getSecurityScope() {
      return this.securityScope;
   }

   public String getResponseType() {
      return this.responseType;
   }

   public String getFilterFunction() {
      return this.filterFunction;
   }

   public String getCharSet() {
      return this.charSet;
   }

   public String getSecProvider() {
      return this.secProvider;
   }

   public String getSecuritySequenceNumber() {
      return this.secSeqNo;
   }

   public SecurityTimestamp getTimeStamp() {
      return this.timeStamp;
   }

   public List<SecurityID> getSecurityIDDetails() {
      return this.secIDDetails;
   }

   public void setSecurityService(String securityService) {
      this.securityService = securityService;
   }

   public void setSecurityReferenceNum(String secRefNum) {
      this.secRefNum = secRefNum;
   }

   public void setSecurityScope(String securityScope) {
      this.securityScope = securityScope;
   }

   public void setResponseType(String responseType) {
      this.responseType = responseType;
   }

   public void setFilterFunction(String filterFunction) {
      this.filterFunction = filterFunction;
   }

   public void setCharSet(String charSet) {
      this.charSet = charSet;
   }

   public void setSecProvider(String secProvider) {
      this.secProvider = secProvider;
   }

   public void setSecIDDetails(List<SecurityID> secIDDetails) {
      this.secIDDetails = secIDDetails;
   }

   public void setSecuritySequenceNumber(String secSeqNo) {
      this.secSeqNo = secSeqNo;
   }

   public void setTimeStamp(SecurityTimestamp timeStamp) {
      this.timeStamp = timeStamp;
   }

   public String generate(char dataElementSeparator, char compositeSeparator) throws SegmentGenException, CompositeGenException {
      if (this.securityService != null && !"".equals(this.securityService) && this.secRefNum != null && !"".equals(this.secRefNum)) {
         this.segment = "";
         if (this.timeStamp != null) {
            this.segment = dataElementSeparator + this.timeStamp.generate(compositeSeparator + "");
         }

         if (this.secSeqNo != null && !"".equals(this.secSeqNo)) {
            this.segment = dataElementSeparator + this.secSeqNo + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.secIDDetails != null && this.secIDDetails.size() > 0) {
            for(int i = this.secIDDetails.size() - 1; i >= 0; --i) {
               this.segment = dataElementSeparator + ((SecurityID)this.secIDDetails.get(i)).generate(compositeSeparator + "") + this.segment;
            }
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.secProvider != null && !"".equals(this.secProvider)) {
            this.segment = dataElementSeparator + this.secProvider + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.charSet != null && !"".equals(this.charSet)) {
            this.segment = dataElementSeparator + this.charSet + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.filterFunction != null && !"".equals(this.filterFunction)) {
            this.segment = dataElementSeparator + this.filterFunction + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.responseType != null && !"".equals(this.responseType)) {
            this.segment = dataElementSeparator + this.responseType + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.securityScope != null && !"".equals(this.securityScope)) {
            this.segment = dataElementSeparator + this.securityScope + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.secRefNum != null && !"".equals(this.secRefNum)) {
            this.segment = dataElementSeparator + this.secRefNum + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.securityService != null && !"".equals(this.securityService)) {
            this.segment = dataElementSeparator + this.securityService + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         this.segment = "USH" + this.segment;
         return this.segment;
      } else {
         throw new SegmentGenException("Mandatory data elements missing");
      }
   }

   public String generate(char dataElementSeparator, char compositeSeparator, char repetitionChar) throws SegmentGenException, CompositeGenException {
      if (this.securityService != null && !"".equals(this.securityService) && this.secRefNum != null && !"".equals(this.secRefNum)) {
         this.segment = "";
         if (this.timeStamp != null) {
            this.segment = dataElementSeparator + this.timeStamp.generate(compositeSeparator + "");
         }

         if (this.secSeqNo != null && !"".equals(this.secSeqNo)) {
            this.segment = dataElementSeparator + this.secSeqNo + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.secIDDetails != null && this.secIDDetails.size() > 0) {
            this.segment = ((SecurityID)this.secIDDetails.get(this.secIDDetails.size() - 1)).generate(compositeSeparator + "") + this.segment;

            for(int i = this.secIDDetails.size() - 2; i >= 0; --i) {
               this.segment = ((SecurityID)this.secIDDetails.get(i)).generate(compositeSeparator + "") + repetitionChar + this.segment;
            }

            this.segment = dataElementSeparator + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.secProvider != null && !"".equals(this.secProvider)) {
            this.segment = dataElementSeparator + this.secProvider + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.charSet != null && !"".equals(this.charSet)) {
            this.segment = dataElementSeparator + this.charSet + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.filterFunction != null && !"".equals(this.filterFunction)) {
            this.segment = dataElementSeparator + this.filterFunction + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.responseType != null && !"".equals(this.responseType)) {
            this.segment = dataElementSeparator + this.responseType + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.securityScope != null && !"".equals(this.securityScope)) {
            this.segment = dataElementSeparator + this.securityScope + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.secRefNum != null && !"".equals(this.secRefNum)) {
            this.segment = dataElementSeparator + this.secRefNum + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         if (this.securityService != null && !"".equals(this.securityService)) {
            this.segment = dataElementSeparator + this.securityService + this.segment;
         } else if (!"".equals(this.segment)) {
            this.segment = dataElementSeparator + this.segment;
         }

         this.segment = "USH" + this.segment;
         return this.segment;
      } else {
         throw new SegmentGenException("Mandatory data elements missing");
      }
   }
}
