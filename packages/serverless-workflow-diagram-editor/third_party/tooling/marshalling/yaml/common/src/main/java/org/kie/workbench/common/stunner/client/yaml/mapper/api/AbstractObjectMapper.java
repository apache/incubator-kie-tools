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

package org.kie.workbench.common.stunner.client.yaml.mapper.api;

import java.io.IOException;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLSerializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.DefaultYAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.bean.AbstractBeanYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.bean.AbstractBeanYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLWriter;

public abstract class AbstractObjectMapper<T> {

  private YAMLDeserializer<T> deserializer;

  private YAMLSerializer<T> serializer;

  /** {@inheritDoc} */
  public T read(String in) throws YAMLDeserializationException, IOException {
    YAMLDeserializationContext context = DefaultYAMLDeserializationContext.builder().build();
    return read(in, context);
  }

  /** {@inheritDoc} */
  public T read(String in, YAMLDeserializationContext ctx)
      throws YAMLDeserializationException, IOException {
    YamlMapping reader = Yaml.createYamlInput(in).readYamlMapping();
    return ((AbstractBeanYAMLDeserializer<T>) getDeserializer()).deserializeInline(reader, ctx);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Getter for the field <code>deserializer</code>.
   */
  public YAMLDeserializer<T> getDeserializer() {
    if (null == deserializer) {
      deserializer = newDeserializer();
    }
    return deserializer;
  }

  /**
   * Instantiates a new deserializer
   *
   * @return a new deserializer
   */
  protected abstract YAMLDeserializer<T> newDeserializer();

  /** {@inheritDoc} */
  public String write(T value) throws YAMLSerializationException {
    YAMLSerializationContext yamlSerializationContext =
        DefaultYAMLSerializationContext.builder().build();
    return write(value, yamlSerializationContext);
  }

  /** {@inheritDoc} */
  public String write(T value, YAMLSerializationContext ctx) throws YAMLSerializationException {
    YAMLWriter writer = ctx.newYAMLWriter();
    try {
      ((AbstractBeanYAMLSerializer) getSerializer()).serializeInternally(writer, value, ctx);
      return writer.getOutput();
    } catch (YAMLSerializationException e) {
      throw new Error(e);
    } catch (Exception e) {
      throw new Error(e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Getter for the field <code>serializer</code>.
   */
  public YAMLSerializer<T> getSerializer() {
    if (null == serializer) {
      serializer = (YAMLSerializer<T>) newSerializer();
    }
    return serializer;
  }

  /**
   * Instantiates a new serializer
   *
   * @return a new serializer
   */
  protected abstract YAMLSerializer<?> newSerializer();
}
