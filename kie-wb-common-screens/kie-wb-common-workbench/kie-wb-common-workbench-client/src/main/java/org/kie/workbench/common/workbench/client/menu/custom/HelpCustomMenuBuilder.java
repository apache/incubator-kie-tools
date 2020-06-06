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

package org.kie.workbench.common.workbench.client.menu.custom;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.common.client.api.IsElement;
import  org.kie.workbench.common.services.shared.resources.PerspectiveIds;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.IconMenuItemPresenter;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@ApplicationScoped
public class HelpCustomMenuBuilder implements MenuFactory.CustomMenuBuilder {

    private IconMenuItemPresenter menuItem;

    public HelpCustomMenuBuilder() {
    }

    @Inject
    public HelpCustomMenuBuilder(final IconMenuItemPresenter menuItem,
                                 final PlaceManager placeManager) {
        this.menuItem = menuItem;

        menuItem.setup("fa fa-question",
                       DefaultWorkbenchConstants.INSTANCE.Help(),
                       () -> placeManager.goTo(PerspectiveIds.ADMIN));
    }

    @Override
    public void push(MenuFactory.CustomMenuBuilder element) {
        GWT.log("HelpCustomMenuBuilder#push does nothing.");
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsElement>() {
            @Override
            public IsElement build() {
                return menuItem.getView();
            }

            @Override
            public MenuPosition getPosition() {
                return MenuPosition.RIGHT;
            }
        };
    }
}
