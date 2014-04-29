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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.core.base.ClassTypeResolver;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.guvnor.common.services.builder.LRUBuilderCache;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.GenerationResult;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.datamodeller.service.ServiceException;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationContext;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationEngine;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;

@Service
@ApplicationScoped
public class DataModelerServiceImpl implements DataModelerService {

    private static final Logger logger = LoggerFactory.getLogger( DataModelerServiceImpl.class );

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
    private LRUBuilderCache builderCache;

    @Inject
    private Event<IncrementalBuildResults> incrementalBuildEvent;

    private static final String DEFAULT_COMMIT_MESSAGE = "Data modeller generated action.";

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

            ClassLoader classLoader = getProjectClassLoader( project );
            //ModelDriver modelDriver = new JavaModelDriver( ioService, Paths.convert( defaultPackage.getPackageMainSrcPath() ) , true, classLoader );

            ModelDriver modelDriver = new JavaRoasterModelDriver( ioService, Paths.convert( defaultPackage.getPackageMainSrcPath() ) , true, classLoader );
            ModelDriverResult result = modelDriver.loadModel();
            dataModel = result.getDataModel();

            if (result.hasErrors()) {
                processErrors(project, result );
            }

            //by now we still use the DMO to calculate project external dependencies.
            ProjectDataModelOracle projectDataModelOracle = dataModelService.getProjectDataModel( projectPath );
            ProjectDataModelOracleUtils.loadExternalDependencies( dataModel, projectDataModelOracle, classLoader );

            //Objects read from persistent .java format are tagged as PERSISTENT objects
            DataModelTO dataModelTO = DataModelerServiceHelper.getInstance().domain2To( dataModel, DataModelTO.TOStatus.PERSISTENT, true );

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

