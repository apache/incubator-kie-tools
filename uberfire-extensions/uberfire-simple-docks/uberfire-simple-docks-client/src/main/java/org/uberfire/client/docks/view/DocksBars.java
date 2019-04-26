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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.docks.view.bars.DocksCollapsedBar;
import org.uberfire.client.docks.view.bars.DocksExpandedBar;
import org.uberfire.client.docks.view.menu.MenuBuilder;
import org.uberfire.client.menu.AuthFilterMenuVisitor;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuItemPlain;
import org.uberfire.workbench.model.menu.impl.BaseMenuVisitor;

@Dependent
public class DocksBars {

    private PlaceManager placeManager;

    private MenuBuilder menuBuilder;

    private Event<UberfireDocksInteractionEvent> dockInteractionEvent;
    private UberfireDocksContainer uberfireDocksContainer;
    private List<DocksBar> docks = new ArrayList<>();
    private AuthorizationManager authorizationManager;
    private User identity;

    @Inject
    public DocksBars(PlaceManager placeManager,
                     MenuBuilder menuBuilder,
                     Event<UberfireDocksInteractionEvent> dockInteractionEvent,
                     UberfireDocksContainer uberfireDocksContainer,
                     AuthorizationManager authorizationManager,
                     User identity) {
        this.placeManager = placeManager;
        this.menuBuilder = menuBuilder;
        this.dockInteractionEvent = dockInteractionEvent;
        this.uberfireDocksContainer = uberfireDocksContainer;
        this.authorizationManager = authorizationManager;
        this.identity = identity;
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
                         createDockOpenCommand(dock,
                                               docksBar),
                         createDockCloseCommand(dock,
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

    public void clearAndCollapseDocks(final UberfireDockPosition position) {
        collapsePosition(position);
        clearPosition(position);
    }

    public void clearAndHideAllDocks() {
        clearAndCollapseDocks(null);
    }

    private void clearPosition(final UberfireDockPosition position) {
        getDocksBars().forEach(docksBar -> {
            if (position == null || docksBar.getPosition().equals(position)) {
                docksBar.clearAll();
            }
        });
    }

    private void collapsePosition(final UberfireDockPosition position) {
        getDocksBars().forEach(docksBar -> {
            // if position is null or equals the docksBar we must collapse the docksBar
            if (position == null || docksBar.getPosition().equals(position)) {
                hide(docksBar);
            }
        });
    }

    private void hide(DocksBar docksBar) {
        uberfireDocksContainer.hide(docksBar.getCollapsedBar());
        uberfireDocksContainer.hide(docksBar.getExpandedBar());
        uberfireDocksContainer.hide(docksBar.getDockResizeBar());
    }

    void hide(Widget bar) {
        uberfireDocksContainer.hide(bar);
    }

    public void clearAndHide(UberfireDockPosition position) {
        DocksBar dockBar = getDockBar(position);
        dockBar.clearAll();
        hide(dockBar);

        resizeDeferred();
    }

    void resizeDeferred() {
        Scheduler.get().scheduleDeferred(() -> uberfireDocksContainer.resize());
    }

    ParameterizedCommand<String> createDockOpenCommand(final UberfireDock targetDock,
                                                       final DocksBar docksBar) {
        return clickDockName -> {
            if (targetDock != null) {
                openDock(targetDock,
                         docksBar);
                if (docksBar.isCollapsedBarInSingleMode()) {
                    hide(docksBar.getCollapsedBar());
                }
                uberfireDocksContainer.resize();
                dockInteractionEvent.fire(new UberfireDocksInteractionEvent(targetDock,
                                                                            UberfireDocksInteractionEvent.InteractionType.OPENED));
            }
        };
    }

    void openDock(UberfireDock targetDock,
                  DocksBar docksBar) {
        docksBar.setOpenDock(targetDock);
        DocksCollapsedBar collapsedBar = docksBar.getCollapsedBar();
        DocksExpandedBar expandedBar = docksBar.getExpandedBar();
        PlaceRequest placeRequest = targetDock.getPlaceRequest();

        setupCollapsedBar(targetDock,
                          collapsedBar);
        setupExpandedBar(targetDock,
                         docksBar,
                         expandedBar);
        show(docksBar.getDockResizeBar());
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
            screen.getMenus(menus -> {
                if (menus != null) {
                    menus.accept(new AuthFilterMenuVisitor(authorizationManager,
                            identity, new BaseMenuVisitor() {

                                @Override
                                public void visit(MenuItemPlain menuItemPlain) {
                                    expandedBar.addContextMenuItem(menuBuilder.makeItem(menuItemPlain, true));
                                }
                                @Override
                                public void visit(MenuItemCommand menuItemCommand) {
                                    expandedBar.addContextMenuItem(menuBuilder.makeItem(menuItemCommand, true));
                                }
                                @Override
                                public void visit(MenuItemPerspective menuItemPerspective) {
                                    expandedBar.addContextMenuItem(menuBuilder.makeItem(menuItemPerspective, true));
                                }
                                @Override
                                public void visit(MenuCustom<?> menuCustom) {
                                    expandedBar.addContextMenuItem(menuBuilder.makeItem(menuCustom, true));
                                }
                            }));
                }
            });
        }
    }

    private void setupCollapsedBar(UberfireDock targetDock,
                                   DocksCollapsedBar collapsedBar) {
        collapsedBar.setDockClosed(targetDock);
    }

    private void setupExpandedBar(UberfireDock targetDock,
                                  DocksBar docksBar,
                                  DocksExpandedBar expandedBar) {
        expandedBar.clear();
        show(expandedBar);

        setupExpandedBarSize(targetDock,
                             docksBar);
        expandedBar.setup(targetDock.getLabel(),
                          createDockCloseCommand(targetDock,
                                                 docksBar));
    }

    public void open(UberfireDock dock) {
        DocksBar dockBar = getDockBar(dock);
        if (dockBar != null) {
            dockBar.open(dock);
        }
    }

    public void close(UberfireDock dock) {
        DocksBar dockBar = getDockBar(dock);
        if (dockBar != null) {
            if (isOpenWith(dock,
                           dockBar)) {
                closeDockProcess(dock,
                                 dockBar);
            }
        }
    }

    public void toggle(UberfireDock dock) {
        DocksBar dockBar = getDockBar(dock);
        if (dockBar != null) {
            if (isOpenWith(dock,
                           dockBar)) {
                closeDockProcess(dock,
                                 dockBar);
            } else {
                dockBar.open(dock);
            }
        }
    }

    boolean isOpenWith(UberfireDock dock,
                               DocksBar dockBar) {
        return dockBar.isOpenWith(dock.getPlaceRequest());
    }

    ParameterizedCommand<String> createDockCloseCommand(final UberfireDock targetDock,
                                                        final DocksBar docksBar) {
        return clickedDockName -> closeDockProcess(targetDock,
                                                   docksBar);
    }

    void closeDockProcess(UberfireDock targetDock,
                                  DocksBar docksBar) {
        if (targetDock != null) {
            closeDock(targetDock,
                      docksBar);
            if (docksBar.isCollapsedBarInSingleMode()) {
                show(docksBar.getCollapsedBar());
            }
            uberfireDocksContainer.resize();
            dockInteractionEvent.fire(new UberfireDocksInteractionEvent(targetDock,
                                                                        UberfireDocksInteractionEvent.InteractionType.CLOSED));
        }
    }

    void closeDock(UberfireDock dock,
                   DocksBar docksBar) {
        DocksCollapsedBar collapsedBar = docksBar.getCollapsedBar();
        DocksExpandedBar dockExpandedBar = docksBar.getExpandedBar();
        docksBar.clearExpandedDock(dock);
        collapsedBar.closeAllDocks();
        dockExpandedBar.clear();
        hide(dockExpandedBar);
        hide(docksBar.getDockResizeBar());
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

    public void show(DocksBar docksBar) {
        if (docksBar.hasDocksItems()) {
            show(docksBar.getCollapsedBar());
        }
    }

    public void show(UberfireDockPosition position) {
        DocksBar dockBar = getDockBar(position);
        if (dockBar.hasDocksItems()) {
            show(dockBar.getCollapsedBar());
        }
    }

    public boolean isReady(UberfireDockPosition dockPosition) {
        return uberfireDocksContainer.isReady() && docksBarIsReady(dockPosition);
    }

    private boolean docksBarIsReady(UberfireDockPosition dockPosition) {
        DocksBar dockBar = getDockBar(dockPosition);
        return dockBar != null;
    }

    void show(Widget widget) {
        uberfireDocksContainer.show(widget);
    }

    private void hide(DocksExpandedBar dock) {
        dock.clear();
        uberfireDocksContainer.hide(dock);
    }

    public List<DocksBar> getDocksBars() {
        return docks;
    }
}
