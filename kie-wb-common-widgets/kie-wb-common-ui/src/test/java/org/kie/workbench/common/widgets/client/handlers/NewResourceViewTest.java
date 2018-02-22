/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.handlers;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import com.google.gwtmockito.fakes.FakeProvider;
import org.guvnor.common.services.project.model.Package;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ModalHeader;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.resources.i18n.KieWorkbenchWidgetsConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WithClassesToStub(ModalHeader.class)
@RunWith(GwtMockitoTestRunner.class)
public class NewResourceViewTest {

    private static final String HANDLER_DESCRIPTION = "Handler Description";

    @Mock
    private NewResourcePresenter presenter;

    @Mock
    private Style handlerExtensionsGroupStyle;

    @GwtMock
    private PackageListBox packageListBox;

    @GwtMock
    private BaseModal modal;

    @InjectMocks
    private NewResourceViewImpl view;

    @Mock
    private NewResourceHandler handler;

    @Mock
    private FormLabel extensionLabel;

    private ArgumentCaptor<ValidatorWithReasonCallback> callbackCaptor = ArgumentCaptor.forClass(ValidatorWithReasonCallback.class);

    @Before
    public void setUp() {
        view.fileNameTextBox = mock(TextBox.class);
        view.fileNameHelpInline = mock(HelpBlock.class);
        view.translationService = mock(TranslationService.class);
        view.packageHelpInline = mock(HelpBlock.class);
        view.handlerExtensions = mock(FlowPanel.class);
        view.fileTypeLabel = mock(FormLabel.class);
        view.modal = modal;
        view.packageListBox = packageListBox;
        view.init(presenter);

        when(view.handlerExtensionsGroup.getStyle()).thenReturn(handlerExtensionsGroupStyle);

        when(handler.getDescription()).thenReturn(HANDLER_DESCRIPTION);

        GwtMockito.useProviderForType(FormLabel.class, new FakeProvider<FormLabel>() {
            @Override
            public FormLabel getFake(Class<?> aClass) {
                return extensionLabel;
            }
        });
    }

    @Test
    public void testModalView() {
        view.show();

        verify(modal).show();

        view.hide();

        verify(modal).hide();
    }

    @Test
    public void testSetHandlerWithoutExtensions() {
        view.setActiveHandler(handler);

        verify(view.fileTypeLabel).setText(HANDLER_DESCRIPTION);
        verify(packageListBox).setUp(anyBoolean());
        verify(view.handlerExtensions).clear();
        verify(handlerExtensionsGroupStyle).setDisplay(Style.Display.NONE);
        verify(view.handlerExtensions,
               never()).add(any());
    }

    @Test
    public void testSetHandlerWithExtensions() {
        final IsWidget extension = mock(IsWidget.class);
        final List<Pair<String, ? extends IsWidget>> extensions = new ArrayList<>();

        final String extensionName = "extension name";
        extensions.add(Pair.newPair(extensionName, extension));

        when(handler.getExtensions()).thenReturn(extensions);

        view.setActiveHandler(handler);

        verify(view.fileTypeLabel).setText(HANDLER_DESCRIPTION);
        verify(packageListBox).setUp(anyBoolean());
        verify(view.handlerExtensions).clear();
        verify(handlerExtensionsGroupStyle).setDisplay(Style.Display.BLOCK);
        verify(view.handlerExtensions).add(extensionLabel);
        verify(view.handlerExtensions).add(extension);
        verify(extensionLabel).setText(extensionName);
    }

    @Test
    public void testSetHandlerWithExtensionsWithoutLabel() {
        final IsWidget extension = mock(IsWidget.class);
        final List<Pair<String, ? extends IsWidget>> extensions = new ArrayList<>();

        final String extensionName = "";
        extensions.add(Pair.newPair(extensionName, extension));

        when(handler.getExtensions()).thenReturn(extensions);

        view.setActiveHandler(handler);

        verify(view.fileTypeLabel).setText(HANDLER_DESCRIPTION);
        verify(packageListBox).setUp(anyBoolean());
        verify(view.handlerExtensions).clear();
        verify(handlerExtensionsGroupStyle).setDisplay(Style.Display.BLOCK);
        verify(view.handlerExtensions, never()).add(extensionLabel);
        verify(view.handlerExtensions).add(extension);
        verify(extensionLabel, never()).setText(eq(extensionName));
        verify(extensionLabel, never()).setText(anyString());
    }

