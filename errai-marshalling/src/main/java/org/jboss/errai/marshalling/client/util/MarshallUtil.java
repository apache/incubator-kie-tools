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

import org.jboss.errai.common.client.api.WrappedPortable;
import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.marshallers.FallbackExceptionMarshaller;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class MarshallUtil {
  public static Marshaller<Object> getQualifiedNumberMarshaller(final Object o) {
    final Class<Object> type = (Class<Object>) o.getClass();

    return new Marshaller<Object>() {

      @Override
      public String marshall(final Object o, final MarshallingSession ctx) {
        return NumbersUtils.qualifiedNumericEncoding(o);
      }

      @Override
      public Object demarshall(final EJValue o, final MarshallingSession ctx) {
        return null;
      }

      @Override
      public Object[] getEmptyArray() {
        throw new UnsupportedOperationException("Not implemented!");
      }
    };
  }

  public static String jsonStringEscape(final String s) {
    final StringBuilder sb = new StringBuilder(s.length());
    for (int i = 0; i < s.length(); i++) {
      jsonStringEscape(sb, s.charAt(i));
    }
    return sb.toString();
  }

  public static String jsonStringEscape(final char ch) {
    final StringBuilder sb = new StringBuilder(5);
    jsonStringEscape(sb, ch);
    return sb.toString();
  }

  public static void jsonStringEscape(final StringBuilder sb, final char ch) {
    switch (ch) {
    case '"':
      sb.append("\\\"");
      break;
    case '\\':
      sb.append("\\\\");
      break;
    case '\b':
      sb.append("\\b");
      break;
    case '\f':
      sb.append("\\f");
      break;
    case '\n':
      sb.append("\\n");
      break;
    case '\r':
      sb.append("\\r");
      break;
    case '\t':
      sb.append("\\t");
      break;
    case '/':
      sb.append("\\/");
      break;
    default:
      if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F')
                || (ch >= '\u2000')) {

        final String ss = Integer.toHexString(ch);
        sb.append("\\u");
        for (int k = 0; k < 4 - ss.length(); k++) {
          sb.append('0');
        }
        sb.append(ss.toUpperCase());
      }
      else {
        sb.append(ch);
      }
    }
  }

  public static Marshaller<Object> getMarshaller(Object obj, final MarshallingSession session) {
    String className;
    if (obj instanceof Enum<?>) {
      className = ((Enum<?>) obj).getDeclaringClass().getName();
    }
    else {
      className = obj.getClass().getName();
    }

    Marshaller<Object> m = session.getMarshallerInstance(className);
    if (m == null && obj instanceof WrappedPortable) {
      className = ((WrappedPortable) obj).unwrap().getClass().getName();
      m = session.getMarshallerInstance(className);
    }
    if (m == null && obj instanceof Throwable) {
      m = new FallbackExceptionMarshaller();
    }
    // Attempt to marshal weld/hibernate javassist created objects.
    if (m == null && (obj.getClass().getName().contains("$HibernateProxy") || obj.getClass().getName()
            .contains("_$$_javassist_") || obj.getClass().getName().contains("_$$_jvst"))) {

      className = obj.getClass().getSuperclass().getName();
      m = session.getMarshallerInstance(className);
    }
    if (m == null) {
      throw new RuntimeException("no marshalling definition available for type:" + className);
    }
    return m;
  }

  public static boolean isEncodedObject(final EJObject value) {
    return value.containsKey(SerializationParts.OBJECT_ID);
  }

  public static boolean isEncodedNumeric(final EJObject value) {
    return value.containsKey(SerializationParts.NUMERIC_VALUE);
  }

  /**
   * Returns the canonical class name of the component type of the given array type.
   *
   * @param fqcn
   *          An array type of any number of dimensions, such as {@code [[Ljava.lang.String;}.
   * @return A class name, such as {@code java.lang.String}.
   */
  public static String getComponentClassName(String fqcn) {

    int dims = 0;
    if (fqcn.startsWith("[") && fqcn.endsWith(";")) {
      while (fqcn.length() > 0 && fqcn.charAt(0) == '[') {
        fqcn = fqcn.substring(1);
        dims++;
      }

      // unfortunately, array types are stored in the map using internal JVM names
      // like "[Ljava.lang.Object;" but scalar types are stored under their regular
      // fully-qualified name like "java.lang.Object". We have to strip off the L and ;.
      if (dims > 0) {
        fqcn = fqcn.substring(1, fqcn.length() - 1);
      }
    }

    return fqcn;
  }
  
  public static Object maybeUnwrap(Object obj) {
    if (obj instanceof WrappedPortable) {
      obj = ((WrappedPortable) obj).unwrap();
    }
    return obj;
  }
}
