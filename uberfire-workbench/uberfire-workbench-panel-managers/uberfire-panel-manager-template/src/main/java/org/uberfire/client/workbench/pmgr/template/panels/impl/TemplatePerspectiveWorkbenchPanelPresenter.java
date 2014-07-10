

package org.uberfire.client.workbench.pmgr.template.panels.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;

@Dependent
public class TemplatePerspectiveWorkbenchPanelPresenter extends AbstractTemplateWorkbenchPanelPresenter<TemplatePerspectiveWorkbenchPanelPresenter> {

    @Inject
    public TemplatePerspectiveWorkbenchPanelPresenter( @Named("TemplatePerspectiveWorkbenchPanelView") final TemplatePerspectiveWorkbenchPanelView view,
                                                       final PerspectiveManager perspectiveManager,
                                                       final Event<MaximizePlaceEvent> maximizePanelEvent ) {
        super( view, perspectiveManager, maximizePanelEvent );
    }

    @Override
    protected TemplatePerspectiveWorkbenchPanelPresenter asPresenterType() {
        return this;
    }
}
