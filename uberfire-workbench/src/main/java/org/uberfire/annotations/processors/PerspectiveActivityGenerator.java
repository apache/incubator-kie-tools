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
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.client.annotations.Perspective;

import freemarker.template.Template;
import freemarker.template.TemplateException;

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
        final TypeElement classElement = (TypeElement) element.getEnclosingElement();
        final String methodName = element.getSimpleName().toString();
        final String realClassName = classElement.getSimpleName().toString();
        final Perspective perspective = element.getAnnotation( Perspective.class );
        final String identifier = perspective.identifier();
        final boolean isDefault = perspective.isDefault();
        final String securityTraitList = GeneratorUtils.getSecurityTraitList( element );
        final String rolesList = GeneratorUtils.getRoleList( element );

        logger.debug( "Package name: " + packageName );
        logger.debug( "Class name: " + className );
        logger.debug( "realClassName: " + realClassName );
        logger.debug( "Identifier: " + identifier );
        logger.debug( "isDefault: " + isDefault );
        logger.debug( "methodName: " + methodName );
        logger.debug( "securityTraitList: " + securityTraitList );
        logger.debug( "rolesList: " + rolesList );

        //Setup data for template sub-system
        Map<String, Object> root = new HashMap<String, Object>();
        root.put( "packageName",
                  packageName );
        root.put( "className",
                  className );
        root.put( "realClassName",
                  realClassName );
        root.put( "identifier",
                  identifier );
        root.put( "isDefault",
                  isDefault );
        root.put( "methodName",
                  methodName );
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
