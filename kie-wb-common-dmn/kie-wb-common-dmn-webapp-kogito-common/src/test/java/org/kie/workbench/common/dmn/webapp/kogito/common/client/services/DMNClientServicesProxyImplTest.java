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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.editors.types.RangeValue;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class DMNClientServicesProxyImplTest {

    private DMNClientServicesProxyImpl service;

    @Before
    public void setup() {
        this.service = new DMNClientServicesProxyImpl();
    }

    @Test
    public void testParseRangeValue() {
        final ServiceCallback<RangeValue> callback = new ServiceCallback<RangeValue>() {
            @Override
            public void onSuccess(final RangeValue actual) {
                assertThat(actual.getIncludeStartValue()).isTrue();
                assertThat(actual.getIncludeEndValue()).isTrue();
                assertThat(actual.getStartValue()).isEqualTo("1");
                assertThat(actual.getEndValue()).isEqualTo("2");
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                fail(error.getMessage());
            }
        };

        service.parseRangeValue("[1..2]", callback);
    }

    @Test
    public void testParseFEELList() {
        final ServiceCallback<List<String>> callback = new ServiceCallback<List<String>>() {
            @Override
            public void onSuccess(final List<String> actual) {
                assertThat(actual).containsExactly("one", "two");
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                fail(error.getMessage());
            }
        };

        service.parseFEELList("one,two", callback);
    }
}