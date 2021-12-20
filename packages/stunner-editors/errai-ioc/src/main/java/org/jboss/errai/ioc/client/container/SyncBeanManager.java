/*
 * Copyright (C) 2013 Red Hat, Inc. and/or its affiliates.
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

/**
 * Contract for injectable client-side instances for run-time bean management in
 * synchronous IOC mode.
 * 
 * @author Mike Brock
 * @author Max Barkley <mbarkley@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface SyncBeanManager extends ClientBeanManager {

  /**
   * Register a bean with the bean manager. The registered bean will be
   * available for lookup, by type and by name if applicable.
   *
   * @param beanDef
   *          The bean def to register.
   */
  <T> void registerBean(SyncBeanDef<T> beanDef);

  /**
   * Register a bean with the bean manager as being assignable to another type.
   *
   * @param beanDef
   *          The bean def to be registered.
   * @param type
   *          The type by which this beanDef will be available for lookup.
   */
  <T> void registerBeanTypeAlias(SyncBeanDef<T> beanDef, final Class<?> type);

  /**
   * Looks up all beans by name. The name is either the fully qualified type
   * name of an assignable type or a given name as specified by
   * {@link javax.inject.Named}.
   *
   * @param name
   *          the fqcn of an assignable type, or a given name specified by
   *          {@link javax.inject.Named}, must not be null.
   *
   * @return and unmodifiable list of all beans with the specified name.
   */
  @SuppressWarnings("rawtypes")
  Collection<SyncBeanDef> lookupBeans(String name);

  /**
   * Looks up all beans of the specified type.
   *
   * @param type
   *          The type of the bean
   *
   * @return An unmodifiable list of all the beans that match the specified
   *         type. Returns an empty list if there is no matching type.
   */
  <T> Collection<SyncBeanDef<T>> lookupBeans(Class<T> type);

  /**
   * Looks up a bean reference based on type and qualifiers. Returns
   * <tt>null</tt> if there is no type associated with the specified
   *
   * @param type
   *          The type of the bean
   * @param qualifiers
   *          qualifiers to match
   *
   * @return An unmodifiable list of all beans which match the specified type
   *         and qualifiers. Returns an empty list if no beans match.
   */
  <T> Collection<SyncBeanDef<T>> lookupBeans(Class<T> type, Annotation... qualifiers);

  /**
   * Looks up a bean reference based on type and qualifiers. Returns
   * <tt>null</tt> if there is no type associated with the specified
   *
   * @param type
   *          The type of the bean
   * @param qualifiers
   *          qualifiers to match
   * @param <T>
   *          The type of the bean
   *
   * @return An instance of the {@link IOCSingletonBean} for the matching type
   *         and qualifiers. Throws an {@link IOCResolutionException} if there
   *         is a matching type but none of the qualifiers match or if more than
   *         one bean matches.
   */
  <T> SyncBeanDef<T> lookupBean(Class<T> type, Annotation... qualifiers);
}
