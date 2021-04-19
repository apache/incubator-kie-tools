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

package org.jboss.errai.marshalling.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jboss.errai.marshalling.client.api.json.EJArray;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.server.JSONDecoder;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Direct tests for the (server-side) JSON decoder.
 *
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
public class JSONDecoderTest {

  @Test
  public void testDecodeArrayOfMixedType() throws Exception {
    EJValue ejv = JSONDecoder.decode("{ \"myValue\": [ \"string\", 123.456, {}, [], true, false, null ] }");
    assertTrue(ejv.isObject().containsKey("myValue"));
    EJArray array = ejv.isObject().get("myValue").isArray();
    assertEquals(7, array.size());
    assertEquals("string", array.get(0).isString().stringValue());
    assertEquals(123.456, array.get(1).isNumber().doubleValue(), .00001);
    assertNotNull(array.get(2).isObject());
    assertNotNull(array.get(3).isArray());
    assertTrue(array.get(4).isBoolean().booleanValue());
    assertFalse(array.get(5).isBoolean().booleanValue());
    assertTrue(array.get(6).isNull());
  }

  @Test
  public void testDecodeEmptyArray() throws Exception {
    EJValue ejv = JSONDecoder.decode("[]");
    assertNotNull(ejv.isArray());
    assertEquals(0, ejv.isArray().size());

    // ensure it's not also identifying as any of the other JSON types
    assertNull(ejv.isString());
    assertNull(ejv.isNumber());
    assertNull(ejv.isObject());
    assertNull(ejv.isBoolean());
    assertFalse(ejv.isNull());
  }

  @Test
  public void testDecodeEmptyObject() throws Exception {
    EJValue ejv = JSONDecoder.decode("{}");
    assertNotNull(ejv.isObject());
    assertEquals(0, ejv.isObject().keySet().size());

    // ensure it's not also identifying as any of the other JSON types
    assertNull(ejv.isString());
    assertNull(ejv.isNumber());
    assertNull(ejv.isArray());
    assertNull(ejv.isBoolean());
    assertFalse(ejv.isNull());
  }

  @Test
  public void testDecodeObjectWithTrue() throws Exception {
    EJValue ejv = JSONDecoder.decode("{ \"myValue\": true }");
    assertTrue(ejv.isObject().containsKey("myValue"));
    EJValue myValue = ejv.isObject().get("myValue");
    assertTrue(myValue.isBoolean().booleanValue());

    // ensure it's not also identifying as any of the other JSON types
    assertNull(myValue.isString());
    assertNull(myValue.isNumber());
    assertNull(myValue.isObject());
    assertNull(myValue.isArray());
    assertFalse(myValue.isNull());
  }

  @Test
  public void testDecodeObjectWithFalse() throws Exception {
    EJValue ejv = JSONDecoder.decode("{ \"myValue\": false }");
    assertTrue(ejv.isObject().containsKey("myValue"));
    EJValue myValue = ejv.isObject().get("myValue");
    assertFalse(myValue.isBoolean().booleanValue());

    // ensure it's not also identifying as any of the other JSON types
    assertNull(myValue.isString());
    assertNull(myValue.isNumber());
    assertNull(myValue.isObject());
    assertNull(myValue.isArray());
    assertFalse(myValue.isNull());
  }

  @Test
  public void testDecodeObjectWithNull() throws Exception {
    EJValue ejv = JSONDecoder.decode("{ \"myValue\": null }");
    assertTrue(ejv.isObject().containsKey("myValue"));
    EJValue myValue = ejv.isObject().get("myValue");
    assertTrue(myValue.isNull());

    // ensure it's not also identifying as any of the other JSON types
    assertNull(myValue.isString());
    assertNull(myValue.isNumber());
    assertNull(myValue.isObject());
    assertNull(myValue.isArray());
    assertNull(myValue.isBoolean());
  }

  @Test
  public void testDecodeObjectWithNumber() throws Exception {
    EJValue ejv = JSONDecoder.decode("{ \"myValue\": 123456 }");
    assertTrue(ejv.isObject().containsKey("myValue"));
    EJValue myValue = ejv.isObject().get("myValue");
    assertEquals(123456, myValue.isNumber().intValue());

    // ensure it's not also identifying as any of the other JSON types
    assertNull(myValue.isString());
    assertNull(myValue.isObject());
    assertNull(myValue.isArray());
    assertNull(myValue.isBoolean());
    assertFalse(myValue.isNull());
  }

  @Test
  public void testDecodeObjectWithNegativeNumber() throws Exception {
    EJValue ejv = JSONDecoder.decode("{ \"myValue\": -123456 }");
    assertTrue(ejv.isObject().containsKey("myValue"));
    EJValue myValue = ejv.isObject().get("myValue");
    assertEquals(-123456, myValue.isNumber().intValue());

    // ensure it's not also identifying as any of the other JSON types
    assertNull(myValue.isString());
    assertNull(myValue.isObject());
    assertNull(myValue.isArray());
    assertNull(myValue.isBoolean());
    assertFalse(myValue.isNull());
  }

