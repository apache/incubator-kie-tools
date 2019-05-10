/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.backend.producers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDIExtensionsRegister;

import static java.util.Collections.singletonList;
import static org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory.newMarshallerWithExtensions;

@ApplicationScoped
public class DMNMarshallerProducer {

    private DMNMarshaller marshaller;

    @Produces
    public DMNMarshaller get() {

        if (marshaller == null) {
            marshaller = newMarshallerWithExtensions(singletonList(new DMNDIExtensionsRegister()));
        }

        return marshaller;
    }
}
