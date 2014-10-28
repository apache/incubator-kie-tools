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

package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Charsets;
import org.drools.core.base.ClassTypeResolver;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.file.JavaFileFilter;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.file.CopyService;
import org.guvnor.common.services.shared.file.DeleteService;
import org.guvnor.common.services.shared.file.RenameService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.screens.datamodeller.backend.server.file.DataModelerCopyHelper;
import org.kie.workbench.common.screens.datamodeller.backend.server.file.DataModelerRenameHelper;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectCreatedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelerError;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.EditorModelContent;
import org.kie.workbench.common.screens.datamodeller.model.GenerationResult;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;
import org.kie.workbench.common.screens.datamodeller.model.TypeInfoResult;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.datamodeller.service.ServiceException;
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationContext;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationEngine;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.PropertyTypeFactoryImpl;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverError;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverResult;
import org.kie.workbench.common.services.datamodeller.driver.impl.DataModelOracleModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.impl.JavaModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.impl.JavaRoasterModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.impl.ProjectDataModelOracleUtils;
import org.kie.workbench.common.services.datamodeller.parser.JavaFileHandler;
import org.kie.workbench.common.services.datamodeller.parser.JavaFileHandlerFactory;
import org.kie.workbench.common.services.datamodeller.parser.descr.ClassDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FieldDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FileDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.VariableDeclarationDescr;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.datamodeller.util.FileHashingUtils;
import org.kie.workbench.common.services.datamodeller.util.FileUtils;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueFieldIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueTypeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Pair;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;

@Service
@ApplicationScoped
public class DataModelerServiceImpl extends KieService implements DataModelerService {

    private static final Logger logger = LoggerFactory.getLogger( DataModelerServiceImpl.class );

    @Inject
    @Named("ioStrategy")
    IOService ioService;

    @Inject
    private User identity;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private DataModelerServiceHelper serviceHelper;

    @Inject
    private ProjectResourceDriverListener generationListener;

    @Inject
    private Event<ResourceBatchChangesEvent> resourceBatchChangesEvent;

    @Inject
    private Event<DataObjectCreatedEvent> dataObjectCreatedEvent;

    @Inject
    private Event<DataObjectDeletedEvent> dataObjectDeletedEvent;

    @Inject
    private RefactoringQueryService queryService;

    @Inject
    private POMService pomService;

    @Inject
    private LRUBuilderCache builderCache;

    @Inject
    private Event<PublishBatchMessagesEvent> publishBatchMessagesEvent;

    @Inject
    private DeleteService deleteService;

    @Inject
    private CopyService copyService;

    @Inject
    private RenameService renameService;

    @Inject
    private DataModelerCopyHelper copyHelper;

    @Inject
    private DataModelerRenameHelper renameHelper;

    @Inject
    private GenericValidator genericValidator;

    private static final String DEFAULT_COMMIT_MESSAGE = "Data modeller generated action.";

    public DataModelerServiceImpl() {
    }

    @Override
    public Path createJavaFile( final Path context,
                                final String fileName,
                                final String comment ) {

        final org.uberfire.java.nio.file.Path nioPath = Paths.convert( context ).resolve( fileName );
        final Path newPath = Paths.convert( nioPath );

        if ( ioService.exists( nioPath ) ) {
            throw new FileAlreadyExistsException( nioPath.toString() );
        }

        try {

            final Package currentPackage = projectService.resolvePackage( context );
            String packageName = currentPackage.getPackageName();
            String className = fileName.substring( 0, fileName.indexOf( ".java" ) );

            final KieProject currentProject = projectService.resolveProject( context );

            DataObject dataObject = new DataObjectImpl( packageName, className );
            String source = createJavaSource( dataObject );

            ioService.write( nioPath, source, serviceHelper.makeCommentedOption( comment ) );

            DataObjectTO dataObjectTO = new DataObjectTO();
            serviceHelper.domain2To( dataObject, dataObjectTO, DataModelTO.TOStatus.PERSISTENT );
            dataObjectTO.setPath( newPath );

            dataObjectCreatedEvent.fire( new DataObjectCreatedEvent( currentProject, dataObjectTO ) );

            return newPath;

        } catch ( Exception e ) {
            //uncommon error.
            logger.error( "It was not possible to create Java file, for path: " + context.toURI() + ", fileName: " + fileName, e );
            throw new ServiceException( "It was not possible to create Java file, for path: " + context.toURI() + ", fileName: " + fileName, e );
        }
    }

    @Override
    public EditorModelContent loadContent( final Path path ) {
        if ( logger.isDebugEnabled() ) {
            logger.debug( "Loading editor model from path: " + path.toURI() );
        }

        Long startTime = System.currentTimeMillis();
        EditorModelContent editorModelContent = new EditorModelContent();

        try {
            //TODO review this method implementation for optimizations
            KieProject project = projectService.resolveProject( path );
            if ( project == null ) {
                logger.warn( "File : " + path.toURI() + " do not belong to a valid project" );
                return editorModelContent;
            }

            Pair<DataModelTO, ModelDriverResult> resultPair = loadModel( project, false );
            DataModelTO dataModelTO = resultPair.getK1();
            String className = calculateClassName( project, path );

            editorModelContent.setCurrentProject( project );
            editorModelContent.setCurrentProjectPackages( projectService.resolvePackages( project ) );
            editorModelContent.setDataModel( dataModelTO );
            editorModelContent.setDataObject( dataModelTO.getDataObjectByClassName( className ) );
            if ( editorModelContent.getDataObject() != null ) {
                editorModelContent.setOriginalClassName( className );
                editorModelContent.setOriginalPackageName( NamingUtils.extractPackageName( className ) );
            }

            //Read the sources for the file being edited.
            if ( ioService.exists( Paths.convert( path ) ) ) {
                String source = ioService.readAllString( Paths.convert( path ) );
                editorModelContent.setSource( source );
            }

            if ( resultPair.getK2().hasErrors() ) {
                editorModelContent.setErrors( serviceHelper.toDataModelerError( resultPair.getK2().getErrors() ) );
            }

            editorModelContent.setOverview( loadOverview( path ) );

            editorModelContent.setElapsedTime( System.currentTimeMillis() - startTime );
            if ( logger.isDebugEnabled() ) {
                logger.debug( "Time elapsed when loading editor model from:" + path + " : " + editorModelContent.getElapsedTime() + " ms" );
            }

            return editorModelContent;

        } catch ( Exception e ) {
            logger.error( "Editor model couldn't be loaded from path: " + ( path != null ? path.toURI() : path ) + ".", e );
            throw new ServiceException( "Editor model couldn't be loaded from path: " + ( path != null ? path.toURI() : path ) + ".", e );
        }
    }

    @Override
    public DataModelTO loadModel( final KieProject project ) {
        Pair<DataModelTO, ModelDriverResult> resultPair = loadModel( project, true );
        return resultPair != null ? resultPair.getK1() : null;
    }

