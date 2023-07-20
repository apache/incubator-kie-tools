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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.map;

import java.util.Map;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;

/**
 * Base {@link YAMLDeserializer} implementation for {@link java.util.Map}.
 *
 * @param <M> Type of the {@link java.util.Map}
 * @param <K> Type of the keys inside the {@link java.util.Map}
 * @param <V> Type of the values inside the {@link java.util.Map}
 * @author Nicolas Morel
 * @version $Id: $
 */
public abstract class BaseMapYAMLDeserializer<M extends Map<K, V>, K, V>
    implements YAMLDeserializer<M> {

  /** {@link YAMLDeserializer} used to deserialize the keys. */
  protected final YAMLDeserializer<K> keyDeserializer;

  /** {@link YAMLDeserializer} used to deserialize the values. */
  protected final YAMLDeserializer<V> valueDeserializer;

  /**
   * Constructor for BaseMapYAMLDeserializer.
   *
   * @param keyDeserializer {@link YAMLDeserializer} used to deserialize the keys.
   * @param valueDeserializer {@link YAMLDeserializer} used to deserialize the values.
   */
  protected BaseMapYAMLDeserializer(
      YAMLDeserializer<K> keyDeserializer, YAMLDeserializer<V> valueDeserializer) {
    if (null == keyDeserializer) {
      throw new IllegalArgumentException("keyDeserializer cannot be null");
    }
    if (null == valueDeserializer) {
      throw new IllegalArgumentException("valueDeserializer cannot be null");
    }
    this.keyDeserializer = keyDeserializer;
    this.valueDeserializer = valueDeserializer;
  }

  /** {@inheritDoc} */
  @Override
  public M deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    throw new UnsupportedOperationException();
  }

  @Override
  public M deserialize(YamlNode node, YAMLDeserializationContext ctx) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  /**
   * Instantiates a new map for deserialization process.
   *
   * @return the new map
   */
  protected abstract M newMap();
}
