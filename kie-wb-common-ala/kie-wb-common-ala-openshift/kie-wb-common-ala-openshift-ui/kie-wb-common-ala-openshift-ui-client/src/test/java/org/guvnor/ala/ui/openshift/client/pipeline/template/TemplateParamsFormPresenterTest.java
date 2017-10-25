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

package org.guvnor.ala.ui.openshift.client.pipeline.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.ala.ui.client.util.PopupHelper;
import org.guvnor.ala.ui.client.widget.FormStatus;
import org.guvnor.ala.ui.client.wizard.NewDeployWizard;
import org.guvnor.ala.ui.client.wizard.container.ContainerConfig;
import org.guvnor.ala.ui.client.wizard.container.ContainerConfigParamsChangeEvent;
import org.guvnor.ala.ui.openshift.client.pipeline.template.table.TemplateParamsTablePresenter;
import org.guvnor.ala.ui.openshift.client.validation.OpenShiftClientValidationService;
import org.guvnor.ala.ui.openshift.model.DefaultSettings;
import org.guvnor.ala.ui.openshift.model.TemplateDescriptorModel;
import org.guvnor.ala.ui.openshift.model.TemplateParam;
import org.guvnor.ala.ui.openshift.service.OpenShiftClientService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.SERVICE_CALLER_EXCEPTION_MESSAGE;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.prepareServiceCallerError;
import static org.guvnor.ala.ui.client.util.UIUtil.EMPTY_STRING;
import static org.guvnor.ala.ui.openshift.client.pipeline.template.TemplateParamsFormPresenter.APPLICATION_NAME;
import static org.guvnor.ala.ui.openshift.client.pipeline.template.TemplateParamsFormPresenter.APPLICATION_NAME_TEMPLATE_PARAM;
import static org.guvnor.ala.ui.openshift.client.pipeline.template.TemplateParamsFormPresenter.IMAGE_STREAM_NAMESPACE_TEMPLATE_PARAM;
import static org.guvnor.ala.ui.openshift.client.pipeline.template.TemplateParamsFormPresenter.KIE_SERVER_CONTAINER_DEPLOYMENT;
import static org.guvnor.ala.ui.openshift.client.pipeline.template.TemplateParamsFormPresenter.PROJECT_NAME;
import static org.guvnor.ala.ui.openshift.client.pipeline.template.TemplateParamsFormPresenter.RESOURCE_SECRETS_URI;
import static org.guvnor.ala.ui.openshift.client.pipeline.template.TemplateParamsFormPresenter.RESOURCE_STREAMS_URI;
import static org.guvnor.ala.ui.openshift.client.pipeline.template.TemplateParamsFormPresenter.RESOURCE_TEMPLATE_PARAM_VALUES;
import static org.guvnor.ala.ui.openshift.client.pipeline.template.TemplateParamsFormPresenter.RESOURCE_TEMPLATE_URI;
import static org.guvnor.ala.ui.openshift.client.pipeline.template.TemplateParamsFormPresenter.SERVICE_NAME;
import static org.guvnor.ala.ui.openshift.client.pipeline.template.TemplateParamsFormPresenter.SERVICE_NAME_SUFFIX;
import static org.guvnor.ala.ui.openshift.client.resources.i18n.GuvnorAlaOpenShiftUIConstants.TemplateParamsFormPresenter_GetTemplateFileConfigError;
import static org.guvnor.ala.ui.openshift.client.resources.i18n.GuvnorAlaOpenShiftUIConstants.TemplateParamsFormPresenter_InvalidProjectNameError;
import static org.guvnor.ala.ui.openshift.client.resources.i18n.GuvnorAlaOpenShiftUIConstants.TemplateParamsFormPresenter_RequiredParamsNotCompletedMessage;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TemplateParamsFormPresenterTest {

    private static final String TITLE = "TITLE";

    private static final String RUNTIME_NAME_VALUE = "RUNTIME_NAME_VALUE";

    private static final String DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE = "DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE";

    private static final String DEFAULT_OPEN_SHIFT_IMAGE_STREAMS_VALUE = "DEFAULT_OPEN_SHIFT_IMAGE_STREAMS_VALUE";

    private static final String DEFAULT_OPEN_SHIFT_SECRETS_VALUE = "DEFAULT_OPEN_SHIFT_SECRETS_VALUE";

    private static final int REQUIRED_TEMPLATE_PARAMS_COUNT = 5;

    private static final int NON_REQUIRED_TEMPLATE_PARAMS_COUNT = 5;

    private static final String PARAMS_NOT_COMPLETED_MESSAGE = "PARAMS_NOT_COMPLETED_MESSAGE";

    private static final String GET_TEMPLATE_ERROR_MESSAGE = "GET_TEMPLATE_ERROR_MESSAGE";

    private static final String RUNTIME_NAME_ERROR_MESSAGE = "RUNTIME_NAME_ERROR_MESSAGE";

    private static final int CONTAINER_CONFIG_COUNT = 10;

    @Mock
    private TemplateParamsFormPresenter.View view;

    @Mock
    private TemplateParamsTablePresenter paramsEditorPresenter;

    @Mock
    private TemplateParamsTablePresenter.View paramsEditorPresenterView;

    @Mock
    private TranslationService translationService;

    @Mock
    private PopupHelper popupHelper;

    private Caller<OpenShiftClientService> openshiftClientServiceCaller;

    @Mock
    private OpenShiftClientService openShiftClientService;

    private OpenShiftClientValidationService openShiftClientValidationService;

    private TemplateParamsFormPresenter presenter;

    private List<TemplateParam> allTemplateParams;

    private List<TemplateParam> managedTemplateParams;

    private List<TemplateParam> requiredTemplateParams;

    private List<TemplateParam> nonRequiredTemplateParams;

    private List<TemplateParam> bannedTemplateParams;

    @Mock
    private TemplateDescriptorModel templateDescriptorModel;

    @Mock
    private ErrorCallback<Message> errorCallback;

    private ArgumentCaptor<Throwable> exceptionCaptor;

    @Before
    public void setUp() {
        when(popupHelper.getPopupErrorCallback()).thenReturn(errorCallback);
        exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);

        initializeParams();
        when(templateDescriptorModel.getParams()).thenReturn(allTemplateParams);

        when(paramsEditorPresenter.getView()).thenReturn(paramsEditorPresenterView);
        openshiftClientServiceCaller = spy(new CallerMock<>(openShiftClientService));
        openShiftClientValidationService = new OpenShiftClientValidationService(openshiftClientServiceCaller);

        presenter = spy(new TemplateParamsFormPresenter(view,
                                                        paramsEditorPresenter,
                                                        translationService,
                                                        popupHelper,
                                                        openshiftClientServiceCaller,
                                                        openShiftClientValidationService));

        presenter.init();
        verify(view,
               times(1)).init(presenter);
        verify(view,
               times(1)).setParamsEditorPresenter(paramsEditorPresenterView);
        verify(paramsEditorPresenter,
               times(1)).setParamChangeHandler(any());
    }

    private void initializeParams() {
        requiredTemplateParams = mockParams("required",
                                            REQUIRED_TEMPLATE_PARAMS_COUNT,
                                            true);
        nonRequiredTemplateParams = mockParams("nonRequired",
                                               NON_REQUIRED_TEMPLATE_PARAMS_COUNT,
                                               false);
        //add the banned parameters
        bannedTemplateParams = new ArrayList<>();
        bannedTemplateParams.add(new TemplateParam(IMAGE_STREAM_NAMESPACE_TEMPLATE_PARAM,
                                                   null,
                                                   null,
                                                   true,
                                                   null));
        bannedTemplateParams.add(new TemplateParam(APPLICATION_NAME_TEMPLATE_PARAM,
                                                   null,
                                                   null,
                                                   true,
                                                   null));

        allTemplateParams = new ArrayList<>();
        allTemplateParams.addAll(requiredTemplateParams);
        allTemplateParams.addAll(nonRequiredTemplateParams);
        allTemplateParams.addAll(bannedTemplateParams);

        //the banned parameters are not shown in the ui
        managedTemplateParams = new ArrayList<>();
        managedTemplateParams.addAll(requiredTemplateParams);
        managedTemplateParams.addAll(nonRequiredTemplateParams);
    }

    @Test
    public void testGetWizardTitle() {
        when(view.getWizardTitle()).thenReturn(TITLE);
        assertEquals(TITLE,
                     presenter.getWizardTitle());
    }

    @Test
    public void testGetView() {
        assertEquals(view,
                     presenter.getView());
    }

    @Test
    public void testInitializeSuccessful() {
        DefaultSettings defaultSettings = mock(DefaultSettings.class);

        when(defaultSettings.getValue(DefaultSettings.DEFAULT_OPEN_SHIFT_TEMPLATE)).thenReturn(DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE);
        when(defaultSettings.getValue(DefaultSettings.DEFAULT_OPEN_SHIFT_IMAGE_STREAMS)).thenReturn(DEFAULT_OPEN_SHIFT_IMAGE_STREAMS_VALUE);
        when(defaultSettings.getValue(DefaultSettings.DEFAULT_OPEN_SHIFT_SECRETS)).thenReturn(DEFAULT_OPEN_SHIFT_SECRETS_VALUE);

        when(openShiftClientService.getDefaultSettings()).thenReturn(defaultSettings);
        when(openShiftClientService.getTemplateModel(DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE)).thenReturn(templateDescriptorModel);

        presenter.initialise();

        verify(view,
               times(1)).setTemplateURL(DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE);
        verify(view,
               times(1)).setImageStreamsURL(DEFAULT_OPEN_SHIFT_IMAGE_STREAMS_VALUE);
        verify(view,
               times(1)).setSecretsFileURL(DEFAULT_OPEN_SHIFT_SECRETS_VALUE);
    }

    @Test
    public void testInitializeFailed() {
        when(openShiftClientService.getTemplateModel(DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE)).thenReturn(templateDescriptorModel);

        prepareServiceCallerError(openShiftClientService,
                                  openshiftClientServiceCaller);
        presenter.initialise();

        verify(errorCallback,
               times(1)).error(any(Message.class),
                               exceptionCaptor.capture());
        assertEquals(SERVICE_CALLER_EXCEPTION_MESSAGE,
                     exceptionCaptor.getValue().getMessage());
    }

    @Test
    public void testPrepareView() {
        presenter.prepareView();
        verify(paramsEditorPresenterView,
               times(1)).redraw();
    }

    @Test
    public void testIsComplete() {

        when(view.getRuntimeName()).thenReturn(EMPTY_STRING);
        when(view.getImageStreamsURL()).thenReturn(EMPTY_STRING);
        when(view.getSecretsFileURL()).thenReturn(EMPTY_STRING);

        presenter.isComplete(Assert::assertFalse);

        //the runtime name is completed.
        when(view.getRuntimeName()).thenReturn(RUNTIME_NAME_VALUE);
        when(openShiftClientService.isValidProjectName(RUNTIME_NAME_VALUE)).thenReturn(true);
        presenter.onRuntimeNameChange();
        presenter.isComplete(Assert::assertFalse);

        //image streams url is completed
        when(view.getImageStreamsURL()).thenReturn(DEFAULT_OPEN_SHIFT_IMAGE_STREAMS_VALUE);
        presenter.isComplete(Assert::assertFalse);

        //the secrets url is completed
        when(view.getSecretsFileURL()).thenReturn(DEFAULT_OPEN_SHIFT_SECRETS_VALUE);
        presenter.isComplete(Assert::assertFalse);

        //emulate the template loading.
        when(view.getTemplateURL()).thenReturn(DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE);
        when(openShiftClientService.getTemplateModel(DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE)).thenReturn(templateDescriptorModel);
        presenter.onTemplateURLChange();

        //the template is loaded, etc, but required parameters are not yet completed.
        presenter.isComplete(Assert::assertFalse);

        //complete the required params
        requiredTemplateParams.forEach(param -> param.setValue("some value"));
        //the params form must be now completed.
        presenter.isComplete(Assert::assertTrue);
    }

    @Test
    public void testClear() {
        presenter.clear();
        verify(paramsEditorPresenter,
               times(1)).clear();
        verify(view,
               times(1)).clearRequiredParamsHelpText();
    }

    @Test
    public void testOnRuntimeNameChangeValid() {
        when(view.getRuntimeName()).thenReturn(RUNTIME_NAME_VALUE);
        when(openShiftClientService.isValidProjectName(RUNTIME_NAME_VALUE)).thenReturn(true);
        presenter.onRuntimeNameChange();
        verify(view,
               times(1)).setRuntimeNameStatus(FormStatus.VALID);
        verify(view,
               times(1)).clearRuntimeNameHelpText();
        verify(presenter,
               times(1)).onContentChange();
    }

    @Test
    public void testOnRuntimeNameInvalid() {
        when(view.getRuntimeName()).thenReturn(RUNTIME_NAME_VALUE);
        when(openShiftClientService.isValidProjectName(RUNTIME_NAME_VALUE)).thenReturn(false);
        when(translationService.getTranslation(TemplateParamsFormPresenter_InvalidProjectNameError)).thenReturn(RUNTIME_NAME_ERROR_MESSAGE);
        presenter.onRuntimeNameChange();

        verify(view,
               times(1)).setRuntimeNameStatus(FormStatus.ERROR);
        verify(view,
               times(1)).setRuntimeNameHelpText(RUNTIME_NAME_ERROR_MESSAGE);
        verify(presenter,
               times(1)).onContentChange();
    }

    @Test
    public void testOnTemplateURLChangeInvalid() {
        when(view.getTemplateURL()).thenReturn(EMPTY_STRING);
        presenter.onTemplateURLChange();
        verify(view,
               times(1)).setTemplateURLStatus(FormStatus.ERROR);
        verify(paramsEditorPresenter,
               times(1)).clear();

        verify(view,
               times(1)).clearRequiredParamsHelpText();
    }

    @Test
    public void testOnTemplateURLChangeValidLoadSuccessful() {
        when(translationService.getTranslation(TemplateParamsFormPresenter_RequiredParamsNotCompletedMessage)).thenReturn(PARAMS_NOT_COMPLETED_MESSAGE);

        when(view.getTemplateURL()).thenReturn(DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE);
        when(openShiftClientService.getTemplateModel(DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE)).thenReturn(templateDescriptorModel);

        presenter.onTemplateURLChange();
        verify(view,
               times(1)).setTemplateURLStatus(FormStatus.VALID);

        verify(paramsEditorPresenter,
               times(1)).clear();
        verify(view,
               times(2)).clearRequiredParamsHelpText();
        verify(paramsEditorPresenter,
               times(1)).setItems(managedTemplateParams);
        verify(view,
               times(1)).setRequiredParamsHelpText(PARAMS_NOT_COMPLETED_MESSAGE);
    }

    @Test
    public void testOnTemplateURLChangeValidLoadFailed() {
        when(translationService.getTranslation(TemplateParamsFormPresenter_GetTemplateFileConfigError)).thenReturn(GET_TEMPLATE_ERROR_MESSAGE);

        when(view.getTemplateURL()).thenReturn(DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE);

        prepareServiceCallerError(openShiftClientService,
                                  openshiftClientServiceCaller);

        presenter.onTemplateURLChange();
        verify(view,
               times(1)).setTemplateURLStatus(FormStatus.ERROR);

        verify(paramsEditorPresenter,
               times(1)).clear();
        verify(view,
               times(1)).clearRequiredParamsHelpText();
        verify(paramsEditorPresenter,
               never()).setItems(any());
        verify(popupHelper,
               times(1)).showErrorPopup(GET_TEMPLATE_ERROR_MESSAGE + ": " + SERVICE_CALLER_EXCEPTION_MESSAGE);
    }

    @Test
    public void testOnImageStreamsURLChangeValid() {
        when(view.getImageStreamsURL()).thenReturn(DEFAULT_OPEN_SHIFT_IMAGE_STREAMS_VALUE);
        presenter.onImageStreamsURLChange();
        verify(view,
               times(1)).setImageStreamsURLStatus(FormStatus.VALID);
        verify(presenter,
               times(1)).onContentChange();
    }

    @Test
    public void testOnImageStreamsURLChangeInvalid() {
        when(view.getImageStreamsURL()).thenReturn(EMPTY_STRING);
        presenter.onImageStreamsURLChange();
        verify(view,
               times(1)).setImageStreamsURLStatus(FormStatus.ERROR);
        verify(presenter,
               times(1)).onContentChange();
    }

    @Test
    public void testOnSecretsFileURLChangeValid() {
        when(view.getSecretsFileURL()).thenReturn(DEFAULT_OPEN_SHIFT_SECRETS_VALUE);
        presenter.onSecretsFileURLChange();
        verify(view,
               times(1)).setSecretsFileURLStatus(FormStatus.VALID);
        verify(presenter,
               times(1)).onContentChange();
    }

    @Test
    public void testOnSecretsFileURLChangeInvalid() {
        when(view.getSecretsFileURL()).thenReturn(EMPTY_STRING);
        presenter.onSecretsFileURLChange();
        verify(view,
               times(1)).setSecretsFileURLStatus(FormStatus.ERROR);
        verify(presenter,
               times(1)).onContentChange();
    }

    @Test
    public void testOnContainerConfigurationsChangeWithConfigs() {
        List<ContainerConfig> containerConfigs = mockContainerConfigList(CONTAINER_CONFIG_COUNT);
        presenter.onContainerConfigurationsChange(new ContainerConfigParamsChangeEvent(containerConfigs));

        Map<String, String> params = presenter.buildParams();
        String containerParam = params.get(KIE_SERVER_CONTAINER_DEPLOYMENT);
        String expectedContainerParam = buildExpectedContainerDeploymentParamValue(containerConfigs);
        assertEquals(expectedContainerParam,
                     containerParam);
    }

    @Test
    public void testOnContainerConfigurationsChangeWithNoConfigs() {
        presenter.onContainerConfigurationsChange(new ContainerConfigParamsChangeEvent(new ArrayList<>()));
        Map<String, String> params = presenter.buildParams();
        String containerParam = params.get(KIE_SERVER_CONTAINER_DEPLOYMENT);
        assertNull(containerParam);
    }

    @Test
    public void testBuildParams() {
        //emulate all is completed
        when(view.getRuntimeName()).thenReturn(RUNTIME_NAME_VALUE);
        when(view.getImageStreamsURL()).thenReturn(DEFAULT_OPEN_SHIFT_IMAGE_STREAMS_VALUE);
        when(view.getSecretsFileURL()).thenReturn(DEFAULT_OPEN_SHIFT_SECRETS_VALUE);

        when(openShiftClientService.isValidProjectName(RUNTIME_NAME_VALUE)).thenReturn(true);
        //valid runtime name was completed
        presenter.onRuntimeNameChange();

        when(view.getTemplateURL()).thenReturn(DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE);
        when(openShiftClientService.getTemplateModel(DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE)).thenReturn(templateDescriptorModel);
        //template was loaded
        presenter.onTemplateURLChange();

        //config configurations were loaded
        List<ContainerConfig> containerConfigs = mockContainerConfigList(CONTAINER_CONFIG_COUNT);
        presenter.onContainerConfigurationsChange(new ContainerConfigParamsChangeEvent(containerConfigs));

        //template params was completed
        for (int i = 0; i < allTemplateParams.size(); i++) {
            String paramValue = "PARAM_VALUE" + String.valueOf(i);
            allTemplateParams.get(i).setValue(paramValue);
        }

        StringBuilder templateParamsValueBuilder = new StringBuilder();
        managedTemplateParams.forEach(param -> {
            if (templateParamsValueBuilder.length() > 0) {
                templateParamsValueBuilder.append(",");
            }
            templateParamsValueBuilder.append(param.getName());
            templateParamsValueBuilder.append("=");
            templateParamsValueBuilder.append(param.getValue());
        });
        templateParamsValueBuilder.append("," + IMAGE_STREAM_NAMESPACE_TEMPLATE_PARAM + "=" + RUNTIME_NAME_VALUE);
        templateParamsValueBuilder.append("," + APPLICATION_NAME_TEMPLATE_PARAM + "=" + RUNTIME_NAME_VALUE);

        Map<String, String> expectedParams = new HashMap<>();
        expectedParams.put(NewDeployWizard.RUNTIME_NAME,
                           RUNTIME_NAME_VALUE);
        expectedParams.put(PROJECT_NAME,
                           RUNTIME_NAME_VALUE);
        expectedParams.put(APPLICATION_NAME,
                           RUNTIME_NAME_VALUE);
        expectedParams.put(SERVICE_NAME,
                           RUNTIME_NAME_VALUE + SERVICE_NAME_SUFFIX);
        expectedParams.put(RESOURCE_TEMPLATE_URI,
                           DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE);
        expectedParams.put(RESOURCE_STREAMS_URI,
                           DEFAULT_OPEN_SHIFT_IMAGE_STREAMS_VALUE);
        expectedParams.put(RESOURCE_SECRETS_URI,
                           DEFAULT_OPEN_SHIFT_SECRETS_VALUE);
        expectedParams.put(KIE_SERVER_CONTAINER_DEPLOYMENT,
                           buildExpectedContainerDeploymentParamValue(containerConfigs));
        expectedParams.put(RESOURCE_TEMPLATE_PARAM_VALUES,
                           templateParamsValueBuilder.toString());

        Map<String, String> result = presenter.buildParams();
        assertEquals(expectedParams,
                     result);
    }

    private List<TemplateParam> mockParams(String prefix,
                                           int count,
                                           boolean required) {
        List<TemplateParam> params = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            params.add(new TemplateParam(prefix + ".name." + i,
                                         null,
                                         null,
                                         required,
                                         null));
        }
        return params;
    }

    private String buildExpectedContainerDeploymentParamValue(List<ContainerConfig> containerConfigs) {
        return presenter.buildContainerDeploymentParamValue(containerConfigs);
    }

    private List<ContainerConfig> mockContainerConfigList(int count) {
        List<ContainerConfig> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(mockContainerConfig(String.valueOf(i)));
        }
        return result;
    }

    private ContainerConfig mockContainerConfig(String suffix) {
        return new ContainerConfig("ContainerConfig.name." + suffix,
                                   "ContainerConfig.groupId." + suffix,
                                   "ContainerConfig.artifactId." + suffix,
                                   "ContainerConfig.version." + suffix);
    }
}