/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.services.tests;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.config.DockerRuntimeConfig;
import org.guvnor.ala.docker.config.impl.DockerProviderConfigImpl;
import org.guvnor.ala.docker.config.impl.DockerRuntimeConfigImpl;
import org.guvnor.ala.docker.model.DockerProvider;
import org.guvnor.ala.docker.model.DockerRuntime;
import org.guvnor.ala.openshift.config.OpenShiftParameters;
import org.guvnor.ala.openshift.config.OpenShiftRuntimeConfig;
import org.guvnor.ala.openshift.config.impl.OpenShiftProviderConfigImpl;
import org.guvnor.ala.openshift.config.impl.OpenShiftRuntimeConfigImpl;
import org.guvnor.ala.openshift.model.OpenShiftProvider;
import org.guvnor.ala.openshift.model.OpenShiftRuntime;
import org.guvnor.ala.openshift.model.OpenShiftRuntimeState;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.services.api.RuntimeProvisioningService;
import org.guvnor.ala.services.api.itemlist.ProviderList;
import org.guvnor.ala.services.api.itemlist.ProviderTypeList;
import org.guvnor.ala.services.api.itemlist.RuntimeList;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Ignore;

import static org.junit.Assert.*;

public class RuntimeEndpointsTestIT {

    private final String APP_URL = "http://localhost:8080/api/";

    /**
     * Can be used if internal to red hat.
     * TODO: replace with more lightweight image and non-internal (minishift?) environment
     * @throws Exception
     */
    @Ignore
    public void checkOpenShiftService() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(APP_URL);
        ResteasyWebTarget restEasyTarget = (ResteasyWebTarget) target;
        RuntimeProvisioningService proxy = restEasyTarget.proxy(RuntimeProvisioningService.class);

        ProviderTypeList allProviderTypes = proxy.getProviderTypes(0,
                                                                   10,
                                                                   "",
                                                                   true);

        assertNotNull(allProviderTypes);
        assertEquals(3,
                     allProviderTypes.getItems().size());

        OpenShiftProviderConfigImpl openshiftProviderConfig = createProviderConfig();
        proxy.registerProvider(openshiftProviderConfig);

        ProviderList allProviders = proxy.getProviders(0,
                                                       10,
                                                       "",
                                                       true);
        assertEquals(1,
                     allProviders.getItems().size());
        assertTrue(allProviders.getItems().get(0) instanceof OpenShiftProvider);
        OpenShiftProvider openshiftProvider = (OpenShiftProvider) allProviders.getItems().get(0);
        OpenShiftRuntimeConfig runtimeConfig = createRuntimeConfig(openshiftProvider,
                                                                   "coss1");

        @SuppressWarnings("unused")
        OpenShiftRuntime openshiftRuntime = getOpenShiftRuntime(proxy,
                                                                0,
                                                                null);

        String runtimeId = proxy.newRuntime(runtimeConfig);
        openshiftRuntime = getOpenShiftRuntime(proxy,
                                               1,
                                               OpenShiftRuntimeState.READY);

        proxy.startRuntime(runtimeId);
        openshiftRuntime = getOpenShiftRuntime(proxy,
                                               1,
                                               OpenShiftRuntimeState.RUNNING);

        proxy.stopRuntime(runtimeId);
        openshiftRuntime = getOpenShiftRuntime(proxy,
                                               1,
                                               OpenShiftRuntimeState.READY);

