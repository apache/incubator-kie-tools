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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser;

import java.util.Collection;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLSerializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;

/**
 * Default {@link AbstractYAMLSerializer} implementation for {@link Collection}.
 *
 * @param <T> Type of the elements inside the {@link Collection}
 * @author Nicolas Morel
 * @version $Id: $
 */
public class CollectionYAMLSerializer<C extends Collection<T>, T>
    extends AbstractYAMLSerializer<C> {

  protected final YAMLSerializer<T> serializer;

  /**
   * Constructor for CollectionYAMLSerializer.
   *
   * @param serializer {@link AbstractYAMLSerializer} used to serialize the objects inside the
   *     {@link Collection}.
   */
  public CollectionYAMLSerializer(YAMLSerializer<T> serializer) {
    if (null == serializer) {
      throw new IllegalArgumentException("serializer cannot be null");
    }
    this.serializer = serializer;
  }

  /**
   * newInstance
   *
   * @param serializer {@link AbstractYAMLSerializer} used to serialize the objects inside the
   *     {@link Collection}.
   * @param <C> Type of the {@link Collection}
   * @return a new instance of {@link CollectionYAMLSerializer}
   */
  public static <C extends Collection<?>> CollectionYAMLSerializer<C, ?> newInstance(
      YAMLSerializer<?> serializer) {
    return new CollectionYAMLSerializer(serializer);
  }

  @Override
  public void serialize(
      YamlMapping writer, String propertyName, C values, YAMLSerializationContext ctx)
      throws YAMLSerializationException {
    if (!ctx.isWriteEmptyYAMLArrays() && isEmpty(values)) {
      writer.addScalarNode(propertyName, "~");
      return;
    }

    YamlSequence yamlSequence = writer.addSequenceNode(propertyName);
    for (T value : (Collection<T>) values) {
      serializer.serialize(yamlSequence, value, ctx);
    }
  }

  @Override
  public void serialize(YamlSequence writer, C value, YAMLSerializationContext ctx) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override
  protected boolean isEmpty(C value) {
    return null == value || value.isEmpty();
  }
}
