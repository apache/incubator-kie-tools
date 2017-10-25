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

package org.guvnor.ala.pipeline.execution;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.Stage;

import static org.guvnor.ala.pipeline.execution.PipelineExecutor.PIPELINE_EXECUTION_ID;

/*
 * Represent the contextual information used by the Pipeline Executor. 
 * it host all the variables that are going to be used to execute the different stages in the
 *  pipeline. 
*/

public class PipelineContext {

    private String executionId;
    private final Iterator<Stage> iterator;
    private final Pipeline pipeline;
    private Optional<Object> lastOutput = Optional.empty();
    private Optional<Stage<Object, ?>> currentStage = Optional.empty();
    private Map<String, Object> values = new HashMap<>();

    private final Deque<Consumer<?>> callbacks = new LinkedList<>();

    Optional<Object> pollOutput() {
        return lastOutput;
    }

    PipelineContext(final Pipeline pipeline) {
        this.pipeline = pipeline;
        this.iterator = pipeline.getStages().iterator();
    }

    public String getExecutionId() {
        return executionId;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    void pushOutput(final String id,
                    final Object value) {
        lastOutput = Optional.of(value);
        this.values.put(id,
                        value);
        if (iterator.hasNext()) {
            currentStage = Optional.of(iterator.next());
        } else {
            currentStage = Optional.empty();
        }
    }

    void start(final Object initialInput) {
        if (isStarted()) {
            throw new RuntimeException("Process has already been started.");
        }

        this.values.put("input",
                        initialInput);
        if (initialInput instanceof Input) {
            executionId = ((Input) initialInput).computeIfAbsent(PIPELINE_EXECUTION_ID,
                                                                 generator -> ExecutionIdGenerator.generateExecutionId());
        } else {
            executionId = ExecutionIdGenerator.generateExecutionId();
        }
        if (iterator.hasNext()) {
            currentStage = Optional.of(iterator.next());
        } else {
            currentStage = Optional.empty();
        }
        lastOutput = Optional.of(initialInput);
    }

    boolean isStarted() {
        return currentStage.isPresent() || lastOutput.isPresent();
    }

    boolean isFinished() {
        return !currentStage.isPresent() && lastOutput.isPresent();
    }

    Optional<Stage<Object, ?>> getCurrentStage() {
        return currentStage;
    }

    void pushCallback(final Consumer<?> callback) {
        callbacks.push(callback);
    }

    boolean hasCallbacks() {
        return !callbacks.isEmpty();
    }

    void applyCallbackAndPop(final Object value) {
        final Consumer callback = callbacks.peek();
        callback.accept(value);
        callbacks.pop();
    }

    Pipeline getPipeline() {
        return pipeline;
    }

    Map<String, Object> getValues() {
        return values;
    }
}