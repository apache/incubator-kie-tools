/*
 * Copyright 2013 JBoss Inc
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
package org.kie.workbench.common.screens.server.management.client.container;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.common.services.project.model.GAV;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.client.util.NumericTextBox;
import org.kie.workbench.common.screens.server.management.client.util.ReadOnlyTextBox;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ServerInstanceRef;

import static org.kie.workbench.common.screens.server.management.client.util.ContainerStatusUtil.*;

@Dependent
public class ContainerInfoView
        extends Composite
        implements ContainerInfoPresenter.View {

    interface Binder
            extends
            UiBinder<Widget, ContainerInfoView> {

    }

    @UiField
    Icon status;

    @UiField
    FormGroup intervalGroup;

    @UiField
    NumericTextBox interval;

    @UiField
    FormGroup groupIdGroup;

    @UiField
    ReadOnlyTextBox groupId;

    @UiField
    FormGroup artifactIdGroup;

    @UiField
    ReadOnlyTextBox artifactId;

    @UiField
    FormGroup versionGroup;

    @UiField
    TextBox version;

    @UiField
    FormGroup resolvedGroupIdGroup;

    @UiField
    ReadOnlyTextBox resolvedGroupId;

    @UiField
    FormGroup resolvedArtifactIdGroup;

    @UiField
    ReadOnlyTextBox resolvedArtifactId;

    @UiField
    FormGroup resolvedVersionGroup;

    @UiField
    ReadOnlyTextBox resolvedVersion;

    @UiField( provided = true )
    CellTable<ServerInstanceRef> endpointTable = new CellTable<ServerInstanceRef>();

    private ListDataProvider<ServerInstanceRef> endpointDataProvider = new ListDataProvider<ServerInstanceRef>();

    @UiField
    Button startScanner;

    @UiField
    Button stopScanner;

    @UiField
    Button scanNow;

    @UiField
    Button upgrade;

    private org.uberfire.mvp.Command stopScannerActive = new org.uberfire.mvp.Command() {
        @Override
        public void execute() {
            Scheduler.get().scheduleDeferred( new Command() {
                @Override
                public void execute() {
                    stopScanner.setActive( true );
                    startScanner.setActive( false );
                }
            } );
        }
    };

    private org.uberfire.mvp.Command startScannerActive = new org.uberfire.mvp.Command() {
        @Override
        public void execute() {
            Scheduler.get().scheduleDeferred( new Command() {
                @Override
                public void execute() {
                    startScanner.setActive( true );
                    stopScanner.setActive( false );
                }
            } );
        }
    };

    private ContainerInfoPresenter presenter;

    private static Binder uiBinder = GWT.create( Binder.class );

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );

        version.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( !version.getText().trim().isEmpty() ) {
                    versionGroup.setValidationState( ValidationState.NONE );
                }
            }
        } );

        interval.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( !interval.getText().trim().isEmpty() ) {
                    intervalGroup.setValidationState( ValidationState.NONE );
                }
            }
        } );
        configureEndpointTable();
    }

    @Override
    public void init( ContainerInfoPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setStatus( final ContainerStatus status ) {
        setupStatus( this.status, status );
    }

    @Override
    public void setInterval( final String pollInterval ) {
        this.intervalGroup.setValidationState( ValidationState.NONE );
        this.interval.setText( pollInterval );
    }

    @Override
    public void setGroupId( final String groupId ) {
        this.groupId.setText( groupId );
    }

    @Override
    public void setArtifactId( final String artifactId ) {
        this.artifactId.setText( artifactId );
    }

    @Override
    public void setVersion( final String version ) {
        this.version.setText( version );
    }

    @Override
    public void setResolvedGroupId( final String resolvedGroupId ) {
        this.resolvedGroupId.setText( resolvedGroupId );
    }

    @Override
    public void setResolvedArtifactId( final String resolvedArtifactId ) {
        this.resolvedArtifactId.setText( resolvedArtifactId );
    }

    @Override
    public void setResolvedVersion( final String resolvedVersion ) {
        this.resolvedVersion.setText( resolvedVersion );
    }

    @Override
    public void setEndpoint( final List<ServerInstanceRef> endpoint ) {
        this.endpointDataProvider.setList( endpoint );
    }

    @Override
    public void setStartScannerState( final ContainerInfoPresenter.State state ) {
        this.startScanner.setEnabled( state.equals( ContainerInfoPresenter.State.ENABLED ) );
    }

    @Override
    public void setStopScannerState( final ContainerInfoPresenter.State state ) {
        this.stopScanner.setEnabled( state.equals( ContainerInfoPresenter.State.ENABLED ) );
    }

    @Override
    public void setScanNowState( final ContainerInfoPresenter.State state ) {
        this.scanNow.setEnabled( state.equals( ContainerInfoPresenter.State.ENABLED ) );
    }

    @Override
    public void setUpgradeState( final ContainerInfoPresenter.State state ) {
        this.upgrade.setEnabled( state.equals( ContainerInfoPresenter.State.ENABLED ) );
    }

    @Override
    public IsWidget getCustomMenuItem( final org.uberfire.mvp.Command onClick ) {
        return new Button() {
            {
                setIcon( IconType.REMOVE );
                setTitle( Constants.INSTANCE.remove() );
                setSize( ButtonSize.SMALL );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onClick.execute();
                    }
                } );
            }
        };
    }

    @Override
    public void cleanup() {
        intervalGroup.setValidationState( ValidationState.NONE );
        interval.setText( "" );
        startScanner.setEnabled( false );
        stopScanner.setEnabled( false );
        scanNow.setEnabled( false );
        groupId.setText( "" );
        artifactId.setText( "" );
        version.setText( "" );
        resolvedGroupId.setText( "" );
        resolvedArtifactId.setText( "" );
        resolvedVersion.setText( "" );
        endpointDataProvider.getList().clear();
        groupIdGroup.setValidationState( ValidationState.NONE );
        artifactIdGroup.setValidationState( ValidationState.NONE );
        versionGroup.setValidationState( ValidationState.NONE );
    }

    @UiHandler( "startScanner" )
    public void startScanner( final ClickEvent e ) {
        if ( startScanner.isActive() ) {
            return;
        }

        try {
            presenter.startScanner( interval.getText() );
        } catch ( final IllegalArgumentException ex ) {
            intervalGroup.setValidationState( ValidationState.ERROR );
            stopScannerActive.execute();
        }
    }

    @UiHandler( "stopScanner" )
    public void stopScanner( final ClickEvent e ) {
        if ( stopScanner.isActive() ) {
            return;
        }
        stopScannerActive.execute();
        presenter.stopScanner();
    }

    @UiHandler( "scanNow" )
    public void scanNow( final ClickEvent e ) {
        stopScannerActive.execute();
        presenter.scanNow();
    }

    @UiHandler( "upgrade" )
    public void upgrade( final ClickEvent e ) {
        try {
            presenter.upgrade( new GAV( groupId.getText(), artifactId.getText(), version.getText() ) );
        } catch ( final IllegalArgumentException ex ) {
            versionGroup.setValidationState( ValidationState.ERROR );
        }
    }

    private void configureEndpointTable() {
        //Setup table
        endpointTable.setStriped( true );
        endpointTable.setCondensed( true );
        endpointTable.setBordered( true );
        endpointTable.setEmptyTableWidget( new Label( Constants.INSTANCE.no_data_defined() ) );

        //Columns
        final Column<ServerInstanceRef, String> urlColumn = new Column<ServerInstanceRef, String>( new TextCell() ) {

            @Override
            public String getValue( final ServerInstanceRef item ) {
                return item.getUrl();
            }
        };

        final Column<ServerInstanceRef, String> statusColumn = new Column<ServerInstanceRef, String>( new TextCell() ) {

            @Override
            public String getValue( final ServerInstanceRef item ) {
                return item.getStatus();
            }
        };

        endpointTable.addColumn( urlColumn,
                new TextHeader( Constants.INSTANCE.endpoint() ) );
        endpointTable.addColumn( statusColumn,
                new TextHeader( Constants.INSTANCE.status() ) );

        //Link data
        endpointDataProvider.addDataDisplay( endpointTable );
    }
}
