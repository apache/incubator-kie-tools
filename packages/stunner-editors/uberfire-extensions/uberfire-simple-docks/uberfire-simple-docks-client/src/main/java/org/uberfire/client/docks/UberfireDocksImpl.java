/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.uberfire.client.docks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.docks.view.DocksBar;
import org.uberfire.client.docks.view.DocksBars;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockContainerReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDockReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class UberfireDocksImpl implements UberfireDocks {

    final List<UberfireDock> uberfireDocks = new ArrayList<>();
    final List<Command> delayedCommands = new ArrayList<>();
    final Set<UberfireDockPosition> disableDocks = new HashSet<>();

    private final DocksBars docksBars;
    private final Event<UberfireDockReadyEvent> dockReadyEvent;

    @Inject
    public UberfireDocksImpl(DocksBars docksBars,
                             Event<UberfireDockReadyEvent> dockReadyEvent) {
        this.docksBars = docksBars;
        this.dockReadyEvent = dockReadyEvent;
    }

    protected void setup(@Observes UberfireDockContainerReadyEvent event) {
        docksBars.setup();

        updateAllDocks();
        executeDelayedCommands();
        fireDockReadyEvent();
    }

    @Override
    public void add(UberfireDock... docks) {
        uberfireDocks.addAll(Arrays.asList(docks));
        clearAndCollapseDocks(docks);
    }

    private void fireDockReadyEvent() {
        dockReadyEvent.fire(new UberfireDockReadyEvent());
    }

    private void executeDelayedCommands() {
        delayedCommands.forEach(Command::execute);
    }

    @Override
    public void remove(UberfireDock... docks) {
        for (UberfireDock dock : docks) {
            uberfireDocks.remove(dock);
        }
        clearAndCollapseDocks(docks);
    }

    @Override
    public void open(UberfireDock dock) {
        executeOnDocks(dock.getDockPosition(),
                       () -> docksBars.open(dock));
    }

    private void executeOnDocks(UberfireDockPosition position,
                                Command open) {
        if (docksBars.isReady(position)) {
            open.execute();
        } else {
            addDelayedCommand(open);
        }
    }

    private void addDelayedCommand(Command delayedCommand) {
        delayedCommands.add(delayedCommand);
    }

    @Override
    public void close(UberfireDock dock) {
        executeOnDocks(dock.getDockPosition(),
                       () -> docksBars.close(dock));
    }

    @Override
    public void show(UberfireDockPosition position) {
        disableDocks.remove(position);
        executeOnDocks(position,
                       () -> showDock(position));
    }

    private void showDock(UberfireDockPosition position) {
        docksBars.clearAndHide(position);
        if (!uberfireDocks.isEmpty()) {
            for (UberfireDock dock : uberfireDocks) {
                if (dock.getDockPosition().equals(position)) {
                    docksBars.addDock(dock);
                }
            }
            docksBars.show(position);
        }
    }

    private void clearAndCollapseDocks(UberfireDock... docks) {
        if (docks != null) {
            List<UberfireDockPosition> processedPositions = new ArrayList<>();
            for (UberfireDock dock : docks) {
                if (!processedPositions.contains(dock.getDockPosition())) {
                    processedPositions.add(dock.getDockPosition());
                    if (docksBars.isReady(dock.getDockPosition())) {
                        docksBars.clearAndCollapseDocks(dock.getDockPosition());
                    }
                }
            }
        }
    }

    private void updateAllDocks() {
        docksBars.clearAndHideAllDocks();
        if (!uberfireDocks.isEmpty()) {
            uberfireDocks.forEach(docksBars::addDock);
            expandAllAvailableDocks();
        }
    }

    private void expandAllAvailableDocks() {
        for (DocksBar docksBar : docksBars.getDocksBars()) {
            if (!disableDocks.contains(docksBar.getPosition())) {
                docksBars.show(docksBar);
            }
        }
    }
}
