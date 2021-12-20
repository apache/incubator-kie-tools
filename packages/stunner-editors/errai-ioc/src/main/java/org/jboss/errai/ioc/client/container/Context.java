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
import java.util.Optional;

import org.jboss.errai.ioc.client.Container;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.ScopeContext;

/**
 * A {@link Context} is responsible for managing the creation and destruction of
 * beans for a given set of scopes.
 *
 * For the most part a {@link Context} is classified by the behaviour of calling
 * {@link #getInstance(String)}. For example the {@link DependentScopeContext}
 * returns a new instance everytime {@link #getInstance(String)} is invoked,
 * whereas the {@link ApplicationScopedContext} returns the same instance for
 * every invocation.
 *
 * At runtime the {@link Container} all factories with their respective
 * {@link Context} via {@link #registerFactory(Factory)} before any beans are
 * created.
 *
 * Application code should not interact directly with a {@link Context}, but
 * should instead use the {@link ClientBeanManager} for programmatic creation of
 * beans.
 *
 * @see ScopeContext
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface Context {

  /**
   * This method will only be called once at runtime, when an application is
   * bootstrapping, before any bean instances are created.
   *
   * @param contextManager
   *          A reference to the application's {@link ContextManager}.
   */
  void setContextManager(ContextManager contextManager);

  /**
   * @return A reference to the {@link ContextManager} that was given as a
   *         parameter of {@link #setContextManager(ContextManager)}.
   */
  ContextManager getContextManager();

  /**
   * When an application is boostrapping, this method is called for every
   * {@link Factory} this {@link Context} is responsible for. This happens
   * before any bean instances are created.
   *
   * Once a {@link Factory} is registered, it's beans should be available
   * through calls to {@link #getInstance(String)} with the respective
   * {@link FactoryHandle#getFactoryName() name}.
   *
   * @param factory
   *          A {@link Factory} that this {@link Context} is respon
   */
  <T> void registerFactory(Factory<T> factory);

  /**
   * Gets and instance of a bean from a {@link Factory} with the given name
   * (that was previously {@link #registerFactory(Factory) registered}).
   *
   * @param factoryName
   *          The {@link FactoryHandle#getFactoryName() name} of the for the
   *          desired bean instance.
   * @return An instance of a bean from the {@link Factory} with the given
   *         {@link FactoryHandle#getFactoryName() name}. This instance may or
   *         may not be {@link Proxy proxied}.
   */
  <T> T getInstance(String factoryName);

  /**
   * Destroys a bean instance if it is {@link #isManaged(Object) managed} by
   * this {@link Context}. For unmanaged beans, this is a noop.
   *
   * @param instance
   *          A bean instance to be destroyed, if {@link #isManaged(Object)
   *          managed} by this {@link Context}.
   */
  void destroyInstance(Object instance);

  /**
   * When a call to {@link #getInstance(String)} returns a {@link Proxy}, the
   * proxy will eventually need to be populated. This is done in generated code
   * by the {@link ProxyHelper} via a call to this method.
   *
   * @param factoryName
   *          The {@link FactoryHandle#getFactoryName() name} of a factory from
   *          which the returned bean instance is created.
   * @return An instance of a bean that must not be a {@link Proxy}.
   */
  <T> T getActiveNonProxiedInstance(String factoryName);

  /**
   * @return A representative annotation for this {@link Context}. This is only
   *         used for logging and error reporting.
   */
  Class<? extends Annotation> getScope();

  /**
   * @param scope
   *          A scope annotation type.
   * @return True iff this context implementation handles the given scope.
   */
  boolean handlesScope(Class<? extends Annotation> scope);

  /**
   * This method is called when a {@link ProxyHelper} attempts to populate a
   * proxy. It is not called when a {@link Proxy} already contains a proxied
   * instance. Therefore, when a {@link Context} becomes inactive it is its
   * responsibility to {@link ProxyHelper#clearInstance() clear instances} from
   * {@link Proxy proxies}.
   *
   * For simple contexts such as {@link ApplicationScopedContext} and
   * {@link DependentScopeContext}, this method will always return true. This
   * method exists to allow for future scopes of higher complexity.
   *
   * @return True iff this {@link Context} is currently active.
   */
  boolean isActive();

  /**
   * @return Get all of the {@link Factory factories} that have been
   *         {@link #registerFactory(Factory) registered} with this context.
   */
  Collection<Factory<?>> getAllFactories();

  /**
   * Regardless of the behaviour of {@link #getInstance(String)} this method
   * must return a previously non-existent bean instance (to support
   * {@link SyncBeanDef#newInstance()}.
   *
   * @param factoryName
   *          The {@link FactoryHandle#getFactoryName() name} of a factory from
   *          which the returned bean instance is created.
   * @return A new bean instance that may or may not be a {@link Proxy}.
   */
  <T> T getNewInstance(String factoryName);

  /**
   * This method should return true if a given reference is a direct bean
   * instance or a {@link Proxy}, provided the bean is from a {@link Factory}
   * registered with this context.
   *
   * @param ref
   *          An object reference that may or may not be a {@link Proxy}.
   * @return True iff this object is managed by this {@link Context}.
   */
  boolean isManaged(Object ref);

  /**
   * Used for adding {@link DestructionCallback destruction callbacks}
   * programmatically at runtime. These callbacks should be invoked before a
   * call to {@link #destroyInstance(Object)} returns.
   *
   * @param instance
   *          An instance managed by this context. This may or may not be a
   *          {@link Proxy}.
   * @param callback
   *          A callback to be invoked when {@link #destroyInstance(Object)} is
   *          called for the given instance.
   * @return True iff the callback was successfully registered. Should return
   *         false if the given instance is not managed by this context.
   */
  boolean addDestructionCallback(Object instance, DestructionCallback<?> callback);

  /**
   * {@link Factory Factories} can store properties associated with instances
   * they've created. This method allows factories from other contexts to access
   * these stored values.
   *
   * @param instance
   *          The bean instance for which the desired property (if it exists) is
   *          associated. May or may not be a {@link Proxy}.
   * @param propertyName
   *          The name of a property associated with an instance.
   * @param type
   *          For convenince. The returned value will be cast to this type.
   * @return If this instance is not managed by this context return {@code null}
   *         . Otherwise, return the result of
   *         {@link Factory#getReferenceAs(Object, String, Class)} for the
   *         {@link Factory} that created this bean instance.
   */
  <P> P getInstanceProperty(Object instance, String propertyName, Class<P> type);

  /**
   * Some contexts support contextual instances (from a {@link ContextualTypeProvider}).
   * This method provides access to that aspect of the context, if supported.
   *
   * @return An option containing this context if contextual instances are supported,
   *         or else an empty option.
   */
  Optional<HasContextualInstanceSupport> withContextualInstanceSupport();

}
