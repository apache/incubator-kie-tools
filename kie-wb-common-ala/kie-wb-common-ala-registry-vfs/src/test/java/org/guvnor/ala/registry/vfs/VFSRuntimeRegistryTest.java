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

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.registry.inmemory.InMemoryRuntimeRegistryTest;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.providers.Provider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.file.Path;

import static org.guvnor.ala.AlaSPITestCommons.mockProviderListSPI;
import static org.guvnor.ala.registry.vfs.VFSRuntimeRegistry.PROVIDER_SUFFIX;
import static org.guvnor.ala.registry.vfs.VFSRuntimeRegistry.RUNTIME_REGISTRY_PATH;
import static org.guvnor.ala.registry.vfs.VFSRuntimeRegistry.RUNTIME_SUFFIX;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VFSRuntimeRegistryTest
        extends InMemoryRuntimeRegistryTest {

    private static final String ID_MD5 = "ID_MD5";

    @Mock
    private VFSRegistryHelper registryHelper;

    @Mock
    private Path registryRoot;

    private List<Object> providers;

    private List<Object> runtimes;

    private Path providerTargetPath;

    private Path runtimeTargetPath;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    @Override
    public void setUp() {
        super.setUp();
        when(registryHelper.ensureDirectory(RUNTIME_REGISTRY_PATH)).thenReturn(registryRoot);
        runtimeRegistry = spy(new VFSRuntimeRegistry(registryHelper));
        ((VFSRuntimeRegistry) runtimeRegistry).init();
    }

    @Test
    public void testInit() throws Exception {
        providers = new ArrayList<>();
        providers.addAll(mockProviderListSPI(providerType,
                                             ELEMENTS_COUNT));

        runtimes = new ArrayList<>();
        runtimes.addAll(mockRuntimeList(providerId,
                                        "",
                                        ELEMENTS_COUNT));

        when(registryHelper.readEntries(registryRoot,
                                        VFSRegistryHelper.BySuffixFilter.newFilter(PROVIDER_SUFFIX))).thenReturn(providers);
        when(registryHelper.readEntries(registryRoot,
                                        VFSRegistryHelper.BySuffixFilter.newFilter(RUNTIME_SUFFIX))).thenReturn(runtimes);

        ((VFSRuntimeRegistry) runtimeRegistry).init();

        verify(registryHelper,
               times(2)).ensureDirectory(RUNTIME_REGISTRY_PATH);
        verify(registryHelper,
               times(2)).readEntries(registryRoot,
                                     VFSRegistryHelper.BySuffixFilter.newFilter(PROVIDER_SUFFIX));
        verify(registryHelper,
               times(2)).readEntries(registryRoot,
                                     VFSRegistryHelper.BySuffixFilter.newFilter(RUNTIME_SUFFIX));

        for (Object provider : providers) {
            verifyProviderIsRegistered((Provider) provider);
        }

        for (Object runtime : runtimes) {
            verifyRuntimeIsRegistered((Runtime) runtime);
        }
    }

    @Test
    @Override
    public void testRegisterProvider() {
        prepareProviderTargetPath();

        runtimeRegistry.registerProvider(provider);

        try {
            verify(registryHelper,
                   times(1)).storeEntry(providerTargetPath,
                                        provider);
        } catch (Exception e) {
            //need to catch this exception because parent class method don't throws exceptions,
            //but this will never happen in this scenario.
            fail(e.getMessage());
        }
        verifyProviderIsRegistered(provider);
    }

    @Test
    public void testRegisterProviderWhenMarshallingErrors() throws Exception {
        prepareProviderTargetPath();

        expectedException.expectMessage("Unexpected error was produced during provider marshalling/storing, provider: " + provider);
        doThrow(new Exception("no matter the message here"))
                .when(registryHelper)
                .storeEntry(providerTargetPath,
                            provider);

        runtimeRegistry.registerProvider(provider);
    }

    @Test
    @Override
    public void testDeregisterProvider() {
        prepareProviderTargetPath();
        runtimeRegistry.registerProvider(provider);
        verifyProviderIsRegistered(provider);

        runtimeRegistry.deregisterProvider(provider);
        verify(registryHelper,
               times(1)).deleteBatch(providerTargetPath);
        verifyProviderIsNotRegistered(provider);
    }

    @Test
    @Override
    public void testDeregisterProviderById() {
        prepareProviderTargetPath();
        runtimeRegistry.registerProvider(provider);
        verifyProviderIsRegistered(provider);

        runtimeRegistry.deregisterProvider(provider.getId());
        verify(registryHelper,
               times(1)).deleteBatch(providerTargetPath);
        verifyProviderIsNotRegistered(provider);
    }

    private void prepareProviderTargetPath() {
        providerTargetPath = prepareTargetPath(provider.getId(),
                                               PROVIDER_SUFFIX);
    }

    @Test
    @Override
    public void testRegisterRuntime() {
        prepareRuntimeTargetPath();

        runtimeRegistry.registerRuntime(runtime);

        try {
            verify(registryHelper,
                   times(1)).storeEntry(runtimeTargetPath,
                                        runtime);
        } catch (Exception e) {
            //need to catch this exception because parent class method don't throws exceptions,
            //but this will never happen in this scenario.
            fail(e.getMessage());
        }
        verifyRuntimeIsRegistered(runtime);
    }

    @Test
    public void testRegisterRuntimeWhenMarshallingErrors() throws Exception {
        prepareRuntimeTargetPath();

        expectedException.expectMessage("Unexpected error was produced during runtime marshalling/storing, runtime: " + runtime);
        doThrow(new Exception("no matter the message here"))
                .when(registryHelper)
                .storeEntry(runtimeTargetPath,
                            runtime);

        runtimeRegistry.registerRuntime(runtime);
    }

    @Test
    @Override
    public void testDeregisterRuntime() {
        prepareRuntimeTargetPath();
        runtimeRegistry.registerRuntime(runtime);
        verifyRuntimeIsRegistered(runtime);

        runtimeRegistry.deregisterRuntime(runtime);
        verify(registryHelper,
               times(1)).deleteBatch(runtimeTargetPath);
        verifyRuntimeIsNotRegistered(runtime);
    }

    private void prepareRuntimeTargetPath() {
        runtimeTargetPath = prepareTargetPath(runtime.getId(),
                                              RUNTIME_SUFFIX);
    }

    private Path prepareTargetPath(String id,
                                   String suffix) {
        when(registryHelper.md5Hex(id)).thenReturn(ID_MD5);
        String expectedPath = ID_MD5 + suffix;
        Path targetPath = mock(Path.class);
        when(registryRoot.resolve(expectedPath)).thenReturn(targetPath);
        return targetPath;
    }
}
