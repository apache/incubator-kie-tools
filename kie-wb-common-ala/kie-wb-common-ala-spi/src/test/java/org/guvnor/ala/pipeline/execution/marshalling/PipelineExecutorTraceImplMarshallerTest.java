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

package org.guvnor.ala.pipeline.execution.marshalling;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.marshalling.BaseMarshallerTest;
import org.guvnor.ala.marshalling.Marshaller;
import org.guvnor.ala.pipeline.impl.BasePipeline;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.execution.PipelineExecutorError;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTask;
import org.guvnor.ala.pipeline.execution.RegistrableOutput;
import org.guvnor.ala.pipeline.execution.impl.PipelineExecutorTaskDefImpl;
import org.guvnor.ala.pipeline.execution.impl.PipelineExecutorTaskImpl;
import org.guvnor.ala.pipeline.execution.impl.PipelineExecutorTraceImpl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PipelineExecutorTraceImplMarshallerTest
        extends BaseMarshallerTest<PipelineExecutorTraceImpl> {

    private static final int STAGE_COUNT = 10;
    private static final int PIPELINE_INPUT_SIZE = 5;
    private static final String PIPELINE_NAME = "PIPELINE_NAME";
    private static final String PIPELINE_EXECUTION_ID = "PIPELINE_EXECUTION_ID";
    private static final String PIPELINE_ERROR = "PIPELINE_ERROR";
    private static final String PIPELINE_ERROR_DETAIL = "PIPELINE_ERROR_DETAIL";
    private static final String STAGE_ERROR = "STAGE_ERROR";
    private static final String STAGE_ERROR_DETAIL = "STAGE_ERROR_DETAIL";
    private static final String PIPELINE_OUTPUT = "PIPELINE_OUTPUT";

    @Override
    public Marshaller<PipelineExecutorTraceImpl> createMarshaller() {
        return new PipelineExecutorTraceImplMarshaller();
    }

    @Override
    public Class<PipelineExecutorTraceImpl> getType() {
        return PipelineExecutorTraceImpl.class;
    }

    @Override
    public PipelineExecutorTraceImpl getValue() {
        //only the stage names are used by the taskDef, so the pipeline can be mocked.
        List<Stage> stages = mockStages(STAGE_COUNT);
        Pipeline pipeline = new BasePipeline(PIPELINE_NAME,
                                             stages) {
        };
        Input input = mockInput(PIPELINE_INPUT_SIZE);

        PipelineExecutorTaskDefImpl taskDef = new PipelineExecutorTaskDefImpl(pipeline,
                                                                              input);

        PipelineExecutorTaskImpl taskImpl = new PipelineExecutorTaskImpl(taskDef,
                                                                         PIPELINE_EXECUTION_ID);
        taskImpl.setPipelineStatus(PipelineExecutorTask.Status.SCHEDULED);
        taskImpl.setPipelineError(mockError(PIPELINE_ERROR,
                                            PIPELINE_ERROR_DETAIL));
        taskImpl.getTaskDef().getStages().forEach(stage -> taskImpl.setStageError(stage,
                                                                                  mockStageError(stage)));
        taskImpl.setOutput(new MockPipelineOutput(PIPELINE_OUTPUT));
        return new PipelineExecutorTraceImpl(taskImpl);
    }

    @Override
    public void testUnMarshall() throws Exception {
        PipelineExecutorTraceImpl result = marshaller.unmarshal(getMarshalledValue());
        PipelineExecutorTraceImpl expectedValue = getValue();
        assertEqualsPipelineExecutorTrace(expectedValue,
                                          result);
    }

    private void assertEqualsPipelineExecutorTrace(PipelineExecutorTraceImpl expectedValue,
                                                   PipelineExecutorTraceImpl value) {
        assertEquals(expectedValue.getTaskId(),
                     value.getTaskId());
        assertEquals(expectedValue.getPipelineId(),
                     value.getPipelineId());
        assertEquals(expectedValue.getTask().getTaskDef(),
                     value.getTask().getTaskDef());
        assertEquals(expectedValue.getTask().getPipelineStatus(),
                     value.getTask().getPipelineStatus());
        assertEquals(expectedValue.getTask().getPipelineError(),
                     value.getTask().getPipelineError());
        for (String stage : expectedValue.getTask().getTaskDef().getStages()) {
            assertEquals(expectedValue.getTask().getStageStatus(stage),
                         value.getTask().getStageStatus(stage));
            assertEquals(expectedValue.getTask().getStageError(stage),
                         value.getTask().getStageError(stage));
        }
        assertEquals(expectedValue.getTask().getOutput(),
                     value.getTask().getOutput());
    }

    private List<Stage> mockStages(int count) {
        List<Stage> stages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Stage stage = mock(Stage.class);
            when(stage.getName()).thenReturn("Stage.name." + Integer.toString(i));
            stages.add(stage);
        }
        return stages;
    }

    private Input mockInput(int count) {
        Input input = new Input();
        for (int i = 0; i < count; i++) {
            input.put("key." + Integer.toString(i),
                      "value." + Integer.toString(i));
        }
        return input;
    }

    private PipelineExecutorError mockStageError(String stage) {
        return mockError(buildStageErrorMessage(stage),
                         buildStageErrorDetail(stage));
    }

    private PipelineExecutorError mockError(String error,
                                            String detail) {
        return new PipelineExecutorError(error,
                                         detail);
    }

    private String buildStageErrorMessage(String stage) {
        return stage + "." + STAGE_ERROR;
    }

    private String buildStageErrorDetail(String stage) {
        return stage + "." + STAGE_ERROR_DETAIL;
    }

    public static class MockPipelineOutput
            implements RegistrableOutput {

        private String outputValue;

        public MockPipelineOutput() {
            //required for marshalling purposes.
        }

        public MockPipelineOutput(String outputValue) {
            this.outputValue = outputValue;
        }

        public String getOutputValue() {
            return outputValue;
        }

        public void setOutputValue(String outputValue) {
            this.outputValue = outputValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            MockPipelineOutput that = (MockPipelineOutput) o;

            return outputValue != null ? outputValue.equals(that.outputValue) : that.outputValue == null;
        }

        @Override
        public int hashCode() {
            return outputValue != null ? outputValue.hashCode() : 0;
        }
    }
}