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
import org.jboss.errai.marshalling.client.util.MarshallUtil;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
@ClientMarshaller(Character.class)
@ServerMarshaller(Character.class)
public class CharacterMarshaller extends AbstractNullableMarshaller<Character> {
  @Override
  public Character[] getEmptyArray() {
    return new Character[0];
  }
  
  @Override
  public Character doNotNullDemarshall(final EJValue o, final MarshallingSession ctx) {
  if (o.isObject() != null) {
      return o.isObject().get(SerializationParts.NUMERIC_VALUE).isString().stringValue().charAt(0);
    }
    else {
      return o.isString().stringValue().charAt(0);
    }
  }

  @Override
  public String doNotNullMarshall(final Character o, final MarshallingSession ctx) {
    return "\"" + MarshallUtil.jsonStringEscape(o) + "\"";
  }
}
