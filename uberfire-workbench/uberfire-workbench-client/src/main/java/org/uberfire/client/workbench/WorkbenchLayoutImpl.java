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

package org.uberfire.client.workbench;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;

/**
 * The default layout implementation.
 */
@ApplicationScoped
public class WorkbenchLayoutImpl implements WorkbenchLayout {

    private final UberfireDocksContainer uberfireDocksContainer;
    private final DockLayoutPanel rootContainer = new DockLayoutPanel(Unit.PX);
    private Widget currentContent;

    @Inject
    public WorkbenchLayoutImpl(final UberfireDocksContainer uberfireDocksContainer) {
        this.uberfireDocksContainer = uberfireDocksContainer;
    }

    @Override
    public DockLayoutPanel getRoot() {
        return rootContainer;
    }

    @Override
    public void addContent(Widget content) {
        if (currentContent != null) {
            rootContainer.remove(currentContent);
        }
        rootContainer.add(content);
        Layouts.setToFillParent(content);
    }

    @Override
    public void onBootstrap() {
        uberfireDocksContainer.setup(rootContainer,
                                     () -> Scheduler.get().scheduleDeferred(this::onResize));
        Layouts.setToFillParent(rootContainer);
    }

    @Override
    public void onResize() {
        resizeTo(Window.getClientWidth(),
                 Window.getClientHeight());
    }

    @Override
    public void resizeTo(int width,
                         int height) {
        rootContainer.setPixelSize(width,
                                   height);

        // The dragBoundary can't be a LayoutPanel, so it doesn't support ProvidesResize/RequiresResize.
        // We start the cascade of onResize() calls at its immediate child.
        rootContainer.onResize();
    }
}
