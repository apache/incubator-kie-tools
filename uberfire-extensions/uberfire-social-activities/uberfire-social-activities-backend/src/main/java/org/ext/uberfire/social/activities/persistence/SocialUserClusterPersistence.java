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

import com.google.gson.Gson;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.server.SocialUserServicesExtendedBackEndImpl;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class SocialUserClusterPersistence extends SocialUserCachePersistence {

    private final SocialUserClusterMessaging socialUserClusterMessaging;

    public SocialUserClusterPersistence(SocialUserServicesExtendedBackEndImpl userServicesBackend,
                                        UserServicesImpl userServices,
                                        IOService ioService,
                                        Gson gson,
                                        SocialUserClusterMessaging socialUserClusterMessaging) {
        super(userServicesBackend,
              userServices,
              ioService,
              gson);
        this.socialUserClusterMessaging = socialUserClusterMessaging;
    }

    @Override
    public void updateUsers(SocialUser... users) {
        for (SocialUser user : users) {
            usersCache.put(user.getUserName(),
                           user);
            Path userFile = userServicesBackend.buildPath(SOCIAL_FILES,
                                                          user.getUserName());
            try {
                ioService.startBatch(userFile.getFileSystem());
                ioService.write(userFile,
                                gson.toJson(user));
            } catch (Exception e) {
                throw new ErrorUpdatingUsers(e);
            } finally {
                ioService.endBatch();
            }
            socialUserClusterMessaging.notify(user);
        }
    }

    @Override
    String syncUserNamesCacheAndFile(String userName) {
        if (usersNamesCache.contains(userName)) {
            return userName;
        } else {
            usersNamesCache.add(userName);
            SocialUser socialUser = createOrRetrieveUserData(userName);
            usersCache.put(userName,
                           socialUser);
            writeUserNamesOnFile(usersNamesCache);
            return userName;
        }
    }

    public void sync(SocialUser user) {
        if (!usersNamesCache.contains(user.getUserName())) {
            usersNamesCache.add(user.getUserName());
        }
        usersCache.put(user.getUserName(),
                       user);
    }
}

