/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.processors.rule;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.kie.workbench.common.stunner.core.processors.MainProcessor;
import org.kie.workbench.common.stunner.core.processors.ProcessingContext;
import org.kie.workbench.common.stunner.core.processors.ProcessingRule;
import org.uberfire.annotations.processors.AbstractGenerator;
import org.uberfire.annotations.processors.exceptions.GenerationException;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DockingRuleGenerator extends AbstractGenerator {

    private final ProcessingContext processingContext = ProcessingContext.getInstance();

    @Override
    public StringBuffer generate( String packageName, PackageElement packageElement, String className, Element element, ProcessingEnvironment processingEnvironment ) throws GenerationException {
        final Messager messager = processingEnvironment.getMessager();
        messager.printMessage( Diagnostic.Kind.NOTE, "Starting code generation for [" + className + "]" );
        //Extract required information
        final TypeElement classElement = ( TypeElement ) element;
        final String annotationName = MainProcessor.ANNOTATION_RULE_CAN_DOCK;
        final String ruleId = MainProcessor.toValidId( className );
        final String ruleDefinitionId = ( ( TypeElement ) element ).getQualifiedName().toString();
        List<String> roles = null;
        for ( final AnnotationMirror am : classElement.getAnnotationMirrors() ) {
            if ( annotationName.equals( am.getAnnotationType().toString() ) ) {
                for ( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet() ) {
                    AnnotationValue aval = entry.getValue();
                    if ( "roles".equals( entry.getKey().getSimpleName().toString() ) ) {
                        roles = ( List<String> ) aval.getValue();
                    }
                }
                break;
            }
        }
        Map<String, Object> root = new HashMap<String, Object>();
        root.put( "ruleId",
                ruleId );
        root.put( "ruleDefinitionId",
                ruleDefinitionId );
        root.put( "roles",
                roles );
        //Generate code
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter( sw );
        try {
            final Template template = config.getTemplate( "DockingRule.ftl" );
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
        messager.printMessage( Diagnostic.Kind.NOTE, "Successfully generated code for [" + className + "]" );
        processingContext.addRule( ruleId, ProcessingRule.TYPE.DOCKING, sw.getBuffer() );
        return null;

    }

}
