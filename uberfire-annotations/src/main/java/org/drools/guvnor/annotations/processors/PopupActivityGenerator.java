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
        final String onRevealMethodName = GeneratorUtils.getOnRevealMethodName( classElement,
                                                                                processingEnvironment );
        final String getPopupMethodName = GeneratorUtils.getPopupMethodName( classElement,
                                                                             processingEnvironment );
        final boolean isPopup = GeneratorUtils.getIsPopup( classElement,
                                                           processingEnvironment );

        logger.debug( "Package name: " + packageName );
        logger.debug( "Class name: " + className );
        logger.debug( "Identifier: " + identifier );
        logger.debug( "onRevealMethodName: " + onRevealMethodName );
        logger.debug( "getPopupMethodName: " + getPopupMethodName );
        logger.debug( "isPopup: " + Boolean.toString( isPopup ) );

        //Validate getPopupMethodName and isPopup
        if ( !isPopup && getPopupMethodName == null ) {
            throw new GenerationException( "The WorkbenchPart must either extend PopupPanel or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.PopupPanel." );
        }
        if ( isPopup && getPopupMethodName != null ) {
            logger.warn( "The WorkbenchPart both extends com.google.gwt.user.client.ui.PopupPanel and provides a @WorkbenchPartView annotated method. The annotated method will take precedence." );
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
        root.put( "onRevealMethodName",
                  onRevealMethodName );
        root.put( "getPopupMethodName",
                  getPopupMethodName );
        root.put( "isPopup",
                  isPopup );

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
