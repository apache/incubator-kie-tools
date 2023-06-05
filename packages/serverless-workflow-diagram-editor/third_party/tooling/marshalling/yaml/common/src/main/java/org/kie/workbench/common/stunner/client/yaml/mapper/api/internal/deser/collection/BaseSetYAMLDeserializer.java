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

import java.util.Collection;
import java.util.Set;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;

/**
 * Base {@link YAMLDeserializer} implementation for {@link java.util.Set}.
 *
 * @param <S> {@link java.util.Set} type
 * @param <T> Type of the elements inside the {@link java.util.Set}
 * @author Nicolas Morel
 * @version $Id: $
 */
public abstract class BaseSetYAMLDeserializer<S extends Set<T>, T>
    extends BaseCollectionYAMLDeserializer<S, T> {

  /**
   * Constructor for BaseSetYAMLDeserializer.
   *
   * @param deserializer {@link YAMLDeserializer} used to map the objects inside the {@link
   *     java.util.Set}.
   */
  public BaseSetYAMLDeserializer(YAMLDeserializer<T> deserializer) {
    super(deserializer);
  }

  @Override
  public S deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    S result = newCollection();
    Collection<T> temp = super.deserialize(yaml, key, ctx);
    for (T val : temp) {
      result.add(val);
    }
    return result;
  }
}
