/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddDecisionRuleCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteDecisionRuleCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteInputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteOutputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetBuiltinAggregatorCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetHitPolicyCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetOrientationCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHeaderValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHeaderValueCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyEditorView;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormProperties;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.GridFactoryCommandUtils.assertCommands;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DecisionTableGridTest {

    private final static int DEFAULT_INSERT_RULE_ABOVE = 0;

    private final static int DEFAULT_INSERT_RULE_BELOW = 1;

    private final static int DEFAULT_DELETE_RULE = 2;

    private final static int INSERT_COLUMN_BEFORE = 0;

    private final static int INSERT_COLUMN_AFTER = 1;

    private final static int DELETE_COLUMN = 2;

    private final static int DIVIDER = 3;

    private static final String HASNAME_NAME = "name";

    private static final String NODE_UUID = "uuid";

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private AbsolutePanel gridLayerDomElementContainer;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private DMNSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Index index;

    @Mock
    private Element element;

    @Mock
    private GraphCommandExecutionContext graphCommandContext;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private TranslationService translationService;

    @Mock
    private HitPolicyEditorView.Presenter hitPolicyEditor;

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private Command command;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private EventSourceMock<RefreshFormProperties> refreshFormPropertiesEvent;

    @Captor
    private ArgumentCaptor<DeleteInputClauseCommand> deleteInputClauseCommandCaptor;

    @Captor
    private ArgumentCaptor<DeleteOutputClauseCommand> deleteOutputClauseCommandCaptor;

    @Captor
    private ArgumentCaptor<AddDecisionRuleCommand> addDecisionRuleCommandCaptor;

    @Captor
    private ArgumentCaptor<DeleteDecisionRuleCommand> deleteDecisionRuleCommandCaptor;

    @Captor
    private ArgumentCaptor<CompositeCommand<AbstractCanvasHandler, CanvasViolation>> setHitPolicyCommandCaptor;

    @Captor
    private ArgumentCaptor<SetBuiltinAggregatorCommand> setBuiltInAggregatorCommandCaptor;

    @Captor
    private ArgumentCaptor<SetOrientationCommand> setOrientationCommandCaptor;

    @Captor
    private ArgumentCaptor<GridLayerRedrawManager.PrioritizedCommand> redrawCommandCaptor;

    private Optional<DecisionTable> expression = Optional.empty();

    private DecisionTableEditorDefinition definition;

    private DecisionTableGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getGridPanel()).thenReturn(gridPanel);
        when(session.getGridLayer()).thenReturn(gridLayer);
        when(session.getCellEditorControls()).thenReturn(cellEditorControls);

        this.definition = new DecisionTableEditorDefinition(definitionUtils,
                                                            sessionManager,
                                                            sessionCommandManager,
                                                            canvasCommandFactory,
                                                            editorSelectedEvent,
                                                            refreshFormPropertiesEvent,
                                                            listSelector,
                                                            translationService,
                                                            hitPolicyEditor,
                                                            new DecisionTableEditorDefinitionEnricher(sessionManager,
                                                                                                      new DMNGraphUtils(sessionManager)));

        expression = definition.getModelClass();
        definition.enrich(Optional.empty(), expression);

        doReturn(canvasHandler).when(session).getCanvasHandler();
        doReturn(graphCommandContext).when(canvasHandler).getGraphExecutionContext();

        when(gridWidget.getModel()).thenReturn(new BaseGridData(false));
        when(gridLayer.getDomElementContainer()).thenReturn(gridLayerDomElementContainer);
        when(gridLayerDomElementContainer.iterator()).thenReturn(mock(Iterator.class));
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, 1000, 2000));
        when(gridLayer.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(transform);

        when(canvasHandler.getGraphIndex()).thenReturn(index);
        when(index.get(anyString())).thenReturn(element);
        when(element.getContent()).thenReturn(mock(Definition.class));
        when(definitionUtils.getNameIdentifier(any())).thenReturn("name");
        when(canvasCommandFactory.updatePropertyValue(any(Element.class),
                                                      anyString(),
                                                      any())).thenReturn(mock(UpdateElementPropertyCommand.class));

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(anyString());
    }

    private void setupGrid(final Optional<HasName> hasName,
                           final int nesting) {
        this.grid = spy((DecisionTableGrid) definition.getEditor(parent,
                                                                 nesting == 0 ? Optional.of(NODE_UUID) : Optional.empty(),
                                                                 hasExpression,
                                                                 expression,
                                                                 hasName,
                                                                 nesting).get());
    }

    private Optional<HasName> makeHasNameForDecision() {
        final Decision decision = new Decision();
        decision.setName(new Name(HASNAME_NAME));
        return Optional.of(decision);
    }

    @Test
    public void testInitialSetupFromDefinition() {
        setupGrid(makeHasNameForDecision(), 0);

        final GridData uiModel = grid.getModel();
        assertTrue(uiModel instanceof DecisionTableGridData);

        assertEquals(4,
                     uiModel.getColumnCount());
        assertTrue(uiModel.getColumns().get(0) instanceof DecisionTableRowNumberColumn);
        assertTrue(uiModel.getColumns().get(1) instanceof InputClauseColumn);
        assertTrue(uiModel.getColumns().get(2) instanceof OutputClauseColumn);
        assertTrue(uiModel.getColumns().get(3) instanceof DescriptionColumn);

        assertEquals(1,
                     uiModel.getRowCount());

        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_UNARY_TEST_TEXT,
                     uiModel.getCell(0, 1).getValue().getValue());
        assertEquals(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT,
                     uiModel.getCell(0, 2).getValue().getValue());
        assertEquals(DecisionTableDefaultValueUtilities.RULE_DESCRIPTION,
                     uiModel.getCell(0, 3).getValue().getValue());
    }

    @Test
    public void testHeaderVisibilityWhenNested() {
        setupGrid(Optional.empty(), 1);

        assertFalse(grid.isHeaderHidden());
    }

    @Test
    public void testHeaderVisibilityWhenNotNested() {
        setupGrid(Optional.empty(), 0);

        assertFalse(grid.isHeaderHidden());
    }

    @Test
    public void testCacheable() {
        setupGrid(Optional.empty(), 0);

        assertTrue(grid.isCacheable());
    }

    @Test
    public void testColumn0MetaData() {
        setupGrid(makeHasNameForDecision(), 0);

        final GridColumn<?> column = grid.getModel().getColumns().get(0);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof RowNumberColumnHeaderMetaData);

        final RowNumberColumnHeaderMetaData md = (RowNumberColumnHeaderMetaData) header.get(0);
        expression.get().setHitPolicy(HitPolicy.FIRST);
        assertEquals("F",
                     md.getTitle());

        expression.get().setHitPolicy(HitPolicy.ANY);
        assertEquals("A",
                     md.getTitle());
    }

    @Test
    public void testColumn1MetaData() {
        setupGrid(makeHasNameForDecision(), 0);

        final GridColumn<?> column = grid.getModel().getColumns().get(1);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof InputClauseColumnHeaderMetaData);

        final InputClauseColumnHeaderMetaData md = (InputClauseColumnHeaderMetaData) header.get(0);
        assertEquals(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_PREFIX + "1",
                     md.getTitle());
    }

    @Test
    public void testColumn2MetaData() {
        setupGrid(makeHasNameForDecision(), 0);

        final GridColumn<?> column = grid.getModel().getColumns().get(2);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof OutputClauseColumnExpressionNameHeaderMetaData);

        final OutputClauseColumnExpressionNameHeaderMetaData md = (OutputClauseColumnExpressionNameHeaderMetaData) header.get(0);
        assertEquals(HASNAME_NAME,
                     md.getTitle());
    }

    @Test
    public void testColumn2MetaDataWithoutHasName() {
        setupGrid(Optional.empty(), 0);

        final GridColumn<?> column = grid.getModel().getColumns().get(2);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof BaseHeaderMetaData);

        final BaseHeaderMetaData md = (BaseHeaderMetaData) header.get(0);
        assertEquals(DMNEditorConstants.DecisionTableEditor_OutputClauseHeader,
                     md.getTitle());
    }

    @Test
    public void testColumn3MetaData() {
        setupGrid(makeHasNameForDecision(), 0);

        final GridColumn<?> column = grid.getModel().getColumns().get(3);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof BaseHeaderMetaData);

        final BaseHeaderMetaData md = (BaseHeaderMetaData) header.get(0);
        assertEquals(DMNEditorConstants.DecisionTableEditor_DescriptionColumnHeader,
                     md.getTitle());
    }

    @Test
    public void testGetItemsRowNumberColumn() {
        setupGrid(makeHasNameForDecision(), 0);

        assertDefaultListItems(grid.getItems(0, 0), true);
    }

    @Test
    public void testGetItemsInputClauseColumn() {
        setupGrid(makeHasNameForDecision(), 0);
        mockInsertColumnCommandExecution();

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 1);

        assertThat(items.size()).isEqualTo(7);
        assertDefaultListItems(items.subList(4, 7), true);

        assertListSelectorItem(items.get(INSERT_COLUMN_BEFORE),
                               DMNEditorConstants.DecisionTableEditor_InsertInputClauseBefore,
                               true);
        assertListSelectorItem(items.get(INSERT_COLUMN_AFTER),
                               DMNEditorConstants.DecisionTableEditor_InsertInputClauseAfter,
                               true);
        assertListSelectorItem(items.get(DELETE_COLUMN),
                               DMNEditorConstants.DecisionTableEditor_DeleteInputClause,
                               false);
        assertThat(items.get(DIVIDER)).isInstanceOf(HasListSelectorControl.ListSelectorDividerItem.class);

        grid.onItemSelected(items.get(INSERT_COLUMN_BEFORE));
        verify(grid).addInputClause(eq(1));

        grid.onItemSelected(items.get(INSERT_COLUMN_AFTER));
        verify(grid).addInputClause(eq(2));

        grid.onItemSelected(items.get(DELETE_COLUMN));
        verify(grid).deleteInputClause(eq(1));
    }

    @Test
    public void testGetItemsOutputClauseColumn() {
        setupGrid(makeHasNameForDecision(), 0);
        mockInsertColumnCommandExecution();

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 2);

        assertThat(items.size()).isEqualTo(7);
        assertDefaultListItems(items.subList(4, 7), true);

        assertListSelectorItem(items.get(INSERT_COLUMN_BEFORE),
                               DMNEditorConstants.DecisionTableEditor_InsertOutputClauseBefore,
                               true);
        assertListSelectorItem(items.get(INSERT_COLUMN_AFTER),
                               DMNEditorConstants.DecisionTableEditor_InsertOutputClauseAfter,
                               true);
        assertListSelectorItem(items.get(DELETE_COLUMN),
                               DMNEditorConstants.DecisionTableEditor_DeleteOutputClause,
                               false);
        assertThat(items.get(DIVIDER)).isInstanceOf(HasListSelectorControl.ListSelectorDividerItem.class);

        grid.onItemSelected(items.get(INSERT_COLUMN_BEFORE));
        verify(grid).addOutputClause(eq(2));

        grid.onItemSelected(items.get(INSERT_COLUMN_AFTER));
        verify(grid).addOutputClause(eq(3));

        grid.onItemSelected(items.get(DELETE_COLUMN));
        verify(grid).deleteOutputClause(eq(2));
    }

    @Test
    public void testGetItemsDescriptionColumn() {
        setupGrid(makeHasNameForDecision(), 0);

        assertDefaultListItems(grid.getItems(0, 3), true);
    }

    @Test
    public void testGetItemsWithCellSelectionsCoveringMultipleRows() {
        setupGrid(makeHasNameForDecision(), 0);

        addDecisionRule(0);
        grid.getModel().selectCell(0, 0);
        grid.getModel().selectCell(1, 0);

        assertDefaultListItems(grid.getItems(0, 0), false);
    }

    @Test
    public void testGetItemsInputClauseColumnWithCellSelectionsCoveringMultipleColumns() {
        setupGrid(makeHasNameForDecision(), 0);

        addInputClause(1);
        grid.getModel().selectCell(0, 0);
        grid.getModel().selectCell(0, 1);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 1);

        assertThat(items.size()).isEqualTo(7);
        assertDefaultListItems(items.subList(4, 7), true);

        assertListSelectorItem(items.get(INSERT_COLUMN_BEFORE),
                               DMNEditorConstants.DecisionTableEditor_InsertInputClauseBefore,
                               false);
        assertListSelectorItem(items.get(INSERT_COLUMN_AFTER),
                               DMNEditorConstants.DecisionTableEditor_InsertInputClauseAfter,
                               false);
        assertListSelectorItem(items.get(DELETE_COLUMN),
                               DMNEditorConstants.DecisionTableEditor_DeleteInputClause,
                               false);
        assertThat(items.get(DIVIDER)).isInstanceOf(HasListSelectorControl.ListSelectorDividerItem.class);
    }

    @Test
    public void testGetItemsOutputClauseColumnWithCellSelectionsCoveringMultipleColumns() {
        setupGrid(makeHasNameForDecision(), 0);

        addOutputClause(2);
        grid.getModel().selectCell(0, 0);
        grid.getModel().selectCell(0, 2);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 2);

        assertThat(items.size()).isEqualTo(7);
        assertDefaultListItems(items.subList(4, 7), true);

        assertListSelectorItem(items.get(INSERT_COLUMN_BEFORE),
                               DMNEditorConstants.DecisionTableEditor_InsertOutputClauseBefore,
                               false);
        assertListSelectorItem(items.get(INSERT_COLUMN_AFTER),
                               DMNEditorConstants.DecisionTableEditor_InsertOutputClauseAfter,
                               false);
        assertListSelectorItem(items.get(DELETE_COLUMN),
                               DMNEditorConstants.DecisionTableEditor_DeleteOutputClause,
                               false);
        assertThat(items.get(DIVIDER)).isInstanceOf(HasListSelectorControl.ListSelectorDividerItem.class);
    }

    private void assertDefaultListItems(final List<HasListSelectorControl.ListSelectorItem> items,
                                        final boolean enabled) {
        assertThat(items.size()).isEqualTo(3);
        assertListSelectorItem(items.get(DEFAULT_INSERT_RULE_ABOVE),
                               DMNEditorConstants.DecisionTableEditor_InsertDecisionRuleAbove,
                               enabled);
        assertListSelectorItem(items.get(DEFAULT_INSERT_RULE_BELOW),
                               DMNEditorConstants.DecisionTableEditor_InsertDecisionRuleBelow,
                               enabled);
        assertListSelectorItem(items.get(DEFAULT_DELETE_RULE),
                               DMNEditorConstants.DecisionTableEditor_DeleteDecisionRule,
                               enabled && grid.getModel().getRowCount() > 1);
    }

    private void assertListSelectorItem(final HasListSelectorControl.ListSelectorItem item,
                                        final String text,
                                        final boolean enabled) {
        assertThat(item).isInstanceOf(HasListSelectorControl.ListSelectorTextItem.class);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) item;
        assertThat(ti.getText()).isEqualTo(text);
        assertThat(ti.isEnabled()).isEqualTo(enabled);
    }

    @Test
    public void testOnItemSelected() {
        setupGrid(makeHasNameForDecision(), 0);

        final Command command = mock(Command.class);
        final HasListSelectorControl.ListSelectorTextItem listSelectorItem = mock(HasListSelectorControl.ListSelectorTextItem.class);
        when(listSelectorItem.getCommand()).thenReturn(command);

        grid.onItemSelected(listSelectorItem);

        verify(command).execute();
    }

    @Test
    public void testOnItemSelectedInsertRowAbove() {
        setupGrid(makeHasNameForDecision(), 0);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DEFAULT_INSERT_RULE_ABOVE);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addDecisionRule(eq(0));
    }

    @Test
    public void testOnItemSelectedInsertRowBelow() {
        setupGrid(makeHasNameForDecision(), 0);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DEFAULT_INSERT_RULE_BELOW);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addDecisionRule(eq(1));
    }

    @Test
    public void testOnItemSelectedDeleteRow() {
        setupGrid(makeHasNameForDecision(), 0);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DEFAULT_DELETE_RULE);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).deleteDecisionRule(eq(0));
    }

    @Test
    public void testAddInputClause() {
        setupGrid(makeHasNameForDecision(), 0);

        addInputClause(1);

        verify(parent).proposeContainingColumnWidth(eq(grid.getWidth() + grid.getPadding() * 2));
        verifyGridPanelRefreshAndEditHeaderCell();
    }

    private void addInputClause(final int index) {
        mockInsertColumnCommandExecution();

        grid.addInputClause(index);
    }

    private void mockInsertColumnCommandExecution() {
        when(sessionCommandManager.execute(eq(canvasHandler),
                                           any(AbstractCanvasGraphCommand.class))).thenAnswer((i) -> {
            final AbstractCanvasHandler handler = (AbstractCanvasHandler) i.getArguments()[0];
            final AbstractCanvasGraphCommand command = (AbstractCanvasGraphCommand) i.getArguments()[1];
            return command.execute(handler);
        });
    }

    @Test
    public void testDeleteInputClause() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.deleteInputClause(1);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              deleteInputClauseCommandCaptor.capture());

        final DeleteInputClauseCommand deleteInputClauseCommand = deleteInputClauseCommandCaptor.getValue();
        deleteInputClauseCommand.execute(canvasHandler);

        verify(parent).proposeContainingColumnWidth(eq(grid.getWidth() + grid.getPadding() * 2));
        verifyGridPanelRefresh();
    }

    @Test
    public void testAddOutputClause() {
        setupGrid(makeHasNameForDecision(), 0);

        addOutputClause(2);

        verify(parent).proposeContainingColumnWidth(eq(grid.getWidth() + grid.getPadding() * 2));
        verifyGridPanelRefreshAndEditHeaderCell();
    }

    private void addOutputClause(final int index) {
        mockInsertColumnCommandExecution();

        grid.addOutputClause(index);
    }

    @Test
    public void testDeleteOutputClause() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.deleteOutputClause(2);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              deleteOutputClauseCommandCaptor.capture());

        final DeleteOutputClauseCommand deleteOutputClauseCommand = deleteOutputClauseCommandCaptor.getValue();
        deleteOutputClauseCommand.execute(canvasHandler);

        verify(parent).proposeContainingColumnWidth(eq(grid.getWidth() + grid.getPadding() * 2));
        verifyGridPanelRefresh();
    }

    @Test
    public void testAddDecisionRule() {
        setupGrid(makeHasNameForDecision(), 0);

        addDecisionRule(0);

        verifyGridPanelRefresh();
    }

    private void addDecisionRule(final int index) {
        grid.addDecisionRule(index);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              addDecisionRuleCommandCaptor.capture());

        final AddDecisionRuleCommand addDecisionRuleCommand = addDecisionRuleCommandCaptor.getValue();
        addDecisionRuleCommand.execute(canvasHandler);
    }

    @Test
    public void testDeleteDecisionRule() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.deleteDecisionRule(0);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              deleteDecisionRuleCommandCaptor.capture());

        final DeleteDecisionRuleCommand deleteDecisionRuleCommand = deleteDecisionRuleCommandCaptor.getValue();
        deleteDecisionRuleCommand.execute(canvasHandler);

        verifyGridPanelRefresh();
    }

    @Test
    public void testSetHitPolicy() {
        final HitPolicy hitPolicy = HitPolicy.ANY;

        setupGrid(makeHasNameForDecision(), 0);

        grid.setHitPolicy(hitPolicy,
                          command);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              setHitPolicyCommandCaptor.capture());

        final CompositeCommand<AbstractCanvasHandler, CanvasViolation> setHitPolicyCommand = setHitPolicyCommandCaptor.getValue();
        assertEquals(2,
                     setHitPolicyCommand.getCommands().size());
        assertTrue(setHitPolicyCommand.getCommands().get(0) instanceof SetBuiltinAggregatorCommand);
        assertTrue(setHitPolicyCommand.getCommands().get(1) instanceof SetHitPolicyCommand);

        setHitPolicyCommand.execute(canvasHandler);

        verify(gridLayer, atLeast(1)).batch();
        verify(command).execute();

        assertEquals(hitPolicy, expression.get().getHitPolicy());
        assertNull(expression.get().getAggregation());
    }

    @Test
    public void testSetBuiltInAggregator() {
        final BuiltinAggregator aggregator = BuiltinAggregator.SUM;

        setupGrid(makeHasNameForDecision(), 0);

        grid.setBuiltinAggregator(aggregator);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              setBuiltInAggregatorCommandCaptor.capture());

        final SetBuiltinAggregatorCommand setBuiltinAggregatorCommand = setBuiltInAggregatorCommandCaptor.getValue();
        setBuiltinAggregatorCommand.execute(canvasHandler);

        verify(gridLayer).batch();
    }

    @Test
    public void testSetDecisionTableOrientation() {
        final DecisionTableOrientation orientation = DecisionTableOrientation.RULE_AS_ROW;

        setupGrid(makeHasNameForDecision(), 0);

        grid.setDecisionTableOrientation(orientation);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              setOrientationCommandCaptor.capture());

        final SetOrientationCommand setOrientationCommand = setOrientationCommandCaptor.getValue();
        setOrientationCommand.execute(canvasHandler);

        verify(gridLayer).batch();
    }

    private void verifyGridPanelRefresh() {
        verify(gridLayer).batch(any(GridLayerRedrawManager.PrioritizedCommand.class));
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
    }

    private void verifyGridPanelRefreshAndEditHeaderCell() {
        verify(gridLayer, times(2)).batch(redrawCommandCaptor.capture());
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();

        final java.util.List<GridLayerRedrawManager.PrioritizedCommand> redrawCommands = redrawCommandCaptor.getAllValues();

        //First call redraws grid following addition of new row
        final GridLayerRedrawManager.PrioritizedCommand redrawCommand0 = redrawCommands.get(0);
        redrawCommand0.execute();

        verify(gridLayer).draw();

        //Second call displays the inline editor for the new row
        final GridLayerRedrawManager.PrioritizedCommand redrawCommand1 = redrawCommands.get(1);
        redrawCommand1.execute();

        verify(gridLayerDomElementContainer).add(any(Widget.class));
    }

    @Test
    public void testBodyTextBoxFactoryWhenNested() {
        setupGrid(makeHasNameForDecision(), 1);

        final GridCellTuple tupleWithoutValue = new GridCellTuple(0, 3, gridWidget);
        final GridCellValueTuple tupleWithValue = new GridCellValueTuple<>(0, 3, gridWidget, new BaseGridCellValue<>("value"));

        final TextBoxSingletonDOMElementFactory factory = grid.getBodyTextBoxFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteCellValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetCellValueCommand.class);
    }

    @Test
    public void testBodyTextBoxFactoryWhenNotNested() {
        setupGrid(makeHasNameForDecision(), 0);

        final GridCellTuple tupleWithoutValue = new GridCellTuple(0, 3, gridWidget);
        final GridCellValueTuple tupleWithValue = new GridCellValueTuple<>(0, 3, gridWidget, new BaseGridCellValue<>("value"));

        final TextBoxSingletonDOMElementFactory factory = grid.getBodyTextBoxFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteCellValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetCellValueCommand.class);
    }

    @Test
    public void testBodyTextAreaFactoryWhenNested() {
        setupGrid(makeHasNameForDecision(), 1);

        final GridCellTuple tupleWithoutValue = new GridCellTuple(0, 1, gridWidget);
        final GridCellValueTuple tupleWithValue = new GridCellValueTuple<>(0, 1, gridWidget, new BaseGridCellValue<>("value"));

        final TextAreaSingletonDOMElementFactory factory = grid.getBodyTextAreaFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteCellValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetCellValueCommand.class);
    }

    @Test
    public void testBodyTextAreaFactoryWhenNotNested() {
        setupGrid(makeHasNameForDecision(), 0);

        final GridCellTuple tupleWithoutValue = new GridCellTuple(0, 1, gridWidget);
        final GridCellValueTuple tupleWithValue = new GridCellValueTuple<>(0, 1, gridWidget, new BaseGridCellValue<>("value"));

        final TextAreaSingletonDOMElementFactory factory = grid.getBodyTextAreaFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteCellValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetCellValueCommand.class);
    }

    @Test
    public void testHeaderTextBoxFactoryWhenNested() {
        setupGrid(makeHasNameForDecision(), 1);

        final GridCellTuple tupleWithoutValue = new GridCellTuple(0, 2, gridWidget);
        final GridCellValueTuple tupleWithValue = new GridCellValueTuple<>(0, 2, gridWidget, new BaseGridCellValue<>("value"));

        final TextBoxSingletonDOMElementFactory factory = grid.getHeaderTextBoxFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteHeaderValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetHeaderValueCommand.class);
    }

    @Test
    public void testHeaderTextBoxFactoryWhenNotNested() {
        setupGrid(makeHasNameForDecision(), 0);

        final GridCellTuple tupleWithoutValue = new GridCellTuple(0, 2, gridWidget);
        final GridCellValueTuple tupleWithValue = new GridCellValueTuple<>(0, 2, gridWidget, new BaseGridCellValue<>("value"));

        final TextBoxSingletonDOMElementFactory factory = grid.getHeaderTextBoxFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteHeaderValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetHeaderValueCommand.class);
    }

    @Test
    public void testHeaderHasNameTextBoxFactoryWhenNested() {
        setupGrid(makeHasNameForDecision(), 1);

        final GridCellTuple tupleWithoutValue = new GridCellTuple(0, 2, gridWidget);
        final GridCellValueTuple tupleWithValue = new GridCellValueTuple<>(0, 2, gridWidget, new BaseGridCellValue<>("value"));

        final TextBoxSingletonDOMElementFactory factory = grid.getHeaderHasNameTextBoxFactory();
        assertCommands(factory.getHasNoValueCommand().apply(tupleWithoutValue),
                       DeleteHeaderValueCommand.class);
        assertCommands(factory.getHasValueCommand().apply(tupleWithValue),
                       SetHeaderValueCommand.class);
    }

    @Test
    public void testHeaderHasNameTextBoxFactoryWhenNotNested() {
        setupGrid(makeHasNameForDecision(), 0);

        final GridCellTuple tupleWithoutValue = new GridCellTuple(0, 2, gridWidget);
        final GridCellValueTuple tupleWithValue = new GridCellValueTuple<>(0, 2, gridWidget, new BaseGridCellValue<>("value"));

        final TextBoxSingletonDOMElementFactory factory = grid.getHeaderHasNameTextBoxFactory();
        assertCommands(factory.getHasNoValueCommand().apply(tupleWithoutValue),
                       DeleteHeaderValueCommand.class, UpdateElementPropertyCommand.class);
        assertCommands(factory.getHasValueCommand().apply(tupleWithValue),
                       SetHeaderValueCommand.class, UpdateElementPropertyCommand.class);
    }

    @Test
    public void testHeaderTextAreaFactoryWhenNested() {
        setupGrid(makeHasNameForDecision(), 1);

        final GridCellTuple tupleWithoutValue = new GridCellTuple(0, 1, gridWidget);
        final GridCellValueTuple tupleWithValue = new GridCellValueTuple<>(0, 1, gridWidget, new BaseGridCellValue<>("value"));

        final TextBoxSingletonDOMElementFactory factory = grid.getHeaderTextBoxFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteHeaderValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetHeaderValueCommand.class);
    }

    @Test
    public void testHeaderTextAreaFactoryWhenNotNested() {
        setupGrid(makeHasNameForDecision(), 0);

        final GridCellTuple tupleWithoutValue = new GridCellTuple(0, 1, gridWidget);
        final GridCellValueTuple tupleWithValue = new GridCellValueTuple<>(0, 1, gridWidget, new BaseGridCellValue<>("value"));

        final TextBoxSingletonDOMElementFactory factory = grid.getHeaderTextBoxFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteHeaderValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetHeaderValueCommand.class);
    }
}
