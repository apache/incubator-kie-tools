/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.client.editor.datasource.selector;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.HasData;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;

@Templated
@Dependent
public class DataSourceSelectorViewImpl
        extends Composite
        implements DataSourceSelectorView {

    private DataSourceSelectorView.Presenter presenter;

    private BaseModal modal;

    @Inject
    @DataField( "main-panel" )
    private FlowPanel mainPanel;

    @Inject
    private PagedTable<DataSourceSelectorPageRow> dataGrid = new PagedTable<>();

    private DataSourceSelectorPageRow selectedRow;

    private boolean cancelNextHiddenEvent = false;

    @Inject
    private TranslationService translationService;

    public DataSourceSelectorViewImpl() {
    }

    @PostConstruct
    private void init() {
        dataGrid.setHeight( "200px" );
        dataGrid.setColumnPickerButtonVisible( false );
        dataGrid.setEmptyTableCaption( translationService.getTranslation(
                DataSourceManagementConstants.DataSourceSelector_NoAvailableDataSourcesMessage ) );

        Column<DataSourceSelectorPageRow, String> nameColumn = new Column<DataSourceSelectorPageRow, String>( new TextCell() ) {
            @Override public String getValue( DataSourceSelectorPageRow row ) {
                return row.getDataSourceDefInfo().getName();
            }
        };
        dataGrid.addColumn( nameColumn,
                translationService.getTranslation( DataSourceManagementConstants.DataSourceSelector_DataSourceColumn ) );

        Column<DataSourceSelectorPageRow, String> selectorColumn = new Column<DataSourceSelectorPageRow, String>(
                new ButtonCell( ButtonType.PRIMARY, ButtonSize.SMALL ) ) {
            @Override
            public String getValue( DataSourceSelectorPageRow row ) {
                return translationService.getTranslation(
                        DataSourceManagementConstants.DataSourceSelector_SelectButton );
            }
        };

        selectorColumn.setFieldUpdater( new FieldUpdater<DataSourceSelectorPageRow, String>() {
            @Override
            public void update( int index, DataSourceSelectorPageRow row, String value ) {
                selectedRow = row;
                cancelNextHiddenEvent = true;
                modal.hide();
                presenter.onSelect();
            }
        } );
        dataGrid.addColumn( selectorColumn, "" );

        mainPanel.add( dataGrid );

        this.modal = new BaseModal();
        this.modal.setTitle(
                translationService.getTranslation( DataSourceManagementConstants.DataSourceSelector_Title ) );
        this.modal.setBody( this );
        this.modal.addHiddenHandler( new ModalHiddenHandler() {
            @Override
            public void onHidden( ModalHiddenEvent evt ) {
                if ( !cancelNextHiddenEvent ) {
                    presenter.onClose();
                }
                cancelNextHiddenEvent = false;
            }
        } );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public HasData<DataSourceSelectorPageRow> getDisplay() {
        return dataGrid;
    }

    @Override
    public DataSourceSelectorPageRow getSelectedRow() {
        return selectedRow;
    }

    public void show() {
        cancelNextHiddenEvent = false;
        modal.show();
    }
}
