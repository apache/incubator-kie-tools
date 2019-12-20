/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.fs.cloud;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.client.OpenShiftClient;
import org.junit.Test;
import org.uberfire.java.nio.IOException;

import static org.assertj.core.api.Assertions.*;

public class CloudClientFactoryTest {

    @Test(expected = IllegalStateException.class)
    public void testSetupConfig() {
        new CloudClientFactory(){}.setupConfig();
    }
    
    @Test
    public void testExecuteCloudFunction() {
        System.setProperty(Config.KUBERNETES_MASTER_SYSTEM_PROPERTY, "https://127.0.0.1:8443");
        System.setProperty(Config.KUBERNETES_OAUTH_TOKEN_SYSTEM_PROPERTY, "dummy");
        
        assertThat(new CloudClientFactory() {}.executeCloudFunction(client -> {
                       return OpenShiftClient.class;
                   }, OpenShiftClient.class)).containsSame(OpenShiftClient.class);

        assertThat(new CloudClientFactory() {}.executeCloudFunction(client -> {
            return KubernetesClient.class;
        }, KubernetesClient.class)).containsSame(KubernetesClient.class);
        
        assertThat(new CloudClientFactory() {}.executeCloudFunction(client -> {
            return null;
        }, KubernetesClient.class)).isEmpty();
       
        assertThat(new CloudClientFactory() {}.executeCloudFunction(client -> {
            return "";
        }, KubernetesClient.class)).isNotEmpty();
        
        assertThatThrownBy(() -> 
            new CloudClientFactory() {}.executeCloudFunction(client -> {
                throw new UnsupportedOperationException();
            }, KubernetesClient.class)
        ).isInstanceOf(UnsupportedOperationException.class);

        assertThatThrownBy(() -> 
            new CloudClientFactory() {}.executeCloudFunction(client -> {
                throw new IllegalStateException();
            }, KubernetesClient.class)
        ).isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> 
            new CloudClientFactory() {}.executeCloudFunction(client -> {
                throw new IndexOutOfBoundsException();
            }, KubernetesClient.class)
        ).isInstanceOf(IOException.class);
        
        System.clearProperty(Config.KUBERNETES_MASTER_SYSTEM_PROPERTY);
        System.clearProperty(Config.KUBERNETES_OAUTH_TOKEN_SYSTEM_PROPERTY);
    }
}
