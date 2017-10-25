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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.events.ProviderTypeSelectedEvent;
import org.guvnor.ala.ui.client.handler.ClientProviderHandler;
import org.guvnor.ala.ui.client.handler.ClientProviderHandlerRegistry;
import org.guvnor.ala.ui.client.handler.FormResolver;
import org.guvnor.ala.ui.client.handler.ProviderConfigurationForm;
import org.guvnor.ala.ui.client.util.PopupHelper;
import org.guvnor.ala.ui.client.wizard.provider.ProviderConfigurationPagePresenter;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.service.ProviderService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.ERROR_MESSAGE;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.SUCCESS_MESSAGE;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderType;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.prepareServiceCallerError;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.NewProviderWizard_ProviderCreateSuccessMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.NewProviderWizard_ProviderNotProperlyConfiguredInSystemErrorMessage;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NewProviderWizardTest
        extends WizardBaseTest {

    @Mock
    private ProviderConfigurationPagePresenter configurationPage;

    @Mock
    private ClientProviderHandlerRegistry handlerRegistry;

    @Mock
    private ErrorCallback<Message> defaultErrorCallback;

    @Mock
    private PopupHelper popupHelper;

    @Mock
    private ProviderService providerService;

    private Caller<ProviderService> providerServiceCaller;

    @Mock
    private EventSourceMock<ProviderTypeSelectedEvent> providerTypeSelectedEvent;

    private NewProviderWizard wizard;

    @Mock
    private ClientProviderHandler providerHandler;

    @Mock
    private FormResolver formResolver;

    @Mock
    private ProviderConfigurationForm configurationForm;

    @Mock
    private ProviderConfiguration providerConfiguration;

    private ProviderType providerType;

    @Before
    public void setUp() {
        when(popupHelper.getPopupErrorCallback()).thenReturn(defaultErrorCallback);
        providerServiceCaller = spy(new CallerMock<>(providerService));
        wizard = new NewProviderWizard(configurationPage,
                                       handlerRegistry,
                                       popupHelper,
                                       translationService,
                                       providerServiceCaller,
                                       notification,
                                       providerTypeSelectedEvent) {
            {
                this.view = wizardView;
            }
        };
        wizard.init();

        providerType = mockProviderType("NewProviderWizardTest");
        when(handlerRegistry.isProviderInstalled(providerType.getKey())).thenReturn(true);
        when(handlerRegistry.getProviderHandler(providerType.getKey())).thenReturn(providerHandler);
        when(providerHandler.getFormResolver()).thenReturn(formResolver);
        when(formResolver.newProviderConfigurationForm()).thenReturn(configurationForm);

        when(translationService.format(NewProviderWizard_ProviderNotProperlyConfiguredInSystemErrorMessage,
                                       providerType.getName()))
                .thenReturn(ERROR_MESSAGE);
        when(translationService.getTranslation(NewProviderWizard_ProviderCreateSuccessMessage)).thenReturn(SUCCESS_MESSAGE);
    }

    @Test
    public void testStartProviderConfigured() {
        wizard.start(providerType);
        verify(handlerRegistry,
               times(2)).getProviderHandler(providerType.getKey());
        verify(providerHandler,
               times(2)).getFormResolver();
        verify(formResolver,
               times(1)).newProviderConfigurationForm();
        verify(configurationPage,
               times(1)).setProviderConfigurationForm(configurationForm);
    }

    @Test
    public void testStartProviderNotConfigured() {
        //the provider is not configured
        when(handlerRegistry.isProviderInstalled(providerType.getKey())).thenReturn(false);

        wizard.start(providerType);

        verify(handlerRegistry,
               never()).getProviderHandler(providerType.getKey());
        verify(providerHandler,
               never()).getFormResolver();
        verify(formResolver,
               never()).newProviderConfigurationForm();
        verify(configurationPage,
               never()).setProviderConfigurationForm(configurationForm);

        wizard.start();
        verify(popupHelper,
               times(1)).showErrorPopup(ERROR_MESSAGE);
    }

    @Test
    public void testCreateProviderSuccess() {
        //initialize and start the wizard.
        wizard.start(providerType);

        //emulate the user completing the wizard.
        preCompleteWizard();

        //emulate the user pressing the finish button.
        wizard.complete();

        //verify that the provider has been created and the proper notifications were fired.
        verify(providerService,
               times(1)).createProvider(providerType,
                                        providerConfiguration);
        verify(notification,
               times(1)).fire(new NotificationEvent(SUCCESS_MESSAGE,
                                                    NotificationEvent.NotificationType.SUCCESS));
        verify(providerTypeSelectedEvent,
               times(1)).fire(new ProviderTypeSelectedEvent(providerType.getKey(),
                                                            providerConfiguration.getId()));
    }

    @Test
    public void testCreateProviderFailure() {
        //initialize and start the wizard.
        wizard.start(providerType);

        //emulate the user completing the wizard.
        preCompleteWizard();

        prepareServiceCallerError(providerService,
                                  providerServiceCaller);

        //emulate the user pressing the finish button.
        wizard.complete();

        verify(providerService,
               times(1)).createProvider(providerType,
                                        providerConfiguration);
        verify(popupHelper,
               times(1)).getPopupErrorCallback();
        verify(defaultErrorCallback,
               times(1)).error(any(Message.class),
                               any(Throwable.class));
        verify(providerTypeSelectedEvent,
               never()).fire(any(ProviderTypeSelectedEvent.class));
    }

    private void preCompleteWizard() {
        //emulate that the page was completed.
        when(configurationPage.buildProviderConfiguration()).thenReturn(providerConfiguration);

        preparePageCompletion(configurationPage);
        wizard.isComplete(Assert::assertTrue);
    }
}
