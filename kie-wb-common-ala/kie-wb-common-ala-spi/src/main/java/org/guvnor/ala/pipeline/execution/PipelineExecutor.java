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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;
import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.ContextAware;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.events.AfterPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.AfterStageExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforePipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforeStageExecutionEvent;
import org.guvnor.ala.pipeline.events.OnErrorPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.OnErrorStageExecutionEvent;
import org.guvnor.ala.pipeline.events.PipelineEventListener;

import static org.guvnor.ala.util.VariableInterpolation.interpolate;

/*
 * Represent the Pipeline Executor which will be in charge of executing a pipeline instance 
 *  by using the Input data provided. After executing the pipeline a Consumer callback will be executed.
*/
public class PipelineExecutor {

    public static final String PIPELINE_EXECUTION_ID = "_pipelineExecutionId_";

    private final Map<Class, ConfigExecutor> configExecutors = new HashMap<>();

    public PipelineExecutor() {
    }

    public void init(final Collection<ConfigExecutor> configExecutors) {
        for (final ConfigExecutor configExecutor : configExecutors) {
            this.configExecutors.put(configExecutor.executeFor(),
                                     configExecutor);
        }
    }

    public PipelineExecutor(final Collection<ConfigExecutor> configExecutors) {
        init(configExecutors);
    }

    public <T> void execute(final Input input,
                            final Pipeline pipeline,
                            final Consumer<T> callback,
                            final PipelineEventListener... eventListeners) {
        final PipelineContext context = new PipelineContext(pipeline);
        context.start(input);
        context.pushCallback(callback);
        propagateEvent(new BeforePipelineExecutionEvent(context.getExecutionId(),
                                                        pipeline),
                       eventListeners);
        continuePipeline(context,
                         eventListeners);
        propagateEvent(new AfterPipelineExecutionEvent(context.getExecutionId(),
                                                       pipeline),
                       eventListeners);
    }

    private void continuePipeline(final PipelineContext context,
                                  final PipelineEventListener... eventListeners) {
        while (!context.isFinished()) {
            final Stage<Object, ?> stage = getCurrentStage(context);
            final Object newInput = pollOutput(context);

            try {
                propagateEvent(new BeforeStageExecutionEvent(context.getExecutionId(),
                                                             context.getPipeline(),
                                                             stage),
                               eventListeners);
                stage.execute(newInput,
                              output -> {

                                  final ConfigExecutor executor = resolve(output.getClass());
                                  if (output instanceof ContextAware) {
                                      ((ContextAware) output).setContext(Collections.unmodifiableMap(context.getValues()));
                                  }
                                  final Object newOutput = interpolate(context.getValues(),
                                                                       output);
                                  if (executor == null) {
                                      throw new RuntimeException("Fail to resolve ConfigExecutor for: " + output.getClass());
                                  }
                                  context.getValues().put(executor.inputId(),
                                                          newOutput);
                                  if (executor instanceof BiFunctionConfigExecutor) {
                                      final Optional result = (Optional) ((BiFunctionConfigExecutor) executor).apply(newInput,
                                                                                                                     newOutput);
                                      context.pushOutput(executor.outputId(),
                                                         result.get());
                                  } else if (executor instanceof FunctionConfigExecutor) {
                                      final Optional result = (Optional) ((FunctionConfigExecutor) executor).apply(newOutput);
                                      context.pushOutput(executor.outputId(),
                                                         result.get());
                                  }

                                  propagateEvent(new AfterStageExecutionEvent(context.getExecutionId(),
                                                                              context.getPipeline(),
                                                                              stage),
                                                 eventListeners);
                              });
            } catch (final Throwable t) {
                t.printStackTrace();
                final RuntimeException exception = new RuntimeException("An error occurred while executing the " + (stage == null ? "null" : stage.getName()) + " stage.",
                                                                        t);
                propagateEvent(new OnErrorStageExecutionEvent(context.getExecutionId(),
                                                              context.getPipeline(),
                                                              stage,
                                                              exception),
                               eventListeners);
                propagateEvent(new OnErrorPipelineExecutionEvent(context.getExecutionId(),
                                                                 context.getPipeline(),
                                                                 stage,
                                                                 exception),
                               eventListeners);
                throw exception;
            }
        }
        final Object output = pollOutput(context);
        while (context.hasCallbacks()) {
            context.applyCallbackAndPop(output);
        }
    }

    private ConfigExecutor resolve(final Class<?> clazz) {
        final ConfigExecutor result = configExecutors.get(clazz);
        if (result != null) {
            return result;
        }
        for (final Map.Entry<Class, ConfigExecutor> entry : configExecutors.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static Object pollOutput(final PipelineContext context) {
        return context.pollOutput()
                .orElseThrow(() -> new IllegalStateException("The " + PipelineContext.class.getSimpleName() + " was polled with no previous output."));
    }

    private static Stage<Object, ?> getCurrentStage(final PipelineContext context) {
        return context
                .getCurrentStage()
                .orElseThrow(() -> new IllegalStateException("There was not current stage even though the process has not finished."));
    }

    private void propagateEvent(final BeforePipelineExecutionEvent beforePipelineExecutionEvent,
                                final PipelineEventListener... eventListeners) {
        for (final PipelineEventListener eventListener : eventListeners) {
            eventListener.beforePipelineExecution(beforePipelineExecutionEvent);
        }
    }

    private void propagateEvent(final BeforeStageExecutionEvent beforeStageExecutionEvent,
                                final PipelineEventListener... eventListeners) {
        for (final PipelineEventListener eventListener : eventListeners) {
            eventListener.beforeStageExecution(beforeStageExecutionEvent);
        }
    }

    private void propagateEvent(final AfterStageExecutionEvent afterStageExecutionEvent,
                                final PipelineEventListener... eventListeners) {
        for (final PipelineEventListener eventListener : eventListeners) {
            eventListener.afterStageExecution(afterStageExecutionEvent);
        }
    }

    private void propagateEvent(final OnErrorStageExecutionEvent onErrorStageExecutionEvent,
                                final PipelineEventListener... eventListeners) {
        for (final PipelineEventListener eventListener : eventListeners) {
            eventListener.onStageError(onErrorStageExecutionEvent);
        }
    }

    private void propagateEvent(final OnErrorPipelineExecutionEvent onErrorPipelineExecutionEvent,
                                final PipelineEventListener... eventListeners) {
        for (final PipelineEventListener eventListener : eventListeners) {
            eventListener.onPipelineError(onErrorPipelineExecutionEvent);
        }
    }

    private void propagateEvent(final AfterPipelineExecutionEvent afterPipelineExecutionEvent,
                                final PipelineEventListener... eventListeners) {
        for (final PipelineEventListener eventListener : eventListeners) {
            eventListener.afterPipelineExecution(afterPipelineExecutionEvent);
        }
    }
}