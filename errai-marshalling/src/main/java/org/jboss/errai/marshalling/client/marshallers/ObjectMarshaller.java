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

import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.annotations.ClientMarshaller;
import org.jboss.errai.marshalling.client.api.annotations.ServerMarshaller;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.util.MarshallUtil;
import org.jboss.errai.marshalling.client.util.NumbersUtils;

import java.util.List;

/**
 * This class is used to handle untyped Objects on the wire.
 *
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@ClientMarshaller(Object.class)
@ServerMarshaller(Object.class)
public class ObjectMarshaller extends AbstractNullableMarshaller<Object> {
  @Override
  public Object[] getEmptyArray() {
    return new Object[0];
  }

  public final Object demarshall(final Class<?> targetType, final EJValue o, final MarshallingSession ctx) {
    if (o.isNull()) {
      return null;
    }

    if (o.isObject() != null) {
      final EJObject jsObject = o.isObject();
      final EJValue ejEncType = jsObject.get(SerializationParts.ENCODED_TYPE);
      String encodedType = null;
      if (!ejEncType.isNull() && ejEncType.isString() != null) {
        encodedType = ejEncType.isString().stringValue();
      }

      if (encodedType == null) {
        if (targetType == null) {
          if (jsObject.containsKey(SerializationParts.QUALIFIED_VALUE)) {
            // the object we're decoding is a wrapper with ^ObjectID and ^Value, but no type information.
            // just bypass this layer and return the "meat"
            return demarshall(Object.class, jsObject.get(SerializationParts.QUALIFIED_VALUE), ctx);
          }
          // without a ^Value property, this must be a Map
          return MapMarshaller.INSTANCE.demarshall(o, ctx);
        }
        else if (ctx.getMarshallerInstance(targetType.getName()) != null) {
          return ctx.getMarshallerInstance(targetType.getName()).demarshall(o, ctx);
        }
      }
      else if ("java.lang.Object".equals(encodedType)) {
          // check for null Objects to avoid a stack overflow,
    	  // otherwise we'll just fall through, find the ObjectMarshaller again
    	  // and keep looping...
    	  if (jsObject.containsKey(SerializationParts.QUALIFIED_VALUE) &&
    			  jsObject.get(SerializationParts.QUALIFIED_VALUE)==null)
    		  return null;
      }

      if (jsObject.containsKey(SerializationParts.NUMERIC_VALUE)) {
        return NumbersUtils.getNumber(encodedType, jsObject.get(SerializationParts.NUMERIC_VALUE));
      }

      if (ctx.getMarshallerInstance(encodedType) == null) {
        throw new RuntimeException("marshalled type is unknown to the marshalling framework: " + encodedType);
      }

      return ctx.getMarshallerInstance(encodedType).demarshall(o, ctx);
    }
    else if (o.isArray() != null) {
      String assumedElementType = ctx.getAssumedElementType();
      ctx.setAssumedElementType(Object.class.getName());
      List result =  new ListMarshaller().doDemarshall(o.isArray(), ctx);
      ctx.setAssumedElementType(assumedElementType);
      return result;
    }
    else if (o.isString() != null) {
      return o.isString().stringValue();
    }
    else if (o.isBoolean() != null) {
      return o.isBoolean().booleanValue();
    }
    else if (o.isNumber() != null) {
      return o.isNumber().doubleValue();
    }

    return null;
  }

  @Override
  public Object doNotNullDemarshall(final EJValue o, final MarshallingSession ctx) {
    return this.demarshall(null, o, ctx);
  }

  @Override
  public String doNotNullMarshall(final Object o, final MarshallingSession ctx) {
    if ((o instanceof Number && !o.getClass().getName().startsWith("java.math.Big")) || o instanceof Boolean) {
      return NumbersUtils.qualifiedNumericEncoding(o);
    }

    if (MarshallUtil.getMarshaller(o, ctx) == null) {
      throw new RuntimeException("marshalled type is unknown to the marshalling framework: " + o.getClass().getName());
    }

    return MarshallUtil.getMarshaller(o, ctx).marshall(o, ctx);
  }
}
