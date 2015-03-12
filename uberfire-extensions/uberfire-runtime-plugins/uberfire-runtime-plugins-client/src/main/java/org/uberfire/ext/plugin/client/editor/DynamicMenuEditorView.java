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

package org.uberfire.ext.plugin.client.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.TooltipCellDecorator;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
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
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.editor.commons.client.BaseEditorViewImpl;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.ext.plugin.client.widget.cell.IconCell;
import org.uberfire.ext.plugin.model.DynamicMenuItem;

@Dependent
public class DynamicMenuEditorView
        extends BaseEditorViewImpl
        implements UberView<DynamicMenuEditorPresenter>,
                   Editor<DynamicMenuItem> {

    interface ViewBinder
            extends
            UiBinder<Widget, DynamicMenuEditorView> {

    }

    interface Driver extends SimpleBeanEditorDriver<DynamicMenuItem, DynamicMenuEditorView> {

    }

    final private Driver driver = GWT.create( Driver.class );

    final private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    TextBox activityId;

    @UiField
    ControlGroup activityIdControlGroup;

    @UiField
    @Ignore
    HelpInline activityIdHelpInline;

    @UiField
    TextBox menuLabel;

    @UiField
    ControlGroup menuLabelControlGroup;

    @UiField
    @Ignore
    HelpInline menuLabelHelpInline;

    @UiField(provided = true)
    CellTable<DynamicMenuItem> menuItems = new CellTable<DynamicMenuItem>( 500,
                                                                           GWT.<CellTable.SelectableResources>create( CellTable.SelectableResources.class ) );

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    private DynamicMenuEditorPresenter presenter;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
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
                                                          ButtonSize.MINI );

            final TooltipCellDecorator<String> decorator = new TooltipCellDecorator<String>( buttonCell );
            decorator.setText( CommonConstants.INSTANCE.MenusDeleteHint() );

            final Column<DynamicMenuItem, String> buttonCol = new Column<DynamicMenuItem, String>( decorator ) {
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
                    presenter.removeObject( object );
                }
            } );

            dynamicMenuTable.addColumn( buttonCol );
            dynamicMenuTable.setColumnWidth( buttonCol,
                                             "80px" );
        }

        final SingleSelectionModel<DynamicMenuItem> selectionModel = new SingleSelectionModel<DynamicMenuItem>();

        selectionModel.addSelectionChangeHandler( new Handler() {

            @Override
            public void onSelectionChange( SelectionChangeEvent event ) {
                driver.edit( selectionModel.getSelectedObject() );
            }
        } );

        dynamicMenuTable.setKeyboardSelectionPolicy( HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.BOUND_TO_SELECTION );

        dynamicMenuTable.setSelectionModel( selectionModel );

        presenter.setDataDisplay( dynamicMenuTable );
    }

    @UiHandler("okButton")
    public void onClick( ClickEvent e ) {
        final DynamicMenuItem menuItem = driver.flush();

        boolean hasError = false;

        if ( menuItem.getActivityId() == null || menuItem.getActivityId().trim().isEmpty() ) {
            activityIdControlGroup.setType( ControlGroupType.ERROR );
            activityIdHelpInline.setText( CommonConstants.INSTANCE.MenusActivityIDIsManatory() );
            hasError = true;
        } else {
            activityIdControlGroup.setType( ControlGroupType.NONE );
            activityIdHelpInline.setText( "" );
        }

        if ( menuItem.getMenuLabel() == null || menuItem.getMenuLabel().trim().isEmpty() ) {
            menuLabelControlGroup.setType( ControlGroupType.ERROR );
            menuLabelHelpInline.setText( CommonConstants.INSTANCE.MenusLabelIsManatory() );
            hasError = true;
        } else {
            menuLabelControlGroup.setType( ControlGroupType.NONE );
            menuLabelHelpInline.setText( "" );
        }

        if ( hasError ) {
            return;
        }

        presenter.addMenuItem( menuItem );

        setMenuItem( new DynamicMenuItem() );
    }

    @UiHandler("cancelButton")
    public void onCancel( ClickEvent e ) {
        setMenuItem( new DynamicMenuItem() );
    }

    public void setSelected( final DynamicMenuItem menuItem ) {
        menuItems.getSelectionModel().setSelected( menuItem, false );
    }

    public void setMenuItem( final DynamicMenuItem menuItem ) {
        driver.edit( menuItem );

        activityIdControlGroup.setType( ControlGroupType.NONE );
        activityIdHelpInline.setText( "" );

        menuLabelControlGroup.setType( ControlGroupType.NONE );
        menuLabelHelpInline.setText( "" );
    }

}