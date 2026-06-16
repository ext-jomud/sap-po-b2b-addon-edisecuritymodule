package com.sap.aii.b2b.sec.api;

public class EDAFilter {
   public static byte[] filter(byte[] source) {
      int[] unsignedSource = new int[source.length];

      for(int index = 0; index < source.length; ++index) {
         unsignedSource[index] = source[index] & 255;
      }

      int edaByteCount = unsignedSource.length * 3 / 2 + unsignedSource.length % 2;
      byte[] target = new byte[edaByteCount];
      int i = 0;

      for(int targetIndex = 0; i < unsignedSource.length; i += 2) {
         int arrayLength = unsignedSource.length - i == 1 ? 2 : 3;
         int[] tempArray = new int[arrayLength - 1];
         System.arraycopy(unsignedSource, i, tempArray, 0, arrayLength - 1);
         byte[] convArray = convertToEDA(tempArray);
         System.arraycopy(convArray, 0, target, targetIndex, arrayLength);
         targetIndex += arrayLength;
      }

      return target;
   }

   private static byte[] convertToEDA(int[] source) {
      byte[] target = new byte[source.length + 1];
      int value = -1;
      if (source.length == 2) {
         value = source[0] << 8 | source[1];
         target[0] = (byte)EDACharacterMap.getEDACharacter(value / 1849);
         target[1] = (byte)EDACharacterMap.getEDACharacter(value % 1849 / 43);
         target[2] = (byte)EDACharacterMap.getEDACharacter(value % 1849 % 43);
      } else if (source.length == 1) {
         value = source[0];
         target[0] = (byte)EDACharacterMap.getEDACharacter(value / 43);
         target[1] = (byte)EDACharacterMap.getEDACharacter(value % 43);
      }

      return target;
   }
}
