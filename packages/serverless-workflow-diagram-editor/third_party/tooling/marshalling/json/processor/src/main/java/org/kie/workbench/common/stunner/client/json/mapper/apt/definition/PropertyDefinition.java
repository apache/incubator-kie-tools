/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kie.workbench.common.stunner.client.json.mapper.apt.definition;

import java.util.Objects;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import jakarta.json.bind.annotation.JsonbProperty;
import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;

public class PropertyDefinition {

  private final VariableElement property;
  private final GenerationContext context;

  protected PropertyDefinition(VariableElement property, GenerationContext context) {
    this.property = property;
    this.context = context;
  }

  public String getName() {
    if (property.getAnnotation(JsonbProperty.class) != null
            && !property.getAnnotation(JsonbProperty.class).value().isEmpty()) {
      return property.getAnnotation(JsonbProperty.class).value();
    }
    return property.getSimpleName().toString();
  }

  public TypeMirror getType() {
    return property.asType();
  }

  public ExecutableElement getGetter() {
    return context.getTypeUtils().getGetter(property);
  }

  public ExecutableElement getSetter() {
    return context.getTypeUtils().getSetter(property);
  }

  public VariableElement getVariableElement() {
    return property;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PropertyDefinition that = (PropertyDefinition) o;
    return Objects.equals(property, that.property);
  }

  @Override
  public int hashCode() {
    return Objects.hash(property);
  }
}
