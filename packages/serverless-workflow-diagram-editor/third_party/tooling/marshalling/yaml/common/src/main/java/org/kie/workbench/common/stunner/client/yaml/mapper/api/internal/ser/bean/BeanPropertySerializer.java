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

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;

/**
 * Serializes a bean's property
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public abstract class BeanPropertySerializer<T, V> extends HasSerializer<V, YAMLSerializer<V>> {

  protected String propertyName;

  /**
   * Constructor for BeanPropertySerializer.
   *
   * @param propertyName a {@link String} object.
   */
  protected BeanPropertySerializer(String propertyName) {
    this.propertyName = propertyName;
  }

  /**
   * Getter for the field <code>propertyName</code>.
   *
   * @return a {@link String} object.
   */
  public String getPropertyName() {
    return propertyName;
  }

  /**
   * Serializes the property defined for this instance.
   *
   * @param writer writer
   * @param bean bean containing the property to serialize
   * @param ctx context of the serialization process
   */
  public void serialize(YamlMapping writer, T bean, YAMLSerializationContext ctx) {
    getSerializer((V) bean.getClass()).serialize(writer, propertyName, getValue(bean, ctx), ctx);
  }

  public void serialize(
      YamlMapping writer, String propertyName, T value, YAMLSerializationContext ctx) {
    getSerializer((V) value.getClass()).serialize(writer, propertyName, getValue(value, ctx), ctx);
  }

  public boolean isAbstractBeanYAMLSerializer(T bean) {
    return getSerializer((V) bean.getClass()) instanceof AbstractBeanYAMLSerializer;
  }

  /**
   * getValue
   *
   * @param bean bean containing the property to serialize
   * @param ctx context of the serialization process
   * @return the property's value
   */
  public abstract V getValue(T bean, YAMLSerializationContext ctx);
}
