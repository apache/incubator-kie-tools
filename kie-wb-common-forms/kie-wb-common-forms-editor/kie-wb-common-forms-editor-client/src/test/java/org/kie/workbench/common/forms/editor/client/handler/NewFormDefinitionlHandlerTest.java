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

package org.kie.workbench.common.forms.editor.client.handler;

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Module;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.client.handler.formModel.FormModelsPresenter;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.editor.client.type.FormDefinitionResourceType;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NewFormDefinitionlHandlerTest {

    private static final String NEW_FORM_NAME = "new_form";

    private NewFormDefinitionlHandler handler;

    private CallerMock<FormEditorService> modelerServiceCaller;

    @Mock
    private FormEditorService formEditorService;

    @Mock
    private FormDefinitionResourceType resourceType;

    @Mock
    private TranslationService translationService;

    @Mock
    private FormModelsPresenter formModelsPresenter;

    @Mock
    private WorkspaceProjectContext context;

    @Mock
    private Module module;

    @Mock
    private KieModuleService moduleService;

    private CallerMock<KieModuleService> moduleServiceCaller;

    @Mock
    private ValidationService validationService;

    private CallerMock<ValidationService> validationServiceCaller;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private EventSourceMock<NewResourceSuccessEvent> newResourceSuccessEvent;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private ErrorCallback<Message> errorCallback;

    @Mock
    private ValidatorWithReasonCallback validatorCallback;

    @Mock
    private org.guvnor.common.services.project.model.Package pkg;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Before
    public void init() {
        modelerServiceCaller = new CallerMock<>(formEditorService);
        moduleServiceCaller = new CallerMock<>(moduleService);
        validationServiceCaller = new CallerMock<>(validationService);

        when(context.getActiveModule()).thenReturn(Optional.of(module));

        handler = new NewFormDefinitionlHandler(modelerServiceCaller, resourceType, translationService, formModelsPresenter,
                                                context, moduleServiceCaller, validationServiceCaller, placeManager,
                                                notificationEvent, newResourceSuccessEvent, busyIndicatorView) {
            {
                setupExtensions();
            }

            @Override
            protected ErrorCallback<Message> getErrorCallback() {
                return errorCallback;
            }
        };
    }

    @Test
    public void testBasicFuntionallity() {

        handler.getDescription();
        verify(translationService).getTranslation(FormEditorConstants.NewFormDefinitionlHandlerForm);

        handler.getIcon();
        verify(resourceType).getIcon();

        assertEquals(resourceType, handler.getResourceType());

        Assertions.assertThat(handler.getExtensions())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    public void testAssetNameValidationWithFormModelPresenterFailure() {
        when(formModelsPresenter.isValid()).thenReturn(false);

        handler.validate(NEW_FORM_NAME, validatorCallback);

        verify(formModelsPresenter, times(1)).isValid();
        verify(validatorCallback, times(1)).onFailure();
    }

    @Test
    public void testAssetNameValidationWithValidationServiceFailure() {
        when(formModelsPresenter.isValid()).thenReturn(true);
        when(validationService.isFileNameValid(anyString())).thenReturn(false);

        handler.validate(NEW_FORM_NAME, validatorCallback);

        verify(formModelsPresenter, times(1)).isValid();
        verify(validatorCallback, times(1)).onFailure(anyString());
    }

    @Test
    public void testAssetNameValidationSuccessful() {
        when(formModelsPresenter.isValid()).thenReturn(true);
        when(validationService.isFileNameValid(anyString())).thenReturn(true);

        handler.validate(NEW_FORM_NAME, validatorCallback);

        verify(formModelsPresenter, times(1)).isValid();
        verify(validatorCallback, times(1)).onSuccess();
    }

    @Test
    public void testSuccessfulCreation() {
        handler.create(pkg, NEW_FORM_NAME, newResourcePresenter);

        verify(translationService).getTranslation(FormEditorConstants.NewFormDefinitionlHandlerSelectFormUse);
        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(busyIndicatorView).hideBusyIndicator();

        verify(newResourcePresenter).complete();
        verify(notificationEvent).fire(any());
        verify(newResourceSuccessEvent).fire(any());
        verify(placeManager).goTo(any(Path.class));
    }

    @Test
    public void testFailedCreation() {
        when(formEditorService.createForm(any(), anyString(), any())).thenThrow(new IllegalStateException("Something wrong happened"));

        handler.create(pkg, NEW_FORM_NAME, newResourcePresenter);

        verify(translationService).getTranslation(FormEditorConstants.NewFormDefinitionlHandlerSelectFormUse);
        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(errorCallback).error(any(), any());
    }
}
