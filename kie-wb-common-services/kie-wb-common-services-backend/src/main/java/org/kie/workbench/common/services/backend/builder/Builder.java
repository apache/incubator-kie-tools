/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.services.backend.builder;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.guvnor.common.services.backend.file.DotFileFilter;
import org.guvnor.common.services.backend.file.JavaFileFilter;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceChangeType;

import static org.kie.workbench.common.services.backend.builder.BaseFileNameResolver.*;
import static org.kie.workbench.common.services.backend.builder.BuildMessageBuilder.*;
import static org.kie.workbench.common.services.backend.builder.MessageConverter.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

public class Builder {

    private static final Logger logger = LoggerFactory.getLogger( Builder.class );

    //TODO internationalize error messages?.
    private final static String ERROR_EXTERNAL_CLASS_VERIFICATON = "Verification of class {0} failed and will not be available for authoring.\n" +
            "Please check the necessary external dependencies for this project are configured correctly.";

    private final static String ERROR_CLASS_NOT_FOUND = "Definition of class \"{0}\" was not found.\n" +
            "Please check the necessary external dependencies for this project are configured correctly.";

    private KieBuilder kieBuilder;
    private final KieServices kieServices;
    private final KieFileSystem kieFileSystem;

    private final Project project;
    private final GAV projectGAV;
    private final Path projectRoot;
    private final String projectPrefix;

    private final Handles handles = new Handles();

    private final IOService ioService;
    private final KieProjectService projectService;
    private final ProjectImportsService importsService;
    private final List<BuildValidationHelper> buildValidationHelpers;
    private final PackageNameWhiteList packageNameWhiteList;

    private final Map<Path, BuildValidationHelper> nonKieResourceValidationHelpers = new HashMap<Path, BuildValidationHelper>();
    private final Map<Path, List<ValidationMessage>> nonKieResourceValidationHelperMessages = new HashMap<Path, List<ValidationMessage>>();

    private final DirectoryStream.Filter<Path> javaResourceFilter = new JavaFileFilter();
    private final DirectoryStream.Filter<Path> dotFileFilter = new DotFileFilter();

    private Set<String> javaResources = new HashSet<String>();

    public Builder( final Project project,
                    final IOService ioService,
                    final KieProjectService projectService,
                    final ProjectImportsService importsService,
                    final List<BuildValidationHelper> buildValidationHelpers,
                    final PackageNameWhiteList packageNameWhiteList ) {
        this.project = project;
        this.ioService = ioService;
        this.projectService = projectService;
        this.importsService = importsService;
        this.buildValidationHelpers = buildValidationHelpers;
        this.packageNameWhiteList = packageNameWhiteList;
        this.projectGAV = project.getPom().getGav();
        this.projectRoot = Paths.convert( project.getRootPath() );
        this.projectPrefix = projectRoot.toUri().toString();
        this.kieServices = KieServices.Factory.get();
        this.kieFileSystem = kieServices.newKieFileSystem();

        DirectoryStream<org.uberfire.java.nio.file.Path> directoryStream = Files.newDirectoryStream( projectRoot );
        visitPaths( directoryStream );
    }

