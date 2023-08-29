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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;

import org.kie.workbench.common.stunner.core.definition.adapter.AbstractDefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.processors.AbstractBindableAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.ProcessingRule;
import org.uberfire.annotations.processors.GenerationException;

public class BindableDefinitionSetRuleAdapterGenerator extends AbstractBindableAdapterGenerator {

    @Override
    protected String getTemplatePath() {
        return "BindableDefinitionSetRuleAdapter.ftl";
    }

    public StringBuffer generate(final String packageName,
                                 final String className,
                                 final String defSetClassName,
                                 final List<ProcessingRule> rules,
                                 final Messager messager) throws GenerationException {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("packageName",
                 packageName);
        root.put("className",
                 className);
        root.put("parentAdapterClassName",
                 AbstractDefinitionSetRuleAdapter.class.getName());
        root.put("generatedByClassName",
                 BindableDefinitionSetRuleAdapterGenerator.class.getName());
        root.put("defSetClassName",
                 defSetClassName);
        root.put("ruleSetName",
                 className);
        root.put("rules",
                 rules);
        root.put("rulesSize",
                 rules.size());
        root.put("ruleNames",
                 getRuleNames(rules));

        // Generate code
        return writeTemplate(packageName,
                             className,
                             root,
                             messager);
    }

    private List<String> getRuleNames(final List<ProcessingRule> rules) {
        List<String> result = new ArrayList<>();
        for (ProcessingRule entity : rules) {
            result.add(entity.getName());
        }
        return result;
    }
}
