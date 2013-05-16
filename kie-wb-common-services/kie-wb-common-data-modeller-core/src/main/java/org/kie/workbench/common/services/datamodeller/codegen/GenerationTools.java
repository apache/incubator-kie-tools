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

import org.kie.workbench.common.services.datamodeller.annotations.Equals;
import org.kie.workbench.common.services.datamodeller.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Helper tools to generate names and other stuff easily from code generation engine.
 */
public class GenerationTools {

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

    public String toJavaClass(String name) {

        return toJavaName(name, true);
    }

    public String toJavaMethod(String name) {

        return toJavaName(name, false);
    }

    public String toJavaGetter(String name) {

        return "get" + toJavaName(name, true);
    }

    public String toJavaSetter(String name) {

        return "set" + toJavaName(name, true);
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
        return toJavaName(name, false);
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

    public String resolveEquals(DataObject dataObject) {
        StringBuilder sb = new StringBuilder();

        Map<String, ObjectProperty> props = dataObject.getProperties();

        if (props != null && props.size() > 0) {
            sb.append("\n").append(TAB).append(TAB);
            for(String propName : props.keySet()) {
                ObjectProperty prop = props.get(propName);
                String _propName = toJavaVar(propName);
                if (prop.getAnnotation(Equals.class.getName()) != null) {
                    // Construction: "if (<_propName> != null ? !<_propName>.equals(that.<_propName>) : that.<_propName> != null) return false;"
                    sb.append("if (");
                    sb.append(_propName).append(" != null ? !").append(_propName).append(".equals(that.").append(_propName).append(")");
                    sb.append(" : that.").append(_propName).append(" != null").append(") return false;");
                    sb.append("\n").append(TAB).append(TAB);
                }
            }
            sb.append("\n").append(TAB).append(TAB).append("return true;");
        } else {
            sb.append(TAB).append(TAB).append("return super.equals(o)");
        }
        return sb.toString();
    }

    public String resolveHashCode(DataObject dataObject) {
        StringBuilder sb = new StringBuilder();

        Map<String, ObjectProperty> props = dataObject.getProperties();

        if (props != null && props.size() > 0) {
            sb.append("\n").append(TAB).append(TAB);
            for(String propName : props.keySet()) {
                ObjectProperty prop = props.get(propName);
                String _propName = toJavaVar(propName);
                if (prop.getAnnotation(Equals.class.getName()) != null) {
                    // Construction: "result = 13 * result + (<_propName> != null ? <_propName>.hashCode() : 0);"
                    sb.append("result = 13 * result + (").append(_propName).append(" != null ? ").append(_propName).append(".hashCode() : 0);");
                    sb.append("\n").append(TAB).append(TAB);
                }
            }
            sb.append("\n").append(TAB).append(TAB).append("return result;");
        } else {
            sb.append(TAB).append(TAB).append("return super.hashCode()");
        }
        return sb.toString();
    }
}
