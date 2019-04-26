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

package org.uberfire.client.screens;

import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.Select;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@Templated
@WorkbenchScreen(identifier = "MultiScreen")
public class MultiScreen extends Composite {

    @Inject
    @DataField("button")
    Button button;

    @Inject
    @DataField("side-button")
    Button sideButton;

    @Inject
    PlaceManager placeManager;

    @Inject
    Button buttonMenu;

    @Inject
    ManagedInstance<Select> selects;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Multi Screen";
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitleDecoration() {
        final Select select = selects.get();
        select.addOption("Some option",
                         "value1",
                         false);
        select.addOption("Another option",
                         "value2",
                         false);
        select.setTitle("Select...");
        select.getElement().addEventListener("change",
                                             event -> Window.alert("Select change: " + select.getValue()),
                                             false);

        select.refresh();
        return ElementWrapperWidget.getWidget(select.getElement());
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return this;
    }

    @PostConstruct
    public void init() {
        buttonMenu.setType(Button.ButtonType.BUTTON);
        buttonMenu.setButtonStyleType(Button.ButtonStyleType.LINK);
        buttonMenu.setClickHandler(() -> Window.alert("Refresh!"));
        buttonMenu.addIcon("fa",
                           "fa-refresh");

        button.setClickHandler(() -> placeManager.goTo("TodoListScreen"));
        sideButton.setClickHandler(() -> placeManager.goTo("SidePanelTodoListScreen"));
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(MenuFactory
                                     .newTopLevelCustomMenu(new MenuFactory.CustomMenuBuilder() {
                                         @Override
                                         public void push(MenuFactory.CustomMenuBuilder element) {
                                         }

                                         @Override
                                         public MenuItem build() {
                                             return new BaseMenuCustom<HTMLElement>() {
                                                 @Override
                                                 public void accept(MenuVisitor visitor) {
                                                     visitor.visit(this);
                                                 }

                                                 @Override
                                                 public HTMLElement build() {
                                                     return buttonMenu.getElement();
                                                 }
                                             };
                                         }
                                     }).endMenu().build());
    }
}
