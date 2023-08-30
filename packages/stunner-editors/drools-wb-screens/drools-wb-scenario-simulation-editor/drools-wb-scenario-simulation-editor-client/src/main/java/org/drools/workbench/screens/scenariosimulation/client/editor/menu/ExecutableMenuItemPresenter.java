/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.drools.workbench.screens.scenariosimulation.client.editor.menu;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.web.bindery.event.shared.Event;

public class ExecutableMenuItemPresenter implements ExecutableMenuItemView.Presenter {

    @Inject
    private Instance<ExecutableMenuItemView> instance;

    EventBus eventBus;

    BaseMenu parent;

    protected Map<LIElement, Event> menuItemsEventMap = new HashMap<>();

    @Override
    public void init(BaseMenu parent) {
        this.parent = parent;
    }

    @Override
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onClickEvent(ClickEvent event, LIElement clickedElement) {
        event.preventDefault();
        event.stopPropagation();
        parent.hide();
        fireEvent(clickedElement);
    }

    @Override
    public void fireEvent(LIElement clickedElement) {
        if (menuItemsEventMap.containsKey(clickedElement)) {
            final Event event = menuItemsEventMap.get(clickedElement);
            eventBus.fireEvent(event);
        }
    }

    @Override
    public LIElement getLExecutableMenuElement(String id, String label, Event event) {
        LIElement toReturn = getLExecutableMenuElement(id, label);
        menuItemsEventMap.put(toReturn, event);
        return toReturn;
    }

    @Override
    public LIElement getLExecutableMenuElement(String id, String label) {
        ExecutableMenuItemView menuItemView = getMenuItemView();
        menuItemView.setId(id);
        menuItemView.setLabel(label);
        menuItemView.setPresenter(this);
        return menuItemView.getLExecutableMenuElement();
    }

    @Override
    public void mapEvent(LIElement executableMenuItem, Event toBeMapped) {
        menuItemsEventMap.put(executableMenuItem, toBeMapped);
    }

    @Override
    public void enableElement(boolean toEnable) {

    }

    protected ExecutableMenuItemView getMenuItemView() {  // This is needed for test because Mockito can not mock Instance
        return instance.get();
    }
}
