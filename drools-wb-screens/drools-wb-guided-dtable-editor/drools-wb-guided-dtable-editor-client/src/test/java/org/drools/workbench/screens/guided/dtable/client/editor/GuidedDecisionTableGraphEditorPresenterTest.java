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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableGraphResourceType;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter.Access.LockedBy;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.wizard.NewGuidedDecisionTableWizardHelper;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphContent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphModel;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphModel.GuidedDecisionTableGraphEntry;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableGraphEditorService;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableGraphSaveAndRenameService;
import org.guvnor.common.services.project.categories.Decision;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.search.common.SearchPerformedEvent;
import org.kie.workbench.common.widgets.client.source.ViewDRLSourceWidget;
import org.kie.workbench.common.widgets.metadata.client.KieDocument;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.ObservablePath.OnConcurrentUpdateEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.client.mvp.LockTarget;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.SaveInProgressEvent;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.ext.editor.commons.version.impl.PortableVersionRecord;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTableGraphEditorPresenterTest extends BaseGuidedDecisionTablePresenterTest<GuidedDecisionTableGraphEditorPresenter> {

    private static final int INITIAL_HASH_CODE = 100;
    private static final int EDITOR_HASH_CODE = 200;

    @Mock
    private LockManager lockManager;

    @Mock
    private GuidedDecisionTableGraphEditorService dtGraphService;
    private Caller<GuidedDecisionTableGraphEditorService> dtGraphServiceCaller;

    @Mock
    private KieModuleService moduleService;
    private Caller<KieModuleService> moduleServiceCaller;

    @Mock
    private GuidedDecisionTableGraphSaveAndRenameService graphSaveAndRenameService;
    private Caller<GuidedDecisionTableGraphSaveAndRenameService> graphSaveAndRenameServiceCaller;

    @Mock
    private NewGuidedDecisionTableWizardHelper helper;

    @Mock
    private org.guvnor.common.services.project.model.Package activePackage;

    @Mock
    private Path activePackageResourcesPath;

    @Mock
    private SaveAndRenameCommandBuilder<List<GuidedDecisionTableEditorContent>, Metadata> saveAndRenameCommandBuilder;

    @Captor
    private ArgumentCaptor<ParameterizedCommand<KieDocument>> activateDocumentCommandCaptor;

    @Captor
    private ArgumentCaptor<ParameterizedCommand<KieDocument>> removeDocumentCommandCaptor;

    @Captor
    private ArgumentCaptor<Command> newDocumentCommandCaptor;

    @Captor
    private ArgumentCaptor<DecisionTableSelectedEvent> dtSelectedEventCaptor;

    @Captor
    private ArgumentCaptor<Path> dtPathCaptor;

    @Captor
    private ArgumentCaptor<List<Path>> dtPathsCaptor;

    @Captor
    private ArgumentCaptor<ObservablePath> dtObservablePathCaptor;

    @Captor
    private ArgumentCaptor<PathPlaceRequest> dtPathPlaceRequestCaptor;

    @Captor
    private ArgumentCaptor<LockTarget> lockTargetCaptor;

    @Captor
    private ArgumentCaptor<ParameterizedCommand<String>> commitMessageCommandCaptor;

    @Captor
    private ArgumentCaptor<Callback<VersionRecord>> versionRecordCallbackCaptor;

    @Captor
    private ArgumentCaptor<RemoteCallback<Path>> onSaveSuccessCallbackCaptor;

    @Mock
    protected AuthoringWorkbenchDocks docks;

    @Mock
    private EventSourceMock<SearchPerformedEvent> searchPerformed;

    private Event<SaveInProgressEvent> saveInProgressEvent = spy(new EventSourceMock<SaveInProgressEvent>() {
        @Override
        public void fire(final SaveInProgressEvent event) {
            //Do nothing
        }
    });

    private GuidedDTableGraphResourceType dtGraphResourceType = new GuidedDTableGraphResourceType(new Decision());

    @Override
    @Before
    public void setup() {
        this.dtGraphServiceCaller = new CallerMock<>(dtGraphService);
        this.moduleServiceCaller = new CallerMock<>(moduleService);
        this.graphSaveAndRenameServiceCaller = new CallerMock<>(graphSaveAndRenameService);
        when(view.asWidget()).thenReturn(mock(Widget.class));
        when(moduleService.resolvePackage(any(Path.class))).thenReturn(activePackage);
        when(activePackage.getPackageMainResourcesPath()).thenReturn(activePackageResourcesPath);
        when(alertsButtonMenuItemBuilder.build()).thenReturn(alertsButtonMenuItem);

        super.setup();
    }

    @Override
    protected GuidedDecisionTableGraphEditorPresenter getPresenter() {
        return new GuidedDecisionTableGraphEditorPresenter(view,
                                                           dtServiceCaller,
                                                           docks,
                                                           mock(PerspectiveManager.class),
                                                           dtGraphServiceCaller,
                                                           moduleServiceCaller,
                                                           graphSaveAndRenameServiceCaller,
                                                           notification,
                                                           saveInProgressEvent,
                                                           decisionTableSelectedEvent,
                                                           validationPopup,
                                                           dtGraphResourceType,
                                                           editMenuBuilder,
                                                           viewMenuBuilder,
                                                           insertMenuBuilder,
                                                           radarMenuBuilder,
                                                           modeller,
                                                           helper,
                                                           beanManager,
                                                           placeManager,
                                                           lockManager,
                                                           columnsPage,
                                                           saveAndRenameCommandBuilder,
                                                           alertsButtonMenuItemBuilder,
                                                           downloadMenuItemBuilder,
                                                           editorSearchIndex,
                                                           searchBarComponent,
                                                           searchableElementFactory,
                                                           searchPerformed) {
            {
                workbenchContext = GuidedDecisionTableGraphEditorPresenterTest.this.workbenchContext;
                projectController = GuidedDecisionTableGraphEditorPresenterTest.this.projectController;
                promises = GuidedDecisionTableGraphEditorPresenterTest.this.promises;
            }

            @Override
            protected Command getSaveAndRenameCommand() {
                return mock(Command.class);
            }

            @Override
            PathPlaceRequest getPathPlaceRequest(final Path path) {
                //Avoid use of IOC.getBeanManager().lookupBean(..) in PathPlaceRequest for Unit Tests
                final PathPlaceRequest pathPlaceRequest = new PathPlaceRequest(path) {
                    @Override
                    protected ObservablePath createObservablePath(final Path path) {
                        final ObservablePath op = new ObservablePathImpl().wrap(path);
                        return op;
                    }
                };
                return pathPlaceRequest;
            }
        };
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkInit() {
        verify(viewMenuBuilder,
               times(1)).setModeller(eq(modeller));
        verify(insertMenuBuilder,
               times(1)).setModeller(eq(modeller));
        verify(radarMenuBuilder,
               times(1)).setModeller(eq(modeller));
        verify(view,
               times(1)).setModellerView(eq(modellerView));

        verify(registeredDocumentsMenuBuilder,
               times(1)).setActivateDocumentCommand(any(ParameterizedCommand.class));
        verify(registeredDocumentsMenuBuilder,
               times(1)).setRemoveDocumentCommand(any(ParameterizedCommand.class));
        verify(registeredDocumentsMenuBuilder,
               times(1)).setNewDocumentCommand(any(Command.class));
    }

    @Test
    public void testGetCurrentHashCode() {

        final GuidedDecisionTableView.Presenter activeDocument1 = mock(GuidedDecisionTableView.Presenter.class);
        final GuidedDecisionTableView.Presenter activeDocument2 = mock(GuidedDecisionTableView.Presenter.class);
        final Integer activeDocument1Hashcode = 123;
        final Integer activeDocument2Hashcode = 456;
        final Integer expectedHashcode = 789;

        doReturn(asSet(activeDocument1, activeDocument2)).when(presenter).getAvailableDecisionTables();
        doReturn(123).when(presenter).currentHashCode(activeDocument1);
        doReturn(456).when(presenter).currentHashCode(activeDocument2);
        doReturn(expectedHashcode).when(presenter).combineHashCodes(asList(activeDocument1Hashcode, activeDocument2Hashcode));
        doReturn(expectedHashcode).when(presenter).combineHashCodes(asList(activeDocument2Hashcode, activeDocument1Hashcode));

        final Integer actualHashcode = presenter.getCurrentHashCodeSupplier().get();

        assertEquals(expectedHashcode, actualHashcode);
    }

    @Test
    public void checkInitActivateDocumentFromRegisteredDocumentMenu() {
        verify(registeredDocumentsMenuBuilder,
               times(1)).setActivateDocumentCommand(activateDocumentCommandCaptor.capture());

        final GuidedDecisionTablePresenter dtPresenter = mock(GuidedDecisionTablePresenter.class);

        final ParameterizedCommand<KieDocument> activeDocumentCommand = activateDocumentCommandCaptor.getValue();
        assertNotNull(activeDocumentCommand);
        activeDocumentCommand.execute(dtPresenter);
        verify(decisionTableSelectedEvent,
               times(1)).fire(dtSelectedEventCaptor.capture());
        assertNotNull(dtSelectedEventCaptor.getValue());
        assertEquals(dtPresenter,
                     dtSelectedEventCaptor.getValue().getPresenter().get());
    }

    @Test
    public void checkInitRemoveDocumentFromRegisteredDocumentMenu() {
        verify(registeredDocumentsMenuBuilder,
               times(1)).setRemoveDocumentCommand(removeDocumentCommandCaptor.capture());

        final GuidedDecisionTablePresenter dtPresenter = mock(GuidedDecisionTablePresenter.class);

        doReturn(true).when(presenter).mayClose(eq(dtPresenter));
        doNothing().when(presenter).removeDocument(any(GuidedDecisionTablePresenter.class));

        final ParameterizedCommand<KieDocument> removeDocumentCommand = removeDocumentCommandCaptor.getValue();
        assertNotNull(removeDocumentCommand);
        removeDocumentCommand.execute(dtPresenter);
        verify(presenter,
               times(1)).mayClose(eq(dtPresenter));
        verify(presenter,
               times(1)).removeDocument(eq(dtPresenter));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkInitNewDocumentFromRegisteredDocumentMenu() {
        verify(registeredDocumentsMenuBuilder,
               times(1)).setNewDocumentCommand(newDocumentCommandCaptor.capture());

        final Command newDocumentCommand = newDocumentCommandCaptor.getValue();
        assertNotNull(newDocumentCommand);
        newDocumentCommand.execute();

        verify(presenter,
               times(1)).onNewDocument();
        verify(helper,
               times(1)).createNewGuidedDecisionTable(eq(activePackageResourcesPath),
                                                      eq(""),
                                                      eq(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY),
                                                      eq(GuidedDecisionTable52.HitPolicy.NONE),
                                                      eq(view),
                                                      onSaveSuccessCallbackCaptor.capture());

        final Path dtPath = mock(Path.class);
        final RemoteCallback<Path> onSaveSuccessCallback = onSaveSuccessCallbackCaptor.getValue();
        assertNotNull(onSaveSuccessCallback);

        doNothing().when(presenter).onOpenDocumentsInEditor(any(List.class));

        onSaveSuccessCallback.callback(dtPath);

        verify(presenter,
               times(1)).onOpenDocumentsInEditor(dtPathsCaptor.capture());

        final List<Path> dtPaths = dtPathsCaptor.getValue();
        assertNotNull(dtPaths);
        assertEquals(1,
                     dtPaths.size());
        assertEquals(dtPath,
                     dtPaths.get(0));
    }

    @Test
    public void testSetupMenuBar() {
        verify(fileMenuBuilder).addSave(any(MenuItem.class));
        verify(fileMenuBuilder).addCopy(any(BasicFileMenuBuilder.PathProvider.class), eq(assetUpdateValidator));
        verify(fileMenuBuilder).addRename(any(Command.class));
        verify(fileMenuBuilder).addDelete(any(BasicFileMenuBuilder.PathProvider.class), eq(assetUpdateValidator));
        verify(fileMenuBuilder).addValidate(any(Command.class));
        verify(fileMenuBuilder).addNewTopLevelMenu(eq(editMenuItem));
        verify(fileMenuBuilder).addNewTopLevelMenu(eq(viewMenuItem));
        verify(fileMenuBuilder).addNewTopLevelMenu(eq(insertMenuItem));
        verify(fileMenuBuilder).addNewTopLevelMenu(eq(radarMenuItem));
        verify(fileMenuBuilder).addNewTopLevelMenu(eq(versionManagerMenuItem));
        verify(fileMenuBuilder).addNewTopLevelMenu(eq(registeredDocumentsMenuItem));
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
        verify(fileMenuBuilder).addNewTopLevelMenu(downloadMenuItemButton);
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
        verify(fileMenuBuilder,
               times(1)).addNewTopLevelMenu(alertsButtonMenuItem);
    }

    @Test
    public void checkOnStartupBasicInitialisation() {
        final ObservablePath dtGraphPath = mock(ObservablePath.class);
        final PlaceRequest dtGraphPlaceRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorGraphContent dtGraphContent = makeDecisionTableGraphContent(INITIAL_HASH_CODE);

        when(dtGraphPath.toURI()).thenReturn("dtGraphPath");
        when(dtGraphService.loadContent(eq(dtGraphPath))).thenReturn(dtGraphContent);
        when(versionRecordManager.getCurrentPath()).thenReturn(dtGraphPath);
        when(dtGraphPath.getFileName()).thenReturn("filename");

        presenter.onStartup(dtGraphPath,
                            dtGraphPlaceRequest);

        assertEquals(dtGraphPath,
                     presenter.editorPath);
        assertEquals(dtGraphPlaceRequest,
                     presenter.editorPlaceRequest);
        assertEquals(INITIAL_HASH_CODE,
                     (int) presenter.originalGraphHash);

        verify(presenter,
               times(1)).initialiseEditor(eq(dtGraphPath),
                                          eq(dtGraphPlaceRequest));
        verify(presenter,
               times(1)).initialiseVersionManager();
        verify(presenter,
               times(1)).addFileChangeListeners(eq(dtGraphPath));

        verify(lockManager,
               times(1)).init(lockTargetCaptor.capture());

        final LockTarget lockTarget = lockTargetCaptor.getValue();
        assertNotNull(lockTarget);
        assertEquals(dtGraphPath,
                     lockTarget.getPath());
        assertEquals(dtGraphPlaceRequest,
                     lockTarget.getPlace());
        assertNotNull(lockTarget.getTitle());
    }

    @Test
    public void checkOnStartupLoadGraphEntries() {
        final ObservablePath dtGraphPath = mock(ObservablePath.class);
        final PlaceRequest dtGraphPlaceRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorGraphContent dtGraphContent = makeDecisionTableGraphContent(INITIAL_HASH_CODE);

        final Path dtPath = mock(Path.class);
        final GuidedDecisionTableEditorContent dtContent = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(dtPath,
                                                                                dtGraphPath,
                                                                                dtGraphPlaceRequest,
                                                                                dtContent);

        final GuidedDecisionTableGraphEntry dtGraphEntry = new GuidedDecisionTableGraphEntry(dtPath,
                                                                                             dtPath);
        dtGraphContent.getModel().getEntries().add(dtGraphEntry);

        when(dtPath.toURI()).thenReturn("dtPath");
        when(dtGraphPath.toURI()).thenReturn("dtGraphPath");
        when(dtGraphPath.getFileName()).thenReturn("filename");
        when(dtService.loadContent(eq(dtPath))).thenReturn(dtContent);
        when(dtGraphService.loadContent(eq(dtGraphPath))).thenReturn(dtGraphContent);
        when(versionRecordManager.getCurrentPath()).thenReturn(dtGraphPath);

        //Fake building of Graph Model from Editor to control hashCode
        doReturn(makeDecisionTableGraphContent(EDITOR_HASH_CODE).getModel()).when(presenter).buildModelFromEditor();

        when(modeller.addDecisionTable(any(ObservablePath.class),
                                       any(PlaceRequest.class),
                                       any(GuidedDecisionTableEditorContent.class),
                                       any(Boolean.class),
                                       any(Double.class),
                                       any(Double.class))).thenReturn(dtPresenter);

        presenter.onStartup(dtGraphPath,
                            dtGraphPlaceRequest);

        verify(view,
               times(1)).showLoading();
        verify(presenter,
               times(1)).loadDocumentGraph(eq(dtGraphPath));

        verify(dtService,
               times(1)).loadContent(eq(dtPath));
        verify(modeller,
               times(1)).addDecisionTable(dtObservablePathCaptor.capture(),
                                          dtPathPlaceRequestCaptor.capture(),
                                          eq(dtContent),
                                          any(Boolean.class),
                                          eq(null),
                                          eq(null));
        final ObservablePath dtObservablePath = dtObservablePathCaptor.getValue();
        final PathPlaceRequest dtPathPlaceRequest = dtPathPlaceRequestCaptor.getValue();
        assertNotNull(dtObservablePath);
        assertNotNull(dtPathPlaceRequest);
        assertEquals(dtPath.toURI(),
                     dtObservablePath.toURI());
        assertEquals(dtPath.toURI(),
                     dtPathPlaceRequest.getPath().toURI());
        assertEquals(EDITOR_HASH_CODE,
                     (int) presenter.originalGraphHash);

        verify(presenter,
               times(1)).registerDocument(eq(dtPresenter));
        verify(view,
               times(1)).hideBusyIndicator();

        verify(decisionTableSelectedEvent,
               times(1)).fire(dtSelectedEventCaptor.capture());
        final DecisionTableSelectedEvent dtSelectedEvent = dtSelectedEventCaptor.getValue();
        assertNotNull(dtSelectedEvent);
        assertTrue(dtSelectedEvent.getPresenter().isPresent());
        assertEquals(dtPresenter,
                     dtSelectedEvent.getPresenter().get());

        verify(modellerGridPanel).setFocus(eq(true));

        verify(lockManager,
               never()).acquireLock();
    }

    @Test
    public void testLoadDocumentGraphEmptyModel() throws Exception {
        final ObservablePath documentPath = mock(ObservablePath.class);
        final Overview overview = mock(Overview.class);
        final GuidedDecisionTableEditorGraphModel graphModel = mock(GuidedDecisionTableEditorGraphModel.class);
        final GuidedDecisionTableEditorGraphContent graphContent = mock(GuidedDecisionTableEditorGraphContent.class);

        when(documentPath.getFileName()).thenReturn("GDT");
        when(versionRecordManager.getCurrentPath()).thenReturn(documentPath);
        when(dtGraphService.loadContent(documentPath)).thenReturn(graphContent);
        when(versionRecordManager.getPathToLatest()).thenReturn(documentPath);
        when(graphContent.getOverview()).thenReturn(overview);
        when(graphContent.getModel()).thenReturn(graphModel);

        presenter.loadDocumentGraph(documentPath);
        verify(view).showLoading();
        verify(view).hideBusyIndicator();
        verify(view).refreshTitle(startsWith("GDT"));

        // initialise when no documents
        verify(editMenuItem).setEnabled(false);
        verify(viewMenuItem).setEnabled(false);
        verify(insertMenuItem).setEnabled(false);
        verify(radarMenuItem).setEnabled(false);

        verify(kieEditorWrapperView).clear();
        verify(kieEditorWrapperView).addMainEditorPage(view);
        verify(kieEditorWrapperView).addOverviewPage(eq(overviewWidget), any(com.google.gwt.user.client.Command.class));
        verify(overviewWidget).setContent(overview, documentPath);
    }

    @Test
    public void checkMayCloseWithCleanDecisionTableGraph() {
        checkMayClose(0,
                      () -> assertTrue(presenter.mayClose()));
    }

    @Test
    public void checkMayCloseWithDirtyDecisionTableGraph() {
        checkMayClose(1,
                      () -> assertFalse(presenter.mayClose()));
    }

    private void checkMayClose(final int uiModelHashCode,
                               final Command assertion) {
        final ObservablePath dtGraphPath = mock(ObservablePath.class);
        final PlaceRequest dtGraphPlaceRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorGraphContent dtGraphContent = makeDecisionTableGraphContent(0);

        when(dtGraphPath.toURI()).thenReturn("dtGraphPath");
        when(dtGraphPath.getFileName()).thenReturn("filename");
        when(dtGraphService.loadContent(eq(dtGraphPath))).thenReturn(dtGraphContent);
        when(versionRecordManager.getCurrentPath()).thenReturn(dtGraphPath);

        doReturn(makeDecisionTableGraphContent(uiModelHashCode).getModel()).when(presenter).buildModelFromEditor();

        presenter.onStartup(dtGraphPath,
                            dtGraphPlaceRequest);

        assertion.execute();
    }

    @Test
    public void checkMayCloseWithCleanDecisionTableGraphEntries() {
        checkMayCloseWithDecisionTableGraphEntries(0,
                                                   () -> assertTrue(presenter.mayClose()));
    }

    @Test
    public void checkMayCloseWithCleanDecisionTableGraphEntriesButDirtyGraphOverview() {
        when(overviewWidget.isDirty()).thenReturn(true);
        checkMayCloseWithDecisionTableGraphEntries(0,
                                                   () -> assertFalse(presenter.mayClose()));
    }

    @Test
    public void checkMayCloseWithDirtyDecisionTableGraphEntries() {
        checkMayCloseWithDecisionTableGraphEntries(1,
                                                   () -> assertFalse(presenter.mayClose()));
    }

    private void checkMayCloseWithDecisionTableGraphEntries(final int uiModelHashCode,
                                                            final Command assertion) {
        final ObservablePath dtGraphPath = mock(ObservablePath.class);
        final PlaceRequest dtGraphPlaceRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorGraphContent dtGraphContent = makeDecisionTableGraphContent();

        final ObservablePath dtPath = mock(ObservablePath.class);
        final PlaceRequest dtPlaceRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent dtContent = makeDecisionTableContent(0);
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(dtPath,
                                                                                dtPath,
                                                                                dtPlaceRequest,
                                                                                dtContent);

        final GuidedDecisionTableGraphEntry dtGraphEntry = new GuidedDecisionTableGraphEntry(dtPath,
                                                                                             dtPath);
        dtGraphContent.getModel().getEntries().add(dtGraphEntry);

        when(dtPath.toURI()).thenReturn("dtPath");
        when(dtGraphPath.toURI()).thenReturn("dtGraphPath");
        when(dtGraphPath.getFileName()).thenReturn("filename");
        when(dtService.loadContent(eq(dtPath))).thenReturn(dtContent);
        when(dtGraphService.loadContent(eq(dtGraphPath))).thenReturn(dtGraphContent);
        when(versionRecordManager.getCurrentPath()).thenReturn(dtGraphPath);

        when(dtPresenter.getOriginalHashCode()).thenReturn(uiModelHashCode);
        doReturn(makeDecisionTableGraphContent(uiModelHashCode).getModel()).when(presenter).buildModelFromEditor();

        when(modeller.addDecisionTable(any(ObservablePath.class),
                                       any(PlaceRequest.class),
                                       any(GuidedDecisionTableEditorContent.class),
                                       any(Boolean.class),
                                       any(Double.class),
                                       any(Double.class))).thenReturn(dtPresenter);
        when(modeller.getAvailableDecisionTables()).thenReturn(new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(dtPresenter);
        }});

        presenter.onStartup(dtGraphPath,
                            dtGraphPlaceRequest);

        assertion.execute();
    }

    @Test
    public void checkBuildModelFromEditor() {
        final ObservablePath dtPath1 = mock(ObservablePath.class);
        final PlaceRequest dtPlaceRequest1 = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent dtContent1 = makeDecisionTableContent(0);
        final GuidedDecisionTableView.Presenter dtPresenter1 = makeDecisionTable(dtPath1,
                                                                                 dtPath1,
                                                                                 dtPlaceRequest1,
                                                                                 dtContent1);

        final ObservablePath dtPath2 = mock(ObservablePath.class);
        final PlaceRequest dtPlaceRequest2 = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent dtContent2 = makeDecisionTableContent(0);
        final GuidedDecisionTableView.Presenter dtPresenter2 = makeDecisionTable(dtPath2,
                                                                                 dtPath2,
                                                                                 dtPlaceRequest2,
                                                                                 dtContent2);

        when(dtPresenter1.getView().getX()).thenReturn(100.0);
        when(dtPresenter1.getView().getY()).thenReturn(110.0);
        when(dtPresenter2.getView().getX()).thenReturn(200.0);
        when(dtPresenter2.getView().getY()).thenReturn(220.0);
        when(modeller.getAvailableDecisionTables()).thenReturn(new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(dtPresenter1);
            add(dtPresenter2);
        }});

        final GuidedDecisionTableEditorGraphModel model = presenter.buildModelFromEditor();
        assertNotNull(model);
        assertNotNull(model.getEntries());
        assertEquals(2,
                     model.getEntries().size());
        assertContains(model.getEntries(),
                       dtPath1,
                       100.0,
                       110.0);
        assertContains(model.getEntries(),
                       dtPath2,
                       200.0,
                       220.0);
    }

    private void assertContains(final Set<GuidedDecisionTableGraphEntry> entries,
                                final ObservablePath path,
                                final Double x,
                                final Double y) {
        if (entries.stream().filter((e) -> e.getPathHead().equals(path) && x.equals(e.getX()) && y.equals(e.getY())).collect(Collectors.toList()).isEmpty()) {
            fail("Path [" + path.toURI() + " not found in GuidedDecisionTableEditorGraphModel.entries()");
        }
    }

    @Test
    public void checkOnClose() {
        presenter.onClose();

        verify(modeller,
               times(1)).onClose();
        verify(lockManager,
               times(1)).releaseLock();
    }

    @Test
    public void checkOnDecisionTableSelectedReadOnly() {
        checkOnDecisionTableSelected((dtGraphPlaceRequest) -> when(dtGraphPlaceRequest.getParameter(eq("readOnly"),
                                                                                                    any())).thenReturn(Boolean.toString(true)),
                                     () -> verify(lockManager,
                                                  never()).acquireLock());
    }

    @Test
    public void checkOnDecisionTableSelectedNotReadOnly() {
        checkOnDecisionTableSelected((dtGraphPlaceRequest) -> {/*Nothing*/},
                                     () -> verify(lockManager,
                                                  times(1)).acquireLock());
    }

    @Test
    public void testOnDecisionTableSelectedWhenPresenterIsNull() {

        final DecisionTableSelectedEvent event = mock(DecisionTableSelectedEvent.class);

        doReturn(Optional.empty()).when(event).getPresenter();
        doNothing().when(presenter).initialiseEditorTabsWhenNoDocuments();

        presenter.onDecisionTableSelected(event);

        verify(presenter).initialiseEditorTabsWhenNoDocuments();
    }

    @Test
    public void testOnDecisionTableSelectedWhenPresenterIsNotNull() {

        final GuidedDecisionTableView.Presenter dtPresenter = mock(GuidedDecisionTableView.Presenter.class);
        final Overview overview = mock(Overview.class);
        final AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final GuidedDecisionTablePresenter.Access access = new GuidedDecisionTablePresenter.Access();
        final DecisionTableSelectedEvent event = new DecisionTableSelectedEvent(dtPresenter);
        final ObservablePath path = mock(ObservablePath.class);

        doNothing().when(presenter).initialiseKieEditorTabs(dtPresenter, overview, oracle, model.getImports(), false);
        doNothing().when(presenter).initialiseEditorTabsWhenNoDocuments();
        doReturn(true).when(modeller).isDecisionTableAvailable(dtPresenter);
        doReturn(Optional.of(dtPresenter)).when(modeller).getActiveDecisionTable();
        doReturn(overview).when(dtPresenter).getOverview();
        doReturn(oracle).when(dtPresenter).getDataModelOracle();
        doReturn(model).when(dtPresenter).getModel();
        doReturn(access).when(dtPresenter).getAccess();
        doReturn(path).when(dtPresenter).getLatestPath();

        presenter.registerDocument(dtPresenter);
        presenter.onDecisionTableSelected(event);

        verify(presenter, never()).initialiseEditorTabsWhenNoDocuments();
        verify(presenter).addColumnsTab();
        verify(presenter).enableColumnsTab(dtPresenter);
    }

    private void checkOnDecisionTableSelected(final ParameterizedCommand<PlaceRequest> setup,
                                              final Command assertion) {
        final ObservablePath dtGraphPath = mock(ObservablePath.class);
        final PlaceRequest dtGraphPlaceRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorGraphContent dtGraphContent = makeDecisionTableGraphContent();

        final ObservablePath dtPath = mock(ObservablePath.class);
        final PlaceRequest dtPlaceRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent dtContent = makeDecisionTableContent(0);
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(dtPath,
                                                                                dtPath,
                                                                                dtPlaceRequest,
                                                                                dtContent);

        final GuidedDecisionTableGraphEntry dtGraphEntry = new GuidedDecisionTableGraphEntry(dtPath,
                                                                                             dtPath);
        dtGraphContent.getModel().getEntries().add(dtGraphEntry);

        when(dtPath.toURI()).thenReturn("dtPath");
        when(dtGraphPath.toURI()).thenReturn("dtGraphPath");
        when(dtGraphPath.getFileName()).thenReturn("filename");
        when(dtService.loadContent(eq(dtPath))).thenReturn(dtContent);
        when(dtGraphService.loadContent(eq(dtGraphPath))).thenReturn(dtGraphContent);
        when(versionRecordManager.getCurrentPath()).thenReturn(dtGraphPath);

        when(modeller.addDecisionTable(any(ObservablePath.class),
                                       any(PlaceRequest.class),
                                       any(GuidedDecisionTableEditorContent.class),
                                       any(Boolean.class),
                                       any(Double.class),
                                       any(Double.class))).thenReturn(dtPresenter);
        when(modeller.getAvailableDecisionTables()).thenReturn(new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(dtPresenter);
        }});

        setup.execute(dtGraphPlaceRequest);

        presenter.onStartup(dtGraphPath,
                            dtGraphPlaceRequest);

        final DecisionTableSelectedEvent event = new DecisionTableSelectedEvent(dtPresenter);

        presenter.onDecisionTableSelected(event);

        assertion.execute();
    }

    @Test
    public void checkEnableMenus() {
        presenter.enableMenus(true);

        checkMenuItems(true);
    }

    @Test
    public void checkDisableMenus() {
        presenter.enableMenus(false);

        checkMenuItems(false);
    }

    private void checkMenuItems(final boolean enabled) {
        verify(saveMenuItem,
               times(1)).setEnabled(eq(enabled));
        verify(versionManagerMenuItem,
               times(1)).setEnabled(eq(enabled));
        verify(editMenuItem,
               times(1)).setEnabled(eq(enabled));
        verify(viewMenuItem,
               times(1)).setEnabled(eq(enabled));
        verify(insertMenuItem,
               times(1)).setEnabled(eq(enabled));
        verify(radarMenuItem,
               times(1)).setEnabled(eq(enabled));
        verify(registeredDocumentsMenuItem,
               times(1)).setEnabled(eq(enabled));
    }

    @Test
    public void checkGetAvailableDocumentPaths() {
        when(dtGraphService.listDecisionTablesInPackage(eq(presenter.editorPath))).thenReturn(new ArrayList<Path>() {{
            add(PathFactory.newPath("file1",
                                    "file1Url"));
        }});

        presenter.getAvailableDocumentPaths((List<Path> result) -> {
            assertNotNull(result);
            assertEquals(1,
                         result.size());
            assertEquals("file1",
                         result.get(0).getFileName());
            assertEquals("file1Url",
                         result.get(0).toURI());
        });

        verify(view,
               times(1)).showLoading();
        verify(dtGraphService,
               times(1)).listDecisionTablesInPackage(eq(presenter.editorPath));
        verify(view,
               times(1)).hideBusyIndicator();
    }

    @Test
    public void checkOnOpenDocumentsInEditor() {
        final Path dtPath1 = PathFactory.newPath("file1",
                                                 "file1Url");
        final Path dtPath2 = PathFactory.newPath("file2",
                                                 "file2Url");
        final List<Path> dtPaths = new ArrayList<Path>() {{
            add(dtPath1);
            add(dtPath2);
        }};

        final ObservablePath dtPath = mock(ObservablePath.class);
        final PlaceRequest dtPlaceRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent dtContent = makeDecisionTableContent(0);
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(dtPath,
                                                                                dtPath,
                                                                                dtPlaceRequest,
                                                                                dtContent);

        when(modeller.addDecisionTable(any(ObservablePath.class),
                                       any(PlaceRequest.class),
                                       any(GuidedDecisionTableEditorContent.class),
                                       any(Boolean.class),
                                       any(Double.class),
                                       any(Double.class))).thenReturn(dtPresenter);

        presenter.onOpenDocumentsInEditor(dtPaths);

        verify(dtService,
               times(2)).loadContent(dtPathCaptor.capture());

        final List<Path> dtLoadedPaths = dtPathCaptor.getAllValues();
        assertNotNull(dtLoadedPaths);
        assertEquals(2,
                     dtLoadedPaths.size());
        assertContains(dtLoadedPaths,
                       dtPath1);
        assertContains(dtLoadedPaths,
                       dtPath2);
    }

    private void assertContains(final List<Path> paths,
                                final Path path) {
        if (paths.stream().filter((p) -> p.toURI().equals(path.toURI())).collect(Collectors.toList()).isEmpty()) {
            fail("Document for path [" + path.toURI() + "] not loaded by GuidedDecisionTableGraphEditorPresenter.loadDocument().");
        }
    }

    @Test
    public void checkDoSaveWhenReadOnlyWithLatestPath() {
        when(versionRecordManager.isCurrentLatest()).thenReturn(true);
        checkDoSave((setup) -> {
                        when(setup.getDecisionTableGraphPlaceRequest().getParameter(eq("readOnly"),
                                                                                    any())).thenReturn(Boolean.toString(true));
                        presenter.onStartup(setup.getDecisionTableGraphPath(),
                                            setup.getDecisionTableGraphPlaceRequest());
                    },
                    () -> verify(view,
                                 times(1)).alertReadOnly());
    }

    @Test
    public void checkDoSaveWhenReadOnlyWithHistoricPath() {
        checkDoSave((setup) -> {
                        when(setup.getDecisionTableGraphPlaceRequest().getParameter(eq("readOnly"),
                                                                                    any())).thenReturn(Boolean.toString(true));
                        presenter.onStartup(setup.getDecisionTableGraphPath(),
                                            setup.getDecisionTableGraphPlaceRequest());
                    },
                    () -> verify(versionRecordManager,
                                 times(1)).restoreToCurrentVersion());
    }

    @Test
    public void checkDoSaveWithConcurrentModificationOfGraph() {
        doNothing().when(presenter).showConcurrentUpdatesPopup();

        checkDoSave((dtGraphPlaceRequest) -> presenter.concurrentUpdateSessionInfo = mock(OnConcurrentUpdateEvent.class),
                    () -> verify(presenter,
                                 times(1)).showConcurrentUpdatesPopup());
    }

    private void checkDoSave(final ParameterizedCommand<OnSaveSetupDataHolder> setup,
                             final Command assertion) {
        final ObservablePath dtGraphPath = mock(ObservablePath.class);
        final PlaceRequest dtGraphPlaceRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorGraphContent dtGraphContent = makeDecisionTableGraphContent();

        when(dtGraphPath.toURI()).thenReturn("dtGraphPath");
        when(dtGraphPath.getFileName()).thenReturn("filename");
        when(dtGraphService.loadContent(eq(dtGraphPath))).thenReturn(dtGraphContent);
        when(versionRecordManager.getCurrentPath()).thenReturn(dtGraphPath);

        setup.execute(new OnSaveSetupDataHolder(dtGraphPath,
                                                dtGraphPlaceRequest));

        presenter.doSave();

        assertion.execute();
    }

    @Test
    public void checkDoSaveWithConcurrentModificationOfGraphEntry() {
        doNothing().when(presenter).showConcurrentUpdatesPopup();

        checkDoSaveWithGraphEntry((setup) -> {
                                      when(setup.getDecisionTablePresenter().getConcurrentUpdateSessionInfo()).thenReturn(mock(OnConcurrentUpdateEvent.class));
                                      presenter.onStartup(setup.getDecisionTableGraphPath(),
                                                          setup.getDecisionTableGraphPlaceRequest());
                                  },
                                  () -> verify(presenter,
                                               times(1)).showConcurrentUpdatesPopup());
    }

    @Test
    public void checkDoSaveWithGraphEntry() {
        doNothing().when(presenter).saveDocumentGraphEntries();

        checkDoSaveWithGraphEntry((setup) -> presenter.onStartup(setup.getDecisionTableGraphPath(),
                                                                 setup.getDecisionTableGraphPlaceRequest()),
                                  () -> verify(presenter,
                                               times(1)).saveDocumentGraphEntries());
    }

    private void checkDoSaveWithGraphEntry(final ParameterizedCommand<OnSaveSetupDataHolder> setup,
                                           final Command assertion) {
        final ObservablePath dtGraphPath = mock(ObservablePath.class);
        final PathPlaceRequest dtGraphPlaceRequest = mock(PathPlaceRequest.class);
        final GuidedDecisionTableEditorGraphContent dtGraphContent = makeDecisionTableGraphContent();

        final ObservablePath dtPath = mock(ObservablePath.class);
        final PlaceRequest dtPlaceRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent dtContent = makeDecisionTableContent(0);
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(dtPath,
                                                                                dtPath,
                                                                                dtPlaceRequest,
                                                                                dtContent);

        final GuidedDecisionTableGraphEntry dtGraphEntry = new GuidedDecisionTableGraphEntry(dtPath,
                                                                                             dtPath);
        dtGraphContent.getModel().getEntries().add(dtGraphEntry);

        when(dtPath.toURI()).thenReturn("dtPath");
        when(dtGraphPath.toURI()).thenReturn("dtGraphPath");
        when(dtGraphPath.getFileName()).thenReturn("filename");
        when(dtService.loadContent(eq(dtPath))).thenReturn(dtContent);
        when(dtGraphService.loadContent(eq(dtGraphPath))).thenReturn(dtGraphContent);
        when(versionRecordManager.getCurrentPath()).thenReturn(dtGraphPath);

        when(modeller.addDecisionTable(any(ObservablePath.class),
                                       any(PlaceRequest.class),
                                       any(GuidedDecisionTableEditorContent.class),
                                       any(Boolean.class),
                                       any(Double.class),
                                       any(Double.class))).thenReturn(dtPresenter);
        when(modeller.getAvailableDecisionTables()).thenReturn(new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(dtPresenter);
        }});

        setup.execute(new OnSaveSetupDataHolder(dtGraphPath,
                                                dtGraphPlaceRequest,
                                                dtPresenter));

        presenter.doSave();

        assertion.execute();
    }

    @Test
    public void checkSaveDocumentGraphEntries() {
        final ObservablePath dtGraphPath = mock(ObservablePath.class);
        final PathPlaceRequest dtGraphPlaceRequest = mock(PathPlaceRequest.class);
        final GuidedDecisionTableEditorGraphContent dtGraphContent = makeDecisionTableGraphContent();

        final ObservablePath dtPath1 = mock(ObservablePath.class);
        final PlaceRequest dtPlaceRequest1 = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent dtContent1 = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter1 = makeDecisionTable(dtPath1,
                                                                                 dtPath1,
                                                                                 dtPlaceRequest1,
                                                                                 dtContent1);
        final ObservablePath dtPath2 = mock(ObservablePath.class);
        final PlaceRequest dtPlaceRequest2 = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent dtContent2 = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter2 = makeDecisionTable(dtPath2,
                                                                                 dtPath2,
                                                                                 dtPlaceRequest2,
                                                                                 dtContent2);

        when(dtPath1.toURI()).thenReturn("dtPath1");
        when(dtPath2.toURI()).thenReturn("dtPath2");
        when(dtGraphPath.toURI()).thenReturn("dtGraphPath");
        when(dtGraphPath.getFileName()).thenReturn("filename");
        when(dtService.loadContent(eq(dtPath1))).thenReturn(dtContent1);
        when(dtService.loadContent(eq(dtPath2))).thenReturn(dtContent2);
        when(dtGraphService.loadContent(eq(dtGraphPath))).thenReturn(dtGraphContent);
        when(versionRecordManager.getCurrentPath()).thenReturn(dtGraphPath);

        when(modeller.addDecisionTable(any(ObservablePath.class),
                                       any(PlaceRequest.class),
                                       eq(dtContent1),
                                       any(Boolean.class),
                                       any(Double.class),
                                       any(Double.class))).thenReturn(dtPresenter1);
        when(modeller.addDecisionTable(any(ObservablePath.class),
                                       any(PlaceRequest.class),
                                       eq(dtContent2),
                                       any(Boolean.class),
                                       any(Double.class),
                                       any(Double.class))).thenReturn(dtPresenter2);

        when(modeller.getAvailableDecisionTables()).thenReturn(new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(dtPresenter1);
            add(dtPresenter2);
        }});

        presenter.onStartup(dtGraphPath,
                            dtGraphPlaceRequest);

        presenter.saveDocumentGraphEntries();

        verify(savePopUpPresenter,
               times(1)).show(any(Path.class),
                              commitMessageCommandCaptor.capture());

        final ParameterizedCommand<String> commitMessageCommand = commitMessageCommandCaptor.getValue();
        assertNotNull(commitMessageCommand);
        commitMessageCommand.execute("message");

        verify(view,
               times(1)).showSaving();
        verify(saveInProgressEvent,
               times(2)).fire(any(SaveInProgressEvent.class));
        verify(dtGraphService,
               times(1)).save(eq(dtGraphPath),
                              any(GuidedDecisionTableEditorGraphModel.class),
                              any(Metadata.class),
                              eq("message"));
        verify(dtService,
               times(1)).save(eq(dtPath2),
                              any(GuidedDecisionTable52.class),
                              any(Metadata.class),
                              eq("message"));
        verify(dtService,
               times(1)).save(eq(dtPath2),
                              any(GuidedDecisionTable52.class),
                              any(Metadata.class),
                              eq("message"));
        verify(notificationEvent,
               times(1)).fire(any(NotificationEvent.class));
        verify(dtPresenter1,
               times(1)).setConcurrentUpdateSessionInfo(eq(null));
        verify(dtPresenter2,
               times(1)).setConcurrentUpdateSessionInfo(eq(null));
        assertNull(presenter.concurrentUpdateSessionInfo);
    }

    @Test
    public void checkSaveDocumentGraphEntriesEmptyGraph() {
        final ObservablePath dtGraphPath = mock(ObservablePath.class);
        final PathPlaceRequest dtGraphPlaceRequest = mock(PathPlaceRequest.class);
        final GuidedDecisionTableEditorGraphContent dtGraphContent = makeDecisionTableGraphContent();

        when(dtGraphPath.toURI()).thenReturn("dtGraphPath");
        when(dtGraphPath.getFileName()).thenReturn("filename");
        when(dtGraphService.loadContent(eq(dtGraphPath))).thenReturn(dtGraphContent);
        when(versionRecordManager.getCurrentPath()).thenReturn(dtGraphPath);

        when(modeller.getAvailableDecisionTables()).thenReturn(new HashSet<>());

        presenter.onStartup(dtGraphPath,
                            dtGraphPlaceRequest);

        presenter.saveDocumentGraphEntries();

        verify(savePopUpPresenter,
               times(1)).show(any(Path.class),
                              commitMessageCommandCaptor.capture());

        final ParameterizedCommand<String> commitMessageCommand = commitMessageCommandCaptor.getValue();
        assertNotNull(commitMessageCommand);
        commitMessageCommand.execute("message");

        verify(view,
               times(1)).showSaving();
        verify(saveInProgressEvent,
               never()).fire(any(SaveInProgressEvent.class));
        verify(dtGraphService,
               times(1)).save(eq(dtGraphPath),
                              any(GuidedDecisionTableEditorGraphModel.class),
                              any(Metadata.class),
                              eq("message"));

        final ArgumentCaptor<NotificationEvent> notification = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notificationEvent,
               times(1)).fire(notification.capture());
        assertEquals(CommonConstants.INSTANCE.ItemSavedSuccessfully(),
                     notification.getValue().getNotification());

        assertNull(presenter.concurrentUpdateSessionInfo);
    }

    @Test
    public void checkInitialiseVersionManager() {
        doNothing().when(presenter).reload();
        when(versionRecordManager.isLatest(any(VersionRecord.class))).thenReturn(true);

        presenter.initialiseVersionManager();

        verify(versionRecordManager,
               times(1)).init(eq(null),
                              eq(presenter.editorPath),
                              versionRecordCallbackCaptor.capture());

        final Callback<VersionRecord> versionRecordCallback = versionRecordCallbackCaptor.getValue();
        assertNotNull(versionRecordCallback);
        versionRecordCallback.callback(new PortableVersionRecord("id",
                                                                 "author",
                                                                 "email",
                                                                 "comment",
                                                                 mock(Date.class),
                                                                 "uri"));

        verify(versionRecordManager,
               times(1)).setVersion(eq("id"));
        verify(registeredDocumentsMenuBuilder,
               times(1)).setReadOnly(eq(false));
        verify(presenter,
               times(1)).reload();
        assertFalse(presenter.access.isReadOnly());
    }

    @Test
    public void checkInitialiseKieEditorTabs() {
        final ObservablePath dtGraphPath = mock(ObservablePath.class);
        final PlaceRequest dtGraphPlaceRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorGraphContent dtGraphContent = makeDecisionTableGraphContent(0);

        when(dtGraphPath.toURI()).thenReturn("dtGraphPath");
        when(dtGraphPath.getFileName()).thenReturn("filename");
        when(dtGraphService.loadContent(eq(dtGraphPath))).thenReturn(dtGraphContent);
        when(versionRecordManager.getCurrentPath()).thenReturn(dtGraphPath);
        when(versionRecordManager.getVersion()).thenReturn("version");

        presenter.onStartup(dtGraphPath,
                            dtGraphPlaceRequest);

        verify(kieEditorWrapperView,
               times(1)).clear();

        final GuidedDecisionTableView.Presenter document = mock(GuidedDecisionTableView.Presenter.class);
        final AsyncPackageDataModelOracle dmo = mock(AsyncPackageDataModelOracle.class);
        final Imports imports = mock(Imports.class);
        final boolean isReadOnly = true;

        final ArgumentCaptor<com.google.gwt.user.client.Command> onFocusCommandCaptor = ArgumentCaptor.forClass(com.google.gwt.user.client.Command.class);

        presenter.initialiseKieEditorTabs(document,
                                          dtGraphContent.getOverview(),
                                          dmo,
                                          imports,
                                          isReadOnly);

        verify(kieEditorWrapperView,
               times(2)).clear();
        verify(kieEditorWrapperView,
               times(2)).addMainEditorPage(view);
        verify(kieEditorWrapperView,
               times(2)).addOverviewPage(eq(overviewWidget),
                                         onFocusCommandCaptor.capture());
        verify(overviewWidget,
               times(2)).setContent(eq(dtGraphContent.getOverview()),
                                    any(ObservablePath.class));

        verify(kieEditorWrapperView,
               times(1)).addSourcePage(any(ViewDRLSourceWidget.class));
        verify(kieEditorWrapperView,
               times(1)).addImportsTab(eq(importsWidget));
        verify(importsWidget,
               times(1)).setContent(eq(dmo),
                                    eq(imports),
                                    eq(isReadOnly));

        final com.google.gwt.user.client.Command onFocusCommand = onFocusCommandCaptor.getValue();
        assertNotNull(onFocusCommand);
        onFocusCommand.execute();

        verify(overviewWidget,
               times(1)).refresh(eq("version"));
    }

    @Test
    public void checkOnDelete() {
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        presenter.editorPlaceRequest = placeRequest;

        presenter.onDelete();

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
    public void checkOnRename() {
        doNothing().when(presenter).reload();
        when(versionRecordManager.getCurrentPath()).thenReturn(mock(ObservablePath.class));

        presenter.onRename();

        verify(presenter,
               times(1)).reload();
        verify(changeTitleEvent,
               times(1)).fire(any(ChangeTitleWidgetEvent.class));
    }

    @Test
    public void checkReload() {
        final ObservablePath dtGraphPath = mock(ObservablePath.class);
        final PathPlaceRequest dtGraphPlaceRequest = mock(PathPlaceRequest.class);
        final GuidedDecisionTableEditorGraphContent dtGraphContent = makeDecisionTableGraphContent();

        final ObservablePath dtPath = mock(ObservablePath.class);
        final PlaceRequest dtPlaceRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent dtContent = makeDecisionTableContent(0);
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(dtPath,
                                                                                dtPath,
                                                                                dtPlaceRequest,
                                                                                dtContent);

        final GuidedDecisionTableGraphEntry dtGraphEntry = new GuidedDecisionTableGraphEntry(dtPath,
                                                                                             dtPath);
        dtGraphContent.getModel().getEntries().add(dtGraphEntry);

        when(dtPath.toURI()).thenReturn("dtPath");
        when(dtGraphPath.toURI()).thenReturn("dtGraphPath");
        when(dtGraphPath.getFileName()).thenReturn("filename");
        when(dtService.loadContent(eq(dtPath))).thenReturn(dtContent);
        when(dtGraphService.loadContent(eq(dtGraphPath))).thenReturn(dtGraphContent);
        when(versionRecordManager.getCurrentPath()).thenReturn(dtGraphPath);

        when(modeller.addDecisionTable(any(ObservablePath.class),
                                       any(PlaceRequest.class),
                                       any(GuidedDecisionTableEditorContent.class),
                                       any(Boolean.class),
                                       any(Double.class),
                                       any(Double.class))).thenReturn(dtPresenter);
        when(modeller.getAvailableDecisionTables()).thenReturn(new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(dtPresenter);
        }});

        presenter.onStartup(dtGraphPath,
                            dtGraphPlaceRequest);

        verify(presenter,
               times(1)).loadDocumentGraph(dtGraphPath);

        presenter.reload();

        verify(presenter,
               times(1)).deregisterDocument(eq(dtPresenter));
        verify(presenter,
               times(2)).loadDocumentGraph(dtGraphPath);
        verify(modeller,
               times(1)).releaseDecisionTables();
        verify(modellerView,
               times(1)).clear();
    }

    @Test
    public void checkOnRestore() {
        final ObservablePath dtGraphPath = mock(ObservablePath.class);
        final PathPlaceRequest dtGraphPlaceRequest = mock(PathPlaceRequest.class);
        final GuidedDecisionTableEditorGraphContent dtGraphContent = makeDecisionTableGraphContent();
        final RestoreEvent event = new RestoreEvent(dtGraphPath);

        when(dtGraphPath.toURI()).thenReturn("dtGraphPath");
        when(dtGraphPath.getFileName()).thenReturn("filename");
        when(dtGraphService.loadContent(eq(dtGraphPath))).thenReturn(dtGraphContent);
        when(versionRecordManager.getCurrentPath()).thenReturn(dtGraphPath);
        when(versionRecordManager.getPathToLatest()).thenReturn(dtGraphPath);

        presenter.onStartup(dtGraphPath,
                            dtGraphPlaceRequest);

        verify(presenter,
               times(1)).initialiseEditor(eq(dtGraphPath),
                                          eq(dtGraphPlaceRequest));

        presenter.onRestore(event);

        verify(presenter,
               times(2)).initialiseEditor(eq(dtGraphPath),
                                          eq(dtGraphPlaceRequest));
        verify(notification,
               times(1)).fire(any(NotificationEvent.class));
    }

    @Test
    public void checkOnUpdatedLockStatusEventWithNullPath() {
        when(modeller.getActiveDecisionTable()).thenReturn(Optional.empty());

        checkOnUpdatedLockStatusEvent(null,
                                      false,
                                      false,
                                      () -> assertEquals(LockedBy.NOBODY,
                                                         presenter.access.getLock()));
    }

    @Test
    public void checkOnUpdatedLockStatusEventNotLocked() {
        when(modeller.getActiveDecisionTable()).thenReturn(Optional.empty());

        checkOnUpdatedLockStatusEvent(mock(ObservablePath.class),
                                      false,
                                      false,
                                      () -> assertEquals(LockedBy.NOBODY,
                                                         presenter.access.getLock()));
    }

    @Test
    public void checkOnUpdatedLockStatusEventLockedByOtherUser() {
        when(modeller.getActiveDecisionTable()).thenReturn(Optional.empty());

        checkOnUpdatedLockStatusEvent(mock(ObservablePath.class),
                                      true,
                                      false,
                                      () -> assertEquals(LockedBy.OTHER_USER,
                                                         presenter.access.getLock()));
    }

    @Test
    public void checkOnUpdatedLockStatusEventLockedByCurrentUser() {
        when(modeller.getActiveDecisionTable()).thenReturn(Optional.empty());

        checkOnUpdatedLockStatusEvent(mock(ObservablePath.class),
                                      true,
                                      true,
                                      () -> assertEquals(LockedBy.CURRENT_USER,
                                                         presenter.access.getLock()));
    }

    @Test
    public void testGetSaveAndRenameCommand() {

        final Command expectedCommand = mock(Command.class);
        final GuidedDecisionTableGraphEditorPresenter presenter = makePresenter();

        doReturn(saveAndRenameCommandBuilder).when(saveAndRenameCommandBuilder).addPathSupplier(any());
        doReturn(saveAndRenameCommandBuilder).when(saveAndRenameCommandBuilder).addValidator(any(Validator.class));
        doReturn(saveAndRenameCommandBuilder).when(saveAndRenameCommandBuilder).addValidator(any(Supplier.class));
        doReturn(saveAndRenameCommandBuilder).when(saveAndRenameCommandBuilder).addRenameService(any());
        doReturn(saveAndRenameCommandBuilder).when(saveAndRenameCommandBuilder).addMetadataSupplier(any());
        doReturn(saveAndRenameCommandBuilder).when(saveAndRenameCommandBuilder).addContentSupplier(any());
        doReturn(saveAndRenameCommandBuilder).when(saveAndRenameCommandBuilder).addIsDirtySupplier(any());
        doReturn(saveAndRenameCommandBuilder).when(saveAndRenameCommandBuilder).addSuccessCallback(any());
        doReturn(expectedCommand).when(saveAndRenameCommandBuilder).build();

        final Command actualCommand = presenter.getSaveAndRenameCommand();

        assertEquals(expectedCommand, actualCommand);
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
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);
        final Overview overview = mock(Overview.class);
        final ObservablePath currentPath = mock(ObservablePath.class);
        final ObservablePath latestPath = mock(ObservablePath.class);

        doReturn(model).when(presenter).getModel();
        doReturn(overview).when(presenter).getOverview();
        doReturn(currentPath).when(presenter).getCurrentPath();
        doReturn(latestPath).when(presenter).getLatestPath();
        doReturn(asSet(presenter)).when(this.presenter).getAvailableDecisionTables();

        final List<GuidedDecisionTableEditorContent> content = this.presenter.getContentSupplier().get();
        final GuidedDecisionTableEditorContent firstContent = content.get(0);

        assertEquals(1, content.size());

        assertEquals(model, firstContent.getModel());
        assertEquals(overview, firstContent.getOverview());
        assertEquals(currentPath, firstContent.getCurrentPath());
        assertEquals(latestPath, firstContent.getLatestPath());
    }

    @Test
    public void testIsGuidedDecisionTablesDirtyWhenItIsDirty() {

        final GuidedDecisionTableView.Presenter presenter = mock(GuidedDecisionTableView.Presenter.class);
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);
        final Set<GuidedDecisionTableView.Presenter> availableDecisionTables = asSet(presenter);
        final int currentHash = 456;
        final int originalHash = 123;

        doReturn(currentHash).when(this.presenter).currentHashCode(presenter);
        doReturn(originalHash).when(this.presenter).originalHashCode(presenter);
        doReturn(model).when(presenter).getModel();
        doReturn(availableDecisionTables).when(this.presenter).getAvailableDecisionTables();

        final boolean isDirty = this.presenter.isGuidedDecisionTablesDirty();

        assertTrue(isDirty);
    }

    @Test
    public void testIsGuidedDecisionTablesDirtyWhenItIsNotDirty() {

        final GuidedDecisionTableView.Presenter presenter = mock(GuidedDecisionTableView.Presenter.class);
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);
        final Set<GuidedDecisionTableView.Presenter> availableDecisionTables = asSet(presenter);
        final int currentHash = 123;
        final int originalHash = 123;

        doReturn(currentHash).when(this.presenter).currentHashCode(presenter);
        doReturn(originalHash).when(this.presenter).originalHashCode(presenter);
        doReturn(model).when(presenter).getModel();
        doReturn(availableDecisionTables).when(this.presenter).getAvailableDecisionTables();

        final boolean isDirty = this.presenter.isGuidedDecisionTablesDirty();

        assertFalse(isDirty);
    }

    @Test
    public void testIsGraphDirtyWhenItIsDirty() {

        presenter.originalGraphHash = 123;
        doReturn(456).when(presenter).getCurrentHashCode();

        final boolean isDirty = presenter.isGraphDirty();

        assertTrue(isDirty);
    }

    @Test
    public void testIsGraphDirtyWhenItIsNotDirty() {

        presenter.originalGraphHash = 123;
        doReturn(123).when(presenter).getCurrentHashCode();

        final boolean isDirty = presenter.isGraphDirty();

        assertFalse(isDirty);
    }

    @Test
    public void testIsOverviewWidgetDirtyWhenItIsDirty() {

        final OverviewWidgetPresenter overviewWidget = mock(OverviewWidgetPresenter.class);

        doReturn(true).when(overviewWidget).isDirty();
        doReturn(overviewWidget).when(presenter).getOverviewWidget();

        final boolean isDirty = presenter.isOverviewWidgetDirty();

        assertTrue(isDirty);
    }

    @Test
    public void testIsOverviewWidgetDirtyWhenItIsNotDirty() {

        final OverviewWidgetPresenter overviewWidget = mock(OverviewWidgetPresenter.class);

        doReturn(false).when(overviewWidget).isDirty();
        doReturn(overviewWidget).when(presenter).getOverviewWidget();

        final boolean isDirty = presenter.isOverviewWidgetDirty();

        assertFalse(isDirty);
    }

    @Test
    public void testGetIsDirtySupplierWhenGuidedDecisionTablesIsDirty() {

        doReturn(true).when(presenter).isGuidedDecisionTablesDirty();
        doReturn(false).when(presenter).isGraphDirty();
        doReturn(false).when(presenter).isOverviewWidgetDirty();

        final Supplier<Boolean> isDirtySupplier = presenter.getIsDirtySupplier();

        assertTrue(isDirtySupplier.get());
    }

    @Test
    public void testGetIsDirtySupplierWhenGraphIsDirty() {

        doReturn(false).when(presenter).isGuidedDecisionTablesDirty();
        doReturn(true).when(presenter).isGraphDirty();
        doReturn(false).when(presenter).isOverviewWidgetDirty();

        final Supplier<Boolean> isDirtySupplier = presenter.getIsDirtySupplier();

        assertTrue(isDirtySupplier.get());
    }

    @Test
    public void testGetIsDirtySupplierWhenOverviewWidgetIsDirty() {

        doReturn(false).when(presenter).isGuidedDecisionTablesDirty();
        doReturn(false).when(presenter).isGraphDirty();
        doReturn(true).when(presenter).isOverviewWidgetDirty();

        final Supplier<Boolean> isDirtySupplier = presenter.getIsDirtySupplier();

        assertTrue(isDirtySupplier.get());
    }

    @Test
    public void testGetIsDirtySupplierWhenItIsNotDirty() {

        doReturn(false).when(presenter).isGuidedDecisionTablesDirty();
        doReturn(false).when(presenter).isGraphDirty();
        doReturn(false).when(presenter).isOverviewWidgetDirty();

        final Supplier<Boolean> isDirtySupplier = presenter.getIsDirtySupplier();

        assertFalse(isDirtySupplier.get());
    }

    private HashSet<GuidedDecisionTableView.Presenter> asSet(final GuidedDecisionTableView.Presenter... presenter) {
        return new HashSet<GuidedDecisionTableView.Presenter>() {{
            this.addAll(asList(presenter));
        }};
    }

    private void checkOnUpdatedLockStatusEvent(final ObservablePath path,
                                               final boolean locked,
                                               final boolean lockedByCurrentUser,
                                               final Command assertion) {
        presenter.editorPath = path;
        presenter.access.setLock(LockedBy.NOBODY);

        final UpdatedLockStatusEvent event = new UpdatedLockStatusEvent(path,
                                                                        locked,
                                                                        lockedByCurrentUser);

        presenter.onUpdatedLockStatusEvent(event);

        assertion.execute();
    }

    protected GuidedDecisionTableEditorGraphContent makeDecisionTableGraphContent() {
        return makeDecisionTableGraphContent(0);
    }

    protected GuidedDecisionTableEditorGraphContent makeDecisionTableGraphContent(final int hashCode) {
        final GuidedDecisionTableEditorGraphModel model = new GuidedDecisionTableEditorGraphModel() {
            @Override
            public int hashCode() {
                return hashCode;
            }

            @Override
            public boolean equals(Object o) {
                return super.equals(o);
            }
        };
        return new GuidedDecisionTableEditorGraphContent(model,
                                                         mock(Overview.class));
    }

    private GuidedDecisionTableGraphEditorPresenter makePresenter() {
        return new GuidedDecisionTableGraphEditorPresenter(view,
                                                           dtServiceCaller,
                                                           docks,
                                                           mock(PerspectiveManager.class),
                                                           dtGraphServiceCaller,
                                                           moduleServiceCaller,
                                                           graphSaveAndRenameServiceCaller,
                                                           notification,
                                                           saveInProgressEvent,
                                                           decisionTableSelectedEvent,
                                                           validationPopup,
                                                           dtGraphResourceType,
                                                           editMenuBuilder,
                                                           viewMenuBuilder,
                                                           insertMenuBuilder,
                                                           radarMenuBuilder,
                                                           modeller,
                                                           helper,
                                                           beanManager,
                                                           placeManager,
                                                           lockManager,
                                                           columnsPage,
                                                           saveAndRenameCommandBuilder,
                                                           alertsButtonMenuItemBuilder,
                                                           downloadMenuItemBuilder,
                                                           editorSearchIndex,
                                                           searchBarComponent,
                                                           searchableElementFactory,
                                                           searchPerformed);
    }

    private static class OnSaveSetupDataHolder {

        private ObservablePath dtGraphPath;

        private PlaceRequest dtGraphPlaceRequest;
        private GuidedDecisionTableView.Presenter dtPresenter;

        public OnSaveSetupDataHolder(final ObservablePath dtGraphPath,
                                     final PlaceRequest dtGraphPlaceRequest) {
            this(dtGraphPath,
                 dtGraphPlaceRequest,
                 null);
        }

        public OnSaveSetupDataHolder(final ObservablePath dtGraphPath,
                                     final PlaceRequest dtGraphPlaceRequest,
                                     final GuidedDecisionTableView.Presenter dtPresenter) {
            this.dtGraphPath = dtGraphPath;
            this.dtGraphPlaceRequest = dtGraphPlaceRequest;
            this.dtPresenter = dtPresenter;
        }

        public ObservablePath getDecisionTableGraphPath() {
            return dtGraphPath;
        }

        public PlaceRequest getDecisionTableGraphPlaceRequest() {
            return dtGraphPlaceRequest;
        }

        public GuidedDecisionTableView.Presenter getDecisionTablePresenter() {
            return dtPresenter;
        }
    }
}
