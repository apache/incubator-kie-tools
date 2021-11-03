/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.workbench.model.menu.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.ResourceActionRef;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuItemPlain;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.Menus;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 *
 */
public final class MenuBuilderImpl
        implements MenuFactory.MenuBuilder,
                   MenuFactory.ContributedMenuBuilder,
                   MenuFactory.TopLevelMenusBuilder,
                   MenuFactory.SubMenuBuilder,
                   MenuFactory.SubMenusBuilder,
                   MenuFactory.TerminalMenu,
                   MenuFactory.TerminalCustomMenu {

    final List<MenuItem> menuItems = new ArrayList<MenuItem>();
    final Stack<MenuFactory.CustomMenuBuilder> context = new Stack<MenuFactory.CustomMenuBuilder>();
    int order = 0;

    public MenuBuilderImpl(final MenuType menuType,
                           final String caption) {
        final CurrentContext currentContext = new CurrentContext();
        currentContext.caption = checkNotEmpty("caption",
                                               caption);
        currentContext.menuType = checkNotNull("menuType",
                                               menuType);
        context.push(currentContext);
    }

    public MenuBuilderImpl(final MenuType menuType,
                           final MenuFactory.CustomMenuBuilder builder) {
        context.push(builder);
    }

    @Override
    public MenuBuilderImpl newContributedMenu(final String caption) {
        final CurrentContext currentContext = new CurrentContext();
        currentContext.caption = checkNotEmpty("caption",
                                               caption);
        currentContext.menuType = MenuType.CONTRIBUTED;
        context.push(currentContext);

        return this;
    }

    @Override
    public MenuBuilderImpl newTopLevelMenu(final MenuItem menu) {
        final CurrentContext currentContext = new CurrentContext();
        currentContext.menu = checkNotNull("menu",
                                           menu);
        context.push(currentContext);

        return this;
    }

    @Override
    public MenuBuilderImpl newTopLevelMenu(final String caption) {
        final CurrentContext currentContext = new CurrentContext();
        currentContext.caption = checkNotEmpty("caption",
                                               caption);
        currentContext.menuType = MenuType.TOP_LEVEL;
        context.push(currentContext);

        return this;
    }

    @Override
    public MenuFactory.TerminalCustomMenu newTopLevelCustomMenu(final MenuFactory.CustomMenuBuilder builder) {
        context.push(builder);

        return this;
    }

    @Override
    public MenuBuilderImpl menu(final String caption) {
        final CurrentContext currentContext = new CurrentContext();
        currentContext.caption = checkNotEmpty("caption",
                                               caption);
        currentContext.menuType = MenuType.REGULAR;
        context.push(currentContext);

        return this;
    }

    @Override
    public MenuBuilderImpl menus() {
        ((CurrentContext) context.peek()).menuType = MenuType.GROUP;
        return this;
    }

    @Override
    public MenuFactory.TerminalMenu custom(MenuFactory.CustomMenuBuilder builder) {
        context.push(builder);

        return this;
    }

    @Override
    public MenuBuilderImpl submenu(final String caption) {
        final CurrentContext currentContext = new CurrentContext();
        currentContext.caption = checkNotEmpty("caption",
                                               caption);
        currentContext.menuType = MenuType.GROUP;
        context.push(currentContext);

        return this;
    }

    @Override
    public MenuBuilderImpl contributeTo(final String contributionPoint) {
        ((CurrentContext) context.peek()).contributionPoint = checkNotEmpty("contributionPoint",
                                                                            contributionPoint);
        return this;
    }

    @Override
    public MenuBuilderImpl withItems(final List items) {
        ((CurrentContext) context.peek()).menuItems = new ArrayList<MenuItem>(checkNotEmpty("items",
                                                                                            items));

        return this;
    }

    @Override
    public MenuBuilderImpl respondsWith(final Command command) {
        ((CurrentContext) context.peek()).command = checkNotNull("command",
                                                                 command);

        return this;
    }

    @Override
    public MenuBuilderImpl perspective(final String identifier) {
        checkNotNull("perspective",
                     identifier);
        ((CurrentContext) context.peek()).placeRequest = new DefaultPlaceRequest(identifier);
        return this;
    }

    @Override
    public MenuBuilderImpl place(final PlaceRequest place) {
        ((CurrentContext) context.peek()).placeRequest = checkNotNull("place",
                                                                      place);
        return this;
    }

    @Override
    public MenuBuilderImpl order(final int order) {
        ((CurrentContext) context.peek()).order = order;

        return this;
    }

    @Override
    public MenuBuilderImpl position(final MenuPosition position) {
        ((CurrentContext) context.peek()).position = checkNotNull("position",
                                                                  position);

        return this;
    }

    @Override
    public MenuBuilderImpl identifier(final String id) {
        ((CurrentContext) context.peek()).identifier = checkNotEmpty("identifier",
                                                                     id);

        return this;
    }

    @Override
    public MenuBuilderImpl withPermission(ResourceType resourceType) {
        ResourceRef resource = new ResourceRef(null,
                                               resourceType);
        ResourceActionRef ref = new ResourceActionRef(resource);
        ((CurrentContext) context.peek()).resourceActionRefs.add(ref);

        return this;
    }

    @Override
    public MenuBuilderImpl withPermission(ResourceType resourceType,
                                          ResourceAction resourceAction) {
        ResourceActionRef ref = new ResourceActionRef(resourceType,
                                                      resourceAction);
        ((CurrentContext) context.peek()).resourceActionRefs.add(ref);

        return this;
    }

    @Override
    public MenuBuilderImpl withPermission(ResourceType resourceType,
                                          Resource resource,
                                          ResourceAction resourceAction) {
        if (resource == null) {
            return withPermission(resourceType,
                                  resourceAction);
        } else {
            return withPermission(resource,
                                  resourceAction);
        }
    }

    @Override
    public MenuBuilderImpl withPermission(Resource resource) {
        ResourceActionRef ref = new ResourceActionRef(resource);
        ((CurrentContext) context.peek()).resourceActionRefs.add(ref);

        return this;
    }

    @Override
    public Object withPermission(String resourceId,
                                 ResourceType resourceType) {
        ResourceRef resource = new ResourceRef(resourceId,
                                               resourceType);
        ResourceActionRef ref = new ResourceActionRef(resource);
        ((CurrentContext) context.peek()).resourceActionRefs.add(ref);

        return this;
    }

    @Override
    public Object withPermission(String resourceId,
                                 ResourceType resourceType,
                                 ResourceAction resourceAction) {
        ResourceRef resource = new ResourceRef(resourceId,
                                               resourceType);
        ResourceActionRef ref = new ResourceActionRef(resource,
                                                      resourceAction);
        ((CurrentContext) context.peek()).resourceActionRefs.add(ref);

        return this;
    }

    @Override
    public MenuBuilderImpl withPermission(Resource resource,
                                          ResourceAction resourceAction) {
        ResourceActionRef ref = new ResourceActionRef(resource,
                                                      resourceAction);
        ((CurrentContext) context.peek()).resourceActionRefs.add(ref);

        return this;
    }

    @Override
    public MenuBuilderImpl withPermission(String permission) {
        ((CurrentContext) context.peek()).permissionNames.add(permission);

        return this;
    }

    @Override
    public MenuBuilderImpl endMenus() {
        return this;
    }

    @Override
    public MenuBuilderImpl endMenu() {
        if (context.size() == 1) {
            menuItems.add(context.pop().build());
        } else {
            final MenuFactory.CustomMenuBuilder active = context.pop();
            context.peek().push(active);
        }

        return this;
    }

    @Override
    public MenuBuilderImpl orderAll(final int order) {
        this.order = order;

        return this;
    }

    @Override
    public Menus build() {
        context.clear();

        return new DefaultMenus(menuItems,
                                order);
    }

    public enum MenuType {
        TOP_LEVEL,
        CONTRIBUTED,
        REGULAR,
        GROUP,
        CUSTOM
    }

    private static class CurrentContext implements MenuFactory.CustomMenuBuilder {

        MenuItem menu = null;

        int order = 0;
        MenuType menuType = MenuType.REGULAR;
        String caption = null;
        MenuPosition position = MenuPosition.LEFT;
        String contributionPoint = null;
        Command command = null;
        PlaceRequest placeRequest = null;
        String identifier = null;
        List<ResourceActionRef> resourceActionRefs = new ArrayList<>();
        List<String> permissionNames = new ArrayList<>();
        List menuItems = new ArrayList<MenuItem>();
        Stack<MenuFactory.CustomMenuBuilder> menuRawItems = new Stack<MenuFactory.CustomMenuBuilder>();

        @Override
        public void push(MenuFactory.CustomMenuBuilder element) {
            menuRawItems.push(element);
        }

        @Override
        public MenuItem build() {
            if (menu != null) {
                return menu;
            }
            if (menuItems.size() > 0 || menuRawItems.size() > 0) {
                if (menuRawItems.size() > 0) {
                    for (final MenuFactory.CustomMenuBuilder current : menuRawItems) {
                        menuItems.add(current.build());
                    }
                }

                return new DefaultMenuGroup(menuItems,
                                            resourceActionRefs,
                                            permissionNames,
                                            contributionPoint,
                                            caption,
                                            position,
                                            order);
            } else if (command != null) {
                return new MenuItemCommand() {

                    private final List<EnabledStateChangeListener> enabledStateChangeListeners = new ArrayList<EnabledStateChangeListener>();
                    private boolean isEnabled = true;

                    @Override
                    public String getIdentifier() {
                        if (identifier != null) {
                            return identifier;
                        }
                        if (contributionPoint != null) {
                            return getClass().getName() + "#" + contributionPoint + "#" + caption;
                        }
                        return getClass().getName() + "#" + caption;
                    }

                    @Override
                    public List<ResourceActionRef> getResourceActions() {
                        return resourceActionRefs;
                    }

                    @Override
                    public List<String> getPermissions() {
                        return permissionNames;
                    }

                    @Override
                    public Command getCommand() {
                        return command;
                    }

                    @Override
                    public String getContributionPoint() {
                        return contributionPoint;
                    }

                    @Override
                    public String getCaption() {
                        return caption;
                    }

                    @Override
                    public MenuPosition getPosition() {
                        return position;
                    }

                    @Override
                    public int getOrder() {
                        return order;
                    }

                    @Override
                    public boolean isEnabled() {
                        return isEnabled;
                    }

                    @Override
                    public void setEnabled(final boolean enabled) {
                        this.isEnabled = enabled;
                        notifyListeners(enabled);
                    }

                    @Override
                    public void addEnabledStateChangeListener(final EnabledStateChangeListener listener) {
                        enabledStateChangeListeners.add(listener);
                    }

                    @Override
                    public void accept(MenuVisitor visitor) {
                        visitor.visit(this);
                    }

                    private void notifyListeners(final boolean enabled) {
                        for (final EnabledStateChangeListener listener : enabledStateChangeListeners) {
                            listener.enabledStateChanged(enabled);
                        }
                    }
                };
            } else if (placeRequest != null) {
                return new MenuItemPerspective() {

                    private final List<EnabledStateChangeListener> enabledStateChangeListeners = new ArrayList<EnabledStateChangeListener>();
                    private boolean isEnabled = true;

                    @Override
                    public PlaceRequest getPlaceRequest() {
                        return placeRequest;
                    }

                    @Override
                    public String getIdentifier() {
                        if (identifier != null) {
                            return identifier;
                        }
                        if (contributionPoint != null) {
                            return getClass().getName() + "#" + contributionPoint + "#" + caption;
                        }
                        return getClass().getName() + "#" + caption;
                    }

                    @Override
                    public List<ResourceActionRef> getResourceActions() {
                        return resourceActionRefs;
                    }

                    @Override
                    public List<String> getPermissions() {
                        return permissionNames;
                    }

                    @Override
                    public List<Resource> getDependencies() {
                        ResourceRef ref = new ResourceRef(placeRequest.getIdentifier(),
                                                          ActivityResourceType.PERSPECTIVE);
                        return Collections.singletonList(ref);
                    }

                    @Override
                    public String getContributionPoint() {
                        return contributionPoint;
                    }

                    @Override
                    public String getCaption() {
                        return caption;
                    }

                    @Override
                    public MenuPosition getPosition() {
                        return position;
                    }

                    @Override
                    public int getOrder() {
                        return order;
                    }

                    @Override
                    public boolean isEnabled() {
                        return isEnabled;
                    }

                    @Override
                    public void setEnabled(final boolean enabled) {
                        this.isEnabled = enabled;
                        notifyListeners(enabled);
                    }

                    @Override
                    public void addEnabledStateChangeListener(final EnabledStateChangeListener listener) {
                        enabledStateChangeListeners.add(listener);
                    }

                    @Override
                    public void accept(MenuVisitor visitor) {
                        visitor.visit(this);
                    }

                    private void notifyListeners(final boolean enabled) {
                        for (final EnabledStateChangeListener listener : enabledStateChangeListeners) {
                            listener.enabledStateChanged(enabled);
                        }
                    }
                };
            }
            return new MenuItemPlain() {

                private final List<EnabledStateChangeListener> enabledStateChangeListeners = new ArrayList<EnabledStateChangeListener>();
                private boolean isEnabled = true;

                @Override
                public String getIdentifier() {
                    if (identifier != null) {
                        return identifier;
                    }
                    if (contributionPoint != null) {
                        return getClass().getName() + "#" + contributionPoint + "#" + caption;
                    }
                    return getClass().getName() + "#" + caption;
                }

                @Override
                public List<ResourceActionRef> getResourceActions() {
                    return resourceActionRefs;
                }

                @Override
                public List<String> getPermissions() {
                    return permissionNames;
                }

                @Override
                public String getContributionPoint() {
                    return contributionPoint;
                }

                @Override
                public String getCaption() {
                    return caption;
                }

                @Override
                public MenuPosition getPosition() {
                    return position;
                }

                @Override
                public int getOrder() {
                    return order;
                }

                @Override
                public boolean isEnabled() {
                    return isEnabled;
                }

                @Override
                public void setEnabled(final boolean enabled) {
                    this.isEnabled = enabled;
                    notifyListeners(enabled);
                }

                @Override
                public void addEnabledStateChangeListener(final EnabledStateChangeListener listener) {
                    enabledStateChangeListeners.add(listener);
                }

                @Override
                public void accept(MenuVisitor visitor) {
                    visitor.visit(this);
                }

                private void notifyListeners(final boolean enabled) {
                    for (final EnabledStateChangeListener listener : enabledStateChangeListeners) {
                        listener.enabledStateChanged(enabled);
                    }
                }
            };
        }
    }
}