    public BuildResults build() {
        synchronized ( kieFileSystem ) {
            //KieBuilder is not re-usable for successive "full" builds
            kieBuilder = kieServices.newKieBuilder( kieFileSystem );

            //Record RTEs from KieBuilder - that can fail if a rule uses an inaccessible class
            final BuildResults results = new BuildResults();
            try {
                final Results kieResults = kieBuilder.buildAll().getResults();
                results.addAllBuildMessages( convertMessages( kieResults.getMessages(), handles ) );

            } catch ( LinkageError e ) {
                final String msg = MessageFormat.format( ERROR_CLASS_NOT_FOUND,
                                                         e.getLocalizedMessage() );
                logger.warn( msg, e );
                results.addBuildMessage( makeWarningMessage( msg ) );
            } catch ( Throwable e ) {
                final String msg = e.getLocalizedMessage();
                logger.error( msg, e );
                results.addBuildMessage( makeErrorMessage( msg ) );
            }

            //Add validate messages from external helpers
            for ( Map.Entry<Path, BuildValidationHelper> e : nonKieResourceValidationHelpers.entrySet() ) {
                final org.uberfire.backend.vfs.Path vfsPath = Paths.convert( e.getKey() );
                final List<ValidationMessage> validationMessages = e.getValue().validate( vfsPath );
                nonKieResourceValidationHelperMessages.put( e.getKey(),
                                                            validationMessages );
                results.addAllBuildMessages( convertValidationMessages( validationMessages ) );
            }

            //Check external imports are available. These are loaded when a DMO is requested, but it's better to report them early
            final org.uberfire.java.nio.file.Path nioExternalImportsPath = projectRoot.resolve( "project.imports" );
            if ( Files.exists( nioExternalImportsPath ) ) {
                final org.uberfire.backend.vfs.Path externalImportsPath = Paths.convert( nioExternalImportsPath );
                final ProjectImports projectImports = importsService.load( externalImportsPath );
                final Imports imports = projectImports.getImports();
                for ( final Import item : imports.getImports() ) {
                    final String fullyQualifiedClassName = item.getType();
                    try {
                        Class clazz = this.getClass().getClassLoader().loadClass( item.getType() );
                    } catch ( ClassNotFoundException cnfe ) {
                        logger.error( cnfe.getMessage(), cnfe );
                        final String msg = MessageFormat.format( ERROR_CLASS_NOT_FOUND,
                                                                 fullyQualifiedClassName );
                        results.addBuildMessage( makeWarningMessage( msg ) );
                    }
                }
            }

            //At the end we are interested to ensure that external .jar files referenced as dependencies don't have
            // referential inconsistencies. We will at least provide a basic algorithm to ensure that if an external class
            // X references another external class Y, Y is also accessible by the class loader.
            final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData( ( (InternalKieBuilder) kieBuilder ).getKieModuleIgnoringErrors() );
            final Set<String> packageNamesWhiteList = packageNameWhiteList.filterPackageNames( project,
                                                                                               kieModuleMetaData.getPackages() );
            for ( final String packageName : kieModuleMetaData.getPackages() ) {
                if ( packageNamesWhiteList.contains( packageName ) ) {
                    for ( final String className : kieModuleMetaData.getClasses( packageName ) ) {
                        final String fullyQualifiedClassName = packageName + "." + className;
                        try {
                            final Class clazz = kieModuleMetaData.getClass( packageName,
                                                                            className );
                            if ( clazz != null ) {
                                final TypeSource typeSource = getClassSource( kieModuleMetaData,
                                                                              clazz );
                                if ( TypeSource.JAVA_DEPENDENCY == typeSource ) {
                                    verifyExternalClass( clazz );
                                }
                            } else {
                                final String msg = MessageFormat.format( ERROR_EXTERNAL_CLASS_VERIFICATON,
                                                                         fullyQualifiedClassName );
                                logger.warn( msg );
                            }
                        } catch ( Throwable e ) {
                            final String msg = MessageFormat.format( ERROR_EXTERNAL_CLASS_VERIFICATON,
                                                                     fullyQualifiedClassName );
                            logger.warn( msg, e );
                            results.addBuildMessage( makeWarningMessage( msg ) );
                        }
                    }
                }
            }

            return results;
        }
    }

    private void verifyExternalClass( Class clazz ) {
        //don't recommended to instantiate the class doing clazz.newInstance().
        clazz.getDeclaredConstructors();
        clazz.getDeclaredFields();
        clazz.getDeclaredMethods();
        clazz.getDeclaredClasses();
        clazz.getDeclaredAnnotations();
    }

