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

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeInfo;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.array.ArrayJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.array.ArrayBeanJsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.array.ArrayJsonSerializer;

public class ArrayBeanFieldDefinition extends FieldDefinition {

  public ArrayBeanFieldDefinition(TypeMirror property, GenerationContext context) {
    super(property, context);
  }

  @Override
  public Statement getFieldDeserializer(PropertyDefinition field, CompilationUnit cu) {
    cu.addImport(ArrayJsonDeserializer.ArrayCreator.class);
    cu.addImport(ArrayJsonDeserializer.class);

    ArrayType arrayType = (ArrayType) property;

    if (arrayType.getComponentType().getKind().isPrimitive()) {
      return generatePrimitiveArrayDeserCall(field, arrayType);
    }

    Expression deser;
    if (context.getTypeRegistry().has(arrayType.getComponentType())) {
      deser =
              new ObjectCreationExpr()
                      .setType(
                              context
                                      .getTypeRegistry()
                                      .getDeserializer(arrayType.getComponentType().toString())
                                      .getQualifiedName()
                                      .toString());
    } else if (field.getVariableElement().getAnnotation(JsonbTypeSerializer.class) != null
            && field.getVariableElement().getAnnotation(JsonbTypeDeserializer.class) != null) {
      deser =
              new JsonbTypeSerFieldDefinition(arrayType.getComponentType(), context)
                      .getFieldDeserializerCreationExpr(field, cu);
    } else if (MoreTypes.asTypeElement(arrayType.getComponentType())
            .getAnnotation(JsonbTypeInfo.class)
            != null) {
      deser =
              new JsonbTypeInfoDefinition(
                      MoreTypes.asTypeElement(arrayType.getComponentType())
                              .getAnnotation(JsonbTypeInfo.class),
                      arrayType.getComponentType(),
                      context)
                      .getDeserializerCreationExpr(arrayType.getComponentType(), cu);
    } else if (MoreTypes.asTypeElement(arrayType.getComponentType())
            .getKind()
            .equals(ElementKind.ENUM)) {
      deser =
              new EnumBeanFieldDefinition(arrayType.getComponentType(), context)
                      .getDeserializerCreationExpr(cu);
    } else {
      deser =
              new ObjectCreationExpr()
                      .setType(
                              context
                                      .getTypeUtils()
                                      .getJsonDeserializerImplQualifiedName(
                                              MoreTypes.asTypeElement(arrayType.getComponentType()), cu));
    }

    ObjectCreationExpr arrayJsonDeserializer = new ObjectCreationExpr();
    ClassOrInterfaceType type = new ClassOrInterfaceType();
    type.setName(ArrayJsonDeserializer.class.getSimpleName());
    type.setTypeArguments(
            new ClassOrInterfaceType().setName(arrayType.getComponentType().toString()));
    arrayJsonDeserializer.setType(type);

    return new ExpressionStmt(
            new MethodCallExpr(new NameExpr("bean"), field.getSetter().getSimpleName().toString())
                    .addArgument(
                            new MethodCallExpr(
                                    arrayJsonDeserializer
                                            .addArgument(deser)
                                            .addArgument(createArrayCreatorCall(arrayType.getComponentType())),
                                    "deserialize")
                                    .addArgument(
                                            new MethodCallExpr(new NameExpr("jsonObject"), "getJsonArray")
                                                    .addArgument(new StringLiteralExpr(field.getName())))
                                    .addArgument(new NameExpr("ctx"))));
  }

  private Expression createArrayCreatorCall(TypeMirror array) {
    ClassOrInterfaceType typeOf =
            new ClassOrInterfaceType()
                    .setName(ArrayJsonDeserializer.ArrayCreator.class.getSimpleName())
                    .setTypeArguments(new ClassOrInterfaceType().setName(array.toString()));

    return new CastExpr()
            .setType(typeOf)
            .setExpression(
                    new NameExpr(context.getProcessingEnv().getTypeUtils().erasure(array) + "[]::new"));
  }

