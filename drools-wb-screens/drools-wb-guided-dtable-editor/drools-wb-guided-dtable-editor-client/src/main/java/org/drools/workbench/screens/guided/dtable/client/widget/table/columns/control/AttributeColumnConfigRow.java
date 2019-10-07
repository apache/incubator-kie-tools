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

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
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

    public void init(final AttributeCol52 attributeColumn,
                     final GuidedDecisionTableModellerView.Presenter presenter) {
        view.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        view.addColumnLabel(attributeColumn);

        final AttributeCol52 originalColumn = attributeColumn;

        if (Objects.equals(attributeColumn.getAttribute(), Attribute.SALIENCE.getAttributeName())) {
            useRowNumberCheckBox = view.addUseRowNumberCheckBox(attributeColumn,
                                                                presenter.isActiveDecisionTableEditable(),
                                                                (event) -> {
                                                                    final AttributeCol52 editedColumn = originalColumn.cloneColumn();
                                                                    editedColumn.setUseRowNumber(useRowNumberCheckBox.getValue());
                                                                    reverseOrderCheckBox.setEnabled(useRowNumberCheckBox.getValue());
                                                                    try {
                                                                        final Optional<GuidedDecisionTableView.Presenter> dtPresenter = presenter.getActiveDecisionTable();
                                                                        if (dtPresenter.isPresent()) {
                                                                            dtPresenter.get().updateColumn(originalColumn,
                                                                                                           editedColumn);
                                                                        }
                                                                    } catch (VetoException veto) {
                                                                        presenter.getView().showGenericVetoMessage();
                                                                    }
                                                                });

            view.add(new Span("("));

            reverseOrderCheckBox = view.addReverseOrderCheckBox(attributeColumn,
                                                                presenter.isActiveDecisionTableEditable(),
                                                                (event) -> {
                                                                    final AttributeCol52 editedColumn = originalColumn.cloneColumn();
                                                                    editedColumn.setReverseOrder(reverseOrderCheckBox.getValue());
                                                                    try {
                                                                        final Optional<GuidedDecisionTableView.Presenter> dtPresenter = presenter.getActiveDecisionTable();
                                                                        if (dtPresenter.isPresent()) {
                                                                            dtPresenter.get().updateColumn(originalColumn,
                                                                                                           editedColumn);
                                                                        }
                                                                    } catch (VetoException veto) {
                                                                        presenter.getView().showGenericVetoMessage();
                                                                    }
                                                                });
            view.add(new Span(")"));
        }

        view.addDefaultValue(attributeColumn,
                             presenter.isActiveDecisionTableEditable(),
                             (event) -> {
                                 final AttributeCol52 editedColumn = originalColumn.cloneColumn();
                                 editedColumn.setDefaultValue(event.getEditedDefaultValue());
                                 try {
                                     final Optional<GuidedDecisionTableView.Presenter> dtPresenter = presenter.getActiveDecisionTable();
                                     if (dtPresenter.isPresent()) {
                                         dtPresenter.get().updateColumn(originalColumn,
                                                                        editedColumn);
                                     }
                                 } catch (VetoException veto) {
                                     presenter.getView().showGenericVetoMessage();
                                 }
                             });

        hideColumnCheckBox = view.addHideColumnCheckBox(attributeColumn,
                                                        (event) -> {
                                                            final AttributeCol52 editedColumn = originalColumn.cloneColumn();
                                                            editedColumn.setHideColumn(hideColumnCheckBox.getValue());
                                                            try {
                                                                final Optional<GuidedDecisionTableView.Presenter> dtPresenter = presenter.getActiveDecisionTable();
                                                                if (dtPresenter.isPresent()) {
                                                                    dtPresenter.get().updateColumn(originalColumn,
                                                                                                   editedColumn);
                                                                }
                                                            } catch (VetoException veto) {
                                                                presenter.getView().showGenericVetoMessage();
                                                            }
                                                        });

        addRemoveAttributeButton(attributeColumn,
                                 presenter);
    }

    private void addRemoveAttributeButton(final AttributeCol52 attributeColumn,
                                          final GuidedDecisionTableModellerView.Presenter presenter) {
        final boolean isEditable = presenter.isActiveDecisionTableEditable();

        view.addRemoveAttributeButton(() -> {
                                          try {
                                              final Optional<GuidedDecisionTableView.Presenter> dtPresenter = presenter.getActiveDecisionTable();
                                              if (dtPresenter.isPresent()) {
                                                  dtPresenter.get().deleteColumn(attributeColumn);
                                              }
                                          } catch (VetoException veto) {
                                              presenter.getView().showGenericVetoMessage();
                                          }
                                      },
                                      isEditable);
    }

    public Widget getView() {
        return view;
    }
}
