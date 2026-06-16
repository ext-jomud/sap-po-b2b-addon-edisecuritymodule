package com.sap.aii.edi.sec.module;

import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.aii.b2b.edifact.sec.api.CompositeGenException;
import com.sap.aii.b2b.edifact.sec.api.SegmentGenException;
import com.sap.aii.b2b.sec.api.CertificateApi;
import com.sap.aii.b2b.sec.api.KeystoreUtilsException;
import java.io.UnsupportedEncodingException;
import javax.resource.ResourceException;

public interface AutackHelper {
   String generateUSHSegment(char var1, char var2, char var3, String var4, String var5) throws SegmentGenException, CompositeGenException, ResourceException, ModuleException;

   String generateOuterUSASegment(char var1, char var2, char var3) throws SegmentGenException, CompositeGenException, KeystoreUtilsException, ModuleException;

   String generateUSASegment(char var1, char var2, char var3, String var4, CertificateApi var5, String var6) throws SegmentGenException, CompositeGenException, KeystoreUtilsException, UnsupportedEncodingException, ModuleException;

   String generateUSCSegment(String var1, char var2, char var3, char var4, String var5) throws SegmentGenException, CompositeGenException;

   String generateUSRSegment(String var1, char var2, char var3, char var4) throws SegmentGenException, CompositeGenException;

   String generateUSTSegment(char var1, char var2) throws SegmentGenException, CompositeGenException, ModuleException;

   String generateUNOSegment(char var1, char var2, char var3, String var4, String var5, String var6) throws SegmentGenException, CompositeGenException;

   String generateUNPSegment(char var1, char var2, String var3) throws SegmentGenException, CompositeGenException;

   String generateUSBSegment(char var1, char var2, char var3, String var4, String var5) throws SegmentGenException, CompositeGenException, KeystoreUtilsException;

   String generateUSXSegment(char var1, char var2, String var3) throws SegmentGenException, CompositeGenException;

   String generateUSYSegment(char var1, char var2, boolean var3) throws SegmentGenException, CompositeGenException;

   void generateRandomNumber();
}
