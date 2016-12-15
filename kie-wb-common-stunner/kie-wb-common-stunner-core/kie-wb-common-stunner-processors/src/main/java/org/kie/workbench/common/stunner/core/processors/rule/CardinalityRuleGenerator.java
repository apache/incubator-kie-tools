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

import org.kie.workbench.common.stunner.core.processors.MainProcessor;
import org.kie.workbench.common.stunner.core.processors.ProcessingContext;
import org.kie.workbench.common.stunner.core.processors.ProcessingRule;
import org.kie.workbench.common.stunner.core.rule.annotation.AllowedOccurrences;
import org.kie.workbench.common.stunner.core.rule.annotation.Occurrences;
import org.uberfire.annotations.processors.AbstractGenerator;
import org.uberfire.annotations.processors.exceptions.GenerationException;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class CardinalityRuleGenerator extends AbstractGenerator {

    private final ProcessingContext processingContext = ProcessingContext.getInstance();

    @Override
    public StringBuffer generate( String packageName, PackageElement packageElement, String className, Element element, ProcessingEnvironment processingEnvironment ) throws GenerationException {
        final Messager messager = processingEnvironment.getMessager();
        messager.printMessage( Diagnostic.Kind.NOTE, "Starting code generation for [" + className + "]" );
        final Elements elementUtils = processingEnvironment.getElementUtils();
        //Extract required information
        final TypeElement classElement = ( TypeElement ) element;
        final boolean isInterface = classElement.getKind().isInterface();
        AllowedOccurrences occs = classElement.getAnnotation( AllowedOccurrences.class );
        if ( null != occs ) {
            for ( Occurrences occurrence : occs.value() ) {
                String role = occurrence.role();
                final String ruleNAme = MainProcessor.toValidId( className ) + "_" + role + "_" + MainProcessor.RULE_CARDINALITY_SUFFIX_CLASSNAME;
                long min = occurrence.min();
                long max = occurrence.max();
                StringBuffer ruleSourceCode = generateRule( messager, ruleNAme, role, min, max );
                processingContext.addRule( ruleNAme, ProcessingRule.TYPE.CARDINALITY, ruleSourceCode );

            }

        }
        return null;

    }

    private StringBuffer generateRule( Messager messager,
                                       String ruleName,
                                       String ruleRoleId,
                                       long min,
                                       long max ) throws GenerationException {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put( "ruleName",
                ruleName );
        root.put( "ruleRoleId",
                ruleRoleId );
        root.put( "min",
                min );
        root.put( "max",
                max );
        //Generate code
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter( sw );
        try {
            final Template template = config.getTemplate( "CardinalityRule.ftl" );
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
        messager.printMessage( Diagnostic.Kind.NOTE, "Successfully generated code for [" + ruleName + "]" );
        return sw.getBuffer();
    }

}
