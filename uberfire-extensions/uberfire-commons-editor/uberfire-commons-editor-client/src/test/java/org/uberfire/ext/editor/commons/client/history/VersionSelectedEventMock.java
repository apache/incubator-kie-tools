/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client.history;

import java.lang.annotation.Annotation;
import javax.enterprise.event.Event;
import javax.enterprise.util.TypeLiteral;

import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.editor.commons.client.history.event.VersionSelectedEvent;

public class VersionSelectedEventMock
        implements Event<VersionSelectedEvent> {

    private Callback<VersionSelectedEvent> callback;

    public VersionSelectedEventMock( Callback<VersionSelectedEvent> callback ) {
        this.callback = callback;

    }

    @Override
    public void fire( VersionSelectedEvent event ) {
        callback.callback( event );
    }

    @Override
    public Event<VersionSelectedEvent> select( Annotation... annotations ) {
        return null;
    }

    @Override
    public <U extends VersionSelectedEvent> Event<U> select( Class<U> uClass,
                                                             Annotation... annotations ) {
        return null;
    }

    @Override
    public <U extends VersionSelectedEvent> Event<U> select( TypeLiteral<U> subtype,
                                                             Annotation... qualifiers ) {
        return null;
    }

}