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
package org.drools.guvnor.annotations.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.drools.guvnor.annotations.processors.exceptions.GenerationException;
import org.drools.guvnor.client.annotations.DefaultPosition;
import org.drools.guvnor.client.annotations.OnClose;
import org.drools.guvnor.client.annotations.OnFocus;
import org.drools.guvnor.client.annotations.OnLostFocus;
import org.drools.guvnor.client.annotations.OnMayClose;
import org.drools.guvnor.client.annotations.OnReveal;
import org.drools.guvnor.client.annotations.OnStart;
import org.drools.guvnor.client.annotations.WorkbenchScreen;
import org.drools.guvnor.client.annotations.WorkbenchPartTitle;
import org.drools.guvnor.client.annotations.WorkbenchPartView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * A source code generator for Activities
 */
public class ScreenActivityGenerator extends AbstractGenerator {

    private static final Logger logger = LoggerFactory.getLogger( ScreenActivityGenerator.class );

    public StringBuffer generate(final String packageName,
                                 final PackageElement packageElement,
                                 final String className,
                                 final TypeElement classElement,
                                 final ProcessingEnvironment processingEnvironment) throws GenerationException {
        logger.debug( "Starting code generation for [" + className + "]" );

        //Extract required information
        final WorkbenchScreen wbw = classElement.getAnnotation( WorkbenchScreen.class );
        final String tokenName = wbw.nameToken();
        final String onStartMethodName = getVoidMethodName( classElement,
                                                            processingEnvironment,
                                                            OnStart.class );
        final String mayCloseMethodName = getBooleanMethodName( classElement,
                                                                processingEnvironment,
                                                                OnMayClose.class );
        final String onCloseMethodName = getVoidMethodName( classElement,
                                                            processingEnvironment,
                                                            OnClose.class );
        final String onRevealMethodName = getVoidMethodName( classElement,
                                                             processingEnvironment,
                                                             OnReveal.class );
        final String onLostFocusMethodName = getVoidMethodName( classElement,
                                                                processingEnvironment,
                                                                OnLostFocus.class );
        final String onFocusMethodName = getVoidMethodName( classElement,
                                                            processingEnvironment,
                                                            OnFocus.class );
        final String getDefaultPositionMethodName = getDefaultPositionMethodName( classElement,
                                                                                  processingEnvironment,
                                                                                  DefaultPosition.class );
        final String getTitleMethodName = getStringMethodName( classElement,
                                                               processingEnvironment,
                                                               WorkbenchPartTitle.class );
        final String getWidgetMethodName = getIsWidgetMethodName( classElement,
                                                                  processingEnvironment,
                                                                  WorkbenchPartView.class );
        final boolean isWidget = getIsWidget( classElement,
                                              processingEnvironment );

        logger.debug( "Package name: " + packageName );
        logger.debug( "Class name: " + className );
        logger.debug( "Token name: " + tokenName );
        logger.debug( "onStartMethodName: " + onStartMethodName );
        logger.debug( "mayCloseMethodName: " + mayCloseMethodName );
        logger.debug( "onCloseMethodName: " + onCloseMethodName );
        logger.debug( "onRevealMethodName: " + onRevealMethodName );
        logger.debug( "onLostFocusMethodName: " + onLostFocusMethodName );
        logger.debug( "onFocusMethodName: " + onFocusMethodName );
        logger.debug( "getDefaultPositionMethodName: " + getDefaultPositionMethodName );
        logger.debug( "getTitleMethodName: " + getTitleMethodName );
        logger.debug( "getWidgetMethodName: " + getWidgetMethodName );
        logger.debug( "isWidget: " + Boolean.toString( isWidget ) );

        //Validate getWidgetMethodName and isWidget
        if ( !isWidget && getWidgetMethodName == null ) {
            throw new GenerationException( "The WorkbenchPart must either extend isWidget or provide a @WorkbenchPartView annotated method to return an IsWidget." );
        }
        if ( isWidget && getWidgetMethodName != null ) {
            logger.warn( "The WorkbenchPart both extends isWidget and provides a @WorkbenchPartView annotated method. The annotated method will take precedence." );
        }

        //Setup data for template sub-system
        Map<String, Object> root = new HashMap<String, Object>();
        root.put( "packageName",
                  packageName );
        root.put( "className",
                  className );
        root.put( "tokenName",
                  tokenName );
        root.put( "realClassName",
                  classElement.getSimpleName().toString() );
        root.put( "onStartMethodName",
                  onStartMethodName );
        root.put( "mayCloseMethodName",
                  mayCloseMethodName );
        root.put( "onCloseMethodName",
                  onCloseMethodName );
        root.put( "onRevealMethodName",
                  onRevealMethodName );
        root.put( "onLostFocusMethodName",
                  onLostFocusMethodName );
        root.put( "onFocusMethodName",
                  onFocusMethodName );
        root.put( "getDefaultPositionMethodName",
                  getDefaultPositionMethodName );
        root.put( "getTitleMethodName",
                  getTitleMethodName );
        root.put( "getWidgetMethodName",
                  getWidgetMethodName );
        root.put( "isWidget",
                  isWidget );

        //Generate code
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter( sw );
        try {
            final Template template = config.getTemplate( "activityScreen.ftl" );
            template.process( root,
                              bw );
        } catch ( IOException ioe ) {
            throw new GenerationException( ioe );
        } catch ( TemplateException te ) {
            throw new GenerationException( te );
        } finally {
            try {
                bw.close();
                sw.close();
            } catch ( IOException ioe ) {
                throw new GenerationException( ioe );
            }
        }
        logger.debug( "Successfully generated code for [" + className + "]" );

        return sw.getBuffer();
    }

    //Lookup a public method name with the given annotation. 
    //The method must be public, non-static, have a return-type of void and take zero parameters.
    @SuppressWarnings({"rawtypes", "unchecked"})
    private String getVoidMethodName(final TypeElement classElement,
                                     final ProcessingEnvironment processingEnvironment,
                                     final Class annotation) throws GenerationException {

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

    //Lookup a public method name with the given annotation. 
    //The method must be public, non-static, have a return-type of boolean and take zero parameters.
    @SuppressWarnings({"rawtypes", "unchecked"})
    private String getBooleanMethodName(final TypeElement classElement,
                                        final ProcessingEnvironment processingEnvironment,
                                        final Class annotation) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "java.lang.Boolean" ).asType();
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

    //Lookup a public method name with the given annotation. 
    //The method must be public, non-static, have a return-type of String and take zero parameters.
    @SuppressWarnings({"rawtypes", "unchecked"})
    private String getStringMethodName(final TypeElement classElement,
                                       final ProcessingEnvironment processingEnvironment,
                                       final Class annotation) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "java.lang.String" ).asType();
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

    //Lookup a public method name with the given annotation. 
    //The method must be public, non-static, have a return-type of IsWidget and take zero parameters.
    @SuppressWarnings({"rawtypes", "unchecked"})
    private String getIsWidgetMethodName(final TypeElement classElement,
                                         final ProcessingEnvironment processingEnvironment,
                                         final Class annotation) throws GenerationException {
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
        return match.getSimpleName().toString();
    }

    //Check whether the provided type extends IsWidget.
    private boolean getIsWidget(final TypeElement classElement,
                                final ProcessingEnvironment processingEnvironment) {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "com.google.gwt.user.client.ui.IsWidget" ).asType();
        return typeUtils.isAssignable( classElement.asType(),
                                       requiredReturnType );
    }

    //Lookup a public method name with the given annotation. 
    //The method must be public, non-static, have a return-type of Position and take zero parameters.
    @SuppressWarnings({"rawtypes", "unchecked"})
    private String getDefaultPositionMethodName(final TypeElement classElement,
                                                final ProcessingEnvironment processingEnvironment,
                                                final Class annotation) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "org.drools.guvnor.client.workbench.Position" ).asType();
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

}
