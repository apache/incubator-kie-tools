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

package org.guvnor.common.services.project.backend.server;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import org.guvnor.common.services.project.model.GAV;

public class MavenLocalRepositoryUtils {

    /**
     * Get a Path pointing to a transient M2 Local Repository
     * @param gav GAV (helper to name temporary folder)
     * @return
     * @throws IOException
     */
    public static java.nio.file.Path getRepositoryPath(final GAV gav) throws IOException {
        final java.nio.file.Path tempLocalRepositoryBasePath = java.nio.file.Files.createTempDirectory("m2-" + toFileName(gav));
        return tempLocalRepositoryBasePath;
    }

    private static String toFileName(final GAV gav) {
        final StringBuilder sb = new StringBuilder();
        sb.append("m2-").append(gav.getGroupId() + "-" + gav.getArtifactId() + "-" + gav.getVersion());
        return sb.toString();
    }

    /**
     * Destroy the temporary local Maven Repository and all content.
     * @param m2Folder
     */
    public static void tearDownMavenRepository(final java.nio.file.Path m2Folder) {
        if (m2Folder != null) {
            try {
                Files.walkFileTree(m2Folder,
                                   new java.nio.file.SimpleFileVisitor<java.nio.file.Path>() {

                                       @Override
                                       public FileVisitResult visitFile(final java.nio.file.Path file,
                                                                        final BasicFileAttributes attrs) throws IOException {
                                           Files.delete(file);
                                           return FileVisitResult.CONTINUE;
                                       }

                                       @Override
                                       public FileVisitResult postVisitDirectory(final java.nio.file.Path dir,
                                                                                 final IOException exc) throws IOException {
                                           Files.delete(dir);
                                           return FileVisitResult.CONTINUE;
                                       }
                                   });
            } catch (IOException ioe) {
                //Swallow
            }
        }
    }
}
