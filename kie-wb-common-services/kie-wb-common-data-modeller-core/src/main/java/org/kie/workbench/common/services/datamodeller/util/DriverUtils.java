/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.datamodeller.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.forge.roaster.model.Member;
import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.VisibilityScoped;
import org.jboss.forge.roaster.model.source.Import;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.soup.project.datamodel.commons.types.ClassTypeResolver;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationRetention;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.core.Visibility;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationDefinitionImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationValuePairDefinitionImpl;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;

public class DriverUtils {

    public static ClassTypeResolver createClassTypeResolver(JavaSource javaSource, ClassLoader classLoader) {
        String packageName;
        Set<String> classImports = new HashSet<String>();

        // Importer.getImports() returns both normal and static imports
        // You can see if an Import is static by calling hte
        // Import.isStatic() method
        List<Import> imports = javaSource.getImports();
        if (imports != null) {
            for (Import currentImport : imports) {
                String importName = currentImport.getQualifiedName();
                if (currentImport.isWildcard()) {
                    importName = importName + ".*";
                }
                classImports.add(importName);
            }
        }

        packageName = javaSource.getPackage();
        //add current package too, if not added, the class type resolver don't resolve current package classes.
        if (packageName != null && !"".equals(packageName)) {
            classImports.add(packageName + ".*");
        }

        if (javaSource instanceof JavaClassSource) {
            JavaClassSource javaClassSource = (JavaClassSource) javaSource;

            //add current file inner types as import clauses to help the ClassTypeResolver to find variables of inner types
            //It was detected that current ClassTypeResolver don't resolve inner classes well.
            //workaround for BZ https://bugzilla.redhat.com/show_bug.cgi?id=1172711
            List<JavaSource<?>> innerTypes = javaClassSource.getNestedTypes();
            if (innerTypes != null) {
                for (JavaSource<?> type : innerTypes) {
                    classImports.add(packageName + "." + javaClassSource.getName() + "." + type.getName());
                }
            }
        }

        return new ClassTypeResolver(classImports, classLoader);
    }

    public static ClassTypeResolver createClassTypeResolver(ClassLoader classLoader) {
        return new ClassTypeResolver(new HashSet<String>(), classLoader);
    }

    public static Object[] isSimpleGeneric(Type type, ClassTypeResolver classTypeResolver) throws ModelDriverException {
        Object[] result = new Object[3];
        result[0] = false;
        result[1] = null;
        result[2] = null;

        if (type.isArray() ||
                type.isPrimitive() ||
                !type.isParameterized() ||
                (type.isParameterized() && type.getTypeArguments().size() != 1)) {
            return result;
        }

        Type<?> argument = ((List<Type>) type.getTypeArguments()).get(0);
        if (!isSimpleClass(argument)) {
            return result;
        }

        try {
            String outerClass = classTypeResolver.getFullTypeName(type.getName());
            String argumentClass = classTypeResolver.getFullTypeName(argument.getName());

            result[0] = true;
            result[1] = outerClass;
            result[2] = argumentClass;
            return result;
        } catch (ClassNotFoundException e) {
            throw new ModelDriverException("Class could not be resolved for name: " + type.getName() + ". " + e.getMessage(), e);
        }
    }

    /**
     * @return Return true if the given type can be managed by the driver, and subsequently by the UI.
     * <p/>
     * E.g. of managed types are:
     * int, Integer, java.lang.Integer, org.kie.SomeClass, List<Integer>, java.util.List<org.kie.SomeClass>
     * <p/>
     * e.g. of not manged types are:
     * int[], java.util.List<List<String>>, List<Map<String, org.kie.SomeClass>>
     */
    public static boolean isManagedType(Type type, ClassTypeResolver classTypeResolver) throws ModelDriverException {

        //quickest checks first.
        if (type.isPrimitive()) {
            return true;
        }

        if (type.isArray()) {
            return false;
        }

        if (type.isParameterized() && type.getTypeArguments().size() > 1) {
            return false;
        }

        try {

            Class<?> clazz = classTypeResolver.resolveType(type.getName());

            if (clazz.isAnonymousClass() || clazz.isLocalClass() || clazz.isMemberClass()) {
                return false;
            }

            if (type.isParameterized()) {
                Class<?> bag = classTypeResolver.resolveType(type.getName());
                if (!Collection.class.isAssignableFrom(bag)) {
                    return false;
                }

                return isSimpleClass(((List<Type>) type.getTypeArguments()).get(0));
            }

            return true;
        } catch (ClassNotFoundException e) {
            throw new ModelDriverException("Class could not be resolved for name: " + type.getName() + ". " + e.getMessage(), e);
        }
    }

