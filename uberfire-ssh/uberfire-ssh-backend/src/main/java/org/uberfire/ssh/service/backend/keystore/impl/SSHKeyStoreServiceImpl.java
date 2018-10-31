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

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.ssh.service.backend.keystore.SSHKeyStore;
import org.uberfire.ssh.service.backend.keystore.SSHKeyStoreService;
import org.uberfire.ssh.service.backend.keystore.impl.storage.DefaultSSHKeyStore;

@Startup
@ApplicationScoped
public class SSHKeyStoreServiceImpl implements SSHKeyStoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SSHKeyStoreServiceImpl.class);

    private SSHKeyStore keyStore;

    protected SSHKeyStoreServiceImpl() {
        // CDI Proxy
    }

    @Inject
    public SSHKeyStoreServiceImpl(Instance<SSHKeyStore> keyStores) {

        if (!keyStores.isUnsatisfied()) {
            try {
                Class<? extends SSHKeyStore> keystoreClass = getSSHKeyStoreType();

                LOGGER.info("Looking up SSHKeyStore {}", keystoreClass);

                keyStore = keyStores.select(keystoreClass).get();
            } catch (Exception ex) {
                LOGGER.error("Impossible to lookup any SSHKeyStore named instance: ", ex);
                loadDefaultKeyStore();
            }
        } else {
            loadDefaultKeyStore();
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends SSHKeyStore> getSSHKeyStoreType() {
        Optional<String> optional = Optional.ofNullable(System.getProperty(SSH_KEY_STORE_PARAM));

        if (optional.isPresent()) {
            final String sshKeyStoreType = optional.get();
            if (!sshKeyStoreType.isEmpty()) {
                try {
                    return (Class<? extends SSHKeyStore>) Class.forName(sshKeyStoreType);
                } catch (ClassNotFoundException e) {
                    LOGGER.warn("Cannot lookup configured SSHKeystore ('{}'), looking up default keystore", sshKeyStoreType);
                }
            }
        }
        return DefaultSSHKeyStore.class;
    }

    private void loadDefaultKeyStore() {
        LOGGER.debug("Loading a default SSHKeyStore.");

        DefaultSSHKeyStore defaultSSHKeyStore = new DefaultSSHKeyStore();
        defaultSSHKeyStore.init();

        keyStore = defaultSSHKeyStore;
    }

    @Override
    public SSHKeyStore keyStore() {
        return keyStore;
    }
}
