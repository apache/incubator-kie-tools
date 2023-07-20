/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.client.yaml.processor;


import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.kie.workbench.common.stunner.client.yaml.processor.context.GenerationContext;
import org.kie.workbench.common.stunner.client.yaml.processor.exception.GenerationException;

import static java.util.Objects.isNull;
/**
 * Type class.
 *
 * @author vegegoku
 * @version $Id: $Id
 */
public class TypeUtils {

    /** Constant <code>BEAN_JSON_SERIALIZER_IMPL="BeanXMLSerializerImpl"</code> */
    public static final String BEAN_YAML_SERIALIZER_IMPL = "_YamlSerializerImpl";
    /** Constant <code>BEAN_JSON_DESERIALIZER_IMPL="BeanJsonDeserializerImpl"</code> */
    public static final String BEAN_YAML_DESERIALIZER_IMPL = "_YamlDeserializerImpl";

    private static final int FIRST_ARGUMENT = 0;
    private static final int SECOND_ARGUMENT = 1;

    private final Types types;
    private final Elements elements;
    private final TypeRegistry typeRegistry;

    public TypeUtils(GenerationContext context) {
        this.types = context.getProcessingEnv().getTypeUtils();
        this.elements = context.getProcessingEnv().getElementUtils();
        this.typeRegistry = context.getTypeRegistry();
    }

    /**
     * wrapperType.
     *
     * @param type a {@link TypeMirror} object.
     * @return a object.
     */
    public static String wrapperType(TypeMirror type) {
        if (isPrimitive(type)) {
            if ("boolean".equals(type.toString())) {
                return Boolean.class.getSimpleName();
            } else if ("byte".equals(type.toString())) {
                return Byte.class.getSimpleName();
            } else if ("short".equals(type.toString())) {
                return Short.class.getSimpleName();
            } else if ("int".equals(type.toString())) {
                return Integer.class.getSimpleName();
            } else if ("long".equals(type.toString())) {
                return Long.class.getSimpleName();
            } else if ("char".equals(type.toString())) {
                return Character.class.getSimpleName();
            } else if ("float".equals(type.toString())) {
                return Float.class.getSimpleName();
            } else if ("double".equals(type.toString())) {
                return Double.class.getSimpleName();
            } else {
                return Void.class.getSimpleName();
            }
        } else if (type.getKind().equals(TypeKind.ARRAY)) {
            ArrayType arrayType = (ArrayType) type;
            return arrayType.toString();
        } else {
            return MoreTypes.asElement(type).toString();
        }
    }

    private static boolean isPrimitive(TypeMirror typeMirror) {
        return typeMirror.getKind().isPrimitive();
    }

    /**
     * isPrimitiveArray.
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @return a boolean.
     */
    public static boolean isPrimitiveArray(TypeMirror typeMirror) {
        return (isArray(typeMirror) && isPrimitive(arrayComponentType(typeMirror)))
                || isPrimitive2dArray(typeMirror);
    }

    private static boolean isPrimitive2dArray(TypeMirror typeMirror) {
        return is2dArray(typeMirror) && isPrimitiveArray(arrayComponentType(typeMirror));
    }

    /**
     * is2dArray.
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @return a boolean.
     */
    public static boolean is2dArray(TypeMirror typeMirror) {
        return isArray(typeMirror) && isArray(arrayComponentType(typeMirror));
    }

    /**
     * deepArrayComponentType.
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @return a {@link TypeMirror} object.
     */
    public static TypeMirror deepArrayComponentType(TypeMirror typeMirror) {
        TypeMirror type = ((ArrayType) typeMirror).getComponentType();
        return isArray(type) ? arrayComponentType(type) : type;
    }

    /**
     * isArray.
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @return a boolean.
     */
    public static boolean isArray(TypeMirror typeMirror) {
        return TypeKind.ARRAY.compareTo(typeMirror.getKind()) == 0;
    }

    /**
     * arrayComponentType.
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @return a {@link TypeMirror} object.
     */
    public static TypeMirror arrayComponentType(TypeMirror typeMirror) {
        return ((ArrayType) typeMirror).getComponentType();
    }

    /**
     * isEnum.
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @return a boolean.
     */
    public static boolean isEnum(TypeMirror typeMirror) {
        return !isNull(MoreTypes.asElement(typeMirror))
                && !TypeUtils.isPrimitive(typeMirror)
                && !TypeUtils.isPrimitiveArray(typeMirror)
                && ElementKind.ENUM.compareTo(MoreTypes.asElement(typeMirror).getKind()) == 0;
    }

