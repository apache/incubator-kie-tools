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

package org.guvnor.ala.pipeline.execution.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.enterprise.inject.Instance;

import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.events.AfterPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.AfterStageExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforePipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforeStageExecutionEvent;
import org.guvnor.ala.pipeline.events.OnErrorPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.OnErrorStageExecutionEvent;
import org.guvnor.ala.pipeline.events.PipelineEvent;
import org.guvnor.ala.pipeline.events.PipelineEventListener;
import org.guvnor.ala.pipeline.execution.ExecutionIdGenerator;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTask;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTaskDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.guvnor.ala.pipeline.execution.impl.PipelineExecutorTaskManagerImpl.DEFAULT_THREAD_POOL_SIZE;
import static org.guvnor.ala.pipeline.execution.impl.PipelineExecutorTaskManagerImpl.THREAD_POOL_SIZE_PROPERTY_NAME;

/**
 * This class is directly related to the PipelineExecutorTaskManagerImpl and contains specific helper methods
 * for alleviating his job.
 */
public class PipelineExecutorTaskManagerImplHelper {

    private static final Logger logger = LoggerFactory.getLogger(PipelineExecutorTaskManagerImplHelper.class);

    private Instance<ConfigExecutor> configExecutorInstance;

    private Instance<PipelineEventListener> pipelineEventListenerInstance;

    public PipelineExecutorTaskManagerImplHelper(final Instance<ConfigExecutor> configExecutorInstance,
                                                 final Instance<PipelineEventListener> pipelineEventListenerInstance) {
        this.configExecutorInstance = configExecutorInstance;
        this.pipelineEventListenerInstance = pipelineEventListenerInstance;
    }

    public ExecutorService createExecutorService() {
        final String threadPoolSizeValue = System.getProperties().getProperty(THREAD_POOL_SIZE_PROPERTY_NAME);
        int threadPoolSize;
        if (threadPoolSizeValue == null) {
            threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
            logger.debug(THREAD_POOL_SIZE_PROPERTY_NAME + " property was not set, by default value will be used: " + DEFAULT_THREAD_POOL_SIZE);
        } else {
            try {
                threadPoolSize = Integer.parseInt(threadPoolSizeValue);
                if (threadPoolSize <= 0) {
                    threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
                    logger.error(THREAD_POOL_SIZE_PROPERTY_NAME + " property must be greater than 0, by default value will be used: " + DEFAULT_THREAD_POOL_SIZE);
                } else {
                    logger.debug(THREAD_POOL_SIZE_PROPERTY_NAME + " property will be set to: " + threadPoolSize);
                }
            } catch (Exception e) {
                threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
                logger.error(THREAD_POOL_SIZE_PROPERTY_NAME + " property was set to a wrong value, by default value will be used: " + DEFAULT_THREAD_POOL_SIZE,
                             e);
            }
        }

        return Executors.newFixedThreadPool(threadPoolSize);
    }

    public PipelineExecutor createPipelineExecutor() {
        final Collection<ConfigExecutor> configs = new ArrayList<>();
        configExecutorInstance.iterator().forEachRemaining(configs::add);
        return newPipelineExecutor(configs);
    }

    /**
     * added for testing/internal purposes, interested parties must use createPipelineExecutor()
     */
    protected PipelineExecutor newPipelineExecutor(final Collection<ConfigExecutor> configs) {
        return new PipelineExecutor(configs);
    }

    public List<PipelineEventListener> createExternalListeners() {
        List<PipelineEventListener> eventListeners = new ArrayList<>();
        pipelineEventListenerInstance.iterator().forEachRemaining(eventListeners::add);
        return eventListeners;
    }

    public void notifyExternalListeners(final List<PipelineEventListener> externalListeners,
                                        final PipelineEvent event) {
        externalListeners.forEach(listener -> {
            try {
                if (event instanceof BeforePipelineExecutionEvent) {
                    listener.beforePipelineExecution((BeforePipelineExecutionEvent) event);
                } else if (event instanceof BeforeStageExecutionEvent) {
                    listener.beforeStageExecution((BeforeStageExecutionEvent) event);
                } else if (event instanceof AfterStageExecutionEvent) {
                    listener.afterStageExecution((AfterStageExecutionEvent) event);
                } else if (event instanceof AfterPipelineExecutionEvent) {
                    listener.afterPipelineExecution((AfterPipelineExecutionEvent) event);
                } else if (event instanceof OnErrorPipelineExecutionEvent) {
                    listener.onPipelineError((OnErrorPipelineExecutionEvent) event);
                } else if (event instanceof OnErrorStageExecutionEvent) {
                    listener.onStageError((OnErrorStageExecutionEvent) event);
                }
            } catch (Exception e) {
                //if the notification of the event in a particular listener fails let the execution continue.
                logger.error("Pipeline event notification on listener: " + listener + " failed: " + e.getMessage(),
                             e);
            }
        });
    }

    public PipelineExecutorTaskImpl createTask(final PipelineExecutorTaskDef taskDef) {
        String executionId = generateTaskId();
        return createTask(taskDef,
                          executionId);
    }

    public PipelineExecutorTaskImpl createTask(final PipelineExecutorTaskDef taskDef,
                                               final String executionId) {
        PipelineExecutorTaskImpl task = new PipelineExecutorTaskImpl(taskDef,
                                                                     executionId);
        return task;
    }

    public String generateTaskId() {
        return ExecutionIdGenerator.generateExecutionId();
    }

    public void setTaskInStoppedStatus(final PipelineExecutorTaskImpl task) {
        task.setPipelineStatus(PipelineExecutorTask.Status.STOPPED);
        task.getTaskDef().getStages()
                .stream()
                .filter(stage -> PipelineExecutorTask.Status.RUNNING.equals(task.getStageStatus(stage)) ||
                        PipelineExecutorTask.Status.SCHEDULED.equals(task.getStageStatus(stage)))
                .forEach(stage -> task.setStageStatus(stage,
                                                      PipelineExecutorTask.Status.STOPPED));
        task.clearErrors();
        task.setOutput(null);
    }
}
