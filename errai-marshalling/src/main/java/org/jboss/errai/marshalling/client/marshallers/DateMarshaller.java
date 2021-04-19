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
import org.jboss.errai.marshalling.client.api.json.EJValue;

import java.util.Date;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@ClientMarshaller(Date.class)
@ServerMarshaller(Date.class)
public class DateMarshaller extends AbstractNullableMarshaller<Date> {
  @Override
  public Date[] getEmptyArray() {
    return new Date[0];
  }

  @Override
  public Date doNotNullDemarshall(final EJValue o, final MarshallingSession ctx) {
    if (o.isObject() != null) {
      EJValue qualifiedValue = o.isObject().get(SerializationParts.QUALIFIED_VALUE);
      if (!qualifiedValue.isNull() && qualifiedValue.isString() != null) {
        return new Date(Long.parseLong(qualifiedValue.isString().stringValue()));
      }
      EJValue numericValue = o.isObject().get(SerializationParts.NUMERIC_VALUE);
      if (!numericValue.isNull() && numericValue.isNumber() != null) {
        return new Date(new Double(numericValue.isNumber().doubleValue()).longValue());
      }
      if (!numericValue.isNull() && numericValue.isString() != null) {
        return new Date(Long.parseLong(numericValue.isString().stringValue()));
      }
    }

    return null;
  }

  @Override
  public String doNotNullMarshall(final Date o, final MarshallingSession ctx) {
    return "{\"" + SerializationParts.ENCODED_TYPE + "\":\"" + Date.class.getName() + "\"," +
            "\"" + SerializationParts.OBJECT_ID + "\":\"" + o.hashCode() + "\"," +
            "\"" + SerializationParts.QUALIFIED_VALUE + "\":\"" + o.getTime() + "\"}";
  }
}
