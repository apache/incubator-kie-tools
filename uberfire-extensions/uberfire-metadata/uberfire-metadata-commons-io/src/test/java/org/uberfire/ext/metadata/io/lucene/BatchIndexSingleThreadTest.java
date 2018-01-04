/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.metadata.io.lucene;

import java.net.URI;

import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMUnitConfig(debug = true)
@BMScript(dir = "byteman", value = "lucene.btm")
public class BatchIndexSingleThreadTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{this.getClass().getSimpleName()};
    }

    @Test
    //See https://bugzilla.redhat.com/show_bug.cgi?id=1288132
    public void testSingleBatchIndexExecution() throws InterruptedException {
        final Path path1 = getBasePath(this.getClass().getSimpleName()).resolve("xxx");
        ioService().write(path1,
                          "xxx!");

        setupCountDown(3);
        //Make multiple requests for the FileSystem. We should only have one batch index operation
        final URI fsURI = URI.create("git://" + this.getClass().getSimpleName() + "/file1");

        final FileSystem fs1 = ioService().getFileSystem(fsURI);
        assertNotNull(fs1);

        final FileSystem fs2 = ioService().getFileSystem(fsURI);
        assertNotNull(fs2);

        final FileSystem fs3 = ioService().getFileSystem(fsURI);
        assertNotNull(fs3);

        waitForCountDown(5000);

        assertEquals(1,
                     getStartBatchCount());
    }
}