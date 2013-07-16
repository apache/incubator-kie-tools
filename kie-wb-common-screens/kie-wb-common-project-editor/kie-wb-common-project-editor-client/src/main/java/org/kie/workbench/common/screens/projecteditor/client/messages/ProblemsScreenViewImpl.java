/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.client.messages;

import javax.inject.Inject;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import org.guvnor.common.services.shared.builder.BuildMessage;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.uberfire.client.mvp.PlaceManager;

public class ProblemsScreenViewImpl
        extends Composite
        implements ProblemsScreenView,
        RequiresResize {

    private static Binder uiBinder = GWT.create(Binder.class);
    private Presenter presenter;
    private final PlaceManager placeManager;

    interface Binder extends UiBinder<Widget, ProblemsScreenViewImpl> {

    }

    @UiField(provided = true)
    DataGrid<BuildMessage> dataGrid;

    @UiField
    HorizontalPanel panel;

    public static final ProvidesKey<BuildMessage> KEY_PROVIDER = new ProvidesKey<BuildMessage>() {
        @Override
        public Object getKey(BuildMessage item) {
            return item == null ? null : item.getId();
        }
    };

    @Inject
    public ProblemsScreenViewImpl(ProblemsService problemsService, PlaceManager placeManager) {
        this.placeManager = placeManager;
        dataGrid = new DataGrid<BuildMessage>(KEY_PROVIDER);
        dataGrid.setWidth("100%");

        dataGrid.setAutoHeaderRefreshDisabled(true);

        dataGrid.setEmptyTableWidget(new Label("---"));

        setUpColumns();

        problemsService.addDataDisplay(dataGrid);

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void onResize() {
        dataGrid.setPixelSize(getParent().getOffsetWidth(),
                getParent().getOffsetHeight());
        dataGrid.onResize();
    }

    private void setUpColumns() {
        addLevelColumn();
        addTextColumn();
        addFileNameColumn();
        addArtifactIDColumn();
        addColumnColumn();
        addLineColumn();
    }

    private void addLineColumn() {
        Column<BuildMessage, String> lineColumn = new Column<BuildMessage, String>(new TextCell()) {
            @Override
            public String getValue(BuildMessage message) {
                return Integer.toString(message.getLine());
            }
        };
        dataGrid.addColumn(lineColumn, ProjectEditorConstants.INSTANCE.Line());
        dataGrid.setColumnWidth(lineColumn, 60, Style.Unit.PCT);
    }

    private void addColumnColumn() {
        Column<BuildMessage, String> column = new Column<BuildMessage, String>(new TextCell()) {
            @Override
            public String getValue(BuildMessage message) {
                return Integer.toString(message.getColumn());
            }
        };
        dataGrid.addColumn(column, ProjectEditorConstants.INSTANCE.Column());
        dataGrid.setColumnWidth(column, 60, Style.Unit.PCT);
    }

    private void addTextColumn() {
        Column<BuildMessage, String> column = new Column<BuildMessage, String>(new TextCell()) {
            @Override
            public String getValue(BuildMessage message) {
                return message.getText();
            }
        };
        dataGrid.addColumn(column, ProjectEditorConstants.INSTANCE.Text());
        dataGrid.setColumnWidth(column, 60, Style.Unit.PCT);
    }

    private void addFileNameColumn() {
        Column<BuildMessage, String> column = new Column<BuildMessage, String>(new ClickableTextCell()) {
            @Override
            public String getValue(BuildMessage message) {
                if (message.getPath() != null) {
                    return message.getPath().getFileName();
                } else {
                    return "-";
                }
            }
        };
        column.setFieldUpdater(new FieldUpdater<BuildMessage, String>() {
            @Override
            public void update(int index, BuildMessage buildMessage, String value) {
                if ( buildMessage.getPath() != null) {
                    placeManager.goTo( buildMessage.getPath());
                }
            }
        });
        dataGrid.addColumn(column, ProjectEditorConstants.INSTANCE.FileName());
        dataGrid.setColumnWidth(column, 60, Style.Unit.PCT);
    }

    private void addArtifactIDColumn() {
        Column<BuildMessage, String> column = new Column<BuildMessage, String>(new TextCell()) {
            @Override
            public String getValue(BuildMessage message) {
                return message.getArtifactID();
            }
        };
        dataGrid.addColumn(column, ProjectEditorConstants.INSTANCE.ArtifactID());
        dataGrid.setColumnWidth(column, 60, Style.Unit.PCT);
    }

    private void addLevelColumn() {
        Column<BuildMessage, ImageResource> column = new Column<BuildMessage, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(BuildMessage message) {
                switch (message.getLevel()) {
                    case ERROR:
                        return ProjectEditorResources.INSTANCE.Error();

                    case WARNING:
                        return ProjectEditorResources.INSTANCE.Warning();
                    case INFO:
                    default:
                        return ProjectEditorResources.INSTANCE.Information();
                }
            }
        };
        dataGrid.addColumn(column, ProjectEditorConstants.INSTANCE.Level());
        dataGrid.setColumnWidth(column, 60, Style.Unit.PCT);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
}
