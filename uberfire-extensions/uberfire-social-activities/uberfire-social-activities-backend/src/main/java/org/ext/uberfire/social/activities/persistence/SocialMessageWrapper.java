/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.ext.uberfire.social.activities.persistence;

import java.io.Serializable;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialUser;

public class SocialMessageWrapper implements Serializable {

    private final String nodeId;
    private SocialClusterMessage messageType;
    private SocialActivitiesEvent event;
    private SocialUser user;
    private SocialClusterMessage subMessageType = SocialClusterMessage.NO_TYPE;

    public SocialMessageWrapper(String nodeId,
                                SocialClusterMessage messageType,
                                SocialActivitiesEvent event,
                                SocialUser user) {
        this.nodeId = nodeId;
        this.messageType = messageType;
        this.event = event;
        this.user = user;
    }

    public SocialMessageWrapper(String nodeId,
                                SocialClusterMessage messageType,
                                SocialActivitiesEvent event,
                                SocialUser user,
                                SocialClusterMessage subMessageType) {
        this.nodeId = nodeId;
        this.messageType = messageType;
        this.event = event;
        this.user = user;
        this.subMessageType = subMessageType;
    }

    public SocialMessageWrapper(String nodeId,
                                SocialClusterMessage messageType) {
        this.nodeId = nodeId;
        this.messageType = messageType;
    }

    public String getNodeId() {
        return nodeId;
    }

    public SocialUser getUser() {
        return user;
    }

    public SocialActivitiesEvent getEvent() {
        return event;
    }

    public SocialClusterMessage getMessageType() {
        return messageType;
    }

    public SocialClusterMessage getSubMessageType() {
        return subMessageType;
    }
}
