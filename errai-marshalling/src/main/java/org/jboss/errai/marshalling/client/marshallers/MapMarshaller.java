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

package org.jboss.errai.marshalling.client.marshallers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.ParserFactory;
import org.jboss.errai.marshalling.client.api.annotations.AlwaysQualify;
import org.jboss.errai.marshalling.client.api.annotations.ClientMarshaller;
import org.jboss.errai.marshalling.client.api.annotations.ImplementationAliases;
import org.jboss.errai.marshalling.client.api.annotations.ServerMarshaller;
import org.jboss.errai.marshalling.client.api.json.EJArray;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.util.MarshallUtil;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@ClientMarshaller(Map.class)
@ServerMarshaller(Map.class)
@AlwaysQualify
@ImplementationAliases({AbstractMap.class, HashMap.class})
public class MapMarshaller<T extends Map<Object, Object>> implements Marshaller<T> {
  public static final MapMarshaller INSTANCE = new MapMarshaller();

  @Override
  public T[] getEmptyArray() {
    return (T[]) new HashMap[0];
  }

  @SuppressWarnings("unchecked")
  @Override
  public T demarshall(final EJValue o, final MarshallingSession ctx) {
    return doDemarshall((T) new HashMap(), o, ctx);
  }

  protected T doDemarshall(final T impl, final EJValue o, final MarshallingSession ctx) {
    if (o.isObject() == null)
      return impl;

    Object demarshalledKey;
    final String assumedKeyType = ctx.getAssumedMapKeyType();
    final String assumedValueType = ctx.getAssumedMapValueType();

    // the assumed map k/v types can only be used once since they are not set for nested maps.
    ctx.setAssumedMapKeyType(null);
    ctx.setAssumedMapValueType(null);

    for (final String key : o.isObject().keySet()) {
      final EJValue ejValue = o.isObject().get(key);
      if (key.startsWith(SerializationParts.EMBEDDED_JSON)) {
        final EJValue ejKey = ParserFactory.get().parse(key.substring(SerializationParts.EMBEDDED_JSON.length()));
        demarshalledKey = ctx.getMarshallerInstance(ctx.determineTypeFor(null, ejKey)).demarshall(ejKey, ctx);

        final String valueType = ctx.determineTypeFor(null, ejValue);
        impl.put(demarshalledKey,
            ctx.getMarshallerInstance(valueType).demarshall(ejValue, ctx));
      }
      else {
        if (key.equals(SerializationParts.OBJECT_ID)) {
          continue;
        }
        
        if (assumedKeyType != null && assumedValueType != null) {
          if (key.equals(SerializationParts.NULL_VALUE)) {
            demarshalledKey = null;
          }
          else {
            demarshalledKey = convertKey(assumedKeyType, key, ctx);
          }

          final String valueType;
          if (ejValue.isObject() != null && ejValue.isObject().containsKey(SerializationParts.ENCODED_TYPE)) {
            valueType = ctx.determineTypeFor(null, ejValue);
          }
          else {
            valueType = assumedValueType;
          }
          final Object demarshalledValue = ctx.getMarshallerInstance(valueType).demarshall(ejValue, ctx);
          impl.put(demarshalledKey, demarshalledValue);
        }
        else {
          demarshalledKey = (key.equals(SerializationParts.NULL_VALUE)) ? null : key;
          impl.put(demarshalledKey,
              ctx.getMarshallerInstance(ctx.determineTypeFor(null, ejValue)).demarshall(ejValue, ctx));
        }
      }
    }
    return impl;
  }

  // This only exists to support demarshalling of maps using Jackson. The Jackson payload doesn't contain our
  // EMBEDDED_JSON or any type information, so we have to convert the key (which is always a String) to it's actual
  // type. We only need to support primitive wrapper types and enums as key types. Other types require a custom
  // Key(De)Serializer in Jackson anyway which would be unknown to Errai.
  private Object convertKey(final String toType, final String key, final MarshallingSession ctx) {
    if (key == null) return null;
    
    Marshaller<?> keyMarshaller = ctx.getMarshallerInstance(toType);
    if (toType.equals(Integer.class.getName())) {
      return Integer.parseInt(key);
    }
    else if (toType.equals(Long.class.getName())) {
      return Long.parseLong(key);
    }
    else if (toType.equals(Float.class.getName())) {
      return Float.parseFloat(key);
    }
    else if (toType.equals(Double.class.getName())) {
      return Double.parseDouble(key);
    }
    else if (toType.equals(Short.class.getName())) {
      return Short.parseShort(key);
    }
    else if (toType.equals(Boolean.class.getName())) {
      return Boolean.parseBoolean(key);
    }
    else if (toType.equals(Date.class.getName())) {
      return new Date(Long.parseLong(key));
    }
    else if (toType.equals(Character.class.getName())) {
      return new Character(key.charAt(0));
    }
    else if (toType.equals(String.class.getName())) {
      return key;
    }
    else if (keyMarshaller != null) {
      EJArray eja = ParserFactory.get().parse("[\"" + key + "\"]").isArray();
      if (eja != null && eja.size() == 1) {
        return keyMarshaller.demarshall(eja.get(0), ctx);
      }
    }
    return key;
  }

  @Override
  public String marshall(final T o, final MarshallingSession ctx) {
    final StringBuilder buf = new StringBuilder();
    buf.append("{");
    int i = 0;
    for (final Map.Entry<Object, Object> entry : o.entrySet()) {
      if (i++ > 0) {
        buf.append(",");
      }

      final Marshaller<Object> keyMarshaller;
      final Marshaller<Object> valueMarshaller;
      if (entry.getKey() instanceof String) {
        buf.append("\"").append(entry.getKey()).append("\"");
      }
      else if (entry.getKey() != null) {
        if ((entry.getKey() instanceof Number && !(entry.getKey() instanceof BigInteger || entry.getKey() instanceof BigDecimal))
            || entry.getKey() instanceof Boolean || entry.getKey() instanceof Character) {
          keyMarshaller = MarshallUtil.getQualifiedNumberMarshaller(entry.getKey());
        }
        else {
          keyMarshaller = MarshallUtil.getMarshaller(entry.getKey(), ctx);
        }

        buf.append(("\"" + SerializationParts.EMBEDDED_JSON))
            .append(MarshallUtil.jsonStringEscape(keyMarshaller.marshall(
                MarshallUtil.maybeUnwrap(entry.getKey()), ctx)))
            .append("\"");
      }
      else {
        buf.append("\"" + SerializationParts.NULL_VALUE + "\"");
      }

      buf.append(":");

      if (entry.getValue() == null) {
        buf.append("null");
      }
      else {
        if ((entry.getValue() instanceof Number && !(entry.getValue() instanceof BigInteger || entry.getValue() instanceof BigDecimal))
            || entry.getValue() instanceof Boolean || entry.getValue() instanceof Character) {

          valueMarshaller = MarshallUtil.getQualifiedNumberMarshaller(entry.getValue());
        }
        else {
          valueMarshaller = MarshallUtil.getMarshaller(entry.getValue(), ctx);
        }
        buf.append(valueMarshaller.marshall(MarshallUtil.maybeUnwrap(entry.getValue()), ctx));
      }
    }

    return buf.append("}").toString();
  }
}
