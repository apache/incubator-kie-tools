/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.annotations.processors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Qualifier;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import static java.util.Collections.singletonList;

/**
 * Utilities for code generation
 */
public class GeneratorUtils {

    private static final String POSITION_CLASS = "org.uberfire.workbench.model.Position";
    private static final String PLACE_REQUEST_CLASS = "org.uberfire.mvp.PlaceRequest";
    private static final String SET_CONTENT_CLASS = "org.uberfire.lifecycle.SetContent";
    private static final String GET_CONTENT_CLASS = "org.uberfire.lifecycle.GetContent";
    private static final String GET_PREVIEW_CLASS = "org.uberfire.lifecycle.GetPreview";
    private static final String ON_CLOSE_CLASS = "org.uberfire.lifecycle.OnClose";
    private static final String ON_OPEN_CLASS = "org.uberfire.lifecycle.OnOpen";
    private static final String ON_STARTUP_CLASS = "org.uberfire.lifecycle.OnStartup";
    private static final String ACTIVATED_BY_CLASS = "org.jboss.errai.ioc.client.api.ActivatedBy";

    /**
     * Handy constant for an emtpy array of argument types.
     */
    private static final String[] NO_PARAMS = new String[0];

    /**
     * Passing a reference to exactly this array causes
     * {@link #getAnnotatedMethods(TypeElement, ProcessingEnvironment, String, TypeMirror, String[])},
     * {@link #getAnnotatedMethods(TypeElement, ProcessingEnvironment, String, TypeMirror[], String[])} and
     * friends not to care about parameter types.
     */
    private static final String[] ANY_PARAMS = new String[0];

    /**
     * Finds the {@code @OnStartup} method suitable for workbench classes that are not {@code @WorkbenchClientEditor}.
     * The method must be public, non-static, have a return-type of void and either take zero parameters or one
     * parameter of type {@code PlaceRequest}.
     * <p/>
     * If no such method is found, returns null. If methods annotated with {@code @OnStartup} are found but they do not
     * satisfy all the requirements, they are marked with errors explaining the problem.
     */
    public static ExecutableElement getOnStartupMethodForNonEditors(final TypeElement classElement,
                                                                    final ProcessingEnvironment processingEnvironment) {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final TypeMirror requiredReturnType = typeUtils.getNoType(TypeKind.VOID);

        List<ExecutableElement> onStartupMethods = getAnnotatedMethods(
                classElement,
                processingEnvironment,
                ON_STARTUP_CLASS,
                requiredReturnType,
                ANY_PARAMS);
        Elements elementUtils = processingEnvironment.getElementUtils();

        ExecutableElement zeroArgMethod = null;
        ExecutableElement oneArgMethod = null;
        for (ExecutableElement m : onStartupMethods) {
            if (doParametersMatch(typeUtils,
                                  elementUtils,
                                  m,
                                  NO_PARAMS)) {
                zeroArgMethod = m;
            } else if (doParametersMatch(typeUtils,
                                         elementUtils,
                                         m,
                                         new String[]{PLACE_REQUEST_CLASS})) {
                oneArgMethod = m;
            } else {
                processingEnvironment.getMessager().printMessage(
                        Kind.ERROR,
                        formatProblemsList(ON_STARTUP_CLASS,
                                           singletonList("take no arguments or one argument of type " + PLACE_REQUEST_CLASS)));
            }
        }

        if (zeroArgMethod != null && oneArgMethod != null) {
            // TODO multiple methods should be allowed, but only if inherited. See UF-42.
            processingEnvironment.getMessager().printMessage(
                    Kind.ERROR,
                    "Found multiple @OnStartup methods. Each class can declare at most one.",
                    zeroArgMethod);
        }

        if (oneArgMethod != null) {
            return oneArgMethod;
        }

        return zeroArgMethod;
    }

