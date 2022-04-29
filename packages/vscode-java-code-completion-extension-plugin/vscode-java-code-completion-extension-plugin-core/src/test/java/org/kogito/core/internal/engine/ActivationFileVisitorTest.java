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
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ActivationFileVisitorTest {

    private ActivatorFileVisitor activatorFileVisitor;

    @BeforeEach
    public void setUp() {
        activatorFileVisitor = new ActivatorFileVisitor();
    }

    @Test
    void testContainsActivator() throws IOException {
        Path workspace = Paths.get("src/test/resources/testProject");
        Files.walkFileTree(workspace.toAbsolutePath(), this.activatorFileVisitor);
        assertThat(this.activatorFileVisitor.isPresent()).isTrue();
    }
    @Test
    void testDoesntContainsActivator() {
        Path workspace = Paths.get("src/test/resources/testProject/empty");
        assertThrows(NoSuchFileException.class,
                     () -> Files.walkFileTree(workspace.toAbsolutePath(), this.activatorFileVisitor));
    }

    @Test
    void testContainsInvalidActivator() {
        Path workspace = Paths.get("src/test/resources/testProject/invalid");
        assertThat(this.activatorFileVisitor.isPresent()).isFalse();
    }

}
