package com.sap.aii.edi.sec.module;

import java.util.ArrayList;
import java.util.List;

public class EDIMessageUtil {
   private char SEGMENT_TERMINATOR = '\'';
   private char DATA_ELEMENT_SEPARATOR = '+';
   private char COMPOSITE_ELEMENT_SEPARATOR = ':';
   private char RELEASE_CHARACTER = '?';
   private char REPEAT_CHARACTER = '*';
   private String messageType = "";
   private String messageVersion = "";
   private String unaSegment = "";
   private String unbSegment = "";
   private String ungSegment = "";
   private String unzSegment = "";
   private String interchangeRecipientIdentification = "";
   private String interchangeSenderIdentification = "";
   private boolean autackRequired = false;
   private String interchangeControlNumber = "";
   private String interchangeControlNumberForAUTACK = "";
   private String inboundCertificate = "";
   private String inboundFilterFunction = "";
   private String syntaxID;
   private String messageSubVersion = "";
   private List<String> unhSegmentList = new ArrayList();
   private List<String> untSegmentList = new ArrayList();
   private List<String> subMsgList = new ArrayList();
   private List<String> verifyMsgList = new ArrayList();
   private List<String> signatureMsgList = new ArrayList();
   private List<String> segInVersionList = new ArrayList();
   private List<Boolean> verifiactionResultList = new ArrayList();
   private final String UNH = "UNH";
   private final String UNA = "UNA";
   private final String UNB = "UNB";
   private final String UNG = "UNG";
   private final String UNT = "UNT";
   private final String UNZ = "UNZ";
   private final String USH = "USH";
   private final String UST = "UST";
   private final String UNO = "UNO";
   private final String USR = "USR";
   private final String USA = "USA";
   private final String USX = "USX";
   private final String AUTACK = "AUTACK";
   private final String DISTINCT_SEGMENT_QUERY = "SELECT DISTINCT SEGMENT FROM B2B_EDI_DEF_SEG WHERE MESSAGEVERSION = ?";
   private String messageRelease;

   public char getSEGMENT_TERMINATOR() {
      return this.SEGMENT_TERMINATOR;
   }

   public void setSEGMENT_TERMINATOR(char sEGMENTTERMINATOR) {
      this.SEGMENT_TERMINATOR = sEGMENTTERMINATOR;
   }

   public char getDATA_ELEMENT_SEPARATOR() {
      return this.DATA_ELEMENT_SEPARATOR;
   }

   public void setDATA_ELEMENT_SEPARATOR(char dATAELEMENTSEPARATOR) {
      this.DATA_ELEMENT_SEPARATOR = dATAELEMENTSEPARATOR;
   }

   public char getCOMPOSITE_ELEMENT_SEPARATOR() {
      return this.COMPOSITE_ELEMENT_SEPARATOR;
   }

   public void setCOMPOSITE_ELEMENT_SEPARATOR(char cOMPOSITEELEMENTSEPARATOR) {
      this.COMPOSITE_ELEMENT_SEPARATOR = cOMPOSITEELEMENTSEPARATOR;
   }

   public char getRELEASE_CHARACTER() {
      return this.RELEASE_CHARACTER;
   }

   public void setRELEASE_CHARACTER(char rELEASECHARACTER) {
      this.RELEASE_CHARACTER = rELEASECHARACTER;
   }

   public char getREPEAT_CHARACTER() {
      return this.REPEAT_CHARACTER;
   }

   public void setREPEAT_CHARACTER(char rEPEATCHARACTER) {
      this.REPEAT_CHARACTER = rEPEATCHARACTER;
   }

   public String getMessageType() {
      return this.messageType;
   }

   public void setMessageType(String messageType) {
      this.messageType = messageType;
   }

   public String getMessageVersion() {
      return this.messageVersion;
   }

   public void setMessageVersion(String messageVersion) {
      this.messageVersion = messageVersion;
   }

   public String getUnaSegment() {
      return this.unaSegment;
   }

   public void setUnaSegment(String unaSegment) {
      this.unaSegment = unaSegment;
   }

   public String getUnbSegment() {
      return this.unbSegment;
   }

   public void setUnbSegment(String unbSegment) {
      this.unbSegment = unbSegment;
   }

   public String getUngSegment() {
      return this.ungSegment;
   }

   public void setUngSegment(String ungSegment) {
      this.ungSegment = ungSegment;
   }

   public String getUnzSegment() {
      return this.unzSegment;
   }

   public void setUnzSegment(String unzSegment) {
      this.unzSegment = unzSegment;
   }

