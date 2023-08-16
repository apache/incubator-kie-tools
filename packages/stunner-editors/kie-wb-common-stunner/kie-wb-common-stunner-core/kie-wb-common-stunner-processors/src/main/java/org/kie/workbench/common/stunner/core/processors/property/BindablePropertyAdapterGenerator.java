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


package org.kie.workbench.common.stunner.core.processors.property;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Messager;

import org.kie.workbench.common.stunner.core.processors.AbstractBindableAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.ProcessingPropertyAnnotations;
import org.uberfire.annotations.processors.GenerationException;

public class BindablePropertyAdapterGenerator extends AbstractBindableAdapterGenerator {

    @Override
    protected String getTemplatePath() {
        return "BindablePropertyAdapter.ftl";
    }

    public StringBuffer generate(final String packageName,
                                 final String className,
                                 final ProcessingPropertyAnnotations processingPropertyAnnotations,
                                 final Messager messager) throws GenerationException {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("packageName",
                 packageName);
        root.put("className",
                 className);
        root.put("generatedByClassName",
                 BindablePropertyAdapterGenerator.class.getName());
        addFields("valuePropNames",
                  root,
                  processingPropertyAnnotations.getValueFieldNames());
        //Generate code
        return writeTemplate(packageName,
                             className,
                             root,
                             messager);
    }
}
