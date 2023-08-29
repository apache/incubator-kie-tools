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

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.web.bindery.event.shared.Event;

public interface ExecutableMenuItemView {

    interface Presenter {

        void init(BaseMenu parent);

        void setEventBus(EventBus eventBus);

        void fireEvent(LIElement clickedElement);

        /**
         * This method retrieve an <b>EXECUTABLE</b> element (i.e. with a behaviour) to be put inside the menu
         * @param id
         * @param innerText
         * @param event
         * @return
         */
        LIElement getLExecutableMenuElement(String id, String innerText, Event event);

        /**
         * This method retrieve an <b>EXECUTABLE</b> element (i.e. with a behaviour) to be put inside the menu
         * It is an <b>overload</b> of {@link #getLExecutableMenuElement(String, String, Event)} to dynamically provide
         * an event at runtime
         * @param id
         * @param innerText
         * @return
         */
        LIElement getLExecutableMenuElement(String id, String innerText);

        /**
         * Method to map an <code>Event</code> to a given <b>EXECUTABLE</b> <code>LIElement</code>.
         * To be used when <code>LIElement</code> has been retrieved with {@link #getLExecutableMenuElement(String, String)}
         * @param executableMenuItem
         * @param toBeMapped
         */
        void mapEvent(LIElement executableMenuItem, Event toBeMapped);

        void enableElement(boolean toEnable);

        void onClickEvent(ClickEvent event, LIElement clickedElement);
    }

    void setPresenter(ExecutableMenuItemPresenter executableMenuItemPresenter);

    void setId(String id);

    void setDataI18nKey(String dataI18nKey);

    void setLabel(String label);

    /**
     * This method retrieve an <b>EXECUTABLE</b> element (i.e. with a behaviour) to be put inside the menu
     * @return
     */
    LIElement getLExecutableMenuElement();
}