    /**
     * firstTypeArgument.
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @return a {@link TypeMirror} object.
     */
    public static TypeMirror firstTypeArgument(TypeMirror typeMirror) {
        return ((DeclaredType) typeMirror).getTypeArguments().get(FIRST_ARGUMENT);
    }

    /**
     * secondTypeArgument.
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @return a {@link TypeMirror} object.
     */
    public static TypeMirror secondTypeArgument(TypeMirror typeMirror) {
        return ((DeclaredType) typeMirror).getTypeArguments().get(SECOND_ARGUMENT);
    }

    /**
     * Check if given TypeMirror has wildcards
     *
     * @param type {@link TypeMirror} to be checked
     * @return true if given TypeMirror has wildcards
     */
    public static boolean hasWildcards(TypeMirror type) {
        return type.accept(
                new SimpleTypeVisitor8<Boolean, Void>() {
                    @Override
                    public Boolean visitPrimitive(PrimitiveType t, Void p) {
                        return false;
                    }

                    @Override
                    public Boolean visitArray(ArrayType t, Void p) {
                        return visit(t.getComponentType(), p);
                    }

                    @Override
                    public Boolean visitDeclared(DeclaredType t, Void p) {
                        return t.getTypeArguments().stream()
                                .map(typeArg -> visit(typeArg, p))
                                .filter(b -> b)
                                .findFirst()
                                .orElse(false);
                    }

                    @Override
                    public Boolean visitTypeVariable(TypeVariable t, Void p) {
                        return false;
                    }

                    @Override
                    public Boolean visitWildcard(WildcardType t, Void p) {
                        return true;
                    }
                },
                null);
    }

    /**
     * Check if given TypeMirror is a generic Java type
     *
     * @param type {@link TypeMirror} to be checked
     * @return true if given TypeMirror is a generic type
     */
    public static boolean isGenericType(TypeMirror type) {
        return type.accept(
                new SimpleTypeVisitor8<Boolean, Void>() {
                    @Override
                    public Boolean visitPrimitive(PrimitiveType t, Void p) {
                        return false;
                    }

                    @Override
                    public Boolean visitArray(ArrayType t, Void p) {
                        return visit(t.getComponentType(), p);
                    }

                    @Override
                    public Boolean visitDeclared(DeclaredType t, Void p) {
                        return !t.getTypeArguments().isEmpty();
                    }

                    @Override
                    public Boolean visitTypeVariable(TypeVariable t, Void p) {
                        return true;
                    }

                    @Override
                    public Boolean visitWildcard(WildcardType t, Void p) {
                        return true;
                    }
                },
                null);
    }

    /**
     * Check if given TypeMirror has type parameter(s).
     *
     * @param type {@link TypeMirror} object to be checked
     * @return true if given TypeMirror has type parameter(s)
     */
    public static boolean hasTypeParameter(TypeMirror type) {
        return type.accept(
                new SimpleTypeVisitor8<Boolean, Void>() {
                    @Override
                    public Boolean visitPrimitive(PrimitiveType t, Void p) {
                        return false;
                    }

                    @Override
                    public Boolean visitArray(ArrayType t, Void p) {
                        return visit(t.getComponentType(), p);
                    }

                    @Override
                    public Boolean visitDeclared(DeclaredType t, Void p) {
                        return t.getTypeArguments().stream()
                                .map(typeArg -> visit(typeArg, p))
                                .filter(b -> b)
                                .findFirst()
                                .orElse(false);
                    }

                    @Override
                    public Boolean visitTypeVariable(TypeVariable t, Void p) {
                        return true;
                    }

                    @Override
                    public Boolean visitWildcard(WildcardType t, Void p) {
                        return t.getExtendsBound() != null
                                ? visit(t.getExtendsBound(), p)
                                : t.getSuperBound() != null ? visit(t.getSuperBound(), p) : false;
                    }
                },
                null);
    }

