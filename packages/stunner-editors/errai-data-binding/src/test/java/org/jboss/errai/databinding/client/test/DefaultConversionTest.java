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


package org.jboss.errai.databinding.client.test;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.jboss.errai.databinding.client.api.Convert;
import org.junit.Test;

import static org.jboss.errai.databinding.client.api.Convert.getConverter;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the built-in default conversions in {@link Convert}.
 *
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 * @autor Max Barkley <mbarkley@redhat.com>
 */
public class DefaultConversionTest {

  @Test
  public void testStringToString() throws Exception {
    Object result = getConverter(String.class, String.class).toWidgetValue("test");
    assertEquals("test", result);
  }

  @Test
  public void testNullStringBecomesEmpty() throws Exception {
    Object result = getConverter(Integer.class, String.class).toWidgetValue(null);
    assertEquals("", result);
  }

  @Test
  public void testStringToInt() throws Exception {
    Object result = getConverter(String.class, Integer.class).toWidgetValue("1234");
    assertEquals(Integer.valueOf(1234), result);
  }

  @Test
  public void testIntToString() throws Exception {
    Object result = getConverter(Integer.class, String.class).toWidgetValue(1234);
    assertEquals("1234", result);
  }

  @Test
  public void testStringToLong() throws Exception {
    Object result = getConverter(String.class, Long.class).toWidgetValue("1234");
    assertEquals(Long.valueOf(1234), result);
  }

  @Test
  public void testLongToString() throws Exception {
    Object result = getConverter(Long.class, String.class).toWidgetValue(1234L);
    assertEquals("1234", result);
  }

  @Test
  public void testStringToFloat() throws Exception {
    Object result = getConverter(String.class, Float.class).toWidgetValue("1234");
    assertEquals(Float.valueOf(1234), result);
  }

  @Test
  public void testFloatToString() throws Exception {
    Object result = getConverter(Float.class, String.class).toWidgetValue(1234.5f);
    assertEquals("1234.5", result);
  }

  @Test
  public void testStringToDouble() throws Exception {
    Object result = getConverter(String.class, Double.class).toWidgetValue("1234");
    assertEquals(Double.valueOf(1234), result);
  }

  @Test
  public void testDoubleToString() throws Exception {
    Object result = getConverter(Double.class, String.class).toWidgetValue(1234.5);
    assertEquals("1234.5", result);
  }

  @Test
  public void testStringToBooleanTrue() throws Exception {
    Object result = getConverter(String.class, Boolean.class).toWidgetValue("true");
    assertEquals(Boolean.TRUE, result);
  }

  @Test
  public void testBooleanTrueToString() throws Exception {
    Object result = getConverter(Boolean.class, String.class).toWidgetValue(true);
    assertEquals("true", result);
  }

  @Test
  public void testStringToBooleanFalse() throws Exception {
    Object result = getConverter(String.class, Boolean.class).toWidgetValue("false");
    assertEquals(Boolean.FALSE, result);
  }

  @Test
  public void testBooleanFalseToString() throws Exception {
    Object result = getConverter(Boolean.class, String.class).toWidgetValue(false);
    assertEquals("false", result);
  }

  @Test
  public void testBigDecimalToString() throws Exception {
    BigDecimal bd = new BigDecimal(System.currentTimeMillis());
    Object result = getConverter(BigDecimal.class, String.class).toWidgetValue(bd);
    assertEquals(bd.toString(), result);
  }

  @Test
  public void testStringToBigDecimal() throws Exception {
    BigDecimal bd = new BigDecimal(System.currentTimeMillis());
    Object result = getConverter(String.class, BigDecimal.class).toWidgetValue(bd.toString());
    assertEquals(bd, result);
  }

  @Test
  public void testBigIntegerToString() throws Exception {
    BigInteger bn = new BigInteger(String.valueOf(System.currentTimeMillis()));
    Object result = getConverter(BigInteger.class, String.class).toWidgetValue(bn);
    assertEquals(bn.toString(), result);
  }

  @Test
  public void testStringToBigInteger() throws Exception {
    BigInteger bn = new BigInteger(String.valueOf(System.currentTimeMillis()));
    Object result = getConverter(String.class, BigInteger.class).toWidgetValue(bn.toString());
    assertEquals(bn, result);
  }

}
