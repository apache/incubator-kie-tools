package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.TemplatedActivity;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;

@Dependent
public class TemplatedWorkbenchPanelPresenter extends AbstractWorkbenchPanelPresenter<TemplatedWorkbenchPanelPresenter> {

    @Inject
    public TemplatedWorkbenchPanelPresenter( @Named("TemplatedWorkbenchPanelView") TemplatedWorkbenchPanelView view,
                                             PerspectiveManager panelManager ) {
        super( view,
               panelManager );
    }

    @Override
    public TemplatedWorkbenchPanelView getPanelView() {
        return (TemplatedWorkbenchPanelView) super.getPanelView();
    }

    @Override
    protected TemplatedWorkbenchPanelPresenter asPresenterType() {
        return this;
    }

    /**
     * Returns the fully-qualified class name for {@link StaticWorkbenchPanelPresenter}.
     */
    @Override
    public String getDefaultChildType() {
        return StaticWorkbenchPanelPresenter.class.getName();
    }

    // could generalize this to a HasActivity interface some day. for now, it's a special case for templated perspectives.
    public void setActivity( TemplatedActivity activity ) {
        getPanelView().setActivity( activity );
    }

    /**
     * Forwards the request to the first child panel (the one marked as {@link WorkbenchPanel#isDefault}).
     */
    @Override
    public void addPart( WorkbenchPartPresenter part,
                         String contextId ) {
        if ( getPanels().isEmpty() ) {
            throw new IllegalStateException( "This panel type does not support parts directly; it forwards add part requests"
                    + " to its first child panel. However, this panel currently has no child panels." );
        }
        getPanels().values().iterator().next().addPart( part, contextId );
    }
}
