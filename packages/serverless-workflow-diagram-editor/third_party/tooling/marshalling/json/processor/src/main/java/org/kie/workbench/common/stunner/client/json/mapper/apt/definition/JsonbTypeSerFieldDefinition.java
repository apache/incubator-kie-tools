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

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.UnknownType;
import com.google.auto.common.MoreTypes;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.DeserializerJsonbTypeSerializerWrapper;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.JsonbDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.SerializerJsonbTypeSerializerWrapper;


public class JsonbTypeSerFieldDefinition extends FieldDefinition {

  private TypeMirror jsonbDeserializer;

  protected JsonbTypeSerFieldDefinition(TypeMirror property, GenerationContext context) {
    super(property, context);
    TypeElement jsonbDeserializer =
        context
            .getProcessingEnv()
            .getElementUtils()
            .getTypeElement(
                JsonbDeserializer.class
                    .getCanonicalName());
    this.jsonbDeserializer =
        context.getProcessingEnv().getTypeUtils().erasure(jsonbDeserializer.asType());
  }

  @Override
  public Statement getFieldDeserializer(PropertyDefinition field, CompilationUnit cu) {
    JsonbTypeDeserializer jsonbTypeDeserializer =
        field.getVariableElement().getAnnotation(JsonbTypeDeserializer.class) != null
            ? field.getVariableElement().getAnnotation(JsonbTypeDeserializer.class)
            : MoreTypes.asTypeElement(property).getAnnotation(JsonbTypeDeserializer.class);
    try {
      jsonbTypeDeserializer.value();
    } catch (MirroredTypeException e) {
      Expression deserializerCreationExpr = getFieldDeserializerCreationExpr(field, cu);
      return new ExpressionStmt(
          new MethodCallExpr(new NameExpr("bean"), field.getSetter().getSimpleName().toString())
              .addArgument(
                  new MethodCallExpr(deserializerCreationExpr, "deserialize")
                      .addArgument(
                          new MethodCallExpr(new NameExpr("jsonObject"), "get")
                              .addArgument(new StringLiteralExpr(field.getName())))
                      .addArgument(new NameExpr("ctx"))));
    }
    return null;
  }

  @Override
  public Statement getFieldSerializer(PropertyDefinition field, CompilationUnit cu) {
    JsonbTypeSerializer jsonbTypeSerializer =
        field.getVariableElement().getAnnotation(JsonbTypeSerializer.class) != null
            ? field.getVariableElement().getAnnotation(JsonbTypeSerializer.class)
            : MoreTypes.asTypeElement(property).getAnnotation(JsonbTypeSerializer.class);
    try {
      jsonbTypeSerializer.value();
    } catch (MirroredTypeException e) {
      return new ExpressionStmt(
          new MethodCallExpr(
                  new ObjectCreationExpr()
                      .setType(SerializerJsonbTypeSerializerWrapper.class.getCanonicalName())
                      .addArgument(new ObjectCreationExpr().setType(e.getTypeMirror().toString()))
                      .addArgument(new StringLiteralExpr(field.getName())),
                  "serialize")
              .addArgument(
                  new MethodCallExpr(
                      new NameExpr("bean"), field.getGetter().getSimpleName().toString()))
              .addArgument(new NameExpr("generator"))
              .addArgument(new NameExpr("ctx")));
    }

    return null;
  }

  public Expression getFieldSerializerCreationExpr(PropertyDefinition field, CompilationUnit cu) {
    JsonbTypeSerializer jsonbTypeSerializer =
        field.getVariableElement().getAnnotation(JsonbTypeSerializer.class) != null
            ? field.getVariableElement().getAnnotation(JsonbTypeSerializer.class)
            : MoreTypes.asTypeElement(property).getAnnotation(JsonbTypeSerializer.class);
    try {
      jsonbTypeSerializer.value();
    } catch (MirroredTypeException e) {
      return new ObjectCreationExpr().setType(e.getTypeMirror().toString());
    }
    return null;
  }

  public Expression getFieldDeserializerCreationExpr(PropertyDefinition field, CompilationUnit cu) {
    JsonbTypeDeserializer jsonbTypeDeserializer =
        field.getVariableElement().getAnnotation(JsonbTypeDeserializer.class) != null
            ? field.getVariableElement().getAnnotation(JsonbTypeDeserializer.class)
            : MoreTypes.asTypeElement(property).getAnnotation(JsonbTypeDeserializer.class);
    try {
      jsonbTypeDeserializer.value();
    } catch (MirroredTypeException e) {

      TypeMirror typeMirror = field.getType();
      if (context.getTypeUtils().isIterable(typeMirror)) {
        typeMirror = MoreTypes.asDeclared(field.getType()).getTypeArguments().get(0);
      } else if (typeMirror.getKind().equals(TypeKind.ARRAY)) {
        typeMirror = MoreTypes.asArray(typeMirror).getComponentType();
      }

      ClassOrInterfaceType type = new ClassOrInterfaceType();
      type.setName(DeserializerJsonbTypeSerializerWrapper.class.getCanonicalName());
      type.setTypeArguments(new UnknownType());

      return new ObjectCreationExpr()
          .setType(type)
          .addArgument(new ObjectCreationExpr().setType(e.getTypeMirror().toString()))
          .addArgument(new FieldAccessExpr(new NameExpr(typeMirror.toString()), "class"));
    }
    return null;
  }
}
