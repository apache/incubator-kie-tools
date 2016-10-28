/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datasource.management.client.dbexplorer.schemas;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;

@Templated
public class DatabaseSchemaExplorerViewImpl
        implements DatabaseSchemaExplorerView, IsElement {

    private Presenter presenter;

    @Inject
    @DataField( "content-panel" )
    private FlowPanel contentPanel;

    private PagedTable< DatabaseSchemaRow > dataGrid;

    @Inject
    private TranslationService translationService;

    public DatabaseSchemaExplorerViewImpl( ) {
    }

    @PostConstruct
    private void init( ) {
        dataGrid = new PagedTable<>( 20, new ProvidesKey< DatabaseSchemaRow >( ) {
            @Override
            public Object getKey( DatabaseSchemaRow item ) {
                return item.getName( );
            }
        } );
        initializeTable( );
        contentPanel.add( dataGrid );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setDataProvider( AsyncDataProvider< DatabaseSchemaRow > dataProvider ) {
        dataGrid.setDataProvider( dataProvider );
    }

    @Override
    public void redraw( ) {
        dataGrid.redraw( );
    }

    @Override
    public void showBusyIndicator( String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator( ) {
        BusyPopup.close( );
    }

    private void initializeTable( ) {
        dataGrid.setEmptyTableCaption( translationService.getTranslation(
                DataSourceManagementConstants.DatabaseSchemaExplorerViewImpl_schemasListEmpty ) );
        dataGrid.setToolBarVisible( false );
        addNameColumn( );
        addOpenColumn( );
    }

    private void addNameColumn( ) {
        Column< DatabaseSchemaRow, String > column = new Column< DatabaseSchemaRow, String >( new TextCell( ) ) {
            @Override
            public String getValue( DatabaseSchemaRow row ) {
                return row.getName( );
            }
        };
        dataGrid.addColumn( column, translationService.getTranslation(
                DataSourceManagementConstants.DatabaseSchemaExplorerViewImpl_schemaNameColumn ) );
        dataGrid.setColumnWidth( column, 80, Style.Unit.PCT );
    }

    private void addOpenColumn( ) {
        Column< DatabaseSchemaRow, String > column = new Column< DatabaseSchemaRow, String >( new ButtonCell( ButtonType.DEFAULT, ButtonSize.SMALL ) ) {
            @Override
            public String getValue( DatabaseSchemaRow row ) {
                return translationService.getTranslation(
                        DataSourceManagementConstants.DatabaseSchemaExplorerViewImpl_schemaOpenAction );
            }
        };
        column.setFieldUpdater( new FieldUpdater< DatabaseSchemaRow, String >( ) {
            @Override
            public void update( int index,
                                DatabaseSchemaRow row,
                                String value ) {
                onOpen( row );
            }
        } );
        dataGrid.addColumn( column, translationService.getTranslation(
                DataSourceManagementConstants.DatabaseSchemaExplorerViewImpl_actionColumn ) );
        dataGrid.setColumnWidth( column, 10, Style.Unit.PCT );
    }

    private void onOpen( DatabaseSchemaRow row ) {
        presenter.onOpen( row );
    }

}