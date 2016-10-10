/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.processors;

import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.relocated.freemarker.template.Configuration;
import org.uberfire.relocated.freemarker.template.DefaultObjectWrapper;
import org.uberfire.relocated.freemarker.template.Template;
import org.uberfire.relocated.freemarker.template.TemplateException;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractAdapterGenerator {

    protected static ExceptionInInitializerError INITIALIZER_EXCEPTION = null;
    protected Configuration config;

    public AbstractAdapterGenerator() {
        try {
            this.config = new Configuration();
            this.config.setClassForTemplateLoading( this.getClass(), "templates" );
            this.config.setObjectWrapper( new DefaultObjectWrapper() );
        } catch ( NoClassDefFoundError var2 ) {
            if ( var2.getCause() == null ) {
                var2.initCause( INITIALIZER_EXCEPTION );
            }
            throw var2;
        } catch ( ExceptionInInitializerError var3 ) {
            INITIALIZER_EXCEPTION = var3;
            throw var3;
        }
    }

    protected abstract String getTemplatePath();

    protected StringBuffer writeTemplate( String packageName, String className, Map<String, Object> ctxt, Messager messager ) throws GenerationException {
        //Generate code
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter( sw );
        try {
            final Template template = config.getTemplate( getTemplatePath() );
            template.process( ctxt,
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
        messager.printMessage( Diagnostic.Kind.NOTE, "Successfully generated code for [" + packageName + "." + className + "]" );
        return sw.getBuffer();
    }

    protected List<ProcessingElement> toList( Map<String, String> map ) {
        List<ProcessingElement> result = new LinkedList<>();
        for ( Map.Entry<String, String> entry : map.entrySet() ) {
            result.add( new ProcessingElement( entry.getKey(), entry.getValue() ) );
        }
        return result;
    }

    protected List<ProcessingMultipleElement> toMultipleList( Map<String, Set<String>> map ) {
        List<ProcessingMultipleElement> result = new LinkedList<>();
        for ( Map.Entry<String, Set<String>> entry : map.entrySet() ) {
            result.add( new ProcessingMultipleElement( entry.getKey(), entry.getValue() ) );
        }
        return result;
    }

}
