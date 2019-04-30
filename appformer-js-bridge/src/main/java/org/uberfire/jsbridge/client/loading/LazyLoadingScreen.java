/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.jsbridge.client.loading;

import javax.annotation.PostConstruct;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.menu.Menus;

@Templated("lazy-loading.html")
@WorkbenchScreen(identifier = LazyLoadingScreen.IDENTIFIER)
public class LazyLoadingScreen implements IsElement {

    public static final String IDENTIFIER = "LazyLoadingScreen";

    private Label title;

    @PostConstruct
    public void init() {
        this.title = new Label(getTitle());
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Lazy Loading Screen";
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitleDecoration() {
        return title;
    }

    @WorkbenchPartView
    public org.jboss.errai.common.client.api.elemental2.IsElement getView() {
        return this;
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return null;
    }
}