    public static boolean isSimpleClass(Type<?> type) {
        return !type.isArray() && !type.isPrimitive() && !type.isParameterized();
    }

    public static boolean equalsType(Type type, String fullClassName, boolean multiple, String fullBagClassName, ClassTypeResolver classTypeResolver) throws Exception {

        String currentClassName;
        String currentBag;

        if (type.isArray()) {
            return false;
        }

        if (type.isPrimitive()) {
            return !multiple && fullClassName.equals(type.getName());
        }

        if (isSimpleClass(type)) {
            currentClassName = classTypeResolver.getFullTypeName(type.getName());
            return !multiple && fullClassName.equals(currentClassName);
        }

        Object[] simpleGenerics = isSimpleGeneric(type, classTypeResolver);
        if (multiple && Boolean.TRUE.equals(simpleGenerics[0]) && isManagedType(type, classTypeResolver)) {

            currentBag = (String) simpleGenerics[1];
            currentBag = classTypeResolver.getFullTypeName(currentBag);

            currentClassName = (String) simpleGenerics[2];
            currentClassName = classTypeResolver.getFullTypeName(currentClassName);

            return fullBagClassName.equals(currentBag) && fullClassName.equals(currentClassName);
        }

        return false;
    }

    public static int buildModifierRepresentation(Member<?> member) {

        int result = 0x0;
        result = addModifierRepresentation(result, member);
        result = addModifierRepresentation(result, (VisibilityScoped) member);
        return result;
    }

    public static Visibility buildVisibility(int javaSpecModifiers) {
        if (Modifier.isPublic(javaSpecModifiers)) {
            return Visibility.PUBLIC;
        }
        if (Modifier.isProtected(javaSpecModifiers)) {
            return Visibility.PROTECTED;
        }
        if (Modifier.isPrivate(javaSpecModifiers)) {
            return Visibility.PRIVATE;
        }
        return Visibility.PACKAGE_PRIVATE;
    }

    public static Visibility buildVisibility(org.jboss.forge.roaster.model.Visibility visibility) {
        switch (visibility) {
            case PUBLIC:
                return Visibility.PUBLIC;
            case PROTECTED:
                return Visibility.PROTECTED;
            case PRIVATE:
                return Visibility.PRIVATE;
            default:
                return Visibility.PACKAGE_PRIVATE;
        }
    }

    public static org.jboss.forge.roaster.model.Visibility buildVisibility(Visibility visibility) {
        switch (visibility) {
            case PUBLIC:
                return org.jboss.forge.roaster.model.Visibility.PUBLIC;
            case PROTECTED:
                return org.jboss.forge.roaster.model.Visibility.PROTECTED;
            case PRIVATE:
                return org.jboss.forge.roaster.model.Visibility.PRIVATE;
            default:
                return org.jboss.forge.roaster.model.Visibility.PACKAGE_PRIVATE;
        }
    }

    public static AnnotationRetention buildRetention(RetentionPolicy retention) {
        switch (retention) {
            case RUNTIME:
                return AnnotationRetention.RUNTIME;
            case SOURCE:
                return AnnotationRetention.SOURCE;
            default:
                return AnnotationRetention.CLASS;
        }
    }

    public static ElementType buildElementType(java.lang.annotation.ElementType elementType) {

        switch (elementType) {
            case TYPE:
                return ElementType.TYPE;
            case FIELD:
                return ElementType.FIELD;
            case METHOD:
                return ElementType.METHOD;
            case PARAMETER:
                return ElementType.PARAMETER;
            case CONSTRUCTOR:
                return ElementType.CONSTRUCTOR;
            case LOCAL_VARIABLE:
                return ElementType.LOCAL_VARIABLE;
            case ANNOTATION_TYPE:
                return ElementType.ANNOTATION_TYPE;
            case PACKAGE:
                return ElementType.PACKAGE;
        }
        return null;
    }

