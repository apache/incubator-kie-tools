/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.discussion;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class CommentLinePresenterTest {

    @Mock
    private CommentLineView view;

    @InjectMocks
    CommentLinePresenter presenter;

    @Test
    public void testVisualization() {

        CommentLineView view = mock( CommentLineView.class );
        CommentLinePresenter presenter = new CommentLinePresenterWithNOGWTCode( view );

        Date commentDate = new Date(  );
        DiscussionRecord record = new DiscussionRecord( commentDate.getTime(), "test user", "test note" );
        presenter.setRecord( record );

        verify( view, times( 1 ) ).setAuthor( eq ( expectedAuthorFormat( "test user" ) ) );
        verify( view, times( 1 ) ).setComment( eq( expectedCommentFormat( "test note" ) ) );
        verify( view, times( 1 ) ).setDate( expectedDateFormat( commentDate.getTime() ) );
    }

    private String expectedDateFormat( long timestamp ) {
        //expected format to see in the UI -> "2015-10-21 12:09"
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
        return sdf.format( new Date( timestamp ) );
    }

    private String expectedCommentFormat( String comment ) {
        return "\"" + comment + "\"";
    }

    private String expectedAuthorFormat( String author ) {
        return author + ":";
    }

    private class CommentLinePresenterWithNOGWTCode extends CommentLinePresenter {

        public CommentLinePresenterWithNOGWTCode( CommentLineView view ) {
            super( view );
        }

        @Override
        protected String formatTimestamp( long timestamp ) {
            //override method to avoid GWT invocation during test
            return expectedDateFormat( timestamp );
        }
    }
}
