/*
 * Copyright 2019 Red Hat; Inc. and/or its affiliates.
 *
 * Licensed under the Apache License; Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing; software
 * distributed under the License is distributed on an "AS IS" BASIS;
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND; either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.projecteditor.client.build.exec.impl;

import java.util.HashMap;

import org.assertj.core.api.Assertions;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.build.exec.SnapshotDeploymentSettings;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildType;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.Executor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.build.BuildExecutor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.deploy.DefaultBuildAndDeployExecutor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.deploy.SnapshotBuildAndDeployExecutor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.install.DefaultInstallExecutor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.install.SnapshotInstallExecutor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.redeploy.SnapshotRedeployExecutor;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.util.BuildExecutionTestConstants.ARTIFACT;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.util.BuildExecutionTestConstants.GROUP;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.util.BuildExecutionTestConstants.SNAPSHOT;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.util.BuildExecutionTestConstants.VERSION;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BuildExecutionManagerImplTest {

    @Mock
    private BuildExecutor defaultBuildExecutor;

    @Mock
    private DefaultBuildAndDeployExecutor defaultBuildAndDeployExecutor;

    @Mock
    private DefaultInstallExecutor defaultInstallExecutor;

    @Mock
    private BuildExecutor snapshotBuildExecutor;

    @Mock
    private SnapshotBuildAndDeployExecutor snapshotBuildAndDeployExecutor;

    @Mock
    private SnapshotInstallExecutor snapshotInstallExecutor;

    @Mock
    private SnapshotRedeployExecutor snapshotRedeployExecutor;

    @Mock
    private SnapshotDeploymentSettings settings;

    @Mock
    private KieModule module;

    @Mock
    private BuildExecutionContext context;

    private BuildExecutionManagerImpl buildExecutionManager;

    @Before
    public void init() {

        when(module.getPom()).thenReturn(new POM(new GAV(GROUP, ARTIFACT, VERSION)));

        when(context.getModule()).thenReturn(module);

        this.buildExecutionManager = new BuildExecutionManagerImpl() {
            @Override
            protected void init() {
                defaultRunners = new HashMap<>();
                defaultRunners.put(BuildType.BUILD, defaultBuildExecutor);
                defaultRunners.put(BuildType.DEPLOY, defaultBuildAndDeployExecutor);
                defaultRunners.put(BuildType.INSTALL, defaultInstallExecutor);

                snapshotRunners = new HashMap<>();
                snapshotRunners.put(BuildType.BUILD, snapshotBuildExecutor);
                snapshotRunners.put(BuildType.DEPLOY, snapshotBuildAndDeployExecutor);
                snapshotRunners.put(BuildType.INSTALL, snapshotInstallExecutor);
                snapshotRunners.put(BuildType.REDEPLOY, snapshotRedeployExecutor);
            }
        };

        buildExecutionManager.init();
    }

    @Test
    public void testDefaultBuild() {
        testExecutor(BuildType.BUILD, defaultBuildExecutor);
    }

    @Test
    public void testDefaultBuildAndDeploy() {
        testExecutor(BuildType.DEPLOY, defaultBuildAndDeployExecutor);
    }

    @Test
    public void testDefaultInstall() {
        testExecutor(BuildType.INSTALL, defaultInstallExecutor);
    }

    @Test
    public void testDefaultRedeploy() {
        Assertions.assertThatThrownBy(() -> buildExecutionManager.execute(BuildType.REDEPLOY, context))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot run REDEPLOY for module groupId:artifactId:version");
    }

    @Test
    public void testSnapshotBuild() {
        testSnapshotExecutor(BuildType.BUILD, snapshotBuildExecutor);
    }

    @Test
    public void testSnapshotBuildAndDeploy() {
        testSnapshotExecutor(BuildType.DEPLOY, snapshotBuildAndDeployExecutor);
    }

    @Test
    public void testSnapshotInstall() {
        testSnapshotExecutor(BuildType.INSTALL, snapshotInstallExecutor);
    }

    @Test
    public void testSnapshotRedeploy() {
        testSnapshotExecutor(BuildType.REDEPLOY, snapshotRedeployExecutor);
    }

    private void testSnapshotExecutor(BuildType buildType, Executor executor) {
        module.getPom().getGav().setVersion(VERSION + SNAPSHOT);
        testExecutor(buildType, executor);
    }

    private void testExecutor(BuildType buildType, Executor executor) {
        buildExecutionManager.execute(buildType, context);
        verify(executor).run(eq(context));
    }
}
