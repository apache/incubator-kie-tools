/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.kogito.core.internal.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ActivationFileVisitorTest {

    private ActivatorFileVisitor activatorFileVisitor;

    @BeforeEach
    public void setUp() {

        activatorFileVisitor = new ActivatorFileVisitor();
    }

    @Test
    public void testContainsActivator() throws IOException {
        Path workspace = Paths.get("src/test/resources/testProject");
        Files.walkFileTree(workspace.toAbsolutePath(), this.activatorFileVisitor);
        assertThat(this.activatorFileVisitor.isPresent()).isTrue();
    }

    @Test
    public void testContainsActivatorImport() {
        boolean present = this.activatorFileVisitor.containsActivator(ActivatorFileVisitor.IMPORT_ACTIVATOR);
        assertThat(present).isTrue();
    }

    @Test
    public void testContainsActivatorAnnotation() {
        boolean present = this.activatorFileVisitor.containsActivator(ActivatorFileVisitor.ANNOTATION_ACTIVATOR);
        assertThat(present).isTrue();
    }
}
