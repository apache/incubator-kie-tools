/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.common.services.builder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * Producer for Executor services so we can plug-in a different implementation in tests
 */
@ApplicationScoped
public class BuildExecutorServiceFactoryImpl implements BuildExecutorServiceFactory {

    private ExecutorService service;

    @PostConstruct
    public void setup() {
        final int cores = Runtime.getRuntime().availableProcessors();
        service = Executors.newFixedThreadPool( cores );
    }

    @Override
    public ExecutorService getExecutorService() {
        return service;
    }

}
