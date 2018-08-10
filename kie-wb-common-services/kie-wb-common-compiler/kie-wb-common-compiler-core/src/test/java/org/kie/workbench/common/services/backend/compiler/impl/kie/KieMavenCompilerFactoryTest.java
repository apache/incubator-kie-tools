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
package org.kie.workbench.common.services.backend.compiler.impl.kie;

import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.BaseMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.ClasspathDepsAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.JGITCompilerBeforeDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.KieAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.OutputLogAfterDecorator;

import static org.assertj.core.api.Assertions.assertThat;

public class KieMavenCompilerFactoryTest {

    @Test
    public void noneTest() {
        AFCompiler none = KieMavenCompilerFactory.getCompiler(KieDecorator.NONE);
        assertThat(none).isInstanceOf(BaseMavenCompiler.class);
    }

    @Test
    public void logOutputAfterDecoratorTest() {
        AFCompiler logAfter = KieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);
        assertThat(logAfter).isInstanceOf(OutputLogAfterDecorator.class);
    }

    @Test
    public void kieAfterDecoratorTest() {
        AFCompiler kieAfter = KieMavenCompilerFactory.getCompiler(KieDecorator.KIE_AFTER);
        assertThat(kieAfter).isInstanceOf(KieAfterDecorator.class);
    }

    @Test
    public void jGitBeforeDecoratorTest() {
        AFCompiler jgitBefore = KieMavenCompilerFactory.getCompiler(KieDecorator.JGIT_BEFORE);
        assertThat(jgitBefore).isInstanceOf(JGITCompilerBeforeDecorator.class);
    }

    @Test
    public void classpathDepsAfterDecoratorTest() {
        AFCompiler classpathAfter = KieMavenCompilerFactory.getCompiler(KieDecorator.CLASSPATH_DEPS_AFTER_DECORATOR);
        assertThat(classpathAfter).isInstanceOf(ClasspathDepsAfterDecorator.class);
    }

    @Test
    public void kieAndLogAfterDecoratorTest() {
        AFCompiler kieAfterDecorator = KieMavenCompilerFactory.getCompiler(KieDecorator.KIE_AND_LOG_AFTER);
        assertThat(kieAfterDecorator).isInstanceOf(KieAfterDecorator.class);
        AFCompiler outputLofAfterDecorator = ((KieAfterDecorator) kieAfterDecorator).getCompiler();
        assertThat(outputLofAfterDecorator).isInstanceOf(OutputLogAfterDecorator.class);
        AFCompiler baseMavenCompiler = ((OutputLogAfterDecorator) outputLofAfterDecorator).getCompiler();
        assertThat(baseMavenCompiler).isInstanceOf(BaseMavenCompiler.class);
    }

    @Test
    public void kieAndClasspathAfterDepsTest() {
        AFCompiler kieAfterDecorator = KieMavenCompilerFactory.getCompiler(KieDecorator.KIE_AND_CLASSPATH_AFTER_DEPS);
        assertThat(kieAfterDecorator).isInstanceOf(KieAfterDecorator.class);
        AFCompiler classpathAfter = ((KieAfterDecorator) kieAfterDecorator).getCompiler();
        assertThat(classpathAfter).isInstanceOf(ClasspathDepsAfterDecorator.class);
        AFCompiler baseMavenCompiler = ((ClasspathDepsAfterDecorator) classpathAfter).getCompiler();
        assertThat(baseMavenCompiler).isInstanceOf(BaseMavenCompiler.class);
    }

    @Test
    public void kieLogAndClasspathDepsAfterTest() {
        AFCompiler kieAfterDecorator = KieMavenCompilerFactory.getCompiler(KieDecorator.KIE_LOG_AND_CLASSPATH_DEPS_AFTER);
        assertThat(kieAfterDecorator).isInstanceOf(KieAfterDecorator.class);
        AFCompiler outputLofAfterDecorator = ((KieAfterDecorator) kieAfterDecorator).getCompiler();
        assertThat(outputLofAfterDecorator).isInstanceOf(OutputLogAfterDecorator.class);
        AFCompiler classpathAfter = ((OutputLogAfterDecorator) outputLofAfterDecorator).getCompiler();
        assertThat(classpathAfter).isInstanceOf(ClasspathDepsAfterDecorator.class);
        AFCompiler baseMavenCompiler = ((ClasspathDepsAfterDecorator) classpathAfter).getCompiler();
        assertThat(baseMavenCompiler).isInstanceOf(BaseMavenCompiler.class);
    }

    @Test
    public void jgitBeforeAndLogAfterDecoratorTest() {
        AFCompiler jgitBeforeAndLogAfter = KieMavenCompilerFactory.getCompiler(KieDecorator.JGIT_BEFORE_AND_LOG_AFTER);
        assertThat(jgitBeforeAndLogAfter).isInstanceOf(JGITCompilerBeforeDecorator.class);
        AFCompiler outputLofAfterDecorator = ((JGITCompilerBeforeDecorator) jgitBeforeAndLogAfter).getCompiler();
        assertThat(outputLofAfterDecorator).isInstanceOf(OutputLogAfterDecorator.class);
        AFCompiler baseMavenCompiler = ((OutputLogAfterDecorator) outputLofAfterDecorator).getCompiler();
        assertThat(baseMavenCompiler).isInstanceOf(BaseMavenCompiler.class);
    }

    @Test
    public void jgitBeforeAndKieAfterDecoratorTest() {
        AFCompiler jgitBeforeAndLogAfter = KieMavenCompilerFactory.getCompiler(KieDecorator.JGIT_BEFORE_AND_KIE_AFTER);
        assertThat(jgitBeforeAndLogAfter).isInstanceOf(JGITCompilerBeforeDecorator.class);
        AFCompiler kieAfterDecorator = ((JGITCompilerBeforeDecorator) jgitBeforeAndLogAfter).getCompiler();
        assertThat(kieAfterDecorator).isInstanceOf(KieAfterDecorator.class);
        AFCompiler baseMavenCompiler = ((KieAfterDecorator) kieAfterDecorator).getCompiler();
        assertThat(baseMavenCompiler).isInstanceOf(BaseMavenCompiler.class);
    }

    @Test
    public void jgitBeforeAndKieAndLogAfterDecoratorTest() {
        AFCompiler jgitBeforeAndLogAfter = KieMavenCompilerFactory.getCompiler(KieDecorator.JGIT_BEFORE_AND_KIE_AND_LOG_AFTER);
        assertThat(jgitBeforeAndLogAfter).isInstanceOf(JGITCompilerBeforeDecorator.class);
        AFCompiler kieAfterDecorator = ((JGITCompilerBeforeDecorator) jgitBeforeAndLogAfter).getCompiler();
        assertThat(kieAfterDecorator).isInstanceOf(KieAfterDecorator.class);
        AFCompiler outputLofAfterDecorator = ((KieAfterDecorator) kieAfterDecorator).getCompiler();
        assertThat(outputLofAfterDecorator).isInstanceOf(OutputLogAfterDecorator.class);
        AFCompiler baseMavenCompiler = ((OutputLogAfterDecorator) outputLofAfterDecorator).getCompiler();
        assertThat(baseMavenCompiler).isInstanceOf(BaseMavenCompiler.class);
    }

    @Test
    public void jgitBeforeAndKieAndLogAndClasspathAfterTest() {
        AFCompiler jgitBeforeAndLogAfter = KieMavenCompilerFactory.getCompiler(KieDecorator.JGIT_BEFORE_AND_KIE_AND_LOG_AND_CLASSPATH_AFTER);
        assertThat(jgitBeforeAndLogAfter).isInstanceOf(JGITCompilerBeforeDecorator.class);
        AFCompiler kieAfterDecorator = ((JGITCompilerBeforeDecorator) jgitBeforeAndLogAfter).getCompiler();
        assertThat(kieAfterDecorator).isInstanceOf(KieAfterDecorator.class);
        AFCompiler outputLofAfterDecorator = ((KieAfterDecorator) kieAfterDecorator).getCompiler();
        assertThat(outputLofAfterDecorator).isInstanceOf(OutputLogAfterDecorator.class);
        AFCompiler classpathAfter = ((OutputLogAfterDecorator) outputLofAfterDecorator).getCompiler();
        assertThat(classpathAfter).isInstanceOf(ClasspathDepsAfterDecorator.class);
        AFCompiler baseMavenCompiler = ((ClasspathDepsAfterDecorator) classpathAfter).getCompiler();
        assertThat(baseMavenCompiler).isInstanceOf(BaseMavenCompiler.class);
    }
}
