/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import org.uberfire.annotations.processors.GenerationException;

public class GeneratorUtils extends org.uberfire.annotations.processors.GeneratorUtils {

    private static final String[] NO_PARAMS = new String[0];
    private static final String[] ANY_PARAMS = new String[0];

    public static String getTypedMethodName(final TypeElement classElement,
                                            final String annName,
                                            final String returnTypeName,
                                            final ProcessingEnvironment processingEnvironment) {
        final Elements elementUtils = processingEnvironment.getElementUtils();
        return getMethodName(classElement,
                             processingEnvironment,
                             elementUtils.getTypeElement(returnTypeName).asType(),
                             annName);
    }

    public static String getStringMethodName(final TypeElement classElement,
                                             final String annName,
                                             final ProcessingEnvironment processingEnvironment) {
        final Elements elementUtils = processingEnvironment.getElementUtils();
        return getMethodName(classElement,
                             processingEnvironment,
                             elementUtils.getTypeElement(String.class.getName()).asType(),
                             annName);
    }

    private static String getMethodName(final TypeElement classElement,
                                        final ProcessingEnvironment processingEnvironment,
                                        final TypeMirror mirror,
                                        final String annotationName) {
        ExecutableElement match = getUniqueAnnotatedMethod(classElement,
                                                           processingEnvironment,
                                                           annotationName,
                                                           mirror,
                                                           NO_PARAMS);
        if (match == null) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    /*
    classElem=MyDiagram
    annotClassNamee=IsProperty
    returnClassName=MyPropery
     */
    public static ExecutableElement getExecutableElementMethodName(final TypeElement classElement,
                                                                   final String returnClassName,
                                                                   final String annotClassName,
                                                                   final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getExecutableElementMethodName(classElement,
                                              processingEnvironment,
                                              returnClassName,
                                              annotClassName);
    }

    private static ExecutableElement getExecutableElementMethodName(final TypeElement originalClassElement,
                                                                    final ProcessingEnvironment processingEnvironment,
                                                                    final String returnClassName,
                                                                    final String annotationName) throws GenerationException {
        final Elements elementUtils = processingEnvironment.getElementUtils();
        return getUniqueAnnotatedMethod(originalClassElement,
                                        processingEnvironment,
                                        annotationName,
                                        // elementUtils.getTypeElement( "com.google.gwt.user.client.ui.IsWidget" ).asType(),
                                        elementUtils.getTypeElement(returnClassName).asType(),
                                        NO_PARAMS);
    }

    private static ExecutableElement getUniqueAnnotatedMethod(final TypeElement originalClassElement,
                                                              final ProcessingEnvironment processingEnvironment,
                                                              final String annotationName,
                                                              final TypeMirror requiredReturnType,
                                                              final String[] requiredParameterTypes) {
        List<ExecutableElement> matches = getAnnotatedMethods(originalClassElement,
                                                              processingEnvironment,
                                                              annotationName,
                                                              requiredReturnType,
                                                              requiredParameterTypes);
        if (matches.size() == 1) {
            return matches.get(0);
        } else if (matches.size() > 1) {
            for (ExecutableElement match : matches) {
                processingEnvironment.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "Found multiple methods annotated with @" + fqcnToSimpleName(annotationName) + ". There should only be one.",
                        match);
            }
        }
        return null;
    }

    public static List<ExecutableElement> getAnnotatedMethods(final TypeElement originalClassElement,
                                                              final ProcessingEnvironment processingEnvironment,
                                                              final String annotationName,
                                                              final TypeMirror requiredReturnType,
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
                if (!typeUtils.isAssignable(actualReturnType,
                                            requiredReturnType)) {
                    problems.add("return " + requiredReturnType);
                }
                if (!doParametersMatch(typeUtils,
                                       elementUtils,
                                       e,
                                       requiredParameterTypes)) {
                    if (requiredParameterTypes.length == 0) {
                        problems.add("take no parameters");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("take ")
                                .append(requiredParameterTypes.length)
                                .append(" parameters of type (");
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
                    processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR,
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

    public static AnnotationMirror getAnnotation(final Elements elementUtils,
                                                 final Element annotationTarget,
                                                 final String annotationName) {
        Iterator i$ = elementUtils.getAllAnnotationMirrors(annotationTarget).iterator();
        AnnotationMirror annotation;
        do {
            if (!i$.hasNext()) {
                return null;
            }
            annotation = (AnnotationMirror) i$.next();
        } while (!annotationName.contentEquals(getQualifiedName(annotation)));
        return annotation;
    }

    private static boolean doParametersMatch(final Types typeUtils,
                                             final Elements elementUtils,
                                             final ExecutableElement e,
                                             final String[] requiredParameterTypes) {
        if (requiredParameterTypes == ANY_PARAMS) {
            return true;
        } else if (e.getParameters().size() != requiredParameterTypes.length) {
            return false;
        } else {
            ArrayList requiredTypes = new ArrayList();
            String[] i = requiredParameterTypes;
            int actualType = requiredParameterTypes.length;
            for (int requiredType = 0; requiredType < actualType; ++requiredType) {
                String parameterType = i[requiredType];
                requiredTypes.add(elementUtils.getTypeElement(parameterType).asType());
            }
            for (int var9 = 0; var9 < requiredTypes.size(); ++var9) {
                TypeMirror var10 = ((VariableElement) e.getParameters().get(var9)).asType();
                TypeMirror var11 = (TypeMirror) requiredTypes.get(var9);
                if (!typeUtils.isAssignable(var10,
                                            var11)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static String getTypeMirrorDeclaredName(final TypeMirror typeMirror) {
        TypeKind returnKind = typeMirror.getKind();
        if (returnKind == TypeKind.DECLARED) {
            DeclaredType declaredReturnType = (DeclaredType) typeMirror;
            return declaredReturnType.toString();
        }
        return null;
    }

    private static String fqcnToSimpleName(final String fqcn) {
        int lastIndexOfDot = fqcn.lastIndexOf(46);
        return lastIndexOfDot != -1 ? fqcn.substring(lastIndexOfDot + 1) : fqcn;
    }

    static String formatProblemsList(final String annotationFqcn,
                                     final List<String> problems) {
        StringBuilder msg = new StringBuilder();
        msg.append("Methods annotated with @").append(fqcnToSimpleName(annotationFqcn)).append(" must ");
        for (int i = 0; i < problems.size(); ++i) {
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
}
