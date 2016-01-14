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

package org.kie.workbench.common.screens.search.client.menu;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.search.ClearSearchEvent;
import org.kie.workbench.common.widgets.client.search.ContextualSearch;
import org.kie.workbench.common.widgets.client.search.SearchBehavior;
import org.kie.workbench.common.widgets.client.search.SetSearchTextEvent;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class SearchMenuPresenterTest {

    @Mock
    private ContextualSearch contextualSearch;

    @Mock
    private SearchMenuPresenter.View view;

    @InjectMocks
    private SearchMenuPresenter presenter;

    @Test
    public void testClearEvent() {
        presenter.onClearSearchBox( mock( ClearSearchEvent.class ) );

        verify( view ).setText( "" );
    }

    @Test
    public void testSetSearchText() {
        final SetSearchTextEvent setSearchText = mock( SetSearchTextEvent.class );
        final String text = RandomStringUtils.random( 10 );
        when( setSearchText.getSearchText() ).thenReturn( text );

        presenter.onSetSearchText( setSearchText );

        verify( view ).setText( text );
    }

    @Test
    public void testSearch() {
        final SearchBehavior searchBehavior = mock( SearchBehavior.class );
        when( contextualSearch.getSearchBehavior() ).thenReturn( searchBehavior );
        final String text = RandomStringUtils.random( 10 );

        presenter.search( text );

        verify( searchBehavior ).execute( text );
    }
}
