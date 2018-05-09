/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.project.migration.cli.maven;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.kie.workbench.common.migration.cli.MigrationServicesCDIWrapper;
import org.kie.workbench.common.services.backend.pom.PomEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Path;

public class PomMigrationEditor {

    private final Logger logger = LoggerFactory.getLogger(PomMigrationEditor.class);
    private PomEditor pomEditor;
    private MavenXpp3Writer writer;

    public PomMigrationEditor() {
        pomEditor = new PomEditor();
        writer = new MavenXpp3Writer();
    }

    public Model updatePom(Path pom, MigrationServicesCDIWrapper cdiWrapper) {
        try {
            Model model = pomEditor.updatePomWithoutWrite(pom);
            boolean written = write(model, pom, cdiWrapper);
            if (written) {
                return model;
            } else {
                return new Model();
            }
        } catch (Exception e) {
            System.err.println("Error occurred during POMs migration:" + e.getMessage());
            logger.error(e.getMessage());
            return new Model();
        }
    }

    public Model updatePom(Path pom, String pathJsonFile, MigrationServicesCDIWrapper cdiWrapper) {
        try {
            Model model = pomEditor.updatePomWithoutWrite(pom, pathJsonFile);
            boolean written = write(model, pom, cdiWrapper);
            if (written) {
                return model;
            } else {
                return new Model();
            }
        } catch (Exception e) {
            System.err.println("Error occurred during POMs migration:" + e.getMessage());
            logger.error(e.getMessage());
            return new Model();
        }
    }

    public boolean write(Model model, Path path, MigrationServicesCDIWrapper cdiWrapper) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            writer.write(baos, model);
            if (logger.isInfoEnabled()) {
                logger.info("Pom changed of the groupID:{} artifactID:{}:\n{}",
                            model.getGroupId(),
                            model.getArtifactId(),
                            new String(baos.toByteArray(),
                                       StandardCharsets.UTF_8));
            }

            cdiWrapper.write(org.uberfire.backend.server.util.Paths.convert(path),
                             new String(baos.toByteArray(), StandardCharsets.UTF_8),
                             "Pom's Migration" + path.toString());
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                //suppressed
            }
        }
    }
}
