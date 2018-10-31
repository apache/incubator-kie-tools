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

package org.uberfire.ssh.service.backend.keystore;

/**
 * Entry point API to access the {@link SSHKeyStore}
 */
public interface SSHKeyStoreService {

    /**
     * Environment variable to load a custom {@link SSHKeyStore} on the system. It should be a valid className
     * extending {@link SSHKeyStore} (e.g. -Dappformer.ssh.keystore=my.example.MySSHKeyStore.
     * If the variable doesn't exist the default implementation of the {@link SSHKeyStore} will be loaded.
     */
    String SSH_KEY_STORE_PARAM = "appformer.ssh.keystore";

    /**
     * Returns the current instance of the {@link SSHKeyStore}
     * @return
     */
    SSHKeyStore keyStore();
}
