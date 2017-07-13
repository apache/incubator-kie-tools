/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.showcase.client.screens.explorer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.dmn.showcase.client.screens.BaseSessionScreen;
import org.kie.workbench.common.dmn.showcase.client.screens.SessionScreenView;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = SessionTreeExplorerScreen.SCREEN_ID)
public class SessionTreeExplorerScreen extends BaseSessionScreen {

    public static final String SCREEN_ID = "SessionTreeExplorerScreen";
    public static final String TITLE = "Explorer";

    private TreeExplorer explorer;
    private SessionScreenView view;

    public SessionTreeExplorerScreen() {
        //CDI proxy
    }

    @Inject
    public SessionTreeExplorerScreen(final SessionScreenView view,
                                     final TreeExplorer explorer) {
        this.view = view;
        this.explorer = explorer;
    }

    @PostConstruct
    public void init() {
        view.showEmptySession();
    }

    @OnStartup
    @SuppressWarnings("unused")
    public void onStartup(final PlaceRequest placeRequest) {
        //Nothing to do, move on...
    }

    @OnClose
    public void onClose() {
        close();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return TITLE;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @Override
    protected void doOpenSession() {
        // No need to initialize state or views until a diagram is present.
    }

    @Override
    protected void doOpenDiagram() {
        final AbstractCanvasHandler handler = getCanvasHandler();
        if (handler != null) {
            explorer.show(handler);
            view.showScreenView(explorer);
        }
    }

    @Override
    protected void doCloseSession() {
        explorer.clear();
        explorer.destroy();
        view.showEmptySession();
    }
}
