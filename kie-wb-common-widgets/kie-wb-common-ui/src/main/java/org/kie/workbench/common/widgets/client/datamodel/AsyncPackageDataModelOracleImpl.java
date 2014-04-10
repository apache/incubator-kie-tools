/*
 * Copyright 2013 JBoss Inc
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
package org.kie.workbench.common.widgets.client.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.oracle.Annotation;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.datamodel.model.LazyModelField;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleIncrementalPayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Default implementation of DataModelOracle
 */
@Dependent
public class AsyncPackageDataModelOracleImpl implements AsyncPackageDataModelOracle {

    @Inject
    protected Caller<IncrementalDataModelService> service;

    //Path that this DMO is coupled to
    private Path resourcePath;

    //Project name
    protected String projectName;

    //Package for which this DMO relates
    private String packageName = "";

    //Imports from the Project into this Package
    private Imports imports = new Imports();

    // List of available package names
    private List<String> packageNames = new ArrayList<String>();

    // ####################################
    // Project Scope
    // ####################################

    //Fact Types and their corresponding fields
    protected Map<String, ModelField[]> projectModelFields = new HashMap<String, ModelField[]>();

    //Map of the field that contains the parametrized type of a collection
    //for example given "List<String> name", key = "name" value = "String"
    protected Map<String, String> projectFieldParametersType = new HashMap<String, String>();

    //Map {factType, isEvent} to determine which Fact Type can be treated as events.
    protected Map<String, Boolean> projectEventTypes = new HashMap<String, Boolean>();

    //Map {factType, TypeSource} to determine where a Fact Type as defined.
    protected Map<String, TypeSource> projectTypeSources = new HashMap<String, TypeSource>();

    //Map {factType, superType} to determine the Super Type of a FactType.
    protected Map<String, List<String>> projectSuperTypes = new HashMap<String, List<String>>();

    //Map {factType, Set<Annotation>} containing the FactType's annotations.
    protected Map<String, Set<Annotation>> projectTypeAnnotations = new HashMap<String, Set<Annotation>>();

    //Map {factType, Map<fieldName, Set<Annotation>>} containing the FactType's Field annotations.
    protected Map<String, Map<String, Set<Annotation>>> projectTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();

    // Scoped (current package and imports) map of { TypeName.field : String[] } - where a list is valid values to display in a drop down for a given Type.field combination.
    protected Map<String, String[]> projectJavaEnumLists = new HashMap<String, String[]>();

    //Method information used (exclusively) by ExpressionWidget and ActionCallMethodWidget
    protected Map<String, List<MethodInfo>> projectMethodInformation = new HashMap<String, List<MethodInfo>>();

    // A map of FactTypes {factType, isCollection} to determine which Fact Types are Collections.
    protected Map<String, Boolean> projectCollectionTypes = new HashMap<String, Boolean>();

    // ####################################
    // Package Scope
    // ####################################

    // Filtered (current package and imports) Fact Types and their corresponding fields
    private Map<String, ModelField[]> filteredModelFields = new HashMap<String, ModelField[]>();

    // Filtered (current package and imports) map of the field that contains the parametrized type of a collection
    // for example given "List<String> name", key = "name" value = "String"
    private Map<String, String> filteredFieldParametersType = new HashMap<String, String>();

    // Filtered (current package and imports) map of { TypeName.field : String[] } - where a list is valid values to display in a drop down for a given Type.field combination.
    private Map<String, String[]> filteredEnumLists = new HashMap<String, String[]>();

    // Filtered (current package and imports) Map {factType, isEvent} to determine which Fact Type can be treated as events.
    private Map<String, Boolean> filteredEventTypes = new HashMap<String, Boolean>();

    // Filtered (current package and imports) Map {factType, isCollection} to determine which Fact Types are Collections.
    private Map<String, Boolean> filteredCollectionTypes = new HashMap<String, Boolean>();

    // Filtered (current package and imports) Map {factType, TypeSource} to determine where a Fact Type as defined.
    private Map<String, TypeSource> filteredTypeSources = new HashMap<String, TypeSource>();

    // Filtered (current package and imports) Map {factType, superType} to determine the Super Type of a FactType.
    protected Map<String, List<String>> filteredSuperTypes = new HashMap<String, List<String>>();

    // Filtered (current package and imports) Map {factType, Set<Annotation>} containing the FactType's annotations.
    protected Map<String, Set<Annotation>> filteredTypeAnnotations = new HashMap<String, Set<Annotation>>();

    // Filtered (current package and imports) Map {factType, {fieldName, Set<Annotation>}} containing the FactType's Fields annotations.
    protected Map<String, Map<String, Set<Annotation>>> filteredTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();

    // Filtered (current package and imports) map of Globals {alias, class name}.
    private Map<String, String> filteredGlobalTypes = new HashMap<String, String>();

    // Package-level enumeration definitions derived from "Workbench" enumerations.
    private Map<String, String[]> packageWorkbenchEnumLists = new HashMap<String, String[]>();

    // Package-level DSL language extensions.
    private List<DSLSentence> packageDSLConditionSentences = new ArrayList<DSLSentence>();
    private List<DSLSentence> packageDSLActionSentences = new ArrayList<DSLSentence>();

    // Package-level map of Globals {alias, class name}.
    private Map<String, String> packageGlobalTypes = new HashMap<String, String>();

    // This is used to calculate what fields an enum list may depend on.
    private transient Map<String, Object> enumLookupFields;

    // Keep the link between fact name and the full qualified class name inside the package
    private FactNameToFQCNHandleRegistry factNameToFQCNHandleRegistry = new FactNameToFQCNHandleRegistry();

    //Public constructor is needed for Errai Marshaller :(
    public AsyncPackageDataModelOracleImpl() {
    }

