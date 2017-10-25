/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.asset.management.client.editors.project.structure.widgets;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.i18n.Constants;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;

public class ProjectModulesViewImpl extends Composite
        implements ProjectModulesView {

    interface ProjectModulesEditorViewImplUIBinder
            extends UiBinder<Widget, ProjectModulesViewImpl> {

    }

    private static ProjectModulesEditorViewImplUIBinder uiBinder = GWT.create(ProjectModulesEditorViewImplUIBinder.class);

    @UiField(provided = true)
    final SimpleTable<ProjectModuleRow> modulesTable = new SimpleTable<ProjectModuleRow>();

    @UiField
    Button addModuleButton;

    @UiField
    Heading modulesLabel;

    private Column<ProjectModuleRow, ?> modulesColumn;

    private Presenter presenter;

    private boolean actionsEnabled = true;

    public ProjectModulesViewImpl() {
        addModuleColumn();
        addEditModuleColumn();
        addDeleteModuleColumn();
        modulesTable.setToolBarVisible(false);

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
        presenter.addDataDisplay(modulesTable);
    }

    @Override
    public void setMode(final ViewMode mode) {
        if (mode == ViewMode.PROJECTS_VIEW) {
            addModuleButton.setText(Constants.INSTANCE.NewProject());
            modulesLabel.setText(Constants.INSTANCE.Projects());
            if (modulesColumn != null) {
                modulesColumn.setDataStoreName(Constants.INSTANCE.Project());
            }
        } else {
            addModuleButton.setText(Constants.INSTANCE.AddModule());
            modulesLabel.setText(Constants.INSTANCE.Modules());
            if (modulesColumn != null) {
                modulesColumn.setDataStoreName(Constants.INSTANCE.Module());
            }
        }
    }

    @Override
    public void enableActions(final boolean value) {
        addModuleButton.setEnabled(value);
        actionsEnabled = value;
    }

    private void addModuleColumn() {
        modulesColumn = new Column<ProjectModuleRow, String>(new TextCell()) {
            @Override
            public String getValue(final ProjectModuleRow row) {
                return row.getName();
            }
        };
        modulesTable.addColumn(modulesColumn,
                               Constants.INSTANCE.Module());
        modulesTable.setColumnWidth(modulesColumn,
                                    70,
                                    Style.Unit.PCT);
    }

    private void addDeleteModuleColumn() {
        final ButtonCell deleteModuleButton = new ButtonCell(IconType.TRASH,
                                                             ButtonType.DANGER,
                                                             ButtonSize.SMALL);
        final Column<ProjectModuleRow, String> deleteModuleColumn = new Column<ProjectModuleRow, String>(deleteModuleButton) {
            @Override
            public String getValue(final ProjectModuleRow moduleRow) {
                return Constants.INSTANCE.DeleteModule();
            }
        };
        deleteModuleColumn.setFieldUpdater(new FieldUpdater<ProjectModuleRow, String>() {
            public void update(final int index,
                               final ProjectModuleRow moduleRow,
                               final String value) {
                if (presenter != null && actionsEnabled) {
                    presenter.onDeleteModule(moduleRow);
                }
            }
        });

        modulesTable.addColumn(deleteModuleColumn,
                               "");
        modulesTable.setColumnWidth(deleteModuleColumn,
                                    15,
                                    Style.Unit.PCT);
    }

    private void addEditModuleColumn() {
        final ButtonCell editModuleButton = new ButtonCell(IconType.EDIT,
                                                           ButtonType.PRIMARY,
                                                           ButtonSize.SMALL);
        final Column<ProjectModuleRow, String> editModuleColumn = new Column<ProjectModuleRow, String>(editModuleButton) {
            @Override
            public String getValue(final ProjectModuleRow moduleRow) {
                return Constants.INSTANCE.EditModule();
            }
        };
        editModuleColumn.setFieldUpdater(new FieldUpdater<ProjectModuleRow, String>() {
            public void update(final int index,
                               final ProjectModuleRow moduleRow,
                               final String value) {
                if (presenter != null && actionsEnabled) {
                    presenter.onEditModule(moduleRow);
                }
            }
        });

        modulesTable.addColumn(editModuleColumn,
                               "");
        modulesTable.setColumnWidth(editModuleColumn,
                                    15,
                                    Style.Unit.PCT);
    }

    @UiHandler("addModuleButton")
    void onAddModuleButtonClick(final ClickEvent e) {
        presenter.onAddModule();
    }
}
