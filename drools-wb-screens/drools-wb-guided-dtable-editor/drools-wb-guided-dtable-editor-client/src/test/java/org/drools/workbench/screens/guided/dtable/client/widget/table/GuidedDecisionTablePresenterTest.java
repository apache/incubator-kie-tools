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

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.google.gwt.user.client.Command;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.Clipboard;
import org.drools.workbench.screens.guided.dtable.client.editor.search.GuidedDecisionTableSearchableElement;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.DecisionTableAnalyzerProvider;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableColumnSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectionsChangedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshActionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshAttributesPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshConditionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshMenusEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshMetaDataPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.lockmanager.GuidedDecisionTableLockManager;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.GridWidgetCellFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.GridWidgetColumnFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.DependentEnumsUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.EnumLoaderUtilities;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableLinkManager;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableLinkManager.LinkFoundCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.services.verifier.reporting.client.panel.IssueSelectedEvent;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.search.common.SearchPerformedEvent;
import org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.events.NotificationEvent;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter.Access.LockedBy.CURRENT_USER;
import static org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter.Access.LockedBy.NOBODY;
import static org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter.Access.LockedBy.OTHER_USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
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
public class GuidedDecisionTablePresenterTest extends BaseGuidedDecisionTablePresenterTest {

    private GuidedDecisionTable52 model1;
    private GuidedDecisionTable52 model2;
    private GuidedDecisionTable52 model3;
    private List uiModel1Columns;
    private List uiModel2Columns;
    private List uiModel3Columns;
    private GridColumn uiModel1MockColumn;
    private GridColumn uiModel2MockColumn;
    private GridColumn uiModel3MockColumn;

    @Captor
    private ArgumentCaptor<Map<String, String>> callbackValueCaptor;

    @Captor
    private ArgumentCaptor<List> listArgumentCaptor;

    private int originalHashCode;

    @Before
    public void setup() {
        super.setup();

        //Adding rows affects the HashCode so store the original
        originalHashCode = model.hashCode();

        dtPresenter.onAppendRow();
        dtPresenter.onAppendRow();
        dtPresenter.onAppendRow();

        model1 = new GuidedDecisionTable52();
        model2 = new GuidedDecisionTable52();
        model3 = new GuidedDecisionTable52();

        uiModel1Columns = mock(List.class);
        uiModel2Columns = mock(List.class);
        uiModel3Columns = mock(List.class);

        uiModel1MockColumn = mock(GridColumn.class);
        uiModel2MockColumn = mock(GridColumn.class);
        uiModel3MockColumn = mock(GridColumn.class);
    }

    @Test
    public void testOnUpdatedLockStatusEvent_LockedByCurrentUser() {
        final UpdatedLockStatusEvent event = mock(UpdatedLockStatusEvent.class);
        when(event.getFile()).thenReturn(dtPath);
        when(event.isLockedByCurrentUser()).thenReturn(true);
        when(event.isLocked()).thenReturn(true);

        dtPresenter.onUpdatedLockStatusEvent(event);

        verify(dtPresenter).refreshColumnsPage();
        verify(dtPresenter).refreshMenus();
        assertEquals(CURRENT_USER, dtPresenter.getAccess().getLock());
    }

    @Test
    public void testOnUpdatedLockStatusEvent_LockedByOtherUser() {
        final UpdatedLockStatusEvent event = mock(UpdatedLockStatusEvent.class);
        when(event.getFile()).thenReturn(dtPath);
        when(event.isLockedByCurrentUser()).thenReturn(false);
        when(event.isLocked()).thenReturn(true);

        dtPresenter.onUpdatedLockStatusEvent(event);

        verify(dtPresenter).refreshColumnsPage();
        verify(dtPresenter).refreshMenus();
        assertEquals(OTHER_USER, dtPresenter.getAccess().getLock());
    }

    @Test
    public void testOnUpdatedLockStatusEvent_NotLocked() {
        final UpdatedLockStatusEvent event = mock(UpdatedLockStatusEvent.class);
        when(event.getFile()).thenReturn(dtPath);
        dtPresenter.onUpdatedLockStatusEvent(event);

        verify(dtPresenter).refreshColumnsPage();
        verify(dtPresenter).refreshMenus();
        assertEquals(NOBODY, dtPresenter.getAccess().getLock());
    }

