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
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;

/**
 * @author Mike Brock
 */
public abstract class AbstractBackReferencingMarshaller<C> implements Marshaller<C> {

  @Override
  public final String marshall(final C o, final MarshallingSession ctx) {
    if (o == null) {
      return "null";
    }

    return marshall(o, o.getClass().getName(), ctx);
  }

  public final String marshall(final C o, final String encodedType, final MarshallingSession ctx) {
    if (o == null) {
      return "null";
    }

    final boolean isNew = !ctx.hasObject(o);
    final String objId = ctx.getObject(o);

    final StringBuilder buf = new StringBuilder("{\"").append(SerializationParts.ENCODED_TYPE).append("\":\"")
            .append(encodedType).append("\",\"").append(SerializationParts.OBJECT_ID).append("\":\"")
            .append(objId).append("\"");

    if (!isNew) {
      return buf.append("}").toString();
    }
    else {
      doMarshall(buf.append(",\"").append(SerializationParts.QUALIFIED_VALUE).append("\":"), o, ctx);
      return buf.append("}").toString();
    }
  }

  public abstract void doMarshall(StringBuilder buf, C o, MarshallingSession ctx);

  @SuppressWarnings("unchecked")
  @Override
  public C demarshall(final EJValue o, final MarshallingSession ctx) {
    if (o.isNull()) {
      return null;
    }

    final EJObject obj = o.isObject();

    final String objId = obj.get(SerializationParts.OBJECT_ID).isString().stringValue();
    if (ctx.hasObject(objId)) {
      return (C) ctx.getObject(Object.class, objId);
    }

    final C val = doDemarshall(o, ctx);
    ctx.recordObject(objId, val);
    return val;
  }

  public abstract C doDemarshall(EJValue o, MarshallingSession ctx);
}
