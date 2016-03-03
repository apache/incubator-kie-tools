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

package org.uberfire.ext.plugin.client.widget.popup;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.plugin.client.validation.PluginNameValidator;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewPluginPopUpTest {

    private PluginNameValidator successValidator;
    private PluginNameValidator failureValidator;

    @Mock
    private NewPluginPopUpView view;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private PluginServices pluginServices;
    private Caller<PluginServices> pluginServicesCaller;

    @Mock
    private Plugin plugin;

    @Mock
    private PathPlaceRequest pathPlaceRequest;

    private NewPluginPopUp presenter;

    @Before
    public void setUp() {
        presenter = new NewPluginPopUp( view ) {
            @Override
            protected PlaceRequest getPathPlaceRequest( Plugin response ) {
                return new PathPlaceRequest();
            }
        };

        pluginServicesCaller = new CallerMock<PluginServices>( pluginServices );
        presenter.pluginServices = pluginServicesCaller;
        presenter.placeManager = placeManager;

        when( pluginServices.createNewPlugin( anyString(), any( PluginType.class ) ) ).thenReturn( new Plugin() );

        successValidator = spy( new PluginNameValidator() {
            @Override
            public void validate( String value,
                                  ValidatorCallback callback ) {
                callback.onSuccess();
            }
        } );

        failureValidator = spy( new PluginNameValidator() {
            @Override
            public void validate( String value,
                                  ValidatorCallback callback ) {
                callback.onFailure();
            }
        } );
    }

    @Test
    public void testSuccessfulValidation() {
        presenter.pluginNameValidator = successValidator;

        presenter.onOK( "newPlugin", PluginType.PERSPECTIVE );

        verify( successValidator ).validate( eq( "newPlugin.plugin" ), any( ValidatorCallback.class ) );
        verify( pluginServices ).createNewPlugin( "newPlugin", PluginType.PERSPECTIVE );
    }

    @Test
    public void testFailedValidation() {
        presenter.pluginNameValidator = failureValidator;

        presenter.onOK( "invalid*", PluginType.PERSPECTIVE );

        verify( failureValidator ).validate( eq( "invalid*.plugin" ), any( ValidatorCallback.class ) );
        verify( view ).handleNameValidationError( anyString() );
        verify( view ).invalidName();
        verify( pluginServices, never() ).createNewPlugin( anyString(), any( PluginType.class ) );
    }

    @Test
    public void testPopupCanceled() {
        presenter.onCancel();

        verify( successValidator, never() ).validate( anyString(), any( ValidatorCallback.class ) );
        verify( failureValidator, never() ).validate( anyString(), any( ValidatorCallback.class ) );
        verify( pluginServices, never() ).createNewPlugin( anyString(), any( PluginType.class ) );
        verify( view ).hide();
    }
}