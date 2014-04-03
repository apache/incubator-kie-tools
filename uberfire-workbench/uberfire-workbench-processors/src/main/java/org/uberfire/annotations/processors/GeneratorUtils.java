/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.annotations.processors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

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
     * Get the method name annotated with {@code @OnStartup}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnStartupZeroParameterMethodName( final TypeElement classElement,
                                                              final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getVoidMethodName( classElement,
                                  processingEnvironment,
                                  APIModule.getOnStartupClass() );
    }

    /**
     * Get the method name annotated with {@code @OnStartup}. The method must be
     * public, non-static, have a return-type of void and take one parameter of
     * type {@code org.drools.guvnor.vfs.Path}.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnStartupPathParameterMethodName( final TypeElement classElement,
                                                              final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getVoidMethodName( classElement,
                                  processingEnvironment,
                                  new String[]{ BackendModule.getPathClass().getName() },
                                  APIModule.getOnStartupClass() );
    }

    /**
     * Get the method name annotated with {@code @OnStartup}. The method must be
     * public, non-static, have a return-type of void and take two parameters;
     * the first of type {@code org.drools.guvnor.vfs.Path} and the second of
     * type {@code org.uberfire.shared.mvp.PlaceRequest}.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnStartupPathPlaceRequestParametersMethodName( final TypeElement classElement,
                                                                           final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getVoidMethodName( classElement,
                                  processingEnvironment,
                                  new String[]{ BackendModule.getPathClass().getName(), APIModule.getPlaceRequestClass().getName() },
                                  APIModule.getOnStartupClass() );
    }

    public static String getOnContextAttachPanelDefinitionMethodName( final TypeElement classElement,
                                                                      final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getVoidMethodName( classElement,
                                  processingEnvironment,
                                  new String[]{ APIModule.getPanelDefinitionClass().getName() },
                                  APIModule.getOnContextAttachClass() );
    }

    /**
     * Get the method name annotated with {@code @OnStartup}. The method must be
     * public, non-static, have a return-type of void and take one parameter of
     * type {@code org.uberfire.shared.mvp.PlaceRequest}.
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnStartPlaceRequestParameterMethodName( final TypeElement classElement,
                                                                    final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        return getVoidMethodName( classElement,
                                  processingEnvironment,
                                  new String[]{ APIModule.getPlaceRequestClass().getName() },
                                  APIModule.getOnStartupClass() );
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

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of void and take zero parameters.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String getVoidMethodName( final TypeElement classElement,
                                             final ProcessingEnvironment processingEnvironment,
                                             final Class annotation ) throws GenerationException {

        final Types typeUtils = processingEnvironment.getTypeUtils();
        final TypeMirror requiredReturnType = typeUtils.getNoType( TypeKind.VOID );
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
                continue;
            }
            if ( !typeUtils.isSameType( actualReturnType,
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
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of void and take parameters matching 
    // those provided.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String getVoidMethodName( final TypeElement classElement,
                                             final ProcessingEnvironment processingEnvironment,
                                             final String[] parameterTypes,
                                             final Class annotation ) throws GenerationException {

        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = typeUtils.getNoType( TypeKind.VOID );
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
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
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    //Check whether the ExecutableElement's parameter list matches the requiredParameterTypes.
    //For a match to be found the number of parameters must equal together with both the sequence 
    //and types. 
    private static boolean doParametersMatch( final Types typeUtils,
                                              final Elements elementUtils,
                                              final ExecutableElement e,
                                              final String[] requiredParameterTypes ) {
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

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of boolean and take zero
    // parameters.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String getBooleanMethodName( final TypeElement classElement,
                                                final ProcessingEnvironment processingEnvironment,
                                                final Class annotation ) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( Boolean.class.getName() ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
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
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of String and take zero
    // parameters.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String getStringMethodName( final TypeElement classElement,
                                               final ProcessingEnvironment processingEnvironment,
                                               final Class annotation ) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( String.class.getName() ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
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
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of IsWidget and take zero
    // parameters.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static ExecutableElement getWidgetMethodName( final TypeElement classElement,
                                                          final ProcessingEnvironment processingEnvironment,
                                                          final Class annotation ) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "com.google.gwt.user.client.ui.IsWidget" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
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
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match;
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of PopupPanel and take zero
    // parameters.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String getPopupMethodName( final TypeElement classElement,
                                              final ProcessingEnvironment processingEnvironment,
                                              final Class annotation ) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "com.google.gwt.user.client.ui.PopupPanel" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
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
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of Position and take zero
    // parameters.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String getDefaultPositionMethodName( final TypeElement classElement,
                                                        final ProcessingEnvironment processingEnvironment,
                                                        final Class annotation ) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( APIModule.getPositionClass().getName() ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
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
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of WorkbenchMenuBar and take zero
    // parameters.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String getMenuBarMethodName( final TypeElement classElement,
                                                final ProcessingEnvironment processingEnvironment,
                                                final Class annotation ) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "org.uberfire.workbench.model.menu.Menus" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
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
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
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
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String getToolBarMethodName( final TypeElement classElement,
                                                final ProcessingEnvironment processingEnvironment,
                                                final Class annotation ) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "org.uberfire.workbench.model.toolbar.ToolBar" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
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
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of PerspectiveDefinition and take zero
    // parameters.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String getMethodName( final TypeElement classElement,
                                         final ProcessingEnvironment processingEnvironment,
                                         final String expectedReturnType,
                                         final Class annotation ) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( expectedReturnType ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
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
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    public static String getSecurityTraitList( final Element element ) throws GenerationException {

        final List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();

        final Set<String> traits = new HashSet<String>( annotationMirrors.size() );

        for ( final AnnotationMirror annotationMirror : annotationMirrors ) {
            final Element annotationElement = annotationMirror.getAnnotationType().asElement();
            if ( annotationElement.getAnnotation( SecurityModule.getSecurityTraitClass() ) != null ) {
                traits.add( annotationElement.asType().toString() );
            }
        }

        if ( traits.isEmpty() ) {
            return null;
        }

        return collectionAsString( traits );
    }

    public static String getRoleList( final Element element ) throws GenerationException {
        final List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        final Set<String> result = new HashSet<String>();
        for ( final AnnotationMirror annotationMirror : annotationMirrors ) {
            if ( annotationMirror.getAnnotationType().asElement().getAnnotation( SecurityModule.getRolesTypeClass() ) != null ) {
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

    public static <T extends AnnotationValue> List<String> extractValue( final T value ) {
        if ( value.getValue() instanceof Collection ) {
            final Collection varray = (List) value.getValue();
            final ArrayList<String> result = new ArrayList<String>( varray.size() );
            for ( final Object active : varray ) {
                result.addAll( extractValue( (AnnotationValue) active ) );
            }
            return result;
        }
        return new ArrayList<String>( 1 ) {{
            add( value.getValue().toString() );
        }};
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

    public static String formatAssociatedResources( final List<String> resourceTypes ) {
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
}