    @Test
    public void testOnOkButton_successfulValidation() {
        when(packageListBox.getSelectedPackage()).thenReturn(mock(Package.class));

        when(view.fileNameTextBox.getText()).thenReturn("mock");

        view.onOKButtonClick();

        validateClearErrors();

        verify(presenter).validate(anyString(),
                                   any(ValidatorWithReasonCallback.class));
    }

    @Test
    public void testOnOKButton_nullFileNameValidationFailure() {
        testFileNameFailure(null);
    }

    @Test
    public void testOnOKButton_emptyFileNameValidationFailure() {
        testFileNameFailure("");
    }

    @Test
    public void testOnOKButton_whiteSpaceFileNameValidationFailure() {
        testFileNameFailure(" ");
    }

    protected void testFileNameFailure(String fileName) {
        when(view.fileNameTextBox.getText()).thenReturn(fileName);

        view.onOKButtonClick();

        validateClearErrors();

        verify(view.fileNameGroup).addClassName(ValidationState.ERROR.getCssName());
        verify(view.fileNameHelpInline).setText(anyString());
        verify(view.translationService).getTranslation(KieWorkbenchWidgetsConstants.NewResourceViewFileNameIsMandatory);

        verify(packageListBox,
               never()).getSelectedPackage();

        verify(view.packageGroup,
               never()).addClassName(ValidationState.ERROR.getCssName());
        verify(view.packageHelpInline,
               never()).setText(anyString());
        verify(view.translationService,
               never()).getTranslation(KieWorkbenchWidgetsConstants.NewResourceViewMissingPath);

        verify(presenter,
               never()).validate(anyString(),
                                 any(ValidatorWithReasonCallback.class));
    }

    @Test
    public void testOnOKButton_packageValidationFailure() {

        when(packageListBox.getSelectedPackage()).thenReturn(null);
        when(view.fileNameTextBox.getText()).thenReturn("mock");

        view.onOKButtonClick();

        validateClearErrors();

        verify(view.fileNameGroup,
               never()).addClassName(ValidationState.ERROR.getCssName());
        verify(view.fileNameHelpInline,
               never()).setText(anyString());
        verify(view.translationService,
               never()).getTranslation(KieWorkbenchWidgetsConstants.NewResourceViewFileNameIsMandatory);

        verify(packageListBox).getSelectedPackage();

        verify(view.packageGroup).addClassName(ValidationState.ERROR.getCssName());
        verify(view.packageHelpInline).setText(anyString());
        verify(view.translationService).getTranslation(KieWorkbenchWidgetsConstants.NewResourceViewMissingPath);

        verify(presenter,
               never()).validate(anyString(),
                                 any(ValidatorWithReasonCallback.class));
    }

    protected void validateClearErrors() {
        verify(view.fileNameGroup).removeClassName(ValidationState.ERROR.getCssName());
        verify(view.fileNameHelpInline).clearError();
        verify(view.packageGroup).removeClassName(ValidationState.ERROR.getCssName());
        verify(view.packageHelpInline).clearError();
    }

    /* If validation fails, no item is created, the callback should also set the error state ... */

    @Test
    public void callbackOnValidationFailure_noReason() {
        getCallback().onFailure();
        verify(view.fileNameGroup,
               never()).addClassName(ValidationState.ERROR.getCssName());
        verify(presenter,
               never()).makeItem(anyString());
    }

    /* and show any reason given. */

    @Test
    public void callbackOnValidationFailure_withReason() {
        getCallback().onFailure("mock reason");
        verify(view.fileNameGroup).addClassName(ValidationState.ERROR.getCssName());
        verify(view.fileNameHelpInline).setText("mock reason");
        verify(presenter,
               never()).makeItem(anyString());
    }

    /* Whereas successful validation results in item being created. */

    @Test
    public void callbackOnValidationsuccess() {
        when(packageListBox.getSelectedPackage()).thenReturn(mock(Package.class));
        getCallback().onSuccess();
        verify(view.fileNameGroup).removeClassName(ValidationState.ERROR.getCssName());
        verify(presenter).makeItem(anyString());
    }

    private ValidatorWithReasonCallback getCallback() {
        when(view.fileNameTextBox.getText()).thenReturn("mock");
        when(packageListBox.getSelectedPackage()).thenReturn(mock(Package.class));

        view.onOKButtonClick();
        verify(presenter).validate(anyString(),
                                   callbackCaptor.capture());

        return callbackCaptor.getValue();
    }
}
