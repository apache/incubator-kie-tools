/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.builder.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class IncrementalBuildResults {

    private GAV gav;
    private ArrayList<BuildMessage> addedMessages = new ArrayList<BuildMessage>();
    private ArrayList<BuildMessage> removedMessages = new ArrayList<BuildMessage>();

    public IncrementalBuildResults() {
        //Marshalling
    }

    public IncrementalBuildResults(final GAV gav) {
        this.gav = gav;
    }

    public GAV getGAV() {
        return gav;
    }

    public List<BuildMessage> getAddedMessages() {
        return Collections.unmodifiableList(addedMessages);
    }

    public List<BuildMessage> getRemovedMessages() {
        return Collections.unmodifiableList(removedMessages);
    }

    public void addAddedMessage(final BuildMessage message) {
        this.addedMessages.add(message);
    }

    public void addRemovedMessage(final BuildMessage message) {
        this.removedMessages.add(message);
    }

    public void addAllAddedMessages(List<BuildMessage> buildMessages) {
        addedMessages.addAll(buildMessages);
    }

    public void addAllRemovedMessages(List<BuildMessage> buildMessages) {
        removedMessages.addAll(buildMessages);
    }
}
