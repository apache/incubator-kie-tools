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

package org.jboss.errai.marshalling.client.marshallers;

import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.json.EJValue;

/**
 * Handles null-ing of types as a simple wrapper.
 *
 * @author Mike Brock
 */
public abstract class AbstractNullableMarshaller<T> implements Marshaller<T> {
  @Override
  public final T demarshall(final EJValue o, final MarshallingSession ctx) {
    if (o.isNull()) {
      return null;
    }
    else {
      return doNotNullDemarshall(o, ctx);
    }
  }

  @Override
  public final String marshall(final T o, final MarshallingSession ctx) {
    if (o == null) {
      return "null";
    }
    else {
      return doNotNullMarshall(o, ctx);
    }
  }

  public abstract T doNotNullDemarshall(EJValue o, MarshallingSession ctx);

  public abstract String doNotNullMarshall(T o, MarshallingSession ctx);
}
