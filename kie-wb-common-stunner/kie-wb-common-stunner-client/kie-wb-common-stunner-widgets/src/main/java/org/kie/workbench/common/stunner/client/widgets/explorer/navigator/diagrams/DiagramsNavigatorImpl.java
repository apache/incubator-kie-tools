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

package org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.event.LoadDiagramEvent;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.Navigator;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorItem;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorItemView;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorView;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.util.StunnerClientLogger;
import org.kie.workbench.common.stunner.core.lookup.LookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

@Dependent
public class DiagramsNavigatorImpl implements DiagramsNavigator {

    private static Logger LOGGER = Logger.getLogger(DiagramsNavigatorImpl.class.getName());

    private final ClientDiagramService clientDiagramServices;

    private final ManagedInstance<DiagramNavigatorItem> navigatorItemInstances;

    private final Event<LoadDiagramEvent> loadDiagramEventEvent;

    private final NavigatorView<?> view;

    private final ErrorPopupPresenter errorPopupPresenter;

    private final DiagramClientErrorHandler diagramClientErrorHandler;

    private final List<NavigatorItem<DiagramRepresentation>> items;

    private int width;

    private int height;

    @Inject
    public DiagramsNavigatorImpl(final ClientDiagramService clientDiagramServices,
                                 final @Any ManagedInstance<DiagramNavigatorItem> navigatorItemInstances,
                                 final Event<LoadDiagramEvent> loadDiagramEventEvent,
                                 final NavigatorView<?> view,
                                 final DiagramClientErrorHandler diagramClientErrorHandler,
                                 final ErrorPopupPresenter errorPopupPresenter) {
        this.clientDiagramServices = clientDiagramServices;
        this.navigatorItemInstances = navigatorItemInstances;
        this.loadDiagramEventEvent = loadDiagramEventEvent;
        this.view = view;
        this.diagramClientErrorHandler = diagramClientErrorHandler;
        this.errorPopupPresenter = errorPopupPresenter;

        this.width = 140;
        this.height = 140;
        this.items = new LinkedList<>();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public Navigator<DiagramRepresentation> setItemPxSize(int width,
                                                          int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public DiagramsNavigatorImpl show() {
        // Notify some processing starts.
        fireProcessingStarted();
        clear();
        final DiagramLookupRequest request = new DiagramLookupRequest.Builder().build();
        clientDiagramServices.lookup(request,
                                     new ServiceCallback<LookupManager.LookupResponse<DiagramRepresentation>>() {
                                         @Override
                                         public void onSuccess(final LookupManager.LookupResponse<DiagramRepresentation> response) {
                                             final List<DiagramRepresentation> items = response.getResults();
                                             if (null != items && !items.isEmpty()) {
                                                 for (final DiagramRepresentation diagram : items) {
                                                     addEntry(diagram);
                                                 }
                                             }
                                             // Notify some processing ends.
                                             fireProcessingCompleted();
                                         }

                                         @Override
                                         public void onError(final ClientRuntimeError error) {
                                             showError(error);
                                         }
                                     });
        return this;
    }

    public DiagramsNavigatorImpl clear() {
        items.clear();
        view.clear();
        return this;
    }

    @Override
    public List<NavigatorItem<DiagramRepresentation>> getItems() {
        return items;
    }

    @Override
    public NavigatorView<?> getView() {
        return view;
    }

    @PreDestroy
    public void destroy() {
        items.clear();
        navigatorItemInstances.destroyAll();
    }

    private void addEntry(final DiagramRepresentation diagramRepresentation) {
        final DiagramNavigatorItem item = navigatorItemInstances.get();
        view.add(item.getView());
        items.add(item);
        item.show(diagramRepresentation,
                  width,
                  height,
                  () -> onItemSelected(item, diagramRepresentation)
        );
    }

    private void onItemSelected(final DiagramNavigatorItem item,
                                final DiagramRepresentation diagramRepresentation) {
        items.stream().forEach(i -> {
            final NavigatorItemView iv = i.getView();
            if (i.equals(item)) {
                iv.select();
            } else {
                iv.deselect();
            }
        });
        fireLoadDiagram(diagramRepresentation);
    }

    private void fireLoadDiagram(final DiagramRepresentation diagramRepresentation) {
        final String name = diagramRepresentation.getName();
        final String path = diagramRepresentation.getPath().toURI();
        loadDiagramEventEvent.fire(new LoadDiagramEvent(path,
                                                        name));
    }

    private void fireProcessingStarted() {
        view.setLoading(true);
    }

    private void fireProcessingCompleted() {
        view.setLoading(false);
    }

    private void showError(final ClientRuntimeError error) {
        diagramClientErrorHandler.handleError(error, message -> showError(message));
        log(Level.SEVERE, StunnerClientLogger.getErrorMessage(error));
        fireProcessingCompleted();
    }

    private void showError(final String message) {
        errorPopupPresenter.showMessage(message);
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
