/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.jcr2vfsmigration;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class MortgagesRepoImportTest {

    private File outputVfsRepo = new File("target/vfs-importer-tests/mortgages-repo-import");
    private File jcrExportDir = new File("target/vfs-importer-tests/mortgages-jcr-export");

    @Before
    public void setup() throws Exception {
        FileUtils.deleteDirectory(outputVfsRepo);
        FileUtils.forceMkdir(jcrExportDir);
        FileUtils.cleanDirectory(jcrExportDir);
        File jcrExportZip = new File(Class.class.getResource("/jcr-export-xml.zip").getFile());
        unzipFile(jcrExportZip, jcrExportDir);
    }

    @Test
    public void testImportMortgagesRepo() {
        String[] params = new String[]{
                "-i", jcrExportDir.getAbsolutePath(),
                "-o", outputVfsRepo.getAbsolutePath()
        };
        VfsImporterLauncher launcher = new VfsImporterLauncher();
        launcher.run(params);
        // TODO make sure the import was successful by checking the resulting git repo
    }

    private static void unzipFile(File zipFile, File outputDir) {
        try {
            new ZipFile(zipFile).extractAll(outputDir.getAbsolutePath());
        } catch (ZipException e) {
            throw new RuntimeException(
                    "Can't unzip file '" + zipFile.getAbsolutePath() + "' into dir '" + outputDir.getAbsolutePath() + "'!", e);
        }
    }

}
