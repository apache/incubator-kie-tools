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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
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
import org.jboss.forge.roaster.model.source.ParameterSource;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationContext;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationEngine;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationTools;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationMemberDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.JavaTypeInfo;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.JavaTypeInfoImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ModelFactoryImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;
import org.kie.workbench.common.services.datamodeller.driver.AnnotationDriver;
import org.kie.workbench.common.services.datamodeller.driver.DriverResult;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverError;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverListener;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverResult;
import org.kie.workbench.common.services.datamodeller.driver.TypeInfoResult;
import org.kie.workbench.common.services.datamodeller.driver.impl.annotations.CommonAnnotations;
import org.kie.workbench.common.services.datamodeller.driver.impl.annotations.PositionAnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.util.DataModelUtils;
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

    private static final String DATA_OBJECT_LOAD_ERROR = "It was not possible to create or load DataObject: \"{0}\" .";

    private static final String ANNOTATION_LOAD_ERROR = "It was not possible to create or load a DataObject or Field annotation for annotation class name: \"{0}\" .";

    private static final String DATA_OBJECT_FIELD_LOAD_ERROR = "It was not possible to create or load field: \"{0}\" for DataObject: \"{1}\" .";

    private static final String MODEL_LOAD_GENERIC_ERROR = "Unexpected error was produced when a DataModel was being loaded from the following path: \"{0}\" .";

    private static final String GENERIC_ERROR = "Unexpected error was produced.";

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
                        if (javaType.getSyntaxErrors() != null && !javaType.getSyntaxErrors().isEmpty()) {
                            //if a file has parsing errors it will be skipped.
                            addSyntaxErrors( result, scanResult.getFile(), javaType.getSyntaxErrors() );
                        } else {
                            DataObject dataObject;
                            try {
                                //try to load the data object.
                                dataObject = addDataObject( dataModel, (JavaClassSource)javaType );
                                if ( dataObject != null ) result.setClassPath( dataObject.getClassName(), scanResult.getFile() );
                            } catch (ModelDriverException e) {
                                logger.error( "An error was produced when file: " + scanResult.getFile() + " was being loaded into a DataObject.", e );
                                addModelDriverError(result , scanResult.getFile(), e );
                            }
                        }
                    } else {
                        logger.debug( "No Class definition was found for file: " + scanResult.getFile() + ", it will be skipped." );
                    }
                } catch ( Exception e ) {
                    //Unexpected parsing o model loading exception.
                    logger.error( errorMessage( MODEL_LOAD_GENERIC_ERROR, javaRootPath.toUri()), e );
                    throw new ModelDriverException( errorMessage( MODEL_LOAD_GENERIC_ERROR, javaRootPath.toUri()), e );
                }
            }
        }
        return result;
    }

    public ModelDriverResult loadDataObject(final String source, final Path path) throws ModelDriverException {

        ModelDriverResult result = new ModelDriverResult( );
        DataModel dataModel = createModel();
        result.setDataModel( dataModel );

        if ( source == null || "".equals( source ) ) {
            logger.debug( "source: " + source + " is empty." );
            return result;
        }

        try {
            JavaType<?> javaType = Roaster.parse( source );
            if ( javaType.isClass() ) {
                if (javaType.getSyntaxErrors() != null && !javaType.getSyntaxErrors().isEmpty()) {
                    //if a file has parsing errors it will be skipped.
                    addSyntaxErrors( result, path, javaType.getSyntaxErrors() );
                } else {
                    DataObject dataObject;
                    try {
                        //try to load the data object.
                        dataObject = addDataObject( dataModel, (JavaClassSource)javaType );
                        result.setClassPath( dataObject.getClassName(), path );
                    } catch (ModelDriverException e) {
                        logger.error( "An error was produced when source: " + source + " was being loaded into a DataObject.", e );
                        addModelDriverError(result , path, e );
                    }
                }
            } else {
                logger.debug( "No Class definition was found for source: " + source + ", it will be skipped." );
            }
        } catch ( Exception e ) {
            //Unexpected parsing o model loading exception.
            logger.error( errorMessage( MODEL_LOAD_GENERIC_ERROR, javaRootPath.toUri()), e );
            throw new ModelDriverException( errorMessage( MODEL_LOAD_GENERIC_ERROR, javaRootPath.toUri()), e );
        }

        return result;
    }

    public TypeInfoResult loadJavaTypeInfo(final String source) throws ModelDriverException {

        TypeInfoResult result = new TypeInfoResult( );

        if ( source == null || "".equals( source ) ) {
            logger.debug( "source: " + source + " is empty." );
            return result;
        }

        try {
            JavaType<?> javaType = Roaster.parse( source );
            if ( javaType.getSyntaxErrors() != null && !javaType.getSyntaxErrors().isEmpty()) {
                addSyntaxErrors( result, null, javaType.getSyntaxErrors() );
            } else {
                JavaTypeInfoImpl typeInfo = new JavaTypeInfoImpl();
                result.setTypeInfo( typeInfo );

                typeInfo.setName( javaType.getName() );
                typeInfo.setPackageName( javaType.getPackage() );
                typeInfo.setAnnotation( javaType.isAnnotation() );
                typeInfo.setClass( javaType.isClass() );
                typeInfo.setEnum( javaType.isEnum() );
                typeInfo.setInterface( javaType.isInterface() );
                typeInfo.setPackagePrivate( javaType.isPackagePrivate() );
                typeInfo.setPrivate( javaType.isPrivate() );
                typeInfo.setProtected( javaType.isProtected() );
                typeInfo.setInterface( javaType.isInterface() );
            }
        } catch ( Exception e ) {
            //Unexpected parsing o model loading exception.
            logger.error( errorMessage( GENERIC_ERROR, e ) );
            throw new ModelDriverException( errorMessage( MODEL_LOAD_GENERIC_ERROR ), e );
        }

        return result;
    }

    private void addModelDriverError( ModelDriverResult result, Path file, ModelDriverException e ) {
        ModelDriverError error;

        StringBuilder message = new StringBuilder( );
        message.append( e.getMessage() );
        Throwable cause = e.getCause();
        while ( cause != null ) {
            message.append( " : " );
            message.append( cause.getMessage() );
            if ( cause instanceof ModelDriverException ) {
                cause = cause.getCause();
            } else {
                cause = null;
            }
        }
        error = new ModelDriverError( message.toString(), file );
        result.addError( error );
    }

    private void addSyntaxErrors( DriverResult result, Path file, List<SyntaxError> syntaxErrors ) {
        ModelDriverError error;
        for (SyntaxError syntaxError : syntaxErrors) {
            error = new ModelDriverError( syntaxError.getDescription(), file);
            error.setLine( syntaxError.getLine() );
            error.setColumn( syntaxError.getColumn() );
            result.addError( error );
        }
    }

    private void addError( ModelDriverResult result, Path file, Exception e ) {
        ModelDriverError error = new ModelDriverError( e.getMessage(), file );
        result.addError( error );
    }

    @Override
    public DataModel createModel() {
        return ModelFactoryImpl.getInstance().newModel();
    }

    private DataObject addDataObject( DataModel dataModel, JavaClassSource javaClassSource ) throws ModelDriverException {

        String className;
        String packageName;
        String superClass;
        String qualifiedName;
        int modifiers;
        boolean hasErrors = false;
        DriverUtils driverUtils = DriverUtils.getInstance();
        ClassTypeResolver classTypeResolver;

        className = javaClassSource.getName();
        packageName = javaClassSource.getPackage();
        qualifiedName = NamingUtils.createQualifiedName( packageName, className );

        if ( logger.isDebugEnabled() ) {
            logger.debug( "Building DataObject for, packageName: " + packageName + ", className: " + className );
        }

        classTypeResolver = DriverUtils.getInstance().createClassTypeResolver( javaClassSource, classLoader );

        modifiers = driverUtils.buildModifierRepresentation( javaClassSource );

        DataObject dataObject = dataModel.addDataObject( packageName, className, modifiers );

        try {
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
        } catch ( ClassNotFoundException e) {
            hasErrors = true;
            logger.error( errorMessage( DATA_OBJECT_LOAD_ERROR, qualifiedName ), e );
            throw new ModelDriverException( errorMessage( DATA_OBJECT_LOAD_ERROR, qualifiedName ), e );
        } catch ( ModelDriverException e ) {
            hasErrors = true;
            logger.error( errorMessage( DATA_OBJECT_LOAD_ERROR, qualifiedName ), e );
            throw new ModelDriverException( errorMessage( DATA_OBJECT_LOAD_ERROR, qualifiedName ), e );
        } finally {
            if (hasErrors) dataModel.removeDataObject( qualifiedName );
        }
    }

    private boolean isManagedProperty( ObjectProperty property ) {
        return !property.isStatic() && !property.isFinal();
    }

    private List<ObjectProperty> filterManagedProperties( DataObject dataObject ) {
        List<ObjectProperty> result = new ArrayList<ObjectProperty>( );
        for ( ObjectProperty property : dataObject.getProperties().values() ) {
            if (isManagedProperty( property )) result.add( property );
        }
        return result;
    }

    private ObjectProperty addProperty( DataObject dataObject, FieldSource<JavaClassSource> field, ClassTypeResolver classTypeResolver ) throws ModelDriverException {
        ObjectProperty property = parseProperty( field, classTypeResolver );
        dataObject.addProperty( property );
        return property;
    }

    public ObjectProperty parseProperty( FieldSource<JavaClassSource> field, ClassTypeResolver classTypeResolver ) throws ModelDriverException {
        Type type;
        boolean multiple = false;
        String className;
        String bag = null;
        ObjectProperty property;
        int modifiers;
        DriverUtils driverUtils = DriverUtils.getInstance();

        modifiers = driverUtils.buildModifierRepresentation( field );

        try {
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

            property = new ObjectPropertyImpl(field.getName(), className, multiple, bag, modifiers);

            List<AnnotationSource<JavaClassSource>> annotations = field.getAnnotations();
            if ( annotations != null ) {
                for ( AnnotationSource annotation : annotations ) {
                    addPropertyAnnotation( property, annotation, classTypeResolver );
                }
            }
        } catch (ClassNotFoundException e) {
            logger.error( errorMessage( DATA_OBJECT_FIELD_LOAD_ERROR, field.getName(), field.getOrigin().getName() ), e );
            throw new ModelDriverException( errorMessage( DATA_OBJECT_FIELD_LOAD_ERROR, field.getName(), field.getOrigin().getName() ), e );
        }

        return property;
    }

    public List<ObjectProperty> parseManagedTypesProperties(JavaClassSource javaClassSource, ClassTypeResolver classTypeResolver) throws ModelDriverException {

        List<FieldSource<JavaClassSource>> fields = javaClassSource.getFields();
        DriverUtils driverUtils = DriverUtils.getInstance();
        List<ObjectProperty> properties = new ArrayList<ObjectProperty>(  );
        ObjectProperty property;

        for ( FieldSource<JavaClassSource> field : fields ) {
            if ( driverUtils.isManagedType( field.getType(), classTypeResolver ) ) {
                property = parseProperty( field, classTypeResolver );
                properties.add( property );
            } else {
                logger.debug( "field: " + field + "with fieldName: " + field.getName() + " won't be loaded by the diver because type: " + field.getType().getName() + " isn't a managed type." );
            }
        }
        return properties;
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

        try {
            String annotationClassName = resolveTypeName( classTypeResolver, annotationToken.getName() );

            AnnotationDefinition annotationDefinition = getConfiguredAnnotation( annotationClassName );
            Annotation annotation = null;

            if ( annotationDefinition != null ) {
                AnnotationDriver annotationDriver = getAnnotationDriver( annotationDefinition.getClassName() );
                if ( annotationDriver != null ) {
                    annotation = annotationDriver.buildAnnotation( annotationDefinition, annotationToken );
                } else {
                    logger.warn( "AnnotationDriver for annotation: " + annotationToken.getName() + " is not configured for this ModelDriver driver" );
                }
            } else {
                logger.warn( "Annotation: " + annotationToken.getName() + " is not configured for this ModelDriver driver." );
            }
            return annotation;
        } catch ( ClassNotFoundException e ) {
            logger.error( errorMessage( ANNOTATION_LOAD_ERROR, annotationToken.getName() ), e );
            throw new ModelDriverException( errorMessage( ANNOTATION_LOAD_ERROR, annotationToken.getName() ), e );
        }
    }

    private String resolveTypeName( ClassTypeResolver classTypeResolver, String name ) throws ClassNotFoundException {
        try {
            if ( NamingUtils.isQualifiedName( name ) ) {
                return name;
            } else {
                return classTypeResolver.getFullTypeName( name );
            }
        } catch ( ClassNotFoundException e ) {
            logger.error( "Class could not be resolved for name: " + name, e );
            throw e;
        }
    }

    public void updateImports(JavaClassSource javaClassSource, Map<String, String> renamedClasses, List<String> deletedClasses) {

        List<Import> imports = javaClassSource.getImports();
        String newClassName;
        String currentPackage = javaClassSource.isDefaultPackage() ? null : javaClassSource.getPackage();

        if (imports != null) {
            for (Import currentImport : imports) {
                if (!currentImport.isWildcard() && !currentImport.isStatic()) {
                    if ( (newClassName = renamedClasses.get( currentImport.getQualifiedName() ) ) != null ) {
                        javaClassSource.removeImport( currentImport );
                        if (!StringUtils.equals( currentPackage, NamingUtils.extractPackageName( newClassName ) )) {
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

        String oldSuperClassName = javaClassSource.getSuperType() != null ? resolveTypeName( classTypeResolver, javaClassSource.getSuperType() ) : null;

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
                currentAnnotationClassName = resolveTypeName( classTypeResolver, currentAnnotation.getName() );
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
            removeMethodByParamsClassName( javaClassSource, methodName );
            //add the new getter
            javaClassSource.addMethod( methodSource );

            //create setter
            methodSource = genTools.indent( engine.generateFieldSetterString( generationContext, property ) );
            methodName = genTools.toJavaSetter( property.getName() );

            //remove old setter if exists
            //TODO check collections

            //TODO aca tengo un problema cuando creo un Pojo en memoria y a su vez un field de ese tipo.
            //Porque intento resolver la clase con el classTypeResolver y el Pojo aun no ha sido creado con lo cual
            //tengo Class Not found exception.
            //Tengo que implementar el remove de otra forma para este caso, posiblemente iterando todos los metodos.
            //Cuando le cambio el tipo a un field de un pojo existente hacia un tipo de una clase creada en memoria
            //Crei que podria darse tambien esta exception pero parece que no.
            //Tengo que ver a ver porque no se da el error en este caso.

            //Class<?> fieldClass = classTypeResolver.resolveType( property.getClassName() );
            removeMethodByParamsClassName( javaClassSource, methodName, property.getClassName() );
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
            //needs to be removed and created again due to Roaster. Ideally it shouldn't be so.
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


                if (field.getLiteralInitializer() != null) {
                    //current field has an initializer, but the field type changed so we are not sure old initializer is
                    //valid for the new type.
                    if ( NamingUtils.isPrimitiveTypeId( newClassName )) {
                        setPrimitiveTypeDefaultInitializer( field, newClassName );
                    } else {
                        field.setLiteralInitializer( null );
                    }
                }
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
                removeMethodByParamsClass( javaClassSource, accessorName );

                accessorName = genTools.toJavaSetter( fieldName );
                removeMethodByParamsClass( javaClassSource, accessorName, oldClass );

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
            removeMethodByParamsClass( javaClassSource, accessorName );

            accessorName = genTools.toJavaSetter( fieldName );
            removeMethodByParamsClass( javaClassSource, accessorName, oldClass );

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

    public void updateConstructors(JavaClassSource javaClassSource,
            DataObject dataObject,
            List<MethodSource<JavaClassSource>> allFieldsConstructorCandidates,
            List<MethodSource<JavaClassSource>> keyFieldsConstructorCandidates,
            List<MethodSource<JavaClassSource>> positionFieldsConstructorCandidates,
            ClassTypeResolver classTypeResolver) throws Exception {

        GenerationContext generationContext = new GenerationContext( null );
        GenerationEngine engine = GenerationEngine.getInstance();
        GenerationTools genTools = new GenerationTools();
        JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver();

        boolean needsAllFieldsConstructor;
        boolean needsKeyFieldsConstructor;
        boolean needsPositionFieldsConstructor;
        boolean needsEmptyConstructor;

        String defaultConstructorSource;
        String allFieldsConstructorSource;
        String keyFieldsConstructorSource;
        String positionFieldsConstructorSource;
        String equalsMethodSource;
        String hashCodeMethodSource;

        //check if the candidate methods has exactly the same body of the generated by the data modeller.
        List<MethodSource<JavaClassSource>> currentAllFieldsConstructors = modelDriver.filterGeneratedConstructors( allFieldsConstructorCandidates );
        List<MethodSource<JavaClassSource>> currentKeyFieldsConstructors = modelDriver.filterGeneratedConstructors( keyFieldsConstructorCandidates );
        List<MethodSource<JavaClassSource>> currentPositionFieldsConstructors = modelDriver.filterGeneratedConstructors( positionFieldsConstructorCandidates );

        if ( logger.isDebugEnabled() ) {
            logger.debug( "allFieldsConstructorCandidates candidates: " + allFieldsConstructorCandidates.size() );
            logger.debug( allFieldsConstructorCandidates.size() > 0 ? allFieldsConstructorCandidates.get( 0 ).toString() : "" );
            logger.debug( "\n\n" );

            logger.debug("currentAllFieldsConstructors: " + currentAllFieldsConstructors.size());
            logger.debug( currentAllFieldsConstructors.size() > 0 ? currentAllFieldsConstructors.get( 0 ).toString() : "" );
            logger.debug( "\n\n" );

            logger.debug("KeyFieldsConstructorCandidates: " + keyFieldsConstructorCandidates.size());
            logger.debug( keyFieldsConstructorCandidates.size() > 0 ? keyFieldsConstructorCandidates.get( 0 ).toString() : "" );
            logger.debug( "\n\n" );

            logger.debug("currentKeyFieldsConstructors: " + currentKeyFieldsConstructors.size());
            logger.debug( currentKeyFieldsConstructors.size() > 0 ? currentKeyFieldsConstructors.get( 0 ).toString() : "" );
            logger.debug( "\n\n" );

            logger.debug( "positionFieldsConstructorCandidates: " + positionFieldsConstructorCandidates.size());
            logger.debug( positionFieldsConstructorCandidates.size() > 0 ? positionFieldsConstructorCandidates.get( 0 ).toString() : "" );
            logger.debug( "\n\n" );

            logger.debug( "currentPositionFieldsConstructors: " + currentPositionFieldsConstructors.size());
            logger.debug( currentPositionFieldsConstructors.size() > 0 ? currentPositionFieldsConstructors.get( 0 ).toString() : "" );
            logger.debug( "\n\n" );
        }

        //delete current data modeller generated all fields, key fields, and position fields constructors if there are any.
        for ( MethodSource<JavaClassSource> constructor : currentAllFieldsConstructors ) {
            javaClassSource.removeMethod( constructor );
        }
        for ( MethodSource<JavaClassSource> constructor : currentKeyFieldsConstructors ) {
            javaClassSource.removeMethod( constructor );
        }
        for ( MethodSource<JavaClassSource> constructor : currentPositionFieldsConstructors ) {
            javaClassSource.removeMethod( constructor );
        }

        //calculate the file order for the fields.
        List<FieldSource<JavaClassSource>> fields = javaClassSource.getFields();
        if ( fields != null && fields.size() > 0 ) {
            int fileOrder = 0;
            for ( FieldSource<JavaClassSource> field : fields ) {
                ObjectPropertyImpl objectProperty = (ObjectPropertyImpl)dataObject.getProperties().get( field.getName() );
                if ( objectProperty != null ) {
                    objectProperty.setFileOrder( fileOrder );
                }
                fileOrder++;
            }
        }

        //get the sorted list of all fields, position annotated and key annotated fields. These lists will be used
        //to identify collisions with client provided constructors.
        List<ObjectProperty> allFields = DataModelUtils.sortByFileOrder( DataModelUtils.filterAssignableFields( dataObject ) );
        List<ObjectProperty> positionFields = DataModelUtils.sortByPosition( DataModelUtils.filterPositionFields( dataObject ) );
        List<ObjectProperty> keyFields = DataModelUtils.sortByFileOrder( DataModelUtils.filterKeyFields( dataObject ) );

        needsEmptyConstructor = true; //we always wants to generate the default constructor.
        needsAllFieldsConstructor = allFields.size() > 0;
        needsPositionFieldsConstructor = positionFields.size() > 0 &&
                !DataModelUtils.equalsByFieldName( allFields, positionFields ) &&
                !DataModelUtils.equalsByFieldType( allFields, positionFields );

        needsKeyFieldsConstructor = keyFields.size() > 0 &&
                !DataModelUtils.equalsByFieldName( allFields, keyFields ) &&
                !DataModelUtils.equalsByFieldType( allFields, keyFields ) &&
                !DataModelUtils.equalsByFieldName( positionFields, keyFields ) &&
                !DataModelUtils.equalsByFieldType( positionFields, keyFields );

        List<MethodSource<JavaClassSource>> currentConstructors = new ArrayList<MethodSource<JavaClassSource>>( );
        MethodSource<JavaClassSource> currentEquals = null;
        MethodSource<JavaClassSource> currentHashCode = null;
        MethodSource<JavaClassSource> newConstructor;

        //Iterate remaining methods looking for client provided constructors, hashCode and equals methods.
        List<MethodSource<JavaClassSource>> methods = javaClassSource.getMethods();
        if ( methods != null ) {
            for ( MethodSource<JavaClassSource> method : methods ) {
                if (method.isConstructor()) {
                    currentConstructors.add( method );
                    if ( method.getParameters() == null || method.getParameters().size() == 0 ) {
                        needsEmptyConstructor = false;
                    }
                } else if (isEquals( method )) {
                    currentEquals = method;
                } else if (isHashCode( method )) {
                    currentHashCode = method;
                }
            }
        }

        //check collisions with remaining constructors first.
        needsAllFieldsConstructor = needsAllFieldsConstructor && ( findMatchingConstructorsByTypes( javaClassSource, allFields, classTypeResolver ).size() == 0 );
        needsPositionFieldsConstructor = needsPositionFieldsConstructor && ( findMatchingConstructorsByTypes( javaClassSource, positionFields, classTypeResolver ).size() == 0 );
        needsKeyFieldsConstructor = needsKeyFieldsConstructor && ( findMatchingConstructorsByTypes( javaClassSource, keyFields, classTypeResolver ).size() == 0 );

        //remove current equals and hashCode methods
        if (currentEquals != null) javaClassSource.removeMethod( currentEquals );
        if (currentHashCode != null) javaClassSource.removeMethod( currentHashCode );

        //finally create the needed constructors

        if ( needsEmptyConstructor ) {
            defaultConstructorSource = genTools.indent( engine.generateDefaultConstructorString( generationContext, dataObject ) );
            newConstructor = javaClassSource.addMethod( defaultConstructorSource );
            newConstructor.setConstructor( true );
        }

        if ( needsAllFieldsConstructor ) {
            allFieldsConstructorSource = genTools.indent( engine.generateAllFieldsConstructorString( generationContext, dataObject ) );
            if ( allFieldsConstructorSource != null && !allFieldsConstructorSource.trim().isEmpty() ) {
                newConstructor = javaClassSource.addMethod( allFieldsConstructorSource );
                newConstructor.setConstructor( true );
            }
        }

        if ( needsPositionFieldsConstructor ) {
            positionFieldsConstructorSource = genTools.indent( engine.generatePositionFieldsConstructorString( generationContext, dataObject ) );
            if ( positionFieldsConstructorSource != null && !positionFieldsConstructorSource.trim().isEmpty() ) {
                newConstructor = javaClassSource.addMethod( positionFieldsConstructorSource );
                newConstructor.setConstructor( true );
            }
        }
        if ( needsKeyFieldsConstructor ) {
            keyFieldsConstructorSource = genTools.indent( engine.generateKeyFieldsConstructorString( generationContext, dataObject ) );
            if ( keyFieldsConstructorSource != null && !keyFieldsConstructorSource.trim().isEmpty() ) {
                newConstructor = javaClassSource.addMethod( keyFieldsConstructorSource );
                newConstructor.setConstructor( true );
            }
        }
        if ( keyFields.size() > 0 ) {
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
            removeMethodByParamsClass( javaClassSource, methodName );

            methodName = genTools.toJavaSetter( fieldName );
            removeMethodByParamsClass( javaClassSource, methodName, fieldClass );

            //finally remove the field.
            javaClassSource.removeField( field );

        }  else {
            logger.debug( "Field field: " + fieldName + " was not found in class: " + javaClassSource.getName() );
        }
    }

    public void removeMethodByParamsClass( JavaClassSource javaClassSource, String methodName, Class<?>... paramTypes ) {
        logger.debug( "Removing method: " + methodName + ", form class: " + javaClassSource.getName() );
        MethodSource<JavaClassSource> method = javaClassSource.getMethod( methodName, paramTypes );
        if (method != null) {
            javaClassSource.removeMethod( method );
            logger.debug( "Method method: " + methodName + ", was removed from class: " + javaClassSource.getName() );
        } else {
            logger.debug( "Method method: " + methodName + " not exists for class: " + javaClassSource.getName() );
        }
    }

    public void removeMethodByParamsClassName( JavaClassSource javaClassSource, String methodName, String... paramTypes ) {
        logger.debug( "Removing method: " + methodName + ", form class: " + javaClassSource.getName() );
        MethodSource<JavaClassSource> method = javaClassSource.getMethod( methodName, paramTypes );
        if (method != null) {
            javaClassSource.removeMethod( method );
            logger.debug( "Method method: " + methodName + ", was removed from class: " + javaClassSource.getName() );
        } else {
            logger.debug( "Method method: " + methodName + " not exists for class: " + javaClassSource.getName() );
        }
    }

    public List<MethodSource<JavaClassSource>> findAllFieldsConstructorCandidates(JavaClassSource javaClassSource, List<ObjectProperty> properties, ClassTypeResolver classTypeResolver) {
        return findMatchingConstructorsByParameters( javaClassSource, properties, classTypeResolver );
    }

    public List<MethodSource<JavaClassSource>> findKeyFieldsConstructorCandidates(JavaClassSource javaClassSource, List<ObjectProperty> properties, ClassTypeResolver classTypeResolver) {
        List<ObjectProperty> keyFields = DataModelUtils.filterKeyFields( properties );
        return findMatchingConstructorsByParameters( javaClassSource, keyFields, classTypeResolver );
    }

    public List<MethodSource<JavaClassSource>> findPositionFieldsConstructorCandidates(JavaClassSource javaClassSource, List<ObjectProperty> properties, ClassTypeResolver classTypeResolver) {
        List<ObjectProperty> positionalFields = DataModelUtils.filterPositionFields( properties );
        return findMatchingConstructorsByParameters( javaClassSource, DataModelUtils.sortByPosition( positionalFields ), classTypeResolver );
    }

    public List<MethodSource<JavaClassSource>> findMatchingConstructorsByParameters(JavaClassSource javaClassSource, List<ObjectProperty> properties, ClassTypeResolver classTypeResolver) {
        List<MethodSource<JavaClassSource>> result = new ArrayList<MethodSource<JavaClassSource>>(  );
        List<MethodSource<JavaClassSource>> constructors  = getConstructors( javaClassSource );
        for ( MethodSource<JavaClassSource> constructor : constructors ) {
            List<ParameterSource<JavaClassSource>> parameters = constructor.getParameters();
            if ( parameters == null || parameters.size() == 0 || parameters.size() != properties.size() ) continue;
            int unmatchedParams = parameters.size();
            int paramIndex = 0;
            for ( ParameterSource<JavaClassSource> param : parameters ) {
                if ( paramMatchesWithProperty(param, properties.get( paramIndex ), classTypeResolver) ) {
                    unmatchedParams--;
                    //TODO optimize to not visit all parameters, now I want to visit them all by intention
                }
                paramIndex++;
            }
            if ( unmatchedParams == 0 ) {
                result.add( constructor );
            }
        }
        return result;
    }

    public List<MethodSource<JavaClassSource>> findMatchingConstructorsByTypes(JavaClassSource javaClassSource, List<ObjectProperty> properties, ClassTypeResolver classTypeResolver) {
        List<MethodSource<JavaClassSource>> result = new ArrayList<MethodSource<JavaClassSource>>(  );
        List<MethodSource<JavaClassSource>> constructors  = getConstructors( javaClassSource );
        for ( MethodSource<JavaClassSource> constructor : constructors ) {
            List<ParameterSource<JavaClassSource>> parameters = constructor.getParameters();
            if ( parameters == null || parameters.size() == 0 || parameters.size() != properties.size() ) continue;
            int unmatchedParams = parameters.size();
            int paramIndex = 0;
            for ( ParameterSource<JavaClassSource> param : parameters ) {
                if ( paramMatchesWithPropertyType(param, properties.get( paramIndex ), classTypeResolver) ) {
                    unmatchedParams--;
                } else {
                    break;
                }
                paramIndex++;
            }
            if ( unmatchedParams == 0 ) {
                result.add( constructor );
            }
        }
        return result;
    }

    public boolean paramMatchesWithProperty(ParameterSource<JavaClassSource> param, ObjectProperty property, ClassTypeResolver classTypeResolver) {
        if ( !param.getName().equals( property.getName() )) return false;
        DriverUtils driverUtils = DriverUtils.getInstance();
        try {
            return driverUtils.equalsType( param.getType(), property.getClassName(), property.isMultiple(), property.getBag(), classTypeResolver );
        } catch (Exception e) {
            //TODO check if we need to propagate this exception.
            logger.error( "An error was produced on parameter matching test with param: " + param.getName() + " and field: " + property.getName(), e );
            return false;
        }
    }

    public boolean paramMatchesWithPropertyType(ParameterSource<JavaClassSource> param, ObjectProperty property, ClassTypeResolver classTypeResolver) {
        DriverUtils driverUtils = DriverUtils.getInstance();
        try {
            return driverUtils.equalsType( param.getType(), property.getClassName(), property.isMultiple(), property.getBag(), classTypeResolver );
        } catch (Exception e) {
            //TODO check if we need to propagate this exception.
            logger.error( "An error was produced on parameter matching test with param: " + param.getName() + " and field: " + property.getName(), e );
            return false;
        }
    }

    public List<MethodSource<JavaClassSource>> filterGeneratedConstructors(List<MethodSource<JavaClassSource>> constructors) {
        List<MethodSource<JavaClassSource>> result = new ArrayList<MethodSource<JavaClassSource>>();
        if ( constructors != null ) {
            for ( MethodSource<JavaClassSource> constructor : constructors ) {
                if ( isGeneratedConstructor( constructor ) ) result.add( constructor );
            }
        }
        return result;
    }

    /**
     *
     * @param constructor a Constructor method to check.
     *
     * @return true, if the given constructor was generated by the data modeler.
     */
    public boolean isGeneratedConstructor(MethodSource<JavaClassSource> constructor) {
        if ( constructor.isAbstract() || constructor.isStatic() || constructor.isFinal() ) return false;
        if ( !constructor.isPublic() ) return false; //we only generate public constructors.

        if ( constructor.getAnnotations() != null && constructor.getAnnotations().size() > 0) return false; //we never add annotations to constructors

        List<ParameterSource<JavaClassSource>> parameters = constructor.getParameters();
        List<String> expectedBody = new ArrayList<String>();
        List<String> expectedLines = new ArrayList<String>();
        String expectedLine;
        if ( parameters != null ) {
            for ( ParameterSource<JavaClassSource> param : parameters ) {
                if ( param.getAnnotations() != null && param.getAnnotations().size() > 0 ) return false; //we never add annotations to parameters
                //ideally we should know if the parameter is final, but Roaster don't provide that info.
                expectedLine = "this." + param.getName() + "=" + param.getName() + ";";
                expectedLines.add( expectedLine );
            }
        }

        String body = constructor.getBody();
        if ( body == null || ( body = body.trim() ).isEmpty() ) return false;

        try {
            BufferedReader reader = new BufferedReader( new StringReader( body ));
            String line = null;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if ( lineNumber > expectedLines.size() ) return false;
                if (!line.trim().equals( expectedLines.get( lineNumber-1 ) )) return false;
            }

            return lineNumber == expectedLines.size();

        } catch (IOException e) {
            return false;
        }
    }

    public List<MethodSource<JavaClassSource>> getConstructors(JavaClassSource javaClassSource) {
        List<MethodSource<JavaClassSource>> constructors = new ArrayList<MethodSource<JavaClassSource>>(  );
        List<MethodSource<JavaClassSource>> methods = javaClassSource.getMethods();
        if ( methods != null ) {
            for ( MethodSource<JavaClassSource> method : methods ) {
                if ( method.isConstructor() ) {
                    constructors.add( method );
                }
            }
        }
        return constructors;
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
        return method.getName().equals( "equals" ) &&
                ( method.getParameters() == null || method.getParameters().size() == 1 ) &&
                method.getReturnType() != null &&
                method.getReturnType().isPrimitive() &&
                "boolean".equals( method.getReturnType().getName() );
    }

    public boolean isHashCode(MethodSource<?> method) {
        return method.getName().equals( "hashCode" ) &&
                ( method.getParameters() == null || method.getParameters().size() == 0 ) &&
                method.getReturnType() != null &&
                method.getReturnType().isPrimitive() &&
                "int".equals( method.getReturnType().getName() );
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
        String annotationClassName = resolveTypeName( classTypeResolver, annotation.getName() );
        return getConfiguredAnnotation( annotationClassName ) != null;
    }

    private String errorMessage(String message, Object ... params) {
        return MessageFormat.format( message, params );
    }

    private void verifyPositions( DataObject dataObject,
            List<PropertyPosition> naturalOrderPositions ) {

        //1) check if all fields has position and all positions are consumed
        HashMap<String, String> availablePositions = new HashMap<String, String>();
        int i = 0;
        List<ObjectProperty> managedProperties = filterManagedProperties( dataObject );

        for ( ObjectProperty property : managedProperties ) {
            availablePositions.put( String.valueOf( i ), "" );
            i++;
        }

        boolean recalculate = false;
        org.kie.workbench.common.services.datamodeller.core.Annotation position;
        for ( ObjectProperty property : managedProperties ) {
            position = property.getAnnotation( PositionAnnotationDefinition.getInstance().getClassName() );
            if ( position == null ) {
                //the position is missing for at least one field.
                recalculate = true;
                break;
            } else {
                String value = (String) position.getValue( "value" );
                if ( value != null ) {
                    availablePositions.remove( value.trim() );
                }
            }
        }

        org.kie.workbench.common.services.datamodeller.core.Annotation desiredPosition;
        List<PropertyPosition> desiredPositions = new ArrayList<PropertyPosition>();

        if ( recalculate || availablePositions.size() > 0 ) {
            //we need to recalculate positions.
            for ( PropertyPosition propertyPosition : naturalOrderPositions ) {
                desiredPosition = propertyPosition.property.removeAnnotation( PositionAnnotationDefinition.getInstance().getClassName() );
                if ( desiredPosition != null ) {
                    desiredPositions.add( new PropertyPosition( propertyPosition.property, desiredPosition ) );
                }
                propertyPosition.property.addAnnotation( propertyPosition.position );
            }
            recalculatePositions( dataObject, desiredPositions );
        }
    }

    private void recalculatePositions( DataObject dataObject,
            List<PropertyPosition> desiredPositions ) {

        Collection<ObjectProperty> properties = filterManagedProperties( dataObject );
        org.kie.workbench.common.services.datamodeller.core.Annotation currentPosition;

        for ( PropertyPosition desiredPosition : desiredPositions ) {
            ObjectProperty property = dataObject.getProperties().get( desiredPosition.property.getName() );
            currentPosition = property.getAnnotation( PositionAnnotationDefinition.getInstance().getClassName() );
            recalculatePositions( properties, currentPosition, desiredPosition.position );
        }
    }

    private void recalculatePositions( Collection<ObjectProperty> properties,
            org.kie.workbench.common.services.datamodeller.core.Annotation oldPositionAnnotaion,
            org.kie.workbench.common.services.datamodeller.core.Annotation newPositionAnnotation ) {

        Integer newPosition;
        Integer oldPosition;
        Integer maxPosition = properties.size() - 1;

        try {
            oldPosition = Integer.parseInt( (String) oldPositionAnnotaion.getValue( "value" ) );
        } catch ( NumberFormatException e ) {
            //the old position is calculated by construction. This case is not possible.
            return;
        }

        try {
            newPosition = Integer.parseInt( (String) newPositionAnnotation.getValue( "value" ) );
        } catch ( NumberFormatException e ) {
            //if the value for the desired annotation is not valid it has no sence to continue.
            return;
        }

        if ( newPosition < 0 ) {
            newPosition = 0;
        }
        if ( newPosition > maxPosition ) {
            newPosition = maxPosition;
        }

        if ( newPosition == oldPosition ) {
            return;
        }

        org.kie.workbench.common.services.datamodeller.core.Annotation propertyPositionAnnotation;
        int propertyPosition;
        for ( ObjectProperty property : properties ) {

            propertyPositionAnnotation = property.getAnnotation( PositionAnnotationDefinition.getInstance().getClassName() );
            propertyPosition = Integer.parseInt( (String) propertyPositionAnnotation.getValue( "value" ) );

            if ( newPosition < oldPosition ) {
                if ( propertyPosition >= newPosition && propertyPosition < oldPosition ) {
                    propertyPositionAnnotation.setValue( "value", Integer.valueOf( propertyPosition + 1 ).toString() );
                }
            } else {
                if ( propertyPosition <= newPosition && propertyPosition > oldPosition ) {
                    propertyPositionAnnotation.setValue( "value", Integer.valueOf( propertyPosition - 1 ).toString() );
                }
            }

            if ( propertyPosition == oldPosition ) {
                propertyPositionAnnotation.setValue( "value", newPosition.toString() );
            }

        }

    }

    static class PropertyPosition {

        ObjectProperty property;
        org.kie.workbench.common.services.datamodeller.core.Annotation position;

        PropertyPosition( ObjectProperty property,
                org.kie.workbench.common.services.datamodeller.core.Annotation position ) {
            this.property = property;
            this.position = position;
        }
    }

    public static class ModelFilter {

        boolean includeFields = true;

    }

}
