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

package org.kie.workbench.common.stunner.forms.backend.service;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.forms.service.FormGeneratedEvent;
import org.kie.workbench.common.stunner.forms.service.FormGenerationFailureEvent;
import org.kie.workbench.common.stunner.forms.service.FormGenerationService;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
@Service
public class FormGenerationServiceImpl implements FormGenerationService {

    private static Logger LOGGER = Logger.getLogger(FormGenerationServiceImpl.class.getName());

    private final Event<FormGeneratedEvent> formGeneratedEvent;
    private final Event<FormGenerationFailureEvent> formGenerationFailureEvent;
    private final FormDefinitionGenerator formDefinitionGenerator;

    // CDI proxy.
    protected FormGenerationServiceImpl() {
        this(null, null, null);
    }

    @Inject
    public FormGenerationServiceImpl(final Event<FormGeneratedEvent> formGeneratedEvent,
                                     final Event<FormGenerationFailureEvent> formGenerationFailureEvent,
                                     final FormDefinitionGenerator formDefinitionGenerator) {
        this.formGeneratedEvent = formGeneratedEvent;
        this.formGenerationFailureEvent = formGenerationFailureEvent;
        this.formDefinitionGenerator = formDefinitionGenerator;
    }

    @Override
    public void generateProcessForm(final Diagram diagram) {
        doGenerate(diagram, () -> formDefinitionGenerator.generateProcessForm(diagram));
    }

    @Override
    public void generateAllForms(final Diagram diagram) {
        doGenerate(diagram, () -> formDefinitionGenerator.generateAllForms(diagram));
    }

    @Override
    public void generateSelectedForms(final Diagram diagram,
                                      final String[] ids) {
        this.doGenerate(diagram, () -> formDefinitionGenerator.generateSelectedForms(diagram, ids));
    }

    private void doGenerate(final Diagram diagram,
                            final Runnable runnable) {
        LOGGER.finest("Starting form generation...");

        final Metadata metadata = diagram.getMetadata();
        final String definitionSetId = metadata.getDefinitionSetId();
        final String graphUUID = diagram.getGraph().getUUID();
        final Path path = metadata.getPath();
        final String fileName = null != path ?
                path.getFileName() :
                "<no-file>";

        LOGGER.finest("FormGeneration test " +
                              "[definitionSetId=" + definitionSetId +
                              "[graphUUID=" + graphUUID +
                              "[path=" + path +
                              "[fileName=" + fileName);

        try {
            runnable.run();

            // Fire the form generated event.
            formGeneratedEvent.fire(new FormGeneratedEvent(graphUUID, diagram.getName()));

            LOGGER.finest("Form generation completed successfully!");
        } catch (Throwable e) {
            formGenerationFailureEvent.fire(new FormGenerationFailureEvent(graphUUID, diagram.getName()));
            LOGGER.severe("Error during Form Generation service: " + e.getMessage());
        }
    }
}
