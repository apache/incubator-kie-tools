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

import org.guvnor.ala.marshalling.BaseMarshallerTest;
import org.guvnor.ala.marshalling.Marshaller;
import org.guvnor.ala.wildfly.config.impl.WildflyProviderConfigImpl;
import org.guvnor.ala.wildfly.model.WildflyProviderImpl;

public class WildflyProviderImplMarshallerTest
        extends BaseMarshallerTest<WildflyProviderImpl> {

    private static final String NAME = "NAME";
    private static final String HOST = "HOST";
    private static final String PORT = "PORT";
    private static final String MANAGEMENT_PORT = "MANAGEMENT_PORT";
    private static final String USER = "USER";
    private static final String PASSWORD = "PASSWORD";

    @Override
    public Marshaller<WildflyProviderImpl> createMarshaller() {
        return new WildflyProviderImplMarshaller();
    }

    @Override
    public Class<WildflyProviderImpl> getType() {
        return WildflyProviderImpl.class;
    }

    @Override
    public WildflyProviderImpl getValue() {
        return createWildflyProvider();
    }

    public static WildflyProviderImpl createWildflyProvider() {
        return new WildflyProviderImpl(new WildflyProviderConfigImpl(NAME,
                                                                     HOST,
                                                                     PORT,
                                                                     MANAGEMENT_PORT,
                                                                     USER,
                                                                     PASSWORD));
    }
}
