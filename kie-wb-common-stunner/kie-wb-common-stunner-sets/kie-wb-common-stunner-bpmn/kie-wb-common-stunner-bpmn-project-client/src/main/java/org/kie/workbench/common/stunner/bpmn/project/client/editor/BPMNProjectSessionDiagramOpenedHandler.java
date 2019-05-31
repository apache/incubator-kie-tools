/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.client.editor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.project.client.type.BPMNDiagramResourceType;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedHandler;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.uberfire.workbench.events.NotificationEvent;

@ApplicationScoped
public class BPMNProjectSessionDiagramOpenedHandler extends BPMNProjectBaseSessionDiagramHandler implements SessionDiagramOpenedHandler {

    @Inject
    public BPMNProjectSessionDiagramOpenedHandler(final BPMNDiagramResourceType bpmnDiagramResourceType,
                                                  final ClientDiagramService diagramService,
                                                  final CanvasFileExport canvasExport,
                                                  final Event<NotificationEvent> notificationEvent,
                                                  final ClientTranslationService translationService) {
        super(bpmnDiagramResourceType, diagramService, canvasExport, notificationEvent, translationService);
    }

    @Override
    public void onSessionDiagramOpened(final ClientSession clientSession) {
        if (clientSession instanceof EditorSession) {
            final EditorSession editorSession = (EditorSession) clientSession;
            final AbstractCanvasHandler canvasHandler = editorSession.getCanvasHandler();
            final ProjectMetadata metadata = (ProjectMetadata) canvasHandler.getDiagram().getMetadata();
            if (metadata.getDiagramSVGPath() == null || metadata.getDiagramSVGGenerator() == ProjectMetadata.SVGGenerator.JBPM_DESIGNER) {
                super.saveOrUpdateDiagram(editorSession);
            }
        }
    }
}
