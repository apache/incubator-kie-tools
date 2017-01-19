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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.ImageAnchor;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.ioc.client.container.IOC;
import org.ext.uberfire.social.activities.client.user.SocialUserImageProvider;
import org.ext.uberfire.social.activities.client.widgets.item.bundle.SocialBundleHelper;
import org.ext.uberfire.social.activities.client.widgets.timeline.regular.model.UpdateItem;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialUserImageRepositoryAPI.ImageSize;

public class CommentRowWidget extends Composite {

    private final static DateTimeFormat FORMATTER = DateTimeFormat.getFormat( "dd/MM/yyyy HH:mm:ss" );

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

    @UiField
    FlowPanel left;

    @UiField
    Paragraph desc;

    @UiField
    Heading heading;

    SocialUserImageProvider imageProvider;

    interface MyUiBinder extends UiBinder<Widget, CommentRowWidget> {

    }

    public CommentRowWidget() {
        imageProvider = getSocialUserImageProvider();
    }

    SocialUserImageProvider getSocialUserImageProvider() {
        return IOC.getBeanManager().lookupBean( SocialUserImageProvider.class ).getInstance();
    }

    public void init( UpdateItem model ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        createItem( model );
    }

    public void createItem( UpdateItem updateItem ) {
        createThumbNail( updateItem );
        createAdditionalInfo( updateItem.getEvent() );
        createUserInfo( updateItem.getEvent().getSocialUser() );
    }

    private void createUserInfo( final SocialUser socialUser ) {
        heading.setText( socialUser.getName() );
    }

    private void createAdditionalInfo( SocialActivitiesEvent event ) {
        final StringBuilder comment = new StringBuilder();
        comment.append( SocialBundleHelper.getItemDescription( event.getAdicionalInfos() ) );
        comment.append( " " );
        comment.append( FORMATTER.format( event.getTimestamp() ) );
        comment.append( " " );
        if ( event.getDescription() != null && !event.getDescription().isEmpty() ) {
            comment.append( "\"" + event.getDescription() + "\"" );
        }
        desc.add( new Text( comment.toString() ) );
    }

    void createThumbNail( final UpdateItem updateItem ) {
        final SocialUser socialUser = updateItem.getEvent().getSocialUser();
        final Image userImage = imageProvider.getImageForSocialUser( socialUser, ImageSize.SMALL );
        final ImageAnchor newImage = GWT.create( ImageAnchor.class );
        newImage.setUrl( userImage.getUrl() );
        newImage.setAsMediaObject( true );
        newImage.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent clickEvent ) {
                updateItem.getUserClickCommand().execute( socialUser.getUserName() );
            }
        } );
        left.add( newImage );
    }

}
