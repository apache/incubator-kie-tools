package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;

/**
 * An undecorated panel that can contain one part at a time and does not support child panels. The part's view fills
 * the entire panel. Does not support changing the part after it is set. Does not support drag-and-drop rearrangement of
 * parts.
 */
@Dependent
public class ImmutableWorkbenchPanelPresenter extends AbstractWorkbenchPanelPresenter<ImmutableWorkbenchPanelPresenter> {

    private PlaceManager placeManager;

    @Inject
    public ImmutableWorkbenchPanelPresenter(@Named("ImmutableWorkbenchPanelView") final ImmutableWorkbenchPanelView view,
                                            final PerspectiveManager perspectiveManager,
                                            final PlaceManager placeManager) {
        super(view, perspectiveManager);
        this.placeManager = placeManager;
    }

    @Override
    protected ImmutableWorkbenchPanelPresenter asPresenterType() {
        return this;
    }

    /**
     * Returns null (static panels don't support child panels).
     */
    @Override
    public String getDefaultChildType() {
        return null;
    }

    @Override
    public void addPart(final WorkbenchPartPresenter part) {
        this.addPart(part, null);
    }

    @Override
    public void addPart(final WorkbenchPartPresenter part,
                        final String contextId) {
        if (createSinglePartPanelHelper().hasNoParts()) {
            super.addPart(part, contextId);
        }
    }

    SinglePartPanelHelper createSinglePartPanelHelper() {
        return new SinglePartPanelHelper(getPanelView().getParts(), placeManager);
    }
}
