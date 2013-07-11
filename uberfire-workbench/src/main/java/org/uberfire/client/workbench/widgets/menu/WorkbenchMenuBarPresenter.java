/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.widgets.menu;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.workbench.events.ClosePlaceEvent;
import org.uberfire.workbench.events.PlaceGainFocusEvent;
import org.uberfire.workbench.events.PlaceLostFocusEvent;
import org.uberfire.workbench.model.menu.BrandMenuItem;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Presenter for WorkbenchMenuBar that mediates changes to the Workbench MenuBar
 * in response to changes to the selected WorkbenchPart. The menu structure is
 * cloned and items that lack permission are removed. This implementation is
 * specific to GWT. An alternative implementation should be considered for use
 * within Eclipse.
 */
@ApplicationScoped
public class WorkbenchMenuBarPresenter implements WorkbenchMenuBar {

    public interface View
            extends
            IsWidget {

        void setBrandMenu( final BrandMenuItem brand );

        void clear();

        void addMenuItems( Menus menus );
    }

    private PlaceRequest activePlace;

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    public IsWidget getView() {
        return this.view;
    }

    @Override
    public void setBrandMenuItem( final BrandMenuItem brandMenuItem ) {
        if ( brandMenuItem != null ) {
            view.setBrandMenu( brandMenuItem );
        }
    }

    @Override
    public void addMenus( final Menus menus ) {
        if ( menus != null && !menus.getItems().isEmpty() ) {
            view.addMenuItems( menus );
        }
    }

    @Override
    public void clear() {
        view.clear();
    }

}
