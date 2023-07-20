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

package org.kie.workbench.common.stunner.client.yaml.processor.deserializer;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.bean.AbstractBeanYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.bean.BeanPropertyDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.bean.HasDeserializerAndParameters;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.bean.Instance;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.bean.InstanceBuilder;
import org.kie.workbench.common.stunner.client.yaml.processor.TypeUtils;
import org.kie.workbench.common.stunner.client.yaml.processor.context.GenerationContext;
import org.kie.workbench.common.stunner.client.yaml.processor.definition.BeanDefinition;
import org.kie.workbench.common.stunner.client.yaml.processor.definition.PropertyDefinition;
import org.kie.workbench.common.stunner.client.yaml.processor.generator.AbstractGenerator;
import org.kie.workbench.common.stunner.client.yaml.processor.logger.TreeLogger;

/** @author Dmitrii Tikhomirov Created by treblereel 3/18/20 */
public class DeserializerGenerator extends AbstractGenerator {

  public DeserializerGenerator(GenerationContext context, TreeLogger logger) {
    super(context, logger.branch(TreeLogger.INFO, "Deserializers generation started"));
  }

  @Override
  protected String getMapperName(TypeElement type) {
    return context.getTypeUtils().deserializerName(type.asType());
  }

  @Override
  protected void configureClassType(BeanDefinition type) {
    cu.addImport(YAMLDeserializationContext.class);
    cu.addImport(YAMLDeserializer.class);
    cu.addImport(AbstractBeanYAMLDeserializer.class);
    cu.addImport(BeanPropertyDeserializer.class);
    cu.addImport(HasDeserializerAndParameters.class);
    cu.addImport(Instance.class);
    cu.addImport(Map.class);
    cu.addImport(HashMap.class);
    cu.addImport(InstanceBuilder.class);
    cu.addImport(type.getQualifiedName());

    declaration
        .getExtendedTypes()
        .add(
            new ClassOrInterfaceType()
                .setName(AbstractBeanYAMLDeserializer.class.getSimpleName())
                .setTypeArguments(new ClassOrInterfaceType().setName(type.getSimpleName())));
  }

  @Override
  protected void getType(BeanDefinition type) {
    declaration
        .addMethod("getDeserializedType", Modifier.Keyword.PUBLIC)
        .addAnnotation(Override.class)
        .setType(Class.class)
        .getBody()
        .ifPresent(
            body ->
                body.addStatement(
                    new ReturnStmt(
                        new FieldAccessExpr(new NameExpr(type.getSimpleName()), "class"))));
  }

  @Override
  protected void init(BeanDefinition beanDefinition) {
    logger.log(
        TreeLogger.INFO,
        "Generating " + context.getTypeUtils().deserializerName(beanDefinition.getBean()));
    initDeserializers(beanDefinition);
    initInstanceBuilder(beanDefinition);
  }

  private void initDeserializers(BeanDefinition beanDefinition) {
    MethodDeclaration initSerializers =
        declaration.addMethod("initDeserializers", Modifier.Keyword.PROTECTED);

    initSerializers
        .addAnnotation(Override.class)
        .setType(
            new ClassOrInterfaceType()
                .setName(Map.class.getSimpleName())
                .setTypeArguments(
                    new ClassOrInterfaceType().setName(String.class.getSimpleName()),
                    new ClassOrInterfaceType()
                        .setName(BeanPropertyDeserializer.class.getSimpleName())
                        .setTypeArguments(
                            new ClassOrInterfaceType()
                                .setName(beanDefinition.getElement().getSimpleName().toString()),
                            new ClassOrInterfaceType().setName("?"))));
    ClassOrInterfaceType varType =
        new ClassOrInterfaceType()
            .setName("Map")
            .setTypeArguments(
                new ClassOrInterfaceType().setName("String"),
                new ClassOrInterfaceType()
                    .setName("BeanPropertyDeserializer")
                    .setTypeArguments(
                        new ClassOrInterfaceType()
                            .setName(beanDefinition.getElement().getSimpleName().toString()),
                        new ClassOrInterfaceType().setName("?")));

    VariableDeclarator map = new VariableDeclarator();
    map.setType(varType);
    map.setName("map");
    map.setInitializer(new NameExpr("new HashMap<>()"));

    ExpressionStmt expressionStmt = new ExpressionStmt();
    VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
    variableDeclarationExpr.setModifiers(Modifier.Keyword.FINAL);
    expressionStmt.setExpression(variableDeclarationExpr);
    variableDeclarationExpr.getVariables().add(map);

    initSerializers
        .getBody()
        .ifPresent(
            body -> {
              body.addStatement(expressionStmt);
              beanDefinition
                  .getFields()
                  .forEach(
                      field ->
                          addBeanPropertyDeserializer(body, beanDefinition.getElement(), field));
              body.addStatement(new ReturnStmt("map"));
            });
  }

