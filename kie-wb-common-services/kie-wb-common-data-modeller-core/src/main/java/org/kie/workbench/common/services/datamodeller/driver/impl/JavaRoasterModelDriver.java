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

import org.apache.commons.lang.StringUtils;
import org.drools.core.base.ClassTypeResolver;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.SyntaxError;
import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.AnnotationTargetSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.Import;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationContext;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationEngine;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationTools;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationMemberDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.ModelFactoryImpl;
import org.kie.workbench.common.services.datamodeller.driver.AnnotationDriver;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverError;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverListener;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverResult;
import org.kie.workbench.common.services.datamodeller.driver.impl.annotations.CommonAnnotations;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.datamodeller.util.FileUtils;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class JavaRoasterModelDriver implements ModelDriver {

    private static final Logger logger = LoggerFactory.getLogger( JavaRoasterModelDriver.class );

    IOService ioService;

    boolean recursiveScan;

    Path javaRootPath;

    private ClassLoader classLoader;

    private List<AnnotationDefinition> configuredAnnotations = new ArrayList<AnnotationDefinition>();

    private Map<String, AnnotationDefinition> configuredAnnotationsIndex = new HashMap<String, AnnotationDefinition>();

    private Map<String, AnnotationDriver> annotationDrivers = new HashMap<String, AnnotationDriver>();

    public JavaRoasterModelDriver() {
        configuredAnnotations.addAll( CommonAnnotations.getCommonAnnotations() );
        for ( AnnotationDefinition annotationDefinition : configuredAnnotations ) {
            annotationDrivers.put( annotationDefinition.getClassName(), new DefaultJavaRoasterModelAnnotationDriver() );
            configuredAnnotationsIndex.put( annotationDefinition.getClassName(), annotationDefinition );
        }
    }

    public JavaRoasterModelDriver( IOService ioService, Path javaRootPath, boolean recursiveScan, ClassLoader classLoader ) {
        this();
        this.ioService = ioService;
        this.recursiveScan = recursiveScan;
        this.javaRootPath = javaRootPath;
        this.classLoader = classLoader;
    }

    @Override
    public List<AnnotationDefinition> getConfiguredAnnotations() {
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
    public ModelDriverResult loadModel() throws ModelDriverException {

        ModelDriverResult result = new ModelDriverResult( );
        DataModel dataModel;
        String fileContent;
        dataModel = createModel();
        result.setDataModel( dataModel );

        List<Path> rootPaths = new ArrayList<Path>();
        rootPaths.add( javaRootPath );

        Collection<FileUtils.ScanResult> scanResults = FileUtils.getInstance().scan( ioService, rootPaths, ".java", true );
        if ( scanResults != null ) {
            for ( FileUtils.ScanResult scanResult : scanResults ) {

                logger.debug( "Starting file loading into model, file: " + scanResult.getFile() );
                fileContent = ioService.readAllString( scanResult.getFile() );
                if ( fileContent == null || "".equals( fileContent ) ) {
                    logger.debug( "file: " + scanResult.getFile() + " is empty." );
                    continue;
                }
                try {
                    JavaType<?> javaType = Roaster.parse( fileContent );
                    if ( javaType.isClass() ) {
                        if (javaType.getSyntaxErrors() != null && javaType.getSyntaxErrors().isEmpty()) {
                            //if a file has parsing errors it will be skipped
                            addSyntaxErrors( result, scanResult.getFile(), javaType.getSyntaxErrors() );
                        } else {
                            addDataObject( dataModel, (JavaClassSource)javaType );
                        }
                    } else {
                        logger.debug( "No Class definition was found for file: " + scanResult.getFile() + ", it will be skipped." );
                    }
                } catch ( Exception e ) {
                    //TODO add parsing errors processing. When a file can't be parsed the user should receive
                    //a notification and the data object won't be loaded into the IU.
                    logger.error( "An error was produced during file parsing: " + scanResult.getFile(), e );
                    throw new ModelDriverException( e.getMessage(), e );
                }
            }
        }
        return result;
    }

    private void addSyntaxErrors( ModelDriverResult result, Path file, List<SyntaxError> syntaxErrors ) {
        ModelDriverError error;
        for (SyntaxError syntaxError : syntaxErrors) {
            error = new ModelDriverError( syntaxError.getDescription(), file);
            result.addError( error );
        }
    }

    @Override
    public DataModel createModel() {
        return ModelFactoryImpl.getInstance().newModel();
    }

    private DataObject addDataObject( DataModel dataModel, JavaClassSource javaClassSource ) throws ModelDriverException {

        String className;
        String packageName;
        String superClass;
        int modifiers;
        DriverUtils driverUtils = DriverUtils.getInstance();
        ClassTypeResolver classTypeResolver;

        className = javaClassSource.getName();
        packageName = javaClassSource.getPackage();

        if ( logger.isDebugEnabled() ) {
            logger.debug( "Building DataObject for, packageName: " + packageName + ", className: " + className );
        }

        classTypeResolver = DriverUtils.getInstance().createClassTypeResolver( javaClassSource, classLoader );

        modifiers = driverUtils.buildModifierRepresentation( javaClassSource );

        DataObject dataObject = dataModel.addDataObject( packageName, className, modifiers );

        if ( javaClassSource.getSuperType() != null ) {
            superClass = resolveTypeName( classTypeResolver, javaClassSource.getSuperType() );
            dataObject.setSuperClassName( superClass );
        }

        List<AnnotationSource<JavaClassSource>> annotations = javaClassSource.getAnnotations();
        if ( annotations != null ) {
            for ( AnnotationSource annotation : annotations ) {
                addDataObjectAnnotation( dataObject, annotation, classTypeResolver );
            }
        }

        List<FieldSource<JavaClassSource>> fields = javaClassSource.getFields();
        if ( fields != null ) {
            for ( FieldSource<JavaClassSource> field : fields ) {
                if ( driverUtils.isManagedType( field.getType(), classTypeResolver ) ) {
                    addProperty( dataObject, field, classTypeResolver );
                } else {
                    logger.debug( "field: " + field + "with fieldName: " + field.getName() + " won't be loaded by the diver because type: " + field.getType().getName() + " isn't a managed type." );
                }
            }
        }
        return dataObject;
    }

    private ObjectProperty addProperty( DataObject dataObject, FieldSource<JavaClassSource> field, ClassTypeResolver classTypeResolver ) throws ModelDriverException {

        Type type;
        boolean multiple = false;
        String className;
        String bag = null;
        ObjectProperty property;
        int modifiers;
        DriverUtils driverUtils = DriverUtils.getInstance();

        modifiers = driverUtils.buildModifierRepresentation( field );

        type = field.getType();
        if ( type.isPrimitive() ) {
            className = type.getName();
        } else {
            if ( driverUtils.isSimpleClass( type ) ) {
                className = resolveTypeName( classTypeResolver, type.getName() );
            } else {
                //if this point was reached, we know it's a Collection. Managed type check was done previous to adding the property.
                multiple = true;
                Type elementsType = ( ( List<Type> ) type.getTypeArguments() ).get( 0 );
                className = resolveTypeName( classTypeResolver, elementsType.getName() );
                bag = resolveTypeName( classTypeResolver, type.getName() );
            }
        }

        if ( multiple ) {
            property = dataObject.addProperty( field.getName(), className, true, bag, modifiers );
        } else {
            property = dataObject.addProperty( field.getName(), className, modifiers );
        }

        List<AnnotationSource<JavaClassSource>> annotations = field.getAnnotations();
        if ( annotations != null ) {
            for ( AnnotationSource annotation : annotations ) {
                addPropertyAnnotation( property, annotation, classTypeResolver );
            }
        }

        return property;
    }

    private void addDataObjectAnnotation( DataObject dataObject, AnnotationSource annotationSource, ClassTypeResolver classTypeResolver ) throws ModelDriverException {
        Annotation annotation = createAnnotation( annotationSource, classTypeResolver );
        if ( annotation != null ) {
            dataObject.addAnnotation( annotation );
        }
    }

    private void addPropertyAnnotation( ObjectProperty property, AnnotationSource annotationSource, ClassTypeResolver classTypeResolver ) throws ModelDriverException {
        Annotation annotation = createAnnotation( annotationSource, classTypeResolver );
        if ( annotation != null ) {
            property.addAnnotation( annotation );
        }
    }

    private Annotation createAnnotation( AnnotationSource annotationToken, ClassTypeResolver classTypeResolver ) throws ModelDriverException {

        String annotationClassName = resolveTypeName( classTypeResolver, annotationToken.getName() );

        AnnotationDefinition annotationDefinition = getConfiguredAnnotation( annotationClassName );
        Annotation annotation = null;

        if ( annotationDefinition != null ) {
            AnnotationDriver annotationDriver = getAnnotationDriver( annotationDefinition.getClassName() );
            if ( annotationDriver != null ) {
                annotation = annotationDriver.buildAnnotation( annotationDefinition, annotationToken );
            } else {
                logger.warn( "AnnotationDriver for annotation: " + annotationToken.getName() + " is not configured for this driver" );
            }
        } else {
            logger.warn( "Annotation: " + annotationToken.getName() + " is not configured for this driver." );
        }
        return annotation;
    }

    private String resolveTypeName( ClassTypeResolver classTypeResolver, String name ) throws ModelDriverException {
        try {
            Class typeClass = classTypeResolver.resolveType( name );
            return typeClass.getName();
        } catch ( ClassNotFoundException e ) {
            logger.error( "Class could not be resolved for name: " + name, e );
            throw new ModelDriverException( "Class could not be resolved for name: " + name + ". " + e.getMessage(), e );
        }
    }

    public void updateImports(JavaClassSource javaClassSource, Map<String, String> renamedClasses, List<String> deletedClasses) {

        List<Import> imports = javaClassSource.getImports();
        String newClassName;
        String currentPackage = javaClassSource.isDefaultPackage() ? null : javaClassSource.getPackage();
        NamingUtils namingUtils = NamingUtils.getInstance();

        if (imports != null) {
            for (Import currentImport : imports) {
                if (!currentImport.isWildcard() && !currentImport.isStatic()) {
                    if ( (newClassName = renamedClasses.get( currentImport.getQualifiedName() ) ) != null ) {
                        javaClassSource.removeImport( currentImport );
                        if (!StringUtils.equals( currentPackage, namingUtils.extractPackageName( newClassName ) )) {
                            javaClassSource.addImport( newClassName );
                        }
                    } else if (deletedClasses.contains( currentImport.getQualifiedName() )) {
                        javaClassSource.removeImport( currentImport );
                    }
                }
            }
        }
    }

    public boolean updatePackage(JavaClassSource javaClassSource, String packageName) {

        String oldPackageName = javaClassSource.getPackage();
        if (packageName == null) {
            javaClassSource.setDefaultPackage();
        } else {
            javaClassSource.setPackage( packageName );
        }

        return StringUtils.equals( oldPackageName, packageName );
    }

    public boolean updateClassName( JavaClassSource javaClassSource, String name ) throws Exception {

        String oldName = javaClassSource.getName();
        javaClassSource.setName( name );

        return StringUtils.equals( oldName, name );
    }

    public boolean updateSuperClassName( JavaClassSource javaClassSource, String superClassName, ClassTypeResolver classTypeResolver ) throws Exception {

        String oldSuperClassName = javaClassSource.getSuperType() != null ? classTypeResolver.getFullTypeName( javaClassSource.getSuperType() ) : null;

        if (!StringUtils.equals( oldSuperClassName, superClassName )) {
            //TODO remove the extra "import packageName.SuperClassName" added by Roaster when a class name is set as superclass.
            javaClassSource.setSuperType( superClassName );
            return true;
        }
        return false;
    }

    public void updateAnnotations( AnnotationTargetSource annotationTargetSource, List<Annotation> annotations, ClassTypeResolver classTypeResolver ) throws Exception {

        JavaRoasterModelDriver driver = new JavaRoasterModelDriver( );
        String currentAnnotationClassName;
        List<AnnotationSource<?>> currentAnnotations = annotationTargetSource.getAnnotations();
        if (currentAnnotations != null) {
            for (AnnotationSource<?> currentAnnotation : currentAnnotations) {
                currentAnnotationClassName = classTypeResolver.getFullTypeName( currentAnnotation.getName() );
                if (driver.getConfiguredAnnotation( currentAnnotationClassName ) != null) {
                    annotationTargetSource.removeAnnotation( currentAnnotation );
                }
            }
        }

        if (annotations != null) {
            for (Annotation annotation : annotations) {
                addAnnotation( annotationTargetSource, annotation );
            }
        }
    }

    public AnnotationSource<?> addAnnotation( AnnotationTargetSource annotationTargetSource, Annotation annotation) {

        JavaRoasterModelDriver driver = new JavaRoasterModelDriver( );
        AnnotationSource<?> newAnnotationSource = annotationTargetSource.addAnnotation();
        newAnnotationSource.setName( annotation.getClassName() );

        AnnotationDefinition annotationDefinition = annotation.getAnnotationDefinition();
        if (annotationDefinition == null) annotationDefinition = driver.getConfiguredAnnotation( annotation.getClassName() );

        if (annotationDefinition == null) {
            logger.warn( "Annotation: " + annotation.getClassName() + " is not configured for this driver" );
        } else if (!annotationDefinition.isMarker()) {
            for (AnnotationMemberDefinition memberDefinition : annotationDefinition.getAnnotationMembers()) {
                Object value = annotation.getValue( memberDefinition.getName() );
                if (value != null) {
                    addMemberValue( newAnnotationSource, memberDefinition, value );
                }
            }
        }
        return newAnnotationSource;
    }

    public void addMemberValue(AnnotationSource<?> annotationSource, AnnotationMemberDefinition memberDefinition, Object value) {
        StringBuilder strValue = new StringBuilder( );
        GenerationTools genTools = new GenerationTools();

        if (memberDefinition.isEnum()) {

            strValue.append( memberDefinition.getClassName() );
            strValue.append( "." );
            strValue.append( value );
            annotationSource.setLiteralValue( memberDefinition.getName(), strValue.toString() );

        } else if (memberDefinition.isString()) {
            annotationSource.setStringValue( memberDefinition.getName(), ( value != null ? value.toString() : null ) );
        } else if (memberDefinition.isPrimitiveType()) {
            //primitive types are wrapped by the java.lang.type.

            if (Character.class.getName().equals(memberDefinition.getClassName())) {
                annotationSource.setLiteralValue( memberDefinition.getName(), value.toString() );
            } else if (Long.class.getName().equals(memberDefinition.getClassName())) {
                strValue.append(value.toString());
                strValue.append("L");
                annotationSource.setLiteralValue( memberDefinition.getName(), strValue.toString() );
            } else if (Float.class.getName().equals(memberDefinition.getClassName())) {
                strValue.append(value.toString());
                strValue.append("f");
                annotationSource.setLiteralValue( memberDefinition.getName(), strValue.toString() );
            } else if (Double.class.getName().equals(memberDefinition.getClassName())) {
                strValue.append(value.toString());
                strValue.append("d");
                annotationSource.setLiteralValue( memberDefinition.getName(), strValue.toString() );
            } else {
                annotationSource.setLiteralValue( memberDefinition.getName(), value.toString() );
            }
        }
    }

    public void createField(JavaClassSource javaClassSource, ObjectProperty property, ClassTypeResolver classTypeResolver) throws Exception {

        String fieldSource;
        String methodSource;
        String methodName;

        GenerationContext generationContext = new GenerationContext( null );
        GenerationEngine engine;
        GenerationTools genTools = new GenerationTools();

        try {
            engine = GenerationEngine.getInstance();

            fieldSource = genTools.indent( engine.generateCompleteFieldString( generationContext, property ) );
            javaClassSource.addField( fieldSource );

            //create getter
            methodSource = genTools.indent( engine.generateFieldGetterString( generationContext, property ) );
            methodName = genTools.toJavaGetter( property.getName(), property.getClassName() );

            //remove old getter if exists
            removeMethod( javaClassSource, methodName );
            //add the new getter
            javaClassSource.addMethod( methodSource );

            //create setter
            methodSource = genTools.indent( engine.generateFieldSetterString( generationContext, property ) );
            methodName = genTools.toJavaSetter( property.getName() );

            //remove old setter if exists
            //TODO check collections
            Class<?> fieldClass = classTypeResolver.resolveType( property.getClassName() );
            removeMethod( javaClassSource, methodName, fieldClass );
            //add the new setter
            javaClassSource.addMethod( methodSource );

        } catch ( Exception e ) {
            logger.error( "Field: " + property.getName() + " couldn't be created.", e );
            throw e;
        }
    }

    public void updateField(JavaClassSource javaClassSource, String fieldName, ObjectProperty property, ClassTypeResolver classTypeResolver) throws Exception {

        GenerationTools genTools = new GenerationTools();
        GenerationEngine engine = GenerationEngine.getInstance();
        GenerationContext context = new GenerationContext( null );
        DriverUtils driverUtils = DriverUtils.getInstance();
        boolean updateAccessors = false;
        FieldSource<JavaClassSource> field;

        field = javaClassSource.getField( fieldName );
        Type oldType = field.getType();

        if (hasChangedToCollectionType( field, property, classTypeResolver ) ) {
            //fields that changed to a collection like java.util.List<SomeEntity>
            //needs to be removed and created again due to Roaster
            updateCollectionField( javaClassSource, fieldName, property, classTypeResolver );
        } else {
            //for the rest of changes is better to manage the field update without removing the field.

            if ( !fieldName.equals( property.getName() )) {
                field.setName( property.getName() );
                //the field was renamed, accessors must be updated.
                updateAccessors = true;
            }

            if ( driverUtils.isManagedType( field.getType(), classTypeResolver ) &&
                    !driverUtils.equalsType( field.getType(), property.getClassName(), property.isMultiple(), property.getBag(), classTypeResolver ) ) {
                //the has type changed, and not to a collection type.

                String newClassName = property.getClassName();
                field.setType( newClassName );

                //TODO enable this if when Roaster NullPointerException is fixed.
                //if (field.getLiteralInitializer() != null) {
                    //current field has an initializer, but the field type changed so we are not sure old initializer is
                    //valid for the new type.
                    if ( NamingUtils.getInstance().isPrimitiveTypeId( newClassName )) {
                        setPrimitiveTypeDefaultInitializer( field, newClassName );
                    } else {
                        field.setLiteralInitializer( null );
                    }
                //}
                updateAccessors = true;
            }

            updateAnnotations( field, property.getAnnotations(), classTypeResolver );

            if (updateAccessors) {

                String accessorName;
                String methodSource;
                String oldClassName;

                //remove old accessors
                //TODO check primitive types
                Class<?> oldClass = classTypeResolver.resolveType( oldType.getName() );
                oldClassName = oldClass.getName();

                accessorName = genTools.toJavaGetter( fieldName, oldClassName );
                removeMethod( javaClassSource, accessorName );

                accessorName = genTools.toJavaSetter( fieldName );
                removeMethod( javaClassSource, accessorName, oldClass );

                //and generate the new ones
                methodSource = genTools.indent( engine.generateFieldGetterString( context, property ) );
                javaClassSource.addMethod( methodSource );

                methodSource = genTools.indent( engine.generateFieldSetterString( context, property ) );
                javaClassSource.addMethod( methodSource );
            }
        }
    }

    private void updateCollectionField(JavaClassSource javaClassSource, String fieldName, ObjectProperty property, ClassTypeResolver classTypeResolver) throws Exception {

        GenerationTools genTools = new GenerationTools();
        GenerationEngine engine = GenerationEngine.getInstance();
        GenerationContext context = new GenerationContext( null );
        boolean updateAccessors = true;
        FieldSource<JavaClassSource> currentField;

        currentField = javaClassSource.getField( fieldName );
        Type currentType = currentField.getType();

        StringBuilder fieldSource = new StringBuilder( );
        List<AnnotationSource<JavaClassSource>> annotations = currentField.getAnnotations();
        if (annotations != null) {
            for (AnnotationSource<JavaClassSource> annotation : annotations) {
                if (!isManagedAnnotation( annotation, classTypeResolver )) {
                    fieldSource.append( annotation.toString() );
                    fieldSource.append( "\n" );
                }
            }
        }

        fieldSource.append( engine.generateCompleteFieldString( context, property ) );

        javaClassSource.removeField( currentField );
        javaClassSource.addField( fieldSource.toString() );


        if (updateAccessors) {

            String accessorName;
            String methodSource;
            String oldClassName;

            //remove old accessors
            //TODO check primitive types
            Class<?> oldClass = classTypeResolver.resolveType( currentType.getName() );
            oldClassName = oldClass.getName();

            accessorName = genTools.toJavaGetter( fieldName, oldClassName );
            removeMethod( javaClassSource, accessorName );

            accessorName = genTools.toJavaSetter( fieldName );
            removeMethod( javaClassSource, accessorName, oldClass );

            //and generate the new ones
            methodSource = genTools.indent( engine.generateFieldGetterString( context, property ) );
            javaClassSource.addMethod( methodSource );

            methodSource = genTools.indent( engine.generateFieldSetterString( context, property ) );
            javaClassSource.addMethod( methodSource );
        }
    }

    private boolean hasChangedToCollectionType(FieldSource<JavaClassSource> field, ObjectProperty property, ClassTypeResolver classTypeResolver ) throws Exception {
        DriverUtils driverUtils = DriverUtils.getInstance();

        return driverUtils.isManagedType( field.getType(), classTypeResolver )
                && !driverUtils.equalsType( field.getType(), property.getClassName(), property.isMultiple(), property.getBag(), classTypeResolver ) &&
                property.isMultiple();
    }

    public void updateConstructors(JavaClassSource javaClassSource, DataObject dataObject) throws Exception {

        //TODO First implementation deletes all constructors and creates them again.
        //Next iteration should take into account only the needed ones, etc.

        GenerationContext generationContext = new GenerationContext( null );
        GenerationEngine engine = GenerationEngine.getInstance();
        DriverUtils driverUtils = DriverUtils.getInstance();
        GenerationTools genTools = new GenerationTools();

        boolean needsKeyFieldsConstructor;
        int keyFieldsCount = 0;
        int assignableFieldsCount = 0;

        String defaultConstructorSource = null;
        String allFieldsConstructorSource = null;
        String kieFieldsConstructorSource = null;
        String equalsMethodSource = null;
        String hashCodeMethodSource = null;

        List<MethodSource<JavaClassSource>> currentConstructors = new ArrayList<MethodSource<JavaClassSource>>( );
        MethodSource<JavaClassSource> currentEquals = null;
        MethodSource<JavaClassSource> currentHashCode = null;
        MethodSource<JavaClassSource> newConstructor;

        assignableFieldsCount = driverUtils.assignableFieldsCount( dataObject );
        keyFieldsCount = driverUtils.keyFieldsCount( dataObject );
        needsKeyFieldsConstructor =  keyFieldsCount > 0 &&  ( keyFieldsCount < assignableFieldsCount );

        List<MethodSource<JavaClassSource>> methods = javaClassSource.getMethods();
        if (methods != null) {
            for (MethodSource<JavaClassSource> method : methods) {
                if (method.isConstructor()) {
                    currentConstructors.add( method );
                } else if (isEquals( method )) {
                    currentEquals = method;
                } else if (isHashCode( method )) {
                    currentHashCode = method;
                }
            }
        }

        //remove current constructors
        for (MethodSource<JavaClassSource> constructor : currentConstructors) {
            javaClassSource.removeMethod( constructor );
        }
        if (currentEquals != null) javaClassSource.removeMethod( currentEquals );
        if (currentHashCode != null) javaClassSource.removeMethod( currentHashCode );

        defaultConstructorSource = genTools.indent( engine.generateDefaultConstructorString( generationContext, dataObject ) );
        newConstructor = javaClassSource.addMethod( defaultConstructorSource );
        newConstructor.setConstructor( true );

        if (assignableFieldsCount > 0) {
            allFieldsConstructorSource = genTools.indent( engine.generateAllFieldsConstructorString( generationContext, dataObject ) );
            newConstructor = javaClassSource.addMethod( allFieldsConstructorSource );
            newConstructor.setConstructor( true );
        }
        if (needsKeyFieldsConstructor) {
            kieFieldsConstructorSource = genTools.indent( engine.generateKeyFieldsConstructorString( generationContext, dataObject ) );
            newConstructor = javaClassSource.addMethod( kieFieldsConstructorSource );
            newConstructor.setConstructor( true );
        }
        if (keyFieldsCount > 0) {
            equalsMethodSource = genTools.indent( engine.generateEqualsString( generationContext, dataObject ) );
            javaClassSource.addMethod( equalsMethodSource );

            hashCodeMethodSource = genTools.indent( engine.generateHashCodeString( generationContext, dataObject ) );
            javaClassSource.addMethod( hashCodeMethodSource );
        }
    }

    /**
     * Takes care of field and the corresponding setter/getter removal.
     */
    public void removeField(JavaClassSource javaClassSource, String fieldName, ClassTypeResolver classTypeResolver) throws Exception {
        logger.debug( "Removing field: " + fieldName + ", from class: " + javaClassSource.getName() );

        FieldSource<JavaClassSource> field;
        GenerationTools genTools = new GenerationTools();
        String methodName;
        MethodSource<JavaClassSource> method;

        field = javaClassSource.getField( fieldName );
        if (field != null) {

            //check if the class has a setter/getter for the given field.
            Class<?> fieldClass = classTypeResolver.resolveType( field.getType().getName() );
            methodName = genTools.toJavaGetter( fieldName, fieldClass.getName() );
            method = javaClassSource.getMethod( methodName );
            if (method != null) {
                logger.debug( "Removing getter: " + methodName + " for field: " + fieldName );
                javaClassSource.removeMethod( method );
            }

            methodName = genTools.toJavaSetter( fieldName );
            method = javaClassSource.getMethod( methodName, fieldClass );
            if (method != null) {
                logger.debug( "Removing setter: " + methodName + " for field: " + fieldName );
                javaClassSource.removeMethod( method );
            }

            //finally remove the field.
            javaClassSource.removeField( field );

        }  else {
            logger.debug( "Field field: " + fieldName + " was not found in class: " + javaClassSource.getName() );
        }
    }

    public void removeMethod( JavaClassSource javaClassSource, String methodName, Class<?>... paramTypes ) {
        logger.debug( "Removing method: " + methodName + ", form class: " + javaClassSource.getName() );
        MethodSource<JavaClassSource> method = javaClassSource.getMethod( methodName, paramTypes );
        if (method != null) {
            javaClassSource.removeMethod( method );
            logger.debug( "Method method: " + methodName + ", was removed from class: " + javaClassSource.getName() );
        } else {
            logger.debug( "Method method: " + methodName + " not exists for class: " + javaClassSource.getName() );
        }
    }

    public boolean isManagedField( FieldSource<JavaClassSource> field, ClassTypeResolver classTypeResolver ) throws Exception {

        if (!field.isFinal() && !field.isStatic()) {
            //finally we can check if the field type is a managed type.
            //if not, the field should remain untouched
            return DriverUtils.getInstance().isManagedType( field.getType(), classTypeResolver );
        }
        return false;
    }

    public boolean isEquals(MethodSource<?> method) {
        //TODO add return type check and non parameters check
        return method.getName().equals( "equals" );
    }

    public boolean isHashCode(MethodSource<?> method) {
        return method.getName().equals( "hashCode" );
    }

    public void setPrimitiveTypeDefaultInitializer(FieldSource<?> field, String primitiveType) {

        if (NamingUtils.BYTE.equals( primitiveType )) field.setLiteralInitializer( "0" );
        if (NamingUtils.SHORT.equals( primitiveType )) field.setLiteralInitializer( "0" );
        if (NamingUtils.INT.equals( primitiveType )) field.setLiteralInitializer( "0" );
        if (NamingUtils.LONG.equals( primitiveType )) field.setLiteralInitializer( "0L" );
        if (NamingUtils.FLOAT.equals( primitiveType )) field.setLiteralInitializer( "0.0f" );
        if (NamingUtils.DOUBLE.equals( primitiveType )) field.setLiteralInitializer( "0.0d" );
        if (NamingUtils.CHAR.equals( primitiveType )) field.setLiteralInitializer( "\'\\u0000\'" );
        if (NamingUtils.BOOLEAN.equals( primitiveType )) field.setLiteralInitializer( "false" );
    }

    public boolean isManagedAnnotation( AnnotationSource<?> annotation, ClassTypeResolver classTypeResolver) throws Exception {
        String annotationClassName = classTypeResolver.getFullTypeName( annotation.getName() );
        return getConfiguredAnnotation( annotationClassName ) != null;
    }

}
