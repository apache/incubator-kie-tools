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

package org.ext.uberfire.social.activities.repository;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.ext.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class SocialUserRepository implements SocialUserRepositoryAPI {

    @Inject
    @Named("socialUserPersistenceAPI")
    private SocialUserPersistenceAPI socialUserPersistenceAPI;

    @Override
    public List<SocialUser> findAllUsers() {
        List<String> socialUsersName = socialUserPersistenceAPI.getSocialUsersName();
        List<SocialUser> users = new ArrayList<SocialUser>();
        for (String userName : socialUsersName) {
            users.add(socialUserPersistenceAPI.getSocialUser(userName));
        }
        return users;
    }

    @Override
    public SocialUser findSocialUser(String userName) {
        return socialUserPersistenceAPI.getSocialUser(userName);
    }

    public SocialUser systemUser() {
        return socialUserPersistenceAPI.systemUser();
    }
}
