/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.ioc.client.container;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jboss.errai.ioc.client.QualifierUtil;

/**
 * @see FactoryHandle
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class FactoryHandleImpl implements FactoryHandle {

  private static final List<Annotation> defaultQualifiers = Arrays.asList(QualifierUtil.DEFAULT_QUALIFIERS);

  private List<Annotation> qualifiers;
  private List<Class<?>> assignableTypes;
  private final Class<?> actualType;
  private final String factoryName;
  private final Class<? extends Annotation> scope;
  private final boolean eager;
  private final String beanName;
  private final Class<? extends BeanActivator> activatorType;
  private final boolean availableByLookup;

  public FactoryHandleImpl(final Class<?> actualType, final String factoryName, final Class<? extends Annotation> scope,
          final boolean eager, final String beanName, final boolean availableByLookup, final Class<? extends BeanActivator> activatorType) {
    this.actualType = actualType;
    this.factoryName = factoryName;
    this.scope = scope;
    this.eager = eager;
    this.beanName = beanName;
    this.activatorType = activatorType;
    this.availableByLookup = availableByLookup;
  }

  public FactoryHandleImpl(final Class<?> actualType, final String factoryName, final Class<? extends Annotation> scope, final boolean eager, final String beanName, final boolean availableByLookup ) {
    this(actualType, factoryName, scope, eager, beanName, availableByLookup, null);
  }

  @Override
  public String getBeanName() {
    return beanName;
  }

  @Override
  public Collection<Annotation> getQualifiers() {
    if (qualifiers == null) {
      return Collections.unmodifiableCollection(defaultQualifiers);
    }
    else {
      return Collections.unmodifiableCollection(qualifiers);
    }
  }

  public void setQualifiers(final Annotation[] qualifiers) {
    this.qualifiers = Arrays.asList(qualifiers);
  }

  public void setAssignableTypes(final Class<?>[] assignableTypes) {
    this.assignableTypes = Arrays.asList(assignableTypes);
  }

  @Override
  public Collection<Class<?>> getAssignableTypes() {
    return Collections.unmodifiableCollection(assignableTypes);
  }

  @Override
  public Class<?> getActualType() {
    return actualType;
  }

  @Override
  public String getFactoryName() {
    return factoryName;
  }

  @Override
  public Class<? extends Annotation> getScope() {
    return scope;
  }

  @Override
  public String toString() {
    return "[type=" + actualType + ", name=" + factoryName + ", scope=" + scope.getSimpleName() + ", qualifiers=" + qualifiers + "]";
  }

  @Override
  public boolean isEager() {
    return eager;
  }

  @Override
  public Class<? extends BeanActivator> getBeanActivatorType() {
    return activatorType;
  }

  @Override
  public boolean isAvailableByLookup() {
    return availableByLookup;
  }

}
