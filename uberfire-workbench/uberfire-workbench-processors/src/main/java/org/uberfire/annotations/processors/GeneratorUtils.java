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

import static java.util.Collections.singletonList;
import static org.uberfire.annotations.processors.facades.ClientAPIModule.OWNING_PERSPECTIVE;
import static org.uberfire.annotations.processors.facades.ClientAPIModule.workbenchEditor;
import static org.uberfire.annotations.processors.facades.ClientAPIModule.workbenchScreen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
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
import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.annotations.processors.facades.APIModule;
import org.uberfire.annotations.processors.facades.BackendModule;
import org.uberfire.annotations.processors.facades.ClientAPIModule;
import org.uberfire.annotations.processors.facades.SecurityModule;

/**
 * Utilities for code generation
 */
public class GeneratorUtils {

    /**
     * Handy constant for an emtpy array of argument types.
     */
    private static final String[] NO_PARAMS = new String[0];

    /**
     * Passing a reference to exactly this array causes
     * {@link #getAnnotatedMethod(TypeElement, ProcessingEnvironment, String, TypeMirror, String[])} and
     * friends not to care about parameter types.
     */
    private static final String[] ANY_PARAMS = new String[0];

    /**
     * Finds the {@code @OnStartup} method suitable for workbench classes that are not {@code @WorkbenchEditor}.
     * The method must be public, non-static, have a return-type of void and either take zero parameters or one
     * parameter of type {@code PlaceRequest}.
     * <p>
     * If no such method is found, returns null. If methods annotated with {@code @OnStartup} are found but they do not
     * satisfy all the requirements, they are marked with errors explaining the problem.
     */
    public static ExecutableElement getOnStartupMethodForNonEditors( final TypeElement classElement,
                                                                     final ProcessingEnvironment processingEnvironment ) {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final TypeMirror requiredReturnType = typeUtils.getNoType( TypeKind.VOID );

        List<ExecutableElement> onStartupMethods = getAnnotatedMethods(
                classElement, processingEnvironment, APIModule.getOnStartupClass(), requiredReturnType, ANY_PARAMS );
        Elements elementUtils = processingEnvironment.getElementUtils();

        ExecutableElement zeroArgMethod = null;
        ExecutableElement oneArgMethod = null;
        for ( ExecutableElement m : onStartupMethods ) {
            if ( doParametersMatch( typeUtils, elementUtils, m, NO_PARAMS ) ) {
                zeroArgMethod = m;
            } else if ( doParametersMatch( typeUtils, elementUtils, m, new String[] { APIModule.getPlaceRequestClass() } ) ) {
                oneArgMethod = m;
            } else {
                processingEnvironment.getMessager().printMessage(
                        Kind.ERROR,
                        formatProblemsList( APIModule.getOnStartupClass(),
                                singletonList( "take no arguments or one argument of type " + APIModule.getPlaceRequestClass() ) ));
            }
        }

        if ( zeroArgMethod != null && oneArgMethod != null ) {
            // TODO multiple methods should be allowed, but only if inherited. See UF-42.
            processingEnvironment.getMessager().printMessage(
                    Kind.ERROR,
                    "Found multiple @OnStartup methods. Each class can declare at most one.",
                    zeroArgMethod );
        }

        if ( oneArgMethod != null ) {
            return oneArgMethod;
        }

        return zeroArgMethod;
    }

    /**
     * Finds the {@code @OnStartup} method suitable for {@code @WorkbenchEditor} classes.
     * The method must be public, non-static, have a return-type of void and either take one parameter
     * of type {@code Path} or two parameters of type {@code (Path, PlaceRequest)}.
     * <p>
     * If no such method is found, returns null. If methods annotated with {@code @OnStartup} are found but they do not
     * satisfy all the requirements, they are marked with errors explaining the problem.
     */
    public static ExecutableElement getOnStartupMethodForEditors( final TypeElement classElement,
                                                                     final ProcessingEnvironment processingEnvironment ) {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final TypeMirror requiredReturnType = typeUtils.getNoType( TypeKind.VOID );

        List<ExecutableElement> onStartupMethods = getAnnotatedMethods(
                classElement, processingEnvironment, APIModule.getOnStartupClass(), requiredReturnType, ANY_PARAMS );
        Elements elementUtils = processingEnvironment.getElementUtils();

        ExecutableElement oneArgMethod = null;
        ExecutableElement twoArgMethod = null;
        for ( ExecutableElement m : onStartupMethods ) {
            if ( doParametersMatch( typeUtils, elementUtils, m, new String[] { BackendModule.getPathClass() } ) ) {
                oneArgMethod = m;
            } else if ( doParametersMatch( typeUtils, elementUtils, m, new String[] { BackendModule.getPathClass(), APIModule.getPlaceRequestClass() } ) ) {
                twoArgMethod = m;
            } else {
                processingEnvironment.getMessager().printMessage(
                        Kind.ERROR,
                        formatProblemsList( APIModule.getOnStartupClass(),
                                singletonList( "take one argument of type " + BackendModule.getPathClass() + " and an optional second argument of type " + APIModule.getPlaceRequestClass() ) ));
            }
        }

        if ( oneArgMethod != null && twoArgMethod != null ) {
            // TODO make this an error (need to take inherited methods into account). See UF-76.
            processingEnvironment.getMessager().printMessage(
                    Kind.WARNING,
                    "There is also an @OnStartup(Path, PlaceRequest) method in this class. That method takes precedence over this one.",
                    oneArgMethod );
        }

        if ( twoArgMethod != null ) {
            return twoArgMethod;
        }

        return oneArgMethod;
    }

