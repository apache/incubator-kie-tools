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

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import org.drools.guvnor.client.annotations.WorkbenchPart;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 */
public class PlaceGenerator extends AbstractGenerator {

    public void generate(final String packageName,
                         final PackageElement packageElement,
                         final String className,
                         final TypeElement classElement,
                         final ProcessingEnvironment processingEnvironment,
                         final Writer w) {

        final WorkbenchPart wbw = classElement.getAnnotation( WorkbenchPart.class );
        final String tokenName = wbw.nameToken();

        System.out.println( "-----> Generating source code for Place [" + className + "]" );

        Map<String, String> root = new HashMap<String, String>();
        root.put( "packageName",
                  packageName );
        root.put( "className",
                  className );
        root.put( "tokenName",
                  tokenName );

        try {
            final Template template = config.getTemplate( "place.ftl" );
            template.process( root,
                              w );
        } catch ( IOException ioe ) {
            System.out.println( ioe.getMessage() );
        } catch ( TemplateException te ) {
            System.out.println( te.getMessage() );
        }
    }

}
