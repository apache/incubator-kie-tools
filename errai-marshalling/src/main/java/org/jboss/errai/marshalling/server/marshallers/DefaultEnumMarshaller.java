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

package org.jboss.errai.marshalling.server.marshallers;

import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.json.EJValue;

import java.lang.reflect.Array;

/**
 * @author Mike Brock
 */
public class DefaultEnumMarshaller implements Marshaller<Enum> {
  private final Class enumType;

  public DefaultEnumMarshaller(final Class enumType) {
    this.enumType = enumType;
  }

  public Enum demarshall(final EJValue a0, final MarshallingSession a1) {
    try {
      if (a0.isNull()) {
        return null;
      }
      return Enum.valueOf(enumType, a0.isObject().get(SerializationParts.ENUM_STRING_VALUE).isString().stringValue());
    }
    catch (Throwable t) {
      t.printStackTrace();
      throw new RuntimeException("error demarshalling enum: " + enumType.getName(), t);
    }
  }

  public String marshall(final Enum a0, final MarshallingSession a1) {
    if (a0 == null) {
      return "null";
    }

    if (a1.hasObject(a0)) {
      return new StringBuilder().append("{\"" + SerializationParts.ENCODED_TYPE + "\":\"" + enumType.getName()
              + "\",\"" + SerializationParts.OBJECT_ID + "\":\"").append(a1.getObject(a0)).append("\"}").toString();
    }

    return new StringBuilder().append("{\"" + SerializationParts.ENCODED_TYPE + "\":\"" + enumType.getName()
            + "\","
            + "\"" + SerializationParts.OBJECT_ID + "\":\"" + a1.getObject(a0) + "\""
            + ",\"" + SerializationParts.ENUM_STRING_VALUE + "\":\"").append(a0.name()).append("\"}").toString();
  }

  @Override
  public Enum[] getEmptyArray() {
    return (Enum[]) Array.newInstance(enumType, 0);
  }

}
