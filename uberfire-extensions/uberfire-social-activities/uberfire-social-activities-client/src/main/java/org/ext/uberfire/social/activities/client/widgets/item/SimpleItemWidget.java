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

package org.ext.uberfire.social.activities.client.widgets.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.ImageAnchor;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.ioc.client.container.IOC;
import org.ext.uberfire.social.activities.client.user.SocialUserImageProvider;
import org.ext.uberfire.social.activities.client.widgets.item.bundle.SocialBundleHelper;
import org.ext.uberfire.social.activities.client.widgets.item.model.LinkCommandParams;
import org.ext.uberfire.social.activities.client.widgets.item.model.SimpleItemWidgetModel;
import org.ext.uberfire.social.activities.client.widgets.utils.SocialDateFormatter;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialUserImageRepositoryAPI.ImageSize;
import org.uberfire.client.resources.UberfireResources;
import org.uberfire.client.workbench.type.ClientResourceType;

public class SimpleItemWidget extends Composite {

    @UiField
    FlowPanel left;

    @UiField
    Heading heading;

    @UiField
    Paragraph desc;

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

    private static final Image GENERIC_FILE_IMAGE = new Image( UberfireResources.INSTANCE.images().typeGenericFile() );

    private SocialUserImageProvider imageProvider;

    public SimpleItemWidget() {
        imageProvider = IOC.getBeanManager().lookupBean( SocialUserImageProvider.class ).getInstance();
    }

    interface MyUiBinder extends UiBinder<Widget, SimpleItemWidget> {

    }

    public void init( SimpleItemWidgetModel model ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        if ( !model.shouldIPrintIcon() ) {
            createThumbNail( model.getSocialUser() );
        } else {
            createIcon( model );
        }
        createDescriptionContent( model );
        createHeadingContent( model );
    }

    private void createHeadingContent( final SimpleItemWidgetModel model ) {
        heading.setText( model.getSocialUser().getName() );
    }

    private void createDescriptionContent( SimpleItemWidgetModel model ) {
        if ( model.getLinkText() != null ) {
            desc.add( createLink( model ) );
            desc.add( createText( model ) );
        } else {
            desc.setText( model.getDescription() );
        }
    }

    private Widget createText( SimpleItemWidgetModel model ) {
        final StringBuilder sb = new StringBuilder( " " );
        sb.append( SocialBundleHelper.getItemDescription( model.getItemDescription() ) );
        sb.append( " " );
        sb.append( SocialDateFormatter.format( model.getTimestamp() ) );
        return new Text( sb.toString() );
    }

    private void createIcon( final SimpleItemWidgetModel model ) {
        if ( model.isVFSLink() ) {
            for ( ClientResourceType type : model.getResourceTypes() ) {
                if ( type.accept( model.getLinkPath() ) ) {
                    addIconImage( (Image) type.getIcon() );
                    break;
                }
            }
        } else {
            final Image maybeAlreadyAttachedImage = GENERIC_FILE_IMAGE;
            addIconImage( maybeAlreadyAttachedImage );
        }
    }

    private void addIconImage( final Image image ) {
        final ImageAnchor newImage = new ImageAnchor();
        newImage.setUrl( image.getUrl() );
        newImage.setPull( Pull.LEFT );
        newImage.setAsMediaObject( true );
        left.add( newImage );
    }

    private Widget createLink( final SimpleItemWidgetModel model ) {
        final Anchor link = new Anchor();
        link.setText( model.getLinkText() );
        link.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                model.getLinkCommand().execute( new LinkCommandParams( model.getEventType(),
                        model.getLinkURI(),
                        model.getLinkType() )
                        .withLinkParams( model.getLinkParams() ) );
            }
        } );
        return link;
    }

    private void createThumbNail( final SocialUser socialUser ) {
        final Image userImage = imageProvider.getImageForSocialUser( socialUser, ImageSize.SMALL );
        addIconImage( userImage );
    }

}