        proxy.destroyRuntime(runtimeId,
                             true);
        openshiftRuntime = getOpenShiftRuntime(proxy,
                                               0,
                                               null);
    }

    private OpenShiftProviderConfigImpl createProviderConfig() {
        OpenShiftProviderConfigImpl openshiftProviderConfig = new OpenShiftProviderConfigImpl().clear();
        openshiftProviderConfig.setName(getClass().getSimpleName());
        openshiftProviderConfig.setKubernetesMaster("https://ce-os-rhel-master.usersys.redhat.com:8443");
        openshiftProviderConfig.setKubernetesAuthBasicUsername("admin");
        openshiftProviderConfig.setKubernetesAuthBasicPassword("admin");
        return openshiftProviderConfig;
    }

    private OpenShiftRuntimeConfigImpl createRuntimeConfig(ProviderId providerId,
                                                           String testName) throws Exception {
        final String prjName = createProjectName(testName);
        final String appName = "myapp";
        final String svcName = appName + "-execserv";
        OpenShiftRuntimeConfigImpl runtimeConfig = new OpenShiftRuntimeConfigImpl();
        runtimeConfig.setProviderId(providerId);
        runtimeConfig.setProjectName(prjName);
        runtimeConfig.setServiceName(svcName);
        runtimeConfig.setApplicationName(appName);
        runtimeConfig.setResourceSecretsUri(getUri("bpmsuite-app-secret.json"));
        runtimeConfig.setResourceStreamsUri(getUri("bpmsuite-image-streams.json"));
        runtimeConfig.setResourceTemplateUri(getUri("bpmsuite70-execserv.json"));
        runtimeConfig.setResourceTemplateParamValues(new OpenShiftParameters()
                                                             .param("APPLICATION_NAME",
                                                                    appName)
                                                             .param("IMAGE_STREAM_NAMESPACE",
                                                                    prjName)
                                                             .param("KIE_ADMIN_PWD",
                                                                    "admin1!")
                                                             .param("KIE_SERVER_PWD",
                                                                    "execution1!")
                                                             .toString());
        return runtimeConfig;
    }

    private String createProjectName(String testName) {
        return new StringBuilder()
                .append(System.getProperty("user.name",
                                           "anon").replaceAll("[^A-Za-z0-9]",
                                                              "-"))
                .append('-')
                .append(testName != null ? testName : "test")
                .append('-')
                .append(new SimpleDateFormat("YYYYMMddHHmmss").format(new Date()))
                .toString();
    }

    private String getUri(String resourcePath) throws URISyntaxException {
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
        }
        return getClass().getResource(resourcePath).toURI().toString();
    }

    private OpenShiftRuntime getOpenShiftRuntime(RuntimeProvisioningService proxy,
                                                 int expectedCount,
                                                 String expectedState) {
        RuntimeList allRuntimes = proxy.getRuntimes(0,
                                                    10,
                                                    "",
                                                    true);
        assertEquals(expectedCount,
                     allRuntimes.getItems().size());

        if (expectedCount == 0) {
            return null;
        }

        Runtime runtime = allRuntimes.getItems().get(0);

        assertTrue(runtime instanceof OpenShiftRuntime);
        OpenShiftRuntime openshiftRuntime = (OpenShiftRuntime) runtime;
        assertEquals(expectedState,
                     openshiftRuntime.getState().getState());

        return openshiftRuntime;
    }

    @Ignore
    public void checkDockerService() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(APP_URL);
        ResteasyWebTarget restEasyTarget = (ResteasyWebTarget) target;
        RuntimeProvisioningService proxy = restEasyTarget.proxy(RuntimeProvisioningService.class);

        ProviderTypeList allProviderTypes = proxy.getProviderTypes(0,
                                                                   10,
                                                                   "",
                                                                   true);

        assertNotNull(allProviderTypes);
        assertEquals(3,
                     allProviderTypes.getItems().size());

        DockerProviderConfig dockerProviderConfig = new DockerProviderConfigImpl();
        proxy.registerProvider(dockerProviderConfig);

        ProviderList allProviders = proxy.getProviders(0,
                                                       10,
                                                       "",
                                                       true);
        assertEquals(1,
                     allProviders.getItems().size());
        assertTrue(allProviders.getItems().get(0) instanceof DockerProvider);
        DockerProvider dockerProvider = (DockerProvider) allProviders.getItems().get(0);
        DockerRuntimeConfig runtimeConfig = new DockerRuntimeConfigImpl(dockerProvider,
                                                                        "kitematic/hello-world-nginx",
                                                                        "8080",
                                                                        true);

        RuntimeList allRuntimes = proxy.getRuntimes(0,
                                                    10,
                                                    "",
                                                    true);
        assertEquals(0,
                     allRuntimes.getItems().size());

        String newRuntime = proxy.newRuntime(runtimeConfig);

        allRuntimes = proxy.getRuntimes(0,
                                        10,
                                        "",
                                        true);
        assertEquals(1,
                     allRuntimes.getItems().size());

        Runtime runtime = allRuntimes.getItems().get(0);

        assertTrue(runtime instanceof DockerRuntime);
        DockerRuntime dockerRuntime = (DockerRuntime) runtime;

        assertEquals("Running",
                     dockerRuntime.getState().getState());
        proxy.stopRuntime(newRuntime);

        allRuntimes = proxy.getRuntimes(0,
                                        10,
                                        "",
                                        true);
        assertEquals(1,
                     allRuntimes.getItems().size());
        runtime = allRuntimes.getItems().get(0);

        assertTrue(runtime instanceof DockerRuntime);
        dockerRuntime = (DockerRuntime) runtime;

        assertEquals("Stopped",
                     dockerRuntime.getState().getState());

        proxy.destroyRuntime(newRuntime,
                             true);

        allRuntimes = proxy.getRuntimes(0,
                                        10,
                                        "",
                                        true);
        assertEquals(0,
                     allRuntimes.getItems().size());
    }
}
