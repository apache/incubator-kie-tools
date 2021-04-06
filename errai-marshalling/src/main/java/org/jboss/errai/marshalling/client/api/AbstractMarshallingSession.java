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

package org.jboss.errai.marshalling.client.api;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.util.MarshallUtil;

/**
 * @author Mike Brock
 */
public abstract class AbstractMarshallingSession implements MarshallingSession {
  private final MappingContext context;

  private final Map<Object, Integer> objects = new IdentityHashMap<Object, Integer>();
  private final Map<String, Object> objectMap = new HashMap<String, Object>();
  private String assumedElementType = null;
  private String assumedMapKeyType = null;
  private String assumedMapValueType = null;

  protected AbstractMarshallingSession(final MappingContext context) {
    this.context = Assert.notNull(context);
  }

  private static final Marshaller<Object> NULL_MARSHALLER = new Marshaller<Object>() {
    @Override
    public Object demarshall(final EJValue o, final MarshallingSession ctx) {
      return null;
    }

    @Override
    public String marshall(final Object o, final MarshallingSession ctx) {
      return "null";
    }

    @Override
    public Object[] getEmptyArray() {
      return new Object[0];
    }
  };

  @Override
  public Marshaller<Object> getMarshallerInstance(final String fqcn) {
    Marshaller<Object> marshaller = context.getMarshaller(fqcn);
    if (marshaller == null) {
      if (fqcn == null) {
        return NULL_MARSHALLER;
      }
      if (fqcn.startsWith("[")) {
        marshaller = context.getMarshaller(MarshallUtil.getComponentClassName(fqcn));
        if (marshaller != null) {
          marshaller = new ArrayMarshallerWrapper(marshaller);
        }
      }
    }
    return marshaller;
  }

  @Override
  public MappingContext getMappingContext() {
    return context;
  }

  @Override
  public boolean hasObject(final String hashCode) {
    return objectMap.containsKey(hashCode);
  }

  @Override
  public boolean hasObject(final Object reference) {
    return reference != null && objects.containsKey(reference);
  }

  @Override
  public <T> T getObject(final Class<T> type, final String hashCode) {
    return (T) objectMap.get(hashCode);
  }

  @Override
  public <T> T recordObject(final String hashCode, final T instance) {
    if ("-1".equals(hashCode)) return instance;

    objectMap.put(hashCode, instance);

    return instance;
  }

  @Override
  public String getObject(final Object reference) {
    Integer i = objects.get(reference);

    if (i == null) {
      objects.put(reference, (i = objects.size() + 1));
      recordObject(i.toString(), reference);
    }

    return i.toString();
  }

  @Override
  public String getAssumedElementType() {
    return this.assumedElementType;
  }

  @Override
  public void setAssumedElementType(final String assumendElementType) {
     this.assumedElementType = assumendElementType;
  }

  @Override
  public String getAssumedMapKeyType() {
    return this.assumedMapKeyType;
  }

  @Override
  public void setAssumedMapKeyType(String assumedMapKeyType) {
    this.assumedMapKeyType = assumedMapKeyType;
  }

  @Override
  public String getAssumedMapValueType() {
    return this.assumedMapValueType;
  }

  @Override
  public void setAssumedMapValueType(String assumedMapValueType) {
    this.assumedMapValueType = assumedMapValueType;
  }

  @Override
  public void resetAssumedTypes() {
    this.assumedMapKeyType = null;
    this.assumedMapValueType = null;
    this.assumedElementType = null;
  }
}