    @Override
    public void init( final Path resourcePath ) {
        this.resourcePath = PortablePreconditions.checkNotNull( "resourcePath",
                                                                resourcePath );
    }

    // ####################################
    // Packages
    // ####################################

    @Override
    public List<String> getPackageNames() {
        return packageNames;
    }

    // ####################################
    // Fact Types
    // ####################################

    /**
     * Returns fact types available for rule authoring, i.e. those within the same package and those that have been imported.
     * @return
     */
    @Override
    public String[] getFactTypes() {
        final String[] types = filteredModelFields.keySet().toArray( new String[ filteredModelFields.size() ] );
        Arrays.sort( types );
        return types;
    }

    /**
     * Return all fact types available to the project, i.e. everything type defined within the project or externally imported
     * @return
     */
    @Override
    public String[] getAllFactTypes() {
        final List<String> types = new ArrayList<String>();
        types.addAll( this.projectModelFields.keySet() );
        final String[] result = new String[ types.size() ];
        types.toArray( result );
        Arrays.sort( result );
        return result;
    }

    /**
     * Return all fact types that are external to the package, i.e. they need to be imported to be used
     * @return
     */
    @Override
    public String[] getExternalFactTypes() {
        final String[] allTypes = getAllFactTypes();
        final List<String> externalTypes = new ArrayList<String>();
        for ( String type : allTypes ) {
            final String packageName = AsyncPackageDataModelOracleUtilities.getPackageName( type );
            if ( !packageName.equals( this.packageName ) ) {
                externalTypes.add( type );
            }
        }
        final String[] result = new String[ externalTypes.size() ];
        externalTypes.toArray( result );
        Arrays.sort( result );
        return result;
    }

    public String getFQCNByFactName( final String factName ) {
        if ( factName.contains( "." ) ) {
            return factName;
        } else if ( factNameToFQCNHandleRegistry.contains( factName ) ) {
            return factNameToFQCNHandleRegistry.get( factName );
        } else {
            return factName;
        }
    }

    /**
     * Returns fact's name from type
     * @param type for example org.test.Person or Person
     * @return Shorter type name Person, not org.test.Person
     */
    @Override
    public String getFactNameFromType( final String type ) {
        if ( type == null || type.isEmpty() ) {
            return null;
        }
        if ( filteredModelFields.containsKey( type ) ) {
            return type;
        }
        for ( Map.Entry<String, ModelField[]> entry : filteredModelFields.entrySet() ) {
            for ( ModelField mf : entry.getValue() ) {
                if ( DataType.TYPE_THIS.equals( mf.getName() ) && type.equals( mf.getClassName() ) ) {
                    return entry.getKey();
                }
            }
        }

        final String fgcnByFactName = getFQCNByFactName( type );
        if ( projectModelFields.containsKey( fgcnByFactName ) ) {
            return AsyncPackageDataModelOracleUtilities.getTypeName( fgcnByFactName );
        }

        return null;
    }

    /**
     * Is the Fact Type known to the DataModelOracle
     * @param factType
     * @return
     */
    @Override
    public boolean isFactTypeRecognized( final String factType ) {
        return filteredModelFields.containsKey( factType );
    }

