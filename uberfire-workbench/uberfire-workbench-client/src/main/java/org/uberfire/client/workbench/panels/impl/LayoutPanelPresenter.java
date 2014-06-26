/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.panels.impl;

import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.events.MinimizePlaceEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

@Dependent
public class LayoutPanelPresenter extends AbstractWorkbenchPanelPresenter<LayoutPanelPresenter> {

    @Inject
    public LayoutPanelPresenter(@Named("LayoutPanelView") final LayoutPanelView view,
                                              final PanelManager panelManager,
                                              final Event<MaximizePlaceEvent> maximizePanelEvent,
                                              final Event<MinimizePlaceEvent> minimizePanelEvent) {
        super( view, panelManager, maximizePanelEvent, minimizePanelEvent );
    }

    @Override
    protected LayoutPanelPresenter asPresenterType() {
        return this;
    }
}