    private Pair<DataModelTO, ModelDriverResult> loadModel( final KieProject project,
                                                            boolean processErrors ) {

        if ( logger.isDebugEnabled() ) {
            logger.debug( "Loading data model from path: " + project.getRootPath() );
        }

        Long startTime = System.currentTimeMillis();

        DataModel dataModel = null;
        Path projectPath = null;
        Package defaultPackage = null;

        try {
            projectPath = project.getRootPath();
            defaultPackage = projectService.resolveDefaultPackage( project );
            if ( logger.isDebugEnabled() ) {
                logger.debug( "Current project path is: " + projectPath );
            }

            ClassLoader classLoader = getProjectClassLoader( project );
            //ModelDriver modelDriver = new JavaModelDriver( ioService, Paths.convert( defaultPackage.getPackageMainSrcPath() ) , true, classLoader );

            ModelDriver modelDriver = new JavaRoasterModelDriver( ioService, Paths.convert( defaultPackage.getPackageMainSrcPath() ), true, classLoader );
            ModelDriverResult result = modelDriver.loadModel();
            dataModel = result.getDataModel();

            if ( processErrors && result.hasErrors() ) {
                processErrors( project, result );
            }

            //by now we still use the DMO to calculate project external dependencies.
            ProjectDataModelOracle projectDataModelOracle = dataModelService.getProjectDataModel( projectPath );
            ProjectDataModelOracleUtils.loadExternalDependencies( dataModel, projectDataModelOracle, classLoader );

            //Objects read from persistent .java format are tagged as PERSISTENT objects
            DataModelTO dataModelTO = serviceHelper.domain2To( dataModel, result.getClassPaths(), DataModelTO.TOStatus.PERSISTENT, true );

            Long endTime = System.currentTimeMillis();
            if ( logger.isDebugEnabled() ) {
                logger.debug( "Time elapsed when loading " + projectPath.getFileName() + ": " + ( endTime - startTime ) + " ms" );
            }

            return new Pair<DataModelTO, ModelDriverResult>( dataModelTO, result );

        } catch ( Exception e ) {
            logger.error( "Data model couldn't be loaded, path: " + projectPath + ", projectPath: " + projectPath + ".", e );
            throw new ServiceException( "Data model couldn't be loaded, path: " + projectPath + ", projectPath: " + projectPath + ".", e );
        }
    }

    public TypeInfoResult loadJavaTypeInfo( final String source ) {

        try {
            JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver();
            TypeInfoResult result = new TypeInfoResult();
            org.kie.workbench.common.services.datamodeller.driver.TypeInfoResult driverResult = modelDriver.loadJavaTypeInfo( source );
            result.setJavaTypeInfo( serviceHelper.domain2TO( driverResult.getTypeInfo() ) );
            if ( driverResult.hasErrors() ) {
                result.setErrors( serviceHelper.toDataModelerError( driverResult.getErrors() ) );
            }
            return result;
        } catch ( Exception e ) {
            logger.error( "JavaTypeInfo object couldn't be loaded for source: " + source, e );
            throw new ServiceException( "JavaTypeInfo object couldn't be loaded for source.", e );
        }
    }

    private Pair<DataObjectTO, List<DataModelerError>> loadDataObject( final Path projectPath,
                                                                       final String source,
                                                                       final Path sourcePath ) {

        if ( logger.isDebugEnabled() ) {
            logger.debug( "Loading data object from projectPath: " + projectPath.toURI() );
        }

        KieProject project;
        DataObjectTO dataObjectTO = null;

        try {

            project = projectService.resolveProject( projectPath );
            if ( project == null ) {
                return new Pair<DataObjectTO, List<DataModelerError>>( null, new ArrayList<DataModelerError>() );
            }

            ClassLoader classLoader = getProjectClassLoader( project );
            JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver( ioService, null, false, classLoader );
            ModelDriverResult driverResult = modelDriver.loadDataObject( source, Paths.convert( sourcePath ) );

            if ( !driverResult.hasErrors() ) {
                if ( driverResult.getDataModel().getDataObjects().size() > 0 ) {
                    DataModelTO dataModelTO = serviceHelper.domain2To( driverResult.getDataModel(), driverResult.getClassPaths(), DataModelTO.TOStatus.PERSISTENT, true );
                    dataObjectTO = dataModelTO.getDataObjects().iterator().next();
                }
                return new Pair<DataObjectTO, List<DataModelerError>>( dataObjectTO, new ArrayList<DataModelerError>() );
            } else {
                return new Pair<DataObjectTO, List<DataModelerError>>( null, serviceHelper.toDataModelerError( driverResult.getErrors() ) );
            }

        } catch ( Exception e ) {
            logger.error( "Data object couldn't be loaded, path: " + projectPath + ", projectPath: " + projectPath + ".", e );
            throw new ServiceException( "Data object couldn't be loaded, path: " + projectPath + ", projectPath: " + projectPath + ".", e );
        }
    }

    private Pair<DataObjectTO, List<DataModelerError>> loadDataObject( final Path path ) {
        return loadDataObject( path, ioService.readAllString( Paths.convert( path ) ), path );
    }

    /**
     * Updates Java code provided in the source parameter with the data object values provided in the dataObjectTO
     * parameter. This method does not write any changes in the file system.
     * @param source Java code to be updated.
     * @param path Path to the java file. (used for error messages generation and project )
     * @param dataObjectTO Data object definition.
     * @return returns a GenerationResult object with the updated Java code and the dataObjectTO parameter as is.
     */
    @Override
    public GenerationResult updateSource( final String source,
                                          final Path path,
                                          final DataObjectTO dataObjectTO ) {

        GenerationResult result = new GenerationResult();
        KieProject project;

        try {

            project = projectService.resolveProject( path );
            if ( project == null ) {
                logger.warn( "File : " + path.toURI() + " do not belong to a valid project" );
                result.setSource( source );
                return result;
            }

            ClassLoader classLoader = getProjectClassLoader( project );
            Pair<String, List<DataModelerError>> updateResult = updateJavaSource( source, dataObjectTO, new HashMap<String, String>(), new ArrayList<String>(), classLoader );
            result.setSource( updateResult.getK1() );
            result.setDataObject( dataObjectTO );
            result.setErrors( updateResult.getK2() );

            return result;

        } catch ( Exception e ) {
            logger.error( "Source file for data object: " + dataObjectTO.getClassName() + ", couldn't be updated", e );
            throw new ServiceException( "Source file for data object: " + dataObjectTO.getClassName() + ", couldn't be updated", e );
        }
    }

