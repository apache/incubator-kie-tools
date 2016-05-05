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
package org.uberfire.ext.wires.bpmn.client.explorer;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.wires.bpmn.client.resources.i18n.BpmnEditorConstants;

@Dependent
public class BpmnExplorerViewImpl extends Composite implements BpmnExplorerView {

    interface ViewBinder
            extends
            UiBinder<Widget, BpmnExplorerViewImpl> {

    }

    final private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField(provided = true)
    CellTable<Path> table = new CellTable<Path>();

    private ListDataProvider<Path> dataProvider = new ListDataProvider<Path>();

    private BpmnExplorerPresenter presenter;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final BpmnExplorerPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setContent( final List<Path> files ) {
        table.setStriped( true );
        table.setCondensed( true );
        table.setBordered( true );
        table.setEmptyTableWidget( new Label( BpmnEditorConstants.INSTANCE.bpmnExplorerNoFilesFound() ) );

        //Columns
        final TextColumn<Path> urlColumn = new TextColumn<Path>() {

            @Override
            public String getValue( final Path file ) {
                return file.toURI();
            }
        };

        final ButtonCell openButton = new ButtonCell( IconType.EDIT, ButtonType.PRIMARY, ButtonSize.SMALL );
        final Column<Path, String> openColumn = new Column<Path, String>( openButton ) {
            @Override
            public String getValue( final Path global ) {
                return BpmnEditorConstants.INSTANCE.bpmnExplorerNoFilesOpen();
            }
        };
        openColumn.setFieldUpdater( new FieldUpdater<Path, String>() {
            public void update( final int index,
                                final Path file,
                                final String value ) {
                presenter.openFile( file );
            }
        } );

        table.addColumn( urlColumn,
                         new TextHeader( BpmnEditorConstants.INSTANCE.bpmnExplorerFileUrl() ) );
        table.addColumn( openColumn );

        //Link data
        dataProvider.addDataDisplay( table );
        dataProvider.setList( files );
    }
}