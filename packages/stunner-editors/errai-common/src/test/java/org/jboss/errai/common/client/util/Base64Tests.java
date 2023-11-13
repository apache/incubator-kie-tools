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


package org.jboss.errai.common.client.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Tests for the base64 codec. Most of the testing is to ensure that the padding
 * logic works properly, since there are special cases for 0, 1, or 2 characters
 * of padding in the encoded string.
 *
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
public class Base64Tests {

  public void encodeDecodeArray(int length) throws Exception {
    byte[] empty = new byte[length];
    for (int i = 0; i < length; i++) {
      empty[i] = (byte) (i + 1);
    }
    String encoded = Base64Util.encode(empty, 0, length);
    byte[] decoded = Base64Util.decode(encoded);
    assertArrayEquals(empty, decoded);
  }

  @Test
  public void testEncodeEmptyArray() throws Exception {
    encodeDecodeArray(0);
  }

  @Test
  public void testEncodeArrayLength1() throws Exception {
    encodeDecodeArray(1);
  }

  @Test
  public void testEncodeArrayLength2() throws Exception {
    encodeDecodeArray(2);
  }

  @Test
  public void testEncodeArrayLength3() throws Exception {
    encodeDecodeArray(3);
  }

  @Test
  public void testEncodeArrayLength20() throws Exception {
    encodeDecodeArray(20);
  }

  @Test
  public void testEncodeArrayLength21() throws Exception {
    encodeDecodeArray(21);
  }

  @Test
  public void testEncodeArrayLength22() throws Exception {
    encodeDecodeArray(22);
  }

  @Test
  public void testEncodeArrayLength23() throws Exception {
    encodeDecodeArray(23);
  }

  @Test
  public void testEncodeArrayLength24() throws Exception {
    encodeDecodeArray(24);
  }

}
