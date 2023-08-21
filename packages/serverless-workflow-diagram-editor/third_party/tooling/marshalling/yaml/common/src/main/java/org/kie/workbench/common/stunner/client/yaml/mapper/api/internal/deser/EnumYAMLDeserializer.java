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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;

/**
 * Default {@link YAMLDeserializer} implementation for {@link java.lang.Enum}.
 *
 * @param <E> Type of the enum
 * @author Nicolas Morel
 * @version $Id: $
 */
public class EnumYAMLDeserializer<E extends Enum<E>> implements YAMLDeserializer<E> {

  private final Class<E> enumClass;
  private final E[] values;
  /**
   * Constructor for EnumYAMLDeserializer.
   *
   * @param enumClass class of the enumeration
   */
  protected EnumYAMLDeserializer(Class<E> enumClass, E[] values) {
    if (null == enumClass) {
      throw new IllegalArgumentException("enumClass cannot be null");
    }
    this.enumClass = enumClass;
    this.values = values;
  }

  /**
   * newInstance
   *
   * @param enumClass class of the enumeration
   * @return a new instance of {@link EnumYAMLDeserializer}
   */
  public static <E extends Enum<E>> EnumYAMLDeserializer<E> newInstance(
      Class<E> enumClass, E... values) {
    return new EnumYAMLDeserializer<>(enumClass, values);
  }

  /** {@inheritDoc} */
  @Override
  public E deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    return deserialize(yaml.getNode(key), ctx);
  }

  @Override
  public E deserialize(YamlNode value, YAMLDeserializationContext ctx) {
    try {
      return getEnum(values, value);
    } catch (IllegalArgumentException ex) {
      if (ctx.isReadUnknownEnumValuesAsNull()) {
        return null;
      }
      throw ex;
    }
  }

  private <E extends Enum<E>> E getEnum(E[] values, YamlNode name) {
    for (int i = 0; i < values.length; i++) {
      if (values[i].name().equals(name.asScalar().value())) {
        return values[i];
      }
    }
    throw new IllegalArgumentException(
        "[" + name + "] is not a valid enum constant for Enum type " + getEnumClass().getName());
  }

  /**
   * Getter for the field <code>enumClass</code>.
   *
   * @return a {@link java.lang.Class} object.
   */
  public Class<E> getEnumClass() {
    return enumClass;
  }
}
