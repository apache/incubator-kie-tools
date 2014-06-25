package org.kie.uberfire.social.activities.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.Thumbnail;
import com.github.gwtbootstrap.client.ui.Thumbnails;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FocusPanel;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.kie.uberfire.social.activities.client.AppResource;
import org.kie.uberfire.social.activities.client.gravatar.GravatarImage;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialUser;

public class SimpleItemWidget {

    public static FluidRow createRow( SocialActivitiesEvent event,
                                      final PlaceManager placeManager ) {
        FluidRow row = GWT.create( FluidRow.class );
        row.add( createThumbNail( event.getSocialUser() ) );

        row.add( createLink( event, placeManager ) );

        row.add( createText( event ) );

        return row;
    }

    private static Column createText( SocialActivitiesEvent event ) {
        Column column = new Column( 8) ;
        StringBuilder sb = new StringBuilder();
        sb.append( event.getAdicionalInfos() + " " );
        sb.append( SocialDateFormatter.format( event.getTimestamp() ) );
        column.add( new Paragraph( sb.toString() ) );
        return column;
    }

    private static Column createLink( SocialActivitiesEvent event,
                                      final PlaceManager placeManager ) {
        Column column = new Column( 2 );
        FocusPanel panel = createFocusPanel( placeManager );

        NavList list = new NavList();
        NavLink link = new NavLink();
        link.setText( event.getSocialUser().getName() );
        list.add( link );
        panel.add( list );
        column.add( panel );
        return column;
    }

    private static FocusPanel createFocusPanel( final PlaceManager placeManager ) {
        FocusPanel panel = new FocusPanel();
        panel.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put( "alias", "repo4" );
                placeManager.goTo( new DefaultPlaceRequest( "RepositoryEditor", parameters ) );
            }
        } );
        panel.addFocusHandler( new FocusHandler() {
            @Override
            public void onFocus( FocusEvent event ) {

            }
        } );
        return panel;
    }

    private static Column createThumbNail( SocialUser socialUser ) {
        Column column = new Column( 2, 0 );
        Thumbnails tumThumbnails = new Thumbnails();
        Thumbnail t = new Thumbnail();
        Image userImage;
        if ( socialUser.getEmail().isEmpty() ) {
            userImage = generateDefaultImage();
        }
        else{
            userImage = generateGravatarImage(socialUser);
        }
        userImage.setSize( "30px", "30px" );
        t.add( userImage );
        tumThumbnails.add( t );
        column.add( tumThumbnails );
        return column;
    }

    private static Image generateGravatarImage( SocialUser socialUser ) {
        Image gravatarImage= new Image(  new GravatarImage( socialUser.getEmail(), 30 ).getUrl() );
        return gravatarImage;
    }

    private static Image generateDefaultImage() {
        ImageResource imageResource = AppResource.INSTANCE.images().genericAvatar();
        Image userImage = new Image( imageResource );
        return userImage;
    }

}
