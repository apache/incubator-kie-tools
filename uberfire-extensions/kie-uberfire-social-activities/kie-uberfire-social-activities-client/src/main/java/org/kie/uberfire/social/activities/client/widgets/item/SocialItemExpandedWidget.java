package org.kie.uberfire.social.activities.client.widgets.item;

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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.SimplePanel;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.social.activities.client.gravatar.GravatarBuilder;
import org.kie.uberfire.social.activities.client.widgets.item.model.SocialItemExpandedWidgetModel;
import org.kie.uberfire.social.activities.client.widgets.timeline.regular.model.UpdateItem;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.type.ClientResourceType;

public class SocialItemExpandedWidget {

    private final static DateTimeFormat FORMATTER = DateTimeFormat.getFormat( "dd/MM/yyyy HH:mm:ss" );

    public static void createItem( SocialItemExpandedWidgetModel model ) {
        model.getItemsPanel().add( createFirstRow( model ) );
        for ( UpdateItem updateItem : model.getUpdateItems() ) {
            model.getItemsPanel().add( createSecondRow( updateItem ) );
        }

    }

    public static FluidRow createFirstRow(
            SocialItemExpandedWidgetModel model ) {
        FluidRow row = GWT.create( FluidRow.class );

        row.add( createIcon( model ) );
        row.add( createLink( model ) );

        return row;
    }

    private static Column createIcon( final SocialItemExpandedWidgetModel model ) {
        final Column column = new Column( 2 );

        MessageBuilder.createCall( new RemoteCallback<Path>() {
            public void callback( Path path ) {
                for ( ClientResourceType type : model.getModel().getResourceTypes() ) {
                    if ( type.accept( path ) ) {
                        column.add( type.getIcon() );
                        break;
                    }
                }
            }
        }, VFSService.class ).get( model.getUpdateItems().get( 0 ).getEvent().getLinkTarget() );
        return column;
    }

    private static Column createLink( final SocialItemExpandedWidgetModel model ) {
        final UpdateItem updateItem = model.getUpdateItems().get( 0 );
        Column column = new Column( 10 );
        SimplePanel panel = new SimplePanel();
        NavList list = new NavList();
        NavLink link = new NavLink();
        link.setText( updateItem.getEvent().getLinkLabel() );
        link.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                MessageBuilder.createCall( new RemoteCallback<Path>() {
                    public void callback( Path path ) {
                        PlaceManager placeManager = model.getModel().getPlaceManager();
                        placeManager.goTo( path );
                    }
                }, VFSService.class ).get( updateItem.getEvent().getLinkTarget() );
            }
        } );
        list.add( link );
        panel.add( list );
        column.add( panel );
        return column;
    }

    public static FluidRow createSecondRow( UpdateItem updateItem ) {
        FluidRow row = GWT.create( FluidRow.class );
        row.add( createThumbNail( updateItem.getEvent().getSocialUser() ) );
        row.add( createSocialUserName( updateItem.getEvent() ) );
        row.add( createAdditionalInfo( updateItem.getEvent() ) );

        return row;
    }

    private static Column createAdditionalInfo( SocialActivitiesEvent event ) {
        Column column;
        column = new Column( 10 );
        StringBuilder comment = new StringBuilder();
        comment.append( event.getAdicionalInfos() );
        comment.append( " " );
        comment.append( FORMATTER.format( event.getTimestamp() ) );
        column.add( new Paragraph( comment.toString() ) );
        return column;
    }

    private static Column createSocialUserName( SocialActivitiesEvent event ) {
        Column column = new Column( 1 );
        NavList list = new NavList();
        NavLink link = new NavLink();
        link.setText( event.getSocialUser().getUserName() );
        list.add( link );
        column.add( list );
        return column;
    }

    private static Column createThumbNail( SocialUser socialUser ) {
        Column column = new Column( 1 );
        Thumbnails tumThumbnails = new Thumbnails();
        Thumbnail t = new Thumbnail();
        Image userImage;
        userImage = GravatarBuilder.generate( socialUser, GravatarBuilder.SIZE.SMALL );
        t.add( userImage );
        tumThumbnails.add( t );
        column.add( tumThumbnails );
        return column;
    }

}
