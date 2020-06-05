/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.editor.page.accordion.GuidedDecisionTableAccordion;
import org.drools.workbench.screens.guided.dtable.client.editor.page.accordion.GuidedDecisionTableAccordionItem;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control.AttributeColumnConfigRow;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control.ColumnLabelWidget;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control.ColumnManagementView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control.DeleteColumnManagementAnchorWidget;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshActionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshAttributesPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshConditionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshMetaDataPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.ruleselector.RuleSelector;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.mvp.ParameterizedCommand;

import static org.drools.workbench.screens.guided.dtable.client.editor.page.accordion.GuidedDecisionTableAccordionItem.Type.ACTION;
import static org.drools.workbench.screens.guided.dtable.client.editor.page.accordion.GuidedDecisionTableAccordionItem.Type.ATTRIBUTE;
import static org.drools.workbench.screens.guided.dtable.client.editor.page.accordion.GuidedDecisionTableAccordionItem.Type.CONDITION;
import static org.drools.workbench.screens.guided.dtable.client.editor.page.accordion.GuidedDecisionTableAccordionItem.Type.METADATA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ColumnsPagePresenterTest {

    @Mock
    private ColumnsPagePresenter.View view;

    @Mock
    private GuidedDecisionTableAccordion accordion;

    @Mock
    private ManagedInstance<NewGuidedDecisionTableColumnWizard> wizardManagedInstance;

    @Mock
    private TranslationService translationService;

    @Mock
    private ManagedInstance<DeleteColumnManagementAnchorWidget> deleteColumnManagementAnchorWidgets;

    @Mock
    private ManagedInstance<AttributeColumnConfigRow> attributeColumnConfigRow;

    @Mock
    private ColumnManagementView conditionsPanel;

    @Mock
    private ColumnManagementView actionsPanel;

    @Mock
    private ShowRuleNameOptionPresenter showRuleNameOptionPresenter;

    @Mock
    private VerticalPanel verticalPanel;

    @Mock
    private GuidedDecisionTableModellerView.Presenter modeller;

    @Mock
    private MetadataCol52 metadataColumn;

    @Mock
    private CompositeColumn<? extends BaseColumn> compositeColumn1;

    @Mock
    private CompositeColumn<? extends BaseColumn> compositeColumn2;

    @Mock
    private CompositeColumn<? extends BaseColumn> compositeColumn3;

    private ColumnsPagePresenter presenter;

    @Before
    public void setUp() {
        presenter = spy(new ColumnsPagePresenter(view,
                                                 accordion,
                                                 wizardManagedInstance,
                                                 translationService,
                                                 deleteColumnManagementAnchorWidgets,
                                                 attributeColumnConfigRow,
                                                 conditionsPanel,
                                                 actionsPanel,
                                                 showRuleNameOptionPresenter));

        doReturn(modeller).when(presenter).getModeller();
    }

    @Test
    public void testSetup() {

        presenter.setup();

        verify(view).init(presenter);
    }

    @Test
    public void testInit() {

        final GuidedDecisionTableModellerView.Presenter modeller = mock(GuidedDecisionTableModellerView.Presenter.class);

        doNothing().when(presenter).setupAccordion();
        doNothing().when(presenter).setupRuleInheritance();
        doNothing().when(presenter).setupUseRuleNames();
        doNothing().when(presenter).setupColumnsNoteInfo(modeller);
        doNothing().when(presenter).setupConditionsPanel(modeller);
        doNothing().when(presenter).setupActionsPanel(modeller);

        doCallRealMethod().when(presenter).getModeller();

        presenter.init(modeller);

        verify(presenter).setupAccordion();
        verify(presenter).setupRuleInheritance();
        verify(presenter).setupColumnsNoteInfo(modeller);
        verify(presenter).setupConditionsPanel(modeller);
        verify(presenter).setupActionsPanel(modeller);

        assertEquals(modeller, presenter.getModeller());
    }

    @Test
    public void testSetupConditionsPanel() {

        presenter.setupConditionsPanel(modeller);

        verify(conditionsPanel).init(modeller);
    }

    @Test
    public void testSetupActionsPanel() {

        presenter.setupActionsPanel(modeller);

        verify(actionsPanel).init(modeller);
    }

    @Test
    public void testSetupAccordionWidgets() {

        final VerticalPanel verticalPanel = mock(VerticalPanel.class);

        doReturn(verticalPanel).when(presenter).makeDefaultPanel();

        presenter.setupAccordionWidgets();

        verify(presenter).setupAccordionWidget(eq(ATTRIBUTE), any());
        verify(presenter).setupAccordionWidget(eq(METADATA), any());
        verify(presenter).setupAccordionWidget(eq(CONDITION), any());
        verify(presenter).setupAccordionWidget(eq(ACTION), any());

        assertEquals(verticalPanel, presenter.getAttributeWidget());
        assertEquals(verticalPanel, presenter.getMetaDataWidget());
        assertEquals(verticalPanel, presenter.getConditionsWidget());
        assertEquals(verticalPanel, presenter.getActionsWidget());
    }

    @Test
    public void testSetupAccordionWidget() {

        final VerticalPanel defaultPanel = mock(VerticalPanel.class);
        final GuidedDecisionTableAccordionItem.Type accordionType = METADATA;
        final Consumer<VerticalPanel> setWidget = (panel) -> assertEquals(panel, defaultPanel);

        doReturn(defaultPanel).when(presenter).makeDefaultPanel();

        presenter.setupAccordionWidget(accordionType, setWidget);

        verify(accordion).addItem(accordionType, defaultPanel);
    }

    @Test
    public void testRefreshAttributeWidgetWhenAttributeColumnsIsEmpty() {

        final List<AttributeCol52> attributeColumns = new ArrayList<>();
        final AttributeColumnConfigRow attributeColumnConfigRow = mock(AttributeColumnConfigRow.class);
        final Label blankSlate = mock(Label.class);

        doReturn(verticalPanel).when(presenter).getAttributeWidget();
        doReturn(attributeColumnConfigRow).when(this.attributeColumnConfigRow).get();
        doReturn(blankSlate).when(presenter).blankSlate();

        presenter.refreshAttributeWidget(attributeColumns);

        verify(verticalPanel).add(blankSlate);
        verify(attributeColumnConfigRow, never()).init(any(), any());
    }

    @Test
    public void testRefreshAttributeWidgetWhenAttributeColumnsIsNotEmpty() {

        final AttributeColumnConfigRow attributeColumnConfigRow = mock(AttributeColumnConfigRow.class);
        final Label blankSlate = mock(Label.class);
        final AttributeCol52 attributeCol52 = mock(AttributeCol52.class);
        final Widget widget = mock(Widget.class);
        final List<AttributeCol52> attributeColumns = new ArrayList<AttributeCol52>() {{
            add(attributeCol52);
        }};

        doReturn(verticalPanel).when(presenter).getAttributeWidget();
        doReturn(attributeColumnConfigRow).when(this.attributeColumnConfigRow).get();
        doReturn(widget).when(attributeColumnConfigRow).getView();
        doReturn(blankSlate).when(presenter).blankSlate();

        presenter.refreshAttributeWidget(attributeColumns);

        verify(attributeColumnConfigRow).init(attributeCol52, modeller);
        verify(verticalPanel, never()).add(blankSlate);
        verify(verticalPanel).add(widget);
    }

    @Test
    public void testRefreshMetaDataWidgetWhenModellerIsNull() {

        final List<MetadataCol52> metaDataColumns = new ArrayList<>();

        doReturn(null).when(presenter).getModeller();
        doReturn(verticalPanel).when(presenter).getMetaDataWidget();

        presenter.refreshMetaDataWidget(metaDataColumns);

        verify(verticalPanel, never()).clear();
        verify(verticalPanel, never()).add(any());
    }

    @Test
    public void testRefreshMetaDataWidgetWhenModellerIsNotNullAndMetaDataColumnsIsEmpty() {

        final List<MetadataCol52> metaDataColumns = new ArrayList<>();
        final Label blankSlate = mock(Label.class);

        doReturn(verticalPanel).when(presenter).getMetaDataWidget();
        doReturn(blankSlate).when(presenter).blankSlate();

        presenter.refreshMetaDataWidget(metaDataColumns);

        verify(verticalPanel).clear();
        verify(verticalPanel).add(blankSlate);
    }

    @Test
    public void testRefreshMetaDataWidgetWhenModellerIsNotNullAndMetaDataColumnsIsNotEmpty() {

        final MetadataCol52 metadataColumn1 = mock(MetadataCol52.class);
        final MetadataCol52 metadataColumn2 = mock(MetadataCol52.class);
        final HorizontalPanel metaDataWidget1 = mock(HorizontalPanel.class);
        final HorizontalPanel metaDataWidget2 = mock(HorizontalPanel.class);
        final List<MetadataCol52> metaDataColumns = new ArrayList<MetadataCol52>() {{
            add(metadataColumn1);
            add(metadataColumn2);
        }};

        doReturn(verticalPanel).when(presenter).getMetaDataWidget();
        doReturn(metaDataWidget1).when(presenter).makeMetaDataWidget(modeller, metadataColumn1);
        doReturn(metaDataWidget2).when(presenter).makeMetaDataWidget(modeller, metadataColumn2);

        presenter.refreshMetaDataWidget(metaDataColumns);

        verify(verticalPanel).clear();
        verify(verticalPanel).add(metaDataWidget1);
        verify(verticalPanel).add(metaDataWidget2);
    }

    @Test
    public void testMakeMetaDataWidgetWhenDecisionTableIsEditable() {

        final HorizontalPanel expectedHorizontalPanel = mock(HorizontalPanel.class);
        final ColumnLabelWidget columnLabelWidget = mock(ColumnLabelWidget.class);
        final CheckBox hideColumnCheckBox = mock(CheckBox.class);
        final DeleteColumnManagementAnchorWidget deleteColumnManagementAnchorWidget = mock(DeleteColumnManagementAnchorWidget.class);
        final boolean isEditable = true;

        doReturn(expectedHorizontalPanel).when(presenter).makeHorizontalPanel();
        doReturn(columnLabelWidget).when(presenter).makeColumnLabel(metadataColumn);
        doReturn(hideColumnCheckBox).when(presenter).hideColumnCheckBox(modeller, metadataColumn);
        doReturn(deleteColumnManagementAnchorWidget).when(presenter).deleteMetaDataColumnAnchor(modeller, metadataColumn);
        doReturn(isEditable).when(modeller).isActiveDecisionTableEditable();

        final HorizontalPanel actualHorizontalPanel = presenter.makeMetaDataWidget(modeller, metadataColumn);

        verify(actualHorizontalPanel).setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        verify(actualHorizontalPanel).add(columnLabelWidget);
        verify(actualHorizontalPanel).add(hideColumnCheckBox);
        verify(actualHorizontalPanel).add(deleteColumnManagementAnchorWidget);

        assertEquals(expectedHorizontalPanel, actualHorizontalPanel);
    }

    @Test
    public void testMakeMetaDataWidgetWhenDecisionTableIsNotEditable() {

        final HorizontalPanel expectedHorizontalPanel = mock(HorizontalPanel.class);
        final ColumnLabelWidget columnLabelWidget = mock(ColumnLabelWidget.class);
        final CheckBox hideColumnCheckBox = mock(CheckBox.class);
        final DeleteColumnManagementAnchorWidget deleteColumnManagementAnchorWidget = mock(DeleteColumnManagementAnchorWidget.class);
        final boolean isEditable = false;

        doReturn(expectedHorizontalPanel).when(presenter).makeHorizontalPanel();
        doReturn(columnLabelWidget).when(presenter).makeColumnLabel(metadataColumn);
        doReturn(hideColumnCheckBox).when(presenter).hideColumnCheckBox(modeller, metadataColumn);
        doReturn(deleteColumnManagementAnchorWidget).when(presenter).deleteMetaDataColumnAnchor(modeller, metadataColumn);
        doReturn(isEditable).when(modeller).isActiveDecisionTableEditable();

        final HorizontalPanel actualHorizontalPanel = presenter.makeMetaDataWidget(modeller, metadataColumn);

        verify(actualHorizontalPanel).setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        verify(actualHorizontalPanel).add(columnLabelWidget);
        verify(actualHorizontalPanel).add(hideColumnCheckBox);
        verify(actualHorizontalPanel, never()).add(deleteColumnManagementAnchorWidget);

        assertEquals(expectedHorizontalPanel, actualHorizontalPanel);
    }

    @Test
    public void testDeleteMetaDataColumnAnchor() {

        final DeleteColumnManagementAnchorWidget expectedWidget = mock(DeleteColumnManagementAnchorWidget.class);
        final String columnHeader = "columnHeader";
        final Command command = mock(Command.class);

        doReturn(columnHeader).when(metadataColumn).getMetadata();
        doReturn(expectedWidget).when(deleteColumnManagementAnchorWidgets).get();
        doReturn(command).when(presenter).deleteMetadataCommand(modeller, metadataColumn);

        final DeleteColumnManagementAnchorWidget actualWidget = presenter.deleteMetaDataColumnAnchor(modeller, metadataColumn);

        verify(actualWidget).init(columnHeader, command);

        assertEquals(expectedWidget, actualWidget);
    }

    @Test
    public void testDeleteMetadataCommandWhenVetoExceptionIsNotRaised() throws Exception {

        final GuidedDecisionTableView.Presenter activeDecisionTable = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(Optional.of(activeDecisionTable)).when(modeller).getActiveDecisionTable();

        presenter.deleteMetadataCommand(modeller, metadataColumn).execute();

        verify(activeDecisionTable).deleteColumn(metadataColumn);
        verify(presenter, never()).showGenericVetoMessage();
    }

    @Test
    public void testDeleteMetadataCommandWhenVetoExceptionIsRaised() throws Exception {

        final GuidedDecisionTableView.Presenter activeDecisionTable = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(Optional.of(activeDecisionTable)).when(modeller).getActiveDecisionTable();
        doThrow(ModelSynchronizer.VetoException.class).when(activeDecisionTable).deleteColumn(metadataColumn);
        doNothing().when(presenter).showGenericVetoMessage();

        presenter.deleteMetadataCommand(modeller, metadataColumn).execute();

        verify(presenter).showGenericVetoMessage();
    }

    @Test
    public void testHideColumnCheckBox() {

        final CheckBox expectedCheckBox = mock(CheckBox.class);
        final ClickHandler clickHandler = mock(ClickHandler.class);
        final boolean isHideColumn = true;

        doReturn(isHideColumn).when(metadataColumn).isHideColumn();
        doReturn(expectedCheckBox).when(presenter).makeCheckBox(eq("HideThisColumn:"));
        doReturn(clickHandler).when(presenter).hideMetadataClickHandler(modeller, expectedCheckBox, metadataColumn);

        final CheckBox actualCheckBox = presenter.hideColumnCheckBox(modeller, metadataColumn);

        verify(expectedCheckBox).setValue(isHideColumn);
        verify(expectedCheckBox).addClickHandler(clickHandler);

        assertEquals(expectedCheckBox, actualCheckBox);
    }

    @Test
    public void testHideMetadataClickHandlerWhenVetoExceptionIsNotRaised() throws Exception {

        final MetadataCol52 clone = mock(MetadataCol52.class);
        final CheckBox checkBox = mock(CheckBox.class);
        final ClickEvent clickEvent = mock(ClickEvent.class);
        final GuidedDecisionTableView.Presenter activeDecisionTable = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(Optional.of(activeDecisionTable)).when(modeller).getActiveDecisionTable();
        doReturn(clone).when(metadataColumn).cloneColumn();

        final ClickHandler clickHandler = presenter.hideMetadataClickHandler(modeller, checkBox, metadataColumn);

        clickHandler.onClick(clickEvent);

        verify(clone).setHideColumn(checkBox.getValue());
        verify(activeDecisionTable).updateColumn(metadataColumn, clone);
    }

    @Test
    public void testHideMetadataClickHandlerWhenVetoExceptionIsRaised() throws Exception {

        final MetadataCol52 clone = mock(MetadataCol52.class);
        final CheckBox checkBox = mock(CheckBox.class);
        final ClickEvent clickEvent = mock(ClickEvent.class);
        final GuidedDecisionTableView.Presenter activeDecisionTable = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(Optional.of(activeDecisionTable)).when(modeller).getActiveDecisionTable();
        doReturn(clone).when(metadataColumn).cloneColumn();
        doThrow(ModelSynchronizer.VetoException.class).when(activeDecisionTable).updateColumn(metadataColumn, clone);
        doNothing().when(presenter).showGenericVetoMessage();

        final ClickHandler clickHandler = presenter.hideMetadataClickHandler(modeller, checkBox, metadataColumn);

        clickHandler.onClick(clickEvent);

        verify(clone).setHideColumn(checkBox.getValue());
        verify(presenter).showGenericVetoMessage();
    }

    @Test
    public void testRefreshConditionsWidgetWhenConditionColumnsIsEmpty() {

        final List<CompositeColumn<? extends BaseColumn>> conditions = new ArrayList<>();
        final ColumnManagementView columnManagementView = mock(ColumnManagementView.class);
        final GuidedDecisionTableAccordionItem item = mock(GuidedDecisionTableAccordionItem.class);
        final Label blankSlate = mock(Label.class);

        doReturn(verticalPanel).when(presenter).getConditionsWidget();
        doReturn(columnManagementView).when(presenter).getConditionsPanel();
        doReturn(blankSlate).when(presenter).blankSlate();
        doReturn(item).when(accordion).getItem(CONDITION);

        presenter.refreshConditionsWidget(conditions);

        verify(item).setOpen(false);
        verify(verticalPanel).add(blankSlate);
        verify(verticalPanel, never()).add(columnManagementView);
        verify(columnManagementView, never()).renderColumns(any());
    }

    @Test
    public void testRefreshConditionsWidgetWhenConditionColumnsIsNotEmpty() {

        final ColumnManagementView columnManagementView = mock(ColumnManagementView.class);
        final GuidedDecisionTableAccordionItem item = mock(GuidedDecisionTableAccordionItem.class);
        final Label blankSlate = mock(Label.class);
        final List<CompositeColumn<? extends BaseColumn>> conditions1 = new ArrayList<CompositeColumn<? extends BaseColumn>>() {{
            add(compositeColumn1);
        }};
        final Map<String, List<BaseColumn>> conditions2 = new HashMap<String, List<BaseColumn>>() {{
            put("title", new ArrayList<>());
        }};

        doReturn(verticalPanel).when(presenter).getConditionsWidget();
        doReturn(columnManagementView).when(presenter).getConditionsPanel();
        doReturn(blankSlate).when(presenter).blankSlate();
        doReturn(item).when(accordion).getItem(CONDITION);
        doReturn(conditions2).when(presenter).groupByTitle(conditions1);

        presenter.refreshConditionsWidget(conditions1);

        verify(item, never()).setOpen(false);
        verify(verticalPanel, never()).add(blankSlate);
        verify(verticalPanel).add(columnManagementView);
        verify(columnManagementView).renderColumns(conditions2);
    }

    @Test
    public void testGroupByTitle() {

        final List<CompositeColumn<? extends BaseColumn>> conditions = new ArrayList<CompositeColumn<? extends BaseColumn>>() {{
            add(compositeColumn1);
            add(compositeColumn2);
            add(compositeColumn3);
        }};

        final String title1 = "title1";
        final String title2 = "title2";

        doReturn(title1).when(compositeColumn1).getHeader();
        doReturn(title2).when(compositeColumn2).getHeader();
        doReturn(title1).when(compositeColumn3).getHeader();

        final Map<String, List<BaseColumn>> columnGroups = presenter.groupByTitle(conditions);

        final List<BaseColumn> actualTitle1List = columnGroups.get(title1);
        final List<BaseColumn> actualTitle2List = columnGroups.get(title2);

        final ArrayList<CompositeColumn<? extends BaseColumn>> expectedTitle1List = new ArrayList<CompositeColumn<? extends BaseColumn>>() {{
            add(compositeColumn1);
            add(compositeColumn3);
        }};

        final ArrayList<CompositeColumn<? extends BaseColumn>> expectedTitle2List = new ArrayList<CompositeColumn<? extends BaseColumn>>() {{
            add(compositeColumn2);
        }};

        assertEquals(2, columnGroups.size());
        assertEquals(actualTitle1List, expectedTitle1List);
        assertEquals(actualTitle2List, expectedTitle2List);
    }

    @Test
    public void testRefreshActionsWidgetWhenActionColumnsIsEmpty() {

        final List<ActionCol52> actionColumns = new ArrayList<>();
        final ColumnManagementView columnManagementView = mock(ColumnManagementView.class);
        final GuidedDecisionTableAccordionItem item = mock(GuidedDecisionTableAccordionItem.class);
        final Label blankSlate = mock(Label.class);

        doReturn(verticalPanel).when(presenter).getActionsWidget();
        doReturn(columnManagementView).when(presenter).getActionsPanel();
        doReturn(blankSlate).when(presenter).blankSlate();
        doReturn(item).when(accordion).getItem(ACTION);

        presenter.refreshActionsWidget(actionColumns);

        verify(item).setOpen(false);
        verify(verticalPanel).add(blankSlate);
        verify(verticalPanel, never()).add(columnManagementView);
        verify(columnManagementView, never()).renderColumns(any());
    }

    @Test
    public void testRefreshActionsWidgetWhenActionColumnsIsNotEmpty() {

        final ColumnManagementView columnManagementView = mock(ColumnManagementView.class);
        final GuidedDecisionTableAccordionItem item = mock(GuidedDecisionTableAccordionItem.class);
        final Label blankSlate = mock(Label.class);
        final ActionCol52 actionCol52 = mock(ActionCol52.class);
        final List<ActionCol52> actionColumns1 = new ArrayList<ActionCol52>() {{
            add(actionCol52);
        }};
        final Map<String, List<BaseColumn>> actionColumns2 = new HashMap<String, List<BaseColumn>>() {{
            put("title", new ArrayList<>());
        }};

        doReturn(verticalPanel).when(presenter).getActionsWidget();
        doReturn(columnManagementView).when(presenter).getActionsPanel();
        doReturn(blankSlate).when(presenter).blankSlate();
        doReturn(item).when(accordion).getItem(ACTION);
        doReturn(actionColumns2).when(presenter).groupByTitle(actionColumns1);

        presenter.refreshActionsWidget(actionColumns1);

        verify(item, never()).setOpen(false);
        verify(verticalPanel, never()).add(blankSlate);
        verify(verticalPanel).add(columnManagementView);
        verify(columnManagementView).renderColumns(actionColumns2);
    }

    @Test
    public void testOnUpdatedLockStatusEventWhenActiveDecisionTableHasAValidPath() {

        final UpdatedLockStatusEvent event = mock(UpdatedLockStatusEvent.class);
        final GuidedDecisionTableView.Presenter activeDecisionTable = mock(GuidedDecisionTableView.Presenter.class);
        final ObservablePath path = mock(ObservablePath.class);

        doReturn(true).when(presenter).hasActiveDecisionTable();
        doReturn(Optional.of(activeDecisionTable)).when(modeller).getActiveDecisionTable();
        doReturn(path).when(activeDecisionTable).getCurrentPath();
        doReturn(path).when(event).getFile();
        doNothing().when(presenter).refresh();

        presenter.onUpdatedLockStatusEvent(event);

        verify(presenter).refresh();
    }

    @Test
    public void testOnUpdatedLockStatusEventWhenActiveDecisionTableHasAnInvalidPath() {

        final UpdatedLockStatusEvent event = mock(UpdatedLockStatusEvent.class);
        final GuidedDecisionTableView.Presenter activeDecisionTable = mock(GuidedDecisionTableView.Presenter.class);
        final ObservablePath path = mock(ObservablePath.class);

        doReturn(true).when(presenter).hasActiveDecisionTable();
        doReturn(Optional.of(activeDecisionTable)).when(modeller).getActiveDecisionTable();
        doReturn(path).when(activeDecisionTable).getCurrentPath();
        doReturn(null).when(event).getFile();

        presenter.onUpdatedLockStatusEvent(event);

        verify(presenter, never()).refresh();
    }

    @Test
    public void testOnUpdatedLockStatusEventWhenDoesNotHaveActiveDecisionTable() {

        final UpdatedLockStatusEvent event = mock(UpdatedLockStatusEvent.class);

        doReturn(false).when(presenter).hasActiveDecisionTable();

        presenter.onUpdatedLockStatusEvent(event);

        verify(presenter, never()).refresh();
    }

    @Test
    public void testHasActiveDecisionTableWhenModellerIsNull() {

        doReturn(null).when(presenter).getModeller();

        final boolean hasActiveDecisionTable = presenter.hasActiveDecisionTable();

        assertFalse(hasActiveDecisionTable);
    }

    @Test
    public void testHasActiveDecisionTableWhenActiveDecisionTableIsNull() {

        doReturn(null).when(modeller).getActiveDecisionTable();

        final boolean hasActiveDecisionTable = presenter.hasActiveDecisionTable();

        assertFalse(hasActiveDecisionTable);
    }

    @Test
    public void testHasActiveDecisionTableWhenActiveDecisionTableIsNotNull() {

        doReturn(Optional.of(mock(GuidedDecisionTableView.Presenter.class))).when(modeller).getActiveDecisionTable();

        final boolean hasActiveDecisionTable = presenter.hasActiveDecisionTable();

        assertTrue(hasActiveDecisionTable);
    }

    @Test
    public void testOnRefreshAttributesPanelEvent() {

        final RefreshAttributesPanelEvent event = mock(RefreshAttributesPanelEvent.class);
        final GuidedDecisionTableView.Presenter eventPresenter = mock(GuidedDecisionTableView.Presenter.class);
        final List<AttributeCol52> columns = new ArrayList<>();

        doReturn(eventPresenter).when(event).getPresenter();
        doReturn(columns).when(event).getColumns();
        doNothing().when(presenter).refreshAttributeWidget(any());
        doNothing().when(presenter).refreshColumnsNoteInfo(any());

        presenter.onRefreshAttributesPanelEvent(event);

        verify(presenter).refreshAttributeWidget(columns);
        verify(presenter).refreshColumnsNoteInfo(eventPresenter);
    }

    @Test
    public void testOnRefreshMetaDataPanelEvent() {

        final RefreshMetaDataPanelEvent event = mock(RefreshMetaDataPanelEvent.class);
        final GuidedDecisionTableView.Presenter eventPresenter = mock(GuidedDecisionTableView.Presenter.class);
        final List<MetadataCol52> columns = new ArrayList<>();

        doReturn(eventPresenter).when(event).getPresenter();
        doReturn(columns).when(event).getColumns();
        doNothing().when(presenter).refreshMetaDataWidget(any());
        doNothing().when(presenter).refreshColumnsNoteInfo(any());

        presenter.onRefreshMetaDataPanelEvent(event);

        verify(presenter).refreshMetaDataWidget(columns);
        verify(presenter).refreshColumnsNoteInfo(eventPresenter);
    }

    @Test
    public void testOnRefreshConditionsPanelEvent() {

        final RefreshConditionsPanelEvent event = mock(RefreshConditionsPanelEvent.class);
        final GuidedDecisionTableView.Presenter eventPresenter = mock(GuidedDecisionTableView.Presenter.class);
        final List<CompositeColumn<? extends BaseColumn>> columns = new ArrayList<>();

        doReturn(eventPresenter).when(event).getPresenter();
        doReturn(columns).when(event).getColumns();
        doNothing().when(presenter).refreshConditionsWidget(any());
        doNothing().when(presenter).refreshColumnsNoteInfo(any());

        presenter.onRefreshConditionsPanelEvent(event);

        verify(presenter).refreshConditionsWidget(columns);
        verify(presenter).refreshColumnsNoteInfo(eventPresenter);
    }

    @Test
    public void testOnRefreshActionsPanelEvent() {

        final RefreshActionsPanelEvent event = mock(RefreshActionsPanelEvent.class);
        final GuidedDecisionTableView.Presenter eventPresenter = mock(GuidedDecisionTableView.Presenter.class);
        final List<ActionCol52> columns = new ArrayList<>();

        doReturn(eventPresenter).when(event).getPresenter();
        doReturn(columns).when(event).getColumns();
        doNothing().when(presenter).refreshActionsWidget(any());
        doNothing().when(presenter).refreshColumnsNoteInfo(any());

        presenter.onRefreshActionsPanelEvent(event);

        verify(presenter).refreshActionsWidget(columns);
        verify(presenter).refreshColumnsNoteInfo(eventPresenter);
    }

    @Test
    public void testSetupColumnsNoteInfoWhenActiveDecisionTableHasColumnDefinitions() {

        final GuidedDecisionTableView.Presenter activeDecisionTable = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(Optional.of(activeDecisionTable)).when(modeller).getActiveDecisionTable();
        doReturn(true).when(activeDecisionTable).hasColumnDefinitions();

        presenter.setupColumnsNoteInfo(modeller);

        verify(view).setColumnsNoteInfoAsHidden();
    }

    @Test
    public void testSetupColumnsNoteInfoWhenActiveDecisionTableDoesNotHaveColumnDefinitions() {

        final GuidedDecisionTableView.Presenter activeDecisionTable = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(Optional.of(activeDecisionTable)).when(modeller).getActiveDecisionTable();
        doReturn(false).when(activeDecisionTable).hasColumnDefinitions();

        presenter.setupColumnsNoteInfo(modeller);

        verify(view).setColumnsNoteInfoAsVisible();
    }

    @Test
    public void testSetupAccordion() {

        presenter.setupAccordion();

        verify(accordion).clear();
        verify(presenter).setupAccordionWidgets();
        verify(view).setAccordion(accordion);
    }

    @Test
    public void testRuleInheritanceWidget() {

        final FlowPanel expectedPanel = mock(FlowPanel.class);
        final Label label = mock(Label.class);
        final Widget widget = mock(Widget.class);

        doReturn(expectedPanel).when(presenter).makeFlowPanel();
        doReturn(label).when(presenter).ruleInheritanceLabel();
        doReturn(widget).when(presenter).ruleSelector();

        final Widget actualPanel = presenter.ruleInheritanceWidget();

        verify(expectedPanel).setStyleName(GuidedDecisionTableResources.INSTANCE.css().ruleInheritance());
        verify(expectedPanel).add(label);
        verify(expectedPanel).add(widget);

        assertEquals(expectedPanel, actualPanel);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRuleSelectorWidget() {
        final RuleSelector ruleSelector = mock(RuleSelector.class);
        final GuidedDecisionTableView.Presenter dtPresenter = mock(GuidedDecisionTableView.Presenter.class);
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final Collection<String> ruleNames = Collections.singletonList("rule");
        model.setParentName("parent");

        doReturn(ruleSelector).when(presenter).makeRuleSelector();
        when(modeller.getActiveDecisionTable()).thenReturn(Optional.of(dtPresenter));
        when(dtPresenter.getModel()).thenReturn(model);

        final ArgumentCaptor<ValueChangeHandler> valueChangeHandlerCaptor = ArgumentCaptor.forClass(ValueChangeHandler.class);
        final ArgumentCaptor<ParameterizedCommand> commandCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);

        presenter.ruleSelector();

        verify(ruleSelector).addValueChangeHandler(valueChangeHandlerCaptor.capture());
        valueChangeHandlerCaptor.getValue().onValueChange(mock(ValueChangeEvent.class));
        verify(dtPresenter).setParentRuleName(anyString());
        verify(presenter).setupRuleSelector(eq(dtPresenter));

        verify(dtPresenter).getPackageParentRuleNames(commandCaptor.capture());
        commandCaptor.getValue().execute(ruleNames);
        verify(ruleSelector).setRuleName(eq("parent"));
        verify(ruleSelector).setRuleNames(eq(ruleNames));
    }

    @Test
    public void testRuleSelectorWidgetWitNoActiveDecisionTable() {
        final RuleSelector ruleSelector = mock(RuleSelector.class);
        when(modeller.getActiveDecisionTable()).thenReturn(Optional.empty());
        doReturn(ruleSelector).when(presenter).makeRuleSelector();

        presenter.ruleSelector();

        verify(presenter, never()).setupRuleSelector(any(GuidedDecisionTableView.Presenter.class));
    }

    @Test
    public void testOpenNewGuidedDecisionTableColumnWizardWhenColumnCreatingIsNotEnabled() {

        final NewGuidedDecisionTableColumnWizard wizard = mock(NewGuidedDecisionTableColumnWizard.class);

        doReturn(false).when(presenter).isColumnCreationEnabledToActiveDecisionTable();
        doReturn(wizard).when(wizardManagedInstance).get();

        presenter.openNewGuidedDecisionTableColumnWizard();

        verify(wizard, never()).init(any());
        verify(wizard, never()).start();
    }

    @Test
    public void testOpenNewGuidedDecisionTableColumnWizardWhenColumnCreatingIsEnabled() {

        final NewGuidedDecisionTableColumnWizard wizard = mock(NewGuidedDecisionTableColumnWizard.class);
        final GuidedDecisionTableView.Presenter activeDecisionTable = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(true).when(presenter).isColumnCreationEnabledToActiveDecisionTable();
        doReturn(Optional.of(activeDecisionTable)).when(modeller).getActiveDecisionTable();
        doReturn(wizard).when(wizardManagedInstance).get();

        presenter.openNewGuidedDecisionTableColumnWizard();

        verify(wizard).init(activeDecisionTable);
        verify(wizard).start();
    }

    @Test
    public void testIsColumnCreationEnabledToActiveDecisionTableWhenPresenterDoesNotHaveActiveDecisionTable() {

        doReturn(false).when(presenter).hasActiveDecisionTable();

        final boolean isColumnCreationEnabled = presenter.isColumnCreationEnabledToActiveDecisionTable();

        assertFalse(isColumnCreationEnabled);
    }

    @Test
    public void testIsColumnCreationEnabledToActiveDecisionTableWhenPresenterHasActiveDecisionTable() {

        doReturn(true).when(presenter).hasActiveDecisionTable();
        doReturn(true).when(presenter).isColumnCreationEnabled(any());

        final boolean isColumnCreationEnabled = presenter.isColumnCreationEnabledToActiveDecisionTable();

        assertTrue(isColumnCreationEnabled);
    }

    @Test
    public void testIsColumnCreationEnabledWhenActiveDecisionTableIsReadOnly() {

        final GuidedDecisionTableView.Presenter dtPresenter = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(true).when(dtPresenter).isReadOnly();

        final boolean isColumnCreationEnabled = presenter.isColumnCreationEnabled(Optional.of(dtPresenter));

        assertFalse(isColumnCreationEnabled);
    }

    @Test
    public void testIsColumnCreationEnabledWhenActiveDecisionTableDoesNotHaveEditableColumns() {

        final GuidedDecisionTableView.Presenter dtPresenter = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(false).when(dtPresenter).isReadOnly();
        doReturn(false).when(dtPresenter).hasEditableColumns();

        final boolean isColumnCreationEnabled = presenter.isColumnCreationEnabled(Optional.of(dtPresenter));

        assertFalse(isColumnCreationEnabled);
    }

    @Test
    public void testIsColumnCreationEnabledWhenActiveDecisionTableHasEditableColumns() {

        final GuidedDecisionTableView.Presenter dtPresenter = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(false).when(dtPresenter).isReadOnly();
        doReturn(true).when(dtPresenter).hasEditableColumns();

        final boolean isColumnCreationEnabled = presenter.isColumnCreationEnabled(Optional.of(dtPresenter));

        assertTrue(isColumnCreationEnabled);
    }

    @Test
    public void testOnDecisionTableSelectedWhenPresenterDoesNotHaveActiveDecisionTable() {

        final DecisionTableSelectedEvent event = mock(DecisionTableSelectedEvent.class);

        doReturn(false).when(presenter).hasActiveDecisionTable();

        presenter.onDecisionTableSelected(event);

        verify(presenter, never()).setupRuleSelector(any());
    }

    @Test
    public void testOnDecisionTableSelectedWhenEventDoesNotHaveActiveDecisionTable() {

        final DecisionTableSelectedEvent event = mock(DecisionTableSelectedEvent.class);

        doReturn(true).when(presenter).hasActiveDecisionTable();
        doReturn(Optional.empty()).when(event).getPresenter();

        presenter.onDecisionTableSelected(event);

        verify(presenter, never()).setupRuleSelector(any());
    }

    @Test
    public void testOnDecisionTableSelectedWhenEventAndActiveDecisionTableAreEqual() {

        final DecisionTableSelectedEvent event = mock(DecisionTableSelectedEvent.class);
        final GuidedDecisionTableView.Presenter presenterDecisionTable = mock(GuidedDecisionTableView.Presenter.class);

        doReturn(true).when(presenter).hasActiveDecisionTable();
        doReturn(Optional.of(presenterDecisionTable)).when(modeller).getActiveDecisionTable();
        doReturn(Optional.of(presenterDecisionTable)).when(event).getPresenter();

        presenter.onDecisionTableSelected(event);

        verify(presenter, never()).setupRuleSelector(any());
    }

    @Test
    public void testOnDecisionTableSelectedWhenEventAndActiveDecisionTableAreDifferent() {

        final DecisionTableSelectedEvent event = mock(DecisionTableSelectedEvent.class);
        final GuidedDecisionTableView.Presenter presenterDecisionTable = mock(GuidedDecisionTableView.Presenter.class);
        final GuidedDecisionTableView.Presenter eventDecisionTable = mock(GuidedDecisionTableView.Presenter.class);
        final Optional<GuidedDecisionTableView.Presenter> presenterOptional = Optional.ofNullable(eventDecisionTable);

        doReturn(true).when(presenter).hasActiveDecisionTable();
        doReturn(Optional.of(presenterDecisionTable)).when(modeller).getActiveDecisionTable();
        doReturn(presenterOptional).when(event).getPresenter();
        doNothing().when(presenter).setupRuleSelector(any());

        presenter.onDecisionTableSelected(event);

        verify(presenter).setupRuleSelector(eventDecisionTable);
    }

    @Test
    public void testRefresh() {

        final GuidedDecisionTable52 guidedDecisionTable52 = mock(GuidedDecisionTable52.class);

        doReturn(Optional.of(guidedDecisionTable52)).when(presenter).getGuidedDecisionTable52();

        doNothing().when(presenter).refreshAttributeWidget(any());
        doNothing().when(presenter).refreshMetaDataWidget(any());
        doNothing().when(presenter).refreshConditionsWidget(any());
        doNothing().when(presenter).refreshActionsWidget(any());

        final List<AttributeCol52> attributeCol52s = new ArrayList<>();
        final List<MetadataCol52> metadataCol52s = new ArrayList<>();
        final List<CompositeColumn<? extends BaseColumn>> compositeColumns = new ArrayList<>();
        final List<ActionCol52> actionCol52s = new ArrayList<>();

        doReturn(attributeCol52s).when(guidedDecisionTable52).getAttributeCols();
        doReturn(metadataCol52s).when(guidedDecisionTable52).getAttributeCols();
        doReturn(compositeColumns).when(guidedDecisionTable52).getAttributeCols();
        doReturn(actionCol52s).when(guidedDecisionTable52).getAttributeCols();

        presenter.refresh();

        verify(presenter).refreshAttributeWidget(attributeCol52s);
        verify(presenter).refreshMetaDataWidget(metadataCol52s);
        verify(presenter).refreshConditionsWidget(compositeColumns);
        verify(presenter).refreshActionsWidget(actionCol52s);
    }

    @Test
    public void testRefreshColumnsNoteInfo() {

        final GuidedDecisionTableView.Presenter viewPresenter = mock(GuidedDecisionTableView.Presenter.class);
        final GuidedDecisionTableModellerView.Presenter modeller = mock(GuidedDecisionTableModellerView.Presenter.class);

        doReturn(modeller).when(viewPresenter).getModellerPresenter();
        doNothing().when(presenter).setupColumnsNoteInfo(any());

        presenter.refreshColumnsNoteInfo(viewPresenter);

        verify(presenter).setupColumnsNoteInfo(modeller);
    }
}
