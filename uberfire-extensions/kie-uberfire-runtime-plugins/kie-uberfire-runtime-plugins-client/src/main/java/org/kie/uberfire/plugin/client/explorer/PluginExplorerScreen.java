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

package org.kie.uberfire.plugin.client.explorer;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.plugin.client.widget.navigator.PluginNavList;
import org.kie.uberfire.plugin.model.Plugin;
import org.kie.uberfire.plugin.model.PluginType;
import org.kie.uberfire.plugin.service.PluginServices;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.*;

@Dependent
@WorkbenchScreen(identifier = "Plugins Explorer")
public class PluginExplorerScreen
        extends Composite {

    interface ViewBinder
            extends
            UiBinder<Widget, PluginExplorerScreen> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    FlowPanel htmlPanel;

    @Inject
    private PluginNavList pluginNavList;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private PlaceManager placeManager;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        pluginServices.call( new RemoteCallback<Collection<Plugin>>() {
            @Override
            public void callback( final Collection<Plugin> response ) {
                pluginNavList.setup( response );
            }
        } ).listPlugins();
        htmlPanel.add( pluginNavList );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Plugins Explorer";
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                return getNewButton();
                            }
                        };
                    }
                } ).endMenu().build();
    }

    public IsWidget getNewButton() {
        return new DropdownButton( "New..." ) {{
            setSize( MINI );
            setRightDropdown( true );
            add( new NavLink( "New Perspective" ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        placeManager.goTo( new DefaultPlaceRequest( "NewPluginPopUp" ).addParameter( "type", PluginType.PERSPECTIVE.toString() ) );
                    }
                } );
            }} );

            add( new NavLink( "New Screen" ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        placeManager.goTo( new DefaultPlaceRequest( "NewPluginPopUp" ).addParameter( "type", PluginType.SCREEN.toString() ) );
                    }
                } );
            }} );

            add( new NavLink( "New Editor" ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        placeManager.goTo( new DefaultPlaceRequest( "NewPluginPopUp" ).addParameter( "type", PluginType.EDITOR.toString() ) );
                    }
                } );
            }} );

            add( new NavLink( "New SplashScreen" ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        placeManager.goTo( new DefaultPlaceRequest( "NewPluginPopUp" ).addParameter( "type", PluginType.SPLASH.toString() ) );
                    }
                } );
            }} );

            add( new NavLink( "New Dynamic Menu" ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        placeManager.goTo( new DefaultPlaceRequest( "NewPluginPopUp" ).addParameter( "type", PluginType.DYNAMIC_MENU.toString() ) );
                    }
                } );
            }} );
        }};
    }

}