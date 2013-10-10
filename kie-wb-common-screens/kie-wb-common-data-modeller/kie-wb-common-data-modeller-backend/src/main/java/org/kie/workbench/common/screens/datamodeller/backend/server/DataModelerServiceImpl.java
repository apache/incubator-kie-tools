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

import java.util.*;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.GenerationResult;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.datamodeller.service.ServiceException;
import org.kie.workbench.common.services.datamodel.service.DataModelService;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.datamodeller.core.impl.PropertyTypeFactoryImpl;
import org.kie.workbench.common.services.datamodeller.driver.FileChangeDescriptor;
import org.kie.workbench.common.services.datamodeller.driver.impl.DataModelOracleDriver;
import org.kie.workbench.common.services.datamodeller.util.FileUtils;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ChangeType;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;

@Service
@ApplicationScoped
public class DataModelerServiceImpl implements DataModelerService {

    private static final Logger logger = LoggerFactory.getLogger( DataModelerServiceImpl.class );

    private static final String MAIN_JAVA_PATH = "src/main/java";
    private static final String MAIN_RESOURCES_PATH = "src/main/resources";
    private static final String TEST_JAVA_PATH = "src/test/java";
    private static final String TEST_RESOURCES_PATH = "src/test/resources";

    private static final String DEFAULT_GUVNOR_PKG = "defaultpkg";

    @Inject
    @Named("ioStrategy")
    IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private Identity identity;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private Event<InvalidateDMOProjectCacheEvent> invalidateDMOProjectCache;

    @Inject
    private Event<ResourceBatchChangesEvent> resourceBatchChangesEvent;

    public DataModelerServiceImpl() {
    }

    @Override
    public Path createModel( Path context,
                             String fileName ) {

        //TODO remove this method if the model file is no longer created
        return context;
    }

    @Override
    public DataModelTO loadModel( Project project ) {

        if ( logger.isDebugEnabled() ) {
            logger.debug( "Loading data model from path: " + project.getRootPath() );
        }

        Long startTime = System.currentTimeMillis();

        DataModel dataModel = null;
        Path projectPath = null;

        try {
            projectPath = project.getRootPath();
            if ( logger.isDebugEnabled() ) {
                logger.debug( "Current project path is: " + projectPath );
            }

            ProjectDataModelOracle projectDataModelOracle = dataModelService.getProjectDataModel( projectPath );

            DataModelOracleDriver driver = DataModelOracleDriver.getInstance();
            dataModel = driver.loadModel( projectDataModelOracle );

            //Objects read from persistent .java format are tagged as PERSISTENT objects
            DataModelTO dataModelTO = DataModelerServiceHelper.getInstance().domain2To( dataModel, DataObjectTO.PERSISTENT, true );

            Long endTime = System.currentTimeMillis();
            if ( logger.isDebugEnabled() ) {
                logger.debug( "Time elapsed when loading " + projectPath.getFileName() + ": " + ( endTime - startTime ) + " ms" );
            }

            return dataModelTO;

        } catch ( Exception e ) {
            logger.error( "Data model couldn't be loaded, path: " + projectPath + ", projectPath: " + projectPath + ".", e );
            throw new ServiceException( "Data model couldn't be loaded, path: " + projectPath + ", projectPath: " + projectPath + ".", e );
        }
    }


    @Override
    public GenerationResult saveModel( DataModelTO dataModel,
                                       final Project project ) {

        return saveModel(dataModel, project, false);

    }

