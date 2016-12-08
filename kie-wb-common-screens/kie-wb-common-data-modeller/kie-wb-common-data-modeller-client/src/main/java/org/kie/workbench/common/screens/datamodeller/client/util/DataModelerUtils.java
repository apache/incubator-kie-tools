/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.kie.workbench.common.screens.datamodeller.model.droolsdomain.DroolsDomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.services.datamodel.util.SortHelper;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.JavaEnum;
import org.kie.workbench.common.services.datamodeller.core.JavaType;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Pair;

public class DataModelerUtils {

    public static final String EXTERNAL_PREFIX = "- ext - ";
    public static final String CLIPPED_MARKER = "...";

    /*
     * Returns the data-object's class name or the label, in case the object has one.
     */
    public static String getDataObjectUILabel( DataObject dataObject ) {
        if ( dataObject != null ) {
            String label = AnnotationValueHandler.getStringValue( dataObject, MainDomainAnnotations.LABEL_ANNOTATION );
            if ( label == null ) {
                label = dataObject.getName();
            }
            return label;
        }
        return "";
    }

    public static String getMaxLengthClippedString( String s,
                                                    int maxLength ) {
        return s.length() > maxLength ? s.substring( 0, maxLength ) + CLIPPED_MARKER : s;
    }

    public static String getDataObjectFullLabel( DataObject dataObject ) {
        return getDataObjectFullLabel( dataObject, true );
    }

    /*
     * Returns the data-object's class name or, in case the object has a label, the label followed by the
     * class name between brackets.
     */
    public static String getDataObjectFullLabel( DataObject dataObject,
                                                 boolean includePackage ) {
        StringBuilder sb = new StringBuilder( "" );
        if ( dataObject != null ) {
            sb.append( includePackage ? dataObject.getClassName() : dataObject.getName() );
            String objectLabel = AnnotationValueHandler.getStringValue( dataObject, MainDomainAnnotations.LABEL_ANNOTATION );
            if ( objectLabel != null ) {
                sb.insert( 0, objectLabel + " (" ).append( ")" );
            }
        }
        return sb.toString();
    }

    public static String assembleClassName( String objPackage,
                                            String objName ) {
        if ( objName == null || objName.length() == 0 ) {
            return null;
        }
        StringBuilder sb = new StringBuilder( objName );
        if ( objPackage != null && !"".equals( objPackage ) ) {
            sb.insert( 0, "." ).insert( 0, objPackage );
        }
        return sb.toString();
    }

    public static String extractClassName( String fullClassName ) {

        if ( fullClassName == null ) {
            return null;
        }
        int index = fullClassName.lastIndexOf( "." );
        if ( index > 0 ) {
            return fullClassName.substring( index + 1, fullClassName.length() );

        } else {
            return fullClassName;
        }
    }

    public static String extractPackageName( String fullClassName ) {
        if ( fullClassName == null ) {
            return null;
        }
        int index = fullClassName.lastIndexOf( "." );
        if ( index > 0 ) {
            return fullClassName.substring( 0, index );

        } else {
            return null;
        }
    }

    public static String extractSimpleFileName( Path path ) {
        if ( path == null ) {
            return null;
        }
        String fileNameWithExtension = path.getFileName();
        return fileNameWithExtension.substring( 0, fileNameWithExtension.lastIndexOf( "." ) );
    }

    public static String[] getPackageTerms( String packageName ) {
        return packageName.split( "\\.", -1 );
    }

    public static String[] calculateSubPackages( String packageName ) {
        String packageTerms[];
        String subpackages[];

        if ( packageName == null || ( packageTerms = getPackageTerms( packageName ) ) == null ) {
            return null;
        }

        subpackages = new String[ packageTerms.length ];
        for ( int i = 0; i < packageTerms.length; i++ ) {
            String subpackage = "";
            for ( int j = 0; j <= i; j++ ) {
                subpackage += packageTerms[ j ];
                if ( j < i ) {
                    subpackage += ".";
                }
            }
            subpackages[ i ] = subpackage;
        }
        return subpackages;
    }

