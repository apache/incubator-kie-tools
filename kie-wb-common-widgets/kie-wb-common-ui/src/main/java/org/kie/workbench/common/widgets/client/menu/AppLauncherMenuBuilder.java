/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.menu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherPresenter;
import org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherUpdatedEvent;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@ApplicationScoped
public class AppLauncherMenuBuilder implements MenuFactory.CustomMenuBuilder {

    private final AnchorListItem link = GWT.create(AnchorListItem.class);

    @Inject
    private AppLauncherPresenter appLauncher;

    @PostConstruct
    public void init() {
        link.setTitle(CommonConstants.INSTANCE.AppsLauncher());
        link.setIcon(IconType.TH);
        link.addStyleName("dropdown");
        link.setDataToggle(Toggle.DROPDOWN);
        link.setVisible(false);
        link.add(appLauncher.getView());
    }

    @Override
    public void push(MenuFactory.CustomMenuBuilder element) {
        //Do nothing
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public IsWidget build() {
                return link;
            }

            @Override
            public MenuPosition getPosition() {
                return MenuPosition.RIGHT;
            }
        };
    }

    public void onAppLauncherUpdatedEvent(@Observes AppLauncherUpdatedEvent event) {
        link.setVisible(appLauncher.isAppLauncherEmpty() == false);
    }

}