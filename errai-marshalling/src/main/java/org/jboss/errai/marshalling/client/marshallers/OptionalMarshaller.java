/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.errai.marshalling.client.api.annotations.ServerMarshaller;
import org.jboss.errai.marshalling.client.api.json.EJValue;

import java.util.Optional;

/**
 * @author Tiago Bento <tfernand@redhat.com>
 */
@ClientMarshaller(Optional.class)
@ServerMarshaller(Optional.class)
public class OptionalMarshaller<T> extends AbstractNullableMarshaller<Optional<T>> {

  @Override
  @SuppressWarnings("unchecked")
  public Optional<T>[] getEmptyArray() {
    return new Optional[0];
  }

  @Override
  public String doNotNullMarshall(final Optional<T> o, final MarshallingSession ctx) {
    return "{\"" + SerializationParts.ENCODED_TYPE + "\":\"" + Optional.class.getName() + "\"," + "\""
            + SerializationParts.OBJECT_ID + "\":\"" + o.hashCode() + "\"," + "\"" + SerializationParts.QUALIFIED_VALUE
            + "\":" + o.map(Marshalling::toJSON).orElse(null) + "}";
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<T> doNotNullDemarshall(final EJValue o, final MarshallingSession ctx) {
    EJValue optionalQualifiedValue = o.isObject().get(SerializationParts.QUALIFIED_VALUE);
    return Optional.ofNullable((T) Marshalling.fromJSON(optionalQualifiedValue));
  }
}
