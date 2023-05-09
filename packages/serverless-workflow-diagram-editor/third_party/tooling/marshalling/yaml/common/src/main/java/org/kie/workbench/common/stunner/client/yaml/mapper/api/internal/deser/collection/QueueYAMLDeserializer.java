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

import java.util.LinkedList;
import java.util.Queue;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;

/**
 * Default {@link YAMLDeserializer} implementation for {@link java.util.Queue}. The deserialization
 * process returns a {@link java.util.LinkedList}.
 *
 * @param <T> Type of the elements inside the {@link java.util.Queue}
 * @author Nicolas Morel
 * @version $Id: $
 */
public final class QueueYAMLDeserializer<T> extends BaseQueueYAMLDeserializer<Queue<T>, T> {

  /**
   * @param deserializer {@link YAMLDeserializer} used to deserialize the objects inside the {@link
   *     Queue}.
   */
  private QueueYAMLDeserializer(YAMLDeserializer<T> deserializer) {
    super(deserializer);
  }

  /**
   * newInstance
   *
   * @param deserializer {@link YAMLDeserializer} used to deserialize the objects inside the {@link
   *     java.util.Queue}.
   * @param <T> Type of the elements inside the {@link java.util.Queue}
   * @return a new instance of {@link QueueYAMLDeserializer}
   */
  public static <T> QueueYAMLDeserializer<T> newInstance(YAMLDeserializer<T> deserializer) {
    return new QueueYAMLDeserializer<>(deserializer);
  }

  /** {@inheritDoc} */
  @Override
  protected Queue<T> newCollection() {
    return new LinkedList<>();
  }
}
