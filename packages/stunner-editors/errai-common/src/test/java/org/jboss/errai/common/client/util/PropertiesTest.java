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

import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class PropertiesTest {

  @Test
  public void parsePropertiesWithEquals() {
    final String data = "key1=value1\nkey2=value2";
    final Map<String, String> props = Properties.load(data);

    assertEquals("value1", props.get("key1"));
    assertEquals("value2", props.get("key2"));
    assertEquals(2, props.size());
  }

  @Test
  public void parsePropertiesWithColon() throws Exception {
    final String data = "key1:value1\nkey2:value2";
    final Map<String, String> props = Properties.load(data);

    assertEquals("value1", props.get("key1"));
    assertEquals("value2", props.get("key2"));
    assertEquals(2, props.size());
  }

  @Test
  public void parsePropertiesWithEqualsAndColon() throws Exception {
    final String data = "key1:value1\nkey2=value2";
    final Map<String, String> props = Properties.load(data);

    assertEquals("value1", props.get("key1"));
    assertEquals("value2", props.get("key2"));
    assertEquals(2, props.size());
  }

  @Test
  public void ignoreSpaceBetweenKeyAndValue() throws Exception {
    final String data = "key1  =  value1\nkey2=  value2\nkey3   =value3";
    final Map<String, String> props = Properties.load(data);

    assertEquals("value1", props.get("key1"));
    assertEquals("value2", props.get("key2"));
    assertEquals("value3", props.get("key3"));
    assertEquals(3, props.size());
  }

  @Test
  public void ignoreCommentLines() throws Exception {
    final String data = "key1=value1\n#key2=value2\n!key3=value3";
    final Map<String, String> props = Properties.load(data);

    assertEquals("value1", props.get("key1"));
    assertEquals(1, props.size());
  }

  @Test
  public void parseEmpty() throws Exception {
    final String data = "";
    final Map<String, String> props = Properties.load(data);

    assertEquals(0, props.size());
  }

  @Test
  public void parseWhitespaceOnly() throws Exception {
    final String data = "    \n    ";
    final Map<String, String> props = Properties.load(data);

    assertEquals(0, props.size());
  }

  @Test
  public void includeWhitespaceAfterPropertyValueExceptTerminalNewline() throws Exception {
    final String data = "key1=value1 \t\n";
    final Map<String, String> props = Properties.load(data);

    assertEquals("value1 \t", props.get("key1"));
    assertEquals(1, props.size());
  }

  @Test
  public void ignoreWhitespaceAfterBackslash() throws Exception {
    final String data = "key1=value1\\ \t\n, and value2";
    final Map<String, String> props = Properties.load(data);

    assertEquals("value1, and value2", props.get("key1"));
    assertEquals(1, props.size());
  }

  @Test
  public void valueWithReturnNewlineAndTab() throws Exception {
    final String data = "key1=value1\\t\\r\\n";
    final Map<String, String> props = Properties.load(data);

    assertEquals("value1\t\r\n", props.get("key1"));
    assertEquals(1, props.size());
  }

  @Test
  public void escapeBackslashesBecomeSingleBackslashes() throws Exception {
    final String data = "key1=\\\\value1";
    final Map<String, String> props = Properties.load(data);

    assertEquals("\\value1", props.get("key1"));
    assertEquals(1, props.size());
  }

  @Test
  public void unicodeValues() throws Exception {
    final String data = "key1=value\\u2202";
    final Map<String, String> props = Properties.load(data);

    assertEquals("value\u2202", props.get("key1"));
    assertEquals(1, props.size());
  }

  @Test
  public void lineEndingWithReturn() throws Exception {
    final String data = "key1=value1\rkey2=value2";
    final Map<String, String> props = Properties.load(data);

    assertEquals("value1", props.get("key1"));
    assertEquals("value2", props.get("key2"));
    assertEquals(2, props.size());
  }

  @Test
  public void lineEndingWithReturnAndNewline() throws Exception {
    final String data = "key1=value1\r\nkey2=value2";
    final Map<String, String> props = Properties.load(data);

    assertEquals("value1", props.get("key1"));
    assertEquals("value2", props.get("key2"));
    assertEquals(2, props.size());
  }

  @Test
  public void parseValueEndingWithEscapedEquals() {
    final String data = "key1=value1\\=\nkey2=value2";
    final Map<String, String> props = Properties.load(data);

    assertEquals("value1=", props.get("key1"));
    assertEquals("value2", props.get("key2"));
    assertEquals(2, props.size());
  }

  @Test
  public void parseValueEndingWithEscapedTwoPoints() {
    final String data = "key1=value1\\:\nkey2=value2";
    final Map<String, String> props = Properties.load(data);

    assertEquals("value1:", props.get("key1"));
    assertEquals("value2", props.get("key2"));
    assertEquals(2, props.size());
  }

}
