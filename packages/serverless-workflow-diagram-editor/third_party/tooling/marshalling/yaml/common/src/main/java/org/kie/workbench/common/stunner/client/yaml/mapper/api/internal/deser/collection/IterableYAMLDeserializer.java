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

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;

/**
 * Default {@link YAMLDeserializer} implementation for {@link java.lang.Iterable}. The
 * deserialization process returns an {@link java.util.ArrayList}.
 *
 * @param <T> Type of the elements inside the {@link java.lang.Iterable}
 * @author Nicolas Morel
 * @version $Id: $
 */
public class IterableYAMLDeserializer<T> extends BaseIterableYAMLDeserializer<Iterable<T>, T> {

  /**
   * @param deserializer {@link YAMLDeserializer} used to deserialize the objects inside the {@link
   *     Iterable}.
   */
  private IterableYAMLDeserializer(YAMLDeserializer<T> deserializer) {
    super(deserializer);
  }

  /**
   * newInstance
   *
   * @param deserializer {@link YAMLDeserializer} used to deserialize the objects inside the {@link
   *     java.lang.Iterable}.
   * @param <T> Type of the elements inside the {@link java.lang.Iterable}
   * @return a new instance of {@link IterableYAMLDeserializer}
   */
  public static <T> IterableYAMLDeserializer<T> newInstance(YAMLDeserializer<T> deserializer) {
    return new IterableYAMLDeserializer<>(deserializer);
  }

  /** {@inheritDoc} */
  @Override
  public Iterable<T> deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterable<T> deserialize(YamlNode node, YAMLDeserializationContext ctx) {
    throw new UnsupportedOperationException();
  }
}
