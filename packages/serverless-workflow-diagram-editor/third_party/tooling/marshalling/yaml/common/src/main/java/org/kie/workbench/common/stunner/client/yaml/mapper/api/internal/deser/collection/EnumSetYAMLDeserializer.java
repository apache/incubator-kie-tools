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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection;

import java.util.EnumSet;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.EnumYAMLDeserializer;

/**
 * Default {@link YAMLDeserializer} implementation for {@link java.util.EnumSet}.
 *
 * @param <E> Type of the enumeration inside the {@link java.util.EnumSet}
 * @author Nicolas Morel
 * @version $Id: $
 */
public class EnumSetYAMLDeserializer<E extends Enum<E>>
    extends BaseSetYAMLDeserializer<EnumSet<E>, E> {

  private final Class<E> enumClass;

  /**
   * @param deserializer {@link EnumYAMLDeserializer} used to deserialize the enums inside the
   *     {@link EnumSet}.
   */
  private EnumSetYAMLDeserializer(EnumYAMLDeserializer<E> deserializer) {
    super(deserializer);
    this.enumClass = deserializer.getEnumClass();
  }

  /**
   * newInstance
   *
   * @param deserializer {@link EnumYAMLDeserializer} used to deserialize the enums inside the
   *     {@link java.util.EnumSet}.
   * @return a new instance of {@link EnumSetYAMLDeserializer}
   */
  public static <E extends Enum<E>> EnumSetYAMLDeserializer<E> newInstance(
      EnumYAMLDeserializer<E> deserializer) {
    return new EnumSetYAMLDeserializer<>(deserializer);
  }

  /** {@inheritDoc} */
  @Override
  protected EnumSet<E> newCollection() {
    return EnumSet.noneOf(enumClass);
  }

  /** {@inheritDoc} */
  @Override
  protected boolean isNullValueAllowed() {
    return false;
  }
}
