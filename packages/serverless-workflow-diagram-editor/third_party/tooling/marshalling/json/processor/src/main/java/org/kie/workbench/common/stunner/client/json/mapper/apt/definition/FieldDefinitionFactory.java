/*
 * Copyright © 2022 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.json.mapper.apt.definition;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;

import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;
import org.kie.workbench.common.stunner.client.json.mapper.apt.exception.GenerationException;
import org.kie.workbench.common.stunner.client.json.mapper.apt.utils.TypeUtils;

public class FieldDefinitionFactory {

  private final GenerationContext context;
  private final TypeUtils typeUtils;
  private final Map<TypeMirror, FieldDefinition> holder = new HashMap<>();

  public FieldDefinitionFactory(GenerationContext context) {
    this.context = context;
    this.typeUtils = context.getTypeUtils();
  }

  private FieldDefinition getFieldDefinition(TypeMirror type) {
    TypeMirror property = context.getProcessingEnv().getTypeUtils().erasure(type);
    FieldDefinition result = null;

    if (holder.containsKey(property)) {
      result = holder.get(property);
    } else if (typeUtils.isSimpleType(property)) {
      result = new BasicTypeFieldDefinition(property, context);
    } else if (type.getKind().equals(TypeKind.ARRAY)) {
      result = new ArrayBeanFieldDefinition(property, context);
    } else if (context.getTypeUtils().isIterable(property)) {
      result = new CollectionsFieldDefinition(property, context);
    } else {
      result = new DefaultBeanFieldDefinition(property, context);
    }

    if (result == null) {
      throw new GenerationException("Unsupported type: " + type);
    }

    holder.put(property, result);
    return result;
  }

  public FieldDefinition getFieldDefinition(PropertyDefinition propertyDefinition) {
    JsonbTypeSerializer jsonbTypeSerializer =
        propertyDefinition.getVariableElement().getAnnotation(JsonbTypeSerializer.class);
    JsonbTypeDeserializer jsonbTypeDeserializer =
        propertyDefinition.getVariableElement().getAnnotation(JsonbTypeDeserializer.class);
    if (jsonbTypeSerializer != null || jsonbTypeDeserializer != null) {
      if (jsonbTypeSerializer == null || jsonbTypeDeserializer == null) {
        throw new GenerationException(
            "@JsonbTypeSerializer and @JsonbTypeDeserializer MUST be used together");
      }
      return new JsonbTypeSerFieldDefinition(propertyDefinition.getType(), context);
    }
    return getFieldDefinition(propertyDefinition.getType());
  }
}
