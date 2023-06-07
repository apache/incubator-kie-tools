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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.map;

import java.util.Map;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.AbstractYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;

/**
 * Default {@link AbstractYAMLSerializer} implementation for {@link Map}.
 *
 * @param <M> Type of the {@link Map}
 * @param <K> Type of the keys inside the {@link Map}
 * @param <V> Type of the values inside the {@link Map}
 * @author Nicolas Morel
 * @version $Id: $
 */
public class MapYAMLSerializer<M extends Map<K, V>, K, V> extends AbstractYAMLSerializer<M> {

  protected final AbstractYAMLSerializer<K> keySerializer;
  protected final AbstractYAMLSerializer<V> valueSerializer;
  protected final String propertyName;

  /**
   * Constructor for MapYAMLSerializer.
   *
   * @param keySerializer {@link AbstractYAMLSerializer} used to serialize the keys.
   * @param valueSerializer {@link AbstractYAMLSerializer} used to serialize the values.
   */
  protected MapYAMLSerializer(
      AbstractYAMLSerializer<K> keySerializer,
      AbstractYAMLSerializer<V> valueSerializer,
      String propertyName) {
    if (null == keySerializer) {
      throw new IllegalArgumentException("keySerializer cannot be null");
    }
    if (null == valueSerializer) {
      throw new IllegalArgumentException("valueSerializer cannot be null");
    }
    if (null == propertyName) {
      throw new IllegalArgumentException("valueSerializer cannot be null");
    }
    this.keySerializer = keySerializer;
    this.valueSerializer = valueSerializer;
    this.propertyName = propertyName;
  }

  /**
   * newInstance
   *
   * @param keySerializer {@link AbstractYAMLSerializer} used to serialize the keys.
   * @param valueSerializer {@link AbstractYAMLSerializer} used to serialize the values.
   * @param <M> Type of the {@link Map}
   * @return a new instance of {@link MapYAMLSerializer}
   */
  public static <M extends Map<?, ?>> MapYAMLSerializer<M, ?, ?> newInstance(
      AbstractYAMLSerializer<?> keySerializer,
      AbstractYAMLSerializer<?> valueSerializer,
      String propertyName) {
    return new MapYAMLSerializer(keySerializer, valueSerializer, propertyName);
  }

  /** {@inheritDoc} */
  @Override
  public void doSerialize(YamlMapping writer, M values, YAMLSerializationContext ctx) {
    serializeValues(writer, values, ctx);
  }

  /** {@inheritDoc} */
  @Override
  protected boolean isEmpty(M value) {
    return null == value || value.isEmpty();
  }

  /**
   * serializeValues
   *
   * @param writer a {@link YamlMapping} object.
   * @param values a M object.
   * @param ctx a {@link YAMLSerializationContext} object.
   */
  public void serializeValues(YamlMapping writer, M values, YAMLSerializationContext ctx) {
    throw new UnsupportedOperationException();
    /*        if (!values.isEmpty()) {
        Map<K, V> map = values;
        if (ctx.isOrderMapEntriesByKeys() && !(values instanceof SortedMap<?, ?>)) {
            map = new TreeMap<>(map);
        }
        writer.beginObject(propertyName);
        for (Map.Entry<K, V> entry : map.entrySet()) {
            writer.beginObject("entry");
            String keyName = getNodeName(entry.getKey().getClass(), ctx);
            String valueName = getNodeName(entry.getValue().getClass(), ctx);
            writer.unescapeName(keyName);
            keySerializer.setPropertyName(keyName)
                    .serialize(writer, entry.getKey(), ctx, params, true);

            writer.unescapeName(valueName);
            valueSerializer.setPropertyName(valueName)
                    .serialize(writer, entry.getValue(), ctx, params, true);

            writer.endObject();
        }
        writer.endObject();
    }*/
  }

  private String getNodeName(Class clazz, YAMLSerializationContext ctx) {
    /*    if (ctx.isMapKeyAndValueCanonical()) {
      return clazz.getCanonicalName();
    }*/
    return clazz.getSimpleName();
  }

  @Override
  public void serialize(YamlSequence writer, M value, YAMLSerializationContext ctx) {
    throw new UnsupportedOperationException();
  }
}
