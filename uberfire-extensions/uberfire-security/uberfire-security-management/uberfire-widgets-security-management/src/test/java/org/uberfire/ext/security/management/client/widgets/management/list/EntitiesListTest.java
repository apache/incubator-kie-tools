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

import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.client.widgets.management.AbstractSecurityManagementTest;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EntitiesListTest extends AbstractSecurityManagementTest {

    @Mock LoadingBox loadingBox;
    @Mock EntitiesList.View view;
    
    protected EntitiesList<User> presenter;
    protected HeadingSize headingSize = HeadingSize.H3;
    
    @Before
    public void setup() {
        super.setup();
        presenter = new EntitiesList<User>(loadingBox, view);
        presenter.setPageSize(5);
        presenter.setEntityTitleSize(headingSize);
    }

    @Test
    public void testClear() throws Exception {
        presenter.callback = mock(EntitiesList.Callback.class);
        presenter.paginationConstraints = mock(EntitiesList.PaginationConstraints.class);
        presenter.totalPages = 10;
        presenter.emptyEntitiesText = "empty";
        presenter.clear();
        assertNull(presenter.callback);
        assertNull(presenter.paginationConstraints);
        assertEquals(presenter.totalPages, -1);
        assertNull(presenter.emptyEntitiesText);
        verify(view, times(0)).configure(anyString(), any(EntitiesList.PaginationConstraints.class));
        verify(view, times(0)).add(anyInt(), anyString(), anyString(), 
                any(HeadingSize.class), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean());
        verify(view, times(1)).clear();
    }

    @Test
    public void testCallbacks() throws Exception {
        final String id = "user1";
        final EntitiesList.Callback callback = mock(EntitiesList.Callback.class);;
        final EntitiesList.PaginationConstraints paginationConstraints = mock(EntitiesList.PaginationConstraints.class);
        when(paginationConstraints.getCurrentPage()).thenReturn(5);
        presenter.callback = callback;
        presenter.paginationConstraints = paginationConstraints;
        presenter.totalPages = 10;

        presenter.getEntityType();
        verify(callback, times(1)).getEntityType();
        
        presenter.onReadEntity(id);
        verify(callback, times(1)).onReadEntity(id);

        presenter.onRemoveEntity(id);
        verify(callback, times(1)).onRemoveEntity(id);

        presenter.onGoToFirstPage();
        verify(callback, times(1)).onChangePage(anyInt(), eq(1));

        presenter.onGoToPrevPage();
        verify(callback, times(1)).onChangePage(anyInt(), eq(4));

        presenter.onGoToNextPage();
        verify(callback, times(1)).onChangePage(anyInt(), eq(6));
        

        presenter.onGoToLastPage();
        verify(callback, times(1)).onChangePage(anyInt(), eq(11));

        presenter.onSelectEntity(id, 0, false);
        verify(callback, times(1)).onSelectEntity(id, false);

    }
    
    @Test
    public void testCreatePaginationCallbackSinglePage() throws Exception {
        final Integer size = 10;
        List<User> users = buildUsersList(size);
        final boolean hasNextPage = false;
        final int page = 1;
        final int pageSize = 50;
        
        final AbstractEntityManager.SearchResponse<User> searchResponse = createSearchResponse(users, size,
                hasNextPage, "", page, pageSize);
        
        EntitiesList.PaginationConstraints constraints = presenter.createPaginationCallback(searchResponse);
        assertFalse(constraints.isFirstPageEnabled());
        assertFalse(constraints.isFirstPageVisible());
        assertFalse(constraints.isNextPageEnabled());
        assertFalse(constraints.isNextPageVisible());
        assertFalse(constraints.isPrevPageEnabled());
        assertFalse(constraints.isPrevPageVisible());
        assertFalse(constraints.isLastPageEnabled());
        assertFalse(constraints.isLastPageVisible());
        assertEquals(constraints.getTotal(), size);
        assertEquals(constraints.getCurrentPage(), page);
    }

    @Test
    public void testCreatePaginationCallbackMultiplePagesAtFirstPage() throws Exception {
        final Integer size = 10;
        List<User> users = buildUsersList(size);
        final boolean hasNextPage = false;
        final int page = 1;
        final int pageSize = 5;

        final AbstractEntityManager.SearchResponse<User> searchResponse = createSearchResponse(users, size,
                hasNextPage, "", page, pageSize);

        EntitiesList.PaginationConstraints constraints = presenter.createPaginationCallback(searchResponse);
        assertFalse(constraints.isFirstPageEnabled());
        assertFalse(constraints.isFirstPageVisible());
        assertTrue(constraints.isNextPageEnabled());
        assertTrue(constraints.isNextPageVisible());
        assertFalse(constraints.isPrevPageEnabled());
        assertFalse(constraints.isPrevPageVisible());
        assertTrue(constraints.isLastPageEnabled());
        assertTrue(constraints.isLastPageVisible());
        assertEquals(constraints.getTotal(), size);
        assertEquals(constraints.getCurrentPage(), page);
    }

    @Test
    public void testCreatePaginationCallbackMultiplePagesAtSecondPage() throws Exception {
        final Integer size = 10;
        List<User> users = buildUsersList(size);
        final boolean hasNextPage = false;
        final int page = 2;
        final int pageSize = 5;

        final AbstractEntityManager.SearchResponse<User> searchResponse = createSearchResponse(users, size,
                hasNextPage, "", page, pageSize);

        EntitiesList.PaginationConstraints constraints = presenter.createPaginationCallback(searchResponse);
        assertTrue(constraints.isFirstPageEnabled());
        assertTrue(constraints.isFirstPageVisible());
        assertTrue(constraints.isNextPageEnabled());
        assertTrue(constraints.isNextPageVisible());
        assertTrue(constraints.isPrevPageEnabled());
        assertTrue(constraints.isPrevPageVisible());
        assertTrue(constraints.isLastPageEnabled());
        assertTrue(constraints.isLastPageVisible());
        assertEquals(constraints.getTotal(), size);
        assertEquals(constraints.getCurrentPage(), page);
    }
    
    @Test
    public void testShow() throws Exception {
        final int size = 10;
        List<User> users = buildUsersList(size);
        final boolean hasNextPage = false;
        final int page = 1;
        final int pageSize = 50;

        final AbstractEntityManager.SearchResponse<User> searchResponse = createSearchResponse(users, size, 
                hasNextPage, "", page, pageSize);
        final EntitiesList.Callback<User> callback = createEntitiesListCallback(users, true, true, true);

        // Call the public show method.
        presenter.show(searchResponse, callback);
        assertEquals(presenter.callback, callback);

        // Not clear verify.
        verify(view, times(0)).clear();


        // Verify loading popup.
        verify(loadingBox, times(1)).show();
        
        // Verify view configuration.
        verify(view, times(1)).configure(anyString(), any(EntitiesList.PaginationConstraints.class));

        // Verify adding entities to the view. 
        for (int x = 0; x < size; x++) {
            verify(view, times(1)).add(x, getUserIdentifier(x), getUserIdentifier(x), headingSize, true, true, true, false);
        }

        // Verify loading popup.
        verify(loadingBox, times(1)).hide();

    }

    protected EntitiesList.Callback<User> createEntitiesListCallback(Collection<User> entities, boolean canRead,
                                                                     boolean canRemove, boolean canSelect) {
        EntitiesList.Callback<User> callback = mock(EntitiesList.Callback.class);
        when(callback.getEntityType()).thenReturn("User");
        when(callback.canRead()).thenReturn(canRead);
        when(callback.canRemove()).thenReturn(canRemove);
        when(callback.canSelect()).thenReturn(canSelect);
        when(callback.isSelected(anyString())).thenReturn(false);
        when(callback.getIdentifier(any(User.class))).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                User user = (User) invocationOnMock.getArguments()[0];
                return user.getIdentifier();
            }
        });
        when(callback.getTitle(any(User.class))).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                User user = (User) invocationOnMock.getArguments()[0];
                return user.getIdentifier();
            }
        });
        return callback;
    }
    
    protected AbstractEntityManager.SearchResponse<User> createSearchResponse(final List<User> users, final int total, 
                                                                            final boolean hasNextPage, final String searchPattern,
                                                                            final int page, final int pageSize) {
        AbstractEntityManager.SearchResponse<User> response = mock(AbstractEntityManager.SearchResponse.class);
        when(response.getResults()).thenReturn(users);
        when(response.getTotal()).thenReturn(total);
        when(response.hasNextPage()).thenReturn(hasNextPage);
        when(response.getSearchPattern()).thenReturn(searchPattern);
        when(response.getPage()).thenReturn(page);
        when(response.getPageSize()).thenReturn(pageSize);
        return response;
    }
    
}
