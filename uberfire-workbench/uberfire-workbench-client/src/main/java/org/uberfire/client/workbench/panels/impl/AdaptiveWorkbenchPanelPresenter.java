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

import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;

@Dependent
public class AdaptiveWorkbenchPanelPresenter extends AbstractDockingWorkbenchPanelPresenter<AdaptiveWorkbenchPanelPresenter> {

    @Inject
    public AdaptiveWorkbenchPanelPresenter( @Named("AdaptiveWorkbenchPanelView") final WorkbenchPanelView<AdaptiveWorkbenchPanelPresenter> view,
                                            final PerspectiveManager perspectiveManager ) {
        super( view, perspectiveManager );
    }

    @Override
    protected AdaptiveWorkbenchPanelPresenter asPresenterType() {
        return this;
    }

    @Override
    public String getDefaultChildType() {
        if ( getDefinition().isRoot() && getDefinition().getParts().size() > 0 ) {
            return MultiListWorkbenchPanelPresenter.class.getName();
        } else if ( getPanels().size() > 0 ) {
            return MultiListWorkbenchPanelPresenter.class.getName();
        }
        return SimpleWorkbenchPanelPresenter.class.getName();
    }

}
