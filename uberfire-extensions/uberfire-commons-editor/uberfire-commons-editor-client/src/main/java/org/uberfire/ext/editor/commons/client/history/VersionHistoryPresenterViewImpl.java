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

package org.uberfire.ext.editor.commons.client.history;

import java.util.Date;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.AsyncDataProvider;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;
import org.uberfire.java.nio.base.version.VersionRecord;

public class VersionHistoryPresenterViewImpl
        extends Composite
        implements VersionHistoryPresenterView {

    private PagedTable table = new PagedTable( 5 );

    private Presenter presenter;
    private String version;

    public VersionHistoryPresenterViewImpl() {
        initWidget( table );
        table.getElement().setAttribute( "data-uf-lock", "false" );
        Column<VersionRecord, String> column = new Column<VersionRecord, String>( new ButtonCell() ) {

            @Override
            public String getValue( VersionRecord object ) {
                if ( version.equals( object.id() ) ) {
                    return CommonConstants.INSTANCE.Current();
                } else {
                    return CommonConstants.INSTANCE.Select();
                }
            }
        };
        table.addColumn( column, "" );
        column.setFieldUpdater( new FieldUpdater<VersionRecord, String>() {
            @Override
            public void update( int index,
                                VersionRecord record,
                                String value ) {
                presenter.onSelect( record );
            }
        } );
        table.addColumn( new Column<VersionRecord, Date>( new DateCell() ) {

            @Override
            public Date getValue( VersionRecord object ) {
                return object.date();
            }
        }, CommonConstants.INSTANCE.Date() );
        table.addColumn( new Column<VersionRecord, String>( new TextCell() ) {

            @Override
            public String getValue( VersionRecord object ) {
                return object.comment();
            }
        }, CommonConstants.INSTANCE.CommitMessage() );
        table.addColumn( new Column<VersionRecord, String>( new TextCell() ) {

            @Override
            public String getValue( VersionRecord object ) {
                return object.author();
            }
        }, CommonConstants.INSTANCE.Author() );

    }

    @Override
    public void setup( String version,
                       AsyncDataProvider<VersionRecord> dataProvider ) {
        this.version = version;
        if ( !dataProvider.getDataDisplays().contains( table ) ) {
            dataProvider.addDataDisplay( table );
        }
    }

    @Override
    public void refreshGrid() {
        table.refresh();
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void showLoading() {
        BusyPopup.showMessage( CommonConstants.INSTANCE.Loading() );
    }
}
