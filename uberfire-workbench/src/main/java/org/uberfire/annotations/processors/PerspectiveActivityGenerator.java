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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.client.annotations.WorkbenchPerspective;

/**
 * A source code generator for Activities
 */
public class PerspectiveActivityGenerator extends AbstractGenerator {

    private static final Logger logger = LoggerFactory.getLogger( PerspectiveActivityGenerator.class );

    public StringBuffer generate(final String packageName,
                                 final PackageElement packageElement,
                                 final String className,
                                 final Element element,
                                 final ProcessingEnvironment processingEnvironment) throws GenerationException {

        logger.debug( "Starting code generation for [" + className + "]" );

        //Extract required information
        final TypeElement classElement = (TypeElement) element;
        final WorkbenchPerspective wbp = classElement.getAnnotation( WorkbenchPerspective.class );
        final String identifier = wbp.identifier();
        final boolean isDefault = wbp.isDefault();
        final String onStart0ParameterMethodName = GeneratorUtils.getOnStartZeroParameterMethodName( classElement,
                                                                                                     processingEnvironment );
        final String onStart1ParameterMethodName = GeneratorUtils.getOnStartPlaceRequestParameterMethodName( classElement,
                                                                                                             processingEnvironment );
        final String onCloseMethodName = GeneratorUtils.getOnCloseMethodName( classElement,
                                                                              processingEnvironment );
        final String onRevealMethodName = GeneratorUtils.getOnRevealMethodName( classElement,
                                                                                processingEnvironment );
        final String getPerspectiveMethodName = GeneratorUtils.getPerspectiveMethodName( classElement,
                                                                                         processingEnvironment );
        final String getMenuBarMethodName = GeneratorUtils.getMenuBarMethodName( classElement,
                                                                                 processingEnvironment );
        final String getToolBarMethodName = GeneratorUtils.getToolBarMethodName( classElement,
                                                                                 processingEnvironment );
        final String securityTraitList = GeneratorUtils.getSecurityTraitList( classElement );
        final String rolesList = GeneratorUtils.getRoleList( classElement );

        logger.debug( "Package name: " + packageName );
        logger.debug( "Class name: " + className );
        logger.debug( "Identifier: " + identifier );
        logger.debug( "isDefault: " + isDefault );
        logger.debug( "onStart0ParameterMethodName: " + onStart0ParameterMethodName );
        logger.debug( "onStart1ParameterMethodName: " + onStart1ParameterMethodName );
        logger.debug( "onCloseMethodName: " + onCloseMethodName );
        logger.debug( "onRevealMethodName: " + onRevealMethodName );
        logger.debug( "getPerspectiveMethodName: " + getPerspectiveMethodName );
        logger.debug( "getMenuBarMethodName: " + getMenuBarMethodName );
        logger.debug( "getToolBarMethodName: " + getToolBarMethodName );
        logger.debug( "securityTraitList: " + securityTraitList );
        logger.debug( "rolesList: " + rolesList );

        //Validate onStart0ParameterMethodName and onStart1ParameterMethodName
        if ( onStart0ParameterMethodName != null && onStart1ParameterMethodName != null ) {
            final String msg = "The WorkbenchPerspective has methods for both @OnStart() and @OnStart(Place). Method @OnStart(Place) will take precedence.";
            processingEnvironment.getMessager().printMessage( Kind.WARNING,
                                                              msg );
            logger.warn( msg );
        }

        //Validate getPerspectiveMethodName
        if ( getPerspectiveMethodName == null ) {
            throw new GenerationException( "The WorkbenchPerspective must provide a @Perspective annotated method to return a org.uberfire.client.workbench.model.PerspectiveDefinition.", packageName + "." + className );
        }

        //Setup data for template sub-system
        Map<String, Object> root = new HashMap<String, Object>();
        root.put( "packageName",
                  packageName );
        root.put( "className",
                  className );
        root.put( "identifier",
                  identifier );
        root.put( "isDefault",
                  isDefault );
        root.put( "realClassName",
                  classElement.getSimpleName().toString() );
        root.put( "onStart0ParameterMethodName",
                  onStart0ParameterMethodName );
        root.put( "onStart1ParameterMethodName",
                  onStart1ParameterMethodName );
        root.put( "onCloseMethodName",
                  onCloseMethodName );
        root.put( "onRevealMethodName",
                  onRevealMethodName );
        root.put( "getPerspectiveMethodName",
                  getPerspectiveMethodName );
        root.put( "getMenuBarMethodName",
                  getMenuBarMethodName );
        root.put( "getToolBarMethodName",
                  getToolBarMethodName );
        root.put( "securityTraitList",
                  securityTraitList );
        root.put( "rolesList",
                  rolesList );

        //Generate code
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter( sw );
        try {
            final Template template = config.getTemplate( "perspective.ftl" );
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