    @Override
    public GenerationResult saveModel( DataModelTO dataModel,
                                       final Project project,
                                       final boolean overwrite) {

        Path projectPath = project.getRootPath();
        Long startTime = System.currentTimeMillis();
        boolean onBatch = false;

        try {
            //ensure java sources directory exists.
            org.kie.commons.java.nio.file.Path javaPath = ensureProjectJavaPath( paths.convert( projectPath ) );

            if (overwrite) {
                mergeWithExistingModel(dataModel, project);
            }

            //convert to domain model
            DataModel dataModelDomain = DataModelerServiceHelper.getInstance().to2Domain( dataModel );

            if (!overwrite) {
                //optimization remove unmodified data objects from the model in order to skip generation for unmodified objects.
                removeUnmodifiedObjects( dataModelDomain, dataModel );
            }

            //calculate the files that needs to be deleted prior to model generation.
            List<Path> deleteableFiles = calculateDeleteableFiles( dataModel, javaPath );

            //@wmedvede now the InvalidateDMOProjectCacheEvent will be fired in the DataModelResourceChangeObserver
            //invalidate ProjectDataModelOracle for this project.
            //invalidateDMOProjectCache.fire( new InvalidateDMOProjectCacheEvent( projectPath ) );

            //Start IOService bath processing. IOService batch processing causes a blocking operation on the file system
            //to it must be treated carefully.
            CommentedOption option = makeCommentedOption("Data modeller");
            ioService.startBatch();
            onBatch = true;

            //delete removed data objects
            cleanupFiles( deleteableFiles, option );
            javaPath = ensureProjectJavaPath( paths.convert( projectPath ) );
            DataModelOracleDriver driver = DataModelOracleDriver.getInstance();
            driver.generateModel( dataModelDomain, ioService, javaPath , option);

            onBatch = false;
            ioService.endBatch();

            Long endTime = System.currentTimeMillis();
            if ( logger.isDebugEnabled() ) {
                logger.debug( "Time elapsed when saving " + projectPath.getFileName() + ": " + ( endTime - startTime ) + " ms" );
            }

            GenerationResult result = new GenerationResult();
            result.setGenerationTime( endTime - startTime );
            result.setObjectFingerPrints( DataModelerServiceHelper.getInstance().claculateFingerPrints( dataModel ) );
            return result;

        } catch ( Exception e ) {
            logger.error( "An error was produced during data model generation, dataModel: " + dataModel + ", path: " + projectPath, e );
            if (onBatch) {
                try {
                    logger.warn("IOService batch method is still on, trying to end batch processing.");
                    ioService.endBatch();
                    logger.warn("IOService batch method is was successfully finished. The user will still get the exception, but the batch processing was finished.");
                } catch (Exception ex) {
                    logger.error("An error was produced when the IOService.endBatch processing was executed.", ex);
                }
            }
            throw new ServiceException( "Data model: " + dataModel.getParentProjectName() + ", couldn't be generated due to the following error. " + e );
        }
    }

    private void mergeWithExistingModel(DataModelTO dataModel, Project project) {

        Map<String, DataObjectTO> deletedObjects = new HashMap<String, DataObjectTO>();
        Map<String, DataObjectTO> currentObjects = new HashMap<String, DataObjectTO>();
        DataModelTO reloadedModel = loadModel(project);

        for (DataObjectTO dataObject : dataModel.getDataObjects()) {
            currentObjects.put(dataObject.getClassName(), dataObject);
        }

        for (DataObjectTO dataObject : dataModel.getDeletedDataObjects()) {
            deletedObjects.put(dataObject.getClassName(), dataObject);
        }

        for (DataObjectTO reloadedDataObject : reloadedModel.getDataObjects() ) {
            if (!currentObjects.containsKey(reloadedDataObject.getClassName()) && !deletedObjects.containsKey(reloadedDataObject.getClassName())) {
                dataModel.getDeletedDataObjects().add(reloadedDataObject);
            }
        }
    }

