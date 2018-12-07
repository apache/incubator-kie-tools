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

package org.uberfire.ssh.service.backend.editor;

import java.io.IOException;
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.ssh.service.backend.test.AbstractSSHKeyStoreServiceImplTest;
import org.uberfire.ssh.service.shared.editor.PortableSSHPublicKey;

import static org.mockito.Mockito.when;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.ADMIN;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.JOHN;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.KATY;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.KEY_NAME;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.cleanResourceKeysFolder;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.readSampleSSHKey;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.resetUserDir;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.setupUserDir;

@RunWith(MockitoJUnitRunner.class)
public class SSHKeyEditorServiceImplTest extends AbstractSSHKeyStoreServiceImplTest {

    private String userName;

    @Mock
    private SessionInfo sessionInfo;

    private SSHKeyEditorServiceImpl service;

    @BeforeClass
    public static void init() {
        setupUserDir();
    }

    @Before
    public void initTest() {
        super.initTest();

        initService();

        when(sessionInfo.getIdentity()).thenAnswer((Answer<User>) invocationOnMock -> new UserImpl(userName));

        service = new SSHKeyEditorServiceImpl(sessionInfo, keyStoreService);
    }

    @Test
    public void testKatyUser() {
        userName = KATY;

        Assertions.assertThat(service.getUserKeys())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    public void testJohnUser() {
        userName = JOHN;

        Assertions.assertThat(service.getUserKeys())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    public void testAdmin() throws IOException {
        userName = ADMIN;

        Assertions.assertThat(service.getUserKeys())
                .isNotNull()
                .hasSize(0);

        String keyContent = readSampleSSHKey();

        service.addKey(KEY_NAME, keyContent);

        Collection<PortableSSHPublicKey> keys = service.getUserKeys();

        Assertions.assertThat(keys)
                .isNotNull()
                .hasSize(1);

        PortableSSHPublicKey key = keys.iterator().next();

        service.deleteKey(key);

        Assertions.assertThat(service.getUserKeys())
                .isNotNull()
                .hasSize(0);

        Assertions.assertThatThrownBy(() -> service.addKey(KEY_NAME, "wrong content"))
                .isNotNull()
                .isInstanceOf(RuntimeException.class);
    }

    @AfterClass
    public static void clean() {
        cleanResourceKeysFolder();
        resetUserDir();
    }
}
