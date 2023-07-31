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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.bean;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;

/**
 * Deserializes a bean's property
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public abstract class BeanPropertyDeserializer<T, V>
    extends HasDeserializerAndParameters<V, YAMLDeserializer<V>> {

  /**
   * setValue
   *
   * @param bean a T object.
   * @param value a V object.
   * @param ctx a {@link YAMLDeserializationContext} object.
   */
  public abstract void setValue(T bean, V value, YAMLDeserializationContext ctx);

  public void deserialize(YamlMapping yaml, String key, T bean, YAMLDeserializationContext ctx) {
    setValue(bean, deserialize(yaml, key, ctx), ctx);
  }
}
