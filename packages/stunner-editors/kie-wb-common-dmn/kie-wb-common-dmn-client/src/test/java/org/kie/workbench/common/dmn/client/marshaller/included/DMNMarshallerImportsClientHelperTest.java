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

package org.kie.workbench.common.dmn.client.marshaller.included;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.promise.Promise;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes.PMML;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNMarshallerImportsClientHelperTest {

    @Mock
    private Metadata metadataMock;

    @Mock
    private ServiceCallback<List<IncludedModel>> includedModelServiceCallback;

    @Mock
    private ServiceCallback<List<PMMLDocumentMetadata>> pmmlMetadataServiceCallback;

    @Mock
    private DMNMarshallerImportsService dmnImportsService;

    @Mock
    private DMNMarshallerImportsContentService dmnImportsContentService;

    @Mock
    private DMNIncludedNodeFactory includedModelFactory;

    @Captor
    private ArgumentCaptor<List<IncludedModel>> modelsCapture;

    @Captor
    private ArgumentCaptor<List<PMMLDocumentMetadata>> pmmlDocumentMetadataArgumentCaptor;

    private static final String DMN_FILE = "test-dmn.dmn";
    private static final String DMN_PATH = "dmntest/" + DMN_FILE;
    private static final String DMN_CONTENT = "<xml> xml DMN content </xml>";

    private static final String PMML_MODEL_NAME = "model-test";
    private static final String PMML_FILE = "test-pmml.pmml";
    private static final String PMML_PATH = "dmnpmml/" + PMML_FILE;
    private static final String PMML_CONTENT = "<xml> xml PMML content </xml>";

    private static final String TEXT_FILE = "test-file.txt";
    private static final String TEXT_PATH = "invalidfile/" + TEXT_FILE;

    private Promises promises;

    private DMNMarshallerImportsClientHelper importsHelper;

    @Before
    public void setup() {
        promises = new SyncPromises();
        importsHelper = new DMNMarshallerImportsClientHelper(dmnImportsService,
                dmnImportsContentService,
                promises,
                includedModelFactory);
    }

    @Test
    public void loadModelsDMNFile() {
        when(dmnImportsContentService.getModelsURIs()).thenReturn(promises.resolve(new String[]{DMN_PATH}));
        when(dmnImportsContentService.loadFile(DMN_PATH)).thenReturn(promises.resolve(DMN_CONTENT));

        importsHelper.loadModels(includedModelServiceCallback);

        verify(dmnImportsContentService).loadFile(eq(DMN_PATH));
    }

    @Test
    public void loadModelsPMMLFile() {

        final PMMLDocumentMetadata pmmlDocumentMetadata = new PMMLDocumentMetadata(PMML_PATH,
                PMML_FILE,
                PMML.getDefaultNamespace(),
                Collections.emptyList());

        when(dmnImportsContentService.getModelsURIs()).thenReturn(promises.resolve(new String[]{PMML_PATH}));
        when(dmnImportsContentService.loadFile(PMML_PATH)).thenReturn(promises.resolve(PMML_CONTENT));
        when(dmnImportsContentService.loadFile(PMML_PATH)).thenReturn(promises.resolve(PMML_CONTENT));
        doReturn(promises.resolve(pmmlDocumentMetadata)).when(dmnImportsContentService).getPMMLDocumentMetadata(PMML_PATH);

        importsHelper.loadModels(includedModelServiceCallback);

        verify(includedModelServiceCallback).onSuccess(modelsCapture.capture());
        assertEquals(1, modelsCapture.getValue().size());
        assertEquals(PMML_PATH, modelsCapture.getValue().get(0).getPath());
        assertEquals(PMML_FILE, modelsCapture.getValue().get(0).getModelName());
        assertEquals(PMML.getDefaultNamespace(), modelsCapture.getValue().get(0).getImportType());
        assertEquals(0, ((PMMLIncludedModel) modelsCapture.getValue().get(0)).getModelCount().intValue());
    }

    @Test
    public void loadModelsInvalidFile() {
        when(dmnImportsContentService.getModelsURIs()).thenReturn(promises.resolve(new String[]{TEXT_PATH}));

        importsHelper.loadModels(includedModelServiceCallback);

        verify(dmnImportsContentService, never()).loadFile(Mockito.<String>any());
        verify(dmnImportsContentService, never()).getPMMLDocumentMetadata(Mockito.<String>any());
        verify(dmnImportsService, never()).getWbDefinitions(Mockito.<String>any(), any());
    }

    @Test
    public void getPMMLDocumentsAsyncWhenModelDoesNotHaveImports() {
        final Promise<Map<JSITImport, PMMLDocumentMetadata>> returnPromise = importsHelper.getPMMLDocumentsAsync(metadataMock, Collections.emptyList());
        returnPromise.then(p0 -> {
            assertEquals(0, p0.size());
            return promises.resolve();
        }).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });
    }

    @Test
    public void getPMMLDocumentsAsyncWhenAnyFileCouldBeRead() {

        when(dmnImportsContentService.getModelsPMMLFilesURIs()).thenReturn(promises.resolve(new String[0]));

        final Promise<Map<JSITImport, PMMLDocumentMetadata>> returnPromise = importsHelper.getPMMLDocumentsAsync(metadataMock, singletonList(mock(JSITImport.class)));

        returnPromise.then(p0 -> {
            assertEquals(0, p0.size());
            return promises.resolve();
        }).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });
    }

    @Test
    public void getPMMLDocumentsAsync() {

        final PMMLDocumentMetadata pmmlDocumentMetadata = new PMMLDocumentMetadata(PMML_PATH,
                PMML_FILE,
                PMML.getDefaultNamespace(),
                Collections.emptyList());

        when(dmnImportsContentService.getModelsPMMLFilesURIs()).thenReturn(promises.resolve(new String[]{PMML_PATH}));
        when(dmnImportsContentService.loadFile(PMML_PATH)).thenReturn(promises.resolve(PMML_CONTENT));

        doReturn(promises.resolve(pmmlDocumentMetadata)).when(dmnImportsContentService).getPMMLDocumentMetadata(PMML_PATH);

        final List<JSITImport> imports = new ArrayList<>();
        final JSITImport jsImportMock = mock(JSITImport.class);
        when(jsImportMock.getLocationURI()).thenReturn(PMML_PATH);
        imports.add(jsImportMock);

        final Promise<Map<JSITImport, PMMLDocumentMetadata>> returnPromise = importsHelper.getPMMLDocumentsAsync(metadataMock, imports);

        returnPromise.then(def -> {
            assertEquals(1, def.size());
            assertEquals(PMML_PATH, def.get(jsImportMock).getPath());
            assertEquals(PMML_FILE, def.get(jsImportMock).getName());
            assertEquals(PMML.getDefaultNamespace(), def.get(jsImportMock).getImportType());
            assertEquals(0, def.get(jsImportMock).getModels().size());
            return promises.resolve();
        }).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });
    }

    @Test
    public void getPMMLDocumentsMetadataFromFilesEmptyFiles() {
        importsHelper.getPMMLDocumentsMetadataFromFiles(Collections.emptyList(), pmmlMetadataServiceCallback);
        importsHelper.getPMMLDocumentsMetadataFromFiles(null, pmmlMetadataServiceCallback);
        verify(pmmlMetadataServiceCallback, times(2)).onSuccess(eq(Collections.emptyList()));
    }

    @Test
    public void getPMMLDocumentsMetadataFromFiles() {
        final PMMLDocumentMetadata documentMetadata = new PMMLDocumentMetadata(PMML_PATH,
                PMML.getDefaultNamespace(),
                Collections.emptyList());
        final List<PMMLIncludedModel> includedModels = Arrays.asList(new PMMLIncludedModel(PMML_MODEL_NAME, "", PMML_PATH, PMML.getDefaultNamespace(), "https://kie.org/pmml#" + PMML_FILE, 0));
        when(dmnImportsContentService.getModelsPMMLFilesURIs()).thenReturn(promises.resolve(new String[]{PMML_PATH}));
        when(dmnImportsContentService.loadFile(PMML_PATH)).thenReturn(promises.resolve(PMML_CONTENT));
        doReturn(promises.resolve(documentMetadata)).when(dmnImportsContentService).getPMMLDocumentMetadata(PMML_PATH);
        importsHelper.getPMMLDocumentsMetadataFromFiles(includedModels, pmmlMetadataServiceCallback);
        verify(pmmlMetadataServiceCallback, times(1)).onSuccess(pmmlDocumentMetadataArgumentCaptor.capture());
        assertEquals(1, pmmlDocumentMetadataArgumentCaptor.getValue().size());
        assertEquals(PMML_PATH, pmmlDocumentMetadataArgumentCaptor.getValue().get(0).getPath());
        assertEquals(PMML_MODEL_NAME, pmmlDocumentMetadataArgumentCaptor.getValue().get(0).getName());
        assertEquals(PMML.getDefaultNamespace(), pmmlDocumentMetadataArgumentCaptor.getValue().get(0).getImportType());
        assertTrue(pmmlDocumentMetadataArgumentCaptor.getValue().get(0).getModels().isEmpty());
    }
}
