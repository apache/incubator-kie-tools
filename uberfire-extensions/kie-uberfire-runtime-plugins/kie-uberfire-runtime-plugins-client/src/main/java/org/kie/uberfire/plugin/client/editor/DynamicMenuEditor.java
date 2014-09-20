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

package org.kie.uberfire.plugin.client.editor;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.plugin.client.type.DynamicMenuResourceType;
import org.kie.uberfire.plugin.model.DynamicMenu;
import org.kie.uberfire.plugin.model.DynamicMenuItem;
import org.kie.uberfire.plugin.model.Plugin;
import org.kie.uberfire.plugin.model.PluginType;
import org.kie.uberfire.plugin.service.PluginServices;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchEditor(identifier = "Dynamic Menu Editor", supportedTypes = { DynamicMenuResourceType.class }, priority = Integer.MAX_VALUE)
public class DynamicMenuEditor extends Composite implements Editor<DynamicMenuItem> {

    interface ViewBinder
            extends
            UiBinder<Widget, DynamicMenuEditor> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

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
    CellTable<DynamicMenuItem> menuItems = new CellTable<DynamicMenuItem>( 500, GWT.<CellTable.SelectableResources>create( CellTable.SelectableResources.class ) );

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private PlaceManager placeManager;

    private PlaceRequest place;

    interface Driver extends SimpleBeanEditorDriver<DynamicMenuItem, DynamicMenuEditor> {

    }

    private Driver driver = GWT.create( Driver.class );

    private ListDataProvider<DynamicMenuItem> dataProvider = new ListDataProvider<DynamicMenuItem>();

    private DynamicMenu menuItem;

    private org.uberfire.backend.vfs.Path path;

    private Plugin plugin;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );

        driver.initialize( this );

        setMenuItem( new DynamicMenuItem() );

        initTable( menuItems );
    }

    @OnStartup
    public void onStartup( final org.uberfire.backend.vfs.Path path,
                           final PlaceRequest place ) {
        pluginServices.call( new RemoteCallback<DynamicMenu>() {

            @Override
            public void callback( final DynamicMenu response ) {
                menuItem = response;
                for ( final DynamicMenuItem menuItem : response.getMenuItems() ) {
                    dataProvider.getList().add( menuItem );
                }
            }
        } ).getDynamicMenuContent( path );
        this.place = place;
        this.path = path;
        plugin = new Plugin( place.getParameter( "name", "" ), PluginType.DYNAMIC_MENU, path );
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return new PluginsCommonMenu().build( new Command() {
            @Override
            public void execute() {
                pluginServices.call().save( new DynamicMenu( menuItem.getName(), PluginType.DYNAMIC_MENU, path, new ArrayList<DynamicMenuItem>( dataProvider.getList() ) ) );
            }
        }, new Command() {
            @Override
            public void execute() {
                pluginServices.call().delete( menuItem );
                placeManager.forceClosePlace( place );
            }
        } );
    }

    private void initTable( final AbstractCellTable<DynamicMenuItem> exampleTable ) {
        exampleTable.setEmptyTableWidget( new Label( "No menu items." ) );

        final TextColumn<DynamicMenuItem> activityCol = new TextColumn<DynamicMenuItem>() {

            @Override
            public String getValue( DynamicMenuItem object ) {
                return String.valueOf( object.getActivityId() );
            }
        };

        exampleTable.addColumn( activityCol, "Activity" );

        final TextColumn<DynamicMenuItem> labelCol = new TextColumn<DynamicMenuItem>() {

            @Override
            public String getValue( DynamicMenuItem object ) {
                return object.getMenuLabel();
            }
        };

        exampleTable.addColumn( labelCol, "Label" );

        final ButtonCell buttonCell = new ButtonCell( IconType.REMOVE, ButtonType.DANGER, ButtonSize.MINI );

        final TooltipCellDecorator<String> decorator = new TooltipCellDecorator<String>( buttonCell );
        decorator.setText( "delete row, if click" );

        Column<DynamicMenuItem, String> buttonCol = new Column<DynamicMenuItem, String>( decorator ) {

            @Override
            public String getValue( DynamicMenuItem object ) {
                return "delete";
            }
        };

        buttonCol.setFieldUpdater( new FieldUpdater<DynamicMenuItem, String>() {
            @Override
            public void update( final int index,
                                final DynamicMenuItem object,
                                final String value ) {
                dataProvider.getList().remove( object );
                dataProvider.flush();
                dataProvider.refresh();
            }
        } );

        exampleTable.addColumn( buttonCol );

        final SingleSelectionModel<DynamicMenuItem> selectionModel = new SingleSelectionModel<DynamicMenuItem>();

        selectionModel.addSelectionChangeHandler( new Handler() {

            @Override
            public void onSelectionChange( SelectionChangeEvent event ) {
                DynamicMenuItem person = selectionModel.getSelectedObject();
                DynamicMenuEditor.this.driver.edit( person );
            }
        } );

        exampleTable.setKeyboardSelectionPolicy( HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.BOUND_TO_SELECTION );

        exampleTable.setSelectionModel( selectionModel );

        dataProvider.addDataDisplay( exampleTable );
    }

    @UiHandler("okButton")
    public void onClick( ClickEvent e ) {

        final DynamicMenuItem menuItem = driver.flush();

        boolean hasError = false;

        if ( menuItem.getActivityId() == null || menuItem.getActivityId().isEmpty() ) {

            activityIdControlGroup.setType( ControlGroupType.ERROR );
            activityIdHelpInline.setText( "Activity Id is mandatory" );
            hasError = true;
        }

        if ( menuItem.getMenuLabel() == null || menuItem.getMenuLabel().isEmpty() ) {
            menuLabelControlGroup.setType( ControlGroupType.ERROR );
            menuLabelHelpInline.setText( "Label is mandatory." );
            hasError = true;
        }

        if ( hasError ) {
            return;
        }

        addMenuItem( menuItem );

        setMenuItem( new DynamicMenuItem() );
    }

    @UiHandler("cancelButton")
    public void onCancel( ClickEvent e ) {
        setMenuItem( new DynamicMenuItem() );
    }

    private void addMenuItem( final DynamicMenuItem menuItem ) {

        DynamicMenuItem existingItem = null;
        for ( final DynamicMenuItem item : dataProvider.getList() ) {
            if ( menuItem.getMenuLabel().equals( item.getMenuLabel() ) ) {
                existingItem = item;
                break;
            }
        }
        if ( existingItem == null ) {
            dataProvider.getList().add( menuItem );
        } else {
            menuItems.getSelectionModel().setSelected( menuItem, false );
            dataProvider.refresh();
        }

        dataProvider.flush();
    }

    public void setMenuItem( final DynamicMenuItem menuItem ) {
        driver.edit( menuItem );

        activityIdControlGroup.setType( ControlGroupType.NONE );
        activityIdHelpInline.setText( "" );

        menuLabelControlGroup.setType( ControlGroupType.NONE );
        menuLabelHelpInline.setText( "" );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Dynamic Menu Editor [" + plugin.getName() + "]";
    }

}