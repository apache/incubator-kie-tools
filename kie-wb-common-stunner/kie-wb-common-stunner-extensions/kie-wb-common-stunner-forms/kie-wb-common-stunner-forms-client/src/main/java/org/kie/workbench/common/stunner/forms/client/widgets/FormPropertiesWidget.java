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
    private final Event<FormPropertiesOpened> propertiesOpenedEvent;
    private final FormsCanvasSessionHandler formSessionHandler;
    private final FormsContainer formsContainer;

    protected FormPropertiesWidget() {
        this(null, null, null, null, null);
    }

    @Inject
    public FormPropertiesWidget(final FormPropertiesWidgetView view,
                                final DefinitionUtils definitionUtils,
                                final FormsCanvasSessionHandler formSessionHandler,
                                final Event<FormPropertiesOpened> propertiesOpenedEvent,
                                final FormsContainer formsContainer) {
        this.view = view;
        this.definitionUtils = definitionUtils;
        this.formSessionHandler = formSessionHandler;
        this.propertiesOpenedEvent = propertiesOpenedEvent;
        this.formsContainer = formsContainer;
    }

    @PostConstruct
    public void init() {
        log(Level.INFO, "FormPropertiesWidget instance build.");
        formSessionHandler.setRenderer(new FormsCanvasSessionHandler.FormRenderer() {
            @Override
            public void render(String graphUuid, Element element, Command callback) {
                show(graphUuid, element, callback);
            }

            @Override
            public void clear(String graphUuid, Element element) {
                formsContainer.clearFormDisplayer(graphUuid, element.getUUID());
            }

            @Override
            public void clearAll(String graphUuid) {
                formsContainer.clearDiagramDisplayers(graphUuid);
            }
        });
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
    @SuppressWarnings("unchecked")
    public FormPropertiesWidget bind(final ClientSession session) {
        formSessionHandler.bind(session);
        return this;
    }

    /**
     * Unbinds a session.
     */
    @SuppressWarnings("unchecked")
    public FormPropertiesWidget unbind() {
        formSessionHandler.unbind();
        return this;
    }

    public void show() {
        formSessionHandler.show();
    }

    @SuppressWarnings("unchecked")
    public void show(final Command callback) {
        formSessionHandler.show(callback);
    }

    void onCanvasSessionOpened(@Observes SessionOpenedEvent sessionOpenedEvent) {
        checkNotNull("sessionOpenedEvent", sessionOpenedEvent);
        try {
            bind(sessionOpenedEvent.getSession()).show();
        } catch (ClassCastException e) {
            // No writteable session. Do not show properties until read mode available.
            log(Level.INFO, "Session discarded for opening as not instance of full session.");
        }
    }

    void onCanvasSessionDestroyed(@Observes SessionDestroyedEvent sessionDestroyedEvent) {
        checkNotNull("sessionDestroyedEvent", sessionDestroyedEvent);
        unbind();
    }

    private void show(final String graphUuid,
                      final Element<? extends Definition<?>> element,
                      final Command callback) {
        final String uuid = element.getUUID();
        final Diagram<?, ?> diagram = formSessionHandler.getDiagram();
        final Object definition = element.getContent().getDefinition();
        formsContainer.render(graphUuid, element, diagram.getMetadata().getPath(), (fieldName, newValue) -> {
            try {
                formSessionHandler.executeUpdateProperty(element, fieldName, newValue);
            } catch (final Exception ex) {
                log(Level.SEVERE,
                    "Something wrong happened refreshing the canvas for " +
                            "field '" + fieldName + "': " + ex.getCause());
            } finally {
                if (null != callback) {
                    callback.execute();
                }
            }
        });
        final String name = definitionUtils.getName(definition);
        propertiesOpenedEvent.fire(new FormPropertiesOpened(formSessionHandler.getSession(), uuid, name));
    }

    private static void log(final Level level, final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level, message);
        }
    }
}
