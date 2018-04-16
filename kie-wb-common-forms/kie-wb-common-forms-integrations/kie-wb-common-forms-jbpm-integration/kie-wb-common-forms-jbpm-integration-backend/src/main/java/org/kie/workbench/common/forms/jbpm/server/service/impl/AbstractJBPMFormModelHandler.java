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

package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.util.List;

import org.kie.workbench.common.forms.editor.backend.service.impl.AbstractFormModelHandler;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.service.shared.BPMFinderService;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModuleService;

public abstract class AbstractJBPMFormModelHandler<MODEL extends JBPMFormModel> extends AbstractFormModelHandler<MODEL> {

    protected static final String BUNDLE = "org.kie.workbench.common.forms.jbpm.server.service.BackendConstants";
    protected static final String MISSING_PROCESS_SHORT_KEY = "MissingProcess.shortMessage";
    protected static final String MISSING_PROCESS_FULL_KEY = "MissingProcess.fullMessage";
    protected static final String PROCESS_KEY = "process";

    protected FieldManager fieldManager;

    protected BPMFinderService bpmFinderService;

    public AbstractJBPMFormModelHandler(KieModuleService projectService,
                                        ModuleClassLoaderHelper classLoaderHelper,
                                        FieldManager fieldManager,
                                        BPMFinderService bpmFinderService) {
        super(projectService,
              classLoaderHelper);
        this.fieldManager = fieldManager;
        this.bpmFinderService = bpmFinderService;
    }

    @Override
    protected void initialize() {
        super.checkInitialized();
    }

    protected abstract MODEL getSourceModel();

    @Override
    protected List<ModelProperty> getCurrentModelProperties() {
        MODEL sourceModel = getSourceModel();

        if (sourceModel != null) {
            return sourceModel.getProperties();
        }

        return null;
    }
}
