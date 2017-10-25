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

import org.guvnor.ala.marshalling.BaseMarshallerTest;
import org.guvnor.ala.marshalling.Marshaller;
import org.guvnor.ala.openshift.config.impl.OpenShiftProviderConfigImpl;
import org.guvnor.ala.openshift.model.OpenShiftProviderImpl;

public class OpenShiftProviderImplMarshallerTest
        extends BaseMarshallerTest<OpenShiftProviderImpl> {

    private static final String PROVIDER_ID = "PROVIDER_ID";

    @Override
    public Marshaller<OpenShiftProviderImpl> createMarshaller() {
        return new OpenShiftProviderImplMarshaller();
    }

    @Override
    public Class<OpenShiftProviderImpl> getType() {
        return OpenShiftProviderImpl.class;
    }

    @Override
    public OpenShiftProviderImpl getValue() {
        return createOpenShiftProvider();
    }

    public static OpenShiftProviderImpl createOpenShiftProvider() {
        return new OpenShiftProviderImpl(PROVIDER_ID,
                                         new OpenShiftProviderConfigImpl().clear());
    }
}
