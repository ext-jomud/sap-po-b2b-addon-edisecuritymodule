package com.sap.aii.b2b.sec.api;

import com.sap.aii.af.lib.trace.Trace;
import com.sap.security.core.server.util0.Base64;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

public class SigningUtilsIAIK {
   public static final String VERSION_ID = "$Id: //tc/xpi.b2b.libs/PIBtwoB2_02_REL/src/_b2b_security_library_module/libm/api/com/sap/aii/b2b/sec/api/SigningUtilsIAIK.java#2 $";
   private static final Trace TRACE = new Trace("$Id: //tc/xpi.b2b.libs/PIBtwoB2_02_REL/src/_b2b_security_library_module/libm/api/com/sap/aii/b2b/sec/api/SigningUtilsIAIK.java#2 $");
   public static final String EDC_FILTER = "EDC";
   public static final String EDA_FILTER = "EDA";
   public static final String BASE64_FILTER = "BASE64";

   public static byte[] sign(byte[] data, String keyStore, String alias) throws KeystoreUtilsException, CMSException {
      String SIGNATURE = "sign(byte[] data, String keyStore, String alias)";
      TRACE.entering("sign(byte[] data, String keyStore, String alias)");
      RSAPrivateKey privKey = (RSAPrivateKey)KeystoreUtils.getPrivateKey(keyStore, alias);
      byte[] sign = sign(data, privKey);
      TRACE.exiting("sign(byte[] data, String keyStore, String alias)");
      return sign;
   }

   public static byte[] sign(byte[] data, RSAPrivateKey privKey) throws KeystoreUtilsException, CMSException {
      String SIGNATURE = "sign(byte[] data, RSAPrivateKey privKey)";
      TRACE.entering("sign(byte[] data, RSAPrivateKey privKey)");
      byte[] sign = null;

      try {
         Signature signature = Signature.getInstance("RSA-ISO9796-2-2-3", "IAIK");
         signature.initSign(privKey);
         signature.update(data);
         sign = signature.sign();
      } catch (SignatureException e) {
         String error = "Error while signing private key.";
         TRACE.traceThrowable(500, error, e);
         throw new CMSException(error, e);
      } catch (NoSuchAlgorithmException e) {
         String error = "Error while signing with given algorithm.";
         TRACE.traceThrowable(500, error, e);
         throw new CMSException(error, e);
      } catch (NoSuchProviderException e) {
         String error = "Error while signing IAIK provider.";
         TRACE.traceThrowable(500, error, e);
         throw new CMSException(error, e);
      } catch (InvalidKeyException e) {
         String error = "Invalid Key for Signing.";
         TRACE.traceThrowable(500, error, e);
         throw new CMSException(error, e);
      }

      TRACE.exiting("sign(byte[] data, RSAPrivateKey privKey)");
      return sign;
   }

   public static boolean verify(byte[] data, byte[] signature, String keyStore, String alias) throws KeystoreUtilsException, CMSException {
      String SIGNATURE = "verify(byte[] data, byte[] signature, String keyStore, String alias)";
      TRACE.entering("verify(byte[] data, byte[] signature, String keyStore, String alias)");
      Certificate cert = null;

      try {
         cert = KeystoreUtils.getCertificate(keyStore, alias);
      } catch (KeystoreUtilsException e) {
         String error = "Error while getting public key: " + keyStore + ":" + alias;
         TRACE.traceThrowable(500, error, e);
         throw e;
      }

      boolean result = verify(data, signature, (RSAPublicKey)cert.getPublicKey());
      TRACE.exiting("verify(byte[] data, byte[] signature, String keyStore, String alias)");
      return result;
   }

   public static boolean verify(byte[] data, byte[] sign, RSAPublicKey pubKey) throws CMSException {
      String SIGNATURE = "verify(byte[] data, byte[] sign, RSAPublicKey pubKey)";
      TRACE.entering("verify(byte[] data, byte[] sign, RSAPublicKey pubKey)");
      boolean result = false;

      try {
         Signature signature = Signature.getInstance("RSA-ISO9796-2-2-3", "IAIK");
         signature.initVerify(pubKey);
         signature.update(data);
         result = signature.verify(sign);
      } catch (SignatureException e) {
         String error = "Error while verifying with given public key.";
         TRACE.traceThrowable(500, error, e);
         throw new CMSException(error, e);
      } catch (NoSuchAlgorithmException e) {
         String error = "Error while verifying with given algorithm.";
         TRACE.traceThrowable(500, error, e);
         throw new CMSException(error, e);
      } catch (NoSuchProviderException e) {
         String error = "Error while verifying IAIK provider.";
         TRACE.traceThrowable(500, error, e);
         throw new CMSException(error, e);
      } catch (InvalidKeyException e) {
         String error = "Invalid Key for verifying.";
         TRACE.traceThrowable(500, error, e);
         throw new CMSException(error, e);
      }

      TRACE.exiting("verify(byte[] data, byte[] sign, RSAPublicKey pubKey)");
      return result;
   }

