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
import javax.inject.Inject;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.WorkItemHandlerModel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;

public class WorkItemHandlersPanelViewImpl
        extends Composite
        implements WorkItemHandlersPanelView {

    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, WorkItemHandlersPanelViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField(provided = true)
    SimpleTable<WorkItemHandlerModel> dataGrid = new SimpleTable<WorkItemHandlerModel>();

    @UiField
    Button addButton;

    @Inject
    public WorkItemHandlersPanelViewImpl() {
        addNameColumn();
        addTypeColumn();
        addDeleteColumn();

        initWidget( uiBinder.createAndBindUi( this ) );
    }

    private void addDeleteColumn() {
        final ButtonCell deleteCell = new ButtonCell( ButtonType.DANGER, IconType.TRASH );
        final Column<WorkItemHandlerModel, String> column = new Column<WorkItemHandlerModel, String>( deleteCell ) {
            @Override
            public String getValue( WorkItemHandlerModel object ) {
                return "";
            }
        };

        column.setFieldUpdater( new FieldUpdater<WorkItemHandlerModel, String>() {
            @Override
            public void update( int index,
                                WorkItemHandlerModel model,
                                String value ) {
                presenter.onDelete( model );
            }
        } );
        column.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );

        dataGrid.addColumn( column,
                CommonConstants.INSTANCE.Delete() );
        dataGrid.setColumnWidth( column,
                60,
                Style.Unit.PX );
    }

    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    private void addTypeColumn() {

        Column<WorkItemHandlerModel, String> column = new Column<WorkItemHandlerModel, String>( new EditTextCell() ) {
            @Override
            public String getValue( WorkItemHandlerModel model ) {
                return model.getType();
            }
        };

        column.setFieldUpdater( new FieldUpdater<WorkItemHandlerModel, String>() {
            @Override
            public void update( int index,
                                WorkItemHandlerModel model,
                                String value ) {
                model.setType( value );
            }
        } );

        dataGrid.addColumn( column,
                            ProjectEditorResources.CONSTANTS.Type() );
    }

    private void addNameColumn() {

        Column<WorkItemHandlerModel, String> column = new Column<WorkItemHandlerModel, String>( new EditTextCell() ) {
            @Override
            public String getValue( WorkItemHandlerModel model ) {
                return model.getName();
            }
        };

        column.setFieldUpdater( new FieldUpdater<WorkItemHandlerModel, String>() {
            @Override
            public void update( int index,
                                WorkItemHandlerModel model,
                                String value ) {
                model.setName( value );
            }
        } );

        dataGrid.addColumn( column,
                            ProjectEditorResources.CONSTANTS.Name() );
    }

    public void setModels( List<WorkItemHandlerModel> handlerModels ) {
        dataGrid.setRowData( handlerModels );
    }

    @Override
    public void redraw() {
        dataGrid.redraw();
    }

    @UiHandler("addButton")
    public void onAddClick( ClickEvent event ) {
        presenter.onAdd();
    }

}
