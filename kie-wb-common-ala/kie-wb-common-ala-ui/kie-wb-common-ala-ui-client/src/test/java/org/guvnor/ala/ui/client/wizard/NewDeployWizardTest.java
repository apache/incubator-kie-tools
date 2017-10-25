/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.inject.Instance;

import org.guvnor.ala.ui.client.events.RefreshRuntimeEvent;
import org.guvnor.ala.ui.client.util.PopupHelper;
import org.guvnor.ala.ui.client.wizard.pipeline.PipelineDescriptor;
import org.guvnor.ala.ui.client.wizard.pipeline.params.PipelineParamsForm;
import org.guvnor.ala.ui.client.wizard.pipeline.params.PipelineParamsPagePresenter;
import org.guvnor.ala.ui.client.wizard.pipeline.select.SelectPipelinePagePresenter;
import org.guvnor.ala.ui.model.PipelineKey;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.service.RuntimeService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.PIPELINE1_KEY;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.PIPELINE2_KEY;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.SUCCESS_MESSAGE;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.prepareServiceCallerError;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.NewDeployWizard_PipelineStartSuccessMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.NewDeployWizard_Title;
import static org.guvnor.ala.ui.client.wizard.NewDeployWizard.RUNTIME_NAME;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewDeployWizardTest
        extends WizardBaseTest {

    private static final String RUNTIME = "RUNTIME";

    private static final String TITLE = "TITLE";

    private static final int PARAM_FORMS_COUNT = 6;

    private static final int PARAM_VALUES_COUNT = 7;

    @Mock
    private SelectPipelinePagePresenter selectPipelinePage;

    @Mock
    private ManagedInstance<PipelineParamsPagePresenter> pipelineParamsPageInstance;

    @Mock
    private Instance<PipelineDescriptor> pipelineDescriptorInstance;

    @Mock
    private ErrorCallback<Message> defaultErrorCallback;

    @Mock
    private PopupHelper popupHelper;

    @Mock
    private RuntimeService runtimeService;

    private Caller<RuntimeService> runtimeServiceCaller;

    @Mock
    private EventSourceMock<RefreshRuntimeEvent> refreshRuntimeEvent;

    private NewDeployWizard wizard;

    @Mock
    private Provider provider;

    private Collection<PipelineKey> pipelines;

    private List<PipelineDescriptor> pipelineDescriptors = new ArrayList<>();

    @Mock
    private PipelineDescriptor descriptor1;

    @Mock
    private PipelineDescriptor descriptor2;

    private List<PipelineParamsForm> paramsForms;

    private List<Map<String, String>> paramsFormsValues;

    private List<PipelineParamsPagePresenter> paramsPages = new ArrayList<>();

    @Before
    public void setUp() {
        when(popupHelper.getPopupErrorCallback()).thenReturn(defaultErrorCallback);

        pipelines = new ArrayList<>();
        pipelines.add(PIPELINE1_KEY);
        pipelines.add(PIPELINE2_KEY);

        pipelineDescriptors.add(descriptor1);
        pipelineDescriptors.add(descriptor2);
        paramsForms = mockParamForms(PARAM_FORMS_COUNT);
        paramsFormsValues = mockParamFormsValues(paramsForms);

        when(pipelineDescriptorInstance.iterator()).thenReturn(pipelineDescriptors.iterator());

        paramsPages.clear();

        when(provider.getKey()).thenReturn(mock(ProviderKey.class));

        when(translationService.getTranslation(NewDeployWizard_PipelineStartSuccessMessage)).thenReturn(SUCCESS_MESSAGE);

        runtimeServiceCaller = spy(new CallerMock<>(runtimeService));
        wizard = new NewDeployWizard(selectPipelinePage,
                                     pipelineParamsPageInstance,
                                     pipelineDescriptorInstance,
                                     popupHelper,
                                     translationService,
                                     runtimeServiceCaller,
                                     notification,
                                     refreshRuntimeEvent) {
            {
                this.view = wizardView;
            }

            @Override
            protected PipelineParamsPagePresenter newPipelineParamsPage(PipelineParamsForm paramsForm) {
                PipelineParamsPagePresenter pagePresenter = mock(PipelineParamsPagePresenter.class);
                when(pipelineParamsPageInstance.get()).thenReturn(pagePresenter);
                paramsPages.add(pagePresenter);
                return super.newPipelineParamsPage(paramsForm);
            }
        };
        wizard.init();
    }

    @Test
    public void testStart() {
        wizard.start(provider,
                     pipelines);
        verifyStart();
    }

    @Test
    public void testGetTitle() {
        when(translationService.getTranslation(NewDeployWizard_Title)).thenReturn(TITLE);
        assertEquals(TITLE,
                     wizard.getTitle());
    }

    @Test
    public void testOnStatusChangePipelinePageChangeNoPipelineSelected() {
        when(selectPipelinePage.getPipeline()).thenReturn(null);
        wizard.onStatusChange(new WizardPageStatusChangeEvent(selectPipelinePage));
        assertEquals(1,
                     wizard.getPages().size());
        assertEquals(selectPipelinePage,
                     wizard.getPages().get(0));
    }

    @Test
    public void testOnStatusChangePipelinePageSelectedWithNoParamForms() {
        when(selectPipelinePage.getPipeline()).thenReturn(PIPELINE1_KEY);
        when(descriptor1.accept(PIPELINE1_KEY)).thenReturn(false);
        when(descriptor2.accept(PIPELINE1_KEY)).thenReturn(false);

        wizard.onStatusChange(new WizardPageStatusChangeEvent(selectPipelinePage));
        verify(descriptor1,
               times(1)).accept(PIPELINE1_KEY);
        verify(descriptor2,
               times(1)).accept(PIPELINE1_KEY);
        assertEquals(1,
                     wizard.getPages().size());
        assertEquals(selectPipelinePage,
                     wizard.getPages().get(0));
        verify(pipelineParamsPageInstance,
               never()).get();
    }

    @Test
    public void testOnStatusChangePipelinePageSelectedWithParamForms() {
        when(selectPipelinePage.getPipeline()).thenReturn(PIPELINE1_KEY);
        when(descriptor1.accept(PIPELINE1_KEY)).thenReturn(true);
        when(descriptor2.accept(PIPELINE1_KEY)).thenReturn(false);
        when(descriptor1.getParamForms()).thenReturn(paramsForms);

        wizard.onStatusChange(new WizardPageStatusChangeEvent(selectPipelinePage));
        verify(descriptor1,
               times(1)).accept(PIPELINE1_KEY);
        int totalPages = 1 + paramsForms.size();
        assertEquals(totalPages,
                     wizard.getPages().size());
        paramsForms.forEach(paramsForm -> {
            verify(paramsForm,
                   times(1)).clear();
            verify(paramsForm,
                   times(1)).initialise();
        });
        verify(pipelineParamsPageInstance,
               times(paramsForms.size())).get();
        assertEquals(paramsForms.size(),
                     paramsPages.size());
        for (int i = 0; i < paramsForms.size(); i++) {
            PipelineParamsPagePresenter pagePresenter = paramsPages.get(i);
            verify(pagePresenter,
                   times(1)).setPipelineParamsForm(paramsForms.get(i));
        }
    }

    @Test
    public void testStartDeploymentSuccess() {
        //initialize and start the wizard.
        wizard.start(provider,
                     pipelines);
        verifyStart();

        //e.g. PIPELINE1 was selected.
        preSelectForms(PIPELINE1_KEY);

        //emulate the user completing the wizard.
        preCompleteWizard(PIPELINE1_KEY,
                          RUNTIME);

        //emulates the user pressing the finish button
        wizard.complete();

        //the parameters must the the values collected by the forms.
        Map<String, String> expectedPrams = buildExpectedParams(paramsFormsValues);
        expectedPrams.put(RUNTIME_NAME,
                          RUNTIME);

        verify(runtimeService,
               times(1)).createRuntime(provider.getKey(),
                                       RUNTIME,
                                       PIPELINE1_KEY,
                                       expectedPrams);
        verify(notification,
               times(1)).fire(new NotificationEvent(SUCCESS_MESSAGE,
                                                    NotificationEvent.NotificationType.SUCCESS));

        verify(refreshRuntimeEvent,
               times(1)).fire(new RefreshRuntimeEvent(provider.getKey()));
    }

    @Test
    public void testStartDeploymentFailure() {
        //initialize and start the wizard.
        wizard.start(provider,
                     pipelines);
        verifyStart();

        //e.g. PIPELINE1 was selected.
        preSelectForms(PIPELINE1_KEY);

        //emulate the user completing the wizard.
        preCompleteWizard(PIPELINE1_KEY,
                          RUNTIME);

        prepareServiceCallerError(runtimeService,
                                  runtimeServiceCaller);

        //emulates the user pressing the finish button
        wizard.complete();

        //the parameters must the the values collected by the forms.
        Map<String, String> expectedPrams = buildExpectedParams(paramsFormsValues);
        expectedPrams.put(RUNTIME_NAME,
                          RUNTIME);

        verify(runtimeService,
               times(1)).createRuntime(provider.getKey(),
                                       RUNTIME,
                                       PIPELINE1_KEY,
                                       expectedPrams);

        verify(defaultErrorCallback,
               times(1)).error(any(Message.class),
                               any(Throwable.class));
        verify(notification,
               never()).fire(any(NotificationEvent.class));
    }

    private void verifyStart() {
        assertEquals(1,
                     wizard.getPages().size());
        assertEquals(selectPipelinePage,
                     wizard.getPages().get(0));
        verify(selectPipelinePage,
               times(1)).setup(pipelines);
    }

    public void preSelectForms(PipelineKey pipelineKey) {
        when(selectPipelinePage.getPipeline()).thenReturn(pipelineKey);
        when(descriptor1.accept(pipelineKey)).thenReturn(true);
        when(descriptor1.getParamForms()).thenReturn(paramsForms);
        wizard.onStatusChange(new WizardPageStatusChangeEvent(selectPipelinePage));
    }

    private void preCompleteWizard(PipelineKey pipelineKey,
                                   String runtimeName) {
        when(selectPipelinePage.getPipeline()).thenReturn(pipelineKey);
        wizard.getPages().forEach(page -> preparePageCompletion(page));

        //let an arbitrary form populate the runtime name.
        int index = 2;
        paramsFormsValues.get(index).put(RUNTIME_NAME,
                                         runtimeName);

        wizard.isComplete(Assert::assertTrue);
    }

    private List<PipelineParamsForm> mockParamForms(int count) {
        List<PipelineParamsForm> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(mock(PipelineParamsForm.class));
        }
        return result;
    }

    private List<Map<String, String>> mockParamFormsValues(List<PipelineParamsForm> paramsForms) {
        List<Map<String, String>> paramValues = new ArrayList<>();
        for (int i = 0; i < paramsForms.size(); i++) {
            PipelineParamsForm form = paramsForms.get(i);
            Map<String, String> params = mockParamSet(PARAM_VALUES_COUNT,
                                                      String.valueOf(i));
            when(form.buildParams()).thenReturn(params);
            paramValues.add(params);
        }
        return paramValues;
    }

    private Map<String, String> buildExpectedParams(List<Map<String, String>> paramsList) {
        Map<String, String> expectedParams = new HashMap<>();
        paramsList.forEach(expectedParams::putAll);
        return expectedParams;
    }

    private Map<String, String> mockParamSet(int count,
                                             String prefix) {
        HashMap<String, String> paramSet = new HashMap<>();
        for (int i = 0; i < count; i++) {
            paramSet.put(prefix + ".paramName." + i,
                         prefix + ".paramValue." + i);
        }
        return paramSet;
    }
}
