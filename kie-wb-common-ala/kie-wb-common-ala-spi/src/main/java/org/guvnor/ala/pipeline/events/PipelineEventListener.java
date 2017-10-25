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

package org.guvnor.ala.pipeline.events;

/*
 * Pipeline Event Listener covering all the pipeline stages and errors.
 * You can provide and attach different implementations to use as hook points
 *  to trigger custom code. 
*/
public interface PipelineEventListener {

    /*
     * Event emmited before executing the pipeline
    */
    void beforePipelineExecution(final BeforePipelineExecutionEvent bpee);

    /*
     * Event emmited after the pipeline execution finish
    */
    void afterPipelineExecution(final AfterPipelineExecutionEvent apee);

    /*
     * Event emmited before each pipeline stage execution
    */
    void beforeStageExecution(final BeforeStageExecutionEvent bsee);

    /*
     * Event emmited on an error inside a pipeline stage
    */
    void onStageError(final OnErrorStageExecutionEvent oesee);

    /*
     * Event emmited after each pipeline stage execution
    */
    void afterStageExecution(final AfterStageExecutionEvent asee);

    /*
     * Event emmited on an error inside the pipeline
    */
    void onPipelineError(final OnErrorPipelineExecutionEvent oepee);
}