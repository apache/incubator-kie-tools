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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@Dependent
@WorkbenchScreen( identifier = "ReadmeScreen", preferredWidth = 400 )
public class ReadmeScreen extends AbstractMarkdownScreen {

    @Override
    public String getMarkdownFileURI() {
        return "default://uf-playground/README.md";
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "README";
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return MenuFactory
                .newTopLevelMenu( "Validate" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        Window.alert( "valid!" );
                    }
                } )
                .endMenu()
                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                final ButtonGroup group = new ButtonGroup();
                                final Button dropdown = new Button( "Build" );
                                dropdown.setSize( ButtonSize.SMALL );
                                dropdown.setDataToggle( Toggle.DROPDOWN );
                                group.add( dropdown );
                                final DropDownMenu menu = new DropDownMenu();
                                menu.setPull( Pull.RIGHT );
                                final AnchorListItem build = new AnchorListItem( "Build & Deploy" );
                                build.addClickHandler( new ClickHandler() {
                                    @Override
                                    public void onClick( ClickEvent event ) {
                                        Window.alert( "Build!" );
                                    }
                                } );
                                menu.add( build );
                                group.add( menu );
                                return group;
                            }
                        };
                    }
                } ).endMenu()
                .build();
    }

}