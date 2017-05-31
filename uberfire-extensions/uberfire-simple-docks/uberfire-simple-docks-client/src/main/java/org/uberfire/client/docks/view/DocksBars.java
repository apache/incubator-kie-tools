/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.docks.view;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.docks.view.bars.DocksCollapsedBar;
import org.uberfire.client.docks.view.bars.DocksExpandedBar;
import org.uberfire.client.docks.view.menu.MenuBuilder;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceHistoryHandler;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;

@Dependent
public class DocksBars {

    private PlaceManager placeManager;

    private MenuBuilder menuBuilder;

    private Event<UberfireDocksInteractionEvent> dockInteractionEvent;
    private UberfireDocksContainer uberfireDocksContainer;
    private List<DocksBar> docks = new ArrayList<>();
    private PlaceHistoryHandler placeHistoryHandler;

    @Inject
    public DocksBars(PlaceManager placeManager,
                     MenuBuilder menuBuilder,
                     Event<UberfireDocksInteractionEvent> dockInteractionEvent,
                     UberfireDocksContainer uberfireDocksContainer,
                     PlaceHistoryHandler placeHistoryHandler) {
        this.placeManager = placeManager;
        this.menuBuilder = menuBuilder;
        this.dockInteractionEvent = dockInteractionEvent;
        this.uberfireDocksContainer = uberfireDocksContainer;
        this.placeHistoryHandler = placeHistoryHandler;
    }

    public void setup() {
        for (UberfireDockPosition uberfireDockPosition : UberfireDockPosition.values()) {
            createDock(uberfireDockPosition);
        }
    }

    private void createDock(UberfireDockPosition position) {
        DocksBar docksBar = createDockBar(position);

        uberfireDocksContainer.add(position,
                                   docksBar.getCollapsedBar(),
                                   docksBar.getCollapsedBarSize());
        uberfireDocksContainer.add(position,
                                   docksBar.getExpandedBar(),
                                   docksBar.getExpandedBarSize());
        uberfireDocksContainer.add(position,
                                   docksBar.getDockResizeBar(),
                                   docksBar.getDockResizeBarDefaultSize());

        docksBar.configureResizeBar(createResizeCommand(docksBar));
        docks.add(docksBar);
    }

    private DocksBar createDockBar(UberfireDockPosition position) {
        DocksBar docksBar = new DocksBar(position);
        return docksBar;
    }

    public void addDock(UberfireDock dock) {
        DocksBar docksBar = getDockBar(dock);
        docksBar.addDock(dock,
                         createDockSelectCommand(dock,
                                                 docksBar),
                         createDockDeselectCommand(dock,
                                                   docksBar));
    }

    DocksBar getDockBar(UberfireDock dock) {
        for (DocksBar candidate : docks) {
            if (candidate.getPosition().equals(dock.getDockPosition())) {
                return candidate;
            }
        }
        return null;
    }

    DocksBar getDockBar(UberfireDockPosition position) {
        for (DocksBar candidate : docks) {
            if (candidate.getPosition().equals(position)) {
                return candidate;
            }
        }
        return null;
    }

    ParameterizedCommand<Double> createResizeCommand(final DocksBar docksBar) {
        return new ParameterizedCommand<Double>() {
            @Override
            public void execute(Double size) {
                if (sizeIsValid(size,
                                docksBar)) {
                    docksBar.setExpandedSize(size);
                    uberfireDocksContainer.setWidgetSize(docksBar.getExpandedBar(),
                                                         docksBar.getExpandedBarSize());
                    docksBar.getExpandedBar().setupDockContentSize();
                    uberfireDocksContainer.resize();
                    dockInteractionEvent.fire(new UberfireDocksInteractionEvent(docksBar.getPosition(),
                                                                                UberfireDocksInteractionEvent.InteractionType.RESIZED));
                }
            }
        };
    }

    boolean sizeIsValid(Double size,
                        DocksBar docksBar) {
        int max = calculateMaxSize(docksBar);
        int minVisibleSize = 1;
        return size > minVisibleSize && size < max;
    }

    private int calculateMaxSize(DocksBar docksBar) {
        UberfireDockPosition position = docksBar.getPosition();
        int collapsedSize = new Double(docksBar.getCollapsedBarSize()).intValue();

        int max = 0;
        if (position == UberfireDockPosition.SOUTH) {
            max = uberfireDocksContainer.getOffsetHeight() - collapsedSize;
        } else {
            max = uberfireDocksContainer.getOffsetWidth() - 2 * collapsedSize;
        }
        return max;
    }

    public void clearAndCollapseAllDocks() {
        collapseAll();
        clearAll();
    }

    private void clearAll() {
        for (DocksBar docksBar : getDocksBars()) {
            docksBar.clearAll();
        }
    }

    private void collapseAll() {
        for (DocksBar docksBar : getDocksBars()) {
            collapse(docksBar);
        }
    }

    private void collapse(DocksBar docksBar) {
        uberfireDocksContainer.hide(docksBar.getCollapsedBar());
        uberfireDocksContainer.hide(docksBar.getExpandedBar());
        uberfireDocksContainer.hide(docksBar.getDockResizeBar());
    }

    void collapse(Widget bar) {
        uberfireDocksContainer.hide(bar);
    }

    public void clearAndCollapse(UberfireDockPosition position) {
        DocksBar dockBar = getDockBar(position);
        dockBar.clearAll();
        collapse(dockBar);

        resizeDeferred();
    }

    void resizeDeferred() {
        Scheduler.get().scheduleDeferred(() -> uberfireDocksContainer.resize());
    }

