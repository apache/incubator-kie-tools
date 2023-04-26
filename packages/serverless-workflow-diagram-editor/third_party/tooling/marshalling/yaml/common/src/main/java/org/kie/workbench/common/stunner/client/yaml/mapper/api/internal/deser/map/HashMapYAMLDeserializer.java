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

import java.util.HashMap;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;

/**
 * Default {@link YAMLDeserializer} implementation for {@link java.util.HashMap}.
 *
 * <p>Cannot be overriden. Use {@link BaseMapYAMLDeserializer}.
 *
 * @param <K> Type of the keys inside the {@link java.util.HashMap}
 * @param <V> Type of the values inside the {@link java.util.HashMap}
 * @author Nicolas Morel
 * @version $Id: $
 */
public final class HashMapYAMLDeserializer<K, V>
    extends BaseMapYAMLDeserializer<HashMap<K, V>, K, V> {

  /**
   * @param keyDeserializer {@link YAMLDeserializer} used to deserialize the keys.
   * @param valueDeserializer {@link YAMLDeserializer} used to deserialize the values.
   */
  private HashMapYAMLDeserializer(
      YAMLDeserializer<K> keyDeserializer, YAMLDeserializer<V> valueDeserializer) {
    super(keyDeserializer, valueDeserializer);
  }

  /**
   * newInstance
   *
   * @param keyDeserializer {@link YAMLDeserializer} used to deserialize the keys.
   * @param valueDeserializer {@link YAMLDeserializer} used to deserialize the values.
   * @param <K> Type of the keys inside the {@link java.util.HashMap}
   * @param <V> Type of the values inside the {@link java.util.HashMap}
   * @return a new instance of {@link HashMapYAMLDeserializer}
   */
  public static <K, V> HashMapYAMLDeserializer<K, V> newInstance(
      YAMLDeserializer<K> keyDeserializer, YAMLDeserializer<V> valueDeserializer) {
    return new HashMapYAMLDeserializer<>(keyDeserializer, valueDeserializer);
  }

  /** {@inheritDoc} */
  @Override
  protected HashMap<K, V> newMap() {
    return new HashMap<>();
  }
}
