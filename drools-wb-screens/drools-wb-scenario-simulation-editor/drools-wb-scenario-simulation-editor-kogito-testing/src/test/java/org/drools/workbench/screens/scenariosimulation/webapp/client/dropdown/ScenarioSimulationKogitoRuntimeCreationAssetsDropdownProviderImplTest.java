/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.webapp.client.dropdown;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImplTest {
    
    @Mock
    private KogitoResourceContentService kogitoResourceContentServiceMock;
    @Mock
    private ErrorPopupPresenter errorPopupPresenterMock;
    @Mock
    private Consumer<List<KieAssetsDropdownItem>> assetConsumer;
    @Captor
    private ArgumentCaptor<List<KieAssetsDropdownItem>> dropDownListCaptor;

    private ScenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImpl scenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImplSpy;


    @Before
    public void setup() {
        scenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImplSpy = spy(new ScenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImpl() {
            {
                this.resourceContentService = kogitoResourceContentServiceMock;
                this.errorPopupPresenter = errorPopupPresenterMock;
            }
        });
    }

    @Test
    public void getItems() {
        scenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImplSpy.getItems(assetConsumer);
        verify(kogitoResourceContentServiceMock, times(1)).getFilteredItems(eq(ScenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImpl.DMN_FILE_SEARCH_PATTERN),
                                                                                                 isA(RemoteCallback.class),
                                                                                                 isA(ErrorCallback.class));
    }

    @Test
    public void getRemoteCallBack() {
        RemoteCallback<List<String>> remoteCallBack = scenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImplSpy.getRemoteCallback(assetConsumer);
        remoteCallBack.callback(Arrays.asList("path/B", "a", "target/a", "TARGET/a", "/target/a", "/TARGET/a", "a/target/b", "a/TARGET/b", "mytarget/C", "q/mytarget/D"));
        verify(assetConsumer, times(1)).accept(dropDownListCaptor.capture());
        assertEquals(4, dropDownListCaptor.getValue().size());
        assertEquals("a", dropDownListCaptor.getValue().get(0).getText());
        assertEquals("a", dropDownListCaptor.getValue().get(0).getSubText());
        assertEquals("a", dropDownListCaptor.getValue().get(0).getValue());
        assertEquals("B", dropDownListCaptor.getValue().get(1).getText());
        assertEquals("path/B", dropDownListCaptor.getValue().get(1).getSubText());
        assertEquals("path/B", dropDownListCaptor.getValue().get(1).getValue());
        assertEquals("C", dropDownListCaptor.getValue().get(2).getText());
        assertEquals("mytarget/C", dropDownListCaptor.getValue().get(2).getSubText());
        assertEquals("mytarget/C", dropDownListCaptor.getValue().get(2).getValue());
        assertEquals("D", dropDownListCaptor.getValue().get(3).getText());
        assertEquals("q/mytarget/D", dropDownListCaptor.getValue().get(3).getSubText());
        assertEquals("q/mytarget/D", dropDownListCaptor.getValue().get(3).getValue());
    }

    @Test
    public void remoteCallBackSortedIgnoreCase(){
        RemoteCallback<List<String>> remoteCallBack = scenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImplSpy.getRemoteCallback(assetConsumer);
        List<String> dmnFiles = Arrays.asList("AS", "Ap");
        remoteCallBack.callback(dmnFiles);
        verify(assetConsumer, times(1)).accept(dropDownListCaptor.capture());
        Collections.sort(dmnFiles, String::compareToIgnoreCase);
        assertEquals(dmnFiles,
                     dropDownListCaptor.getValue().stream().map(KieAssetsDropdownItem::getValue).collect(Collectors.toList()));
    }

    @Test
    public void getErrorCallback() {
        ErrorCallback<Object> errorCallback = scenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImplSpy.getErrorCallback();
        assertFalse(errorCallback.error("message", new Throwable("ex")));
        verify(errorPopupPresenterMock, times(1)).showMessage(eq("message: ex"));
    }

    @Test
    public void getKieAssetsDropdownItemFileWithoutPath() {
        String fullPath = "filename.etc";
        KieAssetsDropdownItem item = scenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImplSpy.getKieAssetsDropdownItem(fullPath);
        assertNotNull(item);
        assertEquals(fullPath, item.getValue());
        assertEquals(fullPath, item.getSubText());
        assertEquals(fullPath, item.getText());
    }

    @Test
    public void getKieAssetsDropdownItemFileWithPath() {
        String path = "path/";
        String fileName = "filename.etc";
        String fullPath = path + fileName;
        KieAssetsDropdownItem item = scenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImplSpy.getKieAssetsDropdownItem(fullPath);
        assertNotNull(item);
        assertEquals(fullPath, item.getValue());
        assertEquals(fullPath, item.getSubText());
        assertEquals(fileName, item.getText());
    }

    @Test
    public void getKieAssetsDropdownItemFileWithLongPath() {
        String path = "long/path/";
        String fileName = "filename.etc";
        String fullPath = path + fileName;
        KieAssetsDropdownItem item = scenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImplSpy.getKieAssetsDropdownItem(fullPath);
        assertNotNull(item);
        assertEquals(fullPath, item.getValue());
        assertEquals(fullPath, item.getSubText());
        assertEquals(fileName, item.getText());
    }

    @Test
    public void getKieAssetsDropdownItemFileWithPathWindows() {
        String path = "path\\";
        String fileName = "filename.etc";
        String fullPath = path + fileName;
        KieAssetsDropdownItem item = scenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImplSpy.getKieAssetsDropdownItem(fullPath);
        assertNotNull(item);
        assertEquals(fullPath, item.getValue());
        assertEquals(fullPath, item.getSubText());
        assertEquals(fileName, item.getText());
    }

    @Test
    public void getKieAssetsDropdownItemFileWithLongPathWindows() {
        String path = "long\\pat\\";
        String fileName = "filename.etc";
        String fullPath = path + fileName;
        KieAssetsDropdownItem item = scenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImplSpy.getKieAssetsDropdownItem(fullPath);
        assertNotNull(item);
        assertEquals(fullPath, item.getValue());
        assertEquals(fullPath, item.getSubText());
        assertEquals(fileName, item.getText());
    }
}