    /**
     * Check whether a given FactType is an Event for CEP purposes
     * @param factType
     * @return
     */
    @Override
    public void isFactTypeAnEvent( final String factType,
                                   final Callback<Boolean> callback ) {
        if ( factType == null || factType.isEmpty() ) {
            callback.callback( false );
            return;
        }
        final Boolean isFactTypeAnEvent = filteredEventTypes.get( factType );

        //Load incremental content
        if ( isFactTypeAnEvent == null ) {
            service.call( new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback( final PackageDataModelOracleIncrementalPayload dataModel ) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle( AsyncPackageDataModelOracleImpl.this,
                                                                                  dataModel );
                    Boolean isFactTypeAnEvent = filteredEventTypes.get( factType );
                    if ( isFactTypeAnEvent == null ) {
                        isFactTypeAnEvent = false;
                        filteredEventTypes.put( factType,
                                                isFactTypeAnEvent );
                    }
                    callback.callback( isFactTypeAnEvent );
                }
            } ).getUpdates( resourcePath,
                            imports,
                            factType );

        } else {
            callback.callback( isFactTypeAnEvent );
        }
    }

    /**
     * Return where a given FactType was defined
     * @param factType
     * @return
     */
    @Override
    public void getTypeSource( final String factType,
                               final Callback<TypeSource> callback ) {
        final TypeSource typeSource = filteredTypeSources.get( factType );

        //Load incremental content
        if ( typeSource == null ) {
            service.call( new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback( final PackageDataModelOracleIncrementalPayload dataModel ) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle( AsyncPackageDataModelOracleImpl.this,
                                                                                  dataModel );
                    final TypeSource typeSource = filteredTypeSources.get( factType );
                    callback.callback( typeSource );
                }
            } ).getUpdates( resourcePath,
                            imports,
                            factType );

        } else {
            callback.callback( typeSource );
        }
    }

    /**
     * Get the Super Type for a given FactType
     * @param factType
     * @return null if no Super Type
     */
    @Override
    public void getSuperType( final String factType,
                              final Callback<String> callback ) {

        getSuperTypes( factType, new Callback<List<String>>() {
            @Override
            public void callback( List<String> result ) {
                if ( result != null ) {
                    callback.callback( result.get( 0 ) );

                } else {
                    callback.callback( null );
                }
            }
        } );
    }

    @Override
    public void getSuperTypes( final String factType,
                               final Callback<List<String>> callback ) {
        final List<String> superTypes = filteredSuperTypes.get( factType );

        //Load incremental content
        if ( superTypes == null ) {
            service.call( new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback( final PackageDataModelOracleIncrementalPayload dataModel ) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle( AsyncPackageDataModelOracleImpl.this,
                                                                                  dataModel );
                    callback.callback( filteredSuperTypes.get( factType ) );
                }
            } ).getUpdates( resourcePath,
                            imports,
                            factType );

        } else {
            callback.callback( superTypes );
        }
    }

    /**
     * Get the Annotations for a given FactType
     * @param factType
     * @return Empty set if no annotations exist for the type
     */
    @Override
    public void getTypeAnnotations( final String factType,
                                    final Callback<Set<Annotation>> callback ) {
        final Set<Annotation> typeAnnotations = filteredTypeAnnotations.get( factType );

        //Load incremental content
        if ( typeAnnotations == null ) {
            service.call( new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback( final PackageDataModelOracleIncrementalPayload dataModel ) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle( AsyncPackageDataModelOracleImpl.this,
                                                                                  dataModel );
                    Set<Annotation> typeAnnotations = filteredTypeAnnotations.get( factType );
                    if ( typeAnnotations == null ) {
                        typeAnnotations = Collections.EMPTY_SET;
                        filteredTypeAnnotations.put( factType,
                                                     typeAnnotations );
                    }
                    callback.callback( typeAnnotations );
                }
            } ).getUpdates( resourcePath,
                            imports,
                            factType );

        } else {
            callback.callback( typeAnnotations );
        }
    }

    /**
     * Get the Fields Annotations for a given FactType
     * @param factType
     * @return Empty Map if no annotations exist for the type
     */
    @Override
    public void getTypeFieldsAnnotations( final String factType,
                                          final Callback<Map<String, Set<Annotation>>> callback ) {
        final Map<String, Set<Annotation>> typeFieldsAnnotations = filteredTypeFieldsAnnotations.get( factType );

        //Load incremental content
        if ( typeFieldsAnnotations == null ) {
            service.call( new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback( final PackageDataModelOracleIncrementalPayload dataModel ) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle( AsyncPackageDataModelOracleImpl.this,
                                                                                  dataModel );
                    Map<String, Set<Annotation>> typeFieldsAnnotations = filteredTypeFieldsAnnotations.get( factType );
                    if ( typeFieldsAnnotations == null ) {
                        typeFieldsAnnotations = Collections.EMPTY_MAP;
                        filteredTypeFieldsAnnotations.put( factType,
                                                           typeFieldsAnnotations );
                    }
                    callback.callback( typeFieldsAnnotations );
                }
            } ).getUpdates( resourcePath,
                            imports,
                            factType );

        } else {
            callback.callback( typeFieldsAnnotations );
        }
    }

    // ####################################
    // Fact Types' Fields
    // ####################################

    @Override
    public void getFieldCompletions( final String factType,
                                     final Callback<ModelField[]> callback ) {
        final String fgcnByFactName = getFQCNByFactName( factType );
        ModelField[] fields = getModelFields( factType );

        if ( fields == null || fields.length == 0 ) {
            fields = projectModelFields.get( fgcnByFactName );
            if ( fields == null || isLazyProxy( fields ) ) {
                fields = null;
            } else {
                AsyncPackageDataModelOracleUtilities.correctModelFields( packageName,
                                                                         fields,
                                                                         imports );
            }
        }

        //Load incremental content
        if ( fields == null || fields.length == 0 ) {
            service.call( new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback( final PackageDataModelOracleIncrementalPayload dataModel ) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle( AsyncPackageDataModelOracleImpl.this,
                                                                                  dataModel );

                    // This will stop an infinite loop if there are no fields to be found
                    if ( dataModel.getModelFields().get( fgcnByFactName ) == null || dataModel.getModelFields().get( fgcnByFactName ).length == 0 ) {
                        callback.callback( new ModelField[ 0 ] );
                    } else {
                        getFieldCompletions( factType,
                                             callback );
                    }
                }
            } ).getUpdates( resourcePath,
                            imports,
                            fgcnByFactName );

        } else {
            callback.callback( fields );
        }
    }

    private ModelField[] getModelFields( final String modelClassName ) {
        final String shortName = getFactNameFromType( modelClassName );
        if ( !filteredModelFields.containsKey( shortName ) ) {
            return new ModelField[ 0 ];
        }

        //If fields do not exist return null; so they can be incrementally loaded
        final ModelField[] fields = filteredModelFields.get( shortName );
        if ( isLazyProxy( fields ) ) {
            return null;
        }

        //Otherwise return existing fields
        return fields;
    }

    @Override
    public void getFieldCompletions( final String factType,
                                     final FieldAccessorsAndMutators accessorOrMutator,
                                     final Callback<ModelField[]> callback ) {
        getFieldCompletions( factType,
                             new Callback<ModelField[]>() {

                                 @Override
                                 public void callback( ModelField[] fields ) {
                                     ArrayList<ModelField> result = new ArrayList<ModelField>();

                                     for ( ModelField field : fields ) {
                                         if ( FieldAccessorsAndMutators.compare( accessorOrMutator, field.getAccessorsAndMutators() ) ) {
                                             result.add( field );
                                         }
                                     }
                                     callback.callback( result.toArray( new ModelField[ result.size() ] ) );
                                 }
                             } );
    }

    //Check whether the ModelField[] is a place-holder for more information
    private boolean isLazyProxy( final ModelField[] modelFields ) {
        if ( modelFields == null ) {
            return false;
        } else if ( modelFields.length != 1 ) {
            return false;
        }
        return modelFields[ 0 ] instanceof LazyModelField;
    }

    @Override
    public String getFieldType( final String modelClassName,
                                final String fieldName ) {
        //Check fields
        final ModelField field = getField( modelClassName,
                                           fieldName );
        if ( field != null ) {
            return field.getType();
        }

        //Check method information
        final List<MethodInfo> mis = projectMethodInformation.get( modelClassName );
        if ( mis != null ) {
            for ( MethodInfo mi : mis ) {
                if ( mi.getName().equals( fieldName ) ) {
                    return mi.getGenericType();
                }
            }
        }

        return null;
    }

    @Override
    public String getFieldClassName( final String modelClassName,
                                     final String fieldName ) {
        //Check fields
        final ModelField field = getField( modelClassName,
                                           fieldName );
        if ( field != null ) {
            return field.getClassName();
        }

        //Check method information
        final List<MethodInfo> mis = projectMethodInformation.get( modelClassName );
        if ( mis != null ) {
            for ( MethodInfo mi : mis ) {
                if ( mi.getName().equals( fieldName ) ) {
                    return mi.getReturnClassType();
                }
            }
        }

        return null;
    }

    private ModelField getField( final String modelClassName,
                                 final String fieldName ) {
        final String fgcnByFactName = getFQCNByFactName( modelClassName );
        final ModelField[] fields = projectModelFields.get( fgcnByFactName );

        if ( fields == null ) {
            return null;
        }

        for ( ModelField modelField : fields ) {
            if ( modelField.getName().equals( fieldName ) ) {
                return AsyncPackageDataModelOracleUtilities.correctModelFields( packageName,
                                                                                imports,
                                                                                modelField );
            }
        }

        return null;
    }

    /**
     * Get the parametric type of a Field.
     * @param factType
     * @param fieldName
     * @return
     */
    @Override
    public String getParametricFieldType( final String factType,
                                          final String fieldName ) {
        final String qualifiedFactFieldName = factType + "#" + fieldName;
        return filteredFieldParametersType.get( qualifiedFactFieldName );
    }

    // ####################################
    // Operators
    // ####################################

    /**
     * Get the Operators applicable Base Constraints
     * @param factType
     * @param fieldName
     * @return
     */
    @Override
    public void getOperatorCompletions( final String factType,
                                        final String fieldName,
                                        final Callback<String[]> callback ) {
        final String fieldType = getFieldType( factType,
                                               fieldName );

        if ( fieldType == null ) {
            callback.callback( OperatorsOracle.STANDARD_OPERATORS );
            return;

        } else if ( fieldName.equals( DataType.TYPE_THIS ) ) {
            isFactTypeAnEvent( factType,
                               new Callback<Boolean>() {
                                   @Override
                                   public void callback( final Boolean isFactTypeAnEvent ) {
                                       if ( Boolean.TRUE.equals( isFactTypeAnEvent ) ) {
                                           callback.callback( OracleUtils.joinArrays( OperatorsOracle.STANDARD_OPERATORS,
                                                                                      OperatorsOracle.SIMPLE_CEP_OPERATORS,
                                                                                      OperatorsOracle.COMPLEX_CEP_OPERATORS ) );
                                           return;
                                       } else {
                                           callback.callback( OperatorsOracle.STANDARD_OPERATORS );
                                           return;
                                       }
                                   }
                               } );

        } else if ( fieldType.equals( DataType.TYPE_STRING ) ) {
            callback.callback( OracleUtils.joinArrays( OperatorsOracle.STRING_OPERATORS,
                                                       OperatorsOracle.EXPLICIT_LIST_OPERATORS ) );
            return;

        } else if ( DataType.isNumeric( fieldType ) ) {
            callback.callback( OracleUtils.joinArrays( OperatorsOracle.COMPARABLE_OPERATORS,
                                                       OperatorsOracle.EXPLICIT_LIST_OPERATORS ) );
            return;

        } else if ( fieldType.equals( DataType.TYPE_DATE ) ) {
            callback.callback( OracleUtils.joinArrays( OperatorsOracle.COMPARABLE_OPERATORS,
                                                       OperatorsOracle.EXPLICIT_LIST_OPERATORS,
                                                       OperatorsOracle.SIMPLE_CEP_OPERATORS ) );
            return;

        } else if ( fieldType.equals( DataType.TYPE_COMPARABLE ) ) {
            callback.callback( OperatorsOracle.COMPARABLE_OPERATORS );
            return;

        } else if ( fieldType.equals( DataType.TYPE_COLLECTION ) ) {
            callback.callback( OperatorsOracle.COLLECTION_OPERATORS );
            return;

        } else {
            callback.callback( OperatorsOracle.STANDARD_OPERATORS );
        }
    }

    /**
     * Get the Operators applicable for Connective Constraints
     * @param factType
     * @param fieldName
     * @return
     */
    @Override
    public void getConnectiveOperatorCompletions( final String factType,
                                                  final String fieldName,
                                                  final Callback<String[]> callback ) {
        final String fieldType = getFieldType( factType,
                                               fieldName );

        if ( fieldType == null ) {
            callback.callback( OperatorsOracle.STANDARD_CONNECTIVES );
            return;

        } else if ( fieldName.equals( DataType.TYPE_THIS ) ) {
            isFactTypeAnEvent( factType,
                               new Callback<Boolean>() {
                                   @Override
                                   public void callback( final Boolean isFactTypeAnEvent ) {
                                       if ( Boolean.TRUE.equals( isFactTypeAnEvent ) ) {
                                           callback.callback( OracleUtils.joinArrays( OperatorsOracle.STANDARD_CONNECTIVES,
                                                                                      OperatorsOracle.SIMPLE_CEP_CONNECTIVES,
                                                                                      OperatorsOracle.COMPLEX_CEP_CONNECTIVES ) );
                                           return;
                                       } else {
                                           callback.callback( OperatorsOracle.STANDARD_CONNECTIVES );
                                           return;
                                       }
                                   }
                               } );
        } else if ( fieldType.equals( DataType.TYPE_STRING ) ) {
            callback.callback( OperatorsOracle.STRING_CONNECTIVES );
            return;

        } else if ( DataType.isNumeric( fieldType ) ) {
            callback.callback( OperatorsOracle.COMPARABLE_CONNECTIVES );
            return;

        } else if ( fieldType.equals( DataType.TYPE_DATE ) ) {
            callback.callback( OracleUtils.joinArrays( OperatorsOracle.COMPARABLE_CONNECTIVES,
                                                       OperatorsOracle.SIMPLE_CEP_CONNECTIVES ) );
            return;

        } else if ( fieldType.equals( DataType.TYPE_COMPARABLE ) ) {
            callback.callback( OperatorsOracle.COMPARABLE_CONNECTIVES );
            return;

        } else if ( fieldType.equals( DataType.TYPE_COLLECTION ) ) {
            callback.callback( OperatorsOracle.COLLECTION_CONNECTIVES );
            return;

        } else {
            callback.callback( OperatorsOracle.STANDARD_CONNECTIVES );
        }

    }

    // ####################################
    // Methods
    // ####################################

    /**
     * Get a list of MethodInfos for a Fact Type
     * @param factType
     * @param callback
     * @return
     */
    @Override
    public void getMethodInfos( final String factType,
                                final Callback<List<MethodInfo>> callback ) {
        getMethodInfos( factType,
                        -1,
                        callback );
    }

    /**
     * Get a list of MethodInfos for a Fact Type that have at least the specified number of parameters
     * @param factType
     * @param parameterCount
     * @param callback
     * @return
     */
    @Override
    public void getMethodInfos( final String factType,
                                final int parameterCount,
                                final Callback<List<MethodInfo>> callback ) {
        final String fqcnByFactName = getFQCNByFactName( factType );
        final List<MethodInfo> methodInformation = projectMethodInformation.get( fqcnByFactName );

        //Load incremental content
        if ( methodInformation == null ) {
            service.call( new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback( final PackageDataModelOracleIncrementalPayload dataModel ) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle( AsyncPackageDataModelOracleImpl.this,
                                                                                  dataModel );
                    final List<MethodInfo> methodInformation = projectMethodInformation.get( fqcnByFactName );
                    callback.callback( getMethodInfos( parameterCount,
                                                       methodInformation ) );
                }
            } ).getUpdates( resourcePath,
                            imports,
                            fqcnByFactName );

        } else {
            callback.callback( getMethodInfos( parameterCount,
                                               methodInformation ) );
        }
    }

    private List<MethodInfo> getMethodInfos( final int paramCount,
                                             final List<MethodInfo> allMethodInfos ) {
        final List<MethodInfo> methodInfos = new ArrayList<MethodInfo>();
        for ( MethodInfo mi : allMethodInfos ) {
            if ( paramCount == -1 || mi.getParams().size() <= paramCount ) {
                methodInfos.add( mi );
            }
        }
        return AsyncPackageDataModelOracleUtilities.correctMethodInformation( packageName,
                                                                              methodInfos,
                                                                              imports );
    }

    /**
     * Get a list of parameters for a Method of a Fact Type
     * @param factType
     * @param methodNameWithParams
     * @return
     */
    @Override
    public void getMethodParams( final String factType,
                                 final String methodNameWithParams,
                                 final Callback<List<String>> callback ) {
        final String fqcnFactName = getFQCNByFactName( factType );
        final List<MethodInfo> methodInformation = projectMethodInformation.get( fqcnFactName );

        //Load incremental content
        if ( methodInformation == null ) {
            service.call( new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback( final PackageDataModelOracleIncrementalPayload dataModel ) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle( AsyncPackageDataModelOracleImpl.this,
                                                                                  dataModel );
                    final List<MethodInfo> methodInformation = projectMethodInformation.get( fqcnFactName );
                    callback.callback( getMethodParams( methodInformation,
                                                        methodNameWithParams ) );
                }
            } ).getUpdates( resourcePath,
                            imports,
                            fqcnFactName );

        } else {
            callback.callback( getMethodParams( methodInformation,
                                                methodNameWithParams ) );
        }
    }

    private List<String> getMethodParams( final List<MethodInfo> methodInfos,
                                          final String methodNameWithParams ) {
        final List<String> methodParams = new ArrayList<String>();
        for ( MethodInfo methodInfo : methodInfos ) {
            if ( methodInfo.getNameWithParameters().startsWith( methodNameWithParams ) ) {
                methodParams.addAll( methodInfo.getParams() );
            }
        }
        return methodParams;
    }

    /**
     * Get information on a Method of a Fact Type
     * @param factType
     * @param methodNameWithParams
     * @return
     */
    @Override
    public void getMethodInfo( final String factType,
                               final String methodNameWithParams,
                               final Callback<MethodInfo> callback ) {
        final String fqcnFactName = getFQCNByFactName( factType );
        final List<MethodInfo> methodInformation = projectMethodInformation.get( fqcnFactName );

        //Load incremental content
        if ( methodInformation == null ) {
            service.call( new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback( final PackageDataModelOracleIncrementalPayload dataModel ) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle( AsyncPackageDataModelOracleImpl.this,
                                                                                  dataModel );
                    final List<MethodInfo> methodInformation = projectMethodInformation.get( fqcnFactName );
                    callback.callback( getMethodInfo( methodInformation,
                                                      methodNameWithParams ) );
                }
            } ).getUpdates( resourcePath,
                            imports,
                            factType );

        } else {
            callback.callback( getMethodInfo( methodInformation,
                                              methodNameWithParams ) );
        }
    }

    private MethodInfo getMethodInfo( final List<MethodInfo> methodInfos,
                                      final String methodNameWithParams ) {
        for ( MethodInfo methodInfo : methodInfos ) {
            if ( methodInfo.getNameWithParameters().equals( methodNameWithParams ) ) {
                return AsyncPackageDataModelOracleUtilities.correctMethodInformation( packageName,
                                                                                      methodInfo,
                                                                                      imports );
            }
        }
        return null;
    }

    // ####################################
    // Globals
    // ####################################

    @Override
    public String[] getGlobalVariables() {
        return OracleUtils.toStringArray( filteredGlobalTypes.keySet() );
    }

    @Override
    public String getGlobalVariable( final String name ) {
        return filteredGlobalTypes.get( name );
    }

    @Override
    public boolean isGlobalVariable( final String name ) {
        return filteredGlobalTypes.containsKey( name );
    }

    @Override
    public void getFieldCompletionsForGlobalVariable( final String varName,
                                                      final Callback<ModelField[]> callback ) {
        getFieldCompletions(
                getGlobalVariable( varName ),
                new Callback<ModelField[]>() {
                    @Override
                    public void callback( ModelField[] result ) {
                        callback.callback( result );
                    }
                } );
    }

    @Override
    public void getMethodInfosForGlobalVariable( final String varName,
                                                 final Callback<List<MethodInfo>> callback ) {
        final String factType = packageGlobalTypes.get( varName );
        final List<MethodInfo> methodInformation = projectMethodInformation.get( factType );

        //Load incremental content
        if ( methodInformation == null ) {
            service.call( new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback( final PackageDataModelOracleIncrementalPayload dataModel ) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle( AsyncPackageDataModelOracleImpl.this,
                                                                                  dataModel );
                    callback.callback( projectMethodInformation.get( factType ) );
                }
            } ).getUpdates( resourcePath,
                            imports,
                            factType );

        } else {
            callback.callback( methodInformation );
        }
    }

    @Override
    public String[] getGlobalCollections() {
        final List<String> globalCollections = new ArrayList<String>();
        for ( Map.Entry<String, String> e : filteredGlobalTypes.entrySet() ) {
            if ( filteredCollectionTypes.containsKey( e.getValue() ) ) {
                if ( Boolean.TRUE.equals( filteredCollectionTypes.get( e.getValue() ) ) ) {
                    globalCollections.add( e.getKey() );
                }
            }
        }
        return OracleUtils.toStringArray( globalCollections );
    }

    // ####################################
    // DSLs
    // ####################################

    @Override
    public List<DSLSentence> getDSLConditions() {
        return Collections.unmodifiableList( packageDSLConditionSentences );
    }

    @Override
    public List<DSLSentence> getDSLActions() {
        return Collections.unmodifiableList( packageDSLActionSentences );
    }

    // ####################################
    // Enums
    // ####################################

    /**
     * Get enums for a Type and Field.
     */
    @Override
    public DropDownData getEnums( final String type,
                                  final String field ) {
        return getEnums( type,
                         field,
                         new HashMap<String, String>() );
    }

    /**
     * Get enums for a Type and Field where the enum list may depend upon the values of other fields.
     */
    @Override
    public DropDownData getEnums( final String type,
                                  final String field,
                                  final Map<String, String> currentValueMap ) {

        final Map<String, Object> dataEnumLookupFields = loadDataEnumLookupFields();

        if ( !currentValueMap.isEmpty() ) {
            // we may need to check for data dependent enums
            final Object _typeFields = dataEnumLookupFields.get( type + "#" + field );

            if ( _typeFields instanceof String ) {
                final String typeFields = (String) _typeFields;
                final StringBuilder dataEnumListsKeyBuilder = new StringBuilder( type );
                dataEnumListsKeyBuilder.append( "#" ).append( field );

                boolean addOpeninColumn = true;
                final String[] splitTypeFields = typeFields.split( "," );
                for ( int j = 0; j < splitTypeFields.length; j++ ) {
                    final String typeField = splitTypeFields[ j ];

                    for ( Map.Entry<String, String> currentValueEntry : currentValueMap.entrySet() ) {
                        final String fieldName = currentValueEntry.getKey();
                        final String fieldValue = currentValueEntry.getValue();
                        if ( fieldName.trim().equals( typeField.trim() ) ) {
                            if ( addOpeninColumn ) {
                                dataEnumListsKeyBuilder.append( "[" );
                                addOpeninColumn = false;
                            }
                            dataEnumListsKeyBuilder.append( typeField ).append( "=" ).append( fieldValue );

                            if ( j != ( splitTypeFields.length - 1 ) ) {
                                dataEnumListsKeyBuilder.append( "," );
                            }
                        }
                    }
                }

                if ( !addOpeninColumn ) {
                    dataEnumListsKeyBuilder.append( "]" );
                }

                final DropDownData data = DropDownData.create( filteredEnumLists.get( dataEnumListsKeyBuilder.toString() ) );
                if ( data != null ) {
                    return data;
                }
            } else if ( _typeFields != null ) {
                // these enums are calculated on demand, server side...
                final String[] fieldsNeeded = (String[]) _typeFields;
                final String queryString = getQueryString( type,
                                                           field,
                                                           fieldsNeeded,
                                                           filteredEnumLists );
                final String[] valuePairs = new String[ fieldsNeeded.length ];

                // collect all the values of the fields needed, then return it as a string...
                for ( int i = 0; i < fieldsNeeded.length; i++ ) {
                    for ( Map.Entry<String, String> currentValueEntry : currentValueMap.entrySet() ) {
                        final String fieldName = currentValueEntry.getKey();
                        final String fieldValue = currentValueEntry.getValue();
                        if ( fieldName.equals( fieldsNeeded[ i ] ) ) {
                            valuePairs[ i ] = fieldsNeeded[ i ] + "=" + fieldValue;
                        }
                    }
                }

                if ( valuePairs.length > 0 && valuePairs[ 0 ] != null ) {
                    return DropDownData.create( queryString,
                                                valuePairs );
                }
            }
        }
        return DropDownData.create( getEnumValues( type,
                                                   field ) );
    }

    /**
     * Get the query string for a fact.field It will ignore any specified field,
     * and just look for the string - as there should only be one Fact.field of
     * this type (it is all determined server side).
     * @param fieldsNeeded
     */
    private String getQueryString( final String factType,
                                   final String field,
                                   final String[] fieldsNeeded,
                                   final Map<String, String[]> dataEnumLists ) {
        for ( Iterator<String> iterator = dataEnumLists.keySet().iterator(); iterator.hasNext(); ) {
            final String key = iterator.next();
            if ( key.startsWith( factType + "#" + field ) && fieldsNeeded != null && key.contains( "[" ) ) {

                final String[] values = key.substring( key.indexOf( '[' ) + 1,
                                                       key.lastIndexOf( ']' ) ).split( "," );

                if ( values.length != fieldsNeeded.length ) {
                    continue;
                }

                boolean fail = false;
                for ( int i = 0; i < values.length; i++ ) {
                    final String a = values[ i ].trim();
                    final String b = fieldsNeeded[ i ].trim();
                    if ( !a.equals( b ) ) {
                        fail = true;
                        break;
                    }
                }
                if ( fail ) {
                    continue;
                }

                final String[] qry = dataEnumLists.get( key );
                return qry[ 0 ];
            } else if ( key.startsWith( factType + "#" + field ) && ( fieldsNeeded == null || fieldsNeeded.length == 0 ) ) {
                final String[] qry = dataEnumLists.get( key );
                return qry[ 0 ];
            }
        }
        throw new IllegalStateException();
    }

    /**
     * For simple cases - where a list of values are known based on a field.
     */
    @Override
    public String[] getEnumValues( final String factType,
                                   final String field ) {
        return filteredEnumLists.get( factType + "#" + field );
    }

    @Override
    public boolean hasEnums( final String factType,
                             final String field ) {
        return hasEnums( factType + "#" + field );
    }

    @Override
    public boolean hasEnums( final String qualifiedFactField ) {
        boolean hasEnums = false;
        final String key = qualifiedFactField.replace( ".",
                                                       "#" );
        final String dependentType = key + "[";
        for ( String e : filteredEnumLists.keySet() ) {
            //e.g. Fact.field1
            if ( e.equals( key ) ) {
                return true;
            }
            //e.g. Fact.field2[field1=val2]
            if ( e.startsWith( dependentType ) ) {
                return true;
            }
        }
        return hasEnums;
    }

    /**
     * Check whether the childField is related to the parentField through a
     * chain of enumeration dependencies. Both fields belong to the same Fact
     * Type. Furthermore code consuming this function should ensure both
     * parentField and childField relate to the same Fact Pattern
     * @param factType
     * @param parentField
     * @param childField
     * @return
     */
    @Override
    public boolean isDependentEnum( final String factType,
                                    final String parentField,
                                    final String childField ) {
        final Map<String, Object> enums = loadDataEnumLookupFields();
        if ( enums.isEmpty() ) {
            return false;
        }
        //Check if the childField is a direct descendant of the parentField
        final String key = factType + "#" + childField;
        if ( !enums.containsKey( key ) ) {
            return false;
        }

        //Otherwise follow the dependency chain...
        final Object _parent = enums.get( key );
        if ( _parent instanceof String ) {
            final String _parentField = (String) _parent;
            if ( _parentField.equals( parentField ) ) {
                return true;
            } else {
                return isDependentEnum( factType,
                                        parentField,
                                        _parentField );
            }
        }
        return false;
    }

    /**
     * This is only used by enums that are like Fact.field[something=X] and so on.
     */
    private Map<String, Object> loadDataEnumLookupFields() {
        if ( enumLookupFields == null ) {
            enumLookupFields = new HashMap<String, Object>();
            final Set<String> keys = filteredEnumLists.keySet();
            for ( String key : keys ) {
                if ( key.indexOf( '[' ) != -1 ) {
                    int ix = key.indexOf( '[' );
                    final String factField = key.substring( 0,
                                                            ix );
                    final String predicate = key.substring( ix + 1,
                                                            key.indexOf( ']' ) );
                    if ( predicate.indexOf( '=' ) > -1 ) {

                        final String[] bits = predicate.split( "," );
                        final StringBuilder typeFieldBuilder = new StringBuilder();

                        for ( int i = 0; i < bits.length; i++ ) {
                            typeFieldBuilder.append( bits[ i ].substring( 0,
                                                                          bits[ i ].indexOf( '=' ) ) );
                            if ( i != ( bits.length - 1 ) ) {
                                typeFieldBuilder.append( "," );
                            }
                        }

                        enumLookupFields.put( factField,
                                              typeFieldBuilder.toString() );
                    } else {
                        final String[] fields = predicate.split( "," );
                        for ( int i = 0; i < fields.length; i++ ) {
                            fields[ i ] = fields[ i ].trim();
                        }
                        enumLookupFields.put( factField,
                                              fields );
                    }
                }
            }
        }

        return enumLookupFields;
    }

    // ####################################
    // Imports
    // ####################################

    @Override
    public void filter( final Imports imports ) {
        this.imports = imports;
        filter();
    }

    @Override
    public void filter() {

        //Filter and rename Model Fields based on package name and imports
        filteredModelFields = new HashMap<String, ModelField[]>();
        filteredModelFields.putAll( AsyncPackageDataModelOracleUtilities.filterModelFields( packageName,
                                                                                            imports,
                                                                                            projectModelFields,
                                                                                            factNameToFQCNHandleRegistry ) );

        // For filling the factNameToFQCNHandleRegistry
        AsyncPackageDataModelOracleUtilities.visitMethodInformation( projectMethodInformation,
                                                                     factNameToFQCNHandleRegistry );

        //Filter and rename Global Types based on package name and imports
        filteredGlobalTypes = new HashMap<String, String>();
        filteredGlobalTypes.putAll( AsyncPackageDataModelOracleUtilities.filterGlobalTypes( packageName,
                                                                                            imports,
                                                                                            packageGlobalTypes ) );

        //Filter and rename Collection Types based on package name and imports
        filteredCollectionTypes = new HashMap<String, Boolean>();
        filteredCollectionTypes.putAll( AsyncPackageDataModelOracleUtilities.filterCollectionTypes( packageName,
                                                                                                    imports,
                                                                                                    projectCollectionTypes ) );

        //Filter and rename Event Types based on package name and imports
        filteredEventTypes = new HashMap<String, Boolean>();
        filteredEventTypes.putAll( AsyncPackageDataModelOracleUtilities.filterEventTypes( packageName,
                                                                                          imports,
                                                                                          projectEventTypes ) );

        //Filter and rename TypeSources based on package name and imports
        filteredTypeSources = new HashMap<String, TypeSource>();
        filteredTypeSources.putAll( AsyncPackageDataModelOracleUtilities.filterTypeSources( packageName,
                                                                                            imports,
                                                                                            projectTypeSources ) );

        //Filter and rename Declared Types based on package name and imports
        filteredSuperTypes = new HashMap<String, List<String>>();
        filteredSuperTypes.putAll( AsyncPackageDataModelOracleUtilities.filterSuperTypes( packageName,
                                                                                          imports,
                                                                                          projectSuperTypes ) );

        //Filter and rename Type Annotations based on package name and imports
        filteredTypeAnnotations = new HashMap<String, Set<Annotation>>();
        filteredTypeAnnotations.putAll( AsyncPackageDataModelOracleUtilities.filterTypeAnnotations( packageName,
                                                                                                    imports,
                                                                                                    projectTypeAnnotations ) );

        //Filter and rename Type Field Annotations based on package name and imports
        filteredTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();
        filteredTypeFieldsAnnotations.putAll( AsyncPackageDataModelOracleUtilities.filterTypeFieldsAnnotations( packageName,
                                                                                                                imports,
                                                                                                                projectTypeFieldsAnnotations ) );

        //Filter and rename Enum definitions based on package name and imports
        filteredEnumLists = new HashMap<String, String[]>();
        filteredEnumLists.putAll( packageWorkbenchEnumLists );
        filteredEnumLists.putAll( AsyncPackageDataModelOracleUtilities.filterEnumDefinitions( packageName,
                                                                                              imports,
                                                                                              projectJavaEnumLists ) );

        //Filter and rename based on package name and imports
        filteredFieldParametersType = new HashMap<String, String>();
        filteredFieldParametersType.putAll( AsyncPackageDataModelOracleUtilities.filterFieldParametersTypes( packageName,
                                                                                                             imports,
                                                                                                             projectFieldParametersType ) );
    }

    // ####################################
    // Population of DMO
    // ####################################

    @Override
    public void setProjectName( final String projectName ) {
        this.projectName = projectName;
    }

    @Override
    public void setPackageName( final String packageName ) {
        this.packageName = packageName;
    }

    @Override
    public void addModelFields( final Map<String, ModelField[]> modelFields ) {
        this.projectModelFields.putAll( modelFields );
    }

    @Override
    public void addFieldParametersType( final Map<String, String> fieldParametersType ) {
        this.projectFieldParametersType.putAll( fieldParametersType );
    }

    @Override
    public void addEventTypes( final Map<String, Boolean> eventTypes ) {
        this.projectEventTypes.putAll( eventTypes );
    }

    @Override
    public void addTypeSources( final Map<String, TypeSource> typeSources ) {
        this.projectTypeSources.putAll( typeSources );
    }

    @Override
    public void addSuperTypes( final Map<String, List<String>> superTypes ) {
        this.projectSuperTypes.putAll( superTypes );
    }

    @Override
    public void addTypeAnnotations( final Map<String, Set<Annotation>> annotations ) {
        this.projectTypeAnnotations.putAll( annotations );
    }

    @Override
    public void addTypeFieldsAnnotations( final Map<String, Map<String, Set<Annotation>>> typeFieldsAnnotations ) {
        this.projectTypeFieldsAnnotations.putAll( typeFieldsAnnotations );
    }

    @Override
    public void addJavaEnumDefinitions( final Map<String, String[]> dataEnumLists ) {
        this.projectJavaEnumLists.putAll( dataEnumLists );
    }

    @Override
    public void addMethodInformation( final Map<String, List<MethodInfo>> methodInformation ) {
        this.projectMethodInformation.putAll( methodInformation );
    }

    @Override
    public void addCollectionTypes( final Map<String, Boolean> collectionTypes ) {
        this.projectCollectionTypes.putAll( collectionTypes );
    }

    @Override
    public void addPackageNames( final List<String> packageNames ) {
        this.packageNames.addAll( packageNames );
    }

    @Override
    public void addWorkbenchEnumDefinitions( final Map<String, String[]> dataEnumLists ) {
        this.packageWorkbenchEnumLists.putAll( dataEnumLists );
    }

    @Override
    public void addDslConditionSentences( final List<DSLSentence> dslConditionSentences ) {
        this.packageDSLConditionSentences.addAll( dslConditionSentences );
    }

    @Override
    public void addDslActionSentences( final List<DSLSentence> dslActionSentences ) {
        this.packageDSLActionSentences.addAll( dslActionSentences );
    }

    @Override
    public void addGlobals( final Map<String, String> packageGlobalTypes ) {
        this.packageGlobalTypes.putAll( packageGlobalTypes );
    }

}
