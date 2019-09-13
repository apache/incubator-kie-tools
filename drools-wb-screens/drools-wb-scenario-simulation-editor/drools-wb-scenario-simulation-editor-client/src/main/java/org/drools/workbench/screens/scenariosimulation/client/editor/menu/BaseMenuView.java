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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.web.bindery.event.shared.Event;
import org.uberfire.client.mvp.UberView;

/**
 * Basic interface for any menu dynamically generated.
 * @param <M>
 */
public interface BaseMenuView<M extends BaseMenu> extends UberView<M> {

    interface BaseMenuPresenter {

        void initialise();

        /**
         * Add a <i>label</i> menu voice in form of <code>LIElement</code> to the underlying view
         * @param id
         * @param label
         * @param i18n
         * @return
         */
        LIElement addMenuItem(String id, String label, String i18n);

        /**
         * Add an <i>executable</i> menu voice in form of <code>LIElement</code> to the underlying view
         * @param id
         * @param label
         * @param i18n
         * @param event the <code>Event</code> to fire on click
         * @return
         */
        LIElement addExecutableMenuItem(String id, String label, String i18n, Event event);


        /**
         * Add an <i>executable</i> menu voice in form of <code>LIElement</code> to the underlying view
         * It is an <b>overload</b> of {@link #addExecutableMenuItem(String, String, String, Event)} to dynamically provide
         * an event at runtime
         * @param id
         * @param label
         * @param i18n
         */
        LIElement addExecutableMenuItem(String id, String label, String i18n);

        /**
         * Remove a menu voice from the given menu
         * @param toRemove
         */
        void removeMenuItem(LIElement toRemove);

        /**
         * Method to map an <code>Event</code> to a given <b>EXECUTABLE</b> <code>LIElement</code>.
         * To be used when <code>LIElement</code> has been retrieved with {@link #addExecutableMenuItem(String, String, String)}
         * @param executableMenuItem
         * @param toBeMapped
         */
        void mapEvent(LIElement executableMenuItem, Event toBeMapped);

        void show(final int mx,
                  final int my);

        void hide();

        BaseMenuView getView();

        void enableElement(final Element element, final boolean enabled);

        boolean isDisabled(final Element element);

        /**
         * Method to retrieve the visibility state of the view
         * @return
         */
        boolean isShown();

        void onContextMenuEvent(ContextMenuEvent event);
    }

    UListElement getContextMenuDropdown();
}
