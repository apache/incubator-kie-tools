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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.server.SocialUserServicesExtendedBackEndImpl;
import org.ext.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public abstract class SocialUserCachePersistence implements SocialUserPersistenceAPI {

    public static final String SOCIAL_FILES = "social-files";
    private static final String userNamesFileName = "userNames";
    private static final String SYSTEM_USER = "system";
    SocialUserServicesExtendedBackEndImpl userServicesBackend;
    IOService ioService;
    Gson gson;
    Map<String, SocialUser> usersCache = new HashMap<String, SocialUser>();
    List<String> usersNamesCache = new ArrayList<String>();
    private Path userNamesPath;

    public SocialUserCachePersistence(final SocialUserServicesExtendedBackEndImpl userServicesBackend,
                                      final UserServicesImpl userServices,
                                      final IOService ioService,
                                      final Gson gson) {
        this.userServicesBackend = userServicesBackend;
        this.ioService = ioService;
        this.gson = gson;
        this.userNamesPath = userServicesBackend.buildPath(SOCIAL_FILES,
                                                           userNamesFileName);
    }

    @Override
    public void setup() {
        syncSocialUsers();
    }

    private void syncSocialUsers() {
        try {
            ioService.startBatch(userNamesPath.getFileSystem());
            List<String> users = createUserNamesFile();
            usersNamesCache.addAll(users);
            createSocialUserCache(users);
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public List<String> getSocialUsersName() {
        return usersNamesCache;
    }

    @Override
    public SocialUser getSocialUser(String userName) {
        syncUserNamesCacheAndFile(userName);
        return usersCache.get(userName);
    }

    abstract String syncUserNamesCacheAndFile(String userName);

    SocialUser createOrRetrieveUserData(String username) throws RuntimeException {
        try {
            Path userFile = userServicesBackend.buildPath(SOCIAL_FILES,
                                                          username);
            if (ioService.exists(userFile)) {
                String json = ioService.readAllString(userFile);
                return gson.fromJson(json,
                                     SocialUser.class);
            } else {
                try {
                    ioService.startBatch(userFile.getFileSystem());
                    SocialUser newSocialUser = new SocialUser(username);
                    String json = gson.toJson(newSocialUser);
                    ioService.write(userFile,
                                    json);
                    return newSocialUser;
                } finally {
                    ioService.endBatch();
                }
            }
        } catch (Exception e) {
            throw new ErrorCreatingOrRetrievingUserData(e);
        }
    }

    public abstract void updateUsers(SocialUser... users);

    void writeUserNamesOnFile(List<String> userNames) {
        try {
            ioService.startBatch(userNamesPath.getFileSystem());
            ioService.write(userNamesPath,
                            gson.toJson(userNames));
        } finally {
            ioService.endBatch();
        }
    }

    private void createSocialUserCache(List<String> users) {
        for (String username : users) {
            SocialUser socialUser = createOrRetrieveUserData(username);
            usersCache.put(username,
                           socialUser);
        }
    }

    List<String> createUserNamesFile() {
        List<String> users = extractUsersFromBranches();
        createUserNameFiles();
        persistUsersName(users);
        return users;
    }

    private void createUserNameFiles() {
        if (!ioService.exists(userNamesPath)) {
            createFile();
        }
    }

    private void persistUsersName(List<String> users) {
        writeUserNamesOnFile(users);
    }

    private void createFile() {
        ioService.createFile(userNamesPath);
    }

    private List<String> extractUsersFromBranches() {
        List<String> userNames = new ArrayList<String>();
        for (String branchName : getAllBranches()) {
            if (isAValidBranch(branchName)) {
                String cleanName = retrieveUserFrom(branchName);
                if (notAMergedBranch(cleanName)) {
                    userNames.add(cleanName);
                }
            }
        }
        createSystemUser(userNames);
        return userNames;
    }

    private List<String> getAllBranches() {
        return userServicesBackend.getAllBranches();
    }

    private boolean notAMergedBranch(String cleanName) {
        return !cleanName.contains("upstream") && !cleanName.contains("@");
    }

    private void createSystemUser(List<String> userNames) {
        userNames.add(SYSTEM_USER);
    }

    private boolean isAValidBranch(String branchName) {
        return !branchName.contains("master");
    }

    private String retrieveUserFrom(String branchName) {
        String user = branchName;
        if (branchName.indexOf("-") > 0) {
            user = branchName.substring(0,
                                        branchName.indexOf("-"));
        }
        return user;
    }

    @Override
    public SocialUser systemUser() {
        return getSocialUser(SYSTEM_USER);
    }

    public class ErrorCreatingOrRetrievingUserData extends RuntimeException {

        public ErrorCreatingOrRetrievingUserData(Exception e) {
            e.printStackTrace();
        }
    }

    class ErrorUpdatingUsers extends RuntimeException {

        public ErrorUpdatingUsers(Exception e) {
            e.printStackTrace();
        }
    }
}
