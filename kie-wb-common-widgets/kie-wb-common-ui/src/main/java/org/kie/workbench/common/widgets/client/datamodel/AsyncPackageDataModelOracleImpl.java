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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

import org.drools.workbench.models.commons.shared.oracle.OracleUtils;
import org.drools.workbench.models.commons.shared.oracle.PackageDataModelOracleUtils;
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
import org.kie.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleIncrementalPayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.callbacks.Callback;
import org.uberfire.backend.vfs.Path;

/**
 * Default implementation of DataModelOracle
 */
public class AsyncPackageDataModelOracleImpl implements AsyncPackageDataModelOracle {

    @Inject
    private Caller<IncrementalDataModelService> service;

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

    // List of available rule names
    private Map<String, Collection<String>> ruleNames = new HashMap<String, Collection<String>>();

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
    protected Map<String, String> projectSuperTypes = new HashMap<String, String>();

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

    // Filtered (current package and imports) Method information used (exclusively) by ExpressionWidget and ActionCallMethodWidget
    private Map<String, List<MethodInfo>> filteredMethodInformation = new HashMap<String, List<MethodInfo>>();

    // Filtered (current package and imports) Map {factType, isEvent} to determine which Fact Type can be treated as events.
    private Map<String, Boolean> filteredEventTypes = new HashMap<String, Boolean>();

    // Filtered (current package and imports) Map {factType, isCollection} to determine which Fact Types are Collections.
    private Map<String, Boolean> filteredCollectionTypes = new HashMap<String, Boolean>();

    // Filtered (current package and imports) Map {factType, TypeSource} to determine where a Fact Type as defined.
    private Map<String, TypeSource> filteredTypeSources = new HashMap<String, TypeSource>();

    // Filtered (current package and imports) Map {factType, superType} to determine the Super Type of a FactType.
    protected Map<String, String> filteredSuperTypes = new HashMap<String, String>();

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
    // Rule Names
    // ####################################

    @Override
    public Map<String, Collection<String>> getRuleNamesMap() {
        return ruleNames;
    }

    @Override
    public List<String> getRuleNames() {
        List<String> allTheRuleNames = new ArrayList<String>();
        for ( String packageName : ruleNames.keySet() ) {
            allTheRuleNames.addAll( ruleNames.get( packageName ) );
        }
        return allTheRuleNames;
    }

