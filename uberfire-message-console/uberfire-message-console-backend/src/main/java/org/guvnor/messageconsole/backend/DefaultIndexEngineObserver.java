/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.messageconsole.backend;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.shared.message.Level;
import org.guvnor.messageconsole.events.PublishMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.uberfire.ext.metadata.engine.Observer;

/**
 * Observer component translates Index activities into Message Console entries
 */
@ApplicationScoped
public class DefaultIndexEngineObserver implements Observer {

    @Inject
    private Event<PublishMessagesEvent> publishMessagesEvent;

    public void information(final String message) {
        publishMessagesEvent.fire(makeEvent(message,
                                            Level.INFO));
    }

    @Override
    public void warning(final String message) {
        publishMessagesEvent.fire(makeEvent(message,
                                            Level.WARNING));
    }

    @Override
    public void error(final String message) {
        publishMessagesEvent.fire(makeEvent(message,
                                            Level.ERROR));
    }

    private PublishMessagesEvent makeEvent(final String text,
                                           final Level level) {
        final PublishMessagesEvent event = new PublishMessagesEvent();
        final List<SystemMessage> messages = new ArrayList<SystemMessage>();
        final SystemMessage message = new SystemMessage();
        message.setLevel(level);
        message.setText(text);
        messages.add(message);
        event.setMessagesToPublish(messages);
        return event;
    }
}
