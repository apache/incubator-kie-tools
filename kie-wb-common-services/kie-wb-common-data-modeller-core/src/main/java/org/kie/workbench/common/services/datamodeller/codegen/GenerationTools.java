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

package org.kie.workbench.common.services.datamodeller.codegen;

import org.apache.commons.lang.StringUtils;
import org.kie.workbench.common.services.datamodeller.annotations.Equals;
import org.kie.workbench.common.services.datamodeller.core.*;
import org.kie.workbench.common.services.datamodeller.driver.impl.annotations.EqualsAnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.driver.impl.annotations.PositionAnnotationDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Helper tools to generate names and other stuff easily from code generation engine.
 */
public class GenerationTools {

    private static final Logger logger = LoggerFactory.getLogger(GenerationTools.class);
    private static final String TAB = "    ";
    private static final String EOL = System.getProperty("line.separator");
    
    public String fitToSize(int size, String name, char padChar) {
        int n = size - name.length();

        StringBuffer buf = new StringBuffer();

        buf.append(name);

        for (int i = 0; i < n; i++) {
            buf.append(padChar);
        }

        return buf.toString();
    }

/*
    public String toJavaClass(String name) {

        return toJavaName(name, true);
    }

    public String toJavaMethod(String name) {

        return toJavaName(name, false);
    }
*/

    public String toJavaGetter(String name) {

        return "get" + StringUtils.capitalize(name);
    }

    public String toJavaSetter(String name) {

        return "set" + StringUtils.capitalize(name);
    }

    private String toJavaName(String name, boolean firstLetterIsUpperCase) {

        name = name.toLowerCase();

        StringBuffer res = new StringBuffer();

        boolean nextIsUpperCase = firstLetterIsUpperCase;

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            if (nextIsUpperCase) {
                c = Character.toUpperCase(c);
            }

            if (Character.isLetter(c)) {
                res.append(c);
                nextIsUpperCase = false;
            } else {
                nextIsUpperCase = true;
            }
        }

