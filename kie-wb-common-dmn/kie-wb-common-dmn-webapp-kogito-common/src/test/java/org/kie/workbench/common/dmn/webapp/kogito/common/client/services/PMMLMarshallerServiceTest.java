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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.Collections;

import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.PMMLEditorMarshallerApi;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.model.PMMLDocumentData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PMMLDocumentData.class)
public class PMMLMarshallerServiceTest {

    private static final String CONTENT = "<xml>content</xml>";
    private static final String FILENAME = "fileName.pmml";
    private static final String PATH = "test/" + FILENAME;
    private static final String UNDEFINED = "undefined";

    @Mock
    private PMMLEditorMarshallerApi pmmlEditorMarshallerApiMock;

    private Promises promises;
    private PMMLMarshallerService pmmlMarshallerService;

    @Before
    public void setup() {
        promises = new SyncPromises();
        pmmlMarshallerService = new PMMLMarshallerService(promises, pmmlEditorMarshallerApiMock);
    }

    @Test
    public void getDocumentMetadata() {
        final PMMLDocumentData pmmlDocumentData = PowerMockito.mock(PMMLDocumentData.class);
        PowerMockito.when(pmmlDocumentData.getModels()).thenReturn(Collections.emptyList());
        when(pmmlEditorMarshallerApiMock.getPMMLDocumentData(CONTENT)).thenReturn(pmmlDocumentData);
        Promise<PMMLDocumentMetadata> returnPromise = pmmlMarshallerService.getDocumentMetadata(PATH, CONTENT);
        assertNotNull(returnPromise);
        returnPromise.then(pmmlDocumentMetadata -> {
            assertNotNull(pmmlDocumentMetadata);
            assertEquals("test/fileName.pmml", pmmlDocumentMetadata.getPath());
            assertEquals(UNDEFINED, pmmlDocumentMetadata.getName());
            assertEquals(DMNImportTypes.PMML.getDefaultNamespace(), pmmlDocumentMetadata.getImportType());
            assertEquals(0, pmmlDocumentMetadata.getModels().size());
            return promises.resolve();
        }).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });
    }

    @Test
    public void getDocumentMetadataNullFile() {
        getDocumentMetadataInvalidContent(null, CONTENT, "PMML file required to be marshalled is empty or null");
    }

    @Test
    public void getDocumentMetadataEmptyFile() {
        getDocumentMetadataInvalidContent("", CONTENT, "PMML file required to be marshalled is empty or null");
    }

    @Test
    public void getDocumentMetadataNullContent() {
        getDocumentMetadataInvalidContent(PATH, null, "PMML file " + PATH + " content required to be marshalled is empty or null");
    }

    @Test
    public void getDocumentMetadataEmptyContent() {
        getDocumentMetadataInvalidContent(PATH, "", "PMML file " + PATH + " content required to be marshalled is empty or null");
    }

    public void getDocumentMetadataInvalidContent(String pmmlFile, String pmmlFileContent, String expectedMessage) {
        Promise<PMMLDocumentMetadata> returnPromise = pmmlMarshallerService.getDocumentMetadata(pmmlFile, pmmlFileContent);
        assertNotNull(returnPromise);
        returnPromise.then(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        }).catch_(error -> {
            assertEquals(expectedMessage, error);
            return promises.resolve();
        });
    }
}
