/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.openshift.access.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.util.Map;

import io.fabric8.openshift.client.OpenShiftConfig;
import org.guvnor.ala.openshift.access.OpenShiftRuntimeId;
import org.guvnor.ala.openshift.access.OpenShiftTemplate;
import org.guvnor.ala.openshift.access.OpenShiftTemplate.Parameter;
import org.guvnor.ala.openshift.config.impl.OpenShiftProviderConfigImpl;
import org.junit.Test;

public class OpenShiftAccessTest {

    @Test
    public void testGoodRuntimeId() {
        final String expectedPrj = "myProject";
        final String expectedSvc = "myService";
        final String expectedApp = "myApplication";
        OpenShiftRuntimeId oldId = new OpenShiftRuntimeId(expectedPrj, expectedSvc, expectedApp);
        OpenShiftRuntimeId newId = OpenShiftRuntimeId.fromString(oldId.toString());
        assertEquals(expectedPrj, newId.project());
        assertEquals(expectedSvc, newId.service());
        assertEquals(expectedApp, newId.application());
        assertEquals(oldId, newId);
    }

    @Test
    public void testBadRuntimeId() {
        final String nullStr = null;
        final String emptyStr = "";
        final String blankStr = " ";
        final String validStr = "valid";
        try {
            new OpenShiftRuntimeId(nullStr, validStr, validStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(emptyStr, validStr, validStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(blankStr, validStr, validStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(validStr, nullStr, validStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(validStr, emptyStr, validStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(validStr, blankStr, validStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(validStr, validStr, nullStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(validStr, validStr, emptyStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(validStr, validStr, blankStr);
            fail();
        } catch (IllegalArgumentException iae) {}
    }

    @Test
    public void testProviderConfig() {
        OpenShiftProviderConfigImpl providerConfig = new OpenShiftProviderConfigImpl().clear();
        providerConfig.setKubernetesMaster("https://localhost:8443");
        providerConfig.setKubernetesOapiVersion("v2");
        OpenShiftConfig clientConfig = OpenShiftAccessInterfaceImpl.buildOpenShiftConfig(providerConfig);
        assertEquals("https://localhost:8443/", clientConfig.getMasterUrl());
        assertEquals("https://localhost:8443/oapi/v2/", clientConfig.getOpenShiftUrl());
    }

    @Test
    public void testTemplateParams() throws Exception {
        String templateUri = getUri("bpmsuite70-execserv.json");
        OpenShiftTemplate template = new OpenShiftTemplate(templateUri);
        Map<String,Parameter> params = template.getParameterMap();
        verifyParameter(params,
                        "APPLICATION_NAME",
                        "Application Name",
                        "The name for the application.",
                        true,
                        null,
                        null,
                        "myapp");
        verifyParameter(params,
                        "KIE_SERVER_PWD",
                        "KIE Server Password",
                        "KIE execution server password (Sets the org.kie.server.pwd system property)",
                        false,
                        "expression",
                        "[a-zA-Z]{6}[0-9]{1}!",
                        null);
}

    private void verifyParameter(Map<String,Parameter> params,
                                 String name,
                                 String displayName,
                                 String description,
                                 boolean required,
                                 String generate,
                                 String from,
                                 String value) {
        Parameter param = params.get(name);
        assertEquals(name, param.getName());
        assertEquals(displayName, param.getDisplayName());
        assertEquals(description, param.getDescription());
        assertEquals(required, param.isRequired());
        assertEquals(generate, param.getGenerate());
        assertEquals(from, param.getFrom());
        assertEquals(value, param.getValue());
    }

    private String getUri(String resourcePath) throws URISyntaxException {
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
        }
        return getClass().getResource(resourcePath).toURI().toString();
    }

}
