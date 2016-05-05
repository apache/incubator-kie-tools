/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.client.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.uberfire.ext.editor.commons.client.BaseEditorViewImpl;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.ext.plugin.client.widget.cell.IconCell;
import org.uberfire.ext.plugin.model.DynamicMenuItem;

@Dependent
public class DynamicMenuEditorView
        extends BaseEditorViewImpl
        implements DynamicMenuEditorPresenter.View,
                   Editor<DynamicMenuItem> {

    interface ViewBinder
            extends
            UiBinder<Widget, DynamicMenuEditorView> {

    }

    interface Driver extends SimpleBeanEditorDriver<DynamicMenuItem, DynamicMenuEditorView> {

    }

    private final Driver driver = GWT.create( Driver.class );

    private static final ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    TextBox activityId;

    @UiField
    FormGroup activityIdControlGroup;

    @UiField
    @Ignore
    HelpBlock activityIdHelpInline;

    @UiField
    TextBox menuLabel;

    @UiField
    FormGroup menuLabelControlGroup;

    @UiField
    @Ignore
    HelpBlock menuLabelHelpInline;

    //SelectionModel works better with a KeyProvider
    private final ProvidesKey<DynamicMenuItem> keyProvider = new ProvidesKey<DynamicMenuItem>() {
        @Override
        public Object getKey( final DynamicMenuItem item ) {
            return item.getActivityId() + item.getMenuLabel();
        }
    };
    private final SingleSelectionModel<DynamicMenuItem> selectionModel = new SingleSelectionModel<DynamicMenuItem>( keyProvider );

    @UiField(provided = true)
    CellTable<DynamicMenuItem> menuItems = new CellTable<DynamicMenuItem>( 500, GWT.<CellTable.Resources>create( CellTable.Resources.class ), keyProvider, null );

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    private DynamicMenuEditorPresenter presenter;

    private DynamicMenuItem editedItem;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editedItem = null;
    }

    @Override
    public void init( final DynamicMenuEditorPresenter presenter ) {
        this.presenter = presenter;

        driver.initialize( this );

        setMenuItem( new DynamicMenuItem() );

        initTable( menuItems );
    }

    private void initTable( final AbstractCellTable<DynamicMenuItem> dynamicMenuTable ) {
        dynamicMenuTable.setEmptyTableWidget( new Label( CommonConstants.INSTANCE.MenusNoMenuItems() ) );

        //We need to inform the SelectionModel that the ButtonCell (i.e. Delete) column is excluded from selecting a row
        final DefaultSelectionEventManager<DynamicMenuItem> manager = DefaultSelectionEventManager.createBlacklistManager( 4 );
        dynamicMenuTable.setSelectionModel( selectionModel,
                                            manager );

        //Furthermore we cannot have a KeyboardSelectionPolicy with a ButtonCell and a SelectionModel
        dynamicMenuTable.setKeyboardSelectionPolicy( HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED );

        {
            final IconCell iCell = new IconCell( IconType.ARROW_UP );
            iCell.setTooltip( CommonConstants.INSTANCE.MenusMoveUpHint() );

            final Column<DynamicMenuItem, String> iconColumn = new Column<DynamicMenuItem, String>( iCell ) {
                public String getValue( DynamicMenuItem object ) {
                    return "";
                }
            };

            iconColumn.setFieldUpdater( new FieldUpdater<DynamicMenuItem, String>() {
                @Override
                public void update( final int index,
                                    final DynamicMenuItem object,
                                    final String value ) {
                    presenter.updateIndex( object,
                                           index,
                                           DynamicMenuEditorPresenter.UpdateIndexOperation.UP );
                }
            } );

            dynamicMenuTable.addColumn( iconColumn );
            dynamicMenuTable.setColumnWidth( iconColumn,
                                             "25px" );
        }

        {
            final IconCell iCell = new IconCell( IconType.ARROW_DOWN );
            iCell.setTooltip( CommonConstants.INSTANCE.MenusMoveDownHint() );

            final Column<DynamicMenuItem, String> iconColumn = new Column<DynamicMenuItem, String>( iCell ) {
                public String getValue( DynamicMenuItem object ) {
                    return "";
                }
            };

            iconColumn.setFieldUpdater( new FieldUpdater<DynamicMenuItem, String>() {
                @Override
                public void update( final int index,
                                    final DynamicMenuItem object,
                                    final String value ) {
                    presenter.updateIndex( object,
                                           index,
                                           DynamicMenuEditorPresenter.UpdateIndexOperation.DOWN );
                }
            } );

            dynamicMenuTable.addColumn( iconColumn );
            dynamicMenuTable.setColumnWidth( iconColumn,
                                             "25px" );
        }

        {
            final TextColumn<DynamicMenuItem> activityCol = new TextColumn<DynamicMenuItem>() {

                @Override
                public String getValue( DynamicMenuItem object ) {
                    return String.valueOf( object.getActivityId() );
                }
            };

            dynamicMenuTable.addColumn( activityCol,
                                        CommonConstants.INSTANCE.MenusActivityID() );
        }

        {
            final TextColumn<DynamicMenuItem> labelCol = new TextColumn<DynamicMenuItem>() {

                @Override
                public String getValue( DynamicMenuItem object ) {
                    return object.getMenuLabel();
                }
            };

            dynamicMenuTable.addColumn( labelCol,
                                        CommonConstants.INSTANCE.MenusLabel() );
        }

        {
            final ButtonCell buttonCell = new ButtonCell( IconType.REMOVE,
                                                          ButtonType.DANGER,
                                                          ButtonSize.EXTRA_SMALL );

            final Column<DynamicMenuItem, String> buttonCol = new Column<DynamicMenuItem, String>( buttonCell ) {
                @Override
                public String getValue( DynamicMenuItem object ) {
                    return CommonConstants.INSTANCE.MenusDelete();
                }
            };

            buttonCol.setFieldUpdater( new FieldUpdater<DynamicMenuItem, String>() {
                @Override
                public void update( final int index,
                                    final DynamicMenuItem object,
                                    final String value ) {
                    if ( selectionModel.isSelected( object ) ) {
                        selectionModel.clear();
                    }
                    presenter.removeObject( object );
                }
            } );

            dynamicMenuTable.addColumn( buttonCol );
            dynamicMenuTable.setColumnWidth( buttonCol,
                                             "80px" );
        }

        selectionModel.addSelectionChangeHandler( new Handler() {

            @Override
            public void onSelectionChange( SelectionChangeEvent event ) {
                //ListDataProvider raises this event with a null item when a item is removed
                if ( selectionModel.getSelectedObject() == null ) {
                    editedItem = null;
                    setMenuItem( new DynamicMenuItem() );
                } else {
                    editedItem = selectionModel.getSelectedObject();
                    setMenuItem( new DynamicMenuItem( selectionModel.getSelectedObject().getActivityId(), selectionModel.getSelectedObject().getMenuLabel() ) );
                }
            }
        } );

        presenter.setDataDisplay( dynamicMenuTable );
    }

    @UiHandler("okButton")
    public void onClick( ClickEvent e ) {
        DynamicMenuItem menuItem = driver.flush();

        if ( isMenuItemValid( menuItem ) ) {
            if ( editedItem != null ) {
                editedItem.setActivityId( menuItem.getActivityId() );
                editedItem.setMenuLabel( menuItem.getMenuLabel() );
                menuItem = editedItem;
            }
            presenter.addMenuItem( menuItem );
            setMenuItem( new DynamicMenuItem() );
            selectionModel.clear();
        }
    }

    @UiHandler("cancelButton")
    public void onCancel( ClickEvent e ) {
        setMenuItem( new DynamicMenuItem() );
        selectionModel.clear();
    }

    private boolean isMenuItemValid( final DynamicMenuItem menuItem ) {
        boolean activityIdValidatorResult = presenter.getMenuItemActivityIdValidator().validateFieldInline( menuItem.getActivityId(), activityIdControlGroup, activityIdHelpInline );
        boolean menuLabelValidatorResult = presenter.getMenuItemLabelValidator( menuItem, editedItem ).validateFieldInline( menuItem.getMenuLabel(), menuLabelControlGroup, menuLabelHelpInline );

        return activityIdValidatorResult && menuLabelValidatorResult;
    }

    public void setMenuItem( final DynamicMenuItem menuItem ) {
        driver.edit( menuItem );

        activityIdControlGroup.setValidationState( ValidationState.NONE );
        activityIdHelpInline.setText( "" );

        menuLabelControlGroup.setValidationState( ValidationState.NONE );
        menuLabelHelpInline.setText( "" );
    }

    public String emptyActivityID() {
        return CommonConstants.INSTANCE.EmptyActivityID();
    }

    public String invalidActivityID() {
        return CommonConstants.INSTANCE.InvalidActivityID();
    }

    public String emptyMenuLabel() {
        return CommonConstants.INSTANCE.EmptyMenuLabel();
    }

    public String invalidMenuLabel() {
        return CommonConstants.INSTANCE.InvalidMenuLabel();
    }

    public String duplicatedMenuLabel() {
        return CommonConstants.INSTANCE.DuplicatedMenuLabel();
    }
}