  @Test
  public void testDecodeObjectWithScientificNumbers() throws Exception {
    EJValue ejv = JSONDecoder.decode("{" +
            " \"myValue0\": 1.234e56" +
            ", \"myValue1\": 1.234e+56" +
            ", \"myValue2\": 1.234e-56" +
            ", \"myValue3\": 1.234E56" +
            ", \"myValue4\": 1.234E+56" +
            ", \"myValue5\": 1.234E-56" +
            ", \"myValue6\": 1234E-59" +
    		"}");
    for (int i = 0; i < 6; i++) {
      assertTrue(ejv.isObject().containsKey("myValue" + i));
      EJValue myValue = ejv.isObject().get("myValue" + i);
      switch (i) {
      case 2:
      case 5:
        assertEquals("Failed on myValue" + i, 1.234e-56, myValue.isNumber().doubleValue(), 0.0);
        break;
      default:
        assertEquals("Failed on myValue" + i, 1.234e56, myValue.isNumber().doubleValue(), 0.0);
        break;
      }
    }
  }

  /**
   * JSON numbers can't have a leading 0. This test is presently ignored because
   * the logic for detecting a leading 0 is nontrivial (that is, it may impact
   * performance). We can re-enable this test if leading 0 detection becomes
   * important.
   */
  @Test @Ignore
  public void testDecodeObjectWithInvalidNumberLeading0() throws Exception {
    try {
      JSONDecoder.decode("{ \"myValue\": 0123456 }");
      fail("Expected NumberFormatException");
    } catch (Exception e) {
      assertEquals(NumberFormatException.class, findRootCause(e).getClass());
    }
  }

  /**
   * JSON numbers can't have a leading dot.
   */
  @Test
  public void testDecodeObjectWithInvalidNumberLeadingDot() throws Exception {
    try {
      JSONDecoder.decode("{ \"myValue\": .123456 }");
      fail("Expected NumberFormatException");
    } catch (Exception e) {
      assertEquals(NumberFormatException.class, findRootCause(e).getClass());
    }
  }

  /**
   * JSON numbers can't have a trailing e.
   */
  @Test
  public void testDecodeObjectWithInvalidNumberTrailingE() throws Exception {
    try {
      JSONDecoder.decode("{ \"myValue\": 123456e }");
      fail("Expected NumberFormatException");
    } catch (Exception e) {
      assertEquals(NumberFormatException.class, findRootCause(e).getClass());
    }
  }

  @Test
  public void testDecodeObjectWithString() throws Exception {
    EJValue ejv = JSONDecoder.decode("{ \"myValue\": \"myString\" }");
    assertTrue(ejv.isObject().containsKey("myValue"));
    EJValue myValue = ejv.isObject().get("myValue");
    assertEquals("myString", myValue.isString().stringValue());

    // ensure it's not also identifying as any of the other JSON types
    assertNull(myValue.isNumber());
    assertNull(myValue.isObject());
    assertNull(myValue.isArray());
    assertNull(myValue.isBoolean());
    assertFalse(myValue.isNull());
  }

  @Test
  public void testDecodeObjectWithEmptyString() throws Exception {
    EJValue ejv = JSONDecoder.decode("{ \"myValue\": \"\" }");
    assertTrue(ejv.isObject().containsKey("myValue"));
    EJValue myValue = ejv.isObject().get("myValue");
    assertEquals("", myValue.isString().stringValue());

    // ensure it's not also identifying as any of the other JSON types
    assertNull(myValue.isNumber());
    assertNull(myValue.isObject());
    assertNull(myValue.isArray());
    assertNull(myValue.isBoolean());
    assertFalse(myValue.isNull());
  }

  @Test
  public void testDecodeObjectWithNonAsciiString() throws Exception {
    EJValue ejv = JSONDecoder.decode("{ \"myValue\": \"myStr\u00efng\" }");
    assertTrue(ejv.isObject().containsKey("myValue"));
    EJValue myValue = ejv.isObject().get("myValue");
    assertEquals("myStr\u00efng", myValue.isString().stringValue());

    // ensure it's not also identifying as any of the other JSON types
    assertNull(myValue.isNumber());
    assertNull(myValue.isObject());
    assertNull(myValue.isArray());
    assertNull(myValue.isBoolean());
    assertFalse(myValue.isNull());
  }

  private static Throwable findRootCause(Throwable e) {
    while (e.getCause() != null && e.getCause() != e) {
      e = e.getCause();
    }
    return e;
  }
}
