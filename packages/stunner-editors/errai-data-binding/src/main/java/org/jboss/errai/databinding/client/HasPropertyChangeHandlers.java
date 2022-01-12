/*
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeHandler;

/**
 * Implementations are a source of {@link PropertyChangeEvent}s.
 *
 * @author David Cracauer <dcracauer@gmail.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface HasPropertyChangeHandlers {

  /**
   * Adds a {@link PropertyChangeHandler} that will be notified when any property of the underlying
   * object changes. Multiple handlers can be registered. If the same handler instance is passed
   * multiple times, it will be notified multiple times.
   *
   * @param handler
   *          The {@link PropertyChangeHandler} instance, must not be null.
   * @return A handle for unsubscribing the given {@link PropertyChangeHandler}.
   */
  public PropertyChangeUnsubscribeHandle addPropertyChangeHandler(PropertyChangeHandler<?> handler);

  /**
   * Adds a {@link PropertyChangeHandler} that will be notified when the given property of the
   * underlying object changes. Multiple handlers can be registered. If the same handler instance is
   * passed multiple times, it will be notified multiple times.
   *
   * @param property
   *          The name of the property or a property chain (e.g. customer.address.street) to receive
   *          events for. A property expression can end in a wildcard to indicate that changes of
   *          any property of the corresponding bean should be observed (e.g customer.address.*). A
   *          double wildcard can be used at the end of a property expression to register a
   *          cascading change handler for any nested property (e.g customer.**). Must not be null.
   * @param handler
   *          The {@link PropertyChangeHandler} instance that should receive the events. Must not be
   *          null.
   * @return A handle for unsubscribing the give {@link PropertyChangeHandler}.
   */
  public <T> PropertyChangeUnsubscribeHandle addPropertyChangeHandler(String property, PropertyChangeHandler<T> handler);

}
