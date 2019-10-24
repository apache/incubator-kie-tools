/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.messageconsole.client.console;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.event.Event;

import com.google.gwt.view.client.ListDataProvider;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.guvnor.messageconsole.events.FilteredMessagesEvent;
import org.guvnor.messageconsole.events.PublishMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MessageConsoleServiceTest {

    @Mock
    private SyncBeanManager iocManager;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private User identity;

    @Mock
    private StubEventSource<FilteredMessagesEvent> filteredMessagesEvent;

    private MessageConsoleService service;

    @Before
    public void setup() {
        this.service = new MessageConsoleService(iocManager,
                                                 placeManager,
                                                 sessionInfo,
                                                 identity,
                                                 filteredMessagesEvent);
    }

    @Test
    public void testPublishMessagesSortsMessagesInReverseOrder() {
        final PublishMessagesEvent event = new PublishMessagesEvent();
        final SystemMessage systemMessage1 = new SystemMessage();
        final SystemMessage systemMessage2 = new SystemMessage();
        event.setMessagesToPublish(Arrays.asList(systemMessage1, systemMessage2));

        service.publishMessages(event);

        final ListDataProvider<MessageConsoleServiceRow> dataProvider = service.getDataProvider();
        final List<MessageConsoleServiceRow> data = dataProvider.getList();

        assertEquals(2,
                     data.size());
        assertEquals(1,
                     data.get(0).getSequence());
        assertEquals(0,
                     data.get(1).getSequence());

        verify(filteredMessagesEvent, times(1)).fire(any());
    }

    static class StubEventSource<T> implements Event<T> {

        @Override
        public void fire(T event) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public Event<T> select(Annotation... qualifiers) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public <U extends T> Event<U> select(Class<U> subtype,
                                             Annotation... qualifiers) {
            throw new UnsupportedOperationException("Not implemented.");
        }
    }
}
