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

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.mvp.ParameterizedCommand;

public class UserBoxDetailsView extends PopupPanel {

    interface Mybinder
            extends
            UiBinder<Widget, UserBoxDetailsView> {

    }

    private static Mybinder uiBinder = GWT.create( Mybinder.class );

    @UiField
    FlowPanel popup;
    @UiField
    FlowPanel userBoxPanel;
    @UiField
    FlowPanel followLink;

    public UserBoxDetailsView( final Widget widgetToPosition,
                               SocialUser socialUser,
                               UserBoxView.RelationType type,
                               Image userImage,
                               ParameterizedCommand<String> onClick,
                               ParameterizedCommand<String> followUnfollowCommand ) {
        super( true );
        add( uiBinder.createAndBindUi( this ) );
        setupPosition( widgetToPosition );
        onMouseOutHidePopup();
        setupUserBox( socialUser, userImage, onClick );
        setupFollowUnfollow( socialUser, type, userImage, onClick, followUnfollowCommand );
    }


    private void setupFollowUnfollow( final SocialUser socialUser,
                                      UserBoxView.RelationType type,
                                      Image userImage,
                                      final ParameterizedCommand<String> onClick,
                                      final ParameterizedCommand<String> followUnfollowCommand ) {
            Button followUnfollow = GWT.create( Button.class );
            followUnfollow.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    followUnfollowCommand.execute( socialUser.getUserName() );
                    hide();
                }
            } );
            followUnfollow.setText( type.label() );
            followUnfollow.setType( ButtonType.INFO );
            followUnfollow.setSize( ButtonSize.MINI );
            followLink.add( followUnfollow );
    }

    private void setupUserBox( final SocialUser socialUser,
                               Image maybeAlreadyAttachedImage,
                               final ParameterizedCommand<String> onClick ) {
        Image newImage = new Image( maybeAlreadyAttachedImage.getUrl(), maybeAlreadyAttachedImage.getOriginLeft(), maybeAlreadyAttachedImage.getOriginTop(), maybeAlreadyAttachedImage.getWidth(), maybeAlreadyAttachedImage.getHeight() );
        newImage.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                onClick.execute( socialUser.getUserName() );
            }
        } );
        userBoxPanel.add( newImage );
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


    private void setupPosition( final Widget followUnfollow ) {
        this.setPopupPositionAndShow( new PositionCallback() {
            public void setPosition( int offsetWidth,
                                     int offsetHeight ) {
                int borderLeft = 4;
                int left = followUnfollow.getAbsoluteLeft() + borderLeft;
                int borderTop = 10;
                int top = followUnfollow.getAbsoluteTop() - borderTop;
                setPopupPosition( left, top );
            }
        } );
    }

    private void onMouseOutHidePopup() {
        this.addDomHandler( new MouseOutHandler() {
            public void onMouseOut( MouseOutEvent event ) {
                hide();
            }
        }, MouseOutEvent.getType() );
    }

}