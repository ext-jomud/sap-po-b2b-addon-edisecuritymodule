package com.sap.aii.edi.sec.module;

import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.aii.b2b.edifact.sec.api.CompositeGenException;
import com.sap.aii.b2b.edifact.sec.api.ObjectStatus;
import com.sap.aii.b2b.edifact.sec.api.ObjectTypeIdentification;
import com.sap.aii.b2b.edifact.sec.api.ReferenceIdentification;
import com.sap.aii.b2b.edifact.sec.api.SecurityAlgorithm;
import com.sap.aii.b2b.edifact.sec.api.SecurityAlgorithmParam;
import com.sap.aii.b2b.edifact.sec.api.SecurityID;
import com.sap.aii.b2b.edifact.sec.api.SecurityTimestamp;
import com.sap.aii.b2b.edifact.sec.api.SegmentGenException;
import com.sap.aii.b2b.edifact.sec.api.UNOSegment;
import com.sap.aii.b2b.edifact.sec.api.UNPSegment;
import com.sap.aii.b2b.edifact.sec.api.USASegment;
import com.sap.aii.b2b.edifact.sec.api.USCSegment;
import com.sap.aii.b2b.edifact.sec.api.USHSegment;
import com.sap.aii.b2b.edifact.sec.api.USRSegment;
import com.sap.aii.b2b.edifact.sec.api.USTSegment;
import com.sap.aii.b2b.edifact.sec.api.ValidationResult;
import com.sap.aii.b2b.sec.api.CertificateApi;
import com.sap.aii.b2b.sec.api.KeystoreUtilsException;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.tc.logging.Location;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.resource.ResourceException;

public class SignedEDIMsgGenerator implements AutackHelper {
   private final String NUMBER_OF_SECURITY_SEGMENTS = "6";
   private transient Location _location = Location.getLocation(SignedEDIMsgGenerator.class);
   private MessageKey messageKey;
   private final String correlationId;
   private PropertyHandler propHandler;
   private String uniqueRefNumber;
   private boolean firstTime;

   public SignedEDIMsgGenerator(MessageKey messageKey, String correlationId, PropertyHandler propHandler) {
      String SIGNATURE = "SignedEDIMsgGenerator(MessageKey messageKey, ModuleContext ctx, String correlationId, PropertyHandler propHandler)";
      this._location.entering("SignedEDIMsgGenerator(MessageKey messageKey, ModuleContext ctx, String correlationId, PropertyHandler propHandler)");
      this.correlationId = correlationId;
      this.propHandler = propHandler;
      this.messageKey = messageKey;
      this.uniqueRefNumber = this.generateRandom(12);
      this.firstTime = true;
      this._location.exiting("SignedEDIMsgGenerator(MessageKey messageKey, ModuleContext ctx, String correlationId, PropertyHandler propHandler)");
   }

   public SecurityID getSecurityID(String secPartyQual, String secPartyID) {
      SecurityID securityID = new SecurityID();
      securityID.setSecPartyQual(secPartyQual);
      securityID.setSecPartyID(secPartyID);
      return securityID;
   }

   public void generateRandomNumber() {
      this.uniqueRefNumber = this.generateRandom(12);
   }

   private String generateRandom(int length) {
      Random random = new Random();
      char[] digits = new char[length];
      digits[0] = (char)(random.nextInt(9) + 49);

      for(int i = 1; i < length; ++i) {
         digits[i] = (char)(random.nextInt(10) + 48);
      }

      return new String(digits);
   }

