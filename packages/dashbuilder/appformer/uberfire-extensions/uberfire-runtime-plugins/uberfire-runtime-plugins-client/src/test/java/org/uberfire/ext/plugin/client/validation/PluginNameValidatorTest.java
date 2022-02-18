/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.plugin.client.validation;

import java.util.List;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.client.validation.ValidationErrorReason;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PluginNameValidatorTest {

    @Mock
    private ValidatorWithReasonCallback callback;

    @Mock
    private PluginServices pluginServices;
    private Caller<PluginServices> pluginServicesCaller;

    @InjectMocks
    private PluginNameValidator validator;

    @Before
    public void setup() {
        pluginServicesCaller = new CallerMock<PluginServices>(pluginServices);
        validator.pluginServices = pluginServicesCaller;

        var plugins = List.of(
                new Plugin("existingPerspectiveLayout",
                        PluginType.PERSPECTIVE_LAYOUT,
                        PathFactory.newPath("test1",
                                "/tmp/test1")),
                new Plugin("existingScreen",
                        PluginType.SCREEN,
                        PathFactory.newPath("test2",
                                "/tmp/test2")),
                new Plugin("existingEditor",
                        PluginType.EDITOR,
                        PathFactory.newPath("test3",
                                "/tmp/test3")));

        when(pluginServices.listPlugins()).thenReturn(plugins);

    }

    @Test
    public void validateEmptyName() {
        validator.validateName("",
                callback);
        verify(callback).onFailure(ValidationErrorReason.EMPTY_NAME.name());

        validator.validateName("notEmpty",
                callback);
        verify(callback).onSuccess();
    }

    @Test
    public void validateEmptyNameWithExtension() {
        validator.validateName(".plugin",
                callback);
        verify(callback).onFailure(ValidationErrorReason.EMPTY_NAME.name());

        validator.validateName("notEmpty.plugin",
                callback);
        verify(callback).onSuccess();
    }

    @Test
    public void validateInvalidName() {
        validator.validateName("invalid*.plugin",
                callback);
        verify(callback).onFailure(ValidationErrorReason.INVALID_NAME.name());

        validator.validateName("valid.plugin",
                callback);
        verify(callback).onSuccess();
    }

    @Test
    public void validateDuplicatedName() {
        validator.validateName("existingPerspectiveLayout.plugin",
                callback);
        verify(callback).onFailure(ValidationErrorReason.DUPLICATED_NAME.name());

        validator.validateName("nonExistingPerspectiveLayout.plugin",
                callback);
        verify(callback).onSuccess();
    }
}
