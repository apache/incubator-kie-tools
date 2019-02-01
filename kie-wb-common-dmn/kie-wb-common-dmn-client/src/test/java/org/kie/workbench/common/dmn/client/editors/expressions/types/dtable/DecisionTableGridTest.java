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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddDecisionRuleCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddInputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddOutputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteDecisionRuleCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteInputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteOutputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetBuiltinAggregatorCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetHitPolicyCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHasNameCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHasNameCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetTypeRefCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.GridFactoryCommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverView;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.NameAndDataTypeHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
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
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DecisionTableGridTest {

    private static final int DEFAULT_INSERT_RULE_ABOVE = 0;

    private static final int DEFAULT_INSERT_RULE_BELOW = 1;

    private static final int DEFAULT_DELETE_RULE = 2;

    private static final int DEFAULT_DUPLICATE_RULE = 3;

    private static final int INSERT_COLUMN_BEFORE = 0;

    private static final int INSERT_COLUMN_AFTER = 1;

    private static final int DELETE_COLUMN = 2;

    private static final int DIVIDER = 3;

    private static final String INPUT_CLAUSE_NAME = "input-1";

    private static final String OUTPUT_CLAUSE_NAME1 = "output-1";

    private static final String OUTPUT_CLAUSE_NAME2 = "output-2";

    private static final String NAME_NEW = "name-new";

    private static final String HASNAME_NAME = "name";

    private static final String NODE_UUID = "uuid";

    private static final int DEFAULT_ROW_NUMBER_COLUMN_INDEX = 0;

    private static final int DEFAULT_INPUT_CLAUSE_COLUMN_INDEX = 1;

    private static final int DEFAULT_OUTPUT_CLAUSE_COLUMN_INDEX = 2;

    private static final int DEFAULT_DESCRIPTION_COLUMN_INDEX = 3;

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
    private UpdateElementPropertyCommand updateElementPropertyCommand;

    @Mock
    private DMNSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private Node node;

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
    private HitPolicyPopoverView.Presenter hitPolicyEditor;

    @Mock
    private NameAndDataTypePopoverView.Presenter headerEditor;

    @Mock
    private GridWidget parentGridWidget;

    @Mock
    private GridData parentGridData;

    @Mock
    private GridColumn parentGridColumn;

    @Mock
    private Command command;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Captor
    private ArgumentCaptor<AddInputClauseCommand> addInputClauseCommandCaptor;

    @Captor
    private ArgumentCaptor<AddOutputClauseCommand> addOutputClauseCommandCaptor;

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
    private ArgumentCaptor<GridLayerRedrawManager.PrioritizedCommand> redrawCommandCaptor;

    @Captor
    private ArgumentCaptor<CompositeCommand> compositeCommandCaptor;

    @Captor
    private ArgumentCaptor<DomainObjectSelectionEvent> domainObjectSelectionEventCaptor;

    private GridCellTuple parent;

    private Decision hasExpression = new Decision();

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
                                                            domainObjectSelectionEvent,
                                                            listSelector,
                                                            translationService,
                                                            hitPolicyEditor,
                                                            headerEditor,
                                                            new DecisionTableEditorDefinitionEnricher(sessionManager,
                                                                                                      new DMNGraphUtils(sessionManager)));

        expression = definition.getModelClass();
        definition.enrich(Optional.empty(), hasExpression, expression);

        doReturn(canvasHandler).when(session).getCanvasHandler();
        doReturn(graphCommandContext).when(canvasHandler).getGraphExecutionContext();
        doReturn(parentGridData).when(parentGridWidget).getModel();
        doReturn(Collections.singletonList(parentGridColumn)).when(parentGridData).getColumns();

        parent = spy(new GridCellTuple(0, 0, parentGridWidget));

        when(gridWidget.getModel()).thenReturn(new BaseGridData(false));
        when(gridLayer.getDomElementContainer()).thenReturn(gridLayerDomElementContainer);
        when(gridLayerDomElementContainer.iterator()).thenReturn(mock(Iterator.class));
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, 1000, 2000));
        when(gridLayer.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(transform);

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(Collections.singletonList(node));

        when(canvasHandler.getGraphIndex()).thenReturn(index);
        when(index.get(anyString())).thenReturn(element);
        when(element.getContent()).thenReturn(mock(Definition.class));
        when(definitionUtils.getNameIdentifier(any())).thenReturn("name");
        when(canvasCommandFactory.updatePropertyValue(any(Element.class),
                                                      anyString(),
                                                      any())).thenReturn(updateElementPropertyCommand);
        when(updateElementPropertyCommand.execute(canvasHandler)).thenReturn(CanvasCommandResultBuilder.SUCCESS);

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(anyString());
        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).getTranslation(anyString());
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
        hasExpression.setName(new Name(HASNAME_NAME));
        return Optional.of(hasExpression);
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

        assertThat(items.size()).isEqualTo(8);
        assertDefaultListItems(items.subList(4, 8), true);

        assertListSelectorItem(items.get(INSERT_COLUMN_BEFORE),
                               DMNEditorConstants.DecisionTableEditor_InsertInputClauseLeft,
                               true);
        assertListSelectorItem(items.get(INSERT_COLUMN_AFTER),
                               DMNEditorConstants.DecisionTableEditor_InsertInputClauseRight,
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

        assertThat(items.size()).isEqualTo(8);
        assertDefaultListItems(items.subList(4, 8), true);

        assertListSelectorItem(items.get(INSERT_COLUMN_BEFORE),
                               DMNEditorConstants.DecisionTableEditor_InsertOutputClauseLeft,
                               true);
        assertListSelectorItem(items.get(INSERT_COLUMN_AFTER),
                               DMNEditorConstants.DecisionTableEditor_InsertOutputClauseRight,
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

        assertThat(items.size()).isEqualTo(8);
        assertDefaultListItems(items.subList(4, 8), true);

        assertListSelectorItem(items.get(INSERT_COLUMN_BEFORE),
                               DMNEditorConstants.DecisionTableEditor_InsertInputClauseLeft,
                               false);
        assertListSelectorItem(items.get(INSERT_COLUMN_AFTER),
                               DMNEditorConstants.DecisionTableEditor_InsertInputClauseRight,
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

        assertThat(items.size()).isEqualTo(8);
        assertDefaultListItems(items.subList(4, 8), true);

        assertListSelectorItem(items.get(INSERT_COLUMN_BEFORE),
                               DMNEditorConstants.DecisionTableEditor_InsertOutputClauseLeft,
                               false);
        assertListSelectorItem(items.get(INSERT_COLUMN_AFTER),
                               DMNEditorConstants.DecisionTableEditor_InsertOutputClauseRight,
                               false);
        assertListSelectorItem(items.get(DELETE_COLUMN),
                               DMNEditorConstants.DecisionTableEditor_DeleteOutputClause,
                               false);
        assertThat(items.get(DIVIDER)).isInstanceOf(HasListSelectorControl.ListSelectorDividerItem.class);
    }

    private void assertDefaultListItems(final List<HasListSelectorControl.ListSelectorItem> items,
                                        final boolean enabled) {
        assertThat(items.size()).isEqualTo(4);
        assertListSelectorItem(items.get(DEFAULT_INSERT_RULE_ABOVE),
                               DMNEditorConstants.DecisionTableEditor_InsertDecisionRuleAbove,
                               enabled);
        assertListSelectorItem(items.get(DEFAULT_INSERT_RULE_BELOW),
                               DMNEditorConstants.DecisionTableEditor_InsertDecisionRuleBelow,
                               enabled);
        assertListSelectorItem(items.get(DEFAULT_DELETE_RULE),
                               DMNEditorConstants.DecisionTableEditor_DeleteDecisionRule,
                               enabled && grid.getModel().getRowCount() > 1);
        assertListSelectorItem(items.get(DEFAULT_DUPLICATE_RULE),
                               DMNEditorConstants.DecisionTableEditor_DuplicateDecisionRule,
                               enabled);
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
    public void testOnItemSelectedDuplicateRow() {
        setupGrid(makeHasNameForDecision(), 0);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DEFAULT_DUPLICATE_RULE);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).duplicateDecisionRule(eq(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddInputClause() {
        setupGrid(makeHasNameForDecision(), 0);

        addInputClause(1);

        verifyCommandExecuteOperation(BaseExpressionGrid.RESIZE_EXISTING);

        verify(grid).selectHeaderCell(eq(0),
                                      eq(1),
                                      eq(false),
                                      eq(false));

        verifyEditHeaderCell(InputClauseColumnHeaderMetaData.class,
                             Optional.of(DMNEditorConstants.DecisionTableEditor_EditInputClause),
                             0,
                             1);

        //Check undo operation
        reset(gridPanel, gridLayer, grid, parentGridColumn);
        verify(sessionCommandManager).execute(eq(canvasHandler), addInputClauseCommandCaptor.capture());
        addInputClauseCommandCaptor.getValue().undo(canvasHandler);

        verifyCommandUndoOperation(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
    }

    private void addInputClause(final int index) {
        mockInsertColumnCommandExecution();

        grid.addInputClause(index);
    }

    @SuppressWarnings("unchecked")
    private void mockInsertColumnCommandExecution() {
        when(sessionCommandManager.execute(eq(canvasHandler),
                                           any(AbstractCanvasGraphCommand.class))).thenAnswer((i) -> {
            final AbstractCanvasHandler handler = (AbstractCanvasHandler) i.getArguments()[0];
            final org.kie.workbench.common.stunner.core.command.Command command = (org.kie.workbench.common.stunner.core.command.Command) i.getArguments()[1];
            return command.execute(handler);
        });
    }

    private void verifyCommandExecuteOperation(final Function<BaseExpressionGrid, Double> resizeFunction) {
        verify(parent).proposeContainingColumnWidth(eq(grid.getWidth() + grid.getPadding() * 2), eq(resizeFunction));
        verify(parentGridColumn).setWidth(grid.getWidth() + grid.getPadding() * 2);
        verify(gridLayer).batch(redrawCommandCaptor.capture());
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();

        final GridLayerRedrawManager.PrioritizedCommand redrawCommand = redrawCommandCaptor.getValue();
        redrawCommand.execute();
        verify(gridLayer).draw();
    }

    private void verifyCommandUndoOperation(final Function<BaseExpressionGrid, Double> resizeFunction) {
        verify(parent).proposeContainingColumnWidth(eq(grid.getWidth() + grid.getPadding() * 2), eq(resizeFunction));
        verify(parentGridColumn).setWidth(grid.getWidth() + grid.getPadding() * 2);
        verify(gridLayer).batch(redrawCommandCaptor.capture());
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();

        assertThat(redrawCommandCaptor.getAllValues()).hasSize(2);
        redrawCommandCaptor.getAllValues().get(1).execute();
        verify(gridLayer).draw();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteInputClause() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.deleteInputClause(1);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              deleteInputClauseCommandCaptor.capture());

        final DeleteInputClauseCommand deleteInputClauseCommand = deleteInputClauseCommandCaptor.getValue();
        deleteInputClauseCommand.execute(canvasHandler);

        verifyCommandExecuteOperation(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);

        //Check undo operation
        reset(gridPanel, gridLayer, grid, parentGridColumn);
        deleteInputClauseCommand.undo(canvasHandler);

        verifyCommandUndoOperation(BaseExpressionGrid.RESIZE_EXISTING);
    }

    @Test
    public void testAddOutputClause() {
        setupGrid(makeHasNameForDecision(), 0);

        addOutputClause(2);

        verifyCommandExecuteOperation(BaseExpressionGrid.RESIZE_EXISTING);

        verify(grid).selectHeaderCell(eq(1),
                                      eq(2),
                                      eq(false),
                                      eq(false));

        verifyEditHeaderCell(OutputClauseColumnHeaderMetaData.class,
                             Optional.of(DMNEditorConstants.DecisionTableEditor_EditOutputClause),
                             1,
                             2);

        //Check undo operation
        reset(gridPanel, gridLayer, grid, parentGridColumn);
        verify(sessionCommandManager).execute(eq(canvasHandler), addOutputClauseCommandCaptor.capture());
        addOutputClauseCommandCaptor.getValue().undo(canvasHandler);

        verifyCommandUndoOperation(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
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

        verifyCommandExecuteOperation(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);

        //Check undo operation
        reset(gridPanel, gridLayer, grid, parentGridColumn);
        deleteOutputClauseCommand.undo(canvasHandler);

        verifyCommandUndoOperation(BaseExpressionGrid.RESIZE_EXISTING);
    }

    @Test
    public void testAddDecisionRule() {
        setupGrid(makeHasNameForDecision(), 0);

        final DecisionTable dtable = grid.getExpression().get();
        assertThat(dtable.getRule().size()).isEqualTo(1);
        assertThat(dtable.getRule().get(0).getInputEntry().size()).isEqualTo(1);
        assertThat(dtable.getRule().get(0).getOutputEntry().size()).isEqualTo(1);

        addDecisionRule(0);

        assertThat(dtable.getRule().size()).isEqualTo(2);
        assertThat(dtable.getRule().get(1).getInputEntry().size()).isEqualTo(1);
        assertThat(dtable.getRule().get(1).getOutputEntry().size()).isEqualTo(1);

        verifyCommandExecuteOperation(BaseExpressionGrid.RESIZE_EXISTING);
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

        verifyCommandExecuteOperation(BaseExpressionGrid.RESIZE_EXISTING);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDuplicateDecisionRule() {
        setupGrid(makeHasNameForDecision(), 0);

        final DecisionTable dtable = grid.getExpression().get();
        assertThat(dtable.getRule().size()).isEqualTo(1);
        final DecisionRule rule0 = dtable.getRule().get(0);
        assertThat(rule0.getInputEntry().size()).isEqualTo(1);
        assertThat(rule0.getOutputEntry().size()).isEqualTo(1);

        rule0.getInputEntry().get(0).getText().setValue("input");
        rule0.getOutputEntry().get(0).getText().setValue("output");
        rule0.getDescription().setValue("description");

        grid.duplicateDecisionRule(0);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              addDecisionRuleCommandCaptor.capture());

        final AddDecisionRuleCommand addDecisionRuleCommand = addDecisionRuleCommandCaptor.getValue();
        addDecisionRuleCommand.execute(canvasHandler);

        assertThat(dtable.getRule().size()).isEqualTo(2);
        final DecisionRule rule1 = dtable.getRule().get(1);
        assertThat(rule1.getInputEntry().size()).isEqualTo(1);
        assertThat(rule1.getOutputEntry().size()).isEqualTo(1);

        assertThat(rule0.getInputEntry().get(0).getText().getValue()).isEqualTo("input");
        assertThat(rule0.getOutputEntry().get(0).getText().getValue()).isEqualTo("output");
        assertThat(rule0.getDescription().getValue()).isEqualTo("description");
        assertThat(rule1.getInputEntry().get(0).getText().getValue()).isEqualTo("input");
        assertThat(rule1.getOutputEntry().get(0).getText().getValue()).isEqualTo("output");
        assertThat(rule1.getDescription().getValue()).isEqualTo("description");

        verifyCommandExecuteOperation(BaseExpressionGrid.RESIZE_EXISTING);
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

    private void verifyEditHeaderCell(final Class<? extends NameAndDataTypeHeaderMetaData> headerMetaDataClass,
                                      final Optional<String> editorTitle,
                                      final int uiHeaderRowIndex,
                                      final int uiColumnIndex) {
        verify(headerEditor).bind(any(headerMetaDataClass),
                                  eq(uiHeaderRowIndex),
                                  eq(uiColumnIndex));
        verify(cellEditorControls).show(eq(headerEditor),
                                        eq(editorTitle),
                                        anyInt(),
                                        anyInt());
    }

    @Test
    public void testBodyTextBoxFactoryWhenNested() {
        setupGrid(makeHasNameForDecision(), 1);

        final GridCellTuple tupleWithoutValue = new GridCellTuple(0, 3, gridWidget);
        final GridCellValueTuple tupleWithValue = new GridCellValueTuple<>(0, 3, gridWidget, new BaseGridCellValue<>("value"));

        final TextAreaSingletonDOMElementFactory factory = grid.getBodyTextAreaFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteCellValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetCellValueCommand.class);
    }

    @Test
    public void testBodyTextBoxFactoryWhenNotNested() {
        setupGrid(makeHasNameForDecision(), 0);

        final GridCellTuple tupleWithoutValue = new GridCellTuple(0, 3, gridWidget);
        final GridCellValueTuple tupleWithValue = new GridCellValueTuple<>(0, 3, gridWidget, new BaseGridCellValue<>("value"));

        final TextAreaSingletonDOMElementFactory factory = grid.getBodyTextAreaFactory();
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
    public void testGetDisplayName() {
        setupGrid(makeHasNameForDecision(), 0);

        assertThat(extractHeaderMetaData(0, 1).getName().getValue()).isEqualTo(INPUT_CLAUSE_NAME);
        assertThat(extractHeaderMetaData(0, 2).getName().getValue()).isEqualTo(HASNAME_NAME);

        addOutputClause(3);

        assertThat(extractHeaderMetaData(0, 2).getName().getValue()).isEqualTo(HASNAME_NAME);
        assertThat(extractHeaderMetaData(1, 2).getName().getValue()).isEqualTo(OUTPUT_CLAUSE_NAME1);
        assertThat(extractHeaderMetaData(0, 3).getName().getValue()).isEqualTo(HASNAME_NAME);
        assertThat(extractHeaderMetaData(1, 3).getName().getValue()).isEqualTo(OUTPUT_CLAUSE_NAME2);
    }

    private NameAndDataTypeHeaderMetaData extractHeaderMetaData(final int uiHeaderRowIndex,
                                                                final int uiColumnIndex) {
        final GridColumn column = grid.getModel().getColumns().get(uiColumnIndex);
        return (NameAndDataTypeHeaderMetaData) column.getHeaderMetaData().get(uiHeaderRowIndex);
    }

    @Test
    public void testSetDisplayNameWithNoChange() {
        setupGrid(makeHasNameForDecision(), 0);

        assertHeaderMetaDataTest(0, 1, (md) -> md.setName(new Name(INPUT_CLAUSE_NAME)));
        assertHeaderMetaDataTest(0, 2, (md) -> md.setName(new Name(HASNAME_NAME)));

        addOutputClause(3);

        assertHeaderMetaDataTest(0, 2, (md) -> md.setName(new Name(HASNAME_NAME)));
        assertHeaderMetaDataTest(1, 2, (md) -> md.setName(new Name(OUTPUT_CLAUSE_NAME1)));
        assertHeaderMetaDataTest(0, 3, (md) -> md.setName(new Name(HASNAME_NAME)));
        assertHeaderMetaDataTest(1, 3, (md) -> md.setName(new Name(OUTPUT_CLAUSE_NAME2)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameSingleInputClauseWithEmptyValue() {
        setupGrid(makeHasNameForDecision(), 0);

        final Consumer<NameAndDataTypeHeaderMetaData> test = (md) -> md.setName(new Name());

        assertHeaderMetaDataTest(0, 1, test, DeleteHasNameCommand.class);

        compositeCommandCaptor.getValue().execute(canvasHandler);
        assertThat(expression.get().getInput().get(0).getInputExpression().getText()).isEqualTo(new Text());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameSingleOutputClauseWithEmptyValue() {
        setupGrid(makeHasNameForDecision(), 0);

        final Consumer<NameAndDataTypeHeaderMetaData> test = (md) -> md.setName(new Name());

        assertHeaderMetaDataTest(0, 2, test, DeleteHasNameCommand.class, DeleteHasNameCommand.class, UpdateElementPropertyCommand.class);

        compositeCommandCaptor.getValue().execute(canvasHandler);
        assertThat(hasExpression.getName().getValue()).isEqualTo("");
        assertThat(expression.get().getOutput().get(0).getName()).isEqualTo("");
    }

    @Test
    public void testSetDisplayNameMultipleOutputClauseWithEmptyValue() {
        setupGrid(makeHasNameForDecision(), 0);

        final Consumer<NameAndDataTypeHeaderMetaData> test = (md) -> md.setName(new Name());

        addOutputClause(3);

        assertDisplayNameMultipleOutputClause(test);
    }

    @SuppressWarnings("unchecked")
    private void assertDisplayNameMultipleOutputClause(final Consumer<NameAndDataTypeHeaderMetaData> test) {
        final String defaultName = "defaultName";

        final ArgumentCaptor<CompositeCommand> compositeCommandCaptor1 = ArgumentCaptor.forClass(CompositeCommand.class);
        hasExpression.getName().setValue(defaultName);
        expression.get().getOutput().get(0).setName(defaultName);
        assertHeaderMetaDataTest(0, 2, test, compositeCommandCaptor1, DeleteHasNameCommand.class, UpdateElementPropertyCommand.class);

        compositeCommandCaptor1.getValue().execute(canvasHandler);
        assertThat(hasExpression.getName().getValue()).isEqualTo("");
        assertThat(expression.get().getOutput().get(0).getName()).isEqualTo(defaultName);

        final ArgumentCaptor<CompositeCommand> compositeCommandCaptor2 = ArgumentCaptor.forClass(CompositeCommand.class);
        hasExpression.getName().setValue(defaultName);
        expression.get().getOutput().get(0).setName(defaultName);
        assertHeaderMetaDataTest(1, 2, test, compositeCommandCaptor2, DeleteHasNameCommand.class);

        compositeCommandCaptor2.getValue().execute(canvasHandler);
        assertThat(hasExpression.getName().getValue()).isEqualTo(defaultName);
        assertThat(expression.get().getOutput().get(0).getName()).isEqualTo("");

        final ArgumentCaptor<CompositeCommand> compositeCommandCaptor3 = ArgumentCaptor.forClass(CompositeCommand.class);
        hasExpression.getName().setValue(defaultName);
        expression.get().getOutput().get(1).setName(defaultName);
        assertHeaderMetaDataTest(0, 3, test, compositeCommandCaptor3, DeleteHasNameCommand.class, UpdateElementPropertyCommand.class);

        compositeCommandCaptor3.getValue().execute(canvasHandler);
        assertThat(hasExpression.getName().getValue()).isEqualTo("");
        assertThat(expression.get().getOutput().get(1).getName()).isEqualTo(defaultName);

        final ArgumentCaptor<CompositeCommand> compositeCommandCaptor4 = ArgumentCaptor.forClass(CompositeCommand.class);
        hasExpression.getName().setValue(defaultName);
        expression.get().getOutput().get(1).setName(defaultName);
        assertHeaderMetaDataTest(1, 3, test, compositeCommandCaptor4, DeleteHasNameCommand.class);

        compositeCommandCaptor4.getValue().execute(canvasHandler);
        assertThat(hasExpression.getName().getValue()).isEqualTo(defaultName);
        assertThat(expression.get().getOutput().get(0).getName()).isEqualTo("");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameSingleInputClauseWithNullValue() {
        setupGrid(makeHasNameForDecision(), 0);

        final Consumer<NameAndDataTypeHeaderMetaData> test = (md) -> md.setName(null);

        assertHeaderMetaDataTest(0, 1, test, DeleteHasNameCommand.class);

        compositeCommandCaptor.getValue().execute(canvasHandler);
        assertThat(expression.get().getInput().get(0).getInputExpression().getText()).isEqualTo(new Text());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameSingleOutputClauseWithNullValue() {
        setupGrid(makeHasNameForDecision(), 0);

        final Consumer<NameAndDataTypeHeaderMetaData> test = (md) -> md.setName(null);

        assertHeaderMetaDataTest(0, 2, test, DeleteHasNameCommand.class, DeleteHasNameCommand.class, UpdateElementPropertyCommand.class);

        compositeCommandCaptor.getValue().execute(canvasHandler);
        assertThat(hasExpression.getName().getValue()).isEqualTo("");
        assertThat(expression.get().getOutput().get(0).getName()).isEqualTo("");
    }

    @Test
    public void testSetDisplayNameMultipleOutputClauseWithNullValue() {
        setupGrid(makeHasNameForDecision(), 0);

        final Consumer<NameAndDataTypeHeaderMetaData> test = (md) -> md.setName(null);

        addOutputClause(3);

        assertDisplayNameMultipleOutputClause(test);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameSingleInputClauseWithNonEmptyValue() {
        setupGrid(makeHasNameForDecision(), 0);

        final Consumer<NameAndDataTypeHeaderMetaData> test = (md) -> md.setName(new Name(NAME_NEW));

        assertHeaderMetaDataTest(0, 1, test, SetHasNameCommand.class);

        compositeCommandCaptor.getValue().execute(canvasHandler);
        assertThat(expression.get().getInput().get(0).getInputExpression().getText()).isEqualTo(new Text(NAME_NEW));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameSingleOutputClauseWithNonEmptyValue() {
        setupGrid(makeHasNameForDecision(), 0);

        final Consumer<NameAndDataTypeHeaderMetaData> test = (md) -> md.setName(new Name(NAME_NEW));

        assertHeaderMetaDataTest(0, 2, test, SetHasNameCommand.class, SetHasNameCommand.class, UpdateElementPropertyCommand.class);

        compositeCommandCaptor.getValue().execute(canvasHandler);
        assertThat(hasExpression.getName()).isEqualTo(new Name(NAME_NEW));
        assertThat(expression.get().getOutput().get(0).getName()).isEqualTo(NAME_NEW);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameMultipleOutputClauseWithNonEmptyValue() {
        setupGrid(makeHasNameForDecision(), 0);

        final String defaultName = "default-name";

        final Consumer<NameAndDataTypeHeaderMetaData> test = (md) -> md.setName(new Name(NAME_NEW));

        addOutputClause(3);

        final ArgumentCaptor<CompositeCommand> compositeCommandCaptor1 = ArgumentCaptor.forClass(CompositeCommand.class);
        hasExpression.getName().setValue(defaultName);
        expression.get().getOutput().get(0).setName(defaultName);
        assertHeaderMetaDataTest(0, 2, test, compositeCommandCaptor1, SetHasNameCommand.class, UpdateElementPropertyCommand.class);

        compositeCommandCaptor1.getValue().execute(canvasHandler);
        assertThat(hasExpression.getName().getValue()).isEqualTo(NAME_NEW);
        assertThat(expression.get().getOutput().get(0).getName()).isEqualTo(defaultName);

        final ArgumentCaptor<CompositeCommand> compositeCommandCaptor2 = ArgumentCaptor.forClass(CompositeCommand.class);
        hasExpression.getName().setValue(defaultName);
        expression.get().getOutput().get(0).setName(defaultName);
        assertHeaderMetaDataTest(1, 2, test, compositeCommandCaptor2, SetHasNameCommand.class);

        compositeCommandCaptor2.getValue().execute(canvasHandler);
        assertThat(hasExpression.getName().getValue()).isEqualTo(defaultName);
        assertThat(expression.get().getOutput().get(0).getName()).isEqualTo(NAME_NEW);

        final ArgumentCaptor<CompositeCommand> compositeCommandCaptor3 = ArgumentCaptor.forClass(CompositeCommand.class);
        hasExpression.getName().setValue(defaultName);
        expression.get().getOutput().get(1).setName(defaultName);
        assertHeaderMetaDataTest(0, 3, test, compositeCommandCaptor3, SetHasNameCommand.class, UpdateElementPropertyCommand.class);

        compositeCommandCaptor3.getValue().execute(canvasHandler);
        assertThat(hasExpression.getName().getValue()).isEqualTo(NAME_NEW);
        assertThat(expression.get().getOutput().get(1).getName()).isEqualTo(defaultName);

        final ArgumentCaptor<CompositeCommand> compositeCommandCaptor4 = ArgumentCaptor.forClass(CompositeCommand.class);
        hasExpression.getName().setValue(defaultName);
        expression.get().getOutput().get(1).setName(defaultName);
        assertHeaderMetaDataTest(1, 3, test, compositeCommandCaptor4, SetHasNameCommand.class);

        compositeCommandCaptor4.getValue().execute(canvasHandler);
        assertThat(hasExpression.getName().getValue()).isEqualTo(defaultName);
        assertThat(expression.get().getOutput().get(0).getName()).isEqualTo(NAME_NEW);
    }

    @Test
    public void testGetTypeRef() {
        setupGrid(makeHasNameForDecision(), 0);

        assertThat(extractHeaderMetaData(0, 1).getTypeRef()).isNotNull();
        assertThat(extractHeaderMetaData(0, 2).getTypeRef()).isNotNull();

        addOutputClause(3);

        assertThat(extractHeaderMetaData(0, 2).getTypeRef()).isNotNull();
        assertThat(extractHeaderMetaData(1, 2).getTypeRef()).isNotNull();
        assertThat(extractHeaderMetaData(0, 3).getTypeRef()).isNotNull();
        assertThat(extractHeaderMetaData(1, 3).getTypeRef()).isNotNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetTypeRefSingleInputClause() {
        setupGrid(makeHasNameForDecision(), 0);

        final QName typeRef = new QName(QName.NULL_NS_URI,
                                        BuiltInType.DATE.getName());

        final Consumer<NameAndDataTypeHeaderMetaData> test = (md) -> md.setTypeRef(typeRef);
        final ArgumentCaptor<CanvasCommand> canvasCommandCaptor = ArgumentCaptor.forClass(CanvasCommand.class);

        assertHeaderMetaDataTest(0, 1, test, canvasCommandCaptor, SetTypeRefCommand.class);

        canvasCommandCaptor.getValue().execute(canvasHandler);
        assertThat(expression.get().getInput().get(0).getInputExpression().getTypeRef()).isEqualTo(typeRef);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetTypeRefSingleOutputClause() {
        setupGrid(makeHasNameForDecision(), 0);

        final QName typeRef = new QName(QName.NULL_NS_URI,
                                        BuiltInType.DATE.getName());

        final Consumer<NameAndDataTypeHeaderMetaData> test = (md) -> md.setTypeRef(typeRef);

        assertHeaderMetaDataTest(0, 2, test, SetTypeRefCommand.class, SetTypeRefCommand.class);

        compositeCommandCaptor.getValue().execute(canvasHandler);
        assertThat(hasExpression.getVariable().getTypeRef()).isEqualTo(typeRef);
        assertThat(expression.get().getOutput().get(0).getTypeRef()).isEqualTo(typeRef);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetTypeRefMultipleOutputClauses() {
        setupGrid(makeHasNameForDecision(), 0);

        final QName typeRef = new QName(QName.NULL_NS_URI,
                                        BuiltInType.DATE.getName());
        final QName defaultTypeRef = new QName();

        final Consumer<NameAndDataTypeHeaderMetaData> test = (md) -> md.setTypeRef(typeRef);

        addOutputClause(3);

        final ArgumentCaptor<CanvasCommand> canvasCommandCaptor1 = ArgumentCaptor.forClass(CanvasCommand.class);
        hasExpression.getVariable().setTypeRef(defaultTypeRef);
        expression.get().getOutput().get(0).setTypeRef(defaultTypeRef);
        assertHeaderMetaDataTest(0, 2, test, canvasCommandCaptor1, SetTypeRefCommand.class);

        canvasCommandCaptor1.getValue().execute(canvasHandler);
        assertThat(hasExpression.getVariable().getTypeRef()).isEqualTo(typeRef);
        assertThat(expression.get().getOutput().get(0).getTypeRef()).isEqualTo(defaultTypeRef);

        final ArgumentCaptor<CanvasCommand> canvasCommandCaptor2 = ArgumentCaptor.forClass(CanvasCommand.class);
        hasExpression.getVariable().setTypeRef(defaultTypeRef);
        expression.get().getOutput().get(0).setTypeRef(defaultTypeRef);
        assertHeaderMetaDataTest(1, 2, test, canvasCommandCaptor2, SetTypeRefCommand.class);

        canvasCommandCaptor2.getValue().execute(canvasHandler);
        assertThat(hasExpression.getVariable().getTypeRef()).isEqualTo(defaultTypeRef);
        assertThat(expression.get().getOutput().get(0).getTypeRef()).isEqualTo(typeRef);

        final ArgumentCaptor<CanvasCommand> canvasCommandCaptor3 = ArgumentCaptor.forClass(CanvasCommand.class);
        hasExpression.getVariable().setTypeRef(defaultTypeRef);
        expression.get().getOutput().get(1).setTypeRef(defaultTypeRef);
        assertHeaderMetaDataTest(0, 3, test, canvasCommandCaptor3, SetTypeRefCommand.class);

        canvasCommandCaptor3.getValue().execute(canvasHandler);
        assertThat(hasExpression.getVariable().getTypeRef()).isEqualTo(typeRef);
        assertThat(expression.get().getOutput().get(1).getTypeRef()).isEqualTo(defaultTypeRef);

        final ArgumentCaptor<CanvasCommand> canvasCommandCaptor4 = ArgumentCaptor.forClass(CanvasCommand.class);
        hasExpression.getVariable().setTypeRef(defaultTypeRef);
        expression.get().getOutput().get(1).setTypeRef(defaultTypeRef);
        assertHeaderMetaDataTest(1, 3, test, canvasCommandCaptor4, SetTypeRefCommand.class);

        canvasCommandCaptor4.getValue().execute(canvasHandler);
        assertThat(hasExpression.getVariable().getTypeRef()).isEqualTo(defaultTypeRef);
        assertThat(expression.get().getOutput().get(1).getTypeRef()).isEqualTo(typeRef);
    }

    @Test
    public void testSetTypeRefWithoutChange() {
        setupGrid(makeHasNameForDecision(), 0);

        final Consumer<NameAndDataTypeHeaderMetaData> test = (md) -> md.setTypeRef(new QName());

        assertHeaderMetaDataTest(0, 1, test);
        assertHeaderMetaDataTest(0, 2, test);

        addOutputClause(3);

        assertHeaderMetaDataTest(0, 2, test);
        assertHeaderMetaDataTest(1, 2, test);
        assertHeaderMetaDataTest(0, 3, test);
        assertHeaderMetaDataTest(1, 3, test);
    }

    @SuppressWarnings("unchecked")
    private void assertHeaderMetaDataTest(final int uiHeaderRowIndex,
                                          final int uiColumnIndex,
                                          final Consumer<NameAndDataTypeHeaderMetaData> test,
                                          final Class... commands) {
        assertHeaderMetaDataTest(uiHeaderRowIndex,
                                 uiColumnIndex,
                                 test,
                                 compositeCommandCaptor,
                                 commands);
    }

    @SuppressWarnings("unchecked")
    private void assertHeaderMetaDataTest(final int uiHeaderRowIndex,
                                          final int uiColumnIndex,
                                          final Consumer<NameAndDataTypeHeaderMetaData> test,
                                          final ArgumentCaptor<? extends org.kie.workbench.common.stunner.core.command.Command> argumentCaptor,
                                          final Class... commands) {
        reset(sessionCommandManager);

        test.accept(extractHeaderMetaData(uiHeaderRowIndex, uiColumnIndex));

        if (commands.length == 0) {
            verify(sessionCommandManager, never()).execute(any(AbstractCanvasHandler.class),
                                                           any(org.kie.workbench.common.stunner.core.command.Command.class));
        } else {
            verify(sessionCommandManager).execute(eq(canvasHandler),
                                                  argumentCaptor.capture());
            GridFactoryCommandUtils.assertCommands(argumentCaptor.getValue(),
                                                   commands);
        }
    }

    @Test
    public void testAsDMNModelInstrumentedBase() {
        setupGrid(makeHasNameForDecision(), 0);

        assertThat(extractHeaderMetaData(0, 1).asDMNModelInstrumentedBase()).isInstanceOf(LiteralExpression.class);
        assertThat(extractHeaderMetaData(0, 2).asDMNModelInstrumentedBase()).isInstanceOf(InformationItemPrimary.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInputClauseHasNameWrapperForHeaderMetaData() {
        setupGrid(makeHasNameForDecision(), 0);

        final DecisionTable dtable = expression.get();

        assertThat(dtable.getInput().get(0).getInputExpression().getText().getValue()).isEqualTo(grid.getModel().getColumns().get(1).getHeaderMetaData().get(0).getTitle());

        extractHeaderMetaData(0, 1).setName(new Name(NAME_NEW));

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              compositeCommandCaptor.capture());
        ((AbstractCanvasGraphCommand) compositeCommandCaptor.getValue().getCommands().get(0)).execute(canvasHandler);

        assertThat(expression.get().getInput().get(0).getInputExpression().getText().getValue()).isEqualTo(NAME_NEW);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOutputClauseHasNameWrapperForHeaderMetaData() {
        setupGrid(makeHasNameForDecision(), 0);

        //More than one OutputClause column is required before the tested wrapper is instantiated.
        addOutputClause(3);

        final DecisionTable dtable = expression.get();
        final OutputClause outputClause = dtable.getOutput().get(0);
        final GridColumn.HeaderMetaData outputClauseHeaderMetaData = grid.getModel().getColumns().get(2).getHeaderMetaData().get(1);
        assertThat(outputClause.getName()).isEqualTo(outputClauseHeaderMetaData.getTitle());

        reset(sessionCommandManager);

        extractHeaderMetaData(1, 2).setName(new Name(NAME_NEW));

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              compositeCommandCaptor.capture());
        ((AbstractCanvasGraphCommand) compositeCommandCaptor.getValue().getCommands().get(0)).execute(canvasHandler);

        assertThat(outputClause.getName()).isEqualTo(NAME_NEW);
    }

    @Test
    public void testSelectRow() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.selectCell(0, DEFAULT_ROW_NUMBER_COLUMN_INDEX, false, false);

        assertNOPDomainObjectSelection();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectMultipleCells() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.selectCell(0, DEFAULT_INPUT_CLAUSE_COLUMN_INDEX, false, false);

        assertDomainObjectSelection(expression.get().getRule().get(0).getInputEntry().get(0));

        //Reset DomainObjectSelectionEvent tested above.
        reset(domainObjectSelectionEvent);

        grid.selectCell(0, DEFAULT_OUTPUT_CLAUSE_COLUMN_INDEX, false, true);

        assertNOPDomainObjectSelection();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectSingleCellWithHeaderSelected() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.selectHeaderCell(0, DEFAULT_INPUT_CLAUSE_COLUMN_INDEX, false, false);

        assertDomainObjectSelection(expression.get().getInput().get(0));

        //Reset DomainObjectSelectionEvent tested above.
        reset(domainObjectSelectionEvent);

        grid.selectCell(0, DEFAULT_INPUT_CLAUSE_COLUMN_INDEX, false, true);

        assertDomainObjectSelection(expression.get().getRule().get(0).getInputEntry().get(0));
    }

    @Test
    public void testSelectInputClauseColumn() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.selectCell(0, DEFAULT_INPUT_CLAUSE_COLUMN_INDEX, false, false);

        assertDomainObjectSelection(expression.get().getRule().get(0).getInputEntry().get(0));
    }

    @Test
    public void testSelectOutputClauseColumn() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.selectCell(0, DEFAULT_OUTPUT_CLAUSE_COLUMN_INDEX, false, false);

        assertDomainObjectSelection(expression.get().getRule().get(0).getOutputEntry().get(0));
    }

    @Test
    public void testSelectDescriptionColumn() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.selectCell(0, DEFAULT_DESCRIPTION_COLUMN_INDEX, false, false);

        assertNOPDomainObjectSelection();
    }

    @Test
    public void testSelectHeaderRowNumberColumn() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.selectHeaderCell(0, DEFAULT_ROW_NUMBER_COLUMN_INDEX, false, false);

        assertNOPDomainObjectSelection();
    }

    @Test
    public void testSelectHeaderInputClauseColumn() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.selectHeaderCell(0, DEFAULT_INPUT_CLAUSE_COLUMN_INDEX, false, false);

        assertDomainObjectSelection(expression.get().getInput().get(0));
    }

    @Test
    public void testSelectHeaderSingleOutputClauseColumn() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.selectHeaderCell(0, DEFAULT_OUTPUT_CLAUSE_COLUMN_INDEX, false, false);

        assertDomainObjectSelection(expression.get().getOutput().get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectHeaderMultipleOutputClauseColumn() {
        setupGrid(makeHasNameForDecision(), 0);

        addOutputClause(2);

        reset(domainObjectSelectionEvent);

        grid.selectHeaderCell(0, DEFAULT_OUTPUT_CLAUSE_COLUMN_INDEX, false, false);

        assertDomainObjectSelection(hasExpression);

        reset(domainObjectSelectionEvent);

        grid.selectHeaderCell(1, DEFAULT_OUTPUT_CLAUSE_COLUMN_INDEX, false, false);

        assertDomainObjectSelection(expression.get().getOutput().get(0));
    }

    @Test
    public void testSelectHeaderDescriptionColumn() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.selectHeaderCell(0, DEFAULT_DESCRIPTION_COLUMN_INDEX, false, false);

        assertNOPDomainObjectSelection();
    }

    private void assertDomainObjectSelection(final DomainObject domainObject) {
        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(domainObject);
    }

    private void assertNOPDomainObjectSelection() {
        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isInstanceOf(NOPDomainObject.class);
    }

    @Test
    public void testSelectFirstCell() {
        setupGrid(makeHasNameForDecision(), 0);

        grid.selectFirstCell();

        assertThat(grid.getModel().getSelectedCells()).containsOnly(new GridData.SelectedCell(0, 1));
    }
}