    public static ExecutableElement getSetContentMethodName(TypeElement classElement, ProcessingEnvironment processingEnvironment) {
        return getUniqueAnnotatedMethod(
                classElement,
                processingEnvironment,
                SET_CONTENT_CLASS,
                new TypeMirror[]{
                        processingEnvironment.getElementUtils().getTypeElement("elemental2.promise.Promise").asType()
                },
                new String[]{"java.lang.String", "java.lang.String"});
    }

    public static ExecutableElement getGetContentMethodName(TypeElement classElement, ProcessingEnvironment processingEnvironment) {
        return getUniqueAnnotatedMethod(classElement,
                                        processingEnvironment,
                                        GET_CONTENT_CLASS,
                                        new TypeMirror[]{
                                                processingEnvironment.getElementUtils().getTypeElement("elemental2.promise.Promise").asType()
                                        },
                                        NO_PARAMS);
    }


    public static ExecutableElement getGetPreviewMethodName(TypeElement classElement, ProcessingEnvironment processingEnvironment) {
        return getUniqueAnnotatedMethod(classElement,
                                        processingEnvironment,
                                        GET_PREVIEW_CLASS,
                                        new TypeMirror[]{
                                                processingEnvironment.getElementUtils().getTypeElement("elemental2.promise.Promise").asType()
                                        },
                                        NO_PARAMS);
    }

    /**
     * Get the method name annotated with {@code @OnClose}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnCloseMethodName(final TypeElement classElement,
                                              final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getVoidMethodName(classElement,
                                 processingEnvironment,
                                 ON_CLOSE_CLASS);
    }

    /**
     * Get the method name annotated with {@code @OnOpen}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnOpenMethodName(final TypeElement classElement,
                                             final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getVoidMethodName(classElement,
                                 processingEnvironment,
                                 ON_OPEN_CLASS);
    }

    /**
     * Get the method name annotated with {@code @DefaultPosition}. The method
     * must be public, non-static, have a return-type of void and take zero
     * parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getDefaultPositionMethodName(final TypeElement classElement,
                                                      final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getDefaultPositionMethodName(classElement,
                                            processingEnvironment,
                                            ClientAPIModule.getDefaultPositionClass());
    }

    /**
     * Get the method name annotated with {@code @WorkbenchPartView}. The method
     * must be public, non-static, have a return-type of IsWidget and take zero
     * parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static ExecutableElement getWidgetMethodName(final TypeElement classElement,
                                                        final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getWidgetMethodName(classElement,
                                   processingEnvironment,
                                   ClientAPIModule.getWorkbenchPartViewClass());
    }

    /**
     * Check whether the provided type extends IsWidget.
     * @param classElement
     * @param processingEnvironment
     * @return
     */
    public static boolean getIsWidget(final TypeElement classElement,
                                      final ProcessingEnvironment processingEnvironment) {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement("com.google.gwt.user.client.ui.IsWidget").asType();
        return typeUtils.isAssignable(classElement.asType(),
                                      requiredReturnType);
    }

    /**
     * Check whether the provided type extends IsElement.
     * @param type
     * @param processingEnvironment
     * @return
     */
    public static boolean getIsElement(final TypeMirror type,
                                       final ProcessingEnvironment processingEnvironment) {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror deprecatedIsElement = elementUtils.getTypeElement("org.jboss.errai.common.client.api.IsElement").asType();
        final TypeMirror elemental2IsElement = elementUtils.getTypeElement("org.jboss.errai.common.client.api.elemental2.IsElement").asType();
        return typeUtils.isAssignable(type,
                                      deprecatedIsElement) || typeUtils.isAssignable(type,
                                                                                     elemental2IsElement);
    }

    public static boolean hasPresenterInitMethod(final TypeElement classElement,
                                                 final ProcessingEnvironment processingEnvironment,
                                                 final ExecutableElement getWidgetMethod) {
        if (getWidgetMethod == null) {
            return false;
        }
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement("org.uberfire.client.mvp.HasPresenter").asType();

        return typeUtils.isAssignable(typeUtils.erasure(getWidgetMethod.getReturnType()),
                                      requiredReturnType);
    }

