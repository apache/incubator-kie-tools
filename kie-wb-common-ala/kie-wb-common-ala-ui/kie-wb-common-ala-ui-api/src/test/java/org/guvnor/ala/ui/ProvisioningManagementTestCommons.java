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

package org.guvnor.ala.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.guvnor.ala.ui.model.PipelineKey;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.ProviderTypeStatus;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.commons.data.Pair;

import static org.mockito.Mockito.*;

public class ProvisioningManagementTestCommons {

    public static final String PROVIDER_ID = "PROVIDER_ID";

    public static final String PROVIDER_NAME = "PROVIDER_NAME";

    public static final String PROVIDER_VERSION = "PROVIDER_VERSION";

    public static final String IMAGE_URL = "IMAGE_URL";

    public static final String SUCCESS_MESSAGE = "SUCCESS_MESSAGE";

    public static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    public static final String SERVICE_CALLER_ERROR_MESSAGE = "SERVICE_ERROR_MESSAGE";

    public static final String SERVICE_CALLER_EXCEPTION_MESSAGE = "SERVICE_CALLER_EXCEPTION_MESSAGE";

    public static final String PIPELINE1 = "PIPELINE1";

    public static final PipelineKey PIPELINE1_KEY = new PipelineKey(PIPELINE1);

    public static final String PIPELINE2 = "PIPELINE2";

    public static final PipelineKey PIPELINE2_KEY = new PipelineKey(PIPELINE2);

    public static List<ProviderType> mockProviderTypeList(int count) {
        List<ProviderType> providerTypes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            //mock an arbitrary set of provider types.
            providerTypes.add(mockProviderType(Integer.toString(i)));
        }
        return providerTypes;
    }

    public static final ProviderType mockProviderType(String suffix) {
        return new ProviderType(mockProviderTypeKey(suffix),
                                "ProviderType.name." + suffix);
    }

    public static final ProviderTypeKey mockProviderTypeKey(String suffix) {
        return new ProviderTypeKey("ProviderTypeKey.id." + suffix,
                                   "ProviderTypeKey.version." + suffix);
    }

    public static final ProviderKey mockProviderKey(ProviderTypeKey providerTypeKey,
                                                    String suffix) {
        return new ProviderKey(providerTypeKey,
                               "ProviderKey.id." + suffix);
    }

    public static final List<ProviderKey> mockProviderKeyList(ProviderTypeKey providerTypeKey,
                                                              int count) {
        List<ProviderKey> providerKeys = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            //mock an arbitrary set for provider keys.
            providerKeys.add(mockProviderKey(providerTypeKey,
                                             Integer.toString(i)));
        }
        return providerKeys;
    }

    public static List<Pair<ProviderType, ProviderTypeStatus>> buildProviderTypeStatusList(Collection<ProviderType> providerTypes,
                                                                                           ProviderTypeStatus initialStatus) {
        return providerTypes.stream()
                .map(providerType -> new Pair<>(providerType,
                                                initialStatus))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <T> void prepareServiceCallerError(T service,
                                                     Caller<T> serviceCaller) {
        doAnswer(new Answer<T>() {
            public T answer(InvocationOnMock invocation) {
                ErrorCallback callback = (ErrorCallback) invocation.getArguments()[1];
                callback.error(mock(Message.class),
                               new Throwable(SERVICE_CALLER_EXCEPTION_MESSAGE));
                return service;
            }
        }).when(serviceCaller).call(any(RemoteCallback.class),
                                    any(ErrorCallback.class));
    }
}
