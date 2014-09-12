package org.kie.uberfire.social.activities.client.widgets.item;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.social.activities.client.gravatar.GravatarBuilder;
import org.kie.uberfire.social.activities.client.widgets.timeline.regular.model.UpdateItem;
import org.kie.uberfire.social.activities.client.widgets.userbox.UserBoxView;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialUser;

public class CommentRowWidget extends Composite {

    private final static DateTimeFormat FORMATTER = DateTimeFormat.getFormat( "dd/MM/yyyy HH:mm:ss" );

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

    @UiField
    Column thumbnail;

    @UiField
    Column addInfo;

    interface MyUiBinder extends UiBinder<Widget, CommentRowWidget> {

    }

    public void init( UpdateItem model ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        createItem( model );
    }

    public void createItem( UpdateItem updateItem ) {

        createThumbNail( updateItem );
        createAdditionalInfo( updateItem.getEvent() );
    }

    private void createAdditionalInfo( SocialActivitiesEvent event ) {
        StringBuilder comment = new StringBuilder();
        comment.append( event.getAdicionalInfos() );
        comment.append( " " );
        comment.append( FORMATTER.format( event.getTimestamp() ) );
        comment.append( " " );
        if ( !event.getDescription().isEmpty() ) {
            comment.append( "\"" + event.getDescription() + "\"" );
        }
        addInfo.add( new Paragraph( comment.toString() ) );
    }

    private void createThumbNail( UpdateItem updateItem ) {

        UserBoxView followerView = GWT.create( UserBoxView.class );
        SocialUser socialUser = updateItem.getEvent().getSocialUser();
        Image userImage = GravatarBuilder.generate( socialUser, GravatarBuilder.SIZE.MICRO );
        followerView.init( socialUser, userImage, updateItem.getUserClickCommand() );
        thumbnail.add( followerView );
    }

}
