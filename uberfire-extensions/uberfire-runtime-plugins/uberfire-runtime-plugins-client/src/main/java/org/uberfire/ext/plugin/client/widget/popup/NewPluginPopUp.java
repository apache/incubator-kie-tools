/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.ext.plugin.client.widget.popup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.plugin.exception.PluginAlreadyExists;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.uberfire.mvp.impl.PathPlaceRequest;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@ApplicationScoped
public class NewPluginPopUp extends BaseModal {

    private PluginType type;

    interface ViewBinder
            extends
            UiBinder<Widget, NewPluginPopUp> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            onOKButtonClick();
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( okCommand,
                                                                                      cancelCommand );

    @UiField
    TextBox name;

    @UiField
    HelpBlock nameHelpInline;

    @UiField
    FormGroup nameGroup;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private PlaceManager placeManager;

    @PostConstruct
    public void init() {
        footer.enableOkButton( true );

        add( new ModalBody() {{
            add( uiBinder.createAndBindUi( NewPluginPopUp.this ) );
        }} );
        add( footer );
    }

    public void show( final PluginType type ) {
        this.type = checkNotNull( "type", type );

        name.setText( "" );
        nameHelpInline.setText( "" );
        nameGroup.setValidationState( ValidationState.NONE );

        switch ( this.type ) {
            case PERSPECTIVE:
                setTitle( "New Perspective Plugin..." );
                break;
            case PERSPECTIVE_LAYOUT:
                setTitle( "New Perspective Layout Plugin..." );
                break;
            case SCREEN:
                setTitle( "New Screen Plugin..." );
                break;
            case EDITOR:
                setTitle( "New Editor Plugin..." );
                break;
            case SPLASH:
                setTitle( "New Splash Plugin..." );
                break;
            case DYNAMIC_MENU:
                setTitle( "New Dynamic Menu..." );
                break;
        }
        super.show();
    }

    private void onOKButtonClick() {
        if ( name.getText().trim().isEmpty() ) {
            nameHelpInline.setText( "Name is mandatory." );
            nameGroup.setValidationState( ValidationState.ERROR );
            return;
        }

        pluginServices.call( new RemoteCallback<Plugin>() {
            @Override
            public void callback( final Plugin response ) {
                placeManager.goTo( new PathPlaceRequest( response.getPath() ).addParameter( "name", response.getName() ) );
                hide();
            }
        }, new ErrorCallback<Object>() {
            @Override
            public boolean error( final Object message,
                                  final Throwable throwable ) {
                nameGroup.setValidationState( ValidationState.ERROR );
                if ( throwable instanceof PluginAlreadyExists ) {
                    nameHelpInline.setText( "Plugin name already exists." );
                } else {
                    nameHelpInline.setText( "Invalid plugin name." );
                }
                return false;
            }
        } ).createNewPlugin( name.getText(), type );
    }
}