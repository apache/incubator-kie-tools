/*
 *
 *  * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.menu;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.NavbarNav;

public abstract class WorkbenchMenuNavBarView extends Composite implements WorkbenchMenuBarView.WorkbenchMenuNavBarView {

    public static final String UF_PERSPECTIVE_CONTEXT_MENU = "uf-perspective-context-menu";
    public static final String UF_PERSPECTIVE_CONTEXT_MENU_EMPTY = "uf-perspective-context-menu-empty";
    public static final String UF_PERSPECTIVE_CONTEXT_MENU_CONTAINER = "uf-perspective-context-menu-container";

    private final Map<String, ComplexPanel> menuItemWidgetMap = Maps.newHashMap();
    private final Map<String, ComplexPanel> menuItemContextWidgetMap = Maps.newHashMap();
    private final Map<String, ComplexPanel> contextContainerWidgetMap = Maps.newHashMap();

    protected NavbarNav navbarNav = GWT.create( NavbarNav.class );

    protected void setup() {
        initWidget( navbarNav );
    }

    @Override
    public void selectMenuItem( final String id ) {
        final ComplexPanel menuItemWidget = getMenuItemWidgetMap().get( id );
        if ( menuItemWidget != null ) {
            selectElement( menuItemWidget );
        }
    }

    @Override
    public void enableMenuItem( final String menuItemId,
                                final boolean enabled ) {
        final ComplexPanel cp = getMenuItemWidgetMap().get( menuItemId );
        if ( cp == null ) {
            return;
        }
        if ( cp instanceof AnchorListItem ) {
            ( (AnchorListItem) cp ).setEnabled( enabled );
        }
    }

    @Override
    public void enableContextMenuItem( final String menuItemId,
                                       final boolean enabled ) {
        final ComplexPanel cp = getMenuItemContextWidgetMap().get( menuItemId );
        if ( cp == null ) {
            return;
        }
        if ( cp instanceof AnchorListItem ) {
            ( (AnchorListItem) cp ).setEnabled( enabled );
        }
    }

    @Override
    public void clearContextMenu() {
        for( final ComplexPanel contextContainer : getMenuItemContextWidgetMap().values() ){
            contextContainer.clear();
            contextContainer.removeFromParent();
        }

        getMenuItemContextWidgetMap().clear();
    }

    protected abstract void selectElement( ComplexPanel menuItemWidget );

    public void clear() {
        navbarNav.clear();
        menuItemWidgetMap.clear();
        menuItemContextWidgetMap.clear();
        contextContainerWidgetMap.clear();
    }

    protected Map<String, ComplexPanel> getMenuItemWidgetMap() {
        return menuItemWidgetMap;
    }

    protected Map<String, ComplexPanel> getMenuItemContextWidgetMap() {
        return menuItemContextWidgetMap;
    }

    protected Map<String, ComplexPanel> getContextContainerWidgetMap() {
        return contextContainerWidgetMap;
    }

}