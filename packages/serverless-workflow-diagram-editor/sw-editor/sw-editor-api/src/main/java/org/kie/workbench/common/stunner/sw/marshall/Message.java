/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.sw.marshall;

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

    @Override
    public String toString() {
        return messageCode.replaceAll("%s", value);
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