   public String getInterchangeRecipientIdentification() {
      return this.interchangeRecipientIdentification;
   }

   public void setInterchangeRecipientIdentification(String interchangeRecipientIdentification) {
      this.interchangeRecipientIdentification = interchangeRecipientIdentification;
   }

   public String getInterchangeSenderIdentification() {
      return this.interchangeSenderIdentification;
   }

   public void setInterchangeSenderIdentification(String interchangeSenderIdentification) {
      this.interchangeSenderIdentification = interchangeSenderIdentification;
   }

   public boolean isAutackRequired() {
      return this.autackRequired;
   }

   public void setAutackRequired(boolean autackRequired) {
      this.autackRequired = autackRequired;
   }

   public String getInterchangeControlNumber() {
      return this.interchangeControlNumber;
   }

   public void setInterchangeControlNumber(String interchangeControlNumber) {
      this.interchangeControlNumber = interchangeControlNumber;
   }

   public String getInterchangeControlNumberForAUTACK() {
      return !this.interchangeControlNumberForAUTACK.equals("") ? this.interchangeControlNumberForAUTACK : this.interchangeControlNumber;
   }

   public void setInterchangeControlNumberForAUTACK(String interchangeControlNumberForAUTACK) {
      this.interchangeControlNumberForAUTACK = interchangeControlNumberForAUTACK;
   }

   public String getInboundCertificate() {
      return this.inboundCertificate;
   }

   public void setInboundCertificate(String inboundCertificate) {
      this.inboundCertificate = inboundCertificate;
   }

   public String getInboundFilterFunction() {
      return this.inboundFilterFunction;
   }

   public void setInboundFilterFunction(String inboundFilterFunction) {
      this.inboundFilterFunction = inboundFilterFunction;
   }

   public String getSyntaxID() {
      return this.syntaxID;
   }

   public void setSyntaxID(String syntaxID) {
      this.syntaxID = syntaxID;
   }

   public String getMessageSubVersion() {
      return this.messageSubVersion;
   }

   public void setMessageSubVersion(String messageSubVersion) {
      this.messageSubVersion = messageSubVersion;
   }

   public List<String> getUnhSegmentList() {
      return this.unhSegmentList;
   }

   public void setUnhSegmentList(List<String> unhSegmentList) {
      this.unhSegmentList = unhSegmentList;
   }

   public List<String> getUntSegmentList() {
      return this.untSegmentList;
   }

   public void setUntSegmentList(List<String> untSegmentList) {
      this.untSegmentList = untSegmentList;
   }

   public List<String> getSubMsgList() {
      return this.subMsgList;
   }

   public void setSubMsgList(List<String> subMsgList) {
      this.subMsgList = subMsgList;
   }

   public List<String> getVerifyMsgList() {
      return this.verifyMsgList;
   }

   public void setVerifyMsgList(List<String> verifyMsgList) {
      this.verifyMsgList = verifyMsgList;
   }

   public List<String> getSignatureMsgList() {
      return this.signatureMsgList;
   }

   public void setSignatureMsgList(List<String> signatureMsgList) {
      this.signatureMsgList = signatureMsgList;
   }

   public List<String> getSegInVersionList() {
      return this.segInVersionList;
   }

   public void setSegInVersionList(List<String> segInVersionList) {
      this.segInVersionList = segInVersionList;
   }

   public List<Boolean> getVerifiactionResultList() {
      return this.verifiactionResultList;
   }

   public void setVerifiactionResultList(List<Boolean> verifiactionResultList) {
      this.verifiactionResultList = verifiactionResultList;
   }

   public String getUNH() {
      return "UNH";
   }

   public String getUNA() {
      return "UNA";
   }

   public String getUNB() {
      return "UNB";
   }

   public String getUNG() {
      return "UNG";
   }

   public String getUNT() {
      return "UNT";
   }

   public String getUNZ() {
      return "UNZ";
   }

   public String getUSH() {
      return "USH";
   }

   public String getUST() {
      return "UST";
   }

   public String getUNO() {
      return "UNO";
   }

   public String getUSR() {
      return "USR";
   }

   public String getUSA() {
      return "USA";
   }

   public String getUSX() {
      return "USX";
   }

   public String getAUTACK() {
      return "AUTACK";
   }

   public String getDISTINCT_SEGMENT_QUERY() {
      return "SELECT DISTINCT SEGMENT FROM B2B_EDI_DEF_SEG WHERE MESSAGEVERSION = ?";
   }

   public void setMessageRelease(String release) {
      this.messageRelease = release;
   }

   public String getMessageRelease() {
      return this.messageRelease;
   }
}
