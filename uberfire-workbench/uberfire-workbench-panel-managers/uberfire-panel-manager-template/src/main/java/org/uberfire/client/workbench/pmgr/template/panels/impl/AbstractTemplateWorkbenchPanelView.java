package org.uberfire.client.workbench.pmgr.template.panels.impl;

import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.pmgr.template.widgets.panel.SimplePanel;
import org.uberfire.workbench.model.PartDefinition;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.IsWidget;

public abstract class AbstractTemplateWorkbenchPanelView<P extends WorkbenchPanelPresenter> extends BaseWorkbenchTemplatePanelView<P> {

    SimplePanel panel = new SimplePanel();

    @Override
    public void init( P presenter ) {
        this.presenter = presenter;
    }

    @Override
    public P getPresenter() {
        return this.presenter;
    }


    @Override
    public void clear() {
        panel.clear();
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        panel.setPart( view );
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration ) {
    }

    @Override
    public boolean selectPart( final PartDefinition part ) {
        if ( part.equals( panel.getPartDefinition() ) ) {
            scheduleResize();
            return true;
        }
        return false;
    }

    @Override
    public boolean removePart( final PartDefinition part ) {
        if ( part.equals( panel.getPartDefinition() ) ) {
            panel.clear();
            return true;
        }
        return false;
    }

    @Override
    public void setFocus( boolean hasFocus ) {
    }

    private void scheduleResize() {
        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onResize();
            }
        } );
    }

}
