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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.events.AfterPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.AfterStageExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforePipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforeStageExecutionEvent;
import org.guvnor.ala.pipeline.events.OnErrorPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.OnErrorStageExecutionEvent;
import org.guvnor.ala.pipeline.events.PipelineEvent;
import org.guvnor.ala.pipeline.events.PipelineEventListener;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.pipeline.execution.PipelineExecutorError;
import org.guvnor.ala.pipeline.execution.PipelineExecutorException;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTask;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTaskDef;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTaskManager;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTrace;
import org.guvnor.ala.pipeline.execution.RegistrableOutput;
import org.guvnor.ala.registry.PipelineExecutorRegistry;
import org.guvnor.ala.registry.PipelineRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class PipelineExecutorTaskManagerImpl
        implements PipelineExecutorTaskManager {

    private static final Logger logger = LoggerFactory.getLogger(PipelineExecutorTaskManagerImpl.class);

    protected static final int DEFAULT_THREAD_POOL_SIZE = 10;

    protected static final String THREAD_POOL_SIZE_PROPERTY_NAME = "org.guvnor.ala.pipeline.execution.threadPoolSize";

    protected ExecutorService executor;

    protected List<PipelineEventListener> externalListeners;

    protected PipelineExecutor pipelineExecutor;

    protected final Map<String, TaskEntry> currentTasks = new HashMap<>();

    protected Map<String, Future<?>> futureTaskMap = new HashMap<>();

    protected PipelineExecutorRegistry pipelineExecutorRegistry;

    protected PipelineRegistry pipelineRegistry;

    protected PipelineEventListener localListener;

    protected PipelineExecutorTaskManagerImplHelper taskManagerHelper;

    /**
     * Set of pipeline execution status that admits the stop operation.
     */
    private static final Set<PipelineExecutorTask.Status> stopEnabledStatus = new HashSet<PipelineExecutorTask.Status>() {
        {
            add(PipelineExecutorTask.Status.RUNNING);
            add(PipelineExecutorTask.Status.SCHEDULED);
        }
    };

    /**
     * Set of pipeline execution status that admits the delete operation.
     */
    private static final Set<PipelineExecutorTask.Status> deleteEnabledStatus = new HashSet<PipelineExecutorTask.Status>() {
        {
            add(PipelineExecutorTask.Status.STOPPED);
            add(PipelineExecutorTask.Status.ERROR);
            add(PipelineExecutorTask.Status.FINISHED);
        }
    };

    public PipelineExecutorTaskManagerImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public PipelineExecutorTaskManagerImpl(final PipelineRegistry pipelineRegistry,
                                           final Instance<ConfigExecutor> configExecutorInstance,
                                           final Instance<PipelineEventListener> pipelineEventListenerInstance,
                                           final PipelineExecutorRegistry pipelineExecutorRegistry) {

        this.pipelineRegistry = pipelineRegistry;
        this.taskManagerHelper = new PipelineExecutorTaskManagerImplHelper(configExecutorInstance,
                                                                           pipelineEventListenerInstance);
        this.pipelineExecutorRegistry = pipelineExecutorRegistry;
    }

    @PostConstruct
    protected void init() {
        initExecutor();
        initPipelineExecutor();
        initLocalListener();
        initExternalListeners();
    }

    @PreDestroy
    protected void destroy() {
        try {
            synchronized (currentTasks) {
                final Set<TaskEntry> entrySet = new HashSet<>();
                entrySet.addAll(currentTasks.values());
                entrySet.forEach(entry -> {
                    currentTasks.remove(entry.getTask().getId());
                    if (entry.isAsync()) {
                        final PipelineExecutorTaskImpl task = entry.getTask();
                        if (stopEnabledStatus.contains(task.getPipelineStatus())) {
                            try {
                                taskManagerHelper.setTaskInStoppedStatus(task);
                                updateExecutorRegistry(task);
                            } catch (Exception e) {
                                logger.error("It was not possible to update task: " + task.getId() + " during " +
                                                     " PipelineExecutorTaskManager finalization. " + e.getMessage(),
                                             e);
                            }
                        }
                    }
                });
            }
            if (executor != null) {
                executor.shutdown();
            }
        } catch (Exception e) {
            logger.error("executor shutdown failed. " + e.getMessage(),
                         e);
        }
    }

    private void initExecutor() {
        executor = taskManagerHelper.createExecutorService();
    }

    private void initPipelineExecutor() {
        pipelineExecutor = taskManagerHelper.createPipelineExecutor();
    }

    private void initLocalListener() {
        localListener = new PipelineEventListener() {
            @Override
            public void beforePipelineExecution(final BeforePipelineExecutionEvent bpee) {
                final TaskEntry taskEntry = getTaskEntry(bpee.getExecutionId());
                if (taskEntry != null) {
                    PipelineExecutorTaskManagerImpl.this.beforePipelineExecution(bpee,
                                                                                 taskEntry);
                    notifyExternalListeners(bpee);
                }
            }

            @Override
            public void afterPipelineExecution(final AfterPipelineExecutionEvent apee) {
                final TaskEntry taskEntry = getTaskEntry(apee.getExecutionId());
                if (taskEntry != null) {
                    PipelineExecutorTaskManagerImpl.this.afterPipelineExecution(apee,
                                                                                taskEntry);
                    notifyExternalListeners(apee);
                }
            }

            @Override
            public void beforeStageExecution(final BeforeStageExecutionEvent bsee) {
                final TaskEntry taskEntry = getTaskEntry(bsee.getExecutionId());
                if (taskEntry != null) {
                    PipelineExecutorTaskManagerImpl.this.beforeStageExecution(bsee,
                                                                              taskEntry);
                    notifyExternalListeners(bsee);
                }
            }

            @Override
            public void onStageError(final OnErrorStageExecutionEvent oesee) {
                final TaskEntry taskEntry = getTaskEntry(oesee.getExecutionId());
                if (taskEntry != null) {
                    PipelineExecutorTaskManagerImpl.this.onStageError(oesee,
                                                                      taskEntry);
                    notifyExternalListeners(oesee);
                }
            }

            @Override
            public void afterStageExecution(final AfterStageExecutionEvent asee) {
                final TaskEntry taskEntry = getTaskEntry(asee.getExecutionId());
                if (taskEntry != null) {
                    PipelineExecutorTaskManagerImpl.this.afterStageExecution(asee,
                                                                             taskEntry);
                    notifyExternalListeners(asee);
                }
            }

            @Override
            public void onPipelineError(final OnErrorPipelineExecutionEvent oepee) {
                final TaskEntry taskEntry = getTaskEntry(oepee.getExecutionId());
                if (taskEntry != null) {
                    PipelineExecutorTaskManagerImpl.this.onPipelineError(oepee,
                                                                         taskEntry);
                    notifyExternalListeners(oepee);
                }
            }
        };
    }

    private void initExternalListeners() {
        externalListeners = taskManagerHelper.createExternalListeners();
    }

    @Override
    public String execute(final PipelineExecutorTaskDef taskDef,
                          final ExecutionMode executionMode) {
        if (executionMode == ExecutionMode.ASYNCHRONOUS) {
            return executeAsync(taskDef);
        } else {
            return executeSync(taskDef);
        }
    }

    /**
     * Executes the task definition in asynchronous mode.
     * @param taskDef task definition for executing.
     * @return the taskId assigned to the running task.
     * @see PipelineExecutorTaskDef
     */
    private synchronized String executeAsync(final PipelineExecutorTaskDef taskDef) {
        final PipelineExecutorTaskImpl task = taskManagerHelper.createTask(taskDef);
        storeTaskEntry(TaskEntry.newAsyncEntry(task));
        startAsyncTask(task);
        updateExecutorRegistry(task);
        return task.getId();
    }

    /**
     * Executes a task in asynchronous mode.
     * @param task the task for execute.
     * @return the taskId of the task.
     */
    private synchronized void startAsyncTask(final PipelineExecutorTask task) {
        final Future<?> future = executor.submit(() -> {
            final Pipeline pipeline = pipelineRegistry.getPipelineByName(task.getTaskDef().getPipeline());
            try {
                pipelineExecutor.execute(task.getTaskDef().getInput(),
                                         pipeline,
                                         output -> processPipelineOutput(task,
                                                                         output),
                                         localListener);
            } catch (Exception e) {
                logger.error("An error was produced during pipeline execution for PipelineExecutorTask: " + task.getId(),
                             e);
            } finally {
                removeTaskEntry(task.getId());
                removeFutureTask(task.getId());
            }
        });

        storeFutureTask(task.getId(),
                        future);
    }

    /**
     * Executes a task definition in synchronous mode.
     * @param taskDef task definition for executing.
     * @return the taskId assigned to the executed task.
     */
    private String executeSync(final PipelineExecutorTaskDef taskDef) {
        final PipelineExecutorTaskImpl task = taskManagerHelper.createTask(taskDef);
        storeTaskEntry(TaskEntry.newSyncEntry(task));
        final Pipeline pipeline = pipelineRegistry.getPipelineByName(taskDef.getPipeline());
        pipelineExecutor.execute(taskDef.getInput(),
                                 pipeline,
                                 output -> processPipelineOutput(task,
                                                                 output),
                                 localListener);
        removeTaskEntry(task.getId());
        updateExecutorRegistry(task);
        return task.getId();
    }

    private void processPipelineOutput(final PipelineExecutorTask task,
                                       final Object output) {
        if (output instanceof RegistrableOutput) {
            ((PipelineExecutorTaskImpl) task).setOutput((RegistrableOutput) output);
        } else {
            //uncommon case
            logger.debug("Only pipeline outputs of type RegistrableOutput will be registered" +
                                 ", current output value won't be registered: " + output);
        }
    }

    @Override
    public void stop(final String taskId) throws PipelineExecutorException {
        final TaskEntry entry = getTaskEntry(taskId);
        if (entry == null) {
            throw new PipelineExecutorException("No PipelineExecutorTask was found for taskId: " + taskId);
        }
        if (!entry.isAsync()) {
            throw new PipelineExecutorException("Stop operation is not available for taskId: " + taskId +
                                                        " running in SYNCHRONOUS mode");
        }
        final PipelineExecutorTask.Status currentStatus = entry.getTask().getPipelineStatus();
        if (!stopEnabledStatus.contains(currentStatus)) {
            throw new PipelineExecutorException("A PipelineExecutorTask in status: " + currentStatus.name() + " can not" +
                                                        " be stopped. Stop operation is available for the following status set: " + stopEnabledStatus);
        }
        destroyFutureTask(taskId);
        removeTaskEntry(taskId);
        taskManagerHelper.setTaskInStoppedStatus(entry.getTask());
        updateExecutorRegistry(entry.getTask());
    }

    @Override
    public void destroy(final String taskId) throws PipelineExecutorException {
        final TaskEntry entry = getTaskEntry(taskId);
        if (entry == null) {
            throw new PipelineExecutorException("No PipelineExecutorTask was found for taskId: " + taskId);
        }
        if (!entry.isAsync()) {
            throw new PipelineExecutorException("Destroy operation is not available for taskId: " + taskId +
                                                        " running in SYNCHRONOUS mode");
        }
        destroyFutureTask(taskId);
        removeTaskEntry(taskId);
        pipelineExecutorRegistry.deregister(taskId);
    }

    @Override
    public void delete(final String taskId) throws PipelineExecutorException {
        final TaskEntry entry = getTaskEntry(taskId);
        if (entry != null) {
            throw new PipelineExecutorException("An active PipelineExecutorTask was found for taskId: " + taskId +
                                                        " delete operation is only available for the following status set: " + deleteEnabledStatus);
        }
        final PipelineExecutorTrace trace = pipelineExecutorRegistry.getExecutorTrace(taskId);
        if (trace == null) {
            throw new PipelineExecutorException("No PipelineExecutorTask was found for taskId: " + taskId);
        } else {
            if (!deleteEnabledStatus.contains(trace.getTask().getPipelineStatus())) {
                throw new PipelineExecutorException("A PipelineExecutorTask in status: "
                                                            + trace.getTask().getPipelineStatus().name() + " can not" +
                                                            " be deleted. Delete operation is available for the following status set: " + deleteEnabledStatus);
            } else {
                pipelineExecutorRegistry.deregister(taskId);
            }
        }
    }

    private void beforePipelineExecution(final BeforePipelineExecutionEvent bpee,
                                         final TaskEntry taskEntry) {
        taskEntry.getTask().setPipelineStatus(PipelineExecutorTask.Status.RUNNING);
        if (taskEntry.isAsync()) {
            updateExecutorRegistry(taskEntry.getTask());
        }
    }

    private void afterPipelineExecution(final AfterPipelineExecutionEvent apee,
                                        final TaskEntry taskEntry) {
        taskEntry.getTask().setPipelineStatus(PipelineExecutorTask.Status.FINISHED);
        if (taskEntry.isAsync()) {
            updateExecutorRegistry(taskEntry.getTask());
        }
    }

    private void beforeStageExecution(final BeforeStageExecutionEvent bsee,
                                      final TaskEntry taskEntry) {
        taskEntry.getTask().setStageStatus(bsee.getStage().getName(),
                                           PipelineExecutorTask.Status.RUNNING);
        if (taskEntry.isAsync()) {
            updateExecutorRegistry(taskEntry.getTask());
        }
    }

    private void onStageError(final OnErrorStageExecutionEvent oesee,
                              final TaskEntry taskEntry) {
        taskEntry.getTask().setPipelineStatus(PipelineExecutorTask.Status.ERROR);
        taskEntry.getTask().setStageStatus(oesee.getStage().getName(),
                                           PipelineExecutorTask.Status.ERROR);
        taskEntry.getTask().setStageError(oesee.getStage().getName(),
                                          new PipelineExecutorError(oesee.getError().getMessage(),
                                                                        oesee.getError()));
        if (taskEntry.isAsync()) {
            updateExecutorRegistry(taskEntry.getTask());
        }
    }

    private void afterStageExecution(final AfterStageExecutionEvent asee,
                                     final TaskEntry taskEntry) {
        taskEntry.getTask().setStageStatus(asee.getStage().getName(),
                                           PipelineExecutorTask.Status.FINISHED);
        if (taskEntry.isAsync()) {
            updateExecutorRegistry(taskEntry.getTask());
        }
    }

    private void onPipelineError(final OnErrorPipelineExecutionEvent oepee,
                                 final TaskEntry taskEntry) {

        taskEntry.getTask().setPipelineStatus(PipelineExecutorTask.Status.ERROR);
        taskEntry.getTask().setPipelineError(new PipelineExecutorError(oepee.getError().getMessage(),
                                                       oepee.getError()));
        if (taskEntry.isAsync()) {
            updateExecutorRegistry(taskEntry.getTask());
        }
    }

    private synchronized TaskEntry getTaskEntry(final String taskId) {
        return currentTasks.get(taskId);
    }

    private synchronized void removeTaskEntry(final String taskId) {
        currentTasks.remove(taskId);
    }

    private synchronized void storeTaskEntry(final TaskEntry entry) {
        currentTasks.put(entry.task.getId(),
                         entry);
    }

    private synchronized void storeFutureTask(final String taskId,
                                              final Future future) {
        futureTaskMap.put(taskId,
                          future);
    }

    private synchronized void removeFutureTask(final String taskId) {
        futureTaskMap.remove(taskId);
    }

    /**
     * Safe method for destroying a Future task. Used only internally.
     * @param taskId the task id to be destroyed.
     * @return true if the task was destroyed with no errors, false in any other case.
     */
    private synchronized boolean destroyFutureTask(final String taskId) {
        final Future future = futureTaskMap.remove(taskId);
        if (future != null && !future.isCancelled() && !future.isDone()) {
            try {
                future.cancel(true);
            } catch (Exception e) {
                logger.error("Cancellation of Future task: " + taskId + " failed. " + e.getMessage(),
                             e);
                return false;
            }
        }
        return true;
    }

    private void notifyExternalListeners(final PipelineEvent event) {
        taskManagerHelper.notifyExternalListeners(externalListeners,
                                                  event);
    }

    private void updateExecutorRegistry(final PipelineExecutorTaskImpl task) {
        try {
            PipelineExecutorTaskImpl clone = (PipelineExecutorTaskImpl) task.clone();
            pipelineExecutorRegistry.register(new PipelineExecutorTraceImpl(clone));
        } catch (Exception e) {
            //clone is supported by construction, since PipelineExecutorTaskImpl is clonable.
            logger.error("Unexpected error: " + e.getMessage(),
                         e);
        }
    }

    protected static class TaskEntry {

        private PipelineExecutorTaskImpl task;

        private ExecutionMode executionMode;

        private TaskEntry(PipelineExecutorTaskImpl task,
                          ExecutionMode executionMode) {
            this.task = task;
            this.executionMode = executionMode;
        }

        public static TaskEntry newAsyncEntry(PipelineExecutorTaskImpl task) {
            return new TaskEntry(task,
                                 ExecutionMode.ASYNCHRONOUS);
        }

        public static TaskEntry newSyncEntry(PipelineExecutorTaskImpl task) {
            return new TaskEntry(task,
                                 ExecutionMode.SYNCHRONOUS);
        }

        public PipelineExecutorTaskImpl getTask() {
            return task;
        }

        public boolean isAsync() {
            return ExecutionMode.ASYNCHRONOUS == executionMode;
        }
    }
}