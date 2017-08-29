/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.editor.backend.service.impl;

import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.editor.model.FormModelSynchronizationResult;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.editor.service.backend.util.UIDGenerator;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorService;
import org.kie.workbench.common.forms.editor.service.shared.VFSFormFinderService;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.model.HasFormModelProperties;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class FormEditorServiceImpl extends KieService<FormModelerContent> implements FormEditorService {

    private Logger log = LoggerFactory.getLogger(FormEditorServiceImpl.class);

    private IOService ioService;

    private SessionInfo sessionInfo;

    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    protected FieldManager fieldManager;

    protected FormModelHandlerManager modelHandlerManager;

    protected FormDefinitionSerializer formDefinitionSerializer;

    protected VFSFormFinderService vfsFormFinderService;

    protected DeleteService deleteService;

    private CommentedOptionFactory commentedOptionFactory;

    @Inject
    public FormEditorServiceImpl(@Named("ioStrategy") IOService ioService,
                                 SessionInfo sessionInfo,
                                 Event<ResourceOpenedEvent> resourceOpenedEvent,
                                 FieldManager fieldManager,
                                 FormModelHandlerManager modelHandlerManager,
                                 KieProjectService projectService,
                                 FormDefinitionSerializer formDefinitionSerializer,
                                 VFSFormFinderService vfsFormFinderService,
                                 DeleteService deleteService,
                                 CommentedOptionFactory commentedOptionFactory) {
        this.ioService = ioService;
        this.sessionInfo = sessionInfo;
        this.resourceOpenedEvent = resourceOpenedEvent;
        this.fieldManager = fieldManager;
        this.modelHandlerManager = modelHandlerManager;
        this.projectService = projectService;
        this.formDefinitionSerializer = formDefinitionSerializer;
        this.vfsFormFinderService = vfsFormFinderService;
        this.commentedOptionFactory = commentedOptionFactory;
        this.deleteService = deleteService;
    }

    @Override
    public FormModelerContent loadContent(Path path) {
        return super.loadContent(path);
    }

    @Override
    public Path createForm(Path path,
                           String formName,
                           FormModel formModel) {
        org.uberfire.java.nio.file.Path kiePath = Paths.convert(path).resolve(formName);
        try {
            if (ioService.exists(kiePath)) {
                throw new FileAlreadyExistsException(kiePath.toString());
            }
            FormDefinition form = new FormDefinition(formModel);

            form.setId(UIDGenerator.generateUID());

            form.setName(formName.substring(0,
                                            formName.lastIndexOf(".")));

            form.setLayoutTemplate(new LayoutTemplate());

            ioService.write(kiePath,
                            formDefinitionSerializer.serialize(form),
                            commentedOptionFactory.makeCommentedOption(""));

            return Paths.convert(kiePath);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public void delete(Path path,
                       String comment) {
        try {
            KieProject project = projectService.resolveProject(path);
            if (project == null) {
                logger.warn("Form : " + path.toURI() + " does not belong to a valid project");
                return;
            }

            deleteService.delete(path,
                                 comment);
        } catch (final Exception e) {
            logger.error("Form: " + path.toURI() + " couldn't be deleted due to the following error. ",
                         e);
        }
    }

    @Override
    public Path save(Path path,
                     FormModelerContent content,
                     Metadata metadata,
                     String comment) {
        ioService.write(Paths.convert(path),
                        formDefinitionSerializer.serialize(content.getDefinition()),
                        metadataService.setUpAttributes(path,
                                                        metadata),
                        commentedOptionFactory.makeCommentedOption(comment));

        return path;
    }

    @Override
    protected FormModelerContent constructContent(Path path,
                                                  Overview overview) {
        try {
            org.uberfire.java.nio.file.Path kiePath = Paths.convert(path);

            FormDefinition form = findForm(kiePath);

            FormModelerContent formModelConent = new FormModelerContent();
            formModelConent.setDefinition(form);
            formModelConent.setPath(path);
            formModelConent.setOverview(overview);

            FormEditorRenderingContext context = createRenderingContext(form,
                                                                        path);

            formModelConent.setRenderingContext(context);

            if (Optional.ofNullable(form.getModel()).isPresent() && form.getModel() instanceof HasFormModelProperties) {

                HasFormModelProperties formModel = (HasFormModelProperties) form.getModel();

                Optional<FormModelHandler> modelOptional = getHandlerForForm(form,
                                                                             path);
                if (modelOptional.isPresent()) {

                    FormModelHandler formModelHandler = modelOptional.get();

                    FormModelSynchronizationResult synchronizationResult = formModelHandler.synchronizeFormModel();

                    formModel.getProperties().forEach(property -> {
                        Optional<FieldDefinition> fieldOptional = Optional.ofNullable(form.getFieldByBinding(property.getName()));
                        if (!fieldOptional.isPresent()) {
                            formModelConent.getAvailableFields().add(formModelHandler.createFieldDefinition(property));
                            synchronizationResult.resolveConflict(property.getName());
                        }
                    });

                    formModelConent.setSynchronizationResult(synchronizationResult);

                    formModelConent.getModelProperties().addAll(formModel.getProperties());
                }
            }

            resourceOpenedEvent.fire(new ResourceOpenedEvent(path,
                                                             sessionInfo));

            return formModelConent;
        } catch (Exception e) {
            log.warn("Error loading form " + path.toURI(),
                     e);
        }
        return null;
    }

    protected FormEditorRenderingContext createRenderingContext(FormDefinition form,
                                                                Path formPath) {
        FormEditorRenderingContext context = new FormEditorRenderingContext(formPath);
        context.setRootForm(form);

        List<FormDefinition> allForms = vfsFormFinderService.findAllForms(formPath);

        for (FormDefinition vfsForm : allForms) {
            if (!vfsForm.getId().equals(form.getId())) {
                context.getAvailableForms().put(vfsForm.getId(),
                                                vfsForm);
            }
        }
        return context;
    }

    protected FormDefinition findForm(org.uberfire.java.nio.file.Path path) throws Exception {
        String template = ioService.readAllString(path).trim();

        FormDefinition form = formDefinitionSerializer.deserialize(template);
        if (form == null) {
            form = new FormDefinition();
            form.setId(UIDGenerator.generateUID());
        }

        return form;
    }

    protected Optional<FormModelHandler> getHandlerForForm(FormDefinition form,
                                                           Path path) {

        if (!(form.getModel() instanceof HasFormModelProperties)) {
            return Optional.empty();
        }

        Optional<FormModelHandler> optional = Optional.ofNullable(modelHandlerManager.getFormModelHandler(form.getModel().getClass()));

        if (optional.isPresent()) {
            optional.get().init((HasFormModelProperties) form.getModel(),
                                path);
        }
        return optional;
    }
}
