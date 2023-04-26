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

import java.util.AbstractSequentialList;
import java.util.LinkedList;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;

/**
 * Default {@link YAMLDeserializer} implementation for {@link java.util.AbstractSequentialList}. The
 * deserialization process returns a {@link LinkedList}.
 *
 * @param <T> Type of the elements inside the {@link java.util.AbstractSequentialList}
 * @author Nicolas Morel
 * @version $Id: $Id
 */
public class AbstractSequentialListYAMLDeserializer<T>
    extends BaseListYAMLDeserializer<AbstractSequentialList<T>, T> {

  /**
   * @param deserializer {@link YAMLDeserializer} used to deserialize the objects inside the {@link
   *     AbstractSequentialList}.
   */
  private AbstractSequentialListYAMLDeserializer(YAMLDeserializer<T> deserializer) {
    super(deserializer);
  }

  /**
   * newInstance.
   *
   * @param deserializer {@link YAMLDeserializer} used to deserialize the objects inside the {@link
   *     java.util.AbstractSequentialList}.
   * @param <T> Type of the elements inside the {@link java.util.AbstractSequentialList}
   * @return a new instance of {@link AbstractSequentialListYAMLDeserializer}
   */
  public static <T> AbstractSequentialListYAMLDeserializer<T> newInstance(
      YAMLDeserializer<T> deserializer) {
    return new AbstractSequentialListYAMLDeserializer<>(deserializer);
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractSequentialList<T> newCollection() {
    return new LinkedList<>();
  }
}
