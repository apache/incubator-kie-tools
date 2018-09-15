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
package org.kie.workbench.common.services.backend.compiler.impl.decorators;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.SoftAssertions;
import org.junit.AfterClass;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.BaseCompilerTest;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.BaseMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.constants.ResourcesConstants;
import org.uberfire.java.nio.file.Path;

public class OutputLogAfterDecoratorTest extends BaseCompilerTest {

    public OutputLogAfterDecoratorTest() {
        super(ResourcesConstants.DUMMY);
    }

    @AfterClass
    public static void tearDown() {
        BaseCompilerTest.tearDown();
    }

    @Test
    public void compileTest() {

        CompilationRequest req = new DefaultCompilationRequest(mavenRepoPath,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);

        OutputLogAfterDecorator decorator = new OutputLogAfterDecorator(new BaseMavenCompiler(true,true));
        CompilationResponse res = decorator.compile(req);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(res.isSuccessful()).isTrue();
            softly.assertThat(res.getMavenOutput().size()).isGreaterThan(0);
        });
    }

    @Test
    public void compileWithOverrideTest() throws Exception {

        Map<Path, InputStream> override = new HashMap<>();
        org.uberfire.java.nio.file.Path path = org.uberfire.java.nio.file.Paths.get(tmpRoot + "/src/main/java/dummy/DummyOverride.java");
        InputStream input = new FileInputStream(new File(ResourcesConstants.DUMMY_OVERRIDE + "/src/main/java/dummy/DummyOverride.java"));
        override.put(path, input);

        CompilationRequest req = new DefaultCompilationRequest(mavenRepoPath,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);

        OutputLogAfterDecorator decorator = new OutputLogAfterDecorator(new BaseMavenCompiler(true,true));
        CompilationResponse res = decorator.compile(req, override);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(res.isSuccessful()).isTrue();
            softly.assertThat(res.getMavenOutput().size()).isGreaterThan(0);
        });
    }

    @Test
    public void compileFailedTest() throws Exception {
        CompilationRequest req = new DefaultCompilationRequest(mavenRepoPath,
                                                               createdNewPrjInRepo("dummy-fail", ResourcesConstants.DUMMY_FAIL_DEPS_SIMPLE),
                                                               new String[]{MavenCLIArgs.COMPILE, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);

        OutputLogAfterDecorator decorator = new OutputLogAfterDecorator(new BaseMavenCompiler(true,true));
        CompilationResponse res = decorator.compile(req);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(res.isSuccessful()).isFalse();
            softly.assertThat(res.getMavenOutput().size()).isGreaterThan(0);
        });
    }
}
