/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.workbench.panels.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter.View;
import org.uberfire.client.workbench.widgets.panel.StaticFocusedResizePanel;
import org.uberfire.workbench.model.PartDefinition;

/**
 * The view component of {@link StaticWorkbenchPanelPresenter}.
 */
@Dependent
@Named("StaticWorkbenchPanelView")
public class StaticWorkbenchPanelView
        extends AbstractWorkbenchPanelView<StaticWorkbenchPanelPresenter> {

    @Inject
    PlaceManager placeManager;

    @Inject
    StaticFocusedResizePanel panel;

    @PostConstruct
    void postConstruct() {
        Layouts.setToFillParent(panel);
        initWidget(panel);
    }

    // override is for unit test: super.getWidget() returns a new mock every time
    @Override
    public Widget getWidget() {
        return panel;
    }

    public StaticFocusedResizePanel getPanel() {
        return panel;
    }

    @Override
    public void init(final StaticWorkbenchPanelPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public StaticWorkbenchPanelPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void addPart(final WorkbenchPartPresenter.View view) {
        if (panel.getPartView() == null) {
            panel.setPart(view);
            onResize();
        } else {
            throw new RuntimeException("Uberfire Panel Invalid State: This panel support only one part.");
        }
    }

    @Override
    public boolean selectPart(final PartDefinition part) {
        PartDefinition currentPartDefinition = getCurrentPartDefinition();
        return currentPartDefinition != null && currentPartDefinition.equals(part);
    }

    @Override
    public boolean removePart(final PartDefinition part) {
        PartDefinition currentPartDefinition = getCurrentPartDefinition();
        if (currentPartDefinition != null && currentPartDefinition.equals(part)) {
            panel.clear();
            return true;
        }
        return false;
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
        return Collections.singletonList(currentPartDefinition);
    }
}