  private ExpressionStmt generatePrimitiveArrayDeserCall(
          PropertyDefinition field, ArrayType array) {
    TypeElement deser = context.getTypeRegistry().getDeserializer(array.toString());

    return new ExpressionStmt(
            new MethodCallExpr(new NameExpr("bean"), field.getSetter().getSimpleName().toString())
                    .addArgument(
                            new MethodCallExpr(
                                    new ObjectCreationExpr().setType(deser.getQualifiedName().toString()),
                                    "deserialize")
                                    .addArgument(
                                            new MethodCallExpr(new NameExpr("jsonObject"), "getJsonArray")
                                                    .addArgument(new StringLiteralExpr(field.getName())))
                                    .addArgument(new NameExpr("ctx"))));
  }

  @Override
  public Statement getFieldSerializer(PropertyDefinition field, CompilationUnit cu) {
    cu.addImport(ArrayJsonSerializer.class);
    cu.addImport(ArrayBeanJsonSerializer.class);

    ArrayType arrayType = (ArrayType) property;

    if (arrayType.getComponentType().getKind().isPrimitive()) {
      return generatePrimitiveArraySerCall(field, arrayType);
    }

    ObjectCreationExpr arrayJsonSerializer = new ObjectCreationExpr();
    ClassOrInterfaceType type = new ClassOrInterfaceType();

    Expression ser;
    if (context.getTypeRegistry().has(arrayType.getComponentType())) {
      type.setName(ArrayJsonSerializer.class.getSimpleName());
      ser =
              new ObjectCreationExpr()
                      .setType(
                              context
                                      .getTypeRegistry()
                                      .getSerializer(arrayType.getComponentType().toString())
                                      .getQualifiedName()
                                      .toString());
    } else if (field.getVariableElement().getAnnotation(JsonbTypeSerializer.class) != null
            && field.getVariableElement().getAnnotation(JsonbTypeDeserializer.class) != null) {
      type.setName(ArrayBeanJsonSerializer.class.getSimpleName());
      ser =
              new JsonbTypeSerFieldDefinition(arrayType.getComponentType(), context)
                      .getFieldSerializerCreationExpr(field, cu);
    } else if (MoreTypes.asTypeElement(arrayType.getComponentType())
            .getAnnotation(JsonbTypeInfo.class)
            != null) {
      type.setName(ArrayBeanJsonSerializer.class.getSimpleName());
      ser =
              new JsonbTypeInfoDefinition(
                      MoreTypes.asTypeElement(arrayType.getComponentType())
                              .getAnnotation(JsonbTypeInfo.class),
                      arrayType.getComponentType(),
                      context)
                      .getSerializerCreationExpr(cu);
    } else if (MoreTypes.asTypeElement(arrayType.getComponentType())
            .getKind()
            .equals(ElementKind.ENUM)) {
      cu.addImport(ArrayJsonSerializer.class);
      type.setName(ArrayJsonSerializer.class.getSimpleName());
      ser =
              new EnumBeanFieldDefinition(arrayType.getComponentType(), context)
                      .getSerializerCreationExpr(cu);
    } else {
      type.setName(ArrayBeanJsonSerializer.class.getSimpleName());
      ser =
              new ObjectCreationExpr()
                      .setType(
                              context
                                      .getTypeUtils()
                                      .getJsonSerializerImplQualifiedName(
                                              MoreTypes.asTypeElement(arrayType.getComponentType())));
    }

    type.setTypeArguments(
            new ClassOrInterfaceType().setName(arrayType.getComponentType().toString()));
    arrayJsonSerializer.setType(type);

    return new ExpressionStmt(
            new MethodCallExpr(arrayJsonSerializer.addArgument(ser), "serialize")
                    .addArgument(
                            new MethodCallExpr(
                                    new NameExpr("bean"), field.getGetter().getSimpleName().toString()))
                    .addArgument(new StringLiteralExpr(field.getName()))
                    .addArgument(new NameExpr("generator"))
                    .addArgument(new NameExpr("ctx")));
  }

  private Statement generatePrimitiveArraySerCall(PropertyDefinition field, ArrayType array) {
    TypeElement ser = context.getTypeRegistry().getSerializer(array.toString());

    return new ExpressionStmt(
            new MethodCallExpr(
                    new ObjectCreationExpr().setType(ser.getQualifiedName().toString()), "serialize")
                    .addArgument(
                            new MethodCallExpr(
                                    new NameExpr("bean"), field.getGetter().getSimpleName().toString()))
                    .addArgument(new StringLiteralExpr(field.getName()))
                    .addArgument(new NameExpr("generator"))
                    .addArgument(new NameExpr("ctx")));
  }
}
