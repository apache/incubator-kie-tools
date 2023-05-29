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
 * Lazy initialize a {@link YAMLDeserializer}
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public abstract class HasDeserializerAndParameters<V, S extends YAMLDeserializer<V>>
    extends HasDeserializer<V, S> {

  /**
   * Deserializes the property defined for this instance.
   *
   * @param yaml YamlMapping
   * @param key String
   * @param ctx context of the deserialization process
   * @return a V object.
   */
  public V deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    return getDeserializer().deserialize(yaml, key, ctx);
  }
}
