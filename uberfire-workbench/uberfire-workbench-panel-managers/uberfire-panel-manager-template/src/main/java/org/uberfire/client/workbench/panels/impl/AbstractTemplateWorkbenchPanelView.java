package org.uberfire.client.workbench.panels.impl;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.panel.SimplePanel;
import org.uberfire.workbench.model.PartDefinition;

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
    public void selectPart( final PartDefinition part ) {
        scheduleResize();
    }

    @Override
    public void removePart( final PartDefinition part ) {
        panel.clear();
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

    @Override
    public void onResize() {
    }
}
