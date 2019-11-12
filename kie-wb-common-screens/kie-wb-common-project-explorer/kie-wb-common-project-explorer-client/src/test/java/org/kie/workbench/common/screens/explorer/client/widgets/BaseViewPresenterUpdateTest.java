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
package org.kie.workbench.common.screens.explorer.client.widgets;

import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewWidget;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class BaseViewPresenterUpdateTest {

    @Mock
    protected DeletePopUpPresenter deletePopUpPresenterMock;
    @Mock
    protected RenamePopUpPresenter renamePopUpPresenterMock;
    @Mock
    protected CopyPopUpPresenter copyPopUpPresenterMock;
    @Mock
    protected CopyPopupWithPackageView copyPopUpView;
    @Mock
    protected RenamePopUpPresenter.View renamePopUpView;
    @GwtMock
    CommonConstants commonConstants;
    private ExplorerService explorerServiceActual = mock(ExplorerService.class);
    private BuildService buildServiceActual = mock(BuildService.class);
    @Spy
    private Caller<ExplorerService> explorerService = new CallerMock<ExplorerService>(explorerServiceActual);
    @Spy
    private Caller<BuildService> buildService = new CallerMock<BuildService>(buildServiceActual);
    @Spy
    private Caller<VFSService> vfsService = new CallerMock<VFSService>(mock(VFSService.class));
    @Spy
    private Caller<ValidationService> validationService = new CallerMock<ValidationService>(mock(ValidationService.class));
    @Mock
    private EventSourceMock<BuildResults> buildResultsEvent;
    @Mock
    private EventSourceMock<WorkspaceProjectContextChangeEvent> contextChangedEvent;
    @Mock
    private EventSourceMock<NotificationEvent> notification;
    @Mock
    private User identity;
    @Mock
    private PlaceManager placeManager;
    @Mock
    private SessionInfo sessionInfo;
    @Mock
    private BusinessViewWidget view;
    @Mock
    private ActiveContextItems activeContextItems;
    @Mock
    private ActiveContextManager activeContextManager;
    @Mock
    private ActiveContextOptions activeContextOptions;
    private boolean isPresenterVisible = true;

    @InjectMocks
    private BaseViewPresenter presenter = new BaseViewPresenter(view) {
        {
            this.deletePopUpPresenter = deletePopUpPresenterMock;
            this.renamePopUpPresenter = renamePopUpPresenterMock;
            this.copyPopUpPresenter = copyPopUpPresenterMock;
        }

        @Override
        protected boolean isViewVisible() {

            return isPresenterVisible;
        }
    };

    private ProjectExplorerContent content = new ProjectExplorerContent(
            new WorkspaceProject(mock(OrganizationalUnit.class),
                                 mock(Repository.class),
                                 new Branch("master",
                                            mock(Path.class)),
                                 mock(Module.class)),
            new Module(),
            new FolderListing(),
            Collections.<FolderItem, List<FolderItem>>emptyMap());

    @Before
    public void setup() {
        when(view.getExplorer()).thenReturn(mock(Explorer.class));
        when(explorerServiceActual.getContent(any(ProjectExplorerContentQuery.class))).thenReturn(content);
        when(activeContextOptions.getOptions()).thenReturn(new ActiveOptions());
    }

    @Test
    public void showHiddenFiles() throws Exception {
        when(activeContextOptions.areHiddenFilesVisible()).thenReturn(true);
        presenter.update();

        verify(view).showHiddenFiles(true);
    }

    @Test
    public void hideHiddenFiles() throws Exception {
        when(activeContextOptions.areHiddenFilesVisible()).thenReturn(false);
        presenter.update();

        verify(view).showHiddenFiles(false);
    }

    @Test
    public void showTreeNavType() throws Exception {
        when(activeContextOptions.isTreeNavigatorVisible()).thenReturn(true);
        presenter.update();

        verify(view).setNavType(Explorer.NavType.TREE);
    }

    @Test
    public void showBreadCrumbNavType() throws Exception {
        when(activeContextOptions.isTreeNavigatorVisible()).thenReturn(false);
        presenter.update();

        verify(view).setNavType(Explorer.NavType.BREADCRUMB);
    }

    @Test
    public void hideHeaderNavigation() throws Exception {
        when(activeContextOptions.isHeaderNavigationHidden()).thenReturn(true);
        presenter.update();

        verify(view).hideHeaderNavigator();
    }

    @Test
    public void showHeaderNavigation() throws Exception {
        when(activeContextOptions.isHeaderNavigationHidden()).thenReturn(false);
        presenter.update();

        verify(view).showHeaderNavigator();
    }

    @Test
    public void hideTagWhenActiveContentDoesNotExist() throws Exception {
        when(activeContextOptions.canShowTag()).thenReturn(false);
        presenter.update();

        verify(view,
               never()).setItems(any());
    }

    @Test
    public void hideTagWhenActiveContentExists() throws Exception {
        when(activeContextOptions.canShowTag()).thenReturn(false);

        final FolderListing folderListing = mock(FolderListing.class);
        when(activeContextItems.getActiveContent()).thenReturn(folderListing);
        presenter.update();

        verify(view).setItems(folderListing);
    }
}
