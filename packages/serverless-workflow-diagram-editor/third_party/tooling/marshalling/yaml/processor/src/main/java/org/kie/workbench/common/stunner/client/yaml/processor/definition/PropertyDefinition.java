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

package org.kie.workbench.common.stunner.client.yaml.processor.definition;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlProperty;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YamlTypeSerializerWrapper;
import org.kie.workbench.common.stunner.client.yaml.processor.context.GenerationContext;

/** @author Dmitrii Tikhomirov Created by treblereel 4/1/20 */
public class PropertyDefinition extends Definition {

  private final VariableElement property;

  protected PropertyDefinition(VariableElement property, GenerationContext context) {
    super(property.asType(), context);
    this.property = property;
  }

  public Expression getFieldDeserializer(CompilationUnit cu) {

    if (!(context.getTypeUtils().isIterable(bean) || bean.getKind().equals(TypeKind.ARRAY))) {
      YamlTypeDeserializer deserializer = property.getAnnotation(YamlTypeDeserializer.class);
      if (deserializer == null) {
        TypeMirror typeMirror = bean;
        if (!typeMirror.getKind().isPrimitive()) {
          if (MoreTypes.asTypeElement(typeMirror).getAnnotation(YamlTypeDeserializer.class)
              != null) {
            deserializer =
                MoreTypes.asTypeElement(typeMirror).getAnnotation(YamlTypeDeserializer.class);
          }
        }
      }

      if (deserializer != null) {
        return getFieldYamlTypeDeserializerCreationExpr(deserializer);
      }
    }

    TypeMirror asInterface = maybeInterface();
    FieldDefinition fieldDefinition =
        propertyDefinitionFactory.getFieldDefinition(asInterface != null ? asInterface : bean);
    Expression result = fieldDefinition.getFieldDeserializer(this, cu);
    return result;
  }

  private TypeMirror maybeInterface() {
    if (!getBean().getKind().equals(TypeKind.ARRAY)
        && !getBean().getKind().isPrimitive()
        && MoreTypes.isType(getBean())) {
      if (MoreTypes.asElement(getBean()).getKind().isInterface()
          || (MoreTypes.asElement(getBean()).getKind().isClass()
              && MoreTypes.asElement(getBean()).getModifiers().contains(Modifier.ABSTRACT))) {
        return context.getBeans().stream()
            .filter(v -> v.getElement().equals(MoreTypes.asTypeElement(getBean())))
            .findFirst()
            .map(v -> v.getBean())
            .orElse(null);
      }
    }
    return null;
  }

  public Expression getFieldSerializer(CompilationUnit cu) {
    if (!(context.getTypeUtils().isIterable(bean) || bean.getKind().equals(TypeKind.ARRAY))) {
      YamlTypeSerializer serializer = property.getAnnotation(YamlTypeSerializer.class);
      if (serializer == null) {
        TypeMirror typeMirror = bean;
        if (!typeMirror.getKind().isPrimitive()) {
          if (MoreTypes.asTypeElement(typeMirror).getAnnotation(YamlTypeSerializer.class) != null) {
            serializer =
                MoreTypes.asTypeElement(typeMirror).getAnnotation(YamlTypeSerializer.class);
          }
        }
      }

      if (serializer != null) {
        return getFieldYamlTypeSerializerCreationExpr(serializer);
      }
    }

    TypeMirror bean = maybeInterface();
    FieldDefinition fieldDefinition =
        propertyDefinitionFactory.getFieldDefinition(bean != null ? bean : getBean());
    return fieldDefinition.getFieldSerializer(this, cu);
  }

  public String getPropertyName() {
    if (property.getAnnotation(YamlProperty.class) != null
        && !property.getAnnotation(YamlProperty.class).value().isEmpty()) {
      return property.getAnnotation(YamlProperty.class).value();
    }
    return property.getSimpleName().toString();
  }

  public VariableElement getProperty() {
    return property;
  }

  public boolean hasYamlTypeSerializer() {
    return property.getAnnotation(YamlTypeSerializer.class) != null;
  }

  public boolean hasYamlTypeDeserializer() {
    return property.getAnnotation(YamlTypeDeserializer.class) != null;
  }

  public Expression getFieldYamlTypeDeserializerCreationExpr(YamlTypeDeserializer deserializer) {
    try {
      deserializer.value();
    } catch (MirroredTypeException e) {
      ClassOrInterfaceType type = new ClassOrInterfaceType();
      type.setName(e.getTypeMirror().toString());
      return new ObjectCreationExpr().setType(type);
    }
    return null;
  }

  public Expression getFieldYamlTypeSerializerCreationExpr(YamlTypeSerializer serializer) {
    try {
      serializer.value();
    } catch (MirroredTypeException e) {
      ClassOrInterfaceType type = new ClassOrInterfaceType();
      type.setName(e.getTypeMirror().toString());
      return new ObjectCreationExpr()
          .setType(YamlTypeSerializerWrapper.class)
          .addArgument(new ObjectCreationExpr().setType(type));
    }
    return null;
  }
}
