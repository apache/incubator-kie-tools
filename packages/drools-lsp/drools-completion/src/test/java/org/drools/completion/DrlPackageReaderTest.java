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

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DrlPackageReaderTest {

    // ── reading the declaration ──────────────────────────────────────────────

    @Test
    void readsPackageTerminatedBySemicolon() {
        assertThat(DrlPackageReader.declaredPackage("package com.example.rules;\n\nrule \"r\" when then end"))
                .isEqualTo("com.example.rules");
    }

    @Test
    void readsPackageTerminatedByNewlineWhenSemicolonIsOmitted() {
        assertThat(DrlPackageReader.declaredPackage("package com.example.rules\n\nrule \"r\" when then end"))
                .isEqualTo("com.example.rules");
    }

    @Test
    void skipsLeadingLineComments() {
        String drl = "// package com.commented.out;\npackage com.example.real;\n";

        assertThat(DrlPackageReader.declaredPackage(drl)).isEqualTo("com.example.real");
    }

    @Test
    void skipsLeadingBlockComments() {
        String drl = "/*\n * package com.commented.out;\n */\npackage com.example.real;\n";

        assertThat(DrlPackageReader.declaredPackage(drl)).isEqualTo("com.example.real");
    }

    @Test
    void ignoresPackageAppearingInsideAStringLiteral() {
        String drl = "rule \"say package com.fake;\"\nwhen then end\npackage com.example.real;\n";

        assertThat(DrlPackageReader.declaredPackage(drl)).isEqualTo("com.example.real");
    }

    @Test
    void doesNotMatchAnIdentifierThatMerelyStartsWithPackage() {
        assertThat(DrlPackageReader.declaredPackage("packageName = 3;\n")).isNull();
    }

    @Test
    void returnsNullWhenThereIsNoDeclaration() {
        assertThat(DrlPackageReader.declaredPackage("rule \"r\" when then end")).isNull();
        assertThat(DrlPackageReader.declaredPackage("")).isNull();
    }

    // ── deriving a package from the file's location ──────────────────────────

    @Test
    void derivesPackageFromPathBelowAResourcesRoot() {
        Path drl = Path.of("C:/ws/mod/src/main/resources/com/example/rules/A.drl");

        assertThat(DrlPackageReader.packageFromPath(drl, Path.of("C:/ws")))
                .isEqualTo("com.example.rules");
    }

    @Test
    void derivesPackageFromPathBelowABuildOutputRoot() {
        Path drl = Path.of("C:/ws/mod/target/classes/com/example/rules/A.drl");

        assertThat(DrlPackageReader.packageFromPath(drl, Path.of("C:/ws")))
                .isEqualTo("com.example.rules");
    }

    @Test
    void fallsBackToTheWorkspaceRelativePathWhenNoResourcesRootIsPresent() {
        Path drl = Path.of("C:/ws/rules/com/example/A.drl");

        assertThat(DrlPackageReader.packageFromPath(drl, Path.of("C:/ws")))
                .isEqualTo("rules.com.example");
    }

    @Test
    void aFileDirectlyAtTheResourcesRootHasTheDefaultPackage() {
        Path drl = Path.of("C:/ws/mod/src/main/resources/A.drl");

        assertThat(DrlPackageReader.packageFromPath(drl, Path.of("C:/ws"))).isEmpty();
    }
}
