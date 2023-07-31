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
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.CharacterYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;

/**
 * Default {@link YAMLDeserializer} implementation for array of char.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public class PrimitiveCharacterArrayYAMLDeserializer extends AbstractArrayYAMLDeserializer<char[]> {

  public static final PrimitiveCharacterArrayYAMLDeserializer INSTANCE =
      new PrimitiveCharacterArrayYAMLDeserializer();

  /** {@inheritDoc} */
  @Override
  public char[] doDeserializeArray(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    List<Character> list =
        deserializeIntoList(yaml.getSequenceNode(key), CharacterYAMLDeserializer.INSTANCE, ctx);

    char[] result = new char[list.size()];
    int i = 0;
    for (Character value : list) {
      if (null != value) {
        result[i] = value;
      }
      i++;
    }
    return result;
  }

  @Override
  public char[] deserialize(YamlNode node, YAMLDeserializationContext ctx) {
    List<Character> list =
        deserializeIntoList(node.asSequence(), CharacterYAMLDeserializer.INSTANCE, ctx);

    char[] result = new char[list.size()];
    int i = 0;
    for (Character value : list) {
      if (null != value) {
        result[i] = value;
      }
      i++;
    }
    return result;
  }
}
