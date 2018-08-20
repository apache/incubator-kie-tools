/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.offprocess.service.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.offprocess.CompilerIPCCoordinator;
import org.kie.workbench.common.services.backend.compiler.offprocess.impl.CompilerIPCCoordinatorImpl;
import org.kie.workbench.common.services.backend.compiler.offprocess.impl.QueueProvider;
import org.kie.workbench.common.services.backend.compiler.offprocess.service.CompilerOffprocessService;

public class CompilerOffprocessServiceImpl implements CompilerOffprocessService {

    private ExecutorService executor;
    private CompilerIPCCoordinator compilerCoordinator;
    private static final String defaultQueueName = "offprocess-queue";

    public CompilerOffprocessServiceImpl(){
        this(Executors.newCachedThreadPool(), defaultQueueName);
    }

    public CompilerOffprocessServiceImpl(ExecutorService executor, String queueName){
        this(executor, new QueueProvider(queueName));
    }

    public CompilerOffprocessServiceImpl(ExecutorService executor, QueueProvider queueProvider){
        this.executor = executor;
        compilerCoordinator = new CompilerIPCCoordinatorImpl(queueProvider);
    }

    @Override
    public CompletableFuture compile(CompilationRequest req) {
        return CompletableFuture.supplyAsync(() -> (compilerCoordinator.compile(req)), executor);
    }
}
