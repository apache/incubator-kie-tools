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


package org.uberfire.annotations.processors.facades;

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import org.uberfire.annotations.processors.GeneratorUtils;

/**
 * A collection of type names in the UberFire Client API module.
 * Due to a bug in Eclipse annotation processor dependencies, we refer to all UberFire type names using Strings,
 * Elements, and TypeMirrors. We cannot refer to the annotation types as types themselves.
 */
public class ClientAPIModule {

    public static final String IDENTIFIER = "identifier";
    public static final String SIZE = "size";
    public static final String OWNING_PERSPECTIVE = "owningPerspective";
    public static final String IS_DEFAULT = "isDefault";
    public static final String IS_DYNAMIC = "isDynamic";
    public static final String IS_TRANSIENT = "isTransient";
    public static final String IS_TEMPLATE = "isTemplate";
    public static final String IS_ENABLED = "isEnabled";
    public static final String VALUE = "value";
    public static final String workbenchPerspective = "org.uberfire.client.annotations.WorkbenchPerspective";
    public static final String workbenchPopup = "org.uberfire.client.annotations.WorkbenchPopup";
    public static final String workbenchScreen = "org.uberfire.client.annotations.WorkbenchScreen";
    public static final String workbenchContext = "org.uberfire.client.annotations.WorkbenchContext";
    public static final String workbenchEditor = "org.uberfire.client.annotations.WorkbenchEditor";
    public static final String workbenchClientEditor = "org.uberfire.client.annotations.WorkbenchClientEditor";
    public static final String defaultPosition = "org.uberfire.client.annotations.DefaultPosition";
    public static final String workbenchPartTitle = "org.uberfire.client.annotations.WorkbenchPartTitle";
    public static final String workbenchContextId = "org.uberfire.client.annotations.WorkbenchContextId";
    public static final String workbenchPartTitleDecoration =
            "org.uberfire.client.annotations.WorkbenchPartTitleDecoration";
    public static final String workbenchPartView = "org.uberfire.client.annotations.WorkbenchPartView";
    public static final String perspective = "org.uberfire.client.annotations.Perspective";
    public static final String intercept = "org.uberfire.client.annotations.Intercept";
    public static final String workbenchPanel = "org.uberfire.client.annotations.WorkbenchPanel";
    public static final String jsType = "jsinterop.annotations.JsType";

    private ClientAPIModule() {}

    public static String getWorkbenchScreenClass() {
        return workbenchScreen;
    }

    public static String getInterceptClass() {
        return intercept;
    }

    public static String getPerspectiveClass() {
        return perspective;
    }

    public static String getWorkbenchPartViewClass() {
        return workbenchPartView;
    }

    public static String getWorkbenchPartTitleDecorationsClass() {
        return workbenchPartTitleDecoration;
    }

    public static String getWorkbenchContextIdClass() {
        return workbenchContextId;
    }

    public static String getWorkbenchPartTitleClass() {
        return workbenchPartTitle;
    }

    public static String getDefaultPositionClass() {
        return defaultPosition;
    }

    public static String getWorkbenchContextClass() {
        return workbenchContext;
    }

    public static String getWorkbenchEditorClass() {
        return workbenchEditor;
    }

    public static String getWorkbenchClientEditorClass() {
        return workbenchClientEditor;
    }

    public static String getWorkbenchPopupClass() {
        return workbenchPopup;
    }

    public static String getWorkbenchPerspectiveClass() {
        return workbenchPerspective;
    }

    public static String getWorkbenchPanel() {
        return workbenchPanel;
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
     * Returns the value of the Boolean-valued Annotation parameter on the given type, ignoring any default value that
     * exists on the annotation. Returns false if the type lacks the given annotation, or if the annotation
     * lacks the given parameter.
     */
    private static Boolean getAnnotationBooleanParam(TypeElement target,
                                                     String annotationClassName,
                                                     String annotationParamName) {
        AnnotationValue paramValue = getAnnotationParamValue(target,
                annotationClassName,
                annotationParamName);
        if (paramValue == null) {
            return null;
        }
        return Boolean.parseBoolean(paramValue.getValue().toString());
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
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues()
                        .entrySet()) {
                    if (annotationName.equals(entry.getKey().getSimpleName().toString())) {
                        return entry.getValue();
                    }
                }
            }
        }
        return null;
    }

    public static Boolean getWbPerspectiveScreenIsDefaultValueOnClass(TypeElement classElement) {
        String bool = (getAnnotationStringParam(classElement,
                workbenchPerspective,
                IS_DEFAULT));
        return Boolean.valueOf(bool);
    }

    public static Boolean getWbPerspectiveScreenIsDynamicValueOnClass(TypeElement classElement) {
        String bool = (getAnnotationStringParam(classElement,
                workbenchPerspective,
                IS_DYNAMIC));
        return Boolean.valueOf(bool);
    }

    public static Boolean getWbScreenIsDynamicValueOnClass(TypeElement classElement) {
        String bool = (getAnnotationStringParam(classElement,
                workbenchScreen,
                IS_DYNAMIC));
        return Boolean.valueOf(bool);
    }

    public static Boolean getWbEditorIsDynamicValueOnClass(TypeElement classElement) {
        String bool = (getAnnotationStringParam(classElement,
                workbenchEditor,
                IS_DYNAMIC));
        return Boolean.valueOf(bool);
    }

    public static Boolean getWbPerspectiveScreenIsTransientValueOnClass(TypeElement classElement) {
        String bool = (getAnnotationStringParam(classElement,
                workbenchPerspective,
                IS_TRANSIENT));

        // XXX this is non-ideal because it restates the default of the isTransient property
        // we should use the getAnnotationValueWithDefaults method in this entire class so
        // we aren't redundantly declaring defaults here
        if (bool.isEmpty()) {
            return true;
        }

        return Boolean.valueOf(bool);
    }

    public static String getWbPerspectiveScreenIdentifierValueOnClass(TypeElement classElement) {
        return getAnnotationStringParam(classElement,
                workbenchPerspective,
                IDENTIFIER);
    }

    public static String getWbPopupScreenIdentifierValueOnClass(TypeElement classElement) {
        return getAnnotationStringParam(classElement,
                workbenchPopup,
                IDENTIFIER);
    }

    public static String getWbPopupScreenSizeValueOnClass(TypeElement classElement) {
        return getAnnotationStringParam(classElement,
                workbenchPopup,
                SIZE);
    }

    public static String getWbScreenIdentifierValueOnClass(TypeElement classElement) {
        return getAnnotationStringParam(classElement,
                workbenchScreen,
                IDENTIFIER);
    }

    public static String getWbEditorIdentifierValueOnClass(TypeElement classElement) {
        return getAnnotationStringParam(classElement,
                workbenchEditor,
                IDENTIFIER);
    }

    public static String getWbContextIdentifierValueOnClass(TypeElement classElement) {
        return getAnnotationStringParam(classElement,
                workbenchContext,
                IDENTIFIER);
    }

    public static boolean isATemplate(Elements elementUtils,
                                      Element element) {
        return GeneratorUtils.getAnnotation(elementUtils,
                element,
                workbenchPanel) != null;
    }
}
