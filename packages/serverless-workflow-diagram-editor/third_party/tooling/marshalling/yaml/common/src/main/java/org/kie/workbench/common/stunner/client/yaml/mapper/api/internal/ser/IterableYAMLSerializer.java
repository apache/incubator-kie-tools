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

import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLSerializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;

/**
 * Default {@link AbstractYAMLSerializer} implementation for {@link Iterable}.
 *
 * @param <T> Type of the elements inside the {@link Iterable}
 * @author Nicolas Morel
 * @version $Id: $
 */
public class IterableYAMLSerializer<I extends Iterable<T>, T> extends AbstractYAMLSerializer<I> {

  protected final AbstractYAMLSerializer<T> serializer;

  /**
   * Constructor for IterableYAMLSerializer.
   *
   * @param serializer {@link AbstractYAMLSerializer} used to serialize the objects inside the
   *     {@link Iterable}.
   */
  protected IterableYAMLSerializer(AbstractYAMLSerializer<T> serializer) {
    if (null == serializer) {
      throw new IllegalArgumentException("serializer cannot be null");
    }
    this.serializer = serializer;
  }

  /**
   * newInstance
   *
   * @param serializer {@link AbstractYAMLSerializer} used to serialize the objects inside the
   *     {@link Iterable}
   * @param <I> Type of the {@link Iterable}
   * @return a new instance of {@link IterableYAMLSerializer}
   */
  public static <I extends Iterable<?>> IterableYAMLSerializer<I, ?> newInstance(
      AbstractYAMLSerializer<?> serializer) {
    return new IterableYAMLSerializer(serializer);
  }

  /** {@inheritDoc} */
  @Override
  protected boolean isEmpty(I value) {
    return null == value || !value.iterator().hasNext();
  }

  /** {@inheritDoc} */
  @Override
  public void doSerialize(YamlMapping writer, I values, YAMLSerializationContext ctx) {
    throw new YAMLSerializationException("Not implemented");
  }

  @Override
  public void serialize(YamlSequence writer, I value, YAMLSerializationContext ctx) {
    throw new YAMLSerializationException("Not implemented");
  }
}
