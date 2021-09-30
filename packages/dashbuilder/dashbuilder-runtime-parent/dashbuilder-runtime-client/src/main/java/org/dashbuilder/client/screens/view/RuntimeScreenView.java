/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.screens.view;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.dashbuilder.client.RuntimeCommunication;
import org.dashbuilder.client.navbar.AppNavBar;
import org.dashbuilder.client.screens.RuntimeScreen;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@Templated
public class RuntimeScreenView implements RuntimeScreen.View {

    @Inject
    @DataField
    HTMLDivElement runtimePage;

    @Inject
    AppNavBar appNavBar;

    @Inject
    RuntimeCommunication runtimeCommunication;

    @Inject
    BusyIndicatorView loading;

    @Override
    public HTMLElement getElement() {
        return runtimePage;
    }

    @Override
    public void init(RuntimeScreen presenter) {
        // empty
    }

    @Override
    public void addMenus(Menus menus) {
        appNavBar.setupMenus(menus);
    }

}