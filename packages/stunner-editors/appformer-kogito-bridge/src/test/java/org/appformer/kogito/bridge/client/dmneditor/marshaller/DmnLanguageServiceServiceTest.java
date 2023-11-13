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

import org.appformer.kogito.bridge.client.dmneditor.marshaller.model.DmnDocumentData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DmnLanguageServiceServiceTest {

    @Mock
    private DmnLanguageServiceApiInteropWrapper wrapperMock;

    private DmnLanguageServiceService dmnEditorMarshallerServiceSpy;

    @Before
    public void setup() {
        dmnEditorMarshallerServiceSpy = spy(new DmnLanguageServiceService() {
            @Override
            DmnLanguageServiceApiInteropWrapper getWrapper() {
                return wrapperMock;
            }
        });
    }

    @Test
    public void getDmnDocument() {
        final String xmlContent = "<DMN>content</DMN>";
        when(wrapperMock.getDmnDocumentData(xmlContent)).thenReturn(new DmnDocumentData());
        dmnEditorMarshallerServiceSpy.getDmnDocumentData(xmlContent);
        verify(wrapperMock, times(1)).getDmnDocumentData(xmlContent);
    }
}
