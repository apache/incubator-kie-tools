/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.backend;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.dashbuilder.backend.services.dataset.RuntimeCSVFileStorage;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.shared.event.NewDataSetContentEvent;
import org.dashbuilder.shared.event.RemovedRuntimeModelEvent;
import org.dashbuilder.shared.model.DataSetContent;
import org.dashbuilder.shared.model.DataSetContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataSetContentListenerTest {

    @Mock
    DataSetDefRegistry registry;

    @Mock
    RuntimeCSVFileStorage storage;

    @Mock
    DataSetDefJSONMarshaller defMarshaller;

    @Mock
    Map<String, List<String>> runtimeModelDatasetContents;

    @InjectMocks
    DataSetContentListener datasetContentListener;

    @Test
    public void testRegister() throws Exception {
        final String runtimeModelId = "TEST";
        final String C1 = "C1";
        final String C2 = "C2";
        final String content2 = "TESTCONTENT";
        final String content3 = "TESTCSV";
        
        List<DataSetContent> content = Arrays.asList(new DataSetContent(C1, content2, DataSetContentType.DEFINITION),
                                                     new DataSetContent(C2, content3, DataSetContentType.CSV));
        List<String> contentIds = Arrays.asList(C1, C2);
        DataSetDef def = mock(DataSetDef.class);
        when(defMarshaller.fromJson(matches(content2))).thenReturn(def);

        datasetContentListener.register(new NewDataSetContentEvent(runtimeModelId, content));

        verify(runtimeModelDatasetContents).put(runtimeModelId, contentIds);
        verify(storage).storeCSV(C2, content3);
        verify(def).setUUID(matches(C1));
        verify(registry).registerDataSetDef(def);
    }
    
    @Test
    public void testUnregister() {
        final String id = "ID";
        final String C1 = "C1";
        
        List<String> registeredContent = Arrays.asList(C1);
        when(runtimeModelDatasetContents.remove(matches(id))).thenReturn(registeredContent);
        
        datasetContentListener.unregister(new RemovedRuntimeModelEvent(id));
        
        verify(storage).deleteCSVFile(matches(C1));
        verify(registry).removeDataSetDef(matches(C1));
    }

}