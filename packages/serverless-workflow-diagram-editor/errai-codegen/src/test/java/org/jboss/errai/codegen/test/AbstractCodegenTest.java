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


package org.jboss.errai.codegen.test;

import org.jboss.errai.codegen.builder.impl.StatementBuilder;
import org.jboss.errai.codegen.util.GenUtil;
import org.junit.Before;

/**
 * Base class for all {@link StatementBuilder} tests.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public abstract class AbstractCodegenTest {

  @Before
  public void onBefore() {
    GenUtil.setPermissiveMode(false);
  }

  /**
   * assertEquals, less sensitive to indentation differences.
   * TODO compare parsed ASTs instead?
   *
   * @param expected
   * @param actual
   */
  protected static void assertEquals(String expected, String actual) {
    org.junit.Assert.assertEquals(expected.replaceAll("\\s+", " ").trim(),
        actual.replaceAll("\\s+", " ").trim());
  }

  protected static void assertEquals(String message, String expected, String actual) {
    org.junit.Assert.assertEquals(message, expected == null ? null : expected.replaceAll("\\s+", " ").trim(),
        actual == null ? null : actual.replaceAll("\\s+", " ").trim());
  }
}
