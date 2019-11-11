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

package org.kie.workbench.common.services.datamodeller.codegen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.HasAnnotations;
import org.kie.workbench.common.services.datamodeller.core.JavaClass;
import org.kie.workbench.common.services.datamodeller.core.Method;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.Parameter;
import org.kie.workbench.common.services.datamodeller.core.Type;
import org.kie.workbench.common.services.datamodeller.core.Visibility;
import org.kie.workbench.common.services.datamodeller.util.DataModelUtils;
import org.kie.workbench.common.services.datamodeller.util.FileHashingUtils;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.kie.workbench.common.services.datamodeller.util.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.BOOLEAN;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.BYTE;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.CHAR;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.DOUBLE;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.FLOAT;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.LONG;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.SHORT;

/**
 * Helper tools to generate names and other stuff easily from code adf engine.
 */
public class GenerationTools {

    public static final String EOL = System.getProperty("line.separator");

    private static final Logger logger = LoggerFactory.getLogger(GenerationTools.class);
    private static final String TAB = "    ";

    private static final String START_INDENT = "\n\n";
    private static final String ANNOTATION_START_INDENT = "\n";
    private static final String LINE_INDENT = "    ";
    private static final String END_INDENT = "\n";

    /**
     * Constant inherited for drools world that prevents from generating the all fields constructor if a class has
     * >= 120 fields.
     */
    public static final int MAX_FIELDS_FOR_DEFAULT_CONSTRUCTOR = 120;

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
        String prefix = BOOLEAN.equals(className != null ? className.trim() : null) ? "is" : "get";
        return toJavaAccessor(prefix, name);
    }

    public String toJavaSetter(String name) {
        return toJavaAccessor("set", name);
    }

    private String toJavaAccessor(String prefix, String name) {
        if (name == null || name.length() == 0) {
            return name;
        }

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
        if (packageName != null) {
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

    public String resolveAnnotationType(Annotation annotation) {
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
        for (AnnotationValuePairDefinition memberDefinition : annotationDefinition.getValuePairs()) {
            if ((memberValue = annotation.getValue(memberDefinition.getName())) != null) {
                //a value has been set for this member.
                if (memberCount == 0) {
                    type.append("(");
                }
                if (memberCount > 0) {
                    type.append(", ");
                }
                type.append(resolveMemberType(memberDefinition, memberValue));
                memberCount++;
            }
        }
        if (memberCount > 0) {
            type.append(")");
        }

        return type.toString();
    }

    public String resolveMemberType(AnnotationValuePairDefinition valuePairDefinition, Object value) {
        StringBuffer type = new StringBuffer();

        type.append(valuePairDefinition.getName());
        type.append(" = ");
        type.append(resolveMemberTypeExpression(valuePairDefinition, value));
        return type.toString();
    }

    public String resolveMemberTypeExpression(AnnotationValuePairDefinition valuePairDefinition, Object value) {
        if (valuePairDefinition.isArray()) {
            return resolveMemberTypeArrayValue(valuePairDefinition, value);
        } else {
            return resolveMemberTypeValue(valuePairDefinition, value);
        }
    }

    public String resolveMemberTypeArrayValue(AnnotationValuePairDefinition valuePairDefinition, Object value) {
        if (value == null) {
            return null;
        }
        List<Object> values = (List<Object>) value;
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        boolean isFirst = true;
        if (values.size() > 0) {
            for (Object listItem : values) {
                if (!isFirst) {
                    builder.append(", ");
                }
                builder.append(resolveMemberTypeValue(valuePairDefinition, listItem));
                isFirst = false;
            }
        }
        builder.append("}");
        return builder.toString();
    }

    public String resolveMemberTypeValue(AnnotationValuePairDefinition valuePairDefinition, Object value) {
        String typeValue = null;

        if (valuePairDefinition.isEnum()) {
            typeValue = resolveEnumValue(valuePairDefinition, value);
        } else if (valuePairDefinition.isString()) {
            typeValue = resolveStringValue(value);
        } else if (valuePairDefinition.isPrimitiveType()) {
            typeValue = resolvePrimitiveValue(valuePairDefinition, value);
        } else if (valuePairDefinition.isClass()) {
            typeValue = resolveClassValue(value);
        } else if (valuePairDefinition.isAnnotation() && value instanceof Annotation) {
            typeValue = "@" + NamingUtils.normalizeClassName(valuePairDefinition.getAnnotationDefinition().getClassName())
                    + resolveAnnotationType((Annotation) value);
        }
        return typeValue;
    }

    public String resolveEnumValue(AnnotationValuePairDefinition valuePairDefinition, Object value) {
        if (value == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();

        builder.append(NamingUtils.normalizeClassName(valuePairDefinition.getClassName()));
        builder.append(".");
        builder.append(value);
        return builder.toString();
    }

    public String resolveStringValue(Object value) {
        if (value == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("\"");
        builder.append(escapeStringForJavaCode(value != null ? value.toString() : null));
        builder.append("\"");
        return builder.toString();
    }

    public String resolvePrimitiveValue(AnnotationValuePairDefinition valuePairDefinition, Object value) {

        if (value == null) {
            return null;
        }
        //primitive types are wrapped by the java.lang.type.
        StringBuilder builder = new StringBuilder();
        if (Character.class.getName().equals(valuePairDefinition.getClassName())) {
            builder.append("'");
            builder.append(value.toString());
            builder.append("'");
        } else if (Long.class.getName().equals(valuePairDefinition.getClassName())) {
            builder.append(value.toString());
            builder.append("L");
        } else if (Float.class.getName().equals(valuePairDefinition.getClassName())) {
            builder.append(value.toString());
            builder.append("f");
        } else if (Double.class.getName().equals(valuePairDefinition.getClassName())) {
            builder.append(value.toString());
            builder.append("d");
        } else {
            builder.append(value.toString());
        }
        return builder.toString();
    }

    public String resolveClassValue(Object value) {
        if (value == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(value.toString());
        if (!builder.toString().endsWith(".class")) {
            builder.append(".class");
        }
        return builder.toString();
    }

    public String resolveSuperClassType(JavaClass javaClass) {
        StringBuffer type = new StringBuffer("");
        if (javaClass.getSuperClassName() != null && !"".equals(javaClass.getSuperClassName())) {
            type.append("extends ");
            type.append(javaClass.getSuperClassName());
        }
        return type.toString();
    }

    public String resolveImplementedInterfacesType(JavaClass javaClass) {
        StringBuilder type = new StringBuilder("implements " + Serializable.class.getName());

        if (javaClass.getInterfaces() != null) {
            for (String interfaceDefinition : javaClass.getInterfaces()) {
                if (interfaceDefinition.startsWith(Serializable.class.getName())) {
                    continue;
                }
                type.append(", ")
                        .append(interfaceDefinition);
            }
        }
        return type.toString();
    }

    public boolean hasSuperClass(DataObject dataObject) {
        return dataObject != null && dataObject.getSuperClassName() != null && !"".equals(dataObject.getSuperClassName());
    }

    public int keyFieldsCount(DataObject dataObject) {
        int count = 0;
        if (dataObject != null && dataObject.getProperties() != null && !dataObject.getProperties().isEmpty()) {
            for (ObjectProperty prop : dataObject.getProperties()) {
                if (DataModelUtils.isKeyField(prop)) {
                    count++;
                }
            }
        }
        return count;
    }

    public int propertiesCount(DataObject dataObject) {
        return (dataObject != null && dataObject.getProperties() != null) ? dataObject.getProperties().size() : 0;
    }

    public int enabledForConstructorPropertiesCount(DataObject dataObject) {
        int count = 0;
        if (dataObject != null && dataObject.getProperties() != null && !dataObject.getProperties().isEmpty()) {
            for (ObjectProperty prop : dataObject.getProperties()) {
                if (DataModelUtils.isAssignable(prop)) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean hasEquals(DataObject dataObject) {
        return keyFieldsCount(dataObject) > 0;
    }

    public boolean hasProperties(DataObject dataObject) {
        return propertiesCount(dataObject) > 0;
    }

    public boolean hasClassAnnotations(JavaClass javaClass) {
        return javaClass != null && javaClass.getAnnotations() != null && !javaClass.getAnnotations().isEmpty();
    }

    public boolean hasMethodAnnotations(Method method) {
        return method != null && method.getAnnotations() != null && !method.getAnnotations().isEmpty();
    }

    public String resolveMethodParameters(List<String> parameters) {
        StringBuilder builder = new StringBuilder("");
        Iterator<String> parameterIterator = parameters.iterator();
        int i = 1;
        while (parameterIterator.hasNext()) {
            builder.append(parameterIterator.next())
                    .append(" o")
                    .append(i);
            if (parameterIterator.hasNext()) {
                builder.append(",");
            }
            i++;
        }
        return builder.toString();
    }

    public String buildMethodReturnTypeString(Type returnType) {

        if (returnType == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(returnType.getName());

        buildTypeArgumentsString(returnType.getTypeArguments(), builder);

        return builder.toString();
    }

    public String buildMethodParameterString(List<Parameter> methodParameters) {
        if (methodParameters == null || methodParameters.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        Iterator<Parameter> iterator = methodParameters.iterator();

        while (iterator.hasNext()) {
            Parameter parameter = iterator.next();

            Type parameterType = parameter.getType();
            builder.append(parameterType.getName());
            buildTypeArgumentsString(parameter.getType().getTypeArguments(), builder);
            builder.append(" ");
            builder.append(parameter.getName());

            if (iterator.hasNext()) {
                builder.append(",");
            }
        }

        return builder.toString();
    }

    private void buildTypeArgumentsString(List<Type> typeArguments, StringBuilder builder) {
        if (typeArguments == null || typeArguments.isEmpty()) {
            return;
        }
        builder.append("<");

        java.util.Iterator<Type> iterator = typeArguments.iterator();

        while (iterator.hasNext()) {
            Type argument = iterator.next();

            builder.append(argument.getName());

            buildTypeArgumentsString(argument.getTypeArguments(), builder);

            if (iterator.hasNext()) {
                builder.append(",");
            }
        }

        builder.append(">");
    }

    public String resolveEquals(DataObject dataObject, String indent) {

        StringBuilder head = new StringBuilder();
        //head.append(EOL);
        head.append(indent + "@Override" + EOL);
        head.append(indent + "public boolean equals(Object o) {" + EOL);
        head.append(indent + TAB + "if (this == o) return true;" + EOL);
        head.append(indent + TAB + "if (o == null || getClass() != o.getClass()) return false;" + EOL);
        head.append(indent + TAB + dataObject.getClassName() + " that = (" + dataObject.getClassName() + ")o;" + EOL);

        StringBuilder end = new StringBuilder();
        end.append(indent + "}");

        StringBuilder sb = new StringBuilder();

        List<ObjectProperty> props = DataModelUtils.filterKeyFields(dataObject);
        props = DataModelUtils.sortByFileOrder(props);

        boolean hasTerms = false;
        if (props != null && props.size() > 0) {
            for (ObjectProperty prop : props) {
                String _propName = toJavaVar(prop.getName());
                if (DataModelUtils.isKeyField(prop)) {

                    if (NamingUtils.isPrimitiveTypeId(prop.getClassName())) {
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
        if (DOUBLE.equals(primitive)) {
            // if (Double.compare(that._double, _double) != 0) return false;
            sb.append("if (Double.compare(that.").append(_propName).append(", ").append(_propName).append(") != 0) return false;");
        } else if (FLOAT.equals(primitive)) {
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

        List<ObjectProperty> props = DataModelUtils.filterKeyFields(dataObject);
        props = DataModelUtils.sortByFileOrder(props);

        boolean hasTerms = false;
        if (props != null && props.size() > 0) {
            for (ObjectProperty prop : props) {
                String _propName = toJavaVar(prop.getName());
                if (DataModelUtils.isKeyField(prop)) {

                    if (NamingUtils.isPrimitiveTypeId(prop.getClassName())) {
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

        if (BYTE.equals(primitive) || CHAR.equals(primitive) || SHORT.equals(primitive)) {
            //result = 31 * result + (int) _propName;
            sb.append("result = 31 * result + (int) ").append(_propName).append(";");
        } else if (BOOLEAN.equals(primitive)) {
            //result = 31 * result + (_boolean ? 1 : 0);
            sb.append("result = 31 * result + (").append(_propName).append(" ? 1 : 0);");
        } else if (LONG.equals(primitive)) {
            //result = 31 * result + (int) (_long ^ (_long >>> 32));
            sb.append("result = 31 * result + (int) (").append(_propName).append(" ^ (").append(_propName).append(" >>> 32));");
        } else if (DOUBLE.equals(primitive)) {
            String temp = "Double.doubleToLongBits(" + _propName + ")";
            sb.append("result = 31 * result + (int) (" + temp + " ^ (" + temp + " >>> 32));");
        } else if (FLOAT.equals(primitive)) {
            //"result = 31 * result + (_float != +0.0f ? Float.floatToIntBits(_float) : 0);"
            sb.append("result = 31 * result + (").append(_propName).append(" != +0.0f ? Float.floatToIntBits(").append(_propName).append(") : 0);");
        } else {
            //"result = 31 * result + _propName
            sb.append("result = 31 * result + ").append(_propName).append(";");
        }
    }

    public String resolveVisibility(Method method) {
        Visibility visibility = method.getVisibilty();

        if (visibility == null) {
            return "";
        }
        switch (visibility) {
            case PUBLIC:
                return "public";
            case PROTECTED:
                return "protected";
            case PACKAGE_PRIVATE:
                return "";
            case PRIVATE:
                return "private";
            default:
                throw new IllegalArgumentException("Visibility type '" + visibility + "' is not supported.");
        }
    }

    //TODO replace indent String with an integer that represents the indent level and get rid of duplicate methods (created not to affect existing adf)
    public String resolveAllFieldsConstructor(DataObject dataObject, String indent) {
        if (!dataObject.getProperties().isEmpty()) {
            List<ObjectProperty> sortedProperties = new ArrayList<ObjectProperty>();
            for (ObjectProperty property : dataObject.getProperties()) {
                if (DataModelUtils.isAssignable(property)) {
                    sortedProperties.add(property);
                }
            }

            if (sortedProperties.size() > 0 && sortedProperties.size() < MAX_FIELDS_FOR_DEFAULT_CONSTRUCTOR) {
                //condition used by drools. All fields constructor is generated only if a class has less than
                // MAX_FIELDS_FOR_DEFAULT_CONSTRUCTOR
                return resolveConstructor(dataObject, DataModelUtils.sortByPosition(sortByName(sortedProperties)), indent);
            }
        }
        return "";
    }

    // for new template
    public String resolveAllFieldsConstructor(DataObject dataObject) {
        if (!dataObject.getProperties().isEmpty()) {
            List<ObjectProperty> sortedProperties = new ArrayList<ObjectProperty>();
            for (ObjectProperty property : dataObject.getProperties()) {
                if (DataModelUtils.isAssignable(property)) {
                    sortedProperties.add(property);
                }
            }
            if (sortedProperties.size() > 0 && sortedProperties.size() < MAX_FIELDS_FOR_DEFAULT_CONSTRUCTOR) {
                //condition used by drools. All fields constructor is generated only if a class has less than
                // MAX_FIELDS_FOR_DEFAULT_CONSTRUCTOR
                return resolveConstructor2(dataObject, DataModelUtils.sortByFileOrder(sortedProperties), "    ");
            }
        }
        return "";
    }

    public String resolveKeyFieldsConstructor(DataObject dataObject, String indent) {
        if (!dataObject.getProperties().isEmpty()) {
            List<ObjectProperty> sortedProperties = new ArrayList<ObjectProperty>();
            for (ObjectProperty property : dataObject.getProperties()) {
                if (DataModelUtils.isAssignable(property) && DataModelUtils.isKeyField(property)) {
                    //the property is marked as key.
                    sortedProperties.add(property);
                }
            }
            if (sortedProperties.size() > 0 && sortedProperties.size() < dataObject.getProperties().size()) {
                return resolveConstructor(dataObject, DataModelUtils.sortByPosition(sortByName(sortedProperties)), indent);
            }
        }
        return "";
    }

    // for new template
    public String resolveKeyFieldsConstructor(DataObject dataObject) {
        if (!dataObject.getProperties().isEmpty()) {
            List<ObjectProperty> sortedProperties = new ArrayList<ObjectProperty>();
            for (ObjectProperty property : dataObject.getProperties()) {
                if (DataModelUtils.isAssignable(property) && DataModelUtils.isKeyField(property)) {
                    //the property is marked as key.
                    sortedProperties.add(property);
                }
            }
            if (sortedProperties.size() > 0 && sortedProperties.size() < MAX_FIELDS_FOR_DEFAULT_CONSTRUCTOR) {
                return resolveConstructor2(dataObject, DataModelUtils.sortByFileOrder(sortedProperties), "    ");
            }
        }
        return "";
    }

    // for new template
    public String resolvePositionFieldsConstructor(DataObject dataObject) {
        if (!dataObject.getProperties().isEmpty()) {
            List<ObjectProperty> sortedProperties = new ArrayList<ObjectProperty>();
            for (ObjectProperty property : dataObject.getProperties()) {
                if (DataModelUtils.isAssignable(property) && DataModelUtils.isPositionField(property)) {
                    //the property is marked as key.
                    sortedProperties.add(property);
                }
            }
            if (sortedProperties.size() > 0 && sortedProperties.size() < MAX_FIELDS_FOR_DEFAULT_CONSTRUCTOR) {
                return resolveConstructor2(dataObject, DataModelUtils.sortByPosition(sortedProperties), "    ");
            }
        }
        return "";
    }

    public List<Annotation> sortAnnotationsByName(List<Annotation> annotations) {
        Collections.sort(annotations, new Comparator<Annotation>() {
            public int compare(Annotation o1, Annotation o2) {

                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null && o2 != null) {
                    return -1;
                }
                if (o1 != null && o2 == null) {
                    return 1;
                }

                Comparable key1 = o1.getClassName();
                Comparable key2 = o2.getClassName();

                if (key1 == null && key2 == null) {
                    return 0;
                }
                if (key1 != null && key2 != null) {
                    return key1.compareTo(key2);
                }

                if (key1 == null && key2 != null) {
                    return -1;
                }

                //if (key1 != null && key2 == null) return 1;
                return 1;
            }
        });
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
            sortedProperties.addAll(dataObject.getProperties());
        }
        return sortByName(sortedProperties);
    }

    public List<ObjectProperty> sortByName(List<ObjectProperty> properties) {
        Collections.sort(properties, new Comparator<ObjectProperty>() {
            public int compare(ObjectProperty o1, ObjectProperty o2) {

                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null && o2 != null) {
                    return -1;
                }
                if (o1 != null && o2 == null) {
                    return 1;
                }

                Comparable key1 = o1.getName();
                Comparable key2 = o2.getName();

                if (key1 == null && key2 == null) {
                    return 0;
                }
                if (key1 != null && key2 != null) {
                    return key1.compareTo(key2);
                }

                if (key1 == null && key2 != null) {
                    return -1;
                }

                //if (key1 != null && key2 == null) return 1;
                return 1;
            }
        });
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

    // Same as above, but removed some indents that are not wanted for partial code adf purposes
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
        if (value == null) {
            return value;
        }
        //we need to escape characters like this '\r\t', \n, and " to generate the code properly.
        return StringEscapeUtils.escapeJavaNonUTFChars(value);
    }

    public String fileHashEmptyTag() {
        return FileHashingUtils.getFileHashEmptyTag();
    }

    public String indent(String source) throws Exception {
        return START_INDENT + GenerationEngine.indentLines(source, LINE_INDENT);
    }

    public String indentFieldAnnotation(String source) throws Exception {
        return ANNOTATION_START_INDENT + "    " + source;
    }

    public String indentClassAnnotation(String source) throws Exception {
        return ANNOTATION_START_INDENT + source;
    }
}
