/**
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
package org.uberfire.backend.server;

import java.io.File;

import org.junit.Test;

import static org.junit.Assert.*;

public class WebAppSettingsTest {

    @Test
    public void testLastSlashIgnored() {
        WebAppSettings.get().setRootDir("/test/");
        String homeDir = WebAppSettings.get().getRootDir();
        assertEquals(homeDir, "/test");

        WebAppSettings.get().setRootDir("c:\\test\\");
        homeDir = WebAppSettings.get().getRootDir();
        assertEquals(homeDir, "c:\\test");
    }

    @Test
    public void testRelativeDir() {
        WebAppSettings.get().setRootDir("test");
        String myDir = WebAppSettings.get().getAbsolutePath("mydir");
        assertEquals(myDir, "test" + File.separator + "mydir");
    }

    @Test
    public void testEmptyDir() {
        WebAppSettings.get().setRootDir(null);
        String myDir = WebAppSettings.get().getAbsolutePath("mydir");
        assertNull(myDir);
    }
}
