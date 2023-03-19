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
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;

public class BasicTypeFieldDefinition extends FieldDefinition {

  protected BasicTypeFieldDefinition(TypeMirror property, GenerationContext context) {
    super(property, context);
  }

  @Override
  public Statement getFieldDeserializer(PropertyDefinition field, CompilationUnit cu) {
    String setter = field.getSetter().getSimpleName().toString();
    Expression jsonGetter = getPropertyAccessor(field);

    MethodCallExpr method = new MethodCallExpr(new NameExpr("bean"), setter);
    method.addArgument(jsonGetter);
    return new ExpressionStmt(method);
  }

  @Override
  public Statement getFieldSerializer(PropertyDefinition field, CompilationUnit cu) {
    return new ExpressionStmt(
        new MethodCallExpr(new NameExpr("generator"), "write")
            .addArgument(new StringLiteralExpr(field.getName()))
            .addArgument(
                new MethodCallExpr(
                    new NameExpr("bean"), field.getGetter().getSimpleName().toString())));
  }

  private Expression getPropertyAccessor(PropertyDefinition field) {
    TypeElement deser = context.getTypeRegistry().getDeserializer(field.getType());

    NameExpr jsonObject = new NameExpr("jsonObject");
    StringLiteralExpr name = new StringLiteralExpr(field.getName());

    return new MethodCallExpr(
            new ObjectCreationExpr().setType(deser.getQualifiedName().toString()), "deserialize")
        .addArgument(new MethodCallExpr(jsonObject, "get").addArgument(name))
        .addArgument(new NameExpr("ctx"));
  }
}
