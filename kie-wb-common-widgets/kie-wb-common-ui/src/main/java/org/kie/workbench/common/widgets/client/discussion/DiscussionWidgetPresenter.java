/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.discussion;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;

public class DiscussionWidgetPresenter
        implements IsWidget,
                   DiscussionWidgetView.Presenter {

    private DiscussionWidgetView view;
    private User identity;
    private Caller<AppConfigService> appConfigService;

    private Metadata metadata;

    public DiscussionWidgetPresenter() {
    }

    @Inject
    public DiscussionWidgetPresenter(
            final DiscussionWidgetView view,
            final User identity,
            final Caller<AppConfigService> appConfigService) {
        this.view = view;
        this.identity = identity;
        this.appConfigService = appConfigService;

        view.setPresenter( this );

    }

    public void setContent( Metadata metadata ) {
        view.clear();

        this.metadata = metadata;
        for ( DiscussionRecord record : metadata.getDiscussion() ) {
            view.addRow( record );
        }
        view.scrollToBottom();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onAddComment( final String comment ) {
        if ( comment != null && !comment.trim().isEmpty() ) {
            appConfigService.call( new RemoteCallback<Long>() {
                @Override
                public void callback( Long timestamp ) {
                    DiscussionRecord record = new DiscussionRecord( timestamp, identity.getIdentifier(), comment );
                    metadata.getDiscussion().add( record );
                    view.addRow( record );
                    view.clearCommentBox();
                    view.scrollToBottom();
                }
            } ).getTimestamp();
        }
    }

    public void onResize() {
        view.onResize();
    }
}
