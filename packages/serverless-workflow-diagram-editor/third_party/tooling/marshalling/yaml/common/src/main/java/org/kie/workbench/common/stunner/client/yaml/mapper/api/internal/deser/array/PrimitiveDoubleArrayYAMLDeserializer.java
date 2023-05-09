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

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseNumberYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;

/**
 * Default {@link YAMLDeserializer} implementation for array of double.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public class PrimitiveDoubleArrayYAMLDeserializer extends AbstractArrayYAMLDeserializer<double[]> {

  public static final PrimitiveDoubleArrayYAMLDeserializer INSTANCE =
      new PrimitiveDoubleArrayYAMLDeserializer();

  /** {@inheritDoc} */
  @Override
  public double[] doDeserializeArray(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    List<Double> list =
        deserializeIntoList(
            yaml.yamlSequence(key),
            BaseNumberYAMLDeserializer.DoubleYAMLDeserializer.INSTANCE,
            ctx);

    double[] result = new double[list.size()];
    int i = 0;
    for (Double value : list) {
      if (null != value) {
        result[i] = value;
      }
      i++;
    }
    return result;
  }

  @Override
  public double[] deserialize(YamlNode node, YAMLDeserializationContext ctx) {
    List<Double> list =
        deserializeIntoList(
            node.asSequence(), BaseNumberYAMLDeserializer.DoubleYAMLDeserializer.INSTANCE, ctx);

    double[] result = new double[list.size()];
    int i = 0;
    for (Double value : list) {
      if (null != value) {
        result[i] = value;
      }
      i++;
    }
    return result;
  }
}
