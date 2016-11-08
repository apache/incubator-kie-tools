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
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.IsJavaModel;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.services.datamodeller.util.FileUtils;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.forms.editor.service.VFSFormFinderService;
import org.kie.workbench.common.forms.editor.type.FormResourceTypeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@Dependent
@Service
public class VFSFormFinderServiceImpl implements VFSFormFinderService {
    private static final Logger logger = LoggerFactory.getLogger( VFSFormFinderServiceImpl.class );


    private IOService ioService;

    private KieProjectService projectService;

    private FormDefinitionSerializer serializer;

    @Inject
    public VFSFormFinderServiceImpl( @Named("ioStrategy") IOService ioService,
                                     KieProjectService projectService,
                                     FormDefinitionSerializer serializer ) {
        this.ioService = ioService;
        this.projectService = projectService;
        this.serializer = serializer;
    }

    @Override
    public List<FormDefinition> findAllForms( Path path ) {
        return findForms( path, null );
    }

    @Override
    public List<FormDefinition> findFormsForType( final String typeName, Path path ) {
        return findForms( path, new FormSearchConstraint() {
            @Override
            public boolean accepts( FormDefinition form ) {

                if ( form.getModel() instanceof IsJavaModel ) {
                    return ((IsJavaModel) form.getModel()).getType().equals( typeName );
                }

                return false;
            }
        } );
    }

    @Override
    public FormDefinition findFormById( final String id, Path path ) {
        List<FormDefinition> forms = findForms( path, new FormSearchConstraint() {
            @Override
            public boolean accepts( FormDefinition form ) {
                return form.getId().equals( id );
            }
        } );

        if ( forms != null && !forms.isEmpty() ) {
            return forms.get( 0 );
        }
        return null;
    }

    private List<FormDefinition> findForms( Path path, FormSearchConstraint constraint ) {

        List<FormDefinition> result = new ArrayList<>();

        Project project = projectService.resolveProject( path );

        FileUtils utils = FileUtils.getInstance();

        List<org.uberfire.java.nio.file.Path> nioPaths = new ArrayList<>();

        nioPaths.add( Paths.convert( project.getRootPath() ) );

        Collection<FileUtils.ScanResult> forms = utils.scan( ioService, nioPaths, FormResourceTypeDefinition.EXTENSION, true );

        for ( FileUtils.ScanResult form : forms ) {
            org.uberfire.java.nio.file.Path formPath = form.getFile();

            try {
                FormDefinition formDefinition = serializer.deserialize( ioService.readAllString( formPath ).trim() );

                if ( constraint == null || constraint.accepts( formDefinition ) ) {
                    result.add( formDefinition );
                }
            } catch ( Exception ex ) {
                logger.warn( "Unable to generate FormDefinition for {}", path, ex );
            }
        }

        return result;
    }

    private interface FormSearchConstraint {
        boolean accepts( FormDefinition form );
    }
}