   public static byte[] signEDC(byte[] data, String keyStore, String alias) throws KeystoreUtilsException, CMSException {
      String SIGNATURE = "signEDC(byte[] data, String keyStore, String alias)";
      TRACE.entering("signEDC(byte[] data, String keyStore, String alias)");
      byte[] signature = sign(data, keyStore, alias);
      signature = EDCFilter.filter(signature);
      TRACE.exiting("signEDC(byte[] data, String keyStore, String alias)");
      return signature;
   }

   public static byte[] signEDC(byte[] data, RSAPrivateKey privKey) throws KeystoreUtilsException, CMSException {
      String SIGNATURE = "signEDC(byte[] data, RSAPrivateKey privKey)";
      TRACE.entering("signEDC(byte[] data, RSAPrivateKey privKey)");
      byte[] signature = sign(data, privKey);
      signature = EDCFilter.filter(signature);
      TRACE.exiting("signEDC(byte[] data, RSAPrivateKey privKey)");
      return signature;
   }

   public static boolean verifyEDC(byte[] data, byte[] signature, String keyStore, String alias) throws KeystoreUtilsException, CMSException {
      String SIGNATURE = "verifyEDC(byte[] data, byte[] signature, String keyStore, String alias)";
      TRACE.entering("verifyEDC(byte[] data, byte[] signature, String keyStore, String alias)");
      signature = EDCDeFilter.deFilter(signature);
      boolean result = verify(data, signature, keyStore, alias);
      TRACE.exiting("verifyEDC(byte[] data, byte[] signature, String keyStore, String alias)");
      return result;
   }

   public static boolean verifyEDC(byte[] data, byte[] signature, RSAPublicKey pubKey) throws CMSException {
      String SIGNATURE = "verifyEDC(byte[] data, byte[] signature, RSAPublicKey pubKey)";
      TRACE.entering("verifyEDC(byte[] data, byte[] signature, RSAPublicKey pubKey)");
      signature = EDCDeFilter.deFilter(signature);
      boolean result = verify(data, signature, pubKey);
      TRACE.exiting("verifyEDC(byte[] data, byte[] signature, RSAPublicKey pubKey)");
      return result;
   }

   public static byte[] signEDA(byte[] data, String keyStore, String alias) throws KeystoreUtilsException, CMSException {
      String SIGNATURE = "signEDA(byte[] data, String keyStore, String alias)";
      TRACE.entering("signEDA(byte[] data, String keyStore, String alias)");
      byte[] signature = sign(data, keyStore, alias);
      signature = EDAFilter.filter(signature);
      TRACE.exiting("signEDA(byte[] data, String keyStore, String alias)");
      return signature;
   }

   public static byte[] signEDA(byte[] data, RSAPrivateKey privKey) throws KeystoreUtilsException, CMSException {
      String SIGNATURE = "signEDA(byte[] data, RSAPrivateKey privKey)";
      TRACE.entering("signEDA(byte[] data, RSAPrivateKey privKey)");
      byte[] signature = sign(data, privKey);
      signature = EDAFilter.filter(signature);
      TRACE.exiting("signEDA(byte[] data, RSAPrivateKey privKey)");
      return signature;
   }

   public static boolean verifyEDA(byte[] data, byte[] signature, String keyStore, String alias) throws KeystoreUtilsException, CMSException {
      String SIGNATURE = "verifyEDA(byte[] data, byte[] signature, String keyStore, String alias)";
      TRACE.entering("verifyEDA(byte[] data, byte[] signature, String keyStore, String alias)");
      signature = EDADeFilter.deFilter(signature);
      boolean result = verify(data, signature, keyStore, alias);
      TRACE.exiting("verifyEDA(byte[] data, byte[] signature, String keyStore, String alias)");
      return result;
   }

   public static boolean verifyEDA(byte[] data, byte[] signature, RSAPublicKey pubKey) throws CMSException {
      String SIGNATURE = "verifyEDA(byte[] data, byte[] signature, RSAPublicKey pubKey)";
      TRACE.entering("verifyEDA(byte[] data, byte[] signature, RSAPublicKey pubKey)");
      signature = EDADeFilter.deFilter(signature);
      boolean result = verify(data, signature, pubKey);
      TRACE.exiting("verifyEDA(byte[] data, byte[] signature, RSAPublicKey pubKey)");
      return result;
   }

   public static byte[] signBase64(byte[] data, String keyStore, String alias) throws KeystoreUtilsException, CMSException {
      String SIGNATURE = "signBase64(byte[] data, String keyStore, String alias)";
      TRACE.entering("signBase64(byte[] data, String keyStore, String alias)");
      byte[] signature = sign(data, keyStore, alias);
      signature = Base64.encode(signature, 0).getBytes();
      TRACE.exiting("signBase64(byte[] data, String keyStore, String alias)");
      return signature;
   }

