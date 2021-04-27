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

import java.util.Collection;

import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.json.EJArray;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.util.EncDecUtil;

/**
 * @author Mike Brock
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public abstract class AbstractCollectionMarshaller<C extends Collection> extends AbstractBackReferencingMarshaller<C> {

  @Override
  public void doMarshall(final StringBuilder buf, final C o, final MarshallingSession ctx) {
    EncDecUtil.arrayMarshall(buf, o, ctx);
  }

  @Override
  public final C doDemarshall(final EJValue o, final MarshallingSession ctx) {
    if (o.isNull()) return null;

    final EJObject obj = o.isObject();

    if (obj != null) {
      final EJValue val = obj.get(SerializationParts.QUALIFIED_VALUE);
      return doDemarshall(val.isArray(), ctx);
    }
    else {
      return doDemarshall(o.isArray(), ctx);
    }
  }

  public abstract C doDemarshall(final EJArray o, MarshallingSession ctx);

  protected <T extends Collection<Object>> T marshallToCollection(final T collection,
                                                                  final EJArray array,
                                                                  final MarshallingSession ctx) {
    if (array == null) return null;

    final String assumedElementType = ctx.getAssumedElementType();
    // the assumed element type can only be used once since it is not set for nested collections.
    ctx.setAssumedElementType(null);

    for (int i = 0; i < array.size(); i++) {
      final EJValue elem = array.get(i);
      if (!elem.isNull()) {
        String type = null;
        final EJObject jsonObject;
        if ((jsonObject = elem.isObject()) != null) {
          if (!jsonObject.containsKey(SerializationParts.ENCODED_TYPE)) {
            // for collections with a concrete type parameter, we treat the ^EncodedType value as optional
            type = assumedElementType;
          }
        }
        else {
          type = assumedElementType;
        }

        if (type == null) {
          type = ctx.determineTypeFor(null, elem);
        }

        final Marshaller<Object> marshallerInstance = ctx.getMarshallerInstance(type);
        if (marshallerInstance == null) {
          throw new RuntimeException("no marshaller for type: " + type);
        }
        collection.add(marshallerInstance.demarshall(elem, ctx));
      }
      else {
        collection.add(null);
      }
    }

    return collection;
  }
}
