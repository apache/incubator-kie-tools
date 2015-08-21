/*
 * Copyright 2015 JBoss Inc
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

package org.uberfire.client.docks.view.menu;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.MINI;

@ApplicationScoped
public class MenuBuilder {

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private User identity;


    public Widget makeItem(final MenuItem item,
                           boolean isRoot) {
        if (!authzManager.authorize(item, identity)) {
            return null;
        }

        if (item instanceof MenuItemCommand) {
            final MenuItemCommand cmdItem = (MenuItemCommand) item;
            final Widget gwtItem;
            if (isRoot) {
                gwtItem = new Button(cmdItem.getCaption()) {{
                    setSize(MINI);
                    setEnabled(item.isEnabled());
                    addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(final ClickEvent event) {
                            cmdItem.getCommand().execute();
                        }
                    });
                }};
                item.addEnabledStateChangeListener(new EnabledStateChangeListener() {
                    @Override
                    public void enabledStateChanged(final boolean enabled) {
                        ((Button) gwtItem).setEnabled(enabled);
                    }
                });
            } else {
                gwtItem = new NavLink(cmdItem.getCaption()) {{
                    setDisabled(!item.isEnabled());
                    addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(final ClickEvent event) {
                            cmdItem.getCommand().execute();
                        }
                    });
                }};
                item.addEnabledStateChangeListener(new EnabledStateChangeListener() {
                    @Override
                    public void enabledStateChanged(final boolean enabled) {
                        ((NavLink) gwtItem).setDisabled(!enabled);
                    }
                });
            }

            return gwtItem;

        } else if (item instanceof MenuGroup) {
            final MenuGroup groups = (MenuGroup) item;
            final Widget gwtItem;
            if (isRoot) {
                final List<Widget> widgetList = new ArrayList<Widget>();
                for (final MenuItem _item : groups.getItems()) {
                    final Widget widget = makeItem(_item, false);
                    if (widget != null) {
                        widgetList.add(widget);
                    }
                }

                if (widgetList.isEmpty()) {
                    return null;
                }

                gwtItem = new DropdownButton(groups.getCaption()) {{
                    setSize(MINI);
                    for (final Widget _item : widgetList) {
                        add(_item);
                    }
                }};
            } else {
                final List<Widget> widgetList = new ArrayList<Widget>();
                for (final MenuItem _item : groups.getItems()) {
                    final Widget result = makeItem(_item, false);
                    if (result != null) {
                        widgetList.add(result);
                    }
                }

                if (widgetList.isEmpty()) {
                    return null;
                }

                gwtItem = new Dropdown(groups.getCaption()) {{
                    for (final Widget widget : widgetList) {
                        add(widget);
                    }
                }};
            }

            return gwtItem;
        } else if (item instanceof MenuCustom) {
            final Object result = ((MenuCustom) item).build();
            if (result instanceof Widget) {
                return (Widget) result;
            }
        }

        return null;
    }
}
