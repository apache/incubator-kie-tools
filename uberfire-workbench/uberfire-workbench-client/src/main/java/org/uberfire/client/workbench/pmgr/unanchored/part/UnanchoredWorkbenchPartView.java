/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench.pmgr.unanchored.part;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;

/**
 * A Workbench panel part.
 */
@Dependent
public class UnanchoredWorkbenchPartView
        extends SimplePanel
        implements WorkbenchPartPresenter.View {

    private final ScrollPanel sp = new ScrollPanel();
    private WorkbenchPartPresenter presenter;

    public UnanchoredWorkbenchPartView() {
        setWidget(sp);

        // ScrollPanel creates an additional internal div that we need to style
        sp.getElement().getFirstChildElement().setClassName("uf-scroll-panel");
    }

    @Override
    public void init(WorkbenchPartPresenter presenter) {
        this.presenter = presenter;
        Layouts.setToFillParent(this);
        sp.getElement().addClassName("uf-perspective-component");
    }

    @Override
    public WorkbenchPartPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public IsWidget getWrappedWidget() {
        return sp.getWidget();
    }

    @Override
    public void setWrappedWidget(final IsWidget widget) {
        sp.setWidget(widget);
    }

    @Override
    public void onResize() {
        if (getWidget() instanceof RequiresResize) {
            ((RequiresResize) getWidget()).onResize();
        }
    }
}