    public static String unCapitalize( String str ) {
        int strLen = str != null ? str.length() : 0;
        if ( strLen == 0 ) {
            return str;
        }
        if ( strLen > 1 && Character.isUpperCase( str.charAt( 0 ) ) && Character.isUpperCase( str.charAt( 1 ) ) ) {
            return str;
        } else {
            return new StringBuffer( strLen )
                    .append( Character.toLowerCase( str.charAt( 0 ) ) )
                    .append( str.substring( 1 ) )
                    .toString();
        }
    }

    public static List<ObjectProperty> getFieldsUsingPosition( DataObject dataObject,
                                                               int position,
                                                               String skipField ) {
        List<ObjectProperty> fields = new ArrayList<ObjectProperty>();
        if ( dataObject != null && dataObject.getProperties() != null ) {
            for ( ObjectProperty property : dataObject.getProperties() ) {

                if ( skipField != null && skipField.equals( property.getName() ) ) {
                    continue;
                }

                String currentPosition = AnnotationValueHandler.getStringValue(
                        property.getAnnotation( DroolsDomainAnnotations.POSITION_ANNOTATION ),
                        DroolsDomainAnnotations.VALUE_PARAM );
                if ( currentPosition != null && currentPosition.trim().equals( position + "" ) ) {
                    fields.add( property );
                }
            }
        }
        return fields;
    }

    public static Integer getMaxPosition( DataObject dataObject ) {
        List<ObjectProperty> properties = dataObject.getProperties();
        Integer maxPosition = -1;
        Integer currentPosition;
        if ( properties != null && properties.size() > 0 ) {
            for ( ObjectProperty property : properties ) {
                try {
                    currentPosition = new Integer( AnnotationValueHandler.getStringValue( property, DroolsDomainAnnotations.POSITION_ANNOTATION, "value", "-1" ) );
                } catch ( Exception e ) {
                    currentPosition = -1;
                }
                if ( currentPosition > maxPosition ) {
                    maxPosition = currentPosition;
                }
            }
        }
        return maxPosition;
    }

    public static boolean hasPosition( ObjectProperty property ) {
        return property != null && property.getAnnotation( DroolsDomainAnnotations.POSITION_ANNOTATION ) != null;
    }

    public static List<ObjectProperty> getManagedProperties( DataObject dataObject ) {
        List<ObjectProperty> editableProperties = new ArrayList<ObjectProperty>();

        if ( dataObject != null && dataObject.getProperties() != null ) {
            for ( ObjectProperty property : dataObject.getProperties() ) {
                if ( isManagedProperty( property ) ) {
                    editableProperties.add( property );
                }
            }
        }
        return editableProperties;
    }

    public static boolean isManagedProperty( ObjectProperty property ) {
        return !property.isFinal() && !property.isStatic();
    }

    public static String calculateExpectedClassName( Path projectRootPath,
                                                     Path javaFilePath ) {
        if ( projectRootPath == null || javaFilePath == null ) {
            return null;
        }
        return calculateExpectedClassName( projectRootPath.toURI(), javaFilePath.toURI() );
    }

    public static String calculateExpectedClassName( String projectRootPathUri,
                                                     String javaFilePathUri ) {
        String srcPathStrUri = projectRootPathUri + "/src/main/java/";
        if ( !javaFilePathUri.startsWith( srcPathStrUri ) ) {
            return null;
        }

        javaFilePathUri = javaFilePathUri.substring( srcPathStrUri.length(), javaFilePathUri.length() );

        int extensionIndex = javaFilePathUri.lastIndexOf( ".java" );
        if ( extensionIndex <= 0 ) {
            return null;
        }

        javaFilePathUri = javaFilePathUri.substring( 0, extensionIndex );
        return javaFilePathUri.replaceAll( "/", "." );
    }

    public static List<ObjectProperty> filterPropertiesByType( Collection<ObjectProperty> properties,
                                                               List<String> expectedTypes,
                                                               boolean skipUnmanaged ) {

        final ArrayList<ObjectProperty> result = new ArrayList<ObjectProperty>();
        if ( properties == null || properties.size() == 0 ) {
            return result;
        }

        final Map<String, String> types = new HashMap<String, String>();
        if ( expectedTypes != null && expectedTypes.size() > 0 ) {
            for ( String type : expectedTypes ) {
                types.put( type, type );
            }
        } else {
            return result;
        }

        for ( ObjectProperty propertyTO : properties ) {
            if ( propertyTO.getClassName() != null && types.containsKey( propertyTO.getClassName() ) ) {

                if ( skipUnmanaged && ( ReflectionUtil.isStatic( propertyTO.getModifiers() ) || ReflectionUtil.isFinal( propertyTO.getModifiers() ) ) ) {
                    continue;
                }

                result.add( propertyTO );
            }
        }

        return result;
    }

