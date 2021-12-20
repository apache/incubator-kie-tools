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

package org.drools.workbench.screens.scenariosimulation.client.dropdown;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DEFAULT_VALUE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class SettingsScenarioSimulationDropdownViewTest extends AbstractScenarioSimulationDropdownViewTest {

    @Before
    public void setup() {
        super.setup();
        assetsDropdownView = spy(new SettingsScenarioSimulationDropdownView(nativeSelectMock,
                                                                    htmlOptionElementMock,
                                                                    translationServiceMock) {
            {
                this.presenter = presenterMock;
            }

            @Override
            protected JQuerySelectPicker dropdown() {
                return dropdownMock;
            }

            @Override
            protected JQuerySelectPicker.CallbackFunction getOnDropdownChangeHandler() {
                return onDropdownChangeHandlerMock;
            }

        });
    }

    @Test
    public void initialize() {
        ((SettingsScenarioSimulationDropdownView) assetsDropdownView).initialize(DEFAULT_VALUE);
        verify(dropdownMock, times(1)).selectpicker(eq("val"), eq(DEFAULT_VALUE));
        verify(dropdownMock, times(1)).selectpicker(eq("show"));
    }

    @Test
    public void clear() {
        assetsDropdownView.clear();
        verify(assetsDropdownView, times(1)).refreshSelectPicker();
        verify(nativeSelectMock, never()).appendChild(any());
    }
}