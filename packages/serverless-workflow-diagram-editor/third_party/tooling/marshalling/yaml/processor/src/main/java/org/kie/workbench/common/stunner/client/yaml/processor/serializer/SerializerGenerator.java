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

package org.kie.workbench.common.stunner.client.yaml.processor.serializer;

import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
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
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.bean.AbstractBeanYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.bean.BeanPropertySerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.utils.Pair;
import org.kie.workbench.common.stunner.client.yaml.processor.TypeUtils;
import org.kie.workbench.common.stunner.client.yaml.processor.context.GenerationContext;
import org.kie.workbench.common.stunner.client.yaml.processor.definition.BeanDefinition;
import org.kie.workbench.common.stunner.client.yaml.processor.definition.PropertyDefinition;
import org.kie.workbench.common.stunner.client.yaml.processor.generator.AbstractGenerator;
import org.kie.workbench.common.stunner.client.yaml.processor.logger.TreeLogger;

/** @author Dmitrii Tikhomirov Created by treblereel 3/18/20 */
public class SerializerGenerator extends AbstractGenerator {

  public SerializerGenerator(GenerationContext context, TreeLogger logger) {
    super(context, logger.branch(TreeLogger.INFO, "Serializers generation started"));
  }

  @Override
  protected String getMapperName(TypeElement type) {
    return context.getTypeUtils().serializerName(type.asType());
  }

  @Override
  protected void configureClassType(BeanDefinition type) {
    cu.addImport(YAMLSerializationContext.class);
    cu.addImport(AbstractBeanYAMLSerializer.class);
    cu.addImport(Pair.class);
    cu.addImport(List.class);
    cu.addImport(BeanPropertySerializer.class);
    cu.addImport(YAMLSerializer.class);
    cu.addImport(type.getQualifiedName());

    declaration
        .getExtendedTypes()
        .add(
            new ClassOrInterfaceType()
                .setName(AbstractBeanYAMLSerializer.class.getSimpleName())
                .setTypeArguments(new ClassOrInterfaceType().setName(type.getSimpleName())));
  }

  @Override
  protected void getType(BeanDefinition type) {
    getSerializedType(type);
  }

