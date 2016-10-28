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

package org.kie.workbench.common.screens.datasource.management.client.dbexplorer.dbobjects;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Label;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.client.util.UIUtil;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;

@Templated
public class DatabaseObjectExplorerViewImpl
        implements DatabaseObjectExplorerView, IsElement {

    private Presenter presenter;

    @Inject
    @DataField( "header-panel" )
    private Div headerPanel;

    @Inject
    @DataField( "schema-selector-label" )
    private Label schemaSelectorLabel;

    @Inject
    @DataField( "schema-selector" )
    private ListBox schemaSelector;

    @Inject
    @DataField( "object-type-selector-label" )
    private Label objectTypeSelectorLabel;

    @Inject
    @DataField( "object-type-selector" )
    private ListBox objectTypeSelector;

    @Inject
    @DataField( "name-filter-label" )
    private Label nameFilterLabel;

    @Inject
    @DataField( "name-filter-textbox" )
    private TextInput nameFilterTextBox;

    @Inject
    @DataField( "filter-button" )
    private Button filterButton;

    @Inject
    @DataField( "results-panel" )
    private FlowPanel resultsPanel;

    private PagedTable< DatabaseObjectRow > dataGrid;

    @Inject
    private TranslationService translationService;

    public DatabaseObjectExplorerViewImpl( ) {
    }

    @PostConstruct
    private void init( ) {
        nameFilterTextBox.setAttribute( "placeholder", translationService.getTranslation(
                DataSourceManagementConstants.DatabaseObjectExplorerViewImpl_namePatternHelp ) );
        dataGrid = new PagedTable<>( 20, new ProvidesKey< DatabaseObjectRow >( ) {
            @Override
            public Object getKey( DatabaseObjectRow item ) {
                return item.getName( );
            }
        } );
        initializeResultsTable( );
        resultsPanel.add( dataGrid );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public String getSchema( ) {
        return schemaSelector.getSelectedValue( );
    }

    @Override
    public String getObjectType( ) {
        return objectTypeSelector.getSelectedValue( );
    }

    @Override
    public String getFilterTerm( ) {
        return nameFilterTextBox.getValue();
    }

    @Override
    public void loadSchemaOptions( List< Pair< String, String > > options, String selectedOption ) {
        UIUtil.loadOptions( schemaSelector, options, selectedOption );
    }

    @Override
    public void loadDatabaseObjectTypeOptions( List< Pair< String, String > > options ) {
        UIUtil.loadOptions( objectTypeSelector, options );
    }

    @Override
    public void setDataProvider( AsyncDataProvider< DatabaseObjectRow > dataProvider ) {
        dataGrid.setDataProvider( dataProvider );
    }

    @Override
    public void showHeaderPanel( boolean show ) {
        headerPanel.setHidden( !show );
    }

    @Override
    public void showSchemaSelector( boolean show ) {
        schemaSelectorLabel.setHidden( !show );
        showElement( schemaSelector.getElement( ), show );
    }

    @Override
    public void showObjectTypeFilter( boolean show ) {
        objectTypeSelectorLabel.setHidden( !show );
        showElement( objectTypeSelector.getElement( ), show );
    }

    @Override
    public void showObjectNameFilter( boolean show ) {
        nameFilterLabel.setHidden( !show );
        showElement( nameFilterTextBox, show );
    }

    @Override
    public void showFilterButton( boolean show ) {
        showElement( filterButton, show );
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

    @EventHandler( "filter-button" )
    private void onFilterClick( ClickEvent event ) {
        presenter.onSearch( );
    }

    private void initializeResultsTable( ) {
        dataGrid.setEmptyTableCaption( translationService.getTranslation(
                DataSourceManagementConstants.DatabaseObjectExplorerViewImpl_dbObjectsListEmpty ) );
        dataGrid.setToolBarVisible( false );
        addNameColumn( );
        addTypeColumn( );
        addOpenColumn( );
    }

    private void addNameColumn( ) {
        Column< DatabaseObjectRow, String > column = new Column< DatabaseObjectRow, String >( new TextCell( ) ) {
            @Override
            public String getValue( DatabaseObjectRow row ) {
                return row.getName( );
            }
        };
        dataGrid.addColumn( column, translationService.getTranslation(
                DataSourceManagementConstants.DatabaseObjectExplorerViewImpl_dbObjectNameColumn ) );
        dataGrid.setColumnWidth( column, 80, Style.Unit.PCT );
    }

    private void addTypeColumn( ) {
        Column< DatabaseObjectRow, String > column = new Column< DatabaseObjectRow, String >( new TextCell( ) ) {
            @Override
            public String getValue( DatabaseObjectRow row ) {
                return row.getType( );
            }
        };
        dataGrid.addColumn( column, translationService.getTranslation(
                DataSourceManagementConstants.DatabaseObjectExplorerViewImpl_dbObjectTypeColumn ) );
        dataGrid.setColumnWidth( column, 10, Style.Unit.PCT );
    }

    private void addOpenColumn( ) {
        Column< DatabaseObjectRow, String > column = new Column< DatabaseObjectRow, String >( new ButtonCell( ButtonType.DEFAULT, ButtonSize.SMALL ) ) {
            @Override
            public String getValue( DatabaseObjectRow row ) {
                return translationService.getTranslation(
                        DataSourceManagementConstants.DatabaseObjectExplorerViewImpl_dbObjectOpen );
            }
        };
        column.setFieldUpdater( new FieldUpdater< DatabaseObjectRow, String >( ) {
            @Override
            public void update( int index,
                                DatabaseObjectRow row,
                                String value ) {
                onOpen( row );
            }
        } );
        dataGrid.addColumn( column, translationService.getTranslation(
                DataSourceManagementConstants.DatabaseObjectExplorerViewImpl_dbObjectActionColumn ) );
        dataGrid.setColumnWidth( column, 10, Style.Unit.PCT );
    }

    private void onOpen( DatabaseObjectRow row ) {
        presenter.onOpen( row );
    }

    private void showElement( com.google.gwt.dom.client.Element element, boolean show ) {
        element.getStyle( ).setDisplay( show ? Style.Display.INLINE_BLOCK : Style.Display.NONE );
    }

    private void showElement( HTMLElement element, boolean show ) {
        element.getStyle().setProperty( "display",
                show ? Style.Display.INLINE_BLOCK.getCssName() : Style.Display.NONE.getCssName() );
    }
}