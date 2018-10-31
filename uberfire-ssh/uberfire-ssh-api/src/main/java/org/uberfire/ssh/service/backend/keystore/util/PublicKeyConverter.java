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

package org.uberfire.ssh.service.backend.keystore.util;

import java.security.PublicKey;

import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;
import org.apache.sshd.common.config.keys.PublicKeyEntryResolver;

public class PublicKeyConverter {

    public static PublicKey fromString(final String keyStr) throws Exception {
        return AuthorizedKeyEntry.parseAuthorizedKeyEntry(keyStr).resolvePublicKey(PublicKeyEntryResolver.IGNORING);
    }

    public static String fromPublicKey(PublicKey publicKey) {
        return AuthorizedKeyEntry.toString(publicKey);
    }
}