    public static AnnotationValuePairDefinition.ValuePairType buildValuePairType(Class cls) {
        if (cls.isEnum()) {
            return AnnotationValuePairDefinition.ValuePairType.ENUM;
        } else if (cls.isAnnotation()) {
            return AnnotationValuePairDefinition.ValuePairType.ANNOTATION;
        } else if (cls.getName().equals(String.class.getName())) {
            return AnnotationValuePairDefinition.ValuePairType.STRING;
        } else if (NamingUtils.isPrimitiveTypeId(cls.getName())) {
            return AnnotationValuePairDefinition.ValuePairType.PRIMITIVE;
        } else {
            return AnnotationValuePairDefinition.ValuePairType.CLASS;
        }
    }

    public static boolean isAnnotationMember(Class cls, Method method) {
        //TODO review this calculation
        return cls.equals(method.getDeclaringClass()) &&
                Modifier.isPublic(method.getModifiers()) &&
                (method.getParameterTypes() == null || method.getParameterTypes().length == 0) &&
                isAnnotationReturnType(method.getReturnType());
    }

    public static boolean isAnnotationReturnType(Class cls) {
        //TODO review this calculation
        Class targetType = cls;
        if (cls.isArray() && (targetType = cls.getComponentType()).isArray()) {
            return false;
        }

        return (targetType.isAnnotation() || targetType.isEnum() || targetType.isPrimitive()) ||
                (!targetType.isAnonymousClass() && !targetType.isLocalClass());
    }

    public static boolean isValidAnnotationBaseReturnType(Class cls) {
        //TODO review this calculation
        return (cls.isAnnotation() || cls.isEnum() || NamingUtils.isPrimitiveTypeId(cls.getName())) ||
                (!cls.isAnonymousClass() && !cls.isLocalClass());
    }

    public static void copyAnnotationRetention(Class annotationClass, AnnotationDefinition annotationDefinition) {
        if (annotationClass.isAnnotationPresent(Retention.class)) {
            Retention retentionAnnotation = (Retention) annotationClass.getAnnotation(Retention.class);
            annotationDefinition.setRetention(DriverUtils.buildRetention(retentionAnnotation.value()));
        }
    }

    public static void copyAnnotationTarget(Class annotationClass, AnnotationDefinition annotationDefinition) {
        if (annotationClass.isAnnotationPresent(Target.class)) {
            Target targetAnnotation = (Target) annotationClass.getAnnotation(Target.class);
            java.lang.annotation.ElementType[] targets = targetAnnotation.value();
            if (targets != null && targets.length > 0) {
                for (int i = 0; i < targets.length; i++) {
                    annotationDefinition.addTarget(buildElementType(targets[i]));
                }
            } else {
                //added to avoid an errai unmarshalling error in broser side, when an annotation has no targets, e.g.
                //javax.persistence.UniqueConstraint
                annotationDefinition.addTarget(ElementType.UNDEFINED);
            }
        }
    }

    public static int buildModifierRepresentation(JavaClassSource classSource) {
        return addModifierRepresentation(0x0, classSource);
    }

    public static int addModifierRepresentation(int modifiers, Member<?> member) {
        if (member != null) {
            if (member.isStatic()) {
                modifiers = modifiers | Modifier.STATIC;
            }
            if (member.isFinal()) {
                modifiers = modifiers | Modifier.FINAL;
            }
        }
        return modifiers;
    }

    public static int addModifierRepresentation(int modifiers, VisibilityScoped visibilityScoped) {
        if (visibilityScoped != null) {
            if (visibilityScoped.isPublic()) {
                modifiers = modifiers | Modifier.PUBLIC;
            }
            if (visibilityScoped.isProtected()) {
                modifiers = modifiers | Modifier.PROTECTED;
            }
            if (visibilityScoped.isPrivate()) {
                modifiers = modifiers | Modifier.PRIVATE;
            }
        }
        return modifiers;
    }