  private void initInstanceBuilder(BeanDefinition type) {
    MethodDeclaration initInstanceBuilder =
        declaration.addMethod("initInstanceBuilder", Modifier.Keyword.PROTECTED);
    initInstanceBuilder
        .addAnnotation(Override.class)
        .setType(
            new ClassOrInterfaceType()
                .setName(InstanceBuilder.class.getSimpleName())
                .setTypeArguments(new ClassOrInterfaceType().setName(type.getSimpleName())));
    VariableDeclarator deserializers = new VariableDeclarator();
    deserializers.setType("Map<String, HasDeserializerAndParameters>");
    deserializers.setName("deserializers");
    deserializers.setInitializer("null");

    ExpressionStmt expressionStmt = new ExpressionStmt();
    VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
    variableDeclarationExpr.setModifiers(Modifier.Keyword.FINAL);
    expressionStmt.setExpression(variableDeclarationExpr);
    variableDeclarationExpr.getVariables().add(deserializers);

    initInstanceBuilder
        .getBody()
        .ifPresent(
            body -> {
              body.addStatement(variableDeclarationExpr);
              addInstanceBuilder(type, body);
            });
  }

  private void addBeanPropertyDeserializer(
      BlockStmt body, TypeElement type, PropertyDefinition field) {
    NodeList<BodyDeclaration<?>> anonymousClassBody = new NodeList<>();

    ClassOrInterfaceType typeArg = getWrappedType(field.getProperty());
    ClassOrInterfaceType beanPropertyDeserializer =
        new ClassOrInterfaceType().setName(BeanPropertyDeserializer.class.getSimpleName());
    beanPropertyDeserializer.setTypeArguments(
        new ClassOrInterfaceType().setName(type.getSimpleName().toString()), typeArg);

    body.addStatement(
        new MethodCallExpr(new NameExpr("map"), "put")
            .addArgument(new StringLiteralExpr(field.getPropertyName()))
            .addArgument(
                new ObjectCreationExpr()
                    .setType(beanPropertyDeserializer)
                    .setAnonymousClassBody(anonymousClassBody)));
    addNewDeserializer(field, anonymousClassBody);
    setValue(type, typeArg, field, anonymousClassBody);
  }

  private void addInstanceBuilder(BeanDefinition type, BlockStmt body) {
    ObjectCreationExpr instanceBuilder = new ObjectCreationExpr();
    ClassOrInterfaceType instanceBuilderType =
        new ClassOrInterfaceType()
            .setName(InstanceBuilder.class.getSimpleName())
            .setTypeArguments(new ClassOrInterfaceType().setName(type.getSimpleName()));

    instanceBuilder.setType(instanceBuilderType);
    NodeList<BodyDeclaration<?>> anonymousClassBody = new NodeList<>();
    instanceBuilder.setAnonymousClassBody(anonymousClassBody);

    newInstance(type, anonymousClassBody);
    getParametersDeserializer(anonymousClassBody);
    create(type, anonymousClassBody);

    body.addStatement(new ReturnStmt(instanceBuilder));
  }

  private ClassOrInterfaceType getWrappedType(VariableElement field) {
    ClassOrInterfaceType typeArg =
        new ClassOrInterfaceType().setName(TypeUtils.wrapperType(field.asType()));
    if (field.asType() instanceof DeclaredType) {
      if (!((DeclaredType) field.asType()).getTypeArguments().isEmpty()) {
        NodeList<Type> types = new NodeList<>();
        ((DeclaredType) field.asType())
            .getTypeArguments()
            .forEach(t -> types.add(new ClassOrInterfaceType().setName(TypeUtils.wrapperType(t))));
        typeArg.setTypeArguments(types);
      }
    }
    return typeArg;
  }

