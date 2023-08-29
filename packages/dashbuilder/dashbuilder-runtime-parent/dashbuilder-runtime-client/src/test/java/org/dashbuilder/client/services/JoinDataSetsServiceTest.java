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

package org.dashbuilder.client.services;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.external.ExternalDataSetClientProvider;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.client.ClientDataSetManager;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.client.DataSetReadyCallbackAdapter;
import org.dashbuilder.dataset.client.ExternalDataSetParserProvider;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class JoinDataSetsServiceTest {

    private static final String C2_ID = "C2";
    private static final String C1_ID = "C1";
    private DataSet d2;
    private DataSet d1;

    private static final String DS1_UUID = "ds1";
    private static final String DS2_UUID = "ds2";

    private static final String RESULT_UUID = "result_uuid";

    ClientDataSetManager manager;

    @Mock
    ExternalDataSetClientProvider externalDataSetClientProvider;

    @Mock
    ExternalDataSetParserProvider parserProvider;

    @InjectMocks
    private JoinDataSetsService joinService;

    @Captor
    ArgumentCaptor<DataSetReadyCallback> ds1DatasetCallbackCaptor;

    @Captor
    ArgumentCaptor<DataSetReadyCallback> ds2DatasetCallbackCaptor;

    @Captor
    ArgumentCaptor<DataSetReadyCallback> topDatasetCallbackCaptor;
    private ExternalDataSetDef def;

    @Before
    public void init() {

        def = new ExternalDataSetDef();
        def.setJoin(List.of(DS1_UUID, DS2_UUID));
        def.setUUID(RESULT_UUID);

        d1 = DataSetFactory.newDataSetBuilder()
                .uuid(DS1_UUID)
                .label(C1_ID)
                .label(C2_ID)
                .row("D1_C1_R1", "D1_C2_R1")
                .row("D1_C1_R2", "D1_C2_R2")
                .buildDataSet();

        d2 = DataSetFactory.newDataSetBuilder()
                .uuid(DS2_UUID)
                .label(C1_ID)
                .label(C2_ID)
                .row("D2_C1_R1", "D2_C2_R1")
                .row("D2_C1_R2", "D2_C2_R2")
                .buildDataSet();

        manager = new ClientDataSetManager();
        joinService.manager = manager;
    }

    @Test
    public void testJoin() {
        var result = DataSetFactory.newEmptyDataSet();
        result.setDefinition(DataSetDefFactory.newExternalDataSetDef().buildDef());
        joinService.join(result, d1);
        joinService.join(result, d2);
        verifyDataSet(result);
    }

    @Test
    public void testJoinSingleDataSet() {
        var result = DataSetFactory.newEmptyDataSet();
        result.setDefinition(DataSetDefFactory.newExternalDataSetDef().buildDef());
        joinService.join(result, d1);
        assertEquals(List.of("D1_C1_R1", "D1_C1_R2"),
                result.getColumnById(C1_ID).getValues());
        assertEquals(List.of("D1_C2_R1", "D1_C2_R2"),
                result.getColumnById(C2_ID).getValues());
        assertEquals(List.of("ds1", "ds1"),
                result.getColumnById(JoinDataSetsService.DATASET_COLUMN).getValues());
        assertEquals(2, result.getRowCount());
    }

    @Test
    public void testJoinDatasets() {

        var lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(RESULT_UUID)
                .column(C1_ID)
                .column(C2_ID)
                .column(JoinDataSetsService.DATASET_COLUMN)
                .buildLookup();

        joinService.joinDataSets(def, lookup, new DataSetReadyCallbackAdapter() {

            @Override
            public void callback(DataSet result) {
                verifyDataSet(result);
            }
        });

        verify(externalDataSetClientProvider).fetchAndRegister(eq(DS1_UUID), any(), ds1DatasetCallbackCaptor.capture());

        verify(externalDataSetClientProvider).fetchAndRegister(eq(DS2_UUID), any(), ds2DatasetCallbackCaptor.capture());

        ds1DatasetCallbackCaptor.getValue().callback(d1);
        ds2DatasetCallbackCaptor.getValue().callback(d2);
    }

    @Test
    public void testJoinDatasetsKeepOrder() {
        var lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(RESULT_UUID)
                .column(C1_ID)
                .column(C2_ID)
                .column(JoinDataSetsService.DATASET_COLUMN)
                .buildLookup();

        def.setJoin(List.of(DS2_UUID, DS1_UUID));

        joinService.joinDataSets(def, lookup, new DataSetReadyCallbackAdapter() {

            @Override
            public void callback(DataSet result) {
                assertEquals(List.of("ds2", "ds2", "ds1", "ds1"),
                        result.getColumnById(JoinDataSetsService.DATASET_COLUMN).getValues());
            }

        });
        verify(externalDataSetClientProvider).fetchAndRegister(eq(DS1_UUID), any(), ds1DatasetCallbackCaptor.capture());
        verify(externalDataSetClientProvider).fetchAndRegister(eq(DS2_UUID), any(), ds2DatasetCallbackCaptor.capture());

        ds1DatasetCallbackCaptor.getValue().callback(d1);
        ds2DatasetCallbackCaptor.getValue().callback(d2);
    }

    @Test
    public void testJoinDatasetsIgnoringEmpty() {
        var lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(RESULT_UUID)
                .column(C1_ID)
                .column(C2_ID)
                .column(JoinDataSetsService.DATASET_COLUMN)
                .buildLookup();

        joinService.joinDataSets(def, lookup, new DataSetReadyCallbackAdapter() {

            @Override
            public void callback(DataSet result) {
                verifyDataSetD1(result);
            }

        });
        verify(externalDataSetClientProvider).fetchAndRegister(eq(DS1_UUID), any(), ds1DatasetCallbackCaptor.capture());
        verify(externalDataSetClientProvider).fetchAndRegister(eq(DS2_UUID), any(), ds2DatasetCallbackCaptor.capture());

        ds1DatasetCallbackCaptor.getValue().callback(d1);
        ds2DatasetCallbackCaptor.getValue().callback(DataSetFactory.newEmptyDataSet());
    }

    @Test
    public void testJoinDatasetsNotFound() {
        var lookup = DataSetLookupFactory.newDataSetLookupBuilder().buildLookup();
        var datasetReadyCallback = mock(DataSetReadyCallback.class);
        joinService.joinDataSets(def, lookup, datasetReadyCallback);

        verify(externalDataSetClientProvider).fetchAndRegister(Mockito.eq(DS1_UUID), any(),
                ds1DatasetCallbackCaptor.capture());

        verify(externalDataSetClientProvider).fetchAndRegister(Mockito.eq(DS2_UUID), any(),
                ds2DatasetCallbackCaptor.capture());

        ds1DatasetCallbackCaptor.getValue().callback(d1);
        ds2DatasetCallbackCaptor.getValue().notFound();

        verify(datasetReadyCallback, times(1)).onError(any());
        verify(datasetReadyCallback, times(0)).callback(any());
    }

    @Test
    public void testJoinDatasetsError() {
        var lookup = DataSetLookupFactory.newDataSetLookupBuilder().buildLookup();
        var datasetReadyCallback = mock(DataSetReadyCallback.class);
        joinService.joinDataSets(def, lookup, datasetReadyCallback);

        verify(externalDataSetClientProvider).fetchAndRegister(Mockito.eq(DS1_UUID), any(),
                ds1DatasetCallbackCaptor.capture());

        verify(externalDataSetClientProvider).fetchAndRegister(Mockito.eq(DS2_UUID), any(),
                ds2DatasetCallbackCaptor.capture());

        ds1DatasetCallbackCaptor.getValue().callback(d1);
        ds2DatasetCallbackCaptor.getValue().onError(new ClientRuntimeError("No Message"));

        verify(datasetReadyCallback, times(1)).onError(any());
        verify(datasetReadyCallback, times(0)).callback(any());
    }

    private void verifyDataSetD1(DataSet result) {
        assertEquals(List.of("D1_C1_R1", "D1_C1_R2"),
                result.getColumnById(C1_ID).getValues());
        assertEquals(List.of("D1_C2_R1", "D1_C2_R2"),
                result.getColumnById(C2_ID).getValues());
        assertEquals(List.of("ds1", "ds1"),
                result.getColumnById(JoinDataSetsService.DATASET_COLUMN).getValues());
        assertEquals(2, result.getRowCount());
    }

    private void verifyDataSet(DataSet result) {
        assertEquals(List.of("D1_C1_R1", "D1_C1_R2", "D2_C1_R1", "D2_C1_R2"),
                result.getColumnById(C1_ID).getValues());
        assertEquals(List.of("D1_C2_R1", "D1_C2_R2", "D2_C2_R1", "D2_C2_R2"),
                result.getColumnById(C2_ID).getValues());
        assertEquals(List.of("ds1", "ds1", "ds2", "ds2"),
                result.getColumnById(JoinDataSetsService.DATASET_COLUMN).getValues());
        assertEquals(4, result.getRowCount());
    }
}
