/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.mocks.EventSourceMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class RelationEditorDefinitionTest {

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DMNSession session;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private TranslationService translationService;

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private ManagedInstance<ValueAndDataTypePopoverView.Presenter> headerEditors;

    @Mock
    private ValueAndDataTypePopoverView.Presenter headerEditor;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    private Optional<HasName> hasName = Optional.empty();

    private RelationEditorDefinition definition;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getGridPanel()).thenReturn(gridPanel);
        when(session.getGridLayer()).thenReturn(gridLayer);
        when(session.getCellEditorControls()).thenReturn(cellEditorControls);

        when(headerEditors.get()).thenReturn(headerEditor);

        this.definition = new RelationEditorDefinition(definitionUtils,
                                                       sessionManager,
                                                       sessionCommandManager,
                                                       canvasCommandFactory,
                                                       editorSelectedEvent,
                                                       refreshFormPropertiesEvent,
                                                       domainObjectSelectionEvent,
                                                       listSelector,
                                                       translationService,
                                                       headerEditors,
                                                       readOnlyProvider);
        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(Mockito.<String>any());
    }

    @Test
    public void testType() {
        assertThat(definition.getType()).isEqualTo(ExpressionType.RELATION);
    }

    @Test
    public void testName() {
        assertThat(definition.getName()).isEqualTo(DMNEditorConstants.ExpressionEditor_RelationType);
    }

    @Test
    public void testModelDefinition() {
        final Optional<Relation> oModel = definition.getModelClass();
        assertThat(oModel).isPresent();
    }

    @Test
    public void testModelEnrichment() {
        final Optional<Relation> oModel = definition.getModelClass();
        definition.enrich(Optional.empty(), hasExpression, oModel);

        final Relation model = oModel.get();

        assertNotNull(model.getRow());
        assertNotNull(model.getRow().get(0).getId());
        assertNotNull(model.getRow().get(0).getExpression().get(0));
        assertTrue(model.getRow().get(0).getExpression().get(0).getExpression() instanceof LiteralExpression);

        assertNotNull(model.getColumn());
        assertEquals(1, model.getColumn().size());
        assertEquals(RelationDefaultValueUtilities.PREFIX + "1",
                     model.getColumn().get(0).getName().getValue());

        assertEquals(model,
                     model.getRow().get(0).getParent());
        assertEquals(model,
                     model.getColumn().get(0).getParent());
        assertEquals(model.getRow().get(0),
                     model.getRow().get(0).getExpression().get(0).getExpression().getParent());
    }

    @Test
    public void testEditor() {
        when(hasExpression.getExpression()).thenReturn(definition.getModelClass().get());
        final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> oEditor = definition.getEditor(parent,
                                                                                                                                                 Optional.empty(),
                                                                                                                                                 hasExpression,
                                                                                                                                                 hasName,
                                                                                                                                                 false,
                                                                                                                                                 0);

        assertThat(oEditor).isPresent();

        final GridWidget editor = oEditor.get();
        assertThat(editor).isInstanceOf(RelationGrid.class);
    }

    @Test
    public void testIsUserSelectable() {
        assertThat(definition.isUserSelectable()).isTrue();
    }
}
