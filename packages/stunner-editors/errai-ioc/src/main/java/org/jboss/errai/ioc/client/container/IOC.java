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

package org.jboss.errai.ioc.client.container;

import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.ioc.client.container.async.AsyncBeanManager;
import org.jboss.errai.ioc.client.container.async.AsyncBeanManagerImpl;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleEvent;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleListener;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleListenerGenerator;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleListenerRegistrar;

import com.google.gwt.core.client.GWT;

/**
 * A simple utility class which provides a static reference in the client to the
 * bean manager.
 *
 * @author Mike Brock
 */
public final class IOC {
  private static final IOC inst = new IOC();
  private final ClientBeanManager beanManager;

  private IOC() {
    IOCEnvironment iocEnvironment;

    try {
      iocEnvironment = GWT.create(IOCEnvironment.class);
    }
    catch (UnsupportedOperationException e) {
      iocEnvironment = new IOCEnvironment() {
        @Override
        public boolean isAsync() {
          return false;
        }

        @Override
        public ClientBeanManager getNewBeanManager() {
          if (!GWT.isClient()) {
            return new SyncBeanManagerImpl();
          }
          else {
            return null;
          }
        }
      };
    }

    beanManager = iocEnvironment.getNewBeanManager();
  }

  /**
   * Returns a reference to the bean manager in the client.
   *
   * @return the singleton instance of the client bean manager.
   *
   * @see SyncBeanManagerImpl
   */
  public static SyncBeanManager getBeanManager() {
    if (inst.beanManager instanceof AsyncBeanManager) {
      return ((AsyncBeanManagerImpl) inst.beanManager).getInnerBeanManager();
    }
    return (SyncBeanManagerImpl) inst.beanManager;
  }

  public static AsyncBeanManager getAsyncBeanManager() {
    if (inst.beanManager instanceof SyncBeanManager) {
      return new SyncToAsyncBeanManagerAdapter((SyncBeanManager) inst.beanManager);
    }

    return (AsyncBeanManager) inst.beanManager;
  }

  /**
   * Register a {@link LifecycleListenerGenerator} for
   * {@linkplain LifecycleEvent IOC Lifecycle Events}.
   *
   * @param beanType
   *          The type of bean for which {@link LifecycleListener
   *          LifecycleListeners} created by this generator observe.
   * @param listenerGenerator
   *          A generator creating {@link LifecycleListener LifecycleListeners}
   *          which observe events from the specified bean type.
   */
  public static <T> void registerLifecycleListener(final Class<T> beanType,
          final LifecycleListenerGenerator<T> listenerGenerator) {

    getAsyncBeanManager().lookupBean(LifecycleListenerRegistrar.class).getInstance(
            new CreationalCallback<LifecycleListenerRegistrar>() {

              @Override
              public void callback(final LifecycleListenerRegistrar registrar) {
                registrar.registerGenerator(beanType, listenerGenerator);
              }
            });
  }

  /**
   * Register a single {@link LifecycleListener} for {@link LifecycleEvent
   * LifecycleEvents} from a single instance.
   *
   * @param instance The instance to be observed.
   * @param listener The listener to be registered.
   */
  public static <T> void registerLifecycleListener(final T instance, final LifecycleListener<T> listener) {
    getAsyncBeanManager().lookupBean(LifecycleListenerRegistrar.class).getInstance(
            new CreationalCallback<LifecycleListenerRegistrar>() {

              @Override
              public void callback(final LifecycleListenerRegistrar registrar) {
                registrar.registerListener(instance, listener);
              }
            });
  }

  /**
   * Unregister a {@link LifecycleListenerGenerator} and all the
   * {@link LifecycleListener LifecycleListeners} created by this generator.
   *
   * @param beanType
   *          The bean type for which this generator created listeners.
   * @param generator
   *          The generator to be unregistered (must be the same instance as was
   *          registered).
   */
  public static <T> void unregisterLifecycleListener(final Class<T> beanType,
          final LifecycleListenerGenerator<T> generator) {
    getAsyncBeanManager().lookupBean(LifecycleListenerRegistrar.class).getInstance(
            new CreationalCallback<LifecycleListenerRegistrar>() {

              @Override
              public void callback(final LifecycleListenerRegistrar registrar) {
                registrar.unregisterGenerator(beanType, generator);
              }
            });
  }

  /**
   * Unregister a single {@link LifecycleListener} for {@link LifecycleEvent
   * LifecycleEvents} from a single instance.
   *
   * @param instance The instance that was observed.
   * @param listener The listener that was registered.
   */
  public static <T> void unregisterLifecycleListener(final T instance, final LifecycleListener<T> listener) {
    getAsyncBeanManager().lookupBean(LifecycleListenerRegistrar.class).getInstance(
            new CreationalCallback<LifecycleListenerRegistrar>() {

              @Override
              public void callback(final LifecycleListenerRegistrar registrar) {
                registrar.unregisterListener(instance, listener);
              }
            });
  }

  /**
   * For testing only. Resets the bean manager.
   */
  public static void reset() {
    if (inst.beanManager instanceof SyncBeanManagerImpl) {
      ((SyncBeanManagerImpl) inst.beanManager).reset();
    } else if (inst.beanManager instanceof AsyncBeanManagerImpl) {
      ((AsyncBeanManagerImpl) inst.beanManager).reset();
    } else {
      throw new RuntimeException("Cannot reset bean manager of type " + inst.beanManager.getClass().getName());
    }
  }
}
