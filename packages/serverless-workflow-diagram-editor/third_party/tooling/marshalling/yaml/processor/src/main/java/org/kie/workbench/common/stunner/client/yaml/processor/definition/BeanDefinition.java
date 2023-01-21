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

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTransient;
import org.kie.workbench.common.stunner.client.yaml.processor.context.GenerationContext;

/** @author Dmitrii Tikhomirov Created by treblereel 4/1/20 */
public class BeanDefinition extends Definition {

  private final TypeElement element;
  private Set<PropertyDefinition> properties;

  public BeanDefinition(TypeElement element, GenerationContext context) {
    super(element.asType(), context);
    this.element = element;

    loadProperties();
  }

  private void loadProperties() {
    properties =
        context.getTypeUtils().getAllFieldsIn(element).stream()
            .filter(field -> !field.getModifiers().contains(Modifier.STATIC))
            .filter(field -> !field.getModifiers().contains(Modifier.FINAL))
            .filter(field -> !field.getModifiers().contains(Modifier.TRANSIENT))
            .filter(field -> field.getAnnotation(YamlTransient.class) == null)
            .map(field -> new PropertyDefinition(field, context))
            .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<PropertyDefinition> getFields() {
    if (properties == null) {
      getFields();
    }
    return properties;
  }

  @Override
  public int hashCode() {
    return Objects.hash(element);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BeanDefinition)) {
      return false;
    }
    BeanDefinition that = (BeanDefinition) o;
    return Objects.equals(element, that.element);
  }

  @Override
  public String toString() {
    return "BeanDefinition{" + "element=" + element + '}';
  }

  public String getRootElement() {
    return getElement().getSimpleName().toString();
  }

  public TypeElement getElement() {
    return element;
  }

  public String getSimpleName() {
    return getElement().getSimpleName().toString();
  }

  public String getQualifiedName() {
    return getElement().getQualifiedName().toString();
  }

  @Override
  public TypeMirror getBean() {
    return bean;
  }
}
