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

import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.google.auto.common.MoreTypes;
import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.EnumJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.EnumJsonSerializer;


public class EnumBeanFieldDefinition extends FieldDefinition {

  protected EnumBeanFieldDefinition(TypeMirror property, GenerationContext context) {
    super(property, context);
  }

  @Override
  public Statement getFieldDeserializer(PropertyDefinition field, CompilationUnit cu) {
    return new ExpressionStmt(
        new MethodCallExpr(new NameExpr("bean"), field.getSetter().getSimpleName().toString())
            .addArgument(
                new CastExpr()
                    .setType(new ClassOrInterfaceType().setName(field.getType().toString()))
                    .setExpression(
                        new MethodCallExpr(this.getDeserializerCreationExpr(cu), "deserialize")
                            .addArgument(
                                new MethodCallExpr(new NameExpr("jsonObject"), "getJsonString")
                                    .addArgument(new StringLiteralExpr(field.getName())))
                            .addArgument(new NameExpr("ctx")))));
  }

  @Override
  public Statement getFieldSerializer(PropertyDefinition field, CompilationUnit cu) {
    return new ExpressionStmt(
        new MethodCallExpr(getSerializerCreationExpr(cu), "serialize")
            .addArgument(
                new MethodCallExpr(
                    new NameExpr("bean"), field.getGetter().getSimpleName().toString()))
            .addArgument(new StringLiteralExpr(field.getName()))
            .addArgument(new NameExpr("generator"))
            .addArgument(new NameExpr("ctx")));
  }

  private String getEnumName(Element enumConstant) {
    String enumName = enumConstant.toString();
    return enumName;
  }

  public Expression getSerializerCreationExpr(CompilationUnit cu) {
    cu.addImport(EnumJsonSerializer.class);
    cu.addImport(Function.class);

    NodeList<BodyDeclaration<?>> anonymousClassBody = new NodeList<>();
    NodeList<Type> typeArguments = new NodeList<>();
    typeArguments.add(new ClassOrInterfaceType().setName(this.property.toString()));
    typeArguments.add(new ClassOrInterfaceType().setName("String"));

    ClassOrInterfaceType type = new ClassOrInterfaceType().setName("Function");
    type.setTypeArguments(typeArguments);

    ObjectCreationExpr function = new ObjectCreationExpr().setType(type);
    function.setAnonymousClassBody(anonymousClassBody);

    MethodDeclaration apply = new MethodDeclaration();
    apply.setModifiers(Modifier.Keyword.PUBLIC);
    apply.addAnnotation(Override.class);
    apply.setName("apply");
    apply.setType(new ClassOrInterfaceType().setName("String"));
    apply.addParameter(this.property.toString(), "value");

    anonymousClassBody.add(apply);

    for (Element enumConstant : MoreTypes.asTypeElement(this.property).getEnclosedElements()) {
      if (enumConstant.getKind().equals(ElementKind.ENUM_CONSTANT)) {
        apply
            .getBody()
            .ifPresent(
                body ->
                    body.addAndGetStatement(
                            new IfStmt()
                                .setCondition(
                                    new MethodCallExpr(
                                            new NameExpr(
                                                this.property.toString() + "." + enumConstant),
                                            "equals")
                                        .addArgument(new NameExpr("value"))))
                        .setThenStmt(
                            new ReturnStmt(new StringLiteralExpr(getEnumName(enumConstant)))));
      }
    }

    apply
        .getBody()
        .ifPresent(body -> body.addAndGetStatement(new ReturnStmt(new NullLiteralExpr())));

    ObjectCreationExpr ser =
        new ObjectCreationExpr()
            .setType(new ClassOrInterfaceType().setName(EnumJsonSerializer.class.getSimpleName()))
            .addArgument(function);
    return ser;
  }

  public Expression getDeserializerCreationExpr(CompilationUnit cu) {
    cu.addImport(EnumJsonDeserializer.class);
    cu.addImport(Function.class);
    cu.addImport(MoreTypes.asTypeElement(this.property).getQualifiedName().toString());

    ObjectCreationExpr deser =
        new ObjectCreationExpr()
            .setType(new ClassOrInterfaceType().setName(EnumJsonDeserializer.class.getSimpleName()))
            .addArgument(
                MoreTypes.asTypeElement(this.property).getSimpleName().toString() + ".class");

    NodeList<BodyDeclaration<?>> anonymousClassBody = new NodeList<>();
    NodeList<Type> typeArguments = new NodeList<>();
    typeArguments.add(new ClassOrInterfaceType().setName("String"));
    typeArguments.add(new ClassOrInterfaceType().setName(this.property.toString()));

    ClassOrInterfaceType type = new ClassOrInterfaceType().setName("Function");
    type.setTypeArguments(typeArguments);

    ObjectCreationExpr function = new ObjectCreationExpr().setType(type);
    function.setAnonymousClassBody(anonymousClassBody);

    MethodDeclaration apply = new MethodDeclaration();
    apply.setModifiers(Modifier.Keyword.PUBLIC);
    apply.addAnnotation(Override.class);
    apply.setName("apply");
    apply.setType(this.property.toString());
    apply.addParameter("String", "value");

    anonymousClassBody.add(apply);

    for (Element enumConstant : MoreTypes.asTypeElement(this.property).getEnclosedElements()) {
      if (enumConstant.getKind().equals(ElementKind.ENUM_CONSTANT)) {
        apply
            .getBody()
            .ifPresent(
                body ->
                    body.addAndGetStatement(
                            new IfStmt()
                                .setCondition(
                                    new MethodCallExpr(
                                            new StringLiteralExpr(getEnumName(enumConstant)),
                                            "equals")
                                        .addArgument(new NameExpr("value"))))
                        .setThenStmt(
                            new ReturnStmt(
                                new NameExpr(this.property.toString() + "." + enumConstant))));
      }
    }

    apply
        .getBody()
        .ifPresent(body -> body.addAndGetStatement(new ReturnStmt(new NullLiteralExpr())));

    deser.addArgument(function);
    return deser;
  }
}
