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

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.Clipboard;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.impl.DefaultClipboard;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectionsChangedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshMenusEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTablePopoverUtils;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.menu.MenuItemFactory;
import org.uberfire.ext.widgets.common.client.menu.MenuItemView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class EditMenuBuilderTest {

    private EditMenuBuilder builder;

    private GuidedDecisionTable52 model;

    private GuidedDecisionTableUiModel uiModel;

    private Clipboard clipboard;

    private GuidedDecisionTablePresenter.Access access = new GuidedDecisionTablePresenter.Access();

    @Mock
    private TranslationService ts;

    @Mock
    private ManagedInstance<MenuItemView> menuItemViewProducer;

    @Mock
    private MenuItemWithIconView menuItemWithIconView;

    @Mock
    private HTMLElement menuItemHTMLElement;

    @Mock
    private GuidedDecisionTableView.Presenter dtPresenter;

    @Mock
    private GridColumnRenderer<String> gridColumnRenderer;

    @Mock
    private GridColumn.HeaderMetaData headerMetaData;

    @Mock
    private GuidedDecisionTableView dtPresenterView;

    @Mock
    private DecisionTablePopoverUtils popoverUtils;

    @Before
    public void setup() {
        model = new GuidedDecisionTable52();
        uiModel = new GuidedDecisionTableUiModel(mock(ModelSynchronizer.class));
        clipboard = new DefaultClipboard();

        final MenuItemFactory menuItemFactory = new MenuItemFactory(menuItemViewProducer);

        when(dtPresenter.hasEditableColumns()).thenReturn(true);
        when(dtPresenter.getView()).thenReturn(dtPresenterView);
        when(dtPresenter.getModel()).thenReturn(model);
        when(dtPresenter.getAccess()).thenReturn(access);
        when(dtPresenterView.getModel()).thenReturn(uiModel);
        when(ts.getTranslation(any(String.class))).thenReturn("i18n");
        when(menuItemViewProducer.select(any(Annotation.class))).thenReturn(menuItemViewProducer);
        when(menuItemViewProducer.get()).thenReturn(menuItemWithIconView);
        when(menuItemWithIconView.getElement()).thenReturn(menuItemHTMLElement);

        uiModel.appendColumn(new BaseGridColumn<>(headerMetaData, gridColumnRenderer, 100));
        uiModel.appendColumn(new BaseGridColumn<>(headerMetaData, gridColumnRenderer, 100));
        uiModel.appendColumn(new BaseGridColumn<>(headerMetaData, gridColumnRenderer, 100));
        uiModel.appendColumn(new BaseGridColumn<>(headerMetaData, gridColumnRenderer, 100));
        uiModel.appendRow(new BaseGridRow());

        builder = spy(new EditMenuBuilder(clipboard,
                                          ts,
                                          menuItemFactory,
                                          popoverUtils));
        builder.setup();
    }

    @Test
    public void testPopoverSetup() {
        verify(popoverUtils).setupPopover(eq(menuItemHTMLElement),
                                          anyString());
    }

    @Test
    public void testOnDecisionTableSelectedEventWithNoSelections() {

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertFalse(builder.miCut.getMenuItem().isEnabled());
        assertFalse(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertFalse(builder.miOtherwiseCell.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectedEventWithNonOtherwiseColumnSelectedAndItHasEditableColumnsAndItHasEditableColumns() {

        model.getMetadataCols().add(new MetadataCol52());
        model.getData().add(makeDTCellValue52());
        uiModel.selectCell(0, 3);

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertFalse(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(null));
    }

    @Test
    public void testOnDecisionTableSelectedEventWithNonOtherwiseColumnSelectedAndItHasEditableColumnsAndItDoesNotHaveEditableColumns() {

        when(dtPresenter.hasEditableColumns()).thenReturn(false);

        model.getMetadataCols().add(new MetadataCol52());
        model.getData().add(makeDTCellValue52());
        uiModel.selectCell(0, 3);

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertFalse(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(null));
    }

    @Test
    public void testOnDecisionTableSelectedEventWithOtherwiseColumnSelectedAndItHasEditableColumns() {

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52());
        uiModel.selectCell(0, 3);

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertTrue(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(null));
    }

    @Test
    public void testOnDecisionTableSelectedEventWithOtherwiseColumnSelectedAndItDoesNotHaveEditableColumns() {

        when(dtPresenter.hasEditableColumns()).thenReturn(false);

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52());
        uiModel.selectCell(0, 3);

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertTrue(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(null));
    }

    @Test
    public void testOnDecisionTableSelectedEventWithOtherwiseCellSelectedAndItHasEditableColumns() {

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52ListWithOtherwise());
        uiModel.selectCell(0, 3);

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertTrue(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(IconType.CHECK));
    }

    @Test
    public void testOnDecisionTableSelectedEventWithOtherwiseCellSelectedAndItDoesNotHaveEditableColumns() {

        when(dtPresenter.hasEditableColumns()).thenReturn(false);

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52ListWithOtherwise());
        uiModel.selectCell(0, 3);

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertTrue(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(IconType.CHECK));
    }

    @Test
    public void testOnDecisionTableSelectedEventWithSelectionsWithClipboardPopulated() {

        model.getMetadataCols().add(new MetadataCol52());
        model.getData().add(makeDTCellValue52());
        uiModel.selectCell(0, 3);
        clipboard.setData(makeClipboardHashSetData());

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertTrue(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertFalse(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(null));
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithNoSelections() {

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertFalse(builder.miCut.getMenuItem().isEnabled());
        assertFalse(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertFalse(builder.miOtherwiseCell.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithNonOtherwiseColumnSelectedAndItHasEditableColumns() {

        model.getMetadataCols().add(new MetadataCol52());
        model.getData().add(makeDTCellValue52());
        uiModel.selectCell(0, 3);

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertFalse(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(null));
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithNonOtherwiseColumnSelectedAndItDoesNotHaveEditableColumns() {

        when(dtPresenter.hasEditableColumns()).thenReturn(false);

        model.getMetadataCols().add(new MetadataCol52());
        model.getData().add(makeDTCellValue52());
        uiModel.selectCell(0, 3);

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertFalse(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(null));
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithOtherwiseColumnSelectedAndItHasEditableColumns() {

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52());
        uiModel.selectCell(0, 3);

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertTrue(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(null));
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithOtherwiseColumnSelectedAndItDoesNotHaveEditableColumns() {

        when(dtPresenter.hasEditableColumns()).thenReturn(false);

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52());
        uiModel.selectCell(0, 3);

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertTrue(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(null));
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithOtherwiseCellSelectedAndItHasEditableColumns() {

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52ListWithOtherwise());
        uiModel.selectCell(0, 3);

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertTrue(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(IconType.CHECK));
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithOtherwiseCellSelectedAndItDoesNotHaveEditableColumns() {

        when(dtPresenter.hasEditableColumns()).thenReturn(false);

        model.getConditions().add(makePattern52());
        model.getData().add(makeDTCellValue52ListWithOtherwise());
        uiModel.selectCell(0, 3);

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertTrue(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(IconType.CHECK));
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithSelectionsWithClipboardPopulatedAndItHasEditableColumns() {

        model.getMetadataCols().add(new MetadataCol52());
        model.getData().add(makeDTCellValue52());
        uiModel.selectCell(0, 3);
        clipboard.setData(makeClipboardHashSetData());

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertTrue(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertFalse(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(null));
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithSelectionsWithClipboardPopulatedAndItDoesNotHaveEditableColumns() {

        when(dtPresenter.hasEditableColumns()).thenReturn(false);

        model.getMetadataCols().add(new MetadataCol52());
        model.getData().add(makeDTCellValue52());
        uiModel.selectCell(0, 3);
        clipboard.setData(makeClipboardHashSetData());

        builder.onDecisionTableSelectionsChangedEvent(new DecisionTableSelectionsChangedEvent(dtPresenter));

        assertTrue(builder.miCut.getMenuItem().isEnabled());
        assertTrue(builder.miCopy.getMenuItem().isEnabled());
        assertTrue(builder.miPaste.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertTrue(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertFalse(builder.miOtherwiseCell.getMenuItem().isEnabled());
        verify(builder.miOtherwiseCell.getMenuItemView(), times(1)).setIconType(eq(null));
    }

    @Test
    public void testOnDecisionTableSelectedEventReadOnly() {

        dtPresenter.getAccess().setReadOnly(true);
        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertFalse(builder.miCut.getMenuItem().isEnabled());
        assertFalse(builder.miCopy.getMenuItem().isEnabled());
        assertFalse(builder.miPaste.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedCells.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedColumns.getMenuItem().isEnabled());
        assertFalse(builder.miDeleteSelectedRows.getMenuItem().isEnabled());
        assertFalse(builder.miOtherwiseCell.getMenuItem().isEnabled());
    }

    @Test
    public void testOnRefreshMenusEvent() {
        builder.onRefreshMenusEvent(new RefreshMenusEvent());

        verify(builder).initialise();
    }

    private HashSet<Clipboard.ClipboardData> makeClipboardHashSetData() {
        return new HashSet<Clipboard.ClipboardData>() {{
            add(makeDefaultClipboard());
        }};
    }

    private DefaultClipboard.ClipboardDataImpl makeDefaultClipboard() {

        final DTCellValue52 dtCellValue52 = model.getData().get(0).get(3);

        return new DefaultClipboard.ClipboardDataImpl(0, 3, dtCellValue52);
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

    private List<DTCellValue52> makeDTCellValue52() {
        return new ArrayList<DTCellValue52>() {{
            add(new DTCellValue52(1));
            add(new DTCellValue52(""));
            add(new DTCellValue52("descr"));
            add(new DTCellValue52("md"));
        }};
    }

    private List<DTCellValue52> makeDTCellValue52ListWithOtherwise() {
        return new ArrayList<DTCellValue52>() {{
            add(new DTCellValue52(1));
            add(new DTCellValue52(""));
            add(new DTCellValue52("descr"));
            add(new DTCellValue52() {{
                setOtherwise(true);
            }});
        }};
    }
}
