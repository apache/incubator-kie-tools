/*
 * Copyright 2013 JBoss Inc
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
package org.kie.workbench.common.services.backend.validation;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.guvnor.common.services.backend.file.DotFileFilter;
import org.guvnor.common.services.backend.file.PomFileFilter;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;

/**
 * Validator capable of validating generic Kie assets (i.e those that are handled by KieBuilder)
 */
public class DefaultGenericKieValidator implements GenericValidator {

    //TODO internationalize error messages?.
    private final static String ERROR_CLASS_NOT_FOUND = "Definition of class \"{0}\" was not found. Consequentially validation cannot be performed.\n" +
            "Please check the necessary external dependencies for this project are configured correctly.";

    private IOService ioService;

    private KieProjectService projectService;

    //Exclude dot-files
    private final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> dotFileFilter = new DotFileFilter();

    //Exclude Project's kmodule.xml file (in case it contains errors causing build to fail)
    private final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> kmoduleFileFilter = new KModuleFileFilter();

    //Include Project's pom.xml (to ensure dependencies are set-up correctly)
    private final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> pomFileFilter = new PomFileFilter();

    //Filter resource that is being validated
    private static class ResourceFilter implements DirectoryStream.Filter<org.uberfire.java.nio.file.Path> {

        private org.uberfire.java.nio.file.Path resourcePath;

        ResourceFilter( final Path resourcePath ) {
            this.resourcePath = Paths.convert( PortablePreconditions.checkNotNull( "resourcePath",
                                                                                   resourcePath ) );
        }

        @Override
        public boolean accept( final org.uberfire.java.nio.file.Path entry ) throws IOException {
            return entry.equals( resourcePath );
        }

    }

    @Inject
    public DefaultGenericKieValidator( final @Named("ioStrategy") IOService ioService,
                                       final KieProjectService projectService ) {
        this.ioService = ioService;
        this.projectService = projectService;
    }

    public List<ValidationMessage> validate( final Path resourcePath,
                                             final InputStream resource,
                                             final DirectoryStream.Filter<org.uberfire.java.nio.file.Path>... supportingFileFilters ) {

        final Project project = projectService.resolveProject( resourcePath );
        if ( project == null ) {
            return Collections.emptyList();
        }

        final KieServices kieServices = KieServices.Factory.get();
        final KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        final String projectPrefix = project.getRootPath().toURI();

        //Add resource to be validated first as:-
        // - KieBuilder fails fast on some compilation issues
        // - KieBuilder validates resources in the order they were added
        // - We want to catch errors for the resource being validated first
        final String destinationPath = resourcePath.toURI().substring( projectPrefix.length() + 1 );
        final BufferedInputStream bis = new BufferedInputStream( resource );
        kieFileSystem.write( destinationPath,
                             KieServices.Factory.get().getResources().newInputStreamResource( bis ) );

        //Set-up filters ignoring resource being validated
        final ResourceFilter resourceFilter = new ResourceFilter( resourcePath );

        //Visit other files that may be needed to support validation of required resource
        final org.uberfire.java.nio.file.Path nioProjectRoot = Paths.convert( project.getRootPath() );
        final DirectoryStream<org.uberfire.java.nio.file.Path> directoryStream = Files.newDirectoryStream( nioProjectRoot );
        visitPaths( projectPrefix,
                    kieFileSystem,
                    directoryStream,
                    resourceFilter,
                    supportingFileFilters );

        //Validate
        final KieBuilder kieBuilder = kieServices.newKieBuilder( kieFileSystem );
        final List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        try {
            final Results kieResults = kieBuilder.buildAll().getResults();
            final String destinationBasePath = getBasePath( destinationPath );
            for ( final Message message : kieResults.getMessages() ) {
                final String messageBasePath = message.getPath() != null ? getBasePath( message.getPath() ) : null;
                if ( messageBasePath == null ||
                        "".equals( messageBasePath ) ||
                        destinationBasePath.endsWith( messageBasePath ) ) {
                    validationMessages.add( convertMessage( message ) );
                }
            }

        } catch ( NoClassDefFoundError e ) {
            final String msg = MessageFormat.format( ERROR_CLASS_NOT_FOUND,
                                                     e.getLocalizedMessage() );
            validationMessages.add( makeErrorMessage( msg ) );
        } catch ( Throwable e ) {
            final String msg = e.getLocalizedMessage();
            validationMessages.add( makeErrorMessage( msg ) );
        }

        return validationMessages;
    }

    //Strip the file extension as it cannot be relied upon when filtering KieBuilder messages.
    //For example we write MyGuidedTemplate.template to KieFileSystem but KieBuilder returns
    //Messages containing MyGuidedTemplate.drl
    private String getBasePath( final String path ) {
        if ( path != null && path.contains( "." ) ) {
            return path.substring( 0,
                                   path.lastIndexOf( "." ) );
        }
        return path;
    }

    private void visitPaths( final String projectPrefix,
                             final KieFileSystem kieFileSystem,
                             final DirectoryStream<org.uberfire.java.nio.file.Path> directoryStream,
                             final ResourceFilter resourceFilter,
                             final DirectoryStream.Filter<org.uberfire.java.nio.file.Path>... supportingFileFilters ) {
        for ( final org.uberfire.java.nio.file.Path path : directoryStream ) {
            if ( Files.isDirectory( path ) ) {
                visitPaths( projectPrefix,
                            kieFileSystem,
                            Files.newDirectoryStream( path ),
                            resourceFilter,
                            supportingFileFilters );

            } else {
                if ( acceptPath( path,
                                 resourceFilter,
                                 supportingFileFilters ) ) {
                    final String destinationPath = path.toUri().toString().substring( projectPrefix.length() + 1 );
                    final InputStream is = ioService.newInputStream( path );
                    final BufferedInputStream bis = new BufferedInputStream( is );

                    kieFileSystem.write( destinationPath,
                                         KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
                }
            }
        }
    }

    private boolean acceptPath( final org.uberfire.java.nio.file.Path path,
                                final ResourceFilter resourceFilter,
                                final DirectoryStream.Filter<org.uberfire.java.nio.file.Path>... supportingFileFilters ) {
        if ( dotFileFilter.accept( path ) ) {
            return false;
        } else if ( kmoduleFileFilter.accept( path ) ) {
            return false;
        } else if ( pomFileFilter.accept( path ) ) {
            return true;
        } else if ( resourceFilter.accept( path ) ) {
            return false;
        }

        for ( DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter : supportingFileFilters ) {
            if ( filter.accept( path ) ) {
                return true;
            }
        }
        return false;
    }

    private ValidationMessage makeErrorMessage( final String msg ) {
        final ValidationMessage validationMessage = new ValidationMessage();
        validationMessage.setLevel( Level.ERROR );
        validationMessage.setText( msg );
        return validationMessage;
    }

    private ValidationMessage convertMessage( final Message message ) {
        final ValidationMessage msg = new ValidationMessage();
        switch ( message.getLevel() ) {
            case ERROR:
                msg.setLevel( Level.ERROR );
                break;
            case WARNING:
                msg.setLevel( Level.WARNING );
                break;
            case INFO:
                msg.setLevel( Level.INFO );
                break;
        }

        msg.setId( message.getId() );
        msg.setLine( message.getLine() );
        msg.setColumn( message.getColumn() );
        msg.setText( message.getText() );
        return msg;
    }

}
