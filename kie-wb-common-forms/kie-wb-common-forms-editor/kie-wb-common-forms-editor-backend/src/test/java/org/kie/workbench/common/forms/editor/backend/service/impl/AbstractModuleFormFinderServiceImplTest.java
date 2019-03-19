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

package org.kie.workbench.common.forms.editor.backend.service.impl;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

public abstract class AbstractModuleFormFinderServiceImplTest {

    public static final String PERSON = "org.kie.example.Person";
    public static final String ADDRESS = "org.kie.example.Address";
    public static final String OFFICE = "org.kie.example.Office";

    public static final String FORM_MODULE_1_1 = "module1_1";
    public static final String FORM_MODULE_1_2 = "module1_2";
    public static final String FORM_MODULE_1_3 = "module1_3";

    public static final String FORM_MODULE_2_1 = "module2_1";
    public static final String FORM_MODULE_2_2 = "module2_2";

    private static final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();

    protected static WeldContainer weldContainer;

    protected static IOService ioService;
    protected static BuildService buildService;
    protected static KieModuleService moduleService;
    protected static ModuleClassLoaderHelper classLoaderHelper;

    protected static KieModule currentModule;
    protected static Path currentModulePath;

    protected static ModuleFormFinderServiceImpl formFinderService;

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
        formFinderService = weldContainer.select(ModuleFormFinderServiceImpl.class).get();

        fs.forceAsDefault();
    }

    protected void testFindFormById(final String id) {
        FormDefinition result = formFinderService.findFormById(id, currentModulePath);

        Assertions.assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", id);
    }

    protected void testFindFormByType(final String type, final String... names) {

        final Collection<String> validNames = Arrays.asList(names);

        List<FormDefinition> result = formFinderService.findFormsForType(type, currentModulePath);

        Assertions.assertThat(result)
                .isNotNull()
                .hasSize(validNames.size());

        result.forEach(formDefinition -> {
            Assertions.assertThat(formDefinition)
                    .isNotNull()
                    .matches(formDefinition1 -> validNames.contains(formDefinition1.getId()) && validNames.contains(formDefinition1.getName()));
        });
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
            final URL pomUrl = AbstractModuleFormFinderServiceImplTest.class.getResource("/" + moduleFolder + "/pom.xml");
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