    /**
     * Iterate over JsonSubTypes.Type annotations and converts them to a map
     *
     * @param element
     * @return map of JsonSubTypes.Type.name (as String) and JsonSubTypes.Type.value (as TypeMirror)
     */
    // Retrieving Class<?> from Annotation can be tricky in an annotation processor
    // See
    // https://area-51.blog/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
    @SuppressWarnings("unchecked")
    private static Map<String, TypeMirror> getSubtypeTypeMirrors(Element element) {
        List<? extends AnnotationMirror> subTypes =
                element.getAnnotationMirrors().stream()
                        .filter(
                                am ->
                                        am.getAnnotationType()
                                                .asElement()
                                                .getSimpleName()
                                                .toString()
                                                .equals("JsonSubTypes")) // Get JsonSubType annotation mirror
                        .flatMap(
                                am ->
                                        am.getElementValues().entrySet()
                                                .stream()) // do a flat map for JsonSubType element values map entries
                        .filter(
                                entry ->
                                        entry
                                                .getKey()
                                                .getSimpleName()
                                                .toString()
                                                .equals("value")) // find the "value" element of JsonSubType
                        .flatMap(
                                entry ->
                                        ((List<AnnotationMirror>) entry.getValue().getValue())
                                                .stream()) // treat JsonSubType.value() as list of annotation mirrors of
                        // JsonSubType.Type
                        .collect(Collectors.toList());

        return subTypes.stream()
                .collect(
                        Collectors.toMap(
                                am ->
                                        am.getElementValues().entrySet()
                                                .stream() // create a stream from all element values map entries for a given
                                                // JsonSubType.Type
                                                .filter(
                                                        entry ->
                                                                entry
                                                                        .getKey()
                                                                        .getSimpleName()
                                                                        .toString()
                                                                        .equals("name")) // find "name" element
                                                .map(
                                                        entry ->
                                                                (String)
                                                                        entry
                                                                                .getValue()
                                                                                .getValue()) // get the value from "name" element, which is
                                                // a String
                                                .findFirst()
                                                .orElse(null),
                                am ->
                                        am.getElementValues().entrySet()
                                                .stream() // create a stream from all element values map entries for a given
                                                // JsonSubType.Type
                                                .filter(
                                                        entry ->
                                                                entry
                                                                        .getKey()
                                                                        .getSimpleName()
                                                                        .toString()
                                                                        .equals("value")) // find "name" element
                                                .map(entry -> (TypeMirror) entry.getValue().getValue())
                                                .findFirst()
                                                .orElse(null)));
    }

    /**
     * Check if given type has type argument containing unbounded wildcard
     *
     * @param type{@link javax.lang.model.type.TypeMirror} to be checked
     * @return true if given type has type argument containing unbounded wildcard
     */
    public static boolean hasUnboundedWildcards(TypeMirror type) {
        return type.accept(
                new SimpleTypeVisitor8<Boolean, Void>() {
                    @Override
                    public Boolean visitPrimitive(PrimitiveType t, Void p) {
                        return false;
                    }

                    @Override
                    public Boolean visitArray(ArrayType t, Void p) {
                        return visit(t.getComponentType(), p);
                    }

                    @Override
                    public Boolean visitDeclared(DeclaredType t, Void p) {
                        return t.getTypeArguments().stream()
                                .map(typeArg -> visit(typeArg, p))
                                .filter(b -> b)
                                .findFirst()
                                .orElse(false);
                    }

                    @Override
                    public Boolean visitTypeVariable(TypeVariable t, Void p) {
                        return false;
                    }

                    @Override
                    public Boolean visitWildcard(WildcardType t, Void p) {
                        return t.getExtendsBound() != null
                                ? visit(t.getExtendsBound(), p)
                                : t.getSuperBound() != null ? visit(t.getSuperBound(), p) : true;
                    }
                },
                null);
    }

    /**
     * see: typetools/checker-framework Return all methods declared in the given type or any
     * superclass/interface. Note that no constructors will be returned. TODO: should this use
     * javax.lang.model.util.Elements.getAllMembers(TypeElement) instead of our own getSuperTypes?
     */
    public Collection<VariableElement> getAllFieldsIn(TypeElement type) {
        Map<String, VariableElement> fields = new LinkedHashMap<>();
        ElementFilter.fieldsIn(type.getEnclosedElements())
                .forEach(field -> fields.put(field.getSimpleName().toString(), field));

        List<TypeElement> alltypes = getSuperTypes(elements, type);
        for (TypeElement atype : alltypes) {
            ElementFilter.fieldsIn(atype.getEnclosedElements()).stream()
                    .filter(field -> !fields.containsKey(field.getSimpleName().toString()))
                    .forEach(field -> fields.put(field.getSimpleName().toString(), field));
        }
        return fields.values();
    }

