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
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
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
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.collection.BoxedTypeCollectionJsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.collection.CollectionJsonSerializer;

public class CollectionsFieldDefinition extends FieldDefinition {

  protected CollectionsFieldDefinition(TypeMirror property, GenerationContext context) {
    super(property, context);
  }

  @Override
  public Statement getFieldDeserializer(PropertyDefinition field, CompilationUnit cu) {
    TypeElement deserializer =
            context
                    .getTypeRegistry()
                    .getDeserializer(context.getProcessingEnv().getTypeUtils().erasure(property));

    cu.addImport(deserializer.getQualifiedName().toString());
    TypeMirror typeMirror = MoreTypes.asDeclared(field.getType()).getTypeArguments().get(0);

    Expression deser;
    if (context.getTypeRegistry().has(typeMirror)) {
      deser =
              new ObjectCreationExpr()
                      .setType(
                              context
                                      .getTypeRegistry()
                                      .getDeserializer(typeMirror)
                                      .getQualifiedName()
                                      .toString());

    } else if (field.getVariableElement().getAnnotation(JsonbTypeSerializer.class) != null
            && field.getVariableElement().getAnnotation(JsonbTypeDeserializer.class) != null) {
      deser =
              new JsonbTypeSerFieldDefinition(typeMirror, context)
                      .getFieldDeserializerCreationExpr(field, cu);
    } else if (MoreTypes.asTypeElement(typeMirror).getAnnotation(JsonbTypeInfo.class) != null) {
      deser =
              new JsonbTypeInfoDefinition(
                      MoreTypes.asTypeElement(typeMirror).getAnnotation(JsonbTypeInfo.class),
                      typeMirror,
                      context)
                      .getDeserializerCreationExpr(typeMirror, cu);
    } else if (MoreTypes.asTypeElement(typeMirror).getKind().equals(ElementKind.ENUM)) {
      deser = new EnumBeanFieldDefinition(typeMirror, context).getDeserializerCreationExpr(cu);
    } else {
      deser =
              new ObjectCreationExpr()
                      .setType(
                              context
                                      .getTypeUtils()
                                      .getJsonDeserializerImplQualifiedName(
                                              MoreTypes.asTypeElement(typeMirror), cu));
    }

    ClassOrInterfaceType type = new ClassOrInterfaceType();
    type.setName(deserializer.getSimpleName().toString());
    type.setTypeArguments(new ClassOrInterfaceType().setName(typeMirror.toString()));
    ObjectCreationExpr deserializerCreationExpr = new ObjectCreationExpr();
    deserializerCreationExpr.setType(type);

    return new ExpressionStmt(
            new MethodCallExpr(new NameExpr("bean"), field.getSetter().getSimpleName().toString())
                    .addArgument(
                            new MethodCallExpr(deserializerCreationExpr.addArgument(deser), "deserialize")
                                    .addArgument(
                                            new MethodCallExpr(new NameExpr("jsonObject"), "getJsonArray")
                                                    .addArgument(new StringLiteralExpr(field.getName())))
                                    .addArgument(new NameExpr("ctx"))));
  }

  @Override
  public Statement getFieldSerializer(PropertyDefinition field, CompilationUnit cu) {
    ObjectCreationExpr serializerCreationExpr = new ObjectCreationExpr();
    ClassOrInterfaceType type = new ClassOrInterfaceType();
    TypeMirror typeMirror = MoreTypes.asDeclared(field.getType()).getTypeArguments().get(0);
    boolean isBoxedTypeOrString = context.getTypeUtils().isBoxedTypeOrString(typeMirror);

    if (isBoxedTypeOrString
            || MoreTypes.asTypeElement(typeMirror).getKind().equals(ElementKind.ENUM)) {
      cu.addImport(BoxedTypeCollectionJsonSerializer.class);
      type.setName(BoxedTypeCollectionJsonSerializer.class.getSimpleName());
    } else {
      cu.addImport(CollectionJsonSerializer.class);
      type.setName(CollectionJsonSerializer.class.getSimpleName());
    }

    type.setTypeArguments(new ClassOrInterfaceType().setName(typeMirror.toString()));
    ObjectCreationExpr deserializerCreationExpr = new ObjectCreationExpr();
    deserializerCreationExpr.setType(type);
    type.setTypeArguments(new ClassOrInterfaceType().setName(typeMirror.toString()));
    serializerCreationExpr.setType(type);

    Expression ser;
    if (context.getTypeRegistry().has(typeMirror)) {
      ser =
              new ObjectCreationExpr()
                      .setType(
                              new ClassOrInterfaceType()
                                      .setName(
                                              context
                                                      .getTypeRegistry()
                                                      .getSerializer(typeMirror)
                                                      .getQualifiedName()
                                                      .toString()));

    } else if (field.getVariableElement().getAnnotation(JsonbTypeSerializer.class) != null
            && field.getVariableElement().getAnnotation(JsonbTypeDeserializer.class) != null) {
      ser =
              new JsonbTypeSerFieldDefinition(typeMirror, context)
                      .getFieldSerializerCreationExpr(field, cu);
    } else if (MoreTypes.asTypeElement(typeMirror).getAnnotation(JsonbTypeInfo.class) != null) {
      ser =
              new JsonbTypeInfoDefinition(
                      MoreTypes.asTypeElement(typeMirror).getAnnotation(JsonbTypeInfo.class),
                      typeMirror,
                      context)
                      .getSerializerCreationExpr(cu);
    } else if (MoreTypes.asTypeElement(typeMirror).getKind().equals(ElementKind.ENUM)) {
      ser = new EnumBeanFieldDefinition(typeMirror, context).getSerializerCreationExpr(cu);
    } else {
      ser =
              new ObjectCreationExpr()
                      .setType(
                              new ClassOrInterfaceType()
                                      .setName(
                                              context
                                                      .getTypeUtils()
                                                      .getJsonSerializerImplQualifiedName(
                                                              MoreTypes.asTypeElement(typeMirror))));
    }
    return new ExpressionStmt(
            new MethodCallExpr(serializerCreationExpr.addArgument(ser), "serialize")
                    .addArgument(
                            new MethodCallExpr(
                                    new NameExpr("bean"), field.getGetter().getSimpleName().toString()))
                    .addArgument(new StringLiteralExpr(field.getName()))
                    .addArgument(new NameExpr("generator"))
                    .addArgument(new NameExpr("ctx")));
  }
}
