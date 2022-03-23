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

import org.uberfire.client.docks.view.bars.DocksCollapsedBar;
import org.uberfire.client.docks.view.bars.DocksExpandedBar;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;

public class DocksBar {

    private UberfireDockPosition position;

    private DocksCollapsedBar collapsedBar;
    private Double collapsedDefaultSize = 35.0;

    private DocksExpandedBar expandedBar;
    private Double expandedDefaultSize = 150.0;
    private Double expandedSize = expandedDefaultSize;

    private DockResizeBar dockResizeBar;
    private Double dockResizeBarDefaultSize = 2.0;
    private UberfireDock openDock;

    public DocksBar(UberfireDockPosition position) {
        this.position = position;
        setupChildBars(position);
    }

    protected void setupChildBars(UberfireDockPosition position) {
        this.collapsedBar = new DocksCollapsedBar(position);
        this.expandedBar = new DocksExpandedBar(position);
        this.dockResizeBar = new DockResizeBar(this);
    }

    public DocksCollapsedBar getCollapsedBar() {
        return collapsedBar;
    }

    public boolean isCollapsedBarInSingleMode() {
        return collapsedBar.singleDockMode();
    }

    public DocksExpandedBar getExpandedBar() {
        return expandedBar;
    }

    public Double getCollapsedBarSize() {
        return collapsedDefaultSize;
    }

    public double getExpandedBarSize() {
        return expandedSize;
    }

    public void setExpandedSize(Double expandedSize) {
        this.expandedSize = expandedSize;
    }

    public void clearAll() {
        collapsedBar.clear();
        expandedBar.clear();
    }

    public void addDock(final UberfireDock dock,
                        final ParameterizedCommand<String> openCommand,
                        final ParameterizedCommand<String> closeCommand) {
        if (collapsedBar != null) {
            collapsedBar.addDock(dock,
                                 openCommand,
                                 closeCommand);
        }
    }

    public UberfireDockPosition getPosition() {
        return position;
    }

    public boolean hasDocksItems() {
        return !collapsedBar.getDocksItems().isEmpty();
    }

    public DockResizeBar getDockResizeBar() {
        return dockResizeBar;
    }

    public Double getDockResizeBarDefaultSize() {
        return dockResizeBarDefaultSize;
    }

    public void configureResizeBar(ParameterizedCommand<Double> resizeCommand) {
        dockResizeBar.setup(resizeCommand);
    }

    public void open(UberfireDock dock) {
        if (hasDocksItems()) {
            collapsedBar.expand(dock);
        }
    }

    public void setOpenDock(UberfireDock openDock) {
        this.openDock = openDock;
    }

    public boolean isOpen() {
        return openDock != null;
    }

    public boolean isOpenWith(PlaceRequest placeRequest) {
        return isOpen() && openDock.getPlaceRequest().equals(placeRequest);
    }

    public void clearExpandedDock(UberfireDock expandedDock) {
        if (this.openDock != null && this.openDock.getPlaceRequest().equals(expandedDock.getPlaceRequest())) {
            this.openDock = null;
        }
    }
}
