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

package org.uberfire.client.workbench.panels;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartView;
import org.uberfire.workbench.model.PartDefinition;

@Dependent
public class WorkbenchPanelViewImpl extends ResizeComposite implements WorkbenchPanelView {

    @Inject
    StaticFocusedResizePanel panel;

    protected WorkbenchPanelPresenter presenter;

    @PostConstruct
    void postConstruct() {
        Layouts.setToFillParent(panel);
        initWidget(panel);
    }

    @Override
    public Widget getWidget() {
        return panel;
    }

    @Override
    public void init(final WorkbenchPanelPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addPart(final WorkbenchPartView view) {
        if (panel.getPartView() == null) {
            panel.setPart(view);
            onResize();
        } else {
            throw new RuntimeException("Uberfire Panel Invalid State: This panel support only one part.");
        }
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
        WorkbenchPartView partView = panel.getPartView();
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
    public void setElementId(String elementId) {
        if (elementId == null) {
            getElement().removeAttribute("id");
        } else {
            getElement().setAttribute("id",
                                      elementId);
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + "@" + System.identityHashCode(this) +
                " id=" + getElement().getAttribute("id");
    }
}
