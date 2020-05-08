/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.kmodule;

import java.net.URL;
import javax.inject.Inject;

import org.guvnor.test.WeldJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;

@RunWith(WeldJUnitRunner.class)
public class KModuleServiceImplIntegrationTest {

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();

    @Inject
    private Paths paths;

    @Inject
    private KModuleService kModuleService;

    @Test
    public void testIsKModuleFileWithKModuleFile() throws Exception {
        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/resources/META-INF/kmodule.xml");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a kModule.xml file resolves to a null package
        final boolean result = kModuleService.isKModule(testPath);
        assertTrue(result);
    }

    @Test
    public void testIsKModuleFileWithNonKModuleFile() throws Exception {
        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/resources/META-INF");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a kModule.xml file resolves to a null package
        final boolean result = kModuleService.isKModule(testPath);
        assertFalse(result);
    }
}
