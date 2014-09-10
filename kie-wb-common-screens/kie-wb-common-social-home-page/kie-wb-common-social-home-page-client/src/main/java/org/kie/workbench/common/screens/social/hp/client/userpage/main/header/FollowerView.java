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

package org.kie.workbench.common.screens.social.hp.client.userpage.main.header;

import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.mvp.Command;

@Dependent
public class FollowerView extends Composite {

    interface HeaderViewBinder
            extends
            UiBinder<Widget, FollowerView> {

    }

    private static HeaderViewBinder uiBinder = GWT.create( HeaderViewBinder.class );

    @UiField
    FlowPanel followerPanel;

    public void init( SocialUser follower,
                      Image connections,
                      Command command ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        followerPanel.add( connections );
        followerPanel.add( createLink( follower, command ) );
    }

    private NavList createLink( SocialUser follower,
                                final Command command ) {
        NavList list = new NavList();
        NavLink link = new NavLink();
        link.setText( follower.getUserName() );
        link.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                command.execute();
            }
        } );
        list.add( link );
        return list;
    }
}