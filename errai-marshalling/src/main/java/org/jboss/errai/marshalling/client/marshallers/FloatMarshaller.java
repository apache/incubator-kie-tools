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

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
@ClientMarshaller(Float.class)
@ServerMarshaller(Float.class)
public class FloatMarshaller extends AbstractNumberMarshaller<Float> {
  @Override
  public Float[] getEmptyArray() {
    return new Float[0];
  }

  @Override
  public String doNotNullMarshall(Float o, MarshallingSession ctx) {
    if (o.isNaN() || o.isInfinite()) {
      return "\"" + o.toString() + "\"";
    }
    else {
      return o.toString();
    }

  }

  @Override
  public Float doNotNullDemarshall(final EJValue o, final MarshallingSession ctx) {
    if (o.isObject() != null) {
      EJValue numVal = o.isObject().get(SerializationParts.NUMERIC_VALUE);
      if (numVal.isString() != null) {
        return Float.parseFloat(numVal.isString().stringValue());
      }
      return numVal.isNumber().floatValue();
    }
    else if (o.isString() != null) {
      return Float.parseFloat(o.isString().stringValue());
    }
    else {
      return o.isNumber().floatValue();
    }
  }
}
