package com.sap.aii.b2b.sec.api;

public class EDCFilter {
   public static byte[] filter(byte[] sourceBytes) {
      int numCtrlBytes = sourceBytes.length / 7 + (sourceBytes.length % 7 == 0 ? 0 : 1);
      byte[] filteredExp = new byte[sourceBytes.length + numCtrlBytes];

      for(int i = 0; i < numCtrlBytes; ++i) {
         int arrayLength = sourceBytes.length - i * 7 < 7 ? sourceBytes.length % 7 : 7;
         byte[] tempArray = new byte[arrayLength];
         System.arraycopy(sourceBytes, i * 7, tempArray, 0, arrayLength);
         tempArray = getFilteredArray(tempArray);
         System.arraycopy(tempArray, 0, filteredExp, i * 8, tempArray.length);
      }

      return filteredExp;
   }

   private static byte[] getFilteredArray(byte[] sourceBytes) {
      byte[] filteredBytes = new byte[sourceBytes.length + 1];
      byte maskByte = 64;
      byte controlByte = 64;

      for(int i = 0; i < sourceBytes.length; ++i) {
         if (i == 0) {
            filteredBytes[i + 1] = (byte)(sourceBytes[i] | maskByte);
            if (filteredBytes[i + 1] != sourceBytes[i]) {
               controlByte = (byte)(controlByte | 128);
            }
         } else {
            filteredBytes[i + 1] = (byte)(sourceBytes[i] | maskByte);
            if (filteredBytes[i + 1] != sourceBytes[i]) {
               controlByte = (byte)(controlByte | 64 >> i);
            }
         }
      }

      filteredBytes[0] = controlByte;
      return filteredBytes;
   }
}
