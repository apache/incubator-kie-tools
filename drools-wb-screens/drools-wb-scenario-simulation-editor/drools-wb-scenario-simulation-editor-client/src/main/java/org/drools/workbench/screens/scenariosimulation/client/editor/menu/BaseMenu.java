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

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.Event;
import org.gwtbootstrap3.client.ui.constants.Styles;

/**
 * Base Menu implementation for MenuItems.
 */
public abstract class BaseMenu implements IsWidget,
                                          BaseMenuView.BaseMenuPresenter {

    @Inject
    protected MenuItemPresenter menuItemPresenter;

    @Inject
    protected ExecutableMenuItemPresenter executableMenuItemPresenter;

    @Inject
    protected BaseMenuView view;

    @PostConstruct
    @Override
    public void initialise() {
        view.init(this);
        executableMenuItemPresenter.init(this);
    }

    @Override
    public LIElement addMenuItem(String id, String label, String i18n) {
        final LIElement toReturn = menuItemPresenter.getLabelMenuElement(id, label);
        view.getContextMenuDropdown().appendChild(toReturn);
        return toReturn;
    }

    @Override
    public LIElement addExecutableMenuItem(String id, String label, String i18n, Event event) {
        final LIElement toReturn = executableMenuItemPresenter.getLExecutableMenuElement(id, label, event);
        view.getContextMenuDropdown().appendChild(toReturn);
        return toReturn;
    }

    @Override
    public LIElement addExecutableMenuItem(String id, String label, String i18n) {
        LIElement toReturn = executableMenuItemPresenter.getLExecutableMenuElement(id, label);
        view.getContextMenuDropdown().appendChild(toReturn);
        return toReturn;
    }

    @Override
    public void removeMenuItem(LIElement toRemove) {
        view.getContextMenuDropdown().removeChild(toRemove);
    }

    @Override
    public void mapEvent(LIElement executableMenuItem, Event toBeMapped) {
        executableMenuItemPresenter.mapEvent(executableMenuItem, toBeMapped);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void show(final int mx, final int my) {
        //See https://issues.jboss.org/browse/ERRAI-936
        //Errai @Templated proxied beans have their "attached" state set to
        //true, even though they are not physically attached to the DOM.
        hide();
        getRootPanel().add(this);
        view.getContextMenuDropdown().getStyle().setLeft(mx, Style.Unit.PX);
        view.getContextMenuDropdown().getStyle().setTop(my, Style.Unit.PX);
        view.getContextMenuDropdown().getStyle().setDisplay(Style.Display.BLOCK);
    }

    @Override
    public void hide() {
        if (isShown()) {
            getRootPanel().remove(this);
            view.getContextMenuDropdown().getStyle().setDisplay(Style.Display.NONE);
        }
    }

    @Override
    public boolean isShown() {
        return getRootPanel().getWidgetIndex(view) != -1;
    }

    @Override
    public BaseMenuView getView() {
        return view;
    }

    @Override
    public void enableElement(Element element, boolean enabled) {
        if (enabled) {
            element.removeClassName(Styles.DISABLED);
        } else {
            element.addClassName(Styles.DISABLED);
        }
    }

    @Override
    public boolean isDisabled(final Element element) {
        final List<String> classNames = Arrays.asList(element.getClassName().split("\\s"));
        return classNames.contains(Styles.DISABLED);
    }

    @Override
    public void onContextMenuEvent(ContextMenuEvent event) {
        event.preventDefault();
        event.stopPropagation();
        hide();
    }

    protected RootPanel getRootPanel() {
        return RootPanel.get();
    }

    protected void updateMenuItemAttributes(LIElement toUpdate, String id, String label, String i18n) {
        toUpdate.setId(id);
        toUpdate.getElementsByTagName("span").getItem(0).setInnerHTML(label);
    }
}
