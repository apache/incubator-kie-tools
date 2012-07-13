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
import org.drools.guvnor.client.annotations.WorkbenchPopup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * A source code generator for Activities
 */
public class PopupActivityGenerator extends AbstractGenerator {

    private static final Logger logger = LoggerFactory.getLogger( PopupActivityGenerator.class );

    public StringBuffer generate(final String packageName,
                                 final PackageElement packageElement,
                                 final String className,
                                 final TypeElement classElement,
                                 final ProcessingEnvironment processingEnvironment) throws GenerationException {
        logger.debug( "Starting code generation for [" + className + "]" );

        //Extract required information
        final WorkbenchPopup wbp = classElement.getAnnotation( WorkbenchPopup.class );
        final String identifier = wbp.identifier();
        final String onMayCloseMethodName = GeneratorUtils.getOnMayCloseMethodName( classElement,
                                                                                    processingEnvironment );
        final String onCloseMethodName = GeneratorUtils.getOnCloseMethodName( classElement,
                                                                              processingEnvironment );
        final String onRevealMethodName = GeneratorUtils.getOnRevealMethodName( classElement,
                                                                                processingEnvironment );
        final String getTitleMethodName = GeneratorUtils.getTitleMethodName( classElement,
                                                                             processingEnvironment );
        final String getWidgetMethodName = GeneratorUtils.getWidgetMethodName( classElement,
                                                                               processingEnvironment );
        final boolean isWidget = GeneratorUtils.getIsWidget( classElement,
                                                             processingEnvironment );

        logger.debug( "Package name: " + packageName );
        logger.debug( "Class name: " + className );
        logger.debug( "Identifier: " + identifier );
        logger.debug( "onMayCloseMethodName: " + onMayCloseMethodName );
        logger.debug( "onCloseMethodName: " + onCloseMethodName );
        logger.debug( "onRevealMethodName: " + onRevealMethodName );
        logger.debug( "getTitleMethodName: " + getTitleMethodName );
        logger.debug( "getWidgetMethodName: " + getWidgetMethodName );
        logger.debug( "isWidget: " + Boolean.toString( isWidget ) );

        //Validate getPopupMethodName and isPopup
        if ( !isWidget && getWidgetMethodName == null ) {
            throw new GenerationException( "The WorkbenchPart must either extend PopupPanel or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.PopupPanel." );
        }
        if ( isWidget && getWidgetMethodName != null ) {
            logger.warn( "The WorkbenchPart both extends com.google.gwt.user.client.ui.PopupPanel and provides a @WorkbenchPartView annotated method. The annotated method will take precedence." );
        }

        //Validate getTitleMethodName
        if ( getTitleMethodName == null ) {
            throw new GenerationException( "The WorkbenchPart must provide a @WorkbenchPartTitle annotated method to return a java.lang.String." );
        }

        //Setup data for template sub-system
        Map<String, Object> root = new HashMap<String, Object>();
        root.put( "packageName",
                  packageName );
        root.put( "className",
                  className );
        root.put( "identifier",
                  identifier );
        root.put( "realClassName",
                  classElement.getSimpleName().toString() );
        root.put( "onMayCloseMethodName",
                  onMayCloseMethodName );
        root.put( "onCloseMethodName",
                  onCloseMethodName );
        root.put( "onRevealMethodName",
                  onRevealMethodName );
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
            final Template template = config.getTemplate( "popupScreen.ftl" );
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
