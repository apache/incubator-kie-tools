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
import java.util.Collection;

import javax.enterprise.context.Dependent;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.jboss.errai.ioc.client.api.ActivatedBy;

/**
 * Contains all metadata for the bean produced by a {@link Factory}.
 *
 * @see Factory
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface FactoryHandle {

  /**
   * @return The set of {@link Qualifier} annotations for this factory's bean.
   */
  Collection<Annotation> getQualifiers();

  /**
   * @return The set of all types that this factory's bean can be assigned to.
   */
  Collection<Class<?>> getAssignableTypes();

  /**
   * @return This will be the literal class of a type that was type-injectable. If the factory was for a producer
   *         method, this will be the return type. If the factory was for a producer field, this will be for the field
   *         type. For a bean where the specific implementation type is unknown (such as for JsType) this will return
   *         the type that was looked up.
   */
  Class<?> getActualType();

  /**
   * @return The unique name of this factory, used for getting bean instances from {@link ContextManager#getInstance(String)}.
   */
  String getFactoryName();

  /**
   * @return The scope annotation for this type. For pseudo-depdent beans, {@link Dependent} is returned.
   */
  Class<? extends Annotation> getScope();

  /**
   * @return True iff this bean should be initialized immediately after boostrapping completes, rather than lazily on-demand.
   */
  boolean isEager();

  /**
   * @return An implementation type if the factory's bean was annotated with {@link ActivatedBy}. Otherwise {@code null}.
   */
  Class<? extends BeanActivator> getBeanActivatorType();

  /**
   * @return The name of the factory's bean, if it was annotated with {@link Named}.
   */
  String getBeanName();

  /**
   * @return True iff this factory's bean is available for lookup through the bean manager.
   */
  boolean isAvailableByLookup();

}
