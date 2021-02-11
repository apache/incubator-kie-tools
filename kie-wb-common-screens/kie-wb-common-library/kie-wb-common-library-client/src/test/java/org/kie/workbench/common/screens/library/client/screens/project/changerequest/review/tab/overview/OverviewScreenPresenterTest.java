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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.overview;

import java.util.Collections;
import java.util.Date;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestComment;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatus;
import org.guvnor.structure.repositories.changerequest.portable.PaginatedChangeRequestCommentList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.ChangeRequestUtils;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.comment.CommentItemPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class OverviewScreenPresenterTest {

    private OverviewScreenPresenter presenter;

    @Mock
    private OverviewScreenPresenter.View view;

    @Mock
    private TranslationService ts;

    @Mock
    private ManagedInstance<CommentItemPresenter> commentItemPresenterInstances;

    @Mock
    private ChangeRequestService changeRequestService;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private ChangeRequestUtils changeRequestUtils;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private WorkspaceProject workspaceProject;

    @Before
    public void setUp() {
        User user = mock(User.class);
        doReturn("admin").when(user).getIdentifier();
        doReturn(user).when(sessionInfo).getIdentity();

        Repository repository = mock(Repository.class);

        doReturn(workspaceProject).when(libraryPlaces).getActiveWorkspace();
        doReturn(mock(KieModule.class)).when(workspaceProject).getMainModule();
        doReturn(repository).when(workspaceProject).getRepository();
        doReturn(mock(Space.class)).when(workspaceProject).getSpace();

        doReturn(mock(CommentItemPresenter.class)).when(commentItemPresenterInstances).get();

        PaginatedChangeRequestCommentList paginatedList = new PaginatedChangeRequestCommentList(Collections.emptyList(),
                                                                                                0,
                                                                                                0,
                                                                                                0);
        doReturn(paginatedList).when(changeRequestService).getComments(Mockito.<String> any(),
                                                                       Mockito.<String> any(),
                                                                       anyLong(),
                                                                       anyInt(),
                                                                       anyInt());

        this.presenter = spy(new OverviewScreenPresenter(view,
                                                         ts,
                                                         commentItemPresenterInstances,
                                                         new CallerMock<>(changeRequestService),
                                                         libraryPlaces,
                                                         changeRequestUtils,
                                                         sessionInfo));
    }

    @Test
    public void postConstructTest() {
        presenter.postConstruct();

        verify(view).init(presenter);
    }

    @Test
    public void addCommentInvalidTextTest() {
        setPresenterPrivateField("changeRequestAuthorId", "admin");

        doReturn("").when(view).getCommentText();

        presenter.addComment();

        verify(changeRequestService, never()).addComment(Mockito.<String> any(),
                                                         Mockito.<String> any(),
                                                         anyLong(),
                                                         Mockito.<String> any());
    }

    @Test
    public void addCommentSuccessTest() {
        setPresenterPrivateField("workspaceProject", workspaceProject);

        doReturn("my comment").when(view).getCommentText();

        presenter.addComment();

        verify(changeRequestService).addComment(Mockito.<String> any(),
                                                Mockito.<String> any(),
                                                anyLong(),
                                                Mockito.<String> any());
    }

    @Test
    public void resetTest() {
        presenter.reset();

        verify(view).resetAll();
    }

    @Test
    public void setupNotAuthorTest() {
        setPresenterPrivateField("workspaceProject", workspaceProject);

        ChangeRequest changeRequest = mock(ChangeRequest.class);
        doReturn("user").when(changeRequest).getAuthorId();

        presenter.setup(changeRequest, b -> {
        });

        verify(view).showEditModes(false);
    }

    @Test
    public void setupIsAuthorTest() {
        setPresenterPrivateField("workspaceProject", workspaceProject);

        ChangeRequest changeRequest = mock(ChangeRequest.class);
        doReturn("admin").when(changeRequest).getAuthorId();

        presenter.setup(changeRequest, b -> {
        });

        verify(view).showEditModes(true);
    }

    @Test
    public void showConflictIfOpenTest() {
        ChangeRequest changeRequest = mock(ChangeRequest.class);
        doReturn(ChangeRequestStatus.OPEN).when(changeRequest).getStatus();
        doReturn(true).when(changeRequest).isConflict();

        presenter.checkWarnConflict(changeRequest);

        verify(view).showConflictWarning(true);
    }

    @Test
    public void hideConflictIfNotOpenTest() {
        ChangeRequest changeRequest = mock(ChangeRequest.class);
        doReturn(ChangeRequestStatus.REJECTED).when(changeRequest).getStatus();
        doReturn(true).when(changeRequest).isConflict();

        presenter.checkWarnConflict(changeRequest);

        verify(view).showConflictWarning(false);
    }

    @Test
    public void hideConflictIfNotInConflictTest() {
        ChangeRequest changeRequest = mock(ChangeRequest.class);
        doReturn(ChangeRequestStatus.OPEN).when(changeRequest).getStatus();
        doReturn(false).when(changeRequest).isConflict();

        presenter.checkWarnConflict(changeRequest);

        verify(view).showConflictWarning(false);
    }

    @Test
    public void setupCommentsTest() {
        setPresenterPrivateField("workspaceProject", workspaceProject);

        ChangeRequest changeRequest = mock(ChangeRequest.class);
        doReturn("user").when(changeRequest).getAuthorId();
        doReturn(ChangeRequestStatus.OPEN).when(changeRequest).getStatus();
        doReturn(true).when(changeRequest).isConflict();

        ChangeRequestComment changeRequestComment = new ChangeRequestComment(1L,
                                                                             "admin",
                                                                             new Date(),
                                                                             "text");
        PaginatedChangeRequestCommentList paginatedList =
                new PaginatedChangeRequestCommentList(Collections.nCopies(5, changeRequestComment),
                                                      0,
                                                      10,
                                                      5);

        doReturn(paginatedList).when(changeRequestService).getComments(Mockito.<String> any(),
                                                                       Mockito.<String> any(),
                                                                       anyLong(),
                                                                       anyInt(),
                                                                       anyInt());

        presenter.setup(changeRequest, b -> {
        });

        verify(commentItemPresenterInstances, times(5)).get();
        verify(view, times(5)).addCommentItem(any());
    }

    @Test
    public void startEditSummaryTest() {
        presenter.startEditSummary();

        verify(view).enableSummaryEditMode(true);
    }

    @Test
    public void cancelSummaryEditionTest() {
        presenter.cancelSummaryEdition();

        verify(view).enableSummaryEditMode(false);
    }

    @Test
    public void saveSummaryEditionTest() {
        setPresenterPrivateField("workspaceProject", workspaceProject);
        setPresenterPrivateField("changeRequestAuthorId", "admin");

        doReturn("my updated summary").when(view).getSummaryInputText();

        presenter.saveSummaryEdition();

        verify(changeRequestService).updateChangeRequestSummary(Mockito.<String> any(),
                                                                Mockito.<String> any(),
                                                                anyLong(),
                                                                Mockito.<String> any());
    }

    @Test
    public void startEditDescriptionTest() {
        presenter.startEditDescription();

        verify(view).enableDescriptionEditMode(true);
    }

    @Test
    public void cancelDescriptionEditionTest() {
        presenter.cancelDescriptionEdition();

        verify(view).enableDescriptionEditMode(false);
    }

    @Test
    public void saveDescriptionEditionTest() {
        setPresenterPrivateField("workspaceProject", workspaceProject);
        setPresenterPrivateField("changeRequestAuthorId", "admin");

        doReturn("my updated description").when(view).getDescriptionInputText();

        presenter.saveDescriptionEdition();

        verify(changeRequestService).updateChangeRequestDescription(Mockito.<String> any(),
                                                                    Mockito.<String> any(),
                                                                    anyLong(),
                                                                    Mockito.<String> any());
    }

    @Test
    public void nextCommentPageTest() {
        setPresenterPrivateField("workspaceProject", workspaceProject);
        setPresenterPrivateField("commentCurrentPage", 1);
        setPresenterPrivateField("commentTotalPages", 10);

        presenter.nextCommentPage();

        verify(view).setCommentCurrentPage(2);
    }

    @Test
    public void nextCommentPageDoNothingTest() {
        setPresenterPrivateField("workspaceProject", workspaceProject);
        setPresenterPrivateField("commentCurrentPage", 10);
        setPresenterPrivateField("commentTotalPages", 10);

        presenter.nextCommentPage();

        verify(view, never()).setCommentCurrentPage(anyInt());
    }

    @Test
    public void prevCommentPageTest() {
        setPresenterPrivateField("workspaceProject", workspaceProject);
        setPresenterPrivateField("commentCurrentPage", 5);
        setPresenterPrivateField("commentTotalPages", 10);

        presenter.prevCommentPage();

        verify(view).setCommentCurrentPage(4);
    }

    @Test
    public void prevCommentPageDoNothingTest() {
        setPresenterPrivateField("workspaceProject", workspaceProject);
        setPresenterPrivateField("commentCurrentPage", 1);
        setPresenterPrivateField("commentTotalPages", 10);

        presenter.prevCommentPage();

        verify(view, never()).setCommentCurrentPage(anyInt());
    }

    @Test
    public void setCommentCurrentPageTest() {
        setPresenterPrivateField("workspaceProject", workspaceProject);
        setPresenterPrivateField("commentTotalPages", 10);

        presenter.setCommentCurrentPage(5);

        verify(view).enableCommentPreviousButton(anyBoolean());
        verify(view).enableCommentNextButton(anyBoolean());
    }

    @Test
    public void setCommentCurrentOutRangeTest() {
        setPresenterPrivateField("workspaceProject", workspaceProject);
        setPresenterPrivateField("commentCurrentPage", 10);
        setPresenterPrivateField("commentTotalPages", 10);

        presenter.setCommentCurrentPage(50);

        verify(view).setCommentCurrentPage(10);
        verify(view, never()).enableCommentPreviousButton(anyBoolean());
        verify(view, never()).enableCommentNextButton(anyBoolean());
    }

    private void setPresenterPrivateField(final String fieldName, final Object value) {
        try {
            FieldUtils.writeField(OverviewScreenPresenter.class.getDeclaredField(fieldName), presenter, value, true);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Assert.fail();
        }
    }
}
