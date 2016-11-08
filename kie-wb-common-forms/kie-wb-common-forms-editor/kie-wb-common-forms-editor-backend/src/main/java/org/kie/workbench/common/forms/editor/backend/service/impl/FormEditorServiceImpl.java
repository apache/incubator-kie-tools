/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.forms.editor.backend.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.FormCreatorService;
import org.kie.workbench.common.forms.editor.service.FormEditorRenderingContext;
import org.kie.workbench.common.forms.editor.service.FormEditorService;
import org.kie.workbench.common.forms.editor.service.VFSFormFinderService;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.service.FieldManager;
import org.kie.workbench.common.forms.service.FormModelHandler;
import org.kie.workbench.common.forms.service.FormModelHandlerManager;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class FormEditorServiceImpl extends KieService<FormModelerContent> implements FormEditorService {
    public final static String RESOURCE_PATH = "src/main/resources/";

    private Logger log = LoggerFactory.getLogger( FormEditorServiceImpl.class );

    private IOService ioService;

    private User identity;

    private SessionInfo sessionInfo;

    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    protected FieldManager fieldManager;

    protected FormModelHandlerManager modelHandlerManager;

    protected KieProjectService projectService;

    protected FormDefinitionSerializer formDefinitionSerializer;

    protected FormCreatorService formCreatorService;

    protected VFSFormFinderService vfsFormFinderService;

    @Inject
    public FormEditorServiceImpl( @Named("ioStrategy") IOService ioService,
                                  User identity,
                                  SessionInfo sessionInfo,
                                  Event<ResourceOpenedEvent> resourceOpenedEvent,
                                  FieldManager fieldManager,
                                  FormModelHandlerManager modelHandlerManager,
                                  KieProjectService projectService,
                                  FormDefinitionSerializer formDefinitionSerializer,
                                  FormCreatorService formCreatorService,
                                  VFSFormFinderService vfsFormFinderService ) {
        this.ioService = ioService;
        this.identity = identity;
        this.sessionInfo = sessionInfo;
        this.resourceOpenedEvent = resourceOpenedEvent;
        this.fieldManager = fieldManager;
        this.modelHandlerManager = modelHandlerManager;
        this.projectService = projectService;
        this.formDefinitionSerializer = formDefinitionSerializer;
        this.formCreatorService = formCreatorService;
        this.vfsFormFinderService = vfsFormFinderService;
    }

    @Override
    public FormModelerContent loadContent( Path path ) {
        return super.loadContent( path );
    }

    @Inject
    private CommentedOptionFactory commentedOptionFactory;

    @Override
    public Path createForm( Path path, String formName, FormModel formModel ) {
        org.uberfire.java.nio.file.Path kiePath = Paths.convert( path ).resolve(formName);
        try {
            if (ioService.exists(kiePath)) {
                throw new FileAlreadyExistsException(kiePath.toString());
            }
            FormDefinition form = formCreatorService.getNewFormInstance();

            form.setModel( formModel );

            form.setName( formName.substring( 0, formName.lastIndexOf( "." ) ) );

            ioService.write( kiePath, formDefinitionSerializer.serialize( form ),
                    commentedOptionFactory.makeCommentedOption( "" ) );

            return Paths.convert(kiePath);
        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public void delete( Path path, String comment ) {

    }

    @Override
    public Path rename( Path path, String newName, String comment ) {
        return null;
    }

    @Override
    public Path save( Path path, FormModelerContent content, Metadata metadata, String comment ) {
        ioService.write(Paths.convert(path), formDefinitionSerializer.serialize( content.getDefinition() ),
                metadataService.setUpAttributes(path, metadata), commentedOptionFactory.makeCommentedOption(comment));

        return path;
    }

    @Override
    protected FormModelerContent constructContent( Path path, Overview overview ) {
        try {
            org.uberfire.java.nio.file.Path kiePath = Paths.convert(path);

            FormDefinition form = findForm( kiePath );

            FormModelerContent result = new FormModelerContent();
            result.setDefinition( form );
            result.setPath( path );
            result.setOverview( overview );

            FormEditorRenderingContext context = createRenderingContext( form, path );

            result.setRenderingContext( context );


            if ( form.getModel() != null ) {

                FormModelHandler formModelHandler = getHandlerForForm( form, path );

                List<FieldDefinition> modelFields = formModelHandler.getAllFormModelFields();

                Map<String, List<FieldDefinition>> availableFields = new HashMap<String, List<FieldDefinition>>();
                List<FieldDefinition> availableModelFields = new ArrayList<>();

                availableFields.put( form.getModel().getName(), availableModelFields );

                modelFields.forEach( fieldDefinition -> {
                    if ( form.getFieldByName( fieldDefinition.getName() ) == null ) {
                        availableModelFields.add( fieldDefinition );
                    }
                } );

                result.setAvailableFields(availableFields);
            }

            resourceOpenedEvent.fire(new ResourceOpenedEvent( path, sessionInfo ));

            return result;
        } catch (Exception e) {
            log.warn("Error loading form " + path.toURI(), e);
        }
        return null;
    }

    protected FormEditorRenderingContext createRenderingContext( FormDefinition form, Path formPath ) {
        FormEditorRenderingContext context = new FormEditorRenderingContext( formPath );
        context.setRootForm( form );

        List<FormDefinition> allForms = vfsFormFinderService.findAllForms( formPath );

        for ( FormDefinition vfsForm : allForms ) {
            if ( !vfsForm.getId().equals( form.getId() ) ) {
                context.getAvailableForms().put( vfsForm.getId(), vfsForm );
            }
        }
        return context;
    }


    protected FormDefinition findForm( org.uberfire.java.nio.file.Path path ) throws Exception {
        String template = ioService.readAllString( path ).trim();

        FormDefinition form = formDefinitionSerializer.deserialize( template );
        if ( form == null ) {
            form = formCreatorService.getNewFormInstance();
        }

        return form;
    }

    protected FormModelHandler getHandlerForForm( FormDefinition form, Path path ) {
        FormModelHandler handler = modelHandlerManager.getFormModelHandler( form.getModel().getClass() );

        handler.init( form.getModel(), path );

        return handler;
    }
}
