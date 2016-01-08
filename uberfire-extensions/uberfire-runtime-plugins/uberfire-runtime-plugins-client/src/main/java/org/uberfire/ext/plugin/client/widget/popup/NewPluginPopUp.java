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

import java.util.Collection;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.plugin.client.info.PluginsInfo;
import org.uberfire.ext.plugin.client.validation.NameValidator;
import org.uberfire.ext.plugin.client.validation.RuleValidator;
import org.uberfire.ext.plugin.exception.PluginAlreadyExists;
import org.uberfire.ext.plugin.model.Activity;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.mvp.impl.PathPlaceRequest;

@ApplicationScoped
public class NewPluginPopUp implements NewPluginPopUpView.Presenter {

    private NewPluginPopUpView view;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private PluginsInfo pluginsInfo;

    @Inject
    public NewPluginPopUp( final NewPluginPopUpView view ) {
        this.view = view;
        this.view.init( this );
    }

    public RuleValidator getNameValidator() {
        return NameValidator.createNameValidator( view.emptyName(), view.invalidName() );
    }

    @Override
    public void onOK( final String name,
                      final PluginType type ) {

        pluginServices.call( new RemoteCallback<Collection<Plugin>>() {
            @Override
            public void callback( final Collection<Plugin> plugins ) {

                if ( validName( name, plugins ) ) {
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
                            if ( throwable instanceof PluginAlreadyExists ) {
                                view.handleNameValidationError( view.duplicatedName() );
                            } else {
                                view.handleNameValidationError( view.invalidName() );
                            }
                            return false;
                        }
                    } ).createNewPlugin( name, type );
                }
            }
        } ).listPlugins();
    }

    protected boolean validName( final String name,
                                 final Collection<Plugin> plugins ) {
        final RuleValidator nameValidator = getNameValidator();
        if ( !nameValidator.isValid( name ) ) {
            view.handleNameValidationError( nameValidator.getValidationError() );
            return false;
        }

        Set<Activity> activities = getPluginsInfo().getAllPlugins( plugins );

        for ( Activity activity : activities ) {
            if ( activity.getName().equalsIgnoreCase( name ) ) {
                view.handleNameValidationError( view.duplicatedName() );
                return false;
            }
        }

        return true;
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

    protected PluginsInfo getPluginsInfo() {
        return this.pluginsInfo;
    }
}