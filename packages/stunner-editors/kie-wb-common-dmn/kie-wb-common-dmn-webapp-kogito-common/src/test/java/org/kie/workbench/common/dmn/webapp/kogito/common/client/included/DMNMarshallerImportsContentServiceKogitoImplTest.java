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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.included;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.PMMLEditorMarshallerApi;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.model.PMMLDocumentData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.mockito.Mock;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.included.DMNMarshallerImportsContentServiceKogitoImpl.DMN_FILES_PATTERN;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.included.DMNMarshallerImportsContentServiceKogitoImpl.MODEL_FILES_PATTERN;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.included.DMNMarshallerImportsContentServiceKogitoImpl.PMML_FILES_PATTERN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNMarshallerImportsContentServiceKogitoImplTest {

    private static final String FILENAME = "fileName.pmml";

    private static final String PATH = "test/" + FILENAME;

    private static final String CONTENT = "<xml>content</xml>";

    private static final String UNDEFINED = "undefined";

    @Mock
    private KogitoResourceContentService contentService;

    @Mock
    private PMMLEditorMarshallerApi pmmlEditorMarshallerApi;

    private final Promises promises = new SyncPromises();

    private DMNMarshallerImportsContentServiceKogitoImpl service;

    @Before
    public void setup() {
        service = spy(new DMNMarshallerImportsContentServiceKogitoImpl(contentService, promises, pmmlEditorMarshallerApi));
    }

    @Test
    public void testLoadFile() {
        final String file = "file.dmn";
        final Promise<String> expected = makePromise();
        when(contentService.loadFile(file)).thenReturn(expected);
        final Promise<String> actual = service.loadFile(file);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetModelsURIs() {
        final Promise<String[]> expected = makePromise();
        when(contentService.getFilteredItems(eq(MODEL_FILES_PATTERN), any())).thenReturn(expected);
        final Promise<String[]> actual = service.getModelsURIs();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetModelsDMNFilesURIs() {
        final Promise<String[]> expected = makePromise();
        when(contentService.getFilteredItems(eq(DMN_FILES_PATTERN), any())).thenReturn(expected);
        final Promise<String[]> actual = service.getModelsDMNFilesURIs();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetModelsPMMLFilesURIs() {
        final Promise<String[]> expected = makePromise();
        when(contentService.getFilteredItems(eq(PMML_FILES_PATTERN), any())).thenReturn(expected);
        final Promise<String[]> actual = service.getModelsPMMLFilesURIs();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetDocumentMetadata() {

        when(pmmlEditorMarshallerApi.getPMMLDocumentData(CONTENT)).thenReturn(new PMMLDocumentData());
        doReturn(promises.resolve(CONTENT)).when(service).loadFile(PATH);

        final Promise<PMMLDocumentMetadata> returnPromise = service.getPMMLDocumentMetadata(PATH);

        assertNotNull(returnPromise);
        returnPromise.then(pmmlDocumentMetadata -> {
            assertNotNull(pmmlDocumentMetadata);
            assertEquals("test/fileName.pmml", pmmlDocumentMetadata.getPath());
            assertEquals(UNDEFINED, pmmlDocumentMetadata.getName());
            assertEquals(DMNImportTypes.PMML.getDefaultNamespace(), pmmlDocumentMetadata.getImportType());
            return promises.resolve();
        }).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });
    }

    @Test
    public void testGetDocumentMetadataNullFile() {
        getDocumentMetadataInvalidContent(null, CONTENT, "PMML file path cannot be empty or null");
    }

    @Test
    public void testGetDocumentMetadataEmptyFile() {
        getDocumentMetadataInvalidContent("", CONTENT, "PMML file path cannot be empty or null");
    }

    @Test
    public void testGetDocumentMetadataNullContent() {
        getDocumentMetadataInvalidContent(PATH, null, "PMML file " + PATH + " content required to be marshalled is empty or null");
    }

    @Test
    public void testGetDocumentMetadataEmptyContent() {
        getDocumentMetadataInvalidContent(PATH, "", "PMML file " + PATH + " content required to be marshalled is empty or null");
    }

    private void getDocumentMetadataInvalidContent(final String pmmlFile,
                                                   final String pmmlFileContent,
                                                   final String expectedMessage) {

        doReturn(promises.resolve(pmmlFileContent)).when(service).loadFile(pmmlFile);

        final Promise<PMMLDocumentMetadata> returnPromise = service.getPMMLDocumentMetadata(pmmlFile);

        assertNotNull(returnPromise);
        returnPromise.then(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        }).catch_(error -> {
            assertEquals(expectedMessage, error);
            return promises.resolve();
        });
    }

    private <T> Promise<T> makePromise() {
        return new Promise<>(null);
    }
}
