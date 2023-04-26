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

import com.amihaiemil.eoyaml.Node;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;

public class YamlSubtypeDeserializer<T> implements YAMLDeserializer<T> {

  private final Map<String, YamlSubtypeDeserializer.Info> types = new HashMap<>();

  private final String typeFieldName;

  public YamlSubtypeDeserializer(String typeFieldName, YamlSubtypeDeserializer.Info... infos) {
    this.typeFieldName = typeFieldName;
    for (YamlSubtypeDeserializer.Info info : infos) {
      types.put(info.alias, info);
    }
  }

  @Override
  public T deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx)
      throws YAMLDeserializationException {
    if (yaml.value(key).type() == Node.MAPPING
        && yaml.value(key).asMapping().string(typeFieldName) != null) {
      String type = yaml.value(key).asMapping().string(typeFieldName);
      AbstractBeanYAMLDeserializer deser = (AbstractBeanYAMLDeserializer) types.get(type).deser;
      return (T) deser.deserialize(yaml.value(key).asMapping(), ctx);
    }
    throw new YAMLDeserializationException("Unable to find deserializer for " + yaml);
  }

  @Override
  public T deserialize(YamlNode node, YAMLDeserializationContext ctx) {
    if (node.type() == Node.MAPPING && node.asMapping().string(typeFieldName) != null) {
      String type = node.asMapping().string(typeFieldName);
      AbstractBeanYAMLDeserializer deser = (AbstractBeanYAMLDeserializer) types.get(type).deser;
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
