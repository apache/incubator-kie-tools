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

package org.kie.workbench.common.stunner.core.processors.definition;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionAdapterProxy;
import org.kie.workbench.common.stunner.core.processors.AbstractBindableAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.ProcessingDefinitionAnnotations;
import org.uberfire.annotations.processors.exceptions.GenerationException;

import javax.annotation.processing.Messager;
import java.util.HashMap;
import java.util.Map;

public class BindableDefinitionAdapterGenerator extends AbstractBindableAdapterGenerator {

    @Override
    protected String getTemplatePath() {
        return "BindableDefinitionAdapter.ftl";
    }

    public StringBuffer generate( String packageName, String className,
                                  ProcessingDefinitionAnnotations processingDefinitionAnnotations,
                                  String namePropertyClass,
                                  Messager messager ) throws GenerationException {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put( "packageName",
                packageName );
        root.put( "className",
                className );
        root.put( "parentAdapterClassName",
                BindableDefinitionAdapterProxy.class.getName() );
        root.put( "generatedByClassName",
                BindableDefinitionAdapterGenerator.class.getName() );
        root.put( "adapterFactoryClassName", BindableAdapterFactory.class.getName() );
        root.put( "namePropertyClass",
                namePropertyClass );
        addFields( "baseTypes", root, processingDefinitionAnnotations.getBaseTypes() );
        addFields( "categoryFieldNames", root, processingDefinitionAnnotations.getCategoryFieldNames() );
        addFields( "titleFieldNames", root, processingDefinitionAnnotations.getTitleFieldNames() );
        addFields( "descriptionFieldNames", root, processingDefinitionAnnotations.getDescriptionFieldNames() );
        addFields( "labelsFieldNames", root, processingDefinitionAnnotations.getLabelsFieldNames() );
        addFields( "graphFactoryFieldNames", root, processingDefinitionAnnotations.getGraphFactoryFieldNames() );
        addMultipleFields( "propertySetsFieldNames", root, processingDefinitionAnnotations.getPropertySetFieldNames() );
        addMultipleFields( "propertiesFieldNames", root, processingDefinitionAnnotations.getPropertyFieldNames() );
        //Generate code
        return writeTemplate( packageName, className, root, messager );
    }

}
