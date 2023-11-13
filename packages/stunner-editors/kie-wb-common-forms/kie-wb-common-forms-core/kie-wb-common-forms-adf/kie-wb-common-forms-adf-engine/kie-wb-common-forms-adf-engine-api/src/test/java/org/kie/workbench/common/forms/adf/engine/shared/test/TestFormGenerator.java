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


package org.kie.workbench.common.forms.adf.engine.shared.test;

import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.AbstractFormGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.I18nHelper;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout.LayoutGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.FormElementProcessor;
import org.kie.workbench.common.forms.adf.service.building.FormGenerationResourcesProvider;
import org.kie.workbench.common.forms.adf.service.definitions.I18nSettings;

public class TestFormGenerator extends AbstractFormGenerator {

    private I18nHelper i18nHelper;

    public TestFormGenerator(LayoutGenerator layoutGenerator,
                             I18nHelper helper) {
        super(layoutGenerator);
        this.i18nHelper = helper;
    }

    @Override
    public void registerProcessor(FormElementProcessor processor) {
        super.registerProcessor(processor);
    }

    @Override
    public void registerResources(FormGenerationResourcesProvider provider) {
        super.registerResources(provider);
    }

    @Override
    protected I18nHelper getI18nHelper(I18nSettings settings) {
        return i18nHelper;
    }
}
