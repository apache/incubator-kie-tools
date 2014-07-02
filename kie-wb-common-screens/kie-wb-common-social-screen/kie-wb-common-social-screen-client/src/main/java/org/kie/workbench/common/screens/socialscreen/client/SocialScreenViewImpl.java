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

import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.resources.Bootstrap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.socialscreen.client.discussion.DiscussionWidgetPresenter;

public class SocialScreenViewImpl
        extends Composite
        implements SocialScreenView {

    interface Binder
            extends
            UiBinder<Widget, SocialScreenViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField(provided = true)
    TabPanel tabPanel = new TabPanel(Bootstrap.Tabs.ABOVE);

    @UiField(provided = true)
    DiscussionWidgetPresenter discussionArea;

    @Inject
    public SocialScreenViewImpl(DiscussionWidgetPresenter discussionArea) {
        this.discussionArea = discussionArea;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setDescription(String description) {
    }
}
