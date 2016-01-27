/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.search;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class ContextualSearchTest {

    @Test
    public void testDefaultSearchBehavior() {
        ContextualSearch contextualSearch = new ContextualSearch();
        SearchBehavior defaultSearchBehavior = mock( SearchBehavior.class );
        contextualSearch.setDefaultSearchBehavior( defaultSearchBehavior );

        assertEquals( defaultSearchBehavior, contextualSearch.getSearchBehavior() );
    }

    @Test
    public void testDefaultSearchBehaviorWithPerspective() {
        ContextualSearch contextualSearch = new ContextualSearch();

        SearchBehavior defaultSearchBehavior = mock( SearchBehavior.class );
        contextualSearch.setDefaultSearchBehavior( defaultSearchBehavior );

        SearchBehavior perspectiveSearchBehavior = mock( SearchBehavior.class );
        contextualSearch.setPerspectiveSearchBehavior( "perspectiveId", perspectiveSearchBehavior );

        final PerspectiveChange perspectiveChange = mock( PerspectiveChange.class );
        when( perspectiveChange.getPlaceRequest() ).thenReturn( mock( PlaceRequest.class ) );
        contextualSearch.onPerspectiveChange( perspectiveChange );

        assertEquals( defaultSearchBehavior, contextualSearch.getSearchBehavior() );
    }

    @Test
    public void testPerspectiveSearchBehavior() {
        ContextualSearch contextualSearch = new ContextualSearch();

        SearchBehavior defaultSearchBehavior = mock( SearchBehavior.class );
        contextualSearch.setDefaultSearchBehavior( defaultSearchBehavior );

        SearchBehavior perspectiveSearchBehavior = mock( SearchBehavior.class );
        final String perspectiveId = "perspectiveId";
        contextualSearch.setPerspectiveSearchBehavior( perspectiveId, perspectiveSearchBehavior );

        final PerspectiveChange perspectiveChange = mock( PerspectiveChange.class );
        final PlaceRequest placeRequest = mock( PlaceRequest.class );
        when( placeRequest.getIdentifier() ).thenReturn( perspectiveId ).thenReturn( "anotherPerspective" );
        when( perspectiveChange.getPlaceRequest() ).thenReturn( placeRequest );
        contextualSearch.onPerspectiveChange( perspectiveChange );

        assertEquals( perspectiveSearchBehavior, contextualSearch.getSearchBehavior() );

        contextualSearch.onPerspectiveChange( perspectiveChange );

        assertEquals( defaultSearchBehavior, contextualSearch.getSearchBehavior() );
    }
}
