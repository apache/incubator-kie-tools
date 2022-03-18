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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Timer;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasDomainObjectListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.mvp.Command;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class FormsCanvasSessionHandler {

    /**
     * An ORDERED array of feature providers supported.
     */
    private static final FormFeaturesSessionProvider[] FEATURE_SESSION_PROVIDERS = new FormFeaturesSessionProvider[]{new FormFeaturesFullSessionProvider(), new FormFeaturesReadOnlySessionProvider()};
    private final DefinitionManager definitionManager;
    private final CanvasCommandFactory<AbstractCanvasHandler> commandFactory;
    private final FormsCanvasListener canvasListener;
    private final FormsDomainObjectCanvasListener domainObjectCanvasListener;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private ClientSession session;
    private FormFeaturesSessionProvider featuresSessionProvider;
    private FormRenderer renderer;

    @Inject
    public FormsCanvasSessionHandler(final DefinitionManager definitionManager,
                                     final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                                     final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager) {
        this.definitionManager = definitionManager;
        this.commandFactory = commandFactory;
        this.canvasListener = getFormsCanvasListener();
        this.domainObjectCanvasListener = getFormsDomainObjectCanvasListener();
        this.sessionCommandManager = sessionCommandManager;
    }

    protected FormsCanvasListener getFormsCanvasListener() {
        return new FormsCanvasListener();
    }

    protected FormsDomainObjectCanvasListener getFormsDomainObjectCanvasListener() {
        return new FormsDomainObjectCanvasListener();
    }

    public FormsCanvasSessionHandler setRenderer(FormRenderer renderer) {
        this.renderer = renderer;
        return this;
    }

    public FormsCanvasSessionHandler bind(final ClientSession session) {
        this.session = session;
        canvasListener.attach();
        domainObjectCanvasListener.attach();
        featuresSessionProvider = getFeaturesSessionProvider(session);
        if (Objects.isNull(featuresSessionProvider)) {
            throw new UnsupportedOperationException("No client session type supported.");
        }
        return this;
    }

    public FormsCanvasSessionHandler unbind() {
        canvasListener.detach();
        domainObjectCanvasListener.detach();
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
     * Shows properties of elements in current session.
     * See {@link SelectionControl#getSelectedItemDefinition()}
     */
    public void show() {
        this.show(null);
    }

    @SuppressWarnings("unchecked")
    public void show(final Command callback) {
        if (null != session) {
            final SelectionControl selectionControl = featuresSessionProvider.getSelectionControl(session);
            if (!Objects.isNull(selectionControl)) {
                selectionControl.getSelectedItemDefinition().ifPresent(selectedItemDefinition -> {
                    if (selectedItemDefinition instanceof Element) {
                        render((Element) selectedItemDefinition, callback);
                    } else if (selectedItemDefinition instanceof DomainObject) {
                        render((DomainObject) selectedItemDefinition, callback);
                    }
                });
            }
        }
    }

    @SuppressWarnings("unchecked")
    public boolean executeUpdateProperty(final Element<? extends Definition<?>> element,
                                         final String fieldName,
                                         final Object value) {
        return execute(commandFactory.updatePropertyValue(element, fieldName, value), canvasListener);
    }

    @SuppressWarnings("unchecked")
    public boolean executeUpdateDomainObjectProperty(final DomainObject domainObject,
                                                     final String fieldName,
                                                     final Object value) {
        return execute(commandFactory.updateDomainObjectPropertyValue(domainObject, fieldName, value),
                       domainObjectCanvasListener);
    }

    private boolean execute(CanvasCommand<AbstractCanvasHandler> command, FormsListener listener) {
        listener.startProcessing();
        final CommandResult result = sessionCommandManager.execute(getCanvasHandler(), command);
        listener.endProcessing();
        return !CommandUtils.isError(result);
    }

    void onRefreshFormPropertiesEvent(@Observes RefreshFormPropertiesEvent event) {
        checkNotNull("event", event);

        if (checkSession(event.getSession())) { // Possible Improvement
            if (event.hasUuid()) {
                final String uuid = event.getUuid();
                final Element<? extends Definition<?>> element = CanvasLayoutUtils.getElement(getCanvasHandler(), uuid);
                render(element);
            } else {
                show();
            }
        }
    }

    void onCanvasSelectionEvent(@Observes CanvasClearSelectionEvent event) {
        if (checkCanvasHandler(event.getCanvasHandler())) {
            selectRoot();
        }
    }

    void onCanvasSelectionEvent(@Observes CanvasSelectionEvent event) {
        if (checkCanvasHandler(event.getCanvasHandler())) {
            if (event.getIdentifiers().size() == 1) {
                final String uuid = event.getIdentifiers().iterator().next();
                final Element<? extends Definition<?>> element = CanvasLayoutUtils.getElement(getCanvasHandler(), uuid);
                scheduleRender(() -> render(element));
            } else {
                selectRoot();
            }
        }
    }

    private void selectRoot() {
        final Element<? extends Definition<?>> element = CanvasLayoutUtils.getElement(getCanvasHandler(), this.getDiagram().getMetadata().getCanvasRootUUID());
        render(element);
    }

    protected void scheduleRender(final com.google.gwt.user.client.Command command) {
        new Timer() {
            @Override
            public void run() {
                command.execute();
            }
        }.schedule(100);
    }

    void onDomainObjectSelectionEvent(@Observes DomainObjectSelectionEvent event) {
        checkNotNull("event",
                     event);
        if (checkCanvasHandler(event.getCanvasHandler())) {
            final DomainObject domainObject = event.getDomainObject();
            render(domainObject);
        }
    }

    public ClientSession getSession() {
        return session;
    }

    public AbstractCanvasHandler getCanvasHandler() {
        return !Objects.isNull(session) ? (AbstractCanvasHandler) session.getCanvasHandler() : null;
    }

    public Diagram<?, ?> getDiagram() {
        return !Objects.isNull(getCanvasHandler()) ? getCanvasHandler().getDiagram() : null;
    }

    private Graph<?, ?> getGraph() {
        return !Objects.isNull(getDiagram()) ? getDiagram().getGraph() : null;
    }

    private Optional<String> getGraphUUID() {
        if (Objects.isNull(getGraph())) {
            return Optional.empty();
        }
        return Optional.of(getGraph().getUUID());
    }

    public RenderMode getSessionRenderMode() {
        return getRenderMode(session);
    }

    private RenderMode getRenderMode(final ClientSession session) {
        return session instanceof EditorSession ? RenderMode.EDIT_MODE : RenderMode.PRETTY_MODE;
    }

    private void render(final Element<? extends Definition<?>> element) {
        render(element,
               () -> {/*Nothing*/});
    }

    private void render(final Element<? extends Definition<?>> element,
                        final Command callback) {
        if (!Objects.isNull(renderer)) {
            getGraphUUID().ifPresent(graphUUID -> renderer.render(graphUUID,
                                                                  element,
                                                                  callback));
        }
    }

    private void render(final DomainObject domainObject) {
        if (!Objects.isNull(renderer)) {
            getGraphUUID().ifPresent(graphUUID -> renderer.render(graphUUID,
                                                                  domainObject,
                                                                  () -> {/*Nothing*/}));
        }
    }

    private void render(final DomainObject domainObject,
                        final Command callback) {
        if (!Objects.isNull(renderer)) {
            getGraphUUID().ifPresent(graphUUID -> renderer.render(graphUUID,
                                                                  domainObject,
                                                                  callback));
        }
    }

    private boolean checkCanvasHandler(final CanvasHandler ch) {
        AbstractCanvasHandler canvasHandler = getCanvasHandler();
        return !Objects.isNull(canvasHandler) && canvasHandler.equals(ch);
    }

    private boolean checkSession(final ClientSession s) {
        return checkCanvasHandler(s.getCanvasHandler());
    }

    private FormFeaturesSessionProvider getFeaturesSessionProvider(final ClientSession session) {
        for (final FormFeaturesSessionProvider featureSessionProvider : FEATURE_SESSION_PROVIDERS) {
            if (featureSessionProvider.supports(session)) {
                return featureSessionProvider;
            }
        }
        return null;
    }

    private interface FormsListener {

        void startProcessing();

        void endProcessing();
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
         * Renders the form properties panel for the given {@link Object}
         * @param graphUuid the current {@link Graph} UUID
         * @param domainObject the {@link DomainObject} to render properties form
         * @param callback a {@link Command} to execute after a property value change
         */
        void render(String graphUuid, DomainObject domainObject, Command callback);

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

        /**
         * Resets Cache
         */
        void resetCache();

        /**
         * Checks if Positions are the same for the last element rendered and if Elements are the same
         * @param element Element to be checked
         * @return True if Elements are the same and their positions or False if they are not
         */
        boolean areLastPositionsSameForElement(final Element element);
    }

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

        private EditorSession cast(final ClientSession session) {
            return (EditorSession) session;
        }
    }

    /**
     * A listener that refresh the forms once an element has been updated,
     * but it skips the refreshing when updates come from this forms widget instance.
     */
    class FormsCanvasListener implements CanvasElementListener,
                                         FormsListener {

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

            if (!Objects.isNull(renderer) && renderer.areLastPositionsSameForElement(item)) {
                renderer.resetCache();
            }

            if (!areFormsProcessing) {
                scheduleRender(() -> render(item));
            }
        }

        @Override
        public void updateBatch(final List<Element> queue) {
            if (!queue.isEmpty()) {
                // No point in updating lots of elements, just last one or if single the only one
                update(queue.get(queue.size() - 1));
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

    /**
     * A listener that refresh the forms once a DomainObject has been updated,
     * but it skips the refreshing when updates come from this forms widget instance.
     */
    class FormsDomainObjectCanvasListener implements CanvasDomainObjectListener,
                                                     FormsListener {

        private boolean areFormsProcessing;

        public FormsDomainObjectCanvasListener() {
            this.areFormsProcessing = false;
        }

        public void attach() {
            getCanvasHandler().ifPresent(this::_attach);
        }

        public void detach() {
            getCanvasHandler().ifPresent(this::_detach);
        }

        public void startProcessing() {
            this.areFormsProcessing = true;
        }

        public void endProcessing() {
            this.areFormsProcessing = false;
        }

        private void _attach(final AbstractCanvasHandler canvasHandler) {
            canvasHandler.addDomainObjectListener(this);
        }

        private void _detach(final AbstractCanvasHandler canvasHandler) {
            canvasHandler.removeDomainObjectListener(this);
        }

        private Optional<AbstractCanvasHandler> getCanvasHandler() {
            return Optional.ofNullable(FormsCanvasSessionHandler.this.getCanvasHandler());
        }

        @Override
        public void update(final DomainObject domainObject) {
            if (!areFormsProcessing) {
                render(domainObject);
            }
        }
    }
}
