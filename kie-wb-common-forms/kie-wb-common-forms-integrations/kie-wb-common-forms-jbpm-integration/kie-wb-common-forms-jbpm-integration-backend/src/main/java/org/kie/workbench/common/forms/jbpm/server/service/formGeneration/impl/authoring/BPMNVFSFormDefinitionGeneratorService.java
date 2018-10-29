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

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.authoring;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.kie.workbench.common.forms.editor.model.FormModelSynchronizationResult;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.editor.service.shared.VFSFormFinderService;
import org.kie.workbench.common.forms.editor.service.shared.model.FormModelSynchronizationUtil;
import org.kie.workbench.common.forms.editor.type.FormResourceTypeDefinition;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.AbstractBPMNFormGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.GenerationContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.JavaFormModel;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.util.UIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.io.IOService;

@Authoring
@Dependent
public class BPMNVFSFormDefinitionGeneratorService extends AbstractBPMNFormGeneratorService<Path> {

    private static final Logger logger = LoggerFactory.getLogger(BPMNVFSFormDefinitionGeneratorService.class);

    private FormModelHandlerManager formModelHandlerManager;

    private VFSFormFinderService formFinderService;

    private FormDefinitionSerializer formSerializer;

    private IOService ioService;

    private CommentedOptionFactory commentedOptionFactory;

    private FormModelSynchronizationUtil formModelSynchronizationUtil;

    @Inject
    public BPMNVFSFormDefinitionGeneratorService(FieldManager fieldManager,
                                                 FormModelHandlerManager formModelHandlerManager,
                                                 VFSFormFinderService formFinderService,
                                                 FormDefinitionSerializer formSerializer,
                                                 @Named("ioStrategy") IOService ioService,
                                                 CommentedOptionFactory commentedOptionFactory,
                                                 FormModelSynchronizationUtil formModelSynchronizationUtil) {
        super(fieldManager);
        this.formModelHandlerManager = formModelHandlerManager;
        this.formFinderService = formFinderService;
        this.formSerializer = formSerializer;
        this.ioService = ioService;
        this.commentedOptionFactory = commentedOptionFactory;
        this.formModelSynchronizationUtil = formModelSynchronizationUtil;
    }

    public FormDefinition createRootFormDefinition(GenerationContext<Path> context) {
        FormModelHandler modelHandler = formModelHandlerManager.getFormModelHandler(context.getFormModel().getClass());

        modelHandler.init(context.getFormModel(), context.getSource());

        final FormDefinition form;

        org.uberfire.java.nio.file.Path kiePath = Paths.convert(context.getSource());

        logger.info("Started form generation for '{}'",
                    kiePath);

        if (ioService.exists(kiePath)) {

            form = formSerializer.deserialize(ioService.readAllString(kiePath));

            logger.warn("Already exists form '{}'. Synchronizing form fields:",
                        kiePath);

            // If the form exists on the VFS let's synchronize form fields
            FormModelSynchronizationResult synchronizationResult = modelHandler.synchronizeFormModelProperties(form.getModel(),
                                                                                                               context.getFormModel().getProperties());

            formModelSynchronizationUtil.init(form, synchronizationResult);

            if (synchronizationResult.hasRemovedProperties()) {
                logger.warn("Process/Task has removed variables, checking fields:");
                formModelSynchronizationUtil.fixRemovedFields();
            }

            if (synchronizationResult.hasConflicts()) {
                logger.warn("Process/Task has some variables which type has changed. Checking fields:");
                formModelSynchronizationUtil.resolveConflicts();
            }

            if (synchronizationResult.hasNewProperties()) {
                logger.warn("Process/Task has new variables. Adding them to form:");
                formModelSynchronizationUtil.addNewFields(fieldManager::getDefinitionByModelProperty);
            }

            form.setModel(context.getFormModel());
        } else {
            form = new FormDefinition(context.getFormModel());

            form.setId(UIDGenerator.generateUID());

            form.setName(context.getSource().getFileName());

            form.getFields().addAll(context.getFormModel().getProperties().stream().map(fieldManager::getDefinitionByModelProperty).collect(Collectors.toList()));
        }

        form.setModel(context.getFormModel());

        return form;
    }

    @Override
    protected FormDefinition createModelFormDefinition(String modelType,
                                                       GenerationContext<Path> context) {

        FormDefinition form = super.createModelFormDefinition(modelType, context);

        org.uberfire.java.nio.file.Path path = Paths.convert(context.getSource()).getParent().resolve(form.getName() + "." + FormResourceTypeDefinition.EXTENSION);

        ioService.write(path,
                        formSerializer.serialize(form),
                        commentedOptionFactory.makeCommentedOption("Automatically generated form"));

        return form;
    }

    @Override
    protected List<FieldDefinition> extractModelFields(JavaFormModel formModel,
                                                       GenerationContext<Path> context) {
        FormModelHandler handler = formModelHandlerManager.getFormModelHandler(formModel.getClass());

        handler.init(formModel, context.getSource());

        handler.synchronizeFormModel();

        return formModel.getProperties().stream().map(fieldManager::getDefinitionByModelProperty).collect(Collectors.toList());
    }

    @Override
    protected void log(String message, Exception ex) {
        logger.warn(message, ex);
    }

    @Override
    protected FormDefinition findFormDefinitionForModelType(String modelType,
                                                            GenerationContext<Path> context) {
        FormDefinition form = super.findFormDefinitionForModelType(modelType,
                                                                   context);

        if (form != null) {
            return form;
        }

        List<FormDefinition> foundForms = formFinderService.findFormsForType(modelType,
                                                                             context.getSource());

        Optional<FormDefinition> validForm = foundForms.stream().filter(formDefinition -> !formDefinition.getFields().isEmpty()).findFirst();

        return validForm.orElse(foundForms.stream().findFirst().orElse(null));
    }

    @Override
    protected Supplier<LayoutComponent> getRootFormHeader() {
        return null;
    }

    @Override
    protected boolean supportsEmptyNestedForms() {
        return true;
    }
}
