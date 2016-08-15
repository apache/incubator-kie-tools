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

package org.kie.workbench.common.services.backend.validation.asset;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.uberfire.backend.vfs.Path;

public class ValidatorBuildService {

    private final static String ERROR_CLASS_NOT_FOUND = "Definition of class \"{0}\" was not found. Consequentially validation cannot be performed.\nPlease check the necessary external dependencies for this project are configured correctly.";

    private final BuildService buildService;

    private final ProjectService projectService;

    public ValidatorBuildService( final ProjectService projectService,
                                  final BuildService buildService ) {
        this.projectService = projectService;
        this.buildService = buildService;
    }

    public List<ValidationMessage> validate( final Path resourcePath,
                                             final InputStream inputStream ) {
        try {
            List<ValidationMessage> results = buildIncrementally( resourcePath, inputStream );

            return results;
        } catch ( NoProjectException e ) {
            return new ArrayList<ValidationMessage>();
        } catch ( NoClassDefFoundError e ) {
            return error( MessageFormat.format( ERROR_CLASS_NOT_FOUND, e.getLocalizedMessage() ) );
        } catch ( Throwable e ) {
            return error( e.getLocalizedMessage() );
        }
    }

    private List<ValidationMessage> buildIncrementally( final Path resourcePath,
                                                        final InputStream inputStream ) throws NoProjectException {
        ValidatorResultBuilder resultBuilder = new ValidatorResultBuilder();

        if ( incrementalBuild( resourcePath ) ) {
            resultBuilder.add( buildService.updatePackageResource( resourcePath, inputStream ).getAddedMessages() );
        } else {
            resultBuilder.add( buildService.build( project( resourcePath ), resourcePath, inputStream ).getMessages() );
        }

        return resultBuilder.results();
    }

    private boolean incrementalBuild( final Path resourcePath ) throws NoProjectException {
        final boolean alreadyBuilt = buildService.isBuilt( project( resourcePath ) );
        final boolean isResource = getDestinationPath( resourcePath ).startsWith( "src/main/resources/" );

        return alreadyBuilt && isResource;
    }

    private String getDestinationPath( final Path path ) throws NoProjectException {
        final int rootPathLength = project( path ).getRootPath().toURI().length() + 1;
        return path.toURI().toString().substring( rootPathLength );
    }

    private Project project( final Path resourcePath ) throws NoProjectException {
        final Project project = projectService.resolveProject( resourcePath );

        if ( project == null ) {
            throw new NoProjectException();
        }

        return project;
    }

    private ArrayList<ValidationMessage> error( String errorMessage ) {
        return new ArrayList<ValidationMessage>() {{
            add( new ValidationMessage( Level.ERROR, errorMessage ) );
        }};
    }
}
