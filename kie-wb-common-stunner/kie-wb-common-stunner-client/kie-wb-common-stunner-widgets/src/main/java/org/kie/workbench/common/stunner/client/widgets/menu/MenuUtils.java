/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.menu;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

public class MenuUtils {

    public interface HasEnabledIsWidget extends IsWidget {

        void setEnabled(boolean enabled);

        boolean isEnabled();
    }

    public static MenuItem buildItem(final IsWidget widget) {
        return new MenuFactory.CustomMenuBuilder() {
            @Override
            public void push(MenuFactory.CustomMenuBuilder element) {
            }

            @Override
            public MenuItem build() {
                return new BaseMenuCustom<Widget>() {
                    @Override
                    public Widget build() {
                        return widget.asWidget();
                    }

                    @Override
                    public boolean isEnabled() {
                        if (widget instanceof HasEnabledIsWidget) {
                            return ((HasEnabledIsWidget) widget).isEnabled();
                        } else {
                            return super.isEnabled();
                        }
                    }

                    @Override
                    public void setEnabled(boolean enabled) {
                        if (widget instanceof HasEnabledIsWidget) {
                            ((HasEnabledIsWidget) widget).setEnabled(enabled);
                        } else {
                            super.setEnabled(enabled);
                        }
                    }
                };
            }
        }.build();
    }

    public static HasEnabledIsWidget buildHasEnabledWidget(final Button button) {
        return new HasEnabledIsWidget() {
            @Override
            public void setEnabled(boolean enabled) {
                button.setEnabled(enabled);
            }

            @Override
            public boolean isEnabled() {
                return button.isEnabled();
            }

            @Override
            public Widget asWidget() {
                return button;
            }
        };
    }

    public static HasEnabledIsWidget buildHasEnabledWidget(final ButtonGroup buttonGroup,
                                                           final Button button) {
        return new HasEnabledIsWidget() {
            @Override
            public void setEnabled(boolean enabled) {
                button.setEnabled(enabled);
            }

            @Override
            public boolean isEnabled() {
                return button.isEnabled();
            }

            @Override
            public Widget asWidget() {
                return buttonGroup;
            }
        };
    }
}
