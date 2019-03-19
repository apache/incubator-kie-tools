/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.data.modeller.service.impl;

import java.io.File;
import java.net.URL;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.literal.NamedLiteral;
import org.junit.AfterClass;
import org.kie.workbench.common.forms.data.modeller.service.impl.ext.dmo.authoring.ModuleDMOModelReaderServiceTest;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

public abstract class AbstractModelFinderTest {

    public static final String PERSISTENCE_ID_PROPERTY = "id";

    public static final String CLIENT_TYPE = "com.example.model.Client";
    public static final String CLIENT_NAME = "name";
    public static final String CLIENT_NAME_LABEL = "The Name Label";
    public static final String CLIENT_LAST_NAME = "lastName";
    public static final String CLIENT_LAST_NAME_LABEL = "The Last Name Label";

    public static final String LINE_TYPE = "com.example.model.Line";
    public static final String LINE_PRODUCT = "product";
    public static final String LINE_PRODUCT_LABEL = "Product";
    public static final String LINE_PRICE = "price";
    public static final String LINE_PRICE_LABEL = "Price";
    public static final String LINE_DATE = "date";
    public static final String LINE_DATE_LABEL = "Date";

    public static final String EXPENSE_TYPE = "com.example.model.Expense";
    public static final String EXPENSE_DATE = "date";
    public static final String EXPENSE_DATE_LABEL = "Expense Date";
    public static final String EXPENSE_CLIENT = "client";
    public static final String EXPENSE_CLIENT_LABEL = "Expense Client";
    public static final String EXPENSE_LINES = "lines";
    public static final String EXPENSE_LINES_LABEL = "Expense Lines";

    public static final String ADDRESS_TYPE = "com.example.model.Address";
    public static final String ADDRESS_STREET = "street";
    public static final String ADDRESS_STREET_LABEL = "The Street Label";
    public static final String ADDRESS_NUM = "num";
    public static final String ADDRESS_NUM_LABEL = "Num.";
    public static final String ADDRESS_CP = "postalCode";
    public static final String ADDRESS_CP_LABEL = "CP";
    public static final String ADDRESS_CITY = "city";
    public static final String ADDRESS_CITY_LABEL = "City";
    public static final String ADDRESS_MAIN_ADDRESS = "mainAddress";
    public static final String ADDRESS_MAIN_ADDRESS_LABEL = "Is Main Address?";

    public static final String MODEL_TYPE = "com.example.model.Model";

    public static final int ADDRESS_VALID_FIELDS = 5;

    private static final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();

    protected static WeldContainer weldContainer;

    protected static IOService ioService;
    protected static BuildService buildService;
    protected static KieModuleService moduleService;
    protected static ModuleClassLoaderHelper classLoaderHelper;

    protected static KieModule currentModule;
    protected static Path currentModulePath;

    private static File repoFile;

    public static void initialize() throws Exception {

        repoFile = Files.createTempDir();

        System.setProperty(JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED, "false");
        System.setProperty(JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED, "false");
        System.setProperty("org.uberfire.sys.repo.monitor.disabled", "true");
        System.setProperty(ArtifactRepositoryService.ORG_GUVNOR_M2REPO_DIR_PROPERTY, repoFile.getAbsolutePath());

        weldContainer = new Weld().initialize();

        ioService = weldContainer.select(IOService.class, new NamedLiteral("ioStrategy")).get();

        buildService = weldContainer.select(BuildService.class).get();
        moduleService = weldContainer.select(KieModuleService.class).get();
        classLoaderHelper = weldContainer.select(ModuleClassLoaderHelper.class).get();

        fs.forceAsDefault();
    }

    @AfterClass
    public static void tearDown() {
        if (weldContainer != null) {
            weldContainer.shutdown();
        }

        FileUtils.deleteQuietly(repoFile);

        System.clearProperty(JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED);
        System.clearProperty(JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED);
        System.clearProperty("org.uberfire.sys.repo.monitor.disabled");
        System.clearProperty(ArtifactRepositoryService.ORG_GUVNOR_M2REPO_DIR_PROPERTY);
    }

    protected static void buildModules(String... moduleFolders) throws Exception {
        for (String moduleFolder : moduleFolders) {
            final URL pomUrl = ModuleDMOModelReaderServiceTest.class.getResource("/" + moduleFolder + "/pom.xml");
            final org.uberfire.java.nio.file.Path nioPomPath = ioService.get(pomUrl.toURI());

            currentModulePath = Paths.convert(nioPomPath);

            currentModule = moduleService.resolveModule(currentModulePath);
            BuildResults buildResults = buildService.buildAndDeploy(currentModule);

            Assertions.assertThat(buildResults.getErrorMessages())
                    .isNotNull()
                    .hasSize(0);
        }
    }
}
