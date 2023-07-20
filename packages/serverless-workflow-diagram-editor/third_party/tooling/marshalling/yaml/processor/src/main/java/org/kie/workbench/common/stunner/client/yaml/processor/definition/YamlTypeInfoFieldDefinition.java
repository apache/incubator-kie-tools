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

package org.kie.workbench.common.stunner.client.yaml.processor.definition;

import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlSubtype;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeInfo;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.bean.YamlSubtypeDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.bean.YamlSubtypeSerializer;
import org.kie.workbench.common.stunner.client.yaml.processor.context.GenerationContext;

public class YamlTypeInfoFieldDefinition extends FieldDefinition {

  private final YamlTypeInfo yamlTypeInfo;

  public YamlTypeInfoFieldDefinition(TypeMirror property, GenerationContext context) {
    super(property, context);
    yamlTypeInfo = MoreTypes.asElement(bean).getAnnotation(YamlTypeInfo.class);
  }

  @Override
  public Expression getFieldDeserializer(PropertyDefinition field, CompilationUnit cu) {
    cu.addImport(YamlSubtypeDeserializer.class);
    cu.addImport(YamlSubtypeDeserializer.Info.class);
    ClassOrInterfaceType typeOf =
        new ClassOrInterfaceType()
            .setName(YamlSubtypeDeserializer.class.getSimpleName())
            .setTypeArguments(
                new ClassOrInterfaceType()
                    .setName(context.getProcessingEnv().getTypeUtils().erasure(bean).toString()));

    ObjectCreationExpr objectCreationExpr =
        new ObjectCreationExpr()
            .setType(typeOf)
            .addArgument(new StringLiteralExpr(yamlTypeInfo.key()));

    for (YamlSubtype yamlSubtype : yamlTypeInfo.value()) {
      try {
        yamlSubtype.type();
      } catch (MirroredTypeException e) {
        objectCreationExpr.addArgument(
            new ObjectCreationExpr()
                .setType(YamlSubtypeDeserializer.Info.class)
                .addArgument(new StringLiteralExpr(yamlSubtype.alias()))
                .addArgument(
                    new FieldAccessExpr(new NameExpr(e.getTypeMirror().toString()), "class"))
                .addArgument(
                    new ObjectCreationExpr()
                        .setType(
                            new ClassOrInterfaceType()
                                .setName(
                                    context
                                        .getTypeUtils()
                                        .canonicalDeserializerName(e.getTypeMirror())))));
      }
    }

    return objectCreationExpr;
  }

  @Override
  public Expression getFieldSerializer(PropertyDefinition field, CompilationUnit cu) {
    cu.addImport(YamlSubtypeSerializer.class);
    cu.addImport(YamlSubtypeSerializer.Info.class);
    ClassOrInterfaceType typeOf =
        new ClassOrInterfaceType()
            .setName(YamlSubtypeSerializer.class.getSimpleName())
            .setTypeArguments(
                new ClassOrInterfaceType()
                    .setName(context.getProcessingEnv().getTypeUtils().erasure(bean).toString()));

    ObjectCreationExpr objectCreationExpr =
        new ObjectCreationExpr()
            .setType(typeOf)
            .addArgument(new StringLiteralExpr(yamlTypeInfo.key()));

    for (YamlSubtype yamlSubtype : yamlTypeInfo.value()) {
      try {
        yamlSubtype.type();
      } catch (MirroredTypeException e) {
        objectCreationExpr.addArgument(
            new ObjectCreationExpr()
                .setType(YamlSubtypeSerializer.Info.class)
                .addArgument(new StringLiteralExpr(yamlSubtype.alias()))
                .addArgument(
                    new FieldAccessExpr(new NameExpr(e.getTypeMirror().toString()), "class"))
                .addArgument(
                    new ObjectCreationExpr()
                        .setType(
                            new ClassOrInterfaceType()
                                .setName(
                                    context
                                        .getTypeUtils()
                                        .canonicalSerializerName(e.getTypeMirror())))));
      }
    }

    return objectCreationExpr;
  }
}
