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

import org.jboss.errai.ioc.client.Container;
import org.jboss.errai.ioc.client.api.LoadAsync;

/**
 * Provides a single interface for {@link Factory factories} and the
 * {@link ClientBeanManager} to get instances of beans.
 *
 * {@link Factory Factories} contain generated code to request instances of
 * dependencies through calls to {@link #getInstance(String)}. Intances are
 * created by dispatching to the appropriate {@link Context}.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface ContextManager {

  /**
   * Called for each context when the application is bootstrapping. All contexts
   * are added before any instances are requested.
   *
   * @param context
   *          An implementation of {@link Context}.
   */
  void addContext(Context context);

  /**
   * Gets a bean instance by dispatching to the appropriate {@link Context}.
   *
   * @param factoryName
   *          The name of the {@link Factory} that creates the desired bean
   *          instance.
   * @return A bean instance that may or may not be {@link Proxy proxied}. The
   *         number of unique instances returned by this method depends on the
   *         {@link Context} to which the factory with the given name belongs.
   */
  <T> T getInstance(String factoryName);

  /**
   * Gets a contextual bean instance by dispatching to the appropriate {@link Context}.
   *
   * @param factoryName
   *          The name of the {@link Factory} that creates the desired bean
   *          instance.
   * @param typeArgs
   *          The type arguments of the injection site.
   * @param qualifiers
   *          The qualifiers of the injection site.
   * @return A bean instance that may or may not be {@link Proxy proxied}. The
   *         number of unique instances returned by this method depends on the
   *         {@link Context} to which the factory with the given name belongs.
   */
  <T> T getContextualInstance(String factoryName, Class<?>[] typeArgs, Annotation[] qualifiers);

  /**
   * Like {@link #getInstance(String)} except that the returned instance is
   * guaranteed to have been constructed when this method returns, even if it is
   * proxied.
   *
   * @param factoryName
   *          The name of the {@link Factory} that creates the desired bean
   *          instance.
   * @return A bean instance that may or may not be {@link Proxy proxied}. The
   *         number of unique instances returned by this method depends on the
   *         {@link Context} to which the factory with the given name belongs.
   */
  <T> T getEagerInstance(String factoryName);

  /**
   * Like {@link #getInstance(String)} except that this method is guaranteed to
   * return a new instance for every invocation, regardless of the behaviour of
   * the {@link Context} to which the {@link Factory} belongs.
   *
   * @param factoryName
   *          The name of the {@link Factory} that creates the desired bean
   *          instance.
   * @return A new bean instance that may or may not be {@link Proxy proxied}.
   */
  <T> T getNewInstance(String factoryName);

  /**
   * @return A collection of all {@link FactoryHandle FactoryHandles}from all
   *         added {@link Context Contexts}.
   */
  Collection<FactoryHandle> getAllFactoryHandles();

  /**
   * If a bean is {@link #isManaged(Object) managed} then this method will
   * invoke {@link Context#destroyInstance(Object)} for the appropriate context.
   *
   * @param instance
   *          An instance to be destroyed. May or may not be a {@link Proxy}.
   */
  void destroy(Object instance);

  /**
   * @param ref
   *          A reference to an object that may or may not have been created by
   *          a {@link Factory}.
   * @return Returns true iff this is a bean instance or {@link Proxy} that was
   *         created by a {@link Factory} in some {@link Context} that has been
   *         added to this context manager.
   */
  boolean isManaged(Object ref);

  /**
   * Used for adding {@link DestructionCallback destruction callbacks}
   * programmatically at runtime. These callbacks should be invoked before a
   * call to {@link #destroy(Object)} returns.
   *
   * @param instance
   *          An instance managed by a context in this context manager. This may
   *          or may not be a {@link Proxy}.
   * @param callback
   *          A callback to be invoked when {@link #destroyInstance(Object)} is
   *          called for the given instance.
   * @return True iff the callback was successfully registered. Should return
   *         false if the given instance is not managed by any added context.
   */
  boolean addDestructionCallback(Object instance, DestructionCallback<?> callback);

  /**
   * {@link Factory Factories} can store properties associated with instances
   * they've created. This method allows factories to share properties with
   * eachother.
   *
   * @param instance
   *          The bean instance for which the desired property (if it exists) is
   *          associated. May or may not be a {@link Proxy}.
   * @param propertyName
   *          The name of a property associated with an instance.
   * @param type
   *          For convenince. The returned value will be cast to this type.
   * @return If this instance is not managed, return {@code null} . Otherwise,
   *         return the result of
   *         {@link Context#getInstanceProperty(Object, String, Class)} for the
   *         appropriate context.
   */
  <P> P getInstanceProperty(Object instance, String propertyName, Class<P> type);

  /**
   * Called once by the {@link Container} once all {@link Context contexts} have
   * been initialized and added.
   */
  void finishInit();

  /**
   * For adding {@link Factory factories} after {@link #finishInit()} has been
   * called. Used for adding beans with {@link LoadAsync}.
   *
   * @param factory
   *          A {@link Factory} for a type with {@link LoadAsync}.
   */
  void addFactory(Factory<?> factory);

}
