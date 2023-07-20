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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.array;

import java.util.List;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseNumberYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;

/**
 * Default {@link YAMLDeserializer} implementation for array of int.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public class PrimitiveIntegerArrayYAMLDeserializer extends AbstractArrayYAMLDeserializer<int[]> {

  public static final PrimitiveIntegerArrayYAMLDeserializer INSTANCE =
      new PrimitiveIntegerArrayYAMLDeserializer();

  /** {@inheritDoc} */
  @Override
  public int[] doDeserializeArray(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    List<Integer> list =
        deserializeIntoList(
            yaml.getSequenceNode(key),
            BaseNumberYAMLDeserializer.IntegerYAMLDeserializer.INSTANCE,
            ctx);

    int[] result = new int[list.size()];
    int i = 0;
    for (Integer value : list) {
      if (null != value) {
        result[i] = value;
      }
      i++;
    }
    return result;
  }

  @Override
  public int[] deserialize(YamlNode node, YAMLDeserializationContext ctx) {
    List<Integer> list =
        deserializeIntoList(
            node.asSequence(), BaseNumberYAMLDeserializer.IntegerYAMLDeserializer.INSTANCE, ctx);

    int[] result = new int[list.size()];
    int i = 0;
    for (Integer value : list) {
      if (null != value) {
        result[i] = value;
      }
      i++;
    }
    return result;
  }
}
