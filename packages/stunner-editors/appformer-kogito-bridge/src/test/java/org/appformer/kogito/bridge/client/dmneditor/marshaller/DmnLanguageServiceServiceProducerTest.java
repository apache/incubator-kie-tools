/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.appformer.kogito.bridge.client.dmneditor.marshaller;

import elemental2.dom.Console;
import elemental2.dom.DomGlobal;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DmnLanguageServiceServiceProducerTest {

    @Mock
    private Console console;

    private DmnLanguageServiceServiceProducer producer;

    @Before
    public void setup() {
        producer = spy(new DmnLanguageServiceServiceProducer());
        DomGlobal.console = console;
    }

    @Test
    public void produceWithEnvelopeAvailable() {
        doReturn(true).when(producer).isEnvelopeAvailable();
        Assertions.assertThat(producer.produce()).isNotNull().isInstanceOf(DmnLanguageServiceService.class);
    }

    @Test
    public void produceWithEnvelopeNotAvailable() {
        doReturn(false).when(producer).isEnvelopeAvailable();
        Assertions.assertThat(producer.produce()).isNotNull().isInstanceOf(UnavailableDmnLanguageServiceService.class);
        verify(console).warn("[DmnLanguageServiceApi] Envelope API is not available. Empty DMN models will be passed");
    }

}
