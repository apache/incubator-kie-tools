/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kie.workbench.common.stunner.client.yaml.processor.definition;

import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeSerializer;
import org.kie.workbench.common.stunner.client.yaml.processor.context.GenerationContext;

/** @author Dmitrii Tikhomirov Created by treblereel 4/1/20 */
public class BasicTypeFieldDefinition extends FieldDefinition {

  protected BasicTypeFieldDefinition(TypeMirror property, GenerationContext context) {
    super(property, context);
  }

  @Override
  public Expression getFieldDeserializer(PropertyDefinition field, CompilationUnit cu) {
    ObjectCreationExpr expression =
        new ObjectCreationExpr()
            .setType(context.getTypeRegistry().getDeserializer(bean).toString());
    if (field.hasYamlTypeDeserializer()) {
      expression.addArgument(
          field.getFieldYamlTypeDeserializerCreationExpr(
              field.getProperty().getAnnotation(YamlTypeDeserializer.class)));
    }
    return expression;
  }

  @Override
  public Expression getFieldSerializer(PropertyDefinition field, CompilationUnit cu) {
    ObjectCreationExpr expression =
        new ObjectCreationExpr()
            .setType(
                context
                    .getTypeRegistry()
                    .getSerializer(context.getProcessingEnv().getTypeUtils().erasure(bean))
                    .toString());

    if (field.hasYamlTypeSerializer()) {
      expression.addArgument(
          field.getFieldYamlTypeSerializerCreationExpr(
              field.getProperty().getAnnotation(YamlTypeSerializer.class)));
    }
    return expression;
  }

  @Override
  public String toString() {
    return "BasicTypeFieldDefinition{" + "bean=" + bean + '}';
  }
}
