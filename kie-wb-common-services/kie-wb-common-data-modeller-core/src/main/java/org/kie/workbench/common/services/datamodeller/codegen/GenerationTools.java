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
import org.kie.workbench.common.services.datamodeller.core.*;
import org.kie.workbench.common.services.datamodeller.driver.impl.annotations.KeyAnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.driver.impl.annotations.PositionAnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.util.FileHashingUtils;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.kie.workbench.common.services.datamodeller.util.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Helper tools to generate names and other stuff easily from code generation engine.
 */
public class GenerationTools {

    public static final String EOL = System.getProperty("line.separator");

    private static final Logger logger = LoggerFactory.getLogger(GenerationTools.class);
    private static final String TAB = "    ";

    public String fitToSize(int size, String name, char padChar) {
        int n = size - name.length();

        StringBuffer buf = new StringBuffer();

        buf.append(name);

        for (int i = 0; i < n; i++) {
            buf.append(padChar);
        }

        return buf.toString();
    }

    public String toJavaGetter(String name, String className) {
        String prefix = NamingUtils.BOOLEAN.equals(className != null ? className.trim() : null) ? "is" : "get";
        return toJavaAccessor(prefix, name);
    }

    public String toJavaSetter(String name) {
        return toJavaAccessor("set", name);
    }

