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
package org.uberfire.client.workbench.pmgr.template.panels.impl;

import javax.enterprise.event.Event;

import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelPresenter;
import org.uberfire.client.workbench.pmgr.template.TemplatePanelDefinitionImpl;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.user.client.ui.Widget;

/**
 * Specializes {@link AbstractTemplateWorkbenchPanelPresenter} for use with Errai UI templated perspective definitions.
 */
public abstract class AbstractTemplateWorkbenchPanelPresenter<P extends AbstractTemplateWorkbenchPanelPresenter<P>> extends AbstractWorkbenchPanelPresenter<P> {

    protected AbstractTemplateWorkbenchPanelPresenter( final BaseWorkbenchTemplatePanelView<P> view,
                                                       final PanelManager panelManager,
                                                       final Event<MaximizePlaceEvent> maximizePanelEvent ) {
        super( view, panelManager, maximizePanelEvent );
    }


    @Override
    public void addPanel( final PanelDefinition panel,
                          final WorkbenchPanelView view,
                          final Position position ) {
        TemplatePanelDefinitionImpl templateDefinition = (TemplatePanelDefinitionImpl) panel;
        Widget widget = view.asWidget();
        templateDefinition.setPerspective(widget);
    }
}
