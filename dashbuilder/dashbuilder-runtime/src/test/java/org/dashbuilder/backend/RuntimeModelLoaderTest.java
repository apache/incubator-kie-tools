/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.dashbuilder.shared.service.RuntimeModelRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuntimeModelLoaderTest {

    Path baseTempDir;

    @Mock
    RuntimeModelRegistry registry;

    @Mock
    RuntimeOptions runtimeOptions;

    @InjectMocks
    RuntimeModelLoader runtimeModelLoader;

    @Before
    public void init() throws IOException {
        baseTempDir = Files.createTempDirectory("dashbuilder-tests");
    }

    @After
    public void cleanup() {
        FileUtils.deleteQuietly(baseTempDir.toFile());
    }

    @Test
    public void testCreateBaseDir() {
        String tempPath = Paths.get(baseTempDir.toString(), "tmp").toString();
        when(runtimeOptions.getImportsBaseDir()).thenReturn(tempPath);
        assertFalse(Paths.get(tempPath).toFile().exists());
        runtimeModelLoader.createBaseDir();
        assertTrue(Paths.get(tempPath).toFile().exists());
    }

    @Test
    public void testLoadAvailableModels() throws IOException {
        String baseDir = baseTempDir.toString();
        when(runtimeOptions.getImportsBaseDir()).thenReturn(baseDir);

        Path p1 = Paths.get(baseDir, "model1" + RuntimeOptions.DASHBOARD_EXTENSION);
        Path p2 = Paths.get(baseDir, "model2" + RuntimeOptions.DASHBOARD_EXTENSION);
        Path p3 = Paths.get(baseDir, "ignored.bkp");
        Path p4 = Paths.get(baseDir, "intermediary", "ignored.bkp");

        p4.toFile().getParentFile().mkdirs();
        
        for (Path p : Arrays.asList(p1, p2, p3, p4)) {
            p.toFile().createNewFile();
        }

        runtimeModelLoader.loadAvailableModels();

        verify(registry).registerFile(matches(p1.toString()));
        verify(registry).registerFile(matches(p2.toString()));
        verify(registry, times(0)).registerFile(matches(p3.toString()));
        verify(registry, times(0)).registerFile(matches(p4.toString()));
    }

}