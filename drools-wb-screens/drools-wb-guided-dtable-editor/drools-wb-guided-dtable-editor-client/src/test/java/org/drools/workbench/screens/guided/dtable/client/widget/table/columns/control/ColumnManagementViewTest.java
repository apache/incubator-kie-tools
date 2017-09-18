/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ColumnManagementViewTest {

    @Mock
    GuidedDecisionTableModellerView.Presenter presenter;

    @Mock
    GuidedDecisionTableView.Presenter decisionTablePresenter;

    @Mock
    HorizontalPanel horizontalPanel;

    private ColumnManagementView view;

    @Before
    public void setUp() throws Exception {
        view = spy(new ColumnManagementView(presenter));

        doReturn(horizontalPanel).when(view).newHorizontalPanel();
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
        doReturn(true).when(presenter).isActiveDecisionTableEditable();

        view.renderColumns(columnGroups);

        verify(view).renderColumn(pattern);
        // Pattern has three child columns
        verify(horizontalPanel,
               times(3)).add(columnLabel);
        verify(view,
               times(3)).anchor(eq(GuidedDecisionTableConstants.INSTANCE.Edit()),
                                eq(GuidedDecisionTableConstants.INSTANCE.EditThisColumnConfiguration()),
                                any(ClickHandler.class));
        verify(view,
               times(3)).anchor(eq(GuidedDecisionTableConstants.INSTANCE.Delete()),
                                eq(GuidedDecisionTableConstants.INSTANCE.DeleteThisColumn()),
                                any(ClickHandler.class));
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
               times(3)).anchor(eq(GuidedDecisionTableConstants.INSTANCE.Edit()),
                                eq(GuidedDecisionTableConstants.INSTANCE.EditThisColumnConfiguration()),
                                any(ClickHandler.class));
        verify(view,
               never()).anchor(eq(GuidedDecisionTableConstants.INSTANCE.Delete()),
                               eq(GuidedDecisionTableConstants.INSTANCE.DeleteThisColumn()),
                               any(ClickHandler.class));
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
        doReturn(true).when(presenter).isActiveDecisionTableEditable();

        view.renderColumns(columnGroups);

        verify(view).renderColumn(conditionColumnOne);
        verify(view).renderColumn(conditionColumnTwo);

        // There are two brl conditions
        verify(horizontalPanel,
               times(2)).add(columnLabel);
        verify(view,
               times(2)).anchor(eq(GuidedDecisionTableConstants.INSTANCE.Edit()),
                                eq(GuidedDecisionTableConstants.INSTANCE.EditThisColumnConfiguration()),
                                any(ClickHandler.class));
        verify(view,
               times(2)).anchor(eq(GuidedDecisionTableConstants.INSTANCE.Delete()),
                                eq(GuidedDecisionTableConstants.INSTANCE.DeleteThisColumn()),
                                any(ClickHandler.class));
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

        view.renderColumns(columnGroups);

        verify(view).renderColumn(conditionColumnOne);
        verify(view).renderColumn(conditionColumnTwo);

        // There are two brl conditions
        verify(horizontalPanel,
               times(2)).add(columnLabel);
        verify(view,
               times(2)).anchor(eq(GuidedDecisionTableConstants.INSTANCE.Edit()),
                                eq(GuidedDecisionTableConstants.INSTANCE.EditThisColumnConfiguration()),
                                any(ClickHandler.class));
        verify(view,
               never()).anchor(eq(GuidedDecisionTableConstants.INSTANCE.Delete()),
                               eq(GuidedDecisionTableConstants.INSTANCE.DeleteThisColumn()),
                               any(ClickHandler.class));
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
        doReturn(true).when(presenter).isActiveDecisionTableEditable();

        view.renderColumns(columnGroups);

        verify(view).renderColumn(brlActionColumn);
        verify(view).renderColumn(actionInsertFactColumn);
        verify(view).renderColumn(retractFactColumn);

        // There are three action columns
        verify(horizontalPanel,
               times(3)).add(columnLabel);
        verify(view,
               times(3)).anchor(eq(GuidedDecisionTableConstants.INSTANCE.Edit()),
                                eq(GuidedDecisionTableConstants.INSTANCE.EditThisColumnConfiguration()),
                                any(ClickHandler.class));
        verify(view,
               times(3)).anchor(eq(GuidedDecisionTableConstants.INSTANCE.Delete()),
                                eq(GuidedDecisionTableConstants.INSTANCE.DeleteThisColumn()),
                                any(ClickHandler.class));
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

        doReturn(columnLabel).when(view).newColumnLabelWidget(anyString());

        view.renderColumns(columnGroups);

        verify(view).renderColumn(brlActionColumn);
        verify(view).renderColumn(actionInsertFactColumn);
        verify(view).renderColumn(retractFactColumn);

        // There are three action columns
        verify(horizontalPanel,
               times(3)).add(columnLabel);
        verify(view,
               times(3)).anchor(eq(GuidedDecisionTableConstants.INSTANCE.Edit()),
                                eq(GuidedDecisionTableConstants.INSTANCE.EditThisColumnConfiguration()),
                                any(ClickHandler.class));
        verify(view,
               never()).anchor(eq(GuidedDecisionTableConstants.INSTANCE.Delete()),
                               eq(GuidedDecisionTableConstants.INSTANCE.DeleteThisColumn()),
                               any(ClickHandler.class));
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
                .map(classToMock -> mock(classToMock))
                .forEach(column -> {
                    final ArgumentCaptor<ParameterizedCommand> commandCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);

                    doReturn(decisionTablePresenter).when(presenter).getActiveDecisionTable();
                    doReturn(true).when(decisionTablePresenter).canConditionBeDeleted(column);

                    view.removeCondition(column);

                    verify(view).makeRemoveConditionWidget(eq(column),
                                                           commandCaptor.capture());

                    final Command command = mock(Command.class);
                    commandCaptor.getValue().execute(command);

                    verify(command).execute();
                });
    }

    @Test
    public void testRemoveConditionCanNotBeDeleted() throws Exception {
        Stream.of(ConditionCol52.class,
                  BRLConditionColumn.class)
                .map(classToMock -> mock(classToMock))
                .forEach(column -> {
                    final ArgumentCaptor<ParameterizedCommand> commandCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);

                    doReturn(decisionTablePresenter).when(presenter).getActiveDecisionTable();
                    doReturn(false).when(decisionTablePresenter).canConditionBeDeleted(column);
                    doNothing().when(view).showUnableToDeleteColumnMessage(column);

                    view.removeCondition(column);

                    verify(view).makeRemoveConditionWidget(eq(column),
                                                           commandCaptor.capture());

                    final Command command = mock(Command.class);
                    commandCaptor.getValue().execute(command);

                    verify(command,
                           never()).execute();
                    verify(view).showUnableToDeleteColumnMessage(column);
                });
    }

    @Test
    public void testMakeRemoveConditionWidget() throws Exception {
        final ConditionCol52 conditionCol52 = mock(ConditionCol52.class);
        final ParameterizedCommand<Command> command = new ParameterizedCommand<Command>() {
            @Override
            public void execute(Command parameter) {
                parameter.execute();
            }
        };
        final ArgumentCaptor<ClickHandler> clickCaptor = ArgumentCaptor.forClass(ClickHandler.class);
        doNothing().when(view).showConfirmDeleteColumnMessage(eq(conditionCol52),
                                                              any());

        view.makeRemoveConditionWidget(conditionCol52,
                                       command);

        verify(view).anchor(eq(GuidedDecisionTableConstants.INSTANCE.Delete()),
                            eq(GuidedDecisionTableConstants.INSTANCE.DeleteThisColumn()),
                            clickCaptor.capture());

        clickCaptor.getValue().onClick(null);

        verify(view).showConfirmDeleteColumnMessage(eq(conditionCol52),
                                                    any());
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
