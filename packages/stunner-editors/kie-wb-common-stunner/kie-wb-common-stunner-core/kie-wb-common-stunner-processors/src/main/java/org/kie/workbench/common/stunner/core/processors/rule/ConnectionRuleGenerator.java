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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.kie.workbench.common.stunner.core.rule.annotation.CanConnect;
import org.uberfire.annotations.processors.AbstractGenerator;
import org.uberfire.annotations.processors.GenerationException;

public class ConnectionRuleGenerator extends AbstractGenerator {

    public class ConnectionRuleEntry {

        private final String from;
        private final String to;

        public ConnectionRuleEntry(final String from,
                                   final String to) {
            this.from = from;
            this.to = to;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }

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
        //Extract required information
        final TypeElement classElement = (TypeElement) element;
        final String ruleId = MainProcessor.toValidId(className);
        final String ruleDefinitionId = classElement.getQualifiedName().toString();
        CanConnect[] pcs = classElement.getAnnotationsByType(CanConnect.class);
        List<ConnectionRuleEntry> ruleEntries = new ArrayList<>();
        if (null != pcs) {
            for (final CanConnect pc : pcs) {
                String startRole = pc.startRole();
                String endRole = pc.endRole();
                ruleEntries.add(new ConnectionRuleEntry(startRole,
                                                        endRole));
            }
        }
        Map<String, Object> root = new HashMap<>();
        root.put("ruleId",
                 ruleId);
        root.put("ruleDefinitionId",
                 ruleDefinitionId);
        root.put("connectionsSize",
                 ruleEntries.size());
        root.put("connections",
                 ruleEntries);
        //Generate code
        try (final StringWriter sw = new StringWriter();
             final BufferedWriter bw = new BufferedWriter(sw)) {
            final Template template = config.getTemplate("ConnectionRule.ftl");
            template.process(root,
                             bw);
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Successfully generated code for [" + className + "]");
            processingContext.addRule(ruleId,
                                      ProcessingRule.TYPE.CONNECTION,
                                      sw.getBuffer());
            return null;
        } catch (IOException | TemplateException ioe) {
            throw new GenerationException(ioe);
        }
    }
}
