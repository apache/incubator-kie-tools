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

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.google.auto.common.MoreTypes;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.EnumYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.EnumYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.processor.context.GenerationContext;

public class EnumBeanFieldDefinition extends FieldDefinition {

  protected EnumBeanFieldDefinition(TypeMirror property, GenerationContext context) {
    super(property, context);
  }

  @Override
  public Expression getFieldDeserializer(PropertyDefinition field, CompilationUnit cu) {
    cu.addImport(EnumYAMLDeserializer.class);
    cu.addImport(MoreTypes.asTypeElement(bean).getQualifiedName().toString());

    MethodCallExpr expr =
        new MethodCallExpr(new NameExpr(EnumYAMLDeserializer.class.getSimpleName()), "newInstance")
            .addArgument(MoreTypes.asTypeElement(bean).getSimpleName().toString() + ".class");

    for (Element enumConstant : MoreTypes.asTypeElement(bean).getEnclosedElements()) {
      if (enumConstant.getKind().equals(ElementKind.ENUM_CONSTANT)) {
        expr.addArgument(bean.toString() + "." + enumConstant);
      }
    }

    return expr;
  }

  @Override
  public Expression getFieldSerializer(PropertyDefinition field, CompilationUnit cu) {
    cu.addImport(EnumYAMLSerializer.class);
    return new FieldAccessExpr(new NameExpr(EnumYAMLSerializer.class.getSimpleName()), "INSTANCE");
  }

  @Override
  public String toString() {
    return "EnumBeanFieldDefinition{" + "bean=" + bean + '}';
  }
}
