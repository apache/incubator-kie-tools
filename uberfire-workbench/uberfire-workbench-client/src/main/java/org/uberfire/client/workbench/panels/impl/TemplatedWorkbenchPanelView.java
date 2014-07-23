package org.uberfire.client.workbench.panels.impl;

import java.util.IdentityHashMap;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import org.uberfire.client.mvp.TemplatedActivity;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter.View;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.workbench.model.NamedPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

@Dependent
@Named("TemplatedWorkbenchPanelView")
public class TemplatedWorkbenchPanelView implements WorkbenchPanelView<TemplatedWorkbenchPanelPresenter> {

    private TemplatedWorkbenchPanelPresenter presenter;

    private TemplatedActivity activity;

    private final IdentityHashMap<WorkbenchPanelView<?>, NamedPosition> childPanelPositions = new IdentityHashMap<WorkbenchPanelView<?>, NamedPosition>();

    @Override
    public void init( TemplatedWorkbenchPanelPresenter presenter ) {
        this.presenter = presenter;
    }

    public void setActivity( TemplatedActivity activity) {
        this.activity = PortablePreconditions.checkNotNull( "activity", activity );
    }

    @Override
    public Widget asWidget() {
        return activity.getRootWidget().asWidget();
    }

    @Override
    public void onResize() {
        Widget root = asWidget();
        if ( root instanceof RequiresResize ) {
            ((RequiresResize) root).onResize();
        }
    }

    @Override
    public TemplatedWorkbenchPanelPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void addPanel( PanelDefinition panel,
                          WorkbenchPanelView view,
                          Position p ) {
        NamedPosition position = (NamedPosition) p;
        HasWidgets panelContainer = activity.resolvePosition( position );

        // FIXME the current addPanel() contract says we have to replace the existing panel if there is one,
        // but the following line will leak a managed bean if the given position already contains a
        // WorkbenchPanelView
        panelContainer.clear();

        panelContainer.add( view.asWidget() );
        childPanelPositions.put(view, position);
    }

    @Override
    public boolean removePanel( WorkbenchPanelView<?> child ) {
        NamedPosition removedFromPosition = childPanelPositions.remove( child );
        if ( removedFromPosition == null ) {
            return false;
        }
        HasWidgets panelContainer = activity.resolvePosition( removedFromPosition );
        panelContainer.clear();
        return true;
    }

    @Override
    public void setFocus( boolean hasFocus ) {
        // not important since this panel can't hold parts
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("This view doesn't support parts");
    }

    @Override
    public void addPart( View view ) {
        throw new UnsupportedOperationException("This view doesn't support parts");
    }

    @Override
    public void changeTitle( PartDefinition part,
                             String title,
                             IsWidget titleDecoration ) {
        throw new UnsupportedOperationException("This view doesn't support parts");
    }

    @Override
    public boolean selectPart( PartDefinition part ) {
        throw new UnsupportedOperationException("This view doesn't support parts");
    }

    @Override
    public boolean removePart( PartDefinition part ) {
        throw new UnsupportedOperationException("This view doesn't support parts");
    }
}
