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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.bean;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.StringYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;

public class YamlSubtypeSerializer<T> implements YAMLSerializer<T> {
  private final Map<Class, Info> types = new HashMap<>();

  private final StringYAMLSerializer stringSerializer = new StringYAMLSerializer();

  private final String typeFieldName;

  public YamlSubtypeSerializer(String typeFieldName, Info... infos) {
    this.typeFieldName = typeFieldName;
    for (Info info : infos) {
      types.put(info.clazz, info);
    }
  }

  @Override
  public void serialize(
      YamlMapping writer, String propertyName, T value, YAMLSerializationContext ctx) {
    if (value == null) {
      return;
    }
    if (types.containsKey(value.getClass())) {
      Info info = types.get(value.getClass());
      AbstractBeanYAMLSerializer<T> serializer = (AbstractBeanYAMLSerializer) info.ser;

      YamlMapping objWriter = writer.addMappingNode(propertyName);
      stringSerializer.serialize(objWriter, typeFieldName, info.alias, ctx);
      serializer.serialize(objWriter, value, ctx);
    } else {
      throw new Error("Unable to find ser for " + value.getClass());
    }
  }

  // TODO remove code dups
  @Override
  public void serialize(YamlSequence writer, T value, YAMLSerializationContext ctx) {
    if (value == null) {
      return;
    }
    if (types.containsKey(value.getClass())) {
      Info info = types.get(value.getClass());
      AbstractBeanYAMLSerializer<T> serializer = (AbstractBeanYAMLSerializer<T>) info.ser;
      new InnerWrapper(serializer, info.alias).serialize(writer, value, ctx);

    } else {
      throw new Error("Unable to find ser for " + value.getClass());
    }
  }

  // TODO maybe it can be done better
  private class InnerWrapper<T> extends AbstractBeanYAMLSerializer<T> {

    private final AbstractBeanYAMLSerializer<T> serializer;

    private InnerWrapper(AbstractBeanYAMLSerializer<T> serializer, String alias) {
      this.serializer = serializer;
      this.serializers = new BeanPropertySerializer[serializer.serializers.length + 1];
      this.serializers[0] =
          new BeanPropertySerializer<T, String>(typeFieldName) {

            @Override
            protected YAMLSerializer<?> newSerializer() {
              return new StringYAMLSerializer();
            }

            @Override
            public String getValue(T bean, YAMLSerializationContext ctx) {
              return alias;
            }
          };

      for (int i = 1; i <= serializer.serializers.length; i++) {
        this.serializers[i] = serializer.serializers[i - 1];
      }
    }

    @Override
    public Class getSerializedType() {
      return serializer.getSerializedType();
    }
  }

  public static class Info {

    private final String alias;
    private final Class clazz;

    private final YAMLSerializer ser;

    public Info(String alias, Class clazz, YAMLSerializer ser) {
      this.alias = alias;
      this.clazz = clazz;
      this.ser = ser;
    }
  }
}