    /**
     * see: typetools/checker-framework Determine all type elements for the classes and interfaces
     * referenced in the extends/implements clauses of the given type element. TODO: can we learn from
     * the implementation of com.sun.tools.javac.model.JavacElements.getAllMembers(TypeElement)?
     */
    public List<TypeElement> getSuperTypes(Elements elements, TypeElement type) {

        List<TypeElement> superelems = new ArrayList<>();
        if (type == null) {
            return superelems;
        }

        // Set up a stack containing type, which is our starting point.
        Deque<TypeElement> stack = new ArrayDeque<>();
        stack.push(type);

        while (!stack.isEmpty()) {
            TypeElement current = stack.pop();

            // For each direct supertype of the current type element, if it
            // hasn't already been visited, push it onto the stack and
            // add it to our superelems set.
            TypeMirror supertypecls = current.getSuperclass();
            if (supertypecls.getKind() != TypeKind.NONE) {
                TypeElement supercls = (TypeElement) ((DeclaredType) supertypecls).asElement();
                if (!superelems.contains(supercls)) {
                    stack.push(supercls);
                    superelems.add(supercls);
                }
            }
            for (TypeMirror supertypeitf : current.getInterfaces()) {
                TypeElement superitf = (TypeElement) ((DeclaredType) supertypeitf).asElement();
                if (!superelems.contains(superitf)) {
                    stack.push(superitf);
                    superelems.add(superitf);
                }
            }
        }

        // Include java.lang.Object as implicit superclass for all classes and interfaces.
        TypeElement jlobject = elements.getTypeElement(Object.class.getCanonicalName());
        if (!superelems.contains(jlobject)) {
            superelems.add(jlobject);
        }

        return Collections.unmodifiableList(superelems);
    }

    /**
     * If given type is bounded wildcard, remove the wildcard and returns extends bound if exists. If
     * extends bounds is non existing - return the super bound.
     *
     * <p>
     *
     * <p>If given type is not wildcard, returns type.
     *
     * @param type TypeMirror to be processed
     * @return extends or super bounds for given wildcard type
     */
    public TypeMirror removeOuterWildCards(TypeMirror type) {
        return type.accept(
                new SimpleTypeVisitor8<TypeMirror, Void>() {
                    @Override
                    public TypeMirror visitPrimitive(PrimitiveType t, Void p) {
                        return t;
                    }

                    @Override
                    public TypeMirror visitArray(ArrayType t, Void p) {
                        return types.getArrayType(visit(t.getComponentType(), p));
                    }

                    @Override
                    public TypeMirror visitDeclared(DeclaredType t, Void p) {
                        return t;
                    }

                    @Override
                    public TypeMirror visitTypeVariable(TypeVariable t, Void p) {
                        return t;
                    }

                    @Override
                    public TypeMirror visitWildcard(WildcardType t, Void p) {
                        return t.getExtendsBound() != null
                                ? visit(t.getExtendsBound(), p)
                                : t.getSuperBound() != null
                                ? visit(t.getSuperBound(), p)
                                : types.getDeclaredType(elements.getTypeElement(Object.class.getName()));
                    }
                },
                null);
    }

    /**
     * isCollection.
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @return a boolean.
     */
    public boolean isCollection(TypeMirror typeMirror) {
        return !TypeUtils.isPrimitive(typeMirror) && isAssignableFrom(typeMirror, Collection.class);
    }

    /**
     * isAssignableFrom.
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @param targetClass a {@link Class} object.
     * @return a boolean.
     */
    public boolean isAssignableFrom(TypeMirror typeMirror, Class<?> targetClass) {
        return types.isAssignable(
                typeMirror, types.getDeclaredType(elements.getTypeElement(targetClass.getCanonicalName())));
    }

    /**
     * isIterable.
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @return a boolean.
     */
    public boolean isIterable(TypeMirror typeMirror) {
        return !TypeUtils.isPrimitive(typeMirror) && isAssignableFrom(typeMirror, Iterable.class);
    }

    public boolean isAssignableFrom(Element element, Class<?> targetClass) {
        return types.isAssignable(
                element.asType(),
                types.getDeclaredType(elements.getTypeElement(targetClass.getCanonicalName())));
    }

