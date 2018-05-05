/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.databinding.client.HasProperties;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.uberfire.mvp.Command;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class FormsCanvasSessionHandler {

    private static Logger LOGGER = Logger.getLogger(FormsCanvasSessionHandler.class.getName());

    private final DefinitionManager definitionManager;
    private final CanvasCommandFactory<AbstractCanvasHandler> commandFactory;
    private final FormsCanvasListener canvasListener;

    private ClientSession session;
    private FormFeaturesSessionProvider featuresSessionProvider;
    private FormRenderer renderer;

    @Inject
    public FormsCanvasSessionHandler(final DefinitionManager definitionManager,
                                     final CanvasCommandFactory<AbstractCanvasHandler> commandFactory) {
        this.definitionManager = definitionManager;
        this.commandFactory = commandFactory;
        this.canvasListener = new FormsCanvasListener();
    }

    public FormsCanvasSessionHandler setRenderer(FormRenderer renderer) {
        this.renderer = renderer;
        return this;
    }

    public FormsCanvasSessionHandler bind(final ClientSession session) {
        this.session = session;
        canvasListener.attach();
        featuresSessionProvider = getFeaturesSessionProvider(session);
        if (null == featuresSessionProvider) {
            throw new UnsupportedOperationException("No client session type supported.");
        }
        return this;
    }

    public FormsCanvasSessionHandler unbind() {
        canvasListener.detach();
        this.session = null;
        return this;
    }

    @PreDestroy
    public void destroy() {
        unbind();
        featuresSessionProvider = null;
        renderer = null;
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
                render(selectedItemUUID, callback);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public boolean executeUpdateProperty(final Element<? extends Definition<?>> element,
                                         final String fieldName,
                                         final Object value) {
        final Object definition = element.getContent().getDefinition();
        final HasProperties hasProperties = (HasProperties) DataBinder.forModel(definition).getModel();
        final String propertyId = getModifiedPropertyId(hasProperties, fieldName);
        canvasListener.startProcessing();
        final CommandResult result =
                featuresSessionProvider
                        .getCommandManager(session)
                        .execute(getCanvasHandler(),
                                 commandFactory.updatePropertyValue(element,
                                                                    propertyId,
                                                                    value));
        canvasListener.endProcessing();
        return !CommandUtils.isError(result);
    }

    private Element<? extends Definition<?>> getElement(final String uuid) {
        return (null != uuid && null != getCanvasHandler()) ? getCanvasHandler().getGraphIndex().get(uuid) : null;
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
        return definitionManager.adapters().forProperty().getId(property);
    }

    @SuppressWarnings("unchecked")
    void onCanvasSelectionEvent(@Observes CanvasSelectionEvent event) {
        checkNotNull("event",
                     event);
        if (null != getCanvasHandler()) {
            if (event.getIdentifiers().size() == 1) {
                final String uuid = event.getIdentifiers().iterator().next();
                render(uuid);
            }
        }
    }

    public ClientSession getSession() {
        return session;
    }

    public AbstractCanvasHandler getCanvasHandler() {
        return session != null ? (AbstractCanvasHandler) session.getCanvasHandler() : null;
    }

    public Diagram<?, ?> getDiagram() {
        return null != getCanvasHandler() ? getCanvasHandler().getDiagram() : null;
    }

    public RenderMode getSessionRenderMode() {
        return getRenderMode(session);
    }

    private RenderMode getRenderMode(final ClientSession session) {
        return session instanceof EditorSession ? RenderMode.EDIT_MODE : RenderMode.PRETTY_MODE;
    }

    private void render(final String uuid) {
        render(uuid,
               () -> {
               });
    }

    private void render(final String uuid,
                        final Command callback) {
        if (null != renderer) {
            renderer.render(getDiagram().getGraph().getUUID(), getElement(uuid), callback);
        }
    }

    /**
     * A listener that refresh the forms once an element has been updated,
     * but it skips the refreshing when updates come from this forms widget instance.
     */
    private class FormsCanvasListener implements CanvasElementListener {

        private boolean areFormsProcessing;

        public FormsCanvasListener() {
            this.areFormsProcessing = false;
        }

        public void attach() {
            getCanvasHandler().ifPresent(this::_attach);
        }

        public void detach() {
            getCanvasHandler().ifPresent(this::_detach);
        }

        private void _attach(final AbstractCanvasHandler canvasHandler) {
            canvasHandler.addRegistrationListener(this);
        }

        private void _detach(final AbstractCanvasHandler canvasHandler) {
            canvasHandler.removeRegistrationListener(this);
        }

        public void startProcessing() {
            this.areFormsProcessing = true;
        }

        public void endProcessing() {
            this.areFormsProcessing = false;
        }

        @Override
        public void update(final Element item) {
            if (!areFormsProcessing) {
                render(item.getUUID());
            }
        }

        @Override
        public void deregister(final Element element) {
            if (null != renderer) {
                renderer.clear(getDiagram().getGraph().getUUID(), element);
            }
        }

        @Override
        public void clear() {
            if (null != renderer) {
                renderer.clearAll(getDiagram().getGraph().getUUID());
            }
        }

        private Optional<AbstractCanvasHandler> getCanvasHandler() {
            return Optional.ofNullable(FormsCanvasSessionHandler.this.getCanvasHandler());
        }
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
     * Provides form features to the {@link FormsCanvasSessionHandler}
     */
    public interface FormRenderer {

        /**
         * Renders the form properties panel for the given {@link Element}
         * @param graphUuid the current {@link Graph} UUID
         * @param element the {@link Element} to render properties form
         * @param callback a {@link Command} to execute after a property value change
         */
        void render(String graphUuid, Element element, Command callback);

        /**
         * Clears the properties form for the given {@link Element}
         * @param graphUuid the current {@link Graph} UUID
         * @param element the {@link Element} to clear its properties form
         */
        void clear(String graphUuid, Element element);

        /**
         * Clears all properties forms for the current {@link Graph}
         * @param graphUuid the current {@link Graph} UUID
         */
        void clearAll(String graphUuid);
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

    private static class FormFeaturesReadOnlySessionProvider implements FormFeaturesSessionProvider<ViewerSession> {

        @Override
        public boolean supports(final ClientSession type) {
            return type instanceof ViewerSession;
        }

        @Override
        public SelectionControl getSelectionControl(final ViewerSession session) {
            return cast(session).getSelectionControl();
        }

        @Override
        public CanvasCommandManager<AbstractCanvasHandler> getCommandManager(final ViewerSession session) {
            return null;
        }

        private ViewerSession cast(final ClientSession session) {
            return (ViewerSession) session;
        }
    }

    private static class FormFeaturesFullSessionProvider implements FormFeaturesSessionProvider<EditorSession> {

        @Override
        public boolean supports(final ClientSession type) {
            return type instanceof EditorSession;
        }

        @Override
        public SelectionControl getSelectionControl(final EditorSession session) {
            return cast(session).getSelectionControl();
        }

        @Override
        public CanvasCommandManager<AbstractCanvasHandler> getCommandManager(final EditorSession session) {
            return cast(session).getCommandManager();
        }

        private EditorSession cast(final ClientSession session) {
            return (EditorSession) session;
        }
    }

    private static boolean isEmpty(final String s) {
        return s == null || s.trim().length() == 0;
    }
}
