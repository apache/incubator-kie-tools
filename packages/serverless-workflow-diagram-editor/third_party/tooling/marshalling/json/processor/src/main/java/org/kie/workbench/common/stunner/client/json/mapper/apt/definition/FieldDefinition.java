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

import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.Statement;
import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;

public abstract class FieldDefinition {

  protected final GenerationContext context;
  protected final TypeMirror property;

  protected FieldDefinition(TypeMirror property, GenerationContext context) {
    this.context = context;
    this.property = property;
  }

  public abstract Statement getFieldDeserializer(PropertyDefinition field, CompilationUnit cu);

  public abstract Statement getFieldSerializer(PropertyDefinition field, CompilationUnit cu);
}