    public static AnnotationDefinition buildAnnotationDefinition(Class cls) {

        if (!cls.isAnnotation()) return null;

        AnnotationDefinitionImpl annotationDefinition = new AnnotationDefinitionImpl(
                NamingUtils.normalizeClassName(cls.getName()));

        //set retention and target.
        DriverUtils.copyAnnotationRetention(cls, annotationDefinition);
        DriverUtils.copyAnnotationTarget(cls, annotationDefinition);

        Method[] methods = cls.getMethods();
        Method method;
        AnnotationValuePairDefinitionImpl valuePairDefinition;
        Class returnType;
        boolean isArray = false;

        for (int i = 0; methods != null && i < methods.length; i++) {
            method = methods[i];
            if (DriverUtils.isAnnotationMember(cls, method)) {
                returnType = method.getReturnType();
                if ((isArray = returnType.isArray())) returnType = returnType.getComponentType();
                valuePairDefinition = new AnnotationValuePairDefinitionImpl(method.getName(),
                                                                            NamingUtils.normalizeClassName(returnType.getName()),
                                                                            DriverUtils.buildValuePairType(returnType),
                                                                            isArray,
                                                                            //TODO, review this default value assignment, when we have annotations the default value should be an AnnotationInstance
                                                                            method.getDefaultValue() != null ? method.getDefaultValue().toString() : null);
                if (valuePairDefinition.isAnnotation()) {
                    valuePairDefinition.setAnnotationDefinition(buildAnnotationDefinition(returnType));
                }
                if (valuePairDefinition.isEnum()) {
                    Object[] enumConstants = returnType.getEnumConstants();
                    if (enumConstants != null) {
                        String[] strEnumConstants = new String[enumConstants.length];
                        for (int j = 0; j < enumConstants.length; j++) {
                            strEnumConstants[j] = enumConstants[j].toString();
                        }
                        valuePairDefinition.setEnumValues(strEnumConstants);
                    }
                }
                annotationDefinition.addValuePair(valuePairDefinition);
            }
        }

        return annotationDefinition;
    }

    public static String encodePrimitiveArrayValue(AnnotationValuePairDefinition valuePairDefinition, Object value) {

        if (value == null) return null;

        List<Object> encodedValues = new ArrayList<Object>();
        String encodedItem;
        if (value instanceof List) {
            for (Object item : (List) value) {
                if (item != null && (encodedItem = encodePrimitiveValue(valuePairDefinition, item)) != null) {
                    encodedValues.add(encodedItem);
                }
            }
        } else {
            if ((encodedItem = encodePrimitiveValue(valuePairDefinition, value)) != null) {
                encodedValues.add(encodedItem);
            }
        }
        return toEncodedArray(encodedValues);
    }

    public static String encodePrimitiveValue(AnnotationValuePairDefinition valuePairDefinition, Object value) {

        if (value == null) return null;

        StringBuilder encodedValue = new StringBuilder();

        if (NamingUtils.isCharId(valuePairDefinition.getClassName()) || Character.class.getName().equals(valuePairDefinition.getClassName())) {
            String strValue = value.toString();
            if (StringEscapeUtils.isSingleQuoted(strValue)) {
                encodedValue.append(strValue);
            } else {
                encodedValue.append("'");
                encodedValue.append(value.toString());
                encodedValue.append("'");
            }
        } else if (NamingUtils.isLongId(valuePairDefinition.getClassName()) || Long.class.getName().equals(valuePairDefinition.getClassName())) {
            encodedValue.append(value.toString());
            encodedValue.append("L");
        } else if (NamingUtils.isFloatId(valuePairDefinition.getClassName()) || Float.class.getName().equals(valuePairDefinition.getClassName())) {
            encodedValue.append(value.toString());
            encodedValue.append("f");
        } else if (NamingUtils.isDoubleId(valuePairDefinition.getClassName()) || Double.class.getName().equals(valuePairDefinition.getClassName())) {
            encodedValue.append(value.toString());
            encodedValue.append("d");
        } else if (NamingUtils.isByteId(valuePairDefinition.getClassName()) || Byte.class.getName().equals(valuePairDefinition.getClassName())) {
            encodedValue.append("(byte)");
            encodedValue.append(value.toString());
        } else {
            encodedValue.append(value.toString());
        }

        return encodedValue.toString();
    }

