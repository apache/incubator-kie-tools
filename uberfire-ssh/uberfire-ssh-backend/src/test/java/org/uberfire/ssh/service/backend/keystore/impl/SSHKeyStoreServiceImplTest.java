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

package org.uberfire.ssh.service.backend.keystore.impl;

import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ssh.service.backend.keystore.SSHKeyStore;
import org.uberfire.ssh.service.backend.keystore.impl.storage.DefaultSSHKeyStore;
import org.uberfire.ssh.service.backend.test.AbstractSSHKeyStoreServiceImplTest;
import org.uberfire.ssh.service.backend.test.TestSSHKeyStore;

import static org.mockito.Mockito.when;
import static org.uberfire.ssh.service.backend.keystore.impl.SSHKeyStoreServiceImpl.SSH_KEY_STORE_PARAM;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.ADMIN;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.JOHN;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.KATY;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.resetUserDir;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.setupUserDir;

@RunWith(MockitoJUnitRunner.class)
public class SSHKeyStoreServiceImplTest extends AbstractSSHKeyStoreServiceImplTest {

    @BeforeClass
    public static void init() {
        setupUserDir();
    }

    @Test
    public void testLoadDefaultSSHKeyStore() {
        System.clearProperty(SSH_KEY_STORE_PARAM);

        runTest(DefaultSSHKeyStore.class);
    }

    @Test
    public void testLoadDefaultSSHKeyStoreUnsatisfied() {
        when(keyStoreInstance.isUnsatisfied()).thenReturn(true);

        System.clearProperty(SSH_KEY_STORE_PARAM);

        runTest(DefaultSSHKeyStore.class);
    }

    @Test
    public void testCustomSSHKeyStore() {
        System.setProperty(SSH_KEY_STORE_PARAM, TestSSHKeyStore.class.getName());
        runTest(TestSSHKeyStore.class);
    }

    @Test
    public void testWrongCustomSSHKeyStore() {
        System.setProperty(SSH_KEY_STORE_PARAM, "wrong class name");
        runTest(DefaultSSHKeyStore.class);
    }

    private void runTest(Class<? extends SSHKeyStore> expectedKeystore) {
        initService();

        SSHKeyStore store = keyStoreService.keyStore();

        Assertions.assertThat(store)
                .isNotNull()
                .isInstanceOf(expectedKeystore);

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
        resetUserDir();
    }
}
