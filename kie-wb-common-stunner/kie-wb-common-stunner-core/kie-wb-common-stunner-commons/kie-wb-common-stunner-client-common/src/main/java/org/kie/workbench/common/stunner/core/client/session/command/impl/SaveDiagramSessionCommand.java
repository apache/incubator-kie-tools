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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import java.util.Objects;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.event.SaveDiagramSessionCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.util.TimerUtils;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.backend.vfs.Path;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * This session commands saves the current Diagram on {@link CanvasHandler#getDiagram()}.
 * This is responsible to generate the diagram SVG and save it as well.
 * Does not support undo operation.
 */
@Dependent
public class SaveDiagramSessionCommand extends AbstractClientSessionCommand<EditorSession> {

    private static Logger LOGGER = Logger.getLogger(SaveDiagramSessionCommand.class.getName());

    private final ClientDiagramService diagramService;
    private final CanvasFileExport canvasExport;
    private TimerUtils timer;

    protected SaveDiagramSessionCommand() {
        this(null, null);
    }

    @Inject
    public SaveDiagramSessionCommand(final ClientDiagramService diagramService, final CanvasFileExport canvasExport) {
        super(true);
        this.diagramService = diagramService;
        this.canvasExport = canvasExport;
        this.timer = new TimerUtils();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void execute(final Callback<V> callback) {
        //TODO: call the diagram client and remove the logic from Editor Screens
    }

    protected void onSaveDiagram(@Observes SaveDiagramSessionCommandExecutedEvent event) {
        checkNotNull("event", event);

        if (Objects.isNull(getSession())) {
            LOGGER.severe("Session is null. Event: " + event);
            return;
        }

        final Metadata diagramMetadata = getCanvasHandler().getDiagram().getMetadata();
        if (Objects.equals(diagramMetadata.getCanvasRootUUID(), event.getDiagramUUID())) {

            //prevents to render selection on canvas
            getSession().getSelectionControl().clearSelection();

            //This is a workaround to overcome the animations executed on canvas when clear selection
            //FIXME: remove the delay, handle this on the proper way, i.e perform the action on a static way
            timer.executeWithDelay(() -> {
                final String rawSvg = canvasExport.exportToSvg(getCanvasHandler());
                diagramService.saveOrUpdateSvg(diagramMetadata.getPath(), rawSvg, new ServiceCallback<Path>() {
                    @Override
                    public void onSuccess(Path path) {
                        LOGGER.info("Diagram SVG saved on " + path);
                    }

                    @Override
                    public void onError(ClientRuntimeError error) {
                        LOGGER.severe("Error saving diagram SVG " + error.getMessage());
                    }
                });
            }, 150);
        }
    }

    protected void setTimer(TimerUtils timer) {
        this.timer = timer;
    }
}
