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
package org.kie.workbench.common.screens.server.management.client.artifact;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Row;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.service.ServerManagementService;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.mvp.ParameterizedCommand;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class NewContainerForm
        extends PopupPanel {

    interface Binder
            extends
            UiBinder<Widget, NewContainerForm> {

    }

    @Inject
    private DependencyListWidget dependencyListWidget;

    @Inject
    private Caller<M2RepoService> m2RepoService;

    @Inject
    private Caller<ServerManagementService> service;

    @UiField
    BaseModal popup;

    @UiField
    ControlGroup containerNameGroup;

    @UiField
    TextBox containerName;

    @UiField
    TextBox endpoint;

    @UiField
    ControlGroup groupIdGroup;

    @UiField
    TextBox groupId;

    @UiField
    ControlGroup artifactIdGroup;

    @UiField
    TextBox artifactId;

    @UiField
    ControlGroup versionGroup;

    @UiField
    TextBox version;

    @UiField
    Row content;

    private ServerRef serverRef;

    private static Binder uiBinder = GWT.create( Binder.class );

    @PostConstruct
    public void init() {
        setWidget( uiBinder.createAndBindUi( this ) );
        popup.setDynamicSafe( true );

        content.add( new Column( 12, dependencyListWidget ) );

        popup.setWidth( 640 );
        popup.setMaxHeigth( "560px" );

        dependencyListWidget.addOnSelect( new ParameterizedCommand<String>() {
            @Override
            public void execute( final String path ) {
                m2RepoService.call( new RemoteCallback<GAV>() {
                    @Override
                    public void callback( GAV gav ) {
                        groupIdGroup.setType( ControlGroupType.NONE );
                        artifactIdGroup.setType( ControlGroupType.NONE );
                        versionGroup.setType( ControlGroupType.NONE );

                        groupId.setText( gav.getGroupId() );
                        artifactId.setText( gav.getArtifactId() );
                        version.setText( gav.getVersion() );
                    }
                } ).loadGAVFromJar( path );
            }
        } );

        containerName.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                endpoint.setText( serverRef.getUrl() + "/containers/" + containerName.getText() );
                if ( !containerName.getText().trim().isEmpty() ) {
                    containerNameGroup.setType( ControlGroupType.NONE );
                }
            }
        } );

        groupId.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( !groupId.getText().trim().isEmpty() ) {
                    groupIdGroup.setType( ControlGroupType.NONE );
                }
            }
        } );

        artifactId.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( !artifactId.getText().trim().isEmpty() ) {
                    artifactIdGroup.setType( ControlGroupType.NONE );
                }
            }
        } );

        version.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( !version.getText().trim().isEmpty() ) {
                    versionGroup.setType( ControlGroupType.NONE );
                }
            }
        } );

    }

    @UiHandler("ok")
    void onAddDependency( final ClickEvent event ) {
        boolean hasError = false;
        if ( containerName.getText().trim().isEmpty() ) {
            containerNameGroup.setType( ControlGroupType.ERROR );
            hasError = true;
        }

        if ( groupId.getText().trim().isEmpty() ) {
            groupIdGroup.setType( ControlGroupType.ERROR );
            hasError = true;
        }

        if ( artifactId.getText().trim().isEmpty() ) {
            artifactIdGroup.setType( ControlGroupType.ERROR );
            hasError = true;
        }

        if ( version.getText().trim().isEmpty() ) {
            versionGroup.setType( ControlGroupType.ERROR );
            hasError = true;
        }

        if ( hasError ) {
            return;
        }

        service.call().createContainer( serverRef.getId(),
                                        containerName.getText(),
                                        new GAV( groupId.getText(), artifactId.getText(), version.getText() ) );

        hide();
    }

    private void cleanup() {
        containerName.setText( "" );
        endpoint.setText( "" );
        groupId.setText( "" );
        artifactId.setText( "" );
        version.setText( "" );
        containerNameGroup.setType( ControlGroupType.NONE );
        groupIdGroup.setType( ControlGroupType.NONE );
        artifactIdGroup.setType( ControlGroupType.NONE );
        versionGroup.setType( ControlGroupType.NONE );
        serverRef = null;
    }

    public void show( final ServerRef serverRef ) {
        cleanup();
        this.serverRef = checkNotNull( "serverRef", serverRef );
        endpoint.setText( serverRef.getId() + "/containers/" );
        popup.show();
    }

    public void hide() {
        cleanup();
        popup.hide();
    }

}
