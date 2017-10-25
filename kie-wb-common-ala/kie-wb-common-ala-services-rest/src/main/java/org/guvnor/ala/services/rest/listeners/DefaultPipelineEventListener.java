/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.services.rest.listeners;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.ala.pipeline.events.AfterPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.AfterStageExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforePipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforeStageExecutionEvent;
import org.guvnor.ala.pipeline.events.OnErrorPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.OnErrorStageExecutionEvent;
import org.guvnor.ala.pipeline.events.PipelineEventListener;

@ApplicationScoped
public class DefaultPipelineEventListener implements PipelineEventListener {

    @Override
    public void beforePipelineExecution(BeforePipelineExecutionEvent bpee) {

    }

    @Override
    public void afterPipelineExecution(AfterPipelineExecutionEvent apee) {

    }

    @Override
    public void beforeStageExecution(BeforeStageExecutionEvent bsee) {

    }

    @Override
    public void onStageError(OnErrorStageExecutionEvent bsee) {

    }

    @Override
    public void afterStageExecution(AfterStageExecutionEvent asee) {

    }

    @Override
    public void onPipelineError(OnErrorPipelineExecutionEvent apee) {

    }
}
