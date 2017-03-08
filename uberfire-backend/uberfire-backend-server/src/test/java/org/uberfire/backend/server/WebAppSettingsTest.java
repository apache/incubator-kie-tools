/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.backend.server;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.*;

public class WebAppSettingsTest {

    String slash = FileSystems.getDefault().getSeparator();

    @Test
    public void testLastSlashIgnored() {
        WebAppSettings.get().setRootDir("/test/");
        String homeDir = WebAppSettings.get().getRootDir();
        assertEquals(homeDir,
                     slash + "test");

        WebAppSettings.get().setRootDir("c:\\test\\");
        homeDir = WebAppSettings.get().getRootDir();
        assertEquals(homeDir,
                     "c:" + slash + "test");
    }

    @Test
    public void testRelativeDir() {
        WebAppSettings.get().setRootDir("test");
        Path myFile = WebAppSettings.get().getAbsolutePath("mydir",
                                                           "myfile");
        assertEquals(myFile,
                     Paths.get("test",
                               "mydir",
                               "myfile"));
    }

    @Test
    public void testEmptyDir() {
        WebAppSettings.get().setRootDir(null);
        Path myDir = WebAppSettings.get().getAbsolutePath("mydir");
        assertNull(myDir);
    }
}
