/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Set;
import java.util.StringTokenizer;

import org.drools.workbench.models.datamodel.oracle.Annotation;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.datamodeller.driver.*;
import org.kie.workbench.common.services.datamodeller.driver.impl.annotations.*;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.OpenOption;
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationContext;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationEngine;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationListener;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.ObjectSource;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ModelFactoryImpl;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataModelOracleModelDriver implements ModelDriver {

    private static final Logger logger = LoggerFactory.getLogger( DataModelOracleModelDriver.class );

    private List<AnnotationDefinition> configuredAnnotations = new ArrayList<AnnotationDefinition>();

    private Map<String, AnnotationDriver> annotationDrivers = new HashMap<String, AnnotationDriver>();

    private ProjectDataModelOracle oracleDataModel;

    private ClassLoader projectClassLoader;

    public static DataModelOracleModelDriver getInstance() {
        return new DataModelOracleModelDriver();
    }

    public static DataModelOracleModelDriver getInstance(ProjectDataModelOracle oracleDataModel, ClassLoader projectClassLoader) {
        return new DataModelOracleModelDriver(oracleDataModel, projectClassLoader);
    }

    protected DataModelOracleModelDriver( ProjectDataModelOracle oracleDataModel, ClassLoader projectClassLoader ) {
        this();
        this.oracleDataModel = oracleDataModel;
        this.projectClassLoader = projectClassLoader;
    }

    protected DataModelOracleModelDriver() {
        configuredAnnotations.addAll( CommonAnnotations.getCommonAnnotations() );
        for (AnnotationDefinition annotationDefinition : configuredAnnotations) {
            annotationDrivers.put( annotationDefinition.getClassName(), new DefaultDataModelOracleAnnotationDriver() );
        }
    }

    @Override
    public List<AnnotationDefinition> getConfiguredAnnotations() {
        return configuredAnnotations;
    }

    @Override
    public AnnotationDefinition getConfiguredAnnotation( String annotationClassName ) {
        for ( AnnotationDefinition annotationDefinition : configuredAnnotations ) {
            if ( annotationClassName.equals( annotationDefinition.getClassName() ) ) {
                return annotationDefinition;
            }
        }
        return null;
    }

    @Override
    public AnnotationDriver getAnnotationDriver( String annotationClassName ) {
        return annotationDrivers.get( annotationClassName );
    }

    @Override
    public void generateModel( DataModel dataModel, ModelDriverListener generationListener) throws Exception {

        GenerationContext generationContext = new GenerationContext( dataModel );
        generationContext.setGenerationListener( generationListener );
        GenerationEngine generationEngine = GenerationEngine.getInstance();
        generationEngine.generate( generationContext );
    }

    @Override
    public DataModel createModel() {
        return ModelFactoryImpl.getInstance().newModel();
    }

    @Override public DataModel loadModel( ) throws ModelDriverException {
        return loadModel( oracleDataModel, projectClassLoader );
    }

    public DataModel loadModel( ProjectDataModelOracle oracleDataModel, ClassLoader projectClassLoader ) throws ModelDriverException {

        DataModel dataModel = createModel();

        logger.debug( "Adding oracleDataModel: " + oracleDataModel + " to dataModel: " + dataModel );

        String[] factTypes = DataModelOracleUtilities.getFactTypes( oracleDataModel );
        ObjectSource source = null;

        if ( factTypes != null && factTypes.length > 0 ) {
            for ( int i = 0; i < factTypes.length; i++ ) {
                //skip .drl declared fact types.
                source = factSource( oracleDataModel, factTypes[ i ] );
                if ( source != null && ( ObjectSource.INTERNAL.equals( source ) || ObjectSource.DEPENDENCY.equals( source ) ) ) {
                    addFactType( dataModel, oracleDataModel, factTypes[ i ], source, projectClassLoader );
                }
            }
        } else {
            logger.debug( "oracleDataModel hasn't defined fact types" );
        }
        return dataModel;
    }

    private void addFactType( DataModel dataModel,
                              ProjectDataModelOracle oracleDataModel,
                              String factType,
                              ObjectSource source,
                              ClassLoader classLoader) throws ModelDriverException {

        String packageName = NamingUtils.getInstance().extractPackageName( factType );
        String className = NamingUtils.getInstance().extractClassName( factType );
        String superClass = DataModelOracleUtilities.getSuperType( oracleDataModel, factType );
        DataObject dataObject;

        logger.debug( "Adding factType: " + factType + ", to dataModel: " + dataModel + ", from oracleDataModel: " + oracleDataModel );
        ClassMetadata classMetadata = readClassMetadata(factType, classLoader);

        if (classMetadata != null && !classMetadata.isMemberClass() && !classMetadata.isAnonymousClass() && !classMetadata.isLocalClass() ) {
            dataObject = dataModel.addDataObject( factType, source, classMetadata.getModifiers() );
            dataObject.setSuperClassName( superClass );

            //process type annotations
            Set<Annotation> typeAnnotations = DataModelOracleUtilities.getTypeAnnotations( oracleDataModel,
                                                                                           factType );
            if ( typeAnnotations != null ) {
                for ( Annotation annotation : typeAnnotations ) {
                    addFactTypeAnnotation( dataObject, annotation );
                }
            }

            Map<String, ModelField[]> fields = oracleDataModel.getProjectModelFields();
            if ( fields != null ) {
                ModelField[] factFields = fields.get( factType );
                ModelField field;
                ObjectProperty property;
                Map<String, Set<Annotation>> typeFieldsAnnotations = DataModelOracleUtilities.getTypeFieldsAnnotations( oracleDataModel,
                                                                                                                        factType );
                Set<Annotation> fieldAnnotations;
                Integer naturalOrder = 0;
                List<PropertyPosition> naturalOrderPositions = new ArrayList<PropertyPosition>();

                if ( factFields != null && factFields.length > 0 ) {
                    for ( int j = 0; j < factFields.length; j++ ) {
                        field = factFields[ j ];
                        if ( isLoadableField( field ) ) {

                            if ( field.getType().equals( "Collection" ) ) {
                                //particular processing for collection types
                                //read the correct bag and item classes.
                                String bag = DataModelOracleUtilities.getFieldClassName( oracleDataModel,
                                                                                         factType,
                                                                                         field.getName() );
                                String itemsClass = DataModelOracleUtilities.getParametricFieldType( oracleDataModel,
                                                                                                     factType,
                                                                                                     field.getName() );
                                if (itemsClass == null) {
                                    //if we don't know the items class, the property will be managed as a simple property.
                                    property = dataObject.addProperty( field.getName(), bag );
                                } else {
                                    property = dataObject.addProperty( field.getName(), itemsClass, true, bag );
                                }

                            } else {
                                property = dataObject.addProperty( field.getName(), getFieldType( oracleDataModel, packageName, field.getClassName() ) );
                            }

                            //process property annotations
                            if ( typeFieldsAnnotations != null && ( fieldAnnotations = typeFieldsAnnotations.get( field.getName() ) ) != null ) {
                                for ( Annotation fieldAnnotation : fieldAnnotations ) {
                                    addFieldAnnotation( dataObject, property, fieldAnnotation );
                                }
                            }

                            AnnotationImpl position = new AnnotationImpl( PositionAnnotationDefinition.getInstance() );
                            position.setValue( "value", naturalOrder.toString() );
                            naturalOrderPositions.add( new PropertyPosition( property, position ) );
                            naturalOrder++;
                        }
                    }
                    verifyPositions( dataObject, naturalOrderPositions );
                }
            } else {
                logger.debug( "No fields for factTye: " + factType );
            }
        }
    }

    private ClassMetadata readClassMetadata(String factType, ClassLoader classLoader) {
        try {
            Class _class = classLoader.loadClass(factType);
            return new ClassMetadata(_class.getModifiers(), _class.isMemberClass(), _class.isLocalClass(), _class.isAnonymousClass());
        } catch (ClassNotFoundException e) {
            logger.error("It was not possible to read class metadata for class: " + factType);
        }
        return null;
    }

    private void verifyPositions( DataObject dataObject,
                                  List<PropertyPosition> naturalOrderPositions ) {

        //1) check if all fields has position and all positions are consumed
        HashMap<String, String> availablePositions = new HashMap<String, String>();
        for ( int i = 0; i < dataObject.getProperties().size(); i++ ) {
            availablePositions.put( String.valueOf( i ), "" );
        }

        boolean recalculate = false;
        org.kie.workbench.common.services.datamodeller.core.Annotation position;
        for ( ObjectProperty property : dataObject.getProperties().values() ) {
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

        Collection<ObjectProperty> properties = dataObject.getProperties().values();
        org.kie.workbench.common.services.datamodeller.core.Annotation currentPosition, naturalOrder;

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

    private void addFactTypeAnnotation( DataObject dataObject,
                                        Annotation annotationToken ) throws ModelDriverException {
        org.kie.workbench.common.services.datamodeller.core.Annotation annotation = createAnnotation( annotationToken );
        if ( annotation != null ) {
            dataObject.addAnnotation( annotation );
        }
    }

    private void addFieldAnnotation( DataObject dataObject,
                                     ObjectProperty property,
                                     Annotation annotationToken ) throws ModelDriverException {
        org.kie.workbench.common.services.datamodeller.core.Annotation annotation = createAnnotation( annotationToken );
        if ( annotation != null ) {
            property.addAnnotation( annotation );
        }
    }

    private org.kie.workbench.common.services.datamodeller.core.Annotation createAnnotation( Annotation annotationToken ) throws ModelDriverException {

        AnnotationDefinition annotationDefinition = getConfiguredAnnotation(annotationToken.getQualifiedTypeName());
        org.kie.workbench.common.services.datamodeller.core.Annotation annotation = null;

        if ( annotationDefinition != null ) {
            AnnotationDriver annotationDriver = getAnnotationDriver( annotationDefinition.getClassName() );
            if ( annotationDriver != null ) {
                annotation = annotationDriver.buildAnnotation( annotationDefinition, annotationToken );
            } else {
                logger.warn( "AnnotationDriver for annotation: " + annotationToken.getQualifiedTypeName() + " is not configured for this driver" );
            }
        } else {
            logger.warn( "Annotation: " + annotationToken.getQualifiedTypeName() + " is not configured for this driver." );
        }
        return annotation;
    }

    private String getFieldType( ProjectDataModelOracle oracleDataModel,
                                 String packageName,
                                 String fieldType ) {
        return fieldType;
    }

    /**
     * True if the given fact type is a DataObject.
     */
    private ObjectSource factSource( ProjectDataModelOracle oracleDataModel,
                                     String factType ) {
        TypeSource oracleType = DataModelOracleUtilities.getTypeSource( oracleDataModel,
                                                                        factType );
        // for testing if (factType.startsWith("test")) return ObjectSource.DEPENDENCY;

        if ( TypeSource.JAVA_PROJECT.equals( oracleType ) ) {
            return ObjectSource.INTERNAL;
        } else if ( TypeSource.JAVA_DEPENDENCY.equals( oracleType ) ) {
            return ObjectSource.DEPENDENCY;
        }
        return null;
    }

    /**
     * Indicates if this field should be loaded or not.
     * Some fields like a filed with name "this" shouldn't be loaded.
     */
    private boolean isLoadableField( ModelField field ) {
        return ( field.getOrigin().equals( ModelField.FIELD_ORIGIN.DECLARED ) );
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

    static class OracleGenerationListener implements GenerationListener {

        org.uberfire.java.nio.file.Path output;

        IOService ioService;

        OpenOption option;

        List<FileChangeDescriptor> fileChanges = new ArrayList<FileChangeDescriptor>();

        public OracleGenerationListener( IOService ioService,
                                         org.uberfire.java.nio.file.Path output,
                                         OpenOption option ) {
            this.ioService = ioService;
            this.output = output;
            this.option = option;
        }

        @Override
        public void assetGenerated( String fileName,
                                    String content ) {

            String subDir;
            org.uberfire.java.nio.file.Path subDirPath;
            org.uberfire.java.nio.file.Path destFilePath;
            StringTokenizer dirNames;

            subDirPath = output;
            int index = fileName.lastIndexOf( "/" );
            if ( index == 0 ) {
                //the file names was provided in the form /SomeFile.java
                fileName = fileName.substring( 1, fileName.length() );
            } else if ( index > 0 ) {
                //the file name was provided in the most common form /dir1/dir2/SomeFile.java
                String dirNamesPath = fileName.substring( 0, index );
                fileName = fileName.substring( index + 1, fileName.length() );
                dirNames = new StringTokenizer( dirNamesPath, "/" );
                while ( dirNames.hasMoreElements() ) {
                    subDir = dirNames.nextToken();
                    subDirPath = subDirPath.resolve( subDir );
                    if ( !ioService.exists( subDirPath ) ) {
                        ioService.createDirectory( subDirPath );
                    }
                }
            }

            //the last subDirPath is the directory to crate the file.
            destFilePath = subDirPath.resolve( fileName );
            boolean exists = ioService.exists( destFilePath );

            ioService.write( destFilePath,
                             content,
                             option );

            if ( !exists ) {
                if ( logger.isDebugEnabled() ) {
                    logger.debug( "Genertion listener created a new file: " + destFilePath );
                }
                fileChanges.add( new FileChangeDescriptor( destFilePath, FileChangeDescriptor.ADD ) );
            } else {
                if ( logger.isDebugEnabled() ) {
                    logger.debug( "Generation listener modified file: " + destFilePath );
                }
                fileChanges.add( new FileChangeDescriptor( destFilePath, FileChangeDescriptor.UPDATE ) );
            }
        }

        public List<FileChangeDescriptor> getFileChanges() {
            return fileChanges;
        }
    }

    public class ClassMetadata {

        int modifiers;

        boolean memberClass;

        boolean localClass;

        boolean anonymousClass;

        public ClassMetadata(int modifiers, boolean memberClass, boolean localClass, boolean anonymousClass) {
            this.modifiers = modifiers;
            this.memberClass = memberClass;
            this.localClass = localClass;
            this.anonymousClass = anonymousClass;
        }

        public int getModifiers() {
            return modifiers;
        }

        public void setModifiers(int modifiers) {
            this.modifiers = modifiers;
        }

        public boolean isMemberClass() {
            return memberClass;
        }

        public void setMemberClass(boolean memberClass) {
            this.memberClass = memberClass;
        }

        public boolean isLocalClass() {
            return localClass;
        }

        public void setLocalClass(boolean localClass) {
            this.localClass = localClass;
        }

        public boolean isAnonymousClass() {
            return anonymousClass;
        }

        public void setAnonymousClass(boolean anonymousClass) {
            this.anonymousClass = anonymousClass;
        }
    }
}