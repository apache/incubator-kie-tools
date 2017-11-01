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
 */

package org.uberfire.java.nio.fs.jgit.manager;

import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProxy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JGitFileSystemsCacheTest {

    JGitFileSystemsCache cache;
    private JGitFileSystemProviderConfiguration config;

    @Before
    public void setup() {
        config = mock(JGitFileSystemProviderConfiguration.class);
    }

    @Test
    public void addAndGetTest() {
        when(config.getJgitFileSystemsInstancesCache()).thenReturn(2);
        cache = new JGitFileSystemsCache(config);

        assertTrue(cache.fileSystemsSuppliers.isEmpty());
        assertTrue(cache.memoizedSuppliers.isEmpty());

        assertEquals(null,
                     cache.get("fs1"));

        JGitFileSystem fs1 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs1Supplier = () -> fs1;
        cache.addSupplier("fs1",
                          fs1Supplier);

        assertFalse(cache.fileSystemsSuppliers.isEmpty());
        assertFalse(cache.memoizedSuppliers.isEmpty());

        JGitFileSystemProxy fs1Proxy = (JGitFileSystemProxy) cache.get("fs1");

        assertEquals(fs1,
                     fs1Proxy.getRealJGitFileSystem());

        assertTrue(cache.containsKey("fs1"));

        cache.clear();

        assertTrue(cache.fileSystemsSuppliers.isEmpty());
        assertTrue(cache.memoizedSuppliers.isEmpty());
    }

    @Test
    public void addMoreFSThanCacheSupports() {
        when(config.getJgitFileSystemsInstancesCache()).thenReturn(2);
        cache = new JGitFileSystemsCache(config);

        JGitFileSystem fs1 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs1Supplier = getSupplierSpy(fs1);
        cache.addSupplier("fs1",
                          fs1Supplier);

        assertEquals(1,
                     cache.fileSystemsSuppliers.size());
        assertEquals(1,
                     cache.memoizedSuppliers.size());

        ((JGitFileSystemProxy) cache.get("fs1")).getRealJGitFileSystem();

        JGitFileSystem fs2 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs2Supplier = getSupplierSpy(fs2);
        cache.addSupplier("fs2",
                          fs2Supplier);
        ((JGitFileSystemProxy) cache.get("fs2")).getRealJGitFileSystem();

        assertEquals(2,
                     cache.fileSystemsSuppliers.size());
        assertEquals(2,
                     cache.memoizedSuppliers.size());

        JGitFileSystem fs3 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs3Supplier = getSupplierSpy(fs3);
        cache.addSupplier("fs3",
                          fs3Supplier);

        ((JGitFileSystemProxy) cache.get("fs3")).getRealJGitFileSystem();

        assertEquals(3,
                     cache.fileSystemsSuppliers.size());
        assertEquals(2,
                     cache.memoizedSuppliers.size());

        ((JGitFileSystemProxy) cache.get("fs2")).getRealJGitFileSystem();

        //just one call because is on memoized cache
        verify(fs2Supplier,
               times(1)).get();

        ((JGitFileSystemProxy) cache.get("fs3")).getRealJGitFileSystem();

        //just one call because is on memoized cache
        verify(fs3Supplier,
               times(1)).get();

        ((JGitFileSystemProxy) cache.get("fs1")).getRealJGitFileSystem();

        // two calls because is on no longer on memoized cache (oldest instance) needs to regenerate
        // from fs supplier
        verify(fs1Supplier,
               times(2)).get();
    }

    private Supplier<JGitFileSystem> getSupplierSpy(final JGitFileSystem fs1) {
        return spy(new Supplier<JGitFileSystem>() {
            @Override
            public JGitFileSystem get() {
                return fs1;
            }
        });
    }
}