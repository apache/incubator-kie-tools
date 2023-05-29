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
 * Default {@link YAMLDeserializer} implementation for array of float.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public class PrimitiveFloatArrayYAMLDeserializer extends AbstractArrayYAMLDeserializer<float[]> {

  public static final PrimitiveFloatArrayYAMLDeserializer INSTANCE =
      new PrimitiveFloatArrayYAMLDeserializer();

  /** {@inheritDoc} */
  @Override
  public float[] doDeserializeArray(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    List<Float> list =
        deserializeIntoList(
            yaml.getSequenceNode(key),
            BaseNumberYAMLDeserializer.FloatYAMLDeserializer.INSTANCE,
            ctx);

    float[] result = new float[list.size()];
    int i = 0;
    for (Float value : list) {
      if (null != value) {
        result[i] = value;
      }
      i++;
    }
    return result;
  }

  @Override
  public float[] deserialize(YamlNode node, YAMLDeserializationContext ctx) {
    List<Float> list =
        deserializeIntoList(
            node.asSequence(), BaseNumberYAMLDeserializer.FloatYAMLDeserializer.INSTANCE, ctx);

    float[] result = new float[list.size()];
    int i = 0;
    for (Float value : list) {
      if (null != value) {
        result[i] = value;
      }
      i++;
    }
    return result;
  }
}
