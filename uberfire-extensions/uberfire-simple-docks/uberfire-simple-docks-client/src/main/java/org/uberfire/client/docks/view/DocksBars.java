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
import javax.inject.Inject;

import org.uberfire.client.docks.view.bars.DocksCollapsedBar;
import org.uberfire.client.docks.view.bars.DocksExpandedBar;
import org.uberfire.client.docks.view.menu.MenuBuilder;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class DocksBars {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private MenuBuilder menuBuilder;

    private List<DocksBar> docks = new ArrayList<DocksBar>();

    DockLayoutPanel rootContainer;
    Command resizeCommand;

    public void setup(DockLayoutPanel rootContainer, Command resizeCommand) {
        this.rootContainer = rootContainer;
        this.resizeCommand = resizeCommand;
        for (UberfireDockPosition uberfireDockPosition : UberfireDockPosition.values()) {
            createDock(uberfireDockPosition);
        }
    }

    private void createDock(UberfireDockPosition position) {
        DocksBar docksBar = createDockBar(position);

        if (position == UberfireDockPosition.SOUTH) {
            rootContainer.addSouth(docksBar.getCollapsedBar(), docksBar.getCollapsedBarSize());
            rootContainer.addSouth(docksBar.getExpandedBar(), docksBar.getExpandedBarSize());
            rootContainer.addSouth(docksBar.getDockResizeBar(), docksBar.getDockResizeBarDefaultSize());
        } else if (position == UberfireDockPosition.EAST) {
            rootContainer.addEast(docksBar.getCollapsedBar(), docksBar.getCollapsedBarSize());
            rootContainer.addEast(docksBar.getExpandedBar(), docksBar.getExpandedBarSize());
            rootContainer.addEast(docksBar.getDockResizeBar(), docksBar.getDockResizeBarDefaultSize());
        } else if (position == UberfireDockPosition.WEST) {
            rootContainer.addWest(docksBar.getCollapsedBar(), docksBar.getCollapsedBarSize());
            rootContainer.addWest(docksBar.getExpandedBar(), docksBar.getExpandedBarSize());
            rootContainer.addWest(docksBar.getDockResizeBar(), docksBar.getDockResizeBarDefaultSize());
        }
        docksBar.configureResizeBar(createResizeCommand(docksBar));
        docks.add(docksBar);
    }


    private DocksBar createDockBar(UberfireDockPosition position) {
        DocksBar docksBar = new DocksBar(position);
        return docksBar;
    }

    public void addDock(UberfireDock dock) {
        DocksBar docksBar = getDockBar(dock);
        docksBar.addDock(dock, createDockSelectCommand(dock, docksBar), createDockDeselectCommand(dock, docksBar));
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
                if (sizeIsValid(size, docksBar)) {
                    docksBar.setExpandedSize(size);
                    rootContainer.setWidgetSize(docksBar.getExpandedBar(), docksBar.getExpandedBarSize());
                    docksBar.getExpandedBar().setupDockContentSize();
                    resizeCommand.execute();
                }
            }
        };
    }

    boolean sizeIsValid(Double size, DocksBar docksBar) {
        int max = calculateMaxSize(docksBar);
        int minVisibleSize = 1;
        return size > minVisibleSize && size < max;
    }

    private int calculateMaxSize(DocksBar docksBar) {
        UberfireDockPosition position = docksBar.getPosition();
        int collapsedSize = new Double(docksBar.getCollapsedBarSize()).intValue();

        int max = 0;
        if (position == UberfireDockPosition.SOUTH) {
            max = rootContainer.getOffsetHeight() - collapsedSize;
        } else {
            max = rootContainer.getOffsetWidth() - 2 * collapsedSize;
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
        rootContainer.setWidgetHidden(docksBar.getCollapsedBar(), true);
        rootContainer.setWidgetHidden(docksBar.getExpandedBar(), true);
        rootContainer.setWidgetHidden(docksBar.getDockResizeBar(), true);
    }

    void collapse(Widget bar) {
        rootContainer.setWidgetHidden(bar, true);
    }

    public void clearAndCollapse(UberfireDockPosition position) {
        DocksBar dockBar = getDockBar(position);
        dockBar.clearAll();
        collapse(dockBar);
        
        resizeDeferred();
    }
    
    void resizeDeferred() {
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                resizeCommand.execute();
            }
        } );
    }

    ParameterizedCommand<String> createDockSelectCommand(final UberfireDock targetDock, final DocksBar docksBar) {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute(String clickDockName) {
                if (targetDock != null) {
                    selectDock(targetDock, docksBar);
                    if (docksBar.isCollapsedBarInSingleMode()) {
                        collapse(docksBar.getCollapsedBar());
                    }
                    resizeCommand.execute();
                }
            }
        };
    }

    void selectDock(UberfireDock targetDock,
                            DocksBar docksBar) {
        DocksCollapsedBar collapsedBar = docksBar.getCollapsedBar();
        DocksExpandedBar expandedBar = docksBar.getExpandedBar();
        PlaceRequest placeRequest = targetDock.getPlaceRequest();

        setupCollapsedBar(targetDock, collapsedBar);
        setupExpandedBar(targetDock, docksBar, expandedBar);
        expand(docksBar.getDockResizeBar());
        placeManager.goTo(new DefaultPlaceRequest(placeRequest.getIdentifier()), expandedBar.targetPanel());

        lookUpContextMenus(placeRequest, docksBar.getExpandedBar());

    }

    private void lookUpContextMenus(PlaceRequest placeRequest, DocksExpandedBar expandedBar) {
        Activity activity = placeManager.getActivity(placeRequest);
        if (activity instanceof AbstractWorkbenchScreenActivity) {
            AbstractWorkbenchScreenActivity screen = (AbstractWorkbenchScreenActivity) activity;
            if (screen.getMenus() != null) {
               expandedBar.addMenus(screen.getMenus(), menuBuilder);
            }
        }
    }

    private void setupCollapsedBar(UberfireDock targetDock, DocksCollapsedBar collapsedBar) {
        collapsedBar.setDockSelected(targetDock);
    }

    private void setupExpandedBar(UberfireDock targetDock, DocksBar docksBar, DocksExpandedBar expandedBar) {
        expandedBar.clear();
        expand(expandedBar);

        setupExpandedBarSize(targetDock, docksBar);
        expandedBar.setup(targetDock.getLabel(), createDockDeselectCommand(targetDock, docksBar));
    }

    ParameterizedCommand<String> createDockDeselectCommand(final UberfireDock targetDock, final DocksBar docksBar) {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute(String clickDockName) {
                if (targetDock != null) {
                    deselectDock(docksBar);
                    if (docksBar.isCollapsedBarInSingleMode()) {
                        expand(docksBar.getCollapsedBar());
                    }
                    resizeCommand.execute();
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

    private void setupExpandedBarSize(UberfireDock targetDock, DocksBar docksBar) {

        DocksExpandedBar expandedBar = docksBar.getExpandedBar();

        if (thereIsASpecificSize(targetDock)) {
            expandedBar.setPanelSize(targetDock.getSize().intValue(), targetDock.getSize().intValue());
            rootContainer.setWidgetSize(expandedBar, targetDock.getSize());
        } else {
            int width = rootContainer.getElement().getClientWidth();
            Double height = new Double(docksBar.getExpandedBarSize());
            expandedBar.setPanelSize(width, height.intValue());
            rootContainer.setWidgetSize(expandedBar, docksBar.getExpandedBarSize());
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
        return rootContainer != null;
    }

    void expand(Widget dock) {
        rootContainer.setWidgetHidden(dock, false);
    }


    private void collapse(DocksExpandedBar dock) {
        dock.clear();
        rootContainer.setWidgetHidden(dock, true);
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
