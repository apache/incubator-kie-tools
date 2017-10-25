/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.messageconsole.events;

import org.guvnor.common.services.project.builder.model.BuildMessage;

public class MessageUtils {

    public static final String BUILD_SYSTEM_MESSAGE = "BuildSystem";

    public static SystemMessage convert(BuildMessage buildMessage) {

        SystemMessage systemMessage = new SystemMessage();

        systemMessage.setMessageType(BUILD_SYSTEM_MESSAGE);
        systemMessage.setId(buildMessage.getId());
        systemMessage.setLevel(buildMessage.getLevel());
        systemMessage.setColumn(buildMessage.getColumn());
        systemMessage.setLine(buildMessage.getLine());
        systemMessage.setText(buildMessage.getText());
        systemMessage.setPath(buildMessage.getPath());
        return systemMessage;
    }
}
