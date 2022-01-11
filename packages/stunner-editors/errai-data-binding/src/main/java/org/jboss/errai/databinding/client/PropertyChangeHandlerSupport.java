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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeHandler;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * This is a utility class that can be used by implementations of
 * {@link HasPropertyChangeHandlers}. It manages a list of handlers and
 * dispatches {@link PropertyChangeEvent}s.
 *
 * @author David Cracauer <dcracauer@gmail.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class PropertyChangeHandlerSupport {
  final List<PropertyChangeHandler<?>> handlers = new ArrayList<PropertyChangeHandler<?>>();
  final Multimap<String, PropertyChangeHandler<?>> specificPropertyHandlers = ArrayListMultimap.create();

  public Collection<PropertyChangeHandler<?>> removePropertyChangeHandlers() {
    final Collection<PropertyChangeHandler<?>> removedHandlers = new ArrayList<PropertyChangeHandler<?>>(handlers);
    handlers.clear();

    return removedHandlers;
  }

  public Multimap<String, PropertyChangeHandler<?>> removeSpecificPropertyChangeHandlers() {
    final Multimap<String, PropertyChangeHandler<?>> removed = ArrayListMultimap.create(specificPropertyHandlers);
    specificPropertyHandlers.clear();

    return removed;
  }

  /**
   * Adds a {@link PropertyChangeHandler} that will be notified when any
   * property of the bound object changes. Multiple handlers can be registered.
   * If the same handler instance is passed multiple times, it will be notified
   * multiple times.
   *
   * @param handler
   *          The {@link PropertyChangeHandler} to add, must not be null.
   */
  public void addPropertyChangeHandler(PropertyChangeHandler<?> handler) {
    handlers.add(Assert.notNull(handler));
  }

  /**
   * Adds a {@link PropertyChangeHandler} that will be notified only when the
   * given property of the bound object changes. Multiple handlers can be
   * registered for each property. If the same handler instance is passed for
   * the same property multiple times, it will be notified multiple times. If
   * the property name does not correspond to a property of the bound object, no
   * exception is thrown, but no events will ever be delivered to the handler.
   *
   * @param name
   *          The property name for which notifications should be sent.
   * @param handler
   *          The {@link PropertyChangeHandler} to add, must not be null.
   */
  public void addPropertyChangeHandler(String name, PropertyChangeHandler<?> handler) {
    specificPropertyHandlers.put(name, Assert.notNull(handler));
  }

  /**
   * Removes a {@link PropertyChangeHandler} from the list of handlers. If the
   * handler was added more than once to the same event source, it will be
   * notified one less time after being removed. If handler is null, or was
   * never added, no exception is thrown and no action is taken.
   *
   * @param handler
   *          The {@link PropertyChangeHandler} to remove.
   */
  public void removePropertyChangeHandler(PropertyChangeHandler<?> handler) {
    handlers.remove(handler);
  }

  /**
   * Removes a {@link PropertyChangeHandler}, causing it no longer to be
   * notified only when the given property of the bound object changes. If the
   * same handler instance was added for the same property multiple times, it
   * will be notified one less time per change than before. If handler is null,
   * was never added, or the property name does not correspond to a property of
   * the bound object, no exception is thrown and no action is taken.
   *
   * @param name
   *          The property name for which notifications should be sent.
   * @param handler
   *          The {@link PropertyChangeHandler} to add, must not be null.
   */
  public void removePropertyChangeHandler(String name, PropertyChangeHandler<?> handler) {
    specificPropertyHandlers.remove(name, Assert.notNull(handler));
  }

  /**
   * Notify registered {@link PropertyChangeHandlers} of a
   * {@link PropertyChangeEvent}. Will only dispatch events that represent a
   * change. If oldValue and newValue are equal, the event will be ignored.
   *
   * @param event
   *          {@link the PropertyChangeEvent} to provide to handlers.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void notifyHandlers(PropertyChangeEvent<?> event) {
    if (!acceptEvent(event)) {
      return;
    }

    final Collection<PropertyChangeHandler<?>> specHandlers = specificPropertyHandlers.get(event.getPropertyName());
    for (PropertyChangeHandler handler : specHandlers.toArray(new PropertyChangeHandler<?>[specHandlers.size()])) {
      handler.onPropertyChange(event);
    }

    for (PropertyChangeHandler handler : handlers.toArray(new PropertyChangeHandler<?>[handlers.size()])) {
      handler.onPropertyChange(event);
    }
  }

  private boolean acceptEvent(PropertyChangeEvent<?> event) {
    if (event == null) {
      return false;
    }

    if (event.getOldValue() == null) {
      return event.getNewValue() != null;
    }

    if (event.getNewValue() == null) {
      return event.getOldValue() != null;
    }

    return !event.getOldValue().equals(event.getNewValue());
  }
}
