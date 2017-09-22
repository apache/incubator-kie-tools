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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.editor.service.backend.FormModelHandler;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMProcessModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.service.shared.BPMFinderService;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.services.backend.project.ProjectClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class BusinessProcessFormModelHandler extends AbstractJBPMFormModelHandler<BusinessProcessFormModel> {

    private static final Logger logger = LoggerFactory.getLogger(BusinessProcessFormModelHandler.class);

    @Inject
    public BusinessProcessFormModelHandler(KieProjectService projectService,
                                           ProjectClassLoaderHelper classLoaderHelper,
                                           FieldManager fieldManager,
                                           BPMFinderService bpmFinderService) {
        super(projectService,
              classLoaderHelper,
              fieldManager,
              bpmFinderService);
    }

    @Override
    public Class<BusinessProcessFormModel> getModelType() {
        return BusinessProcessFormModel.class;
    }

    @Override
    public FormModelHandler<BusinessProcessFormModel> newInstance() {
        return new BusinessProcessFormModelHandler(projectService,
                                                   classLoaderHelper,
                                                   fieldManager,
                                                   bpmFinderService);
    }

    @Override
    protected List<ModelProperty> getCurrentModelProperties() {
        JBPMProcessModel processModel = bpmFinderService.getModelForProcess(formModel.getProcessId(),
                                                                            path);
        return processModel.getProcessFormModel().getProperties();
    }

    @Override
    protected void log(String message,
                       Exception e) {
        logger.warn(message,
                    e);
    }
}