        return res.toString();
    }

    public String toJavaVar(String name) {
        return name;
    }

    public String getFilePath(String packageName, String simpleClassName, String extension) {
        if( packageName != null) {
            packageName = "/" + packageName.replace(".", "/");
        } else {
            packageName = "";
        }

        return packageName + "/" + toFileName(simpleClassName) + "." + extension;
    }

    public String toFileName(String name) {
        return name.replaceAll("\\s", "");
    }

    public String resolveAttributeType(ObjectProperty attribute) {
        StringBuffer type = new StringBuffer("");
        if (attribute.isMultiple()) {
            if (attribute.getBag() != null && !"".equals(attribute.getBag())) {
                type.append(attribute.getBag());
            } else {
                type.append("java.util.List");
            }
            type.append("<");
        }
        type.append(attribute.getClassName());
        if (attribute.isMultiple()) {
            type.append(">");
        }
        return type.toString();
    }

    public String resolveAnnotationType (Annotation annotation) {
        StringBuffer type = new StringBuffer();
        AnnotationDefinition annotationDefinition = annotation.getAnnotationDefinition();

        if (annotationDefinition == null) {
            logger.warn("Annotation definition for annotation: " + annotation + " is not defined.");
            return type.toString();
        }
        
        if (annotationDefinition.isMarker()) {
            return type.toString();
        }

        //finally we can process annotation members.
        Object memberValue;
        int memberCount = 0;
        for (AnnotationMemberDefinition memberDefinition : annotationDefinition.getAnnotationMembers()) {
            if ( (memberValue = annotation.getValue(memberDefinition.getName())) != null) {
                //a value has been set for this member.
                if (memberCount == 0) type.append("(");
                if (memberCount > 0) type.append(", ");
                type.append(resolveMemberType(memberDefinition, memberValue));
                memberCount++;
            }
        }
        if (memberCount > 0) type.append(")");

        return type.toString();
    }
    
    public String resolveMemberType(AnnotationMemberDefinition memberDefinition, Object value) {
        StringBuffer type = new StringBuffer();

        type.append(memberDefinition.getName());
        type.append(" = ");

        if (memberDefinition.isEnum()) {
            type.append(memberDefinition.getClassName());
            type.append(".");
            type.append(value);
        } else if (memberDefinition.isString()) {
            type.append("\"");
            type.append(value);
            type.append("\"");
        } else if (memberDefinition.isPrimitiveType()) {
            //primitive types are wrapped by the java.lang.type.

            if (Character.class.getName().equals(memberDefinition.getClassName())) {
                type.append("'");
                type.append(value.toString());
                type.append("'");
            } else if (Long.class.getName().equals(memberDefinition.getClassName())) {
                type.append(value.toString());
                type.append("L");
            } else if (Float.class.getName().equals(memberDefinition.getClassName())) {
                type.append(value.toString());
                type.append("f");
            } else if (Double.class.getName().equals(memberDefinition.getClassName())) {
                type.append(value.toString());
                type.append("d");
            } else {
                type.append(value.toString());
            }

        }
        return type.toString();
    }
    
    public String resolveSuperClassType(DataObject dataObject) {
        StringBuffer type = new StringBuffer("");
        if (dataObject.getSuperClassName() != null && !"".equals(dataObject.getSuperClassName())) {
            type.append("extends ");
            type.append(dataObject.getSuperClassName());
        }
        return type.toString();
    }

    public String resolveImplementedInterfacesType(DataObject dataObject) {
        StringBuffer type = new StringBuffer("");
        type.append("implements java.io.Serializable");
        return type.toString();
    }

    public String resolveEquals(DataObject dataObject, String indent) {
        
        StringBuilder head = new StringBuilder();
        //head.append(EOL);
        head.append(indent + "@Override" + EOL);
        head.append(indent + "public boolean equals(Object o) {" + EOL);
        head.append(indent + TAB + "if (this == o) return true;" + EOL);
        head.append(indent + TAB + "if (o == null || getClass() != o.getClass()) return false;" + EOL);
        head.append(indent + TAB + dataObject.getClassName() + " that = ("+ dataObject.getClassName() + ")o;" + EOL);

        StringBuilder end = new StringBuilder();
        end.append(indent + "}");

        StringBuilder sb = new StringBuilder();
        Map<String, ObjectProperty> props = dataObject.getProperties();

        boolean hasTerms = false;
        if (props != null && props.size() > 0) {
            for(String propName : props.keySet()) {
                ObjectProperty prop = props.get(propName);
                String _propName = toJavaVar(propName);
                if (prop.getAnnotation(Equals.class.getName()) != null) {
                    // Construction: "if (<_propName> != null ? !<_propName>.equals(that.<_propName>) : that.<_propName> != null) return false;"
                    sb.append(indent + TAB + "if (");
                    sb.append(_propName).append(" != null ? !").append(_propName).append(".equals(that.").append(_propName).append(")");
                    sb.append(" : that.").append(_propName).append(" != null").append(") return false;");
                    sb.append(EOL);
                    hasTerms = true;
                }
            }
        }

        if (hasTerms) {
            sb.append(indent + TAB + "return true;" + EOL);
            head.append(sb);
            head.append(end);
            return head.toString();
        } else {
            return "";
        }
    }

    public String resolveHashCode(DataObject dataObject, String indent) {

        StringBuilder head = new StringBuilder();
        //head.append(EOL);
        head.append(indent + "@Override" + EOL);
        head.append(indent + "public int hashCode() {" + EOL);
        head.append(indent + TAB + "int result = 17;" + EOL);

        StringBuilder end = new StringBuilder();
        end.append(indent + "}");

        StringBuilder sb = new StringBuilder();
        Map<String, ObjectProperty> props = dataObject.getProperties();

        boolean hasTerms = false;
        if (props != null && props.size() > 0) {
            for(String propName : props.keySet()) {
                ObjectProperty prop = props.get(propName);
                String _propName = toJavaVar(propName);
                if (prop.getAnnotation(Equals.class.getName()) != null) {
                    // Construction: "result = 13 * result + (<_propName> != null ? <_propName>.hashCode() : 0);"
                    sb.append(indent + TAB + "result = 13 * result + (").append(_propName).append(" != null ? ").append(_propName).append(".hashCode() : 0);");
                    sb.append(EOL);
                    hasTerms = true;
                }
            }
        }

        if (hasTerms) {
            sb.append(indent + TAB + "return result;" + EOL);
            head.append(sb);
            head.append(end);
            return head.toString();
        } else {
            return "";
        }
    }

    public String resolveAllFieldsConstructor(DataObject dataObject, String indent) {
        if (!dataObject.getProperties().isEmpty()) {
            List<ObjectProperty> sortedProperties = new ArrayList<ObjectProperty>();
            sortedProperties.addAll(dataObject.getProperties().values());
            return resolveConstructor(dataObject, sortByPosition(sortedProperties), indent);
        }
        return "";
    }

    public String resolveKeyFieldsConstructor(DataObject dataObject, String indent) {
        if (!dataObject.getProperties().isEmpty()) {
            List<ObjectProperty> sortedProperties = new ArrayList<ObjectProperty>();
            for (ObjectProperty property : dataObject.getProperties().values()) {
                if (property.getAnnotation(EqualsAnnotationDefinition.getInstance().getClassName()) != null) {
                    //the property is marked as key.
                    sortedProperties.add(property);
                }
            }
            if (sortedProperties.size() > 0 && sortedProperties.size() < dataObject.getProperties().size()) {
                return resolveConstructor(dataObject, sortByPosition(sortedProperties), indent);
            }
        }
        return "";
    }

    private List<ObjectProperty> sortByPosition(List<ObjectProperty> properties) {
        Collections.sort(properties, new Comparator<ObjectProperty>() {
            public int compare(ObjectProperty o1, ObjectProperty o2) {

                if (o1 == null && o2 == null) return 0;
                if (o1 == null && o2 != null) return -1;
                if (o1 != null && o2 == null) return 1;

                Comparable key1 = null;
                Comparable key2 = null;

                Annotation position1 = o1.getAnnotation(PositionAnnotationDefinition.getInstance().getClassName());
                if (position1 != null) {
                    try {
                        key1 = new Integer((String)position1.getValue("value"));
                    } catch (NumberFormatException e) {
                        key1 = null;
                    }
                }
                
                Annotation position2 = o2.getAnnotation(PositionAnnotationDefinition.getInstance().getClassName());
                if (position2 != null) {
                    try {
                        key2 = new Integer((String)position2.getValue("value"));
                    } catch (NumberFormatException e) {
                        key2 = null;
                    }
                }

                if (key1 == null && key2 == null) return 0;
                if (key1 != null && key2 != null) return key1.compareTo(key2);

                if (key1 == null && key2 != null) return -1;

                //if (key1 != null && key2 == null) return 1;
                return 1;
            }
        } );
        return properties;
    }

    public String resolveConstructor(DataObject dataObject, List<ObjectProperty> properties, String indent) {

        StringBuilder head = new StringBuilder();
        StringBuilder body = new StringBuilder();

        head.append(indent + "public " + dataObject.getName() + "(");

        if (properties != null && properties.size() > 0) {
            boolean isFirst = true;
            String propertyName;
            for (ObjectProperty property : properties) {
                if (!isFirst) {
                    head.append(", ");
                    body.append(EOL);
                }
                propertyName = toJavaVar(property.getName());
                head.append(resolveAttributeType(property));
                head.append(" ");
                head.append(propertyName);

                body.append(indent);
                body.append(indent);
                body.append("this.");
                body.append(propertyName);
                body.append(" = ");
                body.append(propertyName);
                body.append(";");

                isFirst = false;
            }
            body.append(EOL);
        }

        head.append(") {" + EOL);
        head.append(body);
        head.append(indent + "}");

        return head.toString();
    }
}
