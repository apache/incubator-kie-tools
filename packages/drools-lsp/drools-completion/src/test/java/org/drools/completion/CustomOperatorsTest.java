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

package org.drools.completion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The operator cache is JVM-wide and never emptied, so ids registered by one
 * test stay registered; tests asserting absence use ids nothing here declares.
 */
class CustomOperatorsTest {

    static Path classDirDeclaring(Path root, String confFile, String... ids) throws Exception {
        Path conf = root.resolve(confFile);
        Files.createDirectories(conf.getParent());
        StringBuilder text = new StringBuilder();
        for (String id : ids) {
            text.append("drools.evaluator.").append(id).append("=com.example.").append(id).append("Definition\n");
        }
        Files.writeString(conf, text.toString());
        return root;
    }

    @Test
    void discoversIdsFromAClassDirectoryAndAJar(@TempDir Path dir) throws Exception {
        Path classes = classDirDeclaring(dir.resolve("classes"), "META-INF/kie.properties.conf",
                "supersetOf", "equality", "comparable");
        Path jar = dir.resolve("dep.jar");
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar))) {
            out.putNextEntry(new JarEntry("META-INF/kie.default.properties.conf"));
            out.write("drools.evaluator.soundsSimilar = com.example.SoundsSimilarDefinition\n"
                    .getBytes());
            out.closeEntry();
        }

        assertThat(CustomOperators.discover(Set.of(classes, jar)))
                .containsExactly("soundsSimilar", "supersetOf");
    }

    @Test
    void entriesWithoutAConfigurationYieldNothing(@TempDir Path dir) throws Exception {
        Path plainDir = Files.createDirectories(dir.resolve("plain"));
        Path notAJar = Files.writeString(dir.resolve("notes.txt"), "drools.evaluator.x=y\n");

        assertThat(CustomOperators.discover(Set.of(plainDir, notAJar, dir.resolve("missing"))))
                .isEmpty();
        assertThat(CustomOperators.discover(null)).isEmpty();
    }

    /** After registration the DRL10 parser accepts {@code ##id}, as the engine does. */
    @Test
    void registeredIdsParseWithThePrefix(@TempDir Path dir) throws Exception {
        Path classes = classDirDeclaring(dir, "META-INF/kie.properties.conf", "supersetOf");

        assertThat(CustomOperators.register(Set.of(classes))).containsExactly("supersetOf");

        assertThat(CustomOperators.isRegistered("supersetOf")).isTrue();
        assertThat(CustomOperators.isRegistered("neverDeclaredAnywhere")).isFalse();
        assertThat(DRLDiagnosticHelper.parse(
                "package p;\nrule R when Person( name ##supersetOf \"x\" ) then end\n").diagnostics)
                .isEmpty();
    }
}
