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

package org.kie.workbench.common.screens.datamodeller.client.util;

import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;
import org.uberfire.backend.vfs.Path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataModelerUtils {

    public static final String EXTERNAL_PREFIX = "- ext - ";
    public static final String CLIPPED_MARKER = "...";
    public static final String MULTIPLE = " [0..N]";

    public static final String BYTE  = "byte";
    public static final String SHORT = "short";
    public static final String INT = "int";
    public static final String LONG = "long";
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";
    public static final String CHAR = "char";
    public static final String BOOLEAN = "boolean";

    public static DataModelerUtils getInstance() {
        return new DataModelerUtils();
    }

    public static Boolean isMultipleType(String type) {
        return type.lastIndexOf(DataModelerUtils.MULTIPLE) >= 0;
    }

    // Returns the object's type without the multiple ('[0..N]') extension, in case of a multiple type.
    public static String getCanonicalClassName(String type) {
        if (type != null && !"".equals(type)) {
            if (isMultipleType(type)) {
                int i = type.lastIndexOf(DataModelerUtils.MULTIPLE);
                return type.substring(0, i);
            }
        }
        return type;
    }

    /*
     * Returns the data-object's class name or the label, in case the object has one.
     */
    public static String getDataObjectUILabel(DataObjectTO dataObject) {
        return getDataObjectUILabel(dataObject, false, null);
    }

    /*
     * Returns the data-object's class name or the label, in case the object has one.
     */
    public static String getDataObjectUILabel(DataObjectTO dataObject, boolean appendReadonlyMark, String readonlyMark) {
        if (dataObject != null) {
            String label = dataObject.getLabel();
            if (label == null) label = dataObject.getName();
            if (appendReadonlyMark && dataObject.isExternallyModified()) {
                label = label + " (" + readonlyMark + ")";
            }
            return label;
        }
        return "";
    }


    public static String getMaxLengthClippedString(String s, int maxLength) {
        return s.length() > maxLength ? s.substring(0, maxLength) + CLIPPED_MARKER : s;
    }

    /*
     * Returns the data-object's class name or, in case the object has a label, the label followed by the
     * class name between brackets.
     */
    public static String getDataObjectFullLabel(DataObjectTO dataObject) {
        StringBuilder sb = new StringBuilder("");
        if (dataObject != null) {
            sb.append(dataObject.getClassName());
            String objectLabel = dataObject.getLabel();
            if (objectLabel != null) sb.insert(0, objectLabel + " (").append(")");
        }
        return sb.toString();
    }

    public static String assembleClassName(String objPackage, String objName) {
        if (objName == null || objName.length() == 0) return null;
        StringBuilder sb = new StringBuilder(objName);
        if ( objPackage != null && !"".equals(objPackage) ) sb.insert(0, ".").insert(0, objPackage);
        return sb.toString();
    }

    public String extractClassName(String fullClassName) {

        if (fullClassName == null) return null;
        int index = fullClassName.lastIndexOf(".");
        if (index > 0) {
            return fullClassName.substring(index+1, fullClassName.length());

        } else {
            return fullClassName;
        }
    }

    public String extractPackageName(String fullClassName) {
        if (fullClassName == null) return null;
        int index = fullClassName.lastIndexOf(".");
        if (index > 0) {
            return fullClassName.substring(0, index);

        } else {
            return null;
        }
    }

    public String[] getPackageTerms(String packageName) {
        return packageName.split("\\.", -1);
    }

    public String[] calculateSubPackages(String packageName) {
        String packageTerms[];
        String subpackages[];

        if (packageName == null || (packageTerms = getPackageTerms(packageName)) == null) return null;

        subpackages = new String[packageTerms.length];
        for (int i = 0; i < packageTerms.length; i++) {
            String subpackage = "";
            for (int j = 0; j <= i; j++) {
                subpackage += packageTerms[j];
                if (j < i) subpackage += ".";
            }
            subpackages[i] = subpackage;
        }
        return subpackages;
    }

    public String unCapitalize(String str) {
        int strLen = str != null ? str.length() : 0;
        if (strLen == 0) return str;
        if (strLen > 1 && Character.isUpperCase(str.charAt(0)) && Character.isUpperCase(str.charAt(1))) {
            return str;
        } else {
            return new StringBuffer(strLen)
                .append(Character.toLowerCase(str.charAt(0)))
                .append(str.substring(1))
                .toString();
        }
    }

    public Integer getMaxPosition(DataObjectTO dataObjectTO) {
        List<ObjectPropertyTO> properties = dataObjectTO.getProperties();
        Integer maxPosition = -1;
        Integer currentPosition;
        if (properties != null && properties.size() > 0) {
            for (ObjectPropertyTO property : properties) {
                try {
                    currentPosition = new Integer( AnnotationValueHandler.getInstance().getStringValue(property, AnnotationDefinitionTO.POSITION_ANNOTATION, "value", "-1") );
                } catch (Exception e) {
                    currentPosition = -1;
                }
                if (currentPosition > maxPosition) maxPosition = currentPosition;
            }
        }
        return maxPosition;
    }

    public void recalculatePositions(DataObjectTO dataObjectTO, Integer positionRemoved) {
        if (dataObjectTO == null || positionRemoved < 0) return;
        List<ObjectPropertyTO> properties = dataObjectTO.getProperties();

        if (properties != null && properties.size() > 0) {
            for (ObjectPropertyTO property : properties) {
                Integer pos = Integer.parseInt(AnnotationValueHandler.getInstance().getStringValue(property, AnnotationDefinitionTO.POSITION_ANNOTATION, AnnotationDefinitionTO.VALUE_PARAM, "-1"), 10);
                if (pos > positionRemoved) {
                    property.getAnnotation(AnnotationDefinitionTO.POSITION_ANNOTATION ).setValue(AnnotationDefinitionTO.VALUE_PARAM, Integer.valueOf(pos-1).toString());
                }
            }
        }
    }

    /**
     * Recalculates the positions among the object's attributes, according to the following:
     *
     * Example 1:
     * fieldPositions: 1 - 2 - 3 - 4 - 5
     *                     +------>     oldPosition = 2, newPosition = 4 (nP > oP)
     * implies: fieldPos 4 becomes 3    or:   when oP < fP <= nP --> fP = fP - 1
     *          fieldPos 3 becomes 2
     *
     *          fieldPos 2 becomes 4    or:   when fP == oP --> fP = nP
     *
     * Example 2:
     * fieldPositions: 1 - 2 - 3 - 4 - 5
     *                      <------+    oldPosition = 4 | newPosition = 2 (nP < oP)
     * implies: fieldPos 2 becomes 3    or:   when nP <= fP < oP --> fP = fP + 1
     *          fieldPos 3 becomes 4
     *
     *          fieldPos 4 becomes 2    or:   when fP == oP --> fP = nP
     */
    public void recalculatePositions(DataObjectTO dataObjectTO, Integer oldPosition, Integer newPosition) {
        if (dataObjectTO == null || oldPosition == -1 || newPosition.equals(oldPosition)) return;
        List<ObjectPropertyTO> properties = dataObjectTO.getProperties();

        if (properties != null && properties.size() > 0) {
            for (ObjectPropertyTO property : properties) {
                String sfieldPos = AnnotationValueHandler.getInstance().getStringValue(property, AnnotationDefinitionTO.POSITION_ANNOTATION, AnnotationDefinitionTO.VALUE_PARAM, "-1");
                if (sfieldPos != null && sfieldPos.length() > 0) {
                    Integer fieldPos = Integer.parseInt(sfieldPos, 10);

                    if (newPosition < oldPosition) {
                        if (fieldPos >= newPosition && fieldPos < oldPosition) {
                            property.getAnnotation(AnnotationDefinitionTO.POSITION_ANNOTATION )
                                    .setValue(AnnotationDefinitionTO.VALUE_PARAM, Integer.valueOf( fieldPos + 1 ).toString());
                        }
                    } else {
                        if (fieldPos <= newPosition && fieldPos > oldPosition) {
                            property.getAnnotation(AnnotationDefinitionTO.POSITION_ANNOTATION )
                                    .setValue(AnnotationDefinitionTO.VALUE_PARAM, Integer.valueOf( fieldPos - 1 ).toString());
                        }
                    }

                    if (fieldPos == oldPosition)
                        property.getAnnotation(AnnotationDefinitionTO.POSITION_ANNOTATION )
                                .setValue(AnnotationDefinitionTO.VALUE_PARAM, newPosition.toString());

                }
            }
        }
    }

    public List<ObjectPropertyTO> getEditableProperties(DataObjectTO dataObjectTO) {
        List<ObjectPropertyTO> editableProperties = new ArrayList<ObjectPropertyTO>( );

        if ( dataObjectTO != null && dataObjectTO.getProperties() != null ) {
            for (ObjectPropertyTO propertyTO : dataObjectTO.getProperties()) {
                if (!ReflectionUtil.isFinal( propertyTO.getModifiers() ) && !ReflectionUtil.isStatic( propertyTO.getModifiers() )) {
                    editableProperties.add( propertyTO );
                }
            }
        }
        return editableProperties;
    }

    public static String calculateExpectedClassName(Path projectRootPath, Path javaFilePath) {
        if (projectRootPath == null || javaFilePath == null) return null;
        return calculateExpectedClassName(projectRootPath.toURI(), javaFilePath.toURI());
    }

    public static String calculateExpectedClassName(String projectRootPathUri, String javaFilePathUri) {
        String srcPathStrUri = projectRootPathUri + "/src/main/java/";
        if (!javaFilePathUri.startsWith(srcPathStrUri)) return null;

        javaFilePathUri = javaFilePathUri.substring(srcPathStrUri.length(), javaFilePathUri.length());

        int extensionIndex = javaFilePathUri.lastIndexOf(".java");
        if (extensionIndex <= 0) return null;

        javaFilePathUri = javaFilePathUri.substring(0, extensionIndex);
        return javaFilePathUri.replaceAll("/", ".");
    }

    public static List<ObjectPropertyTO> filterPropertiesByType(Collection<ObjectPropertyTO> properties, List<String> expectedTypes) {

        final ArrayList<ObjectPropertyTO> result = new ArrayList<ObjectPropertyTO>( );
        if (properties == null || properties.size() == 0) return result;

        final Map<String, String> types = new HashMap<String, String>( );
        if (expectedTypes != null && expectedTypes.size() > 0) {
            for (String type : expectedTypes) {
                types.put( type, type );
            }
        } else {
            return result;
        }

        for ( ObjectPropertyTO propertyTO : properties ) {
            if (propertyTO.getClassName() != null && types.containsKey( propertyTO.getClassName() )) {
                result.add( propertyTO );
            }
        }

        return result;
    }
}