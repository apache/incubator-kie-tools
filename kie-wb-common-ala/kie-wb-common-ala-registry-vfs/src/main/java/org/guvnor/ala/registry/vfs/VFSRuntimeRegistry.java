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

import org.guvnor.ala.registry.inmemory.InMemoryRuntimeRegistry;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.runtime.providers.Provider;
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
public class VFSRuntimeRegistry
        extends InMemoryRuntimeRegistry {

    protected static final String RUNTIME_REGISTRY_PATH = "runtime-registry";

    protected static final String PROVIDER_SUFFIX = "-provider.entry";

    protected static final String RUNTIME_SUFFIX = "-runtime.entry";

    private static final Logger logger = LoggerFactory.getLogger(VFSRuntimeRegistry.class);

    private VFSRegistryHelper registryHelper;

    private org.uberfire.java.nio.file.Path registryRoot;

    public VFSRuntimeRegistry() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public VFSRuntimeRegistry(final VFSRegistryHelper registryHelper) {
        this.registryHelper = registryHelper;
    }

    @PostConstruct
    protected void init() {
        initializeRegistryRoot();
        initializeRegistry();
    }

    @Override
    public void registerProvider(final Provider provider) {
        checkNotNull("provider",
                     provider);
        final Path path = buildProviderPath(provider.getId());
        try {
            registryHelper.storeEntry(path,
                                      provider);
        } catch (Exception e) {
            //uncommon error
            logger.error("Unexpected error was produced during provider marshalling/storing, provider: " + provider,
                         e);
            throw new RuntimeException("Unexpected error was produced during provider marshalling/storing, provider: " + provider,
                                       e);
        }
        super.registerProvider(provider);
    }

    @Override
    public void deregisterProvider(final Provider provider) {
        checkNotNull("provider",
                     provider);
        deregisterProvider(provider.getId());
    }

    @Override
    public void deregisterProvider(final String providerId) {
        checkNotNull("providerId",
                     providerId);
        final Path path = buildProviderPath(providerId);
        registryHelper.deleteBatch(path);
        super.deregisterProvider(providerId);
    }

    @Override
    public void registerRuntime(final Runtime runtime) {
        checkNotNull("runtime",
                     runtime);
        final Path path = buildRuntimePath(runtime.getId());
        try {
            registryHelper.storeEntry(path,
                                      runtime);
        } catch (Exception e) {
            //uncommon error
            logger.error("Unexpected error was produced during runtime marshalling/storing, runtime: " + runtime,
                         e);
            throw new RuntimeException("Unexpected error was produced during runtime marshalling/storing, runtime: " + runtime,
                                       e);
        }
        super.registerRuntime(runtime);
    }

    @Override
    public void deregisterRuntime(final RuntimeId runtimeId) {
        checkNotNull("runtimeId",
                     runtimeId);
        final Path path = buildRuntimePath(runtimeId.getId());
        registryHelper.deleteBatch(path);
        super.deregisterRuntime(runtimeId);
    }

    private void initializeRegistryRoot() {
        try {
            registryRoot = registryHelper.ensureDirectory(RUNTIME_REGISTRY_PATH);
        } catch (Exception e) {
            //uncommon error
            logger.error("An error was produced during " + VFSRuntimeRegistry.class.getName() +
                                 " directories initialization.",
                         e);
        }
    }

    private void initializeRegistry() {
        try {
            final List<Object> providers = registryHelper.readEntries(registryRoot,
                                                                      newFilter(PROVIDER_SUFFIX));
            providers.forEach(provider -> super.registerProvider((Provider) provider));

            final List<Object> runtimes = registryHelper.readEntries(registryRoot,
                                                                     newFilter(RUNTIME_SUFFIX));
            runtimes.forEach(runtime -> super.registerRuntime((Runtime) runtime));
        } catch (Exception e) {
            logger.error("An error was produced during " + VFSRuntimeRegistry.class.getName() + " initialization.",
                         e);
        }
    }

    private Path buildProviderPath(final String providerId) {
        return registryRoot.resolve(registryHelper.md5Hex(providerId) + PROVIDER_SUFFIX);
    }

    private Path buildRuntimePath(final String runtimeId) {
        return registryRoot.resolve(registryHelper.md5Hex(runtimeId) + RUNTIME_SUFFIX);
    }
}
