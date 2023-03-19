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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.api.ScopeContext;
import org.jboss.errai.ioc.client.api.SharedSingleton;

/**
 * An implementation of {@link Context} for singleton beans. All calls to
 * {@link #getInstance(String)} will return the same bean instance.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@ScopeContext({ApplicationScoped.class, Singleton.class, EntryPoint.class, SharedSingleton.class})
public class ApplicationScopedContext extends AbstractContext {

  private static final Set<Class<? extends Annotation>> handledScopes = new HashSet<>();

  static {
    handledScopes.add(ApplicationScoped.class);
    handledScopes.add(Singleton.class);
    handledScopes.add(EntryPoint.class);
  }

  private final Map<String, Object> instances = new HashMap<>();

  @Override
  @SuppressWarnings("unchecked")
  protected <T> T getActiveInstance(final String factoryName) {
    return (T) instances.get(factoryName);
  }

  @Override
  protected boolean hasActiveInstance(final String factoryName) {
    return instances.containsKey(factoryName);
  }

  @Override
  protected void registerInstance(final Object unwrappedInstance, final Factory<?> factory) {
    super.registerInstance(unwrappedInstance, factory);
    instances.put(factory.getHandle().getFactoryName(), unwrappedInstance);
  }

  @Override
  public Class<? extends Annotation> getScope() {
    return ApplicationScoped.class;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  public boolean handlesScope(final Class<? extends Annotation> scope) {
    return handledScopes.contains(scope);
  }

  @Override
  protected void afterDestroyInstance(final Object instance) {
    if (instance instanceof Proxy) {
      final Proxy<?> proxy = (Proxy<?>) instance;
      final Object rawInstance = proxy.unwrap();
      instances.values().remove(rawInstance);
      proxy.clearInstance();
    }
    else {
      throw new IllegalArgumentException("Cannot destroy ApplicationScoped bean without reference to proxy.");
    }
  }
}
