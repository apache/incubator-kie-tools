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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuVisitor;

/**
 * Presenter for WorkbenchMenuBar that mediates changes to the Workbench MenuBar
 * in response to changes to the selected WorkbenchPart. The menu structure is
 * cloned and items that lack permission are removed. This implementation is
 * specific to GWT. An alternative implementation should be considered for use
 * within Eclipse.
 */
@ApplicationScoped
public class WorkbenchMenuBarPresenter implements WorkbenchMenuBar {

    private boolean useExpandedMode = true;
    private boolean expanded = true;

    public interface View
            extends
            IsWidget {

        void clear();

        void addMenuItems( Menus menus );

        void selectMenu( MenuItem menu );

        void expand();

        void collapse();

        void addCollapseHandler( Command command );

        void addExpandHandler( Command command );
    }

    private final Map<String, MenuItemPerspective> perspectiveMenus = new HashMap<String, MenuItemPerspective>();

    @Inject
    private View view;

    public IsWidget getView() {
        return this.view;
    }

    @PostConstruct
    protected void setup(){
        view.addExpandHandler( new Command() {
            @Override
            public void execute() {
                expanded = true;
            }
        } );
        view.addCollapseHandler( new Command() {
            @Override
            public void execute() {
                expanded = false;
            }
        } );
    }

    @Override
    public void addMenus( final Menus menus ) {
        if ( menus != null && !menus.getItems().isEmpty() ) {
            view.addMenuItems( menus );
            menus.accept( new BaseMenuVisitor() {
                @Override
                public void visit( final MenuItemPerspective menuItemPerspective ) {
                    perspectiveMenus.put( menuItemPerspective.getIdentifier(), menuItemPerspective );
                }
            } );
        }
    }

    protected void onPerspectiveChange( @Observes final PerspectiveChange perspectiveChange ) {
        final MenuItemPerspective mip = perspectiveMenus.get( perspectiveChange.getIdentifier() );
        if ( mip != null ) {
            view.selectMenu( mip );
        }
    }

    protected void onPlaceMinimized( @Observes final PlaceMinimizedEvent event ) {
        if( isUseExpandedMode() ) {
            view.expand();
        }
    }

    protected void onPlaceMaximized( @Observes final PlaceMaximizedEvent event ) {
        view.collapse();
    }

    @Override
    public void clear() {
        perspectiveMenus.clear();
        view.clear();
    }

    @Override
    public void expand() {
        useExpandedMode = true;
        view.expand();
    }

    @Override
    public boolean isUseExpandedMode() {
        return useExpandedMode;
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void collapse() {
        useExpandedMode = false;
        view.collapse();
    }

    @Override
    public void addCollapseHandler( final Command command ) {
        view.addCollapseHandler( command );
    }

    @Override
    public void addExpandHandler( final Command command ) {
        view.addExpandHandler( command );
    }
}
