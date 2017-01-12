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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.kie.workbench.common.stunner.core.processors.MainProcessor;
import org.kie.workbench.common.stunner.core.processors.ProcessingContext;
import org.kie.workbench.common.stunner.core.processors.ProcessingRule;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.annotation.AllowedEdgeOccurrences;
import org.kie.workbench.common.stunner.core.rule.annotation.EdgeOccurrences;
import org.kie.workbench.common.stunner.core.rule.impl.rules.EdgeCardinalityRuleImpl;
import org.uberfire.annotations.processors.AbstractGenerator;
import org.uberfire.annotations.processors.exceptions.GenerationException;

public class EdgeCardinalityRuleGenerator extends AbstractGenerator {

    private final ProcessingContext processingContext = ProcessingContext.getInstance();

    @Override
    public StringBuffer generate( final String packageName,
                                  final PackageElement packageElement,
                                  final String className,
                                  final Element element,
                                  final ProcessingEnvironment processingEnvironment ) throws GenerationException {
        final Messager messager = processingEnvironment.getMessager();
        messager.printMessage( Diagnostic.Kind.NOTE,
                               "Starting code generation for [" + className + "]" );
        final Elements elementUtils = processingEnvironment.getElementUtils();
        //Extract required information
        final TypeElement classElement = ( TypeElement ) element;
        final boolean isInterface = classElement.getKind().isInterface();
        AllowedEdgeOccurrences occs = classElement.getAnnotation( AllowedEdgeOccurrences.class );
        if ( null != occs ) {
            List<EdgeCardinalityRule> edgeRules = new ArrayList<>();
            for ( EdgeOccurrences occurrence : occs.value() ) {
                String role = occurrence.role();
                String ruleById = classElement.getQualifiedName().toString();
                String shortId = ruleById.substring( ruleById.lastIndexOf( "." ) + 1,
                                                     ruleById.length() );
                String name = shortId + "_" + role + "_" + MainProcessor.RULE_EDGE_CARDINALITY_SUFFIX_CLASSNAME;
                EdgeOccurrences.EdgeType _type = occurrence.type();
                EdgeCardinalityRule.Type type = EdgeOccurrences.EdgeType.INCOMING.equals( _type ) ? EdgeCardinalityRule.Type.INCOMING : EdgeCardinalityRule.Type.OUTGOING;
                int min = occurrence.min();
                int max = occurrence.max();
                if ( EdgeCardinalityRule.Type.INCOMING.equals( type ) ) {
                    edgeRules.add( new EdgeCardinalityRuleImpl( ruleById,
                                                                "incoming" + name,
                                                                role,
                                                                type,
                                                                min,
                                                                max ) );
                } else {
                    edgeRules.add( new EdgeCardinalityRuleImpl( ruleById,
                                                                "outgoing" + name,
                                                                role,
                                                                type,
                                                                min,
                                                                max ) );
                }
                StringBuffer ruleSourceCode = generateRule( messager,
                                                            ruleById,
                                                            name,
                                                            role,
                                                            "EdgeCardinalityRule.Type." + type.name(),
                                                            min,
                                                            max );
                processingContext.addRule( name,
                                           ProcessingRule.TYPE.EDGE_CARDINALITY,
                                           ruleSourceCode );
            }
        }
        return null;
    }

    private StringBuffer generateRule( final Messager messager,
                                       final String ruleId,
                                       final String ruleName,
                                       final String ruleRoleId,
                                       final String type,
                                       final long min,
                                       final long max ) throws GenerationException {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put( "ruleName",
                  ruleName );
        root.put( "ruleId",
                  ruleId );
        root.put( "ruleRoleId",
                  ruleRoleId );
        root.put( "ruleRoleType",
                  type );
        root.put( "min",
                  min );
        root.put( "max",
                  max );
        //Generate code
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter( sw );
        try {
            final Template template = config.getTemplate( "EdgeCardinalityRule.ftl" );
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
        messager.printMessage( Diagnostic.Kind.NOTE,
                               "Successfully generated code for [" + ruleId + "]" );
        return sw.getBuffer();
    }
}
