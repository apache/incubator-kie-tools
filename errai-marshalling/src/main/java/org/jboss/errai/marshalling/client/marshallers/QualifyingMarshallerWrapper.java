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
import org.jboss.errai.common.client.util.Base64Util;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;

/**
 * Used to wrap marshallers annotated with {@link org.jboss.errai.marshalling.client.api.annotations.AlwaysQualify}
 * 
 * @author Mike Brock
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class QualifyingMarshallerWrapper<T> extends AbstractNullableMarshaller<T> {
  private final Marshaller<T> delegate;
  private final Class<T> type;

  public QualifyingMarshallerWrapper(final Marshaller<T> delegate, Class<T> type) {
    this.delegate = delegate;
    this.type = type;
  }

  @Override
  public T[] getEmptyArray() {
    return delegate.getEmptyArray();
  }

  @Override
  public T doNotNullDemarshall(final EJValue o, final MarshallingSession ctx) {
    final EJObject obj = o.isObject();

    if (obj != null) {
      final String objId = obj.get(SerializationParts.OBJECT_ID).isString().stringValue();
      if (ctx.hasObject(objId)) {
        // noinspection unchecked
        return (T) ctx.getObject(Object.class, objId);
      }

      EJValue val = obj.get(SerializationParts.QUALIFIED_VALUE);
      if (val.isNull()) {
        val = o;
      }
      return ctx.recordObject(objId, delegate.demarshall(val, ctx));
    }
    else {
      // This is to support one-dimensional Jackson char and byte array representations which cannot be wrapped in
      // an JSON object by the transfomer because they are indistinguishable from plain Strings.
      Class<?> componentType = type.getComponentType();
      if (o.isString() != null && componentType != null) {
        if (componentType.equals(byte.class)) {
          return (T) Base64Util.decode(o.isString().stringValue());
        }
        else if (componentType.equals(char.class)) {
          return (T) o.isString().stringValue().toCharArray();
        }
      }
    }
    return null;
  }

  @Override
  public String doNotNullMarshall(final T o, final MarshallingSession ctx) {
    final boolean isNew = !ctx.hasObject(o);

    final StringBuilder buf = new StringBuilder("{\"").append(SerializationParts.ENCODED_TYPE).append("\":\"")
            .append(o.getClass().getName()).append("\",\"").append(SerializationParts.OBJECT_ID).append("\":\"")
            .append(ctx.getObject(o)).append("\"");

    if (!isNew) {
      return buf.append("}").toString();
    }
    else {
      return buf.append(",\"").append(SerializationParts.QUALIFIED_VALUE).append("\":").append(
          delegate.marshall(o, ctx))
              .append("}").toString();
    }
  }
}
