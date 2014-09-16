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

package org.kie.uberfire.social.activities.client.widgets.userbox;

import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class UserBoxView extends Composite {

    interface HeaderViewBinder
            extends
            UiBinder<Widget, UserBoxView> {

    }

    private static HeaderViewBinder uiBinder = GWT.create( HeaderViewBinder.class );

    @UiField
    FlowPanel userBoxPanel;
    @UiField
    FlowPanel followLink;
    @UiField
    FlowPanel panel;

    public void init( final SocialUser socialUser,
                      RelationType type,
                      Image userImage,
                      final ParameterizedCommand<String> onClick,
                      final ParameterizedCommand<String> followUnfollowCommand ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        setupUserBox( socialUser, userImage, onClick );
        setupFollowUnfollow( socialUser, type, userImage, onClick, followUnfollowCommand );
    }

    private void setupFollowUnfollow( final SocialUser socialUser,
                                      RelationType type,
                                      Image userImage,
                                      final ParameterizedCommand<String> onClick,
                                      final ParameterizedCommand<String> followUnfollowCommand ) {
        if ( type != RelationType.ME ) {
            panel.addDomHandler( new MouseEventHandler( socialUser, type, userImage, onClick, followUnfollowCommand ), MouseOverEvent.getType() );
        }
    }

    private void setupUserBox( final SocialUser socialUser,
                               Image userImage,
                               final ParameterizedCommand<String> onClick ) {
        userImage.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                onClick.execute( socialUser.getUserName() );
            }
        } );
        userBoxPanel.add( userImage );
        userBoxPanel.add( createLink( socialUser, onClick ) );
        userBoxPanel.asWidget();
    }

    private NavList createLink( final SocialUser follower,
                                final ParameterizedCommand<String> command ) {
        NavList list = new NavList();
        NavLink link = new NavLink();
        link.setText( follower.getUserName() );
        link.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                command.execute( follower.getUserName() );
            }
        } );
        list.add( link );
        return list;
    }

    class MouseEventHandler implements MouseOverHandler {

        private final SocialUser socialUser;
        private final RelationType type;
        private final Image userImage;
        private final ParameterizedCommand<String> onClick;
        private final ParameterizedCommand<String> followUnfollowCommand;

        public MouseEventHandler( final SocialUser socialUser,
                                  RelationType type,
                                  Image userImage,
                                  final ParameterizedCommand<String> onClick,
                                  final ParameterizedCommand<String> followUnfollowCommand ) {
            this.socialUser = socialUser;
            this.type = type;
            this.userImage = userImage;
            this.onClick = onClick;
            this.followUnfollowCommand = followUnfollowCommand;
        }

        public void onMouseOver( final MouseOverEvent moe ) {
            new UserBoxDetailsView( asWidget(), socialUser, type, userImage, onClick, followUnfollowCommand ).show();
        }
    }

    public enum RelationType {
        CAN_FOLLOW( "Follow" ), UNFOLLOW( "Unfollow" ), ME;

        private String label;

        RelationType() {

        }

        public String label() {
            return label;
        }

        RelationType( String label ) {
            this.label = label;
        }
    }
}