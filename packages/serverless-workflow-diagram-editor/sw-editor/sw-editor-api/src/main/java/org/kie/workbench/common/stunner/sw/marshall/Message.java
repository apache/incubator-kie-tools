/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.sw.marshall;

import org.kie.j2cl.tools.di.ui.translation.client.TranslationService;

public class Message {

    private final String messageCode;

    private final String value;

    public Message(String messageCode, String value) {
        this.messageCode = messageCode;
        this.value = value;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public String getValue() {
        return value;
    }

    public String translateMessage(TranslationService translationService) {
        return translationService.getTranslation(messageCode).replaceAll("%s", value);
    }

    @Override
    public String toString() {
        return "Message: {\"messageCode\": " + messageCode + ", \"value\": " + value + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }

        Message message = (Message) o;

        if (getMessageCode() != null ? !getMessageCode().equals(message.getMessageCode()) : message.getMessageCode() != null) {
            return false;
        }
        return getValue() != null ? getValue().equals(message.getValue()) : message.getValue() == null;
    }

    @Override
    public int hashCode() {
        int result = getMessageCode() != null ? getMessageCode().hashCode() : 0;
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        return result;
    }
}
