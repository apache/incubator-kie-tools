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
import java.util.Locale;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.service.backend.SourceFormModelNotFoundException;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMProcessModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BusinessProcessFormModelHandlerTest extends AbstractJBPMFormModelHandlerTest {

    private BusinessProcessFormModel model = null;

    private BusinessProcessFormModelHandler handler;

    @Before
    public void init() throws ClassNotFoundException {
        super.init();

        model = new BusinessProcessFormModel(PROCESS_ID, PROCESS_ID, propertyList);

        handler = new BusinessProcessFormModelHandler(moduleService, moduleClassLoaderHelper, new TestFieldManager(), finderService) {
            @Override
            protected Locale getLocale() {
                return Locale.ENGLISH;
            }
        };

        handler.init(model, path);
    }

    @Test
    public void testCheckModelSource() {

        when(finderService.getModelForProcess(any(), any())).thenReturn(getProcessModel());

        Assertions.assertThatCode(() -> handler.checkSourceModel())
                .doesNotThrowAnyException();

        when(finderService.getModelForProcess(any(), any())).thenReturn(null);

        Assertions.assertThatThrownBy(() -> handler.checkSourceModel())
                .isInstanceOf(SourceFormModelNotFoundException.class);
    }

    protected JBPMProcessModel getProcessModel() {
        return new JBPMProcessModel(model, new ArrayList<>());
    }
}
