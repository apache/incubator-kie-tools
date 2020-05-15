/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.net.URISyntaxException;
import java.net.URL;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

public abstract class AbstractDataModelerServiceWeldTest {

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private WeldContainer weldContainer;
    private KieModuleService moduleService;
    protected DataModelerService dataModelService;

    @Before
    public void setUp() throws Exception {
        // disable git and ssh daemons as they are not needed for the tests
        System.setProperty("org.uberfire.nio.git.daemon.enabled", "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled", "false");
        System.setProperty("org.uberfire.sys.repo.monitor.disabled", "true");

        //Bootstrap WELD container
        weldContainer = new Weld().initialize();
        dataModelService = weldContainer.select(DataModelerService.class).get();
        moduleService = weldContainer.select(KieModuleService.class).get();

    }

    @After
    public void tearDown() {
        if (weldContainer != null) {
            weldContainer.shutdown();
        }
    }

    protected KieModule loadProjectFromResources(String resourcesDir) throws URISyntaxException {
        final URL packageUrl = getClass().getResource(resourcesDir);
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = Paths.convert(nioPackagePath);

        return moduleService.resolveModule(packagePath);
    }
}
