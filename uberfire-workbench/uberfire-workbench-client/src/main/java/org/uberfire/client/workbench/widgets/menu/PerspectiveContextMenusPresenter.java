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
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
public class PerspectiveContextMenusPresenter {

    public interface View
            extends
            IsWidget {

        void buildMenu( final Menus menus );

        void clear();
    }

    @Inject
    private View view;

    void onPerspectiveChange( @Observes PerspectiveChange perspectiveChange ) {
        buildMenu( perspectiveChange.getMenus() );
    }

    private void buildMenu( final Menus menus ) {
        if ( menus == null || menus.getItems() == null || menus.getItems().isEmpty() ) {
            view.clear();
            return;
        }
        view.buildMenu( menus );
    }

    public View getView() {
        return view;
    }
}
