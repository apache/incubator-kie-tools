/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.common.rebind;

/**
 * Utility methods for naming generated classes.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class NameUtil {

  /**
   * @return The given fully qualified class name with underscores replacing
   *         character not allowed in identifiers.
   */
  public static String derivedIdentifier(final String fullyQualifiedClassName) {
    return fullyQualifiedClassName.replace('.', '_').replace('$', '_');
  }

  /**
   * @param derivdedIdentifier A value returned from {@link #derivedIdentifier(String)}.
   * @return The derivedIdentifier with package parts shortened to initials.
   */
  public static String shortenDerivedIdentifier(final String derivdedIdentifier) {
    return shortenDerivedIdentifier(derivdedIdentifier, "_");
  }

  private static String shortenDerivedIdentifier(final String derivedIdentifier, final String delimiter) {
    final String[] parts = derivedIdentifier.split(delimiter);
    final StringBuilder builder = new StringBuilder();
    boolean haveSeenUpperCase = false;
    for (final String part : parts) {
      if (haveSeenUpperCase || Character.isUpperCase(part.charAt(0))) {
        builder.append(part);
        haveSeenUpperCase = true;
      }
      else {
        builder.append(part.charAt(0));
      }
      builder.append('_');
    }
    builder.delete(builder.length() - 1, builder.length());

    return builder.toString();
  }

  /**
   * @return A compact String representation of the given object's hashCode, using only characters in the ranges
   *         'A'-'Z', 'a'-'z', '0'-'9', '_', and '$'.
   */
  public static String getShortHashString(final Object obj) {
    return getShortHashString(obj.hashCode());
  }

  /**
   * @return A compact String representation of the given hashCode, using only characters in the ranges
   *         'A'-'Z', 'a'-'z', '0'-'9', '_', and '$'.
   */
  public static String getShortHashString(final int hashCode) {
    int remaining = hashCode;
    final StringBuilder builder = new StringBuilder(6);
    for (int i = 0; i < 6; i++) {
      final int part = remaining & 0b111111;
      builder.append(getHashChar(part));
      remaining = (remaining >> 6);
    }

    return builder.toString();
  }

  static char getHashChar(int part) {
    if (part <= 'Z' - 'A') {
      return (char) ('A' + part);
    }
    else {
      part -= 1 + ('Z' - 'A');
      if (part <= 'z' - 'a') {
        return (char) ('a' + part);
      }
      else {
        part -= 1 + ('z' - 'a');
        if (part <= '9' - '0') {
          return (char) ('0' + part);
        }
        else {
          part -= 1 + ('9' - '0');
          if (part == 0) {
            return '$';
          }
          else if (part == 1) {
            return '_';
          }
          else {
            throw new IllegalArgumentException("Argument [part] should satisfy [0 <= part < 64], but [part = " + part + "].");
          }
        }
      }
    }
  }

}
