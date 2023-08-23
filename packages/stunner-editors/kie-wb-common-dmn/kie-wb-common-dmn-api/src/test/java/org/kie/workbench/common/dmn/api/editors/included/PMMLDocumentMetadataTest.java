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

package org.kie.workbench.common.dmn.api.editors.included;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PMMLDocumentMetadataTest {

    private static final String PATH = "path";

    private static final String MODEL_NAME = "modelName";

    private static final String IMPORT_TYPE = "importType";

    private static final List<PMMLModelMetadata> MODELS = Collections.singletonList(mock(PMMLModelMetadata.class));

    @Test
    public void testGettersWithThreeParameterConstructor() {
        final PMMLDocumentMetadata metadata = new PMMLDocumentMetadata(PATH,
                                                                       IMPORT_TYPE,
                                                                       MODELS);

        assertEquals(PATH, metadata.getPath());
        assertEquals(PMMLDocumentMetadata.UNDEFINED_MODEL_NAME, metadata.getName());
        assertEquals(IMPORT_TYPE, metadata.getImportType());
        assertEquals(MODELS, metadata.getModels());
    }

    @Test
    public void testGettersWithFourParameterConstructor() {
        final PMMLDocumentMetadata metadata = new PMMLDocumentMetadata(PATH,
                                                                       MODEL_NAME,
                                                                       IMPORT_TYPE,
                                                                       MODELS);

        assertEquals(PATH, metadata.getPath());
        assertEquals(MODEL_NAME, metadata.getName());
        assertEquals(IMPORT_TYPE, metadata.getImportType());
        assertEquals(MODELS, metadata.getModels());
    }
}
