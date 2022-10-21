/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kogito.core.internal.util.TestUtil;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.Optional;

import static org.mockito.Mockito.when;

class ActivationCheckerTest {

    private ActivationFileVisitor activationFileVisitorMock;

    private ActivationChecker activationChecker;

    @BeforeEach
    public void setup() {
        activationFileVisitorMock = Mockito.mock(ActivationFileVisitor.class);
        this.activationChecker = new ActivationChecker();
    }

    @Test
    void getActivatorPathWithoutSpecialChars() {
        TestUtil.mockWorkspace("src/test/resources/testProject/", () -> activationChecker.check());
        var activatorUri = activationChecker.getActivatorPath();
        Assertions.assertNotNull(activatorUri);
        Assertions.assertTrue(activatorUri.endsWith("Activator.java"));
    }

    @Test
    void getActivatorPathWithSpecialChars() {
        TestUtil.mockWorkspace("src/test/resources/test projéct", () -> activationChecker.check());
        var activatorUri = activationChecker.getActivatorPath();
        System.out.println(activatorUri);
        var path = activatorUri.toUri().toASCIIString();
        //Assertions.assertTrue(activatorUri.toUri().toASCIIString().contains("test%20proj%C3%A9ct"));
        Assertions.assertTrue(activatorUri.endsWith("Activator.java"));
    }

}
