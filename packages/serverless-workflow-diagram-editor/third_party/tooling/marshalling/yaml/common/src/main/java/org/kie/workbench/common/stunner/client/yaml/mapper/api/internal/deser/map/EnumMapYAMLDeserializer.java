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

import java.util.EnumMap;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.EnumYAMLDeserializer;

/**
 * Default {@link YAMLDeserializer} implementation for {@link java.util.EnumMap}.
 *
 * <p>Cannot be overriden. Use {@link BaseMapYAMLDeserializer}.
 *
 * @param <E> Type of the enum keys inside the {@link java.util.EnumMap}
 * @param <V> Type of the values inside the {@link java.util.EnumMap}
 * @author Nicolas Morel
 * @version $Id: $
 */
public final class EnumMapYAMLDeserializer<E extends Enum<E>, V>
    extends BaseMapYAMLDeserializer<EnumMap<E, V>, E, V> {

  /** Class of the enum key */
  private final Class<E> enumClass;

  /**
   * @param keyDeserializer {@link YAMLDeserializer} used to deserialize the enum keys.
   * @param valueDeserializer {@link YAMLDeserializer} used to deserialize the values.
   */
  private EnumMapYAMLDeserializer(
      EnumYAMLDeserializer<E> keyDeserializer, YAMLDeserializer<V> valueDeserializer) {
    super(keyDeserializer, valueDeserializer);
    this.enumClass = keyDeserializer.getEnumClass();
  }

  /**
   * newInstance
   *
   * @param keyDeserializer {@link EnumYAMLDeserializer} used to deserialize the enum keys.
   * @param valueDeserializer {@link YAMLDeserializer} used to deserialize the values.
   * @param <V> Type of the values inside the {@link java.util.EnumMap}
   * @return a new instance of {@link EnumMapYAMLDeserializer}
   */
  public static <E extends Enum<E>, V> EnumMapYAMLDeserializer<E, V> newInstance(
      EnumYAMLDeserializer<E> keyDeserializer, YAMLDeserializer<V> valueDeserializer) {
    return new EnumMapYAMLDeserializer<>(keyDeserializer, valueDeserializer);
  }

  /** {@inheritDoc} */
  @Override
  protected EnumMap<E, V> newMap() {
    return new EnumMap<>(enumClass);
  }
}
