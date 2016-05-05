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

package org.uberfire.ext.plugin.client.widget.popup;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.validation.ValidationErrorReason;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.plugin.client.validation.NameValidator;
import org.uberfire.ext.plugin.client.validation.PluginNameValidator;
import org.uberfire.ext.plugin.client.validation.RuleValidator;
import org.uberfire.ext.plugin.exception.PluginAlreadyExists;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

@ApplicationScoped
public class NewPluginPopUp implements NewPluginPopUpView.Presenter {

    private NewPluginPopUpView view;

    @Inject
    Caller<PluginServices> pluginServices;

    @Inject
    PlaceManager placeManager;

    @Inject
    PluginNameValidator pluginNameValidator;

    // For proxying
    protected NewPluginPopUp() {}

    @Inject
    public NewPluginPopUp( final NewPluginPopUpView view ) {
        this.view = view;
        this.view.init( this );
    }

    @Override
    public RuleValidator getNameValidator() {
        return NameValidator.createNameValidator( view.emptyName(), view.invalidName() );
    }

    @Override
    public void onOK( final String name,
                      final PluginType type ) {

        pluginNameValidator.validate( name + ".plugin", new ValidatorWithReasonCallback() {
            @Override
            public void onFailure( final String reason ) {
                if ( ValidationErrorReason.EMPTY_NAME.name().equals( reason ) ) {
                    view.handleNameValidationError( view.emptyName() );
                } else if ( ValidationErrorReason.DUPLICATED_NAME.name().equals( reason ) ) {
                    view.handleNameValidationError( view.duplicatedName() );
                } else {
                    view.handleNameValidationError( view.invalidName() );
                }
            }

            @Override
            public void onSuccess() {
                pluginServices.call( new RemoteCallback<Plugin>() {
                    @Override
                    public void callback( final Plugin response ) {
                        placeManager.goTo( getPathPlaceRequest( response ) );
                        hide();
                    }
                }, new ErrorCallback<Object>() {
                    @Override
                    public boolean error( final Object message,
                                          final Throwable throwable ) {
                        if ( throwable instanceof PluginAlreadyExists ) {
                            view.handleNameValidationError( view.duplicatedName() );
                        } else {
                            view.handleNameValidationError( view.invalidName() );
                        }
                        return false;
                    }
                } ).createNewPlugin( name, type );
            }

            @Override
            public void onFailure() {
                view.handleNameValidationError( view.invalidName() );
            }
        } );
    }

    protected PlaceRequest getPathPlaceRequest( Plugin response ) {
        return new PathPlaceRequest( response.getPath() ).addParameter( "name", response.getName() );
    }

    @Override
    public void onCancel() {
        hide();
    }

    public void show( final PluginType type ) {
        view.show( type );
    }

    private void hide() {
        view.hide();
    }
}