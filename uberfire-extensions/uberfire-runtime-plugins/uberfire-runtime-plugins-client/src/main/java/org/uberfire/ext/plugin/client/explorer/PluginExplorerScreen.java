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

import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
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
import org.uberfire.ext.plugin.client.security.PluginController;
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

@Dependent
@WorkbenchScreen(identifier = "Plugins Explorer")
public class PluginExplorerScreen
        extends Composite {

    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);
    @UiField
    FlowPanel htmlPanel;
    private CommonConstants constants = CommonConstants.INSTANCE;
    @Inject
    private NewPluginPopUp newPluginPopUp;
    @Inject
    private PluginNavList pluginNavList;
    @Inject
    private Caller<PluginServices> pluginServices;
    @Inject
    private PluginController pluginController;

    @PostConstruct
    public void init() {
        initWidget(uiBinder.createAndBindUi(this));
        pluginServices.call(new RemoteCallback<Collection<Plugin>>() {
            @Override
            public void callback(final Collection<Plugin> plugins) {
                pluginNavList.setup(plugins);
            }
        }).listPlugins();
        htmlPanel.add(pluginNavList);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.PluginsExplorer();
    }

    @WorkbenchMenu
    public void buildMenu(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(MenuFactory
                                     .newTopLevelCustomMenu(new MenuFactory.CustomMenuBuilder() {
                                         @Override
                                         public void push(MenuFactory.CustomMenuBuilder element) {
                                         }

                                         @Override
                                         public MenuItem build() {
                                             return new BaseMenuCustom<IsWidget>() {
                                                 @Override
                                                 public void accept(MenuVisitor visitor) {
                                                     visitor.visit(this);
                                                 }

                                                 @Override
                                                 public IsWidget build() {
                                                     return getNewButton();
                                                 }
                                             };
                                         }
                                     }).endMenu().build());
    }

    public IsWidget getNewButton() {
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.addStyleName("pull-right");
        buttonGroup.add(new Button() {{
            setSize(ButtonSize.SMALL);
            setDataToggle(Toggle.DROPDOWN);
        }});
        DropDownMenu dropDownMenu = new DropDownMenu();
        addNewAnchorLink(dropDownMenu,
                         CommonConstants.INSTANCE.NewPerspective(),
                         PluginType.PERSPECTIVE_LAYOUT,
                         pluginController.canCreatePerspectives());
        addNewAnchorLink(dropDownMenu,
                         CommonConstants.INSTANCE.NewScreen(),
                         PluginType.SCREEN,
                         true);
        addNewAnchorLink(dropDownMenu,
                         CommonConstants.INSTANCE.NewEditor(),
                         PluginType.EDITOR,
                         true);
        addNewAnchorLink(dropDownMenu,
                         CommonConstants.INSTANCE.NewSplashScreen(),
                         PluginType.SPLASH,
                         true);
        addNewAnchorLink(dropDownMenu,
                         CommonConstants.INSTANCE.NewDynamicMenu(),
                         PluginType.DYNAMIC_MENU,
                         true);
        buttonGroup.add(dropDownMenu);
        return buttonGroup;
    }

    private void addNewAnchorLink(DropDownMenu dropDownMenu,
                                  String text,
                                  PluginType pluginType,
                                  boolean available) {
        if (available) {
            AnchorListItem anchor = new AnchorListItem(text);
            anchor.addClickHandler(event -> newPluginPopUp.show(pluginType));
            dropDownMenu.add(anchor);
        }
    }

    interface ViewBinder
            extends
            UiBinder<Widget, PluginExplorerScreen> {

    }
}