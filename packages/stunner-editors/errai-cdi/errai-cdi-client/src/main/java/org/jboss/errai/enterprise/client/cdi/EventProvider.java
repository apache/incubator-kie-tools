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

package org.jboss.errai.enterprise.client.cdi;

import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.IOCProvider;

import javax.enterprise.event.Event;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;

@IOCProvider
@Singleton
public class EventProvider implements ContextualTypeProvider<Event> {

  @Override
  public Event provide(final Class<?>[] typeargs, final Annotation[] qualifiers) {

    /*
    * If you see a compile error here, ensure that you are using Errai's custom
    * version of javax.enterprise.event.Event, which comes from the
    * errai-javax-enterprise project. The errai-cdi-client POM is set up this
    * way.
    *
    * Eclipse users: seeing an error here probably indicates that M2E has
    * clobbered your errai-javax-enterprise source folder settings. To fix your
    * setup, see the README in the root of errai-javax-enterprise.
    */
    final Class<?> eventType = (typeargs.length == 1 ? typeargs[0] : Object.class);
    return new EventImpl(eventType, qualifiers);
  }

  private static class EventImpl implements Event<Object> {
    private final Class<?> eventType;
    private final Annotation[] _qualifiers;

    private EventImpl(final Class<?> eventType,
                      final Annotation[] _qualifiers) {

      this.eventType = eventType;
      this._qualifiers = _qualifiers;
    }

    public void fire(final Object event) {
      if (event == null)
        return;

      CDI.fireEvent(event, _qualifiers);
    }

    @Override
    public Event<Object> select(final Annotation... qualifiers) {
      if (qualifiers == null) return this;
      return new EventImpl(eventType, concat(_qualifiers, qualifiers));
    }

    @Override
    public <U extends Object> Event<U> select(final Class<U> subtype, final Annotation... qualifiers) {
      return (Event<U>) new EventImpl(subtype, concat(_qualifiers, qualifiers));
    }

    private static Annotation[] concat(Annotation[] a, Annotation[] b) {
      if (a == null) {
        return b;
      }
      else if (b == null) {
        return a;
      }

      final Annotation[] newQualifiers = new Annotation[a.length + b.length];
       for (int i = 0; i < a.length; i++) {
         newQualifiers[i] = a[i];
       }

       for (int i = 0; i < b.length; i++) {
         newQualifiers[i + a.length] = b[i];
       }

      return newQualifiers;
    }


    public Class getEventType() {
      return eventType;
    }

    public Annotation[] getQualifiers() {
      return _qualifiers;
    }
  }
}
