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
import org.kie.workbench.common.services.datamodeller.codegen.GenerationContext;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationEngine;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationTools;
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
import org.kie.workbench.common.services.datamodeller.parser.descr.DescriptorFactory;
import org.kie.workbench.common.services.datamodeller.parser.descr.DescriptorFactoryImpl;
import org.kie.workbench.common.services.datamodeller.parser.descr.ElementDescriptor;
import org.kie.workbench.common.services.datamodeller.parser.descr.FieldDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FileDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.IdentifierDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.JavaTokenDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.MethodDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ModifierDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ModifierListDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ModifiersContainerDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.PackageDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.QualifiedNameDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TextTokenElementDescr;
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

    public boolean isManagedField( FieldDescr fieldDescr, ClassTypeResolver classTypeResolver ) throws Exception {
        //e.g. final, or static fields in the .java file are not managed
        ModifierListDescr modifierListDescr = fieldDescr.getModifiers();
        List<ModifierDescr> modifiers = modifierListDescr != null ? modifierListDescr.getModifiers() : null;
        if (modifiers != null) {
            for (ModifierDescr modifier : modifiers) {
                if ( "static".equals( modifier.getName() ) || "final".equals( modifier.getName() )) {
                    return false;
                }
            }
        }

        //finally we can check if the field type is a managed type.
        //if not, the field should remain untouched
        return DriverUtils.getInstance().isManagedType( fieldDescr.getType(), classTypeResolver );

    }

    public void updateClassOrFieldAnnotations( ElementDescriptor parent, ModifiersContainerDescr modifiersContainer, List<Annotation> annotations, ClassTypeResolver classTypeResolver ) throws Exception {

        //TODO can be improved, and eventually updateFieldAnnotations and updateClassAnnotations can be unified in one method
        ModifierListDescr modifierList;
        ModifierListDescr newModifierList;
        AnnotationDescr newAnnotationDescr;
        List<AnnotationDescr> oldAnnotations;
        List<AnnotationDescr> unmangedAnnotations = new ArrayList<AnnotationDescr>(  );
        List<ModifierDescr> oldModifiers;
        int currentManagedAnnotations = 0;
        boolean isClass = (modifiersContainer instanceof ClassDescr);

        JavaModelDriver driver = new JavaModelDriver( );
        DescriptorFactory descriptorFactory = DescriptorFactoryImpl.getInstance();
        GenerationContext generationContext = new GenerationContext( null );
        String source;
        GenerationEngine engine = GenerationEngine.getInstance();
        GenerationTools genTools = new GenerationTools();

        modifierList = modifiersContainer.getModifiers();
        oldAnnotations = modifierList != null ? modifierList.getAnnotations() : null;
        oldModifiers = modifierList != null ? modifierList.getModifiers() : null;

        //save the unmanged annotations
        if (oldAnnotations != null) {
            for (AnnotationDescr oldAnnotation : oldAnnotations) {
                String annotationClassName = classTypeResolver.getFullTypeName( oldAnnotation.getQualifiedName().getName() );
                if (driver.getConfiguredAnnotation( annotationClassName ) == null) {
                    unmangedAnnotations.add( oldAnnotation );
                } else {
                    currentManagedAnnotations++;
                }
            }
        }

        if ( (currentManagedAnnotations > 0) || (annotations != null && annotations.size() > 0) ) {

            newModifierList = new ModifierListDescr(  );

            //TODO, check if we want to sort the annotations list
            for (Annotation annotation : annotations) {
                source = engine.generateAnnotationString( generationContext, annotation );
                source = isClass ? genTools.indentClassAnnotation( source ) : genTools.indentFieldAnnotation( source );
                newAnnotationDescr = descriptorFactory.createAnnotationDescr( source, true );
                newModifierList.getElements().add( newAnnotationDescr );
            }

            for (AnnotationDescr unmanagedAnnotation : unmangedAnnotations) {
                StringBuilder indentString = isClass ? new StringBuilder( "\n" ) : new StringBuilder( "\n    " );
                TextTokenElementDescr indent = new TextTokenElementDescr( indentString.toString(), 0, indentString.length()-1, 1, 0 );
                indent.setSourceBuffer( indentString );
                unmanagedAnnotation.getElements().add( 0, indent );
                newModifierList.add( unmanagedAnnotation );
            }
            boolean first = true;
            if (oldModifiers != null) {
                for (ModifierDescr oldModifier : oldModifiers) {
                    if (first) {
                        StringBuilder indentString = isClass ? new StringBuilder( "\n") : new StringBuilder( "\n    " );
                        TextTokenElementDescr indent = new TextTokenElementDescr( indentString.toString(), 0, indentString.length()-1, 1, 0 );
                        indent.setSourceBuffer( indentString );
                        newModifierList.getElements().add( indent );
                    }
                    first = false;
                    newModifierList.add( oldModifier );
                }
            }

            if (newModifierList.size() > 0) {
                if (modifierList == null) {
                    StringBuilder spaceString = new StringBuilder( " " );
                    TextTokenElementDescr spaceTextToken = new TextTokenElementDescr( spaceString.toString(), 0, spaceString.length()-1, 1, 0 );
                    spaceTextToken.setSourceBuffer( spaceString );

                    if (isClass) {
                        IdentifierDescr identifierDescr = ((ClassDescr)modifiersContainer).getIdentifier();
                        modifiersContainer.getElements().addMemberBefore( identifierDescr, spaceTextToken );
                    } else {
                        //((FieldDescr)modifiersContainer).getIdentifier();
                        modifiersContainer.getElements().add( 0, spaceTextToken );
                    }
                    modifiersContainer.getElements().addMemberBefore( spaceTextToken, newModifierList );
                } else {
                    modifiersContainer.getElements().addElementAfter( modifierList, newModifierList );
                }
            }

            if (modifierList != null) {
                if (newModifierList.size() > 0) {
                    adjustTokenIndent( parent, modifiersContainer, modifiersIndentationOptions );
                }
                modifiersContainer.getElements().remove( modifierList );
            }
        }
    }

    public void updateConstructors(ClassDescr classDescr, DataObject dataObject ) throws Exception {

        //TODO First implementation deletes all constructors and creates them again.
        //Next iteration should take into account only the needed ones, etc.

        DescriptorFactory descriptorFactory = DescriptorFactoryImpl.getInstance();
        GenerationContext generationContext = new GenerationContext( null );
        GenerationEngine engine = GenerationEngine.getInstance();
        GenerationTools genTools = new GenerationTools();

        MethodDescr defaultConstructor = null;
        MethodDescr allFieldsConstructor = null;
        MethodDescr keyFieldsConstructor = null;
        MethodDescr equalsMethod = null;
        MethodDescr hashCodeMethod = null;
        boolean needsKeyFieldsConstructor;
        int keyFieldsCount = 0;
        int assignableFieldsCount = 0;

        List<MethodDescr> currentConstructors;
        MethodDescr currentEquals;
        MethodDescr currentHashCode;
        ElementDescriptor constructorsInsertionPoint = null;
        ElementDescriptor equalsInsertionPoint = null;
        ElementDescriptor hashCodeInsertionPoint = null;

        assignableFieldsCount = DriverUtils.getInstance().assignableFieldsCount( dataObject );
        keyFieldsCount = DriverUtils.getInstance().keyFieldsCount( dataObject );
        needsKeyFieldsConstructor =  keyFieldsCount > 0 &&  ( keyFieldsCount < assignableFieldsCount );
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

        defaultConstructor = descriptorFactory.createMethodDescr( genTools.indent( engine.generateDefaultConstructorString( generationContext, dataObject ) ), true );
        if (assignableFieldsCount > 0) allFieldsConstructor = descriptorFactory.createMethodDescr( genTools.indent( engine.generateAllFieldsConstructorString( generationContext, dataObject ) ), true );
        if (needsKeyFieldsConstructor) {
            keyFieldsConstructor = descriptorFactory.createMethodDescr( genTools.indent( engine.generateKeyFieldsConstructorString( generationContext, dataObject ) ), true );
        }
        if (keyFieldsCount > 0) {
            equalsMethod = descriptorFactory.createMethodDescr( genTools.indent( engine.generateEqualsString( generationContext, dataObject ) ), true );
            hashCodeMethod = descriptorFactory.createMethodDescr( genTools.indent( engine.generateHashCodeString( generationContext, dataObject ) ), true );

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
                adjustFieldOrMethodIndent( classDescr, constructor );
                classDescr.getElements().remove( constructor );
            }
        }
        if (currentEquals != null) {
            adjustFieldOrMethodIndent( classDescr, currentEquals );
            classDescr.getElements().remove( currentEquals );
        }
        if (currentHashCode != null) {
            adjustFieldOrMethodIndent( classDescr, currentHashCode );
            classDescr.getElements().remove( currentHashCode );
        }
    }

    public boolean updatePackage(FileDescr fileDescr, String packageName) throws Exception {

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

    public boolean updateClassName( ClassDescr classDescr, String name ) throws Exception {

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

    public boolean updateSuperClassName( ClassDescr classDescr, String superClassName, ClassTypeResolver classTypeResolver ) throws Exception {

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
        } else if ( superClassName != null && classDescr.hasSuperClass() ) {
            String currentSuperClass = classTypeResolver.getFullTypeName( classDescr.getSuperClass().getName() );
            if (!superClassName.equals( currentSuperClass )) {
                TypeDescr oldSuperClass = classDescr.getSuperClass();
                TypeDescr newSuperClass = descriptorFactory.createTypeDescr( superClassName );
                classDescr.getElements().addMemberBefore( oldSuperClass, newSuperClass );
                classDescr.getElements().remove( oldSuperClass );
                hasChanged = true;
            }
        }

        return hasChanged;
    }

    public void updateField(ClassDescr classDescr, String fieldName, ObjectProperty property, ClassTypeResolver classTypeResolver) throws Exception {

        FieldDescr fieldDescr;
        FieldDescr targetFieldDescr;
        TypeDescr oldType;

        VariableDeclarationDescr variable;
        DescriptorFactory descriptorFactory = DescriptorFactoryImpl.getInstance();
        GenerationTools genTools = new GenerationTools();
        GenerationEngine engine = GenerationEngine.getInstance();
        GenerationContext context = new GenerationContext( null );
        DriverUtils driverUtils = DriverUtils.getInstance();
        boolean updateAccessors = false;

        fieldDescr = classDescr.getField( fieldName );
        oldType = fieldDescr.getType();

        if (fieldDescr.getVariableDeclarations().size() > 1) {
            variable = fieldDescr.getVariableDeclaration( fieldName );
            String fieldStr = copyFieldVariableToString( fieldDescr, fieldName );
            targetFieldDescr = descriptorFactory.createFieldDescr( genTools.indent( fieldStr ), true );
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

        if ( driverUtils.isManagedType( fieldDescr.getType(), classTypeResolver ) && !driverUtils.equalsType( fieldDescr.getType(), property.getClassName(), property.isMultiple(), property.getBag(), classTypeResolver ) ) {
            //the type changed.
            //TODO review this collections treatment
            String newClassName;
            if (property.isMultiple()) {
                newClassName = property.getBag() + "<" + property.getClassName() +">";
            } else {
                newClassName = property.getClassName();
            }
            TypeDescr newType = descriptorFactory.createTypeDescr( newClassName );
            oldType = targetFieldDescr.getType();
            targetFieldDescr.getElements().addMemberBefore( oldType, newType );
            targetFieldDescr.getElements().remove( oldType );
            updateAccessors = true;
        }

        updateClassOrFieldAnnotations( classDescr, fieldDescr, property.getAnnotations(), classTypeResolver );

        if (updateAccessors) {

            String accessorName;
            String methodSource;
            MethodDescr methodDescr;
            String oldClassName;
            boolean removed = false;

            //remove old accessors
            oldClassName = oldType.isClassOrInterfaceType() ? oldType.getClassOrInterfaceType().getClassName() : oldType.getPrimitiveType().getName();
            accessorName = genTools.toJavaGetter( fieldName, oldClassName );
            logger.debug( "Removing getter: " + accessorName + " for field: " + fieldName );
            removeMethod( classDescr, accessorName );

            accessorName = genTools.toJavaSetter( fieldName );
            logger.debug( "Removing setter: " + accessorName + " for field: " + fieldName );
            removeMethod( classDescr, accessorName );

            //and generate the new ones
            //TODO check if I need to do something aditional when annotations starts to being included
            methodSource = genTools.indent( engine.generateFieldGetterString( context, property ) );
            methodDescr = descriptorFactory.createMethodDescr( methodSource, true );
            classDescr.addMethod( methodDescr );

            methodSource = genTools.indent( engine.generateFieldSetterString( context, property ) );
            methodDescr = descriptorFactory.createMethodDescr( methodSource, true );
            classDescr.addMethod( methodDescr );
        }
    }

    public void createField(ClassDescr classDescr, ObjectProperty property) throws Exception {

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

            fieldSource = genTools.indent( engine.generateCompleteFieldString( generationContext, property ) );
            fieldDescr = descriptorFactory.createFieldDescr( fieldSource, true );
            classDescr.addField( fieldDescr );

            //create getter
            methodSource = genTools.indent( engine.generateFieldGetterString( generationContext, property ) );
            methodDescr = descriptorFactory.createMethodDescr( methodSource, true );
            methodName = genTools.toJavaGetter( property.getName(), property.getClassName() );

            //remove old getter if exists
            removeMethod( classDescr, methodName );
            //add the new getter
            classDescr.addMethod( methodDescr );

            //create setter
            methodSource = genTools.indent( engine.generateFieldSetterString( generationContext, property ) );
            methodDescr = descriptorFactory.createMethodDescr( methodSource, true );
            methodName = genTools.toJavaSetter( property.getName() );

            //remove old setter if exists
            removeMethod( classDescr, methodName );
            //add the new setter
            classDescr.addMethod( methodDescr );

        } catch ( Exception e ) {
            logger.error( "Field: " + property.getName() + " couldn't be created.", e );
            throw e;
        }
    }

    private void removeMethod( ClassDescr classDescr, String methodName ) {
        logger.debug( "Removing method: " + methodName + ", form class: " + classDescr.getIdentifier().getIdentifier() );

        MethodDescr methodDescr = classDescr.getMethod( methodName );
        if (methodDescr != null) {
            adjustFieldOrMethodIndent( classDescr, methodDescr );
            classDescr.getElements().remove( methodDescr );
        } else {
            logger.debug( "Method method: " + methodName + " not exists for class: " + classDescr.getIdentifier().getIdentifier());
        }
    }

    /**
     * Takes care of field and the corresponding setter/getter removal.
     */
    public void removeField(ClassDescr classDescr, String fieldName) {
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

            //TODO experimental
            FieldDescr field = classDescr.getField( fieldName );
            adjustFieldOrMethodIndent( classDescr, field );
            removed = classDescr.removeField( fieldName );
            if (removed) logger.debug( "field: " + fieldName + " was removed." );

            accessorName = genTools.toJavaGetter( fieldName, fieldType );
            logger.debug( "Removing getter: " + accessorName + " for field: " + fieldName );
            removeMethod( classDescr, accessorName );

            accessorName = genTools.toJavaSetter( fieldName );
            logger.debug( "Removing setter: " + accessorName + " for field: " + fieldName );
            removeMethod( classDescr, accessorName );

        } else {
            logger.debug( "Field field: " + fieldName + " was not found in class: " + classDescr.getIdentifier().getIdentifier() );
        }
    }

    static final String[] indentationOptions = new String[] { "\n\n    ", "\n\n   ", "\n\n  ", "\n\n ", "\n\n", "\n    ", "\n   ", "\n  ", "\n ", "\n" };

    static final String[] modifiersIndentationOptions = new String[] { "\n    ", "\n   ", "\n  ", "\n ", "\n" };

    private void adjustTokenIndent(ElementDescriptor parent, ElementDescriptor element, final String[] indentationOptions) {
        int index = parent.getElements().indexOf( element );
        if (index > 0) {
            ElementDescriptor sibling = parent.getElements().get( index -1 );
            if (sibling instanceof TextTokenElementDescr) {
                //try to guess if we can remove empty indentation space
                TextTokenElementDescr textToken = (TextTokenElementDescr)sibling;
                String content = textToken.getSourceBuffer().substring( textToken.getStart(), textToken.getStop()+1 );
                //TODO improve the way to guess if we need to remove some indentation stuff
                if (content != null && content.length() > 0) {
                    for (int i = 0; i < indentationOptions.length; i++) {
                        if (content.endsWith( indentationOptions[i] )) {
                            int newStop = textToken.getStop() - indentationOptions[i].length();
                            if (newStop < textToken.getStart()) {
                                //it was a complete indentation text token, we can just remove it.
                                parent.getElements().remove( sibling );
                            } else {
                                textToken.setStop( newStop );
                            }
                            break;
                        }
                    }
                }
            }
        }
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

    private void adjustModifiersIndent(ElementDescriptor parent, ModifierListDescr modifierListDescr) {
        adjustTokenIndent( parent, modifierListDescr, modifiersIndentationOptions );
    }

    private void adjustFieldOrMethodIndent(ClassDescr classDescr, ElementDescriptor fieldOrMethod) {
        adjustTokenIndent( classDescr, fieldOrMethod, indentationOptions );
    }

}
