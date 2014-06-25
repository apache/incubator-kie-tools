package org.kie.uberfire.social.activities.client.widgets;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.security.Identity;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialUserServiceAPI;

@Dependent
public class SocialRelationsWidget extends Composite {

    @UiField
    TextBox filterBox;

    @UiField
    Button follow;

    @UiField
    Button unfollow;

    @UiField
    FlowPanel items;

    @Inject
    private Identity loggedUser;

    private SocialUser socialLoggedUser;

    @PostConstruct
    public void setup() {
        initWidget( uiBinder.createAndBindUi( this ) );
        updateWidget();
    }

    private void updateWidget() {
        MessageBuilder.createCall( new RemoteCallback<SocialUser>() {
            public void callback( SocialUser user ) {
                socialLoggedUser = user;
                createWidget();
            }
        }, SocialUserRepositoryAPI.class ).findSocialUser( loggedUser.getName() );
    }

    private void createWidget() {
        items.clear();
        items.add( new Label( "Social Relations Widget - User: " + loggedUser.getName() ) );
        printAllFollowing();
        printAllUsers();
    }

    private void printAllFollowers() {
        items.add( new Label( "All followers: " ) );
        for ( String followers : socialLoggedUser.getFollowersName() ) {
            items.add( new Label( followers ) );
        }
    }

    private void printAllFollowing() {
        items.add( new Label( "All following: " ) );
        if ( socialLoggedUser != null ) {
            for ( String following : socialLoggedUser.getFollowingName() ) {
                items.add( new Label( following ) );
            }
        }

    }

    private void printAllUsers() {
        items.add( new Label( "All users: " ) );
        MessageBuilder.createCall( new RemoteCallback<List<SocialUser>>() {
            public void callback( List<SocialUser> users ) {
                for ( SocialUser user : users ) {
                    items.add( new Label( user.getName() ) );
                }
            }
        }, SocialUserRepositoryAPI.class ).findAllUsers();
    }

    @UiHandler("follow")
    void follow( ClickEvent e ) {
        MessageBuilder.createCall( new RemoteCallback<SocialUser>() {
            public void callback( SocialUser user ) {
                updateWidget();
            }
        }, SocialUserServiceAPI.class ).userFollowAnotherUser( socialLoggedUser.getName(), filterBox.getText() );
    }

    @UiHandler("unfollow")
    void unfollow( ClickEvent e ) {
        MessageBuilder.createCall( new RemoteCallback<SocialUser>() {
            public void callback( SocialUser user ) {
                updateWidget();
            }
        }, SocialUserServiceAPI.class ).userUnfollowAnotherUser( socialLoggedUser.getName(), filterBox.getText() );
    }

    interface MyUiBinder extends UiBinder<Widget, SocialRelationsWidget> {

    }

    static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}