   public String generateUSHSegment(char dataEleSep, char compEleSep, char repeatChar, String senderID, String receiverID) throws SegmentGenException, CompositeGenException, ResourceException, ModuleException {
      USHSegment ush = new USHSegment();
      String securityService = this.propHandler.getProperty("edi.sec.service", "1");
      ush.setSecurityService(securityService);
      ush.setSecurityReferenceNum(this.uniqueRefNumber);
      String securityScope = this.propHandler.getProperty("securityScope", "2");
      if (securityScope.equalsIgnoreCase("UNB")) {
         securityScope = "1";
      } else if (securityScope.equalsIgnoreCase("UNH")) {
         securityScope = "2";
      }

      ush.setSecurityScope(securityScope);
      String responseType = this.propHandler.getProperty("requireAUTACK", "1");
      if (this.firstTime) {
         if (responseType.equals("2")) {
            (new AckStatusUtil()).createAckStatusReceiptPending(this.messageKey.getMessageId(), this.getCorrelationId(), AckStatusUtil.ACK_TYPE_AUTACK);
         } else if (responseType.equals("1")) {
            (new AckStatusUtil()).createAckStatusNotSolicited(this.messageKey.getMessageId(), this.getCorrelationId(), AckStatusUtil.ACK_TYPE_AUTACK);
         }

         this.firstTime = false;
      }

      ush.setResponseType(responseType);
      String filterFunction = this.propHandler.getProperty("filterAlgorithm", "6");
      if (filterFunction.equalsIgnoreCase("EDA")) {
         filterFunction = "5";
      } else if (filterFunction.equalsIgnoreCase("EDC")) {
         filterFunction = "6";
      } else if (filterFunction.equalsIgnoreCase("Base64")) {
         filterFunction = "7";
      }

      ush.setFilterFunction(filterFunction);
      String charSet = this.propHandler.getProperty("encodingType", "7");
      if (charSet.equalsIgnoreCase("UTF-8")) {
         charSet = "7";
      } else if (charSet.equalsIgnoreCase("US-ASCII")) {
         charSet = "2";
      }

      ush.setCharSet(charSet);
      String secProvider = this.propHandler.getProperty("edi.sec.providerRole", "1");
      ush.setSecProvider(secProvider);
      String secSeqNo = this.propHandler.getProperty("edi.sec.sequenceNo", "");
      ush.setSecuritySequenceNumber(secSeqNo);
      List<SecurityID> securityIDDetails = ush.getSecurityIDDetails();
      SecurityID securityID = this.getSecurityID("1", senderID);
      securityIDDetails.add(securityID);
      securityID = this.getSecurityID("2", receiverID);
      securityIDDetails.add(securityID);
      Date now = new Date();
      SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
      SimpleDateFormat timeformat = new SimpleDateFormat("HHmmss");
      String date = dateformat.format(now);
      String time = timeformat.format(now);
      SecurityTimestamp timeStamp = new SecurityTimestamp();
      timeStamp.setQualifier("5");
      timeStamp.setDate(date);
      timeStamp.setTime(time);
      ush.setTimeStamp(timeStamp);
      String ushSegment = ush.generate(dataEleSep, compEleSep);
      return ushSegment;
   }

   public String generateOuterUSASegment(char dataEleSep, char compEleSep, char repeatChar) throws SegmentGenException, CompositeGenException, KeystoreUtilsException, ModuleException {
      USASegment usa = new USASegment();
      SecurityAlgorithm secAlgo = new SecurityAlgorithm();
      String algorithm = this.propHandler.getProperty("edi.sec.algorithm", "1");
      algorithm = "1";
      secAlgo.setAlgorithm(algorithm);
      String algoCodeId = this.propHandler.getProperty("edi.sec.algorithmCodeID", "16");
      secAlgo.setAlgoCodeId(algoCodeId);
      String algoCoded = this.propHandler.getProperty("edi.sec.algorithmCodeCoded", "1");
      secAlgo.setAlgoCodeCoded(algoCoded);
      usa.setSecurityAlgo(secAlgo);
      String usaSegment = usa.generate(dataEleSep, compEleSep);
      return usaSegment;
   }

   public String generateUSASegment(char dataEleSep, char compEleSep, char repeatChar, String binEncType, CertificateApi certApi, String filterType) throws SegmentGenException, CompositeGenException, KeystoreUtilsException, UnsupportedEncodingException, ModuleException {
      USASegment usa = new USASegment();
      SecurityAlgorithm secAlgo = new SecurityAlgorithm();
      String algorithm = "6";
      secAlgo.setAlgorithm(algorithm);
      String cryptMode = this.propHandler.getProperty("edi.sec.cryptographicMode", "16");
      secAlgo.setCryptMode(cryptMode);
      String operationID = this.propHandler.getProperty("edi.sec.operationID", "1");
      secAlgo.setOperationId(operationID);
      secAlgo.setAlgoCodeId("10");
      String algoCoded = this.propHandler.getProperty("edi.sec.algorithmCodeCoded", "1");
      secAlgo.setAlgoCodeCoded(algoCoded);
      String paddingMech = this.propHandler.getProperty("edi.sec.paddingMechanism", "");
      secAlgo.setPaddingMech(paddingMech);
      String paddingMechCoded = this.propHandler.getProperty("edi.sec.paddingMechanismCoded", "");
      secAlgo.setPaddingMechCoded(paddingMechCoded);
      usa.setSecurityAlgo(secAlgo);
      if (certApi != null) {
         byte[] modulus = certApi.getModulus(filterType);
         byte[] exponent = certApi.getExponent(filterType);
         String edcMod = new String(modulus, binEncType);
         String edcExp = new String(exponent, binEncType);
         int len = certApi.getModulus().bitLength();
         List<SecurityAlgorithmParam> algoParameters = usa.getAlgoParameters();
         SecurityAlgorithmParam param = new SecurityAlgorithmParam();
         param.setAlgorithmQual("14");
         param.setAlgorithmValue("" + len);
         algoParameters.add(param);
         param = new SecurityAlgorithmParam();
         param.setAlgorithmQual("12");
         param.setAlgorithmValue(edcMod);
         algoParameters.add(param);
         param = new SecurityAlgorithmParam();
         param.setAlgorithmQual("13");
         param.setAlgorithmValue(edcExp);
         algoParameters.add(param);
      }

      String usaSegment = usa.generate(dataEleSep, compEleSep);
      return usaSegment;
   }