    public static List<Pair<String, String>> buildFieldTypeOptions( final Collection<PropertyType> baseTypes,
            final Collection<DataObject> dataObjects,
            final Collection<JavaEnum> javaEnum,
            final Collection<DataObject> externalClasses,
            final Collection<JavaEnum> externalEnums,
            final boolean includeEmptyItem ) {
        return buildFieldTypeOptions( baseTypes, dataObjects, javaEnum, externalClasses, externalEnums, null, includeEmptyItem );
    }

    public static List<Pair<String, String>> buildFieldTypeOptions( final Collection<PropertyType> baseTypes,
            final Collection<DataObject> dataObjects,
            final Collection<JavaEnum> javaEnums,
            final Collection<DataObject> externalClasses,
            final Collection<JavaEnum> externalEnums,
            final String selectedType,
            final boolean includeEmptyItem ) {

        List<Pair<String, String>> typeList = new ArrayList<Pair<String, String>>( );
        Collection<JavaType> javaTypes = new ArrayList<JavaType>( );
        Collection<JavaType> externalJavaTypes = new ArrayList<JavaType>( );
        SortedMap<String, String> sortedModelTypeNames = new TreeMap<String, String>( SortHelper.ALPHABETICAL_ORDER_COMPARATOR );
        SortedMap<String, String> sortedExternalTypeNames = new TreeMap<String, String>( SortHelper.ALPHABETICAL_ORDER_COMPARATOR );
        Map<String, PropertyType> orderedBaseTypes = new TreeMap<String, PropertyType>( SortHelper.ALPHABETICAL_ORDER_COMPARATOR );
        Map<String, PropertyType> baseTypesByClassName = new TreeMap<String, PropertyType>( SortHelper.ALPHABETICAL_ORDER_COMPARATOR );
        boolean selectedTypeIncluded = false;

        if ( includeEmptyItem ) {
            typeList.add( UIUtil.emptyValue() );
        }

        if ( baseTypes != null ) {
            for ( PropertyType type : baseTypes ) {
                orderedBaseTypes.put( type.getName(), type );
                baseTypesByClassName.put( type.getClassName(), type );
            }
        }

        // First add all base types, ordered
        for ( Map.Entry<String, PropertyType> baseType : orderedBaseTypes.entrySet() ) {
            if ( !baseType.getValue().isPrimitive() ) {
                typeList.add( new Pair( baseType.getKey(), baseType.getValue().getClassName() ) );
            }
        }

        // collect all model types, ordered
        if ( dataObjects != null ) {
            javaTypes.addAll( dataObjects );
        }
        if ( javaEnums != null ) {
            javaTypes.addAll( javaEnums );
        }
        for ( JavaType javaType : javaTypes ) {
            String className = javaType.getClassName();
            String classLabel;

            if ( javaType instanceof DataObject ) {
                classLabel = DataModelerUtils.getDataObjectFullLabel( (DataObject) javaType );
            } else {
                classLabel = javaType.getClassName();
            }
            sortedModelTypeNames.put( classLabel, className );
            if ( selectedType != null && selectedType.equals( className ) ) {
                selectedTypeIncluded = true;
            }
        }

        // collect external types, ordered
        if ( externalClasses != null ) {
            externalJavaTypes.addAll( externalClasses );
        }
        if ( externalEnums != null ) {
            externalJavaTypes.addAll( externalEnums );
        }

        if ( externalClasses != null ) {
            for ( JavaType externalJavaType : externalJavaTypes ) {
                String extClass = externalJavaType.getClassName();
                sortedExternalTypeNames.put( DataModelerUtils.EXTERNAL_PREFIX + extClass, extClass );
                if ( selectedType != null && selectedType.equals( extClass ) ) {
                    selectedTypeIncluded = true;
                }
            }
        }

        //check selectedType isn't present
        if ( selectedType != null && !selectedTypeIncluded && !baseTypesByClassName.containsKey( selectedType ) ) {
            //uncommon case. A field was loaded but the class isn't within the model or external classes.

            String extClass = selectedType;
            sortedExternalTypeNames.put( DataModelerUtils.EXTERNAL_PREFIX + extClass, extClass );
        }

        //add project classes to the selector.
        for ( Map.Entry<String, String> typeName : sortedModelTypeNames.entrySet() ) {
            typeList.add( new Pair<String, String>( typeName.getKey(), typeName.getValue() ) );
        }

        //add external classes to the selector.
        for ( Map.Entry<String, String> typeName : sortedExternalTypeNames.entrySet() ) {
            typeList.add( new Pair<String, String>( typeName.getKey(), typeName.getValue() ) );
        }

        //finally add primitives
        for ( Map.Entry<String, PropertyType> baseType : orderedBaseTypes.entrySet() ) {
            if ( baseType.getValue().isPrimitive() ) {
                typeList.add( new Pair<String, String>( baseType.getKey(), baseType.getValue().getClassName() ) );
            }
        }

        return typeList;
    }

