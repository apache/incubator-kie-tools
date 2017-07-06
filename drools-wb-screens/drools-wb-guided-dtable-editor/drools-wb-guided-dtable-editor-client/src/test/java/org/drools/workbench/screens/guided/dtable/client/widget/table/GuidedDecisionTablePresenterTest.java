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

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.IssueSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableColumnSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshActionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshAttributesPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshConditionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.DependentEnumsUtilities;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableLinkManager.LinkFoundCallback;
import org.drools.workbench.screens.guided.rule.client.editor.RuleAttributeWidget;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.api.client.reporting.Severity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.uberfire.backend.vfs.ObservablePath;
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

import static org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter.Access.LockedBy.CURRENT_USER;
import static org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter.Access.LockedBy.NOBODY;
import static org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter.Access.LockedBy.OTHER_USER;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

        verify(modellerPresenter,
               times(1)).onLockStatusUpdated(eq(dtPresenter));
        assertEquals(CURRENT_USER,
                     dtPresenter.getAccess().getLock());
    }

    @Test
    public void testOnUpdatedLockStatusEvent_LockedByOtherUser() {
        final UpdatedLockStatusEvent event = mock(UpdatedLockStatusEvent.class);
        when(event.getFile()).thenReturn(dtPath);
        when(event.isLockedByCurrentUser()).thenReturn(false);
        when(event.isLocked()).thenReturn(true);

        dtPresenter.onUpdatedLockStatusEvent(event);

        verify(modellerPresenter,
               times(1)).onLockStatusUpdated(eq(dtPresenter));
        assertEquals(OTHER_USER,
                     dtPresenter.getAccess().getLock());
    }

    @Test
    public void testOnUpdatedLockStatusEvent_NotLocked() {
        final UpdatedLockStatusEvent event = mock(UpdatedLockStatusEvent.class);
        when(event.getFile()).thenReturn(dtPath);
        dtPresenter.onUpdatedLockStatusEvent(event);

        verify(modellerPresenter,
               times(1)).onLockStatusUpdated(eq(dtPresenter));
        assertEquals(NOBODY,
                     dtPresenter.getAccess().getLock());
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
               times(1)).highlightRows(any(Severity.class),
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
        final Set<GuidedDecisionTableView.Presenter> dtPresenters = new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(dtPresenter);
            add(dtPresenter2);
            add(dtPresenter3);
        }};
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
               atLeast(1)).get(eq(2));
        verify(uiModel2Columns,
               atLeast(1)).get(eq(2));
        verify(uiModel3Columns,
               atLeast(1)).get(eq(2));

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
        final Set<GuidedDecisionTableView.Presenter> dtPresenters = new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(dtPresenter);
            add(dtPresenter2);
            add(dtPresenter3);
        }};

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
        BRLActionVariableColumn variableColumn = new BRLActionVariableColumn(null,
                                                                             null,
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
        verify(lockManager,
               times(1)).acquireLock();
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

        when(ruleNameService.getRuleNames(any(ObservablePath.class),
                                          any(String.class))).thenReturn(parentRuleNames);

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
        attribute.setAttribute("attribute");

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
    public void canConditionBeDeletedWithSingleChildColumnWithAction() {
        final Pattern52 pattern = new Pattern52();
        pattern.setFactType("FactType1");
        pattern.setBoundName("$fact");
        final ConditionCol52 condition1 = new ConditionCol52();
        condition1.setFactField("field1");
        pattern.getChildColumns().add(condition1);

        dtPresenter.getModel().getConditions().add(pattern);

        final ActionInsertFactCol52 action = new ActionInsertFactCol52();
        action.setFactType("FactType1");
        action.setBoundName("$fact");
        action.setFactField("field");

        dtPresenter.getModel().getActionCols().add(action);

        assertFalse(dtPresenter.canConditionBeDeleted(condition1));
    }

    @Test
    public void canConditionBeDeletedWithSingleChildColumnWithNoAction() {
        final Pattern52 pattern = new Pattern52();
        pattern.setFactType("FactType1");
        final ConditionCol52 condition1 = new ConditionCol52();
        condition1.setFactField("field1");
        pattern.getChildColumns().add(condition1);

        dtPresenter.getModel().getConditions().add(pattern);

        assertTrue(dtPresenter.canConditionBeDeleted(condition1));
    }

    @Test
    public void canConditionBeDeletedWithMultipleChildColumns() {
        final Pattern52 pattern = new Pattern52();
        pattern.setFactType("FactType1");
        final ConditionCol52 condition1 = new ConditionCol52();
        condition1.setFactField("field1");
        pattern.getChildColumns().add(condition1);
        final ConditionCol52 condition2 = new ConditionCol52();
        condition2.setFactField("field2");
        pattern.getChildColumns().add(condition2);

        dtPresenter.getModel().getConditions().add(pattern);

        assertTrue(dtPresenter.canConditionBeDeleted(condition1));
    }

    @Test
    public void canBRLFragmentConditionBeDeletedWithAction() {
        final FactPattern fp = new FactPattern("FactType1");
        fp.setBoundName("$fact");
        final BRLConditionColumn column = new BRLConditionColumn();
        column.getDefinition().add(fp);

        dtPresenter.getModel().getConditions().add(column);

        final ActionInsertFactCol52 action = new ActionInsertFactCol52();
        action.setFactType("FactType1");
        action.setBoundName("$fact");
        action.setFactField("field");

        dtPresenter.getModel().getActionCols().add(action);

        assertFalse(dtPresenter.canConditionBeDeleted(column));
    }

    @Test
    public void canBRLFragmentConditionBeDeletedWithNoAction() {
        final FactPattern fp = new FactPattern("FactType1");
        fp.setBoundName("$fact");
        final BRLConditionColumn column = new BRLConditionColumn();
        column.getDefinition().add(fp);

        dtPresenter.getModel().getConditions().add(column);

        assertTrue(dtPresenter.canConditionBeDeleted(column));
    }

    @Test
    public void getValueListLookups() {
        final AttributeCol52 attribute = new AttributeCol52();
        attribute.setAttribute(RuleAttributeWidget.ENABLED_ATTR);

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
    public void appendPatternAndConditionColumn() throws ModelSynchronizer.MoveColumnVetoException {
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
    public void appendConditionColumn() throws ModelSynchronizer.MoveColumnVetoException {
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
    public void appendActionColumn() throws ModelSynchronizer.MoveColumnVetoException {
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
    public void appendRow() throws ModelSynchronizer.MoveColumnVetoException {
        reset(synchronizer,
              modellerPresenter);

        dtPresenter.onAppendRow();

        verify(synchronizer,
               times(1)).appendRow();
        verify(modellerPresenter,
               times(1)).updateLinks();
    }

    @Test
    public void deleteConditionColumn() throws ModelSynchronizer.MoveColumnVetoException {
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
    }

    @Test
    public void deleteActionColumn() throws ModelSynchronizer.MoveColumnVetoException {
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
    }

    @Test
    public void deleteAttributeColumn() throws Exception {
        final AttributeCol52 column = new AttributeCol52();
        column.setAttribute("salience");
        dtPresenter.appendColumn(column);
        reset(modellerPresenter);

        dtPresenter.deleteColumn(column);

        verify(synchronizer,
               times(1)).deleteColumn(eq(column));
        verify(modellerPresenter,
               times(1)).updateLinks();

        verify(refreshAttributesPanelEvent,
               times(2)).fire(any(RefreshAttributesPanelEvent.class));
    }

    @Test
    public void updatePatternAndConditionColumn() throws ModelSynchronizer.MoveColumnVetoException {
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
    public void updateConditionColumn() throws ModelSynchronizer.MoveColumnVetoException {
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
    public void updateActionColumn() throws ModelSynchronizer.MoveColumnVetoException {
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
        final AttributeCol52 column = new AttributeCol52() {{
            setAttribute(RuleAttributeWidget.ENABLED_ATTR);
        }};
        dtPresenter.appendColumn(column);

        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           2);

        dtPresenter.onDeleteSelectedCells();

        verify(synchronizer,
               never()).deleteCell(any(GridData.Range.class),
                                   any(Integer.class));
    }

    @Test
    public void onDeleteSelectedCellsWithSelectionsWithBooleanColumn() {
        final AttributeCol52 column = new AttributeCol52() {{
            setAttribute(RuleAttributeWidget.ENABLED_ATTR);
        }};
        dtPresenter.appendColumn(column);

        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           1);
        uiModel.selectCell(0,
                           2);

        final ArgumentCaptor<Integer> columnIndexCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<GridData.Range> rowRangeCaptor = ArgumentCaptor.forClass(GridData.Range.class);

        dtPresenter.onDeleteSelectedCells();

        verify(synchronizer,
               times(1)).deleteCell(rowRangeCaptor.capture(),
                                    columnIndexCaptor.capture());
        verify(synchronizer,
               never()).deleteCell(any(GridData.Range.class),
                                   eq(2));
        final GridCell<?> booleanCell = uiModel.getCell(0,
                                                        2);
        assertNotNull(booleanCell);
        assertFalse((Boolean) booleanCell.getValue().getValue());

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
    public void onDeleteSelectedCellsWithoutSelections() {
        dtPresenter.onDeleteSelectedCells();

        verify(synchronizer,
               never()).deleteCell(any(GridData.Range.class),
                                   any(Integer.class));
    }

    @Test
    public void onDeleteSelectedColumnsWithSelections() throws ModelSynchronizer.MoveColumnVetoException {
        final AttributeCol52 column = new AttributeCol52() {{
            setAttribute("attribute1");
        }};
        dtPresenter.appendColumn(column);
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           2);

        dtPresenter.onDeleteSelectedColumns();

        verify(synchronizer,
               times(1)).deleteColumn(eq(column));
    }

    @Test
    public void onDeleteSelectedColumnsWithoutSelections() throws ModelSynchronizer.MoveColumnVetoException {
        dtPresenter.onDeleteSelectedColumns();

        verify(synchronizer,
               never()).deleteColumn(any(BaseColumn.class));
    }

    @Test
    public void onDeleteSelectedRowsWithSelections() throws ModelSynchronizer.MoveColumnVetoException {
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
    }

    @Test
    public void onDeleteSelectedRowsWithNoSelections() throws ModelSynchronizer.MoveColumnVetoException {
        dtPresenter.onDeleteSelectedRows();

        verify(synchronizer,
               never()).deleteRow(any(Integer.class));
    }

    @Test
    public void onInsertRowAboveNoRowSelected() throws ModelSynchronizer.MoveColumnVetoException {
        dtPresenter.onInsertRowAbove();

        verify(synchronizer,
               never()).insertRow(any(Integer.class));
    }

    @Test
    public void onInsertRowAboveSingleRowSelected() throws ModelSynchronizer.MoveColumnVetoException {
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           0);

        dtPresenter.onInsertRowAbove();

        verify(synchronizer,
               times(1)).insertRow(eq(0));
    }

    @Test
    public void onInsertRowAboveMultipleRowsSelected() throws ModelSynchronizer.MoveColumnVetoException {
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           0);
        uiModel.selectCell(1,
                           0);

        dtPresenter.onInsertRowAbove();

        verify(synchronizer,
               never()).insertRow(any(Integer.class));
    }

    @Test
    public void onInsertRowBelowNoRowSelected() throws ModelSynchronizer.MoveColumnVetoException {
        dtPresenter.onInsertRowBelow();

        verify(synchronizer,
               never()).insertRow(any(Integer.class));
    }

    @Test
    public void onInsertRowBelowSingleRowSelected() throws ModelSynchronizer.MoveColumnVetoException {
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           0);

        dtPresenter.onInsertRowBelow();

        verify(synchronizer,
               times(1)).insertRow(eq(1));
    }

    @Test
    public void onInsertRowBelowMultipleRowsSelected() throws ModelSynchronizer.MoveColumnVetoException {
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           0);
        uiModel.selectCell(1,
                           0);

        dtPresenter.onInsertRowBelow();

        verify(synchronizer,
               never()).insertRow(any(Integer.class));
    }

    @Test
    public void onOtherwiseCellNoCellSelected() throws ModelSynchronizer.MoveColumnVetoException {
        dtPresenter.onOtherwiseCell();

        verify(synchronizer,
               never()).setCellOtherwiseState(any(Integer.class),
                                              any(Integer.class));
    }

    @Test
    public void onOtherwiseCellSingleCellSelected() throws ModelSynchronizer.MoveColumnVetoException {
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           0);

        dtPresenter.onOtherwiseCell();

        verify(synchronizer,
               times(1)).setCellOtherwiseState(eq(0),
                                               eq(0));
    }

    @Test
    public void onOtherwiseCellMultipleCellsSelected() throws ModelSynchronizer.MoveColumnVetoException {
        final GridData uiModel = dtPresenter.getUiModel();
        uiModel.selectCell(0,
                           0);
        uiModel.selectCell(1,
                           0);

        dtPresenter.onOtherwiseCell();

        verify(synchronizer,
               never()).setCellOtherwiseState(any(Integer.class),
                                              any(Integer.class));
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
}
