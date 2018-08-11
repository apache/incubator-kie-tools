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

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Command;

public interface MenuItemView {

    interface Presenter {

        void executeCommand(LIElement clickedElement);

        LIElement getLIElement(String id, String innerText, Command command);

        void enableElement(boolean toEnable);

        void onClickEvent(ClickEvent event);

    }

    void setPresenter(MenuItemPresenter menuItemPresenter);

    void setId(String id);

    void setDataI18nKey(String dataI18nKey);

    void setLabel(String label);

    LIElement getLIElement();
}