    /**
     * Updates data object provided in the dataObjectTO parameter with the Java code provided in the source parameter.
     * This method does not write changes in the file system.
     * @param dataObjectTO Data object definition to be updated.
     * @param source Java code to use for the update.
     * @param path Path to the java file. (used for error messages generation)
     * @return returns a GenerationResult object with the updated data object and the source and path parameter as is.
     */
    @Override
    public GenerationResult updateDataObject( final DataObjectTO dataObjectTO,
                                              final String source,
                                              final Path path ) {
        //Resolve the dataobject update in memory

        GenerationResult result = new GenerationResult();
        KieProject project;

        try {
            result.setSource( source );
            project = projectService.resolveProject( path );
            if ( project == null ) {
                logger.warn( "File : " + path.toURI() + " do not belong to a valid project" );
                result.setSource( source );
                return result;
            }

            ClassLoader classLoader = getProjectClassLoader( project );
            JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver( ioService, Paths.convert( path ), false, classLoader );
            ModelDriverResult driverResult = modelDriver.loadDataObject( source, Paths.convert( path ) );

            if ( driverResult.hasErrors() ) {
                result.setErrors( serviceHelper.toDataModelerError( driverResult.getErrors() ) );
            } else {
                if ( driverResult.getDataModel().getDataObjects().size() > 0 ) {
                    DataModelTO dataModelTO = serviceHelper.domain2To( driverResult.getDataModel(), driverResult.getClassPaths(), DataModelTO.TOStatus.PERSISTENT, true );
                    result.setDataObject( dataModelTO.getDataObjects().iterator().next() );
                }
            }

            return result;
        } catch ( Exception e ) {
            logger.error( "Source file for data object: " + dataObjectTO.getClassName() + ", couldn't be parsed", e );
            throw new ServiceException( "Source file for data object: " + dataObjectTO.getClassName() + ", couldn't be parsed", e );
        }
    }

    @Override
    public GenerationResult saveSource( final String source,
                                        final Path path,
                                        final DataObjectTO dataObjectTO,
                                        final Metadata metadata,
                                        final String commitMessage ) {
        return saveSource( source, path, dataObjectTO, metadata, commitMessage, null, null );
    }

    @Override
    public GenerationResult saveSource( final String source,
                                        final Path path,
                                        final DataObjectTO dataObjectTO,
                                        final Metadata metadata,
                                        final String commitMessage,
                                        final String newPackageName,
                                        final String newFileName ) {

        Boolean onBatch = false;

        try {

            GenerationResult result = resolveSaveSource( source, path, dataObjectTO );

            Package currentPackage = projectService.resolvePackage( path );
            Package targetPackage = currentPackage;
            String targetName = path.getFileName();
            org.uberfire.java.nio.file.Path targetPath = Paths.convert( path );

            boolean packageChanged = false;
            boolean nameChanged = false;

            if ( newPackageName != null && ( currentPackage == null || !newPackageName.equals( currentPackage.getPackageName() ) ) ) {
                //make sure destination package exists.
                targetPackage = serviceHelper.ensurePackageStructure( projectService.resolveProject( path ), newPackageName );
                packageChanged = true;
            }

            if ( newFileName != null && !(newFileName+".java").equals( path.getFileName() ) ) {
                targetName = newFileName+".java";
                nameChanged = true;
            }


            if ( packageChanged ) {
                targetPath = Paths.convert(  targetPackage.getPackageMainSrcPath() ).resolve( targetName );

                ioService.startBatch( targetPath.getFileSystem() );
                onBatch = true;
                ioService.write( Paths.convert( path ),
                        result.getSource(),
                        metadataService.setUpAttributes( path, metadata ),
                        serviceHelper.makeCommentedOption( commitMessage ) );

                //deleteService.delete( path, commitMessage );
                ioService.move( Paths.convert( path ), targetPath, serviceHelper.makeCommentedOption( commitMessage ) );
                result.setPath( Paths.convert( targetPath ) );

            } else if ( nameChanged ) {
                //obs, rename service already do a startBatch.
                ioService.write( Paths.convert( path ),
                        result.getSource(),
                        metadataService.setUpAttributes( path, metadata ),
                        serviceHelper.makeCommentedOption( commitMessage ) );

                Path newPath = renameService.rename( path, newFileName, commitMessage );
                result.setPath( newPath );
            } else {

                ioService.write( Paths.convert( path ),
                        result.getSource(),
                        metadataService.setUpAttributes( path, metadata ),
                        serviceHelper.makeCommentedOption( commitMessage ) );
                result.setPath( path );

            }

            return result;
        } catch ( Exception e ) {
            logger.error( "Source file couldn't be updated, path: " + path.toURI() + ", dataObject: " + ( dataObjectTO != null ? dataObjectTO.getClassName() : null ) + ".", e );
            throw new ServiceException( "Source file couldn't be updated, path: " + path.toURI() + ", dataObject: " + ( dataObjectTO != null ? dataObjectTO.getClassName() : null ) + ".", e );
        } finally {
            if ( onBatch ) ioService.endBatch();
        }
    }

    private GenerationResult resolveSaveSource( final String source,
                                                final Path path,
                                                final DataObjectTO dataObjectTO ) {
        GenerationResult result = new GenerationResult();
        KieProject project;
        String updatedSource;

        try {

            project = projectService.resolveProject( path );
            if ( project == null ) {
                logger.warn( "File : " + path.toURI() + " do not belong to a valid project" );
                result.setSource( source );
                return result;
            }

            if ( dataObjectTO != null ) {
                //the source needs to be updated with the DataObject definition prior to save
                result = updateSource( source, path, dataObjectTO );
                updatedSource = result.getSource();
            } else {
                //if the dataObjectTO wasn't provided the source is already prepared to be saved and likely
                //it's not parsed at the ui. So we will save the provided source and try to parse the data object
                updatedSource = source;
            }

            if ( dataObjectTO == null ) {
                ClassLoader classLoader = getProjectClassLoader( project );
                JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver( ioService, Paths.convert( path ), false, classLoader );
                ModelDriverResult driverResult = modelDriver.loadDataObject( source, Paths.convert( path ) );

                if ( driverResult.hasErrors() ) {
                    result.setErrors( serviceHelper.toDataModelerError( driverResult.getErrors() ) );
                } else {
                    if ( driverResult.getDataModel().getDataObjects().size() > 0 ) {
                        DataModelTO dataModelTO = serviceHelper.domain2To( driverResult.getDataModel(), driverResult.getClassPaths(), DataModelTO.TOStatus.PERSISTENT, true );
                        result.setDataObject( dataModelTO.getDataObjects().iterator().next() );
                    }
                }
            }

            result.setSource( updatedSource );

            return result;

        } catch ( Exception e ) {
            logger.error( "Source file couldn't be updated, path: " + path.toURI() + ", dataObject: " + ( dataObjectTO != null ? dataObjectTO.getClassName() : null ) + ".", e );
            throw new ServiceException( "Source file couldn't be updated, path: " + path.toURI() + ", dataObject: " + ( dataObjectTO != null ? dataObjectTO.getClassName() : null ) + ".", e );
        }
    }

