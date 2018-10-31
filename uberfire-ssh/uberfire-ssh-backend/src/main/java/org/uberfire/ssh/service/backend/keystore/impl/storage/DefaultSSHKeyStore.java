/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ssh.service.backend.keystore.impl.storage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;

import org.uberfire.ssh.service.backend.keystore.SSHKeyStore;
import org.uberfire.ssh.service.backend.keystore.model.SSHPublicKey;

@Dependent
@Default
public class DefaultSSHKeyStore implements SSHKeyStore {

    public static final String USER_DIR = "user.dir";

    public static final String SSH_KEYS_PATH_PARAM = "appformer.ssh.keys.storage.folder";

    public static final String SSH_KEYS_FOLDER = ".security/pkeys";

    private final Map<String, UserSSHKeyStore> userKeyStores = new HashMap<>();

    private final Path sshStoragePath;

    public DefaultSSHKeyStore() {

        String customStorage = System.getProperty(SSH_KEYS_PATH_PARAM, null);

        if (customStorage != null) {
            sshStoragePath = Paths.get(customStorage);
        } else {
            sshStoragePath = Paths.get(System.getProperty(USER_DIR)).resolve(SSH_KEYS_FOLDER);
        }
    }

    @PostConstruct
    public void init() {
        File keysFolder = sshStoragePath.toFile();

        if (keysFolder.exists()) {
            Stream.of(keysFolder.list())
                    .map(this::getUserKeyStore)
                    .forEach(userSSHKeyStore -> userKeyStores.put(userSSHKeyStore.getUser(), userSSHKeyStore));
        } else {
            keysFolder.mkdirs();
        }
    }

    public UserSSHKeyStore getUserKeyStore(String userName) {
        UserSSHKeyStore userStore = userKeyStores.get(userName);

        if (userStore == null) {
            userStore = new UserSSHKeyStore(userName, sshStoragePath);
            userKeyStores.put(userName, userStore);
        }

        return userStore;
    }

    @Override
    public void addUserKey(final String userName, final SSHPublicKey key) {
        getUserKeyStore(userName).addUserKey(key);
    }

    @Override
    public void removeUserKey(String userName, SSHPublicKey key) {
        getUserKeyStore(userName).removeUserKey(key);
    }

    @Override
    public void updateUserKey(String userName, SSHPublicKey key) {
        addUserKey(userName, key);
    }

    @Override
    public Collection<SSHPublicKey> getUserKeys(String userName) {
        return getUserKeyStore(userName).getUserKeys();
    }
}
