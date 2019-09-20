/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.comment;

import java.util.Date;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.util.DateUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CommentItemPresenterTest {

    private CommentItemPresenter presenter;

    @Mock
    private CommentItemPresenter.View view;

    @Mock
    private DateUtils dateUtils;

    @Mock
    private ChangeRequestService changeRequestService;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Before
    public void setUp() {
        User user = mock(User.class);
        doReturn("admin").when(user).getIdentifier();
        doReturn(user).when(sessionInfo).getIdentity();

        WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        doReturn(mock(KieModule.class)).when(workspaceProject).getMainModule();
        doReturn(mock(Repository.class)).when(workspaceProject).getRepository();
        doReturn(mock(Space.class)).when(workspaceProject).getSpace();

        doReturn(workspaceProject).when(libraryPlaces).getActiveWorkspace();

        this.presenter = spy(new CommentItemPresenter(view,
                                                      dateUtils,
                                                      new CallerMock<>(changeRequestService),
                                                      sessionInfo,
                                                      libraryPlaces));
    }

    @Test
    public void postConstructTest() {
        presenter.postConstruct();

        verify(view).init(presenter);
    }

    @Test
    public void setupWhenNotAuthorTest() {
        presenter.setup(1L,
                        10L,
                        "user",
                        new Date(),
                        "My comment");

        verify(view).showActions(false);
    }

    @Test
    public void setupWhenAuthorTest() {
        presenter.setup(1L,
                        10L,
                        "admin",
                        new Date(),
                        "My comment");

        verify(view).showActions(true);
    }

    @Test
    public void deleteWhenAuthorTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        CommentItemPresenter.class.getDeclaredField("authorId")).set("admin");

        presenter.delete();

        verify(changeRequestService).deleteComment(anyString(),
                                                   anyString(),
                                                   anyLong(),
                                                   anyLong());
    }

    @Test
    public void deleteWhenNotAuthorTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        CommentItemPresenter.class.getDeclaredField("authorId")).set("user");

        presenter.delete();

        verify(changeRequestService, never()).deleteComment(anyString(),
                                                            anyString(),
                                                            anyLong(),
                                                            anyLong());
    }
}