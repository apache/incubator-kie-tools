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

package org.uberfire.client.views.pfly.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.menu.AuthFilterMenuVisitor;
import org.uberfire.client.workbench.widgets.menu.PerspectiveContextMenusPresenter;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
public class PerspectiveContextMenusView implements PerspectiveContextMenusPresenter.View {

    public static final String UF_PERSPECTIVE_CONTEXT_MENU = "uf-perspective-context-menu";

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private User identity;

    @Inject
    private WorkbenchMenuCompactNavBarView workbenchMenuCompactNavBarView;

    @Inject
    private WorkbenchMenuStandardNavBarView workbenchMenuStandardNavBarView;

    @Override
    public Widget asWidget() {
        return null;
    }

    @Override
    public void buildMenu( final Menus menus ) {
        clear();
        menus.accept( new AuthFilterMenuVisitor( authzManager, identity, new StackedDropdownMenuVisitor( new HasMenuItems() {
            @Override
            public void addMenuItem( final MenuPosition position, final Widget menuContent ) {
                menuContent.addStyleName( UF_PERSPECTIVE_CONTEXT_MENU );
                workbenchMenuCompactNavBarView.add( menuContent );
            }

            @Override
            public Widget asWidget() {
                return null;
            }
        } ) ) );

        //Standard menu needs to get widgets in reverse order so pull-right is consistent with compact menu view.
        final List<Widget> widgets = new ArrayList<Widget>();
        menus.accept( new AuthFilterMenuVisitor( authzManager, identity, new StackedDropdownMenuVisitor( new HasMenuItems() {
            @Override
            public void addMenuItem( final MenuPosition position, final Widget menuContent ) {
                menuContent.addStyleName( UF_PERSPECTIVE_CONTEXT_MENU );
                widgets.add( menuContent );
            }

            @Override
            public Widget asWidget() {
                return null;
            }
        } ) ) );
        for ( final Widget widget : Lists.reverse( widgets ) ) {
            workbenchMenuStandardNavBarView.addMenuItem( MenuPosition.RIGHT, widget );
        }
    }

    @Override
    public void clear() {
        removeWidgets( workbenchMenuCompactNavBarView.iterator() );
        removeWidgets( workbenchMenuStandardNavBarView.iterator() );
    }

    protected void removeWidgets( final Iterator<Widget> widgets ) {
        while ( widgets.hasNext() ) {
            final Widget child = widgets.next();

            if ( child.getElement().hasClassName( UF_PERSPECTIVE_CONTEXT_MENU ) ) {
                widgets.remove();
            }
        }
    }

}
