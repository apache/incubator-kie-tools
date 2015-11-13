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
package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;

/**
 * A panel with a tab bar that allows selecting among the parts it contains, with drag-and-drop for moving parts to
 * and from other drag-and-drop enabled panels. Only one part at a time is visible, and it fills the entire
 * available space not used up by the tab bar.
 */
@Dependent
public class MultiTabWorkbenchPanelPresenter extends AbstractMultiPartWorkbenchPanelPresenter<MultiTabWorkbenchPanelPresenter> {

    @Inject
    public MultiTabWorkbenchPanelPresenter( @Named("MultiTabWorkbenchPanelView") final WorkbenchPanelView<MultiTabWorkbenchPanelPresenter> view,
                                            final ActivityManager activityManager,
                                            final PerspectiveManager perspectiveManager ) {
        super( view, activityManager, perspectiveManager );
    }

    @Override
    protected MultiTabWorkbenchPanelPresenter asPresenterType() {
        return this;
    }
}
