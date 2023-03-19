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
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.UnknownType;
import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;
import org.kie.workbench.common.stunner.client.json.mapper.apt.definition.BeanDefinition;
import org.kie.workbench.common.stunner.client.json.mapper.apt.definition.FieldDefinition;
import org.kie.workbench.common.stunner.client.json.mapper.apt.definition.FieldDefinitionFactory;
import org.kie.workbench.common.stunner.client.json.mapper.apt.logger.TreeLogger;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.AbstractBeanJsonSerializer;

public class SerializerGenerator extends AbstractGenerator {

  private final FieldDefinitionFactory fieldDefinitionFactory;

  private ConstructorDeclaration constructor;

  public SerializerGenerator(GenerationContext context, TreeLogger logger) {
    super(context, logger);
    fieldDefinitionFactory = context.getFieldDefinitionFactory();
  }

  @Override
  protected String getMapperName(TypeElement type) {
    return context.getTypeUtils().getJsonSerializerImplName(type);
  }

  @Override
  protected void configureClassType(BeanDefinition type) {
    cu.addImport(AbstractBeanJsonSerializer.class);

    declaration
        .getExtendedTypes()
        .add(
            new ClassOrInterfaceType()
                .setName(AbstractBeanJsonSerializer.class.getSimpleName())
                .setTypeArguments(
                    new ClassOrInterfaceType()
                        .setName(type.getElement().getQualifiedName().toString())));
    constructor = declaration.addConstructor(Modifier.Keyword.PUBLIC);
  }

  @Override
  protected void init(BeanDefinition type) {
    logger.branch(
        TreeLogger.INFO, "Generating serializer for " + type.getElement().getSimpleName());

    addStaticInstance(type);

    type.getPropertyDefinitionsAsStream()
        .forEach(
            propertyDefinition -> {
              FieldDefinition fieldDefinition =
                  fieldDefinitionFactory.getFieldDefinition(propertyDefinition);
              addSetter(
                  type,
                  constructor.getBody(),
                  fieldDefinition.getFieldSerializer(propertyDefinition, cu));
            });
  }

  private void addSetter(BeanDefinition type, BlockStmt body, Statement call) {
    LambdaExpr lambda = new LambdaExpr();
    lambda.setEnclosingParameters(true);
    lambda.getParameters().add(new Parameter().setType(new UnknownType()).setName("bean"));
    lambda.getParameters().add(new Parameter().setType(new UnknownType()).setName("generator"));
    lambda.getParameters().add(new Parameter().setType(new UnknownType()).setName("ctx"));

    lambda.setBody(call);

    body.addStatement(new MethodCallExpr(new NameExpr("properties"), "add").addArgument(lambda));
  }
}
