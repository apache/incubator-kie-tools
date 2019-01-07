/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.service.backend.SourceFormModelNotFoundException;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMProcessModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TaskFormModelHandlerTest extends AbstractJBPMFormModelHandlerTest {

    public static final String TASK_NAME = "taskName";

    private TaskFormModel model = null;

    private TaskFormModelHandler handler;

    @Before
    public void init() throws ClassNotFoundException {
        super.init();

        model = new TaskFormModel(PROCESS_ID, TASK_NAME, propertyList);

        handler = new TaskFormModelHandler(moduleService, moduleClassLoaderHelper, new TestFieldManager(), finderService);

        handler.init(model, path);
    }

    @Test
    public void testCheckModelSource() {

        when(finderService.getModelForProcess(any(), any())).thenReturn(getFullProcessModel());

        Assertions.assertThatCode(() -> handler.checkSourceModel())
                .doesNotThrowAnyException();

        when(finderService.getModelForProcess(any(), any())).thenReturn(null);

        Assertions.assertThatThrownBy(() -> handler.checkSourceModel())
                .isInstanceOf(SourceFormModelNotFoundException.class);

        when(finderService.getModelForProcess(any(), any())).thenReturn(getBaseProcessModel());

        Assertions.assertThatThrownBy(() -> handler.checkSourceModel())
                .isInstanceOf(SourceFormModelNotFoundException.class);
    }

    protected JBPMProcessModel getBaseProcessModel() {
        BusinessProcessFormModel processFormModel = new BusinessProcessFormModel(PROCESS_ID, PROCESS_ID, propertyList);

        return new JBPMProcessModel(processFormModel, new ArrayList<>());
    }

    protected JBPMProcessModel getFullProcessModel() {
        JBPMProcessModel processModel = getBaseProcessModel();

        processModel.getTaskFormModels().add(new TaskFormModel(PROCESS_ID, TASK_NAME, propertyList));

        return processModel;
    }
}
