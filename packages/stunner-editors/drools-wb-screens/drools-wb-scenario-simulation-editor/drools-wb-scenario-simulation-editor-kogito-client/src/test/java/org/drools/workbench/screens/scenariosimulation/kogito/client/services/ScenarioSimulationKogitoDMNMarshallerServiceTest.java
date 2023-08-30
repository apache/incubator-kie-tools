/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.drools.workbench.screens.scenariosimulation.kogito.client.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.model.KogitoDMNModel;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12UnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.DMN12;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationKogitoDMNMarshallerServiceTest {

    private final static String NAMESPACE1 = "Namespace-1";
    private final static String NAMESPACE2 = "Namespace-2";

    @InjectMocks @Spy
    private ScenarioSimulationKogitoDMNMarshallerService dmnMarshallerServiceSpy;
    @Mock
    private Callback<KogitoDMNModel> kogitoDMNModelCallback;
    @Mock
    private DMN12 dmn12Mock;
    @Mock
    private DMN12 importedDmn12Mock;
    @Mock
    private DMN12 importedDmn12Mock2;
    @Mock
    private ErrorCallback<Object> errorCallbackMock;
    @Mock
    private JSITDefinitions jsitDefinitionsMock;
    @Mock
    private JSITDefinitions jsitImportedDefinitionsMock;
    @Mock
    private JSITDefinitions jsitImportedDefinitionsMock2;
    @Mock
    private JSITItemDefinition importedItemDefinitionMock;
    @Mock
    private JSITItemDefinition importedItemDefinitionMock2;
    @Mock
    private ScenarioSimulationKogitoResourceContentService resourceContentServiceMock;
    @Captor
    private ArgumentCaptor<DMN12UnmarshallCallback> dmn12UnmarshallCallbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<DMN12UnmarshallCallback> dmn12UnmarshallImportedCallbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<DMN12UnmarshallCallback> dmn12UnmarshallImportedCallbackArgumentCaptor2;
    @Captor
    private ArgumentCaptor<RemoteCallback<String>> remoteCallbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<RemoteCallback<String>> remoteCallbackImportedArgumentCaptor;
    @Captor
    private ArgumentCaptor<RemoteCallback<String>> remoteCallbackImportedArgumentCaptor2;
    @Captor
    private ArgumentCaptor<KogitoDMNModel> kogitoDMNModelCaptor;

    @Before
    public void setup() {
        doReturn(jsitDefinitionsMock).when(dmnMarshallerServiceSpy).uncheckedCast(dmn12Mock);
        doReturn(jsitImportedDefinitionsMock).when(dmnMarshallerServiceSpy).uncheckedCast(importedDmn12Mock);
        doReturn(jsitImportedDefinitionsMock2).when(dmnMarshallerServiceSpy).uncheckedCast(importedDmn12Mock2);
        doReturn(Arrays.asList(importedItemDefinitionMock)).when(jsitImportedDefinitionsMock).getItemDefinition();
        doReturn(Arrays.asList(importedItemDefinitionMock2)).when(jsitImportedDefinitionsMock2).getItemDefinition();
        doReturn(NAMESPACE1).when(jsitImportedDefinitionsMock).getNamespace();
        doReturn(NAMESPACE2).when(jsitImportedDefinitionsMock2).getNamespace();
    }

    @Test
    public void getDMNContentNullImports() {
        doReturn(null).when(jsitDefinitionsMock).getImport();
        commonPreCallbackProcess();
        verify(kogitoDMNModelCallback, times(1)).callback(kogitoDMNModelCaptor.capture());
        assertTrue(kogitoDMNModelCaptor.getValue().getImportsDefinitions().isEmpty());
        assertEquals(jsitDefinitionsMock, kogitoDMNModelCaptor.getValue().getJsitDefinitions());
        verify(dmnMarshallerServiceSpy, never()).getDMNImportContentRemoteCallback(any(), any(), anyInt());
    }

    @Test
    public void getDMNContentEmptyImports() {
        doReturn(Collections.emptyList()).when(jsitDefinitionsMock).getImport();
        commonPreCallbackProcess();
        verify(dmnMarshallerServiceSpy, times(1)).uncheckedCast(dmn12Mock);
        verify(kogitoDMNModelCallback, times(1)).callback(kogitoDMNModelCaptor.capture());
        assertTrue(kogitoDMNModelCaptor.getValue().getImportsDefinitions().isEmpty());
        assertEquals(jsitDefinitionsMock, kogitoDMNModelCaptor.getValue().getJsitDefinitions());
        verify(dmnMarshallerServiceSpy, never()).getDMNImportContentRemoteCallback(any(), any(), anyInt());
    }

    @Test
    public void getDMNContentWithImport() {
        JSITImport jsitDMNImport = mock(JSITImport.class);
        when(jsitDMNImport.getName()).thenReturn("testA");
        when(jsitDMNImport.getLocationURI()).thenReturn("import.dmn");
        when(jsitDMNImport.getImportType()).thenReturn("http://www.omg.org/spec/DMN/20180521/MODEL/");
        JSITImport jsitPMMLImport = mock(JSITImport.class);
        when(jsitPMMLImport.getName()).thenReturn("testB");
        when(jsitPMMLImport.getLocationURI()).thenReturn("import.pmml");
        when(jsitPMMLImport.getImportType()).thenReturn("http://www.dmg.org/PMML-4_3)");
        List<JSITImport> imports = new ArrayList<>();
        imports.add(jsitDMNImport);
        imports.add(jsitPMMLImport);
        doReturn(imports).when(jsitDefinitionsMock).getImport();
        commonPreCallbackProcess();
        verify(dmnMarshallerServiceSpy, times(1)).uncheckedCast(dmn12Mock);
        verify(dmnMarshallerServiceSpy, times(1)).getDMNImportContentRemoteCallback(eq(kogitoDMNModelCallback), kogitoDMNModelCaptor.capture(), eq(1));
        verify(resourceContentServiceMock, times(1)).getFileContent(eq(PathFactory.newPath("import.dmn", "src/import.dmn")), remoteCallbackImportedArgumentCaptor.capture(), eq(errorCallbackMock));
        assertEquals(0, kogitoDMNModelCaptor.getValue().getImportsDefinitions().size());
        remoteCallbackImportedArgumentCaptor.getValue().callback("<xml>imported content</xml>");
        verify(dmnMarshallerServiceSpy, times(1)).getDMN12ImportsUnmarshallCallback(kogitoDMNModelCallback, kogitoDMNModelCaptor.getValue(), 1);
        verify(dmnMarshallerServiceSpy, times(1)).unmarshallDMN(eq("<xml>imported content</xml>"), dmn12UnmarshallImportedCallbackArgumentCaptor.capture());
        assertEquals(0, kogitoDMNModelCaptor.getValue().getImportsDefinitions().size());
        dmn12UnmarshallImportedCallbackArgumentCaptor.getValue().callEvent(importedDmn12Mock);
        verify(dmnMarshallerServiceSpy, times(1)).uncheckedCast(importedDmn12Mock);
        verify(kogitoDMNModelCallback, times(1)).callback(kogitoDMNModelCaptor.getValue());
        assertEquals(jsitDefinitionsMock, kogitoDMNModelCaptor.getValue().getJsitDefinitions());
        assertEquals(1, kogitoDMNModelCaptor.getValue().getImportsDefinitions().size());
        assertEquals(jsitImportedDefinitionsMock, kogitoDMNModelCaptor.getValue().getImportsDefinitions().get(NAMESPACE1));
    }

    @Test
    public void getDMNContentWithMultipleImport() {
        JSITImport jsitDMNImport = mock(JSITImport.class);
        when(jsitDMNImport.getName()).thenReturn("testA");
        when(jsitDMNImport.getLocationURI()).thenReturn("import.dmn");
        when(jsitDMNImport.getImportType()).thenReturn("http://www.omg.org/spec/DMN/20180521/MODEL/");
        JSITImport jsitDMN2Import = mock(JSITImport.class);
        when(jsitDMN2Import.getName()).thenReturn("testB");
        when(jsitDMN2Import.getLocationURI()).thenReturn("import2.dmn");
        when(jsitDMN2Import.getImportType()).thenReturn("http://www.omg.org/spec/dmn/20180521/MODEL/");
        List<JSITImport> imports = new ArrayList<>();
        imports.add(jsitDMNImport);
        imports.add(jsitDMN2Import);
        doReturn(imports).when(jsitDefinitionsMock).getImport();
        commonPreCallbackProcess();
        verify(dmnMarshallerServiceSpy, times(1)).uncheckedCast(dmn12Mock);
        verify(dmnMarshallerServiceSpy, times(2)).getDMNImportContentRemoteCallback(eq(kogitoDMNModelCallback), kogitoDMNModelCaptor.capture(), eq(2));
        verify(resourceContentServiceMock, times(1)).getFileContent(eq(PathFactory.newPath("import.dmn", "src/import.dmn")), remoteCallbackImportedArgumentCaptor.capture(), eq(errorCallbackMock));
        remoteCallbackImportedArgumentCaptor.getValue().callback("<xml>imported content</xml>");
        verify(dmnMarshallerServiceSpy, times(1)).getDMN12ImportsUnmarshallCallback(kogitoDMNModelCallback, kogitoDMNModelCaptor.getValue(), 2);
        verify(dmnMarshallerServiceSpy, times(1)).unmarshallDMN(eq("<xml>imported content</xml>"), dmn12UnmarshallImportedCallbackArgumentCaptor.capture());
        assertEquals(0, kogitoDMNModelCaptor.getValue().getImportsDefinitions().size());
        dmn12UnmarshallImportedCallbackArgumentCaptor.getValue().callEvent(importedDmn12Mock);
        verify(dmnMarshallerServiceSpy, times(1)).uncheckedCast(importedDmn12Mock);

        verify(resourceContentServiceMock, times(1)).getFileContent(eq(PathFactory.newPath("import2.dmn", "src/import2.dmn")), remoteCallbackImportedArgumentCaptor2.capture(), eq(errorCallbackMock));
        remoteCallbackImportedArgumentCaptor2.getValue().callback("<xml>imported content 2</xml>");
        verify(dmnMarshallerServiceSpy, times(2)).getDMN12ImportsUnmarshallCallback(kogitoDMNModelCallback, kogitoDMNModelCaptor.getValue(), 2);
        verify(dmnMarshallerServiceSpy, times(1)).unmarshallDMN(eq("<xml>imported content 2</xml>"), dmn12UnmarshallImportedCallbackArgumentCaptor2.capture());
        assertEquals(1, kogitoDMNModelCaptor.getValue().getImportsDefinitions().size());
        dmn12UnmarshallImportedCallbackArgumentCaptor2.getValue().callEvent(importedDmn12Mock2);
        verify(dmnMarshallerServiceSpy, times(1)).uncheckedCast(importedDmn12Mock2);
        verify(kogitoDMNModelCallback, times(1)).callback(kogitoDMNModelCaptor.getValue());
        assertEquals(jsitDefinitionsMock, kogitoDMNModelCaptor.getValue().getJsitDefinitions());
        assertEquals(2, kogitoDMNModelCaptor.getValue().getImportsDefinitions().size());
        assertEquals(jsitImportedDefinitionsMock, kogitoDMNModelCaptor.getValue().getImportsDefinitions().get(NAMESPACE1));
        assertEquals(jsitImportedDefinitionsMock2, kogitoDMNModelCaptor.getValue().getImportsDefinitions().get(NAMESPACE2));
    }

    @Test
    public void getDMNContentWithImportsNotDMN() {
        JSITImport jsitPMMLImport = mock(JSITImport.class);
        when(jsitPMMLImport.getName()).thenReturn("testA");
        when(jsitPMMLImport.getLocationURI()).thenReturn("import1.pmml");
        when(jsitPMMLImport.getImportType()).thenReturn("http://www.dmg.org/PMML-4_3)");
        JSITImport jsitPMMLImport2 = mock(JSITImport.class);
        when(jsitPMMLImport2.getName()).thenReturn("testB");
        when(jsitPMMLImport2.getLocationURI()).thenReturn("import2.pmml");
        when(jsitPMMLImport2.getImportType()).thenReturn("http://www.dmg.org/PMML-4_3)");

        List<JSITImport> imports = new ArrayList<>();
        imports.add(jsitPMMLImport);
        imports.add(jsitPMMLImport2);
        doReturn(imports).when(jsitDefinitionsMock).getImport();
        commonPreCallbackProcess();
        verify(dmnMarshallerServiceSpy, times(1)).uncheckedCast(dmn12Mock);
        verify(kogitoDMNModelCallback, times(1)).callback(kogitoDMNModelCaptor.capture());
        verify(dmnMarshallerServiceSpy, never()).getDMNImportContentRemoteCallback(any(), any(), anyInt());
        assertEquals(jsitDefinitionsMock, kogitoDMNModelCaptor.getValue().getJsitDefinitions());
        assertEquals(0, kogitoDMNModelCaptor.getValue().getImportsDefinitions().size());
    }

    private void commonPreCallbackProcess() {
        Path path = PathFactory.newPath("file.dmn", "src/file.dmn");
        dmnMarshallerServiceSpy.getDMNContent(path, kogitoDMNModelCallback, errorCallbackMock);
        verify(resourceContentServiceMock, times(1)).getFileContent(eq(path), remoteCallbackArgumentCaptor.capture(), eq(errorCallbackMock));
        remoteCallbackArgumentCaptor.getValue().callback("<xml>content<xml>");
        verify(dmnMarshallerServiceSpy, times(1)).unmarshallDMN(eq("<xml>content<xml>"), dmn12UnmarshallCallbackArgumentCaptor.capture());
        verify(dmnMarshallerServiceSpy, times(1)).getDMN12UnmarshallCallback(path, kogitoDMNModelCallback, errorCallbackMock);
        dmn12UnmarshallCallbackArgumentCaptor.getValue().callEvent(dmn12Mock);
    }

}
