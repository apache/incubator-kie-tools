/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.datamodel.backend.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.appformer.project.datamodel.oracle.Annotation;
import org.appformer.project.datamodel.oracle.DataType;
import org.appformer.project.datamodel.oracle.MethodInfo;
import org.appformer.project.datamodel.oracle.ModelField;
import org.appformer.project.datamodel.oracle.ProjectDataModelOracle;
import org.appformer.project.datamodel.oracle.TypeSource;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.util.SortHelper;
import org.kie.workbench.common.services.datamodel.model.LazyModelField;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleIncrementalPayload;

/**
 * Utilities to query ProjectDMO content
 */
public class DataModelOracleUtilities {

    /**
     * Convenience method to get an array of all fully qualified class names available in a project
     * @param oracle The DMO representing a project
     * @return
     */
    public static String[] getFactTypes( final ProjectDataModelOracle oracle ) {

        List<String> packageNames = oracle.getProjectPackageNames();

        final Map<String, ModelField[]> modelFields = oracle.getProjectModelFields();
        final List<String> types = new ArrayList<String>();
        for ( String type : modelFields.keySet() ) {
            int beginIndex = type.lastIndexOf( '.' );

            if ( beginIndex < 0 ) {
                types.add( type );
            } else {
                String substring = type.substring( 0, beginIndex );
                if ( packageNames.contains( substring ) ) {
                    types.add( type );
                }
            }
        }
        Collections.sort( types, SortHelper.ALPHABETICAL_ORDER_COMPARATOR );

        return types.toArray( new String[ types.size() ] );
    }

    /**
     * Convenience method to get an array of field names for a type in a project
     * @param oracle The DMO representing a project
     * @param fullyQualifiedClassName The FQCN of the type
     * @return
     */
    public static String[] getFieldNames( final ProjectDataModelOracle oracle,
                                          final String fullyQualifiedClassName ) {
        final ModelField[] modelFields = oracle.getProjectModelFields().get( fullyQualifiedClassName );
        if ( modelFields == null ) {
            return new String[ 0 ];
        }

        final String[] fieldNames = new String[ modelFields.length ];
        for ( int i = 0; i < modelFields.length; i++ ) {
            fieldNames[ i ] = modelFields[ i ].getName();
        }
        Arrays.sort( fieldNames, SortHelper.ALPHABETICAL_ORDER_COMPARATOR );

        return fieldNames;
    }

    /**
     * Convenience method to get the fully qualified class name of the super type of another type in a project
     * @param oracle The DMO representing a project
     * @param fullyQualifiedClassName The FQCN of the type
     * @return
     */
    public static String getSuperType( final ProjectDataModelOracle oracle,
                                       final String fullyQualifiedClassName ) {
        List<String> superTypes = oracle.getProjectSuperTypes().get( fullyQualifiedClassName );
        if ( superTypes != null && superTypes.size() > 0 ) {
            return superTypes.get( 0 );
        } else {
            return null;
        }
    }

    /**
     * Convenience method to get a set of annotations on a type in a project
     * @param oracle The DMO representing a project
     * @param fullyQualifiedClassName The FQCN of the type
     * @return
     */
    public static Set<Annotation> getTypeAnnotations( final ProjectDataModelOracle oracle,
                                                      final String fullyQualifiedClassName ) {
        final Map<String, Set<Annotation>> typeAnnotations = oracle.getProjectTypeAnnotations();
        if ( !typeAnnotations.containsKey( fullyQualifiedClassName ) ) {
            return Collections.EMPTY_SET;
        }
        return typeAnnotations.get( fullyQualifiedClassName );
    }

    /**
     * Convenience method to get all field annotations on a type in a project
     * @param oracle The DMO representing a project
     * @param fullyQualifiedClassName The FQCN of the type
     * @return
     */
    public static Map<String, Set<Annotation>> getTypeFieldsAnnotations( final ProjectDataModelOracle oracle,
                                                                         final String fullyQualifiedClassName ) {
        final Map<String, Map<String, Set<Annotation>>> typeFieldsAnnotations = oracle.getProjectTypeFieldsAnnotations();
        if ( !typeFieldsAnnotations.containsKey( fullyQualifiedClassName ) ) {
            return Collections.EMPTY_MAP;
        }
        return typeFieldsAnnotations.get( fullyQualifiedClassName );
    }

