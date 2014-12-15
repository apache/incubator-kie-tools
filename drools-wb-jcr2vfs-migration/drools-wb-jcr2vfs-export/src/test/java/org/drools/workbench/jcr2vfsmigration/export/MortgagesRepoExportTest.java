/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.jcr2vfsmigration.export;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.drools.workbench.jcr2vfsmigration.JcrExporterLauncher;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class MortgagesRepoExportTest {

    private static final File TMP_DIR = new File("target/jcr2vfs-tests");
    private static final String JCR_REPO_ZIP_NAME = "/mortgages-jcr-repo.zip";

    private File jcrRepoDir;
    private File jcrExportDir;

    @Before
    public void setup() {
        FileUtils.deleteQuietly(TMP_DIR);
        jcrRepoDir = new File(TMP_DIR, "jcr-input-repo");
        jcrExportDir = new File(TMP_DIR, "jcr-export-xml");
        jcrRepoDir.mkdirs();
        File jcrRepoZip = new File(Class.class.getResource(JCR_REPO_ZIP_NAME).getFile());
        unzipFile(jcrRepoZip, jcrRepoDir);
    }

    @Test
    public void testExportMortgagesRepo() {
        String [] params = new String[] {
                "-i", jcrRepoDir.getAbsolutePath(),
                "-o", jcrExportDir.getAbsolutePath()
        };
        JcrExporterLauncher jcrExporterLauncher = new JcrExporterLauncher();
        jcrExporterLauncher.run(params);
        // TODO assert that the XML files were actually created and contain the correct content!
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
