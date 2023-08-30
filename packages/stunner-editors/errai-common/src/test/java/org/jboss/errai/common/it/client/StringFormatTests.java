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


package org.jboss.errai.common.it.client;

import java.util.Date;

import com.google.gwt.junit.client.GWTTestCase;
import org.jboss.errai.common.client.logging.util.StringFormat;

/**
 * Tests for the {@link StringFormat} class.
 *
 * Most of these tests verify behaviour identical to
 * {@link String#format(String, Object...)}, but in some cases the output
 * differs slightly for the convenience of using pre-existing GWT
 * implementations.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@SuppressWarnings("deprecation")
public class StringFormatTests extends GWTTestCase {

  public void testScientificNotationLower() throws Exception {
    assertEquals("3.141593e00", StringFormat.format("%e", Math.PI));
  }

  public void testScientificNotationUpper() throws Exception {
    assertEquals("3.141593E00", StringFormat.format("%E", Math.PI));
  }

  public void testScientificNotationPrecision() throws Exception {
    assertEquals("3.14E00", StringFormat.format("%.2E", Math.PI));
  }

  public void testGConversionLower() throws Exception {
    assertEquals(StringFormat.format("%e", Math.PI), StringFormat.format("%g", Math.PI));
  }

  public void testGConversionUpper() throws Exception {
    assertEquals(StringFormat.format("%E", Math.PI), StringFormat.format("%G", Math.PI));
  }

  // public void testHexScientificNotationLower() throws Exception {
  // assertEquals("0x1.921fb54442d18p1", StringFormat.format("%a", Math.PI));
  // }
  //
  // public void testHexScientificNotationUpper() throws Exception {
  // assertEquals("0x1.921FB54442D18P1", StringFormat.format("%A", Math.PI));
  // }

  public void testStringLower() throws Exception {
    assertEquals("value", StringFormat.format("%s", "value"));
  }

  public void testStringUpper() throws Exception {
    assertEquals("VALUE", StringFormat.format("%S", "value"));
  }

  public void testStringPrecision() throws Exception {
    final String val = "123456789";
    assertEquals(val.substring(0, 3), StringFormat.format("%.3s", val));
  }

  public void testStringWidth() throws Exception {
    final String val = "123456789";
    assertEquals(" " + val, StringFormat.format("%10s", val));
  }

  public void testStringNull() throws Exception {
    assertEquals("null", StringFormat.format("%s", null));
  }

  public void testInt() throws Exception {
    assertEquals("1337", StringFormat.format("%d", 1337));
  }

  public void testBooleanNonNull() throws Exception {
    assertEquals("true", StringFormat.format("%b", new Object()));
  }

  public void testBooleanUpper() throws Exception {
    assertEquals("TRUE", StringFormat.format("%B", new Object()));
  }

  public void testBooleanNullUpper() throws Exception {
    assertEquals("false", StringFormat.format("%b", null));
  }

  public void testBooleanTrue() throws Exception {
    assertEquals("true", StringFormat.format("%b", true));
  }

  public void testBooleanFalse() throws Exception {
    assertEquals("false", StringFormat.format("%b", false));
  }

  public void testHexStringLower() throws Exception {
    final Object obj = new Object() {
      @Override
      public int hashCode() {
        return 0xabcdef;
      }
    };
    assertEquals(Integer.toHexString(obj.hashCode()), StringFormat.format("%h", obj));
  }

  public void testHexStringUpper() throws Exception {
    final Object obj = new Object() {
      @Override
      public int hashCode() {
        return 0xabcdef;
      }
    };
    assertEquals(Integer.toHexString(obj.hashCode()).toUpperCase(), StringFormat.format("%H", obj));
  }

  public void testHexStringNull() throws Exception {
    assertEquals("null", StringFormat.format("%h", null));
  }

  public void testUnicodeCharacterLower() throws Exception {
    assertEquals("c", StringFormat.format("%c", (int) 'c'));
  }

  public void testUnicodeCharacterUpper() throws Exception {
    assertEquals("C", StringFormat.format("%C", (int) 'c'));
  }

  public void testOctal() throws Exception {
    assertEquals(Integer.toOctalString(735), StringFormat.format("%o", 735));
  }

  public void testHexIntLower() throws Exception {
    assertEquals(Integer.toHexString(725815), StringFormat.format("%x", 725815));
  }

  public void testHexIntUpper() throws Exception {
    assertEquals(Integer.toHexString(725815).toUpperCase(), StringFormat.format("%X", 725815));
  }

  /*
   * Currently StringFormat.format("%f", 1.0) returns "1"
   */
  public void ignoreFloatingPoint() throws Exception {
    assertEquals(String.valueOf(1.0), StringFormat.format("%f", 1.0));
  }

  public void testLiteralPercent() throws Exception {
    assertEquals("%", StringFormat.format("%%"));
  }

  public void testNewline() throws Exception {
    assertEquals("\n", StringFormat.format("%n"));
  }

  /*
   * START DISABLED DATE TESTS
   *
   * These Date tests are ignored because they break outside of the EST/EDT timezone.
   * Unfortunately setting the locale in the GWT module does not seem to work.
   */
  public void ignoreLongDate() throws Exception {
    final long time = 1000000000;
    assertEquals("08:46", StringFormat.format("%tR", time));
  }

  public void ignoreDate() throws Exception {
    final Date date = new Date(1000000000);
    assertEquals("08:46", StringFormat.format("%tR", date));
  }

  public void ignoreDateUpperT() throws Exception {
    final Date date = new Date(1000000000);
    assertEquals("08:46:40", StringFormat.format("%tT", date));
  }

  public void ignoreDateLowerR() throws Exception {
    final Date date = new Date(1000000000);
    assertEquals("08:46:40 AM", StringFormat.format("%tr", date));
  }

  public void ignoreDateUpperD() throws Exception {
    final Date date = new Date(1000000000);
    assertEquals("01/12/70", StringFormat.format("%tD", date));
  }

  public void ignoreDateUpperF() throws Exception {
    final Date date = new Date(1000000000);
    assertEquals("1970-01-12", StringFormat.format("%tF", date));
  }

  public void ignoreDateLowerC() throws Exception {
    final Date date = new Date(1000000000);
    assertEquals("Mon Jan 12 08:46:40 UTC-5 1970", StringFormat.format("%tc", date));
  }

  public void ignoreDateLowerK() throws Exception {
    final Date date = new Date(1000000000);
    date.setHours(13);
    assertEquals("13", StringFormat.format("%tk", date));
  }

  public void ignoreDateLowerL() throws Exception {
    final Date date = new Date(1000000000);
    assertEquals("8", StringFormat.format("%tl", date));
  }

  public void ignoreDateUpperL() throws Exception {
    final Date date = new Date(10000000123L);
    assertEquals(String.valueOf(123), StringFormat.format("%tL", date));
  }

  public void ignoreDateUpperN() throws Exception {
    final Date date = new Date(1000000123L);
    assertEquals("123000000", StringFormat.format("%tN", date));
  }

  public void ignoreDateLowerZ() throws Exception {
    final Date date = new Date(1000000000);
    assertEquals("-0500", StringFormat.format("%tz", date));
  }

  public void ignoreDateLowerS() throws Exception {
    final Date date = new Date(1000000000);
    assertEquals(String.valueOf(1000000), StringFormat.format("%ts", date));
  }

  public void ignoreDateUpperQ() throws Exception {
    final Date date = new Date(1000000000);
    assertEquals(String.valueOf(1000000000), StringFormat.format("%tQ", date));
  }

  public void ignoreDateUpperB() throws Exception {
    final Date date = new Date(1000000000);
    assertEquals("January", StringFormat.format("%tB", date));
  }

  public void ignoreDateUpperA() throws Exception {
    final Date date = new Date(1000000000);
    assertEquals("Monday", StringFormat.format("%tA", date));
  }

  public void ignoreDateUpperC() throws Exception {
    final Date date = new Date(1000000000);
    assertEquals("19", StringFormat.format("%tC", date));
  }

  public void ignoreDateLowerJ() throws Exception {
    final Date date = new Date(1000000000);
    assertEquals("012", StringFormat.format("%tj", date));
  }

  public void ignoreDateLowerE() throws Exception {
    final Date date = new Date(1000000000);
    date.setDate(9);
    assertEquals("9", StringFormat.format("%te", date));
  }
  /*
   * END DISABLED DATE TESTS
   */

  public void testMultipleConversions() throws Exception {
    final String val1 = "happy";
    final String val2 = "rainbows";
    final String val3 = "bananas";

    assertEquals(val1 + " " + val2 + " " + val3, StringFormat.format("%s %s %s", val1, val2, val3));
  }

  public void testIndexedConversions() throws Exception {
    final String val1 = "happy";
    final String val2 = "rainbows";
    final String val3 = "bananas";

    assertEquals(val1 + " " + val2 + " " + val3, StringFormat.format("%3$s %1$s %2$s", val2, val3, val1));
  }

  public void testIndexedAndNonIndexedConversions() throws Exception {
    final String val1 = "happy";
    final String val2 = "rainbows";
    final String val3 = "bananas";

    assertEquals(val3 + " " + val1 + " " + val2 + " " + val1, StringFormat.format("%3$s %s %s %1$s", val1, val2, val3));
  }

  public void testInvalidFlagsThrowsException() throws Exception {
    // Currently no flags are supported, so check that an exception is thrown
    // when any is given
    try {
      StringFormat.format("%-s", "test");
      fail("An exception should be thrown.");
    }
    catch (final Exception e) {
    }
  }

  @Override
  public String getModuleName() {
    return "org.jboss.errai.common.it.CommonTests";
  }

}
