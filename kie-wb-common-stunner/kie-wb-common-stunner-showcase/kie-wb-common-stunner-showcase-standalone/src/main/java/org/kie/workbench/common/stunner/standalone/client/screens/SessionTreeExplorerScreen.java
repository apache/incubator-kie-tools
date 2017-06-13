/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.standalone.client.screens;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

// TODO: I18n.
@Dependent
@WorkbenchScreen(identifier = SessionTreeExplorerScreen.SCREEN_ID)
public class SessionTreeExplorerScreen extends AbstractSessionScreen {

    public static final String SCREEN_ID = "SessionTreeExplorerScreen";
    public static final String TITLE = "Explorer";

    @Inject
    TreeExplorer treeExplorer;

    @Inject
    SessionScreenView view;

    @Inject
    Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent;

    private PlaceRequest placeRequest;
    private String title = TITLE;

    @PostConstruct
    public void init() {
        view.showEmptySession();
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
    }

    @OnOpen
    public void onOpen() {
    }

    @OnClose
    public void onClose() {
        close();
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return null;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return title;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "sessionTreeExplorerScreenContext";
    }

    @Override
    protected void doOpenSession() {
        // No need to initialize state or views until a diagram is present.
    }

    @Override
    protected void doOpenDiagram() {
        // No need to initialize state or views.
        if (null != getCanvasHandler()) {
            treeExplorer.show(getCanvasHandler());
            view.showScreenView(treeExplorer);
        }
    }

    @Override
    protected void doCloseSession() {
        treeExplorer.clear();
        treeExplorer.destroy();
        view.showEmptySession();
    }

    protected void doUpdateTitle(final String title) {
        // Change screen title.
        SessionTreeExplorerScreen.this.title = title;
        changeTitleNotificationEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                                                                     this.title));
    }
}
