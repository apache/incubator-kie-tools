/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.sift.AppenderFactory;
import ch.qos.logback.core.sift.Discriminator;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.StatusPrinter;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;
import org.kie.workbench.common.services.backend.logback.appender.KieSiftingAppender;
import org.kie.workbench.common.services.backend.logback.appender.UUIDThreadNameDiscriminator;

/**
 * Class to configure programmatically Logback with the ch.qos.logback.classic.spi.Configurator into
 * the META-INF/services, it works when logabck.xml and logback-test.xml aren't present in the classpath
 */
public class LogbackConfig extends ContextAwareBase implements Configurator {

    public LogbackConfig() { }

    @Override
    public void configure(LoggerContext loggerContext) {
        setContext(loggerContext);
        addInfo("Configure logging programmatically with with org.kie.workbench.common.services.backend.logback.configuration.LogbackConfig");

        UUIDThreadNameDiscriminator discriminator = getDiscriminator();
        PatternLayoutEncoder pl = getPatternLayoutEncoder();

        ConsoleAppender consoleAppender = setConsoleAppender(loggerContext, pl);
        SiftingAppender appender = setSiftingAppender(loggerContext, discriminator);

        Logger log = loggerContext.getLogger("root");
        log.setLevel(Level.INFO);
        log.addAppender(appender);
        log.addAppender(consoleAppender);

        Logger mavenLog = loggerContext.getLogger("org.apache.maven");
        mavenLog.setLevel(Level.INFO);

        Logger droolsLog = loggerContext.getLogger("org.drools");
        droolsLog.setLevel(Level.INFO);

        Logger kieLog = loggerContext.getLogger("org.kie");
        kieLog.setLevel(Level.INFO);

        Logger compilerLog = loggerContext.getLogger("org.kie.workbench.common.services.backend.compiler");
        compilerLog.setLevel(Level.INFO);

        StatusPrinter.print(loggerContext);
    }

    private UUIDThreadNameDiscriminator getDiscriminator() {
        UUIDThreadNameDiscriminator discriminator = new UUIDThreadNameDiscriminator();
        discriminator.start();
        return discriminator;
    }

    private PatternLayoutEncoder getPatternLayoutEncoder() {
        PatternLayoutEncoder pl = new PatternLayoutEncoder();
        pl.setContext(context);
        pl.setPattern("%d [%thread] %level %logger{35} - %msg%n");
        pl.start();
        return pl;
    }

    private SiftingAppender setSiftingAppender(LoggerContext loggerContext, Discriminator discriminator) {
        KieSiftingAppender appender = new KieSiftingAppender();
        appender.setName(MavenConfig.COMPILATION_ID);
        appender.setDiscriminator(discriminator);
        appender.setAppenderFactory(new AppenderFactory<ILoggingEvent>() {

            @Override
            public Appender<ILoggingEvent> buildAppender(Context context, String discriminatingValue) throws JoranException {
                KieSiftingAppender kieAppender = new KieSiftingAppender();
                kieAppender.setName(discriminatingValue);
                kieAppender.setContext(loggerContext);
                return kieAppender;
            }
        });

        appender.setContext(loggerContext);
        appender.start();
        return appender;
    }

    private ConsoleAppender setConsoleAppender(LoggerContext loggerContext, PatternLayoutEncoder pl) {
        ConsoleAppender consoleAppender = new ConsoleAppender<>();
        consoleAppender.setName("consoleAppender");
        consoleAppender.setContext(loggerContext);
        consoleAppender.setEncoder(pl);
        consoleAppender.start();
        return consoleAppender;
    }
}
