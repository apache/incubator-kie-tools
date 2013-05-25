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
package org.uberfire.client.workbench.widgets.statusbar;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.mvp.PlaceRequest;

/**
 * The Status Bar widget
 */
public class WorkbenchStatusBarView extends Composite
        implements
        WorkbenchStatusBarPresenter.View {

    private final HorizontalPanel statusBar = new HorizontalPanel();
    private final HorizontalPanel statusBarContainer = new HorizontalPanel();

    //Map of PlaceRequests to GWT Widgets used to represent them
    private final Map<PlaceRequest, IsWidget> statusBarItemsMap = new HashMap<PlaceRequest, IsWidget>();

    private WorkbenchStatusBarPresenter presenter;

    public WorkbenchStatusBarView() {
        initWidget( statusBarContainer );
        statusBarContainer.setStyleName( WorkbenchResources.INSTANCE.CSS().statusBar() );
        statusBarContainer.setSpacing( 0 );
        statusBar.setSpacing( 0 );
        statusBarContainer.add( statusBar );
    }

    @Override
    public void init( final WorkbenchStatusBarPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void addPlace( final PlaceRequest place ) {
        final IsWidget statusBarItem = makeMinimizedWidget( place );
        statusBarItemsMap.put( place,
                               statusBarItem );
        statusBar.add( statusBarItem );
    }

    @Override
    public void removePlace( final PlaceRequest place ) {
        final IsWidget statusBarItem = statusBarItemsMap.remove( place );
        if ( statusBarItem != null ) {
            statusBar.remove( statusBarItem );
        }
    }

    private IsWidget makeMinimizedWidget( final PlaceRequest place ) {
//        final CustomButton button = new ToolBarButton( WorkbenchResources.INSTANCE.images().minimizedPanel() );
//        button.setTitle( place.getIdentifier() );
//        button.addClickHandler( new ClickHandler() {
//
//            @Override
//            public void onClick( ClickEvent event ) {
//                presenter.restoreMinimizedPlace( place );
//            }
//
//        } );
//        return button;
        return null;
    }

}
