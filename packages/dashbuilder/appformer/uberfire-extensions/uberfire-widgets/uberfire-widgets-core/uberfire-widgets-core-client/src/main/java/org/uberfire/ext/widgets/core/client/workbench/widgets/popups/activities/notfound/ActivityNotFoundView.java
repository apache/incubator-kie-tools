/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound;

import javax.annotation.PostConstruct;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * View for when an Activity cannot be found
 */
public class ActivityNotFoundView extends SimplePanel
        implements
        ActivityNotFoundPresenter.View {

    private ActivityNotFoundPresenter presenter;

    private static ActivityNotFoundViewBinder uiBinder = GWT.create(ActivityNotFoundViewBinder.class);

    @PostConstruct
    public void init() {
        setWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init(final ActivityNotFoundPresenter presenter) {
        this.presenter = presenter;
    }

    interface ActivityNotFoundViewBinder
            extends
            UiBinder<Widget, ActivityNotFoundView> {

    }
}
