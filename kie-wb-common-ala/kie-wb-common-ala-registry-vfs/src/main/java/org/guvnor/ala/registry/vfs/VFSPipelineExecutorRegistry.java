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

package org.guvnor.ala.registry.vfs;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.guvnor.ala.pipeline.execution.PipelineExecutorTrace;
import org.guvnor.ala.registry.inmemory.InMemoryPipelineExecutorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Path;

import static org.guvnor.ala.registry.vfs.VFSRegistryHelper.BySuffixFilter.newFilter;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Stores the registered elements in the VFS.
 */
@ApplicationScoped
@Specializes
public class VFSPipelineExecutorRegistry
        extends InMemoryPipelineExecutorRegistry {

    protected static final String PIPELINE_EXECUTOR_REGISTRY_PATH = "executor-registry";

    protected static final String TRACE_SUFFIX = "-trace.entry";

    private static final Logger logger = LoggerFactory.getLogger(VFSPipelineExecutorRegistry.class);

    private VFSRegistryHelper registryHelper;

    private org.uberfire.java.nio.file.Path registryRoot;

    public VFSPipelineExecutorRegistry() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public VFSPipelineExecutorRegistry(final VFSRegistryHelper registryHelper) {
        this.registryHelper = registryHelper;
    }

    @PostConstruct
    public void init() {
        initializeRegistryRoot();
        initializeRegistry();
    }

    @Override
    public void register(final PipelineExecutorTrace trace) {
        checkNotNull("trace",
                     trace);
        final Path path = buildTracePath(trace.getTaskId());
        try {
            registryHelper.storeEntry(path,
                                      trace);
        } catch (Exception e) {
            //uncommon error
            logger.error("Unexpected error was produced during trace marshalling/storing, trace: " + trace,
                         e);
            throw new RuntimeException("Unexpected error was produced during trace marshalling/storing, trace: " + trace,
                                       e);
        }
        super.register(trace);
    }

    @Override
    public void deregister(final String taskId) {
        checkNotNull("taskId",
                     taskId);
        final Path path = buildTracePath(taskId);
        registryHelper.deleteBatch(path);
        super.deregister(taskId);
    }

    private void initializeRegistryRoot() {
        try {
            registryRoot = registryHelper.ensureDirectory(PIPELINE_EXECUTOR_REGISTRY_PATH);
        } catch (Exception e) {
            //uncommon error
            logger.error("An error was produced during " + VFSPipelineExecutorRegistry.class.getName() +
                                 " directories initialization.",
                         e);
        }
    }

    private void initializeRegistry() {
        try {
            final List<Object> traces = registryHelper.readEntries(registryRoot,
                                                                   newFilter(TRACE_SUFFIX));
            traces.forEach(trace -> super.register((PipelineExecutorTrace) trace));
        } catch (Exception e) {
            logger.error("An error was produced during " + VFSPipelineExecutorRegistry.class.getName() + " initialization.",
                         e);
        }
    }

    private Path buildTracePath(final String pipelineExecutionTraceId) {
        return registryRoot.resolve(registryHelper.md5Hex(pipelineExecutionTraceId) + TRACE_SUFFIX);
    }
}