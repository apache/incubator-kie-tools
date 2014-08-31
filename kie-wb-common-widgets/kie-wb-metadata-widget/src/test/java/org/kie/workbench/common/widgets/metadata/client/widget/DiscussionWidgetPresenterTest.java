/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.widgets.metadata.client.widget;

import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.shared.discussion.CommentAddedEvent;
import org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetPresenter;
import org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetView;
import org.mockito.ArgumentCaptor;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;

import javax.enterprise.event.Event;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class DiscussionWidgetPresenterTest {

    private DiscussionWidgetView view;
    private Identity identity;
    private DiscussionWidgetPresenterTest.MockAppConfigServiceCaller appConfigService;
    private DiscussionWidgetPresenterTest.CommentAddedEventMock commentAddedEvent;

    @Before
    public void setUp() throws Exception {
        view = mock(DiscussionWidgetView.class);
        identity = mock(Identity.class);
        appConfigService = new MockAppConfigServiceCaller();
        commentAddedEvent = spy(new CommentAddedEventMock());
    }

    @Test
    public void testPresenterSet() throws Exception {
        DiscussionWidgetPresenter presenter = new DiscussionWidgetPresenter(view, identity, commentAddedEvent, appConfigService);
        verify(view).setPresenter(presenter);
    }

    @Test
    public void testInit() throws Exception {

        Metadata metadata = new Metadata();
        metadata.getDiscussion().add(new DiscussionRecord(1234, "Toni", "Knock Knock"));
        metadata.getDiscussion().add(new DiscussionRecord(1235, "Michael", "Who is there?"));
        metadata.getDiscussion().add(new DiscussionRecord(1236, "Toni", "Can't think of anything funny :("));

        DiscussionWidgetPresenter presenter = new DiscussionWidgetPresenter(view, identity, commentAddedEvent, appConfigService);

        presenter.setContent(metadata);

        ArgumentCaptor<DiscussionRecord> discussionRecordArgumentCaptor = ArgumentCaptor.forClass(DiscussionRecord.class);
        verify(view, times(3)).addRow(discussionRecordArgumentCaptor.capture());

        assertComment(discussionRecordArgumentCaptor.getAllValues().get(0), 1234, "Toni", "Knock Knock");
        assertComment(discussionRecordArgumentCaptor.getAllValues().get(1), 1235, "Michael", "Who is there?");
        assertComment(discussionRecordArgumentCaptor.getAllValues().get(2), 1236, "Toni", "Can't think of anything funny :(");
    }

    private void assertComment(DiscussionRecord record,
                               long timestamp,
                               String name,
                               String message) {
        assertEquals(record.getTimestamp().longValue(), timestamp);
        assertEquals(record.getAuthor(), name);
        assertEquals(record.getNote(), message);

    }

    @Test
    public void testInitWhenThereIsCurrentlyNoAssetOpen() throws Exception {
        new DiscussionWidgetPresenter(view, identity, commentAddedEvent, appConfigService);

        verify(view, never()).addRow(any(DiscussionRecord.class));
    }

    @Test
    public void testAddComment() throws Exception {

        ArgumentCaptor<DiscussionRecord> discussionRecordArgumentCaptor = ArgumentCaptor.forClass(DiscussionRecord.class);

        when(identity.getName()).thenReturn("Toni");

        DiscussionWidgetPresenter presenterImpl = new DiscussionWidgetPresenter(view, identity, commentAddedEvent, appConfigService);
        DiscussionWidgetView.Presenter presenter = presenterImpl;

        Metadata metadata = spy(new Metadata());
        Path path = mock(Path.class);
        when(metadata.getPath()).thenReturn(path);
        presenterImpl.setContent(metadata);

        presenter.onAddComment("Hello World!");

        verify(view).addRow(discussionRecordArgumentCaptor.capture());
        DiscussionRecord line = discussionRecordArgumentCaptor.getValue();
        assertNotNull(line);
        assertEquals(line.getTimestamp(), new Long(1234));
        assertEquals(line.getAuthor(), "Toni");
        assertEquals(line.getNote(), "Hello World!");

        verify(commentAddedEvent).fire(any(CommentAddedEvent.class));
        assertEquals(path, commentAddedEvent.event.getPath());
        assertEquals("Hello World!", commentAddedEvent.event.getComment());
        assertEquals("Toni", commentAddedEvent.event.getUserName());
        assertEquals(new Long(1234), commentAddedEvent.event.getTimestamp());
    }

    private class MockAppConfigServiceCaller
            implements Caller<AppConfigService> {

        private RemoteCallback callback;

        private AppConfigService service;

        private MockAppConfigServiceCaller() {

            service = new AppConfigService() {

                @Override
                public Map<String, String> loadPreferences() {
                    callback.callback(new HashMap<String, String>());
                    return null;
                }

                @Override
                public long getTimestamp() {
                    callback.callback(new Long(1234));
                    return new Long(1234);
                }
            };
        }

        @Override
        public AppConfigService call() {
            return service;
        }

        @Override
        public AppConfigService call(RemoteCallback<?> remoteCallback) {
            callback = remoteCallback;
            return service;
        }

        @Override
        public AppConfigService call(RemoteCallback<?> remoteCallback,
                                     ErrorCallback<?> errorCallback) {
            callback = remoteCallback;
            return service;
        }
    }

    public class CommentAddedEventMock
            implements Event<CommentAddedEvent> {

        private CommentAddedEvent event;

        @Override
        public void fire(CommentAddedEvent event) {

            this.event = event;
        }

        @Override
        public Event<CommentAddedEvent> select(Annotation... annotations) {
            return null;
        }

        @Override
        public <U extends CommentAddedEvent> Event<U> select(Class<U> uClass, Annotation... annotations) {
            return null;
        }
    }
}
