/*
 * Copyright 2013 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.Dependency;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;

public class DependencyGridViewImpl
        extends Composite
        implements DependencyGridView {


    interface Binder
            extends
            UiBinder<Widget, DependencyGridViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    private Presenter presenter;

    @UiField(provided = true)
    DataGrid<Dependency> dataGrid = new DataGrid<Dependency>();

    @UiField
    Button addDependencyButton;

    @UiField
    Button addFromRepositoryDependencyButton;

    public DependencyGridViewImpl() {
        dataGrid.setEmptyTableWidget(new Label("---"));

        dataGrid.setAutoHeaderRefreshDisabled(true);

        addGroupIdColumn();
        addArtifactIdColumn();
        addVersionColumn();
        addRemoveRowColumn();

        initWidget(uiBinder.createAndBindUi(this));
    }

    private void addArtifactIdColumn() {
        Column<Dependency, String> column = new Column<Dependency, String>(new EditTextCell()) {
            @Override
            public String getValue(Dependency dependency) {
                if (dependency.getArtifactId() != null) {
                    return dependency.getArtifactId();
                } else {
                    return "";
                }
            }
        };

        column.setFieldUpdater(new FieldUpdater<Dependency, String>() {
            @Override
            public void update(int index, Dependency dependency, String value) {
                if (checkIsInValid(value)) {
                    Window.alert(ProjectEditorConstants.INSTANCE.XMLMarkIsNotAllowed());
                    return;
                }
                dependency.setArtifactId(value);
            }
        });

        dataGrid.addColumn(column, ProjectEditorConstants.INSTANCE.ArtifactID());
        dataGrid.setColumnWidth(column, 60, Style.Unit.PCT);
    }

    private void addGroupIdColumn() {
        Column<Dependency, String> column = new Column<Dependency, String>(new EditTextCell()) {
            @Override
            public String getValue(Dependency dependency) {
                if (dependency.getGroupId() != null) {
                    return dependency.getGroupId();
                } else {
                    return "";
                }
            }
        };
        column.setFieldUpdater(new FieldUpdater<Dependency, String>() {
            @Override
            public void update(int index, Dependency dependency, String value) {
                if (checkIsInValid(value)) {
                    Window.alert(ProjectEditorConstants.INSTANCE.XMLMarkIsNotAllowed());
                    return;
                }
                dependency.setGroupId(value);
            }
        });

        dataGrid.addColumn(column, ProjectEditorConstants.INSTANCE.GroupID());
        dataGrid.setColumnWidth(column, 60, Style.Unit.PCT);
    }

    private void addVersionColumn() {
        Column<Dependency, String> column = new Column<Dependency, String>(new EditTextCell()) {
            @Override
            public String getValue(Dependency dependency) {
                if (dependency.getVersion() != null) {
                    return dependency.getVersion();
                } else {
                    return "";
                }
            }
        };
        column.setFieldUpdater(new FieldUpdater<Dependency, String>() {
            @Override
            public void update(int index, Dependency dependency, String value) {
                if (checkIsInValid(value)) {
                    Window.alert(ProjectEditorConstants.INSTANCE.XMLMarkIsNotAllowed());
                    return;
                }
                dependency.setVersion(value);
            }
        });
        dataGrid.addColumn(column, ProjectEditorConstants.INSTANCE.VersionID());
        dataGrid.setColumnWidth(column, 60, Style.Unit.PCT);
    }

    boolean checkIsInValid(String content) {
        if (content != null && (content.contains("<") || content.contains(">") || content.contains("&"))) {
            return true;
        }

        return false;
    }

    private void addRemoveRowColumn() {
        Column<Dependency, ImageResource> column = new Column<Dependency, ImageResource>(new TrashCanImageCell()) {
            @Override
            public ImageResource getValue(Dependency dependency) {
                return ProjectEditorResources.INSTANCE.Trash();
            }
        };

        column.setFieldUpdater(new FieldUpdater<Dependency, ImageResource>() {
            @Override
            public void update(int index, Dependency dependency, ImageResource value) {
                presenter.onRemoveDependency(dependency);
            }
        });


        dataGrid.addColumn(column);
        dataGrid.setColumnWidth(column, 40, Style.Unit.PCT);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setReadOnly() {
        addDependencyButton.setEnabled(false);
        addFromRepositoryDependencyButton.setEnabled(false);
    }

    @Override
    public void setList(List<Dependency> dependencies) {
        dataGrid.setRowData(dependencies);
    }

    @UiHandler("addDependencyButton")
    void onAddDependency(ClickEvent event) {
        presenter.onAddDependencyButton();
    }

    @UiHandler("addFromRepositoryDependencyButton")
    void onAddDependencyFromRepository(ClickEvent event) {
        presenter.onAddDependencyFromRepositoryButton();
    }

    @Override
    public void refresh() {
        dataGrid.redraw();
    }
}
