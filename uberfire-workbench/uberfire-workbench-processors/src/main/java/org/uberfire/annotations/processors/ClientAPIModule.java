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

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * A collection of type names in the UberFire Client API module.
 * Due to a bug in Eclipse annotation processor dependencies, we refer to all UberFire type names using Strings,
 * Elements, and TypeMirrors. We cannot refer to the annotation types as types themselves.
 */
public class ClientAPIModule {

    private static final String IDENTIFIER = "identifier";

    public static final String workbenchPerspective = "org.uberfire.client.annotations.WorkbenchPerspective";
    public static final String workbenchScreen = "org.uberfire.client.annotations.WorkbenchScreen";
    public static final String workbenchClientEditor = "org.uberfire.client.annotations.WorkbenchClientEditor";
    public static final String defaultPosition = "org.uberfire.client.annotations.DefaultPosition";
    public static final String workbenchPartView = "org.uberfire.client.annotations.WorkbenchPartView";
    public static final String perspective = "org.uberfire.client.annotations.Perspective";
    public static final String intercept = "org.uberfire.client.annotations.Intercept";
    public static final String jsType = "jsinterop.annotations.JsType";

    private ClientAPIModule() {
    }

    public static String getWorkbenchScreenClass() {
        return workbenchScreen;
    }

    public static String getPerspectiveClass() {
        return perspective;
    }

    public static String getWorkbenchPartViewClass() {
        return workbenchPartView;
    }

    public static String getDefaultPositionClass() {
        return defaultPosition;
    }

    public static String getWorkbenchClientEditorClass() {
        return workbenchClientEditor;
    }

    public static String getWorkbenchPerspectiveClass() {
        return workbenchPerspective;
    }

    /**
     * Returns the value of the String-valued Annotation parameter on the given type, ignoring any default value that
     * exists on the annotation. Returns an empty string if the type lacks the given annotation, or if the annotation
     * lacks the given parameter.
     */
    private static String getAnnotationStringParam(TypeElement target,
                                                   String annotationClassName,
                                                   String annotationParamName) {
        AnnotationValue paramValue = getAnnotationParamValue(target,
                                                             annotationClassName,
                                                             annotationParamName);
        if (paramValue == null) {
            return "";
        }
        return paramValue.getValue().toString();
    }

    /**
     * Returns the value associated with the given parameter of the given annotation on the given class element,
     * ignoring any default value that exists on the annotation. Returns null if the type lacks the given annotation, or
     * if the annotation lacks the given parameter.
     */
    private static AnnotationValue getAnnotationParamValue(TypeElement target,
                                                           String annotationClassName,
                                                           String annotationName) {
        for (final AnnotationMirror am : target.getAnnotationMirrors()) {
            if (annotationClassName.equals(am.getAnnotationType().toString())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet()) {
                    if (annotationName.equals(entry.getKey().getSimpleName().toString())) {
                        return entry.getValue();
                    }
                }
            }
        }
        return null;
    }

    public static String getWbPerspectiveScreenIdentifierValueOnClass(TypeElement classElement) {
        return getAnnotationStringParam(classElement,
                                        workbenchPerspective,
                                        IDENTIFIER);
    }
}
