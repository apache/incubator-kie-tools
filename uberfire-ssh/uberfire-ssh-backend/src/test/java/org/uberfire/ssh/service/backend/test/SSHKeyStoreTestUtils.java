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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.uberfire.ssh.service.backend.keystore.impl.storage.DefaultSSHKeyStore;
import org.uberfire.ssh.service.backend.keystore.impl.util.SerializingUtils;
import org.uberfire.ssh.service.backend.keystore.model.KeyMetaData;
import org.uberfire.ssh.service.backend.keystore.model.SSHPublicKey;
import org.uberfire.ssh.service.backend.keystore.util.PublicKeyConverter;

public class SSHKeyStoreTestUtils {

    public static final String KEY_NAME = "key name 1";

    public static final String SAMPLE_ID = "1234567890";

    public static final String KEY_FILE = "/pkeys/key.txt";
    public static final String META_FILE = "/pkeys/meta.txt";

    public static final String RESOURCES_FOLDER = "src/test/resources/";
    public static final String CUSTOM_FOLDER = "custom_security/pkeys";
    public static final String DEFAULT_FOLDER = ".security/pkeys";

    public static final String KATY = "katy";
    public static final String KATY_META1 = KATY + "/.katy-key-one.pub.meta";
    public static final String KATY_META2 = KATY + "/.katy-key-two.pub.meta";
    public static final String JOHN = "john";
    public static final String JOHN_META = JOHN + "/.john-key.pub.meta";
    public static final String ADMIN = "admin";

    private static String userDir;

    public static void setupUserDir() {
        userDir = System.getProperty(DefaultSSHKeyStore.USER_DIR);

        if (!userDir.endsWith("/")) {
            userDir += "/";
        }

        System.setProperty(DefaultSSHKeyStore.USER_DIR, userDir + RESOURCES_FOLDER);
    }

    public static void cleanResourceKeysFolder() {
        String userDir = System.getProperty(DefaultSSHKeyStore.USER_DIR);
        FileUtils.deleteQuietly(Paths.get(userDir + CUSTOM_FOLDER).resolve(ADMIN).toFile());
        FileUtils.deleteQuietly(Paths.get(userDir + DEFAULT_FOLDER).resolve(ADMIN).toFile());
        FileUtils.deleteQuietly(Paths.get(userDir + DEFAULT_FOLDER).resolve(KATY_META1).toFile());
        FileUtils.deleteQuietly(Paths.get(userDir + CUSTOM_FOLDER).resolve(KATY_META1).toFile());
        FileUtils.deleteQuietly(Paths.get(userDir + DEFAULT_FOLDER).resolve(KATY_META2).toFile());
        FileUtils.deleteQuietly(Paths.get(userDir + CUSTOM_FOLDER).resolve(KATY_META2).toFile());
        FileUtils.deleteQuietly(Paths.get(userDir + DEFAULT_FOLDER).resolve(JOHN_META).toFile());
        FileUtils.deleteQuietly(Paths.get(userDir + CUSTOM_FOLDER).resolve(JOHN_META).toFile());
    }

    public static SSHPublicKey readSampleSSHPublicKey() throws Exception {
        KeyMetaData metaData = SerializingUtils.readMetaData(IOUtils.toString(SSHKeyStoreTestUtils.class.getResource(META_FILE), Charset.defaultCharset()));

        return new SSHPublicKey(SAMPLE_ID, PublicKeyConverter.fromString(readSampleSSHKey()), metaData);
    }

    public static String readSampleSSHKey() throws IOException {
        return IOUtils.toString(SSHKeyStoreTestUtils.class.getResource(KEY_FILE), Charset.defaultCharset());
    }

    public static void resetUserDir() {
        if (userDir != null) {
            System.setProperty(DefaultSSHKeyStore.USER_DIR, userDir);
        }
    }
}