    /**
     * Convenience method to get the fully qualified class name of a field on a type in a project
     * @param oracle The DMO representing a project
     * @param fullyQualifiedClassName The FQCN of the type
     * @param fieldName The field Name
     * @return
     */
    public static String getFieldClassName( final ProjectDataModelOracle oracle,
                                            final String fullyQualifiedClassName,
                                            final String fieldName ) {
        final ModelField field = getField( oracle,
                                           fullyQualifiedClassName,
                                           fieldName );
        return field == null ? null : field.getClassName();
    }

    private static ModelField getField( final ProjectDataModelOracle oracle,
                                        final String fullyQualifiedClassName,
                                        final String fieldName ) {
        final String shortName = getFactNameFromType( oracle,
                                                      fullyQualifiedClassName );
        final ModelField[] fields = oracle.getProjectModelFields().get( shortName );
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

    private static String getFactNameFromType( final ProjectDataModelOracle oracle,
                                               final String fullyQualifiedClassName ) {
        if ( fullyQualifiedClassName == null ) {
            return null;
        }
        if ( oracle.getProjectModelFields().containsKey( fullyQualifiedClassName ) ) {
            return fullyQualifiedClassName;
        }
        for ( Map.Entry<String, ModelField[]> entry : oracle.getProjectModelFields().entrySet() ) {
            for ( ModelField mf : entry.getValue() ) {
                if ( DataType.TYPE_THIS.equals( mf.getName() ) && fullyQualifiedClassName.equals( mf.getClassName() ) ) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * Convenience method to get the generic type of a field on a type in a project
     * @param oracle The DMO representing a project
     * @param fullyQualifiedClassName The FQCN of the type
     * @param fieldName The field Name
     * @return
     */
    public static String getParametricFieldType( final ProjectDataModelOracle oracle,
                                                 final String fullyQualifiedClassName,
                                                 final String fieldName ) {
        final String qualifiedFactFieldName = fullyQualifiedClassName + "#" + fieldName;
        return oracle.getProjectFieldParametersType().get( qualifiedFactFieldName );
    }

    /**
     * Convenience method to get the source of a type in a project
     * @param oracle The DMO representing a project
     * @param fullyQualifiedClassName The FQCN of the type
     * @return
     */
    public static TypeSource getTypeSource( final ProjectDataModelOracle oracle,
                                            final String fullyQualifiedClassName ) {
        return oracle.getProjectTypeSources().get( fullyQualifiedClassName );
    }

    public static void populateDataModel( final PackageDataModelOracle oracle,
                                          final PackageDataModelOracleBaselinePayload dataModel,
                                          final Set<String> usedFullyQualifiedClassNames ) {
        dataModel.setProjectName( oracle.getProjectName() );
        dataModel.setPackageName( oracle.getPackageName() );
        dataModel.setModelFields( setupModelFields( usedFullyQualifiedClassNames,
                                                    oracle.getProjectModelFields(),
                                                    oracle.getPackageGlobals() ) );
        dataModel.setFieldParametersType( filterFieldParametersTypes( usedFullyQualifiedClassNames,
                                                                      oracle.getProjectFieldParametersType() ) );
        dataModel.setEventTypes( filterEventTypes( usedFullyQualifiedClassNames,
                                                   oracle.getProjectEventTypes() ) );
        dataModel.setTypeSources( filterTypeSources( usedFullyQualifiedClassNames,
                                                     oracle.getProjectTypeSources() ) );
        dataModel.setSuperTypes( filterSuperTypes( usedFullyQualifiedClassNames,
                                                   oracle.getProjectSuperTypes() ) );
        dataModel.setTypeAnnotations( filterTypeAnnotations( usedFullyQualifiedClassNames,
                                                             oracle.getProjectTypeAnnotations() ) );
        dataModel.setTypeFieldsAnnotations( filterTypeFieldsAnnotations( usedFullyQualifiedClassNames,
                                                                         oracle.getProjectTypeFieldsAnnotations() ) );
        dataModel.setJavaEnumDefinitions( oracle.getProjectJavaEnumDefinitions() );
        dataModel.setWorkbenchEnumDefinitions( oracle.getPackageWorkbenchDefinitions() );
        dataModel.setMethodInformation( filterMethodInformation( usedFullyQualifiedClassNames,
                                                                 oracle.getProjectMethodInformation() ) );
        dataModel.setCollectionTypes( filterCollectionTypes( usedFullyQualifiedClassNames,
                                                             oracle.getProjectCollectionTypes() ) );
        dataModel.setDslConditionSentences( oracle.getPackageDslConditionSentences() );
        dataModel.setDslActionSentences( oracle.getPackageDslActionSentences() );
        dataModel.setGlobalTypes( oracle.getPackageGlobals() );
        dataModel.setPackageNames( oracle.getProjectPackageNames() );
    }

    public static void populateDataModel( final PackageDataModelOracle oracle,
                                          final PackageDataModelOracleIncrementalPayload dataModel,
                                          final String usedFullyQualifiedClassName ) {
        final Set<String> usedFullyQualifiedClassNames = new HashSet<String>();
        usedFullyQualifiedClassNames.add( usedFullyQualifiedClassName );
        dataModel.setModelFields( filterModelFields( usedFullyQualifiedClassNames,
                                                     oracle.getProjectModelFields() ) );
        dataModel.setFieldParametersType( filterFieldParametersTypes( usedFullyQualifiedClassNames,
                                                                      oracle.getProjectFieldParametersType() ) );
        dataModel.setEventTypes( filterEventTypes( usedFullyQualifiedClassNames,
                                                   oracle.getProjectEventTypes() ) );
        dataModel.setTypeSources( filterTypeSources( usedFullyQualifiedClassNames,
                                                     oracle.getProjectTypeSources() ) );
        dataModel.setSuperTypes( filterSuperTypes( usedFullyQualifiedClassNames,
                                                   oracle.getProjectSuperTypes() ) );
        dataModel.setTypeAnnotations( filterTypeAnnotations( usedFullyQualifiedClassNames,
                                                             oracle.getProjectTypeAnnotations() ) );
        dataModel.setTypeFieldsAnnotations( filterTypeFieldsAnnotations( usedFullyQualifiedClassNames,
                                                                         oracle.getProjectTypeFieldsAnnotations() ) );
        dataModel.setMethodInformation( filterMethodInformation( usedFullyQualifiedClassNames,
                                                                 oracle.getProjectMethodInformation() ) );
        dataModel.setCollectionTypes( filterCollectionTypes( usedFullyQualifiedClassNames,
                                                             oracle.getProjectCollectionTypes() ) );
    }

    //Setup Model Fields for lazy loading client-side
    private static Map<String, ModelField[]> setupModelFields( final Set<String> usedFullyQualifiedClassNames,
                                                               final Map<String, ModelField[]> projectModelFields,
                                                               final Map<String, String> packageGlobals ) {
        final Map<String, ModelField[]> scopedModelFields = new HashMap<String, ModelField[]>();
        for ( Map.Entry<String, ModelField[]> e : projectModelFields.entrySet() ) {
            final String mfQualifiedType = e.getKey();
            if ( usedFullyQualifiedClassNames.contains( mfQualifiedType ) ) {
                scopedModelFields.put( mfQualifiedType,
                                       e.getValue() );
            } else if ( packageGlobals.containsValue( mfQualifiedType ) ) {
                scopedModelFields.put( mfQualifiedType,
                                       e.getValue() );
            } else {
                scopedModelFields.put( mfQualifiedType,
                                       makeLazyProxyModelField( e.getValue() ) );
            }
        }
        return scopedModelFields;
    }

    //AsyncPackageDataModelOracle.getFactNameFromType() uses THIS to determine the simple Type from a FQCN.
    //Therefore ensure we provide this minimal information for every Type in the DMO to prevent getFactNameFromType()
    //needing a callback to the server which makes things more complicated than really needed.
    private static ModelField[] makeLazyProxyModelField( final ModelField[] modelFields ) {
        for ( ModelField modelField : modelFields ) {
            if ( DataType.TYPE_THIS.equals( modelField.getName() ) ) {
                final ModelField[] result = new ModelField[ 1 ];
                //LazyModelField is a place-holder to tell AsyncPackageDataModelOracle that it needs to load more information
                result[ 0 ] = new LazyModelField( modelField.getName(),
                                                  modelField.getClassName(),
                                                  modelField.getClassType(),
                                                  modelField.getOrigin(),
                                                  modelField.getAccessorsAndMutators(),
                                                  modelField.getType() );
                return result;
            }
        }
        return null;
    }

    //Filter Model Fields by the types used
    private static Map<String, ModelField[]> filterModelFields( final Set<String> usedFullyQualifiedClassNames,
                                                                final Map<String, ModelField[]> projectModelFields ) {
        final Map<String, ModelField[]> scopedModelFields = new HashMap<String, ModelField[]>();
        for ( Map.Entry<String, ModelField[]> e : projectModelFields.entrySet() ) {
            final String mfQualifiedType = e.getKey();
            final ModelField[] mfModelFields = e.getValue();
            if ( isTypeUsed( mfQualifiedType,
                             usedFullyQualifiedClassNames ) ) {
                scopedModelFields.put( mfQualifiedType,
                                       mfModelFields );
            }
        }
        return scopedModelFields;
    }

    //Filter Collection Types by the types used
    private static Map<String, Boolean> filterCollectionTypes( final Set<String> usedFullyQualifiedClassNames,
                                                               final Map<String, Boolean> projectCollectionTypes ) {
        final Map<String, Boolean> scopedCollectionTypes = new HashMap<String, Boolean>();
        for ( Map.Entry<String, Boolean> e : projectCollectionTypes.entrySet() ) {
            final String collectionQualifiedType = e.getKey();
            if ( isTypeUsed( collectionQualifiedType,
                             usedFullyQualifiedClassNames ) ) {
                scopedCollectionTypes.put( collectionQualifiedType,
                                           e.getValue() );

            }
        }
        return scopedCollectionTypes;
    }

    //Filter Event Types by the types used
    private static Map<String, Boolean> filterEventTypes( final Set<String> usedFullyQualifiedClassNames,
                                                          final Map<String, Boolean> projectEventTypes ) {
        final Map<String, Boolean> scopedEventTypes = new HashMap<String, Boolean>();
        for ( Map.Entry<String, Boolean> e : projectEventTypes.entrySet() ) {
            final String eventQualifiedType = e.getKey();
            if ( isTypeUsed( eventQualifiedType,
                             usedFullyQualifiedClassNames ) ) {
                scopedEventTypes.put( eventQualifiedType,
                                      e.getValue() );
            }
        }
        return scopedEventTypes;
    }

    //Filter TypeSource by the types used
    private static Map<String, TypeSource> filterTypeSources( final Set<String> usedFullyQualifiedClassNames,
                                                              final Map<String, TypeSource> projectTypeSources ) {
        final Map<String, TypeSource> scopedTypeSources = new HashMap<String, TypeSource>();
        for ( Map.Entry<String, TypeSource> e : projectTypeSources.entrySet() ) {
            final String typeQualifiedType = e.getKey();
            if ( isTypeUsed( typeQualifiedType,
                             usedFullyQualifiedClassNames ) ) {
                scopedTypeSources.put( typeQualifiedType,
                                       e.getValue() );
            }
        }
        return scopedTypeSources;
    }

    //Filter Super Types by the types used
    private static Map<String, List<String>> filterSuperTypes( final Set<String> usedFullyQualifiedClassNames,
                                                               final Map<String, List<String>> projectSuperTypes ) {
        final Map<String, List<String>> scopedSuperTypes = new HashMap<String, List<String>>();
        for ( Map.Entry<String, List<String>> e : projectSuperTypes.entrySet() ) {
            final String typeQualifiedType = e.getKey();
            final List<String> superTypeQualifiedTypes = e.getValue();
            if ( isTypeUsed( typeQualifiedType,
                             usedFullyQualifiedClassNames ) ) {
                scopedSuperTypes.put( typeQualifiedType,
                                      superTypeQualifiedTypes );
            }
        }
        return scopedSuperTypes;
    }

    //Filter Type Annotations by the types used
    private static Map<String, Set<Annotation>> filterTypeAnnotations( final Set<String> usedFullyQualifiedClassNames,
                                                                       final Map<String, Set<Annotation>> projectTypeAnnotations ) {
        final Map<String, Set<Annotation>> scopedTypeAnnotations = new HashMap<String, Set<Annotation>>();
        for ( Map.Entry<String, Set<Annotation>> e : projectTypeAnnotations.entrySet() ) {
            final String typeAnnotationQualifiedType = e.getKey();
            if ( isTypeUsed( typeAnnotationQualifiedType,
                             usedFullyQualifiedClassNames ) ) {
                scopedTypeAnnotations.put( typeAnnotationQualifiedType,
                                           e.getValue() );
            }
        }
        return scopedTypeAnnotations;
    }

    //Filter Type Fields Annotations by the types used
    private static Map<String, Map<String, Set<Annotation>>> filterTypeFieldsAnnotations( final Set<String> usedFullyQualifiedClassNames,
                                                                                          final Map<String, Map<String, Set<Annotation>>> projectTypeFieldsAnnotations ) {
        final Map<String, Map<String, Set<Annotation>>> scopedTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();
        for ( Map.Entry<String, Map<String, Set<Annotation>>> e : projectTypeFieldsAnnotations.entrySet() ) {
            final String typeAnnotationQualifiedType = e.getKey();
            if ( isTypeUsed( typeAnnotationQualifiedType,
                             usedFullyQualifiedClassNames ) ) {
                scopedTypeFieldsAnnotations.put( typeAnnotationQualifiedType,
                                                 e.getValue() );
            }
        }
        return scopedTypeFieldsAnnotations;
    }

    //Filter Method Information (used by ActionCallXXX and ExpressionBuilder) by the types used
    private static Map<String, List<MethodInfo>> filterMethodInformation( final Set<String> usedFullyQualifiedClassNames,
                                                                          final Map<String, List<MethodInfo>> projectMethodInformation ) {
        final Map<String, List<MethodInfo>> scopedMethodInformation = new HashMap<String, List<MethodInfo>>();
        for ( Map.Entry<String, List<MethodInfo>> e : projectMethodInformation.entrySet() ) {
            final String miQualifiedType = e.getKey();
            if ( isTypeUsed( miQualifiedType,
                             usedFullyQualifiedClassNames ) ) {
                scopedMethodInformation.put( miQualifiedType,
                                             e.getValue() );
            }
        }
        return scopedMethodInformation;
    }

    //Filter Field Parameter Types by the types used
    private static Map<String, String> filterFieldParametersTypes( final Set<String> usedFullyQualifiedClassNames,
                                                                   final Map<String, String> projectFieldParametersTypes ) {
        final Map<String, String> scopedFieldParametersType = new HashMap<String, String>();
        for ( Map.Entry<String, String> e : projectFieldParametersTypes.entrySet() ) {
            final String fieldName = e.getKey();
            final String fieldType = e.getValue();
            final String fFieldName_QualifiedType = getQualifiedTypeFromEncodedFieldName( fieldName );
            if ( isTypeUsed( fFieldName_QualifiedType,
                             usedFullyQualifiedClassNames ) ) {
                scopedFieldParametersType.put( fieldName,
                                               fieldType );
            }
        }
        return scopedFieldParametersType;
    }

    private static String getQualifiedTypeFromEncodedFieldName( final String encodedFieldName ) {
        String typeName = encodedFieldName;
        int hashIndex = typeName.lastIndexOf( "#" );
        if ( hashIndex != -1 ) {
            typeName = typeName.substring( 0,
                                           hashIndex );
        }
        return typeName;
    }

    private static boolean isTypeUsed( final String fullyQualifiedClassName,
                                       final Set<String> usedFullyQualifiedClassNames ) {
        return usedFullyQualifiedClassNames.contains( fullyQualifiedClassName );
    }

}
