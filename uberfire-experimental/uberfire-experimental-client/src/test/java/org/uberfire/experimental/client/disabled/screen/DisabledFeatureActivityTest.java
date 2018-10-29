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

package org.uberfire.experimental.client.disabled.screen;

import java.util.HashMap;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.experimental.client.disabled.component.DisabledFeatureComponent;
import org.uberfire.experimental.client.resources.i18n.UberfireExperimentalConstants;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DisabledFeatureActivityTest {

    private String EXPERIMENTAL_FEATURE_ID = "a random id";

    @Mock
    private PlaceManager placeManager;

    @Mock
    private DisabledFeatureComponent component;

    @Mock
    private TranslationService translationService;

    private PlaceRequest placeRequest;

    private DisabledFeatureActivity activity;

    @Before
    public void init() {
        placeRequest = spy(new DefaultPlaceRequest(DisabledFeatureActivity.ID, new HashMap<>()));
        placeRequest.addParameter(DisabledFeatureActivity.ID_PARAM, EXPERIMENTAL_FEATURE_ID);
        placeRequest.addParameter(DisabledFeatureActivity.FEATURE_ID_PARAM, EXPERIMENTAL_FEATURE_ID);

        activity = new DisabledFeatureActivity(placeManager, component, translationService);

        activity.onStartup(placeRequest);
    }

    @Test
    public void testFunctionality() {
        activity.onOpen();

        verify(placeRequest).getParameter(DisabledFeatureActivity.FEATURE_ID_PARAM, null);

        verify(component).show(EXPERIMENTAL_FEATURE_ID);

        activity.getTitle();

        verify(translationService).getTranslation(UberfireExperimentalConstants.disabledFeatureTitle);
    }
}
