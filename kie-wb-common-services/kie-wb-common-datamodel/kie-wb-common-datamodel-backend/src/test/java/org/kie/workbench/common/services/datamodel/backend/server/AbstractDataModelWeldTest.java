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

package org.kie.workbench.common.services.datamodel.backend.server;

import java.net.URISyntaxException;
import java.net.URL;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

abstract public class AbstractDataModelWeldTest {

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private WeldContainer weldContainer;

    @Before
    public void setUp() throws Exception {
        // disable git and ssh daemons as they are not needed for the tests
        System.setProperty(JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED, "false");
        System.setProperty(JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED, "false");
        System.setProperty("org.uberfire.sys.repo.monitor.disabled", "true");

        //Bootstrap WELD container
        weldContainer = new Weld().initialize();

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();
    }

    @After
    public void tearDown() {
        // Avoid NPE in case weld.initialize() failed
        if (weldContainer != null) {
            weldContainer.shutdown();
        }
    }

    protected ModuleDataModelOracle initializeModuleDataModelOracle(String projectResourceDirectoryPath) throws URISyntaxException {
        DataModelService dataModelService = weldContainer.instance().select(DataModelService.class).get();

        final URL packageUrl = getClass().getResource(projectResourceDirectoryPath);
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = Paths.convert(nioPackagePath);

        return dataModelService.getModuleDataModel(packagePath);
    }
}
