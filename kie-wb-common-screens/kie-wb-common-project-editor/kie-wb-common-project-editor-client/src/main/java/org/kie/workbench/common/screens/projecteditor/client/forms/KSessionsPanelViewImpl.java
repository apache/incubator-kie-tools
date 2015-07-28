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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
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
import com.google.gwt.view.client.ProvidesKey;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.services.shared.kmodule.ClockTypeOption;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;

public class KSessionsPanelViewImpl
        extends Composite
        implements KSessionsPanelView {

    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, KSessionsPanelViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public static final ProvidesKey<KSessionModel> KEY_PROVIDER = new ProvidesKey<KSessionModel>() {
        @Override
        public Object getKey( KSessionModel item ) {
            return item == null ? null : item.getName();
        }
    };

    @UiField(provided = true)
    SimpleTable<KSessionModel> dataGrid = new SimpleTable<KSessionModel>( KEY_PROVIDER );

    @UiField
    Button addButton;

    private final KSessionModelOptionsPopUp kSessionModelOptionsPopUp;

    @Inject
    public KSessionsPanelViewImpl( KSessionModelOptionsPopUp kSessionModelOptionsPopUp ) {
        this.kSessionModelOptionsPopUp = kSessionModelOptionsPopUp;
        setUpNameColumn();
        setUpDefaultColumn();
        setUpStateColumn();
        setUpClockColumn();
        setUpOptionsColumn();
        setUpRemoveColumn();

        initWidget( uiBinder.createAndBindUi( this ) );
    }

    private void setUpNameColumn() {
        final EditTextCell cell = new EditTextCell();
        Column<KSessionModel, String> column = new Column<KSessionModel, String>( cell ) {
            @Override
            public String getValue( KSessionModel kSessionModel ) {
                return kSessionModel.getName();
            }
        };
        dataGrid.addColumn( column,
                            ProjectEditorResources.CONSTANTS.Name() );
        dataGrid.setColumnWidth( column,
                                 40,
                                 Style.Unit.PCT );

        column.setFieldUpdater( new FieldUpdater<KSessionModel, String>() {
            @Override
            public void update( int row,
                                KSessionModel kSessionModel,
                                String value ) {

                cell.clearViewData( KEY_PROVIDER.getKey( kSessionModel ) );

                presenter.onRename( kSessionModel,
                                    value );
            }
        } );
    }

    @Override
    public void refresh() {
        dataGrid.refresh();
    }

    private void setUpClockColumn() {
        ArrayList<String> options = new ArrayList<String>();
        options.add( ProjectEditorResources.CONSTANTS.Realtime() );
        options.add( ProjectEditorResources.CONSTANTS.Pseudo() );

        Column<KSessionModel, String> column = new Column<KSessionModel, String>( new SelectionCell( options ) ) {
            @Override
            public String getValue( KSessionModel kSessionModel ) {
                if ( kSessionModel.getClockType().equals( ClockTypeOption.PSEUDO ) ) {
                    return ProjectEditorResources.CONSTANTS.Pseudo();
                } else if ( kSessionModel.getClockType().equals( ClockTypeOption.REALTIME ) ) {
                    return ProjectEditorResources.CONSTANTS.Realtime();
                } else {
                    return kSessionModel.getClockType().toString();
                }
            }
        };

        column.setFieldUpdater( new FieldUpdater<KSessionModel, String>() {
            @Override
            public void update( int index,
                                KSessionModel model,
                                String value ) {
                if ( value.equals( ProjectEditorResources.CONSTANTS.Pseudo() ) ) {
                    model.setClockType( ClockTypeOption.PSEUDO );
                } else {
                    model.setClockType( ClockTypeOption.REALTIME );
                }
            }
        } );

        dataGrid.addColumn( column,
                            ProjectEditorResources.CONSTANTS.Clock() );
        dataGrid.setColumnWidth( column,
                                 60,
                                 Style.Unit.PCT );
    }

    private void setUpStateColumn() {
        ArrayList<String> options = new ArrayList<String>();
        options.add( ProjectEditorResources.CONSTANTS.Stateful() );
        options.add( ProjectEditorResources.CONSTANTS.Stateless() );

        Column<KSessionModel, String> column = new Column<KSessionModel, String>( new SelectionCell( options ) ) {
            @Override
            public String getValue( KSessionModel kSessionModel ) {
                if ( kSessionModel.getType() == null ) {
                    return ProjectEditorResources.CONSTANTS.Stateful();
                } else if ( kSessionModel.getType().equals( "stateful" ) ) {
                    return ProjectEditorResources.CONSTANTS.Stateful();
                } else if ( kSessionModel.getType().equals( "stateless" ) ) {
                    return ProjectEditorResources.CONSTANTS.Stateless();
                } else {
                    return kSessionModel.getType();
                }
            }
        };

        column.setFieldUpdater( new FieldUpdater<KSessionModel, String>() {
            @Override
            public void update( int index,
                                KSessionModel model,
                                String value ) {
                if ( value.equals( ProjectEditorResources.CONSTANTS.Stateful() ) ) {
                    model.setType( "stateful" );
                } else {
                    model.setType( "stateless" );
                }
            }
        } );

        dataGrid.addColumn( column,
                            ProjectEditorResources.CONSTANTS.State() );
        dataGrid.setColumnWidth( column,
                                 60,
                                 Style.Unit.PCT );
    }

    private void setUpDefaultColumn() {
        Column<KSessionModel, Boolean> column = new Column<KSessionModel, Boolean>( new CheckboxCell() ) {
            @Override
            public Boolean getValue( KSessionModel model ) {
                return model.isDefault();
            }
        };

        column.setFieldUpdater( new FieldUpdater<KSessionModel, Boolean>() {
            @Override
            public void update( int index,
                                KSessionModel model,
                                Boolean value ) {
                model.setDefault( value );
                presenter.onDefaultChanged( model );
            }
        } );

        column.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );

        dataGrid.addColumn( column,
                            ProjectEditorResources.CONSTANTS.Default() );
        dataGrid.setColumnWidth( column,
                                 80,
                                 Style.Unit.PX );
    }

    private void setUpOptionsColumn() {
        final ButtonCell typeImageCell = new ButtonCell( ButtonType.DEFAULT, IconType.EDIT );
        final Column<KSessionModel, String> column = new Column<KSessionModel, String>( typeImageCell ) {
            @Override
            public String getValue( KSessionModel model ) {
                return "";
            }
        };

        column.setFieldUpdater( new FieldUpdater<KSessionModel, String>() {
            @Override
            public void update( int index,
                                KSessionModel model,
                                String value ) {
                presenter.onOptionsSelectedForKSessions( model );
            }
        } );

        column.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );

        dataGrid.addColumn( column,
                            CommonConstants.INSTANCE.Edit() );
        dataGrid.setColumnWidth( column,
                                 60,
                                 Style.Unit.PX );
    }

    private void setUpRemoveColumn() {
        final ButtonCell typeImageCell = new ButtonCell( ButtonType.DANGER, IconType.TRASH );
        final Column<KSessionModel, String> column = new Column<KSessionModel, String>( typeImageCell ) {
            @Override
            public String getValue( KSessionModel model ) {
                return "";
            }
        };

        column.setFieldUpdater( new FieldUpdater<KSessionModel, String>() {
            @Override
            public void update( int index,
                                KSessionModel model,
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

    @Override
    public void showOptionsPopUp( KSessionModel kSessionModel ) {
        kSessionModelOptionsPopUp.show( kSessionModel );
    }

    @Override
    public void showXsdIDError() {
        ErrorPopup.showMessage( ProjectEditorResources.CONSTANTS.XsdIDError() );
    }

    @Override
    public void makeReadOnly() {
        addButton.setEnabled( false );
    }

    @Override
    public void makeEditable() {
        addButton.setEnabled( true );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setItemList( List<KSessionModel> list ) {
        dataGrid.setRowData( list );
    }

    @UiHandler("addButton")
    public void onAddClicked( ClickEvent event ) {
        presenter.onAdd();
    }

}