    @Test
    public void testOnUpdatedLockStatusEvent_NullFile() {
        final UpdatedLockStatusEvent event = mock(UpdatedLockStatusEvent.class);
        dtPresenter.onUpdatedLockStatusEvent(event);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnIssueSelectedEvent_NullEvent() {
        dtPresenter.onIssueSelectedEvent(null);

        verify(renderer,
               never()).clearHighlights();
        verify(renderer,
               never()).highlightRows(any(Severity.class),
                                      any(Set.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnIssueSelectedEvent_ClearHighlightsWithDifferentTable() {
        dtPresenter.onIssueSelectedEvent(new IssueSelectedEvent(mock(PlaceRequest.class),
                                                                mock(Issue.class)));

        verify(renderer,
               times(1)).clearHighlights();
        verify(renderer,
               never()).highlightRows(any(Severity.class),
                                      any(Set.class));
        verify(view,
               times(1)).draw();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnIssueSelectedEvent_HighlightsRows() {
        dtPresenter.onIssueSelectedEvent(new IssueSelectedEvent(dtPlaceRequest,
                                                                mock(Issue.class)));

        verify(renderer,
               never()).clearHighlights();
        verify(renderer,
               times(1)).highlightRows(any(),
                                       any(Set.class));
        verify(view,
               times(1)).draw();
    }

    @Test
    public void testActivate() {
        dtPresenter.activate();
        verify(lockManager,
               times(1)).fireChangeTitleEvent();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void setContent() {
        //dtPresenter.setContent(...) is called by the base tests @Before method
        verify(dtPresenter,
               times(1)).initialiseContent(eq(dtPath),
                                           eq(dtPlaceRequest),
                                           eq(dtContent),
                                           eq(false));
        verify(oracleFactory,
               times(1)).makeAsyncPackageDataModelOracle(eq(dtPath),
                                                         any(GuidedDecisionTable52.class),
                                                         any(PackageDataModelOracleBaselinePayload.class));
        verify(dtPresenter,
               times(1)).makeUiModel();
        verify(dtPresenter,
               times(1)).makeView(any(Set.class));
        verify(dtPresenter,
               times(1)).initialiseAccess(false);
        verify(dtPresenter,
               times(1)).initialiseLockManager();
        verify(dtPresenter,
               times(1)).initialiseUtilities();
        verify(dtPresenter,
               times(1)).initialiseModels();
        verify(dtPresenter,
               times(1)).initialiseValidationAndVerification();
        verify(dtPresenter,
               times(1)).initialiseEventHandlers();
        verify(dtPresenter,
               times(1)).initialiseAuditLog();

        assertEquals(GuidedDecisionTableView.ROW_HEIGHT,
                     dtPresenter.getUiModel().getRow(0).getHeight(),
                     0.0);
        assertEquals(GuidedDecisionTableView.ROW_HEIGHT,
                     dtPresenter.getUiModel().getRow(1).getHeight(),
                     0.0);
        assertEquals(GuidedDecisionTableView.ROW_HEIGHT,
                     dtPresenter.getUiModel().getRow(2).getHeight(),
                     0.0);

        assertEquals(originalHashCode,
                     (int) dtPresenter.getOriginalHashCode());
        assertNotEquals(dtContent.getModel().hashCode(),
                        (int) dtPresenter.getOriginalHashCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void refreshContent() {
        // dtPresenter.setContent(...) is called by the base tests @Before method so
        // expect some invocations to have occurred twice: once for setContent(...)
        // and again for refreshContent(...)
        dtPresenter.refreshContent(dtPath,
                                   dtPlaceRequest,
                                   dtContent,
                                   false);

        verify(dtPresenter,
               times(2)).initialiseContent(eq(dtPath),
                                           eq(dtPlaceRequest),
                                           eq(dtContent),
                                           eq(false));
        verify(oracleFactory,
               times(2)).makeAsyncPackageDataModelOracle(eq(dtPath),
                                                         any(GuidedDecisionTable52.class),
                                                         any(PackageDataModelOracleBaselinePayload.class));
        verify(dtPresenter,
               times(2)).makeUiModel();
        verify(dtPresenter,
               times(2)).makeView(any(Set.class));
        verify(dtPresenter,
               times(2)).initialiseAccess(false);
        verify(dtPresenter,
               times(2)).initialiseLockManager();
        verify(dtPresenter,
               times(2)).initialiseUtilities();
        verify(dtPresenter,
               times(2)).initialiseModels();
        verify(dtPresenter,
               times(2)).initialiseValidationAndVerification();
        verify(dtPresenter,
               times(2)).initialiseAuditLog();

        assertEquals(dtContent.getModel().hashCode(),
                     (int) dtPresenter.getOriginalHashCode());

        //These invocations are as a result of the previous Presenter being destroyed
        verify(dtPresenter,
               times(1)).terminateAnalysis();
        verify(lockManager,
               times(1)).releaseLock();
        verify(oracleFactory,
               times(1)).destroy(eq(oracle));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void initialiseEventHandler() {
        final ArgumentCaptor<NodeDragMoveHandler> nodeDragMoveHandlerArgumentCaptor = ArgumentCaptor.forClass(NodeDragMoveHandler.class);
        final ArgumentCaptor<NodeMouseDoubleClickHandler> nodeMouseDoubleClickHandlerArgumentCaptor = ArgumentCaptor.forClass(NodeMouseDoubleClickHandler.class);

        //dtPresenter.setContent(...) is called by the base tests @Before method
        verify(view,
               times(1)).registerNodeDragMoveHandler(nodeDragMoveHandlerArgumentCaptor.capture());
        verify(view,
               times(1)).registerNodeMouseDoubleClickHandler(nodeMouseDoubleClickHandlerArgumentCaptor.capture());

        final NodeDragMoveHandler nodeDragMoveHandler = nodeDragMoveHandlerArgumentCaptor.getValue();
        final NodeMouseDoubleClickHandler nodeMouseDoubleClickHandler = nodeMouseDoubleClickHandlerArgumentCaptor.getValue();
        assertNotNull(nodeDragMoveHandler);
        assertNotNull(nodeMouseDoubleClickHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkRegisteredNodeDragMoveHandler() {
        final ArgumentCaptor<NodeDragMoveHandler> nodeDragMoveHandlerArgumentCaptor = ArgumentCaptor.forClass(NodeDragMoveHandler.class);

        //dtPresenter.setContent(...) is called by the base tests @Before method
        verify(view,
               times(1)).registerNodeDragMoveHandler(nodeDragMoveHandlerArgumentCaptor.capture());

        final NodeDragMoveHandler nodeDragMoveHandler = nodeDragMoveHandlerArgumentCaptor.getValue();
        assertNotNull(nodeDragMoveHandler);

        nodeDragMoveHandler.onNodeDragMove(mock(NodeDragMoveEvent.class));
        verify(modellerPresenter,
               times(1)).updateRadar();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkRegisteredNodeMouseDoubleClickHandlerOverHeader() {
        final ArgumentCaptor<NodeMouseDoubleClickHandler> nodeMouseDoubleClickHandlerArgumentCaptor = ArgumentCaptor.forClass(NodeMouseDoubleClickHandler.class);

        //dtPresenter.setContent(...) is called by the base tests @Before method
        verify(view,
               times(1)).registerNodeMouseDoubleClickHandler(nodeMouseDoubleClickHandlerArgumentCaptor.capture());

        final NodeMouseDoubleClickHandler nodeMouseDoubleClickHandler = nodeMouseDoubleClickHandlerArgumentCaptor.getValue();
        assertNotNull(nodeMouseDoubleClickHandler);

        //Mouse over Header, not pinned
        final NodeMouseDoubleClickEvent event = mock(NodeMouseDoubleClickEvent.class);
        when(view.isNodeMouseEventOverCaption(eq(event))).thenReturn(true);
        when(modellerPresenter.isGridPinned()).thenReturn(false);

        nodeMouseDoubleClickHandler.onNodeMouseDoubleClick(event);

        verify(dtPresenter,
               times(1)).enterPinnedMode(eq(view),
                                         any(Command.class));

        //Mouse over Header, pinned
        when(modellerPresenter.isGridPinned()).thenReturn(true);

        nodeMouseDoubleClickHandler.onNodeMouseDoubleClick(event);

        verify(dtPresenter,
               times(1)).exitPinnedMode(any(Command.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkRegisteredNodeMouseDoubleClickHandlerNotOverHeader() {
        final ArgumentCaptor<NodeMouseDoubleClickHandler> nodeMouseDoubleClickHandlerArgumentCaptor = ArgumentCaptor.forClass(NodeMouseDoubleClickHandler.class);

        //dtPresenter.setContent(...) is called by the base tests @Before method
        verify(view,
               times(1)).registerNodeMouseDoubleClickHandler(nodeMouseDoubleClickHandlerArgumentCaptor.capture());

        final NodeMouseDoubleClickHandler nodeMouseDoubleClickHandler = nodeMouseDoubleClickHandlerArgumentCaptor.getValue();
        assertNotNull(nodeMouseDoubleClickHandler);

        //Mouse not over Header
        final NodeMouseDoubleClickEvent event = mock(NodeMouseDoubleClickEvent.class);
        when(view.isNodeMouseEventOverCaption(eq(event))).thenReturn(false);

        nodeMouseDoubleClickHandler.onNodeMouseDoubleClick(event);

        verify(dtPresenter,
               never()).enterPinnedMode(any(GridWidget.class),
                                        any(Command.class));
        verify(dtPresenter,
               never()).enterPinnedMode(any(GridWidget.class),
                                        any(Command.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void link() {
        final GuidedDecisionTableView.Presenter dtPresenter2 = mock(GuidedDecisionTableView.Presenter.class);
        final GuidedDecisionTableView.Presenter dtPresenter3 = mock(GuidedDecisionTableView.Presenter.class);
        final Set<GuidedDecisionTableView.Presenter> dtPresenters = new HashSet<GuidedDecisionTableView.Presenter>() {

            {
                add(dtPresenter);
                add(dtPresenter2);
                add(dtPresenter3);
            }
        };
        when(dtPresenter.getModel()).thenReturn(model1);
        when(dtPresenter2.getModel()).thenReturn(model2);
        when(dtPresenter3.getModel()).thenReturn(model3);

        dtPresenter.link(dtPresenters);

        verify(linkManager,
               times(1)).link(eq(model1),
                              eq(model2),
                              any(LinkFoundCallback.class));
        verify(linkManager,
               times(1)).link(eq(model1),
                              eq(model3),
                              any(LinkFoundCallback.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void linkMultipleRelatedTables() {
        addActionInsertFactToModel(model1,
                                   "Applicant",
                                   "name");
        addConstraintToModel(model2,
                             "Applicant",
                             "name");
        addConstraintToModel(model3,
                             "Applicant",
                             "name");

        linkTables();

        verify(uiModel1Columns,
               atLeast(1)).get(eq(3));
        verify(uiModel2Columns,
               atLeast(1)).get(eq(3));
        verify(uiModel3Columns,
               atLeast(1)).get(eq(3));

        verify(uiModel2MockColumn).setLink(eq(uiModel1MockColumn));
        verify(uiModel3MockColumn).setLink(eq(uiModel1MockColumn));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLinkOneProducerTwoConsumersOneFact() throws Exception {
        addActionInsertFactToModel(model1,
                                   "Applicant",
                                   "name");
        addActionInsertFactToModel(model1,
                                   "Applicant",
                                   "age");

        addConstraintToModel(model2,
                             "Applicant",
                             "name");

        addBrlConstraintToModel(model3,
                                "Applicant",
                                "age");

        linkTables();

        verify(uiModel2MockColumn).setLink(eq(uiModel1MockColumn));
        verify(uiModel3MockColumn).setLink(eq(uiModel1MockColumn));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLinkOneProducerTwoConsumersTwoFacts() throws Exception {
        addActionInsertFactToModel(model1,
                                   "Applicant",
                                   "name");
        addActionInsertFactToModel(model1,
                                   "LoanApplication",
                                   "amount");

        addConstraintToModel(model2,
                             "Applicant",
                             "name");

        addBrlConstraintToModel(model3,
                                "LoanApplication",
                                "amount");

        linkTables();

        verify(uiModel2MockColumn).setLink(eq(uiModel1MockColumn));
        verify(uiModel3MockColumn).setLink(eq(uiModel1MockColumn));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLinkTwoProducersOneConsumerOneFact() throws Exception {
        addBrlInsertActionToModel(model1,
                                  "Applicant",
                                  "name");

        addBrlInsertActionToModel(model1,
                                  "Applicant",
                                  "age");

        addActionInsertFactToModel(model2,
                                   "Applicant",
                                   "name");
        addActionInsertFactToModel(model2,
                                   "Applicant",
                                   "age");

        addConstraintToModel(model3,
                             "Applicant",
                             "name");

        linkTables();

        verify(uiModel3MockColumn).setLink(eq(uiModel1MockColumn));
        verify(uiModel3MockColumn).setLink(eq(uiModel2MockColumn));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLinkTwoProducersOneConsumerTwoFacts() throws Exception {
        addBrlInsertActionToModel(model1,
                                  "Applicant",
                                  "name");

        addBrlInsertActionToModel(model1,
                                  "LoanApplication",
                                  "age");

        addActionInsertFactToModel(model2,
                                   "Applicant",
                                   "name");
        addActionInsertFactToModel(model2,
                                   "Applicant",
                                   "age");

        addConstraintToModel(model3,
                             "Applicant",
                             "name");

        linkTables();

        verify(uiModel3MockColumn).setLink(eq(uiModel1MockColumn));
        verify(uiModel3MockColumn).setLink(eq(uiModel2MockColumn));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLinkCircle() throws Exception {
        addConstraintToModel(model1,
                             "Applicant",
                             "name");

        addActionInsertFactToModel(model1,
                                   "Applicant",
                                   "age");

        addConstraintToModel(model2,
                             "Applicant",
                             "age");

        addActionInsertFactToModel(model2,
                                   "LoanApplication",
                                   "amount");

        addConstraintToModel(model3,
                             "LoanApplication",
                             "amount");

        addActionInsertFactToModel(model3,
                                   "Applicant",
                                   "name");

        linkTables();

        verify(uiModel1MockColumn).setLink(eq(uiModel3MockColumn));
        verify(uiModel2MockColumn).setLink(eq(uiModel1MockColumn));
        verify(uiModel3MockColumn).setLink(eq(uiModel2MockColumn));
    }

    @Test
    public void setRuleNameColumnVisibility() {

        dtPresenter.setShowRuleName(true);
        assertFalse(model.getRuleNameColumn().isHideColumn());

        dtPresenter.setShowRuleName(false);
        assertTrue(model.getRuleNameColumn().isHideColumn());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoLink() throws Exception {
        addActionInsertFactToModel(model1,
                                   "Applicant",
                                   "name");
        addConstraintToModel(model2,
                             "Applicant",
                             "age");

        linkTables();

        verify(uiModel2MockColumn,
               never()).setLink(eq(uiModel1MockColumn));
        verify(uiModel1MockColumn,
               never()).setLink(eq(uiModel2MockColumn));
    }

    @SuppressWarnings("unchecked")
    private void linkTables() {
        final GridData uiModel1 = spy(new BaseGridData());
        final GridData uiModel2 = spy(new BaseGridData());
        final GridData uiModel3 = spy(new BaseGridData());

        final GuidedDecisionTableView dtView2 = mock(GuidedDecisionTableView.class);
        final GuidedDecisionTableView dtView3 = mock(GuidedDecisionTableView.class);
        final GuidedDecisionTableView.Presenter dtPresenter2 = setupPresenter();
        final GuidedDecisionTableView.Presenter dtPresenter3 = setupPresenter();
        final Set<GuidedDecisionTableView.Presenter> dtPresenters = new HashSet<GuidedDecisionTableView.Presenter>() {

            {
                add(dtPresenter);
                add(dtPresenter2);
                add(dtPresenter3);
            }
        };

        when(dtPresenter.getModel()).thenReturn(model1);
        when(dtPresenter2.getModel()).thenReturn(model2);
        when(dtPresenter3.getModel()).thenReturn(model3);

        when(dtPresenter2.getView()).thenReturn(dtView2);
        when(dtPresenter3.getView()).thenReturn(dtView3);
        when(view.getModel()).thenReturn(uiModel1);
        when(dtView2.getModel()).thenReturn(uiModel2);
        when(dtView3.getModel()).thenReturn(uiModel3);
        when(uiModel1.getColumns()).thenReturn(uiModel1Columns);
        when(uiModel2.getColumns()).thenReturn(uiModel2Columns);
        when(uiModel3.getColumns()).thenReturn(uiModel3Columns);
        when(uiModel1Columns.get(anyInt())).thenReturn(uiModel1MockColumn);
        when(uiModel2Columns.get(anyInt())).thenReturn(uiModel2MockColumn);
        when(uiModel3Columns.get(anyInt())).thenReturn(uiModel3MockColumn);

        dtPresenter.link(dtPresenters);
        dtPresenter2.link(dtPresenters);
        dtPresenter3.link(dtPresenters);
    }

    private void addConstraintToModel(final GuidedDecisionTable52 model,
                                      final String factType,
                                      final String fieldName) {
        final Pattern52 p = new Pattern52();
        p.setFactType(factType);
        final ConditionCol52 c = new ConditionCol52();
        c.setOperator("==");
        c.setFactField(fieldName);
        c.setFieldType(DataType.TYPE_STRING);
        p.getChildColumns().add(c);
        model.getConditions().add(p);
    }

    private void addActionInsertFactToModel(final GuidedDecisionTable52 model,
                                            final String factType,
                                            final String fieldName) {
        final ActionInsertFactCol52 aif = new ActionInsertFactCol52();
        aif.setFactType(factType);
        aif.setFactField(fieldName);
        model.getActionCols().add(aif);
    }

    private void addBrlInsertActionToModel(final GuidedDecisionTable52 model,
                                           final String factType,
                                           final String field) {
        final BRLActionColumn brlAction = new BRLActionColumn();
        final ActionInsertFact aif = new ActionInsertFact(factType);
        final ActionFieldValue afv = new ActionFieldValue(field,
                                                          "$var",
                                                          DataType.TYPE_STRING);
        afv.setNature(FieldNatureType.TYPE_VARIABLE);
        aif.addFieldValue(afv);
        brlAction.setDefinition(Collections.singletonList(aif));

        BRLActionVariableColumn variableColumn = new BRLActionVariableColumn("$var",
                                                                             DataType.TYPE_STRING,
                                                                             factType,
                                                                             field);
        brlAction.getChildColumns().add(variableColumn);
        model.getActionCols().add(brlAction);
    }

    private void addBrlConstraintToModel(final GuidedDecisionTable52 model,
                                         final String factType,
                                         final String field) {
        final Pattern52 p = new Pattern52();
        p.setFactType(factType);
        final BRLConditionColumn conditionColumn = new BRLConditionColumn();
        conditionColumn.setFactField(field);
        p.getChildColumns().add(conditionColumn);
        model.getConditions().add(p);
    }

    @Test
    public void onClose() {
        // Reset the mocks to check for interactions caused by explicit invocation of onClose(). onClose()
        // is implicitly called by setContent(...) which is called by the base tests @Before method.
        reset(dtPresenter,
              lockManager);

        dtPresenter.onClose();

        verify(dtPresenter,
               times(1)).terminateAnalysis();
        verify(lockManager,
               times(1)).releaseLock();
        verify(oracleFactory,
               times(1)).destroy(eq(oracle));
    }

    @Test
    public void select() {
        dtPresenter.select(dtPresenter.getView());

        verify(decisionTableSelectedEvent,
               times(1)).fire(any(DecisionTableSelectedEvent.class));

        // 3 rows created during the setup + 1 select action
        verify(lockManager, times(4)).acquireLock();
    }

    @Test
    public void selectLinkedColumn() {
        final GridColumn uiColumn = mock(GridColumn.class);

        dtPresenter.selectLinkedColumn(uiColumn);

        verify(decisionTableColumnSelectedEvent,
               times(1)).fire(any(DecisionTableColumnSelectedEvent.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getPackageParentRuleNames() {
        final Set<String> parentRuleNames = new HashSet<>();
        parentRuleNames.add("parentRule1");
        parentRuleNames.add("parentRule2");

        final ParameterizedCommand<Collection<String>> parentRuleNamesCommand = mock(ParameterizedCommand.class);

        when(ruleNameService.getRuleNames(any(),
                                          any())).thenReturn(parentRuleNames);

        dtPresenter.getPackageParentRuleNames(parentRuleNamesCommand);

        verify(parentRuleNamesCommand,
               times(1)).execute(eq(parentRuleNames));
    }

    @Test
    public void hasColumnDefinitionsEmptyModel() {
        assertFalse(dtPresenter.hasColumnDefinitions());
    }

    @Test
    public void hasColumnDefinitionsWithAttributeColumn() {
        final AttributeCol52 attribute = new AttributeCol52();
        attribute.setAttribute(Attribute.SALIENCE.getAttributeName());

        dtPresenter.getModel().getAttributeCols().add(attribute);

        assertTrue(dtPresenter.hasColumnDefinitions());
    }

    @Test
    public void hasColumnDefinitionsWithConditionColumn() {
        final Pattern52 pattern = new Pattern52();
        pattern.setFactType("FactType");
        final ConditionCol52 condition = new ConditionCol52();
        condition.setFactField("field");
        pattern.getChildColumns().add(condition);

        dtPresenter.getModel().getConditions().add(pattern);

        assertTrue(dtPresenter.hasColumnDefinitions());
    }

    @Test
    public void hasColumnDefinitionsWithMetadataColumn() {
        final MetadataCol52 metadataCol52 = new MetadataCol52();

        dtPresenter.getModel().getMetadataCols().add(metadataCol52);

        assertTrue(dtPresenter.hasColumnDefinitions());
    }

    @Test
    public void hasColumnDefinitionsWithActionColumn() {
        final ActionInsertFactCol52 action = new ActionInsertFactCol52();
        action.setFactType("FactType");
        action.setFactField("field");

        dtPresenter.getModel().getActionCols().add(action);

        assertTrue(dtPresenter.hasColumnDefinitions());
    }

    @Test
    public void getBindingsWithSimpleClassName() {
        final Pattern52 pattern1 = new Pattern52();
        pattern1.setFactType("FactType1");
        pattern1.setBoundName("$fact1");
        final ConditionCol52 condition1 = new ConditionCol52();
        condition1.setFactField("field");
        condition1.setBinding("$field1");
        pattern1.getChildColumns().add(condition1);

        final Pattern52 pattern2 = new Pattern52();
        pattern2.setFactType("FactType1");
        final ConditionCol52 condition2 = new ConditionCol52();
        condition2.setFactField("field");
        pattern2.getChildColumns().add(condition2);

        when(oracle.getFieldClassName(eq("FactType1"),
                                      eq("field"))).thenReturn("FactType1");

        dtPresenter.getModel().getConditions().add(pattern1);
        dtPresenter.getModel().getConditions().add(pattern2);

        final Set<String> bindings = dtPresenter.getBindings("FactType1");

        assertNotNull(bindings);
        assertEquals(2,
                     bindings.size());
        assertTrue(bindings.contains("$fact1"));
        assertTrue(bindings.contains("$field1"));
    }

    @Test
    public void getBindingsWithFullyQualifiedClassName() {
        final Pattern52 pattern1 = new Pattern52();
        pattern1.setFactType("FactType1");
        pattern1.setBoundName("$fact1");
        final ConditionCol52 condition1 = new ConditionCol52();
        condition1.setFactField("field");
        condition1.setBinding("$field1");
        pattern1.getChildColumns().add(condition1);

        final Pattern52 pattern2 = new Pattern52();
        pattern2.setFactType("FactType1");
        final ConditionCol52 condition2 = new ConditionCol52();
        condition2.setFactField("field");
        pattern2.getChildColumns().add(condition2);

        when(oracle.getFieldClassName(eq("FactType1"),
                                      eq("field"))).thenReturn("FactType1");

        dtPresenter.getModel().getConditions().add(pattern1);
        dtPresenter.getModel().getConditions().add(pattern2);

        final Set<String> bindings = dtPresenter.getBindings("org.drools.workbench.screens.guided.dtable.client.widget.table.FactType1");

        assertNotNull(bindings);
        assertEquals(1,
                     bindings.size());
        assertTrue(bindings.contains("$fact1"));
    }

    @Test
    public void getValueListLookups() {
        final AttributeCol52 attribute = new AttributeCol52();
        attribute.setAttribute(Attribute.ENABLED.getAttributeName());

        final Map<String, String> valueList = dtPresenter.getValueListLookups(attribute);

        assertNotNull(valueList);
        assertEquals(2,
                     valueList.size());
        assertTrue(valueList.containsKey("true"));
        assertTrue(valueList.containsKey("false"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getEnumLookupsWithNoEnumsDefined() {
        final DependentEnumsUtilities.Context context = mock(DependentEnumsUtilities.Context.class);
        final Callback<Map<String, String>> callback = mock(Callback.class);

        dtPresenter.getEnumLookups("FactType",
                                   "field",
                                   context,
                                   callback);

        verify(callback,
               times(1)).callback(callbackValueCaptor.capture());

        final Map<String, String> callbackValue = callbackValueCaptor.getValue();
        assertNotNull(callbackValue);
        assertTrue(callbackValue.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getEnumLookupsWithFixedListDefinition() {
        final DependentEnumsUtilities.Context context = mock(DependentEnumsUtilities.Context.class);
        final Callback<Map<String, String>> callback = mock(Callback.class);
        final DropDownData dd = DropDownData.create(new String[]{"one", "two"});

        when(oracle.getEnums(eq("FactType"),
                             eq("field"),
                             any(Map.class))).thenReturn(dd);

        dtPresenter.getEnumLookups("FactType",
                                   "field",
                                   context,
                                   callback);

        verify(callback,
               times(1)).callback(callbackValueCaptor.capture());

        final Map<String, String> callbackValue = callbackValueCaptor.getValue();
        assertNotNull(callbackValue);
        assertFalse(callbackValue.isEmpty());
        assertTrue(callbackValue.containsKey("one"));
        assertTrue(callbackValue.containsKey("two"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getEnumLookupsWithFixedListDefinitionWithSplitter() {
        final DependentEnumsUtilities.Context context = mock(DependentEnumsUtilities.Context.class);
        final Callback<Map<String, String>> callback = mock(Callback.class);
        final DropDownData dd = DropDownData.create(new String[]{"1=one", "2=two"});

        when(oracle.getEnums(eq("FactType"),
                             eq("field"),
                             any(Map.class))).thenReturn(dd);

        dtPresenter.getEnumLookups("FactType",
                                   "field",
                                   context,
                                   callback);

        verify(callback,
               times(1)).callback(callbackValueCaptor.capture());

        final Map<String, String> callbackValue = callbackValueCaptor.getValue();
        assertNotNull(callbackValue);
        assertFalse(callbackValue.isEmpty());
        assertTrue(callbackValue.containsKey("1"));
        assertTrue(callbackValue.containsKey("2"));
        assertEquals("one",
                     callbackValue.get("1"));
        assertEquals("two",
                     callbackValue.get("2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getEnumLookupsWithQueryExpressionDefinition() {
        final DependentEnumsUtilities.Context context = mock(DependentEnumsUtilities.Context.class);
        final Callback<Map<String, String>> callback = mock(Callback.class);
        final DropDownData dd = DropDownData.create("query",
                                                    new String[]{"one", "two"});

        when(oracle.getEnums(eq("FactType"),
                             eq("field"),
                             any(Map.class))).thenReturn(dd);
        when(enumDropdownService.loadDropDownExpression(any(Path.class),
                                                        any(String[].class),
                                                        any(String.class))).thenReturn(new String[]{"three", "four"});

        dtPresenter.getEnumLookups("FactType",
                                   "field",
                                   context,
                                   callback);

        verify(callback,
               times(1)).callback(callbackValueCaptor.capture());

        final Map<String, String> callbackValue = callbackValueCaptor.getValue();
        assertNotNull(callbackValue);
        assertFalse(callbackValue.isEmpty());
        assertTrue(callbackValue.containsKey("three"));
        assertTrue(callbackValue.containsKey("four"));
    }

    @Test
    public void editConditionWithPatternAndCondition() {
        dtPresenter.editCondition(mock(Pattern52.class),
                                  mock(ConditionCol52.class));
    }

    @Test
    public void appendPatternAndConditionColumn() throws VetoException {
        reset(modellerPresenter);

        final Pattern52 pattern = new Pattern52();
        pattern.setFactType("FactType");
        final ConditionCol52 condition = new ConditionCol52();
        condition.setFactField("field");
        condition.setHeader("header");

        dtPresenter.appendColumn(pattern,
                                 condition);

        verify(synchronizer,
               times(1)).appendColumn(eq(pattern),
                                      eq(condition));
        verify(refreshConditionsPanelEvent,
               times(1)).fire(any(RefreshConditionsPanelEvent.class));
        verify(modellerPresenter,
               times(1)).updateLinks();
    }

    @Test
    public void appendConditionColumn() throws VetoException {
        reset(modellerPresenter);

        final ConditionCol52 condition = new ConditionCol52();
        condition.setFactField("field");
        condition.setHeader("header");

        dtPresenter.appendColumn(condition);

        verify(synchronizer,
               times(1)).appendColumn(eq(condition));
        verify(refreshConditionsPanelEvent,
               times(1)).fire(any(RefreshConditionsPanelEvent.class));
        verify(modellerPresenter,
               times(1)).updateLinks();
    }

    @Test
    public void appendActionColumn() throws VetoException {
        reset(modellerPresenter);

        final ActionInsertFactCol52 action = new ActionInsertFactCol52();
        action.setFactType("FactType");
        action.setFactField("field");
        action.setHeader("header");
        when(oracle.getFieldType(eq("FactType"),
                                 eq("field"))).thenReturn(DataType.TYPE_STRING);

        dtPresenter.appendColumn(action);

        verify(synchronizer,
               times(1)).appendColumn(eq(action));
        verify(refreshActionsPanelEvent,
               times(1)).fire(any(RefreshActionsPanelEvent.class));
        verify(modellerPresenter,
               times(1)).updateLinks();
    }

    @Test
    public void appendRow() throws VetoException {
        reset(synchronizer,
              modellerPresenter);

        dtPresenter.onAppendRow();

        verify(synchronizer,
               times(1)).appendRow();
        verify(modellerPresenter,
               times(1)).updateLinks();
        // 3 rows created during the setup + 1 row created on this test
        verify(lockManager,
               times(4)).acquireLock();
    }

    @Test
    public void deleteConditionColumn() throws VetoException {
        final Pattern52 pattern = new Pattern52();
        pattern.setFactType("FactType");
        final ConditionCol52 condition = new ConditionCol52();
        condition.setFactField("field");
        condition.setHeader("header");
        dtPresenter.appendColumn(pattern,
                                 condition);
        reset(modellerPresenter);

        dtPresenter.deleteColumn(condition);

        verify(synchronizer,
               times(1)).deleteColumn(eq(condition));
        verify(modellerPresenter,
               times(1)).updateLinks();
        checkDTSelectionsChangedEventFired(1);
    }

    @Test
    public void deleteActionColumn() throws VetoException {
        final ActionInsertFactCol52 column = new ActionInsertFactCol52();
        column.setFactType("FactType");
        column.setFactField("field");
        column.setHeader("header");
        when(oracle.getFieldType(eq("FactType"),
                                 eq("field"))).thenReturn(DataType.TYPE_STRING);
        dtPresenter.appendColumn(column);
        reset(modellerPresenter);

        dtPresenter.deleteColumn(column);

        verify(synchronizer,
               times(1)).deleteColumn(eq(column));
        verify(modellerPresenter,
               times(1)).updateLinks();
        checkDTSelectionsChangedEventFired(1);
    }

    @Test
    public void deleteAttributeColumn() throws Exception {
        final AttributeCol52 column = new AttributeCol52();
        column.setAttribute(Attribute.SALIENCE.getAttributeName());
        dtPresenter.appendColumn(column);
        reset(modellerPresenter);

        dtPresenter.deleteColumn(column);

        verify(synchronizer,
               times(1)).deleteColumn(eq(column));
        verify(modellerPresenter,
               times(1)).updateLinks();

        verify(refreshAttributesPanelEvent,
               times(2)).fire(any(RefreshAttributesPanelEvent.class));
        checkDTSelectionsChangedEventFired(1);
    }

    @Test
    public void updatePatternAndConditionColumn() throws VetoException {
        final Pattern52 pattern = new Pattern52();
        pattern.setFactType("FactType");
        pattern.setBoundName("$f");
        final ConditionCol52 condition = new ConditionCol52();
        condition.setFactField("field");
        condition.setHeader("header");
        dtPresenter.appendColumn(pattern,
                                 condition);
        reset(modellerPresenter);

        final Pattern52 updatePattern = new Pattern52();
        updatePattern.setFactType("NewType");
        updatePattern.setBoundName("$f");
        final ConditionCol52 updateCondition = new ConditionCol52();
        updateCondition.setFactField("newField");
        updateCondition.setHeader("newHeader");

        dtPresenter.updateColumn(pattern,
                                 condition,
                                 updatePattern,
                                 updateCondition);

        verify(synchronizer,
               times(1)).updateColumn(eq(pattern),
                                      eq(condition),
                                      eq(updatePattern),
                                      eq(updateCondition));
        verify(modellerPresenter,
               times(1)).updateLinks();
    }

    @Test
    public void updateConditionColumn() throws VetoException {
        final Pattern52 pattern = new Pattern52();
        pattern.setFactType("FactType");
        final ConditionCol52 condition = new ConditionCol52();
        condition.setFactField("field");
        condition.setHeader("header");
        dtPresenter.appendColumn(pattern,
                                 condition);
        reset(modellerPresenter);

        final ConditionCol52 updateCondition = new ConditionCol52();
        updateCondition.setFactField("newField");
        updateCondition.setHeader("newHeader");

        dtPresenter.updateColumn(condition,
                                 updateCondition);

        verify(synchronizer,
               times(1)).updateColumn(eq(condition),
                                      eq(updateCondition));
        verify(modellerPresenter,
               times(1)).updateLinks();
    }

    @Test
    public void updateActionColumn() throws VetoException {
        final ActionInsertFactCol52 column = new ActionInsertFactCol52();
        column.setFactType("FactType");
        column.setFactField("field");
        column.setHeader("header");
        when(oracle.getFieldType(eq("FactType"),
                                 eq("field"))).thenReturn(DataType.TYPE_STRING);
        dtPresenter.appendColumn(column);
        reset(modellerPresenter);

        final ActionInsertFactCol52 update = new ActionInsertFactCol52();
        update.setFactType("NewType");
        update.setFactField("newField");
        update.setHeader("newHeader");
        when(oracle.getFieldType(eq("NewType"),
                                 eq("newField"))).thenReturn(DataType.TYPE_STRING);

        dtPresenter.updateColumn(column,
                                 update);

        verify(synchronizer,
               times(1)).updateColumn(eq(column),
                                      eq(update));
        verify(modellerPresenter,
               times(1)).updateLinks();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onCutWithSelection() {
        dtPresenter.getUiModel().selectCell(0,
                                            0);

        dtPresenter.onCut();

        verify(clipboard,
               times(1)).setData(any(Set.class));
        // 3 rows created during the setup + 1 selected cell on this test
        verify(lockManager,
               times(4)).acquireLock();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onCutWithoutSelection() {
        dtPresenter.onCut();

        verify(clipboard,
               never()).setData(any(Set.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onCopyWithSelection() {
        dtPresenter.getUiModel().selectCell(0,
                                            0);

        dtPresenter.onCopy();

        verify(clipboard,
               times(1)).setData(any(Set.class));
        // 3 rows created during the setup + 1 selected cell on this test
        verify(lockManager,
               times(4)).acquireLock();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onCopyWithoutSelection() {
        dtPresenter.onCopy();

        verify(clipboard,
               never()).setData(any(Set.class));
    }

    @Test
    public void onPasteWithClipboardDataWithSelection() {
        dtPresenter.getUiModel().selectCell(0,
                                            0);
        dtPresenter.onCopy();

        dtPresenter.getUiModel().selectCell(1,
                                            0);
        dtPresenter.onPaste();

        verify(clipboard,
               times(1)).getData();
        // 3 rows created during the setup + 2 selected cells on this test
        verify(lockManager,
               times(5)).acquireLock();
    }

    @Test
    public void onPasteWithClipboardDataWithoutSelection() {
        dtPresenter.onPaste();

        verify(clipboard,
               never()).getData();
    }

    @Test
    public void onDeleteSelectedCellsWithSelection() {
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           1);

        final ArgumentCaptor<Integer> columnIndexCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<GridData.Range> rowRangeCaptor = ArgumentCaptor.forClass(GridData.Range.class);

        dtPresenter.onDeleteSelectedCells();

        verify(synchronizer,
               times(1)).deleteCell(rowRangeCaptor.capture(),
                                    columnIndexCaptor.capture());
        // 3 rows created during the setup + 1 selected cell on this test
        verify(lockManager,
               times(4)).acquireLock();

        checkDTSelectionsChangedEventFired(2);

        final Integer columnIndex = columnIndexCaptor.getValue();
        final GridData.Range rowRange = rowRangeCaptor.getValue();
        assertEquals(0,
                     rowRange.getMinRowIndex());
        assertEquals(0,
                     rowRange.getMaxRowIndex());
        assertEquals(1,
                     columnIndex.intValue());
    }

    @Test
    public void onDeleteSelectedCellsWithSelectionWithBooleanColumn() {
        final AttributeCol52 column = new AttributeCol52() {

            {
                setAttribute(Attribute.ENABLED.getAttributeName());
            }
        };
        dtPresenter.appendColumn(column);

        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           3);

        dtPresenter.onDeleteSelectedCells();

        verify(synchronizer,
               never()).deleteCell(any(GridData.Range.class),
                                   any(Integer.class));
        // 3 rows created during the setup + 1 selected cell on this test
        verify(lockManager,
               times(4)).acquireLock();
        checkDTSelectionsChangedEventFired(1);
    }

    @Test
    public void onDeleteSelectedCellsWithSelectionsWithBooleanColumn() {
        final AttributeCol52 column = new AttributeCol52() {

            {
                setAttribute(Attribute.ENABLED.getAttributeName());
            }
        };
        dtPresenter.appendColumn(column);

        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           2);
        uiModel.selectCell(0,
                           3);

        final ArgumentCaptor<Integer> columnIndexCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<GridData.Range> rowRangeCaptor = ArgumentCaptor.forClass(GridData.Range.class);

        dtPresenter.onDeleteSelectedCells();

        verify(synchronizer,
               times(1)).deleteCell(rowRangeCaptor.capture(),
                                    columnIndexCaptor.capture());
        verify(synchronizer,
               never()).deleteCell(any(GridData.Range.class),
                                   eq(3));
        // 3 rows created during the setup + 2 selected cells on this test
        verify(lockManager,
               times(5)).acquireLock();
        checkDTSelectionsChangedEventFired(3);

        final GridCell<?> booleanCell = uiModel.getCell(0,
                                                        3);
        assertNotNull(booleanCell);
        assertFalse((Boolean) booleanCell.getValue().getValue());

        final Integer columnIndex = columnIndexCaptor.getValue();
        final GridData.Range rowRange = rowRangeCaptor.getValue();
        assertEquals(0,
                     rowRange.getMinRowIndex());
        assertEquals(0,
                     rowRange.getMaxRowIndex());
        assertEquals(2,
                     columnIndex.intValue());
    }

    @Test
    public void onDeleteSelectedCellsWithoutSelections() {
        dtPresenter.onDeleteSelectedCells();

        verify(synchronizer,
               never()).deleteCell(any(GridData.Range.class),
                                   any(Integer.class));
    }

    @Test
    public void onDeleteSelectedColumnsWithSelections() throws VetoException {
        final AttributeCol52 column = new AttributeCol52() {

            {
                setAttribute(Attribute.SALIENCE.getAttributeName());
            }
        };
        dtPresenter.appendColumn(column);
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           3);

        dtPresenter.onDeleteSelectedColumns();

        verify(synchronizer,
               times(1)).deleteColumn(eq(column));
        // 3 rows created during the setup + 1 selected cell on this test
        verify(lockManager,
               times(4)).acquireLock();
        checkDTSelectionsChangedEventFired(2);
    }

    @Test
    public void onDeleteSelectedColumnsWithoutSelections() throws VetoException {
        dtPresenter.onDeleteSelectedColumns();

        verify(synchronizer,
               never()).deleteColumn(any(BaseColumn.class));
    }

    @Test
    public void onDeleteSelectedRowsWithSelections() throws VetoException {
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           0);
        uiModel.selectCell(2,
                           0);

        dtPresenter.onDeleteSelectedRows();

        verify(synchronizer,
               times(1)).deleteRow(eq(0));
        verify(synchronizer,
               times(1)).deleteRow(eq(1));

        checkDTSelectionsChangedEventFired(4);
        // 3 rows created during the setup + 2 selected cells on this test
        verify(lockManager,
               times(5)).acquireLock();
    }

    @Test
    public void onDeleteSelectedRowsWithNoSelections() throws VetoException {
        dtPresenter.onDeleteSelectedRows();

        verify(synchronizer,
               never()).deleteRow(any(Integer.class));
    }

    @Test
    public void onInsertRowAboveNoRowSelected() throws VetoException {
        dtPresenter.onInsertRowAbove();

        verify(synchronizer,
               never()).insertRow(any(Integer.class));
    }

    @Test
    public void onInsertRowAboveSingleRowSelected() throws VetoException {
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           0);

        dtPresenter.onInsertRowAbove();

        verify(synchronizer,
               times(1)).insertRow(eq(0));
        // 3 rows created during the setup + 1 selected cell on this test
        verify(lockManager,
               times(4)).acquireLock();
    }

    @Test
    public void onInsertRowAboveMultipleRowsSelected() throws VetoException {
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           0);
        uiModel.selectCell(1,
                           0);

        dtPresenter.onInsertRowAbove();

        verify(synchronizer,
               never()).insertRow(any(Integer.class));
        // 3 rows created during the setup + 2 selected cells on this test
        verify(lockManager,
               times(5)).acquireLock();
    }

    @Test
    public void onInsertRowBelowNoRowSelected() throws VetoException {
        dtPresenter.onInsertRowBelow();

        verify(synchronizer,
               never()).insertRow(any(Integer.class));
    }

    @Test
    public void onInsertRowBelowSingleRowSelected() throws VetoException {
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           0);

        dtPresenter.onInsertRowBelow();

        verify(synchronizer,
               times(1)).insertRow(eq(1));
        // 3 rows created during the setup + 1 selected cell on this test
        verify(lockManager,
               times(4)).acquireLock();
    }

    @Test
    public void onInsertRowBelowMultipleRowsSelected() throws VetoException {
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           0);
        uiModel.selectCell(1,
                           0);

        dtPresenter.onInsertRowBelow();

        verify(synchronizer,
               never()).insertRow(any(Integer.class));
        // 3 rows created during the setup + 2 selected cells on this test
        verify(lockManager,
               times(5)).acquireLock();
    }

    @Test
    public void onOtherwiseCellNoCellSelected() throws VetoException {
        dtPresenter.onOtherwiseCell();

        verify(synchronizer,
               never()).setCellOtherwiseState(any(Integer.class),
                                              any(Integer.class));
    }

    @Test
    public void onOtherwiseCellSingleCellSelected() throws VetoException {
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           0);

        dtPresenter.onOtherwiseCell();

        verify(synchronizer,
               times(1)).setCellOtherwiseState(eq(0),
                                               eq(0));
        // 3 rows created during the setup + 1 selected cell on this test
        verify(lockManager,
               times(4)).acquireLock();
    }

    @Test
    public void onOtherwiseCellMultipleCellsSelected() throws VetoException {
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           0);
        uiModel.selectCell(1,
                           0);

        dtPresenter.onOtherwiseCell();

        verify(synchronizer,
               never()).setCellOtherwiseState(any(Integer.class),
                                              any(Integer.class));
        // 3 rows created during the setup + 2 selected cells on this test
        verify(lockManager,
               times(5)).acquireLock();
    }

    @Test
    public void setMergedTrue() {
        reset(gridLayer);

        dtPresenter.setMerged(true);

        assertTrue(dtPresenter.isMerged());

        verify(gridLayer,
               times(1)).draw();
    }

    @Test
    public void setMergedFalse() {
        reset(gridLayer);

        dtPresenter.setMerged(false);

        assertFalse(dtPresenter.isMerged());

        verify(gridLayer,
               times(1)).draw();
    }

    @Test
    public void testAddOnEnterPinnedModeCommand() {

        final GuidedDecisionTableModellerView.Presenter parent = mock(GuidedDecisionTableModellerView.Presenter.class);
        final Command command = mock(Command.class);

        doReturn(parent).when(dtPresenter).getParent();

        dtPresenter.addOnEnterPinnedModeCommand(command);

        verify(parent).addOnEnterPinnedModeCommand(command);
    }

    @Test
    public void testAddOnExitPinnedModeCommand() {

        final GuidedDecisionTableModellerView.Presenter parent = mock(GuidedDecisionTableModellerView.Presenter.class);
        final Command command = mock(Command.class);

        doReturn(parent).when(dtPresenter).getParent();

        dtPresenter.addOnExitPinnedModeCommand(command);

        verify(parent).addOnExitPinnedModeCommand(command);
    }

    @Test
    public void testRefreshView() {
        final GuidedDecisionTableModellerView.Presenter parent = mock(GuidedDecisionTableModellerView.Presenter.class);
        final Layer layer = mock(Layer.class);

        doReturn(parent).when(dtPresenter).getParent();
        doReturn(layer).when(view).getLayer();

        dtPresenter.refreshView();

        verify(parent).updateLinks();
        verify(parent).refreshScrollPosition();
        verify(layer).draw();
    }

    @Test
    public void testInitialiseAccessReadOnlyTableWithEditableColumns() {
        testInitialiseAccess(true, true);
    }

    @Test
    public void testInitialiseAccessReadOnlyTableWithoutEditableColumns() {
        testInitialiseAccess(true, false);
    }

    @Test
    public void testInitialiseAccessEditableTableWithEditableColumns() {
        testInitialiseAccess(false, true);
    }

    @Test
    public void testInitialiseAccessEditableTableWithoutEditableColumns() {
        testInitialiseAccess(false, false);
    }

    @Test
    public void testCanEditColumns() {

        final String permission = WorkbenchFeatures.GUIDED_DECISION_TABLE_EDIT_COLUMNS;
        final User user = mock(User.class);

        doReturn(user).when(sessionInfo).getIdentity();

        dtPresenter.canEditColumns();

        verify(authorizationManager).authorize(permission, user);
    }

    @Test
    public void testHasEditableColumns() {

        final GuidedDecisionTablePresenter.Access access = mock(GuidedDecisionTablePresenter.Access.class);

        doReturn(access).when(dtPresenter).getAccess();
        doReturn(true).when(access).hasEditableColumns();

        final boolean hasEditableColumns = dtPresenter.hasEditableColumns();

        assertTrue(hasEditableColumns);
        verify(access).hasEditableColumns();
    }

    @Test
    public void testRefreshColumnsPage() {
        dtPresenter.refreshColumnsPage();

        verify(refreshAttributesPanelEvent).fire(any(RefreshAttributesPanelEvent.class));
        verify(refreshMetaDataPanelEvent).fire(any(RefreshMetaDataPanelEvent.class));
        verify(refreshConditionsPanelEvent).fire(any(RefreshConditionsPanelEvent.class));
        verify(refreshActionsPanelEvent).fire(any(RefreshActionsPanelEvent.class));
    }

    @Test
    public void testRefreshMenus() {
        dtPresenter.refreshMenus();

        verify(refreshMenusEvent).fire(any(RefreshMenusEvent.class));
    }

    @Test
    public void testOnSearchPerformed() {
        final GuidedDecisionTableSearchableElement element = mock(GuidedDecisionTableSearchableElement.class);
        when(element.getModel()).thenReturn(model);
        final SearchPerformedEvent event = new SearchPerformedEvent(element);

        dtPresenter.onSearchPerformed(event);

        verify(renderer, never()).clearCellHighlight();
        verify(view).draw();
    }

    @Test
    public void testOnSearchPerformedModelNotFromThisTable() {
        // This test is for GDT Graphs
        final GuidedDecisionTableSearchableElement element = mock(GuidedDecisionTableSearchableElement.class);
        final GuidedDecisionTable52 anotherModel = mock(GuidedDecisionTable52.class);
        when(element.getModel()).thenReturn(anotherModel);
        when(element.getColumn()).thenReturn(0);
        when(element.getRow()).thenReturn(0);
        final SearchPerformedEvent event = new SearchPerformedEvent(element);

        dtPresenter.onSearchPerformed(event);

        verify(renderer).clearCellHighlight();
    }

    @Test
    public void testSort() throws VetoException {
        reset(gridLayer);

        dtPresenter.onSort(uiModel2MockColumn);

        verify(uiModel).sort(uiModel2MockColumn);
        verify(gridLayer).draw();
        verify(analyzerController).sort(listArgumentCaptor.capture());

        final List<Integer> value = listArgumentCaptor.getValue();
        Assertions.assertThat(value).containsExactly(0, 1, 2);
    }

    @Test
    public void testSortReversely() throws VetoException {
        reset(gridLayer);

        // asc
        dtPresenter.onSort(uiModel2MockColumn);
        reset(uiModel);
        reset(gridLayer);
        reset(analyzerController);
        // desc
        dtPresenter.onSort(uiModel2MockColumn);

        verify(uiModel).sort(uiModel2MockColumn);
        verify(gridLayer).draw();
        verify(analyzerController).sort(listArgumentCaptor.capture());

        final List<Integer> value = listArgumentCaptor.getValue();
        Assertions.assertThat(value).containsExactly(2, 1, 0);
    }

    /*
     * check that valid DT selections change events are fired the correct number of times for a test case
     */
    private void checkDTSelectionsChangedEventFired(int times) {
        final ArgumentCaptor<DecisionTableSelectionsChangedEvent> dtSelectionsChangedEventCaptor = ArgumentCaptor.forClass(DecisionTableSelectionsChangedEvent.class);

        verify(decisionTableSelectionsChangedEvent,
               times(times)).fire(dtSelectionsChangedEventCaptor.capture());

        final List<DecisionTableSelectionsChangedEvent> dtSelectionsChangedEvents = dtSelectionsChangedEventCaptor.getAllValues();

        assertNotNull(dtSelectionsChangedEvents);
        assertEquals(dtSelectionsChangedEvents.size(),
                     times);
        dtSelectionsChangedEvents.stream().map(DecisionTableSelectionsChangedEvent::getPresenter).forEach(p -> assertEquals("Invalid DecisionTableSelectionsChangedEvent detected.",
                                                                                                                            p,
                                                                                                                            dtPresenter));
    }

    private void testInitialiseAccess(final boolean readOnly, final boolean canEditColumns) {
        final GuidedDecisionTablePresenter.Access access = mock(GuidedDecisionTablePresenter.Access.class);

        doReturn(access).when(dtPresenter).getAccess();
        doReturn(canEditColumns).when(dtPresenter).canEditColumns();

        dtPresenter.initialiseAccess(readOnly);

        verify(access).setReadOnly(readOnly);
        verify(access).setHasEditableColumns(canEditColumns);
    }

    @Override
    protected GuidedDecisionTablePresenter makeGuidedDecisionTablePresenterMock() {
        return new GuidedDecisionTablePresenterMock(identity,
                                                    resourceType,
                                                    ruleNameServiceCaller,
                                                    decisionTableSelectedEvent,
                                                    decisionTableColumnSelectedEvent,
                                                    decisionTableSelectionsChangedEvent,
                                                    refreshAttributesPanelEvent,
                                                    refreshMetaDataPanelEvent,
                                                    refreshConditionsPanelEvent,
                                                    refreshActionsPanelEvent,
                                                    refreshMenusEvent,
                                                    notificationEvent,
                                                    gridWidgetCellFactory,
                                                    gridWidgetColumnFactory,
                                                    oracleFactory,
                                                    synchronizer,
                                                    beanManager,
                                                    lockManager,
                                                    linkManager,
                                                    clipboard,
                                                    decisionTableAnalyzerProvider,
                                                    enumLoaderUtilities,
                                                    pluginHandler,
                                                    authorizationManager,
                                                    sessionInfo);
    }

    class GuidedDecisionTablePresenterMock extends GuidedDecisionTablePresenterBaseMock {

        public GuidedDecisionTablePresenterMock(final User identity,
                                                final GuidedDTableResourceType resourceType,
                                                final Caller<RuleNamesService> ruleNameService,
                                                final Event<DecisionTableSelectedEvent> decisionTableSelectedEvent,
                                                final Event<DecisionTableColumnSelectedEvent> decisionTableColumnSelectedEvent,
                                                final Event<DecisionTableSelectionsChangedEvent> decisionTableSelectionsChangedEvent,
                                                final Event<RefreshAttributesPanelEvent> refreshAttributesPanelEvent,
                                                final Event<RefreshMetaDataPanelEvent> refreshMetaDataPanelEvent,
                                                final Event<RefreshConditionsPanelEvent> refreshConditionsPanelEvent,
                                                final Event<RefreshActionsPanelEvent> refreshActionsPanelEvent,
                                                final Event<RefreshMenusEvent> refreshMenusEvent,
                                                final Event<NotificationEvent> notificationEvent,
                                                final GridWidgetCellFactory gridWidgetCellFactory,
                                                final GridWidgetColumnFactory gridWidgetColumnFactory,
                                                final AsyncPackageDataModelOracleFactory oracleFactory,
                                                final ModelSynchronizer synchronizer,
                                                final SyncBeanManager beanManager,
                                                final GuidedDecisionTableLockManager lockManager,
                                                final GuidedDecisionTableLinkManager linkManager,
                                                final Clipboard clipboard,
                                                final DecisionTableAnalyzerProvider decisionTableAnalyzerProvider,
                                                final EnumLoaderUtilities enumLoaderUtilities,
                                                final PluginHandler pluginHandler,
                                                final AuthorizationManager authorizationManager,
                                                final SessionInfo sessionInfo) {
            super(identity,
                  resourceType,
                  ruleNameService,
                  decisionTableSelectedEvent,
                  decisionTableColumnSelectedEvent,
                  decisionTableSelectionsChangedEvent,
                  refreshAttributesPanelEvent,
                  refreshMetaDataPanelEvent,
                  refreshConditionsPanelEvent,
                  refreshActionsPanelEvent,
                  refreshMenusEvent,
                  notificationEvent,
                  gridWidgetCellFactory,
                  gridWidgetColumnFactory,
                  oracleFactory,
                  synchronizer,
                  beanManager,
                  lockManager,
                  linkManager,
                  clipboard,
                  decisionTableAnalyzerProvider,
                  enumLoaderUtilities,
                  pluginHandler,
                  authorizationManager,
                  sessionInfo);
        }

        @Override
        GuidedDecisionTableUiModel makeUiModel() {
            uiModel = spy(super.makeUiModel());
            return uiModel;
        }
    }
}