    public IncrementalBuildResults addResource( final Path resource ) {
        synchronized ( kieFileSystem ) {
            checkNotNull( "resource",
                          resource );

            //Only files can be processed
            if ( !Files.isRegularFile( resource ) ) {
                return new IncrementalBuildResults( projectGAV );
            }

            checkAFullBuildHasBeenPerformed();

            //Resource Type might require "external" validation (i.e. it's not covered by Kie)
            final IncrementalBuildResults results = new IncrementalBuildResults( projectGAV );
            final BuildValidationHelper validator = getBuildValidationHelper( resource );
            if ( validator != null ) {
                final List<ValidationMessage> addedValidationMessages = validator.validate( Paths.convert( resource ) );

                results.addAllAddedMessages( convertValidationMessages( addedValidationMessages ) );
                results.addAllRemovedMessages( convertValidationMessages( nonKieResourceValidationHelperMessages.remove( resource ) ) );

                nonKieResourceValidationHelpers.put( resource,
                                                     validator );
                nonKieResourceValidationHelperMessages.put( resource,
                                                            addedValidationMessages );
            }

            //Add new resource
            final String destinationPath = resource.toUri().toString().substring( projectPrefix.length() + 1 );
            final InputStream is = ioService.newInputStream( resource );
            final BufferedInputStream bis = new BufferedInputStream( is );
            kieFileSystem.write( destinationPath,
                                 KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
            addJavaClass( resource );
            handles.put( getBaseFileName( destinationPath ),
                         Paths.convert( resource ) );

            buildIncrementally( results, destinationPath );

            return results;
        }
    }

    public IncrementalBuildResults deleteResource( final Path resource ) {
        synchronized ( kieFileSystem ) {
            checkNotNull( "resource",
                          resource );
            //The file has already been deleted so we can't check if the Path is a file or folder :(

            checkAFullBuildHasBeenPerformed();

            //Resource Type might have been validated "externally" (i.e. it's not covered by Kie). Clear any errors.
            final IncrementalBuildResults results = new IncrementalBuildResults( projectGAV );
            final BuildValidationHelper validator = getBuildValidationHelper( resource );
            if ( validator != null ) {
                nonKieResourceValidationHelpers.remove( resource );
                results.addAllRemovedMessages( convertValidationMessages( nonKieResourceValidationHelperMessages.remove( resource ) ) );
            }

            //Delete resource
            final String destinationPath = resource.toUri().toString().substring( projectPrefix.length() + 1 );
            kieFileSystem.delete( destinationPath );
            removeJavaClass( resource );

            buildIncrementally( results, destinationPath );

            return results;
        }
    }

    public IncrementalBuildResults updateResource( final Path resource ) {
        synchronized ( kieFileSystem ) {
            return addResource( resource );
        }
    }

    public IncrementalBuildResults applyBatchResourceChanges( final Map<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> changes ) {
        synchronized ( kieFileSystem ) {
            checkNotNull( "changes",
                          changes );

            checkAFullBuildHasBeenPerformed();

            //Add all changes to KieFileSystem before executing the build
            final List<String> changedFilesKieBuilderPaths = new ArrayList<String>();
            final List<ValidationMessage> nonKieResourceValidatorAddedMessages = new ArrayList<ValidationMessage>();
            final List<ValidationMessage> nonKieResourceValidatorRemovedMessages = new ArrayList<ValidationMessage>();

            for ( final Map.Entry<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> pathCollectionEntry : changes.entrySet() ) {
                for ( final ResourceChange change : pathCollectionEntry.getValue() ) {
                    final ResourceChangeType type = change.getType();
                    final Path resource = Paths.convert( pathCollectionEntry.getKey() );

                    checkNotNull( "type", type );
                    checkNotNull( "resource", resource );

                    final String destinationPath = resource.toUri().toString().substring( projectPrefix.length() + 1 );
                    changedFilesKieBuilderPaths.add( destinationPath );
                    switch ( type ) {
                        case ADD:
                        case UPDATE:
                            //Only files can be processed
                            if ( !Files.isRegularFile( resource ) ) {
                                continue;
                            }

                            update( nonKieResourceValidatorAddedMessages,
                                    nonKieResourceValidatorRemovedMessages,
                                    resource,
                                    destinationPath );

                            break;
                        case DELETE:
                            delete( nonKieResourceValidatorRemovedMessages,
                                    resource,
                                    destinationPath );

                    }
                }
            }

            //Perform the Incremental build and get messages from incremental build
            final IncrementalBuildResults results = new IncrementalBuildResults( projectGAV );
            buildIncrementally( results, toArray( changedFilesKieBuilderPaths ) );

            //Copy in BuildMessages for non-KIE resources
            results.addAllAddedMessages( convertValidationMessages( nonKieResourceValidatorAddedMessages ) );
            results.addAllRemovedMessages( convertValidationMessages( nonKieResourceValidatorRemovedMessages ) );

            return results;
        }
    }

    private String[] toArray( List<String> stringList ) {
        final String[] stringArray = new String[ stringList.size() ];
        stringList.toArray( stringArray );
        return stringArray;
    }

    private void delete( List<ValidationMessage> nonKieResourceValidatorRemovedMessages,
                         Path resource,
                         String destinationPath ) {
        //Resource Type might have been validated "externally" (i.e. it's not covered by Kie). Clear any errors.
        nonKieResourceValidationHelpers.remove( resource );
        final List<ValidationMessage> removedValidationMessages = nonKieResourceValidationHelperMessages.remove( resource );
        if ( !( removedValidationMessages == null || removedValidationMessages.isEmpty() ) ) {
            for ( ValidationMessage validationMessage : removedValidationMessages ) {
                nonKieResourceValidatorRemovedMessages.add( validationMessage );
            }
        }

        //The file has already been deleted so we can't check if the Path is a file or folder :(
        kieFileSystem.delete( destinationPath );
        removeJavaClass( resource );
    }

    private void update( List<ValidationMessage> nonKieResourceValidatorAddedMessages,
                         List<ValidationMessage> nonKieResourceValidatorRemovedMessages,
                         Path resource,
                         String destinationPath ) {
        //Resource Type might require "external" validation (i.e. it's not covered by Kie)
        final BuildValidationHelper validator = getBuildValidationHelper( resource );
        if ( validator != null ) {
            final List<ValidationMessage> addedValidationMessages = validator.validate( Paths.convert( resource ) );

            if ( !( addedValidationMessages == null || addedValidationMessages.isEmpty() ) ) {
                for ( ValidationMessage validationMessage : addedValidationMessages ) {
                    nonKieResourceValidatorAddedMessages.add( validationMessage );
                }
            }

            final List<ValidationMessage> removedValidationMessages = nonKieResourceValidationHelperMessages.remove( resource );
            if ( !( removedValidationMessages == null || removedValidationMessages.isEmpty() ) ) {
                for ( ValidationMessage validationMessage : removedValidationMessages ) {
                    nonKieResourceValidatorRemovedMessages.add( validationMessage );
                }
            }
            nonKieResourceValidationHelpers.put( resource,
                                                 validator );
            nonKieResourceValidationHelperMessages.put( resource,
                                                        addedValidationMessages );
        }

        //Add new resource
        final InputStream is = ioService.newInputStream( resource );
        final BufferedInputStream bis = new BufferedInputStream( is );
        kieFileSystem.write( destinationPath,
                             KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
        addJavaClass( resource );
        handles.put( getBaseFileName( destinationPath ),
                     Paths.convert( resource ) );
    }

    private void buildIncrementally( IncrementalBuildResults results,
                                     String... destinationPath ) {
        try {
            final IncrementalResults incrementalResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( destinationPath ).build();
            results.addAllAddedMessages( convertMessages( incrementalResults.getAddedMessages(), handles ) );
            results.addAllRemovedMessages( convertMessages( incrementalResults.getRemovedMessages(), handles ) );

            //Tidy-up removed message handles
            for ( Message message : incrementalResults.getRemovedMessages() ) {
                handles.remove( Handles.RESOURCE_PATH + "/" + getBaseFileName( message.getPath() ) );
            }

        } catch ( LinkageError e ) {
            final String msg = MessageFormat.format( ERROR_CLASS_NOT_FOUND,
                                                     e.getLocalizedMessage() );
            logger.warn( msg, e );
            results.addAddedMessage( makeWarningMessage( msg ) );
        } catch ( Throwable e ) {
            final String msg = e.getLocalizedMessage();
            logger.error( msg, e );
            results.addAddedMessage( makeErrorMessage( msg ) );
        }
    }

    private void checkAFullBuildHasBeenPerformed() {
        if ( !isBuilt() ) {
            throw new IllegalStateException( "A full build needs to be performed before any incremental operations." );
        }
    }

    public KieModule getKieModule() {
        //Kie classes are only available once built
        if ( !isBuilt() ) {
            build();
        }
        synchronized ( kieFileSystem ) {
            return kieBuilder.getKieModule();
        }
    }

    public KieModule getKieModuleIgnoringErrors() {
        //Kie classes are only available once built
        if ( !isBuilt() ) {
            build();
        }
        synchronized ( kieFileSystem ) {
            return ( (InternalKieBuilder) kieBuilder ).getKieModuleIgnoringErrors();
        }
    }

    public KieContainer getKieContainer() {
        BuildResults results = null;

        //Kie classes are only available once built
        if ( !isBuilt() ) {
            results = build();
        } else {
            results = new BuildResults();
            results.addAllBuildMessages( convertMessages( kieBuilder.getResults().getMessages(), handles ) );
        }
        //It's impossible to retrieve a KieContainer if the KieModule contains errors
        if ( results.getErrorMessages().isEmpty() ) {
            KieModule kieModule = kieBuilder.getKieModule();
            ReleaseId releaseId = kieModule.getReleaseId();
            KieContainer kieContainer = kieServices.newKieContainer( releaseId );
            return kieContainer;
        } else {
            return null;
        }
    }

    public boolean isBuilt() {
        synchronized ( kieFileSystem ) {
            return kieBuilder != null;
        }
    }

    private void visitPaths( final DirectoryStream<org.uberfire.java.nio.file.Path> directoryStream ) {
        for ( final org.uberfire.java.nio.file.Path path : directoryStream ) {
            if ( Files.isDirectory( path ) ) {
                visitPaths( Files.newDirectoryStream( path ) );

            } else {
                //Don't process dotFiles
                if ( !dotFileFilter.accept( path ) ) {

                    //Resource Type might require "external" validation (i.e. it's not covered by Kie)
                    final BuildValidationHelper validator = getBuildValidationHelper( path );
                    if ( validator != null ) {
                        nonKieResourceValidationHelpers.put( path,
                                                             validator );
                    }

                    //Add new resource
                    final String destinationPath = path.toUri().toString().substring( projectPrefix.length() + 1 );
                    final InputStream is = ioService.newInputStream( path );
                    final BufferedInputStream bis = new BufferedInputStream( is );
                    kieFileSystem.write( destinationPath,
                                         KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
                    handles.put( getBaseFileName( destinationPath ),
                                 Paths.convert( path ) );

                    //Java classes are handled by KIE so we can safely post-process them here
                    addJavaClass( path );
                }
            }
        }
    }

    private void addJavaClass( final Path path ) {
        if ( !javaResourceFilter.accept( path ) ) {
            return;
        }
        final String fullyQualifiedClassName = getFullyQualifiedClassName( path );
        if ( fullyQualifiedClassName != null ) {
            javaResources.add( fullyQualifiedClassName );
        }
    }

    private void removeJavaClass( final Path path ) {
        if ( !javaResourceFilter.accept( path ) ) {
            return;
        }
        final String fullyQualifiedClassName = getFullyQualifiedClassName( path );
        if ( fullyQualifiedClassName != null ) {
            javaResources.remove( fullyQualifiedClassName );
        }
    }

    private String getFullyQualifiedClassName( final Path path ) {
        final Package pkg = projectService.resolvePackage( Paths.convert( path ) );
        final String packageName = pkg.getPackageName();
        if ( packageName == null ) {
            return null;
        }
        final String className = path.getFileName().toString().replace( ".java",
                                                                        "" );
        return ( packageName.equals( "" ) ? className : packageName + "." + className );
    }

    public TypeSource getClassSource( final KieModuleMetaData metaData,
                                      final Class<?> clazz ) {
        //Was the Type declared in DRL
        if ( metaData.getTypeMetaInfo( clazz ).isDeclaredType() ) {
            return TypeSource.DECLARED;
        }

        //Was the Type defined inside the project or within a dependency
        String fullyQualifiedClassName = clazz.getName();
        int innerClassIdentifierIndex = fullyQualifiedClassName.indexOf( "$" );
        if ( innerClassIdentifierIndex > 0 ) {
            fullyQualifiedClassName = fullyQualifiedClassName.substring( 0,
                                                                         innerClassIdentifierIndex );
        }
        if ( javaResources.contains( fullyQualifiedClassName ) ) {
            return TypeSource.JAVA_PROJECT;
        }
        return TypeSource.JAVA_DEPENDENCY;
    }

    private BuildValidationHelper getBuildValidationHelper( final Path nioResource ) {
        for ( BuildValidationHelper validator : buildValidationHelpers ) {
            final org.uberfire.backend.vfs.Path resource = Paths.convert( nioResource );
            if ( validator.accepts( resource ) ) {
                return validator;
            }
        }
        return null;
    }

}
