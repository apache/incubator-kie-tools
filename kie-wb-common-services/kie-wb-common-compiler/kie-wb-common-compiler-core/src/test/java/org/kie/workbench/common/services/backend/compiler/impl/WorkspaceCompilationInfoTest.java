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
package org.kie.workbench.common.services.backend.compiler.impl;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.constants.ResourcesConstants;
import org.kie.workbench.common.services.backend.utils.TestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class WorkspaceCompilationInfoTest {

    private static Path mavenRepo;
    private static Logger logger = LoggerFactory.getLogger(WorkspaceCompilationInfoTest.class);
    private static Path tmpRoot;

    @Before
    public void setUp() throws Exception {
        mavenRepo = TestUtil.createMavenRepo();
        tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(), "dummy"));
        TestUtil.copyTree(Paths.get(ResourcesConstants.KJAR_2_SINGLE_RESOURCES), tmp);
    }

    @Test
    public void testMethods() {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(tmpRoot);
        assertThat(info.isKiePluginPresent()).isFalse();
        info.lateAdditionKiePluginPresent(Boolean.TRUE);
        assertThat(info.isKiePluginPresent()).isTrue();
        assertThat(info.getPrjPath().toUri()).isEqualTo(tmpRoot.toUri());
    }
}
