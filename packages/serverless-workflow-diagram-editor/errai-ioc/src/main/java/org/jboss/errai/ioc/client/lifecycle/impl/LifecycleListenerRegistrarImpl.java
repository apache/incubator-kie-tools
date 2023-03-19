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

package org.jboss.errai.ioc.client.lifecycle.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;

import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.lifecycle.api.AuditableLifecycleListener;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleEvent;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleListener;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleListenerGenerator;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleListenerRegistrar;

@Singleton
public class LifecycleListenerRegistrarImpl implements LifecycleListenerRegistrar {

  private final Map<Object, Collection<? extends AuditableLifecycleListener<?>>> activeListenerMap = new IdentityHashMap<Object, Collection<? extends AuditableLifecycleListener<?>>>();
  private final Map<Class<?>, Collection<LifecycleListenerGenerator<?>>> generatorMap = new HashMap<Class<?>, Collection<LifecycleListenerGenerator<?>>>();

  @Override
  public <T> Iterable<LifecycleListener<T>> getListeners(final Class<? extends LifecycleEvent<T>> eventType,
          final T instance) {
    final Collection<AuditableLifecycleListener<T>> allInstanceListeners = getInstanceListeners(instance);
    final Collection<LifecycleListener<T>> eventListeners = new ArrayList<LifecycleListener<T>>();
    for (final LifecycleListener<T> listener : allInstanceListeners) {
      if (listener.isObserveableEventType(eventType)) {
        eventListeners.add(listener);
      }
    }

    return eventListeners;
  }

  private <T> Collection<AuditableLifecycleListener<T>> getInstanceListeners(T instance) {
    instance = Factory.maybeUnwrapProxy(instance);
    @SuppressWarnings("unchecked")
    Collection<AuditableLifecycleListener<T>> listeners = (Collection<AuditableLifecycleListener<T>>) activeListenerMap
            .get(instance);
    if (listeners == null) {
      listeners = generateNewLifecycleListeners(instance);
      activeListenerMap.put(instance, listeners);
    }

    return listeners;
  }

  protected <T> Collection<AuditableLifecycleListener<T>> generateNewLifecycleListeners(T instance) {
    instance = Factory.maybeUnwrapProxy(instance);
    final Collection<AuditableLifecycleListener<T>> newListeners = new ArrayList<AuditableLifecycleListener<T>>();
    final Collection<LifecycleListenerGenerator<?>> generators = generatorMap.get(instance.getClass());
    if (generators != null) {
      for (final LifecycleListenerGenerator<?> generator : generators) {
        @SuppressWarnings("unchecked")
        final LifecycleListener<T> listener = (LifecycleListener<T>) generator.newInstance();
        newListeners.add(new AuditableLifecycleListener<T>() {

          @Override
          public void observeEvent(final LifecycleEvent<T> event) {
            listener.observeEvent(event);
          }

          @Override
          public boolean isObserveableEventType(Class<? extends LifecycleEvent<T>> eventType) {
            return listener.isObserveableEventType(eventType);
          }

          @SuppressWarnings("unchecked")
          @Override
          public LifecycleListenerGenerator<T> getGenerator() {
            return (LifecycleListenerGenerator<T>) generator;
          }

          @Override
          public LifecycleListener<T> unwrap() {
            return listener;
          }
        });
      }
    }

    return newListeners;
  }

  @Override
  public <T> void registerGenerator(Class<T> lifecycleType, LifecycleListenerGenerator<T> generator) {
    Collection<LifecycleListenerGenerator<?>> generators = generatorMap.get(lifecycleType);
    if (generators == null) {
      generators = new ArrayList<LifecycleListenerGenerator<?>>();
      generatorMap.put(lifecycleType, generators);
    }
    if (!generators.contains(generator))
      generators.add(generator);
  }

  @Override
  public <T> void unregisterGenerator(final Class<T> lifecycleType, final LifecycleListenerGenerator<T> generator) {
    unregisterGenerators(lifecycleType, generator);
    unregisterActiveListeners(lifecycleType, generator);
  }

  private <T> void unregisterActiveListeners(final Class<T> lifecycleType, final LifecycleListenerGenerator<T> generator) {
    for (final Entry<Object, Collection<? extends AuditableLifecycleListener<?>>> entry : activeListenerMap.entrySet()) {
      if (entry.getKey().getClass().equals(lifecycleType)) {
        @SuppressWarnings("unchecked")
        final Iterator<AuditableLifecycleListener<T>> iterator = (Iterator<AuditableLifecycleListener<T>>) entry
                .getValue().iterator();
        AuditableLifecycleListener<T> listener;
        while (iterator.hasNext()) {
          listener = iterator.next();
          if (listener.getGenerator() == generator)
            iterator.remove();
        }
      }
    }
  }

  private <T> void unregisterGenerators(final Class<T> lifecycleType, final LifecycleListenerGenerator<T> generator) {
    final Collection<LifecycleListenerGenerator<?>> listenerGenerators = generatorMap.get(lifecycleType);
    if (listenerGenerators != null)
      listenerGenerators.remove(generator);
  }

  @Override
  public <T> boolean endInstanceLifecycle(final T instance) {
    return activeListenerMap.remove(Factory.maybeUnwrapProxy(instance)) != null;
  }

  @Override
  public <T> void registerListener(T instance, final LifecycleListener<T> listener) {
    instance = Factory.maybeUnwrapProxy(instance);
    @SuppressWarnings("unchecked")
    Collection<AuditableLifecycleListener<T>> activeListeners = (Collection<AuditableLifecycleListener<T>>) activeListenerMap
            .get(instance);
    if (activeListeners == null) {
      activeListeners = generateNewLifecycleListeners(instance);
      activeListenerMap.put(instance, activeListeners);
    }

    activeListeners.add(new AuditableLifecycleListener<T>() {
      @Override
      public void observeEvent(LifecycleEvent<T> event) {
        listener.observeEvent(event);
      }

      @Override
      public boolean isObserveableEventType(Class<? extends LifecycleEvent<T>> eventType) {
        return listener.isObserveableEventType(eventType);
      }

      @Override
      public LifecycleListenerGenerator<T> getGenerator() {
        // Return a dummy generator to avoid needing logic elsewhere to check
        // for NPEs.
        return new LifecycleListenerGenerator<T>() {
          @Override
          public LifecycleListener<T> newInstance() {
            // This method should never be called.
            return null;
          }
        };
      }

      @Override
      public LifecycleListener<T> unwrap() {
        return listener;
      }
    });
  }

  @Override
  public <T> void unregisterListener(T instance, final LifecycleListener<T> listener) {
    instance = Factory.maybeUnwrapProxy(instance);
    @SuppressWarnings("unchecked")
    final Collection<AuditableLifecycleListener<T>> instanceListeners = (Collection<AuditableLifecycleListener<T>>) activeListenerMap
            .get(instance);
    if (instanceListeners != null) {
      final Iterator<AuditableLifecycleListener<T>> iterator = instanceListeners.iterator();
      while (iterator.hasNext())
        if (iterator.next().unwrap() == listener) {
          iterator.remove();
          break;
        }
    }
  }

}
