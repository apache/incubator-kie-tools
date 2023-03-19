/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.common.client.util;


/**
 * Base64 String to-and-from byte array utility.
 * <p/>
 * This code is a modified version of Base64Encoder from <a href=
 * "http://code.google.com/p/gwt-crypto/source/browse/trunk/src/main/java/com/googlecode/gwt/crypto/bouncycastle/util/encoders/Base64.java?r=50"
 * >the gwt-crypto project</a>, used under the terms of the ASL 2.0 license.
 *
 * @author gwt-crypto project (original encode/decode logic)
 * @author Jonathan Fuerth <jfuerth@gmail.com> (modified for Errai by removing
 *         usage of java.io)
 */
public class Base64Util {

  private static final byte[] encodingTable = {(byte) 'A', (byte) 'B', (byte) 'C',
      (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I',
      (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O',
      (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
      (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a',
      (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
      (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm',
      (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's',
      (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y',
      (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
      (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+',
      (byte) '/'};


  protected static final byte padding = (byte) '=';

  protected static final byte[] decodingTable = new byte[128];

  static {
    for (int i = 0; i < encodingTable.length; i++) {
      decodingTable[encodingTable[i]] = (byte) i;
    }
  }

  /**
   * Same as {@link #encode(byte[], int, int)} but accepts an array of boxed
   * Byte values.
   *
   * @param data
   *     The sequence of bytes to encode. Every element must be non-null.
   * @param off
   *     The offset into the data array to start encoding from
   * @param length
   *     The number of bytes to encode, starting from {@code off}
   *
   * @return The base64 encoded data.
   */
  public static String encode(final Byte[] data, final int off, final int length) {
    final byte[] primitiveData = new byte[data.length];
    for (int i = 0; i < length; i++) {
      primitiveData[i] = data[off + i];
    }
    return encode(primitiveData, 0, primitiveData.length);
  }

  /**
   * Encodes the input data, producing a base 64 string.
   *
   * @param data
   *     the data to encode. It is assumed that none of the elements are
   *     null.
   * @param off
   *     The offset into the data array to start encoding from
   * @param length
   *     The number of bytes to encode, starting from {@code off}
   *
   * @return the base64 representation of the given binary data.
   */
  public static String encode(final byte[] data, final int off, final int length) {
    final StringBuilder sb = new StringBuilder();
    final int modulus = length % 3;
    final int dataLength = (length - modulus);
    int a1, a2, a3;

    for (int i = off; i < off + dataLength; i += 3) {
      a1 = data[i] & 0xff;
      a2 = data[i + 1] & 0xff;
      a3 = data[i + 2] & 0xff;

      sb.append((char) encodingTable[(a1 >>> 2) & 0x3f]);
      sb.append((char) encodingTable[((a1 << 4) | (a2 >>> 4)) & 0x3f]);
      sb.append((char) encodingTable[((a2 << 2) | (a3 >>> 6)) & 0x3f]);
      sb.append((char) encodingTable[a3 & 0x3f]);
    }

    /*
     * process the tail end.
     */
    final int d1;
    final int d2;

    switch (modulus) {
      case 0: /* nothing left to do */
        break;
      case 1:
        d1 = data[off + dataLength] & 0xff;

        sb.append((char) encodingTable[(d1 >>> 2) & 0x3f]);
        sb.append((char) encodingTable[(d1 << 4) & 0x3f]);
        sb.append((char) padding);
        sb.append((char) padding);
        break;
      case 2:
        d1 = data[off + dataLength] & 0xff;
        d2 = data[off + dataLength + 1] & 0xff;

        sb.append((char) encodingTable[(d1 >>> 2) & 0x3f]);
        sb.append((char) encodingTable[((d1 << 4) | (d2 >>> 4)) & 0x3f]);
        sb.append((char) encodingTable[(d2 << 2) & 0x3f]);
        sb.append((char) padding);
        break;
    }

    return sb.toString();
  }

  private static boolean ignore(final char c) {
    return (c == '\n' || c == '\r' || c == '\t' || c == ' ');
  }

  public static Byte[] decodeAsBoxed(final String data) {
    final byte[] primitiveArray = decode(data);
    final Byte[] boxedData = new Byte[primitiveArray.length];
    for (int i = 0; i < primitiveArray.length; i++) {
      boxedData[i] = primitiveArray[i];
    }
    return boxedData;
  }

  /**
   * Decodes the Base64-encoded String to the equivalent binary data array.
   * Whitespace characters in the input stream are ignored.
   *
   * @return the binary data decoded from the input string
   */
  public static byte[] decode(final String data) {
    byte b2, b3;

    if (data.isEmpty()) {
      return new byte[0];
    }

    int end = data.length();

    while (end > 0) {
      if (!ignore(data.charAt(end - 1))) {
        break;
      }

      end--;
    }

    final byte[] lastBlock = new byte[3];
    final int bytesInLastBlock = decodeLastBlock(
        lastBlock, 0,
        data.charAt(end - 4), data.charAt(end - 3),
        data.charAt(end - 2), data.charAt(end - 1));

    final byte[] out = new byte[data.length() / 4 * 3 - (3 - bytesInLastBlock)];
    int outpos = 0;

    int i = 0;
    final int finish = end - 4;

    while (i < finish) {
      out[outpos++] = (byte) ((decodingTable[data.charAt(i = nextI(data, i, finish))] << 2) | ((b2 = decodingTable[data.charAt(i = nextI(data, ++i, finish))]) >> 4));
      out[outpos++] = (byte) ((b2 << 4) | ((b3 = decodingTable[data.charAt(i = nextI(data, ++i, finish))]) >> 2));
      out[outpos++] = (byte) ((b3 << 6) | decodingTable[data.charAt(i = nextI(data, ++i, finish))]);

      i = nextI(data, i, finish);
      i++;
    }

    for (int j = 0; j < bytesInLastBlock; j++) {
      out[outpos++] = lastBlock[j];
    }

    return out;
  }

  private static int decodeLastBlock(
      final byte[] out, int outpos, final char c1, final char c2, final char c3, final char c4) {

    if (c3 == padding) {
      out[outpos] = (byte) ((decodingTable[c1] << 2) | (decodingTable[c2] >> 4));
      return 1;
    }
    else if (c4 == padding) {
      out[outpos++] = (byte) ((decodingTable[c1] << 2) | (decodingTable[c2] >> 4));
      out[outpos] = (byte) ((decodingTable[c2] << 4) | (decodingTable[c3] >> 2));

      return 2;
    }
    else {
      out[outpos++] = (byte) ((decodingTable[c1] << 2) | (decodingTable[c2] >> 4));
      out[outpos++] = (byte) ((decodingTable[c2] << 4) | (decodingTable[c3] >> 2));
      out[outpos] = (byte) ((decodingTable[c3] << 6) | decodingTable[c4]);

      return 3;
    }
  }

  private static int nextI(final String data, int i, final int finish) {
    while ((i < finish) && ignore(data.charAt(i))) {
      i++;
    }
    return i;
  }
}
