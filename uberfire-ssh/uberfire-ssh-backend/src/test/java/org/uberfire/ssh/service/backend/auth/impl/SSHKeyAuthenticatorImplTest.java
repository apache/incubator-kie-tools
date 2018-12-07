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

package org.uberfire.ssh.service.backend.auth.impl;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Instance;

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
import org.uberfire.security.WorkbenchUserManager;
import org.uberfire.ssh.service.backend.keystore.model.SSHPublicKey;
import org.uberfire.ssh.service.backend.keystore.util.PublicKeyConverter;
import org.uberfire.ssh.service.backend.test.AbstractSSHKeyStoreServiceImplTest;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.ADMIN;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.JOHN;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.KATY;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.cleanResourceKeysFolder;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.readSampleSSHKey;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.resetUserDir;
import static org.uberfire.ssh.service.backend.test.SSHKeyStoreTestUtils.setupUserDir;

@RunWith(MockitoJUnitRunner.class)
public class SSHKeyAuthenticatorImplTest extends AbstractSSHKeyStoreServiceImplTest {

    private List<String> validUsers;

    @Mock
    private Instance<WorkbenchUserManager> workbenchUserManagerInstance;

    @Mock
    private WorkbenchUserManager userManagerService;

    private SSHKeyAuthenticatorImpl authenticator;

    @BeforeClass
    public static void init() {
        setupUserDir();
    }

    @Before
    public void initTest() {
        super.initTest();

        initService();

        validUsers = Arrays.asList(KATY, JOHN, ADMIN);

        when(workbenchUserManagerInstance.get()).thenReturn(userManagerService);

        when(userManagerService.getUser(anyString())).thenAnswer((Answer<User>) invocationOnMock -> {
            String userName = (String) invocationOnMock.getArguments()[0];

            if (validUsers.contains(userName)) {
                return new UserImpl(userName);
            }
            return null;
        });
    }

    @Test
    public void testAuthenticateKaty() throws Exception {
        testUserWithKeys(KATY, false);
    }

    @Test
    public void testAuthenticateKatyWithDependencyIssues() throws Exception {
        testUserWithKeys(KATY, true);
    }

    @Test
    public void testAuthenticateJohn() throws Exception {
        testUserWithKeys(JOHN, false);
    }

    @Test
    public void testAuthenticateJohnWithDependencyIssues() throws Exception {
        testUserWithKeys(JOHN, true);
    }

    @Test
    public void testAuthenticateUserWithoutKeys() throws Exception {
        authenticator = new SSHKeyAuthenticatorImpl(keyStoreService, workbenchUserManagerInstance);

        PublicKey publicKey = PublicKeyConverter.fromString(readSampleSSHKey());

        Assertions.assertThat(authenticator.authenticate(ADMIN, publicKey))
                .isNull();
    }

    @Test
    public void testAuthenticateNonPlatformUser() throws Exception {
        authenticator = new SSHKeyAuthenticatorImpl(keyStoreService, workbenchUserManagerInstance);

        PublicKey publicKey = PublicKeyConverter.fromString(readSampleSSHKey());

        Assertions.assertThat(authenticator.authenticate("user", publicKey))
                .isNull();
    }

    private void testUserWithKeys(final String userName, final boolean dependencyIssue) throws Exception {

        when(workbenchUserManagerInstance.isUnsatisfied()).thenReturn(dependencyIssue);
        when(workbenchUserManagerInstance.isAmbiguous()).thenReturn(dependencyIssue);

        authenticator = new SSHKeyAuthenticatorImpl(keyStoreService, workbenchUserManagerInstance);

        verify(workbenchUserManagerInstance, dependencyIssue ? never() : times(1)).get();

        // Authenticate existing user Key
        SSHPublicKey key = keyStoreService.keyStore().getUserKeys(userName).iterator().next();

        Class expectedUserType = dependencyIssue ? SSHUser.class : UserImpl.class;

        Assertions.assertThat(authenticator.authenticate(userName, key.getKey()))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", userName)
                .isInstanceOf(expectedUserType);

        // Authenticate using none existing key
        PublicKey publicKey = PublicKeyConverter.fromString(readSampleSSHKey());

        Assertions.assertThat(authenticator.authenticate(userName, publicKey))
                .isNull();

        verify(userManagerService, dependencyIssue ? never() : times(2)).getUser(eq(userName));
    }

    @AfterClass
    public static void clean() {
        cleanResourceKeysFolder();
        resetUserDir();
    }
}
