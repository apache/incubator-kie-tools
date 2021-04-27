/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.marshalling.client.util;

import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class NumbersUtils {
  public static Object getEncodedNumber(final EJObject object) {
    return getNumber(object.get(SerializationParts.ENCODED_TYPE).isString().stringValue(),
            object.get(SerializationParts.NUMERIC_VALUE));
  }

  public static Object getNumber(final String wrapperClassName, final EJValue value) {
    if (Integer.class.getName().equals(wrapperClassName)) {
      return new Double(value.isNumber().doubleValue()).intValue();
    }
    else if (Double.class.getName().equals(wrapperClassName)) {
      if (value.isString() != null) {
        return Double.parseDouble(value.isString().stringValue());
      }
      return value.isNumber().doubleValue();
      
    }
    else if (Long.class.getName().equals(wrapperClassName)) {
      return Long.parseLong(value.isString().stringValue());
    }
    else if (Boolean.class.getName().equals(wrapperClassName)) {
      return value.isBoolean().booleanValue();
    }
    else if (Float.class.getName().equals(wrapperClassName)) {
      if (value.isString() != null) {
        return Float.parseFloat(value.isString().stringValue());
      }
      return new Double(value.isNumber().doubleValue()).floatValue();
    }
    else if (Short.class.getName().equals(wrapperClassName)) {
      return new Double(value.isNumber().doubleValue()).shortValue();
    }
    else if (Character.class.getName().equals(wrapperClassName)) {
      return value.isString().stringValue().charAt(0);
    }
    else if (Byte.class.getName().equals(wrapperClassName)) {
      return new Double(value.isNumber().doubleValue()).byteValue();
    }
    else {
      throw new RuntimeException("unknown type");
    }
  }

  private final static String quote = "\"";

  public static String qualifiedNumericEncoding(final Object o) {
    final String typeName;

    if (o == null) {
      typeName = Object.class.getName();
    }
    else if (o instanceof String) {
      typeName = Character.class.getName();
    }
    else {
      typeName = o.getClass().getName();
    }

    return "{" + quote + SerializationParts.ENCODED_TYPE + quote + ":"
            + quote + typeName + quote + ", "
            + quote + SerializationParts.OBJECT_ID + quote + ": " + quote + "-1" + quote + "," +
            quote + SerializationParts.NUMERIC_VALUE + quote + ":"
            + getNumericValue(o) + "}";
  }

  private static String getNumericValue(final Object o) {
    String val;
    if (o instanceof Long || o instanceof Character ||
        (o instanceof Double && (((Double) o).isNaN() || ((Double) o).isInfinite())) ||
        (o instanceof Float && (((Float) o).isNaN() || ((Float) o).isInfinite()))) {
      val = quote + String.valueOf(o) + quote;
    }
    else {
      val = String.valueOf(o);
    }

    return val;
  }
}
