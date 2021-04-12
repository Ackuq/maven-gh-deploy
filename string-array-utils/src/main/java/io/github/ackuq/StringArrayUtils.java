package io.github.ackuq;

import java.util.Arrays;

/**
 * A class that provides simple utility functions that can be used on string
 * arrays.
 */
public class StringArrayUtils {
   /**
    * Trim each element in the array
    * 
    * @param array An array of values to be trimmed
    * @return The resulting array
    */
   public static String[] trim(String[] array) {
      return Arrays.stream(array)
              .map(s -> s != null ? s.trim() : null)
              .toArray(String[]::new);
   }

   /**
    * Capitalize each element in the array
    * 
    * @param array An array of values to be capitalized
    * @return The resulting array
    */
   public static String[] capitalize(String[] array) {
      return Arrays.stream(array)
              .map(s -> s !=null ? s.substring(0, 1).toUpperCase() + s.substring(1) : null)
              .toArray(String[]::new);
   }

   /**
    * Transforms the strings in the array to be lowercase
    * 
    * @param array An array of values which should be transformed
    * @return The resulting array
    */
   public static String[] toLowerCase(String[] array) {
      return Arrays.stream(array)
              .map(s -> s != null ? s.toLowerCase() : null)
              .toArray(String[]::new);
   }

   /**
    * Transforms the strings in the array to be uppercase
    * 
    * @param array An array of values which should be transformed
    * @return The resulting array
    */
   public static String[] toUpperCase(String[] array) {
      return Arrays.stream(array)
              .map(s -> s != null ? s.toUpperCase() : null)
              .toArray(String[]::new);
   }
}
