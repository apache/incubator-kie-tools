/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client.widget;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.workbench.common.services.security.AppRoles;
import org.kie.workbench.common.widgets.metadata.client.resources.ImageResources;
import org.kie.workbench.common.widgets.metadata.client.resources.i18n.MetadataConstants;
import org.uberfire.client.common.DecoratedDisclosurePanel;
import org.uberfire.client.common.DirtyableComposite;
import org.uberfire.client.common.SmallLabel;
import org.uberfire.security.Identity;

import static com.google.gwt.user.client.ui.HasHorizontalAlignment.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * Does the discussion panel for artifacts.
 */
public class DiscussionWidget
        extends DirtyableComposite {

    private final Identity identity;
    private VerticalPanel commentList = new VerticalPanel();
    private VerticalPanel newCommentLayout = new VerticalPanel();
    private Metadata metadata;
    private boolean readOnly;

    public DiscussionWidget( final Metadata metadata,
                             final boolean readOnly ) {
        this.metadata = checkNotNull( "metadata", metadata );
        this.readOnly = readOnly;
        identity = IOC.getBeanManager().lookupBean( Identity.class ).getInstance();

        final DecoratedDisclosurePanel discussionPanel = new DecoratedDisclosurePanel( MetadataConstants.INSTANCE.Discussion() );
        discussionPanel.setWidth( "100%" );

        commentList.setWidth( "100%" );
        VerticalPanel discussionLayout = new VerticalPanel();
        discussionLayout.setWidth( "90%" );

        newCommentLayout.setWidth( "100%" );
        updateCommentList();
        showNewCommentButton();

        discussionLayout.add( newCommentLayout );
        discussionLayout.add( commentList );

        discussionPanel.setContent( discussionLayout );

        //TODO {porcelli} event observer? or just use ERRAI: http://docs.jboss.org/errai/2.2.0.CR2/errai/reference/html_single/#sid-5931280_MessagingAPIBasics-ReceivingMessagesontheClientBus%2FClientServices

        initWidget( discussionPanel );
    }

    private void updateCommentList() {
        commentList.clear();
        for ( final DiscussionRecord dr : metadata.getDiscussion() ) {
            appendComment( dr );
        }
    }

    private Widget appendComment( final DiscussionRecord r ) {
        final SmallLabel hrd = new SmallLabel( MetadataConstants.INSTANCE.smallCommentBy0On1Small( r.getAuthor(), new Date( r.getTimestamp() ) ) );
        hrd.addStyleName( "discussion-header" );
        commentList.add( hrd );

        final String[] parts = r.getNote().split( "\n" );

        if ( parts.length > 0 ) {
            final StringBuilder txtBuilder = new StringBuilder();
            for ( int i = 0; i < parts.length; i++ ) {
                txtBuilder.append( parts[ i ] );
                if ( i != parts.length - 1 ) {
                    txtBuilder.append( "<br/>" );
                }
            }
            final HTML hth = new HTML( txtBuilder.toString() );
            hth.setStyleName( "form-field" );
            commentList.add( hth );
        } else {
            final Label lbl = new Label( r.getNote() );
            lbl.setStyleName( "form-field" );
            commentList.add( lbl );
        }

        commentList.add( new HTML( "<br/>" ) );
        return hrd;
    }

    private void showNewCommentButton() {
        newCommentLayout.clear();

        final HorizontalPanel hp = new HorizontalPanel();

        final Button createNewComment = new Button( MetadataConstants.INSTANCE.AddADiscussionComment() );
        createNewComment.setEnabled( !this.readOnly );
        hp.add( createNewComment );

        if ( identity.hasRole( AppRoles.ADMIN ) ) {
            final Button adminClearAll = new Button( MetadataConstants.INSTANCE.EraseAllComments() );
            adminClearAll.setEnabled( !readOnly );
            hp.add( adminClearAll );
            adminClearAll.addClickHandler( new ClickHandler() {
                public void onClick( final ClickEvent sender ) {
                    if ( Window.confirm( MetadataConstants.INSTANCE.EraseAllCommentsWarning() ) ) {
                        metadata.eraseDiscussion();
                        makeDirty();
                        updateCommentList();
                    }
                }
            } );
        }

        newCommentLayout.add( hp );

        newCommentLayout.setCellHorizontalAlignment( hp, ALIGN_RIGHT );
        createNewComment.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent sender ) {
                showAddNewComment();
            }
        } );
    }

    private void showAddNewComment() {
        newCommentLayout.clear();
        final TextArea comment = new TextArea();
        comment.setWidth( "100%" );
        newCommentLayout.add( comment );

        Button ok = new Button( MetadataConstants.INSTANCE.OK() );
        Button cancel = new Button( MetadataConstants.INSTANCE.Cancel() );

        ok.addClickHandler( new ClickHandler() {
            public void onClick( final ClickEvent sender ) {
                sendNewComment( comment.getText() );
            }
        } );

        cancel.addClickHandler( new ClickHandler() {
            public void onClick( final ClickEvent sender ) {
                showNewCommentButton();
            }
        } );

        final HorizontalPanel hp = new HorizontalPanel();
        hp.add( ok );
        hp.add( cancel );

        newCommentLayout.add( hp );

        comment.setFocus( true );
    }

    private void sendNewComment( final String text ) {
        newCommentLayout.clear();
        newCommentLayout.add( new Image( ImageResources.INSTANCE.spinner() ) );

        MessageBuilder.createCall( new RemoteCallback<Long>() {
            public void callback( final Long timestamp ) {
                showNewCommentButton();
                metadata.addDiscussion( new DiscussionRecord( timestamp, identity.getName(), text ) );
                makeDirty();
                updateCommentList();
            }
        }, AppConfigService.class ).getTimestamp();
    }
}
