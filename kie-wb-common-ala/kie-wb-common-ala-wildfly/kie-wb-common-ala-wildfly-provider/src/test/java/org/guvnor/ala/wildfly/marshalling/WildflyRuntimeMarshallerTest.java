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

package org.guvnor.ala.wildfly.marshalling;

import static org.guvnor.ala.wildfly.marshalling.WildflyProviderImplMarshallerTest.createWildflyProvider;

import org.guvnor.ala.marshalling.BaseMarshallerTest;
import org.guvnor.ala.marshalling.Marshaller;
import org.guvnor.ala.wildfly.config.impl.ContextAwareWildflyRuntimeExecConfig;
import org.guvnor.ala.wildfly.model.WildflyRuntime;
import org.guvnor.ala.wildfly.model.WildflyRuntimeEndpoint;
import org.guvnor.ala.wildfly.model.WildflyRuntimeInfo;
import org.guvnor.ala.wildfly.model.WildflyRuntimeState;

public class WildflyRuntimeMarshallerTest
        extends BaseMarshallerTest<WildflyRuntime> {

    private static final String RUNTIME_ID = "RUNTIME_ID";
    private static final String RUNTIME_NAME = "RUNTIME_NAME";
    private static final String WAR_PATH = "WAR_PATH";
    private static final String REDEPLOY_STRATEGY = "true";
    private static final String PROTOCOL = "PROTOCOL";
    private static final String HOST = "HOST";
    private static final Integer PORT = Integer.valueOf(8080);
    private static final String CONTEXT = "CONTEXT";
    private static final String STATE = "STATE";
    private static final String STARTED_AT = "STARTED_AT";

    @Override
    public Marshaller<WildflyRuntime> createMarshaller() {
        return new WildflyRuntimeMarshaller();
    }

    @Override
    public Class<WildflyRuntime> getType() {
        return WildflyRuntime.class;
    }

    @Override
    public WildflyRuntime getValue() {
        return new WildflyRuntime(RUNTIME_ID,
                                  RUNTIME_NAME,
                                  new ContextAwareWildflyRuntimeExecConfig(RUNTIME_NAME,
                                                                           createWildflyProvider(),
                                                                           WAR_PATH,
                                                                           REDEPLOY_STRATEGY),
                                  createWildflyProvider(),
                                  new WildflyRuntimeEndpoint(PROTOCOL,
                                                             HOST,
                                                             PORT,
                                                             CONTEXT),
                                  new WildflyRuntimeInfo(new ContextAwareWildflyRuntimeExecConfig(RUNTIME_NAME,
                                                                                                  createWildflyProvider(),
                                                                                                  WAR_PATH,
                                                                                                  REDEPLOY_STRATEGY)),
                                  new WildflyRuntimeState(STATE,
                                                          STARTED_AT));
    }
}
