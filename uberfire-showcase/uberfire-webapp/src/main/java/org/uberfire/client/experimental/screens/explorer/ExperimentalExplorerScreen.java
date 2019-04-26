/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.experimental.screens.explorer;

import java.util.function.Consumer;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.resources.i18n.Constants;
import org.uberfire.experimental.definition.annotations.ExperimentalFeature;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchScreen(identifier = ExperimentalExplorerScreen.ID)
@ExperimentalFeature(nameI18nKey = "experimental_asset_explorer", descriptionI18nKey = "experimental_asset_explorer_description")
public class ExperimentalExplorerScreen implements IsElement {

    public static final String ID = "Experimental Explorer";

    private static final String TITLE = "Explorer";

    private final ExperimentalExplorer explorer;

    @Inject
    public ExperimentalExplorerScreen(final ExperimentalExplorer explorer) {
        this.explorer = explorer;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return TITLE;
    }

    @WorkbenchPartView
    public IsElement getView() {
        return this;
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(MenuFactory.newTopLevelMenu(Constants.INSTANCE.experimental_asset_explorer_actionsAdd())
                                     .respondsWith(() -> explorer.createNew())
                                     .endMenu()
                                     .build());
    }

    @Override
    public HTMLElement getElement() {
        return explorer.getElement();
    }

    @OnOpen
    public void onOpen() {
        explorer.load();
    }
}
