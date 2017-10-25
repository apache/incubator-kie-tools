/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.services.api.PipelineStageItem;
import org.guvnor.ala.services.api.RuntimeQueryResultItem;
import org.guvnor.ala.services.api.itemlist.PipelineStageItemList;
import org.guvnor.ala.ui.model.PipelineExecutionTrace;
import org.guvnor.ala.ui.model.PipelineStatus;
import org.guvnor.ala.ui.model.Runtime;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.guvnor.ala.ui.model.Stage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class RuntimeListItemBuilderTest {

    private static final int STAGE_ITEMS_COUNT = 7;

    private static final String RUNTIME_NAME = "RUNTIME_NAME";

    private static final String RUNTIME_ID = "RUNTIME_ID";

    private static final String PROVIDER_ID = "PROVIDER_ID";

    private static final String PROVIDER_NAME = "PROVIDER_NAME";

    private static final String PROVIDER_VERSION = "PROVIDER_VERSION";

    private static final String RUNTIME_ENDPOINT = "RUNTIME_ENDPOINT";

    private static final String RUNTIME_STARTED_AT = "RUNTIME_STARTED_AT";

    private static final String RUNTIME_STATUS = "RUNTIME_STATUS";

    private static final String PIPELINE_EXECUTION_ID = "PIPELINE_EXECUTION_ID";

    private static final String PIPELINE_ID = "PIPELINE_ID";

    private static final String PIPELINE_ERROR = "PIPELINE_ERROR";

    private static final String PIPELINE_ERROR_DETAIL = "PIPELINE_ERROR_DETAIL";

    private RuntimeQueryResultItem queryResultItem;

    private PipelineStageItemList stageItemList;

    @Before
    public void setUp() {
        queryResultItem = new RuntimeQueryResultItem();
    }

    @Test
    public void testBuildItemLabelForRuntime() {
        queryResultItem.setRuntimeName(RUNTIME_NAME);
        RuntimeListItem result = RuntimeListItemBuilder.newInstance().withItem(queryResultItem).build();
        assertEquals(RUNTIME_NAME,
                     result.getItemLabel());
    }

    @Test
    public void testBuildItemLabelForRuntimeName() {
        queryResultItem.setRuntimeName(RUNTIME_ID);
        RuntimeListItem result = RuntimeListItemBuilder.newInstance().withItem(queryResultItem).build();
        assertEquals(RUNTIME_ID,
                     result.getItemLabel());
    }

    @Test
    public void testBuildRuntimeWithNoPipelineExecution() {
        prepareRuntime();

        RuntimeListItem result = RuntimeListItemBuilder.newInstance().withItem(queryResultItem).build();

        assertTrue(result.isRuntime());
        assertNotNull(result.getRuntime());
        assertNull(result.getPipelineTrace());

        assertRuntime(result.getRuntime());
    }

    @Test
    public void testBuildRuntimeWithPipelineExecution() {
        prepareRuntime();
        preparePipelineTrace();

        RuntimeListItem result = RuntimeListItemBuilder.newInstance().withItem(queryResultItem).build();

        assertTrue(result.isRuntime());
        assertNotNull(result.getRuntime());
        assertNotNull(result.getRuntime().getPipelineTrace());
        assertNull(result.getPipelineTrace());

        assertRuntime(result.getRuntime());
        assertPipelineTrace(result.getRuntime().getPipelineTrace());
    }

    @Test
    public void testPipelineTraceBuild() {
        preparePipelineTrace();

        RuntimeListItem result = RuntimeListItemBuilder.newInstance().withItem(queryResultItem).build();

        assertFalse(result.isRuntime());
        assertNotNull(result.getPipelineTrace());
        assertNull(result.getRuntime());

        assertPipelineTrace(result.getPipelineTrace());
    }

    private void prepareRuntime() {
        queryResultItem.setRuntimeId(RUNTIME_ID);
        queryResultItem.setProviderTypeName(PROVIDER_NAME);
        queryResultItem.setProviderVersion(PROVIDER_VERSION);
        queryResultItem.setProviderId(PROVIDER_ID);
        queryResultItem.setRuntimeEndpoint(RUNTIME_ENDPOINT);
        queryResultItem.setStartedAt(RUNTIME_STARTED_AT);
        queryResultItem.setRuntimeStatus(RUNTIME_STATUS);
    }

    private void assertRuntime(Runtime runtime) {
        assertEquals(RUNTIME_ID,
                     runtime.getKey().getId());
        assertEquals(PROVIDER_NAME,
                     runtime.getKey().getProviderKey().getProviderTypeKey().getId());
        assertEquals(PROVIDER_VERSION,
                     runtime.getKey().getProviderKey().getProviderTypeKey().getVersion());
        assertEquals(PROVIDER_NAME,
                     runtime.getKey().getProviderKey().getProviderTypeKey().getId());
        assertEquals(PROVIDER_ID,
                     runtime.getKey().getProviderKey().getId());
        assertEquals(RUNTIME_ENDPOINT,
                     runtime.getEndpoint());
        assertEquals(RUNTIME_STARTED_AT,
                     runtime.getCreatedDate());
        assertEquals(RUNTIME_STATUS,
                     runtime.getStatus());
    }

    private void preparePipelineTrace() {
        queryResultItem.setPipelineId(PIPELINE_ID);
        queryResultItem.setPipelineExecutionId(PIPELINE_EXECUTION_ID);
        queryResultItem.setPipelineStatus(PipelineStatus.RUNNING.name());
        queryResultItem.setPipelineError(PIPELINE_ERROR);
        queryResultItem.setPipelineErrorDetail(PIPELINE_ERROR_DETAIL);
        stageItemList = mockPipelineStageItemList(STAGE_ITEMS_COUNT);
        queryResultItem.setPipelineStageItems(stageItemList);
    }

    private void assertPipelineTrace(PipelineExecutionTrace trace) {
        assertEquals(PIPELINE_ID,
                     trace.getPipeline().getKey().getId());
        assertEquals(PIPELINE_EXECUTION_ID,
                     trace.getKey().getId());
        assertEquals(PipelineStatus.RUNNING,
                     trace.getPipelineStatus());
        assertEquals(PIPELINE_ERROR,
                     trace.getPipelineError().getError());
        assertEquals(PIPELINE_ERROR_DETAIL,
                     trace.getPipelineError().getErrorDetail());

        assertEquals(stageItemList.getItems().size(),
                     trace.getPipeline().getStages().size());
        for (int i = 0; i < STAGE_ITEMS_COUNT; i++) {
            PipelineStageItem stageItem = stageItemList.getItems().get(i);
            Stage stage = trace.getPipeline().getStages().get(i);
            assertEquals(stageItem.getName(),
                         stage.getName());
            assertEquals(stageItem.getStatus(),
                         trace.getStageStatus(stage.getName()).name());
            assertEquals(stageItem.getStageError(),
                         trace.getStageError(stage.getName()).getError());
            assertEquals(stageItem.getStageErrorDetail(),
                         trace.getStageError(stage.getName()).getErrorDetail());
        }
    }

    public static PipelineStageItemList mockPipelineStageItemList(int count) {
        List<PipelineStageItem> stageItems = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            stageItems.add(mockStageItem(Integer.toString(i)));
        }
        return new PipelineStageItemList(stageItems);
    }

    public static PipelineStageItem mockStageItem(String suffix) {
        return new PipelineStageItem("PipelineStageItem.name." + suffix,
                                     "RUNNING",
                                     "PipelineStageItem.stageError." + suffix,
                                     "PipelineStageItem.stageErrorDetail." + suffix);
    }
}
