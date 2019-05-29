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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.sshd.common.config.keys.KeyUtils;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.security.WorkbenchUserManager;
import org.uberfire.ssh.service.backend.auth.SSHKeyAuthenticator;
import org.uberfire.ssh.service.backend.keystore.SSHKeyStoreService;
import org.uberfire.ssh.service.backend.keystore.model.SSHPublicKey;

@ApplicationScoped
public class SSHKeyAuthenticatorImpl implements SSHKeyAuthenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SSHKeyAuthenticatorImpl.class);

    private final SSHKeyStoreService keyStoreService;
    private final WorkbenchUserManager userManager;

    @Inject
    public SSHKeyAuthenticatorImpl(final SSHKeyStoreService keyStoreService, final Instance<WorkbenchUserManager> workbenchUserManagerInstance) {
        this.keyStoreService = keyStoreService;

        if (!workbenchUserManagerInstance.isUnsatisfied() && !workbenchUserManagerInstance.isAmbiguous()) {
            this.userManager = workbenchUserManagerInstance.get();
        } else {
            LOGGER.warn("Cannot find any implementation of 'WorkbenchUserManager'. Loading default implementation on SSH module");
            this.userManager = this::getUser;
        }
    }

    @Override
    public User authenticate(final String userName, final PublicKey key) {

        final Optional<User> userOptional = Optional.ofNullable(userManager.getUser(userName));

        if (userOptional.isPresent()) {
            List<SSHPublicKey> keys = new ArrayList<>(keyStoreService.keyStore().getUserKeys(userName));

            PublicKey resultKey = KeyUtils.findMatchingKey(key, keys.stream().map(SSHPublicKey::getKey).collect(Collectors.toList()));

            if (resultKey != null) {
                keys.stream()
                        .filter(userKey -> userKey.getKey().equals(resultKey))
                        .findAny()
                        .ifPresent(userKey -> updateUserKey(userName, userKey));

                return userOptional.get();
            }
        }

        return null;
    }

    private void updateUserKey(final String userName, final SSHPublicKey userKey) {
        userKey.getMetaData().setLastTimeUsed(new Date());

        keyStoreService.keyStore().updateUserKey(userName, userKey);
    }

    private User getUser(String identifier) {
        return new SSHUser(identifier);
    }
}
