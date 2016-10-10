/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.client.screens;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.*;
import org.uberfire.workbench.model.menu.Menus;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

@Dependent
@WorkbenchScreen( identifier = "HomeScreen" )
public class HomeScreen {

    @WorkbenchPartTitle
    public String getScreenTitle() {
        return "Welcome to Stunner";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        FlowPanel fp = new FlowPanel();
        HTML html = new HTML( "Home Screen" );
        fp.add( html );
        return fp;
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "homeScreenContext";
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return null;
    }

    @PostConstruct
    void doLayout() {
        // Nothing to do.
    }
}
