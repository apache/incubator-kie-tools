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


package org.jboss.errai.validation.rebind;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import javax.validation.Constraint;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.groups.Default;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.validation.client.GwtValidation;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.config.util.ClassScanner;
import org.jboss.errai.ioc.util.PropertiesUtil;
import org.jboss.errai.reflections.util.SimplePackageFilter;

import static java.util.stream.Collectors.toSet;

/**
 * Generates the GWT {@link Validator} interface based on validation
 * annotations.
 *
 * @author Johannes Barop <jb@barop.de>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
class GwtValidatorGenerator {

  private static final String DENYLIST_PROPERTY = "errai.validation.denylist";

  private static final Set<MetaClass> GLOBAL_CONSTRAINTS = Stream.of(
          javax.validation.constraints.AssertFalse.class,
          javax.validation.constraints.AssertTrue.class,
          javax.validation.constraints.DecimalMax.class,
          javax.validation.constraints.DecimalMin.class,
          javax.validation.constraints.Digits.class,
          // javax.validation.constraints.Email.class,
          javax.validation.constraints.Future.class,
          // javax.validation.constraints.FutureOrPresent.class,
          javax.validation.constraints.Max.class,
          javax.validation.constraints.Min.class,
          // javax.validation.constraints.Negative.class,
          // javax.validation.constraints.NegativeOrZero.class,
          // javax.validation.constraints.NotBlank.class,
          // javax.validation.constraints.NotEmpty.class,
          javax.validation.constraints.NotNull.class,
          javax.validation.constraints.Null.class,
          javax.validation.constraints.Past.class,
          // javax.validation.constraints.PastOrPresent.class,
          javax.validation.constraints.Pattern.class,
          // javax.validation.constraints.Positive.class,
          // javax.validation.constraints.PositiveOrZero.class,
          javax.validation.constraints.Size.class,
          org.hibernate.validator.constraints.CreditCardNumber.class,
          org.hibernate.validator.constraints.Email.class,
          org.hibernate.validator.constraints.Length.class,
          org.hibernate.validator.constraints.NotBlank.class,
          org.hibernate.validator.constraints.NotEmpty.class,
          org.hibernate.validator.constraints.Range.class,
          org.hibernate.validator.constraints.ScriptAssert.class,
          org.hibernate.validator.constraints.URL.class)
          .map(MetaClassFactory::get)
          .collect(toSet());

  public ClassStructureBuilder<?> generate(final GeneratorContext context) {
    final Collection<MetaClass> constraintAnnotations =  ClassScanner.getTypesAnnotatedWith(Constraint.class, context);

    final Set<MetaClass> allConstraintAnnotations = new HashSet<>();
    allConstraintAnnotations.addAll(GLOBAL_CONSTRAINTS);
    allConstraintAnnotations.addAll(constraintAnnotations);
    
    final SetMultimap<MetaClass, Annotation> constraintAnnotationsByBeans = getConstraintAnnotationsByBeans(allConstraintAnnotations, context);
    final Set<Class<?>> beans = extractValidatableBeans(constraintAnnotationsByBeans.keySet(), context);
    final Set<Class<?>> groups = extractValidationGroups(constraintAnnotationsByBeans.values());
    
    final Set<Class<?>> filteredBeans = new HashSet<>();
    final SimplePackageFilter filter = new SimplePackageFilter(PropertiesUtil.getPropertyValues(DENYLIST_PROPERTY, " "));
    for (final Class<?> bean : beans) {
      if (!filter.apply(bean.getName())) {
        filteredBeans.add(bean);
      }
    }

    if (filteredBeans.isEmpty() || groups.isEmpty()) {
      // Nothing to validate
      return null;
    }

    final ClassStructureBuilder<?> builder = ClassBuilder.define("Gwt" + Validator.class.getSimpleName())
            .publicScope()
            .interfaceDefinition()
            .implementsInterface(Validator.class)
            .body();

    builder.getClassDefinition().addAnnotation(new GwtValidation() {
      @Override
      public Class<?>[] value() {
        return filteredBeans.toArray(new Class<?>[0]);
      }

      @Override
      public Class<?>[] groups() {
        return groups.toArray(new Class<?>[0]);
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return GwtValidation.class;
      }
    });

    return builder;
  }

  @SuppressWarnings("unchecked")
  private SetMultimap<MetaClass, Annotation> getConstraintAnnotationsByBeans(Collection<MetaClass> constraintAnnotations, GeneratorContext context) {
    final SetMultimap<MetaClass, Annotation> beans = HashMultimap.create();

    for (final MetaClass annotation : constraintAnnotations) {
      for (final MetaField field : ClassScanner.getFieldsAnnotatedWith((Class<? extends Annotation>) annotation.asClass(), null, context)) {
        beans.put(field.getDeclaringClass(), field.getAnnotation((Class<? extends Annotation>) annotation.asClass()));
      }
      for (final MetaMethod method : ClassScanner.getMethodsAnnotatedWith((Class<? extends Annotation>) annotation.asClass(), null, context)) {
        beans.put(method.getDeclaringClass(), method.getAnnotation((Class<? extends Annotation>) annotation.asClass()));
      }
      for (final MetaClass type : ClassScanner.getTypesAnnotatedWith((Class<? extends Annotation>) annotation.asClass(), null, context)) {
        beans.put(type, type.getAnnotation((Class<? extends Annotation>) annotation.asClass()));
      }
    }

    return beans;
  }

  private Set<Class<?>> extractValidatableBeans(final Set<MetaClass> beans, final GeneratorContext context) {
    final Set<Class<?>> allBeans = new HashSet<>();
    
    for (final MetaClass bean : beans) {
      allBeans.add(bean.asClass());
    }
    
    for (final MetaField field : ClassScanner.getFieldsAnnotatedWith(Valid.class, null, context)) {
      allBeans.add(field.getDeclaringClass().asClass());
      allBeans.add(field.getType().asClass());
    }
    for (final MetaMethod method : ClassScanner.getMethodsAnnotatedWith(Valid.class, null, context)) {
      allBeans.add(method.getDeclaringClass().asClass());
      allBeans.add(method.getReturnType().asClass());
    }
    
    return allBeans;
  }

  private Set<Class<?>> extractValidationGroups(Collection<Annotation> constraintAnnotationInstances) {
    final Set<Class<?>> groups = new HashSet<>();

    for (final Annotation instance : constraintAnnotationInstances) {
      try {
        final Method method = instance.getClass().getMethod("groups", (Class<?>[]) null);
        final Class<?>[] ret = (Class<?>[]) method.invoke(instance, (Object[]) null);
        if (ret.length != 0) {
          groups.addAll(Arrays.asList(ret));
        } else {
          groups.add(Default.class);
        }
      }
      catch (final NoSuchMethodException e) {
        throw new RuntimeException("Error finding groups() parameter in " + instance.getClass().getName(), e);
      }
      catch (final InvocationTargetException | IllegalAccessException e) {
        throw new RuntimeException("Error invoking groups() parameter in " + instance.getClass().getName(), e);
      }
    }

    return groups;
  }
}
