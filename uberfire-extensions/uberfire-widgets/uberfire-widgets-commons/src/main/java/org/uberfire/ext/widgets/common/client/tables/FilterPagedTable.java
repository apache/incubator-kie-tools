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

package org.uberfire.ext.widgets.common.client.tables;

import java.util.ArrayList;
import java.util.HashMap;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.shared.event.TabShowEvent;
import org.gwtbootstrap3.client.shared.event.TabShowHandler;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;


/**
 * Widget that shows rows of paged data.
 */
public class FilterPagedTable<T>
        extends Composite {

    @UiField
    NavTabs navTabs;

    @UiField
    TabContent tabContent;

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


    public void removeTab( String gridKey ) {
        int index = getGridIndex( gridKey );
        if ( index != -1 ) {

            dataGridFilterHashMap.remove( gridKey );

            removeTab( index );
            multiGridPreferencesStore.removeTab( gridKey );
            multiGridPreferencesStore.setSelectedGrid( "" );
            if( navTabs.getWidgetCount() > 1 ){
                selectTab( index == 0 ? 0 : index - 1 );
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

        if ( navTabs.getWidgetCount() > 0 ) {
            removeTab( navTabs.getWidgetCount() - 1 );
        }

        addTab( pagedTable, key, filterCommand );

        addAddTableButton( button );
        selectTab( dataGridFilterHashMap.size() - 1 );
    }

    public void addTab( final PagedTable<T> grid, final String key, Command filterCommand ) {

        dataGridFilterHashMap.put( key, new DataGridFilter( key, filterCommand ) );

        final String gridHeader = multiGridPreferencesStore.getGridSettingParam( key, NewTabFilterPopup.FILTER_TAB_NAME_PARAM );
        final String gridTitle = multiGridPreferencesStore.getGridSettingParam( key, NewTabFilterPopup.FILTER_TAB_DESC_PARAM );
        grid.addTableTitle( gridTitle );

        Button close = null;
        if ( !"base".equals( key ) ) {
            close = GWT.create( Button.class );
            close.setType( ButtonType.LINK );
            close.setIcon( IconType.TIMES );
            close.setSize( ButtonSize.EXTRA_SMALL );
            close.setTitle( "close " + gridHeader );
            close.getElement().getStyle().setVerticalAlign( Style.VerticalAlign.TEXT_TOP );
            close.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    getYesNoCancelPopup(gridHeader, key).show();
                }
            } );
        }

        addContentTab( gridHeader, close, grid, key );
        selectTab( dataGridFilterHashMap.size() - 1 );
    }

    protected YesNoCancelPopup getYesNoCancelPopup( final String gridHeader, final String key) {
        return YesNoCancelPopup.newYesNoCancelPopup( CommonConstants.INSTANCE.RemoveTabTitle(),
                                CommonConstants.INSTANCE.RemoveTabConfirm( gridHeader ),
                                new Command() {
                                    @Override public void execute() {
                                        removeTab( key );
                                    }
                                },
                                null,
                                new Command() {
                                    @Override public void execute() {
                                    }
                                });
    }

    public void addAddTableButton( Button addTableButton ) {
        addContentTab( null, addTableButton, GWT.<HTML>create(HTML.class), null );
    }

    public Widget makeWidget() {
        return  uiBinder.createAndBindUi( this );
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
            selectTab( selectedTab );
        else
            selectTab( 0 );
    }

    public void saveTabSettings( String key, HashMap<String, Object> params ) {
        multiGridPreferencesStore.setGridSettings( key, params );
        preferencesService.call().saveUserPreferences( multiGridPreferencesStore );
    }

    public void saveNewTabSettings( String key, HashMap<String, Object> params ) {
        multiGridPreferencesStore.addNewTab( key, params );
        preferencesService.call().saveUserPreferences( multiGridPreferencesStore );
    }

    public void saveNewRefreshInterval(int newValue ) {
        multiGridPreferencesStore.setRefreshInterval( newValue );
        preferencesService.call().saveUserPreferences( multiGridPreferencesStore );
    }

    public int getRefreshInterval( ) {
        return multiGridPreferencesStore.getRefreshInterval();
    }

    public void setRefreshGridCommand( Command refreshGridCommand ) {
        this.refreshGridCommand = refreshGridCommand;
    }


    private void selectTab( int index ){
        final TabListItem widget = (TabListItem) navTabs.getWidget( index );
        if( widget != null ){
            widget.showTab();
        }
    }

    public void removeTab( int index ){
        if( index < 0 ){
            return;
        }
        if( index < navTabs.getWidgetCount()) {
            navTabs.remove( index );
        }
        if( index < tabContent.getWidgetCount()) {
            tabContent.remove( index );
        }
    }

    private void addContentTab( final String title, final Widget titleWidget, final Widget content, final String key ){
        final TabListItem tabListItem = GWT.create( TabListItem.class );
        tabListItem.addShowHandler( new TabShowHandler() {
            @Override
            public void onShow( TabShowEvent event ) {
                if(key!=null) {
                    multiGridPreferencesStore.setSelectedGrid( key );
                    preferencesService.call().saveUserPreferences( multiGridPreferencesStore );
                    dataGridFilterHashMap.get( key ).getFilterCommand().execute();
                }
            }
        } );

        final TabPane tabPane = GWT.create( TabPane.class );
        tabPane.add( content );

        tabListItem.setDataTargetWidget( tabPane );
        if( title != null ){
            tabListItem.setText( title );
        }
        if( titleWidget != null && tabListItem.getWidget( 0 ) instanceof Anchor ) {
            ((Anchor) tabListItem.getWidget( 0 )).add( titleWidget );
        }
        navTabs.add( tabListItem );
        tabContent.add( tabPane );
    }
}