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
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
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

    @UiField(provided = true)
    public Button pageSizesSelector;

    private boolean showPageSizesSelector = false;

    private PopupPanel pageSizePopup ;

    public PagedTable(){
        super();
    }

    public PagedTable( final int pageSize ) {
        super();
        this.pageSize=pageSize;
        this.dataGrid.setPageSize( pageSize );
        this.pager.setDisplay( dataGrid );
    }

    public PagedTable( final int pageSize,
                       final ProvidesKey<T> providesKey ) {
        super( providesKey );
        this.pageSize =pageSize;
        this.dataGrid.setPageSize( pageSize );
        this.pager.setDisplay( dataGrid );
    }

    public PagedTable( final int pageSize,
                       final ProvidesKey<T> providesKey,
                       final GridGlobalPreferences gridGlobalPreferences ) {
        super( providesKey, gridGlobalPreferences );
        pageSizesSelector.setVisible( false );
        this.pageSize=pageSize;
        this.dataGrid.setPageSize( pageSize );
        this.pager.setDisplay( dataGrid );
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
    }
    public PagedTable( final int pageSize,
                       final ProvidesKey<T> providesKey,
                       final GridGlobalPreferences gridGlobalPreferences,
                       final boolean showPageSizesSelector,
                       final boolean showFilterSelector) {

        super( providesKey, gridGlobalPreferences );
        this.showPageSizesSelector = showPageSizesSelector;
        this.pageSize=pageSize;
        this.dataGrid.setPageSize( pageSize );
        this.pager.setDisplay( dataGrid );
        super.setShowFilterSelector( showFilterSelector );
    }

    protected Widget makeWidget() {
        this.pageSizePopup = new PopupPanel(true);
        pageSizesSelector = createPageSizesToggleButton();
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
        pageSizesSelector.setVisible( this.showPageSizesSelector );
    }

    public Button createPageSizesToggleButton() {
        final Button button = new Button();
        button.setToggle( true );
        button.setIcon( IconType.LIST_ALT );
        button.setTitle( CommonConstants.INSTANCE.PageSizeSelectorTooltip() );

        pageSizePopup.getElement().getStyle().setZIndex( Integer.MAX_VALUE );
        pageSizePopup.addAutoHidePartner( button.getElement() );
        pageSizePopup.addCloseHandler( new CloseHandler<PopupPanel>() {
            public void onClose( CloseEvent<PopupPanel> popupPanelCloseEvent ) {
                button.setActive( false );
            } });

        button.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                if ( !button.isActive() ) {
                    showSelectPageSizePopup( button.getAbsoluteLeft() + button.getOffsetWidth(),
                            button.getAbsoluteTop() + button.getOffsetHeight() );
                } else {
                    pageSizePopup.hide( );
                }
            }
        } );
        return button;
    }

    private void showSelectPageSizePopup(final int left,
                                         final int top) {
        VerticalPanel popupContent = new VerticalPanel();
        RadioButton rb;
        for (int i=5;i<20;i=i+5) {
            rb = new RadioButton("pageSizes", String.valueOf( i ));
            rb.setText( String.valueOf( i ) + " " + CommonConstants.INSTANCE.Items() );
            if(i==pageSize) rb.setValue( true );
            final int selectedPageSize =i;
            rb.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    storePageSizeInGridPreferences( selectedPageSize );
                    loadPageSizePreferences( );
                    pageSizePopup.hide();
                }
            } );
            popupContent.add(rb);
        }
        Button resetButton = new Button( CommonConstants.INSTANCE.Reset() );
        resetButton.setSize( ButtonSize.MINI );
        resetButton.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                resetPageSize();
                pageSizePopup.hide();
            }
        } );

        popupContent.add( resetButton );


        pageSizePopup.setWidget( popupContent );
        pageSizePopup.show();
        int finalLeft = left - pageSizePopup.getOffsetWidth();
        pageSizePopup.setPopupPosition( finalLeft, top );

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
}