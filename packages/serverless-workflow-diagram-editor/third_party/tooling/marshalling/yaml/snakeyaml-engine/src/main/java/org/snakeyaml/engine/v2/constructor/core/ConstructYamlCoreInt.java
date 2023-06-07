/*
 * Copyright (c) 2018, SnakeYAML
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.snakeyaml.engine.v2.constructor.core;

import java.math.BigInteger;

import org.snakeyaml.engine.v2.constructor.ConstructScalar;
import org.snakeyaml.engine.v2.exceptions.ConstructorException;
import org.snakeyaml.engine.v2.nodes.Node;

/** Create instances for numbers (Integer, Long, BigInteger) */
public class ConstructYamlCoreInt extends ConstructScalar {

  private static final int[][] RADIX_MAX = new int[17][2];

  static {
    int[] radixList = new int[] {8, 10, 16};
    for (int radix : radixList) {
      RADIX_MAX[radix] =
          new int[] {maxLen(Integer.MAX_VALUE, radix), maxLen(Long.MAX_VALUE, radix)};
    }
  }

  private static int maxLen(final int max, final int radix) {
    return Integer.toString(max, radix).length();
  }

  private static int maxLen(final long max, final int radix) {
    return Long.toString(max, radix).length();
  }

  protected static Number createLongOrBigInteger(final String number, final int radix) {
    try {
      return Long.valueOf(number, radix);
    } catch (NumberFormatException e1) {
      return new BigInteger(number, radix);
    }
  }

  @Override
  public Object construct(Node node) {
    String value = constructScalar(node);
    if (value.isEmpty()) {
      throw new ConstructorException(
          "while constructing an int",
          node.getStartMark(),
          "found empty value",
          node.getStartMark());
    }
    return createIntNumber(value);
  }

  public Object createIntNumber(String value) {
    int sign = +1;
    char first = value.charAt(0);
    if (first == '-') {
      sign = -1;
      value = value.substring(1);
    } else if (first == '+') {
      value = value.substring(1);
    }
    int base;
    if ("0".equals(value)) {
      return Integer.valueOf(0);
    } else if (value.startsWith("0x")) {
      value = value.substring(2);
      base = 16;
    } else if (value.startsWith("0o")) {
      value = value.substring(2);
      base = 8;
    } else {
      return createNumber(sign, value, 10);
    }
    return createNumber(sign, value, base);
  }

  private Number createNumber(int sign, String number, int radix) {
    final int len = number != null ? number.length() : 0;
    if (sign < 0) {
      number = "-" + number;
    }
    final int[] maxArr = radix < RADIX_MAX.length ? RADIX_MAX[radix] : null;
    if (maxArr != null) {
      final boolean gtInt = len > maxArr[0];
      if (gtInt) {
        if (len > maxArr[1]) {
          return new BigInteger(number, radix);
        }
        return createLongOrBigInteger(number, radix);
      }
    }
    Number result;
    try {
      result = Integer.valueOf(number, radix);
    } catch (NumberFormatException e) {
      result = createLongOrBigInteger(number, radix);
    }
    return result;
  }
}
