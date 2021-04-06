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

package org.jboss.errai.marshalling.server.json.impl;

import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;

import java.util.Map;
import java.util.Set;

/**
 * @author Mike Brock
 */
public class ErraiJSONObject implements EJObject {
  final Map oMap;

  public ErraiJSONObject(final Map oMap) {
    this.oMap = oMap;
  }

  @Override
  public EJValue get(final String name) {
    return new ErraiJSONValue(oMap.get(name));
  }
  
  @Override
  public EJValue getIfNotNull(final String name) {
    Object v = oMap.get(name);
    return v == null ? null : new ErraiJSONValue(v);
  }

  @Override
  public Set<String> keySet() {
    return oMap.keySet();
  }

  @Override
  public boolean containsKey(final String name) {
    return oMap.containsKey(name);
  }

  @Override
  public int size() {
    return oMap.size();
  }
}
