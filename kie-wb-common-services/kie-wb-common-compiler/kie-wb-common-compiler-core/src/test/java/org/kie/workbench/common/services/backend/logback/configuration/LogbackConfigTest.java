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
package org.kie.workbench.common.services.backend.logback.configuration;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.Encoder;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;
import org.kie.workbench.common.services.backend.logback.appender.KieSiftingAppender;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class LogbackConfigTest {

    LoggerContext loggerContext = new LoggerContext();

    @Test
    public void configureLoggingProgrammatically() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        LogbackConfig config = new LogbackConfig();
        config.configure(loggerContext);
        root.info("test appender");
        Appender<ILoggingEvent> kieSift = root.getAppender("KieSift");
        assertThat(kieSift).isNotNull();
        KieSiftingAppender kieSiftAppender = (KieSiftingAppender) kieSift;
        assertThat(kieSiftAppender).isNotNull();
        assertThat(kieSiftAppender.getDiscriminator()).isNotNull();
        assertThat(MavenConfig.COMPILATION_ID).isEqualTo(kieSiftAppender.getDiscriminatorKey());
        Appender<ILoggingEvent> consoleAppenderGeneric = root.getAppender("consoleAppender");
        ConsoleAppender consoleAppender = (ConsoleAppender) consoleAppenderGeneric;
        assertThat(consoleAppender).isNotNull();
        Encoder enc = consoleAppender.getEncoder();
        PatternLayoutEncoder encoder = (PatternLayoutEncoder) enc;
        assertThat(encoder.getPattern()).isEqualTo("%d [%thread] %level %logger{35} - %msg%n");
    }
}
