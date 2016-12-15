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

package org.kie.workbench.common.stunner.client.widgets.navigation.home.item;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.client.widgets.navigation.navigator.Navigator;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.logging.Logger;

@Dependent
public class HomeNavigationItem {

    private static Logger LOGGER = Logger.getLogger( HomeNavigationItem.class.getName() );

    public interface View extends UberView<HomeNavigationItem> {

        View setCollapsed( boolean collapsed );

        View setPanelTitle( String title );

        View setPanelIcon( IconType icon );

        View setTooltip( String tooltip );

        View setPanelVisible( boolean visible );

        boolean isPanelVisible();

        boolean isPanelCollapsed();

        View add( IsWidget widget );

        View clear();

    }

    View view;

    @Inject
    public HomeNavigationItem( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public HomeNavigationItem setVisible( final boolean visible ) {
        view.setPanelVisible( visible );
        return this;
    }

    public HomeNavigationItem setCollapsed( final boolean collapsed ) {
        view.setCollapsed( collapsed );
        return this;
    }

    public void show( final String title,
                      final String tooltip,
                      final Navigator<?> navigator ) {
        show( title, null, tooltip, navigator );
    }

    public void show( final IconType icon,
                      final String tooltip,
                      final Navigator<?> navigator ) {
        show( null, icon, tooltip, navigator );
    }

    private void show( final String title,
                       final IconType icon,
                       final String tooltip,
                       final Navigator<?> navigator ) {
        clear();
        if ( null != navigator ) {
            if ( null != title ) {
                view.setPanelTitle( title );
                view.setCollapsed( false );

            } else {
                view.setPanelIcon( icon );
                view.setCollapsed( true );

            }
            view.setTooltip( tooltip );
            view.add( navigator.asWidget() );
            navigator.show();

        }

    }

    public void clear() {
        view.clear();
    }

    public View getView() {
        return view;
    }
}
