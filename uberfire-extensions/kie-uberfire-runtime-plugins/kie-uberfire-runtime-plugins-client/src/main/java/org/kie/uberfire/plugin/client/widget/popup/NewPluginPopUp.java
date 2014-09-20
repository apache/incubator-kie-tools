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

package org.kie.uberfire.plugin.client.widget.popup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.plugin.exception.PluginAlreadyExists;
import org.kie.uberfire.plugin.model.Plugin;
import org.kie.uberfire.plugin.model.PluginType;
import org.kie.uberfire.plugin.service.PluginServices;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import static org.kie.uberfire.plugin.model.PluginType.*;


@Dependent
@WorkbenchPopup(identifier = "NewPluginPopUp")
public class NewPluginPopUp extends Composite {

    private PluginType type;
    private String title;
    private PlaceRequest place;

    interface ViewBinder
            extends
            UiBinder<Widget, NewPluginPopUp> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    TextBox name;

    @UiField
    HelpInline nameHelpInline;

    @UiField
    ControlGroup nameGroup;

    @UiField
    Button okButton;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private PlaceManager placeManager;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        okButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                if ( name.getText().trim().isEmpty() ) {
                    nameHelpInline.setText( "Name is mandatory." );
                    nameGroup.setType( ControlGroupType.ERROR );
                    return;
                }

                pluginServices.call( new RemoteCallback<Plugin>() {
                    @Override
                    public void callback( final Plugin response ) {
                        placeManager.goTo( new PathPlaceRequest( response.getPath() ).addParameter( "name", response.getName() ) );
                        placeManager.forceClosePlace( place );
                    }
                }, new ErrorCallback<Object>() {
                    @Override
                    public boolean error( final Object message,
                                          final Throwable throwable ) {
                        nameGroup.setType( ControlGroupType.ERROR );
                        if ( throwable instanceof PluginAlreadyExists ) {
                            nameHelpInline.setText( "Plugin name already exists." );
                        } else {
                            nameHelpInline.setText( "Invalid plugin name." );
                        }
                        return false;
                    }
                } ).createNewPlugin( name.getText(), type );
            }
        } );
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        String pluginType = placeRequest.getParameter( "type", SCREEN.toString() );
        try {
            this.type = PluginType.valueOf( pluginType.toUpperCase() );
            if ( type == null ) {
                type = SCREEN;
            }
        } catch ( Exception ex ) {
            type = SCREEN;
        }

        switch ( type ) {
            case PERSPECTIVE:
                title = "New Perspective Plugin...";
                break;
            case SCREEN:
                title = "New Screen Plugin...";
                break;
            case EDITOR:
                title = "New Editor Plugin...";
                break;
            case SPLASH:
                title = "New Splash Plugin...";
                break;
            case DYNAMIC_MENU:
                title = "New Dynamic Menu...";
                break;
        }
        this.place = placeRequest;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return title;
    }

}