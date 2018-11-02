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

package org.kie.workbench.common.stunner.forms.backend.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.bpmn2.Definitions;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.BPMNFormModelGenerator;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.BPMNFormGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.authoring.Authoring;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.forms.backend.gen.FormGenerationModelProviders;
import org.kie.workbench.common.stunner.forms.backend.service.FormDefinitionGenerator;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@Dependent
public class FormDefinitionGeneratorImpl implements FormDefinitionGenerator {

    private static Logger LOGGER = Logger.getLogger(FormDefinitionGeneratorImpl.class.getName());

    private final FormGenerationModelProviders providers;
    private final IOService ioService;
    private final BPMNFormModelGenerator bpmnFormModelGenerator;
    private final FormDefinitionSerializer serializer;
    private final BPMNFormGeneratorService<Path> bpmnFormGeneratorService;

    @Inject
    public FormDefinitionGeneratorImpl(FormGenerationModelProviders providers,
                                       @Named("ioStrategy") final IOService ioService,
                                       final BPMNFormModelGenerator bpmnFormModelGenerator,
                                       final FormDefinitionSerializer serializer,
                                       @Authoring final BPMNFormGeneratorService<Path> bpmnFormGeneratorService) {
        this.providers = providers;
        this.ioService = ioService;
        this.bpmnFormModelGenerator = bpmnFormModelGenerator;
        this.serializer = serializer;
        this.bpmnFormGeneratorService = bpmnFormGeneratorService;
    }

    @Override
    public void generateProcessForm(Diagram diagram) {

        try {
            LOGGER.finest("Generating form for process ");

            final Definitions definitions = toDefinitions(diagram);

            final Path path = diagram.getMetadata().getPath();

            final BusinessProcessFormModel formModel = bpmnFormModelGenerator.generateProcessFormModel(definitions, path);

            createFormForModel(formModel, Paths.convert(path));
        } catch (Exception ex) {
            LOGGER.severe("Error generating process form");
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void generateSelectedForms(Diagram diagram, String... taskIds) {
        if (null != taskIds) {
            generateSelectedFormsForTasks(diagram, taskIds);
        }
    }

    private void generateSelectedFormsForTasks(Diagram diagram, String... taskIds) {
        try {
            final String idsRaw = Arrays.stream(taskIds).collect(Collectors.joining(","));

            LOGGER.finest("Generating form for tasks " + idsRaw);

            final Path path = diagram.getMetadata().getPath();

            org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);

            ioService.startBatch(ioService.getFileSystem(nioPath.toUri()));

            final Definitions definitions = toDefinitions(diagram);

            for (String taskId : taskIds) {
                final TaskFormModel formModel = bpmnFormModelGenerator.generateTaskFormModel(definitions, taskId, path);

                createFormForModel(formModel, nioPath);
            }
        } catch (Exception ex) {
            LOGGER.severe("Error generating task forms");
            throw new RuntimeException(ex);
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public void generateAllForms(Diagram diagram) {

        try {
            LOGGER.finest("Generating all forms");

            final Path path = diagram.getMetadata().getPath();

            final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);

            ioService.startBatch(ioService.getFileSystem(nioPath.toUri()));

            final Definitions definitions = toDefinitions(diagram);

            final BusinessProcessFormModel processFormModel = bpmnFormModelGenerator.generateProcessFormModel(definitions, path);

            createFormForModel(processFormModel, nioPath);

            List<TaskFormModel> taskFormModels = bpmnFormModelGenerator.generateTaskFormModels(definitions, path);

            for (TaskFormModel taskFormModel : taskFormModels) {
                createFormForModel(taskFormModel, nioPath);
            }
        } catch (Exception ex) {
            LOGGER.severe("Error generating all diagram forms");
            throw new RuntimeException(ex);
        } finally {
            ioService.endBatch();
        }
    }

    public void createFormForModel(JBPMFormModel formModel, org.uberfire.java.nio.file.Path path) {
        if (formModel == null) {
            LOGGER.severe("Impossible to create form for empty model");
            throw new IllegalArgumentException("Impossible to create form for empty model");
        }

        org.uberfire.java.nio.file.Path nioFormPath = path.getParent().resolve(formModel.getFormName() + ".frm");

        FormDefinition form = bpmnFormGeneratorService.generateForms(formModel,
                                                                     Paths.convert(nioFormPath)).getRootForm();

        if (!ioService.exists(nioFormPath)) {
            ioService.createFile(nioFormPath);
        }

        ioService.write(nioFormPath,
                        serializer.serialize(form));
    }

    protected Definitions toDefinitions(Diagram diagram) throws IOException {
        return (Definitions) providers.getModelProvider(diagram).generate(diagram);
    }
}
