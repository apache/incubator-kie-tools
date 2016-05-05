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

package org.uberfire.ext.security.management.client.widgets.management.explorer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.constants.LabelType;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.widgets.management.AbstractSecurityManagementTest;
import org.uberfire.ext.security.management.client.widgets.management.events.*;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesList;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.mocks.EventSourceMock;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class UsersExplorerTest extends AbstractSecurityManagementTest {

    @Mock EntitiesExplorerView view;
    @Mock EventSourceMock<OnErrorEvent> onErrorEvent;
    @Mock LoadingBox loadingBox;
    @Mock EntitiesList<User> entitiesList;
    @Mock EventSourceMock<ReadUserEvent> readUserEvent;
    
    private UsersExplorer presenter;

    @Before
    public void setup() {
        super.setup();
        presenter = new UsersExplorer(userSystemManager, onErrorEvent, loadingBox, entitiesList, view, readUserEvent);
        assertEquals(view.asWidget(), presenter.asWidget());
    }

    @Test
    public void testClear() throws Exception {
        presenter.clear();
        verify(view, times(1)).clear();
        verify(view, times(0)).clearSearch();
        verify(view, times(0)).showMessage(any(LabelType.class), anyString());
        verify(view, times(0)).show(any(EntitiesExplorerView.ViewContext.class), any(EntitiesExplorerView.ViewCallback.class));
        assertNull(presenter.context);
        assertNull(presenter.selected);
        Assert.assertEquals(presenter.currentPage, 1);
    }

    @Test
    public void testLoadingViewShow() throws Exception {
        presenter.showLoadingView();
        verify(loadingBox, times(1)).show();
    }

    @Test
    public void testLoadingViewHide() throws Exception {
        presenter.hideLoadingView();
        verify(loadingBox, times(1)).hide();
    }
    
    @Test
    public void testShowError() throws Exception {
        final String message = "error1";
        final Throwable t = mock(Throwable.class);
        when(t.getMessage()).thenReturn(message);
        presenter.showError(t);
        verify(loadingBox, times(0)).show();
        verify(loadingBox, times(1)).hide();
        verify(onErrorEvent, times(1)).fire(any(OnErrorEvent.class));
        
    }

    @Test
    public void testShowNotAllowed() throws Exception {
        when(userSystemManager.isUserCapabilityEnabled(Capability.CAN_SEARCH_USERS)).thenReturn(false);
        final ExplorerViewContext context = mock(ExplorerViewContext.class);
        presenter.show(context);
        verify(view, times(1)).showMessage(any(LabelType.class), anyString());
        verify(view, times(0)).show(any(EntitiesExplorerView.ViewContext.class), any(EntitiesExplorerView.ViewCallback.class));
    }
    
    @Test
    public void testShow() throws Exception {
        /// The mocked view context.
        final ExplorerViewContext context = createContext(true, true, true, true, true, new HashSet<String>());

        /// The mocked service response.
        final List<User> users = buildUsersList(10);
        final AbstractEntityManager.SearchResponse<User> response = createResponse(users, 10, false);

        // Test the show method logic.
        testShow(context, response);
    }

    @Test
    public void testShowWithSelectedUsers() throws Exception {
        /// The mocked view context.
        final Set<String> selectedUsers = new HashSet<String>(5);
        selectedUsers.add("user0");
        selectedUsers.add("user1");
        selectedUsers.add("user2");
        selectedUsers.add("user3");
        selectedUsers.add("user4");
        final ExplorerViewContext context = createContext(true, true, true, true, true, selectedUsers);

        /// The mocked service response.
        final List<User> users = buildUsersList(10);
        final AbstractEntityManager.SearchResponse<User> response = createResponse(users, 10, false);

        // Test the show method logic.
        testShow(context, response);
    }

    @Test
    public void testOnUserDeleted() throws Exception {
        final DeleteUserEvent deleteUserEvent = mock(DeleteUserEvent.class);
        presenter.onUserDeleted(deleteUserEvent);
        verify(userManagerService, times(1)).search(any(AbstractEntityManager.SearchRequest.class));
    }

    @Test
    public void testOnCreateUser() throws Exception {
        final CreateUserEvent createUserEvent = mock(CreateUserEvent.class);
        presenter.onUserCreated(createUserEvent);
        verify(userManagerService, times(1)).search(any(AbstractEntityManager.SearchRequest.class));
    }

    @Test
    public void testOnSaveUser() throws Exception {
        final SaveUserEvent saveUserEvent = mock(SaveUserEvent.class);
        presenter.onUserSaved(saveUserEvent);
        verify(userManagerService, times(1)).search(any(AbstractEntityManager.SearchRequest.class));
    }

    private ExplorerViewContext createContext(final boolean canCreate, final boolean canRead,
                                              final boolean canDelete, final boolean canSearch,
                                              final boolean canSelect, final Set<String> selectedUsers) {
        final ExplorerViewContext context = mock(ExplorerViewContext.class);
        when(context.canCreate()).thenReturn(canCreate);
        when(context.canRead()).thenReturn(canRead);
        when(context.canDelete()).thenReturn(canDelete);
        when(context.canSearch()).thenReturn(canSearch);
        when(context.canSelect()).thenReturn(canSelect);
        when(context.getSelectedEntities()).thenReturn(selectedUsers);
        return context;
    }
    
    private AbstractEntityManager.SearchResponse<User> createResponse(final List<User> users, final int size, 
                                                                      final boolean hasNextPage) {
        final AbstractEntityManager.SearchResponse<User> response = mock(AbstractEntityManager.SearchResponse.class);
        when(response.getResults()).thenReturn(users);
        when(response.getTotal()).thenReturn(size);
        when(response.hasNextPage()).thenReturn(hasNextPage);
        return response;
    }
    private void testShow(final ExplorerViewContext context , final AbstractEntityManager.SearchResponse<User> response) {
        when(userManagerService.search(any(AbstractEntityManager.SearchRequest.class))).thenAnswer(new Answer<AbstractEntityManager.SearchResponse<User>>() {
            @Override
            public AbstractEntityManager.SearchResponse<User> answer(InvocationOnMock invocationOnMock) throws Throwable {
                return response;
            }
        });
        
        // Run the logic.
        presenter.show(context);

        // State assertions.
        assertEquals(context, presenter.context);
        assertEquals(context.getSelectedEntities(), presenter.selected);
        verify(context, times(1)).setParent(any(EntitiesExplorerView.ViewContext.class));

        // Verify no messages shown.
        verify(view, times(0)).showMessage(any(LabelType.class), anyString());

        // Verify loading box.
        verify(loadingBox, times(1)).show();
        verify(loadingBox, times(1)).hide();

        // Verify entitiesList#show is called once and the generated callback argument. 
        final ArgumentCaptor<AbstractEntityManager.SearchResponse> responseArgumentCaptor =  ArgumentCaptor.forClass(AbstractEntityManager.SearchResponse.class);
        final ArgumentCaptor<EntitiesList.Callback> callbackArgumentCaptor = ArgumentCaptor.forClass(EntitiesList.Callback.class);
        verify(entitiesList, times(1)).show(responseArgumentCaptor.capture(), callbackArgumentCaptor.capture());
        final AbstractEntityManager.SearchResponse responseArgumentCaptured = responseArgumentCaptor.getValue();
        final List<User> results = response.getResults();
        assertEquals(results, responseArgumentCaptured.getResults());
        assertEquals(response.getTotal(), responseArgumentCaptured.getTotal());
        assertEquals(response.hasNextPage(), responseArgumentCaptured.hasNextPage());
        final EntitiesList.Callback callbackArgumentCaptured = callbackArgumentCaptor.getValue();
        assertEquals(context.canRead(), callbackArgumentCaptured.canRead());
        assertEquals(context.canDelete(), callbackArgumentCaptured.canRemove());
        assertEquals(context.canSelect(), callbackArgumentCaptured.canSelect());
        
        int x = 0;
        for (User user : results) {
            final String username = getUserIdentifier(x);
            assertEquals(username, callbackArgumentCaptured.getIdentifier(user));
            assertEquals(username, callbackArgumentCaptured.getTitle(user));
            final boolean isSelected = context.getSelectedEntities() != null && context.getSelectedEntities().contains(username);
            assertEquals(isSelected, callbackArgumentCaptured.isSelected(user.getIdentifier()));
            x++;
        }
    }
    
}
