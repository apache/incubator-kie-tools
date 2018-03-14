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

package org.uberfire.client.views.pfly.multiscreen;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.KebabMenu;
import org.uberfire.client.views.pfly.widgets.KebabMenuItem;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;

@Dependent
public class MultiScreenMenuBuilder implements Function<MenuItem, Optional<HTMLElement>> {

    @Inject
    private AuthorizationManager authManager;

    @Inject
    private User identity;

    @Inject
    private ManagedInstance<KebabMenu> kebabMenus;

    @Inject
    private ManagedInstance<Button> buttons;

    @Inject
    private ManagedInstance<KebabMenuItem> kebabMenuItems;

    @Inject
    private HTMLDocument document;

    @Override
    public Optional<HTMLElement> apply(final MenuItem menuItem) {
        return Optional.ofNullable(makeItem(menuItem,
                                            true));
    }

    protected HTMLElement makeItem(final MenuItem item,
                                   boolean isRoot) {
        if (authManager.authorize(item,
                                  identity) == false) {
            return null;
        }

        if (item instanceof MenuItemCommand) {
            final MenuItemCommand cmdItem = (MenuItemCommand) item;
            return isRoot ? new RootMenuItemCommandMapper().apply(cmdItem) : new MenuItemCommandMapper().apply(cmdItem);
        }

        if (item instanceof MenuGroup) {
            final MenuGroup groups = (MenuGroup) item;
            final List<HTMLElement> subMenus = groups.getItems().stream().map(i -> makeItem(i,
                                                                                            false)).collect(Collectors.toList());
            return isRoot ? new RootMenuGroupMapper().apply(groups,
                                                            subMenus) : new MenuGroupMapper().apply(groups,
                                                                                                    subMenus);
        }

        if (item instanceof MenuCustom) {
            return new MenuCustomMapper().apply((MenuCustom) item);
        }

        return null;
    }

    private class MenuCustomMapper implements Function<MenuCustom, HTMLElement> {

        @Override
        public HTMLElement apply(final MenuCustom menuCustom) {
            final Object result = menuCustom.build();
            if (result instanceof HTMLElement) {
                return (HTMLElement) result;
            }

            if (result instanceof IsElement) {
                return ((IsElement) result).getElement();
            }

            throw new RuntimeException("Unsupported custom menu type");
        }
    }

    private class RootMenuItemCommandMapper implements Function<MenuItemCommand, HTMLElement> {

        @Override
        public HTMLElement apply(final MenuItemCommand menuItem) {
            final Button button = buttons.get();
            button.setType(Button.ButtonType.BUTTON);
            button.setButtonStyleType(Button.ButtonStyleType.DEFAULT);
            button.setText(menuItem.getCaption());
            button.setEnabled(menuItem.isEnabled());
            button.setClickHandler(menuItem.getCommand());
            menuItem.addEnabledStateChangeListener(button::setEnabled);
            return button.getElement();
        }
    }

    private class MenuItemCommandMapper implements Function<MenuItemCommand, HTMLElement> {

        @Override
        public HTMLElement apply(final MenuItemCommand menuItem) {
            final KebabMenuItem item = kebabMenuItems.get();
            item.setText(menuItem.getCaption());
            item.setClickHandler(menuItem.getCommand());
            return item.getElement();
        }
    }

    private class RootMenuGroupMapper implements BiFunction<MenuGroup, List<HTMLElement>, HTMLElement> {

        @Override
        public HTMLElement apply(final MenuGroup menuItem,
                                 final List<HTMLElement> subMenus) {
            final KebabMenu menu = kebabMenus.get();
            menu.setItemsAlignment(KebabMenu.ItemsAlignment.RIGHT);
            subMenus.forEach(m -> {
                if (m instanceof HTMLLIElement) {
                    menu.addKebabItem((HTMLLIElement) m);
                }
            });
            return menu.getElement();
        }
    }

    private class MenuGroupMapper implements BiFunction<MenuGroup, List<HTMLElement>, HTMLElement> {

        @Override
        public HTMLElement apply(final MenuGroup menuItem,
                                 final List<HTMLElement> subMenus) {
            HTMLDivElement div = (HTMLDivElement) document.createElement("div");
            div.classList.add("form-group");
            subMenus.forEach(e -> div.appendChild(e));
            return div;
        }
    }
}
