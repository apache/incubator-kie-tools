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

package org.uberfire.client.docks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class UberfireDocksImpl implements UberfireDocks {

    final Map<String, List<UberfireDock>> docksPerPerspective = new HashMap<>();
    final Map<String, List<Command>> delayedCommandsPerPerspective = new HashMap<>();

    final Map<String, Set<UberfireDockPosition>> disableDocksPerPerspective = new HashMap<String, Set<UberfireDockPosition>>();
    String currentPerspective;
    private DocksBars docksBars;
    private Event<UberfireDockReadyEvent> dockReadyEvent;

    @Inject
    public UberfireDocksImpl(DocksBars docksBars,
                             Event<UberfireDockReadyEvent> dockReadyEvent) {
        this.docksBars = docksBars;
        this.dockReadyEvent = dockReadyEvent;
    }

    protected void setup(@Observes UberfireDockContainerReadyEvent event) {
        docksBars.setup();
    }

    @Override
    public void add(UberfireDock... docks) {
        for (UberfireDock dock : docks) {
            if (dock.getAssociatedPerspective() != null) {
                List<UberfireDock> uberfireDocks = docksPerPerspective.get(dock.getAssociatedPerspective());
                if (uberfireDocks == null) {
                    uberfireDocks = new ArrayList<>();
                }
                uberfireDocks.add(dock);
                docksPerPerspective.put(dock.getAssociatedPerspective(),
                                        uberfireDocks);
            }
        }
        clearAndCollapseDocks(docks);
    }

    public void perspectiveChangeEvent(@Observes PerspectiveChange perspectiveChange) {
        this.currentPerspective = perspectiveChange.getIdentifier();
        updateAllDocks();
        executeDelayedCommands(perspectiveChange.getIdentifier());
        fireDockReadyEvent();
    }

    private void fireDockReadyEvent() {
        dockReadyEvent.fire(new UberfireDockReadyEvent(currentPerspective));
    }

    private void executeDelayedCommands(String perspective) {
        List<Command> commands = delayedCommandsPerPerspective.get(perspective);
        if (commands != null) {
            commands.forEach(c -> {
                c.execute();
            });
            delayedCommandsPerPerspective.remove(perspective);
        }
    }

    @Override
    public void remove(UberfireDock... docks) {
        for (UberfireDock dock : docks) {
            if (dock.getAssociatedPerspective() != null) {
                List<UberfireDock> uberfireDocks = docksPerPerspective.get(dock.getAssociatedPerspective());
                uberfireDocks.remove(dock);
                docksPerPerspective.put(dock.getAssociatedPerspective(),
                                        uberfireDocks);
            }
        }
        clearAndCollapseDocks(docks);
    }

    @Override
    public void open(UberfireDock dock) {
        executeOnDocks(dock.getAssociatedPerspective(),
                       dock.getDockPosition(),
                       () -> docksBars.open(dock));
    }

    private void executeOnDocks(String perspective,
                                UberfireDockPosition position,
                                Command open) {
        if (isCurrentPerspective(perspective) && docksBars.isReady(position)) {
            open.execute();
        } else {
            addDelayedCommand(perspective,
                              open);
        }
    }

    private boolean isCurrentPerspective(String perspective) {
        return perspective == currentPerspective;
    }

    private void addDelayedCommand(String perspective,
                                   Command delayedCommand) {
        List<Command> commands = delayedCommandsPerPerspective.get(perspective);
        if (commands == null) {
            commands = new ArrayList<>();
        }
        commands.add(delayedCommand);
        delayedCommandsPerPerspective.put(perspective,
                                          commands);
    }

    @Override
    public void close(UberfireDock dock) {
        executeOnDocks(dock.getAssociatedPerspective(),
                       dock.getDockPosition(),
                       () -> docksBars.close(dock));
    }

    @Override
    public void toggle(UberfireDock dock) {
        executeOnDocks(dock.getAssociatedPerspective(),
                       dock.getDockPosition(),
                       () -> docksBars.toggle(dock));
    }

    @Override
    public void hide(UberfireDockPosition position,
                     String perspectiveName) {
        addToDisableDocksList(position,
                              perspectiveName);
        executeOnDocks(perspectiveName,
                       position,
                       () -> docksBars.clearAndHide(position));
    }

    @Override
    public void show(UberfireDockPosition position,
                     String perspectiveName) {
        removeFromDisableDocksList(position,
                                   perspectiveName);
        executeOnDocks(perspectiveName,
                       position,
                       () -> showDock(position));
    }

    private void showDock(UberfireDockPosition position) {
        docksBars.clearAndHide(position);
        if (currentPerspective != null) {
            List<UberfireDock> docks = docksPerPerspective.get(currentPerspective);
            if (docks != null && !docks.isEmpty()) {
                for (UberfireDock dock : docks) {
                    if (dock.getDockPosition().equals(position)) {
                        docksBars.addDock(dock);
                    }
                }
                docksBars.show(position);
            }
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
        if (currentPerspective != null) {
            List<UberfireDock> activeDocks = docksPerPerspective.get(currentPerspective);
            if (activeDocks != null && !activeDocks.isEmpty()) {
                activeDocks.forEach(activeDock -> docksBars.addDock(activeDock));
                expandAllAvailableDocks();
            }
        }
    }

    private void expandAllAvailableDocks() {
        for (DocksBar docksBar : docksBars.getDocksBars()) {
            if (dockIsEnable(docksBar.getPosition())) {
                docksBars.show(docksBar);
            }
        }
    }

    private void addToDisableDocksList(UberfireDockPosition position,
                                       String perspectiveName) {
        Set<UberfireDockPosition> disableDocks = disableDocksPerPerspective.get(perspectiveName);
        if (disableDocks == null) {
            disableDocks = new HashSet<>();
            disableDocksPerPerspective.put(perspectiveName,
                                           disableDocks);
        }
        disableDocks.add(position);
    }

    private void removeFromDisableDocksList(UberfireDockPosition position,
                                            String perspectiveName) {
        Set<UberfireDockPosition> disableDocks = disableDocksPerPerspective.get(perspectiveName);
        if (disableDocks != null) {
            disableDocks.remove(position);
        }
    }

    private boolean dockIsEnable(UberfireDockPosition dockPosition) {
        Set<UberfireDockPosition> uberfireDockPositions = disableDocksPerPerspective.get(currentPerspective);
        if (uberfireDockPositions != null && uberfireDockPositions.contains(dockPosition)) {
            return false;
        }
        return true;
    }
}
