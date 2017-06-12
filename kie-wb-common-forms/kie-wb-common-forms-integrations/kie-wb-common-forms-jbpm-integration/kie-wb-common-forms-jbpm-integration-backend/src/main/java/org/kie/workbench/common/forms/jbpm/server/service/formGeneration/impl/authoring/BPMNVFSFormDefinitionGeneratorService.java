/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.authoring;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.kie.workbench.common.forms.commons.layout.FormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.editor.service.backend.util.UIDGenerator;
import org.kie.workbench.common.forms.editor.service.shared.VFSFormFinderService;
import org.kie.workbench.common.forms.editor.type.FormResourceTypeDefinition;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.AbstractBPMNFormGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.GenerationContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.JavaModel;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.service.FieldManager;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@Authoring
@Dependent
public class BPMNVFSFormDefinitionGeneratorService extends AbstractBPMNFormGeneratorService<Path> {

    private FormModelHandlerManager formModelHandlerManager;

    private VFSFormFinderService formFinderService;

    private FormDefinitionSerializer formSerializer;

    private IOService ioService;

    private CommentedOptionFactory commentedOptionFactory;

    @Inject
    public BPMNVFSFormDefinitionGeneratorService(FieldManager fieldManager,
                                                 FormLayoutTemplateGenerator layoutTemplateGenerator,
                                                 FormModelHandlerManager formModelHandlerManager,
                                                 VFSFormFinderService formFinderService,
                                                 FormDefinitionSerializer formSerializer,
                                                 @Named("ioStrategy") IOService ioService,
                                                 CommentedOptionFactory commentedOptionFactory) {
        super(fieldManager,
              layoutTemplateGenerator);
        this.formModelHandlerManager = formModelHandlerManager;
        this.formFinderService = formFinderService;
        this.formSerializer = formSerializer;
        this.ioService = ioService;
        this.commentedOptionFactory = commentedOptionFactory;
    }

    public FormDefinition createRootFormDefinition(GenerationContext<Path> context) {
        FormModelHandler modelHandler = formModelHandlerManager.getFormModelHandler(context.getFormModel().getClass());

        modelHandler.init(context.getFormModel(),
                          context.getSource());

        List<FieldDefinition> modelFields = modelHandler.getAllFormModelFields();

        FormDefinition form;

        org.uberfire.java.nio.file.Path kiePath = Paths.convert(context.getSource());

        if (ioService.exists(kiePath)) {
            form = formSerializer.deserialize(ioService.readAllString(kiePath));

            form.getFields().forEach(originalField -> {

                FieldDefinition modelField = modelFields.stream().filter(field -> field.getBinding().equals(originalField.getBinding())).findFirst().orElse(null);

                if (modelField != null) {
                    originalField.setName(modelField.getName());
                    originalField.setStandaloneClassName(modelField.getStandaloneClassName());
                    modelFields.remove(modelField);
                } else {
                    originalField.setBinding(null);
                }
            });

            form.getFields().addAll(modelFields);

            layoutTemplateGenerator.updateLayoutTemplate(form,
                                                         modelFields);
        } else {
            form = new FormDefinition(context.getFormModel());

            form.setId(UIDGenerator.generateUID());

            form.setName(context.getSource().getFileName());

            form.getFields().addAll(modelFields);

            layoutTemplateGenerator.generateLayoutTemplate(form);
        }

        form.setModel(context.getFormModel());

        return form;
    }

    @Override
    protected FormDefinition createModelFormDefinition(String modelType,
                                                       GenerationContext<Path> context) {

        FormDefinition form = super.createModelFormDefinition(modelType,
                                                              context);

        org.uberfire.java.nio.file.Path path = Paths.convert(context.getSource()).getParent().resolve(form.getName() + "." + FormResourceTypeDefinition.EXTENSION);

        ioService.write(path,
                        formSerializer.serialize(form),
                        commentedOptionFactory.makeCommentedOption("Automatically generated form"));

        return form;
    }

    @Override
    protected List<FieldDefinition> extractModelFields(JavaModel formModel,
                                                       GenerationContext<Path> context) {
        FormModelHandler handler = formModelHandlerManager.getFormModelHandler(formModel.getClass());
        handler.init(formModel,
                     context.getSource());
        return handler.getAllFormModelFields();
    }

    @Override
    protected FormDefinition findFormDefinitionForModelType(String modelType,
                                                            GenerationContext<Path> context) {
        FormDefinition form = super.findFormDefinitionForModelType(modelType,
                                                                   context);

        if (form != null) {
            return form;
        }

        return formFinderService.findFormsForType(modelType,
                                                  context.getSource()).stream().findFirst().orElse(null);
    }
}
