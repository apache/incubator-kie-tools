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

package org.uberfire.client.screens.multipage;

import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

/**
 * A stand-alone Presenter annotated to hook into the Workbench
 */
@WorkbenchScreen(identifier = "MultiPage")
public class MultiPagePresenter {

    public interface View
            extends
            UberView<MultiPagePresenter> {

    }

    @Inject
    public View view;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Multi View";
    }

    @WorkbenchPartView
    public UberView<MultiPagePresenter> getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelMenu( "My Context" )
                    .menus()
                        .menu( "New Menu" )
                            .respondsWith( new Command() {
                                @Override
                                public void execute() {
                                    Window.alert( "Ok!" );
                                }
                            } )
                        .endMenu()
                    .endMenus().
                endMenu()
                .newTopLevelMenu( "My New" )
                    .respondsWith( new Command() {
                        @Override
                        public void execute() {
                            Window.alert( "Cool!" );
                        }
                    } )
                .endMenu()
                .build();
    }

}