   public String generateUSCSegment(String issuer, char dataEleSep, char compEleSep, char repeatChar, String certReference) throws SegmentGenException, CompositeGenException {
      USCSegment usc = new USCSegment();
      usc.setCertReference(certReference);
      String uscAdvanced = "";

      try {
         uscAdvanced = this.propHandler.getProperty("edi.sec.USCAdvanced", "");
      } catch (ModuleException var10) {
      }

      if (uscAdvanced.equalsIgnoreCase("TRUE") || uscAdvanced.equalsIgnoreCase("YES")) {
         SecurityID securityID = new SecurityID();
         securityID.setSecPartyQual("4");
         securityID.setSecPartyID(issuer);
         securityID.setSecPartyCLQual("ZZZ");
         List<SecurityID> secIDDetails = new ArrayList();
         secIDDetails.add(securityID);
         usc.setSecIDDetails(secIDDetails);
         usc.setCertSyntax("3");
      }

      String uscSegment = usc.generate(dataEleSep, compEleSep);
      return uscSegment;
   }

   public String generateUSRSegment(String signedResult, char dataEleSep, char compEleSep, char repeatChar) throws SegmentGenException, CompositeGenException {
      USRSegment usr = new USRSegment();
      List<ValidationResult> results = usr.getResults();
      ValidationResult result = new ValidationResult();
      String validationQual = "1";
      result.setValidationQual(validationQual);
      result.setValidationValue(signedResult);
      results.add(result);
      String usrSegment = usr.generate(dataEleSep, compEleSep);
      return usrSegment;
   }

   public String generateUSTSegment(char dataEleSep, char compEleSep) throws SegmentGenException, CompositeGenException, ModuleException {
      USTSegment ust = new USTSegment();
      ust.setSecRefNum(this.uniqueRefNumber);
      ust.setTotalSegments("6");
      String ustSegment = ust.generate(dataEleSep, compEleSep);
      return ustSegment;
   }

   public String generateUNOSegment(char dataEleSep, char compEleSep, char repeatChar, String refID, String len, String filterType) throws SegmentGenException, CompositeGenException {
      UNOSegment uno = new UNOSegment();
      uno.setPkgRefNum("0");
      List<ReferenceIdentification> referenceIDs = uno.getReferenceIDs();
      ReferenceIdentification ref = new ReferenceIdentification();
      ref.setReferenceQual("1");
      ref.setReferenceID(refID);
      referenceIDs.add(ref);
      List<ObjectTypeIdentification> objectTypeIDs = uno.getObjectTypeIDs();
      ObjectTypeIdentification obj = new ObjectTypeIdentification();
      obj.setObjectTypeQual("46");
      obj.setObjectTypeAttrID(filterType);
      objectTypeIDs.add(obj);
      obj = new ObjectTypeIdentification();
      obj.setObjectTypeQual("62");
      obj.setObjectTypeAttrID("PKCS7");
      objectTypeIDs.add(obj);
      ObjectStatus objectStatus = new ObjectStatus();
      objectStatus.setObjectLength(len);
      uno.setObjectStatus(objectStatus);
      String unoSegment = uno.generate(dataEleSep, compEleSep);
      return unoSegment;
   }

   public String generateUNPSegment(char dataEleSep, char compEleSep, String len) throws SegmentGenException, CompositeGenException {
      UNPSegment unp = new UNPSegment();
      unp.setPkgRefNum("0");
      unp.setPkgLength(len);
      String unpSegment = unp.generate(dataEleSep, compEleSep);
      return unpSegment;
   }

   public String generateUSBSegment(char dataEleSep, char compEleSep, char repeatChar, String interchangeSender, String interchangeReceiver) throws SegmentGenException, CompositeGenException, KeystoreUtilsException {
      return null;
   }

   public String generateUSXSegment(char dataEleSep, char compEleSep, String refNum) throws SegmentGenException, CompositeGenException {
      return null;
   }

   public String generateUSYSegment(char dataEleSep, char compEleSep, boolean status) throws SegmentGenException, CompositeGenException {
      return null;
   }

   public String getCorrelationId() {
      return this.correlationId;
   }
}
