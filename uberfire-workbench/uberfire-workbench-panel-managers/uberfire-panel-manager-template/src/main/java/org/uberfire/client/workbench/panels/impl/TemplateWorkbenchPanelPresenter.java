

package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.events.MinimizePlaceEvent;

@Dependent
public class TemplateWorkbenchPanelPresenter extends AbstractTemplateWorkbenchPanelPresenter<TemplateWorkbenchPanelPresenter> {

    @Inject
    public TemplateWorkbenchPanelPresenter( @Named("TemplateWorkbenchPanelView") final TemplateWorkbenchPanelView view,
                                            final PanelManager panelManager,
                                            final Event<MaximizePlaceEvent> maximizePanelEvent,
                                            final Event<MinimizePlaceEvent> minimizePanelEvent ) {
        super( view, panelManager, maximizePanelEvent, minimizePanelEvent );
    }

    @Override
    protected TemplateWorkbenchPanelPresenter asPresenterType() {
        return this;
    }
}
