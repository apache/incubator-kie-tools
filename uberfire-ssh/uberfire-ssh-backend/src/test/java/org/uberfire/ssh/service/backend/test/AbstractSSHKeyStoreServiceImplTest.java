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

package org.uberfire.ssh.service.backend.test;

import javax.enterprise.inject.Instance;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.uberfire.ssh.service.backend.keystore.SSHKeyStore;
import org.uberfire.ssh.service.backend.keystore.impl.SSHKeyStoreServiceImpl;
import org.uberfire.ssh.service.backend.keystore.impl.storage.DefaultSSHKeyStore;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractSSHKeyStoreServiceImplTest {

    @Mock
    protected Instance<SSHKeyStore> keyStoreInstance;

    protected SSHKeyStoreServiceImpl keyStoreService;

    @Before
    public void initTest() {
        when(keyStoreInstance.select(any(Class.class))).then((Answer<Instance>) invocationOnMock -> {
            Class<SSHKeyStore> type = (Class<SSHKeyStore>) invocationOnMock.getArguments()[0];

            final DefaultSSHKeyStore keyStore;

            if (type.equals(DefaultSSHKeyStore.class)) {
                keyStore = new DefaultSSHKeyStore();
            } else {
                keyStore = new TestSSHKeyStore();
            }

            keyStore.init();

            Instance<SSHKeyStore> instance = mock(Instance.class);

            when(instance.get()).thenReturn(keyStore);

            return instance;
        });
    }

    protected void initService() {
        keyStoreService = new SSHKeyStoreServiceImpl(keyStoreInstance);
    }
}
