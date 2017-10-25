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

import org.guvnor.ala.docker.config.impl.DockerProviderConfigImpl;
import org.guvnor.ala.docker.model.DockerProviderImpl;
import org.guvnor.ala.marshalling.BaseMarshallerTest;
import org.guvnor.ala.marshalling.Marshaller;

public class DockerProviderImplMarshallerTest
        extends BaseMarshallerTest<DockerProviderImpl> {

    private static final String NAME = "NAME";
    private static final String HOST_IP = "HOST_IP";

    @Override
    public Marshaller<DockerProviderImpl> createMarshaller() {
        return new DockerProviderImplMarshaller();
    }

    @Override
    public Class<DockerProviderImpl> getType() {
        return DockerProviderImpl.class;
    }

    @Override
    public DockerProviderImpl getValue() {
        return createDockerProvider();
    }

    public static DockerProviderImpl createDockerProvider() {
        return new DockerProviderImpl(new DockerProviderConfigImpl(NAME,
                                                                   HOST_IP));
    }
}