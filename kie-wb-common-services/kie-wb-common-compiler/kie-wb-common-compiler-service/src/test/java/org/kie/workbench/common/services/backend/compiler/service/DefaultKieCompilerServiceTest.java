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
package org.kie.workbench.common.services.backend.compiler.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.BaseCompilerTest;
import org.kie.workbench.common.services.backend.compiler.TestUtilMaven;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultKieCompilerServiceTest extends BaseCompilerTest {

    public DefaultKieCompilerServiceTest() {
        super("target/test-classes/kjar-2-single-resources");
    }

    /**
     * Local
     **/
    @Test
    public void buildNonExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.build(tmpRoot,
                                                                            mavenRepoPath,
                                                                            TestUtilMaven.getSettingsFile());
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildAndSkipDepsNonExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.build(tmpRoot,
                                                                            mavenRepoPath,
                                                                            TestUtilMaven.getSettingsFile(),
                                                                            Boolean.FALSE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
        assertThat(res.getDependencies()).isEmpty();
    }

    @Test
    public void buildAndInstallNonExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.buildAndInstall(tmpRoot, mavenRepoPath,TestUtilMaven.getSettingsFile());
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildAndInstallSkipDepsNonExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.buildAndInstall(tmpRoot,
                                                                                      mavenRepoPath,
                                                                                      TestUtilMaven.getSettingsFile(),
                                                                                      Boolean.FALSE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
        assertThat(res.getDependencies()).isEmpty();
    }

    @Test
    public void buildExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.build(Paths.get(tmpRoot.toAbsolutePath() + "/dummy"),
                                                                            mavenRepoPath,
                                                                            TestUtilMaven.getSettingsFile());
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
    }

    @Test
    public void buildAndInstallExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.buildAndInstall(Paths.get(tmpRoot.toAbsolutePath() + "/dummy"),
                                                                                      mavenRepoPath,
                                                                                      TestUtilMaven.getSettingsFile());
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getDependencies()).isNotEmpty();
        assertThat(res.getDependencies().size()).isGreaterThan(0);
    }

    @Test
    public void buildAndInstallSkipDepsExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.buildAndInstall(Paths.get(tmpRoot.toAbsolutePath() + "/dummy"),
                                                                                      mavenRepoPath,
                                                                                      TestUtilMaven.getSettingsFile(),
                                                                                      Boolean.TRUE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getDependencies()).isEmpty();
    }

    @Test
    public void buildSpecializedNonExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.buildSpecialized(tmpRoot,
                                                                                       mavenRepoPath,
                                                                                       new String[]{MavenCLIArgs.ALTERNATE_USER_SETTINGS + TestUtilMaven.getSettingsFile(), MavenCLIArgs.COMPILE});
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildSpecializedSkipDepsExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.buildSpecialized(Paths.get(tmpRoot.toAbsolutePath() + "/dummy"),
                                                                                       mavenRepoPath,
                                                                                       new String[]{ MavenCLIArgs.ALTERNATE_USER_SETTINGS + TestUtilMaven.getSettingsFile(), MavenCLIArgs.COMPILE},
                                                                                       Boolean.TRUE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
    }

    @Test
    public void buildSpecializedSkipDepsNonExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.buildSpecialized(tmpRoot,
                                                                                       mavenRepoPath,
                                                                                       new String[]{ MavenCLIArgs.ALTERNATE_USER_SETTINGS + TestUtilMaven.getSettingsFile(), MavenCLIArgs.COMPILE},
                                                                                       Boolean.TRUE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildWithOverrideNonExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        //change some files
        Map<Path, InputStream> override = new HashMap<>();

        org.uberfire.java.nio.file.Path path = org.uberfire.java.nio.file.Paths.get(tmpRoot + "/dummy/src/main/java/dummy/DummyOverride.java");
        InputStream input = new FileInputStream(new File("target/test-classes/dummy_override/src/main/java/dummy/DummyOverride.java"));
        override.put(path, input);

        CompletableFuture<KieCompilationResponse> futureRes = service.build(tmpRoot,
                                                                            mavenRepoPath,
                                                                            TestUtilMaven.getSettingsFile(),
                                                                            override);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildWithOverrideExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        //change some files
        Map<org.uberfire.java.nio.file.Path, InputStream> override = new HashMap<>();

        org.uberfire.java.nio.file.Path path = org.uberfire.java.nio.file.Paths.get(tmpRoot + "/dummy/src/main/java/dummy/DummyOverride.java");
        InputStream input = new FileInputStream(new File("target/test-classes/dummy_override/src/main/java/dummy/DummyOverride.java"));
        override.put(path, input);

        CompletableFuture<KieCompilationResponse> futureRes = service.build(Paths.get(tmpRoot.toAbsolutePath() + "/dummy"),
                                                                            mavenRepoPath,
                                                                            TestUtilMaven.getSettingsFile(),
                                                                            override);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
    }

    /**
     * Remote
     **/

    @Test
    public void buildRemoteNonExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.build(tmpRoot.toAbsolutePath().toString(),
                                                                            mavenRepoPath,
                                                                            TestUtilMaven.getSettingsFile());
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildRemoteAndSkipDepsNonExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.build(tmpRoot.toAbsolutePath().toString(),
                                                                            mavenRepoPath,
                                                                            TestUtilMaven.getSettingsFile(),
                                                                            Boolean.FALSE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
        assertThat(res.getDependencies()).isEmpty();
    }

    @Test
    public void buildRemoteAndInstallNonExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.buildAndInstall(tmpRoot.toAbsolutePath().toString(),
                                                                                      mavenRepoPath,
                                                                                      TestUtilMaven.getSettingsFile());
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildRemoteAndInstallSkipDepsNonExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.buildAndInstall(tmpRoot.toAbsolutePath().toString(),
                                                                                      mavenRepoPath,
                                                                                      TestUtilMaven.getSettingsFile(),
                                                                                      Boolean.FALSE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
        assertThat(res.getDependencies()).isEmpty();
    }

    @Test
    public void buildRemoteExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.build(Paths.get(tmpRoot.toAbsolutePath() + "/dummy").toAbsolutePath().toString(),
                                                                            mavenRepoPath,
                                                                            TestUtilMaven.getSettingsFile());
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
    }

    @Test
    public void buildRemoteAndInstallExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.buildAndInstall(Paths.get(tmpRoot.toAbsolutePath() + "/dummy").toAbsolutePath().toString(),
                                                                                      mavenRepoPath,
                                                                                      TestUtilMaven.getSettingsFile());
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getDependencies()).isNotEmpty();
        assertThat(res.getDependencies().size()).isGreaterThan(0);
    }

    @Test
    public void buildRemoteAndInstallSkipDepsExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.buildAndInstall(Paths.get(tmpRoot.toAbsolutePath() + "/dummy").toAbsolutePath().toString(),
                                                                                      mavenRepoPath,
                                                                                      TestUtilMaven.getSettingsFile(),
                                                                                      Boolean.TRUE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getDependencies()).isEmpty();
    }

    @Test
    public void buildRemoteSpecializedNonExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.buildSpecialized(Paths.get(tmpRoot.toAbsolutePath().toString()),
                                                                                       mavenRepoPath,
                                                                                       new String[]{MavenCLIArgs.ALTERNATE_USER_SETTINGS + TestUtilMaven.getSettingsFile(), MavenCLIArgs.COMPILE});
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildRemoteSpecializedSkipDepsExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.buildSpecialized(Paths.get(tmpRoot.toAbsolutePath() + "/dummy").toAbsolutePath().toString(),
                                                                                       mavenRepoPath,
                                                                                       new String[]{MavenCLIArgs.ALTERNATE_USER_SETTINGS + TestUtilMaven.getSettingsFile(),
                                                                                               MavenCLIArgs.COMPILE},
                                                                                       Boolean.TRUE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
    }

    @Test
    public void buildRemoteSpecializedSkipDepsNonExistentProject() throws Exception {
        AFCompilerService service = new DefaultKieCompilerService();
        CompletableFuture<KieCompilationResponse> futureRes = service.buildSpecialized(tmpRoot.toAbsolutePath().toString(),
                                                                                       mavenRepoPath,
                                                                                       new String[]{MavenCLIArgs.ALTERNATE_USER_SETTINGS + TestUtilMaven.getSettingsFile(),
                                                                                               MavenCLIArgs.COMPILE},
                                                                                       Boolean.TRUE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }
}
