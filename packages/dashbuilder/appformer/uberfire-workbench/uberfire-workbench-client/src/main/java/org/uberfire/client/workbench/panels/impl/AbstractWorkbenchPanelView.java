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

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.LayoutSelection;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
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

    @Inject
    protected LayoutSelection layoutSelection;

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

    protected void addOnFocusHandler(MultiPartWidget widget) {
        widget.addOnFocusHandler(new Command() {
            @Override
            public void execute() {
                panelManager.onPanelFocus(presenter.getDefinition());
            }
        });
    }

    protected void addSelectionHandler(HasSelectionHandlers<PartDefinition> widget) {
        widget.addSelectionHandler(new SelectionHandler<PartDefinition>() {
            @Override
            public void onSelection(final SelectionEvent<PartDefinition> event) {
                panelManager.onPartLostFocus();
                panelManager.onPartFocus(event.getSelectedItem());
            }
        });
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
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append("@").append(System.identityHashCode(this));
        sb.append(" id=").append(getElement().getAttribute("id"));
        return sb.toString();
    }

    /**
     * This implementation returns null, meaning this panel does not support parts being dropped on it. Subclasses that
     * want to support Drag-and-Drop can override.
     */
    @Override
    public Widget getPartDropRegion() {
        return null;
    }

    @Override
    public void maximize() {
        layoutSelection.get().maximize(this);
    }

    @Override
    public void unmaximize() {
        layoutSelection.get().unmaximize(this);
    }
}
