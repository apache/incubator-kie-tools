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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.GenerationResult;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.datamodeller.service.ServiceException;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationTools;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.datamodeller.core.impl.PropertyTypeFactoryImpl;
import org.kie.workbench.common.services.datamodeller.driver.FileChangeDescriptor;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.impl.DataModelOracleDriver;
import org.kie.workbench.common.services.datamodeller.driver.impl.JavaModelDriver;
import org.kie.workbench.common.services.datamodeller.parser.JavaFileHandler;
import org.kie.workbench.common.services.datamodeller.parser.JavaFileHandlerFactory;
import org.kie.workbench.common.services.datamodeller.parser.JavaFileHandlerImpl;
import org.kie.workbench.common.services.datamodeller.parser.descr.ClassDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.DescriptorFactory;
import org.kie.workbench.common.services.datamodeller.parser.descr.DescriptorFactoryImpl;
import org.kie.workbench.common.services.datamodeller.parser.descr.FieldDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FileDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.IdentifierDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.VariableDeclarationDescr;
import org.kie.workbench.common.services.datamodeller.util.FileHashingUtils;
import org.kie.workbench.common.services.datamodeller.util.FileUtils;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ResourceAdded;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceDeleted;
import org.uberfire.workbench.events.ResourceUpdated;

@Service
@ApplicationScoped
public class DataModelerServiceImpl implements DataModelerService {

    private static final Logger logger = LoggerFactory.getLogger( DataModelerServiceImpl.class );

    private static final boolean checkExternalModifications = !"false".equals( System.getProperty( "org.kie.workbench.datamodeller.checkExternalModifications" ) );

    private static final boolean checkExternalModificationsForOldVersions = !"false".equals( System.getProperty( "org.kie.workbench.datamodeller.checkExternalModificationsForOldVersions" ) );

    @Inject
    @Named("ioStrategy")
    IOService ioService;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private Identity identity;

    @Inject
    private DataModelService dataModelService;

    @Inject
    ProjectResourceDriverListener generationListener;

    @Inject
    private Event<ResourceBatchChangesEvent> resourceBatchChangesEvent;

    @Inject
    private ProjectService projectService;

    @Inject
    private POMService pomService;

    @Inject
    private MetadataService metadataService;

    private static final String DEFAULT_COMMIT_MESSAGE = "Data modeller generated action.";

    private static final String DEFAULT_COMMIT_MESSAGE_OLD_VERSIONS = "Data modeller generated action.";

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
        Package defaultPackage = null;

