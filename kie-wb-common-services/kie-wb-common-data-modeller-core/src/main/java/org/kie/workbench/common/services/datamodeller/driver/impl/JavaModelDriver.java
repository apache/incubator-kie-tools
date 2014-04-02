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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
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
import org.kie.workbench.common.services.datamodeller.parser.descr.ModifierDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.PackageDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.VariableDeclarationDescr;
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
    public DataObject addDataObject( DataModel dataModel, FileDescr fileDescr ) throws ModelDriverException {

        ClassDescr classDescr = fileDescr.getClassDescr();
        PackageDescr packageDescr = fileDescr.getPackageDescr();
        TypeDescr typeDescr;
        String className = classDescr.getIdentifier().getIdentifier();
        String superClass;
        String packageName = packageDescr != null ? packageDescr.getPackageName() : "";
        int modifiers = 0;

        if ( logger.isDebugEnabled() ) logger.debug( "Building DataObject for, packageName: " + packageName + ", className: " + className );

        modifiers = classDescr.getModifiers() != null ? buildModifierRepresentation( classDescr.getModifiers().getModifiers() ) : 0;

        DataObject dataObject = dataModel.addDataObject(packageName, className, modifiers);
        if ( (typeDescr = classDescr.getSuperClass()) != null) {
            if (typeDescr.isClassOrInterfaceType()) {
                superClass = typeDescr.getClassOrInterfaceType().getClassName();
                dataObject.setSuperClassName( superClass );
            }
        }

        List<AnnotationDescr> classAnnotations = classDescr.getModifiers() != null ? classDescr.getModifiers().getAnnotations() : null;
        if (classAnnotations != null) {
            for (AnnotationDescr classAnnotation : classAnnotations) {
                addDataObjectAnnotation(dataObject, classAnnotation);
            }
        }

        List<FieldDescr> fields = classDescr.getFields( );
        if (fields != null) {
            for (FieldDescr field : fields) {
                addProperty( dataObject, field );
            }
        }

        return dataObject;
    }

    private void addDataObjectAnnotation( DataObject dataObject, AnnotationDescr annotationDescr ) throws ModelDriverException {
        Annotation annotation = createAnnotation( annotationDescr );
        if ( annotation != null ) {
            dataObject.addAnnotation( annotation );
        }
    }

    private void addProperty( DataObject dataObject, FieldDescr field ) throws ModelDriverException {

        TypeDescr typeDescr;
        List<VariableDeclarationDescr> variableDeclarations;
        List<AnnotationDescr> fieldAnnotations;
        String className;
        ObjectProperty property;
        int modifiers = 0;

        typeDescr = field.getType();
        className = typeDescr.isPrimitiveType() ? typeDescr.getPrimitiveType().getName() : typeDescr.getClassOrInterfaceType().getClassName();
        variableDeclarations = field.getVariableDeclarations();

        modifiers = field.getModifiers() != null ? buildModifierRepresentation( field.getModifiers().getModifiers() ) : 0;


        if (variableDeclarations != null) {
            for (VariableDeclarationDescr variable : variableDeclarations) {
                //TODO, detect collections List<SomeThing> the same as we do with the DMO
                //TODO, when more than two properties are defined in the same field e.g.  int a, b, c; we must
                //create three properties in the DataModel and clone the annotations and values.
                //since the UI cant manage properties that are defined in the same line.
                property = dataObject.addProperty( variable.getIdentifier().getIdentifier(), className, modifiers );

                fieldAnnotations = field.getModifiers() != null ? field.getModifiers().getAnnotations() : null;
                if (fieldAnnotations != null) {
                    for (AnnotationDescr fieldAnnotation : fieldAnnotations) {
                        addPropertyAnnotation(property, fieldAnnotation);
                    }
                }
            }
        }
    }

    private void addPropertyAnnotation( ObjectProperty property, AnnotationDescr annotationDescr ) throws ModelDriverException {
        Annotation annotation = createAnnotation( annotationDescr );
        if ( annotation != null ) {
            property.addAnnotation( annotation );
        }
    }

    private Annotation createAnnotation( AnnotationDescr annotationToken ) throws ModelDriverException {

        AnnotationDefinition annotationDefinition = getConfiguredAnnotation(annotationToken.getQualifiedName().getName());
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

    private int buildModifierRepresentation( List<ModifierDescr> modifiers ) {
        int result = 0x0;
        if (modifiers != null) {
            for (ModifierDescr modifier : modifiers) {
                if ("public".equals( modifier.getName() )) result = result | Modifier.PUBLIC;
                if ("protected".equals( modifier.getName() )) result = result | Modifier.PROTECTED;
                if ("private".equals( modifier.getName() )) result = result | Modifier.PRIVATE;
                if ("abstract".equals( modifier.getName() )) result = result | Modifier.ABSTRACT;
                if ("static".equals( modifier.getName() )) result = result | Modifier.STATIC;
                if ("final".equals( modifier.getName() )) result = result | Modifier.FINAL;
                if ("transient".equals( modifier.getName() )) result = result | Modifier.TRANSIENT;
                if ("volatile".equals( modifier.getName() )) result = result | Modifier.VOLATILE;
                if ("synchronized".equals( modifier.getName() )) result = result | Modifier.SYNCHRONIZED;
                if ("native".equals( modifier.getName() )) result = result | Modifier.NATIVE;
                if ("strictfp".equals( modifier.getName() )) result = result | Modifier.STRICT;
                if ("interface".equals( modifier.getName() )) result = result | Modifier.INTERFACE;
            }
        }
        return result;
    }

}
