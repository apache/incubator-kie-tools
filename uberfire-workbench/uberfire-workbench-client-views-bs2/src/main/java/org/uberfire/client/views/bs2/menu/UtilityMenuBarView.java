/*
 *
 *  * Copyright 2012 JBoss Inc
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.views.bs2.menu;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.widgets.menu.UtilityMenuBarPresenter;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Utility menu bar. Not implemented on BS2, delegate to {@link WorkbenchMenuBarPresenter}
 */
@ApplicationScoped
public class UtilityMenuBarView implements UtilityMenuBarPresenter.View {

    @Inject
    private WorkbenchMenuBarPresenter menuBarPresenter;

    @Override
    public void addMenus( final Menus menus ) {
        menuBarPresenter.addMenus( menus );
    }

    @Override
    public void clear() {

    }

    @Override
    public Widget asWidget() {
        return menuBarPresenter.getView().asWidget();
    }
}
