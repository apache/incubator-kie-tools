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

import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.workbench.common.stunner.client.yaml.processor.TypeUtils;
import org.kie.workbench.common.stunner.client.yaml.processor.context.GenerationContext;

/** @author Dmitrii Tikhomirov Created by treblereel 4/1/20 */
public class DefaultBeanFieldDefinition extends FieldDefinition {

  private final TypeUtils typeUtils;

  protected DefaultBeanFieldDefinition(TypeMirror property, GenerationContext context) {
    super(property, context);
    this.typeUtils = context.getTypeUtils();
  }

  @Override
  public Expression getFieldDeserializer(PropertyDefinition field, CompilationUnit cu) {
    return new ObjectCreationExpr()
        .setType(new ClassOrInterfaceType().setName(typeUtils.canonicalDeserializerName(bean)));
  }

  @Override
  public Expression getFieldSerializer(PropertyDefinition field, CompilationUnit cu) {
    return new ObjectCreationExpr()
        .setType(new ClassOrInterfaceType().setName(typeUtils.canonicalSerializerName(bean)));
  }

  @Override
  public String toString() {
    return "DefaultBeanFieldDefinition{" + "bean=" + bean + '}';
  }
}
