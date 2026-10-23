/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.lsp.server;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.kie.api.KieServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectEngineTest {

    private static final Duration TIMEOUT = Duration.ofMinutes(2);

    private static final String BROKEN = """
            package com.example;
            rule "R"
                when
                    NoSuchType( field == 1 )
                then
                end
            """;

    @Test
    void engineIsAvailableOverAClasspathThatCarriesDrools() {
        try (ProjectEngine engine = ProjectEngine.over(TestClasspath.withoutTheBridge())) {
            assertThat(engine.available()).isTrue();
        }
    }

    @Test
    void noEngineOverAnEmptyClasspath() {
        try (ProjectEngine engine = ProjectEngine.over(List.of())) {
            assertThat(engine.available()).isFalse();
        }
    }

    @Test
    void noEngineOverAClasspathWithoutDrools(@TempDir Path tmp) {
        try (ProjectEngine engine = ProjectEngine.over(List.of(tmp))) {
            assertThat(engine.available()).isFalse();
        }
    }

    @Test
    void bridgeCompilesThroughTheProjectLoader() throws Exception {
        try (ProjectEngine engine = ProjectEngine.over(TestClasspath.withoutTheBridge())) {
            List<Map<String, Object>> messages = engine.build(Map.of("src/main/resources/rules/A.drl", BROKEN), TIMEOUT);

            assertThat(messages).anySatisfy(m -> assertThat(m.get("level")).isEqualTo("ERROR"));
        }
    }

    @Test
    void bridgeIsDefinedInsideTheProjectWorldNotTheServers() throws Exception {
        try (ProjectEngine engine = ProjectEngine.over(TestClasspath.withoutTheBridge())) {
            Class<?> bridge = engine.bridgeClassForTest();

            assertThat(bridge.getClassLoader()).isNotSameAs(ProjectEngine.class.getClassLoader());
            assertThat(bridge.getClassLoader().getParent()).isSameAs(engine.projectLoaderForTest());
            assertThat(engine.projectLoaderForTest().getParent()).isSameAs(ClassLoader.getPlatformClassLoader());
            assertThat(bridge.getClassLoader().loadClass("org.kie.api.KieServices").getClassLoader())
                    .isSameAs(engine.projectLoaderForTest());
        }
    }

    @Test
    void closedEngineIsUnavailableAndRefusesToBuild() {
        ProjectEngine engine = ProjectEngine.over(TestClasspath.withoutTheBridge());
        assertThat(engine.available()).isTrue();

        engine.close();

        assertThat(engine.isClosed()).isTrue();
        assertThat(engine.available()).isFalse();
        assertThatThrownBy(() -> engine.build(Map.of(), TIMEOUT)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void aPackageLargeEnoughForTheParallelBuildCompiles() throws Exception {
        StringBuilder drl = new StringBuilder("package com.example;\ndeclare Order\n    total : int\nend\n");
        for (int i = 0; i < 30; i++) {
            drl.append("rule \"R").append(i).append("\"\n    when\n        Order( total > ").append(i)
                    .append(" )\n    then\nend\n");
        }
        try (ProjectEngine engine = ProjectEngine.over(TestClasspath.withoutTheBridge())) {
            List<Map<String, Object>> messages = engine.build(Map.of("src/main/resources/rules/A.drl", drl.toString()), TIMEOUT);

            assertThat(messages).isEmpty();
        }
    }

    @Test
    void aBuildPastTheTimeoutFailsInsteadOfBlocking() {
        StringBuilder drl = new StringBuilder("package com.example;\ndeclare Order\n    total : int\nend\n");
        for (int i = 0; i < 30; i++) {
            drl.append("rule \"R").append(i).append("\"\n    when\n        Order( total > ").append(i)
                    .append(" )\n    then\nend\n");
        }
        try (ProjectEngine engine = ProjectEngine.over(TestClasspath.withoutTheBridge())) {
            assertThatThrownBy(() -> engine.build(Map.of("src/main/resources/rules/A.drl", drl.toString()),
                    Duration.ofMillis(1)))
                    .isInstanceOf(TimeoutException.class);
        }
    }

    @Test
    void anErrorInALargePackageIsReportedAsAMessage() throws Exception {
        StringBuilder drl = new StringBuilder("package com.example;\ndeclare Order\n    total : int\nend\n");
        for (int i = 0; i < 30; i++) {
            String field = i == 17 ? "totl" : "total";
            drl.append("rule \"R").append(i).append("\"\n    when\n        Order( ").append(field).append(" > ")
                    .append(i).append(" )\n    then\nend\n");
        }
        try (ProjectEngine engine = ProjectEngine.over(TestClasspath.withoutTheBridge())) {
            List<Map<String, Object>> messages = engine.build(Map.of("src/main/resources/rules/A.drl", drl.toString()), TIMEOUT);

            assertThat(messages).anySatisfy(m -> assertThat((String) m.get("text")).contains("totl"));
        }
    }

    @Test
    void aProbeClassThatFailsToLinkMeansNoEngine(@TempDir Path tmp) throws Exception {
        Path classFile = tmp.resolve("org/kie/api/KieServices.class");
        Files.createDirectories(classFile.getParent());
        Files.write(classFile, new byte[] {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE, 0, 0, 0, 99, 0, 0});

        try (ProjectEngine engine = ProjectEngine.over(List.of(tmp))) {
            assertThat(engine.available()).isFalse();
        }
    }

    @Test
    void kieApiWithoutTheCompilerIsNoEngine() throws Exception {
        Path kieApi = Path.of(KieServices.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        try (ProjectEngine engine = ProjectEngine.over(List.of(kieApi))) {
            assertThat(engine.available()).isFalse();
        }
    }

    @Test
    void compilerWithoutMvelIsNoEngine() {
        List<Path> withoutMvel = new ArrayList<>();
        for (Path entry : TestClasspath.withoutTheBridge()) {
            if (!entry.toString().contains("drools-mvel")) {
                withoutMvel.add(entry);
            }
        }
        try (ProjectEngine engine = ProjectEngine.over(withoutMvel)) {
            assertThat(engine.available()).isFalse();
        }
    }
}
