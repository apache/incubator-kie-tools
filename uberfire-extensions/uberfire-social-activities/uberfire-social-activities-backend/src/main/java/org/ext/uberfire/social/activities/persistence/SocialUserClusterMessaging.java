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

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.message.MessageHandler;
import org.uberfire.commons.message.MessageHandlerResolver;
import org.uberfire.commons.message.MessageType;
import org.uberfire.commons.services.cdi.Startup;

@ApplicationScoped
@Startup
public class SocialUserClusterMessaging {

    private Gson gson;

    private Type gsonCollectionType;

    private String cluster = "social-user";

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    @Inject
    @Named("socialUserPersistenceAPI")
    private SocialUserPersistenceAPI socialUserCachePersistence;

    private ClusterService clusterService;

    @PostConstruct
    public void setup() {
        gsonFactory();

        if (clusterServiceFactory != null) {
            clusterService = clusterServiceFactory.build(new MessageHandlerResolver() {
                @Override
                public String getServiceId() {
                    return cluster;
                }

                @Override
                public MessageHandler resolveHandler(String serviceId,
                                                     MessageType type) {
                    return new MessageHandler() {
                        @Override
                        public Pair<MessageType, Map<String, String>> handleMessage(MessageType type,
                                                                                    Map<String, String> content) {
                            if (type != null) {
                                String strType = type.toString();
                                if (strType.equals(SocialUserClusterMessage.SOCIAL_USER_UPDATE.name())) {
                                    handleUserUpdate(content);
                                }
                            }
                            return new Pair<MessageType, Map<String, String>>(type,
                                                                              content);
                        }
                    };
                }
            });
        } else {
            clusterService = null;
        }
    }

    private void handleUserUpdate(Map<String, String> content) {
        for (final Map.Entry<String, String> entry : content.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(SocialUserClusterMessage.UPDATE_USER.name())) {
                SocialUser user = gson.fromJson(entry.getValue(),
                                                SocialUser.class);
                SocialUserClusterPersistence socialUserClusterPersistence = (SocialUserClusterPersistence) socialUserCachePersistence;
                socialUserClusterPersistence.sync(user);
            }
        }
    }

    void gsonFactory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        gsonCollectionType = new TypeToken<Collection<SocialActivitiesEvent>>() {
        }.getType();
    }

    public void notify(SocialUser user) {
        if (clusterService == null) {
            return;
        }
        Map<String, String> content = new HashMap<String, String>();
        String json = gson.toJson(user);
        content.put(SocialUserClusterMessage.UPDATE_USER.name(),
                    json);
        clusterService.broadcast(cluster,
                                 SocialUserClusterMessage.SOCIAL_USER_UPDATE,
                                 content);
    }

    private enum SocialUserClusterMessage implements MessageType {
        UPDATE_USER,
        SOCIAL_USER_UPDATE
    }
}
