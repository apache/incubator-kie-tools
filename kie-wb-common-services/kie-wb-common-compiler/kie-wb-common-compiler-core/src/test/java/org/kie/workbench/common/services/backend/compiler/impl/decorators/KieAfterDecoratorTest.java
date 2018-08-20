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
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.BaseCompilerTest;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.BaseMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.constants.ResourcesConstants;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class KieAfterDecoratorTest extends BaseCompilerTest {

    public KieAfterDecoratorTest() {
        super(ResourcesConstants.KJAR_2_SINGLE_RESOURCES);
    }

    @Test
    public void compileTest() {

        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.INSTALL, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);

        KieAfterDecorator decorator = new KieAfterDecorator(new BaseMavenCompiler());
        KieCompilationResponse kieRes = (KieCompilationResponse) decorator.compile(req);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(kieRes.isSuccessful()).isTrue();
            softly.assertThat(kieRes.getMavenOutput()).isEmpty();
            softly.assertThat(kieRes.getKieModule()).isNotNull();
            softly.assertThat(kieRes.getKieModuleMetaInfo()).isNotNull();
        });
    }

    @Test
    public void compileWithOverrideTest() throws Exception {

        Map<Path, InputStream> override = new HashMap<>();
        Path path = Paths.get(tmpRoot + "/src/main/java/org/kie/maven/plugin/test/Person.java");
        InputStream input = new FileInputStream(new File(ResourcesConstants.KJAR_2_SINGLE_RESOURCES_OVERRIDE + "/src/main/java/org/kie/maven/plugin/test/Person.java"));
        override.put(path, input);

        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.INSTALL, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);

        KieAfterDecorator decorator = new KieAfterDecorator(new BaseMavenCompiler());
        KieCompilationResponse kieRes = (KieCompilationResponse) decorator.compile(req, override);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(kieRes.isSuccessful()).isTrue();
            softly.assertThat(kieRes.getMavenOutput()).isEmpty();
            softly.assertThat(kieRes.getKieModule()).isNotNull();
            softly.assertThat(kieRes.getKieModuleMetaInfo()).isNotNull();
        });
    }

    @Test
    public void compileWithouKieMavenPlugin() throws Exception {
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               createdNewPrjInRepo("normal-dummy", ResourcesConstants.DUMMY),
                                                               new String[]{MavenCLIArgs.INSTALL, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);

        KieAfterDecorator decorator = new KieAfterDecorator(new BaseMavenCompiler());
        KieCompilationResponse kieRes = (KieCompilationResponse) decorator.compile(req);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(kieRes.isSuccessful()).isTrue();
            softly.assertThat(kieRes.getMavenOutput()).isEmpty();
            softly.assertThat(kieRes.getKieModule()).isNotNull();
            softly.assertThat(kieRes.getKieModuleMetaInfo()).isNotNull();
        });
    }

    @Test
    public void compileWithFailedResponse() throws Exception {
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               createdNewPrjInRepo("kjar-2-fail", ResourcesConstants.KJAR_2_SINGLE_FAIL_RESOURCES),
                                                               new String[]{MavenCLIArgs.INSTALL, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);

        KieAfterDecorator decorator = new KieAfterDecorator(new BaseMavenCompiler());
        KieCompilationResponse kieRes = (KieCompilationResponse) decorator.compile(req);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(kieRes.isSuccessful()).isFalse();
            softly.assertThat(kieRes.getMavenOutput()).isNotEmpty();
            softly.assertThat(kieRes.getKieModule()).isEmpty();
            softly.assertThat(kieRes.getKieModuleMetaInfo()).isEmpty();
        });
    }
}
