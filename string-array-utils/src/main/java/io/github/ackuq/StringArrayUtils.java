package io.github.ackuq;

/**
 * A class that provides simple utility functions that can be used on string
 * arrays.
 */
public class StringArrayUtils {
   /**
    * Trim each element in the array
    * 
    * @param array An array of values
    * @return The resulting array
    */
   public static String[] trim(String[] array) {
      String[] result = new String[array.length];

      for (int i = 0; i < array.length; i++) {
         String value = array[i];
         if (value != null) {
            result[i] = value.trim();
         }
      }
      return result;
   }

   /**
    * Capitalize each element in the array
    * 
    * @param array An array of values
    * @return The resulting array
    */
   public static String[] capitalize(String[] array) {
      String[] result = new String[array.length];

      for (int i = 0; i < array.length; i++) {
         String value = array[i];
         if (value != null) {
            result[i] = value.substring(0, 1).toUpperCase() + value.substring(1);
         }
      }
      return result;
   }

   /**
    * Transforms the strings in the array to be lowercase
    * 
    * @param array An array of values
    * @return The resulting array
    */
   public static String[] toLowerCase(String[] array) {
      String[] result = new String[array.length];

      for (int i = 0; i < array.length; i++) {
         String value = array[i];
         if (value != null) {
            result[i] = value.toLowerCase();
         }
      }
      return result;
   }

   /**
    * Transforms the strings in the array to be uppercase
    * 
    * @param array An array of values
    * @return The resulting array
    */
   public static String[] toUpperCase(String[] array) {
      String[] result = new String[array.length];

      for (int i = 0; i < array.length; i++) {
         String value = array[i];
         if (value != null) {
            result[i] = value.toUpperCase();
         }
      }
      return result;
   }
}
