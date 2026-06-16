package com.sap.aii.b2b.sec.api;

public class EDCDeFilter {
   public static byte[] deFilter(byte[] sourceBytes) {
      int numCtrlBytes = sourceBytes.length / 8 + (sourceBytes.length % 8 == 0 ? 0 : 1);
      byte[] unFilteredExp = new byte[sourceBytes.length - numCtrlBytes];

      for(int i = 0; i < numCtrlBytes; ++i) {
         int arrayLength = sourceBytes.length - i * 8 < 8 ? sourceBytes.length % 8 : 8;
         byte[] tempArray = new byte[arrayLength];
         System.arraycopy(sourceBytes, i * 8, tempArray, 0, arrayLength);
         tempArray = getUnFilteredArray(tempArray);
         System.arraycopy(tempArray, 0, unFilteredExp, i * 7, tempArray.length);
      }

      return unFilteredExp;
   }

   private static byte[] getUnFilteredArray(byte[] sourceBytes) {
      byte[] unFilteredBytes = new byte[sourceBytes.length - 1];
      byte unMaskByte = -65;
      byte controlByte = sourceBytes[0];

      for(int i = 1; i < sourceBytes.length; ++i) {
         boolean unmask = false;
         if (i == 1) {
            unmask = 1 == ((byte)(controlByte >> 7) & 1);
         } else {
            unmask = 1 == ((byte)(controlByte >> 7 - i) & 1);
         }

         if (unmask) {
            unFilteredBytes[i - 1] = (byte)(sourceBytes[i] & unMaskByte);
         } else {
            unFilteredBytes[i - 1] = sourceBytes[i];
         }
      }

      return unFilteredBytes;
   }
}
