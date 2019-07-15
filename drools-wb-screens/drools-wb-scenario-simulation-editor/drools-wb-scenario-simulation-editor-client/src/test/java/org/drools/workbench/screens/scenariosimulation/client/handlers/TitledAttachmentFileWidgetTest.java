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

package org.drools.workbench.screens.scenariosimulation.client.handlers;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.html.Span;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class TitledAttachmentFileWidgetTest extends AbstractNewScenarioTest {

    @Mock
    private FlowPanel fieldsMock;
    @Mock
    private FormLabel titleLabelMock;
    @Mock
    private Span errorLabelMock;

    private TitledAttachmentFileWidget titledAttachmentFileWidget;

    @Before
    public void setup() throws Exception {
        super.setup();
        titledAttachmentFileWidget = spy(new TitledAttachmentFileWidget(ScenarioSimulationEditorConstants.INSTANCE.chooseDMN(),
                                                                        scenarioSimulationServiceMock,
                                                                        scenarioSimulationDropdownMock) {
            {
                this.fields = fieldsMock;
                this.titleLabel = titleLabelMock;
                this.errorLabel = errorLabelMock;
            }
        });
    }

    @Test
    public void clearStatus() {
        titledAttachmentFileWidget.selectedPath = "SELECTED_PATH";
        assertNotNull(titledAttachmentFileWidget.selectedPath);
        titledAttachmentFileWidget.clearStatus();
        verify(titledAttachmentFileWidget, times(1)).updateAssetList();
        verify(errorLabelMock, times(1)).setText(eq(null));
        assertNull(titledAttachmentFileWidget.selectedPath);
    }

    @Test
    public void updateAssetList() {
        titledAttachmentFileWidget.updateAssetList();
        verify(scenarioSimulationDropdownMock, times(1)).loadAssets();
    }

    @Test
    public void validateNullPath() {
        commonValidate(null, false);
    }

    @Test
    public void validateEmptyPath() {
        commonValidate("", false);
    }

    @Test
    public void validatePopulatedPath() {
        commonValidate("SELECTED_PATH", true);
    }

    private void commonValidate(String selectedPath, boolean expected) {
        titledAttachmentFileWidget.selectedPath = selectedPath;
        boolean retrieved = titledAttachmentFileWidget.validate();
        if (expected) {
            verify(errorLabelMock, times(1)).setText(eq(null));
            assertTrue(retrieved);
        } else {
            verify(errorLabelMock, times(1)).setText(eq(ScenarioSimulationEditorConstants.INSTANCE.chooseValidDMNAsset()));
            assertFalse(retrieved);
        }
    }
}