  private void addNewDeserializer(
      PropertyDefinition field, NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.setModifiers(Modifier.Keyword.PROTECTED);
    method.addAnnotation(Override.class);
    method.setName("newDeserializer");
    method.setType(new ClassOrInterfaceType().setName("YAMLDeserializer<?>"));

    method
        .getBody()
        .ifPresent(
            body ->
                body.addAndGetStatement(
                    new ReturnStmt().setExpression(field.getFieldDeserializer(cu))));
    anonymousClassBody.add(method);
  }

  private void setValue(
      TypeElement type,
      ClassOrInterfaceType fieldType,
      PropertyDefinition field,
      NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.addAnnotation(Override.class);
    method.setName("setValue");
    method.setType("void");
    method.addParameter(type.getSimpleName().toString(), "bean");
    method.addParameter(fieldType, "value");
    method.addParameter(YAMLDeserializationContext.class.getSimpleName(), "ctx");

    method.getBody().ifPresent(body -> body.addAndGetStatement(getFieldAccessor(field)));
    anonymousClassBody.add(method);
  }

  private void newInstance(BeanDefinition type, NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.addAnnotation(Override.class);
    method.setName("newInstance");
    method.setType(
        new ClassOrInterfaceType()
            .setName("Instance")
            .setTypeArguments(new ClassOrInterfaceType().setName(type.getSimpleName())));
    addParameter(method, "YAMLDeserializationContext", "ctx");

    ObjectCreationExpr instanceBuilder = new ObjectCreationExpr();
    ClassOrInterfaceType instanceBuilderType =
        new ClassOrInterfaceType()
            .setName(Instance.class.getSimpleName())
            .setTypeArguments(new ClassOrInterfaceType().setName(type.getSimpleName()));

    instanceBuilder.setType(instanceBuilderType);
    instanceBuilder.addArgument(new MethodCallExpr("create"));

    method
        .getBody()
        .ifPresent(
            body -> body.addAndGetStatement(new ReturnStmt().setExpression(instanceBuilder)));
    anonymousClassBody.add(method);
  }

  private void getParametersDeserializer(NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.addAnnotation(Override.class);
    method.setName("getParametersDeserializer");
    method.setType(
        new ClassOrInterfaceType()
            .setName("Map")
            .setTypeArguments(
                new ClassOrInterfaceType().setName("String"),
                new ClassOrInterfaceType().setName("HasDeserializerAndParameters")));
    method
        .getBody()
        .ifPresent(
            body ->
                body.addAndGetStatement(
                    new ReturnStmt().setExpression(new NameExpr("deserializers"))));
    anonymousClassBody.add(method);
  }

  private void create(BeanDefinition type, NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.setModifiers(Modifier.Keyword.PRIVATE);
    method.setName("create");
    method.setType(new ClassOrInterfaceType().setName(type.getSimpleName()));

    ObjectCreationExpr instanceBuilder = new ObjectCreationExpr();
    ClassOrInterfaceType instanceBuilderType =
        new ClassOrInterfaceType().setName(type.getSimpleName());
    instanceBuilder.setType(instanceBuilderType);

    method
        .getBody()
        .ifPresent(
            body -> body.addAndGetStatement(new ReturnStmt().setExpression(instanceBuilder)));
    anonymousClassBody.add(method);
  }

  private Expression getFieldAccessor(PropertyDefinition field) {
    if (typeUtils.hasSetter(field.getProperty())) {
      return new MethodCallExpr(
              new NameExpr("bean"),
              typeUtils.getSetter(field.getProperty()).getSimpleName().toString())
          .addArgument("value");
    } else {
      return new AssignExpr()
          .setTarget(
              new FieldAccessExpr(
                  new NameExpr("bean"), field.getProperty().getSimpleName().toString()))
          .setValue(new NameExpr("value"));
    }
  }

  private void addParameter(MethodDeclaration method, String type, String name) {
    method.addParameter(new ClassOrInterfaceType().setName(type), name);
  }
}
