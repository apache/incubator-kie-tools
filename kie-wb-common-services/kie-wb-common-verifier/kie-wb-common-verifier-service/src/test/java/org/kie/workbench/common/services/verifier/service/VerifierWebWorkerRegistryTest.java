/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.verifier.service;

import java.util.ArrayList;

import javax.enterprise.inject.Instance;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class VerifierWebWorkerRegistryTest {

    @Test
    public void noWebWorkerProviders() throws Exception {
        final Instance verifierWebWorkerProviders = mock(Instance.class);
        doReturn(new ArrayList<>().iterator()).when(verifierWebWorkerProviders).iterator();

        assertFalse(new VerifierWebWorkerRegistry(verifierWebWorkerProviders).get("something").isPresent());
    }

    @Test
    public void wrongId() throws Exception {
        final Instance verifierWebWorkerProviders = mock(Instance.class);
        final ArrayList<Object> webWorkerProviders = new ArrayList<>();
        webWorkerProviders.add(getWebWorkerProvider("somethingElse"));

        doReturn(webWorkerProviders.iterator()).when(verifierWebWorkerProviders).iterator();

        assertFalse(new VerifierWebWorkerRegistry(verifierWebWorkerProviders).get("something").isPresent());
    }

    @Test
    public void correctId() throws Exception {
        final Instance verifierWebWorkerProviders = mock(Instance.class);
        final ArrayList<Object> webWorkerProviders = new ArrayList<>();
        webWorkerProviders.add(getWebWorkerProvider("something"));

        doReturn(webWorkerProviders.iterator()).when(verifierWebWorkerProviders).iterator();

        assertTrue(new VerifierWebWorkerRegistry(verifierWebWorkerProviders).get("something").isPresent());
    }

    private VerifierWebWorkerProvider getWebWorkerProvider(final String id) {
        return new VerifierWebWorkerProvider() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getWebWorker(String fileName) throws Exception {
                return "";
            }
        };
    }
}
