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
import java.util.Set;

import org.jboss.errai.ioc.client.api.ActivatedBy;

/**
 * Definition of a managed bean.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface IOCBeanDef<T> {

  /**
   * @param type
   *          Must not be null.
   * @return True if this bean is assginable to the given type.
   */
  public boolean isAssignableTo(Class<?> type);

  /**
   * Returns the type of the bean.
   *
   * @see #getBeanClass()
   * @return the type of the bean.
   */
  public Class<T> getType();

  /**
   * Returns the actual bean class represented by this bean.
   *
   * @return the actual type of the bean.
   */
  public Class<?> getBeanClass();

  /**
   * Returns the scope of the bean.
   *
   * @returns the annotation type representing the scope of the bean.
   */
  public Class<? extends Annotation> getScope();

  /**
   * Returns any qualifiers associated with the bean.
   *
   * @return Must never be null.
   */
  public Set<Annotation> getQualifiers();

  /**
   * Returns true if the beans qualifiers match the specified set of qualifiers.
   *
   * @param annotations
   *          the qualifiers to compare
   * @return returns whether or not the bean matches the set of qualifiers
   */
  public boolean matches(Set<Annotation> annotations);

  /**
   * Returns the name of the bean.
   *
   * @return the name of the bean. If the bean does not have a name, returns
   *         null.
   */
  public String getName();

  /**
   * Returns true if the bean is activated. All managed beans are activated by
   * default unless a {@link BeanActivator} was specified using
   * {@link ActivatedBy} which will be consulted when invoking this method.
   *
   * @return true if activated, otherwise false.
   */
  public boolean isActivated();

  /**
   * Returns true if this bean definition was discovered and loaded at runtime from
   * an external script.
   * 
   * @return true if dynamic, otherwise false.
   */
  public default boolean isDynamic() {
    return false;
  }

}
