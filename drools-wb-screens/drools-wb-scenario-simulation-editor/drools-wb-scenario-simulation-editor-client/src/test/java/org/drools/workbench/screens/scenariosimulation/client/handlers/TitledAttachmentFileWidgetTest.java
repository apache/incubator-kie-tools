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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.client.wizard.widgets.ComboBox;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.AssetQueryResult;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TitledAttachmentFileWidgetTest extends AbstractNewScenarioTest {

    @Mock
    private VerticalPanel fieldsMock;
    @Mock
    private FormLabel titleLabelMock;
    @Mock
    private ComboBox comboBoxMock;
    @Mock
    private Span errorLabelMock;

    private TitledAttachmentFileWidget titledAttachmentFileWidget;

    @Before
    public void setup() throws Exception {
        super.setup();
        titledAttachmentFileWidget = spy(new TitledAttachmentFileWidget(ScenarioSimulationEditorConstants.INSTANCE.chooseDMN(), libraryPlacesMock, assetQueryServiceMock) {
            {
                this.fields = fieldsMock;
                this.titleLabel = titleLabelMock;
                this.comboBox = comboBoxMock;
                this.errorLabel = errorLabelMock;
            }

            @Override
            protected String getAssetPath(AssetInfo asset) {
                return "TEST";
            }
        });
    }

    @Test
    public void clearStatus() {
        titledAttachmentFileWidget.selectedPath = "SELECTED_PATH";
        assertNotNull(titledAttachmentFileWidget.selectedPath);
        titledAttachmentFileWidget.clearStatus();
        verify(comboBoxMock, times(1)).setText(eq(null));
        verify(titledAttachmentFileWidget, times(1)).updateAssetList();
        verify(errorLabelMock, times(1)).setText(eq(null));
        assertNull(titledAttachmentFileWidget.selectedPath);
    }

    @Test
    public void updateAssetList() {
        titledAttachmentFileWidget.updateAssetList();
        verify(comboBoxMock, times(1)).clear();
        verify(titledAttachmentFileWidget, times(1)).updateAssets(isA(RemoteCallback.class));
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

    @Test
    public void getAssets() {
        RemoteCallback<AssetQueryResult> callbackMock = mock(RemoteCallback.class);
        titledAttachmentFileWidget.updateAssets(callbackMock);
        verify(titledAttachmentFileWidget, times(1)).createProjectQuery();
        verify(assetQueryServiceMock, times(1)).getAssets(isA(ProjectAssetsQuery.class));
        verify(invokerMock, times(1)).call(eq(callbackMock), isA(DefaultErrorCallback.class));
    }

    @Test
    public void createProjectQuery() {
        final ProjectAssetsQuery retrieved = titledAttachmentFileWidget.createProjectQuery();
        assertNotNull(retrieved);
    }

    @Test
    public void addAssets() {
        int size = 4;
        AssetQueryResult assetQueryResult = getAssetQueryResult(size);
        titledAttachmentFileWidget.addAssets(assetQueryResult);
        verify(comboBoxMock, times(size)).addItem(anyString());
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

    private AssetQueryResult getAssetQueryResult(int size) {
        return AssetQueryResult.normal(getAssetInfoList(size));
    }

    private List<AssetInfo> getAssetInfoList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> getAssetInfoMock())
                .collect(Collectors.toList());
    }

    private AssetInfo getAssetInfoMock() {
        AssetInfo toReturn = mock(AssetInfo.class);
        final FolderItem folderItemMock = getFolderItemMock();
        when(toReturn.getFolderItem()).thenReturn(folderItemMock);
        return toReturn;
    }

    private FolderItem getFolderItemMock() {
        FolderItem toReturn = mock(FolderItem.class);
        when(toReturn.getType()).thenReturn(FolderItemType.FILE);
        return toReturn;
    }
}