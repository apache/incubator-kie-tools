/**
 * Copyright 2012 JBoss Inc
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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.screens.datamodeller.model.droolsdomain.DroolsDomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.uberfire.backend.vfs.Path;

public class DataModelerUtils {

    public static final String EXTERNAL_PREFIX = "- ext - ";
    public static final String CLIPPED_MARKER = "...";
    public static final String MULTIPLE = " [0..N]";
    public static final String NOT_SELECTED = "NOT_SELECTED";

    public static final String BYTE = "byte";
    public static final String SHORT = "short";
    public static final String INT = "int";
    public static final String LONG = "long";
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";
    public static final String CHAR = "char";
    public static final String BOOLEAN = "boolean";

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

    public static void initList( final Select select,
                                 boolean includeEmptyItem ) {
        select.clear();
        if ( includeEmptyItem ) {
            select.add( emptyOption() );
        }
        refreshSelect( select );
    }

    public static Option newOption( final String text, final String value ) {
        final Option option = new Option();
        option.setValue( value );
        option.setText( text );
        return option;
    }

    public static Option emptyOption() {
        return newOption( "", NOT_SELECTED );
    }

    public static void initTypeList( final Select typeSelector,
                                     final Collection<PropertyType> baseTypes,
                                     final Collection<DataObject> dataObjects,
                                     final Collection<DataObject> externalClasses,
                                     boolean includeEmptyItem ) {
        initTypeList( typeSelector, baseTypes, dataObjects, externalClasses, null, false, includeEmptyItem );
    }

    public static void initTypeList( final Select typeSelector,
                                     final Collection<PropertyType> baseTypes,
                                     final Collection<DataObject> dataObjects,
                                     final Collection<DataObject> externalClasses,
                                     final String selectedType,
                                     boolean selectedTypeMultiple ) {
        initTypeList( typeSelector, baseTypes, dataObjects, externalClasses, selectedType, selectedTypeMultiple, false );
    }

    public static void initTypeList( final Select typeSelector,
                                     final Collection<PropertyType> baseTypes,
                                     final Collection<DataObject> dataObjects,
                                     final Collection<DataObject> externalClasses,
                                     final String selectedType,
                                     boolean selectedTypeMultiple,
                                     boolean includeEmptyItem ) {

        SortedMap<String, String> sortedModelTypeNames = new TreeMap<String, String>();
        SortedMap<String, String> sortedExternalTypeNames = new TreeMap<String, String>();
        Map<String, PropertyType> orderedBaseTypes = new TreeMap<String, PropertyType>();
        Map<String, PropertyType> baseTypesByClassName = new TreeMap<String, PropertyType>();
        boolean selectedTypeIncluded = false;

        if ( baseTypes != null ) {
            for ( PropertyType type : baseTypes ) {
                orderedBaseTypes.put( type.getName(), type );
                baseTypesByClassName.put( type.getClassName(), type );
            }
        }

        initList( typeSelector, includeEmptyItem );

        // First add all base types, ordered
        for ( Map.Entry<String, PropertyType> baseType : orderedBaseTypes.entrySet() ) {
            if ( !baseType.getValue().isPrimitive() ) {

                typeSelector.add( newOption( baseType.getKey(), baseType.getValue().getClassName() ) );
            }
        }

        if ( dataObjects != null ) {
            // collect all model types, ordered
            for ( DataObject dataObject : dataObjects ) {
                String className = dataObject.getClassName();
                String classLabel = DataModelerUtils.getDataObjectFullLabel( dataObject );
                sortedModelTypeNames.put( classLabel, className );
                if ( selectedType != null && selectedType.equals( className ) ) {
                    selectedTypeIncluded = true;
                }
            }
        }

        // collect external types, ordered
        if ( externalClasses != null ) {
            for ( DataObject externalDataObject : externalClasses ) {
                String extClass = externalDataObject.getClassName();
                sortedExternalTypeNames.put( DataModelerUtils.EXTERNAL_PREFIX + extClass, extClass );
                if ( selectedType != null && selectedType.equals( extClass ) ) {
                    selectedTypeIncluded = true;
                }
            }
        }

        //check selectedType isn't present
        if ( selectedType != null && !selectedTypeIncluded && !baseTypesByClassName.containsKey( selectedType ) ) {
            //uncommon case. A field was loaded but the class isn't within the model or externall classes.

            String extClass = selectedType;
            sortedExternalTypeNames.put( DataModelerUtils.EXTERNAL_PREFIX + extClass, extClass );
        }

        //add project classes to the selector.
        for ( Map.Entry<String, String> typeName : sortedModelTypeNames.entrySet() ) {
            typeSelector.add( newOption( typeName.getKey(), typeName.getValue() ) );
        }

        //add external classes to the selector.
        for ( Map.Entry<String, String> typeName : sortedExternalTypeNames.entrySet() ) {
            typeSelector.add( newOption( typeName.getKey(), typeName.getValue() ) );
        }

        //finally add primitives
        for ( Map.Entry<String, PropertyType> baseType : orderedBaseTypes.entrySet() ) {
            if ( baseType.getValue().isPrimitive() ) {
                typeSelector.add( newOption( baseType.getKey(), baseType.getValue().getClassName() ) );
            }
        }

        setSelectedValue( typeSelector, selectedType );
    }

    public static void setSelectedValue( final Select select,
                                         final String value ) {
        select.setValue( value );
        refreshSelect( select );
    }

    public static void refreshSelect( final Select select ) {
        Scheduler.get().scheduleDeferred( new Command() {
            public void execute() {
                select.refresh();
            }
        } );
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