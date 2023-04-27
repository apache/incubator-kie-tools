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

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;

/**
 * Default {@link YAMLDeserializer} implementation for {@link java.util.List}. The deserialization
 * process returns an {@link java.util.ArrayList}.
 *
 * @param <T> Type of the elements inside the {@link java.util.List}
 * @author Nicolas Morel
 * @version $Id: $
 */
public final class ListYAMLDeserializer<T> extends BaseListYAMLDeserializer<List<T>, T> {

  /**
   * @param deserializer {@link YAMLDeserializer} used to deserialize the objects inside the {@link
   *     List}.
   */
  public ListYAMLDeserializer(YAMLDeserializer<T> deserializer) {
    super(deserializer);
  }

  /**
   * newInstance
   *
   * @param deserializer {@link YAMLDeserializer} used to deserialize the objects inside the {@link
   *     java.util.List}.
   * @param <T> Type of the elements inside the {@link java.util.List}
   * @return a new instance of {@link ListYAMLDeserializer}
   */
  public static <T> ListYAMLDeserializer<T> newInstance(YAMLDeserializer<T> deserializer) {
    return new ListYAMLDeserializer<>(deserializer);
  }

  /** {@inheritDoc} */
  @Override
  protected List<T> newCollection() {
    return new ArrayList<>();
  }
}
