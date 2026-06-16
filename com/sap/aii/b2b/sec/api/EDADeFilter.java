package com.sap.aii.b2b.sec.api;

public class EDADeFilter {
   public static byte[] deFilter(byte[] source) {
      int edaByteCount = source.length * 2 / 3;
      byte[] target = new byte[edaByteCount];
      int i = 0;

      for(int targetIndex = 0; i < source.length; i += 3) {
         int arrayLength = source.length - i == 2 ? 1 : 2;
         byte[] tempArray = new byte[arrayLength + 1];
         System.arraycopy(source, i, tempArray, 0, arrayLength + 1);
         tempArray = convertFromEDA(tempArray);
         System.arraycopy(tempArray, 0, target, targetIndex, arrayLength);
         targetIndex += arrayLength;
      }

      return target;
   }

   private static byte[] convertFromEDA(byte[] source) {
      int length = source.length * 2 / 3;
      byte[] target = new byte[length];
      if (length == 2) {
         int firstByte = EDACharacterMap.getEDAInteger((char)source[0]);
         int secondByte = EDACharacterMap.getEDAInteger((char)source[1]);
         int thirdByte = EDACharacterMap.getEDAInteger((char)source[2]);
         int number = firstByte * 1849 + secondByte * 43 + thirdByte;
         target[0] = (byte)(number >> 8);
         target[1] = (byte)(number << 8 >> 8);
      } else if (length == 1) {
         int firstByte = EDACharacterMap.getEDAInteger((char)source[0]);
         int secondByte = EDACharacterMap.getEDAInteger((char)source[1]);
         int number = firstByte * 43 + secondByte;
         target[0] = (byte)number;
      }

      return target;
   }
}
