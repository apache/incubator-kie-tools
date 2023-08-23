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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.appformer.kogito.bridge.client.pmmleditor.marshaller.model.PMMLDocumentData;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.model.PMMLFieldData;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.model.PMMLModelData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLParameterMetadata;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PMMLDocumentData.class, PMMLModelData.class, PMMLFieldData.class})
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
        final List<PMMLModelData> modelsData = new ArrayList<>();
        final PMMLFieldData fieldData1Mock = PowerMockito.mock(PMMLFieldData.class);
        final PMMLFieldData fieldData2Mock = PowerMockito.mock(PMMLFieldData.class);
        PowerMockito.when(fieldData1Mock.getFieldName()).thenReturn("field1");
        PowerMockito.when(fieldData1Mock.getUsageType()).thenReturn(PMMLMarshallerConverter.ACTIVE);
        PowerMockito.when(fieldData2Mock.getFieldName()).thenReturn("field2");
        PowerMockito.when(fieldData2Mock.getUsageType()).thenReturn(PMMLMarshallerConverter.ACTIVE);
        final List<PMMLFieldData> fieldsData = Arrays.asList(fieldData1Mock, fieldData2Mock);
        final PMMLModelData pmmlModelDataMock = PowerMockito.mock(PMMLModelData.class);
        modelsData.add(pmmlModelDataMock);
        PowerMockito.when(pmmlModelDataMock.getModelName()).thenReturn(modelName);
        PowerMockito.when(pmmlModelDataMock.getFields()).thenReturn(fieldsData);
        PMMLDocumentData pmmlDocumentDataMock = PowerMockito.mock(PMMLDocumentData.class);
        PowerMockito.when(pmmlDocumentDataMock.getModels()).thenReturn(modelsData);
        PMMLDocumentMetadata metadata = PMMLMarshallerConverter.fromJSInteropToMetadata(PATH, pmmlDocumentDataMock);
        assertEquals(PATH, metadata.getPath());
        assertEquals(UNDEFINED, metadata.getName());
        assertEquals(1, metadata.getModels().size());
        assertEquals(modelName, metadata.getModels().get(0).getName());
        ArrayList<PMMLParameterMetadata> fields = new ArrayList<>(metadata.getModels().get(0).getInputParameters());
        Collections.sort(fields, Comparator.comparing(PMMLParameterMetadata::getName));
        assertEquals(2, fields.size());
        assertEquals("field1", fields.get(0).getName());
        assertEquals("field2", fields.get(1).getName());
    }

    @Test
    public void fromJSInteropToMetadata_WithModelsAndPredictedFields() {
        final String modelName = "LinearRegression";
        final List<PMMLModelData> modelsData = new ArrayList<>();
        final PMMLFieldData fieldData1Mock = PowerMockito.mock(PMMLFieldData.class);
        final PMMLFieldData fieldData2Mock = PowerMockito.mock(PMMLFieldData.class);
        PowerMockito.when(fieldData1Mock.getFieldName()).thenReturn("field1");
        PowerMockito.when(fieldData1Mock.getUsageType()).thenReturn("predictive");
        PowerMockito.when(fieldData2Mock.getFieldName()).thenReturn("field2");
        PowerMockito.when(fieldData2Mock.getUsageType()).thenReturn(PMMLMarshallerConverter.ACTIVE);
        final List<PMMLFieldData> fieldsData = Arrays.asList(fieldData1Mock, fieldData2Mock);
        final PMMLModelData pmmlModelDataMock = PowerMockito.mock(PMMLModelData.class);
        modelsData.add(pmmlModelDataMock);
        PowerMockito.when(pmmlModelDataMock.getModelName()).thenReturn(modelName);
        PowerMockito.when(pmmlModelDataMock.getFields()).thenReturn(fieldsData);
        PMMLDocumentData pmmlDocumentDataMock = PowerMockito.mock(PMMLDocumentData.class);
        PowerMockito.when(pmmlDocumentDataMock.getModels()).thenReturn(modelsData);
        PMMLDocumentMetadata metadata = PMMLMarshallerConverter.fromJSInteropToMetadata(PATH, pmmlDocumentDataMock);
        assertEquals(PATH, metadata.getPath());
        assertEquals(UNDEFINED, metadata.getName());
        assertEquals(1, metadata.getModels().size());
        assertEquals(modelName, metadata.getModels().get(0).getName());
        assertEquals(1, metadata.getModels().get(0).getInputParameters().size());
        ArrayList<PMMLParameterMetadata> fields = new ArrayList<>(metadata.getModels().get(0).getInputParameters());
        Collections.sort(fields, Comparator.comparing(PMMLParameterMetadata::getName));
        assertEquals(1, fields.size());
        assertEquals("field2", fields.get(0).getName());
    }
}
