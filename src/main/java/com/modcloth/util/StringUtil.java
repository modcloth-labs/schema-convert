package com.modcloth.util;

/**
 * Utilities and convenience methods for processing strings.
 * 
 * @author modcloth
 */
public class StringUtil {
  /**
   * Performs a null-safe "equals" comparison of two objects.
   * 
   * @param left the left hand side of the comparison
   * @param right the right hand side of the comparison
   * @return true if both values are non-null and equal, false otherwise
   */
  public static boolean nullSafeEquals(Object left, Object right) {
      boolean result = false;

      if (left != null && right != null && left.equals(right)) {
        result = true;
      }
      return result;
  }
}
