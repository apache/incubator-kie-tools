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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.bean.AbstractBeanYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;

/**
 * Base {@link YAMLDeserializer} implementation for {@link java.util.Collection}.
 *
 * @param <C> {@link java.util.Collection} type
 * @param <T> Type of the elements inside the {@link java.util.Collection}
 * @author Nicolas Morel
 * @version $Id: $
 */
public abstract class BaseCollectionYAMLDeserializer<C extends Collection<T>, T>
    extends BaseIterableYAMLDeserializer<C, T> {

  /**
   * Constructor for BaseCollectionYAMLDeserializer.
   *
   * @param deserializer {@link YAMLDeserializer} used to map the objects inside the {@link
   *     java.util.Collection}.
   */
  public BaseCollectionYAMLDeserializer(YAMLDeserializer<T> deserializer) {
    super(deserializer);
  }

  /** {@inheritDoc} */
  @Override
  public C deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    return deserialize(yaml.getSequenceNode(key), ctx);
  }

  protected C deserialize(YamlSequence sequence, YAMLDeserializationContext ctx) {
    if (sequence == null) {
      return null;
    }
    List<T> list = new ArrayList<>();
    if (deserializer instanceof AbstractBeanYAMLDeserializer) {
      for (int i = 0; i < sequence.size(); i++) {
        list.add(
            ((AbstractBeanYAMLDeserializer<T>) deserializer).deserialize(sequence.mapping(i), ctx));
      }
    } else {
      Iterator<YamlNode> iterator = sequence.iterator();
      while (iterator.hasNext()) {
        list.add(deserializer.deserialize(iterator.next(), ctx));
      }
    }
    return (C) list;
  }

  @Override
  public C deserialize(YamlNode node, YAMLDeserializationContext ctx) {
    C result = newCollection();
    Collection<T> temp = deserialize(node.asSequence(), ctx);
    for (T val : temp) {
      result.add(val);
    }
    return result;
  }

  /**
   * Instantiates a new collection for deserialization process.
   *
   * @return the new collection
   */
  protected abstract C newCollection();

  /**
   * isNullValueAllowed
   *
   * @return true if the collection accepts null value
   */
  protected boolean isNullValueAllowed() {
    return true;
  }
}
