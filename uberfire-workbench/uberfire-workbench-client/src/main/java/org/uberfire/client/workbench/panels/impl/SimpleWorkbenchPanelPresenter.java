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
import org.uberfire.client.workbench.panels.DockingWorkbenchPanelView;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;

/**
 * A panel with a title bar. Can contain one part at a time. The part's view fills the entire space not used up by
 * the title bar. Adding a new part replaces the existing part. Does not support drag-and-drop rearrangement of
 * parts.
 *
 * @see SimpleDnDWorkbenchPanelPresenter
 */
@Dependent
public class SimpleWorkbenchPanelPresenter extends AbstractDockingWorkbenchPanelPresenter<SimpleWorkbenchPanelPresenter> {

    @Inject
    public SimpleWorkbenchPanelPresenter( @Named("SimpleWorkbenchPanelView") final WorkbenchPanelView<SimpleWorkbenchPanelPresenter> view,
                                          final PerspectiveManager perspectiveManager ) {
        super( view, perspectiveManager );
    }

    @Override
    protected SimpleWorkbenchPanelPresenter asPresenterType() {
        return this;
    }

    @Override
    public DockingWorkbenchPanelView<SimpleWorkbenchPanelPresenter> getPanelView() {
        return super.getPanelView();
    }
}
