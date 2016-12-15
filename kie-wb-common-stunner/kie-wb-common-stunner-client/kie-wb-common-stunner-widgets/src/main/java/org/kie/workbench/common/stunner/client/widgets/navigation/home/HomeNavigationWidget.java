/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.navigation.home;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.client.widgets.navigation.home.item.HomeNavigationItem;
import org.kie.workbench.common.stunner.client.widgets.navigation.navigator.diagrams.DiagramsNavigator;
import org.kie.workbench.common.stunner.client.widgets.navigation.navigator.shapesets.ShapeSetsNavigator;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.logging.Logger;

@Dependent
public class HomeNavigationWidget implements IsWidget {

    private static Logger LOGGER = Logger.getLogger( HomeNavigationWidget.class.getName() );

    public interface View extends UberView<HomeNavigationWidget> {

        View setIcon( IconType iconType );

        View setIconTitle( String text );

        View add( HomeNavigationItem.View view );

        View clear();

    }

    DiagramsNavigator diagramsNavigator;
    ShapeSetsNavigator shapeSetsNavigator;
    Instance<HomeNavigationItem> navigationItemInstances;
    View view;

    private HomeNavigationItem shapeSetsNavigatorItem;
    private HomeNavigationItem diagramsNavigatorItem;

    @Inject
    public HomeNavigationWidget( final View view,
                                 final Instance<HomeNavigationItem> navigationItemInstances,
                                 final DiagramsNavigator diagramsNavigator,
                                 final ShapeSetsNavigator shapeSetsNavigator ) {
        this.view = view;
        this.navigationItemInstances = navigationItemInstances;
        this.diagramsNavigator = diagramsNavigator;
        this.shapeSetsNavigator = shapeSetsNavigator;
        this.shapeSetsNavigatorItem = null;
        this.diagramsNavigatorItem = null;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void show() {
        clear();
        // A group for creating new diagrams using the Shape Sets navigator.
        this.shapeSetsNavigatorItem = newItem();
        view.add( shapeSetsNavigatorItem.getView() );
        shapeSetsNavigatorItem
                .setCollapsed( true )
                .setVisible( false )
                .show( "Create a diagram", "Create a new diagram", shapeSetsNavigator );
        // A group that contains existing diagrams using the Diagrams navigator.
        this.diagramsNavigatorItem = newItem();
        view.add( diagramsNavigatorItem.getView() );
        diagramsNavigatorItem
                .setCollapsed( false )
                .setVisible( true )
                .show( "My diagrams", "Load a diagram", diagramsNavigator );

    }

    public void clear() {
        view.clear();
        this.diagramsNavigatorItem = null;
        this.shapeSetsNavigatorItem = null;

    }

    public DiagramsNavigator getDiagramsNavigator() {
        return diagramsNavigator;
    }

    public ShapeSetsNavigator getShapeSetsNavigator() {
        return shapeSetsNavigator;
    }

    void onButtonClick() {
        if ( shapeSetsNavigatorItem.getView().isPanelVisible() ) {
            focusDiagramsNavigatorItem();

        } else {
            focusShapeSetsNavigatorItem();

        }

    }

    private void focusShapeSetsNavigatorItem() {
        shapeSetsNavigatorItem
                .setVisible( true )
                .setCollapsed( false );
        diagramsNavigatorItem.setCollapsed( true );
        view.setIcon( IconType.MINUS_CIRCLE );
        view.setIconTitle( "Explore" );

    }

    private void focusDiagramsNavigatorItem() {
        shapeSetsNavigatorItem
                .setVisible( false )
                .setCollapsed( true );
        diagramsNavigatorItem.setCollapsed( false );
        view.setIcon( IconType.PLUS_CIRCLE );
        view.setIconTitle( "Create" );

    }

    private HomeNavigationItem newItem() {
        return navigationItemInstances.get();
    }
}
