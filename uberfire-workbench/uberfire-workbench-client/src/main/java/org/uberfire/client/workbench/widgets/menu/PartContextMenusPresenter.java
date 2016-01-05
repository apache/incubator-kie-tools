/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
public class PartContextMenusPresenter {

    public interface View
            extends
            IsWidget {

        void buildMenu( final Menus menus );

        void clear();
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    private View view;

    private PlaceRequest activePlace = null;

    void onWorkbenchPartOnFocus( @Observes PlaceGainFocusEvent event ) {
        final Activity activity = placeManager.getActivity( event.getPlace() );
        if ( activity == null ) {
            return;
        }
        if ( !( activity instanceof WorkbenchActivity ) ) {
            return;
        }
        final WorkbenchActivity wbActivity = (WorkbenchActivity) activity;

        if ( !event.getPlace().equals( activePlace ) ) {

            //Add items for current WorkbenchPart
            activePlace = event.getPlace();

            buildMenu( wbActivity.getMenus() );
        }
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
