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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.array;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.AbstractYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BaseNumberYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;

/**
 * Default {@link AbstractYAMLSerializer} implementation for array of float.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public class PrimitiveFloatArrayYAMLSerializer extends BasicArrayYAMLSerializer<float[]> {

  public static final PrimitiveFloatArrayYAMLSerializer INSTANCE =
      new PrimitiveFloatArrayYAMLSerializer();

  private final BaseNumberYAMLSerializer.FloatYAMLSerializer serializer =
      BaseNumberYAMLSerializer.FloatYAMLSerializer.INSTANCE;

  @Override
  protected boolean isEmpty(float[] value) {
    return null == value || value.length == 0;
  }

  /** {@inheritDoc} */
  @Override
  public void serialize(
      YamlMapping writer, String propertyName, float[] values, YAMLSerializationContext ctx) {
    if (!ctx.isWriteEmptyYAMLArrays() && values.length == 0) {
      writer.addScalarNode(propertyName, null);
      return;
    }
    YamlSequence yamlSequence = writer.addSequenceNode(propertyName);
    for (float value : values) {
      serializer.serialize(yamlSequence, value, ctx);
    }
  }

  @Override
  public void serialize(YamlSequence writer, float[] value, YAMLSerializationContext ctx) {
    for (float o : value) {
      serializer.serialize(writer, o, ctx);
    }
  }
}
