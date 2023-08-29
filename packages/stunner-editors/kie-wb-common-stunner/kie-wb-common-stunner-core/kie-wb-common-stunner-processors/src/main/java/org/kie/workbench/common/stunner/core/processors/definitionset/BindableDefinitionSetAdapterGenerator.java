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


package org.kie.workbench.common.stunner.core.processors.definitionset;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.Messager;

import org.kie.workbench.common.stunner.core.processors.AbstractBindableAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.ProcessingDefinitionSetAnnotations;
import org.uberfire.annotations.processors.GenerationException;

public class BindableDefinitionSetAdapterGenerator extends AbstractBindableAdapterGenerator {

    @Override
    protected String getTemplatePath() {
        return "BindableDefinitionSetAdapter.ftl";
    }

    public StringBuffer generate(final String packageName,
                                 final String className,
                                 final ProcessingDefinitionSetAnnotations processingDefinitionSetAnnotations,
                                 final Messager messager) throws GenerationException {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("packageName",
                 packageName);
        root.put("className",
                 className);
        root.put("generatedByClassName",
                 BindableDefinitionSetAdapterGenerator.class.getName());

        Map<String, Set<String>> definitionIds = processingDefinitionSetAnnotations.getDefinitionIds();
        String defSetClassName = definitionIds.keySet().iterator().next();
        String graphFactory = processingDefinitionSetAnnotations.getGraphFactoryTypes().get(defSetClassName);
        graphFactory = null != graphFactory && graphFactory.trim().length() > 0 ? graphFactory + ".class" : "null";
        String qualifier = processingDefinitionSetAnnotations.getQualifiers().get(defSetClassName);
        Set<String> defIds = processingDefinitionSetAnnotations.getDefinitionIds().get(defSetClassName);
        String defIdsArray = defIds.stream().map(f -> "\"" + f + "\"").collect(Collectors.joining(","));

        String bindingsRaw = "new DefinitionSetAdapterBindings() " +
                ".setGraphFactory(" + graphFactory + ")" +
                ".setDefinitionIds(new HashSet<>(Arrays.asList(" + defIdsArray + ")))";
        if (null != qualifier) {
            bindingsRaw += ".setQualifier(new " + qualifier + "() {\n" +
                    "                    @Override\n" +
                    "                    public Class<? extends Annotation> annotationType() {\n" +
                    "                        return " + qualifier + ".class;\n" +
                    "                }})";
        }
        root.put("bindingsKey", defSetClassName + ".class");
        root.put("bindingsValue", bindingsRaw);

        // Generate code from the template.
        return writeTemplate(packageName,
                             className,
                             root,
                             messager);
    }
}
