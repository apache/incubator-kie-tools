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

import java.util.List;

public class DataModelerUtils {

    public static final String EXTERNAL_PREFIX = "- ext - ";
    public static final String CLIPPED_MARKER = "...";
    public static final String MULTIPLE = " [0..N]";

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
        if (dataObject != null) {
            String label = dataObject.getLabel();
            if (label != null) return label;
            else return dataObject.getName();
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

    public String unCapitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen)
                .append(Character.toLowerCase(str.charAt(0)))
                .append(str.substring(1))
                .toString();
    }
    
    public Integer getMaxPosition(DataObjectTO dataObjectTO) {
        List<ObjectPropertyTO> properties = dataObjectTO.getProperties();
        Integer maxPosition = -1;
        Integer currentPosition;
        if (properties != null && properties.size() > 0) {
            for (ObjectPropertyTO property : properties) {
                try {
                    currentPosition = new Integer( AnnotationValueHandler.getInstance().getStringValue(property, AnnotationDefinitionTO.POSITION_ANNOTATON, "value", "-1") );
                } catch (Exception e) {
                    currentPosition = -1;
                }
                if (currentPosition > maxPosition) maxPosition = currentPosition;
            }
        }
        return maxPosition;
    }
}