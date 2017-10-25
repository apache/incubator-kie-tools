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

package org.guvnor.ala.registry.inmemory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.ala.pipeline.execution.PipelineExecutorTrace;
import org.guvnor.ala.registry.PipelineExecutorRegistry;
import org.guvnor.ala.runtime.RuntimeId;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class InMemoryPipelineExecutorRegistry
        implements PipelineExecutorRegistry {

    protected Map<String, PipelineExecutorTrace> recordsMap = new ConcurrentHashMap<>();

    public InMemoryPipelineExecutorRegistry() {
        //Empty constructor for Weld proxying
    }

    @Override
    public void register(final PipelineExecutorTrace trace) {
        checkNotNull("trace",
                     trace);
        recordsMap.put(trace.getTaskId(),
                       trace);
    }

    public void deregister(final String pipelineExecutionId) {
        checkNotNull("pipelineExecutionId",
                     pipelineExecutionId);
        recordsMap.remove(pipelineExecutionId);
    }

    @Override
    public PipelineExecutorTrace getExecutorTrace(final String pipelineExecutionId) {
        return recordsMap.get(pipelineExecutionId);
    }

    @Override
    public Collection<PipelineExecutorTrace> getExecutorTraces() {
        return recordsMap.values();
    }

    @Override
    public PipelineExecutorTrace getExecutorTrace(final RuntimeId runtimeId) {
        checkNotNull("runtimeId",
                     runtimeId);
        return recordsMap.values()
                .stream()
                .filter(trace ->
                                (trace.getTask().getOutput() instanceof RuntimeId) &&
                                        runtimeId.getId().equals(((RuntimeId) trace.getTask().getOutput()).getId())
                ).findFirst().orElse(null);
    }
}