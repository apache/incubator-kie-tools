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

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.NodeType;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlScalar;

public class YamlSubtypeDeserializer<T> implements YAMLDeserializer<T> {

  private final Map<String, YamlSubtypeDeserializer.Info> types = new HashMap<>();

  private final String typeFieldName;

  public YamlSubtypeDeserializer(String typeFieldName, YamlSubtypeDeserializer.Info... infos) {
    this.typeFieldName = typeFieldName;
    for (YamlSubtypeDeserializer.Info info : infos) {
      types.put(info.alias, info);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public T deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx)
      throws YAMLDeserializationException {
    if (yaml.getNode(key).type() == NodeType.MAPPING
        && yaml.getNode(key).asMapping().getScalarNode(typeFieldName) != null) {
      YamlScalar<String> scalarNode = yaml.getNode(key).asMapping().getScalarNode(typeFieldName);
      AbstractBeanYAMLDeserializer<T> deser =
          (AbstractBeanYAMLDeserializer<T>) types.get(scalarNode.value()).deser;
      return (T) deser.deserialize(yaml.getNode(key).asMapping(), ctx);
    }
    throw new YAMLDeserializationException("Unable to find deserializer for " + yaml);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T deserialize(YamlNode node, YAMLDeserializationContext ctx) {
    if (node.type() == NodeType.MAPPING && node.asMapping().getScalarNode(typeFieldName) != null) {
      YamlScalar<String> scalarNode = node.asMapping().getScalarNode(typeFieldName);
      AbstractBeanYAMLDeserializer<T> deser =
          (AbstractBeanYAMLDeserializer<T>) types.get(scalarNode.value()).deser;
      return (T) deser.deserialize(node.asMapping(), ctx);
    }
    throw new YAMLDeserializationException("Unable to find deserializer for " + node);
  }

  public static class Info {

    private final String alias;
    private final Class clazz;

    private final YAMLDeserializer deser;

    public Info(String alias, Class clazz, YAMLDeserializer deser) {
      this.alias = alias;
      this.clazz = clazz;
      this.deser = deser;
    }
  }
}
