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

import java.util.Collection;
import java.util.IdentityHashMap;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.mvp.TemplatedActivity;
import org.uberfire.client.workbench.LayoutSelection;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter.View;
import org.uberfire.workbench.model.NamedPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;

/**
 * The view component of the templated panel system. This view supports an arbitrary number of child panel views, each
 * identified by a {@link NamedPosition}.
 * <p>
 * This view does not support having parts added to it directly, so it also does not support drag-and-drop of parts.
 *
 * @see TemplatedWorkbenchPanelPresenter
 * @see WorkbenchPanel
 */
@Dependent
@Named("TemplatedWorkbenchPanelView")
public class TemplatedWorkbenchPanelView implements WorkbenchPanelView<TemplatedWorkbenchPanelPresenter> {

    private final IdentityHashMap<WorkbenchPanelView<?>, NamedPosition> childPanelPositions = new IdentityHashMap<WorkbenchPanelView<?>, NamedPosition>();
    private TemplatedWorkbenchPanelPresenter presenter;
    private TemplatedActivity activity;
    private String elementId;
    @Inject
    private LayoutSelection layoutSelection;

    @Override
    public void init(TemplatedWorkbenchPanelPresenter presenter) {
        this.presenter = presenter;
    }

    public void setActivity(TemplatedActivity activity) {
        this.activity = PortablePreconditions.checkNotNull("activity",
                                                           activity);

        // ensure the new activity's widget gets its ID set
        setElementId(elementId);
    }

    @Override
    public Widget asWidget() {
        if (activity == null) {
            return null;
        }
        return ElementWrapperWidget.getWidget(activity.getRootElement());
    }

    @Override
    public void onResize() {
        Widget root = asWidget();
        if (root instanceof RequiresResize) {
            ((RequiresResize) root).onResize();
        }
    }

    @Override
    public TemplatedWorkbenchPanelPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void addPanel(PanelDefinition panel,
                         WorkbenchPanelView<?> view,
                         Position p) {
        NamedPosition position = (NamedPosition) p;
        HTMLElement panelContainer = activity.resolvePosition(position);

        if (panelContainer.hasChildNodes()) {
            throw new IllegalStateException("Child position " + position + " is already occupied");
        }

        DOMUtil.appendWidgetToElement(panelContainer,
                                      view.asWidget());
        childPanelPositions.put(view,
                                position);
    }

    @Override
    public boolean removePanel(WorkbenchPanelView<?> child) {
        NamedPosition removedFromPosition = childPanelPositions.remove(child);
        if (removedFromPosition == null) {
            return false;
        }

        HTMLElement panelContainer = activity.resolvePosition(removedFromPosition);
        removeAllChildren(panelContainer);
        return true;
    }

    @Override
    public void setFocus(boolean hasFocus) {
        // not important since this panel can't hold parts
    }

    @Override
    public void addPart(View view) {
        throw new UnsupportedOperationException("This view doesn't support parts");
    }

    @Override
    public Collection<PartDefinition> getParts() {
        throw new UnsupportedOperationException("This view doesn't support parts");
    }

    @Override
    public void changeTitle(PartDefinition part,
                            String title,
                            IsWidget titleDecoration) {
        throw new UnsupportedOperationException("This view doesn't support parts");
    }

    @Override
    public boolean selectPart(PartDefinition part) {
        throw new UnsupportedOperationException("This view doesn't support parts");
    }

    @Override
    public boolean removePart(PartDefinition part) {
        throw new UnsupportedOperationException("This view doesn't support parts");
    }

    @Override
    public Widget getPartDropRegion() {
        return null;
    }

    /**
     * Will set, but not clear, the ID of the activity's root element. Clearing is disabled because the templating
     * system may be relying on the element's ID to find it. Of course, setting a different ID will also interfere with
     * templating, but the expectation is that this feature would only be used with templated panels for debugging
     * purposes.
     */
    @Override
    public void setElementId(String elementId) {
        this.elementId = elementId;

        // this call may come in before the activity has been set; if so, the stored ID will be applied to the
        // element when the activity is set.
        if (asWidget() != null) {
            if (elementId != null) {
                asWidget().getElement().setAttribute("id",
                                                     elementId);
            }
        }
    }

    @Override
    public void maximize() {
        layoutSelection.get().maximize(asWidget());
    }

    @Override
    public void unmaximize() {
        layoutSelection.get().unmaximize(asWidget());
    }
}
