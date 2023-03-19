/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.events.DropPlaceEvent;
import org.uberfire.client.workbench.events.PanelFocusEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.client.workbench.panels.DockingWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.debug.Debug;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.CustomPanelDefinition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.CustomPanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.menu.Menus;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.plugin.PluginUtil.ensureIterable;

/**
 * Standard implementation of {@link PanelManager}.
 */
@ApplicationScoped
public class PanelManagerImpl implements PanelManager {

    protected final Map<PartDefinition, WorkbenchPartPresenter> mapPartDefinitionToPresenter = new HashMap<PartDefinition, WorkbenchPartPresenter>();
    protected final Map<PanelDefinition, WorkbenchPanelPresenter> mapPanelDefinitionToPresenter = new HashMap<PanelDefinition, WorkbenchPanelPresenter>();
    /**
     * Remembers which HasWidgets contains each existing custom panel. Items are removed from this map when the panels
     * are closed/removed.
     */
    protected final Map<PanelDefinition, HasWidgets> customPanels = new HashMap<PanelDefinition, HasWidgets>();
    /**
     * Remembers which HTMLElements contain each existing custom panel. Items are removed from this map when the panels
     * are closed/removed.
     */
    protected final Map<PanelDefinition, HTMLElement> customPanelsInsideHTMLElements = new HashMap<>();
    /**
     * Remembers which Elemental2 HTMLElements contain each existing custom panel. Items are removed from this map when the panels
     * are closed/removed.
     */
    protected final Map<PanelDefinition, elemental2.dom.HTMLElement> customPanelsInsideElemental2HTMLElements = new HashMap<>();

    protected Event<PlaceGainFocusEvent> placeGainFocusEvent;
    protected Event<PlaceLostFocusEvent> placeLostFocusEvent;
    protected Event<PanelFocusEvent> panelFocusEvent;
    protected Event<SelectPlaceEvent> selectPlaceEvent;
    protected Event<PlaceMaximizedEvent> placeMaximizedEvent;
    protected Event<PlaceMinimizedEvent> placeMinimizedEvent;
    protected Event<PlaceHiddenEvent> placeHiddenEvent;
    protected SyncBeanManager iocManager;
    protected Instance<PlaceManager> placeManager;
    /**
     * Description that the current root panel was created from. Presently, this is a mutable data structure and the
     * whole UF framework tries to keep this in sync with the reality (syncing each change from DOM -> Widget ->
     * UberView -> Presenter -> Definition). This may change in the future. See UF-117.
     */
    protected PanelDefinition rootPanelDef = null;
    protected PartDefinition activePart = null;
    LayoutSelection layoutSelection;
    private BeanFactory beanFactory;
    private Elemental2DomUtil elemental2DomUtil;
    /**
     * Registration for the native preview handler that watches for ^M events and maximizes/restores the current panel.
     */
    private HandlerRegistration globalHandlerRegistration;

    /**
     * The currently maximized panel. Set to null when a panel is not maximized.
     */
    private WorkbenchPanelPresenter maximizedPanel = null;

    @Inject
    public PanelManagerImpl(
            Event<PlaceGainFocusEvent> placeGainFocusEvent,
            Event<PlaceLostFocusEvent> placeLostFocusEvent,
            Event<PanelFocusEvent> panelFocusEvent,
            Event<SelectPlaceEvent> selectPlaceEvent,
            Event<PlaceMaximizedEvent> placeMaximizedEvent,
            Event<PlaceMinimizedEvent> placeMinimizedEventEvent,
            Event<PlaceHiddenEvent> placeHiddenEvent,
            SyncBeanManager iocManager,
            Instance<PlaceManager> placeManager,
            LayoutSelection layoutSelection,
            BeanFactory beanFactory,
            Elemental2DomUtil elemental2DomUtil) {
        this.placeGainFocusEvent = placeGainFocusEvent;
        this.placeLostFocusEvent = placeLostFocusEvent;
        this.panelFocusEvent = panelFocusEvent;
        this.selectPlaceEvent = selectPlaceEvent;
        this.placeMaximizedEvent = placeMaximizedEvent;
        this.placeMinimizedEvent = placeMinimizedEventEvent;
        this.placeHiddenEvent = placeHiddenEvent;
        this.iocManager = iocManager;
        this.placeManager = placeManager;
        this.layoutSelection = layoutSelection;
        this.beanFactory = beanFactory;
        this.elemental2DomUtil = elemental2DomUtil;
    }

