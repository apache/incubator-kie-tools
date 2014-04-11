/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.datamodeller.driver.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.base.ClassTypeResolver;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.ModelFactoryImpl;
import org.kie.workbench.common.services.datamodeller.driver.AnnotationDriver;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverListener;
import org.kie.workbench.common.services.datamodeller.driver.impl.annotations.CommonAnnotations;
import org.kie.workbench.common.services.datamodeller.parser.JavaParser;
import org.kie.workbench.common.services.datamodeller.parser.JavaParserFactory;
import org.kie.workbench.common.services.datamodeller.parser.descr.AnnotationDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ClassDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FieldDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FileDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.PackageDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.VariableDeclarationDescr;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.datamodeller.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class JavaModelDriver implements ModelDriver {

    private static final Logger logger = LoggerFactory.getLogger( JavaModelDriver.class );

    IOService ioService;

    boolean recursiveScan;

    Path javaRootPath;

    private ClassLoader classLoader;

    private List<AnnotationDefinition> configuredAnnotations = new ArrayList<AnnotationDefinition>( );

    private Map<String, AnnotationDefinition> configuredAnnotationsIndex = new HashMap<String, AnnotationDefinition>(  );

    private Map<String, AnnotationDriver> annotationDrivers = new HashMap<String, AnnotationDriver>();

    public JavaModelDriver() {
        configuredAnnotations.addAll( CommonAnnotations.getCommonAnnotations() );
        for (AnnotationDefinition annotationDefinition : configuredAnnotations) {
            annotationDrivers.put( annotationDefinition.getClassName(), new DefaultJavaModelAnnotationDriver() );
            configuredAnnotationsIndex.put( annotationDefinition.getClassName(), annotationDefinition );
        }
    }

    public JavaModelDriver( IOService ioService, Path javaRootPath, boolean recursiveScan, ClassLoader classLoader ) {
        this();
        this.ioService = ioService;
        this.recursiveScan = recursiveScan;
        this.javaRootPath = javaRootPath;
        this.classLoader = classLoader;
    }

    @Override
    public List<AnnotationDefinition> getConfiguredAnnotations( ) {
        return configuredAnnotations;
    }

    @Override
    public AnnotationDefinition getConfiguredAnnotation( String annotationClassName ) {
        return configuredAnnotationsIndex.get( annotationClassName );
    }

    @Override
    public AnnotationDriver getAnnotationDriver( String annotationClassName ) {
        return annotationDrivers.get( annotationClassName );
    }

    @Override public void generateModel( DataModel dataModel, ModelDriverListener generationListener ) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DataModel loadModel( ) throws ModelDriverException {

        JavaParser parser;
        DataModel dataModel;
        String fileContent;
        dataModel = createModel();

        List<Path> rootPaths = new ArrayList<Path>(  );
        rootPaths.add( javaRootPath );

        Collection<FileUtils.ScanResult> scanResults = FileUtils.getInstance().scan( ioService, rootPaths, ".java", true );
        if (scanResults != null) {
            for (FileUtils.ScanResult scanResult : scanResults) {

                logger.debug( "Starting processing for file: " + scanResult.getFile() );
                fileContent = ioService.readAllString(scanResult.getFile());
                if (fileContent == null || "".equals( fileContent )) {
                    logger.debug( "file: " + scanResult.getFile()  + " is empty." );
                    continue;
                }
                try {
                    parser = JavaParserFactory.newParser( fileContent );
                    parser.compilationUnit();

                    //TODO check that the parsed file is a Class an not an Interface, etc.
                    if (parser.getFileDescr().getClassDescr() != null) {
                        addDataObject( dataModel, parser.getFileDescr() );
                    } else {
                        logger.debug( "No Class definition was found for file: " + scanResult.getFile() );
                    }

                } catch (Exception e) {
                    //TODO add parsing errors processing. When a file can't be parsed the user should receive
                    //a notification and the data object won't be loaded into the IU.
                    logger.error("An error was produced during file parsing: " + scanResult.getFile(), e);
                    throw new ModelDriverException(e.getMessage(), e);
                }
            }
        }
        return dataModel;
    }

    @Override
    public DataModel createModel( ) {
        return ModelFactoryImpl.getInstance( ).newModel();
    }

    /**
     *  Knows how to create a DataObject from the descriptors returned by the parser.
     */
    private DataObject addDataObject( DataModel dataModel, FileDescr fileDescr ) throws ModelDriverException {

        ClassDescr classDescr = fileDescr.getClassDescr();
        PackageDescr packageDescr = fileDescr.getPackageDescr();
        TypeDescr typeDescr;
        String className = classDescr.getIdentifier().getIdentifier();
        String superClass;
        String packageName = packageDescr != null ? packageDescr.getPackageName() : "";
        int modifiers = 0;
        DriverUtils driverUtils = DriverUtils.getInstance();
        ClassTypeResolver classTypeResolver;

        if ( logger.isDebugEnabled() ) logger.debug( "Building DataObject for, packageName: " + packageName + ", className: " + className );

        classTypeResolver = DriverUtils.getInstance().createClassTypeResolver( fileDescr, classLoader );

        modifiers = classDescr.getModifiers() != null ? driverUtils.buildModifierRepresentation( classDescr.getModifiers().getModifiers() ) : 0;

        DataObject dataObject = dataModel.addDataObject(packageName, className, modifiers);
        if ( (typeDescr = classDescr.getSuperClass()) != null) {
            if (typeDescr.isClassOrInterfaceType()) {
                superClass = typeDescr.getClassOrInterfaceType().getClassName();
                superClass = resolveTypeName( classTypeResolver, superClass );
                dataObject.setSuperClassName( superClass );
            }
        }

        List<AnnotationDescr> classAnnotations = classDescr.getModifiers() != null ? classDescr.getModifiers().getAnnotations() : null;
        if (classAnnotations != null) {
            for (AnnotationDescr classAnnotation : classAnnotations) {
                addDataObjectAnnotation(dataObject, classAnnotation, classTypeResolver);
            }
        }

        List<FieldDescr> fields = classDescr.getFields( );
        if (fields != null) {
            for (FieldDescr field : fields) {
                if (isManagedType( field.getType(), classTypeResolver ) ) {
                    addProperty( dataObject, field, classTypeResolver );
                } else {
                    logger.debug( "field: " + field + " won't be loadoded by the diver because type: " + field.getType().getName() + " isn't a managed type.");
                }
            }
        }
        return dataObject;
    }

    private boolean isManagedType(TypeDescr typeDescr, ClassTypeResolver classTypeResolver) throws ModelDriverException {
        return DriverUtils.getInstance().isManagedType( typeDescr, classTypeResolver );
    }

    private String resolveTypeName(ClassTypeResolver classTypeResolver, String name) throws ModelDriverException {
        try {
            Class typeClass = classTypeResolver.resolveType( name );
            return typeClass.getName();
        } catch (ClassNotFoundException e) {
            logger.error( "Class could not be resolved for name: " + name, e );
            throw new ModelDriverException( "Class could not be resolved for name: " + name + ". " + e.getMessage(), e );
        }
    }

    private void addDataObjectAnnotation( DataObject dataObject, AnnotationDescr annotationDescr, ClassTypeResolver classTypeResolver ) throws ModelDriverException {
        Annotation annotation = createAnnotation( annotationDescr, classTypeResolver );
        if ( annotation != null ) {
            dataObject.addAnnotation( annotation );
        }
    }

    private void addProperty( DataObject dataObject, FieldDescr field, ClassTypeResolver classTypeResolver ) throws ModelDriverException {

        TypeDescr typeDescr;
        List<VariableDeclarationDescr> variableDeclarations;
        List<AnnotationDescr> fieldAnnotations;
        boolean multiple = false;
        String className;
        String bag = null;
        ObjectProperty property;
        int modifiers = 0;
        DriverUtils driverUtils = DriverUtils.getInstance();

        typeDescr = field.getType();
        if ( typeDescr.isPrimitiveType() ) {
            className = typeDescr.getPrimitiveType().getName();
        } else {
            if ( driverUtils.isSimpleClass( typeDescr )) {
                className = typeDescr.getClassOrInterfaceType().getClassName();
                className = resolveTypeName( classTypeResolver, className );
            } else {
                //if this point was reached, we know it's a Collection
                multiple = true;
                Object[] split = driverUtils.isSimpleGeneric( typeDescr );
                bag = resolveTypeName( classTypeResolver, split[1].toString() );
                className = ((TypeDescr)split[2]).getName();
                className = resolveTypeName( classTypeResolver, className );
            }
        }
        variableDeclarations = field.getVariableDeclarations();

        modifiers = field.getModifiers() != null ? driverUtils.buildModifierRepresentation( field.getModifiers().getModifiers() ) : 0;

        if (variableDeclarations != null) {
            for (VariableDeclarationDescr variable : variableDeclarations) {
                //TODO, detect collections List<SomeThing> the same as we do with the DMO
                //TODO, when more than two properties are defined in the same field e.g.  int a, b, c; we must
                //create three properties in the DataModel and clone the annotations and values.
                //since the UI cant manage properties that are defined in the same line.
                if (multiple) {
                    property = dataObject.addProperty( variable.getIdentifier().getIdentifier(), className, true, bag, modifiers );
                } else {
                    property = dataObject.addProperty( variable.getIdentifier().getIdentifier(), className, modifiers );
                }

                fieldAnnotations = field.getModifiers() != null ? field.getModifiers().getAnnotations() : null;
                if (fieldAnnotations != null) {
                    for (AnnotationDescr fieldAnnotation : fieldAnnotations) {
                        addPropertyAnnotation(property, fieldAnnotation, classTypeResolver);
                    }
                }
            }
        }
    }

    private void addPropertyAnnotation( ObjectProperty property, AnnotationDescr annotationDescr, ClassTypeResolver classTypeResolver ) throws ModelDriverException {
        Annotation annotation = createAnnotation( annotationDescr, classTypeResolver );
        if ( annotation != null ) {
            property.addAnnotation( annotation );
        }
    }

    private Annotation createAnnotation( AnnotationDescr annotationToken, ClassTypeResolver classTypeResolver ) throws ModelDriverException {

        String annotationClassName = resolveTypeName( classTypeResolver, annotationToken.getQualifiedName().getName() );

        AnnotationDefinition annotationDefinition = getConfiguredAnnotation(annotationClassName);
        Annotation annotation = null;

        if ( annotationDefinition != null ) {
            AnnotationDriver annotationDriver = getAnnotationDriver( annotationDefinition.getClassName() );
            if ( annotationDriver != null ) {
                annotation = annotationDriver.buildAnnotation( annotationDefinition, annotationToken );
            } else {
                logger.warn( "AnnotationDriver for annotation: " + annotationToken.getQualifiedName().getName() + " is not configured for this driver" );
            }
        } else {
            logger.warn( "Annotation: " + annotationToken.getQualifiedName().getName() + " is not configured for this driver." );
        }
        return annotation;
    }
}
