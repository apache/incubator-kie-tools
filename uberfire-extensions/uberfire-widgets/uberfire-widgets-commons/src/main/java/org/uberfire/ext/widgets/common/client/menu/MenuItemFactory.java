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
package org.uberfire.ext.widgets.common.client.menu;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

/**
 * Factory for different {@link MenuCustom} and associated {@link MenuItemView}.
 */
@ApplicationScoped
public class MenuItemFactory {

    private ManagedInstance<MenuItemView> menuItemViewProducer;

    @Inject
    public MenuItemFactory(final @Any ManagedInstance<MenuItemView> menuItemViewProducer) {
        this.menuItemViewProducer = menuItemViewProducer;
    }

    /**
     * Makes a {@link MenuCustom} and associated {@link MenuItemWithIconView} that can be used
     * to replace the default Views created by {@link ListBarWidgetImpl} if an icon is also required.
     * If an icon is not required the caption is indented to the position that it would adopt if an
     * icon had been specified. It is not possible to use BS3's {@link AnchorListItem} as this only
     * indents the caption IF an icon is specified.
     * @param caption Caption to be shown for the menu item.
     * @param cmd Command to execute when the menu item is clicked.
     * @param <T> {@link MenuItemWithIconView}
     * @return A {@link MenuItemViewHolder} with both a {@link MenuItem} and {@link MenuItemView}
     */
    @SuppressWarnings("unchecked")
    public <T extends MenuItemWithIconView> MenuItemViewHolder<T> makeMenuItemWithIcon(final String caption,
                                                                                       final Command cmd) {
        final MenuItemView _view = menuItemViewProducer.select(new MenuItemWithIcon() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return MenuItemWithIcon.class;
            }
        }).get();
        final T view = (T) _view;
        final MenuItem item = MenuFactory.newTopLevelCustomMenu(new MenuFactory.CustomMenuBuilder() {

            @Override
            public void push(final MenuFactory.CustomMenuBuilder element) {

            }

            @Override
            public MenuItem build() {
                final BaseMenuCustom<IsWidget> cmdItem = new BaseMenuCustom<IsWidget>(null,
                                                                                      caption) {

                    private final List<EnabledStateChangeListener> enabledStateChangeListeners = new ArrayList<>();

                    @Override
                    public IsWidget build() {
                        return ElementWrapperWidget.getWidget(view.getElement());
                    }

                    @Override
                    public void setEnabled(final boolean enabled) {
                        super.setEnabled(enabled);
                        notifyListeners(enabled);
                    }

                    @Override
                    public void addEnabledStateChangeListener(final EnabledStateChangeListener listener) {
                        enabledStateChangeListeners.add(listener);
                    }

                    private void notifyListeners(final boolean enabled) {
                        for (final EnabledStateChangeListener listener : enabledStateChangeListeners) {
                            listener.enabledStateChanged(enabled);
                        }
                    }
                };
                cmdItem.addEnabledStateChangeListener(view::setEnabled);

                return cmdItem;
            }
        }).endMenu().build().getItems().get(0);

        view.setCaption(caption);
        view.setClickHandler((e) -> {
            if (item.isEnabled()) {
                cmd.execute();
            }
        });

        return new MenuItemViewHolder<>(item,
                                        view);
    }

    /**
     * Makes a {@link MenuCustom} and associated {@link MenuItemHeaderView} that can be used
     * as a "header" in a menu. See http://getbootstrap.com/components/#dropdowns-headers
     * @param caption Caption to be shown for the menu header.
     * @param <T> {@link MenuItemHeaderView}
     * @return A {@link MenuItemViewHolder} with both a {@link MenuItem} and {@link MenuItemView}
     */
    @SuppressWarnings("unchecked")
    public <T extends MenuItemHeaderView> MenuItemViewHolder<T> makeMenuItemHeader(final String caption) {
        final MenuItemView _view = menuItemViewProducer.select(new MenuItemHeader() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return MenuItemHeader.class;
            }
        }).get();
        final T view = (T) _view;
        final MenuItem item = MenuFactory.newTopLevelCustomMenu(new MenuFactory.CustomMenuBuilder() {

            @Override
            public void push(final MenuFactory.CustomMenuBuilder element) {

            }

            @Override
            public MenuItem build() {
                final BaseMenuCustom<IsWidget> cmdItem = new BaseMenuCustom<IsWidget>(null,
                                                                                      caption) {

                    @Override
                    public IsWidget build() {
                        return ElementWrapperWidget.getWidget(view.getElement());
                    }
                };

                return cmdItem;
            }
        }).endMenu().build().getItems().get(0);

        view.setCaption(caption);

        return new MenuItemViewHolder<>(item,
                                        view);
    }

    /**
     * Makes a {@link MenuCustom} and associated {@link MenuItemDividerView} that can be used as
     * a "divider" in a menu. See http://getbootstrap.com/components/#dropdowns-divider
     * @param <T> {@link }MenuItemDividerView}
     * @return A {@link MenuItemViewHolder} with both a {@link MenuItem} and {@link MenuItemView}
     */
    @SuppressWarnings("unchecked")
    public <T extends MenuItemDividerView> MenuItemViewHolder<T> makeMenuItemDivider() {
        final MenuItemView _view = menuItemViewProducer.select(new MenuItemDivider() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return MenuItemDivider.class;
            }
        }).get();
        final T view = (T) _view;
        final MenuItem item = MenuFactory.newTopLevelCustomMenu(new MenuFactory.CustomMenuBuilder() {

            @Override
            public void push(final MenuFactory.CustomMenuBuilder element) {

            }

            @Override
            public MenuItem build() {
                final BaseMenuCustom<IsWidget> cmdItem = new BaseMenuCustom<IsWidget>() {

                    @Override
                    public IsWidget build() {
                        return ElementWrapperWidget.getWidget(view.getElement());
                    }
                };

                return cmdItem;
            }
        }).endMenu().build().getItems().get(0);

        return new MenuItemViewHolder<>(item,
                                        view);
    }

    /**
     * Container for @{link MenuCustom} and {@link MenuItemView}
     * @param <T> Type of view.
     */
    public static class MenuItemViewHolder<T extends MenuItemView> {

        private final MenuItem item;
        private final T view;

        public MenuItemViewHolder(final MenuItem item,
                                  final T view) {
            this.item = item;
            this.view = view;
        }

        public MenuItem getMenuItem() {
            return item;
        }

        public T getMenuItemView() {
            return view;
        }
    }
}
