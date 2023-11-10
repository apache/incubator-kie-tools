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

package org.kie.workbench.common.stunner.client.yaml.processor.definition;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.array.ArrayYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YamlTypeSerializerWrapper;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.array.ArrayYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.processor.context.GenerationContext;

public class ArrayBeanFieldDefinition extends FieldDefinition {

  protected ArrayBeanFieldDefinition(TypeMirror property, GenerationContext context) {
    super(property, context);
  }

  @Override
  public Expression getFieldDeserializer(PropertyDefinition field, CompilationUnit cu) {
    cu.addImport(ArrayYAMLDeserializer.ArrayCreator.class);
    cu.addImport(ArrayYAMLDeserializer.class);

    ArrayType array = (ArrayType) bean;
    String arrayType = array.getComponentType().toString();
    if (array.getComponentType().getKind().isPrimitive()) {
      arrayType =
          context
              .getProcessingEnv()
              .getTypeUtils()
              .boxedClass((PrimitiveType) array.getComponentType())
              .getSimpleName()
              .toString();
    } else if (array.getComponentType().getKind().equals(TypeKind.ARRAY)) {
      ArrayType array2d = (ArrayType) array.getComponentType();
      if (array2d.getComponentType().getKind().isPrimitive()) {
        arrayType =
            context
                    .getProcessingEnv()
                    .getTypeUtils()
                    .boxedClass((PrimitiveType) array2d.getComponentType())
                    .getSimpleName()
                    .toString()
                + "[]";
      } else {
        throw new UnsupportedOperationException("2D arrays are not supported");
      }
    }

    ClassOrInterfaceType typeOf =
        new ClassOrInterfaceType()
            .setName(ArrayYAMLDeserializer.ArrayCreator.class.getSimpleName())
            .setTypeArguments(new ClassOrInterfaceType().setName(arrayType));

    Expression deserializerCreationExpr;
    if (field.hasYamlTypeDeserializer()) {
      deserializerCreationExpr =
          field.getFieldYamlTypeDeserializerCreationExpr(
              field.getProperty().getAnnotation(YamlTypeDeserializer.class));
    } else {
      deserializerCreationExpr =
          propertyDefinitionFactory
              .getFieldDefinition(array.getComponentType())
              .getFieldDeserializer(field, cu);
    }
    return new MethodCallExpr(
            new NameExpr(ArrayYAMLDeserializer.class.getSimpleName()), "newInstance")
        .addArgument(deserializerCreationExpr)
        .addArgument(
            new CastExpr().setType(typeOf).setExpression(new NameExpr(arrayType + "[]::new")));
  }

  @Override
  public Expression getFieldSerializer(PropertyDefinition field, CompilationUnit cu) {
    cu.addImport(ArrayYAMLSerializer.class);

    ArrayType array = (ArrayType) getBean();
    String serializer = ArrayYAMLSerializer.class.getSimpleName();
    Expression expression =
            propertyDefinitionFactory
                    .getFieldDefinition((array.getComponentType()))
                    .getFieldSerializer(field, cu);

    if (field.hasYamlTypeSerializer()) {
      cu.addImport(YamlTypeSerializerWrapper.class);
      expression =
          field.getFieldYamlTypeSerializerCreationExpr(
              field.getProperty().getAnnotation(YamlTypeSerializer.class));
    }

    return new ObjectCreationExpr()
        .setType(serializer)
        .addArgument(expression);
  }

  @Override
  public String toString() {
    return "ArrayBeanFieldDefinition{" + "bean=" + bean + '}';
  }
}
