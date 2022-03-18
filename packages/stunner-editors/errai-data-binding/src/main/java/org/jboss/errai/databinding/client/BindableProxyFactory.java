/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.databinding.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * Provides access to the generated proxies for {@link Bindable} types.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class BindableProxyFactory {
  private static Map<Class<?>, BindableProxyProvider> bindableProxyProviders = new HashMap<Class<?>, BindableProxyProvider>();
  private static Map<String, Class<?>> bindableTypes = new HashMap<String, Class<?>>();
  private static Map<Class<?>, BindableProxyProvider> builtinProxyProviders = new HashMap<Class<?>, BindableProxyProvider>();
  private static Map<Object, BindableProxy<?>> proxies = new IdentityHashMap<Object, BindableProxy<?>>();

  static {
    builtinProxyProviders.put(List.class, new BindableProxyProvider() {

      @SuppressWarnings("unchecked")
      @Override
      public BindableProxy<?> getBindableProxy(final Object model) {
        return new BindableListWrapper<Object>((List<Object>) model);
      }

      @Override
      public BindableProxy<?> getBindableProxy() {
        return new BindableListWrapper<>(new ArrayList<>());
      }
    });
  }

  /**
   * Returns a new proxy for the provided model instance. Changes to the proxy's state will result
   * in updates on the widget given the corresponding property was bound.
   *
   * @param <T>
   *          the bindable type
   * @param model
   *          The model instance to proxy, must not be null.
   * @return proxy that can be used in place of the model instance.
   */
  @SuppressWarnings("unchecked")
  public static <T> T getBindableProxy(T model) {
    Assert.notNull(model);
    
    if (model instanceof BindableProxy)
      return model;

    BindableProxy<?> proxy = proxies.get(model);
    if (proxy == null) {
      final Class<? extends Object> modelClass = (model instanceof List ? List.class : model.getClass());
      final BindableProxyProvider proxyProvider = getBindableProxyProvider(modelClass);
      proxy = proxyProvider.getBindableProxy(model);
      if (proxy == null) {
        throw new RuntimeException("No proxy instance provided for bindable type: " + modelClass.getName());
      }
      proxies.put(model, proxy);
    }
    return (T) proxy;
  }

  /**
   * Returns a proxy for a newly created model instance of the provided type. Changes to the proxy's
   * state will result in updates on the component given the corresponding property was bound.
   *
   * @param bindableType
   *          the bindable type
   * @return proxy that can be used in place of the model instance.
   */
  @SuppressWarnings("unchecked")
  public static <T> T getBindableProxy(Class<T> bindableType) {
    final BindableProxyProvider proxyProvider = getBindableProxyProvider(bindableType);

    final BindableProxy<?> proxy = proxyProvider.getBindableProxy();
    if (proxy == null) {
      throw new RuntimeException("No proxy instance provided for bindable type: " + bindableType.getName());
    }

    return (T) proxy;
  }

  /**
   * Returns a proxy for a newly created model instance of the provided type. Changes to the proxy's
   * state will result in updates on the widget given the corresponding property was bound.
   *
   * @param bindableType
   *          The fully qualified name of the bindable type
   * @return proxy that can be used in place of the model instance.
   */
  public static BindableProxy<?> getBindableProxy(String bindableType) {
    final Class<?> bindableClass = bindableTypes.get(bindableType);
    return (BindableProxy<?>) getBindableProxy(bindableClass);
  }

  private static BindableProxyProvider getBindableProxyProvider(Class<?> bindableType) {
    if (bindableProxyProviders.isEmpty()) {
      throw new RuntimeException("There are no proxy providers for bindable types registered yet.");
    }

    BindableProxyProvider proxyProvider = bindableProxyProviders.get(bindableType);
    if (proxyProvider == null) {
      proxyProvider = builtinProxyProviders.get(bindableType);
    }
    if (proxyProvider == null) {
      throw new RuntimeException("No proxy provider found for bindable type: " + bindableType.getName());
    }

    return proxyProvider;
  }

  /**
   * Registers a generated bindable proxy. This method is called by the generated
   * BindableProxyLoader.
   *
   * @param proxyType
   *          The bindable type, must not be null.
   * @param proxyProvider
   *          The proxy provider for the generated bindable proxy, must not be null.
   */
  public static void addBindableProxy(Class<?> proxyType, BindableProxyProvider proxyProvider) {
    Assert.notNull(proxyType);
    Assert.notNull(proxyProvider);

    bindableTypes.put(proxyType.getName(), proxyType);
    bindableProxyProviders.put(proxyType, proxyProvider);
  }

  /**
   * Remove the cached proxy for the provided model instance. A future lookup will cause the
   * creation of a new proxy instance.
   *
   * @param <T>
   *          the bindable type
   * @param model
   *          the model instance
   */
  public static <T> void removeCachedProxyForModel(T model) {
    proxies.remove(model);
  }

  /**
   * Checks if the type of the provided model is bindable. That's the case when a proxy provider has
   * been generated for that type (the type has been annotated or configured to be bindable).
   *
   * @param model
   *          the object to be checked, may be null.
   * @return true if the object is bindable, otherwise false.
   */
  @SuppressWarnings("unchecked")
  public static <T> boolean isBindableType(T model) {
    if (model == null) {
      return false;
    }
    
    if (model instanceof BindableProxy) {
      model = (T) ((BindableProxy<T>) model).unwrap();
    }

    final BindableProxyProvider proxyProvider = bindableProxyProviders.get(model.getClass());
    return (proxyProvider != null);
  }

}
