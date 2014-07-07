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

package org.kie.workbench.common.screens.socialscreen.client.discussion;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.screens.socialscreen.client.SocialScreenManager;
import org.uberfire.security.Identity;

public class DiscussionWidgetPresenter
        implements IsWidget, DiscussionWidgetView.Presenter {

    private DiscussionWidgetView view;
    private final Identity identity;

    @Inject
    public DiscussionWidgetPresenter(
            final DiscussionWidgetView view,
            final Identity identity,
            final SocialScreenManager manager) {
        this.view = view;
        this.identity = identity;

        view.setPresenter(this);

    }

    public void setContent(Metadata metadata) {
        for (DiscussionRecord record : metadata.getDiscussion()) {
            view.addRow(new CommentLine(record));
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onAddComment(String comment) {
        view.addRow(new CommentLine(new DiscussionRecord(1, identity.getName(), comment)));
        view.clearCommentBox();
    }
}
