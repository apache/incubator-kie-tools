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

package org.kie.workbench.common.services.backend.compiler.utils;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.appformer.maven.integration.Aether;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenRepos;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenUtils;
import org.kie.workbench.common.services.backend.constants.ResourcesConstants;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService.GLOBAL_M2_REPO_NAME;

public class MavenUtilsTest {

    @Test
    public void presenceOfDepInThePrj() throws Exception {
        List<String> pomList = MavenUtils.searchPoms(Paths.get(ResourcesConstants.DUMMY_KIE_MULTIMODULE_UNTOUCHED_DIR));
        assertThat(pomList).hasSize(3);
        List<Artifact> deps = MavenUtils.resolveDependenciesFromMultimodulePrj(pomList);
        assertThat(deps).hasSize(3);
    }

    @Test
    public void loadNonValidPomTest() {
        List<String> pomList = MavenUtils.searchPoms(Paths.get(ResourcesConstants.DUMMY_KIE_UNVALID_UNTOUCHED_DIR));
        assertThat(pomList).hasSize(3);
        pomList.forEach((pom) -> {
            assertThat(pom).endsWith("/pom.xml");
        });
    }

    @Test
    public void resolveDependenciesFromSinglePrjTest() {
        List<String> pomList = MavenUtils.searchPoms(Paths.get(ResourcesConstants.DUMMY_KIE_UNTOUCHED_DIR));
        assertThat(pomList).hasSize(1);
        List<Artifact> deps = MavenUtils.resolveDependenciesFromMultimodulePrj(pomList);
        assertThat(deps).hasSize(1);
        Artifact artifact = deps.get(0);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(artifact.getArtifactId()).isEqualTo("kie-api");
            softly.assertThat(artifact.getGroupId()).isEqualTo("org.kie");
            softly.assertThat(artifact.getVersion()).isEqualTo("6.5.0.Final");
            softly.assertThat(artifact.getType()).isEqualTo("jar");
            softly.assertThat(artifact.toString()).isEqualTo("org.kie:kie-api:jar:6.5.0.Final");
        });
    }

    @Test
    public void getMavenLocalRepoDirTest() {
        String testRepo = MavenUtils.getMavenRepoDir(MavenRepos.LOCAL);
        assertThat(testRepo).endsWith(".m2/repository/");
    }

    @Test
    public void getMavenGlobalRepoDirTest() {
        System.setProperty(GLOBAL_M2_REPO_NAME, Aether.getAether().getLocalRepository().getUrl());
        String testRepo = MavenUtils.getMavenRepoDir(MavenRepos.GLOBAL);
        assertThat(testRepo).endsWith(".m2/repository/");
    }
}
