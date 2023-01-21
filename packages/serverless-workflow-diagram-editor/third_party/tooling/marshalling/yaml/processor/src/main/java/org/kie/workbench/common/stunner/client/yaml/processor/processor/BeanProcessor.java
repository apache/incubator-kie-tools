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

package org.kie.workbench.common.stunner.client.yaml.processor.processor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import com.google.auto.common.MoreTypes;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlSubtype;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTransient;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeInfo;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeSerializer;
import org.kie.workbench.common.stunner.client.yaml.processor.TypeUtils;
import org.kie.workbench.common.stunner.client.yaml.processor.context.GenerationContext;
import org.kie.workbench.common.stunner.client.yaml.processor.exception.GenerationException;
import org.kie.workbench.common.stunner.client.yaml.processor.generator.MapperGenerator;
import org.kie.workbench.common.stunner.client.yaml.processor.logger.TreeLogger;

/** @author Dmitrii Tikhomirov Created by treblereel 3/11/20 */
public class BeanProcessor {

  private final GenerationContext context;
  private final TreeLogger logger;
  private final Set<TypeElement> annotatedBeans;
  private final Set<TypeElement> beans = new HashSet<>();
  private final TypeUtils typeUtils;
  private final MapperGenerator mapperGenerator;

  public BeanProcessor(
      GenerationContext context, TreeLogger logger, Set<TypeElement> annotatedBeans) {
    this.context = context;
    this.logger = logger;
    this.annotatedBeans = annotatedBeans;
    this.typeUtils = context.getTypeUtils();
    this.mapperGenerator = new MapperGenerator(context, logger);
  }

  public void process() {
    annotatedBeans.forEach(this::processBean);
    beans.forEach(context::addBeanDefinition);
    context.getBeans().stream()
        .filter(
            beanDefinition -> beanDefinition.getElement().getAnnotation(YamlTypeInfo.class) == null)
        .forEach(mapperGenerator::generate);
  }

  private void processBean(TypeElement bean) {
    if (!(beans.contains(bean) || typeUtils.isObject(bean.asType()))) {
      beans.add(checkBean(bean));
      context.getTypeUtils().getAllFieldsIn(bean).forEach(this::processField);
    }
  }

  private void processField(VariableElement field) {
    checkField(field);
    checkTypeAndAdd(field.asType());
  }

  private void checkTypeAndAdd(TypeMirror type) {
    if (context
            .getTypeRegistry()
            .get(context.getProcessingEnv().getTypeUtils().erasure(type).toString())
        == null) {
      if (type.getKind().equals(TypeKind.ARRAY)) {
        ArrayType arrayType = (ArrayType) type;

        if (!context.getTypeUtils().isSimpleType(arrayType.getComponentType())) {
          if (!MoreTypes.asElement(arrayType.getComponentType())
              .getKind()
              .equals(ElementKind.ENUM)) {
            processBean(typeUtils.toTypeElement(arrayType.getComponentType()));
          }
        }
        return;
      } else if (MoreTypes.isType(type)) {
        if (!MoreTypes.asElement(type).getKind().equals(ElementKind.ENUM)) {
          processBean(typeUtils.toTypeElement(type));
        }
      }
    }

    if (context.getTypeUtils().isCollection(type)) {
      DeclaredType collection = (DeclaredType) type;
      collection.getTypeArguments().forEach(this::checkTypeAndAdd);
    }

    if (context.getTypeUtils().isMap(type)) {
      DeclaredType collection = (DeclaredType) type;
      collection.getTypeArguments().forEach(this::checkTypeAndAdd);
    }
    if (type.getKind().isPrimitive() || type.getKind().equals(TypeKind.ARRAY)) {
      return;
    }

    if (MoreTypes.asElement(type).getAnnotation(YamlTypeInfo.class) != null) {

      YamlTypeInfo yamlTypeInfo = MoreTypes.asElement(type).getAnnotation(YamlTypeInfo.class);
      for (YamlSubtype yamlSubtype : yamlTypeInfo.value()) {
        try {
          yamlSubtype.type();
        } catch (MirroredTypeException e) {
          checkTypeAndAdd(e.getTypeMirror());
        }
      }
    }
  }

  private boolean checkField(VariableElement field) {
    if (field.getModifiers().contains(Modifier.STATIC)
        || field.getModifiers().contains(Modifier.TRANSIENT)
        || field.getAnnotation(YamlTransient.class) != null
        || field.getModifiers().contains(Modifier.FINAL)) {
      return false;
    }

    if (context
        .getProcessingEnv()
        .getTypeUtils()
        .isSameType(field.asType(), typeUtils.getObject())) {
      if (field.getAnnotation(YamlTypeSerializer.class) == null
          && field.getAnnotation(YamlTypeDeserializer.class) == null) {
        throw new GenerationException(
            String.format(
                "Field of type Object must be annotated with @YamlTypeSerializer && @YamlTypeDeserializer at [%s.%s]",
                field.getEnclosingElement(), field.toString()));
      }
    }

    if (!field.getModifiers().contains(Modifier.PRIVATE)
        || typeUtils.hasGetter(field) && typeUtils.hasSetter(field)) {
      return true;
    }

    if (!typeUtils.hasGetter(field)) {
      throw new GenerationException(
          String.format(
              "Unable to find suitable getter for [%s] in [%s]",
              field.getSimpleName(), field.getEnclosingElement()));
    }

    if (!typeUtils.hasSetter(field)) {
      throw new GenerationException(
          String.format(
              "Unable to find suitable setter for [%s] in [%s]",
              field.getSimpleName(), field.getEnclosingElement()));
    }
    return true;
  }

  private TypeElement checkBean(TypeElement type) {
    if (type.getModifiers().contains(Modifier.PRIVATE)) {
      throw new GenerationException("A @YAMLMapper bean [" + type + "] must be public");
    }

    List<ExecutableElement> constructors = ElementFilter.constructorsIn(type.getEnclosedElements());
    if (!constructors.isEmpty()) {
      long nonArgConstructorCount =
          constructors.stream()
              .filter(constr -> !constr.getModifiers().contains(Modifier.PRIVATE))
              .filter(constr -> constr.getParameters().isEmpty())
              .count();
      if (nonArgConstructorCount != 1) {
        throw new GenerationException(
            "A @YAMLMapper bean [" + type + "] must have a non-private non-arg constructor");
      }
    }
    return type;
  }
}
