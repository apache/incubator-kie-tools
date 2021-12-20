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

package org.drools.workbench.screens.scenariosimulation.client.editor.menu;

import javax.inject.Inject;

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;

public class MenuItemPresenter implements MenuItemView.Presenter {

    @Inject
    protected ViewsProvider viewsProvider;

    @Override
    public void onClickEvent(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
    }

    @Override
    public LIElement getLabelMenuElement(String id, String label) {
        MenuItemView menuItemView = viewsProvider.getMenuItemView();
        menuItemView.setId(id);
        menuItemView.setLabel(label);
        menuItemView.setPresenter(this);
        return menuItemView.getLabelMenuElement();
    }

    @Override
    public void onEnableElement(boolean toEnable) {

    }

}
