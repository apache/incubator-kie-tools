/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.pdescriptor;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
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
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;

public class ProjectClassListViewImpl
        extends Composite
        implements ProjectClassListView {

    interface ProjectClassListViewImplUiBinder
            extends
            UiBinder<Widget, ProjectClassListViewImpl> {

    }

    private static ProjectClassListViewImplUiBinder uiBinder = GWT.create( ProjectClassListViewImplUiBinder.class );

    private Presenter presenter;

    @UiField(provided = true)
    PagedTable dataGrid = new PagedTable( 10 );

    @UiField
    Button addClassesButton;

    public ProjectClassListViewImpl() {

        dataGrid.setEmptyTableCaption( Constants.INSTANCE.project_class_list_no_classes_message() );
        dataGrid.setToolBarVisible( false );

        addClassNameColumn();
        addRemoveRowColumn();

        initWidget( uiBinder.createAndBindUi( this ) );
    }

    private void addRemoveRowColumn() {
        Column<ClassRow, String> column = new Column<ClassRow, String>( new ButtonCell( IconType.TRASH, ButtonType.DANGER, ButtonSize.SMALL ) ) {
            @Override
            public String getValue( ClassRow classRow ) {
                return Constants.INSTANCE.project_class_list_action_column();
            }
        };

        column.setFieldUpdater( new FieldUpdater<ClassRow, String>() {
            @Override
            public void update( int index,
                    ClassRow classRow,
                    String value ) {
                onRemoveClass( classRow );
            }
        } );

        dataGrid.addColumn( column,
                Constants.INSTANCE.project_class_list_action_delete() );
        dataGrid.setColumnWidth( column, 10, Style.Unit.PCT );
    }

    private void addClassNameColumn() {
        Column<ClassRow, String> column = new Column<ClassRow, String>( new TextCell() ) {
            @Override
            public String getValue( ClassRow classRow ) {
                if ( classRow.getClassName() != null ) {
                    return classRow.getClassName();
                } else {
                    return "";
                }
            }
        };

        dataGrid.addColumn( column,
                Constants.INSTANCE.project_class_list_class_name_column() );
        dataGrid.setColumnWidth( column, 90, Style.Unit.PCT );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setReadOnly( boolean readOnly ) {
        addClassesButton.setEnabled( !readOnly );
    }

    @Override
    public void setList( List<ClassRow> classes ) {
        dataGrid.setRowData( classes );
    }

    @Override public void redraw() {
        dataGrid.redraw();
    }

    @UiHandler( "addClassesButton" )
    void onAddClasses( ClickEvent event ) {
        presenter.onLoadClasses();
    }

    void onRemoveClass( ClassRow classRow ) {
        presenter.onRemoveClass( classRow );
    }
}
