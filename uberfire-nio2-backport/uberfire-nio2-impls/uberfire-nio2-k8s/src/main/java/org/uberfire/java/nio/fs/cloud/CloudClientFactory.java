/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.uberfire.java.nio.fs.cloud;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.IOException;

public interface CloudClientFactory {

    default OpenShiftClient createOpenShiftClient() {
        return new DefaultOpenShiftClient(setupConfig());
    }

    default KubernetesClient createKubernetesClient() {
        return new DefaultKubernetesClient(setupConfig());
    }

    default Config setupConfig() {
        final Logger logger = LoggerFactory.getLogger(CloudClientFactory.class);
        String masterUrl = System.getProperty(Config.KUBERNETES_MASTER_SYSTEM_PROPERTY);
        String token = System.getProperty(Config.KUBERNETES_OAUTH_TOKEN_SYSTEM_PROPERTY);

        if (masterUrl == null) {
            masterUrl = new StringBuilder("https://")
                    .append(System.getenv(CloudClientConstants.ENV_VAR_API_SERVICE_HOST))
                    .append(":")
                    .append(System.getenv(CloudClientConstants.ENV_VAR_API_SERVER_PORT))
                    .toString();
            System.setProperty(Config.KUBERNETES_MASTER_SYSTEM_PROPERTY, masterUrl);
            logger.debug("MasterUrl: {}", masterUrl);
        }
        
        if (token == null || token.length() == 0) {
            try {
                token = new String(Files.readAllBytes(Paths.get(CloudClientConstants.DEFAULT_TOKEN_LOCATION)));
            } catch (Exception e) {
                logger.error("Load kubenetes oauth token failed.", e);
            }
            if (token == null) {
                throw new IllegalStateException("Kubenetes oauth token missing");
            } else {
                System.setProperty(Config.KUBERNETES_OAUTH_TOKEN_SYSTEM_PROPERTY, token);
                logger.debug("Token: [{}]", token);
            }
        }

        return new ConfigBuilder().withMasterUrl(masterUrl).withOauthToken(token).build();
    }

    @SuppressWarnings("unchecked")
    default <T extends KubernetesClient, R> Optional<R> executeCloudFunction(Function<T, R> func, Class<T> type) {
        R result = null;
        try (T client = (T) (type == OpenShiftClient.class ? createOpenShiftClient() : createKubernetesClient())) {
            result = func.apply(client);
        } catch (UnsupportedOperationException uoe) {
            throw uoe;
        } catch (IllegalStateException ise) {
            throw ise;
        } catch (Exception e) {
            throw new IOException(e);
        }
        return Optional.ofNullable(result);
    }
}
