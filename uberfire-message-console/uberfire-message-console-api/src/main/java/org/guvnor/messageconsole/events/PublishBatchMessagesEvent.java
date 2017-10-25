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

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PublishBatchMessagesEvent extends PublishBaseEvent {

    /**
     * If true, existing messages that full fills publication parameters will deleted prior to publication.
     */
    private boolean cleanExisting = false;

    /**
     * Makes sense only when clean is cleanExisting = true.
     */
    private String messageType;

    /**
     * List of messages to selective unpublish. This messages will allways be unpublished independent of cleanExisting value.
     */
    private List<SystemMessage> messagesToUnpublish = new ArrayList<SystemMessage>();

    public PublishBatchMessagesEvent() {
        //needed for marshalling.
    }

    public boolean isCleanExisting() {
        return cleanExisting;
    }

    public void setCleanExisting(boolean cleanExisting) {
        this.cleanExisting = cleanExisting;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public List<SystemMessage> getMessagesToUnpublish() {
        return messagesToUnpublish;
    }

    public void setMessagesToUnpublish(List<SystemMessage> messagesToUnpublish) {
        this.messagesToUnpublish = messagesToUnpublish;
    }
}
