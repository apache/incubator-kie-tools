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

import java.io.PrintWriter;
import java.io.StringWriter;
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
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.editor.model.FormModelSynchronizationResult;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.model.FormModelerContentError;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.editor.service.backend.SourceFormModelNotFoundException;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorService;
import org.kie.workbench.common.forms.editor.service.shared.ModuleFormFinderService;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.util.UIDGenerator;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class FormEditorServiceImpl extends KieService<FormModelerContent> implements FormEditorService {
    public static final String SHORT_KEY = "FormEditorService.shortKey";
    public static final String LONG_KEY = "FormEditorService.longKey";

    private FieldManager fieldManager;
    private FormModelHandlerManager modelHandlerManager;
    private FormDefinitionSerializer formDefinitionSerializer;
    private ModuleFormFinderService moduleFormFinderService;
    private DeleteService deleteService;
    private RenameService renameService;
    private CopyService copyService;
    private Logger log = LoggerFactory.getLogger(FormEditorServiceImpl.class);
    private IOService ioService;
    private SessionInfo sessionInfo;
    private Event<ResourceOpenedEvent> resourceOpenedEvent;
    private CommentedOptionFactory commentedOptionFactory;

    @Inject
    public FormEditorServiceImpl(@Named("ioStrategy") IOService ioService,
                                 SessionInfo sessionInfo,
                                 Event<ResourceOpenedEvent> resourceOpenedEvent,
                                 FieldManager fieldManager,
                                 FormModelHandlerManager modelHandlerManager,
                                 KieModuleService moduleService,
                                 FormDefinitionSerializer formDefinitionSerializer,
                                 ModuleFormFinderService moduleFormFinderService,
                                 DeleteService deleteService,
                                 CommentedOptionFactory commentedOptionFactory,
                                 RenameService renameService,
                                 CopyService copyService) {
        this.ioService = ioService;
        this.sessionInfo = sessionInfo;
        this.resourceOpenedEvent = resourceOpenedEvent;
        this.fieldManager = fieldManager;
        this.modelHandlerManager = modelHandlerManager;
        this.moduleService = moduleService;
        this.formDefinitionSerializer = formDefinitionSerializer;
        this.moduleFormFinderService = moduleFormFinderService;
        this.commentedOptionFactory = commentedOptionFactory;
        this.deleteService = deleteService;
        this.renameService = renameService;
        this.copyService = copyService;
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
            KieModule module = moduleService.resolveModule(path);
            if (module == null) {
                logger.warn("Form : " + path.toURI() + " does not belong to a valid module");
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
    public FormModelerContent rename(Path path,
                                     String newFileName,
                                     String commitMessage,
                                     boolean saveBeforeRenaming,
                                     FormModelerContent content,
                                     Metadata metadata) {

        if (saveBeforeRenaming) {
            save(path, content, metadata, commitMessage);
        }

        renameService.rename(path, newFileName, commitMessage);

        return content;
    }

    @Override
    public void copy(Path path,
                     String newFileName,
                     String commitMessage,
                     boolean saveBeforeCopying,
                     FormModelerContent content,
                     Metadata metadata) {

        if (saveBeforeCopying) {
            save(path, content, metadata, commitMessage);
        }

        copyService.copy(path, newFileName, commitMessage);
    }

    @Override
    protected FormModelerContent constructContent(Path path, Overview overview) {

        FormModelerContent formModelConent = new FormModelerContent();

        try {
            org.uberfire.java.nio.file.Path kiePath = Paths.convert(path);

            FormDefinition form = findForm(kiePath);

            formModelConent.setDefinition(form);
            formModelConent.setPath(path);
            formModelConent.setOverview(overview);

            FormEditorRenderingContext context = createRenderingContext(form, path);

            formModelConent.setRenderingContext(context);

            if (Optional.ofNullable(form.getModel()).isPresent()) {

                FormModel formModel = form.getModel();

                Optional<FormModelHandler> modelHandlerOptional = getHandlerForForm(form);

                if (modelHandlerOptional.isPresent()) {

                    try {
                        FormModelHandler formModelHandler = modelHandlerOptional.get();

                        formModelHandler.init(formModel, path);

                        formModelHandler.checkSourceModel();

                        FormModelSynchronizationResult synchronizationResult = formModelHandler.synchronizeFormModel();

                        formModel.getProperties().forEach(property -> {
                            Optional<FieldDefinition> fieldOptional = Optional.ofNullable(form.getFieldByBinding(property.getName()));
                            if (!fieldOptional.isPresent()) {
                                synchronizationResult.resolveConflict(property.getName());
                            }
                        });

                        formModelConent.setSynchronizationResult(synchronizationResult);
                    } catch (SourceFormModelNotFoundException ex) {
                        formModelConent.setError(new FormModelerContentError(ex.getShortKey(), ex.getShortKeyParams(), ex.getLongKey(), ex.getLongKeyParams(), ex.getModelSourceKey()));
                    }
                }
            }
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            formModelConent.setError(new FormModelerContentError(SHORT_KEY, null, LONG_KEY, new String[] {writer.toString()}, null));
            log.warn("Error loading form " + path.toURI(), e);
        }

        resourceOpenedEvent.fire(new ResourceOpenedEvent(path, sessionInfo));

        return formModelConent;
    }

    protected FormEditorRenderingContext createRenderingContext(FormDefinition form, Path formPath) {

        FormEditorRenderingContext context = new FormEditorRenderingContext("edition", formPath);
        context.setRootForm(form);
        context.setRenderMode(RenderMode.READ_ONLY_MODE);

        List<FormDefinition> allForms = moduleFormFinderService.findAllForms(formPath);

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

    protected Optional<FormModelHandler> getHandlerForForm(FormDefinition form) {

        return Optional.ofNullable(modelHandlerManager.getFormModelHandler(form.getModel().getClass()));
    }
}
