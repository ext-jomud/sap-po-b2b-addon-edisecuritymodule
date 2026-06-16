package com.sap.aii.b2b.edifact.sec.api;

import java.util.ArrayList;
import java.util.List;

public class USCSegment {
   private String segment;
   private static final int SECURITY_ID_MAXCOUNT = 2;
   private static final int SIG_SERVICE_CHAR_MAXCOUNT = 5;
   private static final int SECURITY_TIMESTAMP_MAXCOUNT = 4;
   private String certReference;
   private List<SecurityID> secIDDetails = new ArrayList();
   private String certSyntax;
   private String filterFunction;
   private String orgCharsetEnc;
   private String certOrgCharset;
   private String userAuthLevel;
   private List<SignatureServiceCharacter> serviceCharList = new ArrayList();
   private List<SecurityTimestamp> securityTimestamp = new ArrayList();
   private String secStatusCode;
   private String revReasonCode;

   public USCSegment() {
   }

   private USCSegment(String segment) {
      this.segment = segment;
   }

   public static USCSegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator) {
      USCSegment usc = new USCSegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);
      String escapedCompositeSeperator = "\\" + compositeSeparator;
      int secIDDetailsCount = 0;
      int sigServiceCharCount = 0;
      int secTSCount = 0;

      for(int i = 1; i < dataElements.length; ++i) {
         int index = i - secIDDetailsCount - sigServiceCharCount - secTSCount;
         switch (index) {
            case 1:
               usc.certReference = dataElements[i].trim();
               break;
            case 2:
               usc.secIDDetails.add(SecurityID.parse(dataElements[i], escapedCompositeSeperator));
               if (secIDDetailsCount < 1 && i < dataElements.length - 1 && dataElements[i].split(escapedCompositeSeperator).length == dataElements[i + 1].split(escapedCompositeSeperator).length) {
                  ++secIDDetailsCount;
               }
               break;
            case 3:
               usc.certSyntax = dataElements[i].trim();
               break;
            case 4:
               usc.filterFunction = dataElements[i].trim();
               break;
            case 5:
               usc.orgCharsetEnc = dataElements[i].trim();
               break;
            case 6:
               usc.certOrgCharset = dataElements[i].trim();
               break;
            case 7:
               usc.userAuthLevel = dataElements[i].trim();
               break;
            case 8:
               usc.serviceCharList.add(SignatureServiceCharacter.parse(dataElements[i], escapedCompositeSeperator));
               if (sigServiceCharCount < 4 && i < dataElements.length - 1 && dataElements[i].split(escapedCompositeSeperator).length == dataElements[i + 1].split(escapedCompositeSeperator).length) {
                  ++sigServiceCharCount;
               }
               break;
            case 9:
               usc.securityTimestamp.add(SecurityTimestamp.parse(dataElements[i], escapedCompositeSeperator));
               if (secTSCount < 3 && i < dataElements.length - 1 && dataElements[i].split(escapedCompositeSeperator).length == dataElements[i + 1].split(escapedCompositeSeperator).length) {
                  ++secTSCount;
               }
               break;
            case 10:
               usc.secStatusCode = dataElements[i].trim();
               break;
            case 11:
               usc.revReasonCode = dataElements[i].trim();
         }
      }

      return usc;
   }

   public static USCSegment parse(String segment, char dataElementSeparator, char compositeSeparator, char segmentTerminator, char repetitionSeperator) {
      USCSegment usc = new USCSegment(segment);
      String[] seg = segment.split("\\" + segmentTerminator);
      segment = seg[0];
      String[] dataElements = segment.split("\\" + dataElementSeparator);

      for(int i = 1; i < dataElements.length; ++i) {
         switch (i) {
            case 1:
               usc.certReference = dataElements[i].trim();
               break;
            case 2:
               String[] secIDDetailsComposites = dataElements[i].split("\\" + repetitionSeperator);

               for(String secIDDetailsComp : secIDDetailsComposites) {
                  usc.secIDDetails.add(SecurityID.parse(secIDDetailsComp, compositeSeparator + ""));
               }
               break;
            case 3:
               usc.certSyntax = dataElements[i].trim();
               break;
            case 4:
               usc.filterFunction = dataElements[i].trim();
               break;
            case 5:
               usc.orgCharsetEnc = dataElements[i].trim();
               break;
            case 6:
               usc.certOrgCharset = dataElements[i].trim();
               break;
            case 7:
               usc.userAuthLevel = dataElements[i].trim();
               break;
            case 8:
               String[] sigServiceCharComposites = dataElements[i].split("\\" + repetitionSeperator);

               for(String sigServiceCharComp : sigServiceCharComposites) {
                  usc.serviceCharList.add(SignatureServiceCharacter.parse(sigServiceCharComp, compositeSeparator + ""));
               }
               break;
            case 9:
               String[] securityTimestampComposites = dataElements[i].split("\\" + repetitionSeperator);

               for(String securityTimestampComp : securityTimestampComposites) {
                  usc.securityTimestamp.add(SecurityTimestamp.parse(securityTimestampComp, compositeSeparator + ""));
               }
               break;
            case 10:
               usc.secStatusCode = dataElements[i].trim();
               break;
            case 11:
               usc.revReasonCode = dataElements[i].trim();
         }
      }

      return usc;
   }

   public String getSegment() {
      return this.segment;
   }

   public String getCertReference() {
      return this.certReference;
   }

   public List<SecurityID> getSecIDDetails() {
      return this.secIDDetails;
   }

   public String getCertSyntax() {
      return this.certSyntax;
   }

   public String getFilterFunction() {
      return this.filterFunction;
   }

   public String getOrgCharsetEnc() {
      return this.orgCharsetEnc;
   }

   public String getCertOrgCharset() {
      return this.certOrgCharset;
   }

   public String getUserAuthLevel() {
      return this.userAuthLevel;
   }

   public List<SignatureServiceCharacter> getServiceCharList() {
      return this.serviceCharList;
   }

   public List<SecurityTimestamp> getSecurityTimestamp() {
      return this.securityTimestamp;
   }

   public String getSecStatusCode() {
      return this.secStatusCode;
   }

   public String getRevReasonCode() {
      return this.revReasonCode;
   }

   public void setCertReference(String certReference) {
      this.certReference = certReference;
   }

   public void setSecIDDetails(List<SecurityID> secIDDetails) {
      this.secIDDetails = secIDDetails;
   }

   public void setCertSyntax(String certSyntax) {
      this.certSyntax = certSyntax;
   }

   public void setFilterFunction(String filterFunction) {
      this.filterFunction = filterFunction;
   }

   public void setOrgCharsetEnc(String orgCharsetEnc) {
      this.orgCharsetEnc = orgCharsetEnc;
   }

   public void setCertOrgCharset(String certOrgCharset) {
      this.certOrgCharset = certOrgCharset;
   }

   public void setUserAuthLevel(String userAuthLevel) {
      this.userAuthLevel = userAuthLevel;
   }

   public void setServiceCharList(List<SignatureServiceCharacter> serviceCharList) {
      this.serviceCharList = serviceCharList;
   }

   public void setSecurityTimestamp(List<SecurityTimestamp> securityTimestamp) {
      this.securityTimestamp = securityTimestamp;
   }

   public void setSecStatusCode(String secStatusCode) {
      this.secStatusCode = secStatusCode;
   }

   public void setRevReasonCode(String revReasonCode) {
      this.revReasonCode = revReasonCode;
   }

   public String generate(char dataElementSeparator, char compositeSeparator) throws SegmentGenException, CompositeGenException {
      this.segment = "";
      if (this.revReasonCode != null && !"".equals(this.revReasonCode)) {
         this.segment = dataElementSeparator + this.revReasonCode;
      }

      if (this.secStatusCode != null && !"".equals(this.secStatusCode)) {
         this.segment = dataElementSeparator + this.secStatusCode + this.segment;
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      if (this.securityTimestamp != null && this.securityTimestamp.size() > 0) {
         for(int i = this.securityTimestamp.size() - 1; i >= 0; --i) {
            this.segment = dataElementSeparator + ((SecurityTimestamp)this.securityTimestamp.get(i)).generate(compositeSeparator + "") + this.segment;
         }
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      if (this.serviceCharList != null && this.serviceCharList.size() > 0) {
         for(int i = this.serviceCharList.size() - 1; i >= 0; --i) {
            this.segment = dataElementSeparator + ((SignatureServiceCharacter)this.serviceCharList.get(i)).generate(compositeSeparator + "") + this.segment;
         }
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      if (this.userAuthLevel != null && !"".equals(this.userAuthLevel)) {
         this.segment = dataElementSeparator + this.userAuthLevel + this.segment;
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      if (this.certOrgCharset != null && !"".equals(this.certOrgCharset)) {
         this.segment = dataElementSeparator + this.certOrgCharset + this.segment;
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      if (this.orgCharsetEnc != null && !"".equals(this.orgCharsetEnc)) {
         this.segment = dataElementSeparator + this.orgCharsetEnc + this.segment;
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      if (this.filterFunction != null && !"".equals(this.filterFunction)) {
         this.segment = dataElementSeparator + this.filterFunction + this.segment;
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      if (this.certSyntax != null && !"".equals(this.certSyntax)) {
         this.segment = dataElementSeparator + this.certSyntax + this.segment;
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

      if (this.certReference != null && !"".equals(this.certReference)) {
         this.segment = dataElementSeparator + this.certReference + this.segment;
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      this.segment = "USC" + this.segment;
      return this.segment;
   }

   public String generate(char dataElementSeparator, char compositeSeparator, char repetitionChar) throws SegmentGenException, CompositeGenException {
      this.segment = "";
      if (this.revReasonCode != null && !"".equals(this.revReasonCode)) {
         this.segment = dataElementSeparator + this.revReasonCode;
      }

      if (this.secStatusCode != null && !"".equals(this.secStatusCode)) {
         this.segment = dataElementSeparator + this.secStatusCode + this.segment;
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      if (this.securityTimestamp != null && this.securityTimestamp.size() > 0) {
         this.segment = ((SecurityTimestamp)this.securityTimestamp.get(this.securityTimestamp.size() - 1)).generate(compositeSeparator + "") + this.segment;

         for(int i = this.securityTimestamp.size() - 2; i >= 0; --i) {
            this.segment = ((SecurityTimestamp)this.securityTimestamp.get(i)).generate(compositeSeparator + "") + repetitionChar + this.segment;
         }

         this.segment = dataElementSeparator + this.segment;
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      if (this.serviceCharList != null && this.serviceCharList.size() > 0) {
         this.segment = ((SignatureServiceCharacter)this.serviceCharList.get(this.serviceCharList.size() - 1)).generate(compositeSeparator + "") + this.segment;

         for(int i = this.serviceCharList.size() - 2; i >= 0; --i) {
            this.segment = ((SignatureServiceCharacter)this.serviceCharList.get(i)).generate(compositeSeparator + "") + repetitionChar + this.segment;
         }

         this.segment = dataElementSeparator + this.segment;
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      if (this.userAuthLevel != null && !"".equals(this.userAuthLevel)) {
         this.segment = dataElementSeparator + this.userAuthLevel + this.segment;
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      if (this.certOrgCharset != null && !"".equals(this.certOrgCharset)) {
         this.segment = dataElementSeparator + this.certOrgCharset + this.segment;
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      if (this.orgCharsetEnc != null && !"".equals(this.orgCharsetEnc)) {
         this.segment = dataElementSeparator + this.orgCharsetEnc + this.segment;
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      if (this.filterFunction != null && !"".equals(this.filterFunction)) {
         this.segment = dataElementSeparator + this.filterFunction + this.segment;
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      if (this.certSyntax != null && !"".equals(this.certSyntax)) {
         this.segment = dataElementSeparator + this.certSyntax + this.segment;
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

      if (this.certReference != null && !"".equals(this.certReference)) {
         this.segment = dataElementSeparator + this.certReference + this.segment;
      } else if (!"".equals(this.segment)) {
         this.segment = dataElementSeparator + this.segment;
      }

      this.segment = "USC" + this.segment;
      return this.segment;
   }
}
