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
import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.Window;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.screens.todo.AbstractMarkdownScreen;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "SidePanelTodoListScreen", preferredWidth = 500)
public class MultiScreenSidePanel extends AbstractMarkdownScreen {

    @Override
    public String getMarkdownFileURI() {
        return "default://uf-playground/todo.md";
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Side Panel Todo List";
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(MenuFactory
                                     .newTopLevelMenu("Save")
                                     .respondsWith(() -> Window.alert("Saved!"))
                                     .endMenu()
                                     .newTopLevelMenu("Delete")
                                     .respondsWith(() -> Window.alert("Deleted!"))
                                     .endMenu()
                                     .newTopLevelMenu("Edit")
                                     .menus()
                                     .menu("Cut")
                                     .respondsWith(() -> Window.alert("Cut!"))
                                     .endMenu()
                                     .menu("Paste")
                                     .respondsWith(() -> Window.alert("Paste!"))
                                     .endMenu()
                                     .endMenus()
                                     .endMenu()
                                     .build());
    }

    @DefaultPosition
    public Position getPosition() {
        return CompassPosition.EAST;
    }
}