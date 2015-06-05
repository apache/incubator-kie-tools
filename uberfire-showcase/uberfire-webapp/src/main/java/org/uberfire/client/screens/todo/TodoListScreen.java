/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.screens.todo;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.Window;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen( identifier = "TodoListScreen", preferredWidth = 400 )
public class TodoListScreen extends AbstractMarkdownScreen {

    @Override
    public String getMarkdownFileURI() {
        return "default://uf-playground/todo.md";
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Todo List";
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return MenuFactory
                .newTopLevelMenu( "Save" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        Window.alert( "Saved!" );
                    }
                } )
                .endMenu()
                .newTopLevelMenu( "Delete" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        Window.alert( "Deleted!" );
                    }
                } )
                .endMenu()
                .build();
    }

}