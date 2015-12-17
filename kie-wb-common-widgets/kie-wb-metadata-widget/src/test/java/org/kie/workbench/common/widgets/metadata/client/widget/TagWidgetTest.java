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
package org.kie.workbench.common.widgets.metadata.client.widget;

import junit.framework.TestCase;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class TagWidgetTest extends TestCase {

    private TagWidget presenter;

    private Metadata metadata = new Metadata(  );

    private TagWidgetView view;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        presenter = new TagWidget( );
        view = mock( TagWidgetView.class );
        presenter.setView( view );
    }

    @Test
    public void testSimple() throws Exception {
        presenter.setContent( metadata, false );
        verify( view ).clear();
        verify( view, never() ).addTag( anyString(), anyBoolean() );
    }



    @Test
    public void testTags() throws Exception {
        presenter.setContent( metadata, false );
        verify( view ).clear();
        verify( view, never() ).addTag( anyString(), anyBoolean() );

        presenter.onAddTags( "tag1 tag2 tag3" );
        assertTrue( metadata.getTags().size() == 3 );

        presenter.onRemoveTag( "tag1" );
        assertTrue( metadata.getTags().size() == 2 );

        presenter.onRemoveTag( "tag2" );
        assertTrue( metadata.getTags().size() == 1 );
    }

}