        try {
            projectPath = project.getRootPath();
            defaultPackage = projectService.resolveDefaultPackage(project);
            if ( logger.isDebugEnabled() ) {
                logger.debug( "Current project path is: " + projectPath );
            }

            ProjectDataModelOracle projectDataModelOracle = dataModelService.getProjectDataModel( projectPath );

            DataModelOracleDriver driver = DataModelOracleDriver.getInstance( projectDataModelOracle, getProjectClassLoader(project) );
            //dataModel = driver.loadModel();
            ModelDriver modelDriver = new JavaModelDriver( ioService, Paths.convert( defaultPackage.getPackageMainSrcPath() ) , true, getProjectClassLoader(project) );
            dataModel = modelDriver.loadModel();

            //Objects read from persistent .java format are tagged as PERSISTENT objects
            DataModelTO dataModelTO = DataModelerServiceHelper.getInstance().domain2To( dataModel, DataModelTO.TOStatus.PERSISTENT, true );
            //if (checkExternalModifications) calculateReadonlyStatus( dataModelTO, Paths.convert( defaultPackage.getPackageMainSrcPath() ) );

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

    private ClassLoader getProjectClassLoader(Project project) {
        if (project == null || project.getPomXMLPath() == null) {
            logger.warn("project: " + project + " or pomXMLPath: " + project.getPomXMLPath() + " is null." );return null;
        }
        POM pom = pomService.load(project.getPomXMLPath());
        if (pom == null) {
            logger.warn("Pom couldn't be read for project: " + project + " pomXmlPath: " + project.getPomXMLPath());
            return null;
        }

        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.newKieContainer(kieServices.newReleaseId(pom.getGav().getGroupId(), pom.getGav().getArtifactId(), pom.getGav().getVersion()));
        return kieContainer.getClassLoader();
    }

    @Override
    public GenerationResult saveModel( DataModelTO dataModel,
                                       final Project project ) {

        return saveModel( dataModel, project, false );

    }

    @Override
    public GenerationResult saveModel( DataModelTO dataModel,
            final Project project,
            final boolean overwrite ) {

        Path projectPath = project.getRootPath();
        Long startTime = System.currentTimeMillis();
        boolean onBatch = false;

        try {
            //ensure java sources directory exists.
            org.uberfire.java.nio.file.Path javaPath = ensureProjectJavaPath( Paths.convert( projectPath ) );

            //Start IOService bath processing. IOService batch processing causes a blocking operation on the file system
            //to it must be treated carefully.
            CommentedOption option = makeCommentedOption( );
            ioService.startBatch();
            onBatch = true;

            generateModel( dataModel, javaPath, option );

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


    private Map<String, String> calculateRenamedObjects(DataModelTO dataModelTO) {
        Map<String, String> renames = new HashMap<String, String>(  );
        for (DataObjectTO dataObjectTO : dataModelTO.getDataObjects()) {
            if (dataObjectTO.classNameChanged()) {
                renames.put( dataObjectTO.getClassName(), dataObjectTO.getOriginalClassName() );
            }
        }
        return renames;
    }

    private Map<String, Map<String, String>> calculateRenamedFields(DataModelTO dataModelTO) {
        Map<String, Map<String, String>> renames = new HashMap<String, Map<String, String>>(  );
        Map<String, String> fieldRenames;
        for (DataObjectTO dataObjectTO : dataModelTO.getDataObjects()) {
            for ( ObjectPropertyTO propertyTO : dataObjectTO.getProperties() ) {
                if (propertyTO.nameChanged()) {
                    if ( (fieldRenames = renames.get( dataObjectTO.getClassName() )) == null)  {
                        fieldRenames = new HashMap<String, String>(  );
                        renames.put( dataObjectTO.getClassName(), fieldRenames );
                    }
                    fieldRenames.put( propertyTO.getName(), propertyTO.getOriginalName() );
                }
            }
        }
        return renames;
    }

    private void generateModel ( DataModelTO dataModelTO,
            org.uberfire.java.nio.file.Path javaRootPath,
            CommentedOption option ) throws Exception {

        org.uberfire.java.nio.file.Path sourceFile;
        org.uberfire.java.nio.file.Path targetFile;
        org.uberfire.java.nio.file.Path deletedObjectFile;
        String newSource;
        String originalSource;

        //process deleted objects.
        for ( DataObjectTO dataObjectTO : dataModelTO.getDeletedDataObjects() ) {
            if ( dataObjectTO.isPersistent() ) {
                deletedObjectFile = calculateFilePath( dataObjectTO.getOriginalClassName(), javaRootPath );

                if (logger.isDebugEnabled()) {
                    logger.debug( "Data object: " + dataObjectTO.getClassName() + " was deleted in the UI, associated .java file should be deleted.");
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
                if (logger.isDebugEnabled()) logger.debug( "Data object: " + dataObjectTO.getClassName() + " is a new object created in the UI, java source code will be generated from scratch and written into file: " + targetFile );
                newSource = generateJavaSource( dataObjectTO );
                ioService.write( targetFile, newSource, option );

            } else if ( hasUIChanges( dataObjectTO ) ) {
                //data object that was read from a .java file and was modified in the UI.

                if (logger.isDebugEnabled()) logger.debug( "Data object: " + dataObjectTO.getClassName() + " needs to be updated with UI changes." );

                if (dataObjectTO.classNameChanged()) {
                    sourceFile = calculateFilePath( dataObjectTO.getOriginalClassName(), javaRootPath );
                    targetFile = calculateFilePath( dataObjectTO.getClassName(), javaRootPath );
                    if (logger.isDebugEnabled()) logger.debug( "Data object was renamed form class name: " + dataObjectTO.getOriginalClassName() + " to: " + dataObjectTO.getClassName() );

                } else {
                    sourceFile = calculateFilePath( dataObjectTO.getClassName(), javaRootPath );
                    targetFile = sourceFile;
                }

                if (logger.isDebugEnabled()) {
                    logger.debug( "original content will be read from file: " + sourceFile );
                    logger.debug( "updated content will be written into file: " + targetFile );
                }

                if (ioService.exists( sourceFile )) {
                    //common case, by construction the file should exist.
                    //originalSource = ioService.readAllString( sourceFile );
                    newSource = updateJavaSource( dataObjectTO, sourceFile );
                } else {
                    //uncommon case
                    if (logger.isDebugEnabled()) logger.debug( "original content file: " + sourceFile + ", seems to not exists. Java source code will be generated from scratch.");
                    newSource = generateJavaSource( dataObjectTO );
                }

                ioService.write( targetFile, newSource, option );

                if (!sourceFile.equals( targetFile )) {
                    if (logger.isDebugEnabled()) logger.debug( "original content file: " + sourceFile + " needs to be deleted." );
                    ioService.deleteIfExists( sourceFile );
                }
            } else {
                logger.debug( "Data object: " + dataObjectTO.getClassName() + " wasn't changed in the UI, NO file update is needed." );
            }
        }

    }

    private String updateJavaSource( DataObjectTO dataObjectTO, org.uberfire.java.nio.file.Path path ) throws Exception {

        String originalSource;
        String newSource;
        JavaFileHandler fileHandler;

        if (logger.isDebugEnabled()) logger.debug( "Starting java source update for class: " + dataObjectTO.getClassName() + ", and path: " + path );
        originalSource = ioService.readAllString( path );

        if (logger.isDebugEnabled()) logger.debug( "original source is: " + originalSource );

        fileHandler = JavaFileHandlerFactory.getInstance().newHandler( originalSource );
        updateJavaFileDescr(dataObjectTO, fileHandler.getFileDescr());
        newSource = fileHandler.buildResult();

        if (logger.isDebugEnabled()) logger.debug( "updated source is: " + newSource );
        return newSource;
    }

    private String generateJavaSource(DataObjectTO dataObject) {
        return "the new class";
    }

    private void updateJavaFileDescr(DataObjectTO dataObjectTO, FileDescr fileDescr) throws Exception {


        if (fileDescr == null) {
            logger.warn( "A null FileDescr was provided, no processing will be done." );
            return;
        }

        if (fileDescr.getClassDescr() == null) {
            logger.warn( "ClassDescr is null, no processing will be done." );
            return;
        }

        ClassDescr classDescr = fileDescr.getClassDescr();
        Map<String, FieldDescr> currentFields = new HashMap<String, FieldDescr>( );
        Map<String, String> preservedFields = new HashMap<String, String>( );
        DataModelerServiceHelper helper = DataModelerServiceHelper.getInstance();
        ObjectProperty property;

        for (FieldDescr fieldDescr : classDescr.getFields()) {
            for (VariableDeclarationDescr variableDescr : fieldDescr.getVariableDeclarations()) {
                currentFields.put( variableDescr.getIdentifier().getIdentifier(), fieldDescr );
            }
        }

        for (ObjectPropertyTO propertyTO : dataObjectTO.getProperties()) {

            property = helper.to2Domain( propertyTO );

            if (propertyTO.isVolatile()) {
                if (currentFields.containsKey( propertyTO.getName() )) {
                    removeField( classDescr, propertyTO.getName() );
                }
                createField( classDescr, property );
                preservedFields.put( property.getName(), property.getName() );
            } else {
                if (propertyTO.nameChanged()) {
                    if (currentFields.containsKey( propertyTO.getOriginalName() )) {
                        updateField( classDescr, propertyTO.getOriginalName(), property );
                        preservedFields.put( propertyTO.getName(), propertyTO.getName() );
                    } else {
                        if (currentFields.containsKey( propertyTO.getName() )) {
                            removeField( classDescr, propertyTO.getName() );
                        }
                        createField( classDescr, property );
                        preservedFields.put( property.getName(), property.getName() );
                    }
                } else {
                    if (currentFields.containsKey( propertyTO.getName() )) {
                        updateField( classDescr, propertyTO.getName(), property );
                    } else {
                        createField( classDescr, property );
                    }
                    preservedFields.put( property.getName(), property.getName() );
                }
            }

        }

        List<String> removableFields = new ArrayList<String>(  );
        for (FieldDescr fieldDescr : classDescr.getFields()) {
            for (VariableDeclarationDescr variableDescr : fieldDescr.getVariableDeclarations()) {
                if (!preservedFields.containsKey( variableDescr.getIdentifier().getIdentifier() )) {
                    removableFields.add( variableDescr.getIdentifier().getIdentifier() );
                }
            }
        }
        for (String fieldName : removableFields) {
            classDescr.removeField( fieldName );
        }

    }

    private void updateField(ClassDescr classDescr, String fieldName, ObjectProperty property) {

    }

    private void createField(ClassDescr classDescr, ObjectProperty property) throws Exception {

        String fieldSource = "private " + property.getClassName() + " " + property.getName() + ";";
        FieldDescr fieldDescr;

        DescriptorFactory descriptorFactory = DescriptorFactoryImpl.getInstance();
        fieldDescr = descriptorFactory.createFieldDescr( fieldSource );
        classDescr.addField( fieldDescr );

    }


    /**
     * Takes care of field and the corresponding setter/getter removal.
     */
    private void removeField(ClassDescr classDescr, String fieldName) {
        logger.debug( "Removing field: " + fieldName + ", from class: " + classDescr.getIdentifier().getIdentifier() );

        FieldDescr fieldDescr = classDescr.getField( fieldName );
        String fieldType;
        boolean removed = false;
        GenerationTools genTools = new GenerationTools();
        String accessorName;

        if ( fieldDescr != null ) {
            if ( fieldDescr.getType().isClassOrInterfaceType() ) {
                fieldType = fieldDescr.getType().getClassOrInterfaceType().getClassName();
            } else {
                fieldType = fieldDescr.getType().getPrimitiveType().getName();
            }

            removed = classDescr.removeField( fieldName );
            if (removed) logger.debug( "field: " + fieldName + " was removed." );

            accessorName = genTools.toJavaGetter( fieldName, fieldType );
            logger.debug( "Removing getter: " + accessorName + " for field: " + fieldName );
            removed = classDescr.removeMethod( accessorName );
            if (removed) logger.debug( "getter: " + accessorName + " was removed.");
            accessorName = genTools.toJavaSetter( fieldName );
            logger.debug( "Removing setter: " + accessorName + " for field: " + fieldName );
            removed = classDescr.removeMethod( accessorName );
            if (removed) logger.debug( "setter: " + accessorName + " was removed." );

        } else {
            logger.debug( "Field field: " + fieldName + " was not found in class: " + classDescr.getIdentifier().getIdentifier() );
        }
    }

    private boolean isProcessableField(String fieldName) {
        return true;
    }

    public GenerationResult saveModelOld( DataModelTO dataModel,
                                       final Project project,
                                       final boolean overwrite ) {

        Path projectPath = project.getRootPath();
        Long startTime = System.currentTimeMillis();
        boolean onBatch = false;

        try {
            //ensure java sources directory exists.
            org.uberfire.java.nio.file.Path javaPath = ensureProjectJavaPath( Paths.convert( projectPath ) );

            if ( overwrite ) {
                mergeWithExistingModel( dataModel, project );
            }

            //convert to domain model
            DataModel dataModelDomain = DataModelerServiceHelper.getInstance().to2Domain( dataModel );

            if ( !overwrite ) {
                //optimization remove unmodified data objects from the model in order to skip generation for unmodified objects.
                removeUnmodifiedObjects( dataModelDomain, dataModel );
            }

            //calculate the files that needs to be deleted prior to model generation.
            List<Path> deleteableFiles = calculateDeleteableFiles( dataModel, javaPath );

            //Start IOService bath processing. IOService batch processing causes a blocking operation on the file system
            //to it must be treated carefully.
            CommentedOption option = makeCommentedOption( );
            ioService.startBatch();
            onBatch = true;

            //delete removed data objects
            cleanupFiles( deleteableFiles, option );
            javaPath = ensureProjectJavaPath( Paths.convert( projectPath ) );
            DataModelOracleDriver driver = DataModelOracleDriver.getInstance( );

            generationListener.setCurrentProject( project );
            generationListener.setOption( option );
            generationListener.init();

            driver.generateModel( dataModelDomain, generationListener );

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

    public Boolean verifiesHash(Path javaFile) {
        if (javaFile == null) return false;
        org.uberfire.java.nio.file.Path filePath = Paths.convert(javaFile);
        String content;
        String expectedHashValue;

        content = ioService.readAllString(filePath);
        content = content != null ? content.trim() : null;

        if (content == null) return false;

        expectedHashValue = FileHashingUtils.extractFileHashValue(content);
        if (expectedHashValue != null) {
            return FileHashingUtils.verifiesHash(content, expectedHashValue);
        }
        return false;
    }

    private void mergeWithExistingModel( DataModelTO dataModel,
                                         Project project ) {

        Map<String, DataObjectTO> deletedObjects = new HashMap<String, DataObjectTO>();
        Map<String, DataObjectTO> currentObjects = new HashMap<String, DataObjectTO>();
        DataModelTO reloadedModel = loadModel( project );

        for ( DataObjectTO dataObject : dataModel.getDataObjects() ) {
            currentObjects.put( dataObject.getClassName(), dataObject );
        }

        for ( DataObjectTO dataObject : dataModel.getDeletedDataObjects() ) {
            deletedObjects.put( dataObject.getClassName(), dataObject );
        }

        for ( DataObjectTO reloadedDataObject : reloadedModel.getDataObjects() ) {
            if ( !currentObjects.containsKey( reloadedDataObject.getClassName() ) && !deletedObjects.containsKey( reloadedDataObject.getClassName() ) ) {
                dataModel.getDeletedDataObjects().add( reloadedDataObject );
            }
        }
    }

    private CommentedOption makeCommentedOption() {
        final String name = identity.getName();
        final Date when = new Date();
        final String commitMessage = DEFAULT_COMMIT_MESSAGE;

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
            types.add( new PropertyTypeTO( baseType.getName(), baseType.getClassName(), baseType.isPrimitive() ) );
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

    private void notifyFileChanges( final List<Path> deleteableFiles,
                                    final List<FileChangeDescriptor> driverChanges ) {

        final Map<Path, Collection<ResourceChange>> batchChanges = new HashMap<Path, Collection<ResourceChange>>();

        for ( final Path deleteableFile : deleteableFiles ) {
            batchChanges.put( deleteableFile, new ArrayList<ResourceChange>() {{
                add( new ResourceDeleted() );
            }} );
        }

        for ( final FileChangeDescriptor driverChange : driverChanges ) {
            final Path path = Paths.convert( driverChange.getPath() );
            switch ( driverChange.getAction() ) {
                case FileChangeDescriptor.ADD:
                    logger.debug( "Notifying file created: " + driverChange.getPath() );
                    if ( !batchChanges.containsKey( path ) ) {
                        batchChanges.put( path, new ArrayList<ResourceChange>() );
                    }
                    batchChanges.get( path ).add( new ResourceAdded() );
                    break;
                case FileChangeDescriptor.DELETE:
                    logger.debug( "Notifying file deleted: " + driverChange.getPath() );
                    if ( !batchChanges.containsKey( path ) ) {
                        batchChanges.put( path, new ArrayList<ResourceChange>() );
                    }
                    batchChanges.get( path ).add( new ResourceDeleted() );
                    break;
                case FileChangeDescriptor.UPDATE:
                    logger.debug( "Notifying file updated: " + driverChange.getPath() );
                    if ( !batchChanges.containsKey( path ) ) {
                        batchChanges.put( path, new ArrayList<ResourceChange>() );
                    }
                    batchChanges.get( path ).add( new ResourceUpdated() );
                    break;
            }
        }
        if ( batchChanges.size() > 0 ) {
            resourceBatchChangesEvent.fire( new ResourceBatchChangesEvent( batchChanges, sessionInfo ) );
        }
    }


    private void calculateReadonlyStatus( DataModelTO dataModelTO, org.uberfire.java.nio.file.Path javaPath ) {
        for (DataObjectTO dataObjectTO : dataModelTO.getDataObjects()) {
            if (wasModified(dataObjectTO, javaPath)) {
                dataObjectTO.setStatus( DataModelTO.TOStatus.PERSISTENT_EXTERNALLY_MODIFIED);
            }
        }
    }

    private boolean wasModified(DataObjectTO dataObjectTO, org.uberfire.java.nio.file.Path javaPath) {
        org.uberfire.java.nio.file.Path filePath;
        String content;
        String expectedHashValue;

        filePath = calculateFilePath(dataObjectTO.getClassName(), javaPath);
        content = ioService.readAllString(filePath);
        content = content != null ? content.trim() : null;

        if (content == null) return false;

        expectedHashValue = FileHashingUtils.extractFileHashValue(content);
        if (expectedHashValue != null) {
            return !FileHashingUtils.verifiesHash(content, expectedHashValue);
        }

        return checkExternalModificationsForOldVersions ? wasModifiedForOldFiles(dataObjectTO, filePath) : true;
    }

    private boolean wasModifiedForOldFiles(DataObjectTO dataObjectTO, org.uberfire.java.nio.file.Path filePath) {
        Metadata metadata = metadataService.getMetadata(Paths.convert(filePath));
        if (metadata == null) return true;

        //analyse if all the commits has been done with the data modeller
        List<VersionRecord> versions = metadata.getVersion();
        String comment;
        for (VersionRecord version : versions) {
            comment = version.comment();
            comment = comment != null ? comment.trim() : null;
            if (!DEFAULT_COMMIT_MESSAGE_OLD_VERSIONS.equals(comment)) return true;
        }
        return false;
    }

    private List<Path> calculateDeleteableFiles( final DataModelTO dataModel,
                                                 final org.uberfire.java.nio.file.Path javaPath ) {

        List<DataObjectTO> currentObjects = dataModel.getDataObjects();
        List<DataObjectTO> deletedObjects = dataModel.getDeletedDataObjects();
        List<Path> deleteableFiles = new ArrayList<Path>();
        org.uberfire.java.nio.file.Path filePath;

        //process deleted persistent objects.
        for ( DataObjectTO dataObject : deletedObjects ) {
            if ( dataObject.isPersistent() || dataObject.isExternallyModified() ) {
                filePath = calculateFilePath( dataObject.getOriginalClassName(), javaPath );
                if ( dataModel.getDataObjectByClassName( dataObject.getOriginalClassName() ) != null ) {
                    //TODO check if we need to have this level of control or instead we remove this file directly.
                    //very particular case a persistent object was deleted in memory and a new one with the same name
                    //was created. At the end we will have a file update instead of a delete.

                    //do nothing, the file generator will notify that the file changed.
                    //fileChanges.add(new FileChangeDescriptor(Paths.convert(filePath), FileChangeDescriptor.UPDATE));
                } else {
                    deleteableFiles.add( Paths.convert( filePath ) );
                }
            }
        }

        //process package or class name changes for persistent objects.
        for ( DataObjectTO dataObject : currentObjects ) {
            if ( (dataObject.isPersistent() || dataObject.isExternallyModified()) && dataObject.classNameChanged() ) {
                //if the className changes the old file needs to be removed
                filePath = calculateFilePath( dataObject.getOriginalClassName(), javaPath );

                if ( dataModel.getDataObjectByClassName( dataObject.getOriginalClassName() ) != null ) {
                    //TODO check if we need to have this level of control or instead we remove this file directly.
                    //very particular case of change, a persistent object changes the name to the name of another
                    //object. A kind of name swapping...

                    //do nothing, the file generator will notify that the file changed.
                    //fileChanges.add(new FileChangeDescriptor(Paths.convert(filePath), FileChangeDescriptor.UPDATE));
                } else {
                    deleteableFiles.add( Paths.convert( filePath ) );
                }
            }
        }

        return deleteableFiles;
    }

    private boolean hasUIChanges(DataObjectTO dataObjectTO) {
        String newFingerPrint = DataModelerServiceHelper.getInstance().calculateFingerPrint( dataObjectTO.getStringId() );
        boolean result = !newFingerPrint.equals( dataObjectTO.getFingerPrint() );
        if (!result) logger.debug( "The class : " + dataObjectTO.getClassName() + " wasn't modified" );
        return  result;
    }

    private void removeUnmodifiedObjects( final DataModel dataModelDomain,
                                          final DataModelTO dataModelTO ) throws Exception {
        String newFingerPrint;
        for ( DataObjectTO dataObject : dataModelTO.getDataObjects() ) {
            newFingerPrint = DataModelerServiceHelper.getInstance().calculateFingerPrint( dataObject.getStringId() );
            if ( newFingerPrint.equals( dataObject.getFingerPrint() ) ) {
                logger.debug( "The class : " + dataObject.getClassName() + " wasn't modified" );
                dataModelDomain.removeDataObject( dataObject.getClassName() );
            }
        }
    }

    /**
     * Remove objects marked as readonly form the model, they should never be generated or deleted.
     * @param dataModel
     */
    /*
    private void filterReadonlyObjects( final DataModelTO dataModel ) {
        List<DataObjectTO> deletableObjects = new ArrayList<DataObjectTO>();

        for ( DataObjectTO dataObjectTO : dataModel.getDataObjects() ) {
            if ( dataObjectTO.isExternallyModified() ) {
                deletableObjects.add( dataObjectTO );
            }
        }

        for ( DataObjectTO deletableObject : deletableObjects ) {
            dataModel.removeDataObject( deletableObject );
            dataModel.getDeletedDataObjects().remove( deletableObject );
        }
    }
    */

    private void cleanupFiles( final List<Path> deleteableFiles,
                               final CommentedOption option ) {
        for ( Path filePath : deleteableFiles ) {
            ioService.deleteIfExists( Paths.convert( filePath ), option );
        }
    }

    private void cleanupEmptyDirs( org.uberfire.java.nio.file.Path pojectPath ) {
        FileUtils fileUtils = FileUtils.getInstance();
        List<String> deleteableFiles = new ArrayList<String>();
        deleteableFiles.add( ".gitignore" );
        fileUtils.cleanEmptyDirectories( ioService, pojectPath, false, deleteableFiles );
    }

    private org.uberfire.java.nio.file.Path existsProjectJavaPath( org.uberfire.java.nio.file.Path projectPath ) {
        org.uberfire.java.nio.file.Path javaPath = projectPath.resolve( "src" ).resolve( "main" ).resolve("java");
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

    /**
     * Given a className calculates the path to the java file allocating the corresponding pojo.
     */
    private org.uberfire.java.nio.file.Path calculateFilePath( String className,
                                                               org.uberfire.java.nio.file.Path javaPath ) {

        String name = NamingUtils.getInstance().extractClassName( className );
        String packageName = NamingUtils.getInstance().extractPackageName( className );
        org.uberfire.java.nio.file.Path filePath = javaPath;

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