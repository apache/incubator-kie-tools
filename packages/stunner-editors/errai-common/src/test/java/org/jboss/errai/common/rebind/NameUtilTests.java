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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class NameUtilTests {

  @Test
  public void getHashCharIsValidJavaIdentifierCharForWholeDomain() throws Exception {
    final Map<Integer, Character> invalid = new HashMap<>();
    for (int i = 0; i < 64; i++) {
      final char observed = NameUtil.getHashChar(i);
      if (!Character.isJavaIdentifierPart(observed)) {
        invalid.put(i, observed);
      }
    }

    assertTrue("The following mappings are not valid Java identifier parts: " + invalid, invalid.isEmpty());
  }

  @Test
  public void validHashForLargestUnsignedInt() throws Exception {
    final String observed = NameUtil.getShortHashString(0xFFFFFFFF);
    for (int i = 0; i < observed.length(); i++) {
      assertTrue("Invalid identifier part [" + observed + "].", Character.isJavaIdentifierPart(observed.charAt(i)));
    }
  }

  @Test
  public void validHashForLargestSignedInt() throws Exception {
    final String observed = NameUtil.getShortHashString(Integer.MAX_VALUE);
    for (int i = 0; i < observed.length(); i++) {
      assertTrue("Invalid identifier part [" + observed + "].", Character.isJavaIdentifierPart(observed.charAt(i)));
    }
  }

  @Test
  public void validHashForSmallestSignedInt() throws Exception {
    final String observed = NameUtil.getShortHashString(Integer.MIN_VALUE);
    for (int i = 0; i < observed.length(); i++) {
      assertTrue("Invalid identifier part [" + observed + "].", Character.isJavaIdentifierPart(observed.charAt(i)));
    }
  }

  @Test
  public void validHashForZero() throws Exception {
    final String observed = NameUtil.getShortHashString(0);
    for (int i = 0; i < observed.length(); i++) {
      assertTrue("Invalid identifier part [" + observed + "].", Character.isJavaIdentifierPart(observed.charAt(i)));
    }
  }

}
