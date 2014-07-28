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
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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
        setStatus( response.getScannerStatus() );
        groupId.setText( response.getReleasedId().getGroupId() );
        artifactId.setText( response.getReleasedId().getArtifactId() );
        version.setText( response.getReleasedId().getVersion() );
        if ( response.getResolvedReleasedId() != null ) {
            resolvedGroupId.setText( response.getResolvedReleasedId().getGroupId() );
            resolvedArtifactId.setText( response.getResolvedReleasedId().getArtifactId() );
            resolvedVersion.setText( response.getResolvedReleasedId().getVersion() );
        }
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

        setupStatus( this.status, status );
    }

    @Override
    public void setStatus( final ScannerStatus scannerStatus ) {
        if ( scannerStatus == null ||
                scannerStatus.equals( ScannerStatus.ERROR ) ||
                scannerStatus.equals( ScannerStatus.UNKNOWN ) ) {
            startScanner.setEnabled( false );
            stopScanner.setEnabled( false );
            scanNow.setEnabled( false );
        } else if ( scannerStatus.equals( ScannerStatus.CREATED ) ||
                scannerStatus.equals( ScannerStatus.STARTED ) ) {
            startScanner.setEnabled( true );
            stopScanner.setEnabled( true );
            startScanner.setActive( true );
            stopScanner.setActive( false );
            scanNow.setEnabled( false );
        } else {
            startScanner.setEnabled( true );
            stopScanner.setEnabled( true );
            stopScanner.setActive( true );
            startScanner.setActive( false );
            scanNow.setEnabled( true );
        }
    }

    @Override
    public void cleanup() {
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

        if ( interval.getText().trim().isEmpty() ) {
            intervalGroup.setType( ControlGroupType.ERROR );
            startScanner.removeStyleName( "active" );
            stopScanner.setActive( true );
            return;
        }

        final long value;
        try {
            value = Long.valueOf( interval.getText() );
        } catch ( Exception ex ) {
            intervalGroup.setType( ControlGroupType.ERROR );
            startScanner.removeStyleName( "active" );
            stopScanner.setActive( true );
            return;
        }

        presenter.startScanner( value );
    }

    @UiHandler("stopScanner")
    public void stopScanner( final ClickEvent e ) {
        presenter.stopScanner();
    }

    @UiHandler("scanNow")
    public void scanNow( final ClickEvent e ) {
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
