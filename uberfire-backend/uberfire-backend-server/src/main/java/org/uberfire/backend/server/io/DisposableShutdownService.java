/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.uberfire.commons.concurrent.Managed;
import org.uberfire.commons.concurrent.Unmanaged;
import org.uberfire.commons.lifecycle.Disposable;
import org.uberfire.commons.lifecycle.PriorityDisposable;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

public class DisposableShutdownService implements ServletContextListener {

    private ExecutorService unmanagedExecutorService;
    private ExecutorService executorService;

    public DisposableShutdownService() {
    }

    @Inject
    public DisposableShutdownService(@Managed ExecutorService executorService,
                                     @Unmanaged ExecutorService unmanagedExecutorService) {
        this.executorService = executorService;
        this.unmanagedExecutorService = unmanagedExecutorService;
    }

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {

        final List<PriorityDisposable> disposables = new ArrayList<PriorityDisposable>(PriorityDisposableRegistry.getDisposables());

        sort(disposables);

        for (final PriorityDisposable disposable : disposables) {
            disposable.dispose();
        }

        executorService.shutdown();
        unmanagedExecutorService.shutdown();

        for (final FileSystemProvider fileSystemProvider : FileSystemProviders.installedProviders()) {
            if (fileSystemProvider instanceof Disposable) {
                ((Disposable) fileSystemProvider).dispose();
            }
        }

        PriorityDisposableRegistry.clear();
    }

    void sort(final List<PriorityDisposable> disposables) {
        Collections.sort(disposables,
                         new Comparator<PriorityDisposable>() {
                             @Override
                             public int compare(final PriorityDisposable o1,
                                                final PriorityDisposable o2) {
                                 return (o2.priority() < o1.priority()) ? -1 : ((o2.priority() == o1.priority()) ? 0 : 1);
                             }
                         });
    }
}
