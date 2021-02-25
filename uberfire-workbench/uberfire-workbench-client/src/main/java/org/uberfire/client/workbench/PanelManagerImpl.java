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
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.debug.Debug;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CustomPanelDefinition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.impl.CustomPanelDefinitionImpl;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.plugin.PluginUtil.ensureIterable;

/**
 * Standard implementation of {@link PanelManager}.
 */
@ApplicationScoped
public class PanelManagerImpl implements PanelManager {

    protected final Map<PartDefinition, WorkbenchPartPresenter> mapPartDefinitionToPresenter = new HashMap<>();
    protected final Map<PanelDefinition, WorkbenchPanelPresenter> mapPanelDefinitionToPresenter = new HashMap<>();
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

    protected SyncBeanManager iocManager;
    protected Instance<PlaceManager> placeManager;
    /**
     * Description that the current root panel was created from. Presently, this is a mutable data structure and the
     * whole UF framework tries to keep this in sync with the reality (syncing each change from DOM -> Widget ->
     * UberView -> Presenter -> Definition). This may change in the future. See UF-117.
     */
    protected PanelDefinition rootPanelDef = null;
    private BeanFactory beanFactory;
    private Elemental2DomUtil elemental2DomUtil;
    private WorkbenchLayout workbenchLayout;
    /**
     * Registration for the native preview handler that watches for ^M events and maximizes/restores the current panel.
     */
    private HandlerRegistration globalHandlerRegistration;

    @Inject
    public PanelManagerImpl(
            SyncBeanManager iocManager,
            Instance<PlaceManager> placeManager,
            BeanFactory beanFactory,
            Elemental2DomUtil elemental2DomUtil,
            WorkbenchLayout workbenchLayout) {
        this.iocManager = iocManager;
        this.placeManager = placeManager;
        this.beanFactory = beanFactory;
        this.elemental2DomUtil = elemental2DomUtil;
        this.workbenchLayout = workbenchLayout;
    }

    @PostConstruct
    private void setup() {
        globalHandlerRegistration = com.google.gwt.user.client.Event.addNativePreviewHandler(event -> {
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

        getBeanFactory().destroy(oldRootPanelPresenter);

        this.rootPanelDef = root;
        WorkbenchPanelPresenter newPresenter =
                mapPanelDefinitionToPresenter.computeIfAbsent(root, p ->
                        getBeanFactory().newRootPanel(activity,
                                                      root)
                );
        workbenchLayout.addContent(newPresenter.getPanelView().asWidget());
    }

    @Override
    public void addWorkbenchPart(final PlaceRequest place,
                                 final PartDefinition partDef,
                                 final PanelDefinition panelDef,
                                 final IsWidget widget,
                                 final Integer preferredWidth,
                                 final Integer preferredHeight) {
        checkNotNull("panel",
                     panelDef);

        final WorkbenchPanelPresenter panelPresenter = mapPanelDefinitionToPresenter.get(panelDef);
        if (panelPresenter == null) {
            throw new IllegalArgumentException("Target panel is not part of the layout");
        }

        WorkbenchPartPresenter partPresenter =
                mapPartDefinitionToPresenter.computeIfAbsent(partDef, p -> {
                    WorkbenchPartPresenter part = getBeanFactory().newWorkbenchPart(p,
                                                                                    panelPresenter.getPartType());
                    part.setWrappedWidget(widget);
                    return part;
                });

        panelPresenter.addPart(partPresenter);
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
    public CustomPanelDefinition addCustomPanel(final HasWidgets container,
                                                final String panelType) {
        return addCustomPanelOnContainer(container,
                                         new CustomPanelDefinitionImpl(panelType,
                                                                       container),
                                         false);
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
