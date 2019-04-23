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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.guvnor.common.services.project.categories.Decision;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.client.workbench.widgets.multipage.Page;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

import static org.drools.workbench.screens.guided.dtable.client.editor.BaseGuidedDecisionTableEditorPresenter.COLUMNS_TAB_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BaseGuidedDecisionTableEditorPresenterTest extends BaseGuidedDecisionTablePresenterTest<BaseGuidedDecisionTableEditorPresenter> {

    @Captor
    ArgumentCaptor<PlaceRequest> placeRequestArgumentCaptor;
    private GuidedDTableResourceType resourceType = new GuidedDTableResourceType(new Decision());
    @Mock
    private SaveAndRenameCommandBuilder<GuidedDecisionTable52, Metadata> saveAndRenameCommandBuilder;

    @Override
    protected GuidedDecisionTableEditorPresenter getPresenter() {
        return new GuidedDecisionTableEditorPresenter(view,
                                                      dtServiceCaller,
                                                      docks,
                                                      perspectiveManager,
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
                promises = BaseGuidedDecisionTableEditorPresenterTest.this.promises;
                projectController = BaseGuidedDecisionTableEditorPresenterTest.this.projectController;
            }

            @Override
            protected Command getSaveAndRenameCommand() {
                return mock(Command.class);
            }
        };
    }

    @Test
    public void checkInit() {
        verify(viewMenuBuilder,
               times(1)).setModeller(eq(modeller));
        verify(insertMenuBuilder,
               times(1)).setModeller(eq(modeller));
        verify(radarMenuBuilder,
               times(1)).setModeller(eq(modeller));
        verify(view,
               times(1)).setModellerView(eq(modellerView));
    }

    @Test
    public void checkOnStartup() {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(path,
                                                                                path,
                                                                                placeRequest,
                                                                                content);

        presenter.onStartup(path,
                            placeRequest);

        assertEquals(path,
                     presenter.editorPath);
        assertEquals(placeRequest,
                     presenter.editorPlaceRequest);

        verify(view,
               times(1)).showLoading();
        verify(presenter,
               times(1)).loadDocument(eq(path),
                                      eq(placeRequest));
        verify(dtService,
               times(1)).loadContent(eq(path));
        verify(modeller,
               times(1)).addDecisionTable(eq(path),
                                          eq(placeRequest),
                                          eq(content),
                                          any(Boolean.class),
                                          eq(null),
                                          eq(null));
        verify(presenter,
               times(1)).registerDocument(eq(dtPresenter));
        verify(decisionTableSelectedEvent,
               times(1)).fire(dtSelectedEventCaptor.capture());
        verify(view,
               times(1)).hideBusyIndicator();

        final DecisionTableSelectedEvent dtSelectedEvent = dtSelectedEventCaptor.getValue();
        assertNotNull(dtSelectedEvent);
        assertTrue(dtSelectedEvent.getPresenter().isPresent());
        assertEquals(dtPresenter,
                     dtSelectedEvent.getPresenter().get());
    }

    @Test
    public void setupTheDocks() {

        doReturn("perspectiveId").when(currentPerspective).getIdentifier();
        doReturn(false).when(docks).isSetup();

        final GuidedDecisionTableView.Presenter activeDtable = mock(GuidedDecisionTableView.Presenter.class);
        when(modeller.getActiveDecisionTable()).thenReturn(Optional.of(activeDtable));

        presenter.onFocus();

        verify(docks).setup(eq("perspectiveId"),
                            placeRequestArgumentCaptor.capture());
        assertEquals("org.kie.guvnor.explorer", placeRequestArgumentCaptor.getValue().getIdentifier());
    }

    @Test
    public void doNotSetupTheDocksTwice() {

        doReturn("perspectiveId").when(currentPerspective).getIdentifier();
        doReturn(true).when(docks).isSetup();

        final GuidedDecisionTableView.Presenter activeDtable = mock(GuidedDecisionTableView.Presenter.class);
        when(modeller.getActiveDecisionTable()).thenReturn(Optional.of(activeDtable));

        presenter.onFocus();

        verify(docks, never()).setup(anyString(),
                                     any());
    }

    @Test
    public void checkDecisionTableSelectedEventFiredWhenEditorReceivesFocusWithActiveDecisionTable() {
        final GuidedDecisionTableView.Presenter activeDtable = mock(GuidedDecisionTableView.Presenter.class);
        when(modeller.getActiveDecisionTable()).thenReturn(Optional.of(activeDtable));

        presenter.onFocus();

        verify(activeDtable,
               times(1)).initialiseAnalysis();

        verify(decisionTableSelectedEvent,
               times(1)).fire(dtSelectedEventCaptor.capture());

        final DecisionTableSelectedEvent event = dtSelectedEventCaptor.getValue();
        assertNotNull(event);
        assertTrue(event.getPresenter().isPresent());
        assertEquals(activeDtable,
                     event.getPresenter().get());
    }

    @Test
    public void checkDecisionTableSelectedEventNotFiredWhenEditorReceivesFocusWithoutActiveDecisionTable() {
        when(modeller.getActiveDecisionTable()).thenReturn(Optional.empty());

        presenter.onFocus();

        verify(decisionTableSelectedEvent,
               never()).fire(any(DecisionTableSelectedEvent.class));
    }

    @Test
    public void checkMayCloseWithNoDecisionTable() {
        assertTrue(presenter.mayClose());
    }

    @Test
    public void checkMayCloseWithCleanDecisionTable() {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(path,
                                                                                path,
                                                                                placeRequest,
                                                                                content);
        when(dtPresenter.getOriginalHashCode()).thenReturn(0);
        when(modeller.getAvailableDecisionTables()).thenReturn(new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(dtPresenter);
        }});

        assertTrue(presenter.mayClose());
    }

    @Test
    public void checkMayCloseWithDirtyDecisionTable() {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(path,
                                                                                path,
                                                                                placeRequest,
                                                                                content);
        when(dtPresenter.getOriginalHashCode()).thenReturn(10);
        when(modeller.getAvailableDecisionTables()).thenReturn(new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(dtPresenter);
        }});

        assertFalse(presenter.mayClose());
    }

    @Test
    public void checkOnClose() {
        presenter.onClose();

        verify(modeller,
               times(1)).onClose();
    }

    @Test
    public void checkOnDecisionTableSelectedWhenAvailableSelected() {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(path,
                                                                                path,
                                                                                placeRequest,
                                                                                content);
        final DecisionTableSelectedEvent event = new DecisionTableSelectedEvent(dtPresenter);

        when(modeller.isDecisionTableAvailable(any(GuidedDecisionTableView.Presenter.class))).thenReturn(true);
        when(presenter.getActiveDocument()).thenReturn(dtPresenter);

        presenter.onDecisionTableSelected(event);

        verify(presenter,
               never()).activateDocument(any(GuidedDecisionTableView.Presenter.class));

        presenter.getMenus(menus -> assertTrue(getMenuState(menus,
                                                            MenuItems.VALIDATE)));
    }

    private boolean getMenuState(final Menus menus,
                                 final MenuItems menuItem) {
        return menus.getItems().stream().filter(m -> m.getIdentifier() != null).filter(m -> m.getCaption().toLowerCase().equals(menuItem.name().toLowerCase())).findFirst().get().isEnabled();
    }

    @Test
    public void checkOnDecisionTableSelectedWhenAvailableNotSelected() {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(path, path, placeRequest, content);
        final DecisionTableSelectedEvent event = new DecisionTableSelectedEvent(dtPresenter);
        final MultiPageEditor pageEditor = mock(MultiPageEditor.class);

        when(modeller.isDecisionTableAvailable(any(GuidedDecisionTableView.Presenter.class))).thenReturn(true);
        when(presenter.getActiveDocument()).thenReturn(null);
        when(presenter.getKieEditorWrapperMultiPage()).thenReturn(pageEditor);

        presenter.onStartup(path,
                            placeRequest);

        presenter.onDecisionTableSelected(event);

        verify(presenter,
               times(1)).activateDocument(any(GuidedDecisionTableView.Presenter.class));
        verify(radarMenuItem,
               atLeast(1)).setEnabled(eq(true));

        presenter.getMenus(menus -> assertTrue(getMenuState(menus,
                                                            MenuItems.VALIDATE)));
    }

    @Test
    public void checkOnDecisionTableSelectedWhenNotAvailable() {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(path,
                                                                                path,
                                                                                placeRequest,
                                                                                content);
        final DecisionTableSelectedEvent event = new DecisionTableSelectedEvent(dtPresenter);

        when(modeller.isDecisionTableAvailable(any(GuidedDecisionTableView.Presenter.class))).thenReturn(false);

        presenter.onDecisionTableSelected(event);

        verify(presenter,
               never()).activateDocument(any(GuidedDecisionTableView.Presenter.class));
        presenter.getMenus(menus -> assertTrue(getMenuState(menus,
                                                            MenuItems.VALIDATE)));
    }

    @Test
    public void checkOnDecisionTableSelectedEventNoTableSelected() {
        final DecisionTableSelectedEvent event = DecisionTableSelectedEvent.NONE;

        presenter.onDecisionTableSelected(event);

        verify(presenter,
               never()).activateDocument(any(GuidedDecisionTableView.Presenter.class));
        presenter.getMenus(menus -> assertTrue(getMenuState(menus,
                                                            MenuItems.VALIDATE)));
    }

    @Test
    public void checkOnDecisionTableSelectedEventReselection() {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(path,
                                                                                path,
                                                                                placeRequest,
                                                                                content);
        final DecisionTableSelectedEvent eventSelect = new DecisionTableSelectedEvent(dtPresenter);
        doReturn(true).when(modeller).isDecisionTableAvailable(dtPresenter);

        presenter.onStartup(path,
                            placeRequest);

        presenter.onDecisionTableSelected(eventSelect);
        assertEquals(dtPresenter,
                     presenter.getActiveDocument());

        final DecisionTableSelectedEvent eventDeselect = DecisionTableSelectedEvent.NONE;

        presenter.onDecisionTableSelected(eventDeselect);
        assertNull(presenter.getActiveDocument());

        presenter.onDecisionTableSelected(eventSelect);
        assertEquals(dtPresenter,
                     presenter.getActiveDocument());
    }

    @Test
    public void checkRefreshDocument() {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final MultiPageEditor pageEditor = mock(MultiPageEditor.class);
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(path, path, placeRequest, content);

        presenter.onStartup(path,
                            placeRequest);

        verify(view,
               times(1)).showLoading();
        verify(dtService,
               times(1)).loadContent(eq(path));
        verify(decisionTableSelectedEvent,
               times(1)).fire(dtSelectedEventCaptor.capture());
        verify(view,
               times(1)).hideBusyIndicator();

        final DecisionTableSelectedEvent dtSelectedEvent = dtSelectedEventCaptor.getValue();
        assertNotNull(dtSelectedEvent);
        assertTrue(dtSelectedEvent.getPresenter().isPresent());
        assertEquals(dtPresenter,
                     dtSelectedEvent.getPresenter().get());

        when(dtPresenter.getCurrentPath()).thenReturn(path);
        when(presenter.getKieEditorWrapperMultiPage()).thenReturn(pageEditor);

        presenter.refreshDocument(dtPresenter);

        verify(view,
               times(2)).showLoading();
        verify(dtService,
               times(2)).loadContent(eq(path));
        verify(modeller,
               times(1)).refreshDecisionTable(eq(dtPresenter),
                                              eq(path),
                                              eq(placeRequest),
                                              eq(content),
                                              any(Boolean.class));
        verify(presenter,
               times(1)).activateDocument(eq(dtPresenter));
        verify(view,
               times(2)).hideBusyIndicator();
    }

    @Test
    public void checkRemoveDocument() {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(path,
                                                                                path,
                                                                                placeRequest,
                                                                                content);

        presenter.onStartup(path,
                            placeRequest);

        presenter.removeDocument(dtPresenter);

        verify(modeller,
               times(1)).removeDecisionTable(eq(dtPresenter));
        verify(presenter,
               times(1)).deregisterDocument(eq(dtPresenter));
        verify(presenter,
               times(1)).openOtherDecisionTable();
        verify(dtPresenter,
               times(1)).onClose();
    }

    @Test
    public void checkOpenOtherDecisionTableIsLastDecisionTable() {
        when(modeller.getAvailableDecisionTables()).thenReturn(Collections.emptySet());

        presenter.openOtherDecisionTable();

        verify(presenter,
               never()).activateDocument(any(GuidedDecisionTableView.Presenter.class));
        verify(placeManager,
               never()).forceClosePlace(any(String.class));
        verify(placeManager,
               never()).forceClosePlace(any(PlaceRequest.class));
        verify(decisionTableSelectedEvent,
               times(1)).fire(dtSelectedEventCaptor.capture());

        final DecisionTableSelectedEvent dtSelectedEvent = dtSelectedEventCaptor.getValue();
        assertNotNull(dtSelectedEvent);
        assertFalse(dtSelectedEvent.getPresenter().isPresent());
    }

    @Test
    public void checkOpenOtherDecisionTableIsNotLastDecisionTable() {
        final GuidedDecisionTableView.Presenter remainingDtPresenter = mock(GuidedDecisionTableView.Presenter.class);

        when(modeller.getAvailableDecisionTables()).thenReturn(new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(remainingDtPresenter);
        }});
        doNothing().when(presenter).activateDocument(any(GuidedDecisionTableView.Presenter.class));

        presenter.openOtherDecisionTable();

        verify(placeManager,
               never()).forceClosePlace(any(String.class));
        verify(placeManager,
               never()).forceClosePlace(any(PlaceRequest.class));
        verify(decisionTableSelectedEvent,
               times(2)).fire(dtSelectedEventCaptor.capture());

        final List<DecisionTableSelectedEvent> dtSelectedEvents = dtSelectedEventCaptor.getAllValues();
        assertNotNull(dtSelectedEvents);
        assertEquals(2,
                     dtSelectedEvents.size());
        assertFalse(dtSelectedEvents.get(0).getPresenter().isPresent());
        assertTrue(dtSelectedEvents.get(1).getPresenter().isPresent());
        assertEquals(dtSelectedEvents.get(1).getPresenter().get(),
                     remainingDtPresenter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkOnValidateWithErrors() {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(path,
                                                                                path,
                                                                                placeRequest,
                                                                                content);
        final List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>() {{
            add(new ValidationMessage());
        }};

        when(dtService.validate(any(Path.class),
                                any(GuidedDecisionTable52.class))).thenReturn(validationMessages);
        doNothing().when(presenter).showValidationPopup(any(List.class));

        presenter.onValidate(dtPresenter);

        final ArgumentCaptor<GuidedDecisionTable52> modelCaptor = ArgumentCaptor.forClass(GuidedDecisionTable52.class);

        verify(dtService,
               times(1)).validate(eq(path),
                                  modelCaptor.capture());
        assertNotNull(modelCaptor.getValue());
        assertEquals(dtPresenter.getModel(),
                     modelCaptor.getValue());
        verify(notification,
               never()).fire(any(NotificationEvent.class));
        verify(presenter,
               times(1)).showValidationPopup(eq(validationMessages));
    }

    @Test
    public void checkOnValidateWithoutErrors() {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(path,
                                                                                path,
                                                                                placeRequest,
                                                                                content);

        when(dtService.validate(any(Path.class),
                                any(GuidedDecisionTable52.class))).thenReturn(Collections.emptyList());

        presenter.onValidate(dtPresenter);

        final ArgumentCaptor<GuidedDecisionTable52> modelCaptor = ArgumentCaptor.forClass(GuidedDecisionTable52.class);

        verify(dtService,
               times(1)).validate(eq(path),
                                  modelCaptor.capture());
        assertNotNull(modelCaptor.getValue());
        assertEquals(dtPresenter.getModel(),
                     modelCaptor.getValue());
        verify(notification,
               times(1)).fire(any(NotificationEvent.class));
    }

    @Test
    public void checkOnSave() {
        final String commitMessage = "message";
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(path,
                                                                                path,
                                                                                placeRequest,
                                                                                content);

        presenter.onSave(dtPresenter,
                         commitMessage);

        final ArgumentCaptor<GuidedDecisionTable52> modelCaptor = ArgumentCaptor.forClass(GuidedDecisionTable52.class);
        final ArgumentCaptor<Metadata> metadataCaptor = ArgumentCaptor.forClass(Metadata.class);

        verify(dtService,
               times(1)).saveAndUpdateGraphEntries(eq(path),
                                                   modelCaptor.capture(),
                                                   metadataCaptor.capture(),
                                                   eq(commitMessage));
        assertNotNull(modelCaptor.getValue());
        assertEquals(dtPresenter.getModel(),
                     modelCaptor.getValue());
        assertNotNull(metadataCaptor.getValue());
        assertEquals(dtPresenter.getOverview().getMetadata(),
                     metadataCaptor.getValue());
    }

    @Test
    public void checkOnSourceTabSelected() {
        final String source = "source";
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable(path,
                                                                                path,
                                                                                placeRequest,
                                                                                content);

        when(dtService.toSource(eq(path),
                                any(GuidedDecisionTable52.class))).thenReturn(source);

        presenter.onSourceTabSelected(dtPresenter);

        final ArgumentCaptor<GuidedDecisionTable52> modelCaptor = ArgumentCaptor.forClass(GuidedDecisionTable52.class);

        verify(dtService,
               times(1)).toSource(eq(path),
                                  modelCaptor.capture());
        assertNotNull(modelCaptor.getValue());
        assertEquals(dtPresenter.getModel(),
                     modelCaptor.getValue());

        verify(presenter,
               times(1)).updateSource(eq(source));
    }

    @Test
    public void testActivateDocument() {

        final GuidedDecisionTableView.Presenter dtPresenter = mock(GuidedDecisionTableView.Presenter.class);
        final Overview overview = mock(Overview.class);
        final AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);
        final Imports imports = mock(Imports.class);
        final GuidedDecisionTablePresenter.Access access = mock(GuidedDecisionTablePresenter.Access.class);
        final MultiPageEditor pageEditor = mock(MultiPageEditor.class);
        final boolean isEditable = true;

        doReturn(overview).when(dtPresenter).getOverview();
        doReturn(oracle).when(dtPresenter).getDataModelOracle();
        doReturn(model).when(dtPresenter).getModel();
        doReturn(imports).when(model).getImports();
        doReturn(access).when(dtPresenter).getAccess();
        doReturn(isEditable).when(access).isEditable();
        doReturn(pageEditor).when(presenter).getKieEditorWrapperMultiPage();
        doNothing().when(presenter).activateDocument(any(), any(), any(), any(), anyBoolean());

        presenter.activateDocument(dtPresenter);

        verify(dtPresenter).activate();
        verify(presenter).enableMenus(true);
        verify(presenter).addColumnsTab();
        verify(presenter).enableColumnsTab(dtPresenter);
        verify(presenter).activateDocument(dtPresenter, overview, oracle, imports, !isEditable);
    }

    @Test
    public void testEnableColumnsTab() {
        final GuidedDecisionTableView.Presenter dtPresenter = mock(GuidedDecisionTableView.Presenter.class);
        final boolean isGuidedDecisionTableEditable = true;

        doReturn(isGuidedDecisionTableEditable).when(presenter).isGuidedDecisionTableEditable(any());

        presenter.enableColumnsTab(dtPresenter);

        verify(presenter).enableColumnsTab(eq(true));
    }

    @Test
    public void testIsGuidedDecisionTableEditableWhenDecisionTableDoesNotHaveEditableColumns() {

        final GuidedDecisionTableView.Presenter dtPresenter = mock(GuidedDecisionTableView.Presenter.class);
        final GuidedDecisionTablePresenter.Access access = mock(GuidedDecisionTablePresenter.Access.class);

        doReturn(access).when(dtPresenter).getAccess();
        doReturn(false).when(access).isReadOnly();
        doReturn(false).when(access).hasEditableColumns();

        final boolean isGuidedDecisionTableEditable = presenter.isGuidedDecisionTableEditable(dtPresenter);

        assertFalse(isGuidedDecisionTableEditable);
    }

    @Test
    public void testIsGuidedDecisionTableEditableWhenDecisionTableIsNotEditable() {

        final GuidedDecisionTableView.Presenter dtPresenter = mock(GuidedDecisionTableView.Presenter.class);
        final GuidedDecisionTablePresenter.Access access = mock(GuidedDecisionTablePresenter.Access.class);

        doReturn(access).when(dtPresenter).getAccess();
        doReturn(true).when(access).isReadOnly();
        doReturn(true).when(access).hasEditableColumns();

        final boolean isGuidedDecisionTableEditable = presenter.isGuidedDecisionTableEditable(dtPresenter);

        assertFalse(isGuidedDecisionTableEditable);
    }

    @Test
    public void testIsGuidedDecisionTableEditableWhenDecisionTableIsEditable() {

        final GuidedDecisionTableView.Presenter dtPresenter = mock(GuidedDecisionTableView.Presenter.class);
        final GuidedDecisionTablePresenter.Access access = mock(GuidedDecisionTablePresenter.Access.class);

        doReturn(access).when(dtPresenter).getAccess();
        doReturn(false).when(access).isReadOnly();
        doReturn(true).when(access).hasEditableColumns();

        final boolean isGuidedDecisionTableEditable = presenter.isGuidedDecisionTableEditable(dtPresenter);

        assertTrue(isGuidedDecisionTableEditable);
    }

    @Test
    public void testAddColumnsTab() {

        final MultiPageEditor pageEditor = mock(MultiPageEditor.class);

        doReturn(pageEditor).when(presenter).getKieEditorWrapperMultiPage();

        presenter.addColumnsTab();

        verify(columnsPage).init(modeller);
        verify(presenter).addEditorPage(COLUMNS_TAB_INDEX, columnsPage);
    }

    @Test
    public void testAddEditorPage() {

        final MultiPageEditor multiPage = mock(MultiPageEditor.class);
        final Page page = mock(Page.class);
        final int index = 1;

        doReturn(multiPage).when(presenter).getKieEditorWrapperMultiPage();

        presenter.addEditorPage(index, page);

        verify(multiPage).addPage(index, page);
    }

    @Test
    public void testDisableColumnsPage() {

        final MultiPageEditor multiPage = mock(MultiPageEditor.class);

        doReturn(multiPage).when(presenter).getKieEditorWrapperMultiPage();

        presenter.disableColumnsPage();

        verify(multiPage).disablePage(COLUMNS_TAB_INDEX);
    }

    @Test
    public void testEnableColumnsPage() {

        final MultiPageEditor multiPage = mock(MultiPageEditor.class);

        doReturn(multiPage).when(presenter).getKieEditorWrapperMultiPage();

        presenter.enableColumnsPage();

        verify(multiPage).enablePage(COLUMNS_TAB_INDEX);
    }

    @Test
    public void testOnUpdatedLockStatusEventWhenTableIsNotLockedAndIsEditable() {
        final UpdatedLockStatusEvent event = mock(UpdatedLockStatusEvent.class);
        final GuidedDecisionTableView.Presenter activeDecisionTable = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(false).when(event).isLocked();
        doReturn(false).when(event).isLockedByCurrentUser();
        doReturn(true).when(presenter).isGuidedDecisionTableEditable(activeDecisionTable);
        doReturn(Optional.of(activeDecisionTable)).when(modeller).getActiveDecisionTable();
        doNothing().when(presenter).enableColumnsTab(anyBoolean());

        presenter.onUpdatedLockStatusEvent(event);

        verify(presenter).enableColumnsTab(eq(true));
    }

    @Test
    public void testOnUpdatedLockStatusEventWhenTableIsNotLockedAndIsNotEditable() {
        final UpdatedLockStatusEvent event = mock(UpdatedLockStatusEvent.class);
        final GuidedDecisionTableView.Presenter activeDecisionTable = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(false).when(event).isLocked();
        doReturn(false).when(event).isLockedByCurrentUser();
        doReturn(false).when(presenter).isGuidedDecisionTableEditable(activeDecisionTable);
        doReturn(Optional.of(activeDecisionTable)).when(modeller).getActiveDecisionTable();
        doNothing().when(presenter).enableColumnsTab(anyBoolean());

        presenter.onUpdatedLockStatusEvent(event);

        verify(presenter).enableColumnsTab(eq(false));
    }

    @Test
    public void testOnUpdatedLockStatusEventWhenIsLockedByTheCurrentUser() {
        final UpdatedLockStatusEvent event = mock(UpdatedLockStatusEvent.class);
        final GuidedDecisionTableView.Presenter activeDecisionTable = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(true).when(event).isLocked();
        doReturn(true).when(event).isLockedByCurrentUser();
        doReturn(true).when(presenter).isGuidedDecisionTableEditable(activeDecisionTable);
        doReturn(Optional.of(activeDecisionTable)).when(modeller).getActiveDecisionTable();
        doNothing().when(presenter).enableColumnsTab(anyBoolean());

        presenter.onUpdatedLockStatusEvent(event);

        verify(presenter).enableColumnsTab(eq(true));
    }

    @Test
    public void testOnUpdatedLockStatusEventWhenIsLockedByAnotherUser() {
        final UpdatedLockStatusEvent event = mock(UpdatedLockStatusEvent.class);
        final GuidedDecisionTableView.Presenter activeDecisionTable = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(true).when(event).isLocked();
        doReturn(false).when(event).isLockedByCurrentUser();
        doReturn(true).when(presenter).isGuidedDecisionTableEditable(activeDecisionTable);
        doReturn(Optional.of(activeDecisionTable)).when(modeller).getActiveDecisionTable();
        doNothing().when(presenter).enableColumnsTab(anyBoolean());

        presenter.onUpdatedLockStatusEvent(event);

        verify(presenter).enableColumnsTab(eq(false));
    }

    @Test
    public void testOnUpdatedLockStatusEventWhenActiveDecisionTableIsNull() {

        final UpdatedLockStatusEvent event = mock(UpdatedLockStatusEvent.class);

        doReturn(Optional.empty()).when(modeller).getActiveDecisionTable();

        presenter.onUpdatedLockStatusEvent(event);

        verify(presenter, never()).enableColumnsTab(any());
    }
}
