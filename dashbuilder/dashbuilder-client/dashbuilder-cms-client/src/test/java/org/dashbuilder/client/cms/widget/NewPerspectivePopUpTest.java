/*
 * Copyright 2017 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.cms.widget;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.layout.editor.api.PerspectiveServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.plugin.client.validation.PluginNameValidator;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewPerspectivePopUpTest {

    @Mock
    private NewPerspectivePopUpView view;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private PerspectiveServices perspectiveServices;

    @Mock
    private PathPlaceRequest pathPlaceRequest;

    private PluginNameValidator pluginNameValidator;
    private NewPerspectivePopUp presenter;
    private boolean validationError = false;

    @Before
    public void setUp() {
        pluginNameValidator = spy(new PluginNameValidator() {
            @Override
            public void validate(String value, ValidatorCallback callback) {
                if (validationError) {
                    callback.onFailure();
                } else {
                    callback.onSuccess();
                }
            }
        });
        presenter = new NewPerspectivePopUp(view,
                new CallerMock<>(perspectiveServices),
                pluginNameValidator,
                placeManager) {

            @Override
            protected PlaceRequest getPathPlaceRequest(Plugin response) {
                return pathPlaceRequest;
            }
        };
    }

    @Test
    public void testSuccessfulValidation() {
        this.validationError = false;
        when(view.getName()).thenReturn("newPerspective");
        when(view.getStyle()).thenReturn(LayoutTemplate.Style.FLUID.toString());
        presenter.onOK();

        verify(pluginNameValidator).validate(eq("newPerspective.plugin"), any(ValidatorCallback.class));
        verify(perspectiveServices).createNewPerspective("newPerspective", LayoutTemplate.Style.FLUID);
        verify(placeManager).goTo(pathPlaceRequest);
        verify(view).hide();
    }

    @Test
    public void testFailedValidation() {
        this.validationError = true;
        when(view.getName()).thenReturn("invalid*");
        when(view.getStyle()).thenReturn(LayoutTemplate.Style.FLUID.toString());
        presenter.onOK();

        verify(pluginNameValidator).validate(eq("invalid*.plugin"), any(ValidatorCallback.class));
        verify(perspectiveServices, never()).createNewPerspective(anyString(), any());
        verify(view).errorInvalidName();
        verify(view, never()).hide();
    }

    @Test
    public void testPopupCanceled() {
        presenter.onCancel();

        verify(pluginNameValidator, never()).validate(anyString(), any(ValidatorCallback.class));
        verify(perspectiveServices, never()).createNewPerspective(anyString(),any());
        verify(view).hide();
    }
}