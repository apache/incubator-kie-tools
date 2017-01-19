/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.ext.uberfire.social.activities.client.widgets.relations;

import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Badge;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.ext.uberfire.social.activities.client.resources.i18n.Constants;
import org.ext.uberfire.social.activities.client.user.SocialUserImageProvider;
import org.ext.uberfire.social.activities.client.widgets.utils.FollowButton;
import org.ext.uberfire.social.activities.client.widgets.utils.FollowButton.FollowType;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialUserImageRepositoryAPI.ImageSize;
import org.ext.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialUserServiceAPI;
import org.ext.uberfire.social.activities.utils.SocialUserNameComparator;
import org.uberfire.mvp.Command;

@Dependent
public class SocialRelationsWidget extends Composite {

    private final Constants constants = Constants.INSTANCE;

    @UiField
    Heading title;

    @UiField
    ListGroup allfollowing;

    @UiField
    ListGroup allfollowers;

    @UiField
    ListGroup allusers;

    @UiField
    Badge followingBadge;

    @UiField
    Badge followersBadge;

    @UiField
    Badge allusersBadge;

    @Inject
    private User loggedUser;

    private SocialUser socialLoggedUser;

    @Inject
    private SocialUserImageProvider imageProvider;

    @PostConstruct
    public void setup() {
        initWidget( uiBinder.createAndBindUi( this ) );
        updateWidget();
    }

    public void updateWidget() {
        MessageBuilder.createCall( new RemoteCallback<SocialUser>() {
            public void callback( SocialUser user ) {
                socialLoggedUser = user;
                title.setText(constants.SocialUser() + ": " + socialLoggedUser.getName());
                printAllUsers();
            }
        }, SocialUserRepositoryAPI.class ).findSocialUser( loggedUser.getIdentifier() );
    }

    private void printAllFollowers( final List<SocialUser> users ) {
        allfollowers.clear();
        followersBadge.setText( String.valueOf( socialLoggedUser.getFollowersName().size() ) );
        if ( socialLoggedUser.getFollowersName().isEmpty() ) {
            final ListGroupItem user = new ListGroupItem();
            user.setText(constants.CurrentlyNotFollowedByAnyUser());
            allfollowers.add( user );
        } else {
            for ( final SocialUser follower : users ) {
                if ( socialLoggedUser.getFollowersName().contains( follower.getUserName() ) ) {
                    allfollowers.add( createUserItem( follower, false ) );
                }
            }
        }
    }

    private void printAllFollowing( final List<SocialUser> users ) {
        allfollowing.clear();
        followingBadge.setText( String.valueOf( socialLoggedUser.getFollowingName().size() ) );
        if ( socialLoggedUser.getFollowingName().isEmpty() ) {
            final ListGroupItem user = new ListGroupItem();
            user.setText(constants.CurrentlyNotFollowingAnyUser());
            allfollowing.add( user );
        } else {
            for ( final SocialUser following : users ) {
                if ( socialLoggedUser.getFollowingName().contains( following.getUserName() ) ) {
                    allfollowing.add( createUserItem( following, true ) );
                }
            }
        }

    }

    private void printAllUsers() {
        MessageBuilder.createCall( new RemoteCallback<List<SocialUser>>() {
            public void callback( final List<SocialUser> users ) {
                allusers.clear();
                allusersBadge.setText( String.valueOf( users.size() ) );
                Collections.sort( users, new SocialUserNameComparator() );
                for ( final SocialUser socialUser : users ) {
                    allusers.add( createUserItem( socialUser, socialUser.getFollowersName().contains( socialLoggedUser.getUserName() ) ) );
                }
                printAllFollowing( users );
                printAllFollowers( users );
            }
        }, SocialUserRepositoryAPI.class ).findAllUsers();
    }

    private ListGroupItem createUserItem( final SocialUser socialUser, final boolean follow ) {
        final ListGroupItem user = new ListGroupItem();
        final Image userImage = imageProvider.getImageForSocialUser( socialUser, ImageSize.SMALL );
        userImage.addStyleName( "img-circle" );
        userImage.addStyleName( Styles.PULL_LEFT );
        user.getWidget( 0 ).getElement().getStyle().setPaddingLeft( 10, Style.Unit.PX );
        user.add( userImage );
        user.setText( socialUser.getName() );
        if ( socialUser.getUserName().equals( socialLoggedUser.getUserName() ) == false ) {
            user.add( createFollowButton( socialUser, follow ) );
        }
        return user;
    }

    private Button createFollowButton( final SocialUser socialUser, final boolean follow ) {
        final RemoteCallback<SocialUser> callback = new RemoteCallback<SocialUser>() {
            @Override
            public void callback( final SocialUser socialUser ) {
                updateWidget();
            }
        };

        final FollowType followType = follow ? FollowType.UNFOLLOW : FollowType.FOLLOW;

        final FollowButton followButton = new FollowButton( followType, new Command() {
            @Override
            public void execute() {
                if ( follow ) {
                    MessageBuilder.createCall( callback, SocialUserServiceAPI.class ).userUnfollowAnotherUser( socialLoggedUser.getUserName(), socialUser.getUserName() );
                } else {
                    MessageBuilder.createCall( callback, SocialUserServiceAPI.class ).userFollowAnotherUser( socialLoggedUser.getUserName(), socialUser.getUserName() );
                }
            }
        } );
        followButton.addStyleName( Styles.PULL_RIGHT );
        return followButton;
    }

    interface MyUiBinder extends UiBinder<Widget, SocialRelationsWidget> {

    }

    static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}