    /**
     * isMap.
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @return a boolean.
     */
    public boolean isMap(TypeMirror typeMirror) {
        return !TypeUtils.isPrimitive(typeMirror) && isAssignableFrom(typeMirror, Map.class);
    }

    public boolean isBasicType(TypeMirror typeMirror) {
        return typeRegistry.isBasicType(stringifyTypeWithPackage(typeMirror));
    }

    /**
     * Stringify given TypeMirror including generic arguments and append package name
     *
     * @param type a {@link TypeMirror} object
     * @return a {@link String} containing string representation of given TypeMirror
     */
    public String stringifyTypeWithPackage(TypeMirror type) {
        return stringifyType(type, true);
    }

    private String stringifyType(TypeMirror type, boolean appendPackage) {
        return (appendPackage ? !getPackage(type).isEmpty() ? getPackage(type) + "." : "" : "")
                + type.accept(
                new SimpleTypeVisitor8<String, Void>() {
                    @Override
                    public String visitPrimitive(PrimitiveType t, Void p) {
                        return t.toString();
                    }

                    @Override
                    public String visitArray(ArrayType t, Void p) {
                        return visit(t.getComponentType(), p) + "[]";
                    }

                    @Override
                    public String visitDeclared(DeclaredType t, Void p) {
                        return t.asElement().getSimpleName()
                                + ((!t.getTypeArguments().isEmpty())
                                ? "_"
                                + t.getTypeArguments().stream()
                                .map(type -> visit(type, p))
                                .collect(Collectors.joining("_"))
                                : "");
                    }

                    @Override
                    public String visitTypeVariable(TypeVariable t, Void p) {
                        return t.toString();
                    }

                    @Override
                    public String visitWildcard(WildcardType t, Void p) {
                        return (t.getExtendsBound() != null)
                                ? "extends_" + visit(t.getExtendsBound(), p)
                                : (t.getSuperBound() != null) ? "super_" + visit(t.getSuperBound(), p) : "";
                    }
                },
                null);
    }

    /**
     * Returns package name of given TypeMirror as String. For primitive types, returns emtpy string
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @return a {@link String} object.
     */
    public String getPackage(TypeMirror typeMirror) {
        return types.asElement(types.erasure(typeMirror)) != null
                ? elements.getPackageOf(types.asElement(types.erasure(typeMirror))).toString()
                : "";
    }

    /**
     * isBasicType.
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @return a boolean.
     */
    public boolean isSimpleType(TypeMirror typeMirror) {
        return typeRegistry.isBasicType(stringifyTypeWithPackage(typeMirror))
                || typeRegistry.isSimpleType(stringifyTypeWithPackage(typeMirror));
    }

    /**
     * simpleName.
     *
     * @param typeMirror a {@link TypeMirror} object.
     * @return a {@link Name} object.
     */
    public Name simpleName(TypeMirror typeMirror) {
        return types.asElement(typeMirror).getSimpleName();
    }

    /**
     * Create serializer name for given packageName and beanType. Package name for corresponding
     * serializer is prepended to the result.
     *
     * @param beanType {@link TypeMirror} object
     * @return fully-qualified serializer class name
     */
    public String canonicalSerializerName(TypeMirror beanType) {
        return getPackage(beanType) + "." + serializerName(beanType);
    }

    public String serializerName(TypeMirror mirror) {
        if (typeRegistry.containsSerializer(mirror.toString())) {
            return typeRegistry.getCustomSerializer(mirror).getQualifiedName().toString();
        }
        TypeElement type = MoreTypes.asTypeElement(mirror);
        return (type.getEnclosingElement().getKind().equals(ElementKind.PACKAGE)
                ? ""
                : MoreElements.asType(type.getEnclosingElement()).getSimpleName().toString() + "_")
                + type.getSimpleName()
                + BEAN_YAML_SERIALIZER_IMPL;
    }

    /**
     * Returns deserializer name for given typeMirror. Package name for corresponding deserializer is
     * prepended to the result.
     *
     * @param beanType a {@link TypeMirror} object
     * @return fully qualified deserializer name
     */
    public String canonicalDeserializerName(TypeMirror beanType) {
        return getPackage(beanType) + "." + deserializerName(beanType);
    }

