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

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Command;

public class MenuItemPresenter implements MenuItemView.Presenter {

    @Inject
    private Instance<MenuItemView> instance;

    protected Map<LIElement, Command> menuItemsCommandMap = new HashMap<>();

    @Override
    public void onClickEvent(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
    }

    @Override
    public void executeCommand(LIElement clickedElement) {
        if (menuItemsCommandMap.containsKey(clickedElement)) {
            menuItemsCommandMap.get(clickedElement).execute();
        }
    }

    @Override
    public LIElement getLIElement(String id, String label, Command command) {
        MenuItemView menuItemView = getMenuItemView();
        menuItemView.setId(id);
        menuItemView.setLabel(label);
        menuItemView.setPresenter(this);
        LIElement toReturn = menuItemView.getLIElement();
        menuItemsCommandMap.put(toReturn, command);
        return toReturn;
    }

    @Override
    public void enableElement(boolean toEnable) {

    }

    protected MenuItemView getMenuItemView() {  // This is needed for test because Mockito can not mock Instance
        return instance.get(); 

    }
}
