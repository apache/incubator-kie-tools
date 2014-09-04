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
import com.google.gwt.user.client.ui.SimplePanel;
import org.kie.uberfire.social.activities.client.gravatar.GravatarBuilder;
import org.kie.uberfire.social.activities.client.widgets.item.model.SimpleItemWidgetModel;
import org.kie.uberfire.social.activities.client.widgets.utils.SocialDateFormatter;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.type.ClientResourceType;

public class SimpleItemWidget {

    public static FluidRow createRow( SimpleItemWidgetModel model ) {
        FluidRow row = GWT.create( FluidRow.class );
        if ( !model.shouldIPrintIcon() ) {
            row.add( createThumbNail( model.getSocialUser() ) );
        } else {
            row.add( createIcon( model ) );
        }

        if ( model.getLinkText() != null ) {
            row.add( createLink( model ) );
        } else {
            row.add( new Paragraph( model.getDescription() ) );
        }
        row.add( createText( model ) );

        return row;
    }

    private static Column createText( SimpleItemWidgetModel model ) {
        Column column = new Column( 4 );
        StringBuilder sb = new StringBuilder();
        sb.append( model.getItemDescription() );
        sb.append( SocialDateFormatter.format( model.getTimestamp() ) );
        column.add( new Paragraph( sb.toString() ) );
        return column;
    }

    private static Column createIcon( final SimpleItemWidgetModel model ) {
        final Column column = new Column( 2 );
        for ( ClientResourceType type : model.getResourceTypes() ) {
            if ( type.accept( model.getLinkPath() ) ) {
                com.google.gwt.user.client.ui.Image maybeAlreadyAttachedImage = ( com.google.gwt.user.client.ui.Image ) type.getIcon();
                Image newImage = new Image( maybeAlreadyAttachedImage.getUrl(),maybeAlreadyAttachedImage.getOriginLeft(), maybeAlreadyAttachedImage.getOriginTop(),maybeAlreadyAttachedImage.getWidth(),maybeAlreadyAttachedImage.getHeight() );
                column.add( newImage );
                break;
            }
        }
        return column;
    }

    private static Column createLink( final SimpleItemWidgetModel model ) {
        Column column = new Column( 4 );
        SimplePanel panel = new SimplePanel();

        NavList list = new NavList();
        NavLink link = new NavLink();
        link.setText( model.getLinkText() );
        link.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                PlaceManager placeManager = model.getPlaceManager();
                placeManager.goTo( model.getLinkPath() );
            }
        } );
        list.add( link );
        panel.add( list );
        column.add( panel );
        return column;
    }

    private static Column createThumbNail( SocialUser socialUser ) {
        Column column = new Column( 2, 0 );
        Thumbnails tumThumbnails = new Thumbnails();
        Thumbnail t = new Thumbnail();
        Image userImage;
        userImage = GravatarBuilder.generate( socialUser, GravatarBuilder.SIZE.SMALL );
        userImage.setSize( "30px", "30px" );
        t.add( userImage );
        tumThumbnails.add( t );
        column.add( tumThumbnails );
        return column;
    }

}