  private void getSerializedType(BeanDefinition type) {
    declaration
        .addMethod("getSerializedType", Modifier.Keyword.PUBLIC)
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
        "Generating " + context.getTypeUtils().serializerName(beanDefinition.getBean()));
    MethodDeclaration initSerializers =
        declaration.addMethod("initSerializers", Modifier.Keyword.PROTECTED);
    initSerializers
        .addAnnotation(Override.class)
        .setType(BeanPropertySerializer[].class)
        .getBody()
        .ifPresent(body -> processInitSerializersMethodBody(body, beanDefinition));
  }

  private void processInitSerializersMethodBody(BlockStmt body, BeanDefinition beanDefinition) {

    List<PropertyDefinition> fields =
        beanDefinition.getFields().stream().collect(Collectors.toList());
    addBeanPropertySerializerDeclaration(body, fields);

    for (int i = 0; i < fields.size(); i++) {
      addBeanPropertySerializer(body, beanDefinition, fields.get(i), i);
    }

    body.addStatement(new ReturnStmt(new NameExpr("result")));
  }

  private void addBeanPropertySerializerDeclaration(
      BlockStmt body, List<PropertyDefinition> fields) {
    VariableDeclarator result = new VariableDeclarator();
    result.setType("BeanPropertySerializer[]");
    result.setName("result");
    result.setInitializer("new BeanPropertySerializer[" + fields.size() + "]");
    ExpressionStmt expressionStmt = new ExpressionStmt();
    VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();

    expressionStmt.setExpression(variableDeclarationExpr);
    variableDeclarationExpr.getVariables().add(result);
    body.addStatement(variableDeclarationExpr);
  }

  private void addBeanPropertySerializer(
      BlockStmt body, BeanDefinition beanDefinition, PropertyDefinition variableElement, int i) {
    ObjectCreationExpr beanProperty = new ObjectCreationExpr();
    ClassOrInterfaceType beanType =
        new ClassOrInterfaceType().setName(BeanPropertySerializer.class.getSimpleName());

    beanProperty.setType(beanType);
    beanProperty.addArgument(new StringLiteralExpr(variableElement.getPropertyName()));
    setTypeParams(beanDefinition, variableElement, beanType);

    body.addStatement(
        new AssignExpr()
            .setTarget(new ArrayAccessExpr(new NameExpr("result"), new IntegerLiteralExpr(i)))
            .setValue(beanProperty));

    addMethods(beanProperty, beanDefinition, variableElement);
  }

  private void setTypeParams(
      BeanDefinition beanDefinition,
      PropertyDefinition variableElement,
      ClassOrInterfaceType beanType) {
    NodeList<Type> typeArguments = new NodeList<>();
    typeArguments.add(new ClassOrInterfaceType().setName(beanDefinition.getSimpleName()));

    String fieldType;
    if (variableElement.getBean().getKind().isPrimitive()) {
      fieldType = TypeUtils.wrapperType(variableElement.getBean());
    } else if (variableElement.getBean().getKind().equals(TypeKind.ARRAY)) {
      ArrayType arrayType = (ArrayType) variableElement.getBean();
      fieldType = arrayType.toString();
    } else {
      fieldType = typeUtils.toTypeElement(variableElement.getBean()).toString();
    }

    ClassOrInterfaceType interfaceType = new ClassOrInterfaceType();
    interfaceType.setName(fieldType);

    addTypeArguments(variableElement.getBean(), interfaceType);
    typeArguments.add(interfaceType);
    beanType.setTypeArguments(typeArguments);
  }

  private void addMethods(
      ObjectCreationExpr beanProperty,
      BeanDefinition beanDefinition,
      PropertyDefinition propertyDefinition) {
    NodeList<BodyDeclaration<?>> anonymousClassBody = new NodeList<>();
    beanProperty.setAnonymousClassBody(anonymousClassBody);

    newSerializer(anonymousClassBody, propertyDefinition);
    getValue(anonymousClassBody, beanDefinition, propertyDefinition);
  }

  private void addTypeArguments(TypeMirror type, ClassOrInterfaceType interfaceType) {
    if (type instanceof DeclaredType) {
      if (!((DeclaredType) type).getTypeArguments().isEmpty()) {
        NodeList<Type> types = new NodeList<>();
        ((DeclaredType) type)
            .getTypeArguments()
            .forEach(param -> types.add(new ClassOrInterfaceType().setName(param.toString())));
        interfaceType.setTypeArguments(types);
      }
    }
  }

  private void newSerializer(
      NodeList<BodyDeclaration<?>> anonymousClassBody, PropertyDefinition field) {
    MethodDeclaration method = new MethodDeclaration();
    method.setModifiers(Modifier.Keyword.PROTECTED);
    method.addAnnotation(Override.class);
    method.setName("newSerializer");

    method.setType(new ClassOrInterfaceType().setName("YAMLSerializer<?>"));

    method
        .getBody()
        .ifPresent(
            body ->
                body.addAndGetStatement(
                    new ReturnStmt().setExpression(field.getFieldSerializer(cu))));
    anonymousClassBody.add(method);
  }

  private void getValue(
      NodeList<BodyDeclaration<?>> anonymousClassBody,
      BeanDefinition bean,
      PropertyDefinition field) {
    MethodDeclaration method = new MethodDeclaration();
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.addAnnotation(Override.class);
    method.setName("getValue");
    method.addParameter(new ClassOrInterfaceType().setName(bean.getSimpleName()), "bean");
    method.addParameter(YAMLSerializationContext.class.getSimpleName(), "ctx");

    ClassOrInterfaceType interfaceType =
        new ClassOrInterfaceType().setName(TypeUtils.wrapperType(field.getBean()));
    addTypeArguments(field.getBean(), interfaceType);

    method.setType(interfaceType);
    method
        .getBody()
        .ifPresent(body -> body.addAndGetStatement(new ReturnStmt(getFieldAccessor(field))));
    anonymousClassBody.add(method);
  }

  private Expression getFieldAccessor(PropertyDefinition field) {
    if (typeUtils.hasGetter(field.getProperty())) {
      return new MethodCallExpr(
          new NameExpr("bean"),
          typeUtils.getGetter(field.getProperty()).getSimpleName().toString());
    } else {
      return new FieldAccessExpr(
          new NameExpr("bean"), field.getProperty().getSimpleName().toString());
    }
  }
}
