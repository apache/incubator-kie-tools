/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.client.workbench.widgets.menu;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Created by Cristiano Nicolai.
 */
@ApplicationScoped
public class UtilityMenuBarPresenter implements UtilityMenuBar {

    public interface View
            extends
            IsWidget, HasMenus {

        void clear();
    }

    @Inject
    private View view;

    public IsWidget getView() {
        return this.view;
    }

    @Override
    public void addMenus( final Menus menus ) {
        if ( menus != null && !menus.getItems().isEmpty() ) {
            view.addMenus( menus );
        }
    }

    @Override
    public void clear() {
        view.clear();
    }

}
