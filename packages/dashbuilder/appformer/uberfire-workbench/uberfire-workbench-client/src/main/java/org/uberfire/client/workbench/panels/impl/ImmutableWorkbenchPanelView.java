package org.uberfire.client.workbench.panels.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter.View;
import org.uberfire.client.workbench.widgets.panel.StaticFocusedResizePanel;
import org.uberfire.workbench.model.PartDefinition;

/**
 * The view component of {@link ImmutableWorkbenchPanelPresenter}.
 */
@Dependent
@Named("ImmutableWorkbenchPanelView")
public class ImmutableWorkbenchPanelView
        extends AbstractWorkbenchPanelView<ImmutableWorkbenchPanelPresenter> {

    @Inject
    PlaceManager placeManager;

    @Inject
    StaticFocusedResizePanel panel;

    @PostConstruct
    void postConstruct() {
        Layouts.setToFillParent(panel);
        initWidget(panel);
    }

    @Override
    public Widget getWidget() {
        return panel;
    }

    public StaticFocusedResizePanel getPanel() {
        return panel;
    }

    @Override
    public void init(final ImmutableWorkbenchPanelPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public ImmutableWorkbenchPanelPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void addPart(final View view) {
        if (panel.getPartView() == null) {
            panel.setPart(view);
            onResize();
        } else {
            throw new RuntimeException("Uberfire Panel Invalid State: This panel support only one part.");
        }
    }

    @Override
    public void changeTitle(final PartDefinition part,
                            final String title,
                            final IsWidget titleDecoration) {
    }

    @Override
    public boolean selectPart(final PartDefinition part) {
        final PartDefinition currentPartDefinition = getCurrentPartDefinition();
        if (currentPartDefinition != null && currentPartDefinition.equals(part)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean removePart(final PartDefinition part) {
        return false;
    }

    @Override
    public void setFocus(boolean hasFocus) {
        panel.setFocus(hasFocus);
    }

    @Override
    public void onResize() {
        presenter.onResize(getOffsetWidth(),
                           getOffsetHeight());
        super.onResize();
    }

    PartDefinition getCurrentPartDefinition() {
        View partView = panel.getPartView();
        if (partView == null) {
            return null;
        }

        WorkbenchPartPresenter presenter = partView.getPresenter();
        if (presenter == null) {
            return null;
        }

        return presenter.getDefinition();
    }

    @Override
    public Collection<PartDefinition> getParts() {
        PartDefinition currentPartDefinition = getCurrentPartDefinition();
        if (currentPartDefinition == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(currentPartDefinition);
    }
}
