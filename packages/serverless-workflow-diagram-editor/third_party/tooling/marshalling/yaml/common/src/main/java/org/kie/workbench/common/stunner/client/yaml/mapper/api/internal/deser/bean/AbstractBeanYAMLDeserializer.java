/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.bean;

import java.util.Collection;
import java.util.Map;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.NodeType;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;

/**
 * Base implementation of {@link YAMLDeserializer} for beans.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public abstract class AbstractBeanYAMLDeserializer<T> implements YAMLDeserializer<T> {

  protected final InstanceBuilder<T> instanceBuilder;
  private Map<String, BeanPropertyDeserializer<T, ?>> deserializers = initDeserializers();

  /** Constructor for AbstractBeanYAMLDeserializer. */
  protected AbstractBeanYAMLDeserializer() {
    this.instanceBuilder = initInstanceBuilder();
  }

  /**
   * Initialize the {@link InstanceBuilder}. Returns null if the class isn't instantiable.
   *
   * @return a {@link InstanceBuilder} object.
   */
  protected InstanceBuilder<T> initInstanceBuilder() {
    return null;
  }

  /** {@inheritDoc} */
  public T deserialize(YamlMapping yaml, YAMLDeserializationContext ctx) {
    return deserializeInline(yaml, ctx);
  }

  @Override
  public T deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    return deserialize(yaml.addMappingNode(key), ctx);
  }

  /**
   * Initialize the {@link Map} containing the property deserializers. Returns an empty map if there
   * are no properties to deserialize.
   *
   * @return a {@link Map} object.
   */
  protected abstract Map<String, BeanPropertyDeserializer<T, ?>> initDeserializers();

  /**
   * getDeserializedType
   *
   * @return a {@link java.lang.Class} object.
   */
  public abstract Class getDeserializedType();

  private BeanPropertyDeserializer<T, ?> getPropertyDeserializer(
      String propertyName, YAMLDeserializationContext ctx) {
    BeanPropertyDeserializer<T, ?> property = deserializers.get(propertyName);
    if (null == property) {
      throw new Error(
          "Unknown property '"
              + propertyName
              + "' in (de)serializer "
              + this.getClass().getCanonicalName());
    }
    return property;
  }

  public final T deserializeInline(YamlMapping yaml, YAMLDeserializationContext ctx) {
    if (yaml == null || yaml.isEmpty()) {
      return null;
    }

    Collection<String> props = yaml.keys();
    props.retainAll(deserializers.keySet());
    if (props.isEmpty()) {
      return null;
    }
    T instance = instanceBuilder.newInstance(ctx).getInstance();
    props.forEach(
        key -> {
          if (getPropertyDeserializer(key, ctx).getDeserializer()
              instanceof AbstractBeanYAMLDeserializer) {
            YamlMapping node = yaml.getMappingNode(key);
            BeanPropertyDeserializer propertyDeserializer = getPropertyDeserializer(key, ctx);
            Object value =
                ((AbstractBeanYAMLDeserializer) propertyDeserializer.getDeserializer())
                    .deserialize(node, ctx);
            propertyDeserializer.setValue(instance, value, ctx);
          } else {
            getPropertyDeserializer(key, ctx).deserialize(yaml, key, instance, ctx);
          }
        });
    return instance;
  }

  @Override
  public T deserialize(YamlNode node, YAMLDeserializationContext ctx) {
    if (node.type() == NodeType.MAPPING) {
      return deserialize(node.asMapping(), ctx);
    }
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
