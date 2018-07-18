/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.backend.server;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.backend.file.FileExtensionFilter;
import org.guvnor.common.services.backend.file.LinkedDotFileFilter;
import org.guvnor.common.services.backend.file.LinkedFilter;
import org.guvnor.common.services.backend.file.LinkedMetaInfFolderFilter;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;

public class ScenarioLoader {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private ScenarioTestEditorService scenarioTestEditorService;

    public List<Scenario> loadScenarios(Path testResourcePath) {
        List<Scenario> scenarios = new ArrayList<Scenario>();

        for (Path path : loadScenarioPaths(testResourcePath)) {
            scenarios.add(scenarioTestEditorService.load(path));
        }
        return scenarios;
    }

    private List<Path> loadScenarioPaths(final Path path) {
        // Check Path exists
        final List<Path> items = new ArrayList<Path>();
        if (!Files.exists(Paths.convert(path))) {
            return items;
        }

        // Ensure Path represents a Folder
        org.uberfire.java.nio.file.Path pPath = Paths.convert(path);
        if (!Files.isDirectory(pPath)) {
            pPath = pPath.getParent();
        }

        LinkedFilter filter = new LinkedDotFileFilter();
        LinkedFilter metaInfFolderFilter = new LinkedMetaInfFolderFilter();
        filter.setNextFilter(metaInfFolderFilter);
        FileExtensionFilter fileExtensionFilter = new FileExtensionFilter(".scenario");

        // Get list of immediate children
        try (final DirectoryStream<org.uberfire.java.nio.file.Path> directoryStream = ioService.newDirectoryStream(pPath)) {
            for (final org.uberfire.java.nio.file.Path p : directoryStream) {
                if (filter.accept(p) && fileExtensionFilter.accept(p)) {
                    if (Files.isRegularFile(p)) {
                        items.add(Paths.convert(p));
                    } else if (Files.isDirectory(p)) {
                        items.add(Paths.convert(p));
                    }
                }
            }
        }

        return items;
    }
}
