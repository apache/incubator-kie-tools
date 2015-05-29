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

import javax.enterprise.context.Dependent;

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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class NewContainerFormView extends Composite implements NewContainerFormPresenter.View {

    interface Binder
            extends
            UiBinder<Widget, NewContainerFormView> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

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

    private NewContainerFormPresenter presenter;

    public NewContainerFormView() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final NewContainerFormPresenter presenter ) {
        this.presenter = presenter;

        content.add( new Column( 12, presenter.getDependencyListWidgetPresenter().getView().asWidget() ) );

        containerName.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                presenter.setContainerName( containerName.getText() );
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

    @Override
    public void setGroupId( final String value ) {
        groupId.setText( value );
        if ( !groupId.getText().trim().isEmpty() ) {
            groupIdGroup.setType( ControlGroupType.NONE );
        }
    }

    @Override
    public void setAtifactId( final String value ) {
        artifactId.setText( value );
        if ( !artifactId.getText().trim().isEmpty() ) {
            artifactIdGroup.setType( ControlGroupType.NONE );
        }
    }

    @Override
    public void setVersion( final String value ) {
        version.setText( value );
        if ( !version.getText().trim().isEmpty() ) {
            versionGroup.setType( ControlGroupType.NONE );
        }
    }

    @Override
    public void setEndpoint( final String value ) {
        endpoint.setText( value );
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

        presenter.createContainer( containerName.getText(), groupId.getText(), artifactId.getText(), version.getText() );
        presenter.close();
    }
}
