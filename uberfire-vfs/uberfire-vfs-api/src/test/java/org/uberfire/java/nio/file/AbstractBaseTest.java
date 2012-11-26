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

package org.uberfire.java.nio.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;

public abstract class AbstractBaseTest {

    public List<File> cleanupList = new ArrayList<File>();

    @After
    public void cleanup() throws java.io.IOException {
        for (final File file : cleanupList) {
            FileUtils.deleteDirectory(file);
        }
    }

    protected Path newTempDir() {
        return newTempDir(null);
    }

    protected Path newTempDir(final Path parent) {
        final Path dir;
        if (parent == null) {
            dir = Files.createTempDirectory("temp");
        } else {
            dir = Files.createTempDirectory(parent, "temp");
        }

        cleanupList.add(dir.toFile());
        return dir;
    }

    protected Path newDirToClean() {
        final Path dir = Paths.get("temp" + System.currentTimeMillis());
        cleanupList.add(dir.toFile());
        return dir;
    }

}
