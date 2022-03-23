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

package org.jboss.errai.ioc.client.lifecycle.api;

/**
 * A listener for observing {@link LifecycleEvent LifecycleEvents} on IOC beans.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface LifecycleListener<T> {

  /**
   * This method is called when a {@link LifecycleEvent} is called such that
   * {@link #isObserveableEventType(Class)} returns true for this listener and
   * event.
   * 
   * If {@link LifecycleEvent#veto()} is called in this method, any pending
   * listeners will be cancelled and callback of
   * {@link LifecycleEvent#fireAsync(LifecycleCallback)} will be invoked with a
   * failure result.
   * 
   * @param event
   *          The event being observed.
   */
  public void observeEvent(LifecycleEvent<T> event);

  /**
   * Check if this listener observes this event type.
   * 
   * @param eventType
   *          A type of subclass of {@link LifecycleEvent}.
   * @return True iff this listener observes this event type.
   */
  public boolean isObserveableEventType(Class<? extends LifecycleEvent<T>> eventType);

}
