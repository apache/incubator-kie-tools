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
import org.jboss.errai.marshalling.client.Marshalling;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.annotations.ClientMarshaller;
import org.jboss.errai.marshalling.client.api.json.EJValue;

/**
 * This marshaller is used to handle class instances on the client.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@ClientMarshaller(Class.class)
public class ClientClassMarshaller extends AbstractNullableMarshaller<Class<?>> {
  @Override
  public Class<?>[] getEmptyArray() {
    return new Class[0];
  }

  public final Class<?> demarshall(final Class<?> targetType, final EJValue o, final MarshallingSession ctx) {
    if (o.isNull() || o.isObject() == null) {
      return null;
    }
    final EJValue className = o.isObject().get(SerializationParts.QUALIFIED_VALUE);
    return Marshalling.getMarshaller(className.isString().stringValue())
            .getEmptyArray().getClass().getComponentType();
  }

  @Override
  public Class<?> doNotNullDemarshall(final EJValue o, final MarshallingSession ctx) {
    return this.demarshall(null, o, ctx);
  }

  @Override
  public String doNotNullMarshall(final Class<?> o, final MarshallingSession ctx) {
    return "{\"" + SerializationParts.ENCODED_TYPE + "\":\"" + Class.class.getName() + "\"," +
            "\"" + SerializationParts.OBJECT_ID + "\":\"" + o.hashCode() + "\"," +
            "\"" + SerializationParts.QUALIFIED_VALUE + "\":\"" + o.getName() + "\"}";
  }
}