    public Path copy( final Path path,
                      final String newName,
                      final String comment,
                      final boolean refactor ) {
        Path targetPath = null;
        if ( refactor ) {
            try {
                GenerationResult refactoringResult = refactorClass( path, null, newName );
                if ( !refactoringResult.hasErrors() ) {
                    targetPath = Paths.convert( Paths.convert( path ).resolveSibling( newName + ".java" ) );
                    copyHelper.addRefactoredPath( targetPath, refactoringResult.getSource(), comment );
                    KieProject project = projectService.resolveProject( targetPath );
                    if ( project != null ) {
                        dataObjectCreatedEvent.fire( new DataObjectCreatedEvent( project, refactoringResult.getDataObject() ) );
                    }
                }
            } catch ( Exception e ) {
                //if the refactoring fails for whatever reason the file still needs to be copied.
                logger.error( "An error was produced during class refactoring at file copying for file: " + path + ". The file copying will continue without class refactoring", e );
            }
        }
        try {
            return copyService.copy( path, newName, comment );
        } finally {
            if ( targetPath != null ) {
                copyHelper.removeRefactoredPath( targetPath );
            }
        }
    }

    public Path rename( final Path path,
                        final String newName,
                        String comment,
                        final boolean refactor,
                        final boolean saveCurrentChanges,
                        final String source,
                        final DataObjectTO dataObjectTO,
                        final Metadata metadata ) {

        GenerationResult saveResult = null;
        if ( saveCurrentChanges ) {
            saveResult = resolveSaveSource( source, path, dataObjectTO );
            ioService.write( Paths.convert( path ),
                             saveResult.getSource(),
                             metadataService.setUpAttributes( path, metadata ),
                             serviceHelper.makeCommentedOption( comment ) );
        }

        Path targetPath = null;
        String newContent = null;
        if ( refactor ) {
            String sourceToRefactor;
            if ( saveCurrentChanges ) {
                sourceToRefactor = ( saveResult != null && !saveResult.hasErrors() ) ? saveResult.getSource() : null;
            } else {
                sourceToRefactor = source;
            }

            if ( sourceToRefactor != null ) {
                try {
                    GenerationResult refactoringResult = refactorClass( sourceToRefactor, path, null, newName );
                    if ( !refactoringResult.hasErrors() ) {
                        targetPath = Paths.convert( Paths.convert( path ).resolveSibling( newName + ".java" ) );
                        renameHelper.addRefactoredPath( targetPath, refactoringResult.getSource(), comment );
                        //TODO send data object renamed event.
                        //if (project != null) dataObjectCreatedEvent.fire( new DataObjectCreatedEvent( project, refactoringResult.getDataObject() ) );
                        newContent = refactoringResult.getSource();
                    }
                } catch ( Exception e ) {
                    //if the refactoring fails for whatever reason the file still needs to be renamed.
                    logger.error( "An error was produced during class refactoring at file renaming for file: " + path + ". The file renaming will continue without class refactoring", e );
                }
            }
        }
        try {

            //TODO we need to investigate why we have a DeleteEvent, and a CreateEvent for the case of .java files.
            boolean workaround = true;
            if (!workaround) {
                return renameService.rename( path, newName, comment );
            } else {
                //I will implement the rename here as a workaround
                //remove this workaround when we can find the error.
                return renameWorkaround( path, newName, newContent, comment );
            }

        } finally {
            if ( targetPath != null ) {
                renameHelper.removeRefactoredPath( targetPath );
            }
        }
    }

