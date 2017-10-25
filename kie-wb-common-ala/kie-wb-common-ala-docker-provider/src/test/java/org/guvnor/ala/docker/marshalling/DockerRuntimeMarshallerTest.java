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

package org.guvnor.ala.docker.marshalling;

import org.guvnor.ala.docker.config.impl.DockerRuntimeConfigImpl;
import org.guvnor.ala.docker.model.DockerRuntime;
import org.guvnor.ala.docker.model.DockerRuntimeEndpoint;
import org.guvnor.ala.docker.model.DockerRuntimeInfo;
import org.guvnor.ala.docker.model.DockerRuntimeState;
import org.guvnor.ala.marshalling.BaseMarshallerTest;
import org.guvnor.ala.marshalling.Marshaller;

import static org.guvnor.ala.docker.marshalling.DockerProviderImplMarshallerTest.createDockerProvider;

public class DockerRuntimeMarshallerTest
        extends BaseMarshallerTest<DockerRuntime> {

    private static final String RUNTIME_ID = "RUNTIME_ID";
    private static final String RUNTIME_NAME = "RUNTIME_NAME";
    private static final String IMAGE = "IMAGE";
    private static final String PROTOCOL = "PROTOCOL";
    private static final String HOST = "HOST";
    private static final String PORT = "PORT";
    private static final boolean PULL = true;
    private static final Integer PORT_VALUE = Integer.valueOf(8888);
    private static final String CONTEXT = "CONTEXT";
    private static final String STATE = "STATE";
    private static final String STARTED_AT = "STARTED_AT";

    @Override
    public Marshaller<DockerRuntime> createMarshaller() {
        return new DockerRuntimeMarshaller();
    }

    @Override
    public Class<DockerRuntime> getType() {
        return DockerRuntime.class;
    }

    @Override
    public DockerRuntime getValue() {
        return new DockerRuntime(RUNTIME_ID,
                                 RUNTIME_NAME,
                                 new DockerRuntimeConfigImpl(
                                         createDockerProvider(),
                                         IMAGE,
                                         PORT,
                                         PULL),
                                 createDockerProvider(),
                                 new DockerRuntimeEndpoint(PROTOCOL,
                                                           HOST,
                                                           PORT_VALUE,
                                                           CONTEXT),
                                 new DockerRuntimeInfo(new DockerRuntimeConfigImpl(createDockerProvider(),
                                                                                   IMAGE,
                                                                                   PORT,
                                                                                   PULL)),
                                 new DockerRuntimeState(STATE,
                                                        STARTED_AT));
    }
}
