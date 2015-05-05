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
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.widgets.common.client.resources.UberfireSimplePagerResources;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;


/**
 * Widget that shows rows of paged data.
 */
public class PagedTable<T>
        extends SimpleTable<T> {

    interface Binder
            extends
            UiBinder<Widget, PagedTable> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    private int pageSize;
    private AsyncDataProvider<T> dataProvider;

    @UiField
    public UberfireSimplePager pager;

    @UiField
    public ListBox pageSizesListBox;

    private boolean showPageSizesSelector = false;

    private boolean showFFButton= true;
    private boolean showLButton = true;

    public PagedTable(){
        super();
    }

    public PagedTable( final int pageSize ) {
        super();
        this.pageSize=pageSize;
        this.dataGrid.setPageSize( pageSize );
        this.pager.setDisplay( dataGrid );
        createPageSizesListBox();
    }

    public PagedTable( final int pageSize,
                       final ProvidesKey<T> providesKey ) {
        super( providesKey );
        this.pageSize =pageSize;
        this.dataGrid.setPageSize( pageSize );
        this.pager.setDisplay( dataGrid );
        createPageSizesListBox();
    }

    public PagedTable( final int pageSize,
                       final ProvidesKey<T> providesKey,
                       final GridGlobalPreferences gridGlobalPreferences ) {
        super( providesKey, gridGlobalPreferences );
        pageSizesListBox.setVisible( false );
        this.pageSize=pageSize;
        this.dataGrid.setPageSize( pageSize );
        this.pager.setDisplay( dataGrid );
        createPageSizesListBox();
    }

    public PagedTable( final int pageSize,
                       final ProvidesKey<T> providesKey,
                       final GridGlobalPreferences gridGlobalPreferences,
                       final boolean showPageSizesSelector ) {

        super( providesKey, gridGlobalPreferences );
        this.showPageSizesSelector = showPageSizesSelector;
        this.pageSize=pageSize;
        this.dataGrid.setPageSize( pageSize );
        this.pager.setDisplay( dataGrid );
        createPageSizesListBox();
    }

    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    /**
     * Link a data provider to the table
     * @param dataProvider
     */
    public void setDataProvider( final AsyncDataProvider<T> dataProvider ) {
        this.dataProvider = dataProvider;
        this.dataProvider.addDataDisplay( dataGrid );
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getPageStart() {
        return this.pager.getPageStart();
    }

    public final void loadPageSizePreferences(  ) {
        pageSize = getPageSizeStored();
        this.dataGrid.setPageSize( pageSize );
        this.pager.setPageSize( pageSize );
        this.dataGrid.setHeight( ( pageSize * 41 ) + 42 + "px" );
        pageSizesListBox.setVisible( this.showPageSizesSelector );
    }

    public void createPageSizesListBox() {
        pageSizesListBox.clear();
        for (int i=5;i<20;i=i+5) {
            pageSizesListBox.addItem(String.valueOf( i ) + " " + CommonConstants.INSTANCE.Items(),String.valueOf(i));
            if(i==pageSize){
                pageSizesListBox.setSelectedValue( String.valueOf(i) );
            }
        }

        pageSizesListBox.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                storePageSizeInGridPreferences( Integer.parseInt( pageSizesListBox.getValue()) );
                loadPageSizePreferences( );
            }
        } );
    }

    private void storePageSizeInGridPreferences(int pageSize) {
        GridPreferencesStore gridPreferencesStore =super.getGridPreferencesStore();
        if ( gridPreferencesStore != null ) {
            gridPreferencesStore.setPageSizePreferences( pageSize );
            super.saveGridPreferences();
        }
    }

    private int getPageSizeStored(){
        GridPreferencesStore gridPreferencesStore =super.getGridPreferencesStore();
        if ( gridPreferencesStore != null ) {
            return gridPreferencesStore.getPageSizePreferences();
        }
        return pageSize;
    }

    private void resetPageSize() {
        GridPreferencesStore gridPreferencesStore = super.getGridPreferencesStore();

        if ( gridPreferencesStore != null ) {
            gridPreferencesStore.resetPageSizePreferences();
            storePageSizeInGridPreferences( gridPreferencesStore.getGlobalPreferences().getPageSize() );
            loadPageSizePreferences();
        }
    }


    public void setShowLastPagerButton( boolean showLastPagerButton ) {
        this.showLButton = showLastPagerButton;
    }

    public void setShowFastFordwardPagerButton( boolean showFastFordwardPagerButton ) {
        this.showFFButton = showFastFordwardPagerButton;
    }

    @UiFactory
    public UberfireSimplePager makeUberfireSimplePager () {
        return new UberfireSimplePager(
                UberfireSimplePager.TextLocation.CENTER,
                UberfireSimplePagerResources.INSTANCE,
                this.showFFButton,          //avoid pager FastForwardButton
                100,
                this.showLButton );        //avoid pager LastPageButton
    }
}