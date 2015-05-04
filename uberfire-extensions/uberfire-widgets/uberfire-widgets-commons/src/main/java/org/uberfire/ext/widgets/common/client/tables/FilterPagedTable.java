/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uberfire.ext.widgets.common.client.tables;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Widget that shows rows of paged data.
 */
public class FilterPagedTable<T>
        extends Composite {

    @UiField
    public TabPanel tabPanel;

    interface Binder
            extends
            UiBinder<Widget, FilterPagedTable> {

    }

    private HashMap<String, DataGridFilter> dataGridFilterHashMap = new HashMap<String, DataGridFilter>();

    protected MultiGridPreferencesStore multiGridPreferencesStore;
    public Command refreshGridCommand;

    @Inject
    private Caller<UserPreferencesService> preferencesService;

    private static Binder uiBinder = GWT.create( Binder.class );


    public FilterPagedTable() {

    }

    public FilterPagedTable( MultiGridPreferencesStore gridsPreferences ) {
        this.multiGridPreferencesStore = gridsPreferences;
    }


    public void removeTab(String gridKey ) {
        int index = getGridIndex( gridKey );

        if ( index != -1 ) {

            dataGridFilterHashMap.remove( gridKey );

            tabPanel.remove( index );
            multiGridPreferencesStore.removeTab( gridKey );
            multiGridPreferencesStore.setSelectedGrid( "" );
            if(tabPanel.getWidgetCount()>1){
                tabPanel.selectTab( index-1 );
            }
            preferencesService.call().saveUserPreferences( multiGridPreferencesStore );
        }
    }

    public String getValidKeyForAdditionalListGrid( String baseName ) {
        int i;
        for ( i = dataGridFilterHashMap.size(); dataGridFilterHashMap.get( baseName + i ) != null; i++ ) {
        }
        return baseName + i;
    }

    public void createNewTab( PagedTable<T> pagedTable, final String key, Button button, Command filterCommand ) {
        multiGridPreferencesStore.setSelectedGrid( key );
        preferencesService.call().saveUserPreferences( multiGridPreferencesStore );

        if ( tabPanel.getWidgetCount() > 0 ) {
            tabPanel.remove( tabPanel.getWidgetCount() - 1 );
        }

        addTab( pagedTable, key, filterCommand );

        addAddTableButton( button );
        tabPanel.selectTab( dataGridFilterHashMap.size() - 1 );
    }

    public void addTab( final PagedTable<T> grid, final String key, Command filterCommand ) {

        dataGridFilterHashMap.put( key, new DataGridFilter( key, filterCommand ) );

        String gridHeader = multiGridPreferencesStore.getGridSettingParam( key, NewTabFilterPopup.FILTER_TAB_NAME_PARAM );
        String gridTitle = multiGridPreferencesStore.getGridSettingParam( key, NewTabFilterPopup.FILTER_TAB_DESC_PARAM );


        HorizontalPanel panel = new HorizontalPanel();
        panel.setStyleName( "tabHeader" );
        panel.setTitle( gridHeader );
        Label text = new Label();
        text.setText( gridHeader );
        text.setStyleDependentName( "text", true );
        panel.add( text );
        panel.setCellHorizontalAlignment( text, HasHorizontalAlignment.ALIGN_LEFT );

        if ( !"base".equals( key ) ) {
            Button close = new Button();
            close.setIcon( IconType.REMOVE );
            close.setSize( ButtonSize.MINI );
            close.setTitle( "close " + gridHeader );
            text.setStyleDependentName( "close", true );
            close.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    removeTab( key );
                }
            } );
            panel.add( close );
            panel.setCellHorizontalAlignment( close, HasHorizontalAlignment.ALIGN_RIGHT );
        }

        panel.setWidth( "100px" );
        panel.setHeight( "25px" );

        VerticalPanel tableWithTitle = new VerticalPanel();
        tableWithTitle.add( new HTML( "<h2>" + gridTitle + "</h2>" ) );
        tableWithTitle.add( grid );

        tabPanel.add( tableWithTitle, panel );
        tabPanel.selectTab( dataGridFilterHashMap.size() - 1 );
    }

    public void addAddTableButton( Button addTableButton ) {
        HorizontalPanel panel = new HorizontalPanel();

        panel.setStyleName( "tabHeader" );
        panel.add( addTableButton );


        panel.setWidth( "30px" );
        panel.setHeight( "25px" );
        addTableButton.setSize( ButtonSize.MINI );
        tabPanel.add( new HTML( "Default" ), panel );
    }

    public Widget makeWidget() {
        Widget w = uiBinder.createAndBindUi( this );
        tabPanel.addSelectionHandler( new SelectionHandler<Integer>() {
            @Override
            public void onSelection( SelectionEvent<Integer> event ) {
                Integer selectedPosition = event.getSelectedItem();
                ArrayList<String> tabsId = multiGridPreferencesStore.getGridsId();
                if(selectedPosition< tabsId.size()) {
                    String key = tabsId.get( selectedPosition );
                    multiGridPreferencesStore.setSelectedGrid( key );
                    preferencesService.call().saveUserPreferences( multiGridPreferencesStore );
                    dataGridFilterHashMap.get( key ).getFilterCommand().execute();
                }
            }
        } );


        return w;
    }

    public MultiGridPreferencesStore getMultiGridPreferencesStore() {
        return multiGridPreferencesStore;
    }

    public void setMultiGridPreferencesStore( MultiGridPreferencesStore multiGridPreferencesStore ) {
        this.multiGridPreferencesStore = multiGridPreferencesStore;
    }

    public void setPreferencesService( Caller<UserPreferencesService> preferencesService ) {
        this.preferencesService = preferencesService;
    }

    private int getGridIndex( String key ) {
        if ( key == null ) return -1;
        ArrayList<String> tabsId = multiGridPreferencesStore.getGridsId();
        for ( int i = 0; i < tabsId.size(); i++ ) {
            if ( key.equals( tabsId.get( i ) ) ) return i;
        }
        return -1;
    }

    public void setSelectedTab() {
        int selectedTab = getGridIndex( getMultiGridPreferencesStore().getSelectedGrid() );
        if ( selectedTab != -1 )
            tabPanel.selectTab( selectedTab );
        else
            tabPanel.selectTab( 0 );

    }

    public void saveTabSettings( String key, HashMap<String, Object> params ) {
        multiGridPreferencesStore.setGridSettings( key, params );
        preferencesService.call().saveUserPreferences( multiGridPreferencesStore );
    }

    public void saveNewTabSettings( String key, HashMap<String, Object> params ) {
        multiGridPreferencesStore.addNewTab( key, params );
        preferencesService.call().saveUserPreferences( multiGridPreferencesStore );
    }

    public void setRefreshGridCommand( Command refreshGridCommand ) {
        this.refreshGridCommand = refreshGridCommand;
    }


}