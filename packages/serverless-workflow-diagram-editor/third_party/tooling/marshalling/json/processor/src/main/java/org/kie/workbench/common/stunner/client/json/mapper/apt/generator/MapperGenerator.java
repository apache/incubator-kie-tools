/*
 * Copyright © 2020 Treblereel
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

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.google.auto.common.MoreElements;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.AbstractObjectMapper;
import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;
import org.kie.workbench.common.stunner.client.json.mapper.apt.definition.BeanDefinition;
import org.kie.workbench.common.stunner.client.json.mapper.apt.logger.TreeLogger;

/** @author Dmitrii Tikhomirov Created by treblereel 3/20/20 */
public class MapperGenerator extends AbstractGenerator {

  private static final String MAPPER_IMPL = "_JsonMapperImpl";

  private final DeserializerGenerator deserializerGenerator;
  private final SerializerGenerator serializerGenerator;

  public MapperGenerator(GenerationContext context, TreeLogger logger) {
    super(context, logger);
    this.deserializerGenerator = new DeserializerGenerator(context, logger);
    this.serializerGenerator = new SerializerGenerator(context, logger);
  }

  @Override
  protected void configureClassType(BeanDefinition type) {
    cu.addImport(JsonbDeserializer.class);
    cu.addImport(JsonbSerializer.class);

    setExtendedType(type);
  }

  private void setExtendedType(BeanDefinition type) {
    String typeMapperName = getTypeMapperName(type);
    declaration
        .getExtendedTypes()
        .add(
            new ClassOrInterfaceType()
                .setName(AbstractObjectMapper.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType().setName(typeMapperName)));
  }

  private String getTypeMapperName(BeanDefinition type) {
    return type.getElement().getSimpleName().toString();
  }

  @Override
  protected void init(BeanDefinition type) {
    serializerGenerator.generate(type);
    deserializerGenerator.generate(type);

    addStaticInstance(type);
    addDeserializer(type);
    addSerializer(type);
  }

  private void addSerializer(BeanDefinition type) {
    ClassOrInterfaceType returnType =
        new ClassOrInterfaceType()
            .setName(JsonbSerializer.class.getSimpleName())
            .setTypeArguments(new ClassOrInterfaceType().setName(getTypeMapperName(type)));
    declaration
        .addMethod("newSerializer", Modifier.Keyword.PROTECTED)
        .addAnnotation(Override.class)
        .setType(returnType)
        .getBody()
        .ifPresent(
            body ->
                body.addStatement(
                    new ReturnStmt(
                        new FieldAccessExpr(
                            new NameExpr(serializerGenerator.getMapperName(type.getElement())),
                            "INSTANCE"))));
  }

  private void addDeserializer(BeanDefinition type) {
    ClassOrInterfaceType returnType =
        new ClassOrInterfaceType()
            .setName(JsonbDeserializer.class.getSimpleName())
            .setTypeArguments(new ClassOrInterfaceType().setName(getTypeMapperName(type)));
    declaration
        .addMethod("newDeserializer", Modifier.Keyword.PROTECTED)
        .addAnnotation(Override.class)
        .setType(returnType)
        .getBody()
        .ifPresent(
            body ->
                body.addStatement(
                    new ReturnStmt(
                        new FieldAccessExpr(
                            new NameExpr(deserializerGenerator.getMapperName(type.getElement())),
                            "INSTANCE"))));
  }

  @Override
  protected String getMapperName(TypeElement type) {
    return (type.getEnclosingElement().getKind().equals(ElementKind.PACKAGE)
            ? ""
            : MoreElements.asType(type.getEnclosingElement()).getSimpleName().toString() + "_")
        + type.getSimpleName()
        + MAPPER_IMPL;
  }

  private Expression addObjectCreationExpr(
      BeanDefinition type, ClassOrInterfaceType returnType, ObjectCreationExpr creationExpr) {
    if (type.getElement().getKind().isClass()) {
      return creationExpr;
    }
    return new CastExpr().setType(returnType).setExpression(creationExpr);
  }

  @Override
  protected void addTypeParam(BeanDefinition type, ClassOrInterfaceDeclaration declaration) {
    if (!type.getElement().getKind().isClass()) {
      declaration
          .getTypeParameters()
          .add(
              new TypeParameter()
                  .setName(new SimpleName("T extends " + type.getElement().getSimpleName())));
    }
  }
}
