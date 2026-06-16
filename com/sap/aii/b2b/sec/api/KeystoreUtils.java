package com.sap.aii.b2b.sec.api;

import com.sap.aii.af.lib.trace.Trace;
import com.sap.aii.security.impl.keystore.KeyStoreManagerImplSystemLevel;
import com.sap.aii.security.lib.KeyStoreManager;
import com.sap.aii.security.lib.SecurityContext;
import com.sap.aii.security.lib.XiPermissionMode;
import com.sap.security.api.ssf.ISsfProfile;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class KeystoreUtils {
   public static final String VERSION_ID = "$Id: //tc/xpi.b2b.libs/PIBtwoB2_02_REL/src/_b2b_security_library_module/libm/api/com/sap/aii/b2b/sec/api/KeystoreUtils.java#3 $";
   private static final Trace TRACE = new Trace("$Id: //tc/xpi.b2b.libs/PIBtwoB2_02_REL/src/_b2b_security_library_module/libm/api/com/sap/aii/b2b/sec/api/KeystoreUtils.java#3 $");

   private static KeyStoreManager getKeyStoreManager() throws KeystoreUtilsException {
      String SIGNATURE = "getKeyStoreManager()";
      ClassLoader old_context_classloader = Thread.currentThread().getContextClassLoader();
      KeyStoreManager manager = null;

      try {
         Thread.currentThread().setContextClassLoader(NullPointerException.class.getClassLoader());
         SecurityContext securityContext = (SecurityContext)(new InitialContext()).lookup("com.sap.aii.sec.svc");
         manager = securityContext.getKeyStoreManager(XiPermissionMode.SYSTEM_LEVEL_FULL);
      } catch (NamingException e) {
         String error = "JNDI lookup for security context 'com.sap.aii.sec.svc' failed";
         TRACE.traceThrowable(500, error, e);
         throw new KeystoreUtilsException(error, e);
      } catch (KeyStoreException e) {
         String error = "KeyStoreException while retrieving KeyStore manager with System permission";
         TRACE.traceThrowable(500, error, e);
         throw new KeystoreUtilsException(error, e);
      } finally {
         Thread.currentThread().setContextClassLoader(old_context_classloader);
      }

      if (manager == null) {
         throw new KeystoreUtilsException("Unable to retriev KeyStore manager with System permission");
      } else {
         return manager;
      }
   }

   public static String[] getAllKeyStoreNames() throws KeystoreUtilsException {
      String SIGNATURE = "getAllKeyStoreNames()";
      TRACE.entering("getAllKeyStoreNames()");
      String[] keystores = null;

      try {
         KeyStoreManager km = getKeyStoreManager();
         keystores = km.getAllKeyStoreViews();
      } catch (KeyStoreException e) {
         String error = "Keystore manager failed to get keystores due to: " + e;
         TRACE.traceThrowable(500, error, e);
         throw new KeystoreUtilsException(error, e);
      }

      TRACE.exiting("getAllKeyStoreNames()");
      return keystores;
   }

   public static String[] getAllCertNames(String keyStore) throws KeystoreUtilsException {
      String SIGNATURE = "getAllCertNames(String keyStore)";
      TRACE.entering("getAllCertNames(String keyStore)");
      String[] certs = null;
      if (keyStore == null) {
         String error = "Keystore cannot be null.";
         TRACE.errorT("getAllCertNames(String keyStore)", error);
         throw new KeystoreUtilsException(error);
      } else {
         try {
            KeyStoreManager km = getKeyStoreManager();
            KeyStore ks = km.getKeyStore(keyStore);
            if (ks == null) {
               throw new KeystoreUtilsException("KeyStoreException while retrieving KeyStore");
            }

            certs = km.getKeyStoreAliases(ks);
         } catch (KeyStoreException e) {
            String error = "Keystore manager failed to get certificates from keystore: " + keyStore + " reason: " + e;
            TRACE.traceThrowable(500, error, e);
            throw new KeystoreUtilsException(error, e);
         }

         TRACE.exiting("getAllCertNames(String keyStore)");
         return certs;
      }
   }

   public static KeyStore getKeyStore(String keystore) throws KeystoreUtilsException {
      String SIGNATURE = "getKeyStore(String keyStore)";
      TRACE.entering("getKeyStore(String keyStore)");
      if (keystore != null && keystore.length() != 0) {
         KeyStore keyStore = null;

         try {
            KeyStoreManager km = getKeyStoreManager();
            keyStore = km.getKeyStore(keystore);
         } catch (KeyStoreException e) {
            String error = "Error while getting retrieving keystore: ";
            TRACE.traceThrowable(500, error, e);
            throw new KeystoreUtilsException(error, e);
         }

         TRACE.exiting("getKeyStore(String keyStore)");
         return keyStore;
      } else {
         String error = "Keystore cannot be null.";
         TRACE.errorT("getKeyStore(String keyStore)", error);
         throw new KeystoreUtilsException(error);
      }
   }

   public static Certificate getCertificate(String keyStore, String certName) throws KeystoreUtilsException {
      String SIGNATURE = "getCertificate(String keyStore, String certName)";
      TRACE.entering("getCertificate(String keyStore, String certName)");
      if (keyStore != null && keyStore.length() != 0) {
         if (certName != null && certName.length() != 0) {
            Certificate cert = null;

            try {
               KeyStoreManager km = getKeyStoreManager();
               KeyStore keystore = km.getKeyStore(keyStore);
               if (keystore == null) {
                  throw new KeystoreUtilsException("KeyStoreException while retrieving KeyStore");
               }

               cert = km.getISsfProfile(keystore, certName, (String)null).getCertificate();
            } catch (KeyStoreException e) {
               String error = "Keystore manager failed to get the certificate: " + certName + " from keystore: " + keyStore + " reason: " + e;
               TRACE.traceThrowable(500, error, e);
               throw new KeystoreUtilsException(error, e);
            }

            TRACE.exiting("getCertificate(String keyStore, String certName)");
            return cert;
         } else {
            String error = "Certificate name cannot be null or empty.";
            TRACE.errorT("getCertificate(String keyStore, String certName)", error);
            throw new KeystoreUtilsException(error);
         }
      } else {
         String error = "Keystore cannot be null.";
         TRACE.errorT("getCertificate(String keyStore, String certName)", error);
         throw new KeystoreUtilsException(error);
      }
   }

   public static X509Certificate getX509Certificate(String keyStore, String certName) throws KeystoreUtilsException {
      String SIGNATURE = "getX509Certificate(String keyStore, String certName)";
      TRACE.entering("getX509Certificate(String keyStore, String certName)");
      if (keyStore != null && keyStore.length() != 0) {
         if (certName != null && certName.length() != 0) {
            X509Certificate certificate;
            try {
               KeyStoreManager km = getKeyStoreManager();
               KeyStore keystore = km.getKeyStore(keyStore);
               if (keystore == null) {
                  throw new KeystoreUtilsException("KeyStoreException while retrieving KeyStore");
               }

               ISsfProfile ssfProfile = getKeyStoreManager().getISsfProfile(keystore, certName, (String)null);
               certificate = ssfProfile.getCertificate();
            } catch (KeyStoreException e) {
               String error = "Keystore manager failed to get the certificate: " + certName + " from keystore: " + keyStore + " reason: " + e;
               TRACE.catching("getX509Certificate(String keyStore, String certName)", e);
               throw new KeystoreUtilsException(error, e);
            }

            TRACE.exiting("getX509Certificate(String keyStore, String certName)");
            return certificate;
         } else {
            String error = "Certificate name cannot be null or empty.";
            TRACE.errorT("getX509Certificate(String keyStore, String certName)", error);
            throw new KeystoreUtilsException(error);
         }
      } else {
         String error = "Keystore cannot be null.";
         TRACE.errorT("getX509Certificate(String keyStore, String certName)", error);
         throw new KeystoreUtilsException(error);
      }
   }

   public static PrivateKey getPrivateKey(String keyStore, String alias) throws KeystoreUtilsException {
      String SIGNATURE = "getCertificate(String keyStore, String alias)";
      TRACE.entering("getCertificate(String keyStore, String alias)");
      if (keyStore != null && keyStore.length() != 0) {
         if (alias != null && alias.length() != 0) {
            PrivateKey key = null;

            try {
               KeyStoreManager km = getKeyStoreManager();
               KeyStore keystore = km.getKeyStore(keyStore);
               if (keystore == null) {
                  throw new KeystoreUtilsException("KeyStoreException while retrieving KeyStore");
               }

               key = km.getISsfProfile(keystore, alias, (String)null).getPrivateKey();
            } catch (KeyStoreException e) {
               String error = "Keystore manager failed to get the PrivateKey: " + key + " from keystore: " + keyStore + " reason: " + e;
               TRACE.traceThrowable(500, error, e);
               throw new KeystoreUtilsException(error, e);
            }

            TRACE.exiting("getCertificate(String keyStore, String alias)");
            return key;
         } else {
            String error = "Certificate name cannot be null or empty.";
            TRACE.errorT("getCertificate(String keyStore, String alias)", error);
            throw new KeystoreUtilsException(error);
         }
      } else {
         String error = "Keystore cannot be null.";
         TRACE.errorT("getCertificate(String keyStore, String alias)", error);
         throw new KeystoreUtilsException(error);
      }
   }

   public static void setCertificate(Certificate cert, String keystore, String certName) throws KeystoreUtilsException {
      String SIGNATURE = "setCertificate(Certificate cert, String keystore, String certName)";
      TRACE.entering("setCertificate(Certificate cert, String keystore, String certName)");
      ClassLoader old_context_classloader = Thread.currentThread().getContextClassLoader();

      try {
         Thread.currentThread().setContextClassLoader(NullPointerException.class.getClassLoader());
         KeyStoreManagerImplSystemLevel manager = new KeyStoreManagerImplSystemLevel(true);
         KeyStore ks = manager.getKeyStore(keystore);
         manager.setCertificateEntry(ks, certName, cert);
      } catch (KeyStoreException e) {
         TRACE.catching("setCertificate(Certificate cert, String keystore, String certName)", e);
         String error = "KeyStoreException while retrieving KeyStore manager with System permission";
         TRACE.traceThrowable(500, error, e);
         throw new KeystoreUtilsException(error, e);
      } finally {
         Thread.currentThread().setContextClassLoader(old_context_classloader);
      }

      TRACE.exiting("setCertificate(Certificate cert, String keystore, String certName)");
   }
}
