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

package org.kie.workbench.common.stunner.project.client.service;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramServiceImpl;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramSavedEvent;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.kie.workbench.common.stunner.project.client.resources.i18n.StunnerProjectClientMessages;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.uberfire.backend.vfs.Path;

/**
 * A wrapper util class for handling different diagram services for the current Guvnor Project from client side.
 */
@Dependent
@Specializes
public class ClientProjectDiagramService extends ClientDiagramServiceImpl<ProjectMetadata, ProjectDiagram, ProjectDiagramService> {

    @Inject
    private ClientTranslationService translationService;

    protected ClientProjectDiagramService() {
        this(null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public ClientProjectDiagramService(final ShapeManager shapeManager,
                                       final SessionManager sessionManager,
                                       final Caller<ProjectDiagramService> diagramServiceCaller,
                                       final Caller<DiagramLookupService> diagramLookupServiceCaller,
                                       final Event<SessionDiagramSavedEvent> saveEvent) {
        super(shapeManager,
              sessionManager,
              diagramServiceCaller,
              diagramLookupServiceCaller,
              saveEvent);
    }

    public void create(final Path path,
                       final String name,
                       final String defSetId,
                       final String projectName,
                       final Package projectPkg,
                       final Optional<String> projectType,
                       final ServiceCallback<Path> callback) {
        diagramServiceCaller.call((RemoteCallback<Path>) callback::onSuccess,
                                  (message, throwable) -> {
                                      callback.onError(createOnErrorHandler(throwable, name, projectPkg));
                                      return false;
                                  }).create(path,
                                            name,
                                            defSetId,
                                            projectName,
                                            projectPkg,
                                            projectType);
    }

    public void saveOrUpdate(final Path path,
                             final ProjectDiagram diagram,
                             final Metadata metadata,
                             final String comment,
                             final ServiceCallback<ProjectDiagram> callback) {
        diagramServiceCaller.call(v -> {
                                      updateClientMetadata(diagram);
                                      callback.onSuccess(diagram);
                                      fireSavedEvent(sessionManager.getCurrentSession());
                                  },
                                  (message, throwable) -> {
                                      callback.onError(new ClientRuntimeError(throwable));
                                      return false;
                                  }).save(path,
                                          diagram,
                                          metadata,
                                          comment);
    }

    public void saveAsXml(final Path path,
                          final String xml,
                          final Metadata metadata,
                          final String comment,
                          final ServiceCallback<String> callback) {
        diagramServiceCaller.call(v -> callback.onSuccess(xml),
                                  (message, throwable) -> {
                                      callback.onError(new ClientRuntimeError(throwable));
                                      return false;
                                  }).saveAsXml(path,
                                               xml,
                                               metadata,
                                               comment);
    }

    private ClientRuntimeError createOnErrorHandler(final Throwable throwable,
                                                    final String name,
                                                    final Package projectPkg) {
        String FileAlreadyExistsExceptionClassName = "org.uberfire.java.nio.file.FileAlreadyExistsException";
        if (throwable.getClass().getCanonicalName().equals(FileAlreadyExistsExceptionClassName)) {
            String message = translationService.getValue(StunnerProjectClientMessages.BUSINESS_PROCESS_ALREADY_EXISTS,
                                                         projectPkg.getPackageName() + "." + name);
            return new ClientRuntimeError(message, throwable);
        } else {
            return new ClientRuntimeError(throwable);
        }
    }
}