   public static byte[] signBase64(byte[] data, RSAPrivateKey privKey) throws KeystoreUtilsException, CMSException {
      String SIGNATURE = "signBase64(byte[] data, RSAPrivateKey privKey)";
      TRACE.entering("signBase64(byte[] data, RSAPrivateKey privKey)");
      byte[] signature = sign(data, privKey);
      signature = Base64.encode(signature, 0).getBytes();
      TRACE.exiting("signBase64(byte[] data, RSAPrivateKey privKey)");
      return signature;
   }

   public static boolean verifyBase64(byte[] data, byte[] signature, String keyStore, String alias) throws KeystoreUtilsException, CMSException {
      String SIGNATURE = "verifyBase64(byte[] data, byte[] signature, String keyStore, String alias)";
      TRACE.entering("verifyBase64(byte[] data, byte[] signature, String keyStore, String alias)");

      try {
         signature = Base64.decode(new String(signature));
      } catch (ParseException e) {
         String error = "Error in Base64 decoding.";
         TRACE.traceThrowable(500, error, e);
         throw new CMSException(error, e);
      }

      boolean result = verify(data, signature, keyStore, alias);
      TRACE.exiting("verifyBase64(byte[] data, byte[] signature, String keyStore, String alias)");
      return result;
   }

   public static boolean verifyBase64(byte[] data, byte[] signature, RSAPublicKey pubKey) throws CMSException {
      String SIGNATURE = "verifyBase64(byte[] data, byte[] signature, RSAPublicKey pubKey)";
      TRACE.entering("verifyBase64(byte[] data, byte[] signature, RSAPublicKey pubKey)");

      try {
         signature = Base64.decode(new String(signature));
      } catch (ParseException e) {
         String error = "Error in Base64 decoding.";
         TRACE.traceThrowable(500, error, e);
         throw new CMSException(error, e);
      }

      boolean result = verify(data, signature, pubKey);
      TRACE.exiting("verifyBase64(byte[] data, byte[] signature, RSAPublicKey pubKey)");
      return result;
   }

   public static byte[] sign(byte[] data, String keyStore, String alias, String filter) throws KeystoreUtilsException, CMSException {
      if (filter == null) {
         return sign(data, keyStore, alias);
      } else if ("EDC".equalsIgnoreCase(filter)) {
         return signEDC(data, keyStore, alias);
      } else if ("EDA".equalsIgnoreCase(filter)) {
         return signEDA(data, keyStore, alias);
      } else if ("BASE64".equalsIgnoreCase(filter)) {
         return signBase64(data, keyStore, alias);
      } else {
         throw new CMSException("Filter: " + filter + " not Supported.");
      }
   }

   public static byte[] sign(byte[] data, RSAPrivateKey privKey, String filter) throws KeystoreUtilsException, CMSException {
      if (filter == null) {
         return sign(data, privKey);
      } else if ("EDC".equalsIgnoreCase(filter)) {
         return signEDC(data, privKey);
      } else if ("EDA".equalsIgnoreCase(filter)) {
         return signEDA(data, privKey);
      } else if ("BASE64".equalsIgnoreCase(filter)) {
         return signBase64(data, privKey);
      } else {
         throw new CMSException("Filter: " + filter + " not Supported.");
      }
   }

   public static boolean verify(byte[] data, byte[] signature, String keyStore, String alias, String filter) throws KeystoreUtilsException, CMSException {
      if (filter == null) {
         return verify(data, signature, keyStore, alias);
      } else if ("EDC".equalsIgnoreCase(filter)) {
         return verifyEDC(data, signature, keyStore, alias);
      } else if ("EDA".equalsIgnoreCase(filter)) {
         return verifyEDA(data, signature, keyStore, alias);
      } else if ("BASE64".equalsIgnoreCase(filter)) {
         return verifyBase64(data, signature, keyStore, alias);
      } else {
         throw new CMSException("Filter: " + filter + " not Supported.");
      }
   }

   public static boolean verify(byte[] data, byte[] signature, RSAPublicKey pubKey, String filter) throws CMSException {
      if (filter == null) {
         return verify(data, signature, pubKey);
      } else if ("EDC".equalsIgnoreCase(filter)) {
         return verifyEDC(data, signature, pubKey);
      } else if ("EDA".equalsIgnoreCase(filter)) {
         return verifyEDA(data, signature, pubKey);
      } else if ("BASE64".equalsIgnoreCase(filter)) {
         return verifyBase64(data, signature, pubKey);
      } else {
         throw new CMSException("Filter: " + filter + " not Supported.");
      }
   }
}