    @PostConstruct
    private void setup() {
        globalHandlerRegistration = com.google.gwt.user.client.Event.addNativePreviewHandler(new NativePreviewHandler() {

            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event) {
                if (event.getTypeInt() == com.google.gwt.user.client.Event.ONKEYPRESS &&
                        event.getNativeEvent().getCharCode() == 'm' &&
                        event.getNativeEvent().getCtrlKey()) {
                    if (maximizedPanel != null) {
                        maximizedPanel.unmaximize();
                        maximizedPanel = null;
                    } else if (activePart != null) {
                        WorkbenchPanelPresenter activePanelPresenter = mapPanelDefinitionToPresenter.get(activePart.getParentPanel());
                        activePanelPresenter.maximize();
                        maximizedPanel = activePanelPresenter;
                    }
                }
            }
        });
    }

    @PreDestroy
    private void teardown() {
        globalHandlerRegistration.removeHandler();
    }

    protected BeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    public PanelDefinition getRoot() {
        return this.rootPanelDef;
    }

    @Override
    public void setRoot(PerspectiveActivity activity,
                        PanelDefinition root) {
        checkNotNull("root",
                     root);

        final WorkbenchPanelPresenter oldRootPanelPresenter = mapPanelDefinitionToPresenter.remove(rootPanelDef);

        if (!mapPanelDefinitionToPresenter.isEmpty()) {
            String message = "Can't replace current root panel because it is not empty. The following panels remain: " + mapPanelDefinitionToPresenter;
            mapPanelDefinitionToPresenter.put(rootPanelDef,
                                              oldRootPanelPresenter);
            throw new IllegalStateException(message);
        }

        HasWidgets perspectiveContainer = layoutSelection.get().getPerspectiveContainer();
        perspectiveContainer.clear();

        getBeanFactory().destroy(oldRootPanelPresenter);

        this.rootPanelDef = root;
        WorkbenchPanelPresenter newPresenter = mapPanelDefinitionToPresenter.get(root);
        if (newPresenter == null) {
            newPresenter = getBeanFactory().newRootPanel(activity,
                                                         root);
            mapPanelDefinitionToPresenter.put(root,
                                              newPresenter);
        }
        perspectiveContainer.add(newPresenter.getPanelView().asWidget());
    }

    @Override
    public void addWorkbenchPart(final PlaceRequest place,
                                 final PartDefinition partDef,
                                 final PanelDefinition panelDef,
                                 final Menus menus,
                                 final UIPart uiPart,
                                 final String contextId,
                                 final Integer preferredWidth,
                                 final Integer preferredHeight) {
        checkNotNull("panel",
                     panelDef);

        final WorkbenchPanelPresenter panelPresenter = mapPanelDefinitionToPresenter.get(panelDef);
        if (panelPresenter == null) {
            throw new IllegalArgumentException("Target panel is not part of the layout");
        }

        WorkbenchPartPresenter partPresenter = mapPartDefinitionToPresenter.get(partDef);
        if (partPresenter == null) {
            partPresenter = getBeanFactory().newWorkbenchPart(menus,
                                                              uiPart.getTitle(),
                                                              uiPart.getTitleDecoration(),
                                                              partDef,
                                                              panelPresenter.getPartType());
            partPresenter.setWrappedWidget(uiPart.getWidget());
            partPresenter.setContextId(contextId);
            mapPartDefinitionToPresenter.put(partDef,
                                             partPresenter);
        }

        panelPresenter.addPart(partPresenter,
                               contextId);
        if (panelPresenter.getParent() instanceof DockingWorkbenchPanelPresenter) {
            DockingWorkbenchPanelPresenter parent = (DockingWorkbenchPanelPresenter) panelPresenter.getParent();
            parent.setChildSize(panelPresenter,
                                preferredWidth,
                                preferredHeight);
        }

        //Select newly inserted part
        selectPlaceEvent.fire(new SelectPlaceEvent(place));
    }

    @Override
    public boolean removePartForPlace(PlaceRequest toRemove) {
        final PartDefinition removedPart = getPartForPlace(toRemove);
        if (removedPart != null) {
            removePart(removedPart);
            return true;
        }
        return false;
    }

    @Override
    public PanelDefinition addWorkbenchPanel(final PanelDefinition targetPanel,
                                             final Position position,
                                             final Integer height,
                                             final Integer width,
                                             final Integer minHeight,
                                             final Integer minWidth) {
        final PanelDefinitionImpl childPanel = new PanelDefinitionImpl(PanelDefinition.PARENT_CHOOSES_TYPE);

        childPanel.setHeight(height);
        childPanel.setWidth(width);
        childPanel.setMinHeight(minHeight);
        childPanel.setMinWidth(minWidth);
        return addWorkbenchPanel(targetPanel,
                                 childPanel,
                                 position);
    }

    @Override
    public void removeWorkbenchPanel(final PanelDefinition toRemove) throws IllegalStateException {
        if (toRemove.isRoot()) {
            throw new IllegalArgumentException("The root panel cannot be removed. To replace it, call setRoot()");
        }
        if (!toRemove.getParts().isEmpty()) {
            throw new IllegalStateException("Panel still contains parts: " + toRemove.getParts());
        }

        final WorkbenchPanelPresenter presenterToRemove = mapPanelDefinitionToPresenter.remove(toRemove);
        if (presenterToRemove == null) {
            throw new IllegalArgumentException("Couldn't find panel to remove: " + toRemove);
        }

        removeWorkbenchPanelFromParent(toRemove,
                                       presenterToRemove);

        // we do this check last because some panel types (eg. docking panels) can "rescue" orphaned child panels
        // during the PanelPresenter.remove() call
        if (!toRemove.getChildren().isEmpty()) {
            throw new IllegalStateException("Panel still contains child panels: " + toRemove.getChildren());
        }

        getBeanFactory().destroy(presenterToRemove);
    }

    private void removeWorkbenchPanelFromParent(final PanelDefinition toRemove,
                                                final WorkbenchPanelPresenter presenterToRemove) {
        HasWidgets customContainer = customPanels.remove(toRemove);
        if (customContainer != null) {
            customContainer.remove(presenterToRemove.getPanelView().asWidget());
        } else {
            HTMLElement customHTMLElementContainer = customPanelsInsideHTMLElements.remove(toRemove);
            if (customHTMLElementContainer != null) {
                DOMUtil.removeFromParent(presenterToRemove.getPanelView().asWidget());
            } else {
                elemental2.dom.HTMLElement customElemental2HtmlElement = customPanelsInsideElemental2HTMLElements.remove(toRemove);
                if (customElemental2HtmlElement != null) {
                    final elemental2.dom.HTMLElement element =
                            elemental2DomUtil.asHTMLElement(presenterToRemove.getPanelView().asWidget().getElement());
                    elemental2DomUtil.removeAllElementChildren(element);
                } else {
                    final PanelDefinition parentDef = toRemove.getParent();
                    final WorkbenchPanelPresenter parentPresenter = mapPanelDefinitionToPresenter.get(parentDef);
                    if (parentPresenter == null) {
                        throw new IllegalArgumentException("The given panel's parent could not be found");
                    }

                    parentPresenter.removePanel(presenterToRemove);
                }
            }
        }
    }

    @Override
    public void onPartFocus(final PartDefinition part) {
        activePart = part;
        panelFocusEvent.fire(new PanelFocusEvent(part.getParentPanel()));
        placeGainFocusEvent.fire(new PlaceGainFocusEvent(part.getPlace()));
    }

    @Override
    public void onPartMaximized(final PartDefinition part) {
        placeMaximizedEvent.fire(new PlaceMaximizedEvent(part.getPlace()));
    }

    @Override
    public void onPartMinimized(final PartDefinition part) {
        placeMinimizedEvent.fire(new PlaceMinimizedEvent(part.getPlace()));
    }

    @Override
    public PartDefinition getFocusedPart() {
        return activePart;
    }

    @Override
    public void onPartHidden(final PartDefinition part) {
        placeHiddenEvent.fire(new PlaceHiddenEvent(part.getPlace()));
    }

    @Override
    public void onPartLostFocus() {
        if (activePart == null) {
            return;
        }
        placeLostFocusEvent.fire(new PlaceLostFocusEvent(activePart.getPlace()));
        activePart = null;
    }

    @Override
    public void onPanelFocus(final PanelDefinition panel) {
        for (Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet()) {
            e.getValue().setFocus(e.getKey().asString().equals(panel.asString()));
        }
    }

    @Override
    public void closePart(final PartDefinition part) {
        placeManager.get().closePlace(part.getPlace());
    }

    void onSelectPlaceEvent(@Observes SelectPlaceEvent event) {
        final PlaceRequest place = event.getPlace();

        // TODO (hbraun): PanelDefinition is not distinct (missing hashcode)
        for (Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : new HashSet<>(mapPanelDefinitionToPresenter.entrySet())) {
            WorkbenchPanelPresenter panelPresenter = e.getValue();
            for (PartDefinition part : ensureIterable(panelPresenter.getDefinition().getParts())) {
                if (part.getPlace().asString().equals(place.asString())) {
                    panelPresenter.selectPart(part);
                    onPanelFocus(e.getKey());
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private void onDropPlaceEvent(@Observes DropPlaceEvent event) {
        final PartDefinition part = getPartForPlace(event.getPlace());
        if (part != null) {
            removePart(part);
        }
    }

    @Override
    public PanelDefinition getPanelForPlace(final PlaceRequest place) {
        for (PartDefinition part : mapPartDefinitionToPresenter.keySet()) {
            if (part.getPlace().equals(place)) {
                return part.getParentPanel();
            }
        }
        return null;
    }

    /**
     * Returns the first live (associated with an active presenter) PartDefinition whose place matches the given one.
     *
     * @return the definition for the live part servicing the given place, or null if no such part can be found.
     */
    protected PartDefinition getPartForPlace(final PlaceRequest place) {
        for (PartDefinition part : mapPartDefinitionToPresenter.keySet()) {
            if (part.getPlace().asString().equals(place.asString())) {
                return part;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private void onChangeTitleWidgetEvent(@Observes ChangeTitleWidgetEvent event) {
        if (event.getPlaceRequest() == null) {
            return;
        }
        final PlaceRequest place = event.getPlaceRequest();
        final IsWidget titleDecoration = event.getTitleDecoration();
        final String title = event.getTitle();
        for (Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet()) {
            final PanelDefinition panel = e.getKey();
            final WorkbenchPanelPresenter presenter = e.getValue();
            for (PartDefinition part : ensureIterable(panel.getParts())) {
                if (place.equals(part.getPlace())) {
                    mapPartDefinitionToPresenter.get(part).setTitle(title);
                    presenter.changeTitle(part,
                                          title,
                                          titleDecoration);
                    break;
                }
            }
        }
    }

    /**
     * Destroys the presenter bean associated with the given part and removes the part's presenter from the panel
     * presenter that contains it (which in turn removes the part definition from the panel definition and the part view
     * from the panel view).
     *
     * @param part the definition of the workbench part (screen or editor) to remove from the layout.
     */
    protected void removePart(final PartDefinition part) {
        for (Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet()) {
            final WorkbenchPanelPresenter panelPresenter = e.getValue();
            if (panelPresenter.getDefinition().getParts().contains(part)) {
                panelPresenter.removePart(part);
                break;
            }
        }

        WorkbenchPartPresenter deadPartPresenter = mapPartDefinitionToPresenter.remove(part);
        getBeanFactory().destroy(deadPartPresenter);
    }

    @Override
    public PanelDefinition addWorkbenchPanel(final PanelDefinition targetPanel,
                                             final PanelDefinition childPanel,
                                             final Position position) {

        WorkbenchPanelPresenter targetPanelPresenter = mapPanelDefinitionToPresenter.get(targetPanel);

        if (targetPanelPresenter == null) {
            targetPanelPresenter = beanFactory.newWorkbenchPanel(targetPanel);
            mapPanelDefinitionToPresenter.put(targetPanel,
                                              targetPanelPresenter);
        }

        PanelDefinition newPanel;

        // Position instance could come from a different script so we compare using position.getName
        if (CompassPosition.ROOT.getName().equals(position.getName())) {
            newPanel = rootPanelDef;
        } else if (CompassPosition.SELF.getName().equals(position.getName())) {
            newPanel = targetPanelPresenter.getDefinition();
        } else {
            String defaultChildType = targetPanelPresenter.getDefaultChildType();
            if (defaultChildType == null) {
                throw new IllegalArgumentException("Target panel (type " + targetPanelPresenter.getClass().getName() + ")"
                                                           + " does not allow child panels");
            }

            if (childPanel.getPanelType().equals(PanelDefinition.PARENT_CHOOSES_TYPE)) {
                childPanel.setPanelType(defaultChildType);
            }

            final WorkbenchPanelPresenter childPanelPresenter = beanFactory.newWorkbenchPanel(childPanel);
            mapPanelDefinitionToPresenter.put(childPanel,
                                              childPanelPresenter);

            targetPanelPresenter.addPanel(childPanelPresenter,
                                          position);
            newPanel = childPanel;
        }

        onPanelFocus(newPanel);

        return newPanel;
    }

    @Override
    public CustomPanelDefinition addCustomPanel(final HasWidgets container,
                                                final String panelType) {
        return addCustomPanelOnContainer(container,
                                         new CustomPanelDefinitionImpl(panelType,
                                                                       container),
                                         false);
    }

    @Override
    public CustomPanelDefinition addCustomPanel(final HTMLElement container,
                                                final String panelType) {
        return addCustomPanelOnContainer(container,
                                         new CustomPanelDefinitionImpl(panelType,
                                                                       container),
                                         false);
    }

    @Override
    public CustomPanelDefinition addCustomPanel(final elemental2.dom.HTMLElement container,
                                                final String panelType) {
        return addCustomPanelOnContainer(container,
                                         new CustomPanelDefinitionImpl(panelType,
                                                                       container),
                                         true);
    }

    private CustomPanelDefinition addCustomPanelOnContainer(final Object container,
                                                            CustomPanelDefinitionImpl panelDef,
                                                            final boolean isElemental2) {
        final WorkbenchPanelPresenter panelPresenter = beanFactory.newWorkbenchPanel(panelDef);
        Widget panelViewWidget = panelPresenter.getPanelView().asWidget();
        panelViewWidget.addAttachHandler(new CustomPanelCleanupHandler(panelPresenter));

        if (container instanceof HasWidgets) {
            HasWidgets widgetContainer = (HasWidgets) container;
            widgetContainer.add(panelViewWidget);
            customPanels.put(panelDef,
                             widgetContainer);
        } else {
            // Cannot do instanceof against native JsType interface
            if (isElemental2) {
                elemental2.dom.HTMLElement htmlContainer = (elemental2.dom.HTMLElement) container;
                appendWidgetToElement(htmlContainer,
                                      panelViewWidget);
                customPanelsInsideElemental2HTMLElements.put(panelDef,
                                                             htmlContainer);
            } else {
                HTMLElement htmlContainer = (HTMLElement) container;
                appendWidgetToElement(htmlContainer,
                                      panelViewWidget);
                customPanelsInsideHTMLElements.put(panelDef,
                                                   htmlContainer);
            }
        }

        mapPanelDefinitionToPresenter.put(panelDef,
                                          panelPresenter);
        onPanelFocus(panelDef);
        return panelDef;
    }

    void appendWidgetToElement(final HTMLElement container,
                               final Widget panelViewWidget) {
        DOMUtil.appendWidgetToElement(container,
                                      panelViewWidget.asWidget());
    }

    void appendWidgetToElement(final elemental2.dom.HTMLElement container,
                               final Widget panelViewWidget) {
        elemental2DomUtil.appendWidgetToElement(container,
                                                panelViewWidget.asWidget());
    }

    /**
     * Cleanup handler for custom panels that are removed from the DOM before they are removed via PlaceManager.
     *
     * @see PanelManagerImpl#addCustomPanel(HasWidgets, String)
     */
    private final class CustomPanelCleanupHandler implements AttachEvent.Handler {

        private final WorkbenchPanelPresenter panelPresenter;
        private boolean detaching;

        private CustomPanelCleanupHandler(WorkbenchPanelPresenter panelPresenter) {
            this.panelPresenter = panelPresenter;
        }

        @Override
        public void onAttachOrDetach(AttachEvent event) {
            if (event.isAttached()) {
                return;
            }
            if (!detaching && mapPanelDefinitionToPresenter.containsKey(panelPresenter.getDefinition())) {
                System.out.println("Running cleanup for " + Debug.objectId(this));
                detaching = true;
                Scheduler.get().scheduleFinally(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        try {
                            for (PartDefinition part : ensureIterable(panelPresenter.getDefinition().getParts())) {
                                placeManager.get().closePlace(part.getPlace());
                            }

                            // in many cases, the panel will have cleaned itself up when we closed its last part in the loop above.
                            // for other custom panel use cases, the panel may still be open. we can do the cleanup here.
                            if (mapPanelDefinitionToPresenter.containsKey(panelPresenter.getDefinition())) {
                                removeWorkbenchPanel(panelPresenter.getDefinition());
                            }
                        } finally {
                            detaching = false;
                        }
                    }
                });
            }
        }
    }
}
