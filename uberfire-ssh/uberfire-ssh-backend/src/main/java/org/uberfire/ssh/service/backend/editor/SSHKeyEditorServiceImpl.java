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

import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.ssh.service.backend.keystore.SSHKeyStore;
import org.uberfire.ssh.service.backend.keystore.SSHKeyStoreService;
import org.uberfire.ssh.service.backend.keystore.model.KeyMetaData;
import org.uberfire.ssh.service.backend.keystore.model.SSHPublicKey;
import org.uberfire.ssh.service.backend.keystore.util.PublicKeyConverter;
import org.uberfire.ssh.service.shared.editor.PortableSSHPublicKey;
import org.uberfire.ssh.service.shared.editor.SSHKeyEditorService;

@Service
@Dependent
public class SSHKeyEditorServiceImpl implements SSHKeyEditorService {

    private SessionInfo sessionInfo;
    private SSHKeyStoreService keyStoreService;

    @Inject
    public SSHKeyEditorServiceImpl(SessionInfo sessionInfo, SSHKeyStoreService keyStoreService) {
        this.sessionInfo = sessionInfo;
        this.keyStoreService = keyStoreService;
    }

    @Override
    public Collection<PortableSSHPublicKey> getUserKeys() {
        return keyStoreService.keyStore().getUserKeys(sessionInfo.getIdentity().getIdentifier()).stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteKey(final PortableSSHPublicKey portableKey) {
        SSHKeyStore keyStore = keyStoreService.keyStore();

        Collection<SSHPublicKey> keys = keyStore.getUserKeys(sessionInfo.getIdentity().getIdentifier());

        keys.stream()
                .filter(key -> key.getId().equals(portableKey.getId()))
                .findAny()
                .ifPresent(sshPublicKey -> keyStore.removeUserKey(sessionInfo.getIdentity().getIdentifier(), sshPublicKey));
    }

    @Override
    public void addKey(String name, String keyContent) {
        keyStoreService.keyStore().addUserKey(sessionInfo.getIdentity().getIdentifier(), convert(name, keyContent));
    }

    private SSHPublicKey convert(String name, String keyContent) {
        try {
            return new SSHPublicKey(UUID.randomUUID().toString(), PublicKeyConverter.fromString(keyContent), new KeyMetaData(name, new Date()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PortableSSHPublicKey convert(SSHPublicKey key) {
        return new PortableSSHPublicKey(key.getId(), key.getMetaData().getName(), PublicKeyConverter.fromPublicKey(key.getKey()), key.getMetaData().getCreationDate(), key.getMetaData().getLastTimeUsed());
    }
}