    public Path renameWorkaround( final Path path,
            final String newName,
            final String newContent,
            final String comment ) {
        try {

            final org.uberfire.java.nio.file.Path _path = Paths.convert( path );

            String originalFileName = _path.getFileName().toString();
            final String extension = originalFileName.substring( originalFileName.lastIndexOf( "." ) );
            final org.uberfire.java.nio.file.Path _target = _path.resolveSibling( newName + extension );
            final Path targetPath = Paths.convert( _target );

            try {

                if ( newContent != null ) {
                    //first overwrite the content with the new content, then we can rename the file.
                    //this is the workaround.
                    ioService.write( _path, newContent, serviceHelper.makeCommentedOption( comment ) );
                }

                ioService.startBatch( new FileSystem[]{_target.getFileSystem()} );

                ioService.move( _path,
                        _target,
                        serviceHelper.makeCommentedOption( "File [" + path.toURI() + "] renamed to [" + targetPath.toURI() + "]." )
                );

            } catch ( final Exception e ) {
                throw e;
            } finally {
                ioService.endBatch();
            }

            return Paths.convert( _target );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public String getSource(Path path) {
        //TODO, remove this method if source service is defined for .java files.
        org.uberfire.java.nio.file.Path convertedPath = Paths.convert(path);
        return ioService.readAllString( convertedPath );
    }

    private void processErrors( KieProject project,
                                ModelDriverResult result ) {
        PublishBatchMessagesEvent publishEvent = new PublishBatchMessagesEvent();
        publishEvent.setCleanExisting( true );
        publishEvent.setUserId( identity != null ? identity.getIdentifier() : null );
        publishEvent.setMessageType( "DataModeler" );

        SystemMessage systemMessage;
        for ( ModelDriverError error : result.getErrors() ) {
            systemMessage = new SystemMessage();
            systemMessage.setMessageType( "DataModeler" );
            systemMessage.setLevel( SystemMessage.Level.ERROR );
            systemMessage.setId( error.getId() );
            systemMessage.setText( error.getMessage() );
            systemMessage.setColumn( error.getColumn() );
            systemMessage.setLine( error.getLine() );
            systemMessage.setPath( Paths.convert( error.getFile() ) );
            publishEvent.getMessagesToPublish().add( systemMessage );
        }

        publishBatchMessagesEvent.fire( publishEvent );
    }

    @Override
    public GenerationResult saveModel( final DataModelTO dataModel,
                                       final KieProject project,
                                       final boolean overwrite,
                                       final String commitMessage ) {

        Long startTime = System.currentTimeMillis();
        boolean onBatch = false;

        try {

            //Start IOService bath processing. IOService batch processing causes a blocking operation on the file system
            //to it must be treated carefully.
            CommentedOption option = serviceHelper.makeCommentedOption( commitMessage );
            ioService.startBatch();
            onBatch = true;

            generateModel( dataModel, project, option );

            onBatch = false;
            ioService.endBatch();

            Long endTime = System.currentTimeMillis();
            if ( logger.isDebugEnabled() ) {
                logger.debug( "Time elapsed when saving " + project.getProjectName() + ": " + ( endTime - startTime ) + " ms" );
            }

            GenerationResult result = new GenerationResult();
            result.setGenerationTime( endTime - startTime );
            result.setObjectFingerPrints( serviceHelper.claculateFingerPrints( dataModel ) );
            return result;

        } catch ( Exception e ) {
            logger.error( "An error was produced during data model generation, dataModel: " + dataModel + ", path: " + project.getRootPath(), e );
            if ( onBatch ) {
                try {
                    logger.warn( "IOService batch method is still on, trying to end batch processing." );
                    ioService.endBatch();
                    logger.warn( "IOService batch method is was successfully finished. The user will still get the exception, but the batch processing was finished." );
                } catch ( Exception ex ) {
                    logger.error( "An error was produced when the IOService.endBatch processing was executed.", ex );
                }
            }
            throw new ServiceException( "Data model: " + dataModel.getParentProjectName() + ", couldn't be generated due to the following error. " + e );
        }
    }

    @Override
    public GenerationResult saveModel( DataModelTO dataModel,
                                       final KieProject project ) {

        return saveModel( dataModel, project, false, DEFAULT_COMMIT_MESSAGE );

    }

    @Override
    public void delete( final Path path,
                        final String comment ) {
        try {
            KieProject project = projectService.resolveProject( path );
            if ( project == null ) {
                logger.warn( "File : " + path.toURI() + " do not belong to a valid project" );
                return;
            }
            deleteService.delete( path, comment );
            String className = calculateClassName( project, path );
            DataObjectTO dataObjectTO = new DataObjectTO( NamingUtils.extractClassName( className ), NamingUtils.extractPackageName( className ), null );
            dataObjectDeletedEvent.fire( new DataObjectDeletedEvent( project, dataObjectTO ) );
        } catch ( final Exception e ) {
            logger.error( "File: " + path.toURI() + " couldn't be deleted due to the following error. ", e );
            throw new ServiceException( "File: " + path.toURI() + " couldn't be deleted due to the following error. " + e.getMessage() );
        }
    }

    @Override
    public GenerationResult refactorClass( final Path path,
                                           final String newPackageName,
                                           final String newClassName ) {
        final String source = ioService.readAllString( Paths.convert( path ) );
        return refactorClass( source, path, newPackageName, newClassName );
    }

    private GenerationResult refactorClass( final String source,
                                            final Path path,
                                            final String newPackageName,
                                            final String newClassName ) {
        Pair<DataObjectTO, List<DataModelerError>> result = loadDataObject( path, source, path );
        if ( ( result.getK2() == null || result.getK2().isEmpty() ) && result.getK1() != null ) {

            final DataObjectTO dataObjectTO = result.getK1();

            if ( newPackageName != null ) {
                dataObjectTO.setPackageName( newPackageName );
            }
            if ( newClassName != null ) {
                dataObjectTO.setName( newClassName );
            }

            return updateSource( source, path, dataObjectTO );
        } else {
            return new GenerationResult( null, null, result.getK2() );
        }
    }

    @Override
    public List<ValidationMessage> validate( final String source,
                                             final Path path,
                                             final DataObjectTO dataObjectTO ) {

        try {
            String validationSource = null;
            List<ValidationMessage> validations = new ArrayList<ValidationMessage>();

            KieProject project = projectService.resolveProject( path );
            if ( project == null ) {
                logger.warn( "File : " + path.toURI() + " do not belong to a valid project" );
                ValidationMessage validationMessage = new ValidationMessage();
                validationMessage.setPath( path );
                validationMessage.setText( "File do no belong to a valid project" );
                validationMessage.setLevel( ValidationMessage.Level.ERROR );
                validations.add( new ValidationMessage() );
                return validations;
            }

            if ( dataObjectTO != null ) {
                //the source needs to be updated with the DataObject definition prior to validation calculation.
                //we must to the same processing as if the file was about to be saved.
                GenerationResult result = updateSource( source, path, dataObjectTO );
                if ( !result.hasErrors() ) {
                    validationSource = result.getSource();
                } else {
                    //it was not possible to update the source with the data object definition.
                    return serviceHelper.toValidationMessage( result.getErrors() );
                }
            } else {
                validationSource = source;
            }

            return genericValidator.validate( path,
                                              new ByteArrayInputStream( validationSource != null ? validationSource.getBytes( Charsets.UTF_8 ) : "".getBytes() ),
                                              new JavaFileFilter() );

        } catch ( Exception e ) {
            logger.error( "An error was produced during validation", e );
            throw new ServiceException( "An error was produced during validation", e );
        }
    }

    private void generateModel( DataModelTO dataModelTO,
                                KieProject project,
                                CommentedOption option ) throws Exception {

        org.uberfire.java.nio.file.Path sourceFile;
        org.uberfire.java.nio.file.Path targetFile;
        org.uberfire.java.nio.file.Path deletedObjectFile;
        org.uberfire.java.nio.file.Path javaRootPath;
        ClassLoader classLoader;

        String newSource;
        Map<String, String> renames = serviceHelper.calculatePersistentDataObjectRenames( dataModelTO );
        List<String> deletions = serviceHelper.calculatePersistentDataObjectDeletions( dataModelTO );

        classLoader = getProjectClassLoader( project );
        //ensure java sources directory exists.
        Path projectPath = project.getRootPath();
        javaRootPath = ensureProjectJavaPath( Paths.convert( projectPath ) );

        //process deleted objects.
        for ( DataObjectTO dataObjectTO : dataModelTO.getDeletedDataObjects() ) {
            if ( dataObjectTO.isPersistent() ) {
                deletedObjectFile = calculateFilePath( dataObjectTO.getOriginalClassName(), javaRootPath );

                if ( logger.isDebugEnabled() ) {
                    logger.debug( "Data object: " + dataObjectTO.getClassName() + " was deleted in the UI, associated .java file should be deleted." );
                    logger.debug( "current class name is: " + dataObjectTO.getClassName() );
                    logger.debug( "original class name is: " + dataObjectTO.getOriginalClassName() );
                    logger.debug( "file to be deleted is: " + deletedObjectFile );
                }

                ioService.deleteIfExists( deletedObjectFile, option );
            }
        }

        for ( DataObjectTO dataObjectTO : dataModelTO.getDataObjects() ) {

            if ( dataObjectTO.isVolatile() ) {
                //data object created in the UI

                targetFile = calculateFilePath( dataObjectTO.getClassName(), javaRootPath );
                if ( logger.isDebugEnabled() ) {
                    logger.debug( "Data object: " + dataObjectTO.getClassName() + " is a new object created in the UI, java source code will be generated from scratch and written into file: " + targetFile );
                }
                newSource = createJavaSource( serviceHelper.to2Domain( dataObjectTO ) );
                ioService.write( targetFile, newSource, option );

            } else if ( hasUIChanges( dataObjectTO ) ) {
                //data object that was read from a .java file and was modified in the UI.

                if ( logger.isDebugEnabled() ) {
                    logger.debug( "Data object: " + dataObjectTO.getClassName() + " needs to be updated with UI changes." );
                }

                if ( dataObjectTO.classNameChanged() ) {
                    sourceFile = calculateFilePath( dataObjectTO.getOriginalClassName(), javaRootPath );
                    targetFile = calculateFilePath( dataObjectTO.getClassName(), javaRootPath );
                    if ( logger.isDebugEnabled() ) {
                        logger.debug( "Data object was renamed form class name: " + dataObjectTO.getOriginalClassName() + " to: " + dataObjectTO.getClassName() );
                    }

                } else {
                    sourceFile = calculateFilePath( dataObjectTO.getClassName(), javaRootPath );
                    targetFile = sourceFile;
                }

                if ( logger.isDebugEnabled() ) {
                    logger.debug( "original content will be read from file: " + sourceFile );
                    logger.debug( "updated content will be written into file: " + targetFile );
                }

                if ( ioService.exists( sourceFile ) ) {
                    //common case, by construction the file should exist.
                    newSource = updateJavaSource( sourceFile, dataObjectTO, renames, deletions, classLoader ).getK1();
                } else {
                    //uncommon case
                    if ( logger.isDebugEnabled() ) {
                        logger.debug( "original content file: " + sourceFile + ", seems to not exists. Java source code will be generated from scratch." );
                    }
                    newSource = createJavaSource( serviceHelper.to2Domain( dataObjectTO ) );
                }

                ioService.write( targetFile, newSource, option );

                if ( !sourceFile.equals( targetFile ) ) {
                    if ( logger.isDebugEnabled() ) {
                        logger.debug( "original content file: " + sourceFile + " needs to be deleted." );
                    }
                    ioService.deleteIfExists( sourceFile );
                }
            } else {
                logger.debug( "Data object: " + dataObjectTO.getClassName() + " wasn't changed in the UI, NO file update is needed." );
            }
        }
    }

    private Pair<String, List<DataModelerError>> updateJavaSource( String originalSource,
                                                                   DataObjectTO dataObjectTO,
                                                                   Map<String, String> renames,
                                                                   List<String> deletions,
                                                                   ClassLoader classLoader ) throws Exception {

        String newSource;
        ClassTypeResolver classTypeResolver;
        List<DataModelerError> errors = new ArrayList<DataModelerError>();

        if ( logger.isDebugEnabled() ) {
            logger.debug( "Starting java source update for class: " + dataObjectTO.getClassName() );
        }

        if ( logger.isDebugEnabled() ) {
            logger.debug( "original source is: " + originalSource );
        }

        JavaType<?> javaType = Roaster.parse( originalSource );
        if ( javaType.isClass() ) {
            if ( javaType.getSyntaxErrors() != null && !javaType.getSyntaxErrors().isEmpty() ) {
                //if a file has parsing errors it will be skipped.
                errors.addAll( serviceHelper.toDataModelerError( javaType.getSyntaxErrors(), null ) );
                newSource = originalSource;
            } else {
                JavaClassSource javaClassSource = (JavaClassSource) javaType;
                classTypeResolver = DriverUtils.getInstance().createClassTypeResolver( javaClassSource, classLoader );
                updateJavaClassSource( dataObjectTO, javaClassSource, renames, deletions, classTypeResolver );
                newSource = javaClassSource.toString();
            }
        } else {
            logger.debug( "No Class definition was found for source: " + originalSource + ", original source won't be modified." );
            newSource = originalSource;
        }

        if ( logger.isDebugEnabled() ) {
            logger.debug( "updated source is: " + newSource );
        }
        return new Pair<String, List<DataModelerError>>( newSource, errors );
    }

    private Pair<String, List<DataModelerError>> updateJavaSource( org.uberfire.java.nio.file.Path path,
                                                                   DataObjectTO dataObjectTO,
                                                                   Map<String, String> renames,
                                                                   List<String> deletions,
                                                                   ClassLoader classLoader ) throws Exception {

        String originalSource;
        originalSource = ioService.readAllString( path );
        if ( logger.isDebugEnabled() ) {
            logger.debug( "path is: " + path );
        }

        return updateJavaSource( originalSource, dataObjectTO, renames, deletions, classLoader );
    }

    private void updateJavaClassSource( DataObjectTO dataObjectTO,
                                        JavaClassSource javaClassSource,
                                        Map<String, String> renames,
                                        List<String> deletions,
                                        ClassTypeResolver classTypeResolver ) throws Exception {

        if ( javaClassSource == null || !javaClassSource.isClass() ) {
            logger.warn( "A null javaClassSource or javaClassSouce is not a Class, no processing will be done. javaClassSource: " + javaClassSource + " className: " + ( javaClassSource != null ? javaClassSource.getName() : null ) );
            return;
        }

        Map<String, FieldSource<JavaClassSource>> currentClassFields = new HashMap<String, FieldSource<JavaClassSource>>();
        List<FieldSource<JavaClassSource>> classFields = javaClassSource.getFields();
        Map<String, String> preservedFields = new HashMap<String, String>();
        ObjectProperty property;
        DataObject dataObject = serviceHelper.to2Domain( dataObjectTO );
        JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver();

        //update package, class name, and super class name if needed.
        modelDriver.updatePackage( javaClassSource, dataObjectTO.getPackageName() );
        modelDriver.updateImports( javaClassSource, renames, deletions );
        modelDriver.updateAnnotations( javaClassSource, dataObject.getAnnotations(), classTypeResolver );
        modelDriver.updateClassName( javaClassSource, dataObjectTO.getName() );
        modelDriver.updateSuperClassName( javaClassSource, dataObjectTO.getSuperClassName(), classTypeResolver );

        if ( classFields != null ) {
            for ( FieldSource<JavaClassSource> field : classFields ) {
                currentClassFields.put( field.getName(), field );
            }
        }

        //create new fields and update existing.
        for ( ObjectPropertyTO propertyTO : dataObjectTO.getProperties() ) {

            property = serviceHelper.to2Domain( propertyTO );

            if ( property.isFinal() || property.isStatic() ) {
                preservedFields.put( property.getName(), property.getName() );
                continue;
            }

            if ( propertyTO.isVolatile() ) {
                //uncommon case
                if ( currentClassFields.containsKey( propertyTO.getName() ) ) {
                    modelDriver.removeField( javaClassSource, propertyTO.getName(), classTypeResolver );
                }
                modelDriver.createField( javaClassSource, property, classTypeResolver );
                preservedFields.put( property.getName(), property.getName() );
            } else {
                if ( propertyTO.nameChanged() ) {
                    if ( currentClassFields.containsKey( propertyTO.getOriginalName() ) ) {
                        modelDriver.updateField( javaClassSource, propertyTO.getOriginalName(), property, classTypeResolver );
                        preservedFields.put( propertyTO.getName(), propertyTO.getName() );
                    } else {
                        if ( currentClassFields.containsKey( propertyTO.getName() ) ) {
                            modelDriver.removeField( javaClassSource, propertyTO.getName(), classTypeResolver );
                        }
                        modelDriver.createField( javaClassSource, property, classTypeResolver );
                        preservedFields.put( property.getName(), property.getName() );
                    }
                } else {
                    if ( currentClassFields.containsKey( propertyTO.getName() ) ) {
                        modelDriver.updateField( javaClassSource, propertyTO.getName(), property, classTypeResolver );
                    } else {
                        modelDriver.createField( javaClassSource, property, classTypeResolver );
                    }
                    preservedFields.put( property.getName(), property.getName() );
                }
            }
        }

        //update constructors, equals and hashCode methods.
        modelDriver.updateConstructors( javaClassSource, dataObject );

        //delete fields from .java file that not exists in the DataObject.
        List<String> removableFields = new ArrayList<String>();
        for ( FieldSource<JavaClassSource> field : currentClassFields.values() ) {
            if ( !preservedFields.containsKey( field.getName() ) &&
                    modelDriver.isManagedField( field, classTypeResolver ) ) {
                removableFields.add( field.getName() );
            }
        }
        for ( String fieldName : removableFields ) {
            modelDriver.removeField( javaClassSource, fieldName, classTypeResolver );
        }
    }

    private String createJavaSource( DataObject dataObject ) throws Exception {

        GenerationContext generationContext = new GenerationContext( null );
        String source;
        GenerationEngine engine;

        try {
            engine = GenerationEngine.getInstance();
            source = engine.generateJavaClassString( generationContext, dataObject );
        } catch ( Exception e ) {
            logger.error( "Java source for dataObject: " + dataObject.getClassName() + " couldn't be created.", e );
            throw e;
        }
        return source;
    }

    private ClassLoader getProjectClassLoader( KieProject project ) {

        final KieModule module = builderCache.assertBuilder( project ).getKieModuleIgnoringErrors();
        final ClassLoader classLoader = KieModuleMetaData.Factory.newKieModuleMetaData( module ).getClassLoader();
        return classLoader;
    }

    public Boolean verifiesHash( Path javaFile ) {
        if ( javaFile == null ) {
            return false;
        }
        org.uberfire.java.nio.file.Path filePath = Paths.convert( javaFile );
        String content;
        String expectedHashValue;

        content = ioService.readAllString( filePath );
        content = content != null ? content.trim() : null;

        if ( content == null ) {
            return false;
        }

        expectedHashValue = FileHashingUtils.extractFileHashValue( content );
        if ( expectedHashValue != null ) {
            return FileHashingUtils.verifiesHash( content, expectedHashValue );
        }
        return false;
    }

    @Override
    public List<Path> findClassUsages( String className ) {

        HashSet<ValueIndexTerm> queryTerms = new HashSet<ValueIndexTerm>();
        queryTerms.add( new ValueTypeIndexTerm( className ) );
        return executeReferencesQuery( "FindTypesQuery", queryTerms );
    }

    @Override
    public List<Path> findFieldUsages( String className,
                                       String fieldName ) {

        HashSet<ValueIndexTerm> queryTerms = new HashSet<ValueIndexTerm>();
        queryTerms.add( new ValueTypeIndexTerm( className ) );
        queryTerms.add( new ValueFieldIndexTerm( fieldName ) );
        return executeReferencesQuery( "FindTypeFieldsQuery", queryTerms );
    }

    private List<Path> executeReferencesQuery( String queryName,
                                               HashSet<ValueIndexTerm> queryTerms ) {

        List<Path> results = new ArrayList<Path>();
        final RefactoringPageRequest request = new RefactoringPageRequest( queryName,
                                                                           queryTerms,
                                                                           0,
                                                                           100 );

        try {

            final PageResponse<RefactoringPageRow> response = queryService.query( request );
            if ( response != null && response.getPageRowList() != null ) {
                for ( RefactoringPageRow row : response.getPageRowList() ) {
                    results.add( (org.uberfire.backend.vfs.Path) row.getValue() );
                }
            }
            return results;

        } catch ( Exception e ) {
            logger.error( "References query: " + queryName + ", couldn't be executed: " + e.getMessage(), e );
            throw new ServiceException( "References query: " + queryName + ", couldn't be executed: " + e.getMessage(), e );
        }
    }

    @Override
    public List<PropertyTypeTO> getBasePropertyTypes() {
        List<PropertyTypeTO> types = new ArrayList<PropertyTypeTO>();

        for ( PropertyType baseType : PropertyTypeFactoryImpl.getInstance().getBasePropertyTypes() ) {
            types.add( new PropertyTypeTO( baseType.getName(), baseType.getClassName(), baseType.isPrimitive() ) );
        }
        return types;
    }

    @Override
    public Map<String, AnnotationDefinitionTO> getAnnotationDefinitions() {
        Map<String, AnnotationDefinitionTO> annotations = new HashMap<String, AnnotationDefinitionTO>();
        List<AnnotationDefinition> annotationDefinitions = DataModelOracleModelDriver.getInstance().getConfiguredAnnotations();
        AnnotationDefinitionTO annotationDefinitionTO;

        for ( AnnotationDefinition annotationDefinition : annotationDefinitions ) {
            annotationDefinitionTO = serviceHelper.domain2To( annotationDefinition );
            annotations.put( annotationDefinitionTO.getClassName(), annotationDefinitionTO );
        }
        return annotations;
    }

    @Override
    public Boolean exists( Path path ) {
        return ioService.exists( Paths.convert( path ) );
    }

    @Override
    public Set<Package> resolvePackages( final Path path ) {
        Project project = null;
        if ( path != null ) {
            project = projectService.resolveProject( path );
        }
        if ( path == null || project == null ) {
            return new HashSet<Package>( );
        } else {
            return projectService.resolvePackages( project );
        }
    }

    private boolean hasUIChanges( DataObjectTO dataObjectTO ) {
        String newFingerPrint = serviceHelper.calculateFingerPrint( dataObjectTO.getStringId() );
        boolean result = !newFingerPrint.equals( dataObjectTO.getFingerPrint() );
        if ( !result ) {
            logger.debug( "The class : " + dataObjectTO.getClassName() + " wasn't modified" );
        }
        return result;
    }

    private void cleanupEmptyDirs( org.uberfire.java.nio.file.Path pojectPath ) {
        FileUtils fileUtils = FileUtils.getInstance();
        List<String> deleteableFiles = new ArrayList<String>();
        deleteableFiles.add( ".gitignore" );
        fileUtils.cleanEmptyDirectories( ioService, pojectPath, false, deleteableFiles );
    }

    private org.uberfire.java.nio.file.Path existsProjectJavaPath( org.uberfire.java.nio.file.Path projectPath ) {
        org.uberfire.java.nio.file.Path javaPath = projectPath.resolve( "src" ).resolve( "main" ).resolve( "java" );
        if ( ioService.exists( javaPath ) ) {
            return javaPath;
        }
        return null;
    }

    private org.uberfire.java.nio.file.Path ensureProjectJavaPath( org.uberfire.java.nio.file.Path projectPath ) {
        org.uberfire.java.nio.file.Path javaPath = projectPath.resolve( "src" );
        if ( !ioService.exists( javaPath ) ) {
            javaPath = ioService.createDirectory( javaPath );
        }
        javaPath = javaPath.resolve( "main" );
        if ( !ioService.exists( javaPath ) ) {
            javaPath = ioService.createDirectory( javaPath );
        }
        javaPath = javaPath.resolve( "java" );
        if ( !ioService.exists( javaPath ) ) {
            javaPath = ioService.createDirectory( javaPath );
        }

        return javaPath;
    }

    private String calculateClassName( Project project,
                                       Path path ) {

        Path rootPath = project.getRootPath();
        if ( !path.toURI().startsWith( rootPath.toURI() ) ) {
            return null;
        }

        Package defaultPackage = projectService.resolveDefaultPackage( project );
        Path srcPath = null;

        if ( path.toURI().startsWith( defaultPackage.getPackageMainSrcPath().toURI() ) ) {
            srcPath = defaultPackage.getPackageMainSrcPath();
        } else if ( path.toURI().startsWith( defaultPackage.getPackageTestSrcPath().toURI() ) ) {
            srcPath = defaultPackage.getPackageTestSrcPath();
        }

        //project: default://master@uf-playground/mortgages/main/src/Pojo.java
        if ( srcPath == null ) {
            return null;
        }

        String strPath = path.toURI().substring( srcPath.toURI().length() + 1, path.toURI().length() );
        strPath = strPath.replace( "/", "." );
        strPath = strPath.substring( 0, strPath.indexOf( ".java" ) );

        return strPath;
    }

    /**
     * Given a className calculates the path to the java file allocating the corresponding pojo.
     */
    private org.uberfire.java.nio.file.Path calculateFilePath( String className,
                                                               org.uberfire.java.nio.file.Path javaPath ) {

        String name = NamingUtils.extractClassName( className );
        String packageName = NamingUtils.extractPackageName( className );
        org.uberfire.java.nio.file.Path filePath = javaPath;

        if ( packageName != null ) {
            List<String> packageNameTokens = NamingUtils.tokenizePackageName( packageName );
            for ( String token : packageNameTokens ) {
                filePath = filePath.resolve( token );
            }
        }

        filePath = filePath.resolve( name + ".java" );
        return filePath;
    }

    //TODO refactor this two methods to other class
    private String updateJavaSourceAntlr( DataObjectTO dataObjectTO,
                                          org.uberfire.java.nio.file.Path path,
                                          ClassLoader classLoader ) throws Exception {

        String originalSource;
        String newSource;
        JavaFileHandler fileHandler;
        ClassTypeResolver classTypeResolver;

        if ( logger.isDebugEnabled() ) {
            logger.debug( "Starting java source update for class: " + dataObjectTO.getClassName() + ", and path: " + path );
        }
        originalSource = ioService.readAllString( path );

        if ( logger.isDebugEnabled() ) {
            logger.debug( "original source is: " + originalSource );
        }

        fileHandler = JavaFileHandlerFactory.getInstance().newHandler( originalSource );
        classTypeResolver = DriverUtils.getInstance().createClassTypeResolver( fileHandler.getFileDescr(), classLoader );
        updateJavaFileDescrAntlr( dataObjectTO, fileHandler.getFileDescr(), classTypeResolver );
        newSource = fileHandler.buildResult();

        if ( logger.isDebugEnabled() ) {
            logger.debug( "updated source is: " + newSource );
        }
        return newSource;
    }

    private void updateJavaFileDescrAntlr( DataObjectTO dataObjectTO,
                                           FileDescr fileDescr,
                                           ClassTypeResolver classTypeResolver ) throws Exception {

        if ( fileDescr == null || fileDescr.getClassDescr() == null ) {
            logger.warn( "A null FileDescr or ClassDescr was provided, no processing will be done. fileDescr: " + fileDescr + " classDescr: " + ( fileDescr != null ? fileDescr.getClassDescr() : null ) );
            return;
        }

        ClassDescr classDescr = fileDescr.getClassDescr();
        Map<String, FieldDescr> currentFields = new HashMap<String, FieldDescr>();
        Map<String, String> preservedFields = new HashMap<String, String>();
        ObjectProperty property;
        DataObject dataObject = serviceHelper.to2Domain( dataObjectTO );
        JavaModelDriver modelDriver = new JavaModelDriver();

        //update package, class name, and super class name if needed.
        modelDriver.updatePackage( fileDescr, dataObjectTO.getPackageName() );
        modelDriver.updateClassOrFieldAnnotations( fileDescr, classDescr, dataObject.getAnnotations(), classTypeResolver );
        modelDriver.updateClassName( classDescr, dataObjectTO.getName() );
        modelDriver.updateSuperClassName( classDescr, dataObjectTO.getSuperClassName(), classTypeResolver );

        //create new fields and update existing.
        for ( FieldDescr fieldDescr : classDescr.getFields() ) {
            for ( VariableDeclarationDescr variableDescr : fieldDescr.getVariableDeclarations() ) {
                currentFields.put( variableDescr.getIdentifier().getIdentifier(), fieldDescr );
            }
        }

        for ( ObjectPropertyTO propertyTO : dataObjectTO.getProperties() ) {

            property = serviceHelper.to2Domain( propertyTO );

            if ( propertyTO.isVolatile() ) {
                if ( currentFields.containsKey( propertyTO.getName() ) ) {
                    modelDriver.removeField( classDescr, propertyTO.getName() );
                }
                modelDriver.createField( classDescr, property );
                preservedFields.put( property.getName(), property.getName() );
            } else {
                if ( propertyTO.nameChanged() ) {
                    if ( currentFields.containsKey( propertyTO.getOriginalName() ) ) {
                        modelDriver.updateField( classDescr, propertyTO.getOriginalName(), property, classTypeResolver );
                        preservedFields.put( propertyTO.getName(), propertyTO.getName() );
                    } else {
                        if ( currentFields.containsKey( propertyTO.getName() ) ) {
                            modelDriver.removeField( classDescr, propertyTO.getName() );
                        }
                        modelDriver.createField( classDescr, property );
                        preservedFields.put( property.getName(), property.getName() );
                    }
                } else {
                    if ( currentFields.containsKey( propertyTO.getName() ) ) {
                        modelDriver.updateField( classDescr, propertyTO.getName(), property, classTypeResolver );
                    } else {
                        modelDriver.createField( classDescr, property );
                    }
                    preservedFields.put( property.getName(), property.getName() );
                }
            }
        }

        //update constructors, equals and hashCode methods.
        modelDriver.updateConstructors( classDescr, dataObject );

        //delete fields from .java file that not exists in the DataObject.
        List<String> removableFields = new ArrayList<String>();
        for ( FieldDescr fieldDescr : classDescr.getFields() ) {
            for ( VariableDeclarationDescr variableDescr : fieldDescr.getVariableDeclarations() ) {
                if ( !preservedFields.containsKey( variableDescr.getIdentifier().getIdentifier() ) &&
                        variableDescr.getDimensionsCount() == 0 &&
                        modelDriver.isManagedField( fieldDescr, classTypeResolver ) ) {
                    removableFields.add( variableDescr.getIdentifier().getIdentifier() );
                }
            }
        }
        for ( String fieldName : removableFields ) {
            modelDriver.removeField( classDescr, fieldName );
        }
    }

}
