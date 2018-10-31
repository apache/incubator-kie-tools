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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.security.PublicKey;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ssh.service.backend.keystore.impl.util.SerializingUtils;
import org.uberfire.ssh.service.backend.keystore.model.KeyMetaData;
import org.uberfire.ssh.service.backend.keystore.model.SSHPublicKey;
import org.uberfire.ssh.service.backend.keystore.util.PublicKeyConverter;

public class UserSSHKeyStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSSHKeyStore.class);

    public static final String DEFAULT_KEY_NAME = "Key #{0}";
    private static final String PUBLIC_KEY_EXTENSION = ".pub";

    private static final String METADATA_PREFFIX = ".";
    private static final String METADATA_EXTENSION = PUBLIC_KEY_EXTENSION + ".meta";

    private final String user;

    private final Path rootPath;

    private List<SSHPublicKey> keys = new ArrayList<>();

    public UserSSHKeyStore(final String user, final Path rootPath) {
        this.user = user;
        this.rootPath = rootPath.resolve(user);

        init();
    }

    private void init() {
        File rootFile = rootPath.toFile();

        if (rootFile.exists()) {
            Stream.of(rootFile.listFiles())
                    .filter(File::isFile)
                    .filter(file -> file.getName().endsWith(PUBLIC_KEY_EXTENSION))
                    .map(this::loadKey)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(() -> keys));
        } else {
            rootFile.mkdirs();
        }
    }

    public String getUser() {
        return user;
    }

    public Collection<SSHPublicKey> getUserKeys() {
        return keys;
    }

    public void addUserKey(final SSHPublicKey key) {
        Optional<SSHPublicKey> optional = getKeyById(key.getId());

        if (optional.isPresent()) {
            SSHPublicKey oldKey = optional.get();
            removeUserKey(oldKey);
        }

        keys.add(key);
        writeKey(key);
    }

    public void removeUserKey(final SSHPublicKey oldKey) {
        Optional<SSHPublicKey> optional = getKeyById(oldKey.getId());

        if (optional.isPresent()) {
            SSHPublicKey key = optional.get();

            keys.remove(key);

            FileUtils.deleteQuietly(rootPath.resolve(getKeyFileName(oldKey.getId())).toFile());
            FileUtils.deleteQuietly(rootPath.resolve(getMetadataFileName(oldKey.getId())).toFile());
        }
    }

    public Optional<SSHPublicKey> getKeyById(String keyId) {
        return keys.stream()
                .filter(userKey -> userKey.getId().equals(keyId))
                .findAny();
    }

    private void writeKey(final SSHPublicKey key) {
        File file = rootPath.resolve(key.getId() + PUBLIC_KEY_EXTENSION).toFile();

        if (file.exists()) {
            file.delete();
        }

        try {
            writeKeyFile(key.getId(), key.getKey());
            writeMetaData(key.getId(), key.getMetaData());
        } catch (Exception ex) {
            LOGGER.warn("Cannot create public key for user '{}' on file '{}': ", user, file.getAbsolutePath(), ex);
        }
    }

    private void writeKeyFile(String id, PublicKey key) throws IOException {
        File fileKey = rootPath.resolve(getKeyFileName(id)).toFile();

        if (fileKey.exists()) {
            fileKey.delete();
        }

        fileKey.createNewFile();

        FileUtils.write(fileKey, PublicKeyConverter.fromPublicKey(key), Charset.defaultCharset());
    }

    private SSHPublicKey loadKey(final File keyFile) {
        try {
            // Read Public Key
            final String fileName = keyFile.getName();

            final String id = fileName.substring(0, fileName.lastIndexOf(PUBLIC_KEY_EXTENSION));

            final String keyContent = FileUtils.readFileToString(keyFile, Charset.defaultCharset());

            return new SSHPublicKey(id, PublicKeyConverter.fromString(keyContent), readMetaData(id));
        } catch (Exception ex) {
            LOGGER.warn("Cannot read public key for user '{}' on file '{}': ", user, keyFile.getAbsolutePath(), ex);
        }
        return null;
    }

    private void writeMetaData(String id, KeyMetaData metaData) throws IOException {
        File file = rootPath.resolve(getMetadataFileName(id)).toFile();

        if (file.exists()) {
            file.delete();
        }

        file.createNewFile();

        FileUtils.write(file, SerializingUtils.readMetaData(metaData), Charset.defaultCharset());
    }

    private KeyMetaData readMetaData(String id) throws IOException {
        final File metaDataFile = rootPath.resolve(getMetadataFileName(id)).toFile();

        final KeyMetaData metaData;

        if (!metaDataFile.exists()) {
            metaDataFile.createNewFile();

            String keyName = MessageFormat.format(DEFAULT_KEY_NAME, id);

            metaData = new KeyMetaData(keyName, new Date());

            writeMetaData(id, metaData);
        } else {
            String metaDataContent = FileUtils.readFileToString(metaDataFile, Charset.defaultCharset());

            metaData = SerializingUtils.readMetaData(metaDataContent);
        }

        return metaData;
    }

    private String getKeyFileName(String id) {
        return id + PUBLIC_KEY_EXTENSION;
    }

    private String getMetadataFileName(String id) {
        return METADATA_PREFFIX + id + METADATA_EXTENSION;
    }
}
