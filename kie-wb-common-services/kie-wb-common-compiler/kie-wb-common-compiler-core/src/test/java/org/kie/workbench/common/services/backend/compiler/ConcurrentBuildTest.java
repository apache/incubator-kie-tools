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

package org.kie.workbench.common.services.backend.compiler;

import org.kie.workbench.common.services.backend.utils.TestUtil;
import org.kie.workbench.common.services.backend.constants.ResourcesConstants;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrentBuildTest {

    private Path mavenRepo;
    private Logger logger = LoggerFactory.getLogger(ConcurrentBuildTest.class);

    private CountDownLatch latch = new CountDownLatch(4);

    @Before
    public void setUp() throws Exception {
        mavenRepo = TestUtil.createMavenRepo();
    }

    @Test
    public void buildFourProjectsInFourThreadCompletableFuture() throws Exception {
        latch = new CountDownLatch(4);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        final CompletableFuture<KieCompilationResponse> resOne = supplyAsync(this::compileAndloadKieJarSingleMetadataWithPackagedJar, executor);
        final CompletableFuture<KieCompilationResponse> resTwo = supplyAsync(this::compileAndLoadKieJarMetadataAllResourcesPackagedJar, executor);
        final CompletableFuture<KieCompilationResponse> resThree = supplyAsync(this::compileAndloadKieJarSingleMetadataWithPackagedJar, executor);
        final CompletableFuture<KieCompilationResponse> resFour = supplyAsync(this::compileAndLoadKieJarMetadataAllResourcesPackagedJar, executor);

        latch.await();

        System.err.println(resOne.get());
        System.err.println(resTwo.get());
        System.err.println(resThree.get());
        System.err.println(resFour.get());

        assertThat(resOne.get().isSuccessful()).isTrue();
        assertThat(resTwo.get().isSuccessful()).isTrue();
        assertThat(resThree.get().isSuccessful()).isTrue();
        assertThat(resFour.get().isSuccessful()).isTrue();
    }

    @Test
    public void buildFourProjectsInFourThread() {
        latch = new CountDownLatch(4);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            List<Callable<KieCompilationResponse>> tasks = Arrays.asList(
                    this::compileAndloadKieJarSingleMetadataWithPackagedJar,
                    this::compileAndloadKieJarSingleMetadataWithPackagedJar,
                    this::compileAndLoadKieJarMetadataAllResourcesPackagedJar,
                    this::compileAndLoadKieJarMetadataAllResourcesPackagedJar);
            final List<Future<KieCompilationResponse>> results = executor.invokeAll(tasks);

            latch.await();

            logger.info("\nFinished all threads ");
            assertThat(results).hasSize(4);
            for (Future<KieCompilationResponse> result : results) {
                logger.info("Working dir:" + result.get().getWorkingDir().get() + " success:" + result.get().isSuccessful());
            }
            for (Future<KieCompilationResponse> result : results) {
                assertThat(result.get().isSuccessful()).isTrue();
            }
        } catch (ExecutionException ee) {
            logger.error(ee.getMessage());
        } catch (InterruptedException e) {
            logger.error("tasks interrupted");
        } finally {
            if (!executor.isTerminated()) {
                logger.error("cancel non-finished tasks");
            }
            executor.shutdownNow();
            logger.info("shutdown finished");
        }
    }

    @Test
    public void buildFourProjectsInTheSameThread() throws Exception {
        latch = new CountDownLatch(4);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {

            Callable<Map<Integer, KieCompilationResponse>> task1 = () -> {
                Map<Integer, KieCompilationResponse> map = new ConcurrentHashMap<>(2);
                KieCompilationResponse r1 = compileAndloadKieJarSingleMetadataWithPackagedJar();
                KieCompilationResponse r2 = compileAndLoadKieJarMetadataAllResourcesPackagedJar();
                KieCompilationResponse r3 = compileAndloadKieJarSingleMetadataWithPackagedJar();
                KieCompilationResponse r4 = compileAndLoadKieJarMetadataAllResourcesPackagedJar();
                map.put(1, r1);
                map.put(2, r2);
                map.put(3, r3);
                map.put(4, r4);
                return map;
            };

            Future<Map<Integer, KieCompilationResponse>> future = executor.submit(task1);
            latch.await();
            Map<Integer, KieCompilationResponse> result = future.get();// blocking call
            assertThat(result.get(1).isSuccessful()).isTrue();
            assertThat(result.get(2).isSuccessful()).isTrue();
            assertThat(result.get(3).isSuccessful()).isTrue();
            assertThat(result.get(4).isSuccessful()).isTrue();
            logger.info("\nFinished all threads");
        } catch (InterruptedException e) {
            logger.error("tasks interrupted");
        } finally {
            if (!executor.isTerminated()) {
                logger.error("cancel non-finished tasks");
            }
            executor.shutdownNow();
            logger.info("shutdown finished");
        }
    }

    private KieCompilationResponse compileAndloadKieJarSingleMetadataWithPackagedJar() {
        String alternateSettingsAbsPath = TestUtil.getSettingsFile();;
        Path tmpRoot = Files.createTempDirectory("repo_" + UUID.randomUUID().toString());
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(), "dummy"));
        try {
            TestUtil.copyTree(Paths.get(ResourcesConstants.KJAR_2_SINGLE_RESOURCES), tmp);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        final AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.KIE_AND_LOG_AFTER);
        final WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(tmp.toUri()));
        final CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                     info,
                                                                     new String[]{
                                                                             MavenCLIArgs.COMPILE,
                                                                             MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath
                                                                     },
                                                                     Boolean.FALSE);
        final KieCompilationResponse res = (KieCompilationResponse) compiler.compile(req);

        logger.info("\nFinished " + res.isSuccessful() + " Single metadata tmp:" + tmp + " UUID:" + req.getRequestUUID() + " res.getMavenOutput().isEmpty():" + res.getMavenOutput().isEmpty());
        if (!res.isSuccessful()) {
            try {
                logger.error(" Fail, writing output on target folder:" + tmp + " UUID:" + req.getRequestUUID());
                TestUtil.writeMavenOutputIntoTargetFolder(tmp, res.getMavenOutput(),
                                                          "ConcurrentBuildTest.compileAndloadKieJarSingleMetadataWithPackagedJar_" + req.getRequestUUID());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        latch.countDown();
        return res;
    }

    private KieCompilationResponse compileAndLoadKieJarMetadataAllResourcesPackagedJar() {
        String alternateSettingsAbsPath = TestUtil.getSettingsFile();;
        Path tmpRoot = Files.createTempDirectory("repo_" + UUID.randomUUID().toString());
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(), "dummy"));
        try {
            TestUtil.copyTree(Paths.get(ResourcesConstants.KJAR_2_ALL_RESOURCES), tmp);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        final AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.KIE_AND_LOG_AFTER);
        final WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(tmp);
        final CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                     info,
                                                                     new String[]{
                                                                             MavenCLIArgs.COMPILE,
                                                                             MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath
                                                                     },
                                                                     Boolean.FALSE);
        final KieCompilationResponse res = (KieCompilationResponse) compiler.compile(req);

        logger.info("\nFinished " + res.isSuccessful() + " all Metadata tmp:" + tmp + " UUID:" + req.getRequestUUID() + " res.getMavenOutput().isEmpty():" + res.getMavenOutput().isEmpty());
        if (!res.isSuccessful()) {
            try {
                logger.error("writing output on target folder:" + tmp);
                TestUtil.writeMavenOutputIntoTargetFolder(tmp, res.getMavenOutput(),
                                                          "ConcurrentBuildTest.compileAndLoadKieJarMetadataAllResourcesPackagedJar_" + req.getRequestUUID());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        latch.countDown();
        return res;
    }
}
