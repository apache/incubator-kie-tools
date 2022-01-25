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
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;

import org.jboss.errai.ioc.client.api.ContextualTypeProvider;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

/**
 * Creates proxies and wires dependencies for a bean. The abstract methods in
 * this class are implemented by code generation. Because of this, care should
 * be taken when modifying the names of fields or parameters in this type.
 *
 * @param <T>
 *          The type of the bean that this factory creates.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public abstract class Factory<T> {

  /*
   * Do not remove! Used in generated code.
   */
  protected final Factory<T> thisInstance = this;

  /*
   * Do not remove! Used in generated code.
   */
  protected final FactoryHandleImpl handle;


  private final Map<T, Map<String, Object>> referenceMaps = new IdentityHashMap<>();
  private final SetMultimap<T, Object> dependentScopedDependencies = Multimaps
          .newSetMultimap(new IdentityHashMap<T, Collection<Object>>(), new Supplier<Set<Object>>() {
            @Override
            public Set<Object> get() {
              return Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
            }
          });

  private T incompleteInstance;

  protected Factory() {
    this.handle = null;
  }

  protected Factory(final FactoryHandleImpl handle) {
    this.handle = handle;
  }

  /**
   * At runtime the init method is called once after all factories and
   * {@link Context contexts} have been registered. This allows decorators an
   * opportunity to register a bean type for services that may lookup instances
   * on demand.
   *
   * @param context
   *          Some decorators may wish to register callbacks that create an
   *          instance of the bean. This should be done using
   *          {@link Context#getInstance(String)}.
   */
  public void init(final Context context) {}

  /**
   * This method is invoked whenever an actual instance of a bean must be
   * constructed. If a bean is proxied, this will likely happen on the first
   * invocation of a method on the proxy. Otherwise this will occur when the
   * bean is injected into another type.
   *
   * This method always returns an unproxied, fully wired instance of a type.
   *
   * @param contextManager
   *          For requesting instances from other factories. Never {@code null}.
   * @return A fully wired, unproxied instance of a bean.
   */
  public T createInstance(final ContextManager contextManager) {
    throw new UnsupportedOperationException("The factory, " + getClass().getSimpleName() + ", only supports contextual instances.");
  }

  /**
   * Like {@link #createInstance(ContextManager)} but with contextual paramters for factories backed by a
   * {@link ContextualTypeProvider}.
   */
  public T createContextualInstance(final ContextManager contextManager, final Class<?>[] typeArgs, final Annotation[] qualifiers) {
    throw new UnsupportedOperationException("The factory, " + getClass().getSimpleName() + ", does not support contextual instances.");
  }

  public void invokePostConstructs(final T instance) {}

  public void setReference(final T instance, final String referenceName, final Object ref) {
    final Map<String, Object> instanceRefMap = getInstanceRefMap(instance);
    instanceRefMap.put(referenceName, ref);
  }

  private Map<String, Object> getInstanceRefMap(final T instance) {
    Map<String, Object> map = referenceMaps.get(maybeUnwrapProxy(instance));
    if (map == null) {
      map = new HashMap<>();
      referenceMaps.put(instance, map);
    }

    return map;
  }

  @SuppressWarnings("unchecked")
  public <P> P getReferenceAs(final T instance, final String referenceName, final Class<P> type) {
    return (P) getInstanceRefMap(maybeUnwrapProxy(instance)).get(referenceName);
  }

  public Proxy<T> createProxy(final Context context) {
    return null;
  }

  public FactoryHandle getHandle() {
    return handle;
  }

  protected <D> D registerDependentScopedReference(final T instance, final D dependentScopedBeanRef) {
    dependentScopedDependencies.put(maybeUnwrapProxy(instance), dependentScopedBeanRef);

    return dependentScopedBeanRef;
  }

  /**
   * This method performs any cleanup required for destroying a type. It will
   * invoke generated statements from decorators, invoke disposers or predestroy
   * methods, and destroy and {@link Dependent} scoped dependencies.
   *
   * @param instance The instance being destroyed.
   * @param contextManager For destroying dependencies.
   */
  @SuppressWarnings("unchecked")
  public void destroyInstance(final Object instance, final ContextManager contextManager) {
    final Object unwrapped = maybeUnwrapProxy(instance);
    generatedDestroyInstance(unwrapped, contextManager);
    referenceMaps.remove(unwrapped);
    for (final Object depRef : dependentScopedDependencies.get((T) unwrapped)) {
      contextManager.destroy(depRef);
    }
    dependentScopedDependencies.removeAll(instance);
  }

  protected void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {}

  @SuppressWarnings("unchecked")
  public static <P> P maybeUnwrapProxy(final P instance) {
    if (instance instanceof Proxy) {
      return (P) ((Proxy<P>) instance).unwrap();
    }
    else {
      return instance;
    }
  }

  public T getIncompleteInstance() {
    return incompleteInstance;
  }

  protected void setIncompleteInstance(final T instance) {
    incompleteInstance = instance;
  }

}
