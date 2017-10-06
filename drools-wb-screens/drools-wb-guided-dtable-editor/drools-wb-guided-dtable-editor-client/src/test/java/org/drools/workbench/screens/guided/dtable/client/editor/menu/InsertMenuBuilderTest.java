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

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.appformer.project.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectionsChangedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.menu.MenuItemFactory;
import org.uberfire.ext.widgets.common.client.menu.MenuItemView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InsertMenuBuilderTest {

    private InsertMenuBuilder builder;

    private GuidedDecisionTable52 model;

    private GuidedDecisionTableUiModel uiModel;

    private GuidedDecisionTablePresenter.Access access = new GuidedDecisionTablePresenter.Access();

    @Mock
    private TranslationService translationService;

    @Mock
    private ManagedInstance<MenuItemView> menuItemViewProducer;

    @Mock
    private GuidedDecisionTableView.Presenter dtPresenter;

    @Mock
    private GridColumnRenderer<String> gridColumnRenderer;

    @Mock
    private GridColumn.HeaderMetaData headerMetaData;

    @Mock
    private GuidedDecisionTableView dtPresenterView;

    @Mock
    private GuidedDecisionTableModellerView.Presenter modeller;

    @Before
    public void setup() {
        model = new GuidedDecisionTable52();
        uiModel = new GuidedDecisionTableUiModel(mock(ModelSynchronizer.class));

        final MenuItemFactory menuItemFactory = new MenuItemFactory(menuItemViewProducer);

        when(dtPresenter.hasEditableColumns()).thenReturn(true);
        when(dtPresenter.getView()).thenReturn(dtPresenterView);
        when(dtPresenter.getModel()).thenReturn(model);
        when(dtPresenter.getAccess()).thenReturn(access);
        when(dtPresenterView.getModel()).thenReturn(uiModel);
        when(translationService.getTranslation(any(String.class))).thenReturn("i18n");
        when(menuItemViewProducer.select(any(Annotation.class))).thenReturn(menuItemViewProducer);
        when(menuItemViewProducer.get()).thenReturn(mock(MenuItemWithIconView.class));

        uiModel.appendColumn(new BaseGridColumn<>(headerMetaData, gridColumnRenderer, 100));
        uiModel.appendColumn(new BaseGridColumn<>(headerMetaData, gridColumnRenderer, 100));
        uiModel.appendColumn(new BaseGridColumn<>(headerMetaData, gridColumnRenderer, 100));
        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());

        builder = new InsertMenuBuilder(translationService, menuItemFactory);
        builder.setup();
        builder.setModeller(modeller);
    }

    @Test
    public void testOnDecisionTableSelectedEventWithNoSelectionsAndItHasEditableColumns() {

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miAppendRow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowAbove.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowBelow.getMenuItem().isEnabled());
        assertTrue(builder.miInsertColumn.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectedEventWithNoSelectionsAndItDoesNotHaveEditableColumns() {

        when(dtPresenter.hasEditableColumns()).thenReturn(false);

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miAppendRow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowAbove.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowBelow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertColumn.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectedEventWithSingleRowSelectedAndItHasEditableColumns() {

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52List());
        uiModel.selectCell(0, 2);

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miAppendRow.getMenuItem().isEnabled());
        assertTrue(builder.miInsertRowAbove.getMenuItem().isEnabled());
        assertTrue(builder.miInsertRowBelow.getMenuItem().isEnabled());
        assertTrue(builder.miInsertColumn.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectedEventWithSingleRowSelectedAndItDoesNotHaveEditableColumns() {

        when(dtPresenter.hasEditableColumns()).thenReturn(false);

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52List());
        uiModel.selectCell(0, 2);

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miAppendRow.getMenuItem().isEnabled());
        assertTrue(builder.miInsertRowAbove.getMenuItem().isEnabled());
        assertTrue(builder.miInsertRowBelow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertColumn.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectedEventWithMultipleRowsSelectedAndItHasEditableColumns() {

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52List());
        model.getData().add(makeDTCellValue52List());

        uiModel.selectCells(0, 2, 1, 2);

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miAppendRow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowAbove.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowBelow.getMenuItem().isEnabled());
        assertTrue(builder.miInsertColumn.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectedEventWithMultipleRowsSelectedAndItDoesNotHaveEditableColumns() {

        when(dtPresenter.hasEditableColumns()).thenReturn(false);

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52List());
        model.getData().add(makeDTCellValue52List());
        uiModel.selectCells(0, 2, 1, 2);

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miAppendRow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowAbove.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowBelow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertColumn.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithNoSelectionsAndItHasEditableColumns() {

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertTrue(builder.miAppendRow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowAbove.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowBelow.getMenuItem().isEnabled());
        assertTrue(builder.miInsertColumn.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithNoSelectionsAndItDoesNotHaveEditableColumns() {

        when(dtPresenter.hasEditableColumns()).thenReturn(false);

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertTrue(builder.miAppendRow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowAbove.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowBelow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertColumn.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithSingleRowSelectedAndItHasEditableColumns() {

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52List());
        uiModel.selectCell(0, 2);

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertTrue(builder.miAppendRow.getMenuItem().isEnabled());
        assertTrue(builder.miInsertRowAbove.getMenuItem().isEnabled());
        assertTrue(builder.miInsertRowBelow.getMenuItem().isEnabled());
        assertTrue(builder.miInsertColumn.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithSingleRowSelectedAndItDoesNotHaveEditableColumns() {

        when(dtPresenter.hasEditableColumns()).thenReturn(false);

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52List());
        uiModel.selectCell(0, 2);

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertTrue(builder.miAppendRow.getMenuItem().isEnabled());
        assertTrue(builder.miInsertRowAbove.getMenuItem().isEnabled());
        assertTrue(builder.miInsertRowBelow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertColumn.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithMultipleRowsSelectedAndItHasEditableColumns() {

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52List());
        model.getData().add(makeDTCellValue52List());
        uiModel.selectCells(0, 2, 1, 2);

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertTrue(builder.miAppendRow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowAbove.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowBelow.getMenuItem().isEnabled());
        assertTrue(builder.miInsertColumn.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithMultipleRowsSelectedAndItDoesNotHaveEditableColumns() {

        when(dtPresenter.hasEditableColumns()).thenReturn(false);

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52List());
        model.getData().add(makeDTCellValue52List());
        uiModel.selectCells(0, 2, 1, 2);

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertTrue(builder.miAppendRow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowAbove.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowBelow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertColumn.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectedEventReadOnly() {

        dtPresenter.getAccess().setReadOnly(true);

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertFalse(builder.miAppendRow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowAbove.getMenuItem().isEnabled());
        assertFalse(builder.miInsertRowBelow.getMenuItem().isEnabled());
        assertFalse(builder.miInsertColumn.getMenuItem().isEnabled());
    }

    private Pattern52 makePattern52() {
        return new Pattern52() {{
            setFactType("Fact");
            getChildColumns().add(new ConditionCol52() {{
                setFactType("Fact");
                setFactField("field1");
                setFieldType(DataType.TYPE_STRING);
                setOperator("==");
            }});
        }};
    }

    private List<DTCellValue52> makeDTCellValue52List() {
        return new ArrayList<DTCellValue52>() {{
            add(new DTCellValue52(1));
            add(new DTCellValue52("descr"));
            add(new DTCellValue52("md"));
        }};
    }
}