    private CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = identity.getName();
        final Date when = new Date();
        final CommentedOption option = new CommentedOption( sessionInfo.getId(),
                name,
                null,
                commitMessage,
                when );
        return option;
    }

    @Override
    public List<PropertyTypeTO> getBasePropertyTypes() {
        List<PropertyTypeTO> types = new ArrayList<PropertyTypeTO>();

        for ( PropertyType baseType : PropertyTypeFactoryImpl.getInstance().getBasePropertyTypes() ) {
            types.add( new PropertyTypeTO( baseType.getName(), baseType.getClassName() ) );
        }
        return types;
    }

    @Override
    public Map<String, AnnotationDefinitionTO> getAnnotationDefinitions() {
        Map<String, AnnotationDefinitionTO> annotations = new HashMap<String, AnnotationDefinitionTO>();
        List<AnnotationDefinition> annotationDefinitions = DataModelOracleDriver.getInstance().getConfiguredAnnotations();
        AnnotationDefinitionTO annotationDefinitionTO;
        DataModelerServiceHelper serviceHelper = DataModelerServiceHelper.getInstance();

        for ( AnnotationDefinition annotationDefinition : annotationDefinitions ) {
            annotationDefinitionTO = serviceHelper.domain2To( annotationDefinition );
            annotations.put( annotationDefinitionTO.getClassName(), annotationDefinitionTO );
        }
        return annotations;
    }

    private void notifyFileChanges( List<Path> deleteableFiles,
                                    List<FileChangeDescriptor> driverChanges ) {

        Set<ResourceChange> batchChanges = new HashSet<ResourceChange>();

        for ( Path deleteableFile : deleteableFiles ) {
            batchChanges.add( new ResourceChange( ChangeType.DELETE, deleteableFile, sessionInfo ) );
        }

        for ( FileChangeDescriptor driverChange : driverChanges ) {
            switch ( driverChange.getAction() ) {
                case FileChangeDescriptor.ADD:
                    logger.debug( "Notifying file created: " + driverChange.getPath() );
                    batchChanges.add( new ResourceChange( ChangeType.ADD, paths.convert( driverChange.getPath() ), sessionInfo ) );
                    break;
                case FileChangeDescriptor.DELETE:
                    logger.debug( "Notifying file deleted: " + driverChange.getPath() );
                    batchChanges.add( new ResourceChange( ChangeType.DELETE, paths.convert( driverChange.getPath() ), sessionInfo ) );
                    break;
                case FileChangeDescriptor.UPDATE:
                    logger.debug( "Notifying file updated: " + driverChange.getPath() );
                    batchChanges.add( new ResourceChange( ChangeType.UPDATE, paths.convert( driverChange.getPath() ), sessionInfo ) );
                    break;
            }
        }
        if ( batchChanges.size() > 0 ) {
            resourceBatchChangesEvent.fire( new ResourceBatchChangesEvent( batchChanges ) );
        }
    }

    private List<Path> calculateDeleteableFiles( DataModelTO dataModel,
                                                 org.kie.commons.java.nio.file.Path javaPath ) {

        List<DataObjectTO> currentObjects = dataModel.getDataObjects();
        List<DataObjectTO> deletedObjects = dataModel.getDeletedDataObjects();
        List<Path> deleteableFiles = new ArrayList<Path>();
        org.kie.commons.java.nio.file.Path filePath;

        //process deleted persistent objects.
        for ( DataObjectTO dataObject : deletedObjects ) {
            if ( dataObject.isPersistent() ) {
                filePath = calculateFilePath( dataObject.getOriginalClassName(), javaPath );
                if ( dataModel.getDataObjectByClassName( dataObject.getOriginalClassName() ) != null ) {
                    //TODO check if we need to have this level of control or instead we remove this file directly.
                    //very particular case a persistent object was deleted in memory and a new one with the same name
                    //was created. At the end we will have a file update instead of a delete.

                    //do nothing, the file generator will notify that the file changed.
                    //fileChanges.add(new FileChangeDescriptor(paths.convert(filePath), FileChangeDescriptor.UPDATE));
                } else {
                    deleteableFiles.add( paths.convert( filePath ) );
                }
            }
        }

        //process package or class name changes for persistent objects.
        for ( DataObjectTO dataObject : currentObjects ) {
            if ( dataObject.isPersistent() && dataObject.classNameChanged() ) {
                //if the className changes the old file needs to be removed
                filePath = calculateFilePath( dataObject.getOriginalClassName(), javaPath );

                if ( dataModel.getDataObjectByClassName( dataObject.getOriginalClassName() ) != null ) {
                    //TODO check if we need to have this level of control or instead we remove this file directly.
                    //very particular case of change, a persistent object changes the name to the name of another
                    //object. A kind of name swapping...

                    //do nothing, the file generator will notify that the file changed.
                    //fileChanges.add(new FileChangeDescriptor(paths.convert(filePath), FileChangeDescriptor.UPDATE));
                } else {
                    deleteableFiles.add( paths.convert( filePath ) );
                }
            }
        }

        return deleteableFiles;
    }

    private void removeUnmodifiedObjects( DataModel dataModelDomain,
                                          DataModelTO dataModelTO ) throws Exception {
        String newFingerPrint;
        for ( DataObjectTO dataObject : dataModelTO.getDataObjects() ) {
            newFingerPrint = DataModelerServiceHelper.getInstance().calculateFingerPrint( dataObject.getStringId() );
            if ( newFingerPrint.equals( dataObject.getFingerPrint() ) ) {
                logger.debug( "XXXXXXXXXXXXXXXXXXX the class : " + dataObject.getClassName() + " wasn't modified" );
                dataModelDomain.removeDataObject( dataObject.getClassName() );
            }
        }
    }

    private void cleanupFiles( List<Path> deleteableFiles, CommentedOption option ) {
        for ( Path filePath : deleteableFiles ) {
            ioService.deleteIfExists( paths.convert( filePath ) , option );
        }
    }

    /**
     * This auxiliary method deletes the files that belongs to data objects that was removed in memory.
     */
    private List<ResourceChange> cleanupFiles( DataModelTO dataModel,
                                               org.kie.commons.java.nio.file.Path javaPath ) {

        List<DataObjectTO> currentObjects = dataModel.getDataObjects();
        List<DataObjectTO> deletedObjects = dataModel.getDeletedDataObjects();
        List<ResourceChange> fileChanges = new ArrayList<ResourceChange>();
        org.kie.commons.java.nio.file.Path filePath;

        //process deleted persistent objects.
        for ( DataObjectTO dataObject : deletedObjects ) {
            if ( dataObject.isPersistent() ) {
                filePath = calculateFilePath( dataObject.getOriginalClassName(), javaPath );
                if ( dataModel.getDataObjectByClassName( dataObject.getOriginalClassName() ) != null ) {
                    //TODO check if we need to have this level of control or instead we remove this file directly.
                    //very particular case a persistent object was deleted in memory and a new one with the same name
                    //was created. At the end we will have a file update instead of a delete.

                    //do nothing, the file generator will notify that the file changed.
                    //fileChanges.add(new FileChangeDescriptor(paths.convert(filePath), FileChangeDescriptor.UPDATE));
                } else {
                    fileChanges.add( new ResourceChange( ChangeType.DELETE, paths.convert( filePath ), sessionInfo ) );
                    ioService.delete( filePath );
                }
            }
        }

        //process package or class name changes for persistent objects.
        for ( DataObjectTO dataObject : currentObjects ) {
            if ( dataObject.isPersistent() && dataObject.classNameChanged() ) {
                //if the className changes the old file needs to be removed
                filePath = calculateFilePath( dataObject.getOriginalClassName(), javaPath );

                if ( dataModel.getDataObjectByClassName( dataObject.getOriginalClassName() ) != null ) {
                    //TODO check if we need to have this level of control or instead we remove this file directly.
                    //very particular case of change, a persistent object changes the name to the name of another
                    //object. A kind of name swapping...

                    //do nothing, the file generator will notify that the file changed.
                    //fileChanges.add(new FileChangeDescriptor(paths.convert(filePath), FileChangeDescriptor.UPDATE));
                } else {
                    fileChanges.add( new ResourceChange( ChangeType.DELETE, paths.convert( filePath ), sessionInfo ) );
                    ioService.delete( filePath );
                }
            }
        }

        return fileChanges;
    }

    private void cleanupEmptyDirs( org.kie.commons.java.nio.file.Path pojectPath ) {
        FileUtils fileUtils = FileUtils.getInstance();
        List<String> deleteableFiles = new ArrayList<String>();
        deleteableFiles.add( ".gitignore" );
        fileUtils.cleanEmptyDirectories( ioService, pojectPath, false, deleteableFiles );
    }

    private org.kie.commons.java.nio.file.Path existsProjectJavaPath( org.kie.commons.java.nio.file.Path projectPath ) {
        org.kie.commons.java.nio.file.Path javaPath = projectPath.resolve( "src" ).resolve( "main" ).resolve( "java" );
        if ( ioService.exists( javaPath ) ) {
            return javaPath;
        }
        return null;
    }

    private org.kie.commons.java.nio.file.Path ensureProjectJavaPath( org.kie.commons.java.nio.file.Path projectPath ) {
        org.kie.commons.java.nio.file.Path javaPath = projectPath.resolve( "src" );
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

    /**
     * Given a className calculates the path to the java file allocating the corresponding pojo.
     */
    private org.kie.commons.java.nio.file.Path calculateFilePath( String className,
                                                                  org.kie.commons.java.nio.file.Path javaPath ) {

        String name = NamingUtils.getInstance().extractClassName( className );
        String packageName = NamingUtils.getInstance().extractPackageName( className );
        org.kie.commons.java.nio.file.Path filePath = javaPath;

        if ( packageName != null ) {
            List<String> packageNameTokens = NamingUtils.getInstance().tokenizePackageName( packageName );
            for ( String token : packageNameTokens ) {
                filePath = filePath.resolve( token );
            }
        }

        filePath = filePath.resolve( name + ".java" );
        return filePath;
    }

}