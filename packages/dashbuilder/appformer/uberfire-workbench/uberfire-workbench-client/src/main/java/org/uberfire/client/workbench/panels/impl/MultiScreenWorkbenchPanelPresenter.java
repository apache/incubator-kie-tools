/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;

/**
 * A panel that allows opening screens within the current perspective, without closing the previous one.
 */
@Dependent
public class MultiScreenWorkbenchPanelPresenter extends AbstractMultiPartWorkbenchPanelPresenter<MultiScreenWorkbenchPanelPresenter> {

    @Inject
    public MultiScreenWorkbenchPanelPresenter(@Named("MultiScreenWorkbenchPanelView") final WorkbenchPanelView<MultiScreenWorkbenchPanelPresenter> view,
                                              final ActivityManager activityManager,
                                              final PerspectiveManager perspectiveManager) {
        super(view,
              activityManager,
              perspectiveManager);
    }

    @Override
    protected MultiScreenWorkbenchPanelPresenter asPresenterType() {
        return this;
    }
}
