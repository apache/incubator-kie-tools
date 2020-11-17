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

package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoDeletePatternInUseException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ColumnManagementViewTest {

    @Mock
    private GuidedDecisionTableModellerView modellerView;

    @Mock
    private GuidedDecisionTableModellerView.Presenter modellerPresenter;

    @Mock
    private GuidedDecisionTableView.Presenter decisionTablePresenter;

    @Mock
    private HorizontalPanel horizontalPanel;

    @Mock
    private DeleteColumnManagementAnchorWidget deleteWidget;

    @Mock
    private ManagedInstance<DeleteColumnManagementAnchorWidget> deleteColumnManagementAnchorWidgets;

    @Captor
    private ArgumentCaptor<ClickHandler> clickHandlerCaptor;

    @Captor
    private ArgumentCaptor<Command> deleteCommandCaptor;

    private ColumnManagementView view;

    @Before
    public void setUp() throws Exception {
        doReturn(deleteWidget).when(deleteColumnManagementAnchorWidgets).get();

        view = spy(new ColumnManagementView(deleteColumnManagementAnchorWidgets));
        view.init(modellerPresenter);

        doReturn(modellerView).when(modellerPresenter).getView();
        doReturn(horizontalPanel).when(view).newHorizontalPanel();

        when(modellerPresenter.getActiveDecisionTable()).thenReturn(Optional.of(decisionTablePresenter));
    }

    private ColumnLabelWidget mockColumnLabelWidget() {
        return mock(ColumnLabelWidget.class);
    }

    @Test
    public void testRenderColumnsClearTheView() throws Exception {
        view.renderColumns(Collections.emptyMap());
        verify(view).clear();
    }

    @Test
    public void testRenderColumnPatternEditable() throws Exception {
        final Pattern52 pattern = new Pattern52() {{
            setBoundName("p");
            setFactType("Person");
            getChildColumns().add(new ConditionCol52() {{
                setHeader("one");
            }});
            getChildColumns().add(new ConditionCol52() {{
                setHeader("two");
            }});
            getChildColumns().add(new ConditionCol52() {{
                setHeader("three");
            }});
        }};

        final Map<String, List<BaseColumn>> columnGroups = new HashMap<String, List<BaseColumn>>() {{
            put("Person [p]",
                Collections.singletonList(pattern));
        }};

        final ColumnLabelWidget columnLabel = mockColumnLabelWidget();

        doReturn(columnLabel).when(view).newColumnLabelWidget(anyString());
        doReturn(true).when(modellerPresenter).isActiveDecisionTableEditable();

        view.renderColumns(columnGroups);

        verify(view).renderColumn(pattern);
        // Pattern has three child columns
        verify(horizontalPanel,
               times(3)).add(columnLabel);
        verify(view,
               times(3)).editAnchor(clickHandlerCaptor.capture());
        verify(view).deleteAnchor(eq("one"),
                                  any(Command.class));
        verify(view).deleteAnchor(eq("two"),
                                  any(Command.class));
        verify(view).deleteAnchor(eq("three"),
                                  any(Command.class));

        clickHandlerCaptor.getAllValues().get(0).onClick(mock(ClickEvent.class));
        verify(decisionTablePresenter).editCondition(eq(pattern),
                                                     eq(pattern.getChildColumns().get(0)));

        clickHandlerCaptor.getAllValues().get(1).onClick(mock(ClickEvent.class));
        verify(decisionTablePresenter).editCondition(eq(pattern),
                                                     eq(pattern.getChildColumns().get(1)));

        clickHandlerCaptor.getAllValues().get(2).onClick(mock(ClickEvent.class));
        verify(decisionTablePresenter).editCondition(eq(pattern),
                                                     eq(pattern.getChildColumns().get(2)));
    }

    @Test
    public void testRenderColumnPatternNotEditable() throws Exception {
        final Pattern52 pattern = new Pattern52() {{
            setBoundName("p");
            setFactType("Person");
            getChildColumns().add(new ConditionCol52());
            getChildColumns().add(new ConditionCol52());
            getChildColumns().add(new ConditionCol52());
        }};

        final Map<String, List<BaseColumn>> columnGroups = new HashMap<String, List<BaseColumn>>() {{
            put("Person [p]",
                Collections.singletonList(pattern));
        }};

        final ColumnLabelWidget columnLabel = mockColumnLabelWidget();

        doReturn(columnLabel).when(view).newColumnLabelWidget(anyString());

        view.renderColumns(columnGroups);

        verify(view).renderColumn(pattern);
        // Pattern has three child columns
        verify(horizontalPanel,
               times(3)).add(columnLabel);
        verify(view,
               times(3)).editAnchor(any(ClickHandler.class));
        verify(view,
               never()).deleteAnchor(eq("one"),
                                     any(Command.class));
        verify(view,
               never()).deleteAnchor(eq("two"),
                                     any(Command.class));
        verify(view,
               never()).deleteAnchor(eq("three"),
                                     any(Command.class));
    }

    @Test
    public void testRenderColumnBrlConditionEditable() throws Exception {
        final BRLConditionColumn conditionColumnOne = mock(BRLConditionColumn.class);
        final BRLConditionColumn conditionColumnTwo = mock(BRLConditionColumn.class);
        final Map<String, List<BaseColumn>> columnGroups = new HashMap<String, List<BaseColumn>>() {{
            put(GuidedDecisionTableConstants.INSTANCE.BrlConditions(),
                Arrays.asList(conditionColumnOne,
                              conditionColumnTwo));
        }};

        final ColumnLabelWidget columnLabel = mockColumnLabelWidget();

        doReturn(columnLabel).when(view).newColumnLabelWidget(anyString());
        doReturn(true).when(modellerPresenter).isActiveDecisionTableEditable();
        doReturn("brl one").when(conditionColumnOne).getHeader();
        doReturn("brl two").when(conditionColumnTwo).getHeader();

        view.renderColumns(columnGroups);

        verify(view).renderColumn(conditionColumnOne);
        verify(view).renderColumn(conditionColumnTwo);

        // There are two brl conditions
        verify(horizontalPanel,
               times(2)).add(columnLabel);
        verify(view,
               times(2)).editAnchor(clickHandlerCaptor.capture());
        verify(view).deleteAnchor(eq("brl one"),
                                  any(Command.class));
        verify(view).deleteAnchor(eq("brl two"),
                                  any(Command.class));

        clickHandlerCaptor.getAllValues().get(0).onClick(mock(ClickEvent.class));
        verify(decisionTablePresenter).editCondition(eq(conditionColumnOne));

        clickHandlerCaptor.getAllValues().get(1).onClick(mock(ClickEvent.class));
        verify(decisionTablePresenter).editCondition(eq(conditionColumnTwo));
    }

    @Test
    public void testRenderColumnBrlConditionNotEditable() throws Exception {
        final BRLConditionColumn conditionColumnOne = mock(BRLConditionColumn.class);
        final BRLConditionColumn conditionColumnTwo = mock(BRLConditionColumn.class);
        final Map<String, List<BaseColumn>> columnGroups = new HashMap<String, List<BaseColumn>>() {{
            put(GuidedDecisionTableConstants.INSTANCE.BrlConditions(),
                Arrays.asList(conditionColumnOne,
                              conditionColumnTwo));
        }};

        final ColumnLabelWidget columnLabel = mockColumnLabelWidget();

        doReturn(columnLabel).when(view).newColumnLabelWidget(anyString());
        doReturn("brl one").when(conditionColumnOne).getHeader();
        doReturn("brl two").when(conditionColumnTwo).getHeader();

        view.renderColumns(columnGroups);

        verify(view).renderColumn(conditionColumnOne);
        verify(view).renderColumn(conditionColumnTwo);

        // There are two brl conditions
        verify(horizontalPanel,
               times(2)).add(columnLabel);
        verify(view,
               times(2)).editAnchor(any(ClickHandler.class));
        verify(view,
               never()).deleteAnchor(eq("brl one"),
                                     any(Command.class));
        verify(view,
               never()).deleteAnchor(eq("brl two"),
                                     any(Command.class));
    }

    @Test
    public void testRenderColumnActionsEditable() throws Exception {
        final BRLActionColumn brlActionColumn = mock(BRLActionColumn.class);
        final ActionInsertFactCol52 actionInsertFactColumn = mock(ActionInsertFactCol52.class);
        final ActionRetractFactCol52 retractFactColumn = mock(ActionRetractFactCol52.class);
        final Map<String, List<BaseColumn>> columnGroups = new HashMap<String, List<BaseColumn>>() {{
            put(GuidedDecisionTableConstants.INSTANCE.BrlConditions(),
                Arrays.asList(brlActionColumn,
                              actionInsertFactColumn,
                              retractFactColumn));
        }};

        final ColumnLabelWidget columnLabel = mockColumnLabelWidget();

        doReturn(columnLabel).when(view).newColumnLabelWidget(anyString());
        doReturn(true).when(modellerPresenter).isActiveDecisionTableEditable();
        doReturn("one").when(brlActionColumn).getHeader();
        doReturn("two").when(actionInsertFactColumn).getHeader();
        doReturn("three").when(retractFactColumn).getHeader();

        view.renderColumns(columnGroups);

        verify(view).renderColumn(brlActionColumn);
        verify(view).renderColumn(actionInsertFactColumn);
        verify(view).renderColumn(retractFactColumn);

        // There are three action columns
        verify(horizontalPanel,
               times(3)).add(columnLabel);
        verify(view,
               times(3)).editAnchor(clickHandlerCaptor.capture());
        verify(view).deleteAnchor(eq("one"),
                                  deleteCommandCaptor.capture());
        verify(view).deleteAnchor(eq("two"),
                                  deleteCommandCaptor.capture());
        verify(view).deleteAnchor(eq("three"),
                                  deleteCommandCaptor.capture());

        clickHandlerCaptor.getAllValues().get(0).onClick(mock(ClickEvent.class));
        verify(decisionTablePresenter).editAction(eq(brlActionColumn));

        clickHandlerCaptor.getAllValues().get(1).onClick(mock(ClickEvent.class));
        verify(decisionTablePresenter).editAction(eq(actionInsertFactColumn));

        clickHandlerCaptor.getAllValues().get(2).onClick(mock(ClickEvent.class));
        verify(decisionTablePresenter).editAction(eq(retractFactColumn));

        deleteCommandCaptor.getAllValues().get(0).execute();
        verify(decisionTablePresenter).deleteColumn(brlActionColumn);

        deleteCommandCaptor.getAllValues().get(1).execute();
        verify(decisionTablePresenter).deleteColumn(actionInsertFactColumn);

        deleteCommandCaptor.getAllValues().get(2).execute();
        verify(decisionTablePresenter).deleteColumn(retractFactColumn);
    }

    @Test
    public void testRenderColumnActionsNotEditable() throws Exception {
        final BRLActionColumn brlActionColumn = mock(BRLActionColumn.class);
        final ActionInsertFactCol52 actionInsertFactColumn = mock(ActionInsertFactCol52.class);
        final ActionRetractFactCol52 retractFactColumn = mock(ActionRetractFactCol52.class);
        final Map<String, List<BaseColumn>> columnGroups = new HashMap<String, List<BaseColumn>>() {{
            put(GuidedDecisionTableConstants.INSTANCE.BrlConditions(),
                Arrays.asList(brlActionColumn,
                              actionInsertFactColumn,
                              retractFactColumn));
        }};

        final ColumnLabelWidget columnLabel = mockColumnLabelWidget();

        doReturn(columnLabel).when(view).newColumnLabelWidget(any());

        view.renderColumns(columnGroups);

        verify(view).renderColumn(brlActionColumn);
        verify(view).renderColumn(actionInsertFactColumn);
        verify(view).renderColumn(retractFactColumn);

        // There are three action columns
        verify(horizontalPanel,
               times(3)).add(columnLabel);
        verify(view,
               times(3)).editAnchor(any(ClickHandler.class));
        verify(view,
               never()).deleteAnchor(eq("one"),
                                     any(Command.class));
        verify(view,
               never()).deleteAnchor(eq("two"),
                                     any(Command.class));
        verify(view,
               never()).deleteAnchor(eq("three"),
                                     any(Command.class));
    }

    @Test
    public void testMakeColumnLabelCondition() throws Exception {
        testMakeColumnLabelForAllConditionColumns(false,
                                                  false);
    }

    @Test
    public void testMakeColumnLabelConditionHidden() throws Exception {
        testMakeColumnLabelForAllConditionColumns(true,
                                                  false);
    }

    @Test
    public void testMakeColumnLabelConditionBound() throws Exception {
        testMakeColumnLabelForAllConditionColumns(false,
                                                  true);
    }

    @Test
    public void testMakeColumnLabelConditionHiddenBound() throws Exception {
        testMakeColumnLabelForAllConditionColumns(true,
                                                  true);
    }

    @Test
    public void testMakeColumnLabelAction() throws Exception {
        testMakeColumnLabelForAllActionColumns(false);
    }

    @Test
    public void testMakeColumnLabelActionHidden() throws Exception {
        testMakeColumnLabelForAllActionColumns(true);
    }

    @Test
    public void testRemoveCondition() throws Exception {
        Stream.of(ConditionCol52.class,
                  BRLConditionColumn.class)
                .map(Mockito::mock)
                .forEach(column -> {
                    final String columnHeader = "column header";
                    final ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);

                    doReturn(Optional.of(decisionTablePresenter)).when(modellerPresenter).getActiveDecisionTable();
                    doReturn(columnHeader).when(column).getHeader();

                    view.removeCondition(column);

                    verify(view).deleteAnchor(eq("column header"),
                                              commandCaptor.capture());

                    commandCaptor.getValue().execute();

                    try {
                        verify(decisionTablePresenter).deleteColumn(column);
                    } catch (VetoException veto) {
                        fail("Deletion should have succeeded.");
                    }
                    reset(view);
                });
    }

    @Test
    public void testRemoveConditionCanNotBeDeleted() throws Exception {
        Stream.of(ConditionCol52.class,
                  BRLConditionColumn.class)
                .map(Mockito::mock)
                .forEach(column -> {
                    final String columnHeader = "column header";
                    final ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);

                    doReturn(Optional.of(decisionTablePresenter)).when(modellerPresenter).getActiveDecisionTable();
                    doReturn(columnHeader).when(column).getHeader();
                    doNothing().when(modellerView).showUnableToDeleteColumnMessage(column);

                    try {
                        doThrow(VetoDeletePatternInUseException.class).when(decisionTablePresenter).deleteColumn(eq(column));

                        view.removeCondition(column);

                        verify(view).deleteAnchor(eq("column header"),
                                                  commandCaptor.capture());

                        commandCaptor.getValue().execute();

                        verify(modellerView).showUnableToDeleteColumnMessage(eq(column));
                    } catch (VetoException veto) {
                    }
                    reset(view);
                });
    }

    private void testMakeColumnLabelForAllConditionColumns(final boolean isColumnHidden,
                                                           final boolean isColumnBound) {
        Stream.of(ConditionCol52.class,
                  BRLConditionColumn.class)
                .map(classToMock -> {
                    final ConditionCol52 column = mock(classToMock);
                    final ColumnLabelWidget columnLabel = mockColumnLabelWidget();

                    doReturn(columnLabel).when(view).newColumnLabelWidget(anyString());
                    doReturn(column.getClass().toString()).when(column).getHeader();
                    doReturn(isColumnHidden).when(column).isHideColumn();
                    doReturn(isColumnBound).when(column).isBound();
                    doReturn("binding").when(column).getBinding();
                    return column;
                })
                .forEach(column -> {
                    final ColumnLabelWidget label = view.makeColumnLabel(column);
                    verify(view).newColumnLabelWidget(isColumnBound ? "binding : " + column.getClass().toString() : column.getClass().toString());
                    testColumnLabelIsHidden(label,
                                            isColumnHidden);
                });
    }

    private void testMakeColumnLabelForAllActionColumns(final boolean isColumnHidden) {

        Stream.of(ActionInsertFactCol52.class,
                  ActionSetFieldCol52.class,
                  ActionWorkItemCol52.class,
                  ActionWorkItemInsertFactCol52.class,
                  ActionWorkItemSetFieldCol52.class,
                  ActionRetractFactCol52.class,
                  BRLActionColumn.class)
                .map(classToMock -> {
                    final ActionCol52 column = mock(classToMock);
                    final ColumnLabelWidget columnLabel = mockColumnLabelWidget();

                    doReturn(columnLabel).when(view).newColumnLabelWidget(anyString());
                    doReturn(column.getClass().toString()).when(column).getHeader();
                    doReturn(isColumnHidden).when(column).isHideColumn();
                    return column;
                })
                .forEach(column -> {
                    final ColumnLabelWidget label = view.makeColumnLabel(column);
                    verify(view).newColumnLabelWidget(column.getClass().toString());
                    testColumnLabelIsHidden(label,
                                            isColumnHidden);
                });
    }

    private void testColumnLabelIsHidden(final ColumnLabelWidget columnLabel,
                                         final boolean isColumnHidden) {
        if (isColumnHidden) {
            verify(columnLabel).addStyleName(GuidedDecisionTableResources.INSTANCE.css().columnLabelHidden());
        } else {
            verify(columnLabel).removeStyleName(GuidedDecisionTableResources.INSTANCE.css().columnLabelHidden());
        }
    }
}
