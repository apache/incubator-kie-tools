package org.kie.uberfire.social.activities.client.widgets.item;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.Thumbnail;
import com.github.gwtbootstrap.client.ui.Thumbnails;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.social.activities.client.gravatar.GravatarBuilder;
import org.kie.uberfire.social.activities.client.widgets.item.model.SimpleItemWidgetModel;
import org.kie.uberfire.social.activities.client.widgets.utils.SocialDateFormatter;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.type.ClientResourceType;

public class SimpleItemWidget extends Composite {

    @UiField
    Column icon;

    @UiField
    Column link;

    @UiField
    Column desc;

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

    interface MyUiBinder extends UiBinder<Widget, SimpleItemWidget> {

    }

    public void init( SimpleItemWidgetModel model ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        if ( !model.shouldIPrintIcon() ) {
            createThumbNail( model.getSocialUser() );
        } else {
            createIcon( model );
        }
        createColumnContent( model ) ;
    }

    private void createColumnContent( SimpleItemWidgetModel model ) {
        if ( model.getLinkText() != null ) {
            link.add( createLink( model ) );
        } else {
            link.add( new Paragraph( model.getDescription() ) );
        }
        desc.add( createText( model ) );
    }

    private Paragraph createText( SimpleItemWidgetModel model ) {
        StringBuilder sb = new StringBuilder();
        sb.append( model.getItemDescription() );
        sb.append( SocialDateFormatter.format( model.getTimestamp() ) );
        return new Paragraph( sb.toString() );
    }

    private void createIcon( final SimpleItemWidgetModel model ) {
        for ( ClientResourceType type : model.getResourceTypes() ) {
            if ( type.accept( model.getLinkPath() ) ) {
                com.google.gwt.user.client.ui.Image maybeAlreadyAttachedImage = (com.google.gwt.user.client.ui.Image) type.getIcon();
                Image newImage = new Image( maybeAlreadyAttachedImage.getUrl(), maybeAlreadyAttachedImage.getOriginLeft(), maybeAlreadyAttachedImage.getOriginTop(), maybeAlreadyAttachedImage.getWidth(), maybeAlreadyAttachedImage.getHeight() );
                icon.add( newImage );
                break;
            }
        }
    }

    private NavList createLink( final SimpleItemWidgetModel model ) {
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
        return list;
    }

    private void createThumbNail( SocialUser socialUser ) {
        Thumbnails tumThumbnails = new Thumbnails();
        Thumbnail t = new Thumbnail();
        Image userImage;
        userImage = GravatarBuilder.generate( socialUser, GravatarBuilder.SIZE.SMALL );
        userImage.setSize( "30px", "30px" );
        t.add( userImage );
        tumThumbnails.add( t );
        icon.add( tumThumbnails );
    }

}
