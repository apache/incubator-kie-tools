/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.cluster.ClusterJMSService;
import org.uberfire.commons.services.cdi.Startup;

@ApplicationScoped
@Startup
public class SocialUserClusterMessaging {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocialUserClusterMessaging.class);

    public static final String topicName = "SOCIAL_USER_MESSAGE";

    private SocialUserPersistenceAPI socialUserPersistenceAPI;

    private ClusterJMSService clusterJMSService;

    private String nodeId = UUID.randomUUID().toString();

    public void setup(ClusterJMSService clusterJMSService,
                      SocialUserPersistenceAPI socialUserPersistenceAPI) {
        this.clusterJMSService = clusterJMSService;
        this.socialUserPersistenceAPI = socialUserPersistenceAPI;
        if (clusterJMSService.isAppFormerClustered()) {
            clusterJMSService.connect();

            clusterJMSService.createConsumer(ClusterJMSService.DESTINATION_TYPE.TOPIC,
                                             topicName,
                                             message -> topicMessageListener(message));
        }
    }

    private void topicMessageListener(Message message) {
        if (message instanceof ObjectMessage) {
            try {
                Serializable object = ((ObjectMessage) message).getObject();
                if (object instanceof SocialUserClusterMessageWrapper) {
                    SocialUserClusterMessageWrapper messageWrapper = (SocialUserClusterMessageWrapper) object;
                    if (!messageWrapper.getNodeId().equals(nodeId)) {
                        SocialUserClusterPersistence socialUserClusterPersistence = (SocialUserClusterPersistence) socialUserPersistenceAPI;
                        socialUserClusterPersistence.sync(messageWrapper.getUser());
                    }
                }
            } catch (JMSException e) {
                LOGGER.error("Exception receiving JMS message: " + e.getMessage());
            }
        }
    }

    public void notify(SocialUser user) {
        if (!clusterJMSService.isAppFormerClustered()) {
            return;
        }
        clusterJMSService.broadcast(ClusterJMSService.DESTINATION_TYPE.TOPIC,
                                    topicName,
                                    new SocialUserClusterMessageWrapper(nodeId,
                                                                        user));
    }
}