    private String toJavaAccessor(String prefix, String name) {
        if (name == null || name.length() == 0) return name;

        StringBuilder method = new StringBuilder(prefix);

        if (name.charAt(0) == '_') {
            return method.append(name).toString();
        }

        if (name.length() == 1) {
            return method.append(name.toUpperCase()).toString();
        }

        if (Character.isUpperCase(name.charAt(0))) {
            return method.append(name).toString();
        }

        if (Character.isUpperCase(name.charAt(1))) {
            return method.append(name).toString();
        } else {
            return method.append(StringUtils.capitalize(name)).toString();
        }
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
            type.append(escapeStringForJavaCode(value != null ? value.toString() : null));
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

    public boolean hasSuperClass(DataObject dataObject) {
        return dataObject != null && dataObject.getSuperClassName() != null && !"".equals( dataObject.getSuperClassName() );
    }

    public int keyFieldsCount(DataObject dataObject) {
        int count = 0;
        if (dataObject != null && dataObject.getProperties() != null && !dataObject.getProperties().isEmpty()) {
            for (ObjectProperty prop : dataObject.getProperties().values()) {
                if (prop.getAnnotation(org.kie.api.definition.type.Key.class.getName()) != null) count++;
            }
        }
        return count;
    }

    public int propertiesCount(DataObject dataObject) {
        return (dataObject != null && dataObject.getProperties() != null) ? dataObject.getProperties().size() : 0;
    }

    public boolean hasEquals(DataObject dataObject) {
        return keyFieldsCount( dataObject ) > 0;
    }

    public boolean hasProperties(DataObject dataObject) {
        return propertiesCount( dataObject ) > 0;
    }

    public boolean hasClassAnnotations(DataObject dataObject) {
        return dataObject != null && dataObject.getAnnotations() != null && dataObject.getAnnotations().size() > 0;
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

        List<ObjectProperty> props = sortedProperties(dataObject);

        boolean hasTerms = false;
        if (props != null && props.size() > 0) {
            for(ObjectProperty prop : props) {
                String _propName = toJavaVar(prop.getName());
                if (prop.getAnnotation(org.kie.api.definition.type.Key.class.getName()) != null) {

                    if (NamingUtils.getInstance().isPrimitiveTypeId(prop.getClassName())) {
                        // Construction: "if (<_propName> != that.<_propName>) return false;
                        sb.append(indent + TAB);
                        addEqualsTermForPrimitive(sb, _propName, prop.getClassName());
                    } else {
                        // Construction: "if (<_propName> != null ? !<_propName>.equals(that.<_propName>) : that.<_propName> != null) return false;"
                        sb.append(indent + TAB + "if (");
                        sb.append(_propName).append(" != null ? !").append(_propName).append(".equals(that.").append(_propName).append(")");
                        sb.append(" : that.").append(_propName).append(" != null").append(") return false;");
                    }
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

    // for new template
    public String resolveEquals(DataObject dataObject) {
        return resolveEquals(dataObject, "");
    }

    private void addEqualsTermForPrimitive(StringBuilder sb, String _propName, String primitive) {
        if (NamingUtils.DOUBLE.equals(primitive)) {
            // if (Double.compare(that._double, _double) != 0) return false;
            sb.append("if (Double.compare(that.").append(_propName).append(", ").append(_propName).append(") != 0) return false;");
        } else if (NamingUtils.FLOAT.equals(primitive)) {
            // if (Float.compare(that._float, _float) != 0) return false;
            sb.append("if (Float.compare(that.").append(_propName).append(", ").append(_propName).append(") != 0) return false;");
        } else {
            // Construction: "if (<_propName> != that.<_propName>) return false;
            sb.append("if (").append(_propName).append(" != that.").append(_propName).append(")").append(" return false;");
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
        List<ObjectProperty> props = sortedProperties(dataObject);

        boolean hasTerms = false;
        if (props != null && props.size() > 0) {
            for(ObjectProperty prop : props) {
                String _propName = toJavaVar(prop.getName());
                if (prop.getAnnotation(org.kie.api.definition.type.Key.class.getName()) != null) {

                    if (NamingUtils.getInstance().isPrimitiveTypeId(prop.getClassName())) {
                        sb.append(indent + TAB);
                        addHashCodeTermForPrimitive(sb, _propName, prop.getClassName());
                    } else {
                        // Construction: "result = 13 * result + (<_propName> != null ? <_propName>.hashCode() : 0);"
                        sb.append(indent + TAB + "result = 31 * result + (").append(_propName).append(" != null ? ").append(_propName).append(".hashCode() : 0);");
                    }
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

    // for new template
    public String resolveHashCode(DataObject dataObject) {
        return resolveHashCode(dataObject, "");
    }

    private void addHashCodeTermForPrimitive(StringBuilder sb, String _propName, String primitive) {

        if (NamingUtils.BYTE.equals(primitive) || NamingUtils.CHAR.equals(primitive) || NamingUtils.SHORT.equals(primitive)) {
            //result = 31 * result + (int) _propName;
            sb.append("result = 31 * result + (int) ").append(_propName).append(";");
        } else if (NamingUtils.BOOLEAN.equals(primitive)) {
           //result = 31 * result + (_boolean ? 1 : 0);
            sb.append("result = 31 * result + (").append(_propName).append(" ? 1 : 0);");
        } else if (NamingUtils.LONG.equals(primitive)) {
            //result = 31 * result + (int) (_long ^ (_long >>> 32));
            sb.append("result = 31 * result + (int) (").append(_propName).append(" ^ (").append(_propName).append(" >>> 32));");
        } else if (NamingUtils.DOUBLE.equals(primitive)) {
            String temp = "Double.doubleToLongBits("+_propName+")";
            sb.append("result = 31 * result + (int) (" + temp + " ^ (" + temp +" >>> 32));");
        } else if (NamingUtils.FLOAT.equals(primitive)) {
            //"result = 31 * result + (_float != +0.0f ? Float.floatToIntBits(_float) : 0);"
            sb.append("result = 31 * result + (").append(_propName).append(" != +0.0f ? Float.floatToIntBits(").append(_propName).append(") : 0);");
        } else {
            //"result = 31 * result + _propName
            sb.append("result = 31 * result + ").append(_propName).append(";");
        }
    }

    //TODO replace indent String with an integer that represents the indent level and get rid of duplicate methods (created not to affect existing generation)
    public String resolveAllFieldsConstructor(DataObject dataObject, String indent) {
        if (!dataObject.getProperties().isEmpty()) {
            List<ObjectProperty> sortedProperties = new ArrayList<ObjectProperty>();
            sortedProperties.addAll(dataObject.getProperties().values());
            return resolveConstructor(dataObject, sortByPosition(sortByName(sortedProperties)), indent);
        }
        return "";
    }

    // for new template
    public String resolveAllFieldsConstructor(DataObject dataObject) {
        if (!dataObject.getProperties().isEmpty()) {
            List<ObjectProperty> sortedProperties = new ArrayList<ObjectProperty>();
            for (ObjectProperty property : dataObject.getProperties().values()) {
                //TODO improve this kind of filtering
                if (!property.isFinal() && !property.isStatic()) {
                    sortedProperties.add(property);
                }
            }
            return resolveConstructor2(dataObject, sortByPosition(sortByName(sortedProperties)), "    ");
        }
        return "";
    }

    public String resolveKeyFieldsConstructor(DataObject dataObject, String indent) {
        if (!dataObject.getProperties().isEmpty()) {
            List<ObjectProperty> sortedProperties = new ArrayList<ObjectProperty>();
            for (ObjectProperty property : dataObject.getProperties().values()) {
                if (property.getAnnotation(KeyAnnotationDefinition.getInstance().getClassName()) != null) {
                    //the property is marked as key.
                    sortedProperties.add(property);
                }
            }
            if (sortedProperties.size() > 0 && sortedProperties.size() < dataObject.getProperties().size()) {
                return resolveConstructor(dataObject, sortByPosition(sortByName(sortedProperties)), indent);
            }
        }
        return "";
    }

    // for new template
    public String resolveKeyFieldsConstructor(DataObject dataObject) {
        if (!dataObject.getProperties().isEmpty()) {
            List<ObjectProperty> sortedProperties = new ArrayList<ObjectProperty>();
            for (ObjectProperty property : dataObject.getProperties().values()) {
                if (property.getAnnotation(KeyAnnotationDefinition.getInstance().getClassName()) != null) {
                    //the property is marked as key.
                    sortedProperties.add(property);
                }
            }
            if (sortedProperties.size() > 0 && sortedProperties.size() < dataObject.getProperties().size()) {
                return resolveConstructor2(dataObject, sortByPosition(sortByName(sortedProperties)), "    ");
            }
        }
        return "";
    }

    public List<Annotation> sortAnnotationsByName(List<Annotation> annotations) {
        Collections.sort(annotations, new Comparator<Annotation>() {
            public int compare(Annotation o1, Annotation o2) {

                if (o1 == null && o2 == null) return 0;
                if (o1 == null && o2 != null) return -1;
                if (o1 != null && o2 == null) return 1;

                Comparable key1 = o1.getName();
                Comparable key2 = o2.getName();

                if (key1 == null && key2 == null) return 0;
                if (key1 != null && key2 != null) return key1.compareTo(key2);

                if (key1 == null && key2 != null) return -1;

                //if (key1 != null && key2 == null) return 1;
                return 1;
            }
        } );
        return annotations;

    }

    public List<Annotation> sortedAnnotations(HasAnnotations hasAnnotations) {
        List<Annotation> sortedAnnotations = new ArrayList<Annotation>();
        if (hasAnnotations != null && !hasAnnotations.getAnnotations().isEmpty()) {
            sortedAnnotations.addAll(hasAnnotations.getAnnotations());
        }
        return sortAnnotationsByName(sortedAnnotations);
    }

    public List<ObjectProperty> sortedProperties(DataObject dataObject) {
        List<ObjectProperty> sortedProperties = new ArrayList<ObjectProperty>();
        if (dataObject != null && !dataObject.getProperties().isEmpty()) {
            sortedProperties.addAll(dataObject.getProperties().values());
        }
        return sortByName(sortedProperties);
    }

    public List<ObjectProperty> sortByName(List<ObjectProperty> properties) {
        Collections.sort(properties, new Comparator<ObjectProperty>() {
            public int compare(ObjectProperty o1, ObjectProperty o2) {

                if (o1 == null && o2 == null) return 0;
                if (o1 == null && o2 != null) return -1;
                if (o1 != null && o2 == null) return 1;

                Comparable key1 = o1.getName();
                Comparable key2 = o2.getName();

                if (key1 == null && key2 == null) return 0;
                if (key1 != null && key2 != null) return key1.compareTo(key2);

                if (key1 == null && key2 != null) return -1;

                //if (key1 != null && key2 == null) return 1;
                return 1;
            }
        } );
        return properties;
    }

    public List<ObjectProperty> sortByPosition(List<ObjectProperty> properties) {
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

    // Same as above, but removed some indents that are not wanted for partial code generation purposes
    // TODO to be refactored
    public String resolveConstructor2(DataObject dataObject, List<ObjectProperty> properties, String indent) {

        StringBuilder head = new StringBuilder();
        StringBuilder body = new StringBuilder();

        head.append("public " + dataObject.getName() + "(");

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
        head.append("}");

        return head.toString();
    }

    public String escapeStringForJavaCode(String value) {
        if (value == null) return value;
        //we need to escape characters like this '\r\t', \n, and " to generate the code properly.
        return StringEscapeUtils.escapeJavaNonUTFChars( value );
    }

    public String fileHashEmptyTag() {
        return FileHashingUtils.getFileHashEmptyTag();
    }
}
