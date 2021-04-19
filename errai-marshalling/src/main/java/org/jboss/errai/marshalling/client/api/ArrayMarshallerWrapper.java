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

package org.jboss.errai.marshalling.client.api;

import java.util.Arrays;

import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.marshallers.AbstractNullableMarshaller;
import org.jboss.errai.marshalling.client.marshallers.ListMarshaller;

/**
 * A marshaller that wraps another marshaller, producing and consuming arrays of
 * objects handled by that marshaller.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
public class ArrayMarshallerWrapper extends AbstractNullableMarshaller<Object> {

  private final Marshaller<?> wrappedMarshaller;
  
  public ArrayMarshallerWrapper(final Marshaller<?> wrappedMarshaller) {
    this.wrappedMarshaller = wrappedMarshaller;
  }

  @Override
  public Object doNotNullDemarshall(final EJValue o, final MarshallingSession ctx) {
    return ListMarshaller.INSTANCE.demarshall(o, ctx).toArray(wrappedMarshaller.getEmptyArray());
  }

  @Override
  public String doNotNullMarshall(final Object o, final MarshallingSession ctx) {
    return ListMarshaller.INSTANCE.marshall(Arrays.asList((Object[]) o), 
        o.getClass().getName(), ctx);
  }

  @Override
  public Object[] getEmptyArray() {
    throw new UnsupportedOperationException("Not implemented, but should create an array with n+1 dimensions");
  }
}
