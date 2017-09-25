/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.DefaultValueWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.rule.client.editor.RuleAttributeWidget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.html.Span;

@Dependent
public class AttributeColumnConfigRow {

    @Inject
    protected AttributeColumnConfigRowView view;
    private CheckBox useRowNumberCheckBox;
    private CheckBox reverseOrderCheckBox;
    private CheckBox hideColumnCheckBox;

    public AttributeColumnConfigRow() {
    }

    public void init(AttributeCol52 attributeColumn,
                     final GuidedDecisionTableModellerView.Presenter presenter) {
        view.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        view.addColumnLabel(attributeColumn);

        final AttributeCol52 originalColumn = attributeColumn;

        if (attributeColumn.getAttribute().equals(RuleAttributeWidget.SALIENCE_ATTR)) {
            useRowNumberCheckBox = view.addUseRowNumberCheckBox(attributeColumn,
                                                                presenter.isActiveDecisionTableEditable(),
                                                                new ClickHandler() {
                                                                    @Override
                                                                    public void onClick(ClickEvent event) {
                                                                        final AttributeCol52 editedColumn = originalColumn.cloneColumn();
                                                                        editedColumn.setUseRowNumber(useRowNumberCheckBox.getValue());
                                                                        reverseOrderCheckBox.setEnabled(useRowNumberCheckBox.getValue());
                                                                        presenter.getActiveDecisionTable().updateColumn(originalColumn,
                                                                                                                        editedColumn);
                                                                    }
                                                                });

            view.add(new Span("("));

            reverseOrderCheckBox = view.addReverseOrderCheckBox(attributeColumn,
                                                                presenter.isActiveDecisionTableEditable(),
                                                                new ClickHandler() {
                                                                    @Override
                                                                    public void onClick(ClickEvent event) {
                                                                        final AttributeCol52 editedColumn = originalColumn.cloneColumn();
                                                                        editedColumn.setReverseOrder(reverseOrderCheckBox.getValue());
                                                                        presenter.getActiveDecisionTable().updateColumn(originalColumn,
                                                                                                                        editedColumn);
                                                                    }
                                                                });
            view.add(new Span(")"));
        }

        view.addDefaultValue(attributeColumn,
                             presenter.isActiveDecisionTableEditable(),
                             new DefaultValueWidgetFactory.DefaultValueChangedEventHandler() {
                                 @Override
                                 public void onDefaultValueChanged(DefaultValueWidgetFactory.DefaultValueChangedEvent event) {
                                     final AttributeCol52 editedColumn = originalColumn.cloneColumn();
                                     editedColumn.setDefaultValue(event.getEditedDefaultValue());
                                     presenter.getActiveDecisionTable().updateColumn(originalColumn,
                                                                                     editedColumn);
                                 }
                             });

        hideColumnCheckBox = view.addHideColumnCheckBox(attributeColumn,
                                                        new ClickHandler() {
                                                            @Override
                                                            public void onClick(ClickEvent event) {
                                                                final AttributeCol52 editedColumn = originalColumn.cloneColumn();
                                                                editedColumn.setHideColumn(hideColumnCheckBox.getValue());
                                                                presenter.getActiveDecisionTable().updateColumn(originalColumn,
                                                                                                                editedColumn);
                                                            }
                                                        });

        addRemoveAttributeButton(attributeColumn,
                                 presenter);
    }

    private void addRemoveAttributeButton(final AttributeCol52 attributeColumn,
                                          final GuidedDecisionTableModellerView.Presenter presenter) {
        final boolean isEditable = presenter.isActiveDecisionTableEditable();

        view.addRemoveAttributeButton(() -> presenter.getActiveDecisionTable().deleteColumn(attributeColumn),
                                      isEditable);
    }

    public Widget getView() {
        return view;
    }
}
