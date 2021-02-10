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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;

/**
 * The default layout implementation.
 */
@ApplicationScoped
public class WorkbenchLayoutImpl implements WorkbenchLayout {

    public static final String UF_ROOT_CSS_CLASS = "uf-workbench-layout";
    /**
     * Dock Layout panel: in center root perspective and also (if available) with east west south docks
     */
    private final DockLayoutPanel rootContainer = new DockLayoutPanel(Unit.PX);
    /**
     * The panel within which the current perspective's root view resides. This panel lasts the lifetime of the app; it's
     * cleared and repopulated with the new perspective's root view each time
     */
    private final SimpleLayoutPanel perspectiveRootContainer = new SimpleLayoutPanel();
    /**
     * Top-level widget of the whole workbench layout. This panel contains the nested container panels for headers,
     * footers, and the current perspective. During a normal startup of UberFire, this panel would be added directly to
     * the RootLayoutPanel.
     */
    private HeaderPanel root;
    /**
     * An abstraction for DockLayoutPanel used by Uberfire Docks.
     */
    private UberfireDocksContainer uberfireDocksContainer;

    public WorkbenchLayoutImpl() {
        // Empty
    }

    @Inject
    public WorkbenchLayoutImpl(HeaderPanel root,
                               UberfireDocksContainer uberfireDocksContainer) {

        this.root = root;
        this.uberfireDocksContainer = uberfireDocksContainer;
    }

    @PostConstruct
    private void init() {
        perspectiveRootContainer.ensureDebugId("perspectiveRootContainer");
        root.addStyleName(UF_ROOT_CSS_CLASS);
    }

    @Override
    public HeaderPanel getRoot() {
        return root;
    }

    @Override
    public HasWidgets getPerspectiveContainer() {
        return perspectiveRootContainer;
    }

    @Override
    public void onBootstrap() {
        setupDocksContainer();
        rootContainer.add(perspectiveRootContainer);

        Layouts.setToFillParent(perspectiveRootContainer);
        Layouts.setToFillParent(rootContainer);

        root.setContentWidget(rootContainer);
    }

    private void setupDocksContainer() {
        uberfireDocksContainer.setup(rootContainer,
                                     () -> Scheduler.get().scheduleDeferred(this::onResize));
    }

    @Override
    public void onResize() {
        resizeTo(Window.getClientWidth(),
                 Window.getClientHeight());
    }

    @Override
    public void resizeTo(int width,
                         int height) {
        root.setPixelSize(width,
                          height);

        // The dragBoundary can't be a LayoutPanel, so it doesn't support ProvidesResize/RequiresResize.
        // We start the cascade of onResize() calls at its immediate child.
        perspectiveRootContainer.onResize();
    }
}
