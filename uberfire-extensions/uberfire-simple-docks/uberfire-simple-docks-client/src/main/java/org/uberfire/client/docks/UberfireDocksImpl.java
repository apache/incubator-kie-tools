package org.uberfire.client.docks;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class UberfireDocksImpl implements UberfireDocks {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private SyncBeanManager iocManager;

    private DockLayoutPanel rootContainer;

    DocksBar southCollapsed;
    DocksExpandedBar southExpanded;

    DocksBar westCollapsed;
    DocksExpandedBar westExpanded;

    DocksBar eastCollapsed;
    DocksExpandedBar eastExpanded;

    Map<String, Set<UberfireDock>> docksPerPerspective = new HashMap<String, Set<UberfireDock>>();

    Set<UberfireDock> avaliableDocks = new HashSet<UberfireDock>();

    private String currentPerspective;

    Map<String, Set<UberfireDockPosition>> disableDocksPerPerspective = new HashMap<String, Set<UberfireDockPosition>>();

    @PostConstruct
    public void init() {
        createSouthDock();
        createEastDock();
        createWestDock();
    }

    @Override
    public void setup(DockLayoutPanel rootContainer) {
        this.rootContainer = rootContainer;
        //layoutPanel Has To Have At Least One Displayed Component
        rootContainer.addSouth(new FlowPanel(), 1);

        rootContainer.addSouth(southCollapsed, southCollapsed.widgetSize());
        rootContainer.addSouth(southExpanded, southExpanded.defaultWidgetSize());

        rootContainer.addWest(new FlowPanel(), 1);
        rootContainer.addWest(westCollapsed, westCollapsed.widgetSize());
        rootContainer.addWest(westExpanded, westExpanded.defaultWidgetSize());

        rootContainer.addEast(new FlowPanel(), 1);
        rootContainer.addEast(eastCollapsed, eastCollapsed.widgetSize());
        rootContainer.addEast(eastExpanded, eastExpanded.defaultWidgetSize());

        updateDocks();
    }

    @Override
    public void disable(UberfireDockPosition position, String perspectiveName) {
        if (docksContainerIsAttached()) {
            DocksBar docksBar = resolveDockBar(position.name());
            DocksExpandedBar docksExpandedBar = resolveDockExpandedBar(position.name());
            collapse(docksBar);
            collapse(docksExpandedBar);

            addToDisableDocksList(position, perspectiveName);

        }
    }

    private void addToDisableDocksList(UberfireDockPosition position, String perspectiveName) {
        Set<UberfireDockPosition> disableDocks = disableDocksPerPerspective.get(perspectiveName);
        if (disableDocks == null) {
            disableDocks = new HashSet<UberfireDockPosition>();
            disableDocksPerPerspective.put(perspectiveName, disableDocks);
        }
        disableDocks.add(position);
    }

    @Override
    public void enable(UberfireDockPosition position, String perspectiveName) {
        removeFromDisableDocksList(position, perspectiveName);
        DocksBar docksBar = resolveDockBar(position.name());
        expand(docksBar);
    }

    private void removeFromDisableDocksList(UberfireDockPosition position, String perspectiveName) {
        Set<UberfireDockPosition> disableDocks = disableDocksPerPerspective.get(perspectiveName);
        if (disableDocks != null) {
            disableDocks.remove(position);
        }
    }


    @Override
    public void register(UberfireDock... docks) {
        for (UberfireDock dock : docks) {
            avaliableDocks.add(dock);
            if (dock.getAssociatedPerspective() != null) {
                Set<UberfireDock> uberfireDocks = docksPerPerspective.get(dock.getAssociatedPerspective());
                if (uberfireDocks == null) {
                    uberfireDocks = new HashSet<UberfireDock>();
                }
                uberfireDocks.add(dock);
                docksPerPerspective.put(dock.getAssociatedPerspective(), uberfireDocks);
            }
        }
        updateAvaliableDocksMenu();
    }

    private void updateAvaliableDocksMenu() {
        southCollapsed.updateAvaliableDocksMenu(avaliableDocks, createOpenDockLink());
    }

    public void perspectiveChangeEvent(@Observes PerspectiveChange perspectiveChange) {
        this.currentPerspective = perspectiveChange.getIdentifier();
        if (docksContainerIsAttached()) {
            updateDocks();
        }
    }

    private void updateDocks() {
        clearAndCollapseAllDocks();
        updateDockContent();
    }

    private boolean docksContainerIsAttached() {
        return rootContainer != null;
    }

    private void clearAndCollapseAllDocks() {
        collapseAll();
        southCollapsed.clearDocks();
        westCollapsed.clearDocks();
        eastCollapsed.clearDocks();
    }

    private void updateDockContent() {
        if (currentPerspective != null) {
            Set<UberfireDock> docks = docksPerPerspective.get(currentPerspective);
            if (docks != null && !docks.isEmpty()) {
                for (UberfireDock dock : docks) {
                    DocksBar docksBar = resolveDockBar(dock.getDockPosition().name());
                    DocksExpandedBar docksExpandedBar = resolveDockExpandedBar(dock.getDockPosition().name());
                    if (docksBar != null) {
                        docksBar.addDock(dock, createDockSelectCommand(docksBar, docksExpandedBar), createDockDeselectCommand(docksBar, docksExpandedBar));
                    }
                }
                expandAllCollapsed();
            }
        }
    }

    private void createEastDock() {
        eastExpanded = new DocksExpandedBar(UberfireDockPosition.EAST);
        eastCollapsed = new DocksBar(UberfireDockPosition.EAST, createDropHandler(UberfireDockPosition.EAST));
    }

    private void createWestDock() {
        westExpanded = new DocksExpandedBar(UberfireDockPosition.WEST);
        westCollapsed = new DocksBar(UberfireDockPosition.WEST, createDropHandler(UberfireDockPosition.WEST));
    }

    private void createSouthDock() {
        southExpanded = new DocksExpandedBar(UberfireDockPosition.SOUTH);
        southCollapsed = new DocksBar(UberfireDockPosition.SOUTH, createDropHandler(UberfireDockPosition.SOUTH));
    }

    private ParameterizedCommand<String> createDropHandler(final UberfireDockPosition targetDock) {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute(String dropName) {
                UberfireDock dock = searchForDockByDockItemName(dropName);
                if (dock != null) {
                    moveDock(dock, targetDock);
                }
            }
        };
    }

    void moveDock(UberfireDock dock,
                  UberfireDockPosition targetDock) {
        DocksBar oldDockBar = resolveDockBar(dock.getDockPosition().name());
        oldDockBar.removeDock(dock);

        dock.setUberfireDockPosition(targetDock);
        avaliableDocks.add(dock);

        if (dock.getAssociatedPerspective() != null) {
            Set<UberfireDock> uberfireDocks = docksPerPerspective.get(dock.getAssociatedPerspective());
            uberfireDocks.add(dock);
        }

        DocksBar targetDockBar = resolveDockBar(targetDock.name());
        DocksExpandedBar targetExpandedDockBar = resolveDockExpandedBar(targetDock.name());
        targetDockBar.addDock(dock, createDockSelectCommand(targetDockBar, targetExpandedDockBar), createDockDeselectCommand(targetDockBar, targetExpandedDockBar));
    }

    private ParameterizedCommand<String> createDockSelectCommand(final DocksBar dockBar,
                                                                 final DocksExpandedBar dockExpandedBar) {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute(String clickDockName) {
                UberfireDock targetDock = searchForDockByDockItemName(clickDockName);
                if (targetDock != null) {
                    selectDock(targetDock, dockBar, dockExpandedBar);
                }
            }
        };
    }

    private void selectDock(UberfireDock targetDock,
                            DocksBar dockBar,
                            DocksExpandedBar dockExpandedBar) {
        dockBar.setDockSelected(targetDock.getIdentifier());
        dockExpandedBar.clear();
        expand(dockExpandedBar);

        setupExpandedBarSize(targetDock, dockExpandedBar);

        dockExpandedBar.setup(targetDock.getIdentifier(), createDockDeselectCommand(dockBar, dockExpandedBar));
        placeManager.goTo(new DefaultPlaceRequest(targetDock.getIdentifier()), dockExpandedBar.targetPanel());
    }

    private void setupExpandedBarSize(UberfireDock targetDock, DocksExpandedBar dockExpandedBar) {
        int width = rootContainer.getElement().getClientWidth();
        if (thereIsASpecificSize(targetDock)) {
            dockExpandedBar.setPanelSize(targetDock.getSize().intValue(), targetDock.getSize().intValue());
            rootContainer.setWidgetSize(dockExpandedBar, targetDock.getSize());
        } else {
            Double height = new Double(dockExpandedBar.defaultWidgetSize());
            dockExpandedBar.setPanelSize(width, height.intValue());
            rootContainer.setWidgetSize(dockExpandedBar, dockExpandedBar.defaultWidgetSize());
        }
    }

    private boolean thereIsASpecificSize(UberfireDock targetDock) {
        return targetDock.getSize() != null;
    }

    private ParameterizedCommand<String> createDockDeselectCommand(final DocksBar dockBar,
                                                                   final DocksExpandedBar dockExpandedBar) {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute(String clickDockName) {
                UberfireDock targetDock = searchForDockByDockItemName(clickDockName);
                if (targetDock != null) {
                    deselectDock(dockBar, dockExpandedBar);
                }
            }
        };
    }

    private void deselectDock(DocksBar dockBar,
                              DocksExpandedBar dockExpandedBar) {
        dockBar.deselectAllDocks();
        dockExpandedBar.clear();
        collapse(dockExpandedBar);
    }

    private ParameterizedCommand<String> createOpenDockLink() {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute(String clickDockName) {
                UberfireDock targetDock = searchForDockByDockItemName(clickDockName);
                if (targetDock != null) {
                    DocksBar dockBar = resolveDockBar(targetDock.getDockPosition().name());
                    DocksExpandedBar dockExpandedBar = resolveDockExpandedBar(targetDock.getDockPosition().name());
                    if (dockBar != null && dockExpandedBar != null) {
                        selectDock(targetDock, dockBar, dockExpandedBar);
                    }
                }
            }
        };
    }

    private DocksExpandedBar resolveDockExpandedBar(String dockPosition) {
        if (dockPosition.equalsIgnoreCase(UberfireDockPosition.SOUTH.name())) {
            return southExpanded;
        } else if (dockPosition.equalsIgnoreCase(UberfireDockPosition.WEST.name())) {
            return westExpanded;
        } else {
            return eastExpanded;
        }
    }

    private DocksBar resolveDockBar(String dockPosition) {
        if (dockPosition.equalsIgnoreCase(UberfireDockPosition.SOUTH.name())) {
            return southCollapsed;
        } else if (dockPosition.equalsIgnoreCase(UberfireDockPosition.WEST.name())) {
            return westCollapsed;
        } else {
            return eastCollapsed;
        }
    }

    UberfireDock searchForDockByDockItemName(String clickDockName) {
        UberfireDock targetDock = null;
        for (UberfireDock avaliableDock : avaliableDocks) {
            if (avaliableDock.getIdentifier().equalsIgnoreCase(clickDockName)) {
                targetDock = avaliableDock;
            }
        }
        return targetDock;
    }

    private void collapseAll() {
        collapse(southExpanded);
        collapse(eastExpanded);
        collapse(westExpanded);
        collapse(southCollapsed);
        collapse(eastCollapsed);
        collapse(westCollapsed);
    }

    private void expandAllCollapsed() {
        expand(eastCollapsed);
        expand(westCollapsed);
        expand(southCollapsed);
    }

    private void collapse(DocksBar dock) {
        rootContainer.setWidgetHidden(dock, true);
    }

    private void expand(DocksBar dock) {
        if (dockIsEnable(dock.getPosition())) {
            rootContainer.setWidgetHidden(dock, false);
        }
    }

    private void expand(DocksExpandedBar dock) {
        if (dockIsEnable(dock.getPosition())) {
            rootContainer.setWidgetHidden(dock, false);
        }

    }

    private boolean dockIsEnable(UberfireDockPosition dockPosition) {
        Set<UberfireDockPosition> uberfireDockPositions = disableDocksPerPerspective.get(currentPerspective);
        if (uberfireDockPositions != null && uberfireDockPositions.contains(dockPosition)) {
            return false;
        }
        return true;
    }

    private void collapse(DocksExpandedBar dock) {
        dock.clear();
        rootContainer.setWidgetHidden(dock, true);
    }

}
