/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor;

import java.util.Collections;
import java.util.Optional;

import com.google.gwt.core.client.Scheduler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.guvnor.common.services.project.categories.Decision;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTableEditorPresenterTest extends BaseGuidedDecisionTablePresenterTest<GuidedDecisionTableEditorPresenter> {

    private GuidedDTableResourceType resourceType = new GuidedDTableResourceType(new Decision());

    @Mock
    private SaveAndRenameCommandBuilder<GuidedDecisionTable52, Metadata> saveAndRenameCommandBuilder;

    @Mock
    protected AuthoringWorkbenchDocks docks;

    @Override
    protected GuidedDecisionTableEditorPresenter getPresenter() {
        return new GuidedDecisionTableEditorPresenter(view,
                                                      dtServiceCaller,
                                                      docks,
                                                      mock(PerspectiveManager.class),
                                                      notification,
                                                      decisionTableSelectedEvent,
                                                      validationPopup,
                                                      resourceType,
                                                      editMenuBuilder,
                                                      viewMenuBuilder,
                                                      insertMenuBuilder,
                                                      radarMenuBuilder,
                                                      modeller,
                                                      beanManager,
                                                      placeManager,
                                                      columnsPage,
                                                      saveAndRenameCommandBuilder,
                                                      alertsButtonMenuItemBuilder,
                                                      downloadMenuItem) {
            {
                workbenchContext = GuidedDecisionTableEditorPresenterTest.this.workbenchContext;
                projectController = GuidedDecisionTableEditorPresenterTest.this.projectController;
                promises = GuidedDecisionTableEditorPresenterTest.this.promises;
            }

            @Override
            protected Command getSaveAndRenameCommand() {
                return mock(Command.class);
            }
        };
    }

    @Test
    public void testSetupMenuBar() {
        verify(fileMenuBuilder,
               times(1)).addSave(any(MenuItem.class));
        verify(fileMenuBuilder,
               times(1)).addCopy(any(BasicFileMenuBuilder.PathProvider.class),
                                 eq(assetUpdateValidator));
        verify(fileMenuBuilder,
               times(1)).addRename(any(Command.class));
        verify(fileMenuBuilder,
               times(1)).addDelete(any(BasicFileMenuBuilder.PathProvider.class),
                                   eq(assetUpdateValidator));
        verify(fileMenuBuilder,
               times(1)).addValidate(any(Command.class));
        verify(fileMenuBuilder,
               times(1)).addNewTopLevelMenu(eq(editMenuItem));
        verify(fileMenuBuilder,
               times(1)).addNewTopLevelMenu(eq(viewMenuItem));
        verify(fileMenuBuilder,
               times(1)).addNewTopLevelMenu(eq(insertMenuItem));
        verify(fileMenuBuilder,
               times(1)).addNewTopLevelMenu(eq(radarMenuItem));
        verify(fileMenuBuilder,
               times(1)).addNewTopLevelMenu(eq(versionManagerMenuItem));
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        reset(fileMenuBuilder);
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(promises.resolve(false)).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder,
               never()).addSave(any(MenuItem.class));
        verify(fileMenuBuilder,
               never()).addCopy(any(BasicFileMenuBuilder.PathProvider.class),
                                eq(assetUpdateValidator));
        verify(fileMenuBuilder,
               never()).addRename(any(BasicFileMenuBuilder.PathProvider.class),
                                  eq(assetUpdateValidator));
        verify(fileMenuBuilder,
               never()).addDelete(any(BasicFileMenuBuilder.PathProvider.class),
                                  eq(assetUpdateValidator));
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
    }

    @Test
    public void startUpSelectsDecisionTable() {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(path,
                                                                                path,
                                                                                placeRequest,
                                                                                content);

        presenter.onStartup(path,
                            placeRequest);

        verify(decisionTableSelectedEvent,
               times(1)).fire(dtSelectedEventCaptor.capture());

        final DecisionTableSelectedEvent dtSelectedEvent = dtSelectedEventCaptor.getValue();
        assertNotNull(dtSelectedEvent);
        assertTrue(dtSelectedEvent.getPresenter().isPresent());
        assertEquals(dtPresenter,
                     dtSelectedEvent.getPresenter().get());

        verify(modellerGridPanel).setFocus(eq(true));
    }

    @Test
    public void checkGetAvailableDocumentPaths() {
        presenter.getAvailableDocumentPaths((result) -> assertTrue(result.isEmpty()));
    }

    @Test
    public void checkOnOpenDocumentsInEditor() {
        exception.expect(UnsupportedOperationException.class);
        presenter.onOpenDocumentsInEditor(Collections.<Path>emptyList());
    }

    @Test
    public void checkRemoveDocumentClosesEditor() {
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableView.Presenter dtPresenter = mock(GuidedDecisionTableView.Presenter.class);
        presenter.editorPlaceRequest = placeRequest;

        presenter.removeDocument(dtPresenter);

        final ArgumentCaptor<Scheduler.ScheduledCommand> commandCaptor = ArgumentCaptor.forClass(Scheduler.ScheduledCommand.class);

        verify(presenter,
               times(1)).scheduleClosure(commandCaptor.capture());

        final Scheduler.ScheduledCommand command = commandCaptor.getValue();
        assertNotNull(command);
        command.execute();

        verify(placeManager,
               times(1)).forceClosePlace(eq(placeRequest));
    }

    @Test
    public void testGetMetadataSupplier() {

        final GuidedDecisionTableView.Presenter document = mock(GuidedDecisionTableView.Presenter.class);
        final Overview overview = mock(Overview.class);
        final Metadata expectedMetadata = mock(Metadata.class);

        doReturn(document).when(presenter).getActiveDocument();
        doReturn(overview).when(document).getOverview();
        doReturn(expectedMetadata).when(overview).getMetadata();

        final Metadata actualMetadata = presenter.getMetadataSupplier().get();

        assertEquals(expectedMetadata, actualMetadata);
    }

    @Test
    public void testGetContentSupplier() {

        final GuidedDecisionTableView.Presenter presenter = mock(GuidedDecisionTableView.Presenter.class);
        final GuidedDecisionTable52 expected = mock(GuidedDecisionTable52.class);

        doReturn(expected).when(presenter).getModel();
        doReturn(presenter).when(this.presenter).getActiveDocument();

        final GuidedDecisionTable52 actual = this.presenter.getContentSupplier().get();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetIsDirtySupplierWhenItIsDirty() {

        final GuidedDecisionTableView.Presenter presenter = mock(GuidedDecisionTableView.Presenter.class);
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);
        final int currentHash = 456;
        final int originalHash = 123;

        doReturn(currentHash).when(this.presenter).currentHashCode(presenter);
        doReturn(originalHash).when(this.presenter).originalHashCode(presenter);
        doReturn(model).when(presenter).getModel();
        doReturn(presenter).when(this.presenter).getActiveDocument();

        final boolean isDirty = this.presenter.getIsDirtySupplier().get();

        assertTrue(isDirty);
    }

    @Test
    public void testGetIsDirtySupplierWhenItIsNotDirty() {

        final GuidedDecisionTableView.Presenter presenter = mock(GuidedDecisionTableView.Presenter.class);
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);
        final int currentHash = 123;
        final int originalHash = 123;

        doReturn(currentHash).when(this.presenter).currentHashCode(presenter);
        doReturn(originalHash).when(this.presenter).originalHashCode(presenter);
        doReturn(model).when(presenter).getModel();
        doReturn(presenter).when(this.presenter).getActiveDocument();

        final boolean isDirty = this.presenter.getIsDirtySupplier().get();

        assertFalse(isDirty);
    }

    @Test
    public void testImportsTabIsAdded() {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtDocument = makeDecisionTable(path,
                                                                               path,
                                                                               placeRequest,
                                                                               content);

        presenter.registerDocument(dtDocument);
        presenter.refreshDocument(dtDocument);

        verify(kieEditorWrapperView).addImportsTab(eq(importsWidget));
        final AsyncPackageDataModelOracle oracle = dtDocument.getDataModelOracle();
        final Imports imports = dtDocument.getModel().getImports();
        verify(importsWidget).setContent(same(oracle), same(imports), eq(false));
    }
}
