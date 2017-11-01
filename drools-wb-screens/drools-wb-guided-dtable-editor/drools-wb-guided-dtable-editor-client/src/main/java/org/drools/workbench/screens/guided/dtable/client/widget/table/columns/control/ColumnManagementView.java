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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoDeletePatternInUseException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.jboss.errai.ioc.client.api.ManagedInstance;

@Dependent
public class ColumnManagementView extends VerticalPanel {

    private ManagedInstance<DeleteColumnManagementAnchorWidget> deleteColumnManagementAnchorWidgets;

    private GuidedDecisionTableModellerView.Presenter presenter;

    public ColumnManagementView() {
    }

    @Inject
    public ColumnManagementView(final ManagedInstance<DeleteColumnManagementAnchorWidget> deleteColumnManagementAnchorWidgets) {
        this.deleteColumnManagementAnchorWidgets = deleteColumnManagementAnchorWidgets;
    }

    public void init(final GuidedDecisionTableModellerView.Presenter presenter) {
        this.presenter = presenter;
    }

    public void renderColumns(final Map<String, List<BaseColumn>> columnGroups) {
        clear();
        for (String groupLabel : columnGroups.keySet()) {

            final VerticalPanel columnGroup = new VerticalPanel();
            final HorizontalPanel columnGroupHeader = new HorizontalPanel();
            final VerticalPanel columnGroupColumns = new VerticalPanel();
            columnGroupHeader.add(new Label(groupLabel));
            columnGroup.add(columnGroupHeader);
            columnGroup.add(columnGroupColumns);
            add(columnGroup);

            for (final BaseColumn column : columnGroups.get(groupLabel)) {
                if (column instanceof ActionCol52) {
                    columnGroupColumns.add(renderColumn((ActionCol52) column));
                } else if (column instanceof BRLConditionColumn) {
                    columnGroupColumns.add(renderColumn((BRLConditionColumn) column));
                } else if (column instanceof Pattern52) {
                    renderColumn((Pattern52) column).forEach(columnGroupColumns::add);
                }
            }
        }
    }

    HorizontalPanel renderColumn(final ActionCol52 actionColumn) {
        HorizontalPanel action = newHorizontalPanel();

        final ColumnLabelWidget actionLabel = makeColumnLabel(actionColumn);
        action.add(actionLabel);

        final FlowPanel buttons = new FlowPanel() {{
            add(editAnchor((clickEvent) -> presenter.getActiveDecisionTable().editAction(actionColumn)));

            if (presenter.isActiveDecisionTableEditable()) {
                add(deleteAnchor(actionColumn.getHeader(),
                                 () -> {
                                     try {
                                         presenter.getActiveDecisionTable().deleteColumn(actionColumn);
                                     } catch (VetoDeletePatternInUseException veto) {
                                         presenter.getView().showUnableToDeleteColumnMessage(actionColumn);
                                     } catch (VetoException veto) {
                                         presenter.getView().showGenericVetoMessage();
                                     }
                                 }));
            }
        }};

        action.add(buttons);

        return action;
    }

    List<HorizontalPanel> renderColumn(final Pattern52 pattern) {
        final List<HorizontalPanel> conditions = new ArrayList<>();

        for (ConditionCol52 conditionColumn : pattern.getChildColumns()) {
            final HorizontalPanel condition = newHorizontalPanel();

            final ColumnLabelWidget conditionLabel = makeColumnLabel(conditionColumn);
            condition.add(conditionLabel);

            final FlowPanel buttons = new FlowPanel() {{
                add(editAnchor((clickEvent) -> presenter.getActiveDecisionTable().editCondition(pattern,
                                                                                                conditionColumn))
                );

                if (presenter.isActiveDecisionTableEditable()) {
                    add(removeCondition(conditionColumn));
                }
            }};

            condition.add(buttons);
            conditions.add(condition);
        }

        return conditions;
    }

    HorizontalPanel renderColumn(final BRLConditionColumn conditionColumn) {
        HorizontalPanel condition = newHorizontalPanel();

        final ColumnLabelWidget columnLabel = makeColumnLabel(conditionColumn);
        condition.add(columnLabel);

        final FlowPanel buttons = new FlowPanel() {{
            add(editAnchor((clickEvent) -> presenter.getActiveDecisionTable().editCondition(conditionColumn)));

            if (presenter.isActiveDecisionTableEditable()) {
                add(removeCondition(conditionColumn));
            }
        }};

        condition.add(buttons);

        return condition;
    }

    ColumnLabelWidget makeColumnLabel(final ConditionCol52 conditionColumn) {
        final StringBuilder labelBuilder = new StringBuilder();
        if (conditionColumn.isBound()) {
            labelBuilder.append(conditionColumn.getBinding())
                    .append(" : ");
        }
        labelBuilder.append(conditionColumn.getHeader());
        final ColumnLabelWidget label = newColumnLabelWidget(labelBuilder.toString());
        ColumnUtilities.setColumnLabelStyleWhenHidden(label,
                                                      conditionColumn.isHideColumn());
        return label;
    }

    ColumnLabelWidget makeColumnLabel(final ActionCol52 actionColumn) {
        final ColumnLabelWidget label = newColumnLabelWidget(actionColumn.getHeader());
        ColumnUtilities.setColumnLabelStyleWhenHidden(label,
                                                      actionColumn.isHideColumn());
        return label;
    }

    EditColumnManagementAnchorWidget editAnchor(final ClickHandler clickHandler) {
        return new EditColumnManagementAnchorWidget(clickHandler);
    }

    Widget removeCondition(final ConditionCol52 column) {
        return deleteAnchor(column.getHeader(),
                            () -> {
                                try {
                                    presenter.getActiveDecisionTable().deleteColumn(column);
                                } catch (VetoDeletePatternInUseException veto) {
                                    presenter.getView().showUnableToDeleteColumnMessage(column);
                                } catch (VetoException veto) {
                                    presenter.getView().showGenericVetoMessage();
                                }
                            });
    }

    HorizontalPanel newHorizontalPanel() {
        return new HorizontalPanel();
    }

    ColumnLabelWidget newColumnLabelWidget(final String text) {
        return new ColumnLabelWidget(text);
    }

    DeleteColumnManagementAnchorWidget deleteAnchor(final String columnHeader,
                                                    final Command command) {
        final DeleteColumnManagementAnchorWidget widget = deleteColumnManagementAnchorWidgets.get();
        widget.init(columnHeader,
                    command);
        return widget;
    }
}