    public static String getOnContextAttachPanelDefinitionMethodName( final TypeElement classElement,
                                                                      final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getVoidMethodName( classElement,
                processingEnvironment,
                new String[]{ APIModule.getPanelDefinitionClass() },
                APIModule.getOnContextAttachClass() );
    }

    /**
     * Get the method name annotated with {@code @OnMayClose}. The method must
     * be public, non-static, have a return-type of void and take zero
     * parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnMayCloseMethodName( final TypeElement classElement,
                                                  final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getBooleanMethodName( classElement,
                processingEnvironment,
                APIModule.getOnMayCloseClass() );
    }

    /**
     * Get the method name annotated with {@code @OnClose}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnCloseMethodName( final TypeElement classElement,
                                               final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getVoidMethodName( classElement,
                processingEnvironment,
                APIModule.getOnCloseClass() );
    }

    /**
     * Get the method name annotated with {@code @OnShutdown}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnShutdownMethodName( final TypeElement classElement,
                                                  final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getVoidMethodName( classElement,
                processingEnvironment,
                APIModule.getOnShutdownlass() );
    }

    /**
     * Get the method name annotated with {@code @OnOpen}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnOpenMethodName( final TypeElement classElement,
                                              final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getVoidMethodName( classElement,
                processingEnvironment,
                APIModule.getOnOpenClass() );
    }

    /**
     * Get the method name annotated with {@code @OnLostFocus}. The method must
     * be public, non-static, have a return-type of void and take zero
     * parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnLostFocusMethodName( final TypeElement classElement,
                                                   final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getVoidMethodName( classElement,
                processingEnvironment,
                APIModule.getOnLostFocusClass() );
    }

    /**
     * Get the method name annotated with {@code @OnFocus}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnFocusMethodName( final TypeElement classElement,
                                               final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getVoidMethodName( classElement,
                processingEnvironment,
                APIModule.getOnFocusClass() );
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
    public static String getDefaultPositionMethodName( final TypeElement classElement,
                                                       final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getDefaultPositionMethodName( classElement,
                processingEnvironment,
                ClientAPIModule.getDefaultPositionClass() );
    }

    /**
     * Get the method name annotated with {@code @WorkbenchPartTitle}. The
     * method must be public, non-static, have a return-type of java.lang.String
     * and take zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getTitleMethodName( final TypeElement classElement,
                                             final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getStringMethodName( classElement,
                processingEnvironment,
                ClientAPIModule.getWorkbenchPartTitleClass() );
    }

    public static String getContextIdMethodName( final TypeElement classElement,
                                                 final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getStringMethodName( classElement,
                processingEnvironment,
                ClientAPIModule.getWorkbenchContextIdClass() );
    }

    /**
     * Get the method name annotated with {@code @WorkbenchPartTitleDecoration}. The
     * method must be public, non-static, have a return-type of
     * com.google.gwt.user.client.ui.IsWidget and take zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static ExecutableElement getTitleWidgetMethodName( final TypeElement classElement,
                                                              final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getWidgetMethodName( classElement,
                processingEnvironment,
                ClientAPIModule.getWorkbenchPartTitleDecorationsClass() );
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
    public static ExecutableElement getWidgetMethodName( final TypeElement classElement,
                                                         final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getWidgetMethodName( classElement,
                processingEnvironment,
                ClientAPIModule.getWorkbenchPartViewClass() );
    }

    /**
     * Check whether the provided type extends IsWidget.
     * @param classElement
     * @param processingEnvironment
     * @return
     */
    public static boolean getIsWidget( final TypeElement classElement,
                                       final ProcessingEnvironment processingEnvironment ) {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "com.google.gwt.user.client.ui.IsWidget" ).asType();
        return typeUtils.isAssignable( classElement.asType(),
                requiredReturnType );
    }

    public static boolean hasUberViewReference( final TypeElement classElement,
                                                final ProcessingEnvironment processingEnvironment,
                                                final ExecutableElement getWidgetMethod ) {
        if ( getWidgetMethod == null ) {
            return false;
        }
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "org.uberfire.client.mvp.UberView" ).asType();

        return typeUtils.isAssignable( typeUtils.erasure( getWidgetMethod.getReturnType() ),
                requiredReturnType );
    }

    /**
     * Get the method name annotated with {@code @WorkbenchPartView}. The method
     * must be public, non-static, have a return-type of PopupPanel and take
     * zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getPopupMethodName( final TypeElement classElement,
                                             final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getPopupMethodName( classElement,
                processingEnvironment,
                ClientAPIModule.getWorkbenchPartViewClass() );
    }

    /**
     * Check whether the provided type extends PopupPanel.
     * @param classElement
     * @param processingEnvironment
     * @return
     */
    public static boolean getIsPopup( final TypeElement classElement,
                                      final ProcessingEnvironment processingEnvironment ) {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "com.google.gwt.user.client.ui.PopupPanel" ).asType();
        return typeUtils.isAssignable( classElement.asType(),
                requiredReturnType );
    }

    /**
     * Get the method name annotated with {@code @IsDirty}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getIsDirtyMethodName( final TypeElement classElement,
                                               final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getBooleanMethodName( classElement,
                processingEnvironment,
                APIModule.getIsDirtyClass() );
    }

    /**
     * Get the method name annotated with {@code @OnSave}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnSaveMethodName( final TypeElement classElement,
                                              final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getVoidMethodName( classElement,
                processingEnvironment,
                APIModule.getOnSaveClass() );
    }

    /**
     * Get the method name annotated with {@code @WorkbenchMenu}. The method
     * must be public, non-static, have a return-type of WorkbenchMenuBar and
     * take zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getMenuBarMethodName( final TypeElement classElement,
                                               final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getMenuBarMethodName( classElement,
                processingEnvironment,
                ClientAPIModule.getWorkbenchMenuClass() );
    }

    /**
     * Get the method name annotated with {@code @WorkbenchToolBar}. The method
     * must be public, non-static, have a return-type of WorkbenchToolBar and
     * take zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getToolBarMethodName( final TypeElement classElement,
                                               final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getToolBarMethodName( classElement,
                processingEnvironment,
                ClientAPIModule.getWorkbenchToolBarClass() );
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
    public static String getPerspectiveMethodName( final TypeElement classElement,
                                                   final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getMethodName( classElement,
                processingEnvironment,
                "org.uberfire.workbench.model.PerspectiveDefinition",
                ClientAPIModule.getPerspectiveClass() );
    }

    public static String getSplashFilterMethodName( final TypeElement classElement,
                                                    final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getMethodName( classElement,
                processingEnvironment,
                "org.uberfire.workbench.model.SplashScreenFilter",
                ClientAPIModule.getSplashFilterClass() );
    }

    public static String getBodyHeightMethodName( TypeElement classElement,
                                                  ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getMethodName( classElement,
                processingEnvironment,
                "java.lang.Integer",
                ClientAPIModule.getSplashBodyHeightClass() );
    }

    public static String getInterceptMethodName( final TypeElement classElement,
                                                 final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getMethodName( classElement,
                processingEnvironment,
                "java.lang.Boolean",
                ClientAPIModule.getInterceptClass() );
    }

    public static String getBeanActivatorClassName( final TypeElement classElement,
                                                    final ProcessingEnvironment processingEnvironment ) {
        AnnotationMirror activatedByAnnotation = getAnnotation( processingEnvironment.getElementUtils(), classElement, APIModule.activatedBy );
        if ( activatedByAnnotation != null ) {
            return extractAnnotationStringValue( processingEnvironment.getElementUtils(), activatedByAnnotation, "value" );
        }
        return null;
    }

    /**
     * Returns the identifier (PlaceRequest ID) of the perspective that owns the given part.
     *
     * @param screenOrEditorClass
     *            a type annotated with either {@code @WorkbenchScreen} or {@code @WorkbenchEditor}. Not null.
     * @param processingEnvironment
     *            the current annotation processing environment.
     * @return
     * @throws GenerationException
     *             if the owningPerspective parameter is present, but points to something other than a
     *             {@code @WorkbenchPerspective} class.
     */
    public static String getOwningPerspectivePlaceRequest( TypeElement screenOrEditorClass, ProcessingEnvironment processingEnvironment ) throws GenerationException {
        Elements elementUtils = processingEnvironment.getElementUtils();
        final Types typeUtils = processingEnvironment.getTypeUtils();

        AnnotationMirror screenOrEditorAnnotation = getAnnotation( elementUtils, screenOrEditorClass, workbenchScreen );
        if ( screenOrEditorAnnotation == null ) {
            screenOrEditorAnnotation = getAnnotation( elementUtils, screenOrEditorClass, workbenchEditor );
        }

        AnnotationValue owningPerspectiveParam = extractAnnotationPropertyValue( elementUtils, screenOrEditorAnnotation, OWNING_PERSPECTIVE );
        final TypeElement owningPerspectiveType = (TypeElement) typeUtils.asElement( (TypeMirror) owningPerspectiveParam.getValue() );
        if ( owningPerspectiveType == null ) {
            return null;
        }

        final String owningPerspectivePlace = ClientAPIModule.getWbPerspectiveScreenIdentifierValueOnClass( owningPerspectiveType );
        if ( owningPerspectivePlace.equals( "" ) ) {
            processingEnvironment.getMessager()
                .printMessage( Kind.ERROR,
                               "owningPerspective must be a class annotated with @WorkbenchPerspective.",
                               screenOrEditorClass,
                               screenOrEditorAnnotation,
                               owningPerspectiveParam );
            return null;
        }
        return owningPerspectivePlace;
    }

    /**
     * Searches for an accessible method annotated with the given annotation. The method must be non-private,
     * non-static, take no arguments, and return void.
     * <p>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above, the
     * method will be marked with an error explaining the problem.
     *
     * @param classElement
     *            the class to search for the annotated method.
     * @param processingEnvironment
     *            the current annotation processing environment.
     * @param annotationName
     *            the fully-qualified name of the annotation to search for
     * @return the name of the method that satisfies all the requirements and bears the given annotation, or null if
     *         there is no such method.
     */
    private static String getVoidMethodName( final TypeElement classElement,
                                             final ProcessingEnvironment processingEnvironment,
                                             final String annotationName ) throws GenerationException {

        final Types typeUtils = processingEnvironment.getTypeUtils();
        final TypeMirror requiredReturnType = typeUtils.getNoType( TypeKind.VOID );

        ExecutableElement match = getUniqueAnnotatedMethod(
                classElement,
                processingEnvironment,
                annotationName,
                requiredReturnType,
                NO_PARAMS);
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of void and take parameters matching
    // those provided.
    private static String getVoidMethodName( final TypeElement classElement,
                                             final ProcessingEnvironment processingEnvironment,
                                             final String[] parameterTypes,
                                             final String annotationName ) throws GenerationException {

        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = typeUtils.getNoType( TypeKind.VOID );
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( getAnnotation( elementUtils, e, annotationName ) == null ) {
                continue;
            }
            if ( !typeUtils.isSameType( actualReturnType,
                    requiredReturnType ) ) {
                continue;
            }
            if ( !doParametersMatch( typeUtils,
                    elementUtils,
                    e,
                    parameterTypes ) ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            if ( match != null ) {
                throw new GenerationException( "Multiple methods with @" + fqcnToSimpleName( annotationName ) + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    /**
     * Checks whether the ExecutableElement's parameter list matches the requiredParameterTypes (order matters).
     *
     * @param typeUtils
     *            type utils from current processing environment.
     * @param elementUtils
     *            element utils from current processing environment.
     * @param e
     *            the method whose parameter list to check.
     * @param requiredParameterTypes
     *            the required parameter types. Must not be null.
     *            If a reference to {@link #ANY_PARAMS}, this method returns true without any further checks.
     * @return true if the target method's parameter list matches the given required parameter types, or if the special
     *         {@link #ANY_PARAMS} value is passed as {@code requiredParameterTypes}. False otherwise.
     */
    private static boolean doParametersMatch( final Types typeUtils,
                                              final Elements elementUtils,
                                              final ExecutableElement e,
                                              final String[] requiredParameterTypes ) {
        if ( requiredParameterTypes == ANY_PARAMS ) {
            return true;
        }
        if ( e.getParameters().size() != requiredParameterTypes.length ) {
            return false;
        }
        List<TypeMirror> requiredTypes = new ArrayList<TypeMirror>();
        for ( String parameterType : requiredParameterTypes ) {
            requiredTypes.add( elementUtils.getTypeElement( parameterType ).asType() );
        }
        for ( int i = 0; i < requiredTypes.size(); i++ ) {
            final TypeMirror actualType = e.getParameters().get( i ).asType();
            final TypeMirror requiredType = requiredTypes.get( i );
            if ( !typeUtils.isAssignable( actualType,
                    requiredType ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds a public, non-static, no-args method annotated with the given annotation which returns boolean.
     * <p>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above, the
     * method will be marked with an error explaining the problem.
     * <p>
     * If more than one method satisfies all the criteria, all such methods are marked with an error explaining the
     * problem.
     *
     * @param classElement
     *            the class to search for the annotated method.
     * @param processingEnvironment
     *            the current annotation processing environment.
     * @param annotationName
     *            the fully-qualified name of the annotation to search for
     * @return null if no such method exists; otherwise, the method's name.
     */
    private static String getBooleanMethodName( final TypeElement classElement,
                                                final ProcessingEnvironment processingEnvironment,
                                                final String annotationName ) throws GenerationException {
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( Boolean.class.getName() ).asType();
        ExecutableElement match = getUniqueAnnotatedMethod(
                classElement,
                processingEnvironment,
                annotationName,
                requiredReturnType,
                NO_PARAMS );
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    /**
     * Finds a public, non-static, no-args method annotated with the given annotation which returns String.
     * <p>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above, the
     * method will be marked with an error explaining the problem.
     * <p>
     * If more than one method satisfies all the criteria, all such methods are marked with an error explaining the
     * problem.
     *
     * @return null if no such method exists; otherwise, the method's name.
     */
    private static String getStringMethodName( final TypeElement classElement,
                                               final ProcessingEnvironment processingEnvironment,
                                               final String annotationName ) throws GenerationException {

        final Elements elementUtils = processingEnvironment.getElementUtils();

        ExecutableElement match = getUniqueAnnotatedMethod( classElement,
                processingEnvironment,
                annotationName,
                elementUtils.getTypeElement( String.class.getName() ).asType(),
                NO_PARAMS );

        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    /**
     * Finds a public, non-static, no-args method annotated with the given annotation which returns boolean.
     * <p>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above, the
     * method will be marked with an error explaining the problem.
     * <p>
     * If more than one method satisfies all the criteria, all such methods are marked with an error explaining the
     * problem.
     *
     * @return null if no such method exists; otherwise, the method's name.
     */
    private static ExecutableElement getWidgetMethodName( final TypeElement originalClassElement,
                                                          final ProcessingEnvironment processingEnvironment,
                                                          final String annotationName ) throws GenerationException {
        final Elements elementUtils = processingEnvironment.getElementUtils();
        return getUniqueAnnotatedMethod( originalClassElement,
                processingEnvironment,
                annotationName,
                elementUtils.getTypeElement( "com.google.gwt.user.client.ui.IsWidget" ).asType(),
                NO_PARAMS );
    }

    /**
     * Finds a public, non-static, method annotated with the given annotation which returns the given type and accepts
     * the given arguments.
     * <p>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above, the
     * method will be marked with an error explaining the problem. This will trigger a compilation failure.
     * <p>
     * If more than one method satisfies all the criteria, all such methods are marked with an error explaining the
     * problem.
     *
     * @param originalClassElement
     *            the class to search for the annotated method.
     * @param processingEnvironment
     *            the current annotation processing environment.
     * @param annotationName
     *            the fully-qualified name of the annotation to search for.
     * @param requiredReturnType
     *            the fully qualified name of the type the method must return.
     * @param requiredParameterTypes
     *            the parameter types the method must take. If the method must take no parameters, use
     *            {@link #NO_PARAMS}. If the method can take any parameters, use {@link #ANY_PARAMS}.
     * @return null if no such method exists; null if multiple methods satisfying the criteria are found; otherwise, a
     *         reference to the method.
     */
    private static ExecutableElement getUniqueAnnotatedMethod( final TypeElement originalClassElement,
                                                               final ProcessingEnvironment processingEnvironment,
                                                               final String annotationName,
                                                               final TypeMirror requiredReturnType,
                                                               final String[] requiredParameterTypes ) {

        List<ExecutableElement> matches = getAnnotatedMethods(
                originalClassElement, processingEnvironment, annotationName, requiredReturnType, requiredParameterTypes );

        if ( matches.size() == 1 ) {
            return matches.get( 0 );
        } else if ( matches.size() > 1 ) {
            for ( ExecutableElement match : matches ) {
                processingEnvironment.getMessager().printMessage(
                        Kind.ERROR,
                        "Found multiple methods annotated with @" + fqcnToSimpleName( annotationName ) + ". There should only be one.",
                        match );
            }
        }

        return null;
    }

    /**
     * Finds all public, non-static, no-args method annotated with the given annotation which returns the given type.
     * <p>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above, the
     * method will be marked with an error explaining the problem. This will trigger a compilation failure.
     * <p>
     * If more than one method satisfies all the criteria, all such methods are marked with an error explaining the
     * problem.
     *
     * @param originalClassElement
     *            the class to search for the annotated method.
     * @param processingEnvironment
     *            the current annotation processing environment.
     * @param annotationName
     *            the fully-qualified name of the annotation to search for.
     * @param requiredReturnType
     *            the fully qualified name of the type the method must return.
     * @param requiredParameterTypes
     *            the parameter types the method must take. If the method must take no parameters, use
     *            {@link #NO_PARAMS}. If the method can take any parameters, use {@link #ANY_PARAMS}.
     * @return a list of references to the methods that satisfy the criteria (empty list if no such method exists).
     */
    private static List<ExecutableElement> getAnnotatedMethods( final TypeElement originalClassElement,
                                                               final ProcessingEnvironment processingEnvironment,
                                                               final String annotationName,
                                                               final TypeMirror requiredReturnType,
                                                               final String[] requiredParameterTypes ) {

        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();

        TypeElement classElement = originalClassElement;
        while ( true ) {
            final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

            List<ExecutableElement> matches = new ArrayList<ExecutableElement>();
            for ( ExecutableElement e : methods ) {

                final TypeMirror actualReturnType = e.getReturnType();

                if ( getAnnotation( elementUtils, e, annotationName ) == null ) {
                    continue;
                }

                List<String> problems = new ArrayList<String>();

                if ( !typeUtils.isAssignable( actualReturnType, requiredReturnType ) ) {
                    problems.add( "return " + requiredReturnType );
                }
                if ( !doParametersMatch( typeUtils, elementUtils, e, requiredParameterTypes ) ) {
                    if ( requiredParameterTypes.length == 0 ) {
                        problems.add( "take no parameters" );
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append( "take " ).append( requiredParameterTypes ).append( " parameters of type (" );
                        boolean first = true;
                        for ( String p : requiredParameterTypes ) {
                            if ( !first ) {
                                sb.append( ", " );
                            }
                            sb.append( p );
                            first = false;
                        }
                        sb.append( ")" );
                        problems.add( sb.toString() );
                    }
                }
                if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                    problems.add( "be non-static" );
                }
                if ( e.getModifiers().contains( Modifier.PRIVATE ) ) {
                    problems.add( "be non-private" );
                }


                if ( problems.isEmpty() ) {
                    matches.add( e );
                } else {
                    processingEnvironment.getMessager().printMessage(
                            Kind.ERROR, formatProblemsList( annotationName, problems ), e );
                }
            }

            if ( !matches.isEmpty() ) {
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
     *
     * @param annotationFqcn
     *            the fully-qualified name of the annotation the problems pertain to.
     * @param problems
     *            the list of problems, as verb phrases. Must not be null, and should contain at least one item.
     * @return a nice English sentence summarizing the problems.
     */
    static String formatProblemsList( final String annotationFqcn, List<String> problems ) {
        StringBuilder msg = new StringBuilder();
        msg.append( "Methods annotated with @" )
            .append( fqcnToSimpleName( annotationFqcn ) )
            .append( " must " );
        for ( int i = 0; i < problems.size(); i++ ) {
            if ( problems.size() > 2 && i > 0 ) {
                msg.append(", ");
            }
            if ( problems.size() == 2 && i == 1 ) {
                msg.append( " and " );
            }
            if ( problems.size() > 2 && i == problems.size() - 1 ) {
                msg.append( "and " );
            }
            msg.append( problems.get( i ) );
        }
        return msg.toString();
    }

    /**
     * Lookup a public method name with the given annotation. The method must be
     * public, non-static, have a return-type of PopupPanel and take zero
     * parameters.
     */
    private static String getPopupMethodName( final TypeElement classElement,
                                              final ProcessingEnvironment processingEnvironment,
                                              final String annotationName ) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "com.google.gwt.user.client.ui.PopupPanel" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( getAnnotation( elementUtils, e, annotationName ) == null ) {
                continue;
            }
            if ( !typeUtils.isAssignable( actualReturnType,
                    requiredReturnType ) ) {
                continue;
            }
            if ( e.getParameters().size() != 0 ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            if ( match != null ) {
                throw new GenerationException( "Multiple methods with @" + fqcnToSimpleName( annotationName ) + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    /**
     * Looks up a public method name with the given annotation. The method must be
     * public, non-static, have a return-type of Position and take zero
     * parameters.
     */
    private static String getDefaultPositionMethodName( final TypeElement classElement,
                                                        final ProcessingEnvironment processingEnvironment,
                                                        final String annotationName ) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( APIModule.getPositionClass() ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( getAnnotation( elementUtils, e, annotationName ) == null ) {
                continue;
            }
            if ( !typeUtils.isAssignable( actualReturnType,
                    requiredReturnType ) ) {
                continue;
            }
            if ( e.getParameters().size() != 0 ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            if ( match != null ) {
                throw new GenerationException( "Multiple methods with @" + fqcnToSimpleName( annotationName ) + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    public static AnnotationMirror getAnnotation( Elements elementUtils, Element annotationTarget, String annotationName ) {
        for ( AnnotationMirror annotation : elementUtils.getAllAnnotationMirrors( annotationTarget ) ) {
            if ( annotationName.contentEquals( getQualifiedName( annotation ) ) ) {
                return annotation;
            }
        }
        return null;
    }

    public static Name getQualifiedName( AnnotationMirror annotation ) {
        return ( (TypeElement) annotation.getAnnotationType().asElement() ).getQualifiedName();
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of WorkbenchMenuBar and take zero
    // parameters.
    private static String getMenuBarMethodName( final TypeElement classElement,
                                                final ProcessingEnvironment processingEnvironment,
                                                final String annotationName ) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "org.uberfire.workbench.model.menu.Menus" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( getAnnotation( elementUtils, e, annotationName ) == null ) {
                continue;
            }
            if ( !typeUtils.isAssignable( actualReturnType,
                    requiredReturnType ) ) {
                continue;
            }
            if ( e.getParameters().size() != 0 ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            if ( match != null ) {
                throw new GenerationException( "Multiple methods with @" + fqcnToSimpleName( annotationName ) + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of WorkbenchToolBar and take zero
    // parameters.
    private static String getToolBarMethodName( final TypeElement classElement,
                                                final ProcessingEnvironment processingEnvironment,
                                                final String annotationName ) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "org.uberfire.workbench.model.toolbar.ToolBar" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( getAnnotation( elementUtils, e, annotationName ) == null ) {
                continue;
            }
            if ( !typeUtils.isAssignable( actualReturnType,
                    requiredReturnType ) ) {
                continue;
            }
            if ( e.getParameters().size() != 0 ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            if ( match != null ) {
                throw new GenerationException( "Multiple methods with @" + fqcnToSimpleName( annotationName ) + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    /**
     * Finds a public, non-static, no-args method annotated with the given annotation which returns the given type.
     * <p>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above, the
     * method will be marked with an error explaining the problem.
     * <p>
     * If more than one method satisfies all the criteria, all such methods are marked with an error explaining the
     * problem.
     *
     * @param classElement
     *            the class to search for the annotated method.
     * @param processingEnvironment
     *            the current annotation processing environment.
     * @param expectedReturnType
     *            the fully-qualified name of the type the method must return.
     * @param annotationName
     *            the fully-qualified name of the annotation to search for.
     * @return null if no such method exists; otherwise, the method's name.
     */
    private static String getMethodName( final TypeElement classElement,
                                         final ProcessingEnvironment processingEnvironment,
                                         final String expectedReturnType,
                                         final String annotationName ) throws GenerationException {
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( expectedReturnType ).asType();
        ExecutableElement match = getUniqueAnnotatedMethod(
                classElement,
                processingEnvironment,
                annotationName,
                requiredReturnType,
                NO_PARAMS );
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    public static String getSecurityTraitList( final Elements elementUtils, final Element element ) throws GenerationException {

        final List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();

        final Set<String> traits = new HashSet<String>( annotationMirrors.size() );

        for ( final AnnotationMirror annotationMirror : annotationMirrors ) {
            final Element annotationElement = annotationMirror.getAnnotationType().asElement();
            if ( getAnnotation( elementUtils, annotationElement, SecurityModule.getSecurityTraitClass() ) != null ) {
                traits.add( annotationElement.asType().toString() );
            }
        }

        if ( traits.isEmpty() ) {
            return null;
        }

        return collectionAsString( traits );
    }

    public static String getRoleList( final Elements elementUtils, final Element element ) throws GenerationException {
        final List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        final Set<String> result = new TreeSet<String>();
        for ( final AnnotationMirror annotationMirror : annotationMirrors ) {
            if ( getAnnotation( elementUtils, annotationMirror.getAnnotationType().asElement(), SecurityModule.getRolesTypeClass() ) != null ) {
                for ( final Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet() ) {
                    if ( entry.getKey().getSimpleName().toString().equals( "value" ) ) {
                        result.addAll( extractValue( entry.getValue() ) );
                    }
                }
            }
        }

        if ( result.isEmpty() ) {
            return null;
        }

        return collectionAsString( result );
    }

    /**
     * Provides a uniform way of working with single- and multi-valued AnnotationValue objects.
     *
     * @return the annotation values as strings. For multi-valued annotation params, the collection's iteration order matches the
     * order the values appeared in the source code. Single-valued params are wrapped in a single-element collection.
     * In either case, don't attempt to modify the returned collection.
     */
    public static Collection<String> extractValue( final AnnotationValue value ) {
        if ( value.getValue() instanceof Collection ) {
            final Collection<?> varray = (List<?>) value.getValue();
            final ArrayList<String> result = new ArrayList<String>( varray.size() );
            for ( final Object active : varray ) {
                result.addAll( extractValue( (AnnotationValue) active ) );
            }
            return result;
        }
        return Collections.singleton( value.getValue().toString() );
    }

    /**
     * Pulls nested annotations out of the annotation that contains them.
     *
     * @param elementUtils the current Elements object from this round of annotation processing.
     * @param element The element targeted by the containing annotation.
     * @param annotationName The containing annotation's fully-qualified name.
     * @param paramName The name of the parameter on the containing annotation. The parameter's type must be an array of annotations.
     */
    public static List<AnnotationMirror> extractAnnotationsFromAnnotation( Elements elementUtils,
                                                                            Element element,
                                                                            String annotationName,
                                                                            String paramName ) {
            final AnnotationMirror am = getAnnotation( elementUtils, element, annotationName );
            AnnotationValue nestedAnnotations = GeneratorUtils.extractAnnotationPropertyValue( elementUtils, am, paramName );
            if ( nestedAnnotations == null ) {
                return Collections.emptyList();
            }
            final List<AnnotationMirror> result = new ArrayList<AnnotationMirror>();
            nestedAnnotations.accept( new SimpleAnnotationValueVisitor6<Void, Void>() {
                @Override
                public Void visitArray( List<? extends AnnotationValue> vals, Void x ) {
                    for ( AnnotationValue av : vals ) {
                        av.accept( new SimpleAnnotationValueVisitor6<Void, Void>() {
                            @Override
                            public Void visitAnnotation( AnnotationMirror am, Void x ) {
                                result.add( am );
                                return null;
                            }
                        }, null );
                    }
                    return null;
                }
            }, null );
            return result;
        }

    private static String collectionAsString( final Collection<String> collection ) {
        final StringBuilder sb = new StringBuilder();

        Iterator<String> iterator = collection.iterator();
        int i = 0;
        while ( iterator.hasNext() ) {
            final String next = iterator.next();
            sb.append( '"' ).append( next ).append( '"' );
            if ( i + 1 < collection.size() ) {
                sb.append( ", " );
            }
            i++;
        }

        return sb.toString();
    }

    public static String formatAssociatedResources( final Collection<String> resourceTypes ) {
        if ( resourceTypes == null || resourceTypes.size() == 0 ) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();

        sb.append( "@AssociatedResources" ).append( "({\n" );
        for ( final String resourceType : resourceTypes ) {
            sb.append( "    " ).append( resourceType ).append( ".class" ).append( ",\n" );
        }
        sb.delete( sb.length() - 2,
                sb.length() );
        sb.append( "\n})\n" );

        return sb.toString();
    }

    private static String fqcnToSimpleName(String fqcn) {
        int lastIndexOfDot = fqcn.lastIndexOf( '.' );
        if (lastIndexOfDot != -1) {
            return fqcn.substring( lastIndexOfDot + 1 );
        }
        return fqcn;
    }

    public static boolean debugLoggingEnabled() {
        return Boolean.parseBoolean( System.getProperty( "org.uberfire.processors.debug", "false" ) );
    }

    public static String extractAnnotationStringValue( Elements elementUtils, AnnotationMirror annotation, CharSequence paramName ) {
        final AnnotationValue av = extractAnnotationPropertyValue( elementUtils, annotation, paramName );
        if ( av != null && av.getValue() != null ) {
            return av.getValue().toString();
        }
        return null;
    }

    static AnnotationValue extractAnnotationPropertyValue( Elements elementUtils,
                                                          AnnotationMirror annotation,
                                                          CharSequence annotationProperty ) {

        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationParams =
                elementUtils.getElementValuesWithDefaults( annotation );

        for ( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> param : annotationParams.entrySet() ) {
            if (param.getKey().getSimpleName().contentEquals( annotationProperty )) {
                return param.getValue();
            }
        }
        return null;
    }
}
