/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.list;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EntitiesPagedListTest extends EntitiesListTest {
    
    private EntitiesPagedList<User> presenter;
    private final static int PAGE_SIZE = 5;

    @Before
    public void setup() {
        super.setup();
        presenter = new EntitiesPagedList<User>(loadingBox, view);
        presenter.setPageSize(PAGE_SIZE);
        presenter.setEntityTitleSize(headingSize);
        super.presenter = presenter;
    }

    @Test
    public void testClear() throws Exception {
        super.testClear();
        assertEquals(-1, presenter.currentPage);
    }

    @Test
    public void testCreatePaginationCallbackSinglePage() throws Exception {
        presenter.totalPages = 1;
        presenter.currentPage = 1;
        EntitiesList.PaginationConstraints constraints = presenter.buildPaginationConstraints(10);
        assertFalse(constraints.isFirstPageEnabled());
        assertFalse(constraints.isFirstPageVisible());
        assertFalse(constraints.isPrevPageEnabled());
        assertFalse(constraints.isPrevPageVisible());
        assertFalse(constraints.isNextPageEnabled());
        assertFalse(constraints.isNextPageVisible());
        assertFalse(constraints.isLastPageEnabled());
        assertFalse(constraints.isLastPageVisible());
        assertEquals(constraints.getCurrentPage(), 1);
    }

    @Test
    public void testCreatePaginationCallbackMultiplePages() throws Exception {
        presenter.totalPages = 5;
        presenter.currentPage = 2;
        EntitiesList.PaginationConstraints constraints = presenter.buildPaginationConstraints(10);
        assertTrue(constraints.isFirstPageEnabled());
        assertTrue(constraints.isFirstPageVisible());
        assertTrue(constraints.isPrevPageEnabled());
        assertTrue(constraints.isPrevPageVisible());
        assertTrue(constraints.isNextPageEnabled());
        assertTrue(constraints.isNextPageVisible());
        assertTrue(constraints.isLastPageEnabled());
        assertTrue(constraints.isLastPageVisible());
        assertEquals(constraints.getCurrentPage(), 2);
    }
    
    @Test
    public void testShowFirstPage() throws Exception {
        final int size = 10;
        final Collection<User> entities = buildUsersList(size);
        testShowPage(entities, 1, PAGE_SIZE);
    }

    @Test
    public void testShowSecondPage() throws Exception {
        final int size = 10;
        final Collection<User> entities = buildUsersList(size);
        testShowPage(entities, 2, PAGE_SIZE);
    }
    
    protected void testShowPage(Collection<User> entities, final int page, final int pageSize) throws Exception {
        final EntitiesList.Callback<User> callback = createEntitiesListCallback(entities, true, true, true);
        presenter.currentPage = page;
        presenter.show(entities, callback);
        assertEquals(presenter.entities, entities);
        assertEquals(presenter.callback, callback);
        
        // Verify loading popup.
        verify(loadingBox, times(1)).show();

        // Verify view configuration.
        verify(view, times(1)).configure(anyString(), any(EntitiesList.PaginationConstraints.class));

        // Verify adding entities to the view. 
        int start = pageSize * (page - 1);
        for (int x = 0; x < pageSize; x++) {
            final int pos = start  + x;
            verify(view, times(1)).add(x, getUserIdentifier(pos), getUserIdentifier(pos), headingSize, true, true, true, false);
        }

        // Verify loading popup.
        verify(loadingBox, times(1)).hide();
    }
    
}
