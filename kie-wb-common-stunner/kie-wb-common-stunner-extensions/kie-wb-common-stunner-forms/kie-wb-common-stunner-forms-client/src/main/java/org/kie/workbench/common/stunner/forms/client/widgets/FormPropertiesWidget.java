/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.forms.client.widgets;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.logging.client.LogConfiguration;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.databinding.client.HasProperties;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientReadOnlySession;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.FormPropertiesOpened;
import org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainer;
import org.uberfire.mvp.Command;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class FormPropertiesWidget implements IsElement,
                                             FormPropertiesWidgetView.Presenter {

    private static Logger LOGGER = Logger.getLogger(FormPropertiesWidget.class.getName());

    private final FormPropertiesWidgetView view;
    private final DefinitionUtils definitionUtils;
    private final CanvasCommandFactory<AbstractCanvasHandler> commandFactory;
    private final Event<FormPropertiesOpened> propertiesOpenedEvent;

    private ClientSession session;
    private FormFeaturesSessionProvider featuresSessionProvider;
    private final FormsContainer formsContainer;

    protected FormPropertiesWidget() {
        this(null, null, null, null, null);
    }

    @Inject
    public FormPropertiesWidget(final FormPropertiesWidgetView view, final DefinitionUtils definitionUtils, final CanvasCommandFactory<AbstractCanvasHandler> commandFactory, final Event<FormPropertiesOpened> propertiesOpenedEvent, FormsContainer formsContainer) {
        this.view = view;
        this.definitionUtils = definitionUtils;
        this.commandFactory = commandFactory;
        this.propertiesOpenedEvent = propertiesOpenedEvent;
        this.formsContainer = formsContainer;
    }

    @PostConstruct
    public void init() {
        log(Level.INFO, "FormPropertiesWidget instance build.");
        view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public HTMLElement getDisplayerElement() {
        return formsContainer.getElement();
    }

    /**
     * Binds a session.
     */
    public FormPropertiesWidget bind(final ClientSession session) {
        this.session = session;
        featuresSessionProvider = getFeaturesSessionProvider(session);
        if (null == featuresSessionProvider) {
            throw new UnsupportedOperationException("No client session type supported.");
        }
        return this;
    }

    /**
     * Unbinds a session.
     */
    public FormPropertiesWidget unbind() {
        this.session = null;
        return this;
    }

    /**
     * Shows properties of elements in current session as: 1.- If any element
     * selected on session control, show properties for it. 2.- If no element
     * selected on session control: 2.1- If no canvas root fot the diagram, show the
     * diagram's graph properties. 2.2- If diagram has a canvas root, show the
     * properties for that element.
     */
    public void show() {
        this.show(null);
    }

    @SuppressWarnings("unchecked")
    public void show(final Command callback) {
        if (null != session) {
            // Obtain first element selected on session, if any.
            String selectedItemUUID = null;
            final SelectionControl selectionControl = featuresSessionProvider.getSelectionControl(session);
            if (null != selectionControl) {
                final Collection<String> selectedItems = selectionControl.getSelectedItems();
                if (null != selectedItems && !selectedItems.isEmpty()) {
                    selectedItemUUID = selectedItems.iterator().next();
                }
            } else {
                LOGGER.log(Level.WARNING, "Cannot show properties as session type does not provides " + "selection control's support.");
            }
            if (null == selectedItemUUID) {
                final Diagram<?, ?> diagram = getDiagram();
                if (null != diagram) {
                    final String cRoot = diagram.getMetadata().getCanvasRootUUID();
                    // Check if there exist any canvas root element.
                    if (!isEmpty(cRoot)) {
                        selectedItemUUID = cRoot;
                    }
                }
            }
            if (null != selectedItemUUID) {
                showByUUID(selectedItemUUID, getSessionRenderMode(), callback);
            }
        }
    }

    /**
     * Show properties for the element with the given identifier.
     */
    public void showByUUID(final String uuid, final RenderMode renderMode) {
        this.showByUUID(uuid, renderMode, null);
    }

    @SuppressWarnings("unchecked")
    public void showByUUID(final String uuid, final RenderMode renderMode, final Command callback) {
        final Element<? extends Definition<?>> element = (null != uuid && null != getCanvasHandler()) ? getCanvasHandler().getGraphIndex().get(uuid) : null;

        if (null != element) {

            Diagram<?, ?> diagram = getDiagram();

            final Object definition = element.getContent().getDefinition();

            formsContainer.render(diagram.getGraph().getUUID(), element, diagram.getMetadata().getPath(), (fieldName, newValue) -> {
                try {
                    final HasProperties hasProperties = (HasProperties) DataBinder.forModel(definition).getModel();
                    final String pId = getModifiedPropertyId(hasProperties, fieldName);
                    FormPropertiesWidget.this.executeUpdateProperty(element, pId, newValue);
                } catch (final Exception ex) {
                    log(Level.SEVERE, "Something wrong happened refreshing the canvas for field '" + fieldName + "': " + ex.getCause());
                } finally {
                    if (null != callback) {
                        callback.execute();
                    }
                }
            });

            final String name = definitionUtils.getName(definition);
            propertiesOpenedEvent.fire(new FormPropertiesOpened(session, uuid, name));
        } else {
            if (null != callback) {
                callback.execute();
            }
        }
    }

    private AbstractCanvasHandler getCanvasHandler() {
        return session != null ? (AbstractCanvasHandler) session.getCanvasHandler() : null;
    }

    private Diagram<?, ?> getDiagram() {
        return null != getCanvasHandler() ? getCanvasHandler().getDiagram() : null;
    }

    private RenderMode getSessionRenderMode() {
        return getRenderMode(session);
    }

    private RenderMode getRenderMode(final ClientSession session) {
        return session instanceof ClientFullSession ? RenderMode.EDIT_MODE : RenderMode.PRETTY_MODE;
    }

    @SuppressWarnings("unchecked")
    void onCanvasSelectionEvent(@Observes CanvasSelectionEvent event) {
        checkNotNull("event",
                     event);
        if (null != getCanvasHandler()) {
            if (event.getIdentifiers().size() == 1) {
                final String uuid = event.getIdentifiers().iterator().next();
                showByUUID(uuid,
                           getSessionRenderMode());
            }
        }
    }

    void onCanvasElementUpdatedEvent(final @Observes CanvasElementUpdatedEvent canvasElementUpdatedEvent) {
        checkNotNull("canvasElementUpdatedEvent",
                     canvasElementUpdatedEvent);
        final String uuid = canvasElementUpdatedEvent.getElement().getUUID();
        showByUUID(uuid,
                   getSessionRenderMode());
    }

    void onCanvasSessionOpened(@Observes SessionOpenedEvent sessionOpenedEvent) {
        checkNotNull("sessionOpenedEvent", sessionOpenedEvent);
        doOpenSession(sessionOpenedEvent.getSession());
    }

    void onCanvasSessionDestroyed(@Observes SessionDestroyedEvent sessionDestroyedEvent) {
        checkNotNull("sessionDestroyedEvent", sessionDestroyedEvent);
        unbind();
    }

    private void doOpenSession(final ClientSession session) {
        try {
            bind(session).show();
        } catch (ClassCastException e) {
            // No writteable session. Do not show properties until read mode available.
            log(Level.INFO, "Session discarded for opening as not instance of full session.");
        }
    }

    @SuppressWarnings("unchecked")
    private void executeUpdateProperty(final Element<? extends Definition<?>> element, final String propertyId, final Object value) {
        final CanvasCommandManager<AbstractCanvasHandler> commandManager = featuresSessionProvider.getCommandManager(session);
        if (null != commandManager) {
            commandManager.execute(getCanvasHandler(), commandFactory.updatePropertyValue(element, propertyId, value));
        } else {
            LOGGER.log(Level.WARNING, "Cannot update property [" + propertyId + "] as session type is not supported.");
        }
    }

    private String getModifiedPropertyId(HasProperties model, String fieldName) {
        int separatorIndex = fieldName.indexOf(".");
        // Check if it is a nested property, if it is we must obtain the nested property
        // instead of the root one.
        if (separatorIndex != -1) {
            String rootProperty = fieldName.substring(0, separatorIndex);
            fieldName = fieldName.substring(separatorIndex + 1);
            Object property = model.get(rootProperty);
            model = (HasProperties) DataBinder.forModel(property).getModel();
            return getModifiedPropertyId(model, fieldName);
        }
        Object property = model.get(fieldName);
        return definitionUtils.getDefinitionManager().adapters().forProperty().getId(property);
    }

    private FormFeaturesSessionProvider getFeaturesSessionProvider(final ClientSession session) {
        for (final FormFeaturesSessionProvider featureSessionProvider : FEATURE_SESSION_PROVIDERS) {
            if (featureSessionProvider.supports(session)) {
                return featureSessionProvider;
            }
        }
        return null;
    }

    /**
     * An ORDERED array of feature providers supported.
     */
    private static final FormFeaturesSessionProvider[] FEATURE_SESSION_PROVIDERS = new FormFeaturesSessionProvider[]{new FormFeaturesFullSessionProvider(), new FormFeaturesReadOnlySessionProvider()};

    /**
     * This type provides required features that are specific for concrete client
     * session types.
     */
    private interface FormFeaturesSessionProvider<S extends ClientSession> {

        /**
         * Returns <code>true</code> is the session type is supported.
         */
        boolean supports(ClientSession type);

        /**
         * Returns the session's selection control instance, if not available, it
         * returns <code>null</code>.
         */
        SelectionControl getSelectionControl(S session);

        /**
         * Returns the session's command manager instance, if not available, it returns
         * <code>null</code>.
         */
        CanvasCommandManager<AbstractCanvasHandler> getCommandManager(S session);
    }

    private static class FormFeaturesReadOnlySessionProvider implements FormFeaturesSessionProvider<ClientReadOnlySession> {

        @Override
        public boolean supports(final ClientSession type) {
            return type instanceof ClientReadOnlySession;
        }

        @Override
        public SelectionControl getSelectionControl(final ClientReadOnlySession session) {
            return cast(session).getSelectionControl();
        }

        @Override
        public CanvasCommandManager<AbstractCanvasHandler> getCommandManager(final ClientReadOnlySession session) {
            return null;
        }

        private ClientReadOnlySession cast(final ClientSession session) {
            return (ClientReadOnlySession) session;
        }
    }

    private static class FormFeaturesFullSessionProvider implements FormFeaturesSessionProvider<ClientFullSession> {

        @Override
        public boolean supports(final ClientSession type) {
            return type instanceof ClientFullSession;
        }

        @Override
        public SelectionControl getSelectionControl(final ClientFullSession session) {
            return cast(session).getSelectionControl();
        }

        @Override
        public CanvasCommandManager<AbstractCanvasHandler> getCommandManager(final ClientFullSession session) {
            return cast(session).getCommandManager();
        }

        private ClientFullSession cast(final ClientSession session) {
            return (ClientFullSession) session;
        }
    }

    private boolean isEmpty(final String s) {
        return s == null || s.trim().length() == 0;
    }

    private void log(final Level level, final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level, message);
        }
    }
}
