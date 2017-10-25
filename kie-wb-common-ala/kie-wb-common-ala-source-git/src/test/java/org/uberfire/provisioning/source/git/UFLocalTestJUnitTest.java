/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.provisioning.source.git;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.GitRepository;
import org.guvnor.ala.source.git.UFLocal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystems;

import static org.junit.Assert.*;

public class UFLocalTestJUnitTest {

    private File tempPath;

    @Before
    public void setUp() {
        try {
            tempPath = Files.createTempDirectory("xxx").toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(tempPath);
    }

    @Test
    public void sourceCloneTest() throws Exception {
        final URI uri = URI.create("git://tempx");
        final FileSystem fs = FileSystems.newFileSystem(uri,
                                                        new HashMap<String, Object>() {{
                                                            put("init",
                                                                Boolean.TRUE);
                                                            put("internal",
                                                                Boolean.TRUE);
                                                            put("out-dir",
                                                                tempPath.getAbsolutePath());
                                                        }});

        final UFLocal local = new UFLocal();
        final GitRepository repository = (GitRepository) local.getRepository("tempx",
                                                                             Collections.emptyMap());
        final Source source = repository.getSource("master");
        assertNotNull(source);
        assertEquals(source.getPath().getFileSystem(),
                     fs);
    }
}
