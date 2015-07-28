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

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
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

    private boolean readOnly = false;

    @UiField(provided = true)
    PagedTable<ClassRow> dataGrid = new PagedTable<ClassRow>( 10, new ProvidesKey<ClassRow>() {
        @Override
        public Object getKey( ClassRow item ) {
            return item.getClassName();
        }
    } );

    @UiField
    TextBox newClassTextBox;

    @UiField
    HelpBlock newClassHelpInline;

    @UiField
    Button addClassesButton;

    @UiField
    Button addClassButton;

    public ProjectClassListViewImpl() {

        dataGrid.setEmptyTableCaption( Constants.INSTANCE.project_class_list_no_classes_message() );
        dataGrid.setToolBarVisible( false );

        addClassNameColumn();
        addRemoveRowColumn();

        initWidget( uiBinder.createAndBindUi( this ) );

        newClassTextBox.addKeyDownHandler( new KeyDownHandler() {
            @Override
            public void onKeyDown( KeyDownEvent event ) {
                presenter.onClassNameChange();
            }
        } );
    }

    private void addRemoveRowColumn() {
        Column<ClassRow, String> column = new Column<ClassRow, String>( new ButtonCell( IconType.TRASH, ButtonType.DANGER, ButtonSize.SMALL ) ) {
            @Override
            public String getValue( ClassRow classRow ) {
                return Constants.INSTANCE.project_class_list_action_delete();
            }
        };

        column.setFieldUpdater( new FieldUpdater<ClassRow, String>() {
            @Override
            public void update( int index,
                                ClassRow classRow,
                                String value ) {
                if ( !readOnly ) {
                    onRemoveClass( classRow );
                }
            }
        } );

        dataGrid.addColumn( column,
                            Constants.INSTANCE.project_class_list_action_column() );
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
        this.readOnly = readOnly;
        newClassTextBox.setText( null );
        newClassTextBox.setReadOnly( readOnly );
        addClassesButton.setEnabled( !readOnly );
        addClassButton.setEnabled( !readOnly );
    }

    @Override
    public void setDataProvider( AsyncDataProvider<ClassRow> dataProvider ) {
        dataGrid.setDataProvider( dataProvider );
    }

    @Override
    public String getNewClassName() {
        return newClassTextBox.getText();
    }

    @Override
    public void setNewClassName( String newClassName ) {
        newClassTextBox.setText( newClassName );
    }

    @Override
    public void setNewClassHelpMessage( String newClassHelpInline ) {
        this.newClassHelpInline.setText( newClassHelpInline );
    }

    @Override
    public void redraw() {
        dataGrid.redraw();
    }

    @UiHandler("addClassesButton")
    void onAddClasses( ClickEvent event ) {
        presenter.onLoadClasses();
    }

    @UiHandler("addClassButton")
    void onAddClassButton( ClickEvent event ) {
        presenter.onLoadClass();
    }

    void onRemoveClass( ClassRow classRow ) {
        presenter.onRemoveClass( classRow );
    }
}
