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

import java.util.stream.Stream;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import jakarta.json.bind.annotation.JsonbTransient;
import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;

public class BeanDefinition {

  private final TypeElement element;
  private final GenerationContext context;

  public BeanDefinition(TypeElement asTypeElement, GenerationContext generationContext) {
    this.element = asTypeElement;
    this.context = generationContext;
  }

  public Stream<PropertyDefinition> getPropertyDefinitionsAsStream() {
    return context.getTypeUtils().getAllFieldsIn(element).stream()
        .filter(field -> !field.getModifiers().contains(Modifier.STATIC))
        .filter(field -> !field.getModifiers().contains(Modifier.FINAL))
        .filter(field -> !field.getModifiers().contains(Modifier.TRANSIENT))
        .filter(field -> field.getAnnotation(JsonbTransient.class) == null)
        .map(
            field -> {
              PropertyDefinition propertyDefinition = new PropertyDefinition(field, context);
              /*                      if (TypeUtils.hasTypeParameter(field.asType())) {
                TypeMirror typeMirror =
                        context
                                .getProcessingEnv()
                                .getTypeUtils()
                                .asMemberOf((DeclaredType) element.asType(), field);
                propertyDefinition.setBean(typeMirror);
              }*/
              return propertyDefinition;
            });
  }

  public String getPackageQualifiedName() {
    return MoreElements.getPackage(element).getQualifiedName().toString();
  }

  public TypeElement getElement() {
    return element;
  }
}
