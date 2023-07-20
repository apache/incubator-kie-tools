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
 * Default {@link YAMLDeserializer} implementation for array of long.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public class PrimitiveLongArrayYAMLDeserializer extends AbstractArrayYAMLDeserializer<long[]> {

  public static final PrimitiveLongArrayYAMLDeserializer INSTANCE =
      new PrimitiveLongArrayYAMLDeserializer();

  /** {@inheritDoc} */
  @Override
  public long[] doDeserializeArray(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    List<Long> list =
        deserializeIntoList(
            yaml.getSequenceNode(key),
            BaseNumberYAMLDeserializer.LongYAMLDeserializer.INSTANCE,
            ctx);

    long[] result = new long[list.size()];
    int i = 0;
    for (Long value : list) {
      if (null != value) {
        result[i] = value;
      }
      i++;
    }
    return result;
  }

  @Override
  public long[] deserialize(YamlNode node, YAMLDeserializationContext ctx) {
    List<Long> list =
        deserializeIntoList(
            node.asSequence(), BaseNumberYAMLDeserializer.LongYAMLDeserializer.INSTANCE, ctx);

    long[] result = new long[list.size()];
    int i = 0;
    for (Long value : list) {
      if (null != value) {
        result[i] = value;
      }
      i++;
    }
    return result;
  }
}
