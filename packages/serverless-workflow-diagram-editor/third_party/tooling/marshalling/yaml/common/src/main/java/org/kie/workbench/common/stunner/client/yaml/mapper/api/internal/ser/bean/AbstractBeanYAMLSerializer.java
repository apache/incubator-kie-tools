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

import java.util.Map;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.AbstractYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;

/**
 * Base implementation of {@link AbstractYAMLSerializer} for beans.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public abstract class AbstractBeanYAMLSerializer<T> extends AbstractYAMLSerializer<T>
    implements InternalSerializer<T> {

  protected BeanPropertySerializer[] serializers;

  /** Constructor for AbstractBeanYAMLSerializer. */
  protected AbstractBeanYAMLSerializer() {
    this.serializers = initSerializers();
  }

  /**
   * Initialize the {@link Map} containing the property serializers. Returns an empty map if there
   * are no properties to serialize.
   *
   * @return an array of {@link BeanPropertySerializer} objects.
   */
  protected BeanPropertySerializer[] initSerializers() {
    return new BeanPropertySerializer[0];
  }

  /** {@inheritDoc} */
  @Override
  public void doSerialize(YamlMapping writer, T value, YAMLSerializationContext ctx) {
    serializeInternally(writer, value, ctx);
  }

  /**
   * getSerializedType
   *
   * @return a {@link Class} object.
   */
  public abstract Class getSerializedType();

  /** {@inheritDoc} */
  public void serializeInternally(YamlMapping writer, T value, YAMLSerializationContext ctx) {
    serializeObject(writer, value, ctx);
  }

  /**
   * Serializes all the properties of the bean in a json object.
   *
   * @param writer writer
   * @param value bean to serialize
   * @param ctx context of the serialization process
   */
  private void serializeObject(YamlMapping writer, T value, YAMLSerializationContext ctx) {
    serializeObject(writer, value, ctx, getSerializeObjectName());
  }

  /**
   * Serializes all the properties of the bean in a json object.
   *
   * @param writer writer
   * @param value bean to serialize
   * @param ctx context of the serialization process
   * @param typeName in case of type info as property, the name of the property
   */
  protected void serializeObject(
      YamlMapping writer, T value, YAMLSerializationContext ctx, String typeName) {
    if (value == null && !ctx.isSerializeNulls()) {
      return;
    }

    serializeProperties(writer, value, ctx);
  }

  private String getSerializeObjectName() {
    return getSerializedType().getSimpleName();
  }

  private void serializeProperties(YamlMapping writer, T value, YAMLSerializationContext ctx) {

    for (BeanPropertySerializer<T, ?> propertySerializer : serializers) {
      if (propertySerializer.getValue(value, ctx) == null && !ctx.isSerializeNulls()) {
        continue;
      }
      if (propertySerializer.isAbstractBeanYAMLSerializer(value)) {
        propertySerializer.serialize(writer, propertySerializer.getPropertyName(), value, ctx);
      } else {
        propertySerializer.serialize(writer, value, ctx);
      }
    }
  }

  @Override
  public void serialize(YamlSequence writer, T value, YAMLSerializationContext ctx) {
    YamlMapping objWriter = writer.addMappingNode();
    serializeObject(objWriter, value, ctx);
  }
}
