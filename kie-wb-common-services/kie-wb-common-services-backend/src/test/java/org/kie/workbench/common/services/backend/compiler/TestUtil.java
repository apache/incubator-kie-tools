/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.uberfire.java.nio.file.Path;

public class TestUtil {

    public static void copyTree(Path source,
                                Path target) throws IOException {
        FileUtils.copyDirectory(source.toFile(), target.toFile());
    }

    public static void rm(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                rm(c);
            }
        }
        if (!f.delete()) {
            System.err.println("Couldn't delete file " + f);
        }
    }

    public static void writeMavenOutputIntoTargetFolder(List<String> mavenOutput,
                                                        String testName) throws Exception {
        if (mavenOutput.size() > 0) {
            StringBuffer sb = new StringBuffer("target/").append(testName).append(".test.log");

            File fout = new File(sb.toString());
            FileOutputStream fos = new FileOutputStream(fout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            for (String item : mavenOutput) {
                bw.write(item);
                bw.newLine();
            }
            bw.close();
        }
    }
}
