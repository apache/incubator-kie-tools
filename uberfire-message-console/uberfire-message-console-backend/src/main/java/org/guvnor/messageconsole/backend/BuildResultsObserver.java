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

package org.guvnor.messageconsole.backend;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.messageconsole.events.MessageUtils;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;

/**
 * Observer component translates BuildResult and IncrementalBuildResults messages to messages that can be printed
 * on the Message Console.
 */
@ApplicationScoped
public class BuildResultsObserver {

    @Inject
    private Event<PublishBatchMessagesEvent> publishBatchMessagesEvent;

    public void addBuildMessages(final @Observes BuildResults results) {

        PublishBatchMessagesEvent batchMessages = new PublishBatchMessagesEvent();
        batchMessages.setCleanExisting(true);
        batchMessages.setMessageType(MessageUtils.BUILD_SYSTEM_MESSAGE);

        if (results.getMessages() != null) {
            for (BuildMessage buildMessage : results.getMessages()) {
                batchMessages.getMessagesToPublish().add(MessageUtils.convert(buildMessage));
            }
        }

        publishBatchMessagesEvent.fire(batchMessages);
    }

    public void addIncrementalBuildMessages(final @Observes IncrementalBuildResults results) {

        PublishBatchMessagesEvent batchMessages = new PublishBatchMessagesEvent();
        batchMessages.setMessageType(MessageUtils.BUILD_SYSTEM_MESSAGE);

        if (results.getAddedMessages() != null) {
            for (BuildMessage buildMessage : results.getAddedMessages()) {
                batchMessages.getMessagesToPublish().add(MessageUtils.convert(buildMessage));
            }
        }

        if (results.getRemovedMessages() != null) {
            for (BuildMessage buildMessage : results.getRemovedMessages()) {
                batchMessages.getMessagesToUnpublish().add(MessageUtils.convert(buildMessage));
            }
        }

        publishBatchMessagesEvent.fire(batchMessages);
    }
}
