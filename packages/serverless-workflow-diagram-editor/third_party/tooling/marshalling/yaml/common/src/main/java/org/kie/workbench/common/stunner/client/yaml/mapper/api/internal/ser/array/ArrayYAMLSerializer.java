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

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLSerializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.AbstractYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLSequenceWriter;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLWriter;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.impl.DefaultYAMLSequenceWriter;

/**
 * Default {@link AbstractYAMLSerializer} implementation for array.
 *
 * @param <T> Type of the elements inside the array
 * @author Nicolas Morel
 * @version $Id: $
 */
public class ArrayYAMLSerializer<T> extends AbstractYAMLSerializer<T[]> {

  private final YAMLSerializer<T> serializer;
  protected final String propertyName;

  /**
   * Constructor for ArrayYAMLSerializer.
   *
   * @param serializer {@link YAMLSerializer} used to serialize the objects inside the array.
   */
  public ArrayYAMLSerializer(YAMLSerializer<T> serializer, String propertyName) {
    if (null == serializer) {
      throw new IllegalArgumentException("serializer cannot be null");
    }
    if (null == propertyName) {
      throw new IllegalArgumentException("propertyName cannot be null");
    }
    this.serializer = serializer;
    this.propertyName = propertyName;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean isEmpty(T[] value) {
    return null == value || value.length == 0;
  }

  @Override
  public void serialize(YAMLWriter writer, T[] values, YAMLSerializationContext ctx)
      throws YAMLSerializationException {
    throw new RuntimeException("Not implemented");
  }

  /** {@inheritDoc} */
  @Override
  public void serialize(
      YAMLWriter writer, String propertyName, T[] values, YAMLSerializationContext ctx) {
    if (!ctx.isWriteEmptyYAMLArrays() && isEmpty(values)) {
      writer.nullValue(propertyName);
      return;
    }
    YAMLSequenceWriter yamlSequenceWriter = new DefaultYAMLSequenceWriter();

    for (T value : values) {
      serializer.serialize(yamlSequenceWriter, value, ctx);
    }
    writer.value(propertyName, yamlSequenceWriter.getWriter());
  }

  @Override
  public void serialize(YAMLSequenceWriter writer, T[] value, YAMLSerializationContext ctx) {
    throw new RuntimeException("Not implemented");
  }
}
