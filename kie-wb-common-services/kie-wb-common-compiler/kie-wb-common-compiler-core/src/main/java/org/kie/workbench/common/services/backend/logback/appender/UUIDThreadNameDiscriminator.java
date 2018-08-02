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
package org.kie.workbench.common.services.backend.logback.appender;

import ch.qos.logback.classic.sift.MDCBasedDiscriminator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;

/***
 * This discriminator use the name of the Thread to choose the store of the messages
 */
public class UUIDThreadNameDiscriminator extends MDCBasedDiscriminator {

    private boolean started;

    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
        /**
         * We using the UUID of the compiler as a threadName and we change its name
         * in the AFMavenCli and in the ReusableAFMavenCli
         * */
        return Thread.currentThread().getName();
    }

    @Override
    public String getKey() {
        return MavenConfig.COMPILATION_ID;
    }

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }

    public boolean isStarted() {
        return started;
    }
}
