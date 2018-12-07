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

import java.util.Date;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ssh.service.backend.keystore.model.SSHPublicKey;

import static org.junit.Assert.assertTrue;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.ADMIN;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.CUSTOM_FOLDER;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.JOHN;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.KATY;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.SAMPLE_ID;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.cleanResourceKeysFolder;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.readSampleSSHPublicKey;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.resetUserDir;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.setupUserDir;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSSHKeyStoreTest {

    private DefaultSSHKeyStore store;

    @BeforeClass
    public static void init() {
        setupUserDir();
    }

    @Test
    public void testLoadKeysDefaultFolder() {
        loadKeys();
    }

    @Test
    public void testLoadKeysCustomFolder() {
        String userDir = System.getProperty(DefaultSSHKeyStore.USER_DIR);

        if (!userDir.endsWith("/")) {
            userDir += "/";
        }

        System.setProperty(DefaultSSHKeyStore.SSH_KEYS_PATH_PARAM, userDir + CUSTOM_FOLDER);

        loadKeys();
    }

    @Test
    public void testHandleUserKeysDefaultFolder() throws Exception {
        testLoadKeysDefaultFolder();
        handleUserKeys();
    }

    @Test
    public void testHandleUserKeysCustomFolder() throws Exception {
        testLoadKeysCustomFolder();
        handleUserKeys();
    }

    private void handleUserKeys() throws Exception {

        SSHPublicKey key = readSampleSSHPublicKey();

        store.addUserKey(ADMIN, key);

        Assertions.assertThat(store.getUserKeys(ADMIN))
                .isNotNull()
                .hasSize(1);

        key = readSampleSSHPublicKey();

        Date date = new Date();

        key.getMetaData().setLastTimeUsed(date);

        store.updateUserKey(ADMIN, key);

        Optional<SSHPublicKey> optional = store.getUserKeyStore(ADMIN).getKeyById(SAMPLE_ID);

        assertTrue(optional.isPresent());

        Assertions.assertThat(optional.get().getMetaData())
                .isNotNull()
                .hasFieldOrPropertyWithValue("lastTimeUsed", date);

        store.removeUserKey(ADMIN, key);

        Assertions.assertThat(store.getUserKeys(ADMIN))
                .isNotNull()
                .hasSize(0);
    }

    private void loadKeys() {
        store = new DefaultSSHKeyStore();

        store.init();

        Assertions.assertThat(store.getUserKeys(KATY))
                .isNotNull()
                .hasSize(2);

        Assertions.assertThat(store.getUserKeys(JOHN))
                .isNotNull()
                .hasSize(1);

        Assertions.assertThat(store.getUserKeys(ADMIN))
                .isNotNull()
                .hasSize(0);
    }

    @AfterClass
    public static void clean() {
        cleanResourceKeysFolder();
        resetUserDir();
    }
}
