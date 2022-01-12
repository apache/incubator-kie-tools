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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @see ContextManager
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class ContextManagerImpl implements ContextManager {

  private final Map<String, Context> contextsByFactoryName = new HashMap<String, Context>();
  private final Collection<Context> contexts = new ArrayList<Context>();

  @Override
  public void addContext(final Context context) {
    if (!contexts.contains(context)) {
      contexts.add(context);
      context.setContextManager(this);
      for (final Factory<?> factory : context.getAllFactories()) {
        contextsByFactoryName.put(factory.getHandle().getFactoryName(), context);
      }
    }
  }

  @Override
  public <T> T getInstance(final String factoryName) {
    return getContext(factoryName).getInstance(factoryName);
  }

  @Override
  public <T> T getContextualInstance(final String factoryName, final Class<?>[] typeArgs, final Annotation[] qualifiers) {
    return getContext(factoryName)
            .withContextualInstanceSupport()
            .orElseThrow(() -> new RuntimeException("The scope, " + getClass().getSimpleName() + ", does not support contextual instances."))
            .getContextualInstance(factoryName, typeArgs, qualifiers);
  }

  private Context getContext(final String factoryName) {
    final Context context = contextsByFactoryName.get(factoryName);
    if (context == null) {
      throw new RuntimeException("Could not find a context for the factory " + factoryName);
    } else {
      return context;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getEagerInstance(final String factoryName) {
    final T instance = getContext(factoryName).<T>getInstance(factoryName);
    if ((instance instanceof Proxy)) {
      ((Proxy<T>) instance).unwrap();
    }

    return instance;
  }

  @Override
  public <T> T getNewInstance(final String factoryName) {
    return getContext(factoryName).getNewInstance(factoryName);
  }

  @Override
  public Collection<FactoryHandle> getAllFactoryHandles() {
    final Collection<FactoryHandle> allHandles = new ArrayList<FactoryHandle>();
    for (final Context context : contexts) {
      for (final Factory<?> factory : context.getAllFactories()) {
        allHandles.add(factory.getHandle());
      }
    }

    return allHandles;
  }

  @Override
  public void destroy(final Object instance) {
    for (final Context context : contexts) {
      context.destroyInstance(instance);
    }
  }

  @Override
  public boolean isManaged(final Object ref) {
    for (final Context context : contexts) {
      if (context.isManaged(ref)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean addDestructionCallback(final Object instance, final DestructionCallback<?> callback) {
    boolean success = false;
    for (final Context context : contexts) {
      success = success || context.addDestructionCallback(instance, callback);
    }

    return success;
  }

  @Override
  public <P> P getInstanceProperty(final Object instance, final String propertyName, final Class<P> type) {
    for (final Context context : contexts) {
      if (context.isManaged(instance)) {
        return context.getInstanceProperty(instance, propertyName, type);
      }
    }

    throw new RuntimeException("The given instance, " + instance + ", is not managed.");
  }

  @Override
  public void finishInit() {
    for (final Context context : contexts) {
      for (final Factory<?> factory : context.getAllFactories()) {
        factory.init(context);
      }
    }
  }

  @Override
  public void addFactory(final Factory<?> factory) {
    final Context context = getContextForScope(factory.getHandle().getScope());
    context.registerFactory(factory);
    contextsByFactoryName.put(factory.getHandle().getFactoryName(), context);
    factory.init(context);
  }

  private Context getContextForScope(final Class<? extends Annotation> scope) {
    for (final Context context : contexts) {
      if (context.handlesScope(scope)) {
        return context;
      }
    }

    throw new RuntimeException("Could not find context for the scope " + scope.getName());
  }

}
