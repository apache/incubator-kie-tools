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

import java.io.IOException;

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

public class ClasspathDepsAfterDecoratorTest extends BaseCompilerTest {

    public ClasspathDepsAfterDecoratorTest() {
        super(ResourcesConstants.DUMMY_DEPS_SIMPLE);
    }

    @AfterClass
    public static void tearDown() {
        BaseCompilerTest.tearDown();
    }

    @Test
    public void compileTest() {

        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{
                                                                       MavenCLIArgs.INSTALL,
                                                                       MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath
                                                               },
                                                               Boolean.FALSE);

        ClasspathDepsAfterDecorator decorator = new ClasspathDepsAfterDecorator(new BaseMavenCompiler());
        CompilationResponse res = decorator.compile(req);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(res.isSuccessful()).isTrue();
            softly.assertThat(res.getDependencies()).hasSize(4);
        });
    }

    @Test
    public void failCompileTest() throws IOException {
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               createdNewPrjInRepo("dummy-fail", ResourcesConstants.DUMMY_FAIL_DEPS_SIMPLE),
                                                               new String[]{
                                                                       MavenCLIArgs.INSTALL,
                                                                       MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath
                                                               },
                                                               Boolean.FALSE);

        ClasspathDepsAfterDecorator decorator = new ClasspathDepsAfterDecorator(new BaseMavenCompiler());
        CompilationResponse res = decorator.compile(req);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(res.isSuccessful()).isFalse();
            softly.assertThat(res.getDependencies()).hasSize(0);
        });
    }
}
