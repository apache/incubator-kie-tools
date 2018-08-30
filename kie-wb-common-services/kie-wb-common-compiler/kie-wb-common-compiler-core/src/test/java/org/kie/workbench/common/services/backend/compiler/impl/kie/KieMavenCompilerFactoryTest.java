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

import java.util.EnumSet;
import java.util.HashSet;

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
        final AFCompiler none = KieMavenCompilerFactory.getCompiler(new HashSet<>());
        assertThat(none).isInstanceOf(BaseMavenCompiler.class);
    }

    @Test
    public void logOutputAfterDecoratorTest() {
        final AFCompiler logAfter = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.ENABLE_LOGGING ));
        assertThat(logAfter).isInstanceOf(OutputLogAfterDecorator.class);
    }

    @Test
    public void kieAfterDecoratorTest() {
        final AFCompiler kieAfter = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.STORE_KIE_OBJECTS));
        assertThat(kieAfter).isInstanceOf(KieAfterDecorator.class);
    }

    @Test
    public void jGitBeforeDecoratorTest() {
        final AFCompiler jgitBefore = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.UPDATE_JGIT_BEFORE_BUILD ));
        assertThat(jgitBefore).isInstanceOf(JGITCompilerBeforeDecorator.class);
    }

    @Test
    public void classpathDepsAfterDecoratorTest() {
        final AFCompiler classpathAfter = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.STORE_BUILD_CLASSPATH));
        assertThat(classpathAfter).isInstanceOf(ClasspathDepsAfterDecorator.class);
    }

    @Test
    public void kieAndLogAfterDecoratorTest() {
        final AFCompiler kieAfterDecorator = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.STORE_KIE_OBJECTS, KieDecorator.ENABLE_LOGGING ));
        assertThat(kieAfterDecorator).isInstanceOf(KieAfterDecorator.class);
        AFCompiler outputLofAfterDecorator = ((KieAfterDecorator) kieAfterDecorator).getCompiler();
        assertThat(outputLofAfterDecorator).isInstanceOf(OutputLogAfterDecorator.class);
        AFCompiler baseMavenCompiler = ((OutputLogAfterDecorator) outputLofAfterDecorator).getCompiler();
        assertThat(baseMavenCompiler).isInstanceOf(BaseMavenCompiler.class);
    }

    @Test
    public void kieAndClasspathAfterDepsTest() {
        final AFCompiler kieAfterDecorator = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.STORE_KIE_OBJECTS, KieDecorator.STORE_BUILD_CLASSPATH ));
        assertThat(kieAfterDecorator).isInstanceOf(KieAfterDecorator.class);
        AFCompiler classpathAfter = ((KieAfterDecorator) kieAfterDecorator).getCompiler();
        assertThat(classpathAfter).isInstanceOf(ClasspathDepsAfterDecorator.class);
        AFCompiler baseMavenCompiler = ((ClasspathDepsAfterDecorator) classpathAfter).getCompiler();
        assertThat(baseMavenCompiler).isInstanceOf(BaseMavenCompiler.class);
    }

    @Test
    public void kieLogAndClasspathDepsAfterTest() {
        final AFCompiler kieAfterDecorator = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.STORE_KIE_OBJECTS, KieDecorator.ENABLE_LOGGING, KieDecorator.STORE_BUILD_CLASSPATH ));
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
        final AFCompiler jgitBeforeAndLogAfter = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.UPDATE_JGIT_BEFORE_BUILD, KieDecorator.ENABLE_LOGGING ));
        assertThat(jgitBeforeAndLogAfter).isInstanceOf(JGITCompilerBeforeDecorator.class);
        AFCompiler outputLofAfterDecorator = ((JGITCompilerBeforeDecorator) jgitBeforeAndLogAfter).getCompiler();
        assertThat(outputLofAfterDecorator).isInstanceOf(OutputLogAfterDecorator.class);
        AFCompiler baseMavenCompiler = ((OutputLogAfterDecorator) outputLofAfterDecorator).getCompiler();
        assertThat(baseMavenCompiler).isInstanceOf(BaseMavenCompiler.class);
    }

    @Test
    public void jgitBeforeAndKieAfterDecoratorTest() {
        final AFCompiler jgitBeforeAndLogAfter = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.UPDATE_JGIT_BEFORE_BUILD, KieDecorator.STORE_KIE_OBJECTS ));
        assertThat(jgitBeforeAndLogAfter).isInstanceOf(JGITCompilerBeforeDecorator.class);
        AFCompiler kieAfterDecorator = ((JGITCompilerBeforeDecorator) jgitBeforeAndLogAfter).getCompiler();
        assertThat(kieAfterDecorator).isInstanceOf(KieAfterDecorator.class);
        AFCompiler baseMavenCompiler = ((KieAfterDecorator) kieAfterDecorator).getCompiler();
        assertThat(baseMavenCompiler).isInstanceOf(BaseMavenCompiler.class);
    }

    @Test
    public void jgitBeforeAndKieAndLogAfterDecoratorTest() {
        final AFCompiler jgitBeforeAndLogAfter = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.UPDATE_JGIT_BEFORE_BUILD, KieDecorator.STORE_KIE_OBJECTS, KieDecorator.ENABLE_LOGGING ));
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
        AFCompiler jgitBeforeAndLogAfter = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.UPDATE_JGIT_BEFORE_BUILD, KieDecorator.STORE_KIE_OBJECTS, KieDecorator.ENABLE_LOGGING, KieDecorator.STORE_BUILD_CLASSPATH ));
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
