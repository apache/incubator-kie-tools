/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.processors.propertyset;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Messager;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindablePropertySetAdapterProxy;
import org.kie.workbench.common.stunner.core.processors.AbstractBindableAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.ProcessingPropertySetAnnotations;
import org.uberfire.annotations.processors.exceptions.GenerationException;

public class BindablePropertySetAdapterGenerator extends AbstractBindableAdapterGenerator {

    @Override
    protected String getTemplatePath() {
        return "BindablePropertySetAdapter.ftl";
    }

    public StringBuffer generate(final String packageName,
                                 final String className,
                                 final ProcessingPropertySetAnnotations processingPropertySetAnnotations,
                                 final Messager messager) throws GenerationException {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("packageName",
                 packageName);
        root.put("className",
                 className);
        root.put("parentAdapterClassName",
                 BindablePropertySetAdapterProxy.class.getName());
        root.put("adapterFactoryClassName",
                 BindableAdapterFactory.class.getName());
        root.put("generatedByClassName",
                 BindablePropertySetAdapterGenerator.class.getName());
        addFields("nameFieldNames",
                  root,
                  processingPropertySetAnnotations.getNameFieldNames());
        addSetFields("propertiesFieldNames",
                     root,
                     processingPropertySetAnnotations.getPropertiesFieldNames());

        //Generate code
        return writeTemplate(packageName,
                             className,
                             root,
                             messager);
    }
}
