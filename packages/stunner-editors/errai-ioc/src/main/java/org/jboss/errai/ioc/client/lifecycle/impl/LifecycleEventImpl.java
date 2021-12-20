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

import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.lifecycle.api.Access;
import org.jboss.errai.ioc.client.lifecycle.api.Destruction;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleCallback;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleEvent;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleListener;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleListenerRegistrar;

public abstract class LifecycleEventImpl<T> implements LifecycleEvent<T> {

  private T instance;
  private boolean isVetoed = false;

  @Override
  public void fireAsync(final T instance) {
    fireAsync(instance, null);
  }

  @Override
  public void fireAsync(final T instance, final LifecycleCallback callback) {
    IOC.getAsyncBeanManager().lookupBean(LifecycleListenerRegistrar.class)
            .getInstance(new CreationalCallback<LifecycleListenerRegistrar>() {

              @Override
              public void callback(LifecycleListenerRegistrar registrar) {
                isVetoed = false;
                @SuppressWarnings("unchecked")
                final Iterable<LifecycleListener<T>> listeners = registrar
                        .getListeners((Class<? extends LifecycleEvent<T>>) getEventType(), instance);

                LifecycleEventImpl.this.instance = instance;
                for (final LifecycleListener<T> listener : listeners) {
                  if (isVetoed)
                    break;
                  listener.observeEvent(LifecycleEventImpl.this);
                }
                LifecycleEventImpl.this.instance = null;

                final boolean outcome = !isVetoed;
                if (outcome && getEventType().equals(Destruction.class)) {
                  registrar.endInstanceLifecycle(instance);
                }
                if (callback != null) {
                  callback.callback(outcome);
                }
              }
            });
  }

  @Override
  public void veto() {
    isVetoed = true;
  }

  /**
   * This must return the interface of the event it represents (i.e.
   * {@link Access} rather than {@link AccessImpl}).
   */
  public abstract Class<?> getEventType();

  public T getInstance() {
    return instance;
  }

}
