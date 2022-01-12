/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.processors.morph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Messager;

import org.kie.workbench.common.stunner.core.definition.morph.BindableMorphDefinition;
import org.kie.workbench.common.stunner.core.processors.AbstractBindableAdapterGenerator;
import org.uberfire.annotations.processors.GenerationException;

public class MorphDefinitionGenerator extends AbstractBindableAdapterGenerator {

    @Override
    protected String getTemplatePath() {
        return "MorphDefinition.ftl";
    }

    public StringBuffer generate(final String packageName,
                                 final String className,
                                 final String baseMorphType,
                                 final Collection<String> targetTypes,
                                 final String defaultMorphType,
                                 final Messager messager) throws GenerationException {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("packageName",
                 packageName);
        root.put("className",
                 className);
        root.put("parentClassName",
                 BindableMorphDefinition.class.getName());
        root.put("generatedByClassName",
                 MorphDefinitionGenerator.class.getName());
        root.put("morphBaseClassName",
                 baseMorphType);
        root.put("targetClassNames",
                 targetTypes);
        root.put("targetClassNamesSize",
                 targetTypes.size());
        root.put("defaultTypeClassName",
                 defaultMorphType);

        //Generate code
        return writeTemplate(packageName,
                             className,
                             root,
                             messager);
    }
}
