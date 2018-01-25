/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.uberfire.backend.server.cdi.workspace;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.enterprise.inject.Vetoed;

import org.uberfire.workspace.WorkspaceContext;

@Vetoed
public class WorkspaceExecutorService implements ExecutorService {

    private WorkspaceNameResolver workspaceNameResolver;
    private ExecutorService executorService;

    public WorkspaceExecutorService() {
    }

    public WorkspaceExecutorService(WorkspaceNameResolver workspaceNameResolver,
                                    ExecutorService executorService) {
        this.workspaceNameResolver = workspaceNameResolver;
        this.executorService = executorService;
    }

    private String getWorkspaceName() {
        return this.getWorkspaceNameResolver().getWorkspaceName();
    }

    @Override
    public void shutdown() {
        getExecutorService().shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return getExecutorService().shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return getExecutorService().isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return getExecutorService().isTerminated();
    }

    @Override
    public boolean awaitTermination(final long l,
                                    final TimeUnit timeUnit) throws InterruptedException {
        return getExecutorService().awaitTermination(l,
                                                     timeUnit);
    }

    @Override
    public <T> Future<T> submit(final Callable<T> callable) {

        return getExecutorService().submit(this.generateCallable(callable));
    }

    @Override
    public <T> Future<T> submit(final Runnable runnable,
                                final T t) {
        return getExecutorService().submit(this.generateRunnable(runnable),
                                           t);
    }

    @Override
    public Future<?> submit(final Runnable runnable) {
        return getExecutorService().submit(this.generateRunnable(runnable));
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> collection) throws InterruptedException {
        return getExecutorService().invokeAll(collection);
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> collection,
                                         final long l,
                                         final TimeUnit timeUnit) throws InterruptedException {
        return getExecutorService().invokeAll(collection,
                                              l,
                                              timeUnit);
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
        return getExecutorService().invokeAny(collection);
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> collection,
                           final long l,
                           final TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return getExecutorService().invokeAny(collection,
                                              l,
                                              timeUnit);
    }

    @Override
    public void execute(final Runnable runnable) {
        getExecutorService().execute(this.generateRunnable(runnable));
    }

    private Runnable generateRunnable(final Runnable runnable) {

        String workspace = getWorkspaceName();
        return () -> {
            WorkspaceContext.set(workspace);
            runnable.run();
        };
    }

    private <T> Callable<T> generateCallable(final Callable<T> callable) {

        String workspace = getWorkspaceName();
        return () -> {
            WorkspaceContext.set(workspace);
            return callable.call();
        };
    }

    private ExecutorService getExecutorService() {
        return executorService;
    }

    private WorkspaceNameResolver getWorkspaceNameResolver() {
        return this.workspaceNameResolver;
    }
}
