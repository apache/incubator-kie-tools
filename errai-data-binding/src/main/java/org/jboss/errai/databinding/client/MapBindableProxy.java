/*
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.databinding.client;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.StateSync;

/**
 * A custom {@link BindableProxy} allowing data-binding to a {@link Map}. This allows data-binding to be used with a
 * collection of properties that is not known at compile-time.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class MapBindableProxy implements Map<String, Object>, BindableProxy<Map<String, Object>> {

  private final BindableProxyAgent<Map<String, Object>> agent;

  public MapBindableProxy( final Map<String, PropertyType> propertyTypes ) {
    agent = new BindableProxyAgent<>(this, new HashMap<>());
    agent.propertyTypes.putAll(propertyTypes);
    agent
      .propertyTypes
      .entrySet()
      .stream()
      .filter(entry -> entry.getValue() instanceof MapPropertyType)
      .forEach(entry -> agent.binders.put(entry.getKey(), DataBinder.forMap(((MapPropertyType) entry.getValue()).getPropertyTypes())));
  }

  @Override
  public Object unwrap() {
    return agent.target;
  }

  @Override
  public Object get(final String propertyName) {
    if (!agent.propertyTypes.containsKey(propertyName)) {
      throw new NonExistingPropertyException("Map", propertyName);
    }

    return agent.target.get(propertyName);
  }

  @Override
  public void set(final String propertyName, final Object value) {
    if (!agent.propertyTypes.containsKey(propertyName)) {
      throw new NonExistingPropertyException("Map", propertyName);
    }

    agent.target.put(propertyName, value);
  }

  @Override
  public Map<String, PropertyType> getBeanProperties() {
    return Collections.unmodifiableMap(agent.propertyTypes);
  }

  @Override
  public BindableProxyAgent<Map<String, Object>> getBindableProxyAgent() {
    return agent;
  }

  @Override
  public void updateWidgets() {
    agent.updateWidgetsAndFireEvents();
  }

  @Override
  public Map<String, Object> deepUnwrap() {
    final Map<String, Object> clone = new HashMap<>();
    agent.target.forEach((k,v) -> clone.put(k, (v instanceof BindableProxy ? ((BindableProxy<?>) v).deepUnwrap() : v)));

    return clone;
  }

  @Override
  public int size() {
    return agent.target.size();
  }

  @Override
  public boolean isEmpty() {
    return agent.target.isEmpty();
  }

  @Override
  public boolean containsKey(final Object key) {
    return agent.target.containsKey(key);
  }

  @Override
  public boolean containsValue(final Object value) {
    return agent.target.containsValue(value);
  }

  @Override
  public Object get(final Object key) {
    return agent.target.get(key);
  }

  @Override
  public Object put(final String key, Object value) {
    final PropertyType propertyType = agent.propertyTypes.get(key);
    if (propertyType == null) {
      throw new NonExistingPropertyException("Map", key);
    }

    if (propertyType.isList() && value instanceof List) {
      value = agent.ensureBoundListIsProxied(key, (List<?>) value);
    }
    else if (propertyType.isBindable() && !(value instanceof BindableProxy)) {
      DataBinder nestedBinder = agent.binders.get(key);
      if (nestedBinder == null) {
        if (propertyType instanceof MapPropertyType) {
          nestedBinder = DataBinder.forMap(((MapPropertyType) propertyType).getPropertyTypes());
        }
        else {
          nestedBinder = DataBinder.forModel(value);
        }
        agent.binders.put(key, nestedBinder);
      }
      else if (propertyType instanceof MapPropertyType) {
        final MapBindableProxy mapProxy = new MapBindableProxy(((MapPropertyType) propertyType).getPropertyTypes());
        mapProxy.agent.target = (Map<String, Object>) value;
        nestedBinder.setModel(mapProxy, StateSync.FROM_MODEL, true);
      }
      else {
        nestedBinder.setModel(value, StateSync.FROM_MODEL, true);
      }
      value = nestedBinder.getModel();
    }

    final Object oldValue = agent.target.get(key);
    final Object retVal = agent.target.put(key, value);
    agent.updateWidgetsAndFireEvent(false, key, oldValue, value);

    return retVal;
  }

  @Override
  public Object remove(final Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(final Map<? extends String, ? extends Object> m) {
    m.forEach(this::put);
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<String> keySet() {
    return Collections.unmodifiableSet(agent.target.keySet());
  }

  @Override
  public Collection<Object> values() {
    return Collections.unmodifiableCollection(agent.target.values());
  }

  @Override
  public Set<java.util.Map.Entry<String, Object>> entrySet() {
    return Collections.unmodifiableSet(agent.target.entrySet());
  }

}