    ParameterizedCommand<String> createDockSelectCommand(final UberfireDock targetDock,
                                                         final DocksBar docksBar) {
        return clickDockName -> {
            if (targetDock != null) {
                selectDock(targetDock,
                           docksBar);
                if (docksBar.isCollapsedBarInSingleMode()) {
                    collapse(docksBar.getCollapsedBar());
                }
                uberfireDocksContainer.resize();
                dockInteractionEvent.fire(new UberfireDocksInteractionEvent(targetDock,
                                                                            UberfireDocksInteractionEvent.InteractionType.SELECTED));
            }
        };
    }

    void selectDock(UberfireDock targetDock,
                    DocksBar docksBar) {
        DocksCollapsedBar collapsedBar = docksBar.getCollapsedBar();
        DocksExpandedBar expandedBar = docksBar.getExpandedBar();
        PlaceRequest placeRequest = targetDock.getPlaceRequest();

        setupCollapsedBar(targetDock,
                          collapsedBar);
        setupExpandedBar(targetDock,
                         docksBar,
                         expandedBar);
        expand(docksBar.getDockResizeBar());
        goToPlace(expandedBar,
                  placeRequest);

        lookUpContextMenus(placeRequest,
                           docksBar.getExpandedBar());
    }

    private void goToPlace(DocksExpandedBar expandedBar,
                           PlaceRequest placeRequest) {
        placeRequest.setUpdateLocationBar(false);
        placeManager.goTo(placeRequest,
                          expandedBar.targetPanel());
    }

    private void lookUpContextMenus(PlaceRequest placeRequest,
                                    DocksExpandedBar expandedBar) {
        Activity activity = placeManager.getActivity(placeRequest);
        if (activity instanceof AbstractWorkbenchScreenActivity) {
            AbstractWorkbenchScreenActivity screen = (AbstractWorkbenchScreenActivity) activity;
            if (screen.getMenus() != null) {
                expandedBar.addMenus(screen.getMenus(),
                                     menuBuilder);
            }
        }
    }

    private void setupCollapsedBar(UberfireDock targetDock,
                                   DocksCollapsedBar collapsedBar) {
        collapsedBar.setDockSelected(targetDock);
    }

    private void setupExpandedBar(UberfireDock targetDock,
                                  DocksBar docksBar,
                                  DocksExpandedBar expandedBar) {
        expandedBar.clear();
        expand(expandedBar);

        setupExpandedBarSize(targetDock,
                             docksBar);
        expandedBar.setup(targetDock.getLabel(),
                          createDockDeselectCommand(targetDock,
                                                    docksBar));
    }

    ParameterizedCommand<String> createDockDeselectCommand(final UberfireDock targetDock,
                                                           final DocksBar docksBar) {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute(String clickDockName) {
                if (targetDock != null) {
                    deselectDock(docksBar);
                    if (docksBar.isCollapsedBarInSingleMode()) {
                        expand(docksBar.getCollapsedBar());
                    }
                    uberfireDocksContainer.resize();
                    dockInteractionEvent.fire(new UberfireDocksInteractionEvent(targetDock,
                                                                                UberfireDocksInteractionEvent.InteractionType.DESELECTED));
                }
            }
        };
    }

    void deselectDock(DocksBar docksBar) {
        DocksCollapsedBar collapsedBar = docksBar.getCollapsedBar();
        DocksExpandedBar dockExpandedBar = docksBar.getExpandedBar();

        collapsedBar.deselectAllDocks();
        dockExpandedBar.clear();
        collapse(dockExpandedBar);
        collapse(docksBar.getDockResizeBar());
    }

    private void setupExpandedBarSize(UberfireDock targetDock,
                                      DocksBar docksBar) {

        DocksExpandedBar expandedBar = docksBar.getExpandedBar();

        if (thereIsASpecificSize(targetDock)) {
            expandedBar.setPanelSize(targetDock.getSize().intValue(),
                                     targetDock.getSize().intValue());
            uberfireDocksContainer.setWidgetSize(expandedBar,
                                                 targetDock.getSize());
        } else {
            int width = uberfireDocksContainer.getClientWidth();
            Double height = new Double(docksBar.getExpandedBarSize());
            expandedBar.setPanelSize(width,
                                     height.intValue());
            uberfireDocksContainer.setWidgetSize(expandedBar,
                                                 docksBar.getExpandedBarSize());
        }
    }

    private boolean thereIsASpecificSize(UberfireDock targetDock) {
        return targetDock.getSize() != null;
    }

    public void expand(DocksBar docksBar) {
        if (docksBar.hasDocksItems()) {
            expand(docksBar.getCollapsedBar());
        }
    }

    public void expand(UberfireDockPosition position) {
        DocksBar dockBar = getDockBar(position);
        if (dockBar.hasDocksItems()) {
            expand(dockBar.getCollapsedBar());
        }
    }

    public boolean isReady() {
        return uberfireDocksContainer.isReady();
    }

    void expand(Widget widget) {
        uberfireDocksContainer.show(widget);
    }

    private void collapse(DocksExpandedBar dock) {
        dock.clear();
        uberfireDocksContainer.hide(dock);
    }

    public List<DocksBar> getDocksBars() {
        return docks;
    }

    public void setIDEdock(Boolean IDEdock) {
        for (DocksBar dock : getDocksBars()) {
            dock.setupDnD();
        }
    }

    public void expand(UberfireDock dock) {
        DocksBar dockBar = getDockBar(dock);
        if (dockBar != null) {
            dockBar.expand(dock);
        }
    }
}
