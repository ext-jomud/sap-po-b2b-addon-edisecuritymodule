package com.sap.aii.b2b.sec.api;

import java.util.HashMap;
import java.util.Map;

public class EDACharacterMap {
   private static Map<Integer, Character> charMap = new HashMap();
   private static Map<Character, Integer> intMap = new HashMap();

   public static char getEDACharacter(int num) {
      return (Character)charMap.get(num);
   }

   public static int getEDAInteger(char ch) {
      return (Integer)intMap.get(ch);
   }

   static {
      charMap.put(0, '0');
      charMap.put(1, '1');
      charMap.put(2, '2');
      charMap.put(3, '3');
      charMap.put(4, '4');
      charMap.put(5, '5');
      charMap.put(6, '6');
      charMap.put(7, '7');
      charMap.put(8, '8');
      charMap.put(9, '9');
      charMap.put(10, 'A');
      charMap.put(11, 'B');
      charMap.put(12, 'C');
      charMap.put(13, 'D');
      charMap.put(14, 'E');
      charMap.put(15, 'F');
      charMap.put(16, 'G');
      charMap.put(17, 'H');
      charMap.put(18, 'I');
      charMap.put(19, 'J');
      charMap.put(20, 'K');
      charMap.put(21, 'L');
      charMap.put(22, 'M');
      charMap.put(23, 'N');
      charMap.put(24, 'O');
      charMap.put(25, 'P');
      charMap.put(26, 'Q');
      charMap.put(27, 'R');
      charMap.put(28, 'S');
      charMap.put(29, 'T');
      charMap.put(30, 'U');
      charMap.put(31, 'V');
      charMap.put(32, 'W');
      charMap.put(33, 'X');
      charMap.put(34, 'Y');
      charMap.put(35, 'Z');
      charMap.put(36, '(');
      charMap.put(37, ')');
      charMap.put(38, ',');
      charMap.put(39, '-');
      charMap.put(40, '.');
      charMap.put(41, '/');
      charMap.put(42, '=');
      intMap.put('0', 0);
      intMap.put('1', 1);
      intMap.put('2', 2);
      intMap.put('3', 3);
      intMap.put('4', 4);
      intMap.put('5', 5);
      intMap.put('6', 6);
      intMap.put('7', 7);
      intMap.put('8', 8);
      intMap.put('9', 9);
      intMap.put('A', 10);
      intMap.put('B', 11);
      intMap.put('C', 12);
      intMap.put('D', 13);
      intMap.put('E', 14);
      intMap.put('F', 15);
      intMap.put('G', 16);
      intMap.put('H', 17);
      intMap.put('I', 18);
      intMap.put('J', 19);
      intMap.put('K', 20);
      intMap.put('L', 21);
      intMap.put('M', 22);
      intMap.put('N', 23);
      intMap.put('O', 24);
      intMap.put('P', 25);
      intMap.put('Q', 26);
      intMap.put('R', 27);
      intMap.put('S', 28);
      intMap.put('T', 29);
      intMap.put('U', 30);
      intMap.put('V', 31);
      intMap.put('W', 32);
      intMap.put('X', 33);
      intMap.put('Y', 34);
      intMap.put('Z', 35);
      intMap.put('(', 36);
      intMap.put(')', 37);
      intMap.put(',', 38);
      intMap.put('-', 39);
      intMap.put('.', 40);
      intMap.put('/', 41);
      intMap.put('=', 42);
   }
}
