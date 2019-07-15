/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.workbench.client.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TestRunnerReportingScreenTest {

    @Mock
    PlaceManager placeManager;

    @InjectMocks
    TestRunnerReportingScreen screen;

    @Test
    public void nullIdentifier() {
        screen.onPlaceGainFocusEvent(new PlaceGainFocusEvent(new DefaultPlaceRequest()));
        verify(placeManager).closePlace(TestRunnerReportingScreen.IDENTIFIER);
    }

    @Test
    public void doNotCloseWhenThisScreenGainsFocus() {
        screen.onPlaceGainFocusEvent(new PlaceGainFocusEvent(new DefaultPlaceRequest(TestRunnerReportingScreen.IDENTIFIER)));
        verify(placeManager, never()).closePlace(TestRunnerReportingScreen.IDENTIFIER);
    }

    @Test
    public void closeWhenAnyOtherScreenGainsFocus() {
        screen.onPlaceGainFocusEvent(new PlaceGainFocusEvent(new DefaultPlaceRequest("ProjectScreen")));
        verify(placeManager).closePlace(TestRunnerReportingScreen.IDENTIFIER);
    }
}