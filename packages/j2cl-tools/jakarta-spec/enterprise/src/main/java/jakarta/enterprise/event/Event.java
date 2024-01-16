/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, 2015, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jakarta.enterprise.event;

import jakarta.enterprise.inject.Any;

/**
 * Allows the application to fire events of a particular type.
 *
 * <p>Beans fire events via an instance of the <code>Event</code> interface, which may be injected:
 *
 * <pre>
 * &#064;Inject
 * &#064;Any
 * Event&lt;LoggedInEvent&gt; loggedInEvent;
 * </pre>
 *
 * <p>The <code>fire()</code> method accepts an event object:
 *
 * <pre>
 * public void login() {
 *    ...
 *    loggedInEvent.fire( new LoggedInEvent(user) );
 * }
 * </pre>
 *
 * <p>Any combination of qualifiers may be specified at the injection point:
 *
 * <pre>
 * &#064;Inject
 * &#064;Admin
 * Event&lt;LoggedInEvent&gt; adminLoggedInEvent;
 * </pre>
 *
 * <p>Or, the {@link Any &#064;Any} qualifier may be used, allowing the application to specify
 * qualifiers dynamically:
 *
 * <pre>
 * &#064;Inject
 * &#064;Any
 * Event&lt;LoggedInEvent&gt; loggedInEvent;
 * </pre>
 *
 * <p>For an injected <code>Event</code>:
 *
 * <ul>
 *   <li>the <em>specified type</em> is the type parameter specified at the injection point, and
 *   <li>the <em>specified qualifiers</em> are the qualifiers specified at the injection point.
 * </ul>
 *
 * <p>Events may also be fired asynchronously with {@link #fireAsync(Object)} and {@link
 * #fireAsync(Object, NotificationOptions)} methods
 *
 * @author Gavin King
 * @author Pete Muir
 * @author David Allen
 * @author Antoine Sabot-Durand
 * @param <T> the type of the event object
 */
public interface Event<T> {

  /**
   * Fires an event with the specified qualifiers and notifies observers.
   *
   * @param event the event object
   * @throws IllegalArgumentException if the runtime type of the event object contains a type
   *     variable
   * @throws ObserverException if a notified observer throws a checked exception, it will be wrapped
   *     and rethrown as an (unchecked) {@link ObserverException}
   */
  public void fire(T event);
}
