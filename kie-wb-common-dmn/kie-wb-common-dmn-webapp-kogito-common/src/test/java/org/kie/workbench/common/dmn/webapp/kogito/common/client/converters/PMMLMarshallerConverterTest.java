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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.appformer.kogito.bridge.client.pmmleditor.marshaller.model.PMMLDocumentData;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.model.PMMLModelData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PMMLDocumentData.class, PMMLModelData.class})
public class PMMLMarshallerConverterTest {

    private static final String FILENAME = "fileName.pmml";
    private static final String PATH = "test/" + FILENAME;
    private static final String UNDEFINED = "undefined";

    @Test
    public void fromJSInteropToMetadata_EmptyModels() {
        final PMMLDocumentData pmmlDocumentDataMock = PowerMockito.mock(PMMLDocumentData.class);
        PowerMockito.when(pmmlDocumentDataMock.getModels()).thenReturn(Collections.emptyList());
        final PMMLDocumentMetadata metadata = PMMLMarshallerConverter.fromJSInteropToMetadata(PATH, pmmlDocumentDataMock);
        assertEquals(PATH, metadata.getPath());
        assertEquals(UNDEFINED, metadata.getName());
        assertEquals(0, metadata.getModels().size());
    }

    @Test
    public void fromJSInteropToMetadata_WithModels() {
        final String modelName = "LinearRegression";
        final String[] fieldsNames = {"field1", "field2"};
        final List<String> fields = Arrays.asList(fieldsNames);
        final List<PMMLModelData> modelsData = new ArrayList<>();
        final PMMLModelData pmmlModelDataMock = PowerMockito.mock(PMMLModelData.class);
        modelsData.add(pmmlModelDataMock);
        PowerMockito.when(pmmlModelDataMock.getModelName()).thenReturn(modelName);
        PowerMockito.when(pmmlModelDataMock.getFields()).thenReturn(fields);
        PMMLDocumentData pmmlDocumentDataMock = PowerMockito.mock(PMMLDocumentData.class);
        PowerMockito.when(pmmlDocumentDataMock.getModels()).thenReturn(modelsData);
        PMMLDocumentMetadata metadata = PMMLMarshallerConverter.fromJSInteropToMetadata(PATH, pmmlDocumentDataMock);
        assertEquals(PATH, metadata.getPath());
        assertEquals(UNDEFINED, metadata.getName());
        assertEquals(1, metadata.getModels().size());
        assertEquals(modelName, metadata.getModels().get(0).getName());
        assertEquals(fieldsNames.length, metadata.getModels().get(0).getInputParameters().size());
   }
}
