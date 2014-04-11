package org.kie.workbench.common.services.datamodeller.driver.impl;

import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.datamodel.oracle.Annotation;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.ObjectSource;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectDataModelOracleUtils {

    private static final Logger logger = LoggerFactory.getLogger( ProjectDataModelOracleUtils.class );

    public static void loadExternalDependencies(DataModel dataModel, ProjectDataModelOracle projectDataModelOracle, ClassLoader classLoader) throws ModelDriverException {

        String[] factTypes = DataModelOracleUtilities.getFactTypes( projectDataModelOracle );
        ObjectSource source = null;

        if ( factTypes != null && factTypes.length > 0 ) {
            for ( int i = 0; i < factTypes.length; i++ ) {
                //skip .drl declared fact types.
                source = factSource( projectDataModelOracle, factTypes[ i ] );
                if ( source != null && ( /* ObjectSource.INTERNAL.equals( source ) || */ ObjectSource.DEPENDENCY.equals( source ) ) ) {
                    addFactType( dataModel, projectDataModelOracle, factTypes[ i ], source, classLoader );
                }
            }
        }
    }

    private static void addFactType( DataModel dataModel,
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

            /*
            Set<Annotation> typeAnnotations = DataModelOracleUtilities.getTypeAnnotations( oracleDataModel,
                    factType );
            if ( typeAnnotations != null ) {
                for ( Annotation annotation : typeAnnotations ) {
                    addFactTypeAnnotation( dataObject, annotation );
                }
            }
            */

            Map<String, ModelField[]> fields = oracleDataModel.getProjectModelFields();
            if ( fields != null ) {
                ModelField[] factFields = fields.get( factType );
                ModelField field;
                ObjectProperty property;
                Map<String, Set<Annotation>> typeFieldsAnnotations = DataModelOracleUtilities.getTypeFieldsAnnotations( oracleDataModel,
                        factType );
                Set<Annotation> fieldAnnotations;
                Integer naturalOrder = 0;
                //List<PropertyPosition> naturalOrderPositions = new ArrayList<PropertyPosition>();

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
                                property = dataObject.addProperty( field.getName(), field.getClassName() );
                            }

                            /*
                            //process property annotations
                            if ( typeFieldsAnnotations != null && ( fieldAnnotations = typeFieldsAnnotations.get( field.getName() ) ) != null ) {
                                for ( Annotation fieldAnnotation : fieldAnnotations ) {
                                    addFieldAnnotation( dataObject, property, fieldAnnotation );
                                }
                            }
                            */

                            /*
                            AnnotationImpl position = new AnnotationImpl( PositionAnnotationDefinition.getInstance() );
                            position.setValue( "value", naturalOrder.toString() );
                            naturalOrderPositions.add( new PropertyPosition( property, position ) );
                            naturalOrder++;
                            */

                        }
                    }
                    //verifyPositions( dataObject, naturalOrderPositions );
                }
            } else {
                logger.debug( "No fields for factTye: " + factType );
            }
        }
    }

    private static ClassMetadata readClassMetadata(String factType, ClassLoader classLoader) {
        try {
            Class _class = classLoader.loadClass(factType);
            return new ClassMetadata(_class.getModifiers(), _class.isMemberClass(), _class.isLocalClass(), _class.isAnonymousClass());
        } catch (ClassNotFoundException e) {
            logger.error("It was not possible to read class metadata for class: " + factType);
        }
        return null;
    }

    private static ObjectSource factSource( ProjectDataModelOracle oracleDataModel,
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
    private static boolean isLoadableField( ModelField field ) {
        return ( field.getOrigin().equals( ModelField.FIELD_ORIGIN.DECLARED ) );
    }

    static class ClassMetadata {

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