    private void processErrors( Project project, ModelDriverResult result ) {
        IncrementalBuildResults buildResults = new IncrementalBuildResults( );
        BuildMessage buildMessage;
        for ( ModelDriverError error : result.getErrors()) {
            buildMessage = new BuildMessage();
            buildMessage.setId( error.getId() );
            buildMessage.setText( error.getMessage() );
            buildMessage.setColumn( error.getColumn() );
            buildMessage.setLine( error.getLine() );
            buildMessage.setPath( Paths.convert( error.getFile() ) );
            buildResults.addAddedMessage( new BuildMessage() );
        }
        incrementalBuildEvent.fire( buildResults );
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


        Long startTime = System.currentTimeMillis();
        boolean onBatch = false;

        try {

            //Start IOService bath processing. IOService batch processing causes a blocking operation on the file system
            //to it must be treated carefully.
            CommentedOption option = makeCommentedOption( );
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
            result.setObjectFingerPrints( DataModelerServiceHelper.getInstance().claculateFingerPrints( dataModel ) );
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

    private void generateModel ( DataModelTO dataModelTO, Project project, CommentedOption option) throws Exception {

        org.uberfire.java.nio.file.Path sourceFile;
        org.uberfire.java.nio.file.Path targetFile;
        org.uberfire.java.nio.file.Path deletedObjectFile;
        org.uberfire.java.nio.file.Path javaRootPath;
        ClassLoader classLoader;

        String newSource;
        DataModelerServiceHelper helper = DataModelerServiceHelper.getInstance();
        Map<String, String> renames = helper.calculatePersistentDataObjectRenames( dataModelTO );
        List<String> deletions = helper.calculatePersistentDataObjectDeletions( dataModelTO );

        classLoader = getProjectClassLoader( project );
        //ensure java sources directory exists.
        Path projectPath = project.getRootPath();
        javaRootPath = ensureProjectJavaPath( Paths.convert( projectPath ) );

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
                newSource = createJavaSource( helper.to2Domain( dataObjectTO ) );
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
                    newSource = updateJavaSource( dataObjectTO, sourceFile, renames, deletions, classLoader );
                } else {
                    //uncommon case
                    if (logger.isDebugEnabled()) logger.debug( "original content file: " + sourceFile + ", seems to not exists. Java source code will be generated from scratch.");
                    newSource = createJavaSource( helper.to2Domain( dataObjectTO ) );
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

    private String updateJavaSource( DataObjectTO dataObjectTO, org.uberfire.java.nio.file.Path path, Map<String, String> renames, List<String> deletions, ClassLoader classLoader ) throws Exception {

        String originalSource;
        String newSource;
        ClassTypeResolver classTypeResolver;

        if (logger.isDebugEnabled()) logger.debug( "Starting java source update for class: " + dataObjectTO.getClassName() + ", and path: " + path );
        originalSource = ioService.readAllString( path );

        if (logger.isDebugEnabled()) logger.debug( "original source is: " + originalSource );

        JavaClassSource javaClassSource = Roaster.parse( JavaClassSource.class, originalSource );
        classTypeResolver = DriverUtils.getInstance().createClassTypeResolver( javaClassSource, classLoader );
        updateJavaClassSource( dataObjectTO, javaClassSource, renames, deletions, classTypeResolver);
        newSource = javaClassSource.toString();

        if (logger.isDebugEnabled()) logger.debug( "updated source is: " + newSource );
        return newSource;
    }

    private void updateJavaClassSource(DataObjectTO dataObjectTO, JavaClassSource javaClassSource, Map<String, String> renames, List<String> deletions, ClassTypeResolver classTypeResolver) throws Exception {

        if (javaClassSource == null || !javaClassSource.isClass())  {
            logger.warn( "A null javaClassSource or javaClassSouce is not a Class, no processing will be done. javaClassSource: " + javaClassSource + " className: " + ( javaClassSource != null ? javaClassSource.getName() : null) );
            return;
        }

        Map<String, FieldSource<JavaClassSource>> currentClassFields = new HashMap<String, FieldSource<JavaClassSource>>( );
        List<FieldSource<JavaClassSource>> classFields = javaClassSource.getFields();
        Map<String, String> preservedFields = new HashMap<String, String>( );
        DataModelerServiceHelper helper = DataModelerServiceHelper.getInstance();
        ObjectProperty property;
        DataObject dataObject = helper.to2Domain( dataObjectTO );
        JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver( );

        //update package, class name, and super class name if needed.
        modelDriver.updatePackage( javaClassSource, dataObjectTO.getPackageName() );
        modelDriver.updateImports( javaClassSource, renames, deletions );
        modelDriver.updateAnnotations( javaClassSource, dataObject.getAnnotations(), classTypeResolver );
        modelDriver.updateClassName( javaClassSource, dataObjectTO.getName() );
        modelDriver.updateSuperClassName( javaClassSource, dataObjectTO.getSuperClassName(), classTypeResolver );

        if (classFields != null) {
            for ( FieldSource<JavaClassSource> field : classFields) {
                currentClassFields.put( field.getName(), field );
            }
        }

        //create new fields and update existing.
        for (ObjectPropertyTO propertyTO : dataObjectTO.getProperties()) {

            property = helper.to2Domain( propertyTO );

            if (property.isFinal() || property.isStatic()) {
                preservedFields.put( property.getName(), property.getName() );
                continue;
            }

            if (propertyTO.isVolatile()) {
                //uncommon case
                if (currentClassFields.containsKey( propertyTO.getName() )) {
                    modelDriver.removeField( javaClassSource, propertyTO.getName(), classTypeResolver );
                }
                modelDriver.createField( javaClassSource, property, classTypeResolver );
                preservedFields.put( property.getName(), property.getName() );
            } else {
                if (propertyTO.nameChanged()) {
                    if (currentClassFields.containsKey( propertyTO.getOriginalName() )) {
                        modelDriver.updateField( javaClassSource, propertyTO.getOriginalName(), property, classTypeResolver );
                        preservedFields.put( propertyTO.getName(), propertyTO.getName() );
                    } else {
                        if (currentClassFields.containsKey( propertyTO.getName() )) {
                            modelDriver.removeField( javaClassSource, propertyTO.getName(), classTypeResolver );
                        }
                        modelDriver.createField( javaClassSource, property, classTypeResolver );
                        preservedFields.put( property.getName(), property.getName() );
                    }
                } else {
                    if (currentClassFields.containsKey( propertyTO.getName() )) {
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
        List<String> removableFields = new ArrayList<String>(  );
        for (FieldSource<JavaClassSource> field : currentClassFields.values()) {
            if (!preservedFields.containsKey( field.getName() ) &&
                    modelDriver.isManagedField( field, classTypeResolver ) ) {
                removableFields.add( field.getName() );
            }
        }
        for (String fieldName : removableFields) {
            modelDriver.removeField( javaClassSource, fieldName, classTypeResolver );
        }
    }

    private String createJavaSource(DataObject dataObject) throws Exception {

        GenerationContext generationContext = new GenerationContext( null );
        String source;
        GenerationEngine engine;

        try {
            engine = GenerationEngine.getInstance();
            source = engine.generateJavaClassString( generationContext, dataObject  );
        } catch ( Exception e ) {
            logger.error( "Java source for dataObject: " + dataObject.getClassName() + " couldn't be created.", e );
            throw e;
        }
        return source;
    }

    private ClassLoader getProjectClassLoader(Project project) {

        final KieModule module = builderCache.assertBuilder( project ).getKieModuleIgnoringErrors();
        final ClassLoader classLoader = KieModuleMetaData.Factory.newKieModuleMetaData( module ).getClassLoader();
        return classLoader;
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
        DataModelerServiceHelper serviceHelper = DataModelerServiceHelper.getInstance();

        for ( AnnotationDefinition annotationDefinition : annotationDefinitions ) {
            annotationDefinitionTO = serviceHelper.domain2To( annotationDefinition );
            annotations.put( annotationDefinitionTO.getClassName(), annotationDefinitionTO );
        }
        return annotations;
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

    private boolean hasUIChanges(DataObjectTO dataObjectTO) {
        String newFingerPrint = DataModelerServiceHelper.getInstance().calculateFingerPrint( dataObjectTO.getStringId() );
        boolean result = !newFingerPrint.equals( dataObjectTO.getFingerPrint() );
        if (!result) logger.debug( "The class : " + dataObjectTO.getClassName() + " wasn't modified" );
        return  result;
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

    //TODO refactor this two methods to other class
    private String updateJavaSourceAntlr( DataObjectTO dataObjectTO, org.uberfire.java.nio.file.Path path, ClassLoader classLoader ) throws Exception {

        String originalSource;
        String newSource;
        JavaFileHandler fileHandler;
        ClassTypeResolver classTypeResolver;

        if (logger.isDebugEnabled()) logger.debug( "Starting java source update for class: " + dataObjectTO.getClassName() + ", and path: " + path );
        originalSource = ioService.readAllString( path );

        if (logger.isDebugEnabled()) logger.debug( "original source is: " + originalSource );

        fileHandler = JavaFileHandlerFactory.getInstance().newHandler( originalSource );
        classTypeResolver = DriverUtils.getInstance().createClassTypeResolver( fileHandler.getFileDescr(), classLoader );
        updateJavaFileDescrAntlr( dataObjectTO, fileHandler.getFileDescr(), classTypeResolver );
        newSource = fileHandler.buildResult();

        if (logger.isDebugEnabled()) logger.debug( "updated source is: " + newSource );
        return newSource;
    }

    private void updateJavaFileDescrAntlr(DataObjectTO dataObjectTO, FileDescr fileDescr, ClassTypeResolver classTypeResolver) throws Exception {

        if (fileDescr == null || fileDescr.getClassDescr() == null) {
            logger.warn( "A null FileDescr or ClassDescr was provided, no processing will be done. fileDescr: " + fileDescr + " classDescr: " + ( fileDescr != null ? fileDescr.getClassDescr() : null) );
            return;
        }

        ClassDescr classDescr = fileDescr.getClassDescr();
        Map<String, FieldDescr> currentFields = new HashMap<String, FieldDescr>( );
        Map<String, String> preservedFields = new HashMap<String, String>( );
        DataModelerServiceHelper helper = DataModelerServiceHelper.getInstance();
        ObjectProperty property;
        DataObject dataObject = helper.to2Domain( dataObjectTO );
        JavaModelDriver modelDriver = new JavaModelDriver( );


        //update package, class name, and super class name if needed.
        modelDriver.updatePackage( fileDescr, dataObjectTO.getPackageName() );
        modelDriver.updateClassOrFieldAnnotations( fileDescr, classDescr, dataObject.getAnnotations(), classTypeResolver );
        modelDriver.updateClassName( classDescr, dataObjectTO.getName() );
        modelDriver.updateSuperClassName( classDescr, dataObjectTO.getSuperClassName(), classTypeResolver );


        //create new fields and update existing.
        for (FieldDescr fieldDescr : classDescr.getFields()) {
            for (VariableDeclarationDescr variableDescr : fieldDescr.getVariableDeclarations()) {
                currentFields.put( variableDescr.getIdentifier().getIdentifier(), fieldDescr );
            }
        }

        for (ObjectPropertyTO propertyTO : dataObjectTO.getProperties()) {

            property = helper.to2Domain( propertyTO );

            if (propertyTO.isVolatile()) {
                if (currentFields.containsKey( propertyTO.getName() )) {
                    modelDriver.removeField( classDescr, propertyTO.getName() );
                }
                modelDriver.createField( classDescr, property );
                preservedFields.put( property.getName(), property.getName() );
            } else {
                if (propertyTO.nameChanged()) {
                    if (currentFields.containsKey( propertyTO.getOriginalName() )) {
                        modelDriver.updateField( classDescr, propertyTO.getOriginalName(), property, classTypeResolver );
                        preservedFields.put( propertyTO.getName(), propertyTO.getName() );
                    } else {
                        if (currentFields.containsKey( propertyTO.getName() )) {
                            modelDriver.removeField( classDescr, propertyTO.getName() );
                        }
                        modelDriver.createField( classDescr, property );
                        preservedFields.put( property.getName(), property.getName() );
                    }
                } else {
                    if (currentFields.containsKey( propertyTO.getName() )) {
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
        List<String> removableFields = new ArrayList<String>(  );
        for (FieldDescr fieldDescr : classDescr.getFields()) {
            for (VariableDeclarationDescr variableDescr : fieldDescr.getVariableDeclarations()) {
                if (!preservedFields.containsKey( variableDescr.getIdentifier().getIdentifier() ) &&
                        variableDescr.getDimensionsCount() == 0 &&
                        modelDriver.isManagedField( fieldDescr, classTypeResolver ) ) {
                    removableFields.add( variableDescr.getIdentifier().getIdentifier() );
                }
            }
        }
        for (String fieldName : removableFields) {
            modelDriver.removeField( classDescr, fieldName );
        }
    }

}