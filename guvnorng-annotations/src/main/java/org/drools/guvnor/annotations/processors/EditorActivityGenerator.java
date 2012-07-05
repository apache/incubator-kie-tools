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
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import org.drools.guvnor.annotations.processors.exceptions.GenerationException;
import org.drools.guvnor.client.annotations.WorkbenchScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * A source code generator for Activities
 */
public class EditorActivityGenerator extends AbstractGenerator {

    private static final Logger logger = LoggerFactory.getLogger( EditorActivityGenerator.class );

    public StringBuffer generate(final String packageName,
                                 final PackageElement packageElement,
                                 final String className,
                                 final TypeElement classElement,
                                 final ProcessingEnvironment processingEnvironment) throws GenerationException {
        logger.debug( "Starting code generation for [" + className + "]" );

        //Extract required information
        final WorkbenchScreen wbw = classElement.getAnnotation( WorkbenchScreen.class );
        final String tokenName = wbw.nameToken();
        final String onStartMethodName = GeneratorUtils.getOnStartPathParameterMethodName( classElement,
                                                                                           processingEnvironment );
        final String onMayCloseMethodName = GeneratorUtils.getOnMayCloseMethodName( classElement,
                                                                                    processingEnvironment );
        final String onCloseMethodName = GeneratorUtils.getOnCloseMethodName( classElement,
                                                                              processingEnvironment );
        final String onRevealMethodName = GeneratorUtils.getOnRevealMethodName( classElement,
                                                                                processingEnvironment );
        final String onLostFocusMethodName = GeneratorUtils.getOnLostFocusMethodName( classElement,
                                                                                      processingEnvironment );
        final String onFocusMethodName = GeneratorUtils.getOnFocusMethodName( classElement,
                                                                              processingEnvironment );
        final String getDefaultPositionMethodName = GeneratorUtils.getDefaultPositionMethodName( classElement,
                                                                                                 processingEnvironment );
        final String getTitleMethodName = GeneratorUtils.getTitleMethodName( classElement,
                                                                             processingEnvironment );
        final String getWidgetMethodName = GeneratorUtils.getWidgetMethodName( classElement,
                                                                               processingEnvironment );
        final boolean isWidget = GeneratorUtils.getIsWidget( classElement,
                                                             processingEnvironment );
        final String isDirtyMethodName = GeneratorUtils.getIsDirtyMethodName( classElement,
                                                                              processingEnvironment );
        final String onSaveMethodName = GeneratorUtils.getOnSaveMethodName( classElement,
                                                                            processingEnvironment );

        logger.debug( "Package name: " + packageName );
        logger.debug( "Class name: " + className );
        logger.debug( "Token name: " + tokenName );
        logger.debug( "onStartMethodName: " + onStartMethodName );
        logger.debug( "onMayCloseMethodName: " + onMayCloseMethodName );
        logger.debug( "onCloseMethodName: " + onCloseMethodName );
        logger.debug( "onRevealMethodName: " + onRevealMethodName );
        logger.debug( "onLostFocusMethodName: " + onLostFocusMethodName );
        logger.debug( "onFocusMethodName: " + onFocusMethodName );
        logger.debug( "getDefaultPositionMethodName: " + getDefaultPositionMethodName );
        logger.debug( "getTitleMethodName: " + getTitleMethodName );
        logger.debug( "getWidgetMethodName: " + getWidgetMethodName );
        logger.debug( "isWidget: " + Boolean.toString( isWidget ) );
        logger.debug( "isDirtyMethodName: " + isDirtyMethodName );
        logger.debug( "onSaveMethodName: " + onSaveMethodName );

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
        root.put( "onMayCloseMethodName",
                  onMayCloseMethodName );
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
        root.put( "isDirtyMethodName",
                  isDirtyMethodName );
        root.put( "onSaveMethodName",
                  onSaveMethodName );

        //Generate code
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter( sw );
        try {
            final Template template = config.getTemplate( "activityEditor.ftl" );
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

}
