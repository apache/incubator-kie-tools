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

package org.uberfire.ext.plugin.client.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.ext.plugin.client.widget.navigator.PluginNavList;
import org.uberfire.ext.plugin.client.widget.popup.NewPluginPopUp;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Collection;

@Dependent
@WorkbenchScreen( identifier = "Plugins Explorer" )
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
    private NewPluginPopUp newPluginPopUp;

    @Inject
    private PluginNavList pluginNavList;

    @Inject
    private Caller<PluginServices> pluginServices;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        pluginServices.call( new RemoteCallback<Collection<Plugin>>() {
            @Override
            public void callback( final Collection<Plugin> plugins ) {
                pluginNavList.setup( plugins );
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
                            public void accept( MenuVisitor visitor ) {
                                visitor.visit( this );
                            }

                            @Override
                            public IsWidget build() {
                                return getNewButton();
                            }
                        };
                    }
                } ).endMenu().build();
    }

    public IsWidget getNewButton() {
        return new ButtonGroup() {{
            addStyleName( "pull-right" );
            add( new Button() {{
                setSize( ButtonSize.SMALL );
                setDataToggle( Toggle.DROPDOWN );
            }} );
            add( new DropDownMenu() {{
                add( new AnchorListItem( CommonConstants.INSTANCE.NewPerspective() ) {{
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            newPluginPopUp.show( PluginType.PERSPECTIVE_LAYOUT );
                        }
                    } );
                }} );

                add( new AnchorListItem( CommonConstants.INSTANCE.NewScreen() ) {{
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            newPluginPopUp.show( PluginType.SCREEN );
                        }
                    } );
                }} );

                add( new AnchorListItem( CommonConstants.INSTANCE.NewEditor() ) {{
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            newPluginPopUp.show( PluginType.EDITOR );
                        }
                    } );
                }} );

                add( new AnchorListItem( CommonConstants.INSTANCE.NewSplashScreen() ) {{
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            newPluginPopUp.show( PluginType.SPLASH );
                        }
                    } );
                }} );

                add( new AnchorListItem( CommonConstants.INSTANCE.NewDynamicMenu() ) {{
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            newPluginPopUp.show( PluginType.DYNAMIC_MENU );
                        }
                    } );
                }} );
            }} );
        }};
    }
}