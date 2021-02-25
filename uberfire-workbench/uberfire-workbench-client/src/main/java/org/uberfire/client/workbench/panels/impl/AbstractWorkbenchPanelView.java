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

import javax.inject.Inject;

import com.google.gwt.user.client.ui.ResizeComposite;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

/**
 * Implements focus and selection handling.
 * @param <P> the type of presenter this view goes with
 */
public abstract class AbstractWorkbenchPanelView<P extends WorkbenchPanelPresenter>
        extends ResizeComposite
        implements WorkbenchPanelView<P> {

    @Inject
    protected PanelManager panelManager;

    protected P presenter;

    /**
     * Throws {@code UnsupportedOperationException} when called. Subclasses that wish to support child panels should
     * override this and {@link #removePanel(WorkbenchPanelView)}.
     */
    @Override
    public void addPanel(PanelDefinition panel,
                         WorkbenchPanelView<?> view,
                         Position position) {
        throw new UnsupportedOperationException("This panel does not support child panels");
    }

    /**
     * Throws {@code UnsupportedOperationException} when called. Subclasses that wish to support child panels should
     * override this and {@link #addPanel(PanelDefinition, WorkbenchPanelView, Position)}.
     */
    @Override
    public boolean removePanel(WorkbenchPanelView<?> child) {
        throw new UnsupportedOperationException("This panel does not support child panels");
    }

    @Override
    public P getPresenter() {
        return this.presenter;
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
