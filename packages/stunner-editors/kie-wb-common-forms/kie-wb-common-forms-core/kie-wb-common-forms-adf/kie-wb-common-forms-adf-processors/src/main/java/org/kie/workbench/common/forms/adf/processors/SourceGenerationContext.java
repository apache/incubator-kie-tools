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


package org.kie.workbench.common.forms.adf.processors;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;

public class SourceGenerationContext {

    private final ProcessingEnvironment processingEnvironment;
    private final RoundEnvironment roundEnvironment;

    private List<String> imports = new ArrayList<>();

    private List<FieldDefinitionModifierData> fieldDefinitions = new ArrayList<>();

    private List<FormDefinitionData> forms = new ArrayList<>();

    public SourceGenerationContext(ProcessingEnvironment processingEnvironment, RoundEnvironment roundEnvironment) {
        this.processingEnvironment = processingEnvironment;
        this.roundEnvironment = roundEnvironment;
    }

    public List<String> getImports() {
        return imports;
    }

    public List<FormDefinitionData> getForms() {
        return forms;
    }

    public List<FieldDefinitionModifierData> getFieldDefinitions() {
        return fieldDefinitions;
    }

    public ProcessingEnvironment getProcessingEnvironment() {
        return processingEnvironment;
    }

    public RoundEnvironment getRoundEnvironment() {
        return roundEnvironment;
    }

    public Elements getElementUtils() {
        return processingEnvironment.getElementUtils();
    }

    public Messager getMessager() {
        return processingEnvironment.getMessager();
    }
}
