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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.client.ui.html.Text;
import org.uberfire.workbench.model.menu.MenuPosition;

@Dependent
public class UserMenuViewImpl extends AnchorListItem implements UserMenu.UserMenuView {

    private final DropDownMenu menu = new DropDownMenu();
    private final Text userNameWidget = new Text();

    @PostConstruct
    public void setup() {
        anchor.addStyleName( Styles.DROPDOWN_TOGGLE );
        anchor.setDataToggle( Toggle.DROPDOWN );

        final Span userIcon = new Span();
        userIcon.addStyleName( "pficon" );
        userIcon.addStyleName( "pficon-user" );

        anchor.add( userIcon );
        anchor.add( userNameWidget );
        final Span caret = new Span();
        caret.addStyleName( Styles.CARET );
        anchor.add( caret );

        addStyleName( Styles.DROPDOWN );
        add( anchor );
        add( menu );
    }

    @Override
    public void setUserName( final String userName ) {
        userNameWidget.setText( userName );
    }

    @Override
    public void addMenuItem( final MenuPosition position, final Widget menuContent ) {
        //Always add new option on top
        menu.insert( menuContent, 0 );
    }

}
