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
import java.nio.file.Files;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.GitHub;
import org.guvnor.ala.source.git.GitRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This test shows how the Cloning works
 */
public class CloneTestJUnitTest {

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
    public void hello() throws Exception {
        final GitHub gitHub = new GitHub();
        final GitRepository repository = (GitRepository) gitHub.getRepository("salaboy/drools-workshop",
                                                                              new HashMap<String, String>() {{
                                                                                  put("out-dir",
                                                                                      tempPath.getAbsolutePath());
                                                                              }});
        final Source source = repository.getSource("master");
        assertNotNull(source);
    }
}