    public String deserializerName(TypeMirror mirror) {
        if (typeRegistry.containsDeserializer(mirror.toString())) {
            return typeRegistry.getCustomDeserializer(mirror).getQualifiedName().toString();
        }
        TypeElement type = MoreTypes.asTypeElement(mirror);
        return (type.getEnclosingElement().getKind().equals(ElementKind.PACKAGE)
                ? ""
                : MoreElements.asType(type.getEnclosingElement()).getSimpleName().toString() + "_")
                + type.getSimpleName()
                + BEAN_YAML_DESERIALIZER_IMPL;
    }

    public Optional<TypeMirror> getClassValueFromAnnotation(
            Element element, Class<? extends Annotation> annotation, String paramName) {
        for (AnnotationMirror am : element.getAnnotationMirrors()) {
            if (types.isSameType(
                    am.getAnnotationType(),
                    elements.getTypeElement(annotation.getCanonicalName()).asType())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
                        am.getElementValues().entrySet()) {
                    if (paramName.equals(entry.getKey().getSimpleName().toString())) {
                        AnnotationValue annotationValue = entry.getValue();
                        return Optional.of((DeclaredType) annotationValue.getValue());
                    }
                }
            }
        }
        return Optional.empty();
    }

    public boolean hasGetter(VariableElement variable) {
        return getGetter(variable) != null;
    }

    public ExecutableElement getGetter(VariableElement variable) {
        List<String> method = compileGetterMethodName(variable);
        return MoreElements.asType(variable.getEnclosingElement()).getEnclosedElements().stream()
                .filter(e -> e.getKind().equals(ElementKind.METHOD))
                .filter(e -> method.contains(e.getSimpleName().toString()))
                .filter(e -> !e.getModifiers().contains(Modifier.PRIVATE))
                .filter(e -> !e.getModifiers().contains(Modifier.STATIC))
                .map(MoreElements::asExecutable)
                .filter(elm -> elm.getParameters().isEmpty())
                .filter(elm -> types.isSameType(elm.getReturnType(), variable.asType()))
                .findFirst()
                .orElseThrow(
                        () ->
                                new GenerationException(
                                        String.format(
                                                "Unable to find suitable getter for %s.%s",
                                                variable.getEnclosingElement(), variable.getSimpleName())));
    }

    public List<String> compileGetterMethodName(VariableElement variable) {
        String varName = variable.getSimpleName().toString();
        boolean isBoolean = isBoolean(variable);
        List<String> result = new ArrayList<>();
        result.add("get" + capitalize(varName));
        if (isBoolean) {
            result.add("is" + capitalize(varName));
        }
        return result;
    }

    public boolean isBoolean(VariableElement variable) {
        return variable.asType().getKind().equals(TypeKind.BOOLEAN)
                || variable.asType().toString().equals(Boolean.class.getCanonicalName());
    }

    public boolean hasSetter(VariableElement variable) {
        return getSetter(variable) != null;
    }

    public ExecutableElement getSetter(VariableElement variable) {
        String method = compileSetterMethodName(variable);
        return ElementFilter.methodsIn(variable.getEnclosingElement().getEnclosedElements()).stream()
                .filter(e -> !e.getModifiers().contains(Modifier.PRIVATE))
                .filter(e -> !e.getModifiers().contains(Modifier.STATIC))
                .filter(e -> method.equals(e.getSimpleName().toString()))
                .filter(elm -> elm.getParameters().size() == 1)
                .filter(elm -> types.isSameType(elm.getParameters().get(0).asType(), variable.asType()))
                .findFirst()
                .orElseThrow(
                        () ->
                                new GenerationException(
                                        String.format(
                                                "Unable to find suitable setter for %s.%s",
                                                variable.getEnclosingElement(), variable.getSimpleName())));
    }

    private String compileSetterMethodName(VariableElement variable) {
        String varName = variable.getSimpleName().toString();
        StringBuffer sb = new StringBuffer();
        sb.append("set");
        sb.append(capitalize(varName));
        return sb.toString();
    }

    public TypeMirror toType(Class<?> clazz) {
        return elements.getTypeElement(clazz.getCanonicalName()).asType();
    }

    public TypeElement toTypeElement(TypeMirror type) {
        return ((TypeElement) types.asElement(type));
    }

    private TypeMirror objectType = null;

    public TypeMirror getObject() {
        if (objectType == null) {
            objectType = elements.getTypeElement(Object.class.getCanonicalName()).asType();
        }
        return objectType;
    }

    public boolean isObject(TypeMirror type) {
        return types.isSameType(type, getObject());
    }

    private String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
