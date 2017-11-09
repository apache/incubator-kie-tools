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

import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;

import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.services.cdi.Startup;

@ApplicationScoped
@Startup
public class SocialUserClusterMessaging {

    public static final String CHANNEL_NAME = "SOCIAL_USER_MESSAGE";

    private SocialUserPersistenceAPI socialUserPersistenceAPI;

    private ClusterService clusterService;

    private String nodeId = UUID.randomUUID().toString();

    public void setup(ClusterService clusterService,
                      SocialUserPersistenceAPI socialUserPersistenceAPI) {
        this.clusterService = clusterService;
        this.socialUserPersistenceAPI = socialUserPersistenceAPI;
        if (clusterService.isAppFormerClustered()) {
            clusterService.connect();

            clusterService.createConsumer(ClusterService.DestinationType.PubSub,
                                          CHANNEL_NAME,
                                          SocialUserClusterMessageWrapper.class,
                                          message -> {
                                              if (!message.getNodeId().equals(nodeId)) {
                                                  SocialUserClusterPersistence socialUserClusterPersistence = (SocialUserClusterPersistence) socialUserPersistenceAPI;
                                                  socialUserClusterPersistence.sync(message.getUser());
                                              }
                                          });
        }
    }

    public void notify(SocialUser user) {
        if (!clusterService.isAppFormerClustered()) {
            return;
        }
        clusterService.broadcast(ClusterService.DestinationType.PubSub,
                                 CHANNEL_NAME,
                                 new SocialUserClusterMessageWrapper(nodeId,
                                                                     user));
    }
}
