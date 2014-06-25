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

package org.kie.workbench.common.screens.socialscreen.client.discussion;

import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.socialscreen.client.SocialScreenManager;
import org.mockito.ArgumentCaptor;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.security.Identity;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DiscussionWidgetPresenterTest {

    private DiscussionWidgetView view;
    private SocialScreenManagerMock manager;
    private Identity identity;

    @Before
    public void setUp() throws Exception {
        view = mock(DiscussionWidgetView.class);
        identity = mock(Identity.class);
        manager = new SocialScreenManagerMock();
    }

    @Test
    public void testPresenterSet() throws Exception {
        DiscussionWidgetPresenter presenter = new DiscussionWidgetPresenter(view, identity, manager);
        verify(view).setPresenter(presenter);
    }

    @Test
    public void testInit() throws Exception {

        Metadata metadata = new Metadata();
        metadata.getDiscussion().add(new DiscussionRecord(1234, "Toni", "Knock Knock"));
        metadata.getDiscussion().add(new DiscussionRecord(1235, "Michael", "Who is there?"));
        metadata.getDiscussion().add(new DiscussionRecord(1236, "Toni", "Can't think of anything funny :("));
        manager.metadata = metadata;

        new DiscussionWidgetPresenter(view, identity, manager);

        ArgumentCaptor<CommentLine> commentLineArgumentCaptor = ArgumentCaptor.forClass(CommentLine.class);
        verify(view, times(3)).addRow(commentLineArgumentCaptor.capture());

        assertComment(commentLineArgumentCaptor.getAllValues().get(0), 1234, "Toni", "Knock Knock");
        assertComment(commentLineArgumentCaptor.getAllValues().get(1), 1235, "Michael", "Who is there?");
        assertComment(commentLineArgumentCaptor.getAllValues().get(2), 1236, "Toni", "Can't think of anything funny :(");
    }

    private void assertComment(CommentLine comment, long timestamp, String name, String message) {
        assertEquals(comment.getRecord().getTimestamp().longValue(), timestamp);
        assertEquals(comment.getRecord().getAuthor(), name);
        assertEquals(comment.getRecord().getNote(), message);

    }

    @Test
    public void testInitWhenThereIsCurrentlyNoAssetOpen() throws Exception {
        new DiscussionWidgetPresenter(view, identity, manager);

        verify(view, never()).addRow(any(CommentLine.class));
    }

    @Test
    public void testAddComment() throws Exception {

        ArgumentCaptor<CommentLine> commentLineArgumentCaptor = ArgumentCaptor.forClass(CommentLine.class);

        when(identity.getName()).thenReturn("Toni");

        DiscussionWidgetView.Presenter presenter = new DiscussionWidgetPresenter(view, identity, manager);
        presenter.onAddComment("Hello World!");

        verify(view).addRow(commentLineArgumentCaptor.capture());
        CommentLine line = commentLineArgumentCaptor.getValue();
        assertNotNull(line);
        assertEquals(line.getRecord().getAuthor(), "Toni");
        assertEquals(line.getRecord().getNote(), "Hello World!");

        // save
    }

    private class SocialScreenManagerMock
            extends SocialScreenManager {

        protected Metadata metadata;

        @Override
        public void getMetaData(final Callback<Metadata> callback) {
            callback.callback(metadata);
        }
    }
}
