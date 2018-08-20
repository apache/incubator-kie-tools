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
package org.kie.workbench.common.services.backend.compiler.impl.pomprocessor;

import java.util.HashSet;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.BaseCompilerTest;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.configuration.ConfigurationContextProvider;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.constants.ResourcesConstants;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultPomEditorTest extends BaseCompilerTest {

    public DefaultPomEditorTest() {
        super(ResourcesConstants.KJAR_2_SINGLE_RESOURCES);
    }

    private DefaultPomEditor editor;

    @Before
    public void setUp() {
        ConfigurationContextProvider provider = new ConfigurationContextProvider();
        editor = new DefaultPomEditor(new HashSet<>(), provider);
    }

    @Test
    public void readSingleTest() {
        assertThat(editor.getHistory()).isEmpty();
        PomPlaceHolder placeholder = editor.readSingle(Paths.get(tmpRoot.toAbsolutePath() + "/dummy/pom.xml"));
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(placeholder.isValid()).isTrue();
            softly.assertThat(placeholder.getVersion()).isEqualTo("1.0.0.Final");
            softly.assertThat(placeholder.getPackaging()).isEqualTo(("kjar"));
            softly.assertThat(placeholder.getGroupID()).isEqualTo("org.kie");
            softly.assertThat(placeholder.getArtifactID()).isEqualTo("kie-maven-plugin-test-kjar-2");
        });
    }

    @Test
    public void writeTest() {
        assertThat(editor.getHistory()).isEmpty();
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.INSTALL, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);

        assertThat(editor.write(Paths.get(tmpRoot.toAbsolutePath() + "/dummy/pom.xml"), req)).isTrue();
    }

    @Test
    public void cleanHistoryTest() {
        assertThat(editor.getHistory()).isEmpty();
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.INSTALL, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);

        editor.write(Paths.get(tmpRoot.toAbsolutePath() + "/dummy/pom.xml"), req);
        assertThat(editor.getHistory()).isNotEmpty();

        editor.cleanHistory();
        assertThat(editor.getHistory()).isEmpty();
    }
}
