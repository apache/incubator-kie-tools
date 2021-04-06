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

import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.annotations.AlwaysQualify;
import org.jboss.errai.marshalling.client.api.annotations.ClientMarshaller;
import org.jboss.errai.marshalling.client.api.annotations.ServerMarshaller;
import org.jboss.errai.marshalling.client.api.json.EJValue;

import java.util.LinkedHashMap;

/**
 * @author Mike Brock
 */
@ClientMarshaller(LinkedHashMap.class)
@ServerMarshaller(LinkedHashMap.class)
@AlwaysQualify
public class LinkedMapMarshaller extends MapMarshaller<LinkedHashMap<Object, Object>> {
  @SuppressWarnings("unchecked")
  @Override
  public LinkedHashMap<Object, Object> demarshall(final EJValue o, final MarshallingSession ctx) {
    return doDemarshall(new LinkedHashMap(), o, ctx);
  }

  @Override
  public LinkedHashMap[] getEmptyArray() {
    return new LinkedHashMap[0];
  }
}
