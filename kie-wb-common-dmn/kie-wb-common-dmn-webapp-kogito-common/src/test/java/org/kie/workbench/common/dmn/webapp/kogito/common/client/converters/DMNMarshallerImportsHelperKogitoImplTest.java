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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientDiagramServiceImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.PMMLMarshallerService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.DMNMarshallerImportsHelperKogitoImpl.MODEL_FILES_PATTERN;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.DMNMarshallerImportsHelperKogitoImpl.PMML_FILES_PATTERN;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNMarshallerImportsHelperKogitoImplTest {

    @Mock
    private DMNClientDiagramServiceImpl dmnClientDiagramServiceMock;
    @Mock
    private DMNDiagramUtils dmnDiagramUtilsMock;
    @Mock
    private DMNIncludedNodeFactory dmnIncludedNodeFactoryMock;
    @Mock
    private KogitoResourceContentService kogitoResourceContentServiceMock;
    @Mock
    private Metadata metadataMock;
    @Mock
    private PMMLMarshallerService pmmlMarshallerServiceMock;
    @Mock
    private ServiceCallback serviceCallbackMock;
    @Captor
    private ArgumentCaptor<List<IncludedModel>> modelsCapture;

    private static final String DMN_FILE = "test-dmn.dmn";
    private static final String DMN_PATH = "dmntest/" + DMN_FILE;
    private static final String DMN_CONTENT = "<xml> xml DMN content </xml>";

    private static final String PMML_FILE = "test-pmml.pmml";
    private static final String PMML_PATH = "dmnpmml/" + PMML_FILE;
    private static final String PMML_CONTENT = "<xml> xml PMML content </xml>";

    private static final String TEXT_FILE = "test-file.txt";
    private static final String TEXT_PATH = "invalidfile/" + TEXT_FILE;

    private DMNMarshallerImportsHelperKogitoImpl dmnMarshallerImportsHelperKogitoImpl;
    private Promises promises;

    @Before
    public void setup() {
        promises = new SyncPromises();
        dmnMarshallerImportsHelperKogitoImpl = new DMNMarshallerImportsHelperKogitoImpl(kogitoResourceContentServiceMock,
                                                                                        dmnClientDiagramServiceMock,
                                                                                        promises,
                                                                                        dmnDiagramUtilsMock,
                                                                                        dmnIncludedNodeFactoryMock,
                                                                                        pmmlMarshallerServiceMock);

    }

    @Test
    public void loadModelsDMNFile() {
        when(kogitoResourceContentServiceMock.getFilteredItems(eq(MODEL_FILES_PATTERN), isA(ResourceListOptions.class))).thenReturn(promises.resolve(new String[]{DMN_PATH}));
        when(kogitoResourceContentServiceMock.loadFile(DMN_PATH)).thenReturn(promises.resolve(DMN_CONTENT));
        dmnMarshallerImportsHelperKogitoImpl.loadModels(serviceCallbackMock);
        verify(kogitoResourceContentServiceMock, times(1)).getFilteredItems(eq(MODEL_FILES_PATTERN), isA(ResourceListOptions.class));
        verify(kogitoResourceContentServiceMock, times(1)).loadFile(eq(DMN_PATH));
        verify(dmnClientDiagramServiceMock, times(1)).transform(eq(DMN_CONTENT), isA(ServiceCallback.class));
    }

    @Test
    public void loadModelsPMMLFile() {
        when(kogitoResourceContentServiceMock.getFilteredItems(eq(MODEL_FILES_PATTERN), isA(ResourceListOptions.class))).thenReturn(promises.resolve(new String[]{PMML_PATH}));
        when(kogitoResourceContentServiceMock.loadFile(PMML_PATH)).thenReturn(promises.resolve(PMML_CONTENT));
        when(pmmlMarshallerServiceMock.getDocumentMetadata(PMML_PATH, PMML_CONTENT)).thenReturn(promises.resolve(new PMMLDocumentMetadata(PMML_PATH,
                                                                                                                         PMML_FILE,
                                                                                                                         DMNImportTypes.PMML.getDefaultNamespace(),
                                                                                                                         Collections.emptyList())));
        dmnMarshallerImportsHelperKogitoImpl.loadModels(serviceCallbackMock);
        verify(kogitoResourceContentServiceMock, times(1)).getFilteredItems(eq(MODEL_FILES_PATTERN), isA(ResourceListOptions.class));
        verify(kogitoResourceContentServiceMock, times(1)).loadFile(eq(PMML_PATH));
        verify(pmmlMarshallerServiceMock, times(1)).getDocumentMetadata(eq(PMML_PATH), eq(PMML_CONTENT));
        verify(serviceCallbackMock, times(1)).onSuccess(modelsCapture.capture());
        assertEquals(1, modelsCapture.getValue().size());
        assertEquals(PMML_FILE, modelsCapture.getValue().get(0).getPath());
        assertEquals(PMML_FILE, modelsCapture.getValue().get(0).getModelName());
        assertEquals(DMNImportTypes.PMML.getDefaultNamespace(), modelsCapture.getValue().get(0).getImportType());
        assertEquals(0, ((PMMLIncludedModel) modelsCapture.getValue().get(0)).getModelCount().intValue());
    }

    @Test
    public void loadModelsInvalidFile() {
        when(kogitoResourceContentServiceMock.getFilteredItems(eq(MODEL_FILES_PATTERN), isA(ResourceListOptions.class))).thenReturn(promises.resolve(new String[]{TEXT_PATH}));
        dmnMarshallerImportsHelperKogitoImpl.loadModels(serviceCallbackMock);
        verify(kogitoResourceContentServiceMock, times(1)).getFilteredItems(eq(MODEL_FILES_PATTERN), isA(ResourceListOptions.class));
        verify(kogitoResourceContentServiceMock, never()).loadFile(any());
        verify(pmmlMarshallerServiceMock, never()).getDocumentMetadata(any(), any());
        verify(dmnClientDiagramServiceMock, never()).transform(any(), any());
    }

    @Test
    public void getPMMLDocumentsAsync_EmptyImports() {
        Promise<Map<JSITImport, PMMLDocumentMetadata>> returnPromise = dmnMarshallerImportsHelperKogitoImpl.getPMMLDocumentsAsync(metadataMock, Collections.emptyList());
        returnPromise.then(p0 -> {
            assertEquals(0, p0.size());
            return promises.resolve();
        }).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });
    }

    @Test
    public void getPMMLDocumentsAsyncNoPMMLFiles() {
        when(kogitoResourceContentServiceMock.getFilteredItems(eq(PMML_FILES_PATTERN), isA(ResourceListOptions.class))).thenReturn(promises.resolve(new String[0]));
        List<JSITImport> imports = new ArrayList<>();
        imports.add(mock(JSITImport.class));
        Promise<Map<JSITImport, PMMLDocumentMetadata>> returnPromise = dmnMarshallerImportsHelperKogitoImpl.getPMMLDocumentsAsync(metadataMock, imports);
        verify(kogitoResourceContentServiceMock, times(1)).getFilteredItems(eq(PMML_FILES_PATTERN), isA(ResourceListOptions.class));
        returnPromise.then(p0 -> {
            assertEquals(0, p0.size());
            return promises.resolve();
        }).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });
    }

    @Test
    public void getPMMLDocumentsAsync() {
        when(kogitoResourceContentServiceMock.getFilteredItems(eq(PMML_FILES_PATTERN), isA(ResourceListOptions.class))).thenReturn(promises.resolve(new String[]{PMML_PATH}));
        when(kogitoResourceContentServiceMock.loadFile(PMML_PATH)).thenReturn(promises.resolve(PMML_CONTENT));
        when(pmmlMarshallerServiceMock.getDocumentMetadata(PMML_PATH, PMML_CONTENT)).thenReturn(promises.resolve(new PMMLDocumentMetadata(PMML_PATH,
                                                                                                                                          PMML_FILE,
                                                                                                                                          DMNImportTypes.PMML.getDefaultNamespace(),
                                                                                                                                          Collections.emptyList())));



        List<JSITImport> imports = new ArrayList<>();
        JSITImport jsImportMock = mock(JSITImport.class);
        when(jsImportMock.getLocationURI()).thenReturn(PMML_FILE);
        imports.add(jsImportMock);
        Promise<Map<JSITImport, PMMLDocumentMetadata>> returnPromise = dmnMarshallerImportsHelperKogitoImpl.getPMMLDocumentsAsync(metadataMock, imports);
        verify(kogitoResourceContentServiceMock, times(1)).getFilteredItems(eq(PMML_FILES_PATTERN), isA(ResourceListOptions.class));
        returnPromise.then(def -> {
            assertEquals(1, def.size());
            assertEquals(PMML_PATH, def.get(jsImportMock).getPath());
            assertEquals(PMML_FILE, def.get(jsImportMock).getName());
            assertEquals(DMNImportTypes.PMML.getDefaultNamespace(), def.get(jsImportMock).getImportType());
            assertEquals(0, def.get(jsImportMock).getModels().size());
            return promises.resolve();
        }).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });
    }

}
