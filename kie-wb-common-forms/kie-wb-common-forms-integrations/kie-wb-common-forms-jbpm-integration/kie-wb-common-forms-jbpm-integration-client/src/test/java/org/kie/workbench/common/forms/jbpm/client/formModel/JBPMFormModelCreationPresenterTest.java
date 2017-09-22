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

package org.kie.workbench.common.forms.jbpm.client.formModel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.jbpm.client.resources.i18n.Constants;
import org.kie.workbench.common.forms.jbpm.model.authoring.AbstractJBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMProcessModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.service.shared.BPMFinderService;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class JBPMFormModelCreationPresenterTest {

    private BPMFinderService finderService;

    private CallerMock<BPMFinderService> finderServiceCallerMock;

    private JBPMFormModelCreationView view;

    private Path path;

    private JBPMFormModelCreationPresenterManager presenter;

    private NewResourcePresenter newResourcePresenter;

    private List<JBPMProcessModel> formModels = new ArrayList<>();

    private TranslationService translationService;

    @Before
    public void setup() {

        initFormModels();

        path = mock(Path.class);

        finderService = mock(BPMFinderService.class);

        when(finderService.getAvailableProcessModels(path)).thenReturn(formModels);

        finderServiceCallerMock = new CallerMock<>(finderService);

        view = mock(JBPMFormModelCreationView.class);

        translationService = mock(TranslationService.class);

        newResourcePresenter = mock(NewResourcePresenter.class);

        presenter = new JBPMFormModelCreationPresenterManager(finderServiceCallerMock,
                                                              view,
                                                              translationService,
                                                              newResourcePresenter);
    }

    @Test
    public void testGeneralFunctionallity() {
        presenter.getPriority();

        presenter.reset();
        verify(view).reset();

        presenter.init(path);

        presenter.getLabel();
        verify(translationService).getTranslation(Constants.Process);

        verify(finderService).getAvailableProcessModels(path);
        verify(view).setProcessModels(formModels);

        presenter.setModel(new AbstractJBPMFormModel("processId",
                                                     null) {

            @Override
            public String getFormName() {
                return "testFormName";
            }

            @Override
            public String getName() {
                return "test";
            }
        });

        verify(newResourcePresenter).setResourceName("testFormName");

        presenter.reset();

        boolean isValid = presenter.isValid();

        assertTrue(isValid);
        verify(translationService,
               never()).getTranslation(Constants.InvalidFormModel);
    }

    protected void initFormModels() {
        List<ModelProperty> processVariables = new ArrayList<>();

        processVariables.add(new ModelPropertyImpl("name",
                                                   new TypeInfoImpl(String.class.getName())));
        processVariables.add(new ModelPropertyImpl("age",
                                                   new TypeInfoImpl(Integer.class.getName())));
        processVariables.add(new ModelPropertyImpl("twitter",
                                                   new TypeInfoImpl(String.class.getName())));
        processVariables.add(new ModelPropertyImpl("offering",
                                                   new TypeInfoImpl(Integer.class.getName())));
        processVariables.add(new ModelPropertyImpl("skills",
                                                   new TypeInfoImpl(String.class.getName())));
        processVariables.add(new ModelPropertyImpl("mail",
                                                   new TypeInfoImpl(String.class.getName())));
        processVariables.add(new ModelPropertyImpl("hr_score",
                                                   new TypeInfoImpl(Integer.class.getName())));
        processVariables.add(new ModelPropertyImpl("tech_score",
                                                   new TypeInfoImpl(Integer.class.getName())));
        processVariables.add(new ModelPropertyImpl("signed",
                                                   new TypeInfoImpl(Boolean.class.getName())));

        BusinessProcessFormModel processFormModel = new BusinessProcessFormModel("hiring",
                                                                                 "hiring",
                                                                                 processVariables);

        TaskFormModel taskFormModel;
        List<TaskFormModel> processTasks = new ArrayList<>();
        List<ModelProperty> taskVariables = new ArrayList<>();

        taskVariables.add(new ModelPropertyImpl("name",
                                                new TypeInfoImpl(String.class.getName())));
        taskVariables.add(new ModelPropertyImpl("age",
                                                new TypeInfoImpl(Integer.class.getName())));
        taskVariables.add(new ModelPropertyImpl("mail",
                                                new TypeInfoImpl(String.class.getName())));
        taskVariables.add(new ModelPropertyImpl("hr_score",
                                                new TypeInfoImpl(String.class.getName())));

        taskFormModel = new TaskFormModel("hiring",
                                          "HRInterview",
                                          taskVariables);
        processTasks.add(taskFormModel);

        taskVariables = new ArrayList<>();
        taskVariables.add(new ModelPropertyImpl("name",
                                                new TypeInfoImpl(String.class.getName())));
        taskVariables.add(new ModelPropertyImpl("age",
                                                new TypeInfoImpl(Integer.class.getName())));
        taskVariables.add(new ModelPropertyImpl("mail",
                                                new TypeInfoImpl(String.class.getName())));
        taskVariables.add(new ModelPropertyImpl("skills",
                                                new TypeInfoImpl(String.class.getName())));
        taskVariables.add(new ModelPropertyImpl("tech_score",
                                                new TypeInfoImpl(Integer.class.getName())));
        taskVariables.add(new ModelPropertyImpl("twitter",
                                                new TypeInfoImpl(String.class.getName())));

        taskFormModel = new TaskFormModel("hiring",
                                          "TechInterview",
                                          taskVariables);
        processTasks.add(taskFormModel);

        JBPMProcessModel model = new JBPMProcessModel(processFormModel,
                                                      processTasks);

        formModels.add(model);
    }
}
