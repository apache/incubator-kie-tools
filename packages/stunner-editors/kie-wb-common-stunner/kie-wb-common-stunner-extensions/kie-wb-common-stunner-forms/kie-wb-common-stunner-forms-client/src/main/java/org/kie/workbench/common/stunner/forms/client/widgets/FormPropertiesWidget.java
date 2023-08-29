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


package org.kie.workbench.common.stunner.forms.client.widgets;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.logging.client.LogConfiguration;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.FormPropertiesOpened;
import org.kie.workbench.common.stunner.forms.client.formFilters.FormFiltersProviderFactory;
import org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;

@Dependent
public class FormPropertiesWidget implements IsElement,
                                             FormPropertiesWidgetView.Presenter {

    private static Logger LOGGER = Logger.getLogger(FormPropertiesWidget.class.getName());

    private final FormPropertiesWidgetView view;
    private final DefinitionUtils definitionUtils;
    private final Event<FormPropertiesOpened> propertiesOpenedEvent;
    private final FormsCanvasSessionHandler formSessionHandler;
    private final FormsContainer formsContainer;
    private final TranslationService translationService;
    private final FormsFlushManager formsFlushManager;

    @Inject
    public FormPropertiesWidget(final FormPropertiesWidgetView view,
                                final DefinitionUtils definitionUtils,
                                final FormsCanvasSessionHandler formSessionHandler,
                                final Event<FormPropertiesOpened> propertiesOpenedEvent,
                                final FormsContainer formsContainer,
                                final FormsFlushManager formsFlushManager,
                                final TranslationService translationService) {
        this.view = view;
        this.definitionUtils = definitionUtils;
        this.formSessionHandler = formSessionHandler;
        this.propertiesOpenedEvent = propertiesOpenedEvent;
        this.formsContainer = formsContainer;
        this.translationService = translationService;
        this.formsFlushManager = formsFlushManager;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        formsFlushManager.setCurrentContainer(formsContainer);
        formSessionHandler.setRenderer(new FormsCanvasSessionHandler.FormRenderer() {
            @Override
            public void render(String graphUuid, Element element, Command callback) {
                show(graphUuid, element, callback);
            }

            @Override
            public void render(String graphUuid, DomainObject domainObject, Command callback) {
                show(graphUuid, domainObject, callback);
            }

            @Override
            public void clear(String graphUuid, Element element) {
                formsContainer.clearFormDisplayer(graphUuid, element.getUUID());
            }

            @Override
            public void clearAll(String graphUuid) {
                formsContainer.clearDiagramDisplayers(graphUuid);
            }

            @Override
            public void resetCache() {
                resetLastElementRenderedCache();
            }

            @Override
            public boolean areLastPositionsSameForElement(final Element element) {
                return areLastPositionsForSameElementSame(element);
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

    public FormsCanvasSessionHandler getFormSessionHandler() {
        return formSessionHandler;
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

    @PreDestroy
    public void destroy() {
        formSessionHandler.destroy();
        formsContainer.destroyAll();
    }

    public void show() {
        formSessionHandler.show();
    }

    @SuppressWarnings("unchecked")
    public void show(final Command callback) {
        resetLastElementRenderedCache();
        formSessionHandler.show(callback);
    }

    private Element lastElement = null;
    private Point2D lastPosition = null;

    public void resetLastElementRenderedCache() {
        lastElement = null;
        lastPosition = null;
    }

    public boolean areLastPositionsForSameElementSame(final Element element) {
        final Point2D computedPosition;

        if (isNode(element)) {
            computedPosition = GraphUtils.getComputedPosition((Node<?, ? extends Edge>) element);
        } else {
            return false;
        }

        return (lastElement != null && lastElement.getUUID().equals(element.getUUID()) && computedPosition.equals(lastPosition));
    }

    protected static boolean isNode(final Element<? extends Definition<?>> element) {
        return element instanceof Node;
    }

    protected static boolean isFiltered(final Element<? extends Definition<?>> element) {
        final Definition content = element.getContent();
        if (Objects.isNull(content)) {
            return false;
        }
        final Object definition = content.getDefinition();
        return !FormFiltersProviderFactory.getFilterForDefinition(element.getUUID(), definition).isEmpty();
    }

    private void show(final String graphUuid,
                      final Element<? extends Definition<?>> element,
                      final Command callback) {
        if (element != null) {

            if (isNode(element) &&
                    !isFiltered(element) &&
                    lastElement != null &&
                    lastElement.getUUID().equals(element.getUUID())) {
                lastPosition = GraphUtils.getComputedPosition((Node<?, ? extends Edge>) element);
                return;
            }

            final String elementUUID = element.getUUID();
            final Diagram<?, ?> diagram = formSessionHandler.getDiagram();
            if (Objects.isNull(diagram)) {
                return;
            }
            final Metadata metadata = diagram.getMetadata();
            if (Objects.isNull(metadata)) {
                return;
            }
            final Path diagramPath = metadata.getPath();
            final Definition content = element.getContent();
            if (Objects.isNull(content)) {
                return;
            }
            final Object definition = content.getDefinition();

            final RenderMode renderMode = formSessionHandler.getSession() instanceof EditorSession ? RenderMode.EDIT_MODE : RenderMode.READ_ONLY_MODE;

            formsContainer.render(graphUuid,
                                  elementUUID,
                                  definition,
                                  diagramPath,
                                  (fieldName, newValue) -> {
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
                                  },
                                  renderMode);
            final String elementName = definitionUtils.getName(definition);
            fireFormsPropertiesOpenedEvent(elementUUID, elementName);
            lastElement = element;
            if (isNode(element)) {
                lastPosition = GraphUtils.getComputedPosition((Node<?, ? extends Edge>) element);
            }
        }
    }

    private void show(final String graphUuid,
                      final DomainObject domainObject,
                      final Command callback) {
        final String domainObjectUUID = domainObject.getDomainObjectUUID();
        final String domainObjectName = translationService.getTranslation(domainObject.getDomainObjectNameTranslationKey());
        final Diagram<?, ?> diagram = formSessionHandler.getDiagram();
        if (Objects.isNull(diagram)) {
            return;
        }
        final Metadata metadata = diagram.getMetadata();
        if (Objects.isNull(metadata)) {
            return;
        }
        final Path diagramPath = metadata.getPath();
        final RenderMode renderMode = formSessionHandler.getSession() instanceof EditorSession ? RenderMode.EDIT_MODE : RenderMode.READ_ONLY_MODE;

        formsContainer.render(graphUuid,
                              domainObjectUUID,
                              domainObject,
                              diagramPath,
                              (fieldName, newValue) -> {
                                  try {
                                      formSessionHandler.executeUpdateDomainObjectProperty(domainObject,
                                                                                           fieldName,
                                                                                           newValue);
                                  } catch (final Exception ex) {
                                      log(Level.SEVERE,
                                          "Something wrong happened refreshing the DomainObject '"
                                                  + domainObject + "' for field '"
                                                  + fieldName + "': " + ex.getCause());
                                  } finally {
                                      if (null != callback) {
                                          callback.execute();
                                      }
                                  }
                              },
                              renderMode);
        fireFormsPropertiesOpenedEvent(domainObjectUUID, domainObjectName);
        resetLastElementRenderedCache();
    }

    protected void fireFormsPropertiesOpenedEvent(String uuid, String name) {
        propertiesOpenedEvent.fire(new FormPropertiesOpened(formSessionHandler.getSession(), uuid, name));
    }

    protected void log(final Level level, final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level, message);
        }
    }
}