/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.builder.core;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.api.builder.Message;
import org.kie.internal.builder.InternalMessage;

import static org.kie.workbench.common.services.backend.builder.core.BaseFileNameResolver.getBaseFileName;

class MessageConverter {

    static List<BuildMessage> convertMessages(List<Message> messages,
                                              Handles handles) {
        List<BuildMessage> result = new ArrayList<BuildMessage>();

        if (!(messages == null || messages.isEmpty())) {
            for (Message message : messages) {
                result.add(convertMessage(message,
                                          handles));
            }
        }

        return result;
    }

    static List<BuildMessage> convertValidationMessages(List<ValidationMessage> validationMessages) {
        List<BuildMessage> result = new ArrayList<BuildMessage>();

        if (!(validationMessages == null || validationMessages.isEmpty())) {
            for (ValidationMessage validationMessage : validationMessages) {
                result.add(convertValidationMessage(validationMessage));
            }
        }

        return result;
    }

    static BuildMessage convertValidationMessage(final ValidationMessage message) {
        final BuildMessage m = new BuildMessage();
        m.setLevel(message.getLevel());
        m.setId(message.getId());
        m.setLine(message.getLine());
        m.setColumn(message.getColumn());
        m.setText(message.getText());
        m.setPath(message.getPath());
        return m;
    }

    static BuildMessage convertMessage(final Message message,
                                       Handles handles) {
        final BuildMessage m = new BuildMessage();
        switch (message.getLevel()) {
            case ERROR:
                m.setLevel(Level.ERROR);
                break;
            case WARNING:
                m.setLevel(Level.WARNING);
                break;
            case INFO:
                m.setLevel(Level.INFO);
                break;
        }

        m.setId(message.getId());
        m.setLine(message.getLine());
        m.setPath(convertPath(message.getPath(),
                              handles));
        m.setColumn(message.getColumn());
        m.setText(convertMessageText(message));
        return m;
    }

    private static String convertMessageText(final Message message) {
        final StringBuilder sb = new StringBuilder();
        if (message instanceof InternalMessage) {
            final InternalMessage impl = (InternalMessage) message;
            final String kieBaseName = impl.getKieBaseName();
            if (!(kieBaseName == null || kieBaseName.isEmpty())) {
                sb.append("[KBase: ").append(kieBaseName).append("]: ");
            }
        }
        sb.append(message.getText());
        return sb.toString();
    }

    private static org.uberfire.backend.vfs.Path convertPath(String kieBuilderPath,
                                                             Handles handles) {
        if (kieBuilderPath == null || kieBuilderPath.isEmpty()) {
            return null;
        }

        //look for a resource related path
        org.uberfire.backend.vfs.Path path = handles.get(Handles.RESOURCE_PATH + "/" + getBaseFileName(kieBuilderPath));
        if (path == null) {
            //give a second chance, it might be a java resource error. Java paths has the form src/main/java/org/File.java
            path = handles.get(getBaseFileName(kieBuilderPath));
        }
        return path;
    }
}
