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
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.JsonbDeserializer;

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
            field.getVariableElement().getAnnotation(JsonbTypeDeserializer.class);
    try {
      jsonbTypeDeserializer.value();
    } catch (MirroredTypeException e) {
      if (!context
              .getProcessingEnv()
              .getTypeUtils()
              .isSubtype(e.getTypeMirror(), jsonbDeserializer)) {
        throw new IllegalArgumentException(
                String.format(
                        "@JsonbTypeDeserializer value must be a subclass of %s at %s.%s",
                        jsonbDeserializer,
                        field.getVariableElement().getEnclosingElement(),
                        field.getVariableElement().getSimpleName()));
      }

      return new ExpressionStmt(
              new MethodCallExpr(new NameExpr("bean"), field.getSetter().getSimpleName().toString())
                      .addArgument(
                              new MethodCallExpr(
                                      new ObjectCreationExpr().setType(e.getTypeMirror().toString()),
                                      "deserialize")
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
            field.getVariableElement().getAnnotation(JsonbTypeSerializer.class);
    try {
      jsonbTypeSerializer.value();
    } catch (MirroredTypeException e) {
      return new ExpressionStmt(
              new MethodCallExpr(
                      new ObjectCreationExpr().setType(e.getTypeMirror().toString()), "serialize")
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
            field.getVariableElement().getAnnotation(JsonbTypeSerializer.class);
    try {
      jsonbTypeSerializer.value();
    } catch (MirroredTypeException e) {
      return new ObjectCreationExpr().setType(e.getTypeMirror().toString());
    }
    return null;
  }

  public Expression getFieldDeserializerCreationExpr(PropertyDefinition field, CompilationUnit cu) {
    JsonbTypeDeserializer jsonbTypeDeserializer =
            field.getVariableElement().getAnnotation(JsonbTypeDeserializer.class);
    try {
      jsonbTypeDeserializer.value();
    } catch (MirroredTypeException e) {
      if (!context
              .getProcessingEnv()
              .getTypeUtils()
              .isSubtype(e.getTypeMirror(), jsonbDeserializer)) {
        throw new IllegalArgumentException(
                String.format(
                        "@JsonbTypeDeserializer value must be a subclass of %s at %s.%s",
                        jsonbDeserializer,
                        field.getVariableElement().getEnclosingElement(),
                        field.getVariableElement().getSimpleName()));
      }
      return new ObjectCreationExpr().setType(e.getTypeMirror().toString());
    }
    return null;
  }
}
