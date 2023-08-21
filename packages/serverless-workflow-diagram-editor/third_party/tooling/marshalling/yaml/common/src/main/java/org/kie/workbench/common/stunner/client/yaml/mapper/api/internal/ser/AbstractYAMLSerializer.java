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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLSerializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;

/**
 * Base class for all the serializer. It handles null values and exceptions. The rest is delegated
 * to implementations.
 *
 * @author Nicolas Morel
 * @version $Id: $Id
 */
public abstract class AbstractYAMLSerializer<T> implements YAMLSerializer<T> {

  public void serialize(YamlMapping writer, T value, YAMLSerializationContext ctx)
      throws YAMLSerializationException {
    if (null == value) {
      if (ctx.isSerializeNulls()) {
        serializeNullValue(writer, ctx);
      }
    } else {
      doSerialize(writer, value, ctx);
    }
  }

  @Override
  public void serialize(
      YamlMapping writer, String propertyName, T value, YAMLSerializationContext ctx) {
    YamlMapping childWriter = writer.addMappingNode(propertyName);
    serialize(childWriter, value, ctx);
  }

  /**
   * Serialize the null value. This method allows children to override the default behaviour.
   *
   * @param writer {@link YamlMapping} used to write the serialized YAML
   * @param ctx Context for the full serialization process
   */
  protected void serializeNullValue(YamlMapping writer, YAMLSerializationContext ctx) {
    // writer.nullValue(propertyName); //TODO
  }

  /**
   * Serializes a non-null object into YAML output.
   *
   * @param writer {@link YamlMapping} used to write the serialized YAML
   * @param value Object to serialize
   * @param ctx Context for the full serialization process
   */
  protected void doSerialize(YamlMapping writer, T value, YAMLSerializationContext ctx) {
    throw new RuntimeException("Not implemented");
  }

  /**
   * isEmpty.
   *
   * @param value the value
   * @return true if the value is empty
   */
  protected boolean isEmpty(T value) {
    return null == value;
  }
}
