/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.openshift.marshalling;

import static org.guvnor.ala.openshift.marshalling.OpenShiftProviderImplMarshallerTest.createOpenShiftProvider;

import org.guvnor.ala.marshalling.BaseMarshallerTest;
import org.guvnor.ala.marshalling.Marshaller;
import org.guvnor.ala.openshift.config.OpenShiftRuntimeConfig;
import org.guvnor.ala.openshift.config.impl.ContextAwareOpenShiftRuntimeExecConfig;
import org.guvnor.ala.openshift.model.OpenShiftProvider;
import org.guvnor.ala.openshift.model.OpenShiftRuntime;
import org.guvnor.ala.openshift.model.OpenShiftRuntimeEndpoint;
import org.guvnor.ala.openshift.model.OpenShiftRuntimeInfo;
import org.guvnor.ala.openshift.model.OpenShiftRuntimeState;

public class OpenShiftRuntimeMarshallerTest
        extends BaseMarshallerTest<OpenShiftRuntime> {

    private static final String RUNTIME_ID = "RUNTIME_ID";
    private static final String RUNTIME_NAME = "RUNTIME_NAME";
    private static final String APPLICATION_NAME = "APPLICATION_NAME";
    private static final String KIE_SERVER_CONTAINER_DEPLOYMENT = "KIE_SERVER_CONTAINER_DEPLOYMENT";
    private static final String PROJECT_NAME = "PROJECT_NAME";
    private static final String RESOURCE_SECRETS_URI = "RESOURCE_SECRETS_URI";
    private static final String RESOURCE_STREAMS_URI = "RESOURCE_STREAMS_URI";
    private static final String RESOURCE_TEMPLATE_NAME = "RESOURCE_TEMPLATE_NAME";
    private static final String RESOURCE_TEMPLATE_PARAM_DELIMITER = "RESOURCE_TEMPLATE_PARAM_DELIMITER";
    private static final String RESOURCE_TEMPLATE_PARAM_ASSIGNER = "RESOURCE_TEMPLATE_PARAM_ASSIGNER";
    private static final String RESOURCE_TEMPLATE_PARAM_VALUES = "RESOURCE_TEMPLATE_PARAM_VALUES";
    private static final String RESOURCE_TEMPLATE_URI = "RESOURCE_TEMPLATE_URI";
    private static final String SERVICE_NAME = "SERVICE_NAME";
    private static final String STATE = "STATE";
    private static final String STARTED_AT ="STARTED_AT";
    private static final String PROTOCOL = "PROTOCOL";
    private static final String HOST = "HOST";
    private static final Integer PORT = Integer.valueOf(80);
    private static final String CONTEXT = "CONTEXT";

    @Override
    public Marshaller<OpenShiftRuntime> createMarshaller() {
        return new OpenShiftRuntimeMarshaller();
    }

    @Override
    public Class<OpenShiftRuntime> getType() {
        return OpenShiftRuntime.class;
    }

    @Override
    public OpenShiftRuntime getValue() {
        OpenShiftProvider providerId = createOpenShiftProvider();
        OpenShiftRuntimeConfig runtimeConfig = new ContextAwareOpenShiftRuntimeExecConfig(
                RUNTIME_NAME,
                providerId,
                APPLICATION_NAME,
                KIE_SERVER_CONTAINER_DEPLOYMENT,
                PROJECT_NAME,
                RESOURCE_SECRETS_URI,
                RESOURCE_STREAMS_URI,
                RESOURCE_TEMPLATE_NAME,
                RESOURCE_TEMPLATE_PARAM_DELIMITER,
                RESOURCE_TEMPLATE_PARAM_ASSIGNER,
                RESOURCE_TEMPLATE_PARAM_VALUES,
                RESOURCE_TEMPLATE_URI,
                SERVICE_NAME);
        return new OpenShiftRuntime(RUNTIME_ID,
                                    RUNTIME_NAME,
                                    runtimeConfig,
                                    providerId,
                                    new OpenShiftRuntimeEndpoint(PROTOCOL,
                                                                 HOST,
                                                                 PORT,
                                                                 CONTEXT),
                                    new OpenShiftRuntimeInfo(runtimeConfig),
                                    new OpenShiftRuntimeState(STATE,
                                                              STARTED_AT));
    }
}
