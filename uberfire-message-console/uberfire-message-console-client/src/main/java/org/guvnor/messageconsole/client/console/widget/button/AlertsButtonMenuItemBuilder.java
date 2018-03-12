/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.messageconsole.client.console.widget.button;

import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

public class AlertsButtonMenuItemBuilder {

    private ViewHideAlertsButtonPresenter viewHideAlertsButtonPresenter;

    @Inject
    public AlertsButtonMenuItemBuilder(ViewHideAlertsButtonPresenter viewHideAlertsButtonPresenter) {
        this.viewHideAlertsButtonPresenter = viewHideAlertsButtonPresenter;
    }

    public MenuItem build() {
        viewHideAlertsButtonPresenter.addCssClassToButtons("btn-sm");

        return new MenuFactory.CustomMenuBuilder() {
            @Override
            public void push(MenuFactory.CustomMenuBuilder element) {
            }

            @Override
            public MenuItem build() {
                return new BaseMenuCustom<HTMLElement>() {

                    @Override
                    public HTMLElement build() {
                        return viewHideAlertsButtonPresenter.getView().getElement();
                    }
                };
            }
        }.build();
    }
}