    @Override
    public Collection<String> getRuleNamesForPackage( String packageName ) {
        return ruleNames.get( packageName );
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
            final String packageName = PackageDataModelOracleUtils.getPackageName( type );
            if ( !packageName.equals( this.packageName ) ) {
                externalTypes.add( type );
            }
        }
        final String[] result = new String[ externalTypes.size() ];
        externalTypes.toArray( result );
        Arrays.sort( result );
        return result;
    }

    /**
     * Returns fact's name from class type
     * @param type
     * @return
     */
    @Override
    public String getFactNameFromType( final String type ) {
        if ( type == null ) {
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
    public boolean isFactTypeAnEvent( final String factType ) {
        if ( !filteredEventTypes.containsKey( factType ) ) {
            return false;
        }
        return filteredEventTypes.get( factType );
    }

    /**
     * Return where a given FactType was defined
     * @param factType
     * @return
     */
    @Override
    public TypeSource getTypeSource( final String factType ) {
        return filteredTypeSources.get( factType );
    }

    /**
     * Get the Super Type for a given FactType
     * @param factType
     * @return null if no Super Type
     */
    @Override
    public String getSuperType( final String factType ) {
        return filteredSuperTypes.get( factType );
    }

    /**
     * Get the Annotations for a given FactType
     * @param factType
     * @return Empty set if no annotations exist for the type
     */
    @Override
    public Set<Annotation> getTypeAnnotations( final String factType ) {
        if ( !filteredTypeAnnotations.containsKey( factType ) ) {
            return Collections.EMPTY_SET;
        }
        return filteredTypeAnnotations.get( factType );
    }

    /**
     * Get the Fields Annotations for a given FactType
     * @param factType
     * @return Empty Map if no annotations exist for the type
     */
    @Override
    public Map<String, Set<Annotation>> getTypeFieldsAnnotations( final String factType ) {
        if ( !filteredTypeFieldsAnnotations.containsKey( factType ) ) {
            return Collections.EMPTY_MAP;
        }
        return filteredTypeFieldsAnnotations.get( factType );
    }

    // ####################################
    // Fact Types' Fields
    // ####################################

    @Override
    public Map<String, ModelField[]> getModelFields() {
        return filteredModelFields;
    }

    @Override
    public void getFieldCompletions( final String factType,
                                     final Callback<String[]> callback ) {
        final String[] fieldNames = getModelFields( factType );

        //Load incremental content
        if ( fieldNames == null ) {
            service.call( new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback( final PackageDataModelOracleIncrementalPayload dataModel ) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle( AsyncPackageDataModelOracleImpl.this,
                                                                                  dataModel );
                    final String[] fieldNames = getModelFields( factType );
                    callback.callback( fieldNames );
                }
            } ).getUpdates( resourcePath,
                            factType );

        } else {
            callback.callback( fieldNames );
        }
    }

    private String[] getModelFields( final String modelClassName ) {
        final String shortName = getFactNameFromType( modelClassName );
        if ( !filteredModelFields.containsKey( shortName ) ) {
            return new String[ 0 ];
        }

        //If fields do not exist return null; so they can be incrementally loaded
        final ModelField[] fields = filteredModelFields.get( shortName );
        if ( fields == null ) {
            return null;
        }

        //Otherwise return existing fields
        final String[] fieldNames = new String[ fields.length ];
        for ( int i = 0; i < fields.length; i++ ) {
            fieldNames[ i ] = fields[ i ].getName();
        }
        return fieldNames;
    }

    @Override
    public void getFieldCompletions( final String factType,
                                     final FieldAccessorsAndMutators accessorOrMutator,
                                     final Callback<String[]> callback ) {
        final String[] fieldNames = getModelFields( factType,
                                                    accessorOrMutator );

        //Load incremental content
        if ( fieldNames == null ) {
            service.call( new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback( final PackageDataModelOracleIncrementalPayload dataModel ) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle( AsyncPackageDataModelOracleImpl.this,
                                                                                  dataModel );
                    final String[] fieldNames = getModelFields( factType,
                                                                accessorOrMutator );
                    callback.callback( fieldNames );
                }
            } ).getUpdates( resourcePath,
                            factType );

        } else {
            callback.callback( fieldNames );
        }
    }

    private String[] getModelFields( final String modelClassName,
                                     final FieldAccessorsAndMutators accessorOrMutator ) {
        final String shortName = getFactNameFromType( modelClassName );
        if ( !filteredModelFields.containsKey( shortName ) ) {
            return new String[ 0 ];
        }

        //If fields do not exist return null; so they can be incrementally loaded
        final ModelField[] fields = filteredModelFields.get( shortName );
        if ( fields == null ) {
            return null;
        }

        //Otherwise return existing fields
        final List<String> fieldNames = new ArrayList<String>();
        for ( int i = 0; i < fields.length; i++ ) {
            final ModelField field = fields[ i ];
            if ( FieldAccessorsAndMutators.compare( accessorOrMutator,
                                                    field.getAccessorsAndMutators() ) ) {
                fieldNames.add( field.getName() );
            }
        }
        return fieldNames.toArray( new String[ fieldNames.size() ] );
    }

    @Override
    public String getFieldType( final String modelClassName,
                                final String fieldName ) {
        final ModelField field = getField( modelClassName,
                                           fieldName );
        return field == null ? null : field.getType();
    }

    @Override
    public String getFieldClassName( final String modelClassName,
                                     final String fieldName ) {
        final ModelField field = getField( modelClassName,
                                           fieldName );
        return field == null ? null : field.getClassName();
    }

    private ModelField getField( final String modelClassName,
                                 final String fieldName ) {
        final String shortName = getFactNameFromType( modelClassName );
        final ModelField[] fields = filteredModelFields.get( shortName );
        if ( fields == null ) {
            return null;
        }
        for ( ModelField modelField : fields ) {
            if ( modelField.getName().equals( fieldName ) ) {
                return modelField;
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
    public String[] getOperatorCompletions( final String factType,
                                            final String fieldName ) {

        final String fieldType = getFieldType( factType,
                                               fieldName );

        if ( fieldType == null ) {
            return OperatorsOracle.STANDARD_OPERATORS;
        } else if ( fieldName.equals( DataType.TYPE_THIS ) ) {
            if ( isFactTypeAnEvent( factType ) ) {
                return OracleUtils.joinArrays( OperatorsOracle.STANDARD_OPERATORS,
                                               OperatorsOracle.SIMPLE_CEP_OPERATORS,
                                               OperatorsOracle.COMPLEX_CEP_OPERATORS );
            } else {
                return OperatorsOracle.STANDARD_OPERATORS;
            }
        } else if ( fieldType.equals( DataType.TYPE_STRING ) ) {
            return OracleUtils.joinArrays( OperatorsOracle.STRING_OPERATORS,
                                           OperatorsOracle.EXPLICIT_LIST_OPERATORS );
        } else if ( DataType.isNumeric( fieldType ) ) {
            return OracleUtils.joinArrays( OperatorsOracle.COMPARABLE_OPERATORS,
                                           OperatorsOracle.EXPLICIT_LIST_OPERATORS );
        } else if ( fieldType.equals( DataType.TYPE_DATE ) ) {
            return OracleUtils.joinArrays( OperatorsOracle.COMPARABLE_OPERATORS,
                                           OperatorsOracle.EXPLICIT_LIST_OPERATORS,
                                           OperatorsOracle.SIMPLE_CEP_OPERATORS );
        } else if ( fieldType.equals( DataType.TYPE_COMPARABLE ) ) {
            return OperatorsOracle.COMPARABLE_OPERATORS;
        } else if ( fieldType.equals( DataType.TYPE_COLLECTION ) ) {
            return OperatorsOracle.COLLECTION_OPERATORS;
        } else {
            return OperatorsOracle.STANDARD_OPERATORS;
        }
    }

    /**
     * Get the Operators applicable for Connective Constraints
     * @param factType
     * @param fieldName
     * @return
     */
    @Override
    public String[] getConnectiveOperatorCompletions( final String factType,
                                                      final String fieldName ) {
        final String fieldType = getFieldType( factType,
                                               fieldName );

        if ( fieldType == null ) {
            return OperatorsOracle.STANDARD_CONNECTIVES;
        } else if ( fieldName.equals( DataType.TYPE_THIS ) ) {
            if ( isFactTypeAnEvent( factType ) ) {
                return OracleUtils.joinArrays( OperatorsOracle.STANDARD_CONNECTIVES,
                                               OperatorsOracle.SIMPLE_CEP_CONNECTIVES,
                                               OperatorsOracle.COMPLEX_CEP_CONNECTIVES );
            } else {
                return OperatorsOracle.STANDARD_CONNECTIVES;
            }
        } else if ( fieldType.equals( DataType.TYPE_STRING ) ) {
            return OperatorsOracle.STRING_CONNECTIVES;
        } else if ( DataType.isNumeric( fieldType ) ) {
            return OperatorsOracle.COMPARABLE_CONNECTIVES;
        } else if ( fieldType.equals( DataType.TYPE_DATE ) ) {
            return OracleUtils.joinArrays( OperatorsOracle.COMPARABLE_CONNECTIVES,
                                           OperatorsOracle.SIMPLE_CEP_CONNECTIVES );
        } else if ( fieldType.equals( DataType.TYPE_COMPARABLE ) ) {
            return OperatorsOracle.COMPARABLE_CONNECTIVES;
        } else if ( fieldType.equals( DataType.TYPE_COLLECTION ) ) {
            return OperatorsOracle.COLLECTION_CONNECTIVES;
        } else {
            return OperatorsOracle.STANDARD_CONNECTIVES;
        }

    }

    // ####################################
    // Methods
    // ####################################

    /**
     * Get a list of Methods for a Fact Type
     * @param factType
     * @return
     */
    @Override
    public List<String> getMethodNames( final String factType ) {
        return getMethodNames( factType,
                               -1 );
    }

    /**
     * Get a list of Methods for a Fact Type that have at least the specified number of parameters
     * @param factType
     * @param paramCount
     * @return
     */
    @Override
    public List<String> getMethodNames( final String factType,
                                        final int paramCount ) {
        final List<MethodInfo> infos = filteredMethodInformation.get( factType );
        final List<String> methodList = new ArrayList<String>();
        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                if ( paramCount == -1 || info.getParams().size() <= paramCount ) {
                    methodList.add( info.getNameWithParameters() );
                }
            }
        }
        return methodList;
    }

    /**
     * Get a list of parameters for a Method of a Fact Type
     * @param factType
     * @param methodNameWithParams
     * @return
     */
    @Override
    public List<String> getMethodParams( final String factType,
                                         final String methodNameWithParams ) {
        final List<MethodInfo> infos = filteredMethodInformation.get( factType );
        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                if ( info.getNameWithParameters().startsWith( methodNameWithParams ) ) {
                    return info.getParams();
                }
            }
        }
        return null;
    }

    /**
     * Get information on a Method of a Fact Type
     * @param factType
     * @param methodFullName
     * @return
     */
    @Override
    public MethodInfo getMethodInfo( final String factType,
                                     final String methodFullName ) {
        final List<MethodInfo> infos = filteredMethodInformation.get( factType );
        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                if ( info.getNameWithParameters().equals( methodFullName ) ) {
                    return info;
                }
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
    public String[] getFieldCompletionsForGlobalVariable( final String varName ) {
        final String type = getGlobalVariable( varName );
        return getModelFields( type );
    }

    @Override
    public List<MethodInfo> getMethodInfosForGlobalVariable( final String varName ) {
        final String type = getGlobalVariable( varName );
        return filteredMethodInformation.get( type );
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
        filteredModelFields.putAll( PackageDataModelOracleUtils.filterModelFields( packageName,
                                                                                   imports,
                                                                                   projectModelFields ) );

        //Filter and rename Global Types based on package name and imports
        filteredGlobalTypes = new HashMap<String, String>();
        filteredGlobalTypes.putAll( PackageDataModelOracleUtils.filterGlobalTypes( packageName,
                                                                                   imports,
                                                                                   packageGlobalTypes ) );

        //Filter and rename Collection Types based on package name and imports
        filteredCollectionTypes = new HashMap<String, Boolean>();
        filteredCollectionTypes.putAll( PackageDataModelOracleUtils.filterCollectionTypes( packageName,
                                                                                           imports,
                                                                                           projectCollectionTypes ) );

        //Filter and rename Event Types based on package name and imports
        filteredEventTypes = new HashMap<String, Boolean>();
        filteredEventTypes.putAll( PackageDataModelOracleUtils.filterEventTypes( packageName,
                                                                                 imports,
                                                                                 projectEventTypes ) );

        //Filter and rename TypeSources based on package name and imports
        filteredTypeSources = new HashMap<String, TypeSource>();
        filteredTypeSources.putAll( PackageDataModelOracleUtils.filterTypeSources( packageName,
                                                                                   imports,
                                                                                   projectTypeSources ) );

        //Filter and rename Declared Types based on package name and imports
        filteredSuperTypes = new HashMap<String, String>();
        filteredSuperTypes.putAll( PackageDataModelOracleUtils.filterSuperTypes( packageName,
                                                                                 imports,
                                                                                 projectSuperTypes ) );

        //Filter and rename Type Annotations based on package name and imports
        filteredTypeAnnotations = new HashMap<String, Set<Annotation>>();
        filteredTypeAnnotations.putAll( PackageDataModelOracleUtils.filterTypeAnnotations( packageName,
                                                                                           imports,
                                                                                           projectTypeAnnotations ) );

        //Filter and rename Type Field Annotations based on package name and imports
        filteredTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();
        filteredTypeFieldsAnnotations.putAll( PackageDataModelOracleUtils.filterTypeFieldsAnnotations( packageName,
                                                                                                       imports,
                                                                                                       projectTypeFieldsAnnotations ) );

        //Filter and rename Enum definitions based on package name and imports
        filteredEnumLists = new HashMap<String, String[]>();
        filteredEnumLists.putAll( packageWorkbenchEnumLists );
        filteredEnumLists.putAll( PackageDataModelOracleUtils.filterEnumDefinitions( packageName,
                                                                                     imports,
                                                                                     projectJavaEnumLists ) );

        //Filter and rename based on package name and imports
        filteredMethodInformation = new HashMap<String, List<MethodInfo>>();
        filteredMethodInformation.putAll( PackageDataModelOracleUtils.filterMethodInformation( packageName,
                                                                                               imports,
                                                                                               projectMethodInformation ) );

        //Filter and rename based on package name and imports
        filteredFieldParametersType = new HashMap<String, String>();
        filteredFieldParametersType.putAll( PackageDataModelOracleUtils.filterFieldParametersTypes( packageName,
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
    public void addRuleNames( final Map<String, Collection<String>> ruleNames ) {
        this.ruleNames.putAll( ruleNames );
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
    public void addSuperTypes( final Map<String, String> superTypes ) {
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