    public static String encodeClassValue(String value) {
        if (value == null) return value;
        if (value.endsWith(".class")) return value;
        return value + ".class";
    }

    public static String encodeClassArrayValue(Object value) {
        if (value == null) return null;

        List<Object> encodedValues = new ArrayList<Object>();
        String encodedItem;

        if (value instanceof List) {
            for (Object item : (List) value) {
                if (item != null && (encodedItem = encodeClassValue(item.toString())) != null) {
                    encodedValues.add(encodedItem);
                }
            }
        } else if ((encodedItem = encodeClassValue(value.toString())) != null) {
            encodedValues.add(encodedItem);
        }

        return toEncodedArray(encodedValues);
    }

    public static String encodeStringArrayValue(Object value, boolean escapeJavaNonUTFChars) {
        if (value == null) return null;

        List<Object> encodedValues = new ArrayList<Object>();
        String encodedItem;

        if (value instanceof List) {
            for (Object item : (List) value) {
                if (item != null && (encodedItem = encodeStringValue(item, escapeJavaNonUTFChars)) != null) {
                    encodedValues.add(encodedItem);
                }
            }
        } else if ((encodedItem = encodeStringValue(value.toString(), escapeJavaNonUTFChars)) != null) {
            encodedValues.add(encodedItem);
        }

        return toEncodedArray(encodedValues);
    }

    public static String encodeStringValue(Object value, boolean escapeJavaNonUTFChars) {
        if (value == null) {
            return null;
        } else {
            StringBuilder encodedValue = new StringBuilder();
            String escapedValue = escapeJavaNonUTFChars ?
                    StringEscapeUtils.escapeJavaNonUTFChars(value.toString()) : value.toString();
            encodedValue.append("\"");
            encodedValue.append(escapedValue);
            encodedValue.append("\"");
            return encodedValue.toString();
        }
    }

    public static String[] encodeStringArrayValueToArray(Object value) {
        if (value == null) return null;
        List<String> notNulls = new ArrayList<String>();

        if (value instanceof List) {
            for (Object currentValue : (List) value) {
                if (currentValue != null) {
                    notNulls.add(currentValue.toString());
                }
            }
        } else {
            notNulls.add(value.toString());
        }
        return notNulls.size() > 0 ? notNulls.toArray(new String[notNulls.size()]) : new String[]{};
    }

    public static String encodeEnumValue(AnnotationValuePairDefinition valuePairDefinition, Object value) {
        if (value == null) return null;

        StringBuilder encodedValue = new StringBuilder();
        encodedValue.append(NamingUtils.normalizeClassName(valuePairDefinition.getClassName()));
        encodedValue.append(".");
        encodedValue.append(value.toString());

        return encodedValue.toString();
    }

    public static String encodeEnumArrayValue(AnnotationValuePairDefinition valuePairDefinition, Object value) {
        if (value == null) return null;

        List<Object> encodedValues = new ArrayList<Object>();
        String encodedItem;

        if (value instanceof List) {
            for (Object item : (List) value) {
                if (item != null && (encodedItem = encodeEnumValue(valuePairDefinition, item)) != null) {
                    encodedValues.add(encodedItem);
                }
            }
        } else if ((encodedItem = encodeEnumValue(valuePairDefinition, value)) != null) {
            encodedValues.add(encodedItem);
        }
        return toEncodedArray(encodedValues);
    }

    public static boolean isEmptyArray(String value) {
        if (value == null || (value = value.trim()).equals("") || !value.startsWith("{") || !value.endsWith("}")) {
            return false;
        }
        value = PortableStringUtils.removeLastChar(PortableStringUtils.removeFirstChar(value, '{'), '}');
        return "".equals(value != null ? value.trim() : null);
    }

    private static String toEncodedArray(List<Object> values) {
        StringBuilder encodedValue = new StringBuilder();
        boolean hasItems = false;
        encodedValue.append("{");
        for (Object value : values) {
            if (hasItems) encodedValue.append(", ");
            encodedValue.append(value != null ? value.toString() : "null");
            hasItems = true;
        }
        encodedValue.append("}");
        return encodedValue.toString();
    }
}
