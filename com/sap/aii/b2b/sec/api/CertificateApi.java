package com.sap.aii.b2b.sec.api;

import com.sap.security.core.server.util0.Base64;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CertificateApi {
   public static final String EDC_FILTER = "EDC";
   public static final String EDA_FILTER = "EDA";
   public static final String BASE64_FILTER = "BASE64";
   private Certificate certificate;
   private X509Certificate CERT509;

   public CertificateApi(String keystore, String alias) throws KeystoreUtilsException {
      this.certificate = KeystoreUtils.getCertificate(keystore, alias);
      this.CERT509 = KeystoreUtils.getX509Certificate(keystore, alias);
   }

   public CertificateApi(Certificate certificate) {
      this.certificate = certificate;
   }

   public CertificateApi(byte[] pkcsBytes, String filter) throws KeystoreUtilsException {
      byte[] unFilteredP7BFile = null;
      if ("EDC".equalsIgnoreCase(filter)) {
         unFilteredP7BFile = EDCDeFilter.deFilter(pkcsBytes);
      } else if ("EDA".equalsIgnoreCase(filter)) {
         unFilteredP7BFile = EDADeFilter.deFilter(pkcsBytes);
      } else if ("BASE64".equalsIgnoreCase(filter)) {
         try {
            unFilteredP7BFile = Base64.decode(new String(pkcsBytes));
         } catch (ParseException e) {
            throw new KeystoreUtilsException(e);
         }
      }

      try {
         if (unFilteredP7BFile != null) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Collection<X509Certificate> list = cf.generateCertificates(new ByteArrayInputStream(unFilteredP7BFile));
            this.certificate = (Certificate)list.iterator().next();
         }

      } catch (CertificateException e) {
         throw new KeystoreUtilsException(e);
      }
   }

   public Certificate getCertificate() {
      return this.certificate;
   }

   public String getIssuerDN() {
      String result = this.CERT509.getIssuerDN().toString();
      return result;
   }

   public byte[] getIssuerDNEncoded(String filter, String encType) throws KeystoreUtilsException, UnsupportedEncodingException {
      byte[] issuerDN = this.CERT509.getIssuerDN().toString().getBytes(encType);
      if ("EDC".equalsIgnoreCase(filter)) {
         return EDCFilter.filter(issuerDN);
      } else if ("EDA".equalsIgnoreCase(filter)) {
         return EDAFilter.filter(issuerDN);
      } else if ("BASE64".equalsIgnoreCase(filter)) {
         return Base64.encode(issuerDN, 0).getBytes();
      } else {
         throw new KeystoreUtilsException("Filter: " + filter + " not Supported.");
      }
   }

   public BigInteger getModulus() {
      RSAPublicKey publicKey = (RSAPublicKey)this.certificate.getPublicKey();
      BigInteger modulus = publicKey.getModulus();
      return modulus;
   }

   public byte[] getEDCModulus() {
      BigInteger modulus = this.getModulus();
      byte[] modulusBytes = modulus.toByteArray();
      byte[] trimmedModulus = null;
      if (modulusBytes[0] == 0) {
         trimmedModulus = new byte[modulusBytes.length - 1];
         System.arraycopy(modulusBytes, 1, trimmedModulus, 0, trimmedModulus.length);
      } else {
         trimmedModulus = modulusBytes;
      }

      return EDCFilter.filter(trimmedModulus);
   }

   public byte[] getEDAModulus() {
      BigInteger modulus = this.getModulus();
      byte[] modulusBytes = modulus.toByteArray();
      return EDAFilter.filter(modulusBytes);
   }

   public byte[] getBase64Modulus() {
      BigInteger modulus = this.getModulus();
      byte[] modulusBytes = modulus.toByteArray();
      return Base64.encode(modulusBytes, 0).getBytes();
   }

   public BigInteger getExponent() {
      RSAPublicKey publicKey = (RSAPublicKey)this.certificate.getPublicKey();
      BigInteger exponent = publicKey.getPublicExponent();
      return exponent;
   }

   public byte[] getEDCExponent() {
      BigInteger exponent = this.getExponent();
      byte[] exponentBytes = exponent.toByteArray();
      byte[] trimmedExponent = null;
      if (exponentBytes[0] == 0) {
         trimmedExponent = new byte[exponentBytes.length - 1];
         System.arraycopy(exponentBytes, 1, trimmedExponent, 0, trimmedExponent.length);
      } else {
         trimmedExponent = exponentBytes;
      }

      return EDCFilter.filter(trimmedExponent);
   }

   public byte[] getEDAExponent() {
      BigInteger exponent = this.getExponent();
      byte[] exponentBytes = exponent.toByteArray();
      return EDAFilter.filter(exponentBytes);
   }

   public byte[] getBase64Exponent() {
      BigInteger exponent = this.getExponent();
      byte[] exponentBytes = exponent.toByteArray();
      return Base64.encode(exponentBytes, 0).getBytes();
   }

   public String getSerialNumber() {
      String serialNum = null;
      if (this.certificate instanceof X509Certificate) {
         serialNum = ((X509Certificate)this.certificate).getSerialNumber().toString();
      }

      return serialNum;
   }

   public String getHexSerialNumber() {
      String serialNum = null;
      if (this.certificate instanceof X509Certificate) {
         serialNum = ((X509Certificate)this.certificate).getSerialNumber().toString(16);
      }

      return serialNum;
   }

   public byte[] getCertificateBytes() throws KeystoreUtilsException {
      try {
         return this.certificate.getEncoded();
      } catch (CertificateEncodingException e) {
         throw new KeystoreUtilsException(e);
      }
   }

   public byte[] getCertificateEDCBytes() throws KeystoreUtilsException {
      try {
         return EDCFilter.filter(this.certificate.getEncoded());
      } catch (CertificateEncodingException e) {
         throw new KeystoreUtilsException(e);
      }
   }

   public byte[] getCertificateEDABytes() throws KeystoreUtilsException {
      try {
         return EDAFilter.filter(this.certificate.getEncoded());
      } catch (CertificateEncodingException e) {
         throw new KeystoreUtilsException(e);
      }
   }

   public byte[] getCertificateBase64Bytes() throws KeystoreUtilsException {
      try {
         return Base64.encode(this.certificate.getEncoded(), 0).getBytes();
      } catch (CertificateEncodingException e) {
         throw new KeystoreUtilsException(e);
      }
   }

   public byte[] getPKCS7EncodedCertificateBytes() throws KeystoreUtilsException {
      byte[] result = null;

      try {
         CertificateFactory cf = CertificateFactory.getInstance("X.509");
         List<X509Certificate> certList = new ArrayList();
         certList.add((X509Certificate)this.certificate);
         CertPath certPath = cf.generateCertPath(certList);
         result = certPath.getEncoded("PKCS7");
         return result;
      } catch (CertificateEncodingException e) {
         throw new KeystoreUtilsException(e);
      } catch (CertificateException e) {
         throw new KeystoreUtilsException(e);
      }
   }

   public byte[] getPKCS7EncodedCertificateEDCBytes() throws KeystoreUtilsException {
      return EDCFilter.filter(this.getPKCS7EncodedCertificateBytes());
   }

   public byte[] getPKCS7EncodedCertificateEDABytes() throws KeystoreUtilsException {
      return EDAFilter.filter(this.getPKCS7EncodedCertificateBytes());
   }

   public byte[] getPKCS7EncodedCertificateBase64Bytes() throws KeystoreUtilsException {
      return Base64.encode(this.getPKCS7EncodedCertificateBytes(), 0).getBytes();
   }

   public byte[] getModulus(String filter) throws KeystoreUtilsException {
      if (filter == null) {
         return this.getModulus().toByteArray();
      } else if ("EDC".equalsIgnoreCase(filter)) {
         return this.getEDCModulus();
      } else if ("EDA".equalsIgnoreCase(filter)) {
         return this.getEDAModulus();
      } else if ("BASE64".equalsIgnoreCase(filter)) {
         return this.getBase64Modulus();
      } else {
         throw new KeystoreUtilsException("Filter: " + filter + " not Supported.");
      }
   }

   public byte[] getExponent(String filter) throws KeystoreUtilsException {
      if (filter == null) {
         return this.getExponent().toByteArray();
      } else if ("EDC".equalsIgnoreCase(filter)) {
         return this.getEDCExponent();
      } else if ("EDA".equalsIgnoreCase(filter)) {
         return this.getEDAExponent();
      } else if ("BASE64".equalsIgnoreCase(filter)) {
         return this.getBase64Exponent();
      } else {
         throw new KeystoreUtilsException("Filter: " + filter + " not Supported.");
      }
   }

   public byte[] getCertificateBytes(String filter) throws KeystoreUtilsException {
      if (filter == null) {
         return this.getCertificateBytes();
      } else if ("EDC".equalsIgnoreCase(filter)) {
         return this.getCertificateEDCBytes();
      } else if ("EDA".equalsIgnoreCase(filter)) {
         return this.getCertificateEDABytes();
      } else if ("BASE64".equalsIgnoreCase(filter)) {
         return this.getCertificateBase64Bytes();
      } else {
         throw new KeystoreUtilsException("Filter: " + filter + " not Supported.");
      }
   }

   public byte[] getPKCS7EncodedCertificateBytes(String filter) throws KeystoreUtilsException {
      if (filter == null) {
         return this.getPKCS7EncodedCertificateBytes();
      } else if ("EDC".equalsIgnoreCase(filter)) {
         return this.getPKCS7EncodedCertificateEDCBytes();
      } else if ("EDA".equalsIgnoreCase(filter)) {
         return this.getPKCS7EncodedCertificateEDABytes();
      } else if ("BASE64".equalsIgnoreCase(filter)) {
         return this.getPKCS7EncodedCertificateBase64Bytes();
      } else {
         throw new KeystoreUtilsException("Filter: " + filter + " not Supported.");
      }
   }

   public PublicKey getPublicKey() {
      return this.certificate.getPublicKey();
   }

   public static PublicKey getRSAPublicKey(byte[] modulus, byte[] exponent, String filter) throws KeystoreUtilsException {
      BigInteger mod = null;
      BigInteger exp = null;
      if (filter == null) {
         mod = new BigInteger(modulus);
         exp = new BigInteger(exponent);
      } else if ("EDC".equalsIgnoreCase(filter)) {
         modulus = EDCDeFilter.deFilter(modulus);
         byte[] signedModulus = new byte[modulus.length + 1];
         signedModulus[0] = 0;
         System.arraycopy(modulus, 0, signedModulus, 1, modulus.length);
         exponent = EDCDeFilter.deFilter(exponent);
         byte[] signedExponent = new byte[exponent.length + 1];
         signedExponent[0] = 0;
         System.arraycopy(exponent, 0, signedExponent, 1, exponent.length);
         mod = new BigInteger(signedModulus);
         exp = new BigInteger(signedExponent);
      } else if ("EDA".equalsIgnoreCase(filter)) {
         mod = new BigInteger(EDADeFilter.deFilter(modulus));
         exp = new BigInteger(EDADeFilter.deFilter(exponent));
      } else {
         if (!"BASE64".equalsIgnoreCase(filter)) {
            throw new KeystoreUtilsException("Filter: " + filter + " not Supported.");
         }

         try {
            mod = new BigInteger(Base64.decode(new String(modulus)));
            exp = new BigInteger(Base64.decode(new String(exponent)));
         } catch (ParseException e) {
            throw new KeystoreUtilsException(e);
         }
      }

      PublicKey pubKey = null;

      try {
         RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(mod, exp);
         KeyFactory fact = KeyFactory.getInstance("RSA");
         pubKey = fact.generatePublic(pubKeySpec);
         return pubKey;
      } catch (NoSuchAlgorithmException e) {
         throw new KeystoreUtilsException(e);
      } catch (InvalidKeySpecException e) {
         throw new KeystoreUtilsException(e);
      }
   }
}
