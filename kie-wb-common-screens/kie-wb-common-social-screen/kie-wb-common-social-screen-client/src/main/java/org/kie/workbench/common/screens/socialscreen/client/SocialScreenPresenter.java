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

package org.kie.workbench.common.screens.socialscreen.client;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.Position;

@WorkbenchScreen(identifier = "org.kie.workbench.common.screens.socialscreen")
public class SocialScreenPresenter {

    private final SocialScreenView view;

    @Inject
    public SocialScreenPresenter(
            final SocialScreenView view,
            final SocialScreenManager manager,
            final Caller<MetadataService> metadataService
    ) {

        this.view = view;

        metadataService.call(new RemoteCallback<Metadata>() {
            @Override
            public void callback(Metadata metadata) {
                view.setDescription(metadata.getDescription());

                for (DiscussionRecord record : metadata.getDiscussion()) {
                    view.addDiscussionRow(record.getTimestamp(), record.getAuthor(), record.getNote());
                }
            }
        }).getMetadata(manager.getCurrentPath());
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Collaboration";
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return view;
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return Position.SOUTH;
    }

}
