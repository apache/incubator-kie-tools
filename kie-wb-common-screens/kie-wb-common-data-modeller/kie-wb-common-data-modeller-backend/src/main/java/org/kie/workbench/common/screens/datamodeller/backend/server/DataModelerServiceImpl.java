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

import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
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
import org.kie.workbench.common.services.datamodeller.codegen.GenerationContext;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationEngine;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationTools;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.datamodeller.core.impl.PropertyTypeFactoryImpl;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.impl.DataModelOracleDriver;
import org.kie.workbench.common.services.datamodeller.driver.impl.JavaModelDriver;
import org.kie.workbench.common.services.datamodeller.parser.JavaFileHandler;
import org.kie.workbench.common.services.datamodeller.parser.JavaFileHandlerFactory;
import org.kie.workbench.common.services.datamodeller.parser.descr.ClassDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.DescriptorFactory;
import org.kie.workbench.common.services.datamodeller.parser.descr.DescriptorFactoryImpl;
import org.kie.workbench.common.services.datamodeller.parser.descr.ElementDescriptor;
import org.kie.workbench.common.services.datamodeller.parser.descr.FieldDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FileDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.IdentifierDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.JavaTokenDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.MethodDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ModifierDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.PackageDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.QualifiedNameDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TextTokenElementDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeDescr;
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

    private static final String DEFAULT_COMMIT_MESSAGE = "Data modeller generated action.";

    private static final String START_INDENT = "\n";
    private static final String LINE_INDENT = "    ";
    private static final String END_INDENT = "\n";

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

    private void generateModel ( DataModelTO dataModelTO,
            org.uberfire.java.nio.file.Path javaRootPath,
            CommentedOption option ) throws Exception {

        org.uberfire.java.nio.file.Path sourceFile;
        org.uberfire.java.nio.file.Path targetFile;
        org.uberfire.java.nio.file.Path deletedObjectFile;
        String newSource;
        DataModelerServiceHelper helper = DataModelerServiceHelper.getInstance();

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
                    newSource = updateJavaSource( dataObjectTO, sourceFile );
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

    private void updateJavaFileDescr(DataObjectTO dataObjectTO, FileDescr fileDescr) throws Exception {


        if (fileDescr == null || fileDescr.getClassDescr() == null) {
            logger.warn( "A null FileDescr or ClassDescr was provided, no processing will be done. fileDescr: " + fileDescr + " classDescr: " + ( fileDescr != null ? fileDescr.getClassDescr() : null) );
            return;
        }

        ClassDescr classDescr = fileDescr.getClassDescr();
        Map<String, FieldDescr> currentFields = new HashMap<String, FieldDescr>( );
        Map<String, String> preservedFields = new HashMap<String, String>( );
        DataModelerServiceHelper helper = DataModelerServiceHelper.getInstance();
        ObjectProperty property;


        //TODO update type annotations

        //update package, class name, and super class name if needed.
        updatePackage( fileDescr, dataObjectTO.getPackageName() );
        updateClassName( classDescr, dataObjectTO.getName() );
        updateSuperClassName( classDescr, dataObjectTO.getSuperClassName() );


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


        //update constructors, equals and hashCode methods.
        updateConstructors( classDescr, helper.to2Domain( dataObjectTO ) );

        //delete fields from .java file that not exists in the DataObject.
        List<String> removableFields = new ArrayList<String>(  );
        for (FieldDescr fieldDescr : classDescr.getFields()) {
            for (VariableDeclarationDescr variableDescr : fieldDescr.getVariableDeclarations()) {
                if (!preservedFields.containsKey( variableDescr.getIdentifier().getIdentifier() )) {
                    removableFields.add( variableDescr.getIdentifier().getIdentifier() );
                }
            }
        }
        for (String fieldName : removableFields) {
            removeField( classDescr, fieldName );
        }
    }

    private void updateConstructors(ClassDescr classDescr, DataObject dataObject ) throws Exception {

        //TODO First implementation deletes all constructors and creates them again.
        //Next iteration should take into account only the needed ones, etc.

        DescriptorFactory descriptorFactory = DescriptorFactoryImpl.getInstance();
        GenerationContext generationContext = new GenerationContext( null );
        GenerationEngine engine = GenerationEngine.getInstance();

        MethodDescr defaultConstructor = null;
        MethodDescr allFieldsConstructor = null;
        MethodDescr keyFieldsConstructor = null;
        MethodDescr equalsMethod = null;
        MethodDescr hashCodeMethod = null;
        boolean needsKeyFieldsConstructor;
        int keyFieldsCount = 0;
        boolean needsAllFieldsConstructor;

        List<MethodDescr> currentConstructors;
        MethodDescr currentEquals;
        MethodDescr currentHashCode;
        ElementDescriptor constructorsInsertionPoint = null;
        ElementDescriptor equalsInsertionPoint = null;
        ElementDescriptor hashCodeInsertionPoint = null;

        needsAllFieldsConstructor = dataObject.getProperties() != null && dataObject.getProperties().size() > 0;
        keyFieldsCount = keyFieldsCount( dataObject );
        needsKeyFieldsConstructor =  keyFieldsCount > 0 &&  ( keyFieldsCount < (dataObject.getProperties() != null ? dataObject.getProperties().size() : 0) );
        currentConstructors = classDescr.getConstructors();
        if (currentConstructors != null && currentConstructors.size() > 0) {
            constructorsInsertionPoint = currentConstructors.get( currentConstructors.size() - 1 );
        } else {
            List<FieldDescr> fields = classDescr.getFields();
            if (fields != null && fields.size() > 0) {
                constructorsInsertionPoint = fields.get( fields.size() -1 );
            } else {
                constructorsInsertionPoint = classDescr.getBodyStartBrace();
            }
        }
        currentEquals = classDescr.getMethod( "equals" );
        currentHashCode = classDescr.getMethod( "hashCode" );

        defaultConstructor = descriptorFactory.createMethodDescr( indent( engine.generateDefaultConstructorString( generationContext, dataObject ) ), true );
        if (needsAllFieldsConstructor) allFieldsConstructor = descriptorFactory.createMethodDescr( indent( engine.generateAllFieldsConstructorString( generationContext, dataObject ) ), true );
        if (needsKeyFieldsConstructor) {
            keyFieldsConstructor = descriptorFactory.createMethodDescr( indent( engine.generateKeyFieldsConstructorString( generationContext, dataObject ) ), true );
        }
        if (keyFieldsCount > 0) {
            equalsMethod = descriptorFactory.createMethodDescr( indent( engine.generateEqualsString( generationContext, dataObject ) ), true );
            hashCodeMethod = descriptorFactory.createMethodDescr( indent( engine.generateHashCodeString( generationContext, dataObject ) ), true );

            //calculate equals and hashCode insertion point.

            if (currentEquals != null) {
                equalsInsertionPoint = currentEquals;
            } else {
                List<MethodDescr> methods = classDescr.getMethods();
                if (methods != null && methods.size() > 0) {
                    equalsInsertionPoint = methods.get( methods.size() - 1 );
                } else if (keyFieldsConstructor != null) {
                   equalsInsertionPoint = keyFieldsConstructor;
                } else if (allFieldsConstructor != null) {
                    equalsInsertionPoint = allFieldsConstructor;
                } else {
                    equalsInsertionPoint = defaultConstructor;
                }
            }

            if (currentHashCode != null) {
                hashCodeInsertionPoint = currentHashCode;
            } else {
                hashCodeInsertionPoint = equalsMethod;
            }
        }

        //add the generated methods to the ClassDescr and remove the oldOnes
        if (defaultConstructor != null) {
            classDescr.getElements().addElementAfter( constructorsInsertionPoint, defaultConstructor );
            constructorsInsertionPoint = defaultConstructor;
        }
        if (allFieldsConstructor != null) {
            classDescr.getElements().addElementAfter( constructorsInsertionPoint, allFieldsConstructor );
            constructorsInsertionPoint = allFieldsConstructor;
        }
        if (keyFieldsConstructor != null) {
            classDescr.getElements().addElementAfter( constructorsInsertionPoint, keyFieldsConstructor );
        }
        if (keyFieldsCount > 0) {
            classDescr.getElements().addElementAfter( equalsInsertionPoint, equalsMethod );
            classDescr.getElements().addElementAfter( hashCodeInsertionPoint, hashCodeMethod );
        }

        //finally remove old constructors, and old equals and hashCode implementation
        if (currentConstructors != null) {
            for (MethodDescr constructor : currentConstructors) {
                classDescr.getElements().remove( constructor );
            }
        }
        if (currentEquals != null) {
            classDescr.getElements().remove( currentEquals );
        }
        if (currentHashCode != null) {
            classDescr.getElements().remove( currentHashCode );
        }
    }

    private int keyFieldsCount( DataObject dataObject ) {
        int result = 0;
        for (ObjectProperty property : dataObject.getProperties().values()) {
            if (property.getAnnotation( org.kie.api.definition.type.Key.class.getName() ) != null) {
                result++;
            }
        }
        return result;
    }

    private boolean updatePackage(FileDescr fileDescr, String packageName) throws Exception {

        DescriptorFactory descriptorFactory = DescriptorFactoryImpl.getInstance();
        boolean hasChanged = false;

        if (packageName == null && fileDescr.getPackageDescr() != null) {
            fileDescr.getElements().remove( fileDescr.getPackageDescr() );
            hasChanged = true;
        } else if (packageName != null && fileDescr.getPackageDescr() == null) {
            PackageDescr packageDescr = descriptorFactory.createPackageDescr( "package " + packageName + ";" );
            fileDescr.getElements().add( 0, packageDescr );
            hasChanged = true;
        } else if (packageName != null && fileDescr.getPackageDescr() != null && !packageName.equals( fileDescr.getPackageDescr().getPackageName() )) {
            PackageDescr packageDescr = fileDescr.getPackageDescr();
            QualifiedNameDescr oldPackageName = packageDescr.getQualifiedName();
            QualifiedNameDescr newPackageName = descriptorFactory.createQualifiedNameDescr( packageName );
            packageDescr.getElements().addMemberBefore( oldPackageName, newPackageName );
            packageDescr.getElements().remove( oldPackageName );
            hasChanged = true;
        }

        return hasChanged;
    }

    private boolean updateClassName( ClassDescr classDescr, String name ) throws Exception {

        DescriptorFactory descriptorFactory = DescriptorFactoryImpl.getInstance();
        boolean hasChanged = false;

        if (!name.equals( classDescr.getName() )) {
            IdentifierDescr oldIdentifier = classDescr.getIdentifier();
            IdentifierDescr newIdentifier = descriptorFactory.createIdentifierDescr( name );
            classDescr.getElements().addMemberBefore( oldIdentifier, newIdentifier );
            classDescr.getElements().remove( oldIdentifier );
            hasChanged = true;
        }

        return hasChanged;
    }

    private boolean updateSuperClassName( ClassDescr classDescr, String superClassName ) throws Exception {

        DescriptorFactory descriptorFactory = DescriptorFactoryImpl.getInstance();
        boolean hasChanged = false;

        if (superClassName == null && classDescr.hasSuperClass()) {
            classDescr.getElements().remove( classDescr.getExtendsToken() );
            classDescr.getElements().remove( classDescr.getSuperClass() );
            hasChanged = true;
        } else if ( superClassName != null && !classDescr.hasSuperClass()) {
            TypeDescr superClassType = descriptorFactory.createTypeDescr( superClassName );
            JavaTokenDescr extendsToken = descriptorFactory.createExtendsTokenDescr();
            TextTokenElementDescr space1 = descriptorFactory.createTextTokenDescr( " " );
            TextTokenElementDescr space2 = descriptorFactory.createTextTokenDescr( " " );
            TextTokenElementDescr space3 = descriptorFactory.createTextTokenDescr( " " );

            classDescr.getElements().addElementAfter( classDescr.getIdentifier(), space1 );
            classDescr.getElements().addElementAfter( space1, extendsToken );
            classDescr.getElements().addElementAfter( extendsToken, space2 );
            classDescr.getElements().addElementAfter( space2, superClassType );
            classDescr.getElements().addElementAfter( superClassType, space3 );
            hasChanged = true;
        } else if ( superClassName != null && classDescr.hasSuperClass() && !superClassName.equals( classDescr.getSuperClass().getName() ) ) {
            TypeDescr oldSuperClass = classDescr.getSuperClass();
            TypeDescr newSuperClass = descriptorFactory.createTypeDescr( superClassName );
            classDescr.getElements().addMemberBefore( oldSuperClass, newSuperClass );
            classDescr.getElements().remove( oldSuperClass );
            hasChanged = true;
        }

        return hasChanged;
    }

    private void updateField(ClassDescr classDescr, String fieldName, ObjectProperty property) throws Exception {

        FieldDescr fieldDescr;
        FieldDescr targetFieldDescr;
        TypeDescr oldType;

        VariableDeclarationDescr variable;
        DescriptorFactory descriptorFactory = DescriptorFactoryImpl.getInstance();
        GenerationTools genTools = new GenerationTools();
        GenerationEngine engine = GenerationEngine.getInstance();
        GenerationContext context = new GenerationContext( null );
        boolean updateAccessors = false;

        fieldDescr = classDescr.getField( fieldName );
        oldType = fieldDescr.getType();

        if (fieldDescr.getVariableDeclarations().size() > 1) {
            variable = fieldDescr.getVariableDeclaration( fieldName );
            String fieldStr = copyFieldVariableToString( fieldDescr, fieldName );
            targetFieldDescr = descriptorFactory.createFieldDescr( indent( fieldStr ), true );
            fieldDescr.removeVariableDeclaration( variable );
            classDescr.addField( targetFieldDescr );
        } else {
            targetFieldDescr = fieldDescr;
        }

        variable = targetFieldDescr.getVariableDeclaration( fieldName );
        if ( !fieldName.equals( property.getName() )) {
            //the field was renamed.
            IdentifierDescr newIdentifier = descriptorFactory.createIdentifierDescr( property.getName() );
            IdentifierDescr oldIdentifier = variable.getIdentifier();
            variable.getElements().addMemberBefore( oldIdentifier, newIdentifier );
            variable.getElements().remove( oldIdentifier );
            updateAccessors = true;
        }

        if (!isSameType( fieldDescr.getType(), property.getClassName() ) ) {
            //the type changed.
            TypeDescr newType = descriptorFactory.createTypeDescr( property.getClassName() );
            oldType = targetFieldDescr.getType();
            targetFieldDescr.getElements().addMemberBefore( oldType, newType );
            targetFieldDescr.getElements().remove( oldType );
            updateAccessors = true;
        }
        //TODO, add annotations processing.

        if (updateAccessors) {

            String accessorName;
            String methodSource;
            MethodDescr methodDescr;
            String oldClassName;
            boolean removed = false;

            oldClassName = oldType.isClassOrInterfaceType() ? oldType.getClassOrInterfaceType().getClassName() : oldType.getPrimitiveType().getName();
            //remove old accessors
            accessorName = genTools.toJavaGetter( fieldName, oldClassName );
            logger.debug( "Removing getter: " + accessorName + " for field: " + fieldName );
            removed = classDescr.removeMethod( accessorName );
            if (removed) logger.debug( "getter: " + accessorName + " was removed.");
            accessorName = genTools.toJavaSetter( fieldName );
            logger.debug( "Removing setter: " + accessorName + " for field: " + fieldName );
            removed = classDescr.removeMethod( accessorName );
            if (removed) logger.debug( "setter: " + accessorName + " was removed." );

            //and generate the new ones
            //TODO check if I need to do something aditional when annotations starts to being included
            methodSource = indent( engine.generateFieldGetterString( context, property ) );
            methodDescr = descriptorFactory.createMethodDescr( methodSource, true );
            classDescr.addMethod( methodDescr );

            methodSource = indent( engine.generateFieldSetterString( context, property ) );
            methodDescr = descriptorFactory.createMethodDescr( methodSource, true );
            classDescr.addMethod( methodDescr );
        }
    }

    private boolean isSameType(TypeDescr type, String className) {

        if (type.isPrimitiveType()) return className.equals( type.getPrimitiveType().getName() );

        if (type.isClassOrInterfaceType()) return className.equals( type.getClassOrInterfaceType().getClassName() );

        return false;
    }

    private String copyFieldVariableToString(FieldDescr fieldDescr, String name) {

        StringBuilder newFieldStr = new StringBuilder(  );
        VariableDeclarationDescr variable = fieldDescr.getVariableDeclaration( name );

        //TODO, process annotations.

        boolean first = true;
        if (fieldDescr.getModifiers() != null) {
            for ( ModifierDescr modifierDescr : fieldDescr.getModifiers().getModifiers()) {
                if (!first) newFieldStr.append( " " );
                newFieldStr.append( modifierDescr.getName() );
                first = false;
            }
        }

        if (fieldDescr.getType().isPrimitiveType()) {
            if (!first) newFieldStr.append( " " );
            newFieldStr.append( fieldDescr.getType().getPrimitiveType().getName() );
        } else {
            if (!first) newFieldStr.append( " " );
            newFieldStr.append( fieldDescr.getType().getClassOrInterfaceType().getClassName() );
        }

        newFieldStr.append( " " );
        newFieldStr.append( variable.getIdentifier().getIdentifier() );

        for (int i = 0; i < variable.getDimensionsCount(); i++) {
            newFieldStr.append( "[]" );
        }

        if (variable.getVariableInitializer() != null) {
            newFieldStr.append( " = " );
            newFieldStr.append( variable.getVariableInitializer().getInitializerExpr() );
        }

        newFieldStr.append( ";" );

        return newFieldStr.toString();
    }

    private void createField(ClassDescr classDescr, ObjectProperty property) throws Exception {

        String fieldSource;
        FieldDescr fieldDescr;
        String methodSource;
        MethodDescr methodDescr;
        String methodName;

        DescriptorFactory descriptorFactory = DescriptorFactoryImpl.getInstance();
        GenerationContext generationContext = new GenerationContext( null );
        GenerationEngine engine;
        GenerationTools genTools = new GenerationTools();

        try {

            engine = GenerationEngine.getInstance();

            fieldSource = indent( engine.generateCompleteFieldString( generationContext, property) );
            fieldDescr = descriptorFactory.createFieldDescr( fieldSource, true );
            classDescr.addField( fieldDescr );

            methodSource = indent( engine.generateFieldGetterString( generationContext, property ) );
            methodDescr = descriptorFactory.createMethodDescr( methodSource, true );
            methodName = genTools.toJavaGetter( property.getName(), property.getClassName() );
            classDescr.removeMethod( methodName );
            classDescr.addMethod( methodDescr );

            methodSource = indent( engine.generateFieldSetterString( generationContext, property ) );
            methodDescr = descriptorFactory.createMethodDescr( methodSource, true );
            methodName = genTools.toJavaSetter( property.getName() );
            classDescr.removeMethod( methodName );
            classDescr.addMethod( methodDescr );

        } catch ( Exception e ) {
            logger.error( "Field: " + property.getName() + " couldn't be created.", e );
            throw e;
        }
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

    private boolean hasUIChanges(DataObjectTO dataObjectTO) {
        String newFingerPrint = DataModelerServiceHelper.getInstance().calculateFingerPrint( dataObjectTO.getStringId() );
        boolean result = !newFingerPrint.equals( dataObjectTO.getFingerPrint() );
        if (!result) logger.debug( "The class : " + dataObjectTO.getClassName() + " wasn't modified" );
        return  result;
    }

    private String indent(String source) throws Exception {
        return START_INDENT + GenerationEngine.indentLines( source, LINE_INDENT ) + END_INDENT;
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