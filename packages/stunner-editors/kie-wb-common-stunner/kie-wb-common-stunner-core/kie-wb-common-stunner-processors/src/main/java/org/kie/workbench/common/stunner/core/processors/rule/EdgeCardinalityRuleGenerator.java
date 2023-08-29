/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.processors.rule;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.kie.workbench.common.stunner.core.processors.MainProcessor;
import org.kie.workbench.common.stunner.core.processors.ProcessingContext;
import org.kie.workbench.common.stunner.core.processors.ProcessingRule;
import org.kie.workbench.common.stunner.core.rule.annotation.AllowedEdgeOccurrences;
import org.kie.workbench.common.stunner.core.rule.annotation.EdgeOccurrences;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.uberfire.annotations.processors.AbstractGenerator;
import org.uberfire.annotations.processors.GenerationException;

public class EdgeCardinalityRuleGenerator extends AbstractGenerator {

    private final ProcessingContext processingContext = ProcessingContext.getInstance();

    @Override
    public StringBuffer generate(final String packageName,
                                 final PackageElement packageElement,
                                 final String className,
                                 final Element element,
                                 final ProcessingEnvironment processingEnvironment) throws GenerationException {
        final Messager messager = processingEnvironment.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE,
                              "Starting code adf for [" + className + "]");
        final String ruleDefinitionId = ((TypeElement) element).getQualifiedName().toString();
        //Extract required information
        final TypeElement classElement = (TypeElement) element;
        AllowedEdgeOccurrences occs = classElement.getAnnotation(AllowedEdgeOccurrences.class);
        if (null != occs) {
            int count = 0;
            for (EdgeOccurrences occurrence : occs.value()) {
                String ruleById = classElement.getQualifiedName().toString();
                String shortId = ruleById.substring(ruleById.lastIndexOf(".") + 1);
                String name = shortId + count + MainProcessor.RULE_EDGE_CARDINALITY_SUFFIX_CLASSNAME;
                EdgeOccurrences.EdgeType _type = occurrence.type();
                EdgeCardinalityContext.Direction type = EdgeOccurrences.EdgeType.INCOMING.equals(_type) ? EdgeCardinalityContext.Direction.INCOMING : EdgeCardinalityContext.Direction.OUTGOING;
                final int min = occurrence.min();
                final int max = occurrence.max();
                final String roles = occurrence.role();
                StringBuffer ruleSourceCode = generateRule(messager,
                                                           name,
                                                           ruleDefinitionId,
                                                           roles,
                                                           "EdgeCardinalityContext.Direction." + type.name(),
                                                           min,
                                                           max);
                processingContext.addRule(name,
                                          ProcessingRule.TYPE.EDGE_CARDINALITY,
                                          ruleSourceCode);
                count++;
            }
        }
        return null;
    }

    private StringBuffer generateRule(final Messager messager,
                                      final String ruleName,
                                      final String ruleDefinitionId,
                                      final String role,
                                      final String direction,
                                      final long min,
                                      final long max) throws GenerationException {
        Map<String, Object> root = new HashMap<>();
        root.put("ruleName",
                 ruleName);
        root.put("edgeId",
                 ruleDefinitionId);
        root.put("role",
                 role);
        root.put("direction",
                 direction);
        root.put("min",
                 min);
        root.put("max",
                 max);

        //Generate code
        try (final StringWriter sw = new StringWriter();
             final BufferedWriter bw = new BufferedWriter(sw)) {
            final Template template = config.getTemplate("EdgeCardinalityRule.ftl");
            template.process(root,
                             bw);
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Successfully generated code for [" + ruleName + "]");
            return sw.getBuffer();
        } catch (IOException | TemplateException ioe) {
            throw new GenerationException(ioe);
        }
    }
}
