/*
 *
 *  * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.navbar;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@ApplicationScoped
public class SearchMenuBuilder implements MenuFactory.CustomMenuBuilder {

    private AnchorListItem link = GWT.create( AnchorListItem.class );

    public SearchMenuBuilder() {
        link.setIcon( IconType.SEARCH );
        link.setPull( Pull.RIGHT );
        link.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                Window.alert( "Search!" );
            }
        } );
    }

    @Override
    public void push( final MenuFactory.CustomMenuBuilder element ) {
        // Do nothing
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {

            @Override
            public IsWidget build() {
                return link;
            }

            @Override
            public MenuPosition getPosition() {
                return MenuPosition.RIGHT;
            }

        };
    }
}
