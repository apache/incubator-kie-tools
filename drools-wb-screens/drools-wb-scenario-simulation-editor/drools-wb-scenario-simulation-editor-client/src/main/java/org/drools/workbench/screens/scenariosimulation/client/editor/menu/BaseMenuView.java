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
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.user.client.Command;
import org.drools.workbench.screens.scenariosimulation.client.events.RefreshMenusEvent;
import org.uberfire.client.mvp.UberView;

/**
 * Basic interface for any menu dynamically generated.
 * @param <M>
 */
public interface BaseMenuView<M extends BaseMenu> extends UberView<M> {

    interface BaseMenuPresenter {

        void initialise();

        /**
         * Add a menu voice in form of <code>LIElement</code> to the underlying view
         * @param id
         * @param label
         * @param i18n
         * @param command
         */
        void addMenuItem(String id, String label, String i18n, Command command);

        void onRefreshMenusEvent(final RefreshMenusEvent event);

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
