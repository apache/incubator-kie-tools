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
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.events.ProviderTypeListRefreshEvent;
import org.guvnor.ala.ui.client.wizard.providertype.EnableProviderTypePagePresenter;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeStatus;
import org.guvnor.ala.ui.service.ProviderTypeService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.commons.data.Pair;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.ERROR_MESSAGE;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.SUCCESS_MESSAGE;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.buildProviderTypeStatusList;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderTypeList;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.prepareServiceCallerError;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.EnableProviderTypeWizard_ProviderTypeEnableErrorMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.EnableProviderTypeWizard_ProviderTypeEnableSuccessMessage;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class EnableProviderTypeWizardTest
        extends WizardBaseTest {

    @Mock
    private EnableProviderTypePagePresenter enableProviderTypePage;

    @Mock
    private ProviderTypeService providerTypeService;

    private Caller<ProviderTypeService> providerTypeServiceCaller;

    @Mock
    private EventSourceMock<ProviderTypeListRefreshEvent> providerTypeListRefreshEvent;

    private EnableProviderTypeWizard wizard;

    private List<ProviderType> providerTypes;

    private List<Pair<ProviderType, ProviderTypeStatus>> providerTypeStatus;

    private List<ProviderType> selectedProviders;

    @Before
    public void setUp() {
        //mock an arbitrary set of provider types.
        providerTypes = mockProviderTypeList(3);
        providerTypeStatus = buildProviderTypeStatusList(providerTypes,
                                                         ProviderTypeStatus.DISABLED);

        //setup translationService messages.
        when(translationService.getTranslation(EnableProviderTypeWizard_ProviderTypeEnableSuccessMessage))
                .thenReturn(SUCCESS_MESSAGE);
        when(translationService.getTranslation(EnableProviderTypeWizard_ProviderTypeEnableErrorMessage))
                .thenReturn(ERROR_MESSAGE);

        providerTypeServiceCaller = spy(new CallerMock<>(providerTypeService));
        wizard = new EnableProviderTypeWizard(enableProviderTypePage,
                                              translationService,
                                              providerTypeServiceCaller,
                                              notification,
                                              providerTypeListRefreshEvent) {
            {
                this.view = wizardView;
            }
        };
        wizard.init();
    }

    @Test
    public void testEnableProviderSuccess() {
        //initialize and start the wizard.
        wizard.start(providerTypeStatus);

        //emulate the user completing the wizard.
        preCompleteWizard();

        //emulates the user pressing the finish button
        wizard.complete();

        //verify the provider types has been enabled and the proper notifications were fired.
        verify(providerTypeService,
               times(1)).enableProviderTypes(selectedProviders);
        verify(notification,
               times(1)).fire(new NotificationEvent(SUCCESS_MESSAGE,
                                                    NotificationEvent.NotificationType.SUCCESS));

        verify(providerTypeListRefreshEvent,
               times(1)).fire(new ProviderTypeListRefreshEvent(selectedProviders.get(0).getKey()));
    }

    @Test
    public void testEnableProviderFailure() {
        //initialize and start the wizard.
        wizard.start(providerTypeStatus);

        //emulate the user completing the wizard.
        preCompleteWizard();

        prepareServiceCallerError(providerTypeService,
                                  providerTypeServiceCaller);
        //emulates the user pressing the finish button
        wizard.complete();

        verify(providerTypeService,
               times(1)).enableProviderTypes(selectedProviders);
        verify(notification,
               times(1)).fire(new NotificationEvent(ERROR_MESSAGE,
                                                    NotificationEvent.NotificationType.ERROR));
    }

    private void preCompleteWizard() {
        //select a couple of providers and emulate page completion.
        int selectedIndex1 = 1;
        int selectedIndex2 = 2;
        selectedProviders = new ArrayList<>();
        selectedProviders.add(providerTypes.get(selectedIndex1));
        selectedProviders.add(providerTypes.get(selectedIndex2));
        when(enableProviderTypePage.getSelectedProviderTypes()).thenReturn(selectedProviders);

        preparePageCompletion(enableProviderTypePage);
        wizard.isComplete(Assert::assertTrue);
    }
}