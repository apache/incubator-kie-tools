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

import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.google.auto.common.MoreTypes;
import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;

public class DefaultBeanFieldDefinition extends FieldDefinition {
  public DefaultBeanFieldDefinition(TypeMirror property, GenerationContext context) {
    super(property, context);
  }

  @Override
  public Statement getFieldDeserializer(PropertyDefinition field, CompilationUnit cu) {
    TypeMirror type = field.getType();
    String deser =
        context
            .getTypeUtils()
            .getJsonDeserializerImplQualifiedName(MoreTypes.asTypeElement(type), cu);

    return new ExpressionStmt(
        new MethodCallExpr(new NameExpr("bean"), field.getSetter().getSimpleName().toString())
            .addArgument(
                new MethodCallExpr(new ObjectCreationExpr().setType(deser), "deserialize")
                    .addArgument(
                        new MethodCallExpr(new NameExpr("jsonObject"), "getJsonObject")
                            .addArgument(new StringLiteralExpr(field.getName())))
                    .addArgument(new NameExpr("ctx"))));
  }

  @Override
  public Statement getFieldSerializer(PropertyDefinition field, CompilationUnit cu) {
    TypeMirror type = field.getType();
    String ser =
        context.getTypeUtils().getJsonSerializerImplQualifiedName(MoreTypes.asTypeElement(type));

    return new ExpressionStmt(
        new MethodCallExpr(new ObjectCreationExpr().setType(ser), "serialize")
            .addArgument(
                new MethodCallExpr(
                    new NameExpr("bean"), field.getGetter().getSimpleName().toString()))
            .addArgument(new StringLiteralExpr(field.getName()))
            .addArgument(new NameExpr("generator"))
            .addArgument(new NameExpr("ctx")));
  }
}
