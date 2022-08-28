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
package org.kie.workbench.common.stunner.client.json.mapper.apt.context;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.google.auto.common.MoreTypes;
import org.kie.workbench.common.stunner.client.json.mapper.apt.definition.BeanDefinition;
import org.kie.workbench.common.stunner.client.json.mapper.apt.definition.FieldDefinitionFactory;
import org.kie.workbench.common.stunner.client.json.mapper.apt.utils.TypeRegistry;
import org.kie.workbench.common.stunner.client.json.mapper.apt.utils.TypeUtils;

/** @author Dmitrii Tikhomirov Created by treblereel 3/11/20 */
public class GenerationContext {

  private final RoundEnvironment roundEnvironment;
  private final ProcessingEnvironment processingEnv;
  private final TypeRegistry typeRegistry;
  private final TypeUtils typeUtils;
  private final Map<TypeMirror, BeanDefinition> beans = new ConcurrentHashMap<>();

  private final FieldDefinitionFactory fieldDefinitionFactory;

  public GenerationContext(RoundEnvironment roundEnvironment, ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
    this.roundEnvironment = roundEnvironment;
    this.typeRegistry = new TypeRegistry(this);
    this.typeUtils = new TypeUtils(this);
    this.fieldDefinitionFactory = new FieldDefinitionFactory(this);
  }

  public RoundEnvironment getRoundEnvironment() {
    return roundEnvironment;
  }

  public ProcessingEnvironment getProcessingEnv() {
    return processingEnv;
  }

  public TypeRegistry getTypeRegistry() {
    return typeRegistry;
  }

  public TypeUtils getTypeUtils() {
    return typeUtils;
  }

  public BeanDefinition getBeanDefinition(TypeMirror type) {
    if (beans.containsKey(type)) {
      return beans.get(type);
    } else {
      BeanDefinition beanDefinition = new BeanDefinition(MoreTypes.asTypeElement(type), this);
      beans.put(type, beanDefinition);
      return beanDefinition;
    }
  }

  public void addBeanDefinition(TypeElement type) {
    if (!beans.containsKey(type.asType())) {
      BeanDefinition beanDefinition = new BeanDefinition(type, this);
      beans.put(type.asType(), beanDefinition);
    }
  }

  public Collection<BeanDefinition> getBeans() {
    return beans.values();
  }

  public FieldDefinitionFactory getFieldDefinitionFactory() {
    return fieldDefinitionFactory;
  }
}
