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

import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;
import org.kie.workbench.common.services.backend.logback.OutputSharedMap;

/**
 * This appender add the message received to the buffer with the compileID founded in the MDC
 */
public class KieSiftingAppender extends SiftingAppender {

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!eventObject.getMDCPropertyMap().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(eventObject.getLevel()).append(" ").append(eventObject.getFormattedMessage());
            OutputSharedMap.addMsgToLog(eventObject.getMDCPropertyMap().get(MavenConfig.COMPILATION_ID), sb.toString());
        }
    }
}
