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

package org.kie.workbench.common.stunner.client.json.mapper.apt.generator;

import javax.lang.model.element.TypeElement;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.UnknownType;
import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;
import org.kie.workbench.common.stunner.client.json.mapper.apt.definition.BeanDefinition;
import org.kie.workbench.common.stunner.client.json.mapper.apt.definition.FieldDefinition;
import org.kie.workbench.common.stunner.client.json.mapper.apt.definition.FieldDefinitionFactory;
import org.kie.workbench.common.stunner.client.json.mapper.apt.definition.PropertyDefinition;
import org.kie.workbench.common.stunner.client.json.mapper.apt.logger.TreeLogger;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.AbstractBeanJsonDeserializer;

public class DeserializerGenerator extends AbstractGenerator {

  private ConstructorDeclaration constructor;

  private final FieldDefinitionFactory fieldDefinitionFactory;

  public DeserializerGenerator(GenerationContext context, TreeLogger logger) {
    super(context, logger);
    fieldDefinitionFactory = context.getFieldDefinitionFactory();
  }

  @Override
  protected String getMapperName(TypeElement type) {
    return context.getTypeUtils().getJsonDeserializerImplName(type);
  }

  @Override
  protected void configureClassType(BeanDefinition type) {
    cu.addImport(AbstractBeanJsonDeserializer.class);

    declaration
        .getExtendedTypes()
        .add(
            new ClassOrInterfaceType()
                .setName(AbstractBeanJsonDeserializer.class.getSimpleName())
                .setTypeArguments(
                    new ClassOrInterfaceType()
                        .setName(type.getElement().getQualifiedName().toString())));

    constructor = declaration.addConstructor(Modifier.Keyword.PUBLIC);
  }

  @Override
  protected void init(BeanDefinition type) {
    logger.branch(
        TreeLogger.INFO, "Generating deserializer for " + type.getElement().getSimpleName());

    addStaticInstance(type);
    addNewInstance(declaration, type);

    type.getPropertyDefinitionsAsStream()
        .forEach(
            propertyDefinition -> {
              FieldDefinition fieldDefinition =
                  fieldDefinitionFactory.getFieldDefinition(propertyDefinition);
              addGetter(
                  propertyDefinition,
                  constructor.getBody(),
                  fieldDefinition.getFieldDeserializer(propertyDefinition, cu));
            });
  }

  private void addNewInstance(ClassOrInterfaceDeclaration declaration, BeanDefinition type) {
    MethodDeclaration methodDeclaration =
        declaration.addMethod("newInstance", Modifier.Keyword.PUBLIC);
    methodDeclaration.setType(
        new ClassOrInterfaceType().setName(type.getElement().getQualifiedName().toString()));
    methodDeclaration
        .getBody()
        .get()
        .addAndGetStatement(
            new ReturnStmt(
                new ObjectCreationExpr().setType(type.getElement().getQualifiedName().toString())));
  }

  private void addGetter(PropertyDefinition propertyDefinition, BlockStmt body, Statement call) {

    LambdaExpr lambda = new LambdaExpr();
    lambda.setEnclosingParameters(true);
    lambda.getParameters().add(new Parameter().setType(new UnknownType()).setName("bean"));
    lambda.getParameters().add(new Parameter().setType(new UnknownType()).setName("jsonObject"));
    lambda.getParameters().add(new Parameter().setType(new UnknownType()).setName("ctx"));
    lambda.setBody(call);
    body.addStatement(
        new MethodCallExpr(new NameExpr("properties"), "put")
            .addArgument(new StringLiteralExpr(propertyDefinition.getName()))
            .addArgument(lambda));
  }
}
