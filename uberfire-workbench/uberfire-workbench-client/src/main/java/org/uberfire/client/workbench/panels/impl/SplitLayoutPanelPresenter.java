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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.workbench.model.CompassPosition;

import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * SplitLayoutPanelPresenter and {@link SplitLayoutPanelView} arrange panels using a GWT {@link SplitLayoutPanel}.
 * As such it only supports {@link CompassPosition#WEST} and {@link CompassPosition#CENTER}. Does not support
 * containing parts directly.
 */
@Dependent
public class SplitLayoutPanelPresenter extends AbstractWorkbenchPanelPresenter<SplitLayoutPanelPresenter> {

    @Inject
    public SplitLayoutPanelPresenter(@Named("SplitLayoutPanelView") final SplitLayoutPanelView view,
                                     final PerspectiveManager perspectiveManager,
                                     final Event<MaximizePlaceEvent> maximizePanelEvent ) {
        super( view, perspectiveManager, maximizePanelEvent );
    }

    @Override
    protected SplitLayoutPanelPresenter asPresenterType() {
        return this;
    }

    /**
     * Returns the {@link LayoutPanelPresenter} class name.
     */
    @Override
    public String getDefaultChildType() {
        return LayoutPanelPresenter.class.getName();
    }
}
