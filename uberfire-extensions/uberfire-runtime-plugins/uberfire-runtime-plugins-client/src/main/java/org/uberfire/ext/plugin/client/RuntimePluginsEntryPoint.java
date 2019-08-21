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

package org.uberfire.ext.plugin.client;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.StyleInjector;
import org.jboss.errai.bus.client.util.BusToolsCli;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.EnabledByProperty;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PopupActivity;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
import org.uberfire.ext.plugin.client.resources.WebAppResource;
import org.uberfire.ext.plugin.model.DynamicMenu;
import org.uberfire.ext.plugin.model.DynamicMenuItem;
import org.uberfire.ext.plugin.model.RuntimePlugin;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.menu.MenuFactory;

import static com.google.gwt.core.client.ScriptInjector.TOP_WINDOW;
import static org.uberfire.workbench.model.ActivityResourceType.EDITOR;
import static org.uberfire.workbench.model.ActivityResourceType.PERSPECTIVE;
import static org.uberfire.workbench.model.ActivityResourceType.POPUP;
import static org.uberfire.workbench.model.ActivityResourceType.SCREEN;

@EntryPoint
@Bundle("resources/i18n/Constants.properties")
@EnabledByProperty(value = "uberfire.plugin.mode.active", negated = true)
public class RuntimePluginsEntryPoint {

    @Inject
    private Workbench workbench;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private WorkbenchMenuBar menubar;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ActivityManager activityManager;

    @PostConstruct
    public void init() {
        if (!BusToolsCli.isRemoteCommunicationEnabled()) {
            return;
        }

        WebAppResource.INSTANCE.CSS().ensureInjected();

        workbench.addStartupBlocker(RuntimePluginsEntryPoint.class);
        pluginServices.call(new RemoteCallback<Collection<RuntimePlugin>>() {
            @Override
            public void callback(Collection<RuntimePlugin> response) {
                for (final RuntimePlugin plugin : response) {
                    ScriptInjector.fromString(plugin.getScript()).setWindow(TOP_WINDOW).inject();
                    StyleInjector.inject(plugin.getStyle(),
                                         true);
                }
                pluginServices.call(new RemoteCallback<Collection<DynamicMenu>>() {
                    @Override
                    public void callback(Collection<DynamicMenu> response) {
                        for (final DynamicMenu menu : response) {
                            if (!menu.getMenuItems().isEmpty()) {
                                MenuFactory.SubMenusBuilder<MenuFactory.SubMenuBuilder<MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder>>> dynamicMenu = MenuFactory.newTopLevelMenu(menu.getName()).orderAll(100).menus();
                                for (final DynamicMenuItem dynamicMenuItem : menu.getMenuItems()) {

                                    String activityId = dynamicMenuItem.getActivityId();
                                    ResourceType resourceType = getResourceType(activityId);

                                    dynamicMenu.menu(dynamicMenuItem.getMenuLabel())
                                            .withPermission(activityId,
                                                            resourceType)
                                            .respondsWith(() -> placeManager.goTo(activityId))
                                            .endMenu();
                                }
                                menubar.addMenus(dynamicMenu.endMenus().endMenu().build());
                            }
                        }
                        workbench.removeStartupBlocker(RuntimePluginsEntryPoint.class);
                    }
                }).listDynamicMenus();
            }
        }).listRuntimePlugins();
    }

    public ResourceType getResourceType(String activityId) {

        Activity activity = activityManager.getActivity(new DefaultPlaceRequest(activityId),
                                                        false);
        if (activity != null) {
            if (activity instanceof PerspectiveActivity) {
                return PERSPECTIVE;
            }
            if (activity instanceof WorkbenchScreenActivity) {
                return SCREEN;
            }
            if (activity instanceof WorkbenchEditorActivity) {
                return EDITOR;
            }
            if (activity instanceof SplashScreenActivity) {
                return EDITOR;
            }
            if (activity instanceof PopupActivity) {
                return POPUP;
            }
        }
        return ResourceType.UNKNOWN;
    }
}
