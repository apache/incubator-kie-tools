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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpBlock;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.GAV;
import org.kie.workbench.common.screens.server.management.client.util.NumericTextBox;
import org.kie.workbench.common.screens.server.management.client.util.ReadOnlyTextBox;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;

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
    Element status;

    @UiField
    ControlGroup intervalGroup;

    @UiField
    NumericTextBox interval;

    @UiField
    ControlGroup groupIdGroup;

    @UiField
    ReadOnlyTextBox groupId;

    @UiField
    ControlGroup artifactIdGroup;

    @UiField
    ReadOnlyTextBox artifactId;

    @UiField
    ControlGroup versionGroup;

    @UiField
    TextBox version;

    @UiField
    ControlGroup resolvedGroupIdGroup;

    @UiField
    ReadOnlyTextBox resolvedGroupId;

    @UiField
    ControlGroup resolvedArtifactIdGroup;

    @UiField
    ReadOnlyTextBox resolvedArtifactId;

    @UiField
    ControlGroup resolvedVersionGroup;

    @UiField
    ReadOnlyTextBox resolvedVersion;

    @UiField
    HelpBlock endpoint;

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
                    versionGroup.setType( ControlGroupType.NONE );
                }
            }
        } );

        interval.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( !interval.getText().trim().isEmpty() ) {
                    intervalGroup.setType( ControlGroupType.NONE );
                }
            }
        } );
    }

    @Override
    public void init( ContainerInfoPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setup( final Container response ) {
        setStatus( response.getStatus() );
        setScannerStatus( response.getScannerStatus() );
        intervalGroup.setType( ControlGroupType.NONE );
        if ( response.getPollInterval() != null ) {
            interval.setText( String.valueOf( response.getPollInterval().longValue() ) );
        }
        groupId.setText( response.getReleasedId().getGroupId() );
        artifactId.setText( response.getReleasedId().getArtifactId() );
        version.setText( response.getReleasedId().getVersion() );
        setResolvedReleasedId( response.getResolvedReleasedId() );
        endpoint.setText( response.getServerId() + "/containers/" + response.getId() );
    }

    @Override
    public void setStatus( final ContainerStatus status ) {
        setupStatus( this.status, status );
        if ( status.equals( ContainerStatus.STARTED ) ) {
            startScanner.setEnabled( true );
            stopScanner.setEnabled( true );
            scanNow.setEnabled( true );
            upgrade.setEnabled( true );
        } else {
            resolvedGroupId.setText( "" );
            resolvedArtifactId.setText( "" );
            resolvedVersion.setText( "" );
            startScanner.setEnabled( false );
            stopScanner.setEnabled( false );
            scanNow.setEnabled( false );
            upgrade.setEnabled( false );
        }
    }

    @Override
    public void setScannerStatus( final ScannerStatus scannerStatus ) {
        if ( scannerStatus == null ) {
            startScanner.setEnabled( false );
            stopScanner.setEnabled( false );
            scanNow.setEnabled( false );
            return;
        }

        if ( scannerStatus.equals( ScannerStatus.ERROR ) ||
                scannerStatus.equals( ScannerStatus.UNKNOWN ) ) {
            startScanner.setEnabled( true );
            stopScanner.setEnabled( true );
            scanNow.setEnabled( true );
            stopScannerActive.execute();
        } else {
            startScanner.setEnabled( true );
            stopScanner.setEnabled( true );
            if ( scannerStatus.equals( ScannerStatus.CREATED ) ||
                    scannerStatus.equals( ScannerStatus.STARTED ) ) {
                startScannerActive.execute();
                scanNow.setEnabled( false );
            } else {
                stopScannerActive.execute();
                scanNow.setEnabled( true );
            }
        }
    }

    @Override
    public void setResolvedReleasedId( final GAV resolvedReleasedId ) {
        if ( resolvedReleasedId != null ) {
            resolvedGroupId.setText( resolvedReleasedId.getGroupId() );
            resolvedArtifactId.setText( resolvedReleasedId.getArtifactId() );
            resolvedVersion.setText( resolvedReleasedId.getVersion() );
        }
    }

    @Override
    public void cleanup() {
        intervalGroup.setType( ControlGroupType.NONE );
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
        endpoint.setText( "" );
        groupIdGroup.setType( ControlGroupType.NONE );
        artifactIdGroup.setType( ControlGroupType.NONE );
        versionGroup.setType( ControlGroupType.NONE );
    }

    @UiHandler("startScanner")
    public void startScanner( final ClickEvent e ) {
        if ( startScanner.isActive() ) {
            return;
        }

        if ( interval.getText().trim().isEmpty() ) {
            intervalGroup.setType( ControlGroupType.ERROR );
            stopScannerActive.execute();
            return;
        }

        final long value;
        try {
            value = Long.valueOf( interval.getText() );
        } catch ( Exception ex ) {
            intervalGroup.setType( ControlGroupType.ERROR );
            stopScannerActive.execute();
            return;
        }

        presenter.startScanner( value );
    }

    @UiHandler("stopScanner")
    public void stopScanner( final ClickEvent e ) {
        if ( stopScanner.isActive() ) {
            return;
        }
        stopScannerActive.execute();
        presenter.stopScanner();
    }

    @UiHandler("scanNow")
    public void scanNow( final ClickEvent e ) {
        stopScannerActive.execute();
        presenter.scanNow();
    }

    @UiHandler("upgrade")
    public void upgrade( final ClickEvent e ) {
        if ( version.getText().trim().isEmpty() ) {
            versionGroup.setType( ControlGroupType.ERROR );
            return;
        }

        presenter.upgrade( new GAV( groupId.getText(), artifactId.getText(), version.getText() ) );
    }
}
