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
import org.gwtbootstrap3.client.ui.ImageAnchor;
import org.gwtbootstrap3.client.ui.MediaList;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.ext.uberfire.social.activities.client.widgets.item.model.LinkCommandParams;
import org.ext.uberfire.social.activities.client.widgets.item.model.SocialItemExpandedWidgetModel;
import org.ext.uberfire.social.activities.client.widgets.timeline.regular.model.UpdateItem;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.resources.UberfireResources;
import org.uberfire.client.workbench.type.ClientResourceType;

public class SocialItemExpandedWidget extends Composite {

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

    private static final com.google.gwt.user.client.ui.Image GENERIC_FILE_IMAGE = new com.google.gwt.user.client.ui.Image( UberfireResources.INSTANCE.images().typeGenericFile() );

    @UiField
    FlowPanel left;

    @UiField
    Paragraph desc;

    @UiField
    MediaList items;

    interface MyUiBinder extends UiBinder<Widget, SocialItemExpandedWidget> {

    }

    public void init( SocialItemExpandedWidgetModel model ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        createItem( model );
    }

    public void createItem( SocialItemExpandedWidgetModel model ) {
        createFirstRow( model );
        for ( UpdateItem updateItem : model.getUpdateItems() ) {
            createSecondRow( model, updateItem );
        }

    }

    public void createFirstRow( SocialItemExpandedWidgetModel model ) {
        createIcon( model );
        createLink( model );

    }

    private void createIcon( final SocialItemExpandedWidgetModel model ) {
        UpdateItem updateItem = model.getUpdateItems().get( 0 );
        if ( updateItem.getEvent().isVFSLink() ) {
            MessageBuilder.createCall( new RemoteCallback<Path>() {
                public void callback( Path path ) {
                    for ( ClientResourceType type : model.getModel().getResourceTypes() ) {
                        if ( type.accept( path ) ) {
                            addIconImage( (Image) type.getIcon() );
                            break;
                        }
                    }
                }
            }, VFSService.class ).get( updateItem.getEvent().getLinkTarget() );

        } else {
            //TODO, provide icons per event type.
            addIconImage( GENERIC_FILE_IMAGE );
        }
    }

    private void addIconImage( final Image image ) {
        final ImageAnchor newImage = new ImageAnchor();
        newImage.setUrl( image.getUrl() );
        newImage.setAsMediaObject( true );
        left.add( newImage );
    }

    private void createLink( final SocialItemExpandedWidgetModel model ) {
        final UpdateItem updateItem = model.getUpdateItems().get( 0 );
        final Anchor link = new Anchor();
        link.setText( updateItem.getEvent().getLinkLabel() );
        link.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                model.getModel().getLinkCommand().execute( new LinkCommandParams( updateItem.getEvent().getType(),
                        updateItem.getEvent().getLinkTarget(),
                        updateItem.getEvent().getLinkType() )
                        .withLinkParams( updateItem.getEvent().getLinkParams() ) );
            }
        } );
        desc.add( link );
    }

    public void createSecondRow( final SocialItemExpandedWidgetModel model,
                                 final UpdateItem updateItem ) {

        MessageBuilder.createCall( new RemoteCallback<SocialUser>() {
            public void callback( final SocialUser socialUser ) {
                final CommentRowWidget row = GWT.create( CommentRowWidget.class );
                updateItem.setSocialUser( socialUser );
                updateItem.setUserClickCommand( model.getModel().getUserClickCommand() );
                updateItem.setFollowUnfollowCommand( model.getModel().getFollowUnfollowCommand() );
                updateItem.setLoggedUser( model.getModel().getSocialUser() );
                row.init( updateItem );
                items.add( row );
            }
        }, SocialUserRepositoryAPI.class ).findSocialUser( updateItem.getEvent().getSocialUser().getUserName() );

    }

}
