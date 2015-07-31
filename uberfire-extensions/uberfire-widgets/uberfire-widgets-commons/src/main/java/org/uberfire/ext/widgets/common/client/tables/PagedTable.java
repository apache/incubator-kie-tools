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

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.widgets.common.client.resources.UberfireSimplePagerResources;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;


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
    public ListBox pageSizesSelector;

    public boolean showPageSizesSelector = false;

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
        setShowPageSizesSelector( showPageSizesSelector );
        createPageSizesListBox( 5, 20, 5 );
        storePageSizeInGridPreferences( pageSize );
    }

    public PagedTable( final int pageSize,
                       final ProvidesKey<T> providesKey ) {
        super( providesKey );
        this.pageSize =pageSize;
        this.dataGrid.setPageSize( pageSize );
        this.pager.setDisplay( dataGrid );
        setShowPageSizesSelector( showPageSizesSelector );
        createPageSizesListBox(5,20,5);
    }

    public PagedTable( final int pageSize,
                       final ProvidesKey<T> providesKey,
                       final GridGlobalPreferences gridGlobalPreferences ) {
        super( providesKey, gridGlobalPreferences );
        this.pageSize=pageSize;
        this.dataGrid.setPageSize( pageSize );
        this.pager.setDisplay( dataGrid );
        setShowPageSizesSelector( showPageSizesSelector );
        createPageSizesListBox(5,20,5);
    }

    public PagedTable( final int pageSize,
                       final ProvidesKey<T> providesKey,
                       final GridGlobalPreferences gridGlobalPreferences,
                       final boolean showPageSizesSelector ) {

        super( providesKey, gridGlobalPreferences );
        this.pageSize=pageSize;
        this.dataGrid.setPageSize( pageSize );
        this.pager.setDisplay( dataGrid );
        setShowPageSizesSelector( showPageSizesSelector );
        createPageSizesListBox(5,20,5);
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
        pageSizesSelector.setSelectedValue( String.valueOf( pageSize ) );
        pageSizesSelector.setVisible( this.showPageSizesSelector );
    }


    public void createPageSizesListBox(int minPageSize, int maxPageSize,int incPageSize){
        pageSizesSelector.clear();
        for (int i=minPageSize;i<=maxPageSize;i=i+incPageSize) {
            pageSizesSelector.addItem( String.valueOf( i ) + " " + CommonConstants.INSTANCE.Items(), String.valueOf( i ) );
            if(i==pageSize){
                pageSizesSelector.setSelectedValue( String.valueOf( i ) );
            }
        }

        pageSizesSelector.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                storePageSizeInGridPreferences( Integer.parseInt( pageSizesSelector.getValue() ) );
                pager.setPageStart( 0 );
                loadPageSizePreferences();
            }
        } );
    }

    private void storePageSizeInGridPreferences(int pageSize) {
        GridPreferencesStore gridPreferencesStore =super.getGridPreferencesStore();
        if ( gridPreferencesStore != null ) {
            gridPreferencesStore.setPageSizePreferences( pageSize );
            super.saveGridPreferences();
        }
        this.pageSize=pageSize;
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

    public boolean isShowPageSizesSelector() {
        return showPageSizesSelector;
    }

    public void setShowPageSizesSelector( boolean showPageSizesSelector ) {
        this.showPageSizesSelector = showPageSizesSelector;
        pageSizesSelector.setVisible( this.showPageSizesSelector );
    }

    public void setPageSize (int pageSize){
        this.pageSize = pageSize;
    }
}