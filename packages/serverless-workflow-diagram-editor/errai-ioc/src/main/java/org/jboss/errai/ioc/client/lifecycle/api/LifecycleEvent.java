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
 * The base interface for IOC Lifecycle events. Components may fire IOC Lifecycle events to
 * broadcast events to interested listeners that do not perfectly map to one of the supported IOC
 * scopes.
 * 
 * The general usage for firing an event is:
 * 
 * <pre>
 * Access<String> event = IOC.getBeanManager().lookup(Creation.class).getInstance();
 * // Need to set an instance for the event.
 * event.setInstance("String Instance!");
 * 
 * event.fireAsync(new LifecycleCallback() {
 *      {@code @Override}
 *      public void callback(boolean success) {
 *          if (success) {
 *              // Go through with the action
 *          }
 *      }
 * });
 * </pre>
 * 
 * {@link LifecycleListener LifecycleListeners} can {@linkplain #getInstance() access the event
 * instance} or {@linkplain #veto() veto the event}.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface LifecycleEvent<T> {

  /**
   * Fire this event, notifying any listeners for this event type by calling the respective
   * {@link LifecycleListener#observeEvent(LifecycleEvent)} methods.
   * 
   * @param instance
   *          The bean instance associated with this event.
   */
  public void fireAsync(T instance);

  /**
   * Fire this event, notifying any listeners for this event type by calling the respective
   * {@link LifecycleListener#observeEvent(LifecycleEvent)} methods.
   * 
   * @param instance
   *          The bean instance associated with this event.
   * 
   * @param callback
   *          A callback for receiving the result of a fired event (whether or not any listeners
   *          {@linkplain #veto() vetoed}.
   */
  public void fireAsync(T instance, LifecycleCallback callback);

  /**
   * Veto this event. If this method is called by a {@link LifecycleListener} during the
   * {@link LifecycleListener#observeEvent(LifecycleEvent)} then:
   * <ul>
   * <li>Any pending listeners will not be invoked.</li>
   * <li>The event firer's {@link LifecycleCallback#callback(boolean)} will be invoked with the
   * parameter value {@code false}.
   */
  public void veto();

  /**
   * This method should only be called from within
   * {@link LifecycleListener#observeEvent(LifecycleEvent)}. The instance is set immediately before
   * calling listeners and unset immediately after.
   *
   * @return The instance for which this event has been fired or {@code null} if this event is not
   *         actively being fired.
   */
  public T getInstance();

}