    /**
     * Get the method name annotated with {@code @Perspective}. The method must
     * be public, non-static, have a return-type of PerspectiveDefinition and
     * take zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getPerspectiveMethodName(final TypeElement classElement,
                                                  final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getMethodName(classElement,
                             processingEnvironment,
                             "org.uberfire.workbench.model.PerspectiveDefinition",
                             ClientAPIModule.getPerspectiveClass());
    }

    public static String getBeanActivatorClassName(final TypeElement classElement,
                                                   final ProcessingEnvironment processingEnvironment) {
        AnnotationMirror activatedByAnnotation = getAnnotation(processingEnvironment.getElementUtils(),
                                                               classElement,
                                                               ACTIVATED_BY_CLASS);
        if (activatedByAnnotation != null) {
            return extractAnnotationStringValue(processingEnvironment.getElementUtils(),
                                                activatedByAnnotation,
                                                "value");
        }
        return null;
    }

    /**
     * Searches for an accessible method annotated with the given annotation. The method must be non-private,
     * non-static, take no arguments, and return void.
     * <p/>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above, the
     * method will be marked with an error explaining the problem.
     * @param classElement the class to search for the annotated method.
     * @param processingEnvironment the current annotation processing environment.
     * @param annotationName the fully-qualified name of the annotation to search for
     * @return the name of the method that satisfies all the requirements and bears the given annotation, or null if
     * there is no such method.
     */
    private static String getVoidMethodName(final TypeElement classElement,
                                            final ProcessingEnvironment processingEnvironment,
                                            final String annotationName) throws GenerationException {

        final Types typeUtils = processingEnvironment.getTypeUtils();
        final TypeMirror requiredReturnType = typeUtils.getNoType(TypeKind.VOID);

        ExecutableElement match = getUniqueAnnotatedMethod(
                classElement,
                processingEnvironment,
                annotationName,
                requiredReturnType,
                NO_PARAMS);
        if (match == null) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    /**
     * Checks whether the ExecutableElement's parameter list matches the requiredParameterTypes (order matters).
     * @param typeUtils type utils from current processing environment.
     * @param elementUtils element utils from current processing environment.
     * @param e the method whose parameter list to check.
     * @param requiredParameterTypes the required parameter types. Must not be null.
     * If a reference to {@link #ANY_PARAMS}, this method returns true without any further checks.
     * @return true if the target method's parameter list matches the given required parameter types, or if the special
     * {@link #ANY_PARAMS} value is passed as {@code requiredParameterTypes}. False otherwise.
     */
    private static boolean doParametersMatch(final Types typeUtils,
                                             final Elements elementUtils,
                                             final ExecutableElement e,
                                             final String[] requiredParameterTypes) {
        if (requiredParameterTypes == ANY_PARAMS) {
            return true;
        }
        if (e.getParameters().size() != requiredParameterTypes.length) {
            return false;
        }
        List<TypeMirror> requiredTypes = new ArrayList<TypeMirror>();
        for (String parameterType : requiredParameterTypes) {
            requiredTypes.add(elementUtils.getTypeElement(parameterType).asType());
        }
        for (int i = 0; i < requiredTypes.size(); i++) {
            final TypeMirror actualType = e.getParameters().get(i).asType();
            final TypeMirror requiredType = requiredTypes.get(i);
            if (!typeUtils.isAssignable(actualType,
                                        requiredType)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds a public, non-static, no-args method annotated with the given annotation which returns boolean.
     * <p/>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above, the
     * method will be marked with an error explaining the problem.
     * <p/>
     * If more than one method satisfies all the criteria, all such methods are marked with an error explaining the
     * problem.
     * @return null if no such method exists; otherwise, the method's name.
     */
    private static ExecutableElement getWidgetMethodName(final TypeElement originalClassElement,
                                                         final ProcessingEnvironment processingEnvironment,
                                                         final String annotationName) throws GenerationException {
        final Elements elementUtils = processingEnvironment.getElementUtils();
        return getUniqueAnnotatedMethod(originalClassElement,
                                        processingEnvironment,
                                        annotationName,
                                        new TypeMirror[]{
                                                elementUtils.getTypeElement("com.google.gwt.user.client.ui.IsWidget").asType(),
                                                elementUtils.getTypeElement("org.jboss.errai.common.client.api.IsElement").asType(),
                                                elementUtils.getTypeElement("org.jboss.errai.common.client.api.elemental2.IsElement").asType()
                                        },
                                        NO_PARAMS);
    }

    /**
     * Finds a public, non-static, method annotated with the given annotation which returns the given type and accepts
     * the given arguments.
     * <p/>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above, the
     * method will be marked with an error explaining the problem. This will trigger a compilation failure.
     * <p/>
     * If more than one method satisfies all the criteria, all such methods are marked with an error explaining the
     * problem.
     * @param originalClassElement the class to search for the annotated method.
     * @param processingEnvironment the current annotation processing environment.
     * @param annotationName the fully-qualified name of the annotation to search for.
     * @param requiredReturnType the fully qualified name of the type the method must return.
     * @param requiredParameterTypes the parameter types the method must take. If the method must take no parameters, use
     * {@link #NO_PARAMS}. If the method can take any parameters, use {@link #ANY_PARAMS}.
     * @return null if no such method exists; null if multiple methods satisfying the criteria are found; otherwise, a
     * reference to the method.
     */
    private static ExecutableElement getUniqueAnnotatedMethod(final TypeElement originalClassElement,
                                                              final ProcessingEnvironment processingEnvironment,
                                                              final String annotationName,
                                                              final TypeMirror requiredReturnType,
                                                              final String[] requiredParameterTypes) {

        return getUniqueAnnotatedMethod(originalClassElement,
                                        processingEnvironment,
                                        annotationName,
                                        new TypeMirror[]{requiredReturnType},
                                        requiredParameterTypes);
    }

    /**
     * Finds a public, non-static, method annotated with the given annotation which returns the given type and accepts
     * the given arguments.
     * <p/>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above, the
     * method will be marked with an error explaining the problem. This will trigger a compilation failure.
     * <p/>
     * If more than one method satisfies all the criteria, all such methods are marked with an error explaining the
     * problem.
     * @param originalClassElement the class to search for the annotated method.
     * @param processingEnvironment the current annotation processing environment.
     * @param annotationName the fully-qualified name of the annotation to search for.
     * @param requiredReturnType the fully qualified name of the type the method must return.
     * @param requiredParameterTypes the parameter types the method must take. If the method must take no parameters, use
     * {@link #NO_PARAMS}. If the method can take any parameters, use {@link #ANY_PARAMS}.
     * @return null if no such method exists; null if multiple methods satisfying the criteria are found; otherwise, a
     * reference to the method.
     */
    private static ExecutableElement getUniqueAnnotatedMethod(final TypeElement originalClassElement,
                                                              final ProcessingEnvironment processingEnvironment,
                                                              final String annotationName,
                                                              final TypeMirror[] requiredReturnType,
                                                              final String[] requiredParameterTypes) {

        List<ExecutableElement> matches = getAnnotatedMethods(
                originalClassElement,
                processingEnvironment,
                annotationName,
                requiredReturnType,
                requiredParameterTypes);

        if (matches.size() == 1) {
            return matches.get(0);
        } else if (matches.size() > 1) {
            for (ExecutableElement match : matches) {
                processingEnvironment.getMessager().printMessage(
                        Kind.ERROR,
                        "Found multiple methods annotated with @" + fqcnToSimpleName(annotationName) + ". There should only be one.",
                        match);
            }
        }

        return null;
    }

    /**
     * Finds all public, non-static, no-args method annotated with the given annotation which returns the given type.
     * <p/>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above, the
     * method will be marked with an error explaining the problem. This will trigger a compilation failure.
     * <p/>
     * If more than one method satisfies all the criteria, all such methods are marked with an error explaining the
     * problem.
     * @param originalClassElement the class to search for the annotated method.
     * @param processingEnvironment the current annotation processing environment.
     * @param annotationName the fully-qualified name of the annotation to search for.
     * @param requiredReturnType the fully qualified name of the type the method must return.
     * @param requiredParameterTypes the parameter types the method must take. If the method must take no parameters, use
     * {@link #NO_PARAMS}. If the method can take any parameters, use {@link #ANY_PARAMS}.
     * @return a list of references to the methods that satisfy the criteria (empty list if no such method exists).
     */
    private static List<ExecutableElement> getAnnotatedMethods(final TypeElement originalClassElement,
                                                               final ProcessingEnvironment processingEnvironment,
                                                               final String annotationName,
                                                               final TypeMirror requiredReturnType,
                                                               final String[] requiredParameterTypes) {
        return getAnnotatedMethods(originalClassElement,
                                   processingEnvironment,
                                   annotationName,
                                   new TypeMirror[]{requiredReturnType},
                                   requiredParameterTypes);
    }

    /**
     * Finds all public, non-static, no-args method annotated with the given annotation which returns the given type.
     * <p/>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above, the
     * method will be marked with an error explaining the problem. This will trigger a compilation failure.
     * <p/>
     * If more than one method satisfies all the criteria, all such methods are marked with an error explaining the
     * problem.
     * @param originalClassElement the class to search for the annotated method.
     * @param processingEnvironment the current annotation processing environment.
     * @param annotationName the fully-qualified name of the annotation to search for.
     * @param requiredReturnTypes the fully qualified names of the valid types the method must return.
     * @param requiredParameterTypes the parameter types the method must take. If the method must take no parameters, use
     * {@link #NO_PARAMS}. If the method can take any parameters, use {@link #ANY_PARAMS}.
     * @return a list of references to the methods that satisfy the criteria (empty list if no such method exists).
     */
    private static List<ExecutableElement> getAnnotatedMethods(final TypeElement originalClassElement,
                                                               final ProcessingEnvironment processingEnvironment,
                                                               final String annotationName,
                                                               final TypeMirror[] requiredReturnTypes,
                                                               final String[] requiredParameterTypes) {

        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();

        TypeElement classElement = originalClassElement;
        while (true) {
            final List<ExecutableElement> methods = ElementFilter.methodsIn(classElement.getEnclosedElements());

            List<ExecutableElement> matches = new ArrayList<ExecutableElement>();
            for (ExecutableElement e : methods) {

                final TypeMirror actualReturnType = e.getReturnType();

                if (getAnnotation(elementUtils,
                                  e,
                                  annotationName) == null) {
                    continue;
                }

                List<String> problems = new ArrayList<String>();

                boolean foundRequiredType = false;
                for (TypeMirror requiredReturnType : requiredReturnTypes) {
                    if (typeUtils.isAssignable(actualReturnType,
                                               requiredReturnType)) {
                        foundRequiredType = true;
                        break;
                    }
                }

                if (!foundRequiredType) {
                    if (requiredReturnTypes.length == 1) {
                        problems.add("return " + requiredReturnTypes[0]);
                    } else {
                        final StringBuilder types = new StringBuilder("{");
                        for (final TypeMirror requiredReturnType : requiredReturnTypes) {
                            types.append(requiredReturnType).append(", ");
                        }
                        problems.add("return " + types.substring(0,
                                                                 types.length() - 2) + "}");
                    }
                }

                if (!doParametersMatch(typeUtils,
                                       elementUtils,
                                       e,
                                       requiredParameterTypes)) {
                    if (requiredParameterTypes.length == 0) {
                        problems.add("take no parameters");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("take ").append(requiredParameterTypes).append(" parameters of type (");
                        boolean first = true;
                        for (String p : requiredParameterTypes) {
                            if (!first) {
                                sb.append(", ");
                            }
                            sb.append(p);
                            first = false;
                        }
                        sb.append(")");
                        problems.add(sb.toString());
                    }
                }
                if (e.getModifiers().contains(Modifier.STATIC)) {
                    problems.add("be non-static");
                }
                if (e.getModifiers().contains(Modifier.PRIVATE)) {
                    problems.add("be non-private");
                }

                if (problems.isEmpty()) {
                    matches.add(e);
                } else {
                    processingEnvironment.getMessager().printMessage(
                            Kind.ERROR,
                            formatProblemsList(annotationName,
                                               problems),
                            e);
                }
            }

            if (!matches.isEmpty()) {
                return matches;
            }

            TypeMirror superclass = classElement.getSuperclass();
            if (superclass instanceof DeclaredType) {
                classElement = (TypeElement) ((DeclaredType) superclass).asElement();
            } else {
                break;
            }
        }

        return Collections.emptyList();
    }

    /**
     * Renders the given list of problems with an annotated method as an English sentence.
     * The sentence takes the form "Methods annotated with <i>annotationSimpleName</i> must <i>list of problems</i>".
     * Commas and "and" are inserted as appropriate.
     * @param annotationFqcn the fully-qualified name of the annotation the problems pertain to.
     * @param problems the list of problems, as verb phrases. Must not be null, and should contain at least one item.
     * @return a nice English sentence summarizing the problems.
     */
    static String formatProblemsList(final String annotationFqcn,
                                     List<String> problems) {
        StringBuilder msg = new StringBuilder();
        msg.append("Methods annotated with @")
                .append(fqcnToSimpleName(annotationFqcn))
                .append(" must ");
        for (int i = 0; i < problems.size(); i++) {
            if (problems.size() > 2 && i > 0) {
                msg.append(", ");
            }
            if (problems.size() == 2 && i == 1) {
                msg.append(" and ");
            }
            if (problems.size() > 2 && i == problems.size() - 1) {
                msg.append("and ");
            }
            msg.append(problems.get(i));
        }
        return msg.toString();
    }

    /**
     * Looks up a public method name with the given annotation. The method must be
     * public, non-static, have a return-type of Position and take zero
     * parameters.
     */
    private static String getDefaultPositionMethodName(final TypeElement classElement,
                                                       final ProcessingEnvironment processingEnvironment,
                                                       final String annotationName) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement(POSITION_CLASS).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn(classElement.getEnclosedElements());

        ExecutableElement match = null;
        for (ExecutableElement e : methods) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if (getAnnotation(elementUtils,
                              e,
                              annotationName) == null) {
                continue;
            }
            if (!typeUtils.isAssignable(actualReturnType,
                                        requiredReturnType)) {
                continue;
            }
            if (e.getParameters().size() != 0) {
                continue;
            }
            if (e.getModifiers().contains(Modifier.STATIC)) {
                continue;
            }
            if (!e.getModifiers().contains(Modifier.PUBLIC)) {
                continue;
            }
            if (match != null) {
                throw new GenerationException("Multiple methods with @" + fqcnToSimpleName(annotationName) + " detected.");
            }
            match = e;
        }
        if (match == null) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    public static AnnotationMirror getAnnotation(Elements elementUtils,
                                                 Element annotationTarget,
                                                 String annotationName) {
        for (AnnotationMirror annotation : elementUtils.getAllAnnotationMirrors(annotationTarget)) {
            if (annotationName.contentEquals(getQualifiedName(annotation))) {
                return annotation;
            }
        }
        return null;
    }

    public static Name getQualifiedName(AnnotationMirror annotation) {
        return ((TypeElement) annotation.getAnnotationType().asElement()).getQualifiedName();
    }

    /**
     * Finds a public, non-static, no-args method annotated with the given annotation which returns the given type.
     * <p/>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above, the
     * method will be marked with an error explaining the problem.
     * <p/>
     * If more than one method satisfies all the criteria, all such methods are marked with an error explaining the
     * problem.
     * @param classElement the class to search for the annotated method.
     * @param processingEnvironment the current annotation processing environment.
     * @param expectedReturnType the fully-qualified name of the type the method must return.
     * @param annotationName the fully-qualified name of the annotation to search for.
     * @return null if no such method exists; otherwise, the method's name.
     */
    private static String getMethodName(final TypeElement classElement,
                                        final ProcessingEnvironment processingEnvironment,
                                        final String expectedReturnType,
                                        final String annotationName) throws GenerationException {
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement(expectedReturnType).asType();
        ExecutableElement match = getUniqueAnnotatedMethod(
                classElement,
                processingEnvironment,
                annotationName,
                requiredReturnType,
                NO_PARAMS);
        if (match == null) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    /**
     * Provides a uniform way of working with single- and multi-valued AnnotationValue objects.
     * @return the annotation values as strings. For multi-valued annotation params, the collection's iteration order matches the
     * order the values appeared in the source code. Single-valued params are wrapped in a single-element collection.
     * In either case, don't attempt to modify the returned collection.
     */
    public static Collection<String> extractValue(final AnnotationValue value) {
        if (value.getValue() instanceof Collection) {
            final Collection<?> varray = (List<?>) value.getValue();
            final ArrayList<String> result = new ArrayList<String>(varray.size());
            for (final Object active : varray) {
                result.addAll(extractValue((AnnotationValue) active));
            }
            return result;
        }
        return Collections.singleton(value.getValue().toString());
    }

    private static String fqcnToSimpleName(String fqcn) {
        int lastIndexOfDot = fqcn.lastIndexOf('.');
        if (lastIndexOfDot != -1) {
            return fqcn.substring(lastIndexOfDot + 1);
        }
        return fqcn;
    }

    public static boolean debugLoggingEnabled() {
        return Boolean.parseBoolean(System.getProperty("org.uberfire.processors.debug",
                                                       "false"));
    }

    public static String extractAnnotationStringValue(Elements elementUtils,
                                                      AnnotationMirror annotation,
                                                      CharSequence paramName) {
        final AnnotationValue av = extractAnnotationPropertyValue(elementUtils,
                                                                  annotation,
                                                                  paramName);
        if (av != null && av.getValue() != null) {
            return av.getValue().toString();
        }
        return null;
    }

    public static AnnotationValue extractAnnotationPropertyValue(Elements elementUtils,
                                                                 AnnotationMirror annotation,
                                                                 CharSequence annotationProperty) {

        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationParams =
                elementUtils.getElementValuesWithDefaults(annotation);

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> param : annotationParams.entrySet()) {
            if (param.getKey().getSimpleName().contentEquals(annotationProperty)) {
                return param.getValue();
            }
        }
        return null;
    }

    /**
     * This method builds a list of all qualifier annotations source-code declaration that annotates the passed element.
     * @param element {@link TypeElement} which will be scanned for qualifier annotations.
     * @return A list of the annotations source-code declarations.
     */
    public static List<String> getAllQualifiersDeclarationFromType(TypeElement element) {
        List<String> qualifiers = new ArrayList<>();
        for (final AnnotationMirror am : element.getAnnotationMirrors()) {
            final TypeElement annotationElement = (TypeElement) am.getAnnotationType().asElement();
            if (annotationElement.getAnnotation(Qualifier.class) != null) {
                qualifiers.add(am.toString());
            }
        }

        return qualifiers;
    }

    public static ExecutableElement getValidateMethodName(TypeElement classElement, ProcessingEnvironment processingEnvironment) {
        return getUniqueAnnotatedMethod(classElement,
                                        processingEnvironment,
                                        "org.uberfire.lifecycle.Validate", //FIXME: Tiago
                                        new TypeMirror[]{
                                                processingEnvironment.getElementUtils().getTypeElement("elemental2.promise.Promise").asType()
                                        },
                                        NO_PARAMS);
    }
}