    public static List<Pair<String, String>> buildSuperclassOptions( DataModel dataModel,
            DataObject currentDataObject ) {

        List<Pair<String, String>> options = new ArrayList<Pair<String, String>>();

        if ( dataModel != null ) {
            SortedMap<String, String> sortedModelClasses = new TreeMap<String, String>( SortHelper.ALPHABETICAL_ORDER_COMPARATOR );
            SortedMap<String, String> sortedExternalClasses = new TreeMap<String, String>( SortHelper.ALPHABETICAL_ORDER_COMPARATOR );
            boolean isExtensible = false;
            String className;
            String classLabel;
            String currentClassName;


            // first, all data objects form this model in order
            for ( DataObject internalDataObject : dataModel.getDataObjects() ) {
                className = internalDataObject.getClassName();
                classLabel = getDataObjectFullLabel( internalDataObject );
                isExtensible = !internalDataObject.isAbstract() && !internalDataObject.isFinal() && !internalDataObject.isInterface();
                if ( isExtensible ) {
                    if ( currentDataObject != null && className.toLowerCase().equals( currentDataObject.getClassName().toLowerCase() ) )
                        continue;
                    sortedModelClasses.put( classLabel, className );
                }
            }

            // Then add all external types, ordered
            for ( DataObject externalDataObject : dataModel.getExternalClasses() ) {
                className = externalDataObject.getClassName();
                classLabel = EXTERNAL_PREFIX + className;
                isExtensible = !externalDataObject.isAbstract() && !externalDataObject.isFinal() && !externalDataObject.isInterface();
                if ( isExtensible ) {
                    if ( currentDataObject != null && className.toLowerCase().equals( currentDataObject.getClassName().toLowerCase() ) )
                        continue;
                    sortedExternalClasses.put( classLabel, className );
                }
            }

            if ( currentDataObject != null && currentDataObject.getSuperClassName() != null ) {
                currentClassName = currentDataObject.getSuperClassName();
                if ( !sortedModelClasses.containsKey( currentClassName ) && !sortedExternalClasses.containsKey( currentClassName ) ) {
                    //the model was loaded but the super class is not a model class nor an external class, e.g. java.lang.Object. Still needs to be loaded.
                    sortedModelClasses.put( currentClassName, currentClassName );
                }
            }

            for ( Map.Entry<String, String> classNameEntry : sortedModelClasses.entrySet() ) {
                options.add( new Pair( classNameEntry.getKey(), classNameEntry.getValue()) );
            }

            for ( Map.Entry<String, String> classNameEntry : sortedExternalClasses.entrySet() ) {
                options.add( new Pair( classNameEntry.getKey(), classNameEntry.getValue()) );
            }
        }
        return options;
    }

    public static final String nullTrim( String value ) {
        String result = value != null ? value.trim() : value;
        if ( result != null && !"".equals( result ) ) {
            return result;
        } else {
            return null;
        }
    }

    public static final String trim( String value ) {
        return value != null ? value.trim() : value;
